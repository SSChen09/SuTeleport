package su.luochen.manager;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import su.luochen.SuTeleport;

import java.io.File;

/**
 * 国际化消息管理类：集中管理插件中所有发送给玩家的消息。
 * <p>
 * 语言文件存放在插件数据目录的 {@code language/} 文件夹下（如 {@code zh_CN.yml}、{@code en_US.yml}），
 * 通过 config.yml 的 {@code language} 键指定全局语言。每个语言文件在 {@code messages} 节点下定义消息，支持：
 * <ul>
 *   <li>{@code &} 颜色代码（自动转换为 § 代码）</li>
 *   <li>{@code {0}} {@code {1}}... 位置占位符（按顺序替换传入的参数）</li>
 * </ul>
 */
public class MessageManager {

    /** 语言文件存放目录 */
    private static final String LANG_FOLDER = "language";
    /** 默认语言（回退语言） */
    private static final String DEFAULT_LANGUAGE = "zh_CN";
    /** 插件内置的语言文件，首次运行时会复制到语言目录 */
    private static final String[] BUILT_IN_LANGUAGES = {"zh_CN.yml", "en_US.yml"};

    private final SuTeleport plugin;
    private String language;
    private FileConfiguration messageConfig;

    public MessageManager(SuTeleport plugin) {
        this.plugin = plugin;
        saveDefaultLanguageFiles();
        this.language = resolveLanguage();
        this.messageConfig = loadMessageConfig();
    }

    /**
     * 首次运行时将插件内置的语言文件复制到数据目录的 language 文件夹。
     */
    private void saveDefaultLanguageFiles() {
        File langFolder = new File(plugin.getDataFolder(), LANG_FOLDER);
        for (String fileName : BUILT_IN_LANGUAGES) {
            File target = new File(langFolder, fileName);
            if (!target.exists()) {
                // 注意：saveResource 读取 jar 内资源，路径须使用 "/" 分隔
                plugin.saveResource(LANG_FOLDER + "/" + fileName, false);
            }
        }
    }

    /**
     * 解析当前语言：读取 config.yml 的 language 键，
     * 若对应语言文件不存在则回退到默认语言。
     *
     * @return 语言代码（如 zh_CN、en_US）
     */
    private String resolveLanguage() {
        String lang = plugin.getConfig().getString("language", DEFAULT_LANGUAGE);
        File file = new File(plugin.getDataFolder(), LANG_FOLDER + File.separator + lang + ".yml");
        if (!file.exists()) {
            plugin.getLogger().warning("语言文件 language/" + lang + ".yml 不存在，已回退到 " + DEFAULT_LANGUAGE + "。");
            return DEFAULT_LANGUAGE;
        }
        return lang;
    }

    /**
     * 从数据目录加载当前语言文件。
     */
    private FileConfiguration loadMessageConfig() {
        File file = new File(plugin.getDataFolder(), LANG_FOLDER + File.separator + language + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 重新加载语言设置与语言文件（供 /suteleport reload 调用）。
     */
    public void reload() {
        this.language = resolveLanguage();
        this.messageConfig = loadMessageConfig();
    }

    /**
     * 获取并格式化一条消息。
     *
     * @param key  消息键，对应语言文件中 messages.{@code <key>}
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
     * @param key    消息键，对应语言文件中 messages.{@code <key>}
     * @param args   按顺序替换 {0}、{1}、{2}... 占位符
     */
    public void send(CommandSender sender, String key, Object... args) {
        if (sender != null) {
            sender.sendMessage(get(key, args));
        }
    }
}
