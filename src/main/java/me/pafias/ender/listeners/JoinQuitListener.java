package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class JoinQuitListener implements Listener {

    private final Ender plugin;

    public JoinQuitListener(Ender plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event){
        plugin.getSM().getPlayerManager().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        // TODO
    }

}
