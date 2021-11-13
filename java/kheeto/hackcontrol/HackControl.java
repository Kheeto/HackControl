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
                newConfig = setConfigValues(configFile);
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(newConfig, configFile);
            } else {
                if(configFile.createNewFile()) {
                    newConfig = setConfigValues(configFile);
                    ConfigurationProvider.getProvider(YamlConfiguration.class).save(newConfig, configFile);
                } else {
                    getLogger().severe("Couldn't create a new config file");
                };

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newConfig;

    };

    private Configuration setConfigValues(File file) throws IOException {
        Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

        cfg.set("Control_Server", "control");
        cfg.set("Target_Message", "§cYou are being moved to the Hack Control server!");
        cfg.set("Sender_Message", "§aYou are going to Hack Control a Player!");
        cfg.set("Target_Not_Found", "§cThat player wasn't found in the Proxy Server!");
        cfg.set("No_Target", "§cYou didn't specify a Player to Control!");
        cfg.set("No_Permission", "§cYou do not have the permission to use that command!");

        return cfg;
    }


}
