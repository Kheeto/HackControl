package kheeto.hackcontrol;

import kheeto.hackcontrol.commands.ControlCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public final class HackControl extends Plugin {

    private static HackControl instance;
    private static Configuration config;
    private File configFile;

    @Override
    public void onEnable(){
        instance = this;

        // Loads Config
        Configuration loadedConfig = GetOrCreateConfiguration();
        if(loadedConfig != null) config = loadedConfig;


        // Register Commands
        getLogger().info("Plugin has been Enabled!");
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ControlCommand());
    }

    @Override
    public void onDisable() {
        getLogger().severe("PLUGIN HAS BEEN DISABLED!");
        getLogger().severe("PLUGIN HAS BEEN DISABLED!");
        getLogger().severe("PLUGIN HAS BEEN DISABLED!");
    }

    public static HackControl getInstance() {
        return instance;
    }

    public static Configuration getConfig() {
        return config;
    }

    private Configuration GetOrCreateConfiguration() {

        Configuration newConfig = null;

        configFile = new File(ProxyServer.getInstance().getPluginsFolder() + "/HackControl/config.yml");

        try {
            // Check if the file already exists and if it doesn't, creates a new file.
            if(configFile.exists()) {
                newConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

                newConfig.set("Control_Server", "control");
                newConfig.set("Target_Message", "§cYou are being moved to the Hack Control server!");
                newConfig.set("Sender_Message", "§aYou are going to Hack Control a Player!");
                newConfig.set("Target_Not_Found", "§cThat player wasn't found in the Proxy Server!");
                newConfig.set("No_Target", "§cYou didn't specify a Player to Control!");

                ConfigurationProvider.getProvider(YamlConfiguration.class).save(newConfig, configFile);

            } else {
                configFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newConfig;

    };
}
