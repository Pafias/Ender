package me.pafias.ender.utils;

import me.lucko.helper.random.RandomSelector;
import me.pafias.ender.Ender;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class RandomUtils {

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
        return RandomSelector.uniform(sounds.stream().filter(s -> !s.equalsIgnoreCase("custom.effect.intro")).collect(Collectors.toList())).pick();
    }

    public static File getRandomPage() {
        return new File(Ender.get().getDataFolder() + "/pages/", "page" + new Random().nextInt(9) + ".png");
    }

    public static ItemStack getSkull(UUID skin) {
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
