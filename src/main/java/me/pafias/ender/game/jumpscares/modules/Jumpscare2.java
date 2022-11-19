package me.pafias.ender.game.jumpscares.modules;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.jumpscares.Jumpscare;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class Jumpscare2 extends Jumpscare {

    public Jumpscare2(Ender plugin, Game game) {
        super(plugin, game);
    }

    @Override
    public void execute(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()).clone().add(new Random().nextDouble() + 3, 0, new Random().nextDouble() + 3);
                player.getWorld().strikeLightningEffect(loc);
                loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, Float.MAX_VALUE, 1F);
            }
        }.runTask(plugin);
    }

}
