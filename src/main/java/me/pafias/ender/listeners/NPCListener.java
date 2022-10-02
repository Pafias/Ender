package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.objects.EnderPlayer;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class NPCListener implements Listener {

    private final Ender plugin;

    public NPCListener(Ender plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Enderman) {
            event.setCancelled(true);
            handle((Player) event.getDamager());
        }
    }

    @EventHandler
    public void onInteractAt(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Enderman) {
            event.setCancelled(true);
            handle(event.getPlayer());
        }
    }

    private void handle(Player player) {
        EnderPlayer p = plugin.getSM().getPlayerManager().getPlayer(player);
        Game game = plugin.getSM().getGameManager().getGame(p);
        if (game != null) return;
        plugin.getSM().getGameManager().getGUI().open(player);
    }

}
