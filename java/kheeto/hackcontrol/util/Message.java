package kheeto.hackcontrol.util;

import kheeto.hackcontrol.HackControl;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {
    public static void send(CommandSender sender, String message) {
        send(sender, message, HackControl.getInstance().getConfig().getString("prefix"));
    }

    public static void send(CommandSender sender, String message, String prefix) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }

    public static void send(Player target, String message) {
        send(target, message, HackControl.getInstance().getConfig().getString("prefix"));
    }

    public static void send(Player target, String message, String prefix) {
        target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }
}
