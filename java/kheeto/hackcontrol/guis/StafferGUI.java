package kheeto.hackcontrol.guis;

import kheeto.hackcontrol.HackControl;
import kheeto.hackcontrol.commands.Control;
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
                Material.getMaterial(guiConfig.getString("staffer.items.release.material")));
        ItemMeta releaseMeta = release.getItemMeta();
        releaseMeta.setDisplayName(guiConfig.getString("staffer.items.release.name"));
        releaseMeta.setLore(guiConfig.getStringList("staffer.items.release.lore"));
        release.setItemMeta(releaseMeta);
        GUIButton acceptButton = new GUIButton(release);
        acceptButton.setAction(() -> {
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
        });

        // Ban button
        ItemStack admit = new ItemStack(
                Material.getMaterial(guiConfig.getString("staffer.items.ban.material")));
        ItemMeta admitMeta = admit.getItemMeta();
        admitMeta.setDisplayName(guiConfig.getString("staffer.items.ban.name"));
        admitMeta.setLore(guiConfig.getStringList("staffer.items.ban.lore"));
        admit.setItemMeta(admitMeta);
        GUIButton admitButton = new GUIButton(admit);
        admitButton.setAction(() -> {
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
            long duration = System.currentTimeMillis() + 60 * 60 * ((long) config.getDouble("cheatingBan.duration") * 1000);
            Date date = new Date(duration);

            if (config.getBoolean(("cheatingBan.ipBan")))
                plugin.getServer().getBanList(BanList.Type.IP).addBan(target.getAddress().getAddress().getHostAddress(),
                        plugin.getConfig().getString("cheatingBan.reason"), date, "HackControl");
            else
                plugin.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(),
                        plugin.getConfig().getString("cheatingBan.reason"), date, "HackControl");
        });
    }
}
