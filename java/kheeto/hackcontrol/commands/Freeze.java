package kheeto.hackcontrol.commands;

import kheeto.hackcontrol.HackControl;
import kheeto.hackcontrol.util.CommandBase;
import kheeto.hackcontrol.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Dictionary;
import java.util.List;
import java.util.UUID;

public class Freeze implements Listener {

    private static Freeze instance;
    private HackControl plugin;
    private static List<Player> frozenPlayers;

    public Freeze(HackControl plugin) {
        instance = this;
        this.plugin = plugin;

        new CommandBase("freeze", false) {
            @Override
            public boolean onCommand(CommandSender sender, String[] args) {
                FileConfiguration config = HackControl.getInstance().getConfig();

                if (!sender.hasPermission("hackcontrol.freeze")) {
                    Message.send(sender, config.getString("errors.noPermission"));
                    return false;
                }

                if (args.length == 0) {
                    Message.send(sender, config.getString("errors.noPlayer"));
                    return false;
                }

                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    Message.send(sender, config.getString("errors.noPlayerFound"));
                    return false;
                }

                if (p.hasPermission("hackcontrol.freeze.bypass")) {
                    Message.send(sender, config.getString("errors.immunePlayer"));
                    return false;
                }

                if (frozenPlayers.contains(p)) {
                    Message.send(sender, config.getString("errors.alreadyFrozen"));
                    return false;
                }

                frozenPlayers.add(p);
                sender.sendMessage(config.getString("stafferFreezeMessage"));
                p.sendMessage(config.getString("playerFreezeMessage"));
                return true;
            }

            @Override
            public String getUsage() {
                return "/freeze";
            }
        };
        new CommandBase("unfreeze", false) {
            @Override
            public boolean onCommand(CommandSender sender, String[] args) {
                FileConfiguration config = HackControl.getInstance().getConfig();

                if (!sender.hasPermission("hackcontrol.unfreeze")) {
                    Message.send(sender, config.getString("errors.noPermission"));
                    return false;
                }

                if (args.length == 0) {
                    Message.send(sender, config.getString("errors.noPlayer"));
                    return false;
                }

                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    Message.send(sender, config.getString("errors.noPlayerFound"));
                    return false;
                }

                if (!frozenPlayers.contains(p)) {
                    Message.send(sender, config.getString("errors.notFrozen"));
                    return false;
                }

                frozenPlayers.remove(p);
                sender.sendMessage(config.getString("stafferUnfreezeMessage"));
                p.sendMessage(config.getString("playerUnfreezeMessage"));
                return true;
            }

            @Override
            public String getUsage() {
                return "/unfreeze";
            }
        };
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent e) {
        if (frozenPlayers.contains(e.getPlayer()))
            e.setCancelled(true);
    }

    public static Freeze getInstance() {
        return instance;
    }

    public void FreezePlayer(Player p) {
        if (!frozenPlayers.contains(p))
            frozenPlayers.add(p);
    }

    public void UnfreezePlayer(Player p) {
        if (frozenPlayers.contains(p))
            frozenPlayers.remove(p);
    }
}
