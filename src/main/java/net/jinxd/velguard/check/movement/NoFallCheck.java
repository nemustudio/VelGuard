package net.jinxd.velguard.check.movement;

import net.jinxd.velguard.VelGuard;
import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.Check;
import net.jinxd.velguard.check.CheckType;
import net.jinxd.velguard.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class NoFallCheck extends Check {

    private final VelGuard plugin;

    public NoFallCheck(AlertManager alertManager, VelGuard plugin) {
        super(alertManager);
        this.plugin = plugin;
    }

    public void check(Player player, PlayerData data, Location from, Location to) {
        boolean onGround = player.isOnGround();
        boolean wasOnGround = data.wasOnGround();

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
                        alertManager.flag(player, CheckType.NO_FALL,
                            String.format("fall=%.1f", captured));
                        data.setExpectingFallDamage(false);
                    }
                }, 2L);
            }
        }
    }

    private boolean isSafeBlock(Location loc) {
        Material mat = loc.getBlock().getRelative(BlockFace.DOWN).getType();
        return mat == Material.SLIME_BLOCK
            || mat == Material.HAY_BLOCK
            || mat == Material.HONEY_BLOCK
            || mat == Material.COBWEB;
    }
}
