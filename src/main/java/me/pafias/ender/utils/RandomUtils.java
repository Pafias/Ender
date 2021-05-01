package me.pafias.ender.utils;

import me.pafias.ender.Ender;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class RandomUtils {

    public static <T> T getRandom(List<T> list) {
        Random random = new Random();
        T object = list.get(random.nextInt(list.size()));
        return object;
    }

    public static <T> T getRandom(Set<T> set) {
        List<T> list = new ArrayList<>(set);
        Random random = new Random();
        T object = list.get(random.nextInt(list.size()));
        return object;
    }

    public static String getRandomAmbientSound() {
        Random random = new Random();
        String letter = "a";
        switch (random.nextInt(3)) {
            case 0:
                letter = "a";
                break;
            case 1:
                letter = "b";
                break;
            case 2:
                letter = "c";
                break;
        }
        int index = 4;
        switch (letter) {
            case "a":
                index = random.nextInt(15);
                break;
            case "b":
                index = random.nextInt(41);
                break;
            case "c":
                index = random.nextInt(29);
                break;
        }
        return "custom.ambient." + letter + index;
    }

    public static String getRandomEffectSound(List<String> sounds) {
        Random random = new Random();
        List<String> list = sounds.stream().filter(s -> !s.equalsIgnoreCase("custom.effect.intro")).collect(Collectors.toList());
        String sound = list.get(random.nextInt(list.size()));
        return sound;
    }

    public static File getRandomPage() {
        Random random = new Random();
        int index = random.nextInt(9);
        File file = new File(Ender.get().getDataFolder() + "/pages/", "page" + index + ".png");
        return file;
    }

    public static ItemStack getSkull(UUID skin){
        ItemStack skull = new ItemStack(Material.LEGACY_SKULL_ITEM, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Ender.get().getServer().getOfflinePlayer(skin));
        skull.setItemMeta(meta);
        return skull;
    }

    public static Team registerTeam(Scoreboard scoreboard, String teamName, String teamDisplayname, String prefix, ChatColor color) {
        Team team;
        team = scoreboard.registerNewTeam(teamName);
        team.setDisplayName(teamDisplayname);
        team.setPrefix(prefix);
        team.setColor(color);
        return team;
    }

}
