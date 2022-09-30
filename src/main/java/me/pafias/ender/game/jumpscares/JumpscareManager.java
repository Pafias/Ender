package me.pafias.ender.game.jumpscares;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.jumpscares.modules.Jumpscare1;
import me.pafias.ender.util.RandomUtils;

import java.util.HashSet;
import java.util.Set;

public class JumpscareManager {

    private final Ender plugin;
    private final Game game;

    public JumpscareManager(Ender plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
        jumpscares.add(new Jumpscare1());
        // TODO add more jumpscares
    }

    private final Set<Jumpscare> jumpscares = new HashSet<>();

    public Set<Jumpscare> getJumpscares() {
        return jumpscares;
    }

    public Jumpscare getRandomJumpscare() {
        return RandomUtils.getRandom(jumpscares);
    }

}
