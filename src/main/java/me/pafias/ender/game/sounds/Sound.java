package me.pafias.ender.game.sounds;

import me.pafias.ender.Ender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class Sound {

    private final Ender plugin = Ender.get();

    private String name;
    private SoundType type;
    private int amount;
    private Set<Player> playing;

    public Sound(String name, int variations) {
        this.name = name;
        this.amount = variations;
        type = SoundType.valueOf(name.split("\\.")[1].toUpperCase());
        playing = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public SoundType getType() {
        return type;
    }

    public void play(Player player) {
        if (amount == 0) {
            player.playSound(player.getEyeLocation(), getName(), 1f, 1f);
        } else {
            playing.add(player);
            for (int i = 0; i < amount; i++) {
                int finalI = i;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getEyeLocation(), getName() + finalI, 1f, 1f);
                    }
                }.runTaskLater(plugin, (i * 5L) * 20);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    playing.remove(player);
                }
            }.runTaskLater(plugin, (amount * 5L) * 20);
        }
    }

    public void stopPlaying(Player player) {
        player.stopAllSounds();
        playing.remove(player);
    }

    public boolean isPlaying(Player player) {
        return playing.contains(player);
    }

}
