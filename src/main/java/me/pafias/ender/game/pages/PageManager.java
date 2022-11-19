package me.pafias.ender.game.pages;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PageManager {

    private final Ender plugin;

    public PageManager(Ender plugin, Game game) {
        this.plugin = plugin;
        total = plugin.getSM().getVariables().pages;
        found = 0;
        plugin.getSM().getVariables().pageLocations.forEach(loc -> {
            double x = Double.parseDouble(loc.split(",")[0]);
            double y = Double.parseDouble(loc.split(",")[1]);
            double z = Double.parseDouble(loc.split(",")[2]);
            float yaw = Float.parseFloat(loc.split(",")[3]);
            float pitch = Float.parseFloat(loc.split(",")[4]);
            BlockFace face = BlockFace.valueOf(loc.split(",")[5]);
            Location location = new Location(game.getWorld().getGameWorld(), x, y, z, yaw, pitch);
            location.getWorld().getBlockAt(location).setType(Material.AIR);

            ItemStack map = new ItemStack(Material.FILLED_MAP, 1);
            ItemFrame frame = game.getWorld().getGameWorld().spawn(location, ItemFrame.class, f -> {
                f.setFacingDirection(face, true);
            });
            new BukkitRunnable() {
                @Override
                public void run() {
                    frame.setItem(map, false);
                    frame.setItem(new ItemStack(Material.AIR), false);
                    frame.setVisible(false);
                    frames.put(frame, map);
                }
            }.runTaskLater(plugin, 10);
        });
    }

    private Map<ItemFrame, ItemStack> frames = new HashMap<>();

    public Map<ItemFrame, ItemStack> getItemFrames() {
        return frames;
    }

    public ItemFrame getRandomFrame() {
        return RandomUtils.getRandom(frames.keySet());
    }

    public File getRandomPage() {
        File dir = new File(plugin.getDataFolder() + "/pages/");
        String pageName = String.format("page%d.png", new Random().nextInt(8));
        File page = new File(dir, pageName);
        return page;
    }


    private int found;
    private final int total;

    public int getPagesFound() {
        return found;
    }

    public int getTotalPages() {
        return total;
    }

    public void addPage() {
        found += 1;
    }

}
