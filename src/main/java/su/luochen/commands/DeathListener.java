package su.luochen.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import su.luochen.manager.DeathLocationManager;

public class DeathListener implements Listener {

    private final DeathLocationManager deathLocationManager;

    public DeathListener(DeathLocationManager deathLocationManager) {
        this.deathLocationManager = deathLocationManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Material type = player.getLocation().getBlock().getType();
        boolean burnDeath = type == Material.LAVA || type == Material.FIRE || type == Material.SOUL_FIRE;
        deathLocationManager.setDeathLocation(player, player.getLocation().clone(), burnDeath);
        player.sendMessage("§e你的死亡位置已记录！使用 §a/back §e回到死亡点。");
    }
}
