package me.pafias.ender.game;

import me.pafias.ender.Ender;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.Countdown;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameManager {

    private final Ender plugin;

    public GameManager(Ender plugin) {
        this.plugin = plugin;
        for (int i = 0; i < plugin.getSM().getVariables().games; i++)
            createGame();
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
        return games.stream().filter(game -> game.getWorld().getGameWorld() == world).findAny().orElse(null);
    }

    public void createGame() {
        try {
            Game p = new Game();
            games.add(p);
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
        p.teleport(plugin.getSM().getVariables().gameLobby);
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();
        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        game.addPlayer(player);
        if (game.getPlayers().size() >= 2) {
            if (!game.getCountdownTasks().containsKey("start")) {
                game.getCountdownTasks().put("start", new Countdown(plugin, 15, () -> {
                },
                        () -> {
                            game.getCountdownTasks().remove("start");
                            game.start();
                        },
                        (t) -> {
                            game.broadcastf("The game starts in %s seconds!", t.getSecondsLeft());
                            game.getPlayers().forEach(pp -> {
                                pp.getPlayer().setLevel((int) t.getSecondsLeft());
                                pp.getPlayer().setExp(t.getSecondsLeft() / t.getTotalSeconds());
                                pp.getPlayer().playSound(pp.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f, 1f);
                            });
                        }
                ).scheduleTimer());
            }
        }
    }

    public void removePlayer(EnderPlayer player, Game game) {
        game.removePlayer(player);
        game.broadcastf("&d%s &7left the game.");
        if (game.getPlayers().size() < 2 && game.getState().equals(GameState.LOBBY)) {
            game.cancelTask("start");
            game.broadcastf("&cNot enough players.");
            game.getPlayers().forEach(pp -> {
                pp.getPlayer().setLevel(0);
                pp.getPlayer().setExp(0);
            });
        }
    }

}
