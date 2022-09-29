package me.pafias.ender.game;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameManager {

    public GameManager() {

    }

    private final Set<Game> games = new HashSet<>();

    public Set<Game> getGames() {
        return games;
    }

    public Game getGame(UUID uuid) {
        return games.stream().filter(game -> game.getUUID().equals(uuid)).findAny().orElse(null);
    }

    public void addGame(GameWorld world) {
        Game p = new Game(world);
        games.add(p);
    }

    public void removeGame(Game game) {
        games.remove(game);
    }

}
