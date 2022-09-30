package me.pafias.ender.game;

import me.pafias.ender.Ender;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.CC;
import me.pafias.ender.util.RandomUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Torch {

    private final Ender plugin = Ender.get();

    private final EnderPlayer player;
    private final ItemStack offItem;
    private final ItemStack onItem;
    private ItemStack currentItem;
    private boolean on;
    private double power;
    private BukkitTask task;

    public Torch(EnderPlayer player) {
        this.player = player;
        on = false;
        offItem = new ItemStack(Material.STICK, 1);
        offItem.getItemMeta().setDisplayName(CC.t("&6Torch: &cOFF"));
        onItem = new ItemStack(Material.TORCH, 1);
        onItem.getItemMeta().setDisplayName(CC.t("&6Torch: &aON"));
        currentItem = offItem;
        power = 100;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
        if (on) {
            if (!isEmpty()) {
                currentItem = onItem;
                task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        drain();
                        player.getPlayer().sendActionBar(CC.tf("&6Torch power: &r%s", RandomUtils.getTorchPower(getPower())));
                        if (isEmpty()) setOn(false);
                    }
                }.runTaskTimerAsynchronously(plugin, 0, 10);
                player.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            }
        } else {
            task.cancel();
            currentItem = offItem;
            player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 0, false, false));
        }
        changeInventoryItem();
    }

    public void changeInventoryItem() {
        player.getPlayer().getInventory().setItem(4, getItem());
    }

    public ItemStack getItem() {
        return currentItem;
    }

    public double getPower() {
        return power;
    }

    public void drain() {
        if (power <= 0) return;
        power -= 1;
    }

    public boolean isEmpty() {
        return power <= 0;
    }

}
