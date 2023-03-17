package kheeto.hackcontrol.gui;

import kheeto.hackcontrol.HackControl;
import kheeto.hackcontrol.data.PlayerDataManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUI {
    private final int rows;
    @Getter
    private Inventory inventory;
    private List<GUIButton> buttons = new ArrayList<GUIButton>();
    private HackControl plugin;
    @Getter
    private boolean closeable;

    public GUI(String title, int rows) {
        this(title, rows, true);
    }

    public GUI(String title, int rows, boolean closeable) {
        this.plugin = HackControl.getInstance();

        if ((rows * 9) % 9 != 0) {
            plugin.getLogger().info("Failed to initialize GUI \""+title+"\", invalid row count.");
            this.rows = 0;
            return;
        }

        this.rows = rows;
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.closeable = closeable;
    }

    public void setItem(GUIButton button, int slot) {
        buttons.add(button);
        inventory.setItem(slot, button.getStack());
    }

    public void handleButton(ItemStack stack) {
        GUIButton button = buttons.stream().filter(p -> p.getStack().isSimilar(stack)).findAny().orElse(null);
        if (button != null) {
            button.getAction().run();
        }
    }

    public void show(Player p) {
        PlayerDataManager.getData(p).setCurrentGUI(this);
        p.openInventory(inventory);
    }
}
