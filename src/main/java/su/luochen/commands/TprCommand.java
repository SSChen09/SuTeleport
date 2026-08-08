package su.luochen.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;
import su.luochen.manager.CooldownManager;
import su.luochen.manager.PermissionManager;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TprCommand implements CommandExecutor {

    private final SuTeleport plugin;
    private final CooldownManager cooldownManager;
    private final PermissionManager permissionManager;
    private final Random random = new Random();
    private final int maxRadius;
    private final int maxHeight;
    private final int maxAttempts;
    private final boolean allowEnd;
    private final Set<Biome> blockedBiomes;

    public TprCommand(SuTeleport plugin, CooldownManager cooldownManager, PermissionManager permissionManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.permissionManager = permissionManager;
        this.maxRadius = plugin.getConfig().getInt("tpr.max-radius", 5000);
        this.maxHeight = plugin.getConfig().getInt("tpr.max-height", 256);
        this.maxAttempts = plugin.getConfig().getInt("tpr.max-attempts", 50);
        this.allowEnd = plugin.getConfig().getBoolean("tpr.allow-end", false);
        this.blockedBiomes = loadBlockedBiomes();
    }

    /**
     * 从配置文件加载禁止传送的群系列表
     */
    private Set<Biome> loadBlockedBiomes() {
        Set<Biome> biomes = new HashSet<>();
        List<String> names = plugin.getConfig().getStringList("tpr.blocked-biomes");
        for (String name : names) {
            try {
                biomes.add(Biome.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("未知的群系名称: " + name + "，已跳过。");
            }
        }
        return biomes;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "common.only-player");
            return true;
        }

        if (!permissionManager.hasPermission(player, "suteleport.tpr")) {
            plugin.getMessageManager().send(player, "common.no-permission");
            return true;
        }

        // 检查是否在末地
        if (!allowEnd && player.getWorld().getEnvironment() == World.Environment.THE_END) {
            plugin.getMessageManager().send(player, "tpr.no-end");
            return true;
        }

        // 检查冷却
        if (!cooldownManager.checkCooldown(player, "tpr")) {
            return true;
        }

        plugin.getMessageManager().send(player, "tpr.searching");

        // 异步寻找安全位置
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Location safeLoc = findSafeLocation(player);
            if (safeLoc != null) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    plugin.getPreviousLocationManager().saveLocation(player);
                    player.teleport(safeLoc);
                    plugin.getMessageManager().send(player, "tpr.success");
                    plugin.getMessageManager().send(player, "tpr.coords", safeLoc.getBlockX(), safeLoc.getBlockY(), safeLoc.getBlockZ());
                });
            } else {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    plugin.getMessageManager().send(player, "tpr.fail");
                });
            }
        });

        return true;
    }

    private Location findSafeLocation(Player player) {
        World world = player.getWorld();
        int centerX = player.getLocation().getBlockX();
        int centerZ = player.getLocation().getBlockZ();

        // 末地含有大片虚空，搜索半径乘以5
        int radius = world.getEnvironment() == World.Environment.THE_END
                ? maxRadius * 5 : maxRadius;

        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            int x = centerX + random.nextInt(radius * 2) - radius;
            int z = centerZ + random.nextInt(radius * 2) - radius;
            int y = world.getHighestBlockYAt(x, z);

            if (y < maxHeight && y > 0) {
                Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
                if (isSafeLocation(loc)) {
                    return loc;
                }
            }
        }
        return null;
    }

    private boolean isSafeLocation(Location loc) {
        World world = loc.getWorld();
        if (world == null) return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        // 检查是否在禁止传送的群系中
        if (!blockedBiomes.isEmpty() && blockedBiomes.contains(world.getBiome(x, y, z))) {
            return false;
        }

        Material feet = world.getBlockAt(x, y, z).getType();
        Material below = world.getBlockAt(x, y - 1, z).getType();

        // 检查脚下是否安全（非空气、非岩浆、非火焰等）
        if (below.isAir() || below == Material.LAVA || below == Material.FIRE) {
            return false;
        }

        // 检查是否在水中
        if (feet == Material.WATER || feet == Material.KELP || feet == Material.KELP_PLANT
                || feet == Material.SEAGRASS || feet == Material.TALL_SEAGRASS
                || feet == Material.BUBBLE_COLUMN) {
            return false;
        }
        if (below == Material.WATER || below == Material.KELP || below == Material.KELP_PLANT
                || below == Material.SEAGRASS || below == Material.TALL_SEAGRASS
                || below == Material.BUBBLE_COLUMN) {
            return false;
        }

        // 检查脚和头的位置是否可通行
        if (!feet.isAir() && !feet.isTransparent()) {
            return false;
        }

        Material head = world.getBlockAt(x, y + 1, z).getType();
        if (!head.isAir() && !head.isTransparent()) {
            return false;
        }

        return true;
    }
}
