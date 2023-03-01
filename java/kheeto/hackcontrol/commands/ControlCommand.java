package kheeto.hackcontrol.commands;

import kheeto.hackcontrol.CommandBase;
import kheeto.hackcontrol.HackControl;
import kheeto.hackcontrol.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.UUID;

public class Control {
    private static Control instance;
    private Dictionary<UUID, UUID> controlList; // PlayerUUID, StafferUUID

    public Control(HackControl plugin) {
        instance = this;

        new CommandBase("control", false) {
            @Override
            public boolean onCommand(CommandSender sender, String[] args) {
                if (args.length == 0) {
                    Message.send(sender, plugin.getConfig().getStringList("help.control").toString());
                    return true;
                }

                // Starts a new hack control
                if (args[0] == "start") {
                    Player target = Bukkit.getPlayer(args[1]);

                    // Executed from console
                    if (!(sender instanceof Player)) {
                        Message.send(sender, plugin.getConfig().getString("errors.notPlayer"));
                        return false;
                    }
                    // Executed by a player
                    if (controlList.get(target.getUniqueId()) == Bukkit.getPlayer(sender.getName()).getUniqueId()) {
                        if (sender.hasPermission("hackcontrol.control.start")) {
                            controlList.put(target.getUniqueId(), ((Player) sender).getUniqueId());
                            return true;
                        }
                        else Message.send(sender, plugin.getConfig().getString("errors.noPermission"));
                    }
                }

                // Stops an hack control that is currently ongoing
                if (args[0] == "cancel") {
                    Player target = Bukkit.getPlayer(args[1]);

                    // Executed from console
                    if (!(sender instanceof Player)) {
                        controlList.remove(target.getUniqueId());
                        return true;
                    }
                    // Executed by the same staffer who is controlling the player
                    if (controlList.get(target.getUniqueId()) == Bukkit.getPlayer(sender.getName()).getUniqueId()) {
                        if (sender.hasPermission("hackcontrol.control.cancel")) {
                            controlList.remove(target.getUniqueId());
                            return true;
                        }
                        else Message.send(sender, plugin.getConfig().getString("errors.noPermission"));
                    }
                    // Executed by another staffer
                    else {
                        if (sender.hasPermission("hackcontrol.control.cancel.others")) {
                            controlList.remove(target.getUniqueId());
                            return true;
                        }
                        else Message.send(sender, plugin.getConfig().getString("errors.noPermission"));
                    }
                }

                if (args[0] == "setup") {

                }

                return false;
            }

            @Override
            public String getUsage() {
                return "/control";
            }
        };
    }

    public Control getInstance() {
        return instance;
    }
}
