package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.GameState;
import me.pafias.ender.util.CC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final Ender plugin;

    public JoinQuitListener(Ender plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (plugin.getSM().getGameManager().getGame() == null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.t("&cThis game is not available."));
            return;
        }
        Game game = plugin.getSM().getGameManager().getGame();
        if (game.getState().equals(GameState.STARTING)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.t("&cThis game is still starting. Try again in a few seconds."));
            return;
        }
        if (!game.getState().equals(GameState.LOBBY)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.t("&cThis game has already started!"));
            return;
        } else if (game.getPlayers().size() >= game.getMaxPlayers()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, CC.t("&cThis game is full!"));
            return;
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        plugin.getSM().getPlayerManager().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getSM().getGameManager().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getSM().getPlayerManager().removePlayer(event.getPlayer());
    }

}
