package me.pafias.ender;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.Set;

public class Variables {

    private final Ender plugin;

    public Variables(Ender plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();
        reloadConfigYML();
    }

    public Location hub;
    public String no_permission;
    public List<String> help;
    public Location lobby;
    public int minPlayers;
    public int maxPlayers;
    public World gameWorld;
    public long gameDuration;
    public int totalPages;
    public List<String> pages;
    public Location playersSpawnLocation;
    public int teleportCooldownSeconds;
    public int freezeCooldownSeconds;
    public List<String> soundsAmbient;
    public List<String> soundsEffect;

    private void reloadConfigYML() {
        hub = new Location(
                plugin.getServer().getWorld(plugin.getConfig().getString("hub.world")),
                plugin.getConfig().getDouble("hub.x"),
                plugin.getConfig().getDouble("hub.y"),
                plugin.getConfig().getDouble("hub.z"),
                (float) plugin.getConfig().getDouble("hub.yaw"),
                (float) plugin.getConfig().getDouble("hub.pitch"));
        no_permission = plugin.getConfig().getString("no_permission");
        help = plugin.getConfig().getStringList("help");
        lobby = new Location(
                plugin.getServer().getWorld(plugin.getConfig().getString("lobby.world")),
                plugin.getConfig().getDouble("lobby.x"),
                plugin.getConfig().getDouble("lobby.y"),
                plugin.getConfig().getDouble("lobby.z"),
                (float) plugin.getConfig().getDouble("lobby.yaw"),
                (float) plugin.getConfig().getDouble("lobby.pitch"));
        minPlayers = plugin.getConfig().getInt("min_players");
        maxPlayers = plugin.getConfig().getInt("max_players");
        gameWorld = plugin.getServer().getWorld(plugin.getConfig().getString("gameworld"));
        gameDuration = plugin.getConfig().getLong("gameduration");
        totalPages = plugin.getConfig().getInt("total_pages");
        pages = plugin.getConfig().getStringList("pages");
        playersSpawnLocation = new Location(
                plugin.getServer().getWorld(plugin.getConfig().getString("playersspawnlocation.world")),
                plugin.getConfig().getDouble("playersspawnlocation.x"),
                plugin.getConfig().getDouble("playersspawnlocation.y"),
                plugin.getConfig().getDouble("playersspawnlocation.z"),
                (float) plugin.getConfig().getDouble("playersspawnlocation.yaw"),
                (float) plugin.getConfig().getDouble("playersspawnlocation.pitch"));
        teleportCooldownSeconds = plugin.getConfig().getInt("teleport_cooldown_seconds");
        freezeCooldownSeconds = plugin.getConfig().getInt("freeze_cooldown_seconds");
        soundsAmbient = plugin.getConfig().getStringList("sounds.ambient");
        soundsEffect = plugin.getConfig().getStringList("sounds.effect");
    }

}
