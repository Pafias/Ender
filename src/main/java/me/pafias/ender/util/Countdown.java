package me.pafias.ender.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

/**
 * A simple countdown timer using the Runnable interface in seconds!
 * <b>Great for minigames and other shiz?</b>
 * <p>
 * Project created by
 *
 * @author ExpDev
 */
public class Countdown extends BukkitRunnable {

    private JavaPlugin plugin;

    private BukkitTask assignedTask;

    private float seconds;
    private float secondsLeft;

    private Consumer<Countdown> everySecond;
    private Runnable beforeTimer;
    private Runnable afterTimer;

    public Countdown(JavaPlugin plugin, float seconds, Runnable beforeTimer, Runnable afterTimer, Consumer<Countdown> everySecond) {
        this.plugin = plugin;

        this.seconds = seconds;
        this.secondsLeft = seconds;

        this.beforeTimer = beforeTimer;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    /**
     * Runs the timer once, decrements seconds etc...
     * Really wish we could make it protected/private so you couldn't access it
     */
    @Override
    public void run() {
        if (secondsLeft < 1) {
            afterTimer.run();
            if (assignedTask != null) assignedTask.cancel();
            return;
        }
        if (secondsLeft == seconds) beforeTimer.run();
        everySecond.accept(this);
        secondsLeft--;
    }

    /**
     * Gets the total seconds this timer was set to run for
     *
     * @return Total seconds timer should run
     */
    public float getTotalSeconds() {
        return seconds;
    }

    /**
     * Gets the seconds left this timer should run
     *
     * @return Seconds left timer should run
     */
    public float getSecondsLeft() {
        return secondsLeft;
    }

    /**
     * Schedules this instance to "run" every second
     */
    public Countdown scheduleTimer() {
        assignedTask = runTaskTimer(plugin, 2, 20);
        return this;
    }

}