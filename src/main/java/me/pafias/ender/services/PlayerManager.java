package me.pafias.ender.services;

import me.pafias.ender.Ender;
import me.pafias.ender.objects.EnderPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {

    private final Ender plugin;

    public PlayerManager(Ender plugin) {
        this.plugin = plugin;
    }

    private final Set<EnderPlayer> players = new HashSet<>();

    public Set<EnderPlayer> getPlayers() {
        return players;
    }

    public EnderPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public EnderPlayer getPlayer(UUID uuid) {
        return players.stream().filter(p -> p.getUUID().equals(uuid)).findAny().orElse(null);
    }

    public EnderPlayer getPlayer(String name) {
        return players.stream().filter(p -> p.getName().toLowerCase().startsWith(name.toLowerCase())).findAny().orElse(null);
    }

    public void addPlayer(Player player) {
        EnderPlayer p = new EnderPlayer(player);
        players.add(p);
    }

    public void removePlayer(Player player) {
        EnderPlayer p = getPlayer(player);
        plugin.getSM().getGameManager().removePlayer(p);
        players.remove(p);
    }

}
