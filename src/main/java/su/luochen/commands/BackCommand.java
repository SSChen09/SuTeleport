package su.luochen.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.DeathLocationManager;
import su.luochen.manager.PermissionManager;

public class BackCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final CooldownManager cooldownManager;
    private final PermissionManager permissionManager;
    private final DeathLocationManager deathLocationManager;

    public BackCommand(SuTeleport plugin, CooldownManager cooldownManager, PermissionManager permissionManager, DeathLocationManager deathLocationManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.permissionManager = permissionManager;
        this.deathLocationManager = deathLocationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c只有玩家才能使用此命令！");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.back")) {
            player.sendMessage("§c你没有权限使用此命令！");
            return true;
        }

        if (!deathLocationManager.hasDeathLocation(player)) {
            player.sendMessage("§c你没有死亡位置记录！");
            return true;
        }

        // 检查冷却
        if (!cooldownManager.checkCooldown(player, "back")) {
            return true;
        }

        Location deathLoc = deathLocationManager.getDeathLocation(player);

        // 清除死亡位置记录
        deathLocationManager.clearDeathLocation(player);

        // 保存当前位置并传送到死亡位置
        plugin.getPreviousLocationManager().saveLocation(player);
        player.teleport(deathLoc);
        player.sendMessage("§a已将你传送回死亡位置！");

        return true;
    }
}
