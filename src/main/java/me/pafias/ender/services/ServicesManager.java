package me.pafias.ender.services;

import me.pafias.ender.Ender;
import me.pafias.ender.Variables;
import me.pafias.ender.game.GameManager;

public class ServicesManager {

    public ServicesManager(Ender plugin) {
        variables = new Variables(plugin);
        playerManager = new PlayerManager();
        gameManager = new GameManager(plugin);
    }

    private final Variables variables;

    public Variables getVariables() {
        return variables;
    }

    private final PlayerManager playerManager;

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    private final GameManager gameManager;

    public GameManager getGameManager() {
        return gameManager;
    }

}
