package kheeto.hackcontrol;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Message {
    public static void send(CommandSender sender, String message) {
        send(sender, message, HackControl.getInstance().getConfig().getString("prefix"));
    }

    public static void send(CommandSender sender, String message, String prefix) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    }
}
