package su.luochen.manager;

import java.util.UUID;

public class TpaRequest {

    private final UUID sender;
    private final UUID target;
    private final boolean here;
    private final long timestamp;

    public TpaRequest(UUID sender, UUID target, boolean here) {
        this.sender = sender;
        this.target = target;
        this.here = here;
        this.timestamp = System.currentTimeMillis();
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getTarget() {
        return target;
    }

    public boolean isHere() {
        return here;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
