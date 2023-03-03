package kheeto.hackcontrol.util;

import kheeto.hackcontrol.HackControl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;

public abstract class CommandBase extends BukkitCommand implements CommandExecutor {

    private final int minArguments;
    private final int maxArguments;
    private final boolean playerOnly;

    public CommandBase(String command) {
        this(command, 0);
    }

    public CommandBase(String command, boolean playerOnly) {
        this(command, 0, playerOnly);
    }

    public CommandBase(String command, int requiredArguments) {
        this(command, requiredArguments, requiredArguments);
    }

    public CommandBase(String command, int minArguments, int maxArguments) {
        this(command, minArguments, maxArguments, false);
    }

    public CommandBase(String command, int requiredArguments, boolean playerOnly) {
        this(command, requiredArguments, requiredArguments, playerOnly);
    }

    public CommandBase(String command, int minArguments, int maxArguments, boolean playerOnly) {
        super(command);

        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
        this.playerOnly = playerOnly;

        CommandMap commandMap = getCommandMap();
        if (commandMap != null) {
            commandMap.register(command, this);
        }
    }

    public CommandMap getCommandMap() {
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);

                return (CommandMap) field.get(Bukkit.getPluginManager());
            }
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void sendUsage(CommandSender sender) {
        Message.send(sender, getUsage());
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (args.length < minArguments || (args.length < maxArguments && maxArguments != -1)) {
            sendUsage(sender);
            return true;
        }

        if (playerOnly && !(sender instanceof Player)) {
            Message.send(sender, HackControl.getInstance().getConfig().getString("errors.notPlayer"));
            return true;
        }

        String permission = getPermission();
        if (permission != null && !sender.hasPermission(permission)) {
            Message.send(sender, HackControl.getInstance().getConfig().getString("errors.noPermission"));
            return true;
        }

        if (!onCommand(sender, args)) {
            sendUsage(sender);
        }

        return true;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        return this.onCommand(sender, args);
    }

    public abstract boolean onCommand(CommandSender sender, String[] args);
    public abstract String getUsage();
}
