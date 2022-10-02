package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.objects.EnderPlayer;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ProtectionListener implements Listener {

    private final Ender plugin;

    public ProtectionListener(Ender plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChange(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame)
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Game game = plugin.getSM().getGameManager().getGame(plugin.getSM().getPlayerManager().getPlayer((Player) event.getWhoClicked()));
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.WITHER)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Game game = plugin.getSM().getGameManager().getGame(plugin.getSM().getPlayerManager().getPlayer((Player) event.getEntity()));
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer((Player) event.getEntity());
        Game game = plugin.getSM().getGameManager().getGame(player);
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer(event.getPlayer());
        Game game = plugin.getSM().getGameManager().getGame(player);
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer(event.getPlayer());
        Game game = plugin.getSM().getGameManager().getGame(player);
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            event.setCancelled(true);
    }

}
