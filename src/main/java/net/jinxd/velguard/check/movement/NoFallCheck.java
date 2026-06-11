package net.jinxd.velguard.check.movement;

import net.jinxd.velguard.VelGuard;
import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.Check;
import net.jinxd.velguard.check.CheckType;
import net.jinxd.velguard.data.PlayerData;
import net.jinxd.velguard.punishment.PunishmentManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class NoFallCheck extends Check {

    private final VelGuard plugin;
    private final double vlPerFlag;
    private final double vlAlert;

    public NoFallCheck(AlertManager alertManager, PunishmentManager punishmentManager, VelGuard plugin) {
        super(alertManager, punishmentManager);
        this.plugin    = plugin;
        this.vlPerFlag = plugin.getConfig().getDouble("checks.no-fall.vl-per-flag", 3.0);
        this.vlAlert   = plugin.getConfig().getDouble("checks.no-fall.vl-alert", 3.0);
    }

    public void check(Player player, PlayerData data, Location from, Location to) {
        boolean wasOnGround = data.wasOnGround();
        boolean onGround    = data.getLastDeltaY() <= 0 && isServerSideGrounded(player);

        if (!wasOnGround && onGround) {
            float fallDist = player.getFallDistance();

            if (fallDist > 3.0f
                    && !player.isInWater()
                    && !player.hasPotionEffect(PotionEffectType.SLOW_FALLING)
                    && !isSafeBlock(to)) {
                data.setExpectingFallDamage(true);
                float captured = fallDist;

                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (data.isExpectingFallDamage()) {
                        double vl = data.addVl(CheckType.NO_FALL, vlPerFlag);
                        if (vl >= vlAlert) {
                            alertManager.flag(player, CheckType.NO_FALL,
                                String.format("fall=%.1f", captured), vl);
                        }
                        data.setExpectingFallDamage(false);
                    }
                }, 2L);
            }
        }
    }

    private boolean isServerSideGrounded(Player player) {
        Location loc = player.getLocation();
        int checkY = (int) Math.floor(loc.getY() - 0.01);
        double[] corners = {-0.29, 0.29};

        for (double dx : corners) {
            for (double dz : corners) {
                if (!loc.getWorld().getBlockAt(
                        (int) Math.floor(loc.getX() + dx),
                        checkY,
                        (int) Math.floor(loc.getZ() + dz)).isPassable()) return true;
            }
        }
        return false;
    }

    private boolean isSafeBlock(Location loc) {
        Material mat = loc.getBlock().getRelative(BlockFace.DOWN).getType();
        return mat == Material.SLIME_BLOCK
            || mat == Material.HAY_BLOCK
            || mat == Material.HONEY_BLOCK
            || mat == Material.COBWEB;
    }
}
