package me.pafias.ender.utils;

import me.lucko.helper.Schedulers;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class Renderer extends MapRenderer {

    private BufferedImage image;

    public Renderer(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        Schedulers.async().run(() -> {
            try {
                canvas.drawImage(0, 0, image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
