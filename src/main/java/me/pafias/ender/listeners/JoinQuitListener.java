package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.util.CC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final Ender plugin;

    public JoinQuitListener(Ender plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        plugin.getSM().getPlayerManager().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(CC.tf("&e%s joined the server.", event.getPlayer().getName()));
        event.getPlayer().teleport(plugin.getSM().getVariables().serverLobby);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getSM().getPlayerManager().removePlayer(event.getPlayer());
        event.setQuitMessage(CC.tf("&e%s left the server.", event.getPlayer().getName()));
    }

}
