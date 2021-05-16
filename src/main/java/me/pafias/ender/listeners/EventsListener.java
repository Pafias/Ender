package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.User;
import me.pafias.ender.game.Game;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class EventsListener implements Listener {

    private final Ender plugin;

    public EventsListener(Ender plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (user.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        if (plugin.getSM().getGameManager().isInGame(user)) {
            Game game = plugin.getSM().getGameManager().getGame(user);
            if (game != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (user.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        if (plugin.getSM().getGameManager().isInGame(user)) {
            Game game = plugin.getSM().getGameManager().getGame(user);
            if (game != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        for (Game game : plugin.getSM().getGameManager().getGames()) {
            if (game.getWorld().getName().equals(event.getWorld().getName()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (user.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        if (plugin.getSM().getGameManager().isInGame(user)) {
            Game game = plugin.getSM().getGameManager().getGame(user);
            if (game != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            User user = plugin.getSM().getUserManager().getUser((Player) event.getEntity());
            if (user.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
            if (plugin.getSM().getGameManager().isInGame(user)) {
                Game game = plugin.getSM().getGameManager().getGame(user);
                if (game != null) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onChange(InventoryInteractEvent event) {
        User user = plugin.getSM().getUserManager().getUser((Player) event.getWhoClicked());
        if (user.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        if (plugin.getSM().getGameManager().isInGame(user)) {
            Game game = plugin.getSM().getGameManager().getGame(user);
            if (game != null)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChange(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame || event.getRightClicked() instanceof ArmorStand)
            event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            User user = plugin.getSM().getUserManager().getUser((Player) event.getEntity());
            if (plugin.getSM().getGameManager().isInGame(user)) {
                Game game = plugin.getSM().getGameManager().getGame(user);
                if (game != null) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
