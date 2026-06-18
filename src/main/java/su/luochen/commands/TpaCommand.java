package su.luochen.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.PermissionManager;

public class TpaCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final CooldownManager cooldownManager;
    private final PermissionManager permissionManager;

    public TpaCommand(SuTeleport plugin, CooldownManager cooldownManager, PermissionManager permissionManager) {
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

        if (!permissionManager.hasPermission(player, "suteleport.tpa")) {
            player.sendMessage("§c你没有权限使用此命令！");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§c用法: /tpa <玩家名>");
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
        if (!cooldownManager.checkCooldown(player, "tpa")) {
            return true;
        }

        plugin.getTpaManager().sendRequest(player, target, false);

        player.sendMessage("§a你已向 " + target.getName() + " 发送传送请求！");

        // 发送可点击的传送请求消息给目标
        int timeout = plugin.getConfig().getInt("tpa.timeout", 60);
        TextComponent requestMsg = new TextComponent(new ComponentBuilder("§e" + player.getName() + " §6请求传送到你的位置！ ")
                .append("§a[接受]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§a点击接受传送请求")))
                .append(" §c[拒绝]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§c点击拒绝传送请求")))
                .append("§7（" + timeout + "秒后过期）")
                .create());
        target.spigot().sendMessage(requestMsg);

        return true;
    }
}
