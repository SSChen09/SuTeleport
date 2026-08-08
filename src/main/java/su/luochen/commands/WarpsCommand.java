package su.luochen.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import su.luochen.SuTeleport;
import su.luochen.manager.WarpManager;

import java.util.List;

public class WarpsCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final WarpManager warpManager;

    public WarpsCommand(SuTeleport plugin, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> warps = warpManager.getWarpNames();

        if (warps.isEmpty()) {
            plugin.getMessageManager().send(sender, "warps.empty");
            return true;
        }

        plugin.getMessageManager().send(sender, "warps.header");
        for (String warp : warps) {
            plugin.getMessageManager().send(sender, "warps.entry", warp);
        }
        plugin.getMessageManager().send(sender, "warps.footer");

        return true;
    }
}
