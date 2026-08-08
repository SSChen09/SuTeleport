package su.luochen.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.PermissionManager;
import su.luochen.manager.WarpManager;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {

    private final SuTeleport plugin;
    private final WarpManager warpManager;
    private final CooldownManager cooldownManager;
    private final PermissionManager permissionManager;

    public WarpCommand(SuTeleport plugin, WarpManager warpManager, CooldownManager cooldownManager, PermissionManager permissionManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.cooldownManager = cooldownManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "common.only-player");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.warp")) {
            plugin.getMessageManager().send(player, "common.no-permission");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageManager().send(player, "warp.usage");
            return true;
        }

        String warpName = args[0];

        if (!warpManager.warpExists(warpName)) {
            plugin.getMessageManager().send(player, "warp.not-exist", warpName);
            return true;
        }

        // 检查冷却
        if (!cooldownManager.checkCooldown(player, "warp")) {
            return true;
        }

        if (warpManager.teleportToWarp(warpName, player)) {
            plugin.getMessageManager().send(player, "warp.teleported", warpName);
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
