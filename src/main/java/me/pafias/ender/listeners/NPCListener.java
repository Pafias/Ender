package me.pafias.ender.listeners;

import me.pafias.ender.gui.GamesMenu;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class NPCListener implements Listener {

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
        GamesMenu menu = new GamesMenu(player);
        menu.open();
    }

}
