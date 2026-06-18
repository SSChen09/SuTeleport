package su.luochen.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WarpManager {

    private final SuTeleport plugin;
    private final PreviousLocationManager previousLocationManager;
    private final File warpFile;
    private FileConfiguration warpConfig;

    public WarpManager(SuTeleport plugin, PreviousLocationManager previousLocationManager) {
        this.plugin = plugin;
        this.previousLocationManager = previousLocationManager;
        this.warpFile = new File(plugin.getDataFolder(), "warps.yml");
        loadWarps();
    }

    private void loadWarps() {
        if (!warpFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                warpFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建 warps.yml: " + e.getMessage());
            }
        }
        warpConfig = YamlConfiguration.loadConfiguration(warpFile);
    }

    private void saveWarps() {
        try {
            warpConfig.save(warpFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存 warps.yml: " + e.getMessage());
        }
    }

    /**
     * 设置传送点
     */
    public boolean setWarp(String name, Player player) {
        Location loc = player.getLocation();
        String path = "warps." + name.toLowerCase();
        warpConfig.set(path + ".world", loc.getWorld().getName());
        warpConfig.set(path + ".x", loc.getX());
        warpConfig.set(path + ".y", loc.getY());
        warpConfig.set(path + ".z", loc.getZ());
        warpConfig.set(path + ".yaw", loc.getYaw());
        warpConfig.set(path + ".pitch", loc.getPitch());
        saveWarps();
        return true;
    }

    /**
     * 传送到指定传送点
     */
    public boolean teleportToWarp(String name, Player player) {
        String path = "warps." + name.toLowerCase();
        if (!warpConfig.contains(path)) {
            return false;
        }

        String worldName = warpConfig.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage("§c世界 " + worldName + " 不存在！");
            return false;
        }

        double x = warpConfig.getDouble(path + ".x");
        double y = warpConfig.getDouble(path + ".y");
        double z = warpConfig.getDouble(path + ".z");
        float yaw = (float) warpConfig.getDouble(path + ".yaw");
        float pitch = (float) warpConfig.getDouble(path + ".pitch");

        Location loc = new Location(world, x, y, z, yaw, pitch);
        previousLocationManager.saveLocation(player);
        player.teleport(loc);
        return true;
    }

    /**
     * 删除传送点
     */
    public boolean deleteWarp(String name) {
        String path = "warps." + name.toLowerCase();
        if (!warpConfig.contains(path)) {
            return false;
        }
        warpConfig.set(path, null);
        saveWarps();
        return true;
    }

    /**
     * 获取所有传送点名称
     */
    public List<String> getWarpNames() {
        Set<String> keys = warpConfig.getConfigurationSection("warps") != null ?
                warpConfig.getConfigurationSection("warps").getKeys(false) : null;
        return keys != null ? new ArrayList<>(keys) : new ArrayList<>();
    }

    /**
     * 检查传送点是否存在
     */
    public boolean warpExists(String name) {
        return warpConfig.contains("warps." + name.toLowerCase());
    }
}
