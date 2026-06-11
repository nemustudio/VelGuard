package net.jinxd.velguard.check.movement;

import net.jinxd.velguard.VelGuard;
import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.Check;
import net.jinxd.velguard.check.CheckType;
import net.jinxd.velguard.data.PlayerData;
import net.jinxd.velguard.punishment.PunishmentManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SpeedCheck extends Check {

    private final double baseMax;
    private final double vlPerFlag;
    private final double vlAlert;
    private final double vlPunish;

    public SpeedCheck(AlertManager alertManager, PunishmentManager punishmentManager, VelGuard plugin) {
        super(alertManager, punishmentManager);
        this.baseMax   = plugin.getConfig().getDouble("checks.speed.base-max", 0.56);
        this.vlPerFlag = plugin.getConfig().getDouble("checks.speed.vl-per-flag", 1.5);
        this.vlAlert   = plugin.getConfig().getDouble("checks.speed.vl-alert", 6.0);
        this.vlPunish  = plugin.getConfig().getDouble("checks.speed.vl-punish", 20.0);
    }

    public void check(Player player, PlayerData data, Location from, Location to) {
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            data.resetVl(CheckType.SPEED);
            return;
        }
        if (player.isInsideVehicle()) return;

        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        double max = getMax(player);

        if (dist > max) {
            double vl = data.addVl(CheckType.SPEED, vlPerFlag);
            if (vl >= vlAlert) {
                alertManager.flag(player, CheckType.SPEED,
                    String.format("dist=%.3f max=%.3f", dist, max), vl);
            }
            if (vl >= vlPunish) {
                punishmentManager.execute(player, CheckType.SPEED);
                data.resetVl(CheckType.SPEED);
            }
        } else {
            data.decayVl(vlPerFlag * 0.5);
        }
    }

    private double getMax(Player player) {
        double max = baseMax;
        if (player.hasPotionEffect(PotionEffectType.SPEED)) {
            int amp = player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() + 1;
            max += amp * 0.085;
        }
        int ping = Math.min(player.getPing(), 200);
        max += ping * 0.0003;
        return max;
    }
}
