package me.pafias.ender.util;

import me.pafias.ender.Ender;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RandomUtils {

    private static final Ender plugin = Ender.get();

    public static String getTorchPower(double power) {
        double required_progress = 100.0;
        double progress_percentage = power / required_progress;
        StringBuilder sb = new StringBuilder();
        int bar_length = 100;
        for (int i = 0; i < bar_length; i++) {
            if (i < bar_length * progress_percentage) {
                ChatColor color = power < 25 ? ChatColor.RED : power < 75 ? ChatColor.GOLD : ChatColor.GREEN;
                sb.append(color + "|");
            } else {
                sb.append(ChatColor.GRAY + "|");
            }
        }
        return sb.toString();
    }

    public static <E> E getRandom(Set<E> set) {
        List<E> list = new ArrayList<>(set);
        return list.get(new Random().nextInt(list.size()));
    }

    public static CompletableFuture<ItemStack[]> getEnderOutfit() {
        CompletableFuture<ItemStack[]> future = new CompletableFuture<>();

        ItemStack[] is = new ItemStack[4];

        getSkull(UUID.fromString("9d804032-98c2-4af0-96d7-b1c0f306362f")).thenAccept(skull -> {
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setDisplayName("");
            skullMeta.setLore(Collections.emptyList());
            skull.setItemMeta(skullMeta);

            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
            LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
            chestplateMeta.setColor(Color.BLACK);
            chestplate.setItemMeta(chestplateMeta);

            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
            LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
            leggingsMeta.setColor(Color.BLACK);
            leggings.setItemMeta(leggingsMeta);

            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
            bootsMeta.setColor(Color.BLACK);
            boots.setItemMeta(bootsMeta);

            is[3] = skull;
            is[2] = chestplate;
            is[1] = leggings;
            is[0] = boots;

            future.complete(is);
        });

        return future;
    }

    public static ItemStack[] getEnderTools() {
        ItemStack[] is = new ItemStack[35];

        ItemStack np = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta npMeta = np.getItemMeta();
        npMeta.setDisplayName(CC.t("&bTeleport!"));
        npMeta.setLore(Arrays.asList(CC.t("&7Teleport near to a random player.")));
        np.setItemMeta(npMeta);

        ItemStack ft = new ItemStack(Material.PACKED_ICE, 1);
        ItemMeta ftMeta = ft.getItemMeta();
        ftMeta.setDisplayName(CC.t("&bFreeze!"));
        ftMeta.setLore(Arrays.asList(CC.t("&7Freeze players near to you for 3 seconds.")));
        ft.setItemMeta(ftMeta);

        is[3] = np;
        is[4] = ft;

        return is;
    }

    public static CompletableFuture<ItemStack> getSkull(UUID player) {
        CompletableFuture<ItemStack> future = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack is = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) is.getItemMeta();
                meta.setOwningPlayer(plugin.getServer().getOfflinePlayer(player));
                is.setItemMeta(meta);
                future.complete(is);
            }
        }.runTaskAsynchronously(plugin);
        return future;
    }

}
