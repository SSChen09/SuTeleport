package su.luochen.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.HomeManager;

import java.util.List;

public class HomesCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final HomeManager homeManager;

    public HomesCommand(SuTeleport plugin, HomeManager homeManager) {
        this.plugin = plugin;
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "common.only-player");
            return true;
        }

        List<String> homes = homeManager.getHomeNames(player);

        if (homes.isEmpty()) {
            plugin.getMessageManager().send(player, "homes.empty");
            plugin.getMessageManager().send(player, "homes.hint");
            return true;
        }

        int max = homeManager.getMaxHomes();
        plugin.getMessageManager().send(player, "homes.header", homes.size(), max);
        for (String home : homes) {
            plugin.getMessageManager().send(player, "homes.entry", home);
        }
        plugin.getMessageManager().send(player, "homes.footer");

        return true;
    }
}
