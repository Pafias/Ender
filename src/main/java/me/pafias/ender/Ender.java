package me.pafias.ender;

import me.pafias.ender.commands.EnderCommand;
import me.pafias.ender.listeners.*;
import me.pafias.ender.services.ServicesManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
        getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(plugin);
        servicesManager.getGameManager().getGame().stop();
        getServer().getOnlinePlayers().forEach(p -> sendToServer(p, servicesManager.getVariables().hubServer));
        plugin = null;
    }

    private void register() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitListener(plugin), plugin);
        pm.registerEvents(new ProtectionListener(plugin), plugin);
        pm.registerEvents(new EnderListener(plugin), plugin);
        pm.registerEvents(new GameListener(plugin), plugin);
        pm.registerEvents(new HumansListener(plugin), plugin);
        pm.registerEvents(new SetupListener(plugin), plugin);

        getCommand("ender").setExecutor(new EnderCommand(plugin));
    }

    public void sendToServer(Player player, String server) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    out.writeUTF("Connect");
                    out.writeUTF(server);
                } catch (IOException eee) {
                    eee.printStackTrace();
                }
                player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
            }
        }.runTask(plugin);
    }

}
