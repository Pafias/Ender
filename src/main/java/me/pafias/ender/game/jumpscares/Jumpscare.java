package me.pafias.ender.game.jumpscares;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import org.bukkit.entity.Player;

public abstract class Jumpscare {

    public Ender plugin;
    public Game game;

    public Jumpscare(Ender plugin, Game game){
        this.plugin = plugin;
        this.game = game;
    }

    public abstract void execute(Player player);

}
