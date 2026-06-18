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
     */
    public boolean hasPermission(org.bukkit.command.CommandSender sender, String permission) {
        if (!(sender instanceof Player player)) {
            // Console 拥有所有权限
            return true;
        }

        if (!luckPermsEnabled || luckPerms == null) {
            return player.hasPermission(permission);
        }

        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return player.hasPermission(permission);
            }
            return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        } catch (Exception e) {
            plugin.getLogger().warning("检查权限时出错: " + e.getMessage());
            return player.hasPermission(permission);
        }
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
