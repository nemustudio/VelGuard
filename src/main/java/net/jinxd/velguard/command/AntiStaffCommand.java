package net.jinxd.velguard.command;

import net.jinxd.velguard.staff.StaffManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AntiStaffCommand implements CommandExecutor {

    private static final String PREFIX = "<dark_gray>[<red>VelGuard<dark_gray>] ";

    private final StaffManager staffManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AntiStaffCommand(StaffManager staffManager) {
        this.staffManager = staffManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("velguard.staff")) {
            sender.sendMessage(mm.deserialize(PREFIX + "<red>No permission."));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (args.length < 2) { sendUsage(sender); return true; }
                @SuppressWarnings("deprecation")
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (!target.hasPlayedBefore() && !target.isOnline()) {
                    sender.sendMessage(mm.deserialize(PREFIX + "<red>Player <white>" + args[1] + " <red>has never joined this server."));
                    return true;
                }
                String name = target.getName() != null ? target.getName() : args[1];
                if (staffManager.add(target.getUniqueId(), name)) {
                    staffManager.save();
                    sender.sendMessage(mm.deserialize(PREFIX + "<green>Added <white>" + name + " <green>to the alert list."));
                } else {
                    sender.sendMessage(mm.deserialize(PREFIX + "<yellow>" + name + " is already on the list."));
                }
            }
            case "remove" -> {
                if (args.length < 2) { sendUsage(sender); return true; }
                @SuppressWarnings("deprecation")
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (staffManager.remove(target.getUniqueId())) {
                    staffManager.save();
                    sender.sendMessage(mm.deserialize(PREFIX + "<green>Removed <white>" + args[1] + " <green>from the alert list."));
                } else {
                    sender.sendMessage(mm.deserialize(PREFIX + "<yellow>" + args[1] + " was not on the list."));
                }
            }
            case "list" -> {
                var all = staffManager.getAll();
                if (all.isEmpty()) {
                    sender.sendMessage(mm.deserialize(PREFIX + "<gray>Alert list is empty."));
                    return true;
                }
                sender.sendMessage(mm.deserialize(PREFIX + "<gray>Alert recipients <dark_gray>(" + all.size() + ")"));
                all.forEach((uuid, name) ->
                    sender.sendMessage(mm.deserialize("  <white>" + name + " <dark_gray>(" + uuid + ")")));
            }
            default -> sendUsage(sender);
        }
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(mm.deserialize(PREFIX + "<gray>Usage: <white>/antistaff <add|remove|list> [player]"));
    }
}
