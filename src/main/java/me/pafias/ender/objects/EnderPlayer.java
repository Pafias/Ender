package me.pafias.ender.objects;

import me.pafias.ender.game.Torch;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EnderPlayer {

    private final Player player;

    private boolean ender;
    private Torch torch;
    private boolean frozen;

    public EnderPlayer(Player player) {
        this.player = player;
        ender = false;
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

    public boolean isEnder() {
        return ender;
    }

    public void setEnder(boolean ender) {
        this.ender = ender;
    }

    public Torch getTorch() {
        return torch;
    }

    public void setTorch(Torch torch) {
        this.torch = torch;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
}
