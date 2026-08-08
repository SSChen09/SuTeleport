package su.luochen.manager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import su.luochen.SuTeleport;

import java.io.File;

/**
 * 消息管理类：集中管理插件中所有发送给玩家的消息。
 * <p>
 * 所有消息内容定义在独立的 {@code message.yml} 文件中（{@code messages} 节点下），支持：
 * <ul>
 *   <li>{@code &} 颜色代码（自动转换为 § 代码）</li>
 *   <li>{@code {0}} {@code {1}}... 位置占位符（按顺序替换传入的参数）</li>
 * </ul>
 */
public class MessageManager {

    private static final String FILE_NAME = "message.yml";

    private final SuTeleport plugin;
    private FileConfiguration messageConfig;

    public MessageManager(SuTeleport plugin) {
        this.plugin = plugin;
        saveDefaultMessageFile();
        this.messageConfig = loadMessageConfig();
    }

    /**
     * 首次运行时将插件自带的默认 message.yml 复制到数据文件夹。
     */
    private void saveDefaultMessageFile() {
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.exists()) {
            plugin.saveResource(FILE_NAME, false);
        }
    }

    /**
     * 从数据文件夹加载 message.yml。
     */
    private FileConfiguration loadMessageConfig() {
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 重新加载 message.yml（供 /suteleport reload 调用）。
     */
    public void reload() {
        this.messageConfig = loadMessageConfig();
    }

    /**
     * 获取并格式化一条消息。
     *
     * @param key  消息键，对应 message.yml 中 messages.{@code <key>}
     * @param args 按顺序替换 {0}、{1}、{2}... 占位符
     * @return 格式化后的消息文本（含颜色代码），若键不存在则返回错误提示
     */
    public String get(String key, Object... args) {
        String msg = messageConfig.getString("messages." + key);
        if (msg == null) {
            return ChatColor.RED + "消息未定义: " + key;
        }
        for (int i = 0; i < args.length; i++) {
            msg = msg.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * 向命令发送者（玩家/控制台）发送一条消息。
     *
     * @param sender 消息接收者
     * @param key    消息键，对应 message.yml 中 messages.{@code <key>}
     * @param args   按顺序替换 {0}、{1}、{2}... 占位符
     */
    public void send(CommandSender sender, String key, Object... args) {
        if (sender != null) {
            sender.sendMessage(get(key, args));
        }
    }
}
