package kheeto.hackcontrol.guis;

import kheeto.hackcontrol.HackControl;
import kheeto.hackcontrol.commands.Control;
import kheeto.hackcontrol.data.PlayerDataManager;
import kheeto.hackcontrol.gui.GUI;
import kheeto.hackcontrol.gui.GUIButton;
import kheeto.hackcontrol.gui.GUIConfig;
import kheeto.hackcontrol.util.Message;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Date;

public class StafferGUI {
    public static void show(Player sender, Player target) {
        HackControl plugin = HackControl.getInstance();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration guiConfig = GUIConfig.getConfig();
        Control control = Control.getInstance();

        GUI gui = new GUI(guiConfig.getString("staffer.title"), guiConfig.getInt("staffer.rows"), true);

        // No cheats found
        ItemStack release = new ItemStack(
                Material.matchMaterial(guiConfig.getString("staffer.items.release.material",
                        "LIME_CONCRETE")));
        ItemMeta releaseMeta = release.getItemMeta();
        releaseMeta.setDisplayName(guiConfig.getString("staffer.items.release.name"));
        releaseMeta.setLore(guiConfig.getStringList("staffer.items.release.lore"));
        release.setItemMeta(releaseMeta);
        GUIButton releaseButton = new GUIButton(release);
        releaseButton.setAction(() -> {
            sender.closeInventory();

            // Executed by the same staffer who is controlling the player
            if (control.getControlList().get(target.getUniqueId()) == Bukkit.getPlayer(sender.getName()).getUniqueId()) {
                if (!sender.hasPermission("hackcontrol.control.end")) {
                    Message.send(sender, config.getString("errors.noPermission"));
                    return;
                }
                Message.send(sender, config.getString("control.stafferEndMessage")
                        .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
            }
            // Executed by another staffer
            else {
                if (!sender.hasPermission("hackcontrol.control.end.others")) {
                    Message.send(sender, config.getString("errors.noPermission"));
                    return;
                }
                Player staffer = Bukkit.getPlayer(control.getControlList().get(target.getUniqueId()));
                Message.send(staffer, config.getString("control.stafferEndMessageOther")
                        .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
            }

            control.getControlList().remove(target.getUniqueId());
            Message.send(target, config.getString("control.playerEndMessage")
                    .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));

            control.EndControl(target, sender);
            PlayerDataManager.getData(target).setCurrentGUI(null);
            target.closeInventory();
        });

        // Ban button
        ItemStack ban = new ItemStack(
                Material.matchMaterial(guiConfig.getString("staffer.items.ban.material",
                        "RED_CONCRETE")));
        ItemMeta banMeta = ban.getItemMeta();
        banMeta.setDisplayName(guiConfig.getString("staffer.items.ban.name"));
        banMeta.setLore(guiConfig.getStringList("staffer.items.ban.lore"));
        ban.setItemMeta(banMeta);
        GUIButton banButton = new GUIButton(ban);
        banButton.setAction(() -> {
            sender.closeInventory();

            // Executed by the same staffer who is controlling the player
            if (control.getControlList().get(target.getUniqueId()) == Bukkit.getPlayer(sender.getName()).getUniqueId()) {
                if (!sender.hasPermission("hackcontrol.control.ban")) {
                    Message.send(sender, config.getString("errors.noPermission"));
                    return;
                }
                Message.send(sender, config.getString("control.stafferBanMessage")
                        .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
            }
            // Executed by another staffer
            else {
                if (!sender.hasPermission("hackcontrol.control.ban.others")) {
                    Message.send(sender, config.getString("errors.noPermission"));
                    return;
                }
                Player staffer = Bukkit.getPlayer(control.getControlList().get(target.getUniqueId()));
                Message.send(staffer, config.getString("control.stafferBanMessageOther")
                        .replace("{player}", target.getName()).replace("{staffer}", sender.getName()));
            }

            control.EndControl(target, sender);
            PlayerDataManager.getData(target).setCurrentGUI(null);
            target.closeInventory();

            long duration = System.currentTimeMillis() + 60 * 60 * ((long) config.getDouble("cheatingBan.duration") * 1000);
            Date date = new Date(duration);

            if (config.getBoolean(("cheatingBan.ipBan")))
                plugin.getServer().getBanList(BanList.Type.IP).addBan(target.getAddress().getAddress().getHostAddress(),
                        plugin.getConfig().getString("cheatingBan.reason"), date, "HackControl");
            else
                plugin.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(),
                        plugin.getConfig().getString("cheatingBan.reason"), date, "HackControl");
        });

        if (config.getBoolean("staffer.items.release.enabled"))
            gui.setItem(releaseButton, config.getInt("staffer.items.release.slot"));
        if (config.getBoolean("staffer.items.ban.enabled"))
            gui.setItem(banButton, config.getInt("staffer.items.ban.slot"));

        gui.show(sender);
    }
}
