package su.luochen;

import org.bukkit.plugin.java.JavaPlugin;
import su.luochen.commands.*;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.DeathLocationManager;
import su.luochen.manager.HomeManager;
import su.luochen.manager.PermissionManager;
import su.luochen.manager.PreviousLocationManager;
import su.luochen.manager.TpaManager;
import su.luochen.manager.WarpManager;

public class SuTeleport extends JavaPlugin {

    private static SuTeleport instance;
    private TpaManager tpaManager;
    private WarpManager warpManager;
    private HomeManager homeManager;
    private CooldownManager cooldownManager;
    private PermissionManager permissionManager;
    private DeathLocationManager deathLocationManager;
    private PreviousLocationManager previousLocationManager;
    private TprCommand tprCommand;

    @Override
    public void onEnable() {
        instance = this;
        previousLocationManager = new PreviousLocationManager();
        tpaManager = new TpaManager(this, previousLocationManager);
        warpManager = new WarpManager(this, previousLocationManager);
        homeManager = new HomeManager(this, previousLocationManager);
        cooldownManager = new CooldownManager(this);
        permissionManager = new PermissionManager(this);
        deathLocationManager = new DeathLocationManager(this);
        tprCommand = new TprCommand(this, cooldownManager, permissionManager);

        // 保存默认配置
        saveDefaultConfig();

        // 注册 TPA 命令及 Tab 补全
        TpaCommand tpaCmd = new TpaCommand(this, cooldownManager, permissionManager, false);
        TpaCommand tpahereCmd = new TpaCommand(this, cooldownManager, permissionManager, true);
        getCommand("tpa").setExecutor(tpaCmd);
        getCommand("tpa").setTabCompleter(tpaCmd);
        getCommand("tpahere").setExecutor(tpahereCmd);
        getCommand("tpahere").setTabCompleter(tpahereCmd);
        getCommand("tpaccept").setExecutor(new TpacceptCommand(this, cooldownManager, permissionManager));
        getCommand("tpdeny").setExecutor(new TpdenyCommand(this, permissionManager));
        getCommand("tpr").setExecutor(tprCommand);
        getCommand("suteleport").setExecutor(new ReloadCommand(this));
        getCommand("warps").setExecutor(new WarpsCommand(this, warpManager));
        getCommand("homes").setExecutor(new HomesCommand(this, homeManager));
        getCommand("back").setExecutor(new BackCommand(this, cooldownManager, permissionManager, deathLocationManager));
        getCommand("sback").setExecutor(new SBackCommand(this, cooldownManager, permissionManager, previousLocationManager));
        getCommand("suicide").setExecutor(new SuicideCommand(this, permissionManager));

        // 注册 Home/Warp 命令及 Tab 补全
        HomeCommand homeCmd = new HomeCommand(this, homeManager, cooldownManager, permissionManager);
        SetHomeCommand setHomeCmd = new SetHomeCommand(this, homeManager, permissionManager);
        DelHomeCommand delHomeCmd = new DelHomeCommand(this, homeManager, permissionManager);
        WarpCommand warpCmd = new WarpCommand(this, warpManager, cooldownManager, permissionManager);
        SetWarpCommand setWarpCmd = new SetWarpCommand(this, warpManager, permissionManager);
        DelWarpCommand delWarpCmd = new DelWarpCommand(this, warpManager, permissionManager);

        getCommand("home").setExecutor(homeCmd);
        getCommand("home").setTabCompleter(homeCmd);
        getCommand("sethome").setExecutor(setHomeCmd);
        getCommand("sethome").setTabCompleter(setHomeCmd);
        getCommand("delhome").setExecutor(delHomeCmd);
        getCommand("delhome").setTabCompleter(delHomeCmd);
        getCommand("warp").setExecutor(warpCmd);
        getCommand("warp").setTabCompleter(warpCmd);
        getCommand("setwarp").setExecutor(setWarpCmd);
        getCommand("setwarp").setTabCompleter(setWarpCmd);
        getCommand("delwarp").setExecutor(delWarpCmd);
        getCommand("delwarp").setTabCompleter(delWarpCmd);

        // 注册死亡事件监听
        getServer().getPluginManager().registerEvents(new DeathListener(deathLocationManager), this);

        getLogger().info("SuTeleport 已启用！");
    }

    @Override
    public void onDisable() {
        getLogger().info("SuTeleport 已禁用！");
    }

    public static SuTeleport getInstance() {
        return instance;
    }

    public TpaManager getTpaManager() {
        return tpaManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public PreviousLocationManager getPreviousLocationManager() {
        return previousLocationManager;
    }

    public void reloadTpaManager() {
        this.tpaManager = new TpaManager(this, previousLocationManager);
    }

    public void reloadTprCommand() {
        this.tprCommand = new TprCommand(this, cooldownManager, permissionManager);
        getCommand("tpr").setExecutor(tprCommand);
    }

    public DeathLocationManager getDeathLocationManager() {
        return deathLocationManager;
    }

    public void reloadCooldownManager() {
        this.cooldownManager.reloadCooldowns();
    }
}
