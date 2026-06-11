package net.jinxd.velguard.check.movement;

import net.jinxd.velguard.VelGuard;
import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.Check;
import net.jinxd.velguard.check.CheckType;
import net.jinxd.velguard.data.PlayerData;
import net.jinxd.velguard.punishment.PunishmentManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class FlyCheck extends Check {

    private final int maxAirEvents;
    private final double vlPerFlag;
    private final double vlAlert;
    private final double vlPunish;

    public FlyCheck(AlertManager alertManager, PunishmentManager punishmentManager, VelGuard plugin) {
        super(alertManager, punishmentManager);
        this.maxAirEvents = plugin.getConfig().getInt("checks.fly.max-air-events", 20);
        this.vlPerFlag    = plugin.getConfig().getDouble("checks.fly.vl-per-flag", 2.5);
        this.vlAlert      = plugin.getConfig().getDouble("checks.fly.vl-alert", 5.0);
        this.vlPunish     = plugin.getConfig().getDouble("checks.fly.vl-punish", 15.0);
    }

    public void check(Player player, PlayerData data, Location from, Location to) {
        GameMode gm = player.getGameMode();
        if (gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR) {
            data.resetAirEvents();
            return;
        }

        if (player.isFlying() || player.isGliding() || player.isInWater()
                || player.hasPotionEffect(PotionEffectType.LEVITATION)
                || player.hasPotionEffect(PotionEffectType.SLOW_FALLING)
                || isOnClimbable(player)) {
            data.resetAirEvents();
            return;
        }

        boolean grounded = isServerSideGrounded(player);
        double deltaY = to.getY() - from.getY();

        if (grounded) {
            data.resetAirEvents();
            data.setLastDeltaY(0.0);
            return;
        }

        data.incrementAirEvents();
        data.setLastDeltaY(deltaY);

        if (data.getAirEvents() > maxAirEvents) {
            double vl = data.addVl(CheckType.FLY, vlPerFlag);
            if (vl >= vlAlert) {
                alertManager.flag(player, CheckType.FLY,
                    "air=" + data.getAirEvents(), vl);
            }
            if (vl >= vlPunish) {
                punishmentManager.execute(player, CheckType.FLY);
                data.resetVl(CheckType.FLY);
            }
            data.resetAirEvents();
        }

        if (data.getAirEvents() > 10 && deltaY > 0.04) {
            double vl = data.addVl(CheckType.FLY, vlPerFlag * 1.5);
            if (vl >= vlAlert) {
                alertManager.flag(player, CheckType.FLY,
                    "ascent=+" + String.format("%.3f", deltaY), vl);
            }
            if (vl >= vlPunish) {
                punishmentManager.execute(player, CheckType.FLY);
                data.resetVl(CheckType.FLY);
            }
        }
    }

    private boolean isServerSideGrounded(Player player) {
        Location loc = player.getLocation();
        int checkY = (int) Math.floor(loc.getY() - 0.01);
        double[] corners = {-0.29, 0.29};

        for (double dx : corners) {
            for (double dz : corners) {
                Block b = loc.getWorld().getBlockAt(
                    (int) Math.floor(loc.getX() + dx),
                    checkY,
                    (int) Math.floor(loc.getZ() + dz));
                if (!b.isPassable()) return true;
            }
        }
        return false;
    }

    private boolean isOnClimbable(Player player) {
        Material mat = player.getLocation().getBlock().getType();
        return Tag.CLIMBABLE.isTagged(mat);
    }
}
