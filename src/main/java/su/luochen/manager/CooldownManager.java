package su.luochen.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final SuTeleport plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    private final Map<String, Integer> cooldownTimes = new HashMap<>();

    public CooldownManager(SuTeleport plugin) {
        this.plugin = plugin;
        loadCooldowns();
    }

    private void loadCooldowns() {
        FileConfiguration config = plugin.getConfig();
        cooldownTimes.put("tpa", config.getInt("cooldown.tpa", 0));
        cooldownTimes.put("tpahere", config.getInt("cooldown.tpahere", 0));
        cooldownTimes.put("tpaccept", config.getInt("cooldown.tpaccept", 0));
        cooldownTimes.put("tpr", config.getInt("cooldown.tpr", 0));
        cooldownTimes.put("warp", config.getInt("cooldown.warp", 0));
        cooldownTimes.put("home", config.getInt("cooldown.home", 0));
        cooldownTimes.put("back", config.getInt("cooldown.back", 0));
        cooldownTimes.put("sback", config.getInt("cooldown.sback", 0));
    }

    /**
     * 检查玩家是否在冷却中
     * @return 剩余冷却时间（秒），0表示不在冷却中
     */
    public int getRemainingCooldown(Player player, String command) {
        UUID uuid = player.getUniqueId();
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null || !playerCooldowns.containsKey(command)) {
            return 0;
        }

        long lastUsed = playerCooldowns.get(command);
        int cooldownTime = cooldownTimes.getOrDefault(command, 0);
        if (cooldownTime <= 0) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long elapsed = (currentTime - lastUsed) / 1000;
        int remaining = cooldownTime - (int) elapsed;

        return Math.max(remaining, 0);
    }

    /**
     * 设置玩家的冷却时间
     */
    public void setCooldown(Player player, String command) {
        UUID uuid = player.getUniqueId();
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(command, System.currentTimeMillis());
    }

    /**
     * 检查并应用冷却
     * @return true表示可以使用，false表示在冷却中
     */
    public boolean checkCooldown(Player player, String command) {
        int remaining = getRemainingCooldown(player, command);
        if (remaining > 0) {
            plugin.getMessageManager().send(player, "cooldown.remaining", remaining);
            return false;
        }
        setCooldown(player, command);
        return true;
    }

    /**
     * 重新加载冷却配置
     */
    public void reloadCooldowns() {
        cooldownTimes.clear();
        loadCooldowns();
    }
}
