package su.luochen.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import su.luochen.SuTeleport;

public class ReloadCommand implements CommandExecutor {

    private final SuTeleport plugin;

    public ReloadCommand(SuTeleport plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
            plugin.getMessageManager().send(sender, "reload.usage");
            return true;
        }

        if (!plugin.getPermissionManager().hasPermission(sender, "suteleport.reload")) {
            plugin.getMessageManager().send(sender, "reload.no-permission");
            return true;
        }

        // 重载配置
        plugin.reloadConfig();

        // 重新加载消息配置
        plugin.getMessageManager().reload();

        // 重新加载 TPA 管理器
        plugin.reloadTpaManager();

        // 重新加载 TPR 命令
        plugin.reloadTprCommand();

        // 重新加载冷却管理器
        plugin.reloadCooldownManager();

        plugin.getMessageManager().send(sender, "reload.success");
        plugin.getLogger().info("配置已被 " + sender.getName() + " 重载。");

        return true;
    }
}
