package net.jinxd.velguard.command;

import net.jinxd.velguard.alert.AlertManager;
import net.jinxd.velguard.check.CheckType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VelTestCommand implements CommandExecutor {

    private static final String PREFIX = "<dark_gray>[<red>VelGuard<dark_gray>] ";

    private final AlertManager alertManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public VelTestCommand(AlertManager alertManager) {
        this.alertManager = alertManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("velguard.test")) {
            sender.sendMessage(mm.deserialize(PREFIX + "<red>No permission."));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(mm.deserialize(PREFIX + "<gray>Usage: <white>/veltest <fly|speed|nofall|killaura|reach|all> <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(mm.deserialize(PREFIX + "<red>Player not found or offline."));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "fly"      -> alertManager.flag(target, CheckType.FLY,       "air=25 [TEST]", 5.0);
            case "speed"    -> alertManager.flag(target, CheckType.SPEED,     "dist=0.842 max=0.560 [TEST]", 6.0);
            case "nofall"   -> alertManager.flag(target, CheckType.NO_FALL,   "fall=12.0 [TEST]", 3.0);
            case "killaura" -> alertManager.flag(target, CheckType.KILL_AURA, "cps=22 yaw=2.3 [TEST]", 5.0);
            case "reach"    -> alertManager.flag(target, CheckType.REACH,     "dist=4.21 max=3.26 [TEST]", 4.0);
            case "all" -> {
                alertManager.flag(target, CheckType.FLY,       "air=25 [TEST]", 5.0);
                alertManager.flag(target, CheckType.SPEED,     "dist=0.842 max=0.560 [TEST]", 6.0);
                alertManager.flag(target, CheckType.NO_FALL,   "fall=12.0 [TEST]", 3.0);
                alertManager.flag(target, CheckType.KILL_AURA, "cps=22 yaw=2.3 [TEST]", 5.0);
                alertManager.flag(target, CheckType.REACH,     "dist=4.21 max=3.26 [TEST]", 4.0);
            }
            default -> {
                sender.sendMessage(mm.deserialize(PREFIX + "<red>Unknown type: <white>" + args[0]));
                return true;
            }
        }

        sender.sendMessage(mm.deserialize(PREFIX + "<green>Test alert sent for <white>" + target.getName() + "<green>."));
        return true;
    }
}
