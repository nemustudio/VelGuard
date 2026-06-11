package net.jinxd.velguard.check.movement;

import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.Check;
import net.jinxd.velguard.check.CheckType;
import net.jinxd.velguard.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SpeedCheck extends Check {

    private final int violationLimit;
    private final double baseMax;

    public SpeedCheck(AlertManager alertManager, int violationLimit, double baseMax) {
        super(alertManager);
        this.violationLimit = violationLimit;
        this.baseMax = baseMax;
    }

    public void check(Player player, PlayerData data, Location from, Location to) {
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            data.resetSpeedViolations();
            return;
        }
        if (player.isInsideVehicle()) return;

        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        double max = getMax(player);
        if (dist > max) {
            data.incrementSpeedViolations();
            if (data.getSpeedViolations() >= violationLimit) {
                alertManager.flag(player, CheckType.SPEED,
                    String.format("dist=%.3f max=%.3f vl=%d", dist, max, data.getSpeedViolations()));
                data.resetSpeedViolations();
            }
        } else {
            data.decrementSpeedViolations();
        }
    }

    private double getMax(Player player) {
        double max = baseMax;
        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            int amp = player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() + 1;
            max += amp * 0.085;
        }
        return max;
    }
}
