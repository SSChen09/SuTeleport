package su.luochen.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.PermissionManager;
import su.luochen.manager.WarpManager;

public class SetWarpCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final WarpManager warpManager;
    private final PermissionManager permissionManager;

    public SetWarpCommand(SuTeleport plugin, WarpManager warpManager, PermissionManager permissionManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c只有玩家才能使用此命令！");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.warp.set")) {
            player.sendMessage("§c你没有权限设置传送点！");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§c用法: /setwarp <传送点名称>");
            return true;
        }

        String warpName = args[0];

        if (warpManager.setWarp(warpName, player)) {
            player.sendMessage("§a传送点 " + warpName + " 已设置！");
        }

        return true;
    }
}
