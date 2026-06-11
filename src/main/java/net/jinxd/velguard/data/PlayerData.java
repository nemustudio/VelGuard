package net.jinxd.velguard.data;

import net.jinxd.velguard.check.CheckType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;

    private final Map<CheckType, Double> vl = new EnumMap<>(CheckType.class);

    private boolean wasOnGround = true;
    private boolean expectingFallDamage;
    private double lastDeltaY;
    private int airEvents;

    private final Deque<Long> attackTimestamps = new ArrayDeque<>();
    private float lastYaw;
    private float lastPitch;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() { return uuid; }

    public double addVl(CheckType type, double amount) {
        double next = vl.getOrDefault(type, 0.0) + amount;
        vl.put(type, next);
        return next;
    }

    public double getVl(CheckType type) {
        return vl.getOrDefault(type, 0.0);
    }

    public void resetVl(CheckType type) {
        vl.put(type, 0.0);
    }

    public void decayVl(double perSecond) {
        vl.replaceAll((t, v) -> Math.max(0.0, v - perSecond));
    }

    public boolean wasOnGround() { return wasOnGround; }
    public void setWasOnGround(boolean val) { wasOnGround = val; }

    public boolean isExpectingFallDamage() { return expectingFallDamage; }
    public void setExpectingFallDamage(boolean val) { expectingFallDamage = val; }

    public double getLastDeltaY() { return lastDeltaY; }
    public void setLastDeltaY(double dy) { lastDeltaY = dy; }

    public int getAirEvents() { return airEvents; }
    public void incrementAirEvents() { airEvents++; }
    public void resetAirEvents() { airEvents = 0; }

    public void recordAttack() {
        long now = System.currentTimeMillis();
        attackTimestamps.addLast(now);
        while (!attackTimestamps.isEmpty() && now - attackTimestamps.peekFirst() > 2000L) {
            attackTimestamps.removeFirst();
        }
    }

    public int getCps() {
        long cutoff = System.currentTimeMillis() - 1000L;
        int n = 0;
        for (long t : attackTimestamps) {
            if (t >= cutoff) n++;
        }
        return n;
    }

    public float getLastYaw()   { return lastYaw; }
    public void setLastYaw(float y) { lastYaw = y; }

    public float getLastPitch()   { return lastPitch; }
    public void setLastPitch(float p) { lastPitch = p; }
}
