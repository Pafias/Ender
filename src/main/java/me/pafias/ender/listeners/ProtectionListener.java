package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import org.bukkit.GameMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
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
        if (event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)) return;
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.WITHER)) return;
        if (!(event.getEntity() instanceof Player)) return;
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getWorld().getName().equalsIgnoreCase(plugin.getSM().getVariables().serverLobby.getWorld().getName()))
            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                event.setCancelled(true);
                return;
            }
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getWorld().getName().equalsIgnoreCase(plugin.getSM().getVariables().serverLobby.getWorld().getName()))
            if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                event.setCancelled(true);
                return;
            }
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player && ((Player) event.getRemover()).getGameMode().equals(GameMode.CREATIVE))
            return;
        event.setCancelled(true);
    }

}
