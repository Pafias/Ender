package me.pafias.ender;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

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
                plugin.getConfig().options().copyDefaults(true);
                plugin.saveConfig();
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
    public List<String> pageLocations;
    public int freezeCooldownSeconds;
    public int tpCooldownSeconds;
    public int pages;

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
                plugin.getServer().getWorld(config.getString("game_spawn.world")),
                config.getDouble("game_spawn.x"),
                config.getDouble("game_spawn.y"),
                config.getDouble("game_spawn.z"),
                (float) config.getDouble("game_spawn.yaw"),
                (float) config.getDouble("game_spawn.pitch")
        );
        maxPlayers = config.getInt("max_players");
        gameDuration = config.getInt("game_duration_minutes");
        games = config.getInt("games");
        pageLocations = config.getStringList("page_locations");
        freezeCooldownSeconds = config.getInt("freeze_cooldown_seconds");
        tpCooldownSeconds = config.getInt("teleport_cooldown_seconds");
        pages = config.getInt("pages");
    }

}
