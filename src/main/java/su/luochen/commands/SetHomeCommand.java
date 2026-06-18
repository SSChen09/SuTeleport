package su.luochen.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.PermissionManager;
import su.luochen.manager.HomeManager;

public class SetHomeCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final HomeManager homeManager;
    private final PermissionManager permissionManager;

    public SetHomeCommand(SuTeleport plugin, HomeManager homeManager, PermissionManager permissionManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c只有玩家才能使用此命令！");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.home.set")) {
            player.sendMessage("§c你没有权限使用此命令！");
            return true;
        }

        String homeName = "home"; // 默认名称
        if (args.length >= 1) {
            homeName = args[0];
        }

        if (homeManager.setHome(homeName, player)) {
            player.sendMessage("§a家 " + homeName + " 已设置！");
            int count = homeManager.getHomeCount(player);
            int max = homeManager.getMaxHomes();
            player.sendMessage("§7当前家数量: " + count + "/" + max);
        }

        return true;
    }
}
