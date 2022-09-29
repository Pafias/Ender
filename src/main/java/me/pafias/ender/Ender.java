package me.pafias.ender;

import me.pafias.ender.commands.EnderCommand;
import me.pafias.ender.listeners.JoinQuitListener;
import me.pafias.ender.services.ServicesManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ender extends JavaPlugin {

    private static Ender plugin;

    public static Ender get() {
        return plugin;
    }

    private ServicesManager servicesManager;

    public ServicesManager getSM(){
        return servicesManager;
    }

    @Override
    public void onEnable() {
        plugin = this;
        servicesManager = new ServicesManager(plugin);
        getServer().getOnlinePlayers().forEach(p -> servicesManager.getPlayerManager().addPlayer(p));
        register();
    }

    @Override
    public void onDisable() {
        plugin = null;
        getServer().getScheduler().cancelTasks(plugin);
        getServer().getOnlinePlayers().forEach(p -> servicesManager.getPlayerManager().removePlayer(p));
    }

    private void register(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitListener(plugin), plugin);

        getCommand("ender").setExecutor(new EnderCommand(plugin));
    }

}
