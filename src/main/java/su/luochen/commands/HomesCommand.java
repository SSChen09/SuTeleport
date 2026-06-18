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
            sender.sendMessage("§c只有玩家才能使用此命令！");
            return true;
        }

        List<String> homes = homeManager.getHomeNames(player);

        if (homes.isEmpty()) {
            player.sendMessage("§e你还没有设置任何家。");
            player.sendMessage("§7使用 /sethome <名称> 来设置一个家。");
            return true;
        }

        int max = homeManager.getMaxHomes();
        player.sendMessage("§6=== 你的家 (" + homes.size() + "/" + max + ") ===");
        for (String home : homes) {
            player.sendMessage("§e- §f" + home);
        }
        player.sendMessage("§6========================");

        return true;
    }
}
