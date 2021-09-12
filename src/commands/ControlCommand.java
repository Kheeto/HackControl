package kheeto.hackcontrol.commands;

import kheeto.hackcontrol.HackControl;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class ControlCommand extends Command {

    public ControlCommand() {
        super("Control");
    }

    public void execute(CommandSender sender, String[] args) {
        Configuration config = HackControl.getConfig();

        if ((sender instanceof ProxiedPlayer)) {
            if(args.length > 0) {
                if(ProxyServer.getInstance().getPlayer(args[0]) != null) {
                    // Send both players to the Hack Control Server
                    ProxiedPlayer target = (ProxiedPlayer) ProxyServer.getInstance().getPlayer(args[0]);
                    ProxiedPlayer p = (ProxiedPlayer) sender;

                    target.sendMessage(new ComponentBuilder(config.get("Target_Message").toString()).create());
                    p.sendMessage(new ComponentBuilder(config.get("Sender_Message").toString()).create());

                    target.connect(ProxyServer.getInstance().getServerInfo(config.get("Control_Server").toString()));
                    p.connect(ProxyServer.getInstance().getServerInfo(config.get("Control_Server").toString()));
                } else {
                    ProxiedPlayer p = (ProxiedPlayer) sender;
                    p.sendMessage(new ComponentBuilder(config.get("Target_Not_Found").toString()).create());
                }
            } else {
                ProxiedPlayer p = (ProxiedPlayer) sender;
                p.sendMessage(new ComponentBuilder(config.get("No_Target").toString()).create());
            }
        }
    }
}
