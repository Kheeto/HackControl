package kheeto.hackcontrol;

import kheeto.hackcontrol.commands.Control;
import kheeto.hackcontrol.commands.Freeze;
import org.bukkit.plugin.java.JavaPlugin;
import java.awt.*;

public final class HackControl extends JavaPlugin {

    private static HackControl instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        Control control = new Control(this);
        control.LoadLocations();
        getCommand("control").setExecutor(control);
        getCommand("control").setTabCompleter(control);

        Freeze freeze = new Freeze(this);
        getCommand("freeze").setExecutor(freeze);
        getCommand("freeze").setTabCompleter(freeze);
        getServer().getPluginManager().registerEvents(freeze, this);
    }

    @Override
    public void onDisable() {
        getLogger().severe(Color.RED + "HackControl plugin was disabled!");
    }

    public static HackControl getInstance() { return instance; }
}
