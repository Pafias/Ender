package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.CC;
import me.pafias.ender.util.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EnderListener implements Listener {

    private final Ender plugin;

    public EnderListener(Ender plugin) {
        this.plugin = plugin;
    }

    private Map<UUID, Long> tpCooldown = new HashMap<>();

    @EventHandler
    public void onTeleportItem(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer(event.getPlayer());
        if (!player.isEnder()) return;
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) return;
        if (!event.getItem().getType().equals(Material.PLAYER_HEAD)) return;
        if (tpCooldown.get(event.getPlayer().getUniqueId()) != 0) {
            long secondsLeft = ((tpCooldown.get(event.getPlayer().getUniqueId()) / 1000) + plugin.getSM().getVariables().tpCooldownSeconds) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                event.getPlayer().sendMessage(CC.tf("&cYou cannot use &bTeleport &cfor another %d seconds!", (int) secondsLeft));
                return;
            }
        }
        Set<EnderPlayer> humans = game.getHumans();
        if (!humans.isEmpty()) {
            EnderPlayer target = RandomUtils.getRandom(humans);
            event.getPlayer().teleport(target.getPlayer().getLocation().clone().add(new Random().nextDouble(), 0, new Random().nextDouble()));
            event.getPlayer().sendMessage(CC.t("&6You teleported to &ba human"));
            tpCooldown.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            new BukkitRunnable() {
                @Override
                public void run() {
                    tpCooldown.remove(event.getPlayer().getUniqueId());
                    event.getPlayer().sendMessage(CC.t("&6You can now use your &bTeleport &6ability!"));
                }
            }.runTaskLater(plugin, plugin.getSM().getVariables().tpCooldownSeconds * 20L);
        }
        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
    }

    private long freezeCooldown;

    @EventHandler
    public void onFreezeItem(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer(event.getPlayer());
        if (!player.isEnder()) return;
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) return;
        if (!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.PACKED_ICE)) return;
        if (freezeCooldown != 0) {
            long secondsLeft = ((freezeCooldown / 1000) + plugin.getSM().getVariables().freezeCooldownSeconds) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                event.getPlayer().sendMessage(CC.tf("&cYou cannot use &bFreeze &cfor another %d seconds!", (int) secondsLeft));
                return;
            }
        }
        EnderPlayer target = plugin.getSM().getPlayerManager().getPlayer((Player) event.getRightClicked());
        freeze(target);
        target.getPlayer().sendTitle(CC.t("&b&lFROZEN"), "", 5, 20, 5);
        freezeCooldown = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                freezeCooldown = 0;
                event.getPlayer().sendMessage(CC.t("&6You can now use your &bFreeze &6ability!"));
            }
        }.runTaskLater(plugin, plugin.getSM().getVariables().freezeCooldownSeconds * 20L);
        event.setCancelled(true);
    }

    private void freeze(EnderPlayer player) {
        player.setFrozen(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setFrozen(false);
            }
        }.runTaskLater(plugin, 3 * 20);
    }

}
