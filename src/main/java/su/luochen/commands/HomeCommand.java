package su.luochen.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.HomeManager;
import su.luochen.manager.PermissionManager;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final SuTeleport plugin;
    private final HomeManager homeManager;
    private final CooldownManager cooldownManager;
    private final PermissionManager permissionManager;

    public HomeCommand(SuTeleport plugin, HomeManager homeManager, CooldownManager cooldownManager, PermissionManager permissionManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
        this.cooldownManager = cooldownManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "common.only-player");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.home")) {
            plugin.getMessageManager().send(player, "common.no-permission");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageManager().send(player, "home.usage");
            return true;
        }

        String homeName = args[0];

        if (!homeManager.homeExists(homeName, player)) {
            plugin.getMessageManager().send(player, "home.not-exist", homeName);
            return true;
        }

        // 检查冷却
        if (!cooldownManager.checkCooldown(player, "home")) {
            return true;
        }

        if (homeManager.teleportToHome(homeName, player)) {
            plugin.getMessageManager().send(player, "home.teleported", homeName);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1 && sender instanceof Player player) {
            String input = args[0].toLowerCase();
            for (String home : homeManager.getHomeNames(player)) {
                if (home.toLowerCase().startsWith(input)) {
                    completions.add(home);
                }
            }
        }
        return completions;
    }
}
