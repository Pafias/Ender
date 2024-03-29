package me.pafias.ender.game;

import me.pafias.ender.Ender;
import org.apache.commons.io.FileUtils;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class GameWorld {

    private final Ender plugin = Ender.get();

    private final String id;
    private final World originalWorld;
    private World gameWorld;

    public GameWorld(Location original, String id) {
        this.id = id;
        originalWorld = original.getWorld();
        loadWorld(copyWorldFolder(), original.getWorld()).thenAccept(w -> {
            gameWorld = w;
            gameWorld.setGameRule(GameRule.DO_FIRE_TICK, false);
            gameWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            gameWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            gameWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            gameWorld.setAutoSave(false);
            gameWorld.setTime(14000);
            gameWorld.setThundering(true);
            gameWorld.setThunderDuration(35 * 60 * 20);
            gameWorld.setSpawnFlags(false, false);
            gameWorld.setSpawnLocation((int) original.getX(), (int) original.getY(), (int) original.getZ());
        });
    }

    public World getOriginalWorld() {
        return originalWorld;
    }

    public World getGameWorld() {
        return gameWorld;
    }

    public File copyWorldFolder() {
        File worlddir = originalWorld.getWorldFolder();
        File copyto = new File(plugin.getServer().getWorldContainer() + "/" + originalWorld.getName() + "_" + id);
        try {
            FileUtils.copyDirectory(worlddir, copyto);
        } catch (IOException ignored) {
        }
        return copyto;
    }

    public CompletableFuture<World> loadWorld(File file, @Nullable World toCopy) {
        CompletableFuture<World> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                WorldCreator wc = new WorldCreator(file.getName());
                if (toCopy != null)
                    wc.copy(toCopy);
                else {
                    wc.environment(World.Environment.CUSTOM);
                    wc.generatorSettings("3;minecraft:air;127;decoration;2;");
                }
                gameWorld = plugin.getServer().createWorld(wc);
                future.complete(gameWorld);
            }
        }.runTask(plugin);
        return future;
    }

    public void deleteGameWorld() {
        CompletableFuture.runAsync(() -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getServer().unloadWorld(gameWorld, false);
                }
            }.runTask(plugin);
        }).thenRun(() -> {
            try {
                FileUtils.deleteDirectory(gameWorld.getWorldFolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
