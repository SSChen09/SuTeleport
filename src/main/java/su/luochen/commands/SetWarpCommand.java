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

public class SetWarpCommand implements CommandExecutor, TabCompleter {

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
            plugin.getMessageManager().send(sender, "common.only-player");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.warp.set")) {
            plugin.getMessageManager().send(player, "setwarp.no-permission");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageManager().send(player, "setwarp.usage");
            return true;
        }

        String warpName = args[0];

        if (warpManager.setWarp(warpName, player)) {
            plugin.getMessageManager().send(player, "setwarp.set", warpName);
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
