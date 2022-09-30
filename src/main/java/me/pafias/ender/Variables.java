package me.pafias.ender;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class Variables {

    private final Ender plugin;

    public Variables(Ender plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.reloadConfig();
                reloadConfigYML();
            }
        }.runTaskAsynchronously(plugin);
    }

    public Location serverLobby;
    public Location gameLobby;
    public Location gameSpawn;
    public int maxPlayers;
    public int gameDuration;
    public int games;

    private void reloadConfigYML() {
        FileConfiguration config = plugin.getConfig();
        serverLobby = new Location(
                plugin.getServer().getWorld(config.getString("server_lobby.world")),
                config.getDouble("server_lobby.x"),
                config.getDouble("server_lobby.y"),
                config.getDouble("server_lobby.z"),
                (float) config.getDouble("server_lobby.yaw"),
                (float) config.getDouble("server_lobby.pitch")
        );
        gameLobby = new Location(
                plugin.getServer().getWorld(config.getString("game_lobby.world")),
                config.getDouble("game_lobby.x"),
                config.getDouble("game_lobby.y"),
                config.getDouble("game_lobby.z"),
                (float) config.getDouble("game_lobby.yaw"),
                (float) config.getDouble("game_lobby.pitch")
        );
        gameSpawn = new Location(
                plugin.getServer().getWorld(config.getString("game_lobby.world")),
                config.getDouble("game_lobby.x"),
                config.getDouble("game_lobby.y"),
                config.getDouble("game_lobby.z"),
                (float) config.getDouble("game_lobby.yaw"),
                (float) config.getDouble("game_lobby.pitch")
        );
        maxPlayers = config.getInt("max_players");
        gameDuration = config.getInt("game_duration_minutes");
        games = config.getInt("games");
    }

}
