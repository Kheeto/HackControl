package kheeto.hackcontrol.gui;

import kheeto.hackcontrol.HackControl;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class GUIConfig {

    private static HackControl plugin;
    private static File file;
    @Getter
    private static FileConfiguration config;

    public static void setup(HackControl plugin) {
        GUIConfig.plugin = plugin;
        file = new File(plugin.getDataFolder(), "gui.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() {
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}
