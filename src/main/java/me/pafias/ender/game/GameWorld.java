package me.pafias.ender.game;

import me.pafias.ender.Ender;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

public class GameWorld {

    private final Ender plugin = Ender.get();

    private final String id;
    private final World originalWorld;
    private World gameWorld;

    public GameWorld(World world, String id) throws IOException {
        this.id = id;
        originalWorld = world;
        gameWorld = loadWorld(copyWorldFolder(), world);
    }

    public World getOriginalWorld() {
        return originalWorld;
    }

    public World getGameWorld() {
        return gameWorld;
    }

    public File copyWorldFolder() throws IOException {
        File worlddir = originalWorld.getWorldFolder();
        File copyto = new File(plugin.getServer().getWorldContainer() + "/world_" + id);
        FileUtils.copyDirectory(worlddir, copyto);
        return copyto;
    }

    public World loadWorld(File file, @Nullable World toCopy) {
        WorldCreator wc = new WorldCreator(file.getName());
        if (toCopy != null)
            wc.copy(toCopy);
        else {
            wc.environment(World.Environment.CUSTOM);
            wc.generator("3;minecraft:air;127;decoration;2;");
        }
        gameWorld = plugin.getServer().createWorld(wc);
        return gameWorld;
    }

    public void deleteGameWorld() throws IOException {
        FileUtils.deleteDirectory(gameWorld.getWorldFolder());
    }

}
