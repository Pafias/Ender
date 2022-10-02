package me.pafias.ender.listeners;

import me.pafias.ender.Ender;
import me.pafias.ender.commands.subcommands.AddpagelocationCommand;
import me.pafias.ender.util.CC;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SetupListener implements Listener {

    private final Ender plugin;

    public SetupListener(Ender plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightclick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (AddpagelocationCommand.selecting.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            BlockFace face = event.getBlockFace();
            double x = event.getClickedBlock().getRelative(face).getLocation().getX();
            double y = event.getClickedBlock().getRelative(face).getLocation().getY();
            double z = event.getClickedBlock().getRelative(face).getLocation().getZ();
            float yaw = event.getClickedBlock().getRelative(face).getLocation().getYaw();
            float pitch = event.getClickedBlock().getRelative(face).getLocation().getPitch();
            String s = x + "," + y + "," + z + "," + yaw + "," + pitch + "," + face.name();
            List<String> locations = plugin.getConfig().getStringList("page_locations");
            if (locations.contains(s)) {
                event.getPlayer().sendMessage(CC.t("&cLocation already exists!"));
                return;
            }
            locations.add(s);
            event.getPlayer().sendMessage(CC.t("&aLocation set!"));
            plugin.getConfig().set("page_locations", locations);
            plugin.saveConfig();
        }
    }

}
