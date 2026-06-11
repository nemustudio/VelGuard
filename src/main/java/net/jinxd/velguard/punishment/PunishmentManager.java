package net.jinxd.velguard.punishment;

import net.jinxd.velguard.VelGuard;
import net.jinxd.velguard.check.CheckType;
import org.bukkit.entity.Player;

public class PunishmentManager {

    private final VelGuard plugin;

    public PunishmentManager(VelGuard plugin) {
        this.plugin = plugin;
    }

    public void execute(Player player, CheckType type) {
        String cmd = plugin.getConfig().getString("checks." + type.getConfigKey() + ".punish-command", "");
        if (cmd.isBlank()) return;
        String resolved = cmd.replace("{player}", player.getName());
        plugin.getServer().getScheduler().runTask(plugin,
            () -> plugin.getServer().dispatchCommand(
                plugin.getServer().getConsoleSender(), resolved));
    }
}
