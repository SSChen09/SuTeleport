package su.luochen.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.PermissionManager;
import su.luochen.manager.WarpManager;

import java.util.ArrayList;
import java.util.List;

public class DelWarpCommand implements CommandExecutor, TabCompleter {

    private final SuTeleport plugin;
    private final WarpManager warpManager;
    private final PermissionManager permissionManager;

    public DelWarpCommand(SuTeleport plugin, WarpManager warpManager, PermissionManager permissionManager) {
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

        if (!permissionManager.hasPermission(player, "suteleport.warp.delete")) {
            player.sendMessage("§c你没有权限删除传送点！");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§c用法: /delwarp <传送点名称>");
            return true;
        }

        String warpName = args[0];

        if (!warpManager.warpExists(warpName)) {
            player.sendMessage("§c传送点 " + warpName + " 不存在！");
            return true;
        }

        if (warpManager.deleteWarp(warpName)) {
            player.sendMessage("§a传送点 " + warpName + " 已删除！");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (String warp : warpManager.getWarpNames()) {
                if (warp.toLowerCase().startsWith(input)) {
                    completions.add(warp);
                }
            }
        }
        return completions;
    }
}
