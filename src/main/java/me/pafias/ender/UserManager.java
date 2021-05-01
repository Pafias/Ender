package me.pafias.ender;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    private final Ender plugin;

    public UserManager(Ender plugin) {
        this.plugin = plugin;
    }

    private Set<User> users = new HashSet<>();

    public Set<User> getUsers() {
        return users;
    }

    public User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public User getUser(UUID uuid) {
        return users.stream().filter(user -> user.getUUID().equals(uuid)).findAny().orElse(null);
    }

    public User getUser(String name) {
        return users.stream().filter(user -> user.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public void addUser(Player player) {
        users.add(new User(player));
    }

    public void removeUser(Player player) {
        for (PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setPlayerListName(ChatColor.RESET + player.getDisplayName());
        users.remove(getUser(player));
    }

}
