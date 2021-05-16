package me.pafias.ender.listeners;

import me.lucko.helper.Schedulers;
import me.lucko.helper.random.RandomSelector;
import me.pafias.ender.Ender;
import me.pafias.ender.User;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.GameState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;
import java.util.stream.Collectors;

public class EnderListener implements Listener {

    private final Ender plugin;

    public EnderListener(Ender plugin) {
        this.plugin = plugin;
        tpCooldown = new HashMap<>();
        tpCooldownTime = plugin.getSM().getVariables().teleportCooldownSeconds;
        freezeCooldown = new HashMap<>();
        freezeCooldownTime = plugin.getSM().getVariables().freezeCooldownSeconds;
    }

    Map<UUID, Long> tpCooldown;
    int tpCooldownTime;

    Map<UUID, Long> freezeCooldown;
    int freezeCooldownTime;

    List<UUID> frozen = new ArrayList<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (user != null)
            if (plugin.getSM().getGameManager().isInGame(user)) {
                Game game = plugin.getSM().getGameManager().getGame(user);
                if (game != null)
                    if (game.getGamestate().equals(GameState.INGAME))
                        if (game.getEnder() == user)
                            if (event.getAction() == Action.RIGHT_CLICK_AIR
                                    || event.getAction() == Action.RIGHT_CLICK_BLOCK
                                    || event.getAction() == Action.LEFT_CLICK_AIR
                                    || event.getAction() == Action.LEFT_CLICK_BLOCK)
                                if (event.getItem() != null && event.getItem().getType() == Material.PLAYER_HEAD
                                        && event.getItem().getItemMeta().getDisplayName()
                                        .equals(ChatColor.GOLD + "Next player")) {
                                    if (tpCooldown.containsKey(user.getPlayer().getUniqueId())) {
                                        long secondsLeft = ((tpCooldown.get(user.getPlayer().getUniqueId()) / 1000)
                                                + tpCooldownTime) - (System.currentTimeMillis() / 1000);
                                        if (secondsLeft > 0) {
                                            user.getPlayer()
                                                    .sendMessage(ChatColor.RED + "You have to wait "
                                                            + ChatColor.LIGHT_PURPLE + secondsLeft + ChatColor.RED
                                                            + " seconds before you can use this tool again!");
                                            return;
                                        }
                                    }
                                    tpCooldown.put(user.getPlayer().getUniqueId(), System.currentTimeMillis());
                                    user.getPlayer().teleport(RandomSelector.uniform(game.getPlayers().stream().filter(u -> u != user).collect(Collectors.toSet())).pick().getPlayer().getLocation());
                                    Schedulers.async().runLater(() -> {
                                        tpCooldown.remove(user.getPlayer().getUniqueId());
                                    }, plugin.getSM().getVariables().teleportCooldownSeconds * 20L);
                                }
            }
    }

    @EventHandler
    public void onFreeze(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            User ender = plugin.getSM().getUserManager().getUser((Player) event.getDamager());
            User player = plugin.getSM().getUserManager().getUser((Player) event.getEntity());
            if (ender != null && player != null)
                if (plugin.getSM().getGameManager().isInGame(ender) && plugin.getSM().getGameManager().isInGame(player)) {
                    Game game = plugin.getSM().getGameManager().getGame(ender);
                    if (game != null)
                        if (game.getGamestate().equals(GameState.INGAME))
                            if (game.getEnder() == ender && game.getPlayers().contains(player)) {
                                if (ender.getPlayer().getInventory().getItemInMainHand() != null
                                        && ender.getPlayer().getInventory().getItemInMainHand()
                                        .getType() == Material.PACKED_ICE
                                        && ender.getPlayer().getInventory().getItemInMainHand().getItemMeta()
                                        .getDisplayName().equals(ChatColor.GOLD + "Freeze player")) {
                                    event.setCancelled(true);
                                    handle(ender, player);
                                }
                            }
                }
        }
    }

    @EventHandler
    public void interactAt(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            User ender = plugin.getSM().getUserManager().getUser(event.getPlayer());
            User player = plugin.getSM().getUserManager().getUser((Player) event.getRightClicked());
            if (ender != null && player != null)
                if (plugin.getSM().getGameManager().isInGame(ender) && plugin.getSM().getGameManager().isInGame(player)) {
                    Game game = plugin.getSM().getGameManager().getGame(ender);
                    if (game != null)
                        if (game.getGamestate().equals(GameState.INGAME))
                            if (game.getEnder() == ender && game.getPlayers().contains(player)) {
                                if (ender.getPlayer().getInventory().getItemInMainHand() != null
                                        && ender.getPlayer().getInventory().getItemInMainHand()
                                        .getType() == Material.PACKED_ICE
                                        && ender.getPlayer().getInventory().getItemInMainHand().getItemMeta()
                                        .getDisplayName().equals(ChatColor.GOLD + "Freeze player")) {
                                    event.setCancelled(true);
                                    handle(ender, player);
                                }
                            }
                }
        }

    }

    private void handle(User ender, User player) {
        if (freezeCooldown.containsKey(ender.getPlayer().getUniqueId())) {
            long secondsLeft = ((freezeCooldown.get(ender.getPlayer().getUniqueId()) / 1000) + freezeCooldownTime)
                    - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                ender.getPlayer().sendMessage(ChatColor.RED + "You have to wait " + ChatColor.LIGHT_PURPLE + secondsLeft
                        + ChatColor.RED + " seconds before you can use this tool again!");
                return;
            }
        }
        if (!frozen.contains(player.getPlayer().getUniqueId()))
            frozen.add(player.getPlayer().getUniqueId());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getPlayer().getName()
                + " times 5 20 5");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getPlayer().getName()
                + " title [\"\",{\"text\":\"FROZEN\",\"color\":\"aqua\",\"bold\":true}]");
        Schedulers.async().runLater(() -> frozen.remove(player.getPlayer().getUniqueId()), 60);
        freezeCooldown.put(ender.getPlayer().getUniqueId(), System.currentTimeMillis());
        Schedulers.async().runLater(() -> freezeCooldown.remove(ender.getPlayer().getUniqueId()), plugin.getSM().getVariables().freezeCooldownSeconds * 20L);

    }

    @EventHandler
    public void onMoveFrozen(PlayerMoveEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (user == null) return;
        if (!plugin.getSM().getGameManager().isInGame(user)) return;
        Game game = plugin.getSM().getGameManager().getGame(user);
        if (game == null || !game.getGamestate().equals(GameState.INGAME)) return;
        if (frozen.contains(user.getPlayer().getUniqueId()))
            if (event.getTo().getX() != event.getFrom().getX()
                    || event.getTo().getZ() != event.getFrom().getZ())
                event.setCancelled(true);
    }

}
