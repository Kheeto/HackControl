package kheeto.hackcontrol.commands;

import kheeto.hackcontrol.CommandBase;
import kheeto.hackcontrol.HackControl;
import kheeto.hackcontrol.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
                FileConfiguration config = plugin.getConfig();

                if (args.length == 0) {
                    Message.send(sender, config.getStringList("help.control").toString());
                    return true;
                }

                // Starts a new hack control
                if (args[0] == "start") {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) Message.send(sender, config.getString("error.noPlayer"));

                    // Executed from console
                    if (!(sender instanceof Player)) {
                        Message.send(sender, config.getString("errors.notPlayer"));
                        return false;
                    }
                    // Executed by a player
                    if (controlList.get(target.getUniqueId()) == Bukkit.getPlayer(sender.getName()).getUniqueId()) {
                        if (sender.hasPermission("hackcontrol.control.start")) {
                            controlList.put(target.getUniqueId(), ((Player) sender).getUniqueId());
                            Message.send(sender, config.getString("control.stafferControlMessage"));
                            Message.send(target, config.getString("control.playerControlMessage"));
                            return true;
                        }
                        else Message.send(sender, config.getString("errors.noPermission"));
                    }
                }

                // Stops an hack control that is currently ongoing
                if (args[0] == "cancel") {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) Message.send(sender, config.getString("error.noPlayer"));

                    // Executed from console
                    if (!(sender instanceof Player)) {
                        controlList.remove(target.getUniqueId());
                        return true;
                    }
                    // Executed by the same staffer who is controlling the player
                    if (controlList.get(target.getUniqueId()) == Bukkit.getPlayer(sender.getName()).getUniqueId()) {
                        if (!sender.hasPermission("hackcontrol.control.cancel")) {
                            Message.send(sender, config.getString("errors.noPermission"));
                            return false;
                        }
                    }
                    // Executed by another staffer
                    else {
                        if (!sender.hasPermission("hackcontrol.control.cancel.others")) {
                            Message.send(sender, config.getString("errors.noPermission"));
                            return false;
                        }
                    }

                    // Removes the target from the list of players in hack control
                    controlList.remove(target.getUniqueId());
                    Message.send(sender, config.getString("control.stafferControlMessage"));
                    Message.send(target, config.getString("control.playerControlMessage"));
                    return true;
                }

                if (args[0] == "setup") {
                    // TODO position setup commands
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
