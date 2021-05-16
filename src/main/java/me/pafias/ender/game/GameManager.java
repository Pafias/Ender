package me.pafias.ender.game;

import me.pafias.ender.Ender;
import me.pafias.ender.User;
import me.pafias.ender.utils.CC;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;

public class GameManager {

    private final Ender plugin;

    public GameManager(Ender plugin) {
        this.plugin = plugin;
    }

    private Set<Game> games = new HashSet<>();

    public Set<Game> getGames() {
        return games;
    }

    public Game getGame() {
        return games.stream().findAny().orElse(null);
    }

    public Game getGame(User user) {
        return games.stream().filter(game -> game.getEveryone().contains(user)).findAny().orElse(null);
    }

    public Game getGame(String s) {
        return games.stream().filter(game -> game.getName().equalsIgnoreCase(s)).findAny().orElse(null);
    }

    public Game getGame(World world) {
        return games.stream().filter(game -> game.getWorld().equals(world)).findAny().orElse(null);
    }

    public boolean isInGame(User user) {
        return getGame(user) != null;
    }

    public void removePlayer(User user) {
        Game game = getGame(user);
        if (game == null) return;
        game.removePlayer(user);
        game.broadcast(CC.translate("&b" + user.getName() + " &8has left the game!"));
        user.getPlayer().getInventory().clear();
        user.getPlayer().teleport(plugin.getSM().getVariables().lobby);
        user.getPlayer().setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());
    }

    public void addPlayer(User user, Game game) {
        game.addPlayer(user);
        user.getPlayer().sendMessage(new String[]{
                CC.translate("&9------------ &6HOW TO PLAY &9------------"),
                CC.translate("&e- Collect hidden pages"),
                CC.translate("&e- Don't look at ender"),
                CC.translate("&e- Use torch to light your way"),
                CC.translate("&e- &lTurn sound on &r&efor a better experience"),
                CC.translate("&9------------------------------------"),
                CC.translate("&4&lWARNING: &cThis game contains flashing lights and jump scares.")
        });
        game.broadcast(CC.translate("&5" + user.getName() + " joined the game. " + game.getPlayers().size() + "/" + game.getMaxPlayers()));
        user.getPlayer().teleport(game.getLobby());
        user.getPlayer().getInventory().clear();
        user.getPlayer().setHealth(user.getPlayer().getMaxHealth());
        user.getPlayer().setFoodLevel(20);
        for (PotionEffect pe : user.getPlayer().getActivePotionEffects()) {
            user.getPlayer().removePotionEffect(pe.getType());
        }
        user.getPlayer().setGameMode(GameMode.ADVENTURE);
        if (game.getPlayers().size() >= game.getMinPlayers()) {
            game.start(false);
        }
        if (!user.getPlayer().hasResourcePack())
            user.getPlayer().setResourcePack("https://www.dropbox.com/s/b74jwhgywl9cc2v/CubeCraft-Ender-Pack.zip?dl=1");
    }

    public void addPlayer(User user) {
        addPlayer(user, getGame());
    }

    public void addGame(Game game) {
        games.add(game);
    }

    public void removeGame(Game game) {
        games.remove(game);
    }

}
