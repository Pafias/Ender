package me.pafias.ender.game;

import me.pafias.ender.util.CC;

public enum GameState {

    STARTING("Game starting..."), LOBBY("&aWaiting..."), PREGAME("&cStarted"), INGAME("&cIn game"), POSTGAME("&bEnding...");

    private String name;

    GameState(String name) {
        this.name = name;
    }

    public String getName() {
        return CC.t(name);
    }

}
