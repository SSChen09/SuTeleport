package su.luochen.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HomeManager {

    private final SuTeleport plugin;
    private final PreviousLocationManager previousLocationManager;
    private final File homeFile;
    private FileConfiguration homeConfig;
    private final int maxHomes;

    public HomeManager(SuTeleport plugin, PreviousLocationManager previousLocationManager) {
        this.plugin = plugin;
        this.previousLocationManager = previousLocationManager;
        this.homeFile = new File(plugin.getDataFolder(), "homes.yml");
        this.maxHomes = plugin.getConfig().getInt("home.max-homes", 3);
        loadHomes();
    }

    private void loadHomes() {
        if (!homeFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                homeFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建 homes.yml: " + e.getMessage());
            }
        }
        homeConfig = YamlConfiguration.loadConfiguration(homeFile);
    }

    private void saveHomes() {
        try {
            homeConfig.save(homeFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存 homes.yml: " + e.getMessage());
        }
    }

    /**
     * 设置家
     */
    public boolean setHome(String name, Player player) {
        UUID uuid = player.getUniqueId();
        String path = "homes." + uuid + "." + name.toLowerCase();

        // 检查是否超过最大数量限制
        if (!homeConfig.contains(path)) {
            int currentHomes = getHomeCount(player);
            if (currentHomes >= maxHomes) {
                player.sendMessage("§c你已达到最大数量限制（" + maxHomes + " 个）！");
                return false;
            }
        }

        Location loc = player.getLocation();
        homeConfig.set(path + ".world", loc.getWorld().getName());
        homeConfig.set(path + ".x", loc.getX());
        homeConfig.set(path + ".y", loc.getY());
        homeConfig.set(path + ".z", loc.getZ());
        homeConfig.set(path + ".yaw", loc.getYaw());
        homeConfig.set(path + ".pitch", loc.getPitch());
        saveHomes();
        return true;
    }

    /**
     * 传送到指定的家
     */
    public boolean teleportToHome(String name, Player player) {
        UUID uuid = player.getUniqueId();
        String path = "homes." + uuid + "." + name.toLowerCase();

        if (!homeConfig.contains(path)) {
            return false;
        }

        String worldName = homeConfig.getString(path + ".world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage("§c世界 " + worldName + " 不存在！");
            return false;
        }

        double x = homeConfig.getDouble(path + ".x");
        double y = homeConfig.getDouble(path + ".y");
        double z = homeConfig.getDouble(path + ".z");
        float yaw = (float) homeConfig.getDouble(path + ".yaw");
        float pitch = (float) homeConfig.getDouble(path + ".pitch");

        Location loc = new Location(world, x, y, z, yaw, pitch);
        previousLocationManager.saveLocation(player);
        player.teleport(loc);
        return true;
    }

    /**
     * 删除家
     */
    public boolean deleteHome(String name, Player player) {
        UUID uuid = player.getUniqueId();
        String path = "homes." + uuid + "." + name.toLowerCase();

        if (!homeConfig.contains(path)) {
            return false;
        }

        homeConfig.set(path, null);
        saveHomes();
        return true;
    }

    /**
     * 获取玩家的所有家名称
     */
    public List<String> getHomeNames(Player player) {
        UUID uuid = player.getUniqueId();
        ConfigurationSection section = homeConfig.getConfigurationSection("homes." + uuid);
        if (section == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(section.getKeys(false));
    }

    /**
     * 获取玩家的家数量
     */
    public int getHomeCount(Player player) {
        UUID uuid = player.getUniqueId();
        ConfigurationSection section = homeConfig.getConfigurationSection("homes." + uuid);
        return section != null ? section.getKeys(false).size() : 0;
    }

    /**
     * 检查家是否存在
     */
    public boolean homeExists(String name, Player player) {
        UUID uuid = player.getUniqueId();
        return homeConfig.contains("homes." + uuid + "." + name.toLowerCase());
    }

    /**
     * 获取最大家园数量限制
     */
    public int getMaxHomes() {
        return maxHomes;
    }
}
