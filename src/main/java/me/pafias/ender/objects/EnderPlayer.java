package me.pafias.ender.objects;

import org.bukkit.entity.Player;

import java.util.UUID;

public class EnderPlayer {

    private final Player player;

    public EnderPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

}
