package kheeto.hackcontrol.data;

import kheeto.hackcontrol.gui.GUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class PlayerData {

    @Getter
    private Player player;

    @Getter
    @Setter
    private GUI currentGUI;

    public PlayerData(Player player) {
        this.player = player;
    }
}
