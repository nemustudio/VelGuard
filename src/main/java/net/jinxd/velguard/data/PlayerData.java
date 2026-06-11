package net.jinxd.velguard.data;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private int airTicks;
    private int speedViolations;
    private boolean wasOnGround = true;
    private boolean expectingFallDamage;
    private long lastAttackTime;
    private float lastYaw;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() { return uuid; }

    public int getAirTicks() { return airTicks; }
    public void incrementAirTicks() { airTicks++; }
    public void resetAirTicks() { airTicks = 0; }

    public int getSpeedViolations() { return speedViolations; }
    public void incrementSpeedViolations() { speedViolations = Math.min(speedViolations + 1, 20); }
    public void decrementSpeedViolations() { if (speedViolations > 0) speedViolations--; }
    public void resetSpeedViolations() { speedViolations = 0; }

    public boolean wasOnGround() { return wasOnGround; }
    public void setWasOnGround(boolean val) { wasOnGround = val; }

    public boolean isExpectingFallDamage() { return expectingFallDamage; }
    public void setExpectingFallDamage(boolean val) { expectingFallDamage = val; }

    public long getLastAttackTime() { return lastAttackTime; }
    public void setLastAttackTime(long t) { lastAttackTime = t; }

    public float getLastYaw() { return lastYaw; }
    public void setLastYaw(float yaw) { lastYaw = yaw; }
}
