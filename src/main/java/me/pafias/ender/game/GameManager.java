package me.pafias.ender.game;

import me.pafias.ender.Ender;
import me.pafias.ender.gui.GamesMenu;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.CC;
import me.pafias.ender.util.Countdown;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameManager {

    private final Ender plugin;

    public GameManager(Ender plugin) {
        this.plugin = plugin;
        gui = new GamesMenu();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < plugin.getSM().getVariables().games; i++)
                    createGame();
            }
        }.runTaskLaterAsynchronously(plugin, 3 * 20);
    }

    private final GamesMenu gui;

    public GamesMenu getGUI() {
        return gui;
    }

    private final Set<Game> games = new HashSet<>();

    public Set<Game> getGames() {
        return games;
    }

    public Game getGame(UUID uuid) {
        return games.stream().filter(game -> game.getUUID().equals(uuid)).findAny().orElse(null);
    }

    public Game getGame(String guiItemName) {
        return games.stream().filter(game -> game.getUUID().toString().contains(guiItemName) || guiItemName.contains(game.getUUID().toString())).findAny().orElse(null);
    }

    public Game getGame(World world) {
        return games.stream().filter(game -> game.getWorld().getGameWorld().getName().equalsIgnoreCase(world.getName())).findAny().orElse(null);
    }

    public Game getGame(EnderPlayer player) {
        return games.stream().filter(game -> game.getPlayers().contains(player)).findAny().orElse(null);
    }

    public void createGame() {
        try {
            Game game = new Game();
            games.add(game);
            getGUI().update();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeGame(Game game) {
        games.remove(game);
    }

    public void addPlayer(Player player, Game game) {
        addPlayer(plugin.getSM().getPlayerManager().getPlayer(player), game);
    }

    public void addPlayer(EnderPlayer player, Game game) {
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
            p.setResourcePack("https://www.dropbox.com/s/b74jwhgywl9cc2v/CubeCraft-Ender-Pack.zip?dl=1");
        if (game.getPlayers().size() >= 2) {
            if (!game.getCountdownTasks().containsKey("start")) {
                game.getCountdownTasks().put("start", new Countdown(plugin, 15, () -> {
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
        getGUI().update();
    }

    public void removePlayer(EnderPlayer player, Game game) {
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
        getGUI().update();
    }

    public void removePlayer(EnderPlayer player) {
        Game game = getGame(player);
        if (game != null)
            removePlayer(player, game);
    }

}
