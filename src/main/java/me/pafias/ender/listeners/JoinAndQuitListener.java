package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndQuitListener implements Listener {

    private final Ender plugin;

    public JoinAndQuitListener(Ender plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getSM().getUserManager().addUser(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (user != null) {
            if (plugin.getSM().getGameManager().isInGame(user))
                user.getPlayer().performCommand("ender leave");
            plugin.getSM().getUserManager().removeUser(event.getPlayer());
        }
    }

}
