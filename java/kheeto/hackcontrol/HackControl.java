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

        new Control(this).LoadLocations();

        Freeze freeze = new Freeze();
        getCommand("freeze").setExecutor(freeze);;
        getCommand("unfreeze").setExecutor(freeze);;
    }

    @Override
    public void onDisable() {
        getLogger().severe(Color.RED + "HackControl plugin was disabled!");
    }

    public static HackControl getInstance() { return instance; }
}
