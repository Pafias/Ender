package me.pafias.ender.services;

import me.pafias.ender.Ender;
import me.pafias.ender.Variables;

public class ServicesManager {

    public ServicesManager(Ender plugin){
        variables = new Variables(plugin);
        playerManager = new PlayerManager();
    }

    private final Variables variables;

    public Variables getVariables(){
        return variables;
    }

    private final PlayerManager playerManager;

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
}
