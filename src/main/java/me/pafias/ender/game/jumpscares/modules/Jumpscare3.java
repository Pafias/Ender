package me.pafias.ender.game.jumpscares.modules;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.jumpscares.Jumpscare;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Jumpscare3 extends Jumpscare {

    public Jumpscare3(Ender plugin, Game game) {
        super(plugin, game);
    }

    @Override
    public void execute(Player player) {
        game.getSoundManager().playEffect(player, null, "scream");
        player.spawnParticle(Particle.MOB_APPEARANCE, player.getEyeLocation(), 1);
    }

}
