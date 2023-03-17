package kheeto.hackcontrol.gui;

import kheeto.hackcontrol.HackControl;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GUIConfig {

    private static HackControl plugin;
    private static File file;
    @Getter
    private static FileConfiguration config;

    public static void setup(HackControl plugin) {
        GUIConfig.plugin = plugin;
        GUIConfig.plugin.getLogger().info("Loading GUI config...");

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

    public static String getString(String path) {
        if (config.getString(path) == null) return null;
        return ChatColor.translateAlternateColorCodes('&', config.getString(path));
    }

    public static List<String> getStringList(String path) {
        List<String> stringList = config.getStringList(path);
        if (config.getStringList(path) == null) return null;

        List<String> colorStringList = config.getStringList(path);
        for (String x : stringList) {
            colorStringList.add(ChatColor.translateAlternateColorCodes('&', x));
        }
        return colorStringList;
    }
}
