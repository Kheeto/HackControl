package kheeto.hackcontrol.commands;

import kheeto.hackcontrol.HackControl;
import kheeto.hackcontrol.gui.GUIConfig;
import kheeto.hackcontrol.guis.PlayerGUI;
import kheeto.hackcontrol.guis.StafferGUI;
import kheeto.hackcontrol.util.Message;
import lombok.Getter;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class Control implements CommandExecutor, TabCompleter, Listener {

    @Getter
    private static Control instance;
    private HackControl plugin;
    @Getter
    private Map<UUID, UUID> controlList; // PlayerUUID, StafferUUID

    private Location playerPos = null;
    private Location stafferPos = null;
    private Location endPos = null;

    public Control(HackControl plugin) {
        instance = this;
        this.plugin = plugin;
        controlList = new HashMap<>();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();

        if (!sender.hasPermission("hackcontrol.control")) {
            Message.send(sender, config.getString("errors.noPermission"));
            return true;
        }

        if (args.length == 0) {
            for (String s : config.getStringList("help.control")) {
                Message.send(sender, s, "");
            }
            return true;
        }

        // Starts a new hack control
        if (args[0].equalsIgnoreCase("start")) {
            if (args.length == 1) {
                Message.send(sender, config.getString("errors.noPlayer"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                Message.send(sender, config.getString("errors.noPlayerFound")
                        .replace("{player}", args[1]).replace("{staffer}", sender.getName()));
                return true;
            }

            if (controlList.get(target) != null) {
                Message.send(sender, config.getString("errors.alreadyControlled")
                        .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
                return true;
            }

            // Executed from console
            if (!(sender instanceof Player)) {
                Message.send(sender, config.getString("errors.notPlayer")
                        .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
                return true;
            }

            // Player trying to hack control himself
            if (target.getName() == sender.getName()) {
                Message.send(sender, config.getString("errors.yourself")
                        .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
                return true;
            }

            if (controlList.values().contains(((Player) sender).getUniqueId())) {
                Message.send(sender, config.getString("errors.alreadyControlling")
                        .replace("{staffer}", sender.getName()).replace("{player}", target.getName()));
                return true;
            }

            if (!sender.hasPermission("hackcontrol.control.start")) {
                Message.send(sender, config.getString("errors.noPermission")
                        .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
                return true;
            }

            controlList.put(target.getUniqueId(), ((Player) sender).getUniqueId());
            Message.send(sender, config.getString("control.stafferControlMessage")
                    .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
            Message.send(target, config.getString("control.playerControlMessage")
                    .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
            StartControl(target, (Player)sender);
            PlayerGUI.show(target);
            return true;
        }

        // Stops an hack control that is currently occurring
        else if (args[0].equalsIgnoreCase("end")) {
            if (args.length == 1) {
                Message.send(sender, config.getString("errors.noPlayer"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                Message.send(sender, config.getString("errors.noPlayerFound")
                        .replace("{player}", args[1]).replace("{staffer}", sender.getName()));
                return true;
            }

            if (!controlList.containsKey(target)) {
                Message.send(sender, config.getString("errors.noControl")
                        .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
                return true;
            }

            // Executed from console
            if (!(sender instanceof Player)) {
                controlList.remove(target.getUniqueId());
                return true;
            }

            StafferGUI.show((Player)sender, target);
            return true;
        }

        // Sets the spawn positions of the hack control
        else if (args[0].equalsIgnoreCase("setup")) {
            if (!sender.hasPermission("hackcontrol.control.setup")) {
                Message.send(sender, config.getString("errors.noPermission")
                        .replace("{staffer}", sender.getName()));
                return true;
            }

            if (!(sender instanceof Player)) {
                Message.send(sender, config.getString("errors.notPlayer")
                        .replace("{staffer}", sender.getName()));
                return true;
            }

            if (args.length == 1) {
                for (String s : config.getStringList("help.controlSetup")) {
                    Message.send(sender, s, "");
                }
                return true;
            }
            Player p = (Player)sender;
            Location loc = p.getLocation();

            if (args[1].equalsIgnoreCase("stafferPos")) {
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
            else if (args[1].equalsIgnoreCase("playerPos")) {
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
            else if (args[1].equalsIgnoreCase("endPos")) {
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
                for (String s : config.getStringList("help.controlSetup")) {
                    Message.send(sender, s, "");
                }
                return true;
            }
        }

        else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("hackcontrol.control.reload")) {
                Message.send(sender, config.getString("errors.noPermission")
                        .replace("{staffer}", sender.getName()));
                return true;
            }

            plugin.reloadConfig();
            GUIConfig.reload();
            Message.send(sender, config.getString("configReload")
                .replace("{staffer}", sender.getName()));
            return true;
        }

        else {
            for (String s : config.getStringList("help.control")) {
                Message.send(sender, s, "");
            }
            return true;
        }
    }

    private void StartControl(Player target, Player staffer) {
        FileConfiguration config = plugin.getConfig();

        target.teleport(playerPos);
        staffer.teleport(stafferPos);

        if(config.getBoolean("freezeDuringControl")) {
            Freeze.getInstance().FreezePlayer(target);
        }

        PlayerGUI.show(target);
    }

    public void EndControl(Player target, Player staffer) {
        if (controlList.containsKey(target))
            controlList.remove(target);

        FileConfiguration config = plugin.getConfig();

        target.teleport(endPos);
        staffer.teleport(endPos);

        if(config.getBoolean("freezeDuringControl")) {
            Freeze.getInstance().UnfreezePlayer(target);
        }
    }

    public void LoadLocations() {
        LoadLocation(stafferPos, "stafferPos");
        LoadLocation(playerPos, "playerPos");
        LoadLocation(endPos, "endPos");
    }

    private void LoadLocation(Location location, String name) {
        FileConfiguration config = plugin.getConfig();

        String worldName = config.getString(name + ".world");
        if (worldName == null) {
            Bukkit.getLogger().severe(name + ".world doesn't exist within config.yml, could not load spawn location.");
            return;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Bukkit.getLogger().severe("Could not find world \"" + worldName + "\", could not load spawn location.");
            return;
        }
        int x = config.getInt(name + ".x");
        int y = config.getInt(name + ".y");
        int z = config.getInt(name + ".z");
        float yaw = (float)config.getDouble(name + ".yaw");
        float pitch = (float)config.getDouble(name + ".pitch");
        this.stafferPos = new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> playerNames = new ArrayList<>();
        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().toArray().length];
        Bukkit.getServer().getOnlinePlayers().toArray(players);
        for (Player p : players) {
            playerNames.add(p.getName());
        }

        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("start");
            options.add("end");
            options.add("setup");
            options.add("reload");
            return options;
        }
        else if (args.length == 2) {
            switch (args[0]) {
                case "start":
                    return playerNames;
                case "end":
                    if (controlList == null) return playerNames;
                    List<String> controlledPlayers = new ArrayList<>();
                    for (UUID u : controlList.keySet()) {
                        controlledPlayers.add(Bukkit.getPlayer(u).getName());
                    }
                    return controlledPlayers;
                case "setup":
                    List<String> setupOptions = new ArrayList<>();
                    setupOptions.add("stafferPos");
                    setupOptions.add("playerPos");
                    setupOptions.add("endPos");
                    return setupOptions;
            }
        }

        return null;
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e) {
        if (!plugin.getConfig().getBoolean("leaveBan.enabled")) return;

        if (controlList.containsKey(e.getPlayer())) {
            long duration = System.currentTimeMillis() + 60*60*((long) (plugin.getConfig().getDouble("leaveBan.duration")*1000));
            Date date = new Date(duration);

            if (plugin.getConfig().getBoolean(("leaveBan.ipBan")))
                plugin.getServer().getBanList(BanList.Type.IP).addBan(e.getPlayer().getAddress().getAddress().getHostAddress(),
                        plugin.getConfig().getString("leaveBan.reason"), date, "HackControl");
            else
                plugin.getServer().getBanList(BanList.Type.NAME).addBan(e.getPlayer().getName(),
                        plugin.getConfig().getString("leaveBan.reason"), date, "HackControl");
        }
    }

    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent e) {
        FileConfiguration config = plugin.getConfig();
        Player sender = e.getPlayer();

        if (controlList.containsKey(sender.getUniqueId())) { // if a controlled player writes a message
            Player staffer = Bukkit.getPlayer(controlList.get(sender));

            Message.send(sender, config.getString("controlChannel.playerPrefix")
                    .replace("{player}", sender.getName())
                    .replace("{staffer}", staffer.getName())+ e.getMessage());
            Message.send(staffer, config.getString("controlChannel.playerPrefix")
                    .replace("{player}", sender.getName())
                    .replace("{staffer}", staffer.getName()) + e.getMessage());
            e.setCancelled(true);
        }
        else if (controlList.containsValue(sender)) { // if a staffer in hack control writes a message
            Player player = null;
            // find the player he is controlling
            for (UUID u : controlList.keySet()) {
                if (controlList.get(u) == sender.getUniqueId())
                    player = Bukkit.getPlayer(u);
            }
            if (player == null) return;

            Message.send(player, config.getString("controlChannel.stafferPrefix")
                    .replace("{staffer}", sender.getName())
                    .replace("{player}", player.getName())+ e.getMessage());
            Message.send(sender, config.getString("controlChannel.stafferPrefix")
                    .replace("{staffer}", sender.getName())
                    .replace("{player}", player.getName())+ e.getMessage());
            e.setCancelled(true);
        }
    }
}
