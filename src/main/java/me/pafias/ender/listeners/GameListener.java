package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.User;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.GameEndReason;
import me.pafias.ender.game.GameState;
import me.pafias.ender.utils.CC;
import me.pafias.ender.utils.RandomUtils;
import me.pafias.ender.utils.Renderer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
            Game game = plugin.getSM().getGameManager().getGame(user);
            if (game == null) return;
            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
            if (event.getItem().getType().equals(Material.STICK)) {
                event.getItem().setType(Material.TORCH);
                event.getItem().getItemMeta().setDisplayName(CC.translate("&6Torch &f- &aON"));
            } else if (event.getItem().getType().equals(Material.TORCH)) {
                event.getItem().setType(Material.STICK);
                event.getItem().getItemMeta().setDisplayName(CC.translate("&6Torch &f- &cOFF"));
            }
        }
    }

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {
        Game game = plugin.getSM().getGameManager().getGame(event.getMap().getWorld());
        if (game == null) return;
        event.getMap().setScale(MapView.Scale.CLOSEST);
        event.getMap().setUnlimitedTracking(false);
        event.getMap().getRenderers().clear();
        try {
            BufferedImage image = ImageIO.read(RandomUtils.getRandomPage());
            event.getMap().addRenderer(new Renderer(image));
        } catch (IOException ignored) {
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            User user = plugin.getSM().getUserManager().getUser((Player) event.getEntity());
            if (user != null)
                if (plugin.getSM().getGameManager().isInGame(user)) {
                    Game game = plugin.getSM().getGameManager().getGame(user);
                    if (game != null)
                        if (!event.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM))
                            event.setCancelled(true);
                }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            User user = plugin.getSM().getUserManager().getUser((Player) event.getEntity());
            if (user != null)
                if (plugin.getSM().getGameManager().isInGame(user)) {
                    Game game = plugin.getSM().getGameManager().getGame(user);
                    if (game != null)
                        if (game.getGamestate() != GameState.INGAME)
                            event.setCancelled(true);
                        else if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
                            event.setDamage(0);
                }
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            User user = plugin.getSM().getUserManager().getUser((Player) event.getEntity());
            if (user != null)
                if (plugin.getSM().getGameManager().isInGame(user)) {
                    Game game = plugin.getSM().getGameManager().getGame(user);
                    if (game != null)
                        event.setCancelled(true);
                }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (user != null) {
            if (plugin.getSM().getGameManager().isInGame(user)) {
                Game game = plugin.getSM().getGameManager().getGame(user);
                if (game == null) return;
                if (user == game.getEnder())
                    if (game.getGamestate().equals(GameState.PREGAME))
                        event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getEntity());
        if (user != null) {
            if (plugin.getSM().getGameManager().isInGame(user)) {
                Game game = plugin.getSM().getGameManager().getGame(user);
                if (game != null) {
                    if (game.getGamestate() == GameState.INGAME) {
                        user.getPlayer()
                                .setHealth(user.getPlayer().getMaxHealth());
                        user.getPlayer().getLocation().getWorld().strikeLightningEffect(user.getPlayer().getLocation());
                        event.getDrops().clear();
                        game.broadcast(CC.translate("&b" + user.getName() + " &7was slain by the Ender!"));
                        if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
                            User killer = plugin.getSM().getUserManager().getUser(event.getEntity().getKiller());
                            if (killer != user && killer == game.getEnder()) {
                                game.setSpectator(user);
                                user.getPlayer().teleport(killer.getPlayer().getLocation());
                            }
                        } else {
                            game.setSpectator(user);
                        }
                        if (game.getPlayers().size() <= 1)
                            game.endGame(GameEndReason.ALL_PLAYERS_DEAD);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            User user = plugin.getSM().getUserManager().getUser((Player) event.getDamager());
            if (user != null)
                if (plugin.getSM().getGameManager().isInGame(user)) {
                    if (user.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) return;
                    Game game = plugin.getSM().getGameManager().getGame(user);
                    if (game != null)
                        if (game.getGamestate() == GameState.INGAME)
                            if (event.getEntity() instanceof ItemFrame
                                    && ((ItemFrame) event.getEntity()).getItem().getType() == Material.FILLED_MAP) {
                                event.setCancelled(true);
                                if (user != game.getEnder()) {
                                    event.getEntity().remove();
                                    handle(user, game);
                                }
                            }
                }
        }
    }

    @EventHandler
    public void onPage(PlayerInteractAtEntityEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (user != null)
            if (plugin.getSM().getGameManager().isInGame(user)) {
                if (user.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) return;
                Game game = plugin.getSM().getGameManager().getGame(user);
                if (game != null)
                    if (game.getGamestate() == GameState.INGAME)
                        if (event.getRightClicked() instanceof ItemFrame
                                && ((ItemFrame) event.getRightClicked()).getItem().getType() == Material.FILLED_MAP) {
                            event.setCancelled(true);
                            ((ItemFrame) event.getRightClicked()).setRotation(Rotation.NONE);
                            if (user != game.getEnder()) {
                                ((ItemFrame) event.getRightClicked()).setRotation(Rotation.NONE);
                                ((ItemFrame) event.getRightClicked()).remove();
                                handle(user, game);
                            }
                        }
            }
    }

    private void handle(User user, Game game) {
        game.addPage();
        game.broadcast(ChatColor.GREEN + user.getName() + ChatColor.GOLD + " found a page!"
                + ChatColor.AQUA + " (" + game.getPagesFound() + "/" + game.getTotalPages() + ")");
        if (game.getPagesFound() >= game.getTotalPages())
            game.endGame(GameEndReason.ALL_PAGES_FOUND);
    }
}
