package me.pafias.ender;

import org.bukkit.scheduler.BukkitRunnable;

public class Variables {

    private final Ender plugin;

    public Variables(Ender plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.reloadConfig();
                reloadConfigYML();
            }
        }.runTaskAsynchronously(plugin);
    }



    private void reloadConfigYML() {

    }

}
