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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.MessageManager;
import su.luochen.manager.PermissionManager;

import java.util.ArrayList;
import java.util.List;

public class TpaCommand implements CommandExecutor, TabCompleter {

    private final SuTeleport plugin;
    private final CooldownManager cooldownManager;
    private final PermissionManager permissionManager;
    private final boolean here;

    /**
     * @param here true = tpahere（目标传送到你）, false = tpa（你传送到目标）
     */
    public TpaCommand(SuTeleport plugin, CooldownManager cooldownManager, PermissionManager permissionManager, boolean here) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.permissionManager = permissionManager;
        this.here = here;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "common.only-player");
            return true;
        }

        String permNode = here ? "suteleport.tpahere" : "suteleport.tpa";
        if (!permissionManager.hasPermission(player, permNode)) {
            plugin.getMessageManager().send(player, "common.no-permission");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageManager().send(player, "tpa.usage", label);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            plugin.getMessageManager().send(player, "tpa.player-offline", args[0]);
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            plugin.getMessageManager().send(player, "tpa.self");
            return true;
        }

        // 检查冷却
        if (!cooldownManager.checkCooldown(player, here ? "tpahere" : "tpa")) {
            return true;
        }

        plugin.getTpaManager().sendRequest(player, target, here);

        if (here) {
            plugin.getMessageManager().send(player, "tpa.sent-here", target.getName());
        } else {
            plugin.getMessageManager().send(player, "tpa.sent", target.getName());
        }

        // 发送可点击的传送请求消息给目标
        int timeout = plugin.getConfig().getInt("tpa.timeout", 60);
        MessageManager messageManager = plugin.getMessageManager();
        String requestText = here
                ? messageManager.get("tpa.request-here", player.getName())
                : messageManager.get("tpa.request-there", player.getName());
        TextComponent requestMsg = new TextComponent(new ComponentBuilder(requestText)
                .append(messageManager.get("tpa.accept-button"))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messageManager.get("tpa.accept-hover"))))
                .append(messageManager.get("tpa.deny-button"))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messageManager.get("tpa.deny-hover"))))
                .append(messageManager.get("tpa.timeout", timeout))
                .create());
        target.spigot().sendMessage(requestMsg);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.getName().toLowerCase().startsWith(input)) {
                    completions.add(online.getName());
                }
            }
        }
        return completions;
    }
}
