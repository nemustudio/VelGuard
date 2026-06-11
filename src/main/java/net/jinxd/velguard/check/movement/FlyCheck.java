package net.jinxd.velguard.check.movement;

import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.Check;
import net.jinxd.velguard.check.CheckType;
import net.jinxd.velguard.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class FlyCheck extends Check {

    private final int airTickLimit;

    public FlyCheck(AlertManager alertManager, int airTickLimit) {
        super(alertManager);
        this.airTickLimit = airTickLimit;
    }

    public void check(Player player, PlayerData data, Location from, Location to) {
        GameMode gm = player.getGameMode();
        if (gm == GameMode.CREATIVE || gm == GameMode.SPECTATOR) {
            data.resetAirTicks();
            return;
        }

        if (player.isFlying() || player.isGliding()
                || player.isInWater()
                || player.hasPotionEffect(PotionEffectType.LEVITATION)
                || player.hasPotionEffect(PotionEffectType.SLOW_FALLING)
                || isOnClimbable(player)) {
            data.resetAirTicks();
            return;
        }

        if (!player.isOnGround()) {
            data.incrementAirTicks();
        } else {
            data.resetAirTicks();
        }

        if (data.getAirTicks() > airTickLimit) {
            alertManager.flag(player, CheckType.FLY, "airTicks=" + data.getAirTicks());
            data.resetAirTicks();
        }
    }

    private boolean isOnClimbable(Player player) {
        Material mat = player.getLocation().getBlock().getType();
        return Tag.CLIMBABLE.isTagged(mat);
    }
}
