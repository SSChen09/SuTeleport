package su.luochen.manager;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;

import java.util.Set;

public class PermissionManager {

    private final SuTeleport plugin;
    private LuckPerms luckPerms;
    private final boolean luckPermsEnabled;

    public PermissionManager(SuTeleport plugin) {
        this.plugin = plugin;
        this.luckPermsEnabled = Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
        if (luckPermsEnabled) {
            try {
                this.luckPerms = LuckPermsProvider.get();
                plugin.getLogger().info("LuckPerms 已检测到并启用！");
            } catch (IllegalStateException e) {
                plugin.getLogger().warning("LuckPerms API 获取失败，将使用默认权限系统。");
                this.luckPerms = null;
            }
        } else {
            plugin.getLogger().info("未检测到 LuckPerms，将使用默认权限系统。");
        }
    }

    /**
     * 检查玩家是否有指定权限（支持 CommandSender）
     * 兼容 Geyser 基岩版互通玩家：
     * - 如果 LuckPerms 有该玩家数据，使用 LuckPerms 检查
     * - 如果 LuckPerms 没有该玩家数据（如 Geyser 基岩版玩家），回退到 Bukkit 默认权限
     */
    public boolean hasPermission(org.bukkit.command.CommandSender sender, String permission) {
        if (!(sender instanceof Player player)) {
            // Console 拥有所有权限
            return true;
        }

        // 没有 LuckPerms 时直接使用 Bukkit 权限
        if (!luckPermsEnabled || luckPerms == null) {
            return player.hasPermission(permission);
        }

        try {
            // 先尝试从缓存获取用户
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());

            // 缓存中没有，尝试同步加载（兼容 Geyser 基岩版玩家）
            if (user == null) {
                plugin.getLogger().fine("LuckPerms 缓存中未找到玩家 " + player.getName() + "，尝试加载...");
                user = luckPerms.getUserManager().loadUser(player.getUniqueId()).join();
            }

            // 加载成功，使用 LuckPerms 检查权限
            if (user != null) {
                return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("LuckPerms 权限检查出错: " + e.getMessage());
        }

        // LuckPerms 完全无法处理该玩家时，回退到 Bukkit 默认权限系统
        return player.hasPermission(permission);
    }

    /**
     * 获取玩家的主组名
     */
    public String getPrimaryGroup(Player player) {
        if (!luckPermsEnabled || luckPerms == null) {
            return "default";
        }

        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return "default";
            }
            return user.getPrimaryGroup();
        } catch (Exception e) {
            plugin.getLogger().warning("获取玩家组时出错: " + e.getMessage());
            return "default";
        }
    }

    /**
     * 获取玩家所在组的所有权限
     */
    public Set<String> getGroupPermissions(Player player) {
        if (!luckPermsEnabled || luckPerms == null) {
            return java.util.Collections.emptySet();
        }

        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return java.util.Collections.emptySet();
            }
            return user.getCachedData().getPermissionData().getPermissionMap().keySet();
        } catch (Exception e) {
            plugin.getLogger().warning("获取玩家权限时出错: " + e.getMessage());
            return java.util.Collections.emptySet();
        }
    }

    /**
     * 检查 LuckPerms 是否已启用
     */
    public boolean isLuckPermsEnabled() {
        return luckPermsEnabled && luckPerms != null;
    }
}
