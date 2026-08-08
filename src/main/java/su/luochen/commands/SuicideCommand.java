package su.luochen.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.PermissionManager;

public class SuicideCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final PermissionManager permissionManager;

    public SuicideCommand(SuTeleport plugin, PermissionManager permissionManager) {
        this.plugin = plugin;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "common.only-player");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.suicide")) {
            plugin.getMessageManager().send(player, "common.no-permission");
            return true;
        }

        player.setHealth(0);
        plugin.getMessageManager().send(player, "suicide.message");
        return true;
    }
}
