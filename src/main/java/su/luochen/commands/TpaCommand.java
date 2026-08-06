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
            sender.sendMessage("§c只有玩家才能使用此命令！");
            return true;
        }

        String permNode = here ? "suteleport.tpahere" : "suteleport.tpa";
        if (!permissionManager.hasPermission(player, permNode)) {
            player.sendMessage("§c你没有权限使用此命令！");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§c用法: /" + label + " <玩家名>");
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
        if (!cooldownManager.checkCooldown(player, here ? "tpahere" : "tpa")) {
            return true;
        }

        plugin.getTpaManager().sendRequest(player, target, here);

        if (here) {
            player.sendMessage("§a你已向 " + target.getName() + " 发送传送请求（传送到你这里）！");
        } else {
            player.sendMessage("§a你已向 " + target.getName() + " 发送传送请求！");
        }

        // 发送可点击的传送请求消息给目标
        int timeout = plugin.getConfig().getInt("tpa.timeout", 60);
        String requestText = here
                ? "§e" + player.getName() + " §6请求你传送到他的位置！ "
                : "§e" + player.getName() + " §6请求传送到你的位置！ ";
        TextComponent requestMsg = new TextComponent(new ComponentBuilder(requestText)
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
