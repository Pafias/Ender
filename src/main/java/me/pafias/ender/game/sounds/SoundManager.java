package me.pafias.ender.game.sounds;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.TriggerPlace;
import me.pafias.ender.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SoundManager {

    private final Ender plugin;

    private final Game game;

    public SoundManager(Ender plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
        sounds = new HashSet<>();
        sounds.add(new Sound(game, "custom.ambient.a", 14));
        sounds.add(new Sound(game, "custom.ambient.b", 40));
        sounds.add(new Sound(game, "custom.ambient.c", 28));
        sounds.add(new Sound(game, "custom.effect.chains", 4));
        sounds.add(new Sound(game, "custom.effect.creepy", 1));
        sounds.add(new Sound(game, "custom.effect.helpme", 0));
        sounds.add(new Sound(game, "custom.effect.hurts", 0));
        sounds.add(new Sound(game, "custom.effect.intro", 0));
        sounds.add(new Sound(game, "custom.effect.laugh", 5));
        sounds.add(new Sound(game, "custom.effect.laughlong", 0));
        sounds.add(new Sound(game, "custom.effect.piano", 0));
        sounds.add(new Sound(game, "custom.effect.saveme", 0));
        sounds.add(new Sound(game, "custom.effect.scream", 1));
        sounds.add(new Sound(game, "custom.effect.static", 4));
        triggerPlaces = new HashMap<>();
        triggerPlaces.put(new TriggerPlace(
                        new Location(null, 2005, 16, 60),
                        new Location(null, 2014, 21, 72),
                        new Location(null, 2009, 17.5, 70)),
                getSound("piano"));
        triggerPlaces.put(new TriggerPlace(
                        new Location(null, 1989, 16, 83),
                        new Location(null, 2013, 21, 98)),
                getSound("chains"));
    }

    private final Set<Sound> sounds;
    private final Map<TriggerPlace, Sound> triggerPlaces;

    public Map<TriggerPlace, Sound> getTriggerPlaces() {
        return triggerPlaces;
    }

    public void playRandomAmbient(Player player) {
        Set<Sound> ambient = sounds.stream().filter(sound -> sound.getType().equals(SoundType.AMBIENT)).collect(Collectors.toSet());
        Sound sound = RandomUtils.getRandom(ambient);
        sound.play(player, null);
    }

    public void playEffect(Player player, @Nullable Location location, String name) {
        Sound sound = getSound(name);
        if (sound == null) return;
        sound.play(player, location);
    }

    public Sound getSound(String name) {
        return sounds.stream().filter(s -> s.getType().equals(SoundType.EFFECT) && s.getName().split("\\.")[2].equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public boolean isPlayingSound(Player player) {
        for (Sound s : sounds)
            if (s.isPlaying(player))
                return true;
        return false;
    }

}
