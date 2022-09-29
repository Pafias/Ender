package me.pafias.ender.game;

import org.bukkit.World;

public class GameWorld {

    private World originalWorld;
    private World gameWorld;

    public GameWorld(World world){
        originalWorld = world;
    }

    public World getOriginalWorld() {
        return originalWorld;
    }

    public World getGameWorld() {
        return gameWorld;
    }

}
