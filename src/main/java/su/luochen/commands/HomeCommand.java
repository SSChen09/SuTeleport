package su.luochen.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.HomeManager;
import su.luochen.manager.PermissionManager;

public class HomeCommand implements CommandExecutor {

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
            sender.sendMessage("§c只有玩家才能使用此命令！");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.home")) {
            player.sendMessage("§c你没有权限使用此命令！");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§c用法: /home <家名称>");
            return true;
        }

        String homeName = args[0];

        if (!homeManager.homeExists(homeName, player)) {
            player.sendMessage("§c家 " + homeName + " 不存在！");
            return true;
        }

        // 检查冷却
        if (!cooldownManager.checkCooldown(player, "home")) {
            return true;
        }

        if (homeManager.teleportToHome(homeName, player)) {
            player.sendMessage("§a已传送到家 " + homeName + "！");
        }

        return true;
    }
}
