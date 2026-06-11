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
            sender.sendMessage(mm.deserialize(PREFIX + "<gray>Usage: <white>/veltest <fly|speed|nofall|killaura|all> <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(mm.deserialize(PREFIX + "<red>Player not found or offline."));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "fly"      -> alertManager.flag(target, CheckType.FLY, "airTicks=25 [TEST]");
            case "speed"    -> alertManager.flag(target, CheckType.SPEED, "dist=0.842 max=0.560 vl=5 [TEST]");
            case "nofall"   -> alertManager.flag(target, CheckType.NO_FALL, "fall=12.0 [TEST]");
            case "killaura" -> alertManager.flag(target, CheckType.KILL_AURA, "cps~22 delta=44ms yaw=2.3 [TEST]");
            case "all" -> {
                alertManager.flag(target, CheckType.FLY, "airTicks=25 [TEST]");
                alertManager.flag(target, CheckType.SPEED, "dist=0.842 max=0.560 vl=5 [TEST]");
                alertManager.flag(target, CheckType.NO_FALL, "fall=12.0 [TEST]");
                alertManager.flag(target, CheckType.KILL_AURA, "cps~22 delta=44ms yaw=2.3 [TEST]");
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
