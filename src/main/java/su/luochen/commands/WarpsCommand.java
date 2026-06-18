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
            sender.sendMessage("§e当前没有任何传送点。");
            return true;
        }

        sender.sendMessage("§6=== 传送点列表 ===");
        for (String warp : warps) {
            sender.sendMessage("§e- §f" + warp);
        }
        sender.sendMessage("§6==================");

        return true;
    }
}
