package kheeto.hackcontrol.gui;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class GUIButton {

    @Getter
    private ItemStack stack;
    @Getter
    private Runnable action;

    public GUIButton(ItemStack stack) {
        this.stack = stack;
    }

    public void setAction(Runnable runnable) {
        this.action = runnable;
    }
}
