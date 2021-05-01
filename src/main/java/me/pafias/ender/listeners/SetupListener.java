package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class SetupListener implements Listener {

    private final Ender plugin;

    public SetupListener(Ender plugin) {
        this.plugin = plugin;
    }

    public static List<String> setupPages = new ArrayList<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (setupPages.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
            BlockFace face = event.getBlockFace();
            double x = event.getClickedBlock().getRelative(face).getLocation().getX();
            double y = event.getClickedBlock().getRelative(face).getLocation().getY();
            double z = event.getClickedBlock().getRelative(face).getLocation().getZ();
            List<String> list = plugin.getSM().getVariables().pages;
            list.add(x + "," + y + "," + z + "," + face.name());
            plugin.getSM().getVariables().pages = list;
            plugin.getConfig().set("pages", list);
            plugin.saveConfig();
            event.getPlayer().sendMessage(ChatColor.GREEN + "Page location set!");
            setupPages.remove(event.getPlayer().getName());
        }
    }

}
