package me.pafias.ender.game;

import java.util.UUID;

public class Game {

    private final UUID uuid;

    public Game(GameWorld world) {
        uuid = UUID.randomUUID();
    }

    public UUID getUUID() {
        return uuid;
    }

}
