package kheeto.hackcontrol;

import kheeto.hackcontrol.commands.Control;
import kheeto.hackcontrol.commands.Freeze;
import kheeto.hackcontrol.gui.GUIConfig;
import kheeto.hackcontrol.gui.GUIListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import java.awt.*;

public final class HackControl extends JavaPlugin {

    @Getter
    private static HackControl instance;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("Loading plugin config...");
        saveDefaultConfig();
        //GUIConfig.setup(this);
        //GUIConfig.getConfig().options().copyDefaults(true);
        //GUIConfig.save();

        getLogger().info("Registering commands and event listeners...");

        Control control = new Control(this);
        control.LoadLocations();
        getCommand("control").setExecutor(control);
        getCommand("control").setTabCompleter(control);
        getServer().getPluginManager().registerEvents(control, this);

        Freeze freeze = new Freeze(this);
        getCommand("freeze").setExecutor(freeze);
        getCommand("freeze").setTabCompleter(freeze);
        getServer().getPluginManager().registerEvents(freeze, this);

        //getServer().getPluginManager().registerEvents(new GUIListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin was disabled!");
    }
}
