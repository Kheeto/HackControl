package kheeto.hackcontrol.gui;

import kheeto.hackcontrol.HackControl;
import kheeto.hackcontrol.data.PlayerData;
import kheeto.hackcontrol.data.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        PlayerData data = PlayerDataManager.getData((Player)e.getWhoClicked());
        if (data == null || data.getCurrentGUI() == null) return;

        data.getCurrentGUI().handleButton(e.getCurrentItem());
        e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerDataManager.setData(e.getPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        PlayerData data = PlayerDataManager.getData((Player)e.getPlayer());
        if (data == null || data.getCurrentGUI() == null) return;

        if (!data.getCurrentGUI().isCloseable()) {
            new BukkitRunnable(){
                @Override
                public void run(){
                    e.getPlayer().openInventory(e.getInventory());
                }
            }.runTaskLater(HackControl.getInstance(),  10);
        }
        else
            data.setCurrentGUI(null);
    }
}
