package me.pafias.ender;

import me.pafias.ender.game.GameManager;

public class ServicesManager {

    private final Ender plugin;

    public ServicesManager(Ender plugin) {
        this.plugin = plugin;
        userManager = new UserManager(plugin);
        gameManager = new GameManager(plugin);
        variables = new Variables(plugin);
    }

    private UserManager userManager;

    public UserManager getUserManager() {
        return userManager;
    }

    private GameManager gameManager;

    public GameManager getGameManager() {
        return gameManager;
    }

    private Variables variables;

    public Variables getVariables() {
        return variables;
    }

}
