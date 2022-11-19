package me.pafias.ender.game.jumpscares.modules;

import me.pafias.ender.Ender;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.jumpscares.Jumpscare;
import me.pafias.ender.util.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Jumpscare1 extends Jumpscare {

    public Jumpscare1(Ender plugin, Game game) {
        super(plugin, game);
    }

    @Override
    public void execute(Player player) {
        RandomUtils.getSkull("vennos93").thenAccept(skull -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                    LeatherArmorMeta Cmeta = (LeatherArmorMeta) chestplate.getItemMeta();
                    Cmeta.setColor(Color.BLACK);
                    chestplate.setItemMeta(Cmeta);
                    ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                    LeatherArmorMeta Lmeta = (LeatherArmorMeta) leggings.getItemMeta();
                    Lmeta.setColor(Color.BLACK);
                    leggings.setItemMeta(Lmeta);
                    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                    LeatherArmorMeta Bmeta = (LeatherArmorMeta) boots.getItemMeta();
                    Bmeta.setColor(Color.BLACK);
                    boots.setItemMeta(Bmeta);
                    ItemStack knife = new ItemStack(Material.MUSIC_DISC_WAIT, 1);

                    Location loc = player.getEyeLocation();
                    loc.setYaw(loc.getYaw() + 180);
                    loc.add(loc.getDirection().setX(loc.getDirection().getX() + 5).setZ(loc.getDirection().getZ() + 5));
                    ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(),
                                    ((player.getEyeLocation().getX() + loc.getX()) / 2), player.getLocation().getY(),
                                    ((player.getEyeLocation().getZ() + loc.getZ()) / 2), loc.getYaw(), loc.getPitch()),
                            EntityType.ARMOR_STAND);
                    as.setArms(true);
                    as.setBasePlate(false);
                    as.setGravity(true);
                    as.setInvulnerable(true);
                    as.setAI(true);
                    as.setHelmet(skull);
                    as.setChestplate(chestplate);
                    as.setLeggings(leggings);
                    as.setBoots(boots);
                    as.setItemInHand(player.hasResourcePack() ? knife : new ItemStack(Material.IRON_AXE, 1));
                    player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1F, 1F);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            game.getSoundManager().playEffect(player, as.getEyeLocation(), "scream");
                            // player.playSound(player.getEyeLocation(), "custom.effect.scream" + new Random().nextInt(2), Float.MAX_VALUE, 1f);
                            as.setVelocity(player.getLocation().getDirection().normalize().multiply(-1));
                        }
                    }.runTaskLater(Ender.get(), 40);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            as.remove();
                        }
                    }.runTaskLater(Ender.get(), 100);
                }
            }.runTask(plugin);
        });
    }

}
