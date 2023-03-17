package kheeto.hackcontrol.guis;

import kheeto.hackcontrol.HackControl;
import kheeto.hackcontrol.commands.Control;
import kheeto.hackcontrol.data.PlayerDataManager;
import kheeto.hackcontrol.gui.GUI;
import kheeto.hackcontrol.gui.GUIButton;
import kheeto.hackcontrol.gui.GUIConfig;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Date;

public class PlayerGUI {
    public static void show(Player player) {
        HackControl plugin = HackControl.getInstance();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration guiConfig = GUIConfig.getConfig();

        GUI gui = new GUI(guiConfig.getString("player.title", "Hack Control"),
                guiConfig.getInt("player.rows", 3), false);

        // Accept hack control button
        ItemStack accept = new ItemStack(
                Material.matchMaterial(guiConfig.getString("player.items.accept.material",
                        "LIME_CONCRETE")));
        ItemMeta acceptMeta = accept.getItemMeta();
        acceptMeta.setDisplayName(guiConfig.getString("player.items.accept.name"));
        acceptMeta.setLore(guiConfig.getStringList("player.items.accept.lore"));
        accept.setItemMeta(acceptMeta);
        GUIButton acceptButton = new GUIButton(accept);
        acceptButton.setAction(() -> {
            PlayerDataManager.getData(player).setCurrentGUI(null);
            player.closeInventory();
        });

        // Admit cheating button
        ItemStack admit = new ItemStack(
                Material.matchMaterial(guiConfig.getString("player.items.admit.material",
                        "YELLOW_STAINED_GLASS")));
        ItemMeta admitMeta = admit.getItemMeta();
        admitMeta.setDisplayName(guiConfig.getString("player.items.admit.name"));
        admitMeta.setLore(guiConfig.getStringList("player.items.admit.lore"));
        admit.setItemMeta(admitMeta);
        GUIButton admitButton = new GUIButton(admit);
        admitButton.setAction(() -> {
            player.closeInventory();
            Control.getInstance().EndControl(player,
                    Bukkit.getPlayer(Control.getInstance().getControlList().get(player)));

            long duration = System.currentTimeMillis() + 60*60*((long) config.getDouble("admitBan.duration")*1000);
            Date date = new Date(duration);

            if (config.getBoolean(("admitBan.ipBan")))
                plugin.getServer().getBanList(BanList.Type.IP).addBan(player.getAddress().getAddress().getHostAddress(),
                        plugin.getConfig().getString("admitBan.reason"), date, "HackControl");
            else
                plugin.getServer().getBanList(BanList.Type.NAME).addBan(player.getName(),
                        plugin.getConfig().getString("admitBan.reason"), date, "HackControl");
        });

        // Refuse hack control button
        ItemStack refuse = new ItemStack(
                Material.matchMaterial(guiConfig.getString("player.items.refuse.material",
                        "RED_CONCRETE")));
        ItemMeta refuseMeta = refuse.getItemMeta();
        refuseMeta.setDisplayName(guiConfig.getString("player.items.refuse.name"));
        refuseMeta.setLore(guiConfig.getStringList("player.items.refuse.lore"));
        refuse.setItemMeta(refuseMeta);
        GUIButton refuseButton = new GUIButton(refuse);
        refuseButton.setAction(() -> {
            PlayerDataManager.getData(player).setCurrentGUI(null);
            player.closeInventory();

            Control.getInstance().EndControl(player,
                    Bukkit.getPlayer(Control.getInstance().getControlList().get(player)));

            long duration = System.currentTimeMillis() + 60*60*((long) config.getDouble("refuseBan.duration")*1000);
            Date date = new Date(duration);

            if (config.getBoolean(("refuseBan.ipBan")))
                plugin.getServer().getBanList(BanList.Type.IP).addBan(player.getAddress().getAddress().getHostAddress(),
                        plugin.getConfig().getString("refuseBan.reason"), date, "HackControl");
            else
                plugin.getServer().getBanList(BanList.Type.NAME).addBan(player.getName(),
                        plugin.getConfig().getString("refuseBan.reason"), date, "HackControl");
        });

        if (config.getBoolean("player.items.accept.enabled"))
            gui.setItem(acceptButton, config.getInt("player.items.accept.slot"));
        if (config.getBoolean("player.items.admit.enabled"))
            gui.setItem(admitButton, config.getInt("player.items.admit.slot"));
        if (config.getBoolean("player.items.refuse.enabled"))
            gui.setItem(refuseButton, config.getInt("player.items.refuse.slot"));

        gui.show(player);
    }
}
