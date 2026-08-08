package su.luochen.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.PermissionManager;
import su.luochen.manager.PreviousLocationManager;

public class SBackCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final CooldownManager cooldownManager;
    private final PermissionManager permissionManager;
    private final PreviousLocationManager previousLocationManager;

    public SBackCommand(SuTeleport plugin, CooldownManager cooldownManager, PermissionManager permissionManager, PreviousLocationManager previousLocationManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.permissionManager = permissionManager;
        this.previousLocationManager = previousLocationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "common.only-player");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.sback")) {
            plugin.getMessageManager().send(player, "common.no-permission");
            return true;
        }

        if (!previousLocationManager.hasPreviousLocation(player)) {
            plugin.getMessageManager().send(player, "sback.no-location");
            return true;
        }

        // 检查冷却
        if (!cooldownManager.checkCooldown(player, "sback")) {
            return true;
        }

        Location prevLoc = previousLocationManager.getPreviousLocation(player);

        // 清除记录
        previousLocationManager.clearLocation(player);

        // 传送
        player.teleport(prevLoc);
        plugin.getMessageManager().send(player, "sback.success");

        return true;
    }
}
