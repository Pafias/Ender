package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.GameState;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.PageRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GameListener implements Listener {

    private final Ender plugin;

    public GameListener(Ender plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer(event.getPlayer());
        Game game = plugin.getSM().getGameManager().getGame(player);
        if (game == null) return;
        if (game.getState().equals(GameState.PREGAME))
            if (event.getTo().getX() != event.getFrom().getX() || event.getTo().getZ() != event.getFrom().getZ())
                event.setTo(event.getFrom());
    }

    @EventHandler
    public void onOffHand(PlayerSwapHandItemsEvent event) {
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer(event.getPlayer());
        Game game = plugin.getSM().getGameManager().getGame(player);
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {
        Game game = plugin.getSM().getGameManager().getGame(event.getMap().getWorld());
        if (game == null) return;
        event.getMap().setScale(MapView.Scale.CLOSEST);
        event.getMap().setUnlimitedTracking(false);
        event.getMap().getRenderers().clear();
        try {
            BufferedImage image = ImageIO.read(game.getPageManager().getRandomPage());
            event.getMap().addRenderer(new PageRenderer(image));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
