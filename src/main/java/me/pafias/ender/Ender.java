package me.pafias.ender;

import me.pafias.ender.commands.EnderCommand;
import me.pafias.ender.game.Game;
import me.pafias.ender.listeners.*;
import me.pafias.ender.services.ServicesManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class Ender extends JavaPlugin {

    private static Ender plugin;

    public static Ender get() {
        return plugin;
    }

    private ServicesManager servicesManager;

    public ServicesManager getSM() {
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
        getServer().getScheduler().cancelTasks(plugin);
        Set<Game> games = new HashSet<>(servicesManager.getGameManager().getGames());
        games.forEach(Game::stop);
        getServer().getOnlinePlayers().forEach(p -> servicesManager.getPlayerManager().removePlayer(p));
        plugin = null;
    }

    private void register() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitListener(plugin), plugin);
        pm.registerEvents(new NPCListener(plugin), plugin);
        pm.registerEvents(new ProtectionListener(plugin), plugin);
        pm.registerEvents(new EnderListener(plugin), plugin);
        pm.registerEvents(new GameListener(plugin), plugin);
        pm.registerEvents(new HumansListener(plugin), plugin);
        pm.registerEvents(new SetupListener(plugin), plugin);

        getCommand("ender").setExecutor(new EnderCommand(plugin));
    }

}
