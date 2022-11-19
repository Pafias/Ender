package me.pafias.ender.game;

import me.pafias.ender.Ender;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.CC;
import me.pafias.ender.util.Countdown;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager {

    private final Ender plugin;

    public GameManager(Ender plugin) {
        this.plugin = plugin;
        new BukkitRunnable() {
            @Override
            public void run() {
                createGame();
            }
        }.runTaskLaterAsynchronously(plugin, 3 * 20);
    }

    private Game game;

    public Game getGame() {
        return game;
    }

    public Game createGame() {
        try {
            game = new Game();
            return game;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void addPlayer(Player player) {
        addPlayer(plugin.getSM().getPlayerManager().getPlayer(player));
    }

    public void addPlayer(EnderPlayer player) {
        if (game.getPlayers().size() >= game.getMaxPlayers()) return;
        if (!game.getState().equals(GameState.LOBBY)) return;
        Player p = player.getPlayer();
        p.getInventory().clear();
        p.setLevel(0);
        p.setExp(0);
        p.teleport(game.getLobby().getGameWorld().getSpawnLocation());
        p.getPlayer().sendMessage(CC.t("&3------------ &6HOW TO PLAY &3------------"));
        p.getPlayer().sendMessage(CC.t("&e- Collect hidden pages"));
        p.getPlayer().sendMessage(CC.t("&e- Don't look at ender"));
        p.getPlayer().sendMessage(CC.t("&e- Use torch to light your way"));
        p.getPlayer().sendMessage(CC.t("&e&l- Turn sound on &r&efor a better experience"));
        p.getPlayer().sendMessage(CC.t("&3------------------------------------"));
        p.getPlayer().sendMessage(CC.t("&c&lWARNING: &r&cThis game contains flashing lights and jump scares."));
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        game.addPlayer(player);
        if (!p.hasResourcePack())
            p.setResourcePack("https://www.dropbox.com/s/o9faxsfuz7pului/Ender-Pack-1_16.zip?dl=1");
        if (game.getPlayers().size() >= 2) {
            if (!game.getCountdownTasks().containsKey("start")) {
                game.getCountdownTasks().put("start", new Countdown(plugin, plugin.getSM().getVariables().gameStartCountdown, () -> {
                },
                        () -> {
                            game.getCountdownTasks().remove("start");
                            game.start();
                        },
                        (t) -> {
                            game.getPlayers().forEach(pp -> {
                                pp.getPlayer().setLevel((int) t.getSecondsLeft());
                                pp.getPlayer().setExp(t.getSecondsLeft() / t.getTotalSeconds());
                                if (t.getSecondsLeft() == 15 || t.getSecondsLeft() == 10 || t.getSecondsLeft() <= 5) {
                                    pp.getPlayer().sendMessage(CC.tf("&9Ender starting in %d seconds!", (int) t.getSecondsLeft()));
                                    pp.getPlayer().playSound(pp.getPlayer().getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f, 1f);
                                }
                            });
                        }
                ).scheduleTimer());
            }
        }
    }

    public void removePlayer(EnderPlayer player) {
        game.removePlayer(player);
        game.broadcastf("&b%s &8has left the game!", player.getName());
        if (game.getState().equals(GameState.LOBBY) && game.getPlayers().size() < 2) {
            game.cancelTask("start");
            game.broadcastf("&cNot enough players.");
            game.getPlayers().forEach(pp -> {
                pp.getPlayer().setLevel(0);
                pp.getPlayer().setExp(0);
            });
        }
    }

}
