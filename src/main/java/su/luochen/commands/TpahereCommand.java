package su.luochen.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.PermissionManager;

public class TpahereCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final CooldownManager cooldownManager;
    private final PermissionManager permissionManager;

    public TpahereCommand(SuTeleport plugin, CooldownManager cooldownManager, PermissionManager permissionManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§c只有玩家才能使用此命令！");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.tpahere")) {
            player.sendMessage("§c你没有权限使用此命令！");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§c用法: /tpahere <玩家名>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§c玩家 " + args[0] + " 不在线！");
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("§c你不能向自己发送传送请求！");
            return true;
        }

        // 检查冷却
        if (!cooldownManager.checkCooldown(player, "tpahere")) {
            return true;
        }

        plugin.getTpaManager().sendRequest(player, target, true);

        player.sendMessage("§a你已向 " + target.getName() + " 发送传送请求（传送到你这里）！");
        target.sendMessage("§e" + player.getName() + " §6请求你传送到他的位置！");
        target.sendMessage("§e输入 §a/tpaccept §e接受 或 §c/tpdeny §e拒绝（60秒后过期）");

        return true;
    }
}
