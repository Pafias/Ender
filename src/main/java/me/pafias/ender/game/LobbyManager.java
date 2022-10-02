package me.pafias.ender.game;

import me.pafias.ender.Ender;
import me.pafias.ender.util.CC;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class LobbyManager {

    private final Ender plugin = Ender.get();

    Set<BukkitTask> tasks = new HashSet<>();
    List<String> tips;
    String tip = "&e&lTIP: &r&aDon't be an asshole!";
    int i = 0;

    public LobbyManager(Game game) {
        tips = new ArrayList<>(plugin.getSM().getVariables().lobbyTips);
        i = new Random().nextInt(tips.size());
        tasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                if (game == null) return;
                if (!game.getState().equals(GameState.LOBBY)) {
                    tasks.forEach(BukkitTask::cancel);
                    return;
                }
                game.getPlayers().forEach(player -> player.getPlayer().sendActionBar(CC.t(tip)));
            }
        }.runTaskTimerAsynchronously(plugin, 3 * 20, 2 * 20));
        tasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                if (game == null) return;
                if (!game.getState().equals(GameState.LOBBY)) {
                    tasks.forEach(BukkitTask::cancel);
                    return;
                }
                tip = tips.get(i);
                if (i == (tips.size() - 1))
                    i = 0;
                else
                    i++;
            }
        }.runTaskTimerAsynchronously(plugin, 3 * 20, 5 * 20));
    }

}
