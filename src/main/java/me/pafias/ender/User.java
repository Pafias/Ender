package me.pafias.ender;

import org.bukkit.entity.Player;

import java.util.UUID;

public class User {

    private Player player;
    private int flashlightbattery;

    public User(Player player) {
        this.player = player;
        this.flashlightbattery = 100;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return player.getName();
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public int getFlashlightbattery() {
        return flashlightbattery;
    }

    public void drainFlashlight() {
        if (this.flashlightbattery <= 0)
            this.flashlightbattery = 0;
        this.flashlightbattery = this.flashlightbattery - 5;
    }

    public void setFlashlightbattery(int flashlightbattery) {
        this.flashlightbattery = flashlightbattery;
    }

}
