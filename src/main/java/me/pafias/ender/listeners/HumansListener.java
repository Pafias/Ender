package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.GameEndReason;
import me.pafias.ender.game.GameState;
import me.pafias.ender.game.pages.PageManager;
import me.pafias.ender.game.sounds.Sound;
import me.pafias.ender.objects.EnderPlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class HumansListener implements Listener {

    private final Ender plugin;

    public HumansListener(Ender plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer(event.getEntity());
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) return;
        if (!game.getState().equals(GameState.INGAME)) return;
        event.setDeathMessage(null);
        event.getEntity().setHealth(event.getEntity().getMaxHealth());
        event.getEntity().getLocation().getWorld().strikeLightningEffect(event.getEntity().getLocation());
        event.getDrops().clear();
        game.broadcastf("&b%s &7was slain by the Ender!", player.getName());
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        if (event.getEntity().getKiller() != null)
            player.getPlayer().teleport(event.getEntity().getKiller());
        if (game.getLivingHumans().isEmpty()) {
            game.endGame(GameEndReason.HUMANS_KILLED);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof ItemFrame)) return;
        event.setCancelled(true);
        ItemFrame frame = (ItemFrame) event.getEntity();
        if (!frame.getItem().getType().name().contains("MAP")) return;
        handlePage((Player) event.getDamager(), frame);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        event.setCancelled(true);
        ItemFrame frame = (ItemFrame) event.getRightClicked();
        if (!frame.getItem().getType().name().contains("MAP")) return;
        handlePage(event.getPlayer(), frame);
    }

    private void handlePage(Player player, ItemFrame frame) {
        EnderPlayer p = plugin.getSM().getPlayerManager().getPlayer(player);
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) return;
        if (game.pageCooldown.contains(player.getUniqueId())) return;
        else game.pageCooldown.add(player.getUniqueId());
        if (p.isEnder()) return;
        PageManager pm = game.getPageManager();
        frame.setItem(new ItemStack(Material.AIR), false);
        pm.addPage();
        game.broadcastf("&b%s &6collected a page! &b(%d/%d)", p.getName(), pm.getPagesFound(), pm.getTotalPages());
        new BukkitRunnable() {
            @Override
            public void run() {
                game.pageCooldown.remove(player.getUniqueId());
            }
        }.runTaskLater(plugin, 2);
        if (pm.getPagesFound() >= pm.getTotalPages())
            game.endGame(GameEndReason.PAGES_FOUND);
    }

    @EventHandler
    public void onTorch(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        if (!event.getItem().getType().equals(Material.STICK) && !event.getItem().getType().equals(Material.TORCH))
            return;
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer(event.getPlayer());
        player.getTorch().setOn(!player.getTorch().isOn());
        event.setUseItemInHand(Event.Result.DENY);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer(event.getPlayer());
        if (player.isFrozen())
            event.setTo(event.getFrom());
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) return;
        if (game.soundCooldown.contains(event.getPlayer().getUniqueId())) return;
        game.getSoundManager().getTriggerPlaces().keySet().forEach(place -> {
            if (place.isInside(event.getTo())) {
                game.soundCooldown.add(event.getPlayer().getUniqueId());
                Sound sound = game.getSoundManager().getTriggerPlaces().get(place);
                if (sound == null) return;
                sound.play(event.getPlayer(), place.getSourceLocation());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        game.soundCooldown.remove(event.getPlayer().getUniqueId());
                    }
                }.runTaskLaterAsynchronously(plugin, 2 * 60 * 20);
            }
        });
    }

}
