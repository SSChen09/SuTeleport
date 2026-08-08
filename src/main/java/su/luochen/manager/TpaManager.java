package su.luochen.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.luochen.SuTeleport;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {

    private final SuTeleport plugin;
    private final PreviousLocationManager previousLocationManager;
    // key: 请求接收者UUID, value: TpaRequest
    private final Map<UUID, TpaRequest> pendingRequests = new HashMap<>();
    private final long requestTimeoutTicks;

    public TpaManager(SuTeleport plugin, PreviousLocationManager previousLocationManager) {
        this.plugin = plugin;
        this.previousLocationManager = previousLocationManager;
        int timeoutSeconds = plugin.getConfig().getInt("tpa.timeout", 60);
        this.requestTimeoutTicks = timeoutSeconds * 20L;
    }

    /**
     * 发送传送请求
     * @param sender 发送者
     * @param target 目标玩家
     * @param here true表示tpahere(把目标传送到自己), false表示tpa(自己传送到目标)
     */
    public void sendRequest(Player sender, Player target, boolean here) {
        UUID targetId = target.getUniqueId();

        // 取消该目标之前的请求
        if (pendingRequests.containsKey(targetId)) {
            cancelRequest(targetId);
        }

        TpaRequest request = new TpaRequest(sender.getUniqueId(), targetId, here);
        pendingRequests.put(targetId, request);

        // 60秒后自动过期
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingRequests.containsKey(targetId)
                    && pendingRequests.get(targetId).equals(request)) {
                pendingRequests.remove(targetId);
                Player s = Bukkit.getPlayer(sender.getUniqueId());
                Player t = Bukkit.getPlayer(targetId);
                if (s != null) {
                    plugin.getMessageManager().send(s, "tpa.timeout-sender");
                }
                if (t != null) {
                    plugin.getMessageManager().send(t, "tpa.timeout-target", sender.getName());
                }
            }
        }, requestTimeoutTicks);
    }

    /**
     * 接受传送请求
     * @param target 接受请求的玩家
     * @return 是否成功接受
     */
    public boolean acceptRequest(Player target) {
        TpaRequest request = pendingRequests.remove(target.getUniqueId());
        if (request == null) {
            return false;
        }

        Player sender = Bukkit.getPlayer(request.getSender());
        if (sender == null || !sender.isOnline()) {
            plugin.getMessageManager().send(target, "tpa.sender-offline");
            return false;
        }

        if (request.isHere()) {
            // tpahere: 把发送者传送到目标（目标来到发送者这里）
            previousLocationManager.saveLocation(target);
            target.teleport(sender.getLocation());
            plugin.getMessageManager().send(sender, "tpa.accept-here", target.getName());
            plugin.getMessageManager().send(target, "tpa.accepted-target", sender.getName());
        } else {
            // tpa: 发送者传送到目标
            previousLocationManager.saveLocation(sender);
            sender.teleport(target.getLocation());
            plugin.getMessageManager().send(sender, "tpa.accepted", target.getName());
            plugin.getMessageManager().send(target, "tpa.accept-target", sender.getName());
        }

        return true;
    }

    /**
     * 拒绝传送请求
     * @param target 拒绝请求的玩家
     * @return 是否成功拒绝
     */
    public boolean denyRequest(Player target) {
        TpaRequest request = pendingRequests.remove(target.getUniqueId());
        if (request == null) {
            return false;
        }

        Player sender = Bukkit.getPlayer(request.getSender());
        if (sender != null && sender.isOnline()) {
            plugin.getMessageManager().send(sender, "tpa.denied", target.getName());
        }
        plugin.getMessageManager().send(target, "tpa.deny-self");

        return true;
    }

    private void cancelRequest(UUID targetId) {
        TpaRequest old = pendingRequests.remove(targetId);
        if (old != null) {
            Player sender = Bukkit.getPlayer(old.getSender());
            if (sender != null && sender.isOnline()) {
                plugin.getMessageManager().send(sender, "tpa.overridden");
            }
        }
    }

    public boolean hasRequest(UUID targetId) {
        return pendingRequests.containsKey(targetId);
    }
}
