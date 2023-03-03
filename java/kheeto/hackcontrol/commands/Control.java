package kheeto.hackcontrol.commands;

import kheeto.hackcontrol.util.CommandBase;
import kheeto.hackcontrol.HackControl;
import kheeto.hackcontrol.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Dictionary;
import java.util.UUID;

public class Control {
    private static Control instance;
    private HackControl plugin;
    private Dictionary<UUID, UUID> controlList; // PlayerUUID, StafferUUID

    private Location playerPos = null;
    private Location stafferPos = null;
    private Location endPos = null;

    public Control(HackControl plugin) {
        instance = this;
        this.plugin = plugin;

        new CommandBase("control", false) {
            @Override
            public boolean onCommand(CommandSender sender, String[] args) {
                FileConfiguration config = plugin.getConfig();

                if (!sender.hasPermission("hackcontrol.control")) {
                    Message.send(sender, config.getString("errors.noPermission"));
                    return false;
                }

                if (args.length == 0) {
                    Message.send(sender, config.getStringList("help.control").toString());
                    return true;
                }

                // Starts a new hack control
                if (args[0] == "start") {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) Message.send(sender, config.getString("error.noPlayer"));

                    if (controlList.get(target) != null) {
                        Message.send(sender, config.getString("errors.alreadyControlled"));
                        return false;
                    }

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
                            StartControl(target, (Player)sender);
                            return true;
                        }
                        else Message.send(sender, config.getString("errors.noPermission"));
                    }
                }

                // Stops an hack control that is currently occurring
                else if (args[0] == "cancel") {
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
                    EndControl(target, (Player)sender);
                    return true;
                }

                // Sets the spawn positions of the hack control
                else if (args[0] == "setup") {
                    if (!sender.hasPermission("hackcontrol.control.setup")) {
                        Message.send(sender, config.getString("errors.noPermission"));
                        return false;
                    }

                    if (!(sender instanceof Player)) {
                        Message.send(sender, config.getString("errors.notPlayer"));
                        return false;
                    }

                    if (args.length == 1) {
                        Message.send(sender, config.getString("help.controlSetup"));
                        return true;
                    }
                    Player p = (Player)sender;
                    Location loc = p.getLocation();

                    if (args[1] == "stafferPos") {
                        config.set("stafferPos.world", loc.getWorld().getName());
                        config.set("stafferPos.x", loc.getBlockX());
                        config.set("stafferPos.y", loc.getBlockY());
                        config.set("stafferPos.z", loc.getBlockZ());
                        config.set("stafferPos.yaw", loc.getYaw());
                        config.set("stafferPos.pitch", loc.getPitch());
                        plugin.saveConfig();
                        stafferPos = loc;

                        Message.send(sender, config.getString("setup.stafferPos"));
                        return true;
                    }
                    else if (args[1] == "playerPos") {
                        config.set("playerPos.world", loc.getWorld().getName());
                        config.set("playerPos.x", loc.getBlockX());
                        config.set("playerPos.y", loc.getBlockY());
                        config.set("playerPos.z", loc.getBlockZ());
                        config.set("playerPos.yaw", loc.getYaw());
                        config.set("playerPos.pitch", loc.getPitch());
                        plugin.saveConfig();
                        playerPos = loc;

                        Message.send(sender, config.getString("setup.playerPos"));
                        return true;
                    }
                    else if (args[1] == "endPos") {
                        config.set("endPos.world", loc.getWorld().getName());
                        config.set("endPos.x", loc.getBlockX());
                        config.set("endPos.y", loc.getBlockY());
                        config.set("endPos.z", loc.getBlockZ());
                        config.set("endPos.yaw", loc.getYaw());
                        config.set("endPos.pitch", loc.getPitch());
                        plugin.saveConfig();
                        endPos = loc;

                        Message.send(sender, config.getString("setup.endPos"));
                        return true;
                    }
                    else {
                        Message.send(sender, config.getString("help.controlSetup"));
                        return true;
                    }
                }

                else {
                    Message.send(sender, config.getStringList("help.control").toString());
                    return true;
                }

                return false;
            }

            @Override
            public String getUsage() {
                return "/control";
            }

            private void StartControl(Player target, Player staffer) {
                FileConfiguration config = plugin.getConfig();

                target.teleport(playerPos);
                staffer.teleport(stafferPos);

                if(config.getBoolean("freezeDuringControl")) {
                    Freeze.getInstance().FreezePlayer(target);
                }
            }

            private void EndControl(Player target, Player staffer) {
                FileConfiguration config = plugin.getConfig();

                target.teleport(endPos);
                staffer.teleport(endPos);

                if(config.getBoolean("freezeDuringControl")) {
                    Freeze.getInstance().UnfreezePlayer(target);
                }
            }
        };
    }

    public Control getInstance() {
        return instance;
    }

    public void LoadLocations() {
        FileConfiguration config = plugin.getConfig();

        // Staffer Spawn Position
        Location stafferPos = null;
        String worldName = config.getString("stafferPos.world");
        if (worldName == null) {
            Bukkit.getLogger().severe("stafferPos.world doesn't exist within config.yml, could not load spawn location.");
            return;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Bukkit.getLogger().severe("Could not find world \"" + worldName + "\", could not load spawn location.");
            return;
        }
        stafferPos.setWorld(world);
        stafferPos.setX(config.getDouble("stafferPos.x"));
        stafferPos.setY(config.getDouble("stafferPos.y"));
        stafferPos.setZ(config.getDouble("stafferPos.z"));
        stafferPos.setYaw((float)config.getDouble("stafferPos.yaw"));
        stafferPos.setPitch((float)config.getDouble("stafferPos.pitch"));
        this.stafferPos = stafferPos;

        // Target Spawn Position
        Location playerPos = null;
        String worldName2 = config.getString("playerPos.world");
        if (worldName2 == null) {
            Bukkit.getLogger().severe("playerPos.world doesn't exist within config.yml, could not load spawn location.");
            return;
        }
        World world2 = Bukkit.getWorld(worldName);
        if (world2 == null) {
            Bukkit.getLogger().severe("Could not find world \"" + worldName2 + "\", could not load spawn location.");
            return;
        }
        playerPos.setWorld(world2);
        playerPos.setX(config.getDouble("playerPos.x"));
        playerPos.setY(config.getDouble("playerPos.y"));
        playerPos.setZ(config.getDouble("playerPos.z"));
        playerPos.setYaw((float)config.getDouble("playerPos.yaw"));
        playerPos.setPitch((float)config.getDouble("playerPos.pitch"));
        this.playerPos = playerPos;

        // Staffer Spawn Position
        Location endPos = null;
        String worldName3 = config.getString("endPos.world");
        if (worldName3 == null) {
            Bukkit.getLogger().severe("endPos.world doesn't exist within config.yml, could not load spawn location.");
            return;
        }
        World world3 = Bukkit.getWorld(worldName);
        if (world3 == null) {
            Bukkit.getLogger().severe("Could not find world \"" + worldName3 + "\", could not load spawn location.");
            return;
        }
        endPos.setWorld(world3);
        endPos.setX(config.getDouble("endPos.x"));
        endPos.setY(config.getDouble("endPos.y"));
        endPos.setZ(config.getDouble("endPos.z"));
        endPos.setYaw((float)config.getDouble("endPos.yaw"));
        endPos.setPitch((float)config.getDouble("endPos.pitch"));
        this.endPos = endPos;
    }
}
