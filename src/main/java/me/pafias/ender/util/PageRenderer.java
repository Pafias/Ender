package me.pafias.ender.util;

import me.pafias.ender.Ender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class PageRenderer extends MapRenderer {

    private final Ender plugin;

    private final BufferedImage image;

    public PageRenderer(BufferedImage image) {
        plugin = Ender.get();
        this.image = image;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    canvas.drawImage(0, 0, image);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }

}
