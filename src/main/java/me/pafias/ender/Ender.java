package me.pafias.ender;

import me.lucko.helper.Schedulers;
import me.pafias.ender.commands.EnderCommand;
import me.pafias.ender.game.Game;
import me.pafias.ender.listeners.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ender extends JavaPlugin {

    private static Ender plugin;

    public static Ender get() {
        return plugin;
    }

    private ServicesManager servicesManager;

    public ServicesManager getSM() {
        return servicesManager;
    }

    public void onEnable() {
        plugin = this;
        servicesManager = new ServicesManager(plugin);
        getServer().getOnlinePlayers().forEach(p -> servicesManager.getUserManager().addUser(p));

        registerCommands();
        registerListeners();
    }

    public void onDisable() {
        servicesManager.getGameManager().getGames().forEach(Game::stop);
        Schedulers.bukkit().cancelTasks(plugin);
        plugin = null;
    }

    private void registerCommands() {
        this.getCommand("ender").setExecutor(new EnderCommand(plugin));
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinAndQuitListener(plugin), this);
        pm.registerEvents(new GameListener(plugin), this);
        pm.registerEvents(new EventsListener(plugin), this);
        pm.registerEvents(new EnderListener(plugin), this);
        pm.registerEvents(new SetupListener(plugin), this);
    }

}
