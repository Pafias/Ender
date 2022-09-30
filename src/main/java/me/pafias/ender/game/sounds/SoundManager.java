package me.pafias.ender.game.sounds;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.util.RandomUtils;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SoundManager {

    private final Ender plugin;

    private final Game game;

    public SoundManager(Ender plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
        sounds = new HashSet<>();
        sounds.add(new Sound("custom.ambient.a", 14));
        sounds.add(new Sound("custom.ambient.b", 40));
        sounds.add(new Sound("custom.ambient.c", 28));
        sounds.add(new Sound("custom.effect.chains", 4));
        sounds.add(new Sound("custom.effect.creepy", 1));
        sounds.add(new Sound("custom.effect.helpme", 0));
        sounds.add(new Sound("custom.effect.hurts", 0));
        sounds.add(new Sound("custom.effect.intro", 0));
        sounds.add(new Sound("custom.effect.laugh", 5));
        sounds.add(new Sound("custom.effect.laughlong", 0));
        sounds.add(new Sound("custom.effect.piano", 0));
        sounds.add(new Sound("custom.effect.saveme", 0));
        sounds.add(new Sound("custom.effect.scream", 1));
        sounds.add(new Sound("custom.effect.static", 4));
    }

    private Set<Sound> sounds;

    public void playRandomAmbient(Player player) {
        Set<Sound> ambient = sounds.stream().filter(sound -> sound.getType().equals(SoundType.AMBIENT)).collect(Collectors.toSet());
        Sound sound = RandomUtils.getRandom(ambient);
        sound.play(player);
    }

    public void playEffect(Player player, String name) {
        Sound sound = sounds.stream().filter(s -> s.getType().equals(SoundType.EFFECT) && s.getName().split("\\.")[2].equalsIgnoreCase(name)).findAny().get();
        sound.play(player);
    }

    public boolean isPlayingSound(Player player) {
        for (Sound s : sounds)
            if (s.isPlaying(player))
                return true;
        return false;
    }

}
