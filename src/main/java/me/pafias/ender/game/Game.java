package me.pafias.ender.game;

import me.lucko.helper.Schedulers;
import me.lucko.helper.random.RandomSelector;
import me.lucko.helper.scheduler.Task;
import me.pafias.ender.Ender;
import me.pafias.ender.User;
import me.pafias.ender.utils.CC;
import me.pafias.ender.utils.RandomUtils;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Game {

    private final Ender plugin = Ender.get();

    private boolean started = false;
    private final String name;
    private GameState gamestate;
    private final int minPlayers;
    private final int maxPlayers;
    private final Location lobby;
    private final World world;
    private final Set<User> everyone;
    private User ender;
    private int pages;
    private final int totalPages;
    private final Long gameduration;
    private Map<Location, BlockFace> pagesLocation;
    private final List<String> soundsAmbient;
    private final List<String> soundsEffect;

    private BossBar bossBar;

    private final Scoreboard gameScoreboard;
    private final Objective gameObjective;
    private final Scoreboard lobbyScoreboard;
    private final Objective lobbyObjective;

    private final Team enderTeam;
    private final Team playersTeam;

    private final Map<String, Task> tasks = new HashMap<>();

    public Game(String name) {
        this.name = name;
        gamestate = GameState.LOBBY;
        everyone = new HashSet<>();
        minPlayers = plugin.getSM().getVariables().minPlayers;
        maxPlayers = plugin.getSM().getVariables().maxPlayers;
        lobby = plugin.getSM().getVariables().lobby;
        world = plugin.getSM().getVariables().gameWorld;
        gameduration = plugin.getSM().getVariables().gameDuration;
        totalPages = plugin.getSM().getVariables().totalPages;
        soundsAmbient = plugin.getSM().getVariables().soundsAmbient;
        soundsEffect = plugin.getSM().getVariables().soundsEffect;

        gameScoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        gameObjective = gameScoreboard.registerNewObjective("ender", "dummy");
        gameObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        gameObjective.setDisplayName(CC.translate("&e&lEnder"));
        lobbyScoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        lobbyObjective = lobbyScoreboard.registerNewObjective("lobby", "dummy");
        lobbyObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        lobbyObjective.setDisplayName(CC.translate("&e&lEnder"));

        enderTeam = RandomUtils.registerTeam(gameScoreboard, "Ender", CC.translate("&bEnder"), CC.translate("&b"), ChatColor.AQUA);
        playersTeam = RandomUtils.registerTeam(gameScoreboard, "Players", CC.translate("&8Players"), CC.translate("&8"), ChatColor.DARK_GRAY);

        handleLobbyScoreboard();

        plugin.getSM().getGameManager().addGame(this);
    }

    int time;

    public void start(boolean force) {
        if (started) return;
        if (getPlayers().size() >= minPlayers || force) {
            started = true;
            time = 10;
            tasks.put("start", Schedulers.sync().runRepeating(() -> {
                if (time == 0) {
                    tasks.get("start").stop();
                    tasks.get("lobbyscoreboard").stop();
                    start2();
                    return;
                }
                for (User all : getPlayers()) {
                    all.getPlayer().setLevel(time);
                    all.getPlayer().setExp(time / (float) 30);
                }
                if (time == 10 || time == 5 || time == 4 || time == 3 || time == 2 || time == 1) {
                    for (User p : getPlayers()) {
                        p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                    }
                    broadcast(CC.translate("&9Ender starting in " + time + " seconds"));
                }
                time--;
            }, 2, 20));
        }
    }

    private void start2() {
        setGamestate(GameState.PREGAME);
        handleEnder();
        handleTeleport();
        handlePotionEffects();
        handleTeams();
        handleGameScoreboard();
        time = 10;
        tasks.put("start2", Schedulers.sync().runRepeating(() -> {
            if (time == 0) {
                tasks.get("start2").stop();
                setGamestate(GameState.INGAME);
                getPlayers().forEach(p -> {
                    if (p == ender) {
                        p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                        p.getPlayer().sendTitle(CC.translate("&6You are the &bEnder!"), CC.translate("&7Hunt the humans"));
                        /*
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title "
                                + p.getPlayer().getName()
                                + " title [\"\",{\"text\":\"You are the \",\"color\":\"gold\"},{\"text\":\"Ender!\",\"color\":\"aqua\",\"bold\":true}]");
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                "title " + p.getPlayer().getName()
                                        + " subtitle [\"\",{\"text\":\"Hunt the humans\",\"color\":\"gray\"}]");
                         */
                        p.getPlayer().sendMessage(ChatColor.GOLD + "You are the " + ChatColor.AQUA + "Ender!");
                        p.getPlayer().sendMessage(ChatColor.GOLD
                                + "Hunt down players and murder them with your scythe! Use the abilities in your hotbar to help you.");
                        p.getPlayer().setExp(0);
                        p.getPlayer().setLevel(0);
                    } else {
                        p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
                        p.getPlayer().sendTitle(CC.translate("&6You are a &bHuman!"), CC.translate("&7Don't look at him"));
                        /*
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title "
                                + p.getPlayer().getName()
                                + " title [\"\",{\"text\":\"You are a \",\"color\":\"gold\"},{\"text\":\"Human!\",\"color\":\"aqua\",\"bold\":true}]");
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title "
                                + p.getPlayer().getName()
                                + " subtitle [\"\",{\"text\":\"Don't look at him\",\"color\":\"gray\"}]");
                         */
                        p.getPlayer().sendMessage(CC.translate("&6You are a &b&lHuman!"));
                        p.getPlayer().sendMessage(CC.translate("&6Collect all 10 pages to win the game! Avoid looking into the eyes of the Ender!"));
                        p.getPlayer().setExp(0);
                        p.getPlayer().setLevel(0);
                    }
                });
                world.setTime(21000);
                world.setThundering(true);
                handleActionbar();
                handleGameTimer();
                handleJumpscares();
                handleFlashlight();
                handlePages();
                handleSounds();
                handleRaytracing();
                broadcast(CC.translate("&6Game started!"));
                return;
            }
            getPlayers().forEach(u -> {
                u.getPlayer().setLevel(time);
                u.getPlayer().setExp(time / (float) 10);
            });
            if (time == 10 || time == 5 || time == 4 || time == 3 || time == 2 || time == 1) {
                getPlayers().forEach(p -> {
                    if (p != ender)
                        p.getPlayer().sendMessage(ChatColor.RED + "The Ender will be released in " + ChatColor.GRAY
                                + time + ChatColor.RED + " seconds!");
                    else
                        p.getPlayer().sendMessage(ChatColor.RED + "You will be released in " + ChatColor.GRAY + time + ChatColor.RED + " seconds!");
                });
            }
            time--;
        }, 2, 20));
    }

    private void handleActionbar() {
        tasks.put("actionbar", Schedulers.async().runRepeating(() -> getPlayers().stream().filter(u -> u != ender).forEach(user -> {
            double required_progress = 100.0;
            double current_progress = user.getFlashlightbattery();
            double progress_percentage = current_progress / required_progress;
            StringBuilder sb = new StringBuilder();
            int bar_length = 50;
            for (int i = 0; i < bar_length; i++) {
                if (i < bar_length * progress_percentage) {
                    ChatColor color = current_progress < 25 ? ChatColor.RED : current_progress < 75 ? ChatColor.GOLD : ChatColor.GREEN;
                    sb.append(color + "|");
                } else {
                    sb.append(ChatColor.GRAY + "|");
                }
            }
            user.getPlayer().sendActionBar(CC.translate("&6Torch power: &a" + sb));
        }), 2, 10));
    }

    private void handleGameTimer() {
        AtomicReference<Long> duration = new AtomicReference<>(gameduration);
        bossBar = plugin.getServer().createBossBar(CC.translate("&5Time remaining: &a" + new SimpleDateFormat("mm:ss").format(new Date(TimeUnit.SECONDS.toMillis(duration.get())))), BarColor.PURPLE, BarStyle.SEGMENTED_20);
        getEveryone().forEach(user -> bossBar.addPlayer(user.getPlayer()));
        tasks.put("gametimer", Schedulers.sync().runRepeating(() -> {
            if (duration.get() <= 0) {
                tasks.get("gametimer").stop();
                endGame(GameEndReason.TIME_OVER);
                return;
            }
            bossBar.setProgress((double) (duration.get() / gameduration));
            bossBar.setTitle(CC.translate("&5Time remaining: &a" + new SimpleDateFormat("mm:ss").format(new Date(TimeUnit.SECONDS.toMillis(duration.get())))));
            duration.getAndSet(duration.get() - 1);
        }, 10, 20));
    }

    private void handleJumpscares() {
        tasks.put("jumpscares", Schedulers.sync().runRepeating(() -> {
            Random random = new Random();
            int number = random.nextInt(3);
            switch (number) {
                case 0:
                    jumpscare1();
                    break;
                case 1:
                    jumpscare2();
                    break;
                case 2:
                    jumpscare3();
                    break;
            }
        }, 30, TimeUnit.SECONDS, (new Random().nextInt(60 - 20) + 20), TimeUnit.SECONDS));
    }

    Map<User, Task> flashlight = new HashMap<>();

    private void handleFlashlight() {
        tasks.put("flashlight", Schedulers.sync().runRepeating(() -> getPlayers().stream().filter(u -> u != ender).forEach(user -> {
            if (user.getPlayer().getInventory().getItemInMainHand().getType() == Material.TORCH) {
                if (user.getFlashlightbattery() <= 0) {
                    if (flashlight.containsKey(user)) {
                        flashlight.get(user).stop();
                        flashlight.remove(user);
                        user.getPlayer().addPotionEffect(
                                new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 0, false, false));
                    }
                    return;
                }
                if (!flashlight.containsKey(user)) {
                    user.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
                    flashlight.put(user, Schedulers.async().runRepeating(user::drainFlashlight, 0, 20));
                }
            } else {
                if (flashlight.containsKey(user)) {
                    flashlight.get(user).stop();
                    flashlight.remove(user);
                    user.getPlayer().addPotionEffect(
                            new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 0, false, false));
                }
            }
        }), 2, 2));
    }

    private void handleTeleport() {
        getPlayers().stream().filter(u -> u != ender).forEach(user -> {
            user.getPlayer()
                    .teleport(plugin.getSM().getVariables().playersSpawnLocation);
            ItemStack flashlight = new ItemStack(Material.STICK, 1);
            ItemMeta meta = flashlight.getItemMeta();
            meta.setDisplayName(CC.translate("&6Torch &f- &cOFF"));
            flashlight.setItemMeta(meta);
            user.getPlayer().getInventory().setItem(4, flashlight);
            user.getPlayer().playSound(user.getPlayer().getEyeLocation(), "custom.effect.intro", 100000f, 1f);
        });
    }

    private void handlePotionEffects() {
        getPlayers().forEach(u -> {
            if (u != ender) {
                u.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 0, false, false));
            } else {
                u.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 0, false, false));
            }
            u.getPlayer().setFoodLevel(3);
        });
    }

    private void handleEnder() {
        if (ender == null)
            ender = RandomSelector.uniform(getPlayers()).pick();
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        LeatherArmorMeta Hmeta = (LeatherArmorMeta) helmet.getItemMeta();
        Hmeta.setColor(Color.BLACK);
        helmet.setItemMeta(Hmeta);
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
        ender.getPlayer().getInventory().setHelmet(helmet);
        ender.getPlayer().getInventory().setChestplate(chestplate);
        ender.getPlayer().getInventory().setLeggings(leggings);
        ender.getPlayer().getInventory().setBoots(boots);
        /*
        ItemStack scythe = new ItemStack(Material.IRON_HOE, 1);
        ItemMeta scytheMeta = scythe.getItemMeta();
        scytheMeta.setDisplayName(ChatColor.RED + "Scythe");
        scythe.setItemMeta(scytheMeta);
         */
        ItemStack freezetool = new ItemStack(Material.PACKED_ICE, 1);
        ItemMeta freezetoolMeta = freezetool.getItemMeta();
        freezetoolMeta.setDisplayName(ChatColor.GOLD + "Freeze player");
        freezetool.setItemMeta(freezetoolMeta);
        ItemStack nextplayer = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta nextplayerMeta = nextplayer.getItemMeta();
        nextplayerMeta.setDisplayName(ChatColor.GOLD + "Next player");
        nextplayer.setItemMeta(nextplayerMeta);
        // ender.getPlayer().getInventory().setItem(1, scythe);
        ender.getPlayer().getInventory().setItem(4, nextplayer);
        ender.getPlayer().getInventory().setItem(5, freezetool);
        double nX;
        double nZ;
        float nang = plugin.getSM().getVariables().playersSpawnLocation.getYaw();
        if (nang < 0)
            nang += 360;
        nX = Math.cos(Math.toRadians(nang));
        nZ = Math.sin(Math.toRadians(nang));
        Location loc = new Location(plugin.getSM().getVariables().playersSpawnLocation.getWorld(),
                plugin.getSM().getVariables().playersSpawnLocation.getX() - nX,
                plugin.getSM().getVariables().playersSpawnLocation.getY(),
                plugin.getSM().getVariables().playersSpawnLocation.getZ() - nZ,
                plugin.getSM().getVariables().playersSpawnLocation.getYaw() + 180,
                plugin.getSM().getVariables().playersSpawnLocation.getPitch());
        ender.getPlayer().teleport(loc);
    }

    private void handleTeams() {
        enderTeam.addPlayer(ender.getPlayer());
        getPlayers().stream().filter(u -> u != ender).forEach(u -> playersTeam.addPlayer(u.getPlayer()));
    }

    private void handlePages() {
        tasks.put("pages", Schedulers.sync().runRepeating(() -> {
            if (pagesLocation == null || pagesLocation.isEmpty()) {
                pagesLocation = new HashMap<>();
                for (String s : plugin.getSM().getVariables().pages) {
                    double x = Double.parseDouble(s.split(",")[0]);
                    double y = Double.parseDouble(s.split(",")[1]);
                    double z = Double.parseDouble(s.split(",")[2]);
                    BlockFace face = BlockFace.valueOf(s.split(",")[3]);
                    pagesLocation.put(new Location(world, x, y, z), face);
                }
            }
            /*
            pagesLocation.keySet().forEach(loc ->
                    world.getEntities().stream().filter(
                            e -> e instanceof ItemFrame && e.getLocation().getX() == loc.getX() && e.getLocation().getZ() == loc.getZ())
                            .forEach(Entity::remove));
             */
            for (int i = 0; i < 5; i++) {
                try {
                    Location loc = RandomUtils.getRandom(pagesLocation.keySet());
                    BlockFace face = pagesLocation.get(loc);
                    ItemFrame frame = world.spawn(loc, ItemFrame.class, itemFrame -> {
                        itemFrame.setFacingDirection(face, true);
                        itemFrame.setItem(new ItemStack(Material.FILLED_MAP));
                    });

                    Schedulers.sync().runLater(frame::remove, 15, TimeUnit.SECONDS);
                } catch (Exception ignored) {
                }
            }
        }, 5, TimeUnit.SECONDS, 16, TimeUnit.SECONDS));
    }

    private void handleSounds() {
        tasks.put("soundsAmbient", Schedulers.sync().runRepeating(() -> getPlayers().stream().filter(u -> u != ender).forEach(u -> u.getPlayer().playSound(u.getPlayer().getEyeLocation(), RandomUtils.getRandomAmbientSound(), 10000f, 1f)), 35, TimeUnit.SECONDS, 5, TimeUnit.SECONDS));
        tasks.put("soundsEffect", Schedulers.sync().runRepeating(() -> getPlayers().stream().filter(u -> u != ender).forEach(u -> u.getPlayer().playSound(u.getPlayer().getEyeLocation(), RandomUtils.getRandomEffectSound(soundsEffect), 100000f, 1f)), 1, TimeUnit.MINUTES, (new Random().nextInt(60 - 20) + 20), TimeUnit.SECONDS));
    }

    private void handleRaytracing() {
        tasks.put("raytracing", Schedulers.sync().runRepeating(() -> {
            /*
            RayTraceResult rtr = ender.getPlayer().rayTraceBlocks(4, FluidCollisionMode.SOURCE_ONLY);
            if (rtr == null) return;
            Entity entity = rtr.getHitEntity();
            if (entity == null || !(entity instanceof Player)) return;
            ((Player) entity).damage(1, ender.getPlayer());

             */
            ArrayList<Entity> entities = (ArrayList<Entity>) ender.getPlayer().getNearbyEntities(2, 1, 2);
            ArrayList<Block> sightBlock = (ArrayList<Block>) ender.getPlayer().getLineOfSight((Set<Material>) null, 3);
            ArrayList<Location> sight = new ArrayList<Location>();
            for (int i = 0; i < sightBlock.size(); i++)
                sight.add(sightBlock.get(i).getLocation());
            for (int i = 0; i < sight.size(); i++) {
                for (int k = 0; k < entities.size(); k++) {
                    if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
                        if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
                            if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
                                Entity entity = entities.get(k);
                                if ((entity instanceof Player))
                                    ((Player) entity).damage(1);
                            }
                        }
                    }
                }
            }
        }, 10, TimeUnit.SECONDS, 1, TimeUnit.SECONDS));
    }

    public void jumpscare1() {
        ItemStack skull = RandomUtils.getSkull(UUID.fromString("6ca92720-4304-43b7-ad4b-bb88dbc5668a"));
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

        getPlayers().stream().filter(u -> u != ender).forEach(u -> {
            Location loc = u.getPlayer().getEyeLocation();
            loc.setYaw(loc.getYaw() + 180);
            loc.add(loc.getDirection().setX(loc.getDirection().getX() + 5).setZ(loc.getDirection().getZ() + 5));
            ArmorStand as = (ArmorStand) world.spawnEntity(new Location(world,
                            ((u.getPlayer().getEyeLocation().getX() + loc.getX()) / 2), u.getPlayer().getLocation().getY(),
                            ((u.getPlayer().getEyeLocation().getZ() + loc.getZ()) / 2), loc.getYaw(), loc.getPitch()),
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
            as.setItemInHand(u.getPlayer().hasResourcePack() ? knife : new ItemStack(Material.IRON_AXE, 1));
            u.getPlayer().playSound(u.getPlayer().getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1F, 1F);
            Schedulers.sync().runLater(() -> {
                u.getPlayer().playSound(u.getPlayer().getEyeLocation(), "custom.effect.scream" + new Random().nextInt(2), 100000f, 1f);
                as.setVelocity(u.getPlayer().getLocation().getDirection().normalize().multiply(-1));
            }, 40);
            Schedulers.sync().runLater(as::remove, 100);
        });
    }

    public void jumpscare2() {
        getPlayers().stream().filter(u -> u != ender).forEach(u -> {
            world.strikeLightningEffect(
                    new Location(world, (u.getPlayer().getLocation().getX() + new Random().nextInt(4)),
                            u.getPlayer().getLocation().getY(),
                            (u.getPlayer().getLocation().getZ() + new Random().nextInt(4))));
            world.playSound(u.getPlayer().getEyeLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, Float.MAX_VALUE, 1F);
        });
    }

    public void jumpscare3() {
        getPlayers().stream().filter(u -> u != ender).forEach(u -> {
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(CraftParticle.toNMS(Particle.MOB_APPEARANCE), false,
                    0, 0, 0, 0, 0, 0, 0, 1);
            ((CraftPlayer) u.getPlayer()).getHandle().playerConnection.sendPacket(packet);
            u.getPlayer().playSound(u.getPlayer().getEyeLocation(), "custom.effect.scream0", 100000f, 1f);
        });
    }

    private void handleLobbyScoreboard() {
        Team sbPlayers = lobbyScoreboard.registerNewTeam("SB_players");
        sbPlayers.addEntry(ChatColor.GRAY + "");
        tasks.put("lobbyscoreboard", Schedulers.async().runRepeating(() -> {
            sbPlayers.setPrefix(CC.translate("&a" + getPlayers().size() + "/" + maxPlayers));
            lobbyObjective.getScore(CC.translate("&5Map:")).setScore(7);
            lobbyObjective.getScore(CC.translate("&aMansion")).setScore(6);
            lobbyObjective.getScore(" ").setScore(5);
            lobbyObjective.getScore(CC.translate("&5Players:")).setScore(4);
            lobbyObjective.getScore(ChatColor.GRAY + "").setScore(3);
            lobbyObjective.getScore("").setScore(2);
            lobbyObjective.getScore(CC.translate("&6not cubecraft")).setScore(1);
            getEveryone().forEach(p -> Schedulers.async().run(() -> p.getPlayer().setScoreboard(lobbyScoreboard)));
        }, 2, TimeUnit.MILLISECONDS, 1, TimeUnit.SECONDS));
    }

    private void handleGameScoreboard() {
        Team sbPages = gameScoreboard.registerNewTeam("SB_pages");
        sbPages.addEntry(ChatColor.WHITE + "");
        Team sbHumans = gameScoreboard.registerNewTeam("SB_humans");
        sbHumans.addEntry(ChatColor.BLUE + "");
        tasks.put("gamescoreboard", Schedulers.async().runRepeating(() -> {
            sbPages.setPrefix(CC.translate("&a" + pages + "/" + totalPages));
            sbHumans.setPrefix(CC.translate("&a" + getPlayers().size()));
            gameObjective.getScore(CC.translate("&5Pages:")).setScore(7);
            gameObjective.getScore(ChatColor.WHITE + "").setScore(6);
            gameObjective.getScore(" ").setScore(5);
            gameObjective.getScore(CC.translate("&5Humans:")).setScore(4);
            gameObjective.getScore(ChatColor.BLUE + "").setScore(3);
            gameObjective.getScore("").setScore(2);
            gameObjective.getScore(CC.translate("&6not cubecraft")).setScore(1);
            everyone.forEach(user -> Schedulers.async().run(() -> user.getPlayer().setScoreboard(gameScoreboard)));
        }, 2, TimeUnit.MILLISECONDS, 1, TimeUnit.SECONDS));
    }

    public void stop() {
        setGamestate(GameState.RESETTING);
        enderTeam.unregister();
        playersTeam.unregister();
        bossBar.removeAll();
        bossBar.hide();
        getEveryone().forEach(user -> {
            user.getPlayer().getInventory().clear();
            user.getPlayer().setExp(0);
            user.getPlayer().setLevel(0);
            for (PotionEffect pe : user.getPlayer().getActivePotionEffects()) {
                user.getPlayer().removePotionEffect(pe.getType());
            }
            user.getPlayer().setGameMode(GameMode.SURVIVAL);
            user.getPlayer().setFoodLevel(20);
            user.getPlayer().setHealth(user.getPlayer().getMaxHealth());
            user.getPlayer().teleport(plugin.getSM().getVariables().hub);
            user.getPlayer().setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());
            user.getPlayer().setPlayerListName(ChatColor.RESET + user.getPlayer().getDisplayName());
            user.getPlayer().getInventory().clear();
            user.getPlayer().getActivePotionEffects().forEach(pe -> user.getPlayer().removePotionEffect(pe.getType()));
        });
        world.getEntities().clear();
        tasks.values().forEach(Task::stop);
        plugin.getSM().getGameManager().removeGame(this);
    }

    public void endGame(GameEndReason reason) {
        setGamestate(GameState.POSTGAME);
        tasks.get("gametimer").stop();
        switch (reason) {
            case ALL_PAGES_FOUND:
                broadcast(ChatColor.GOLD + "All pages have been found!");
                broadcast(ChatColor.GOLD + "The " + ChatColor.AQUA + ChatColor.BOLD + "Humans" + ChatColor.RESET
                        + ChatColor.GOLD + " have won the game!");
                Schedulers.sync().runLater(this::stop, 5, TimeUnit.SECONDS);
                break;
            case ALL_PLAYERS_DEAD:
                broadcast(CC.translate("&7-----------------------------------"));
                broadcast(CC.translate("&eThe Ender &awon the game!"));
                broadcast(CC.translate("&7All players were eliminated"));
                broadcast(CC.translate("&7-----------------------------------"));
                Schedulers.sync().runLater(this::stop, 5, TimeUnit.SECONDS);
                break;
            case TIME_OVER:
                broadcast(ChatColor.GOLD + "The time is up!");
                broadcast(ChatColor.RED + "" + ChatColor.BOLD + "Nobody" + ChatColor.RESET + ChatColor.GOLD
                        + " has won the game.");
                Schedulers.sync().runLater(this::stop, 5, TimeUnit.SECONDS);
                break;
        }
    }

    public void broadcast(String message) {
        everyone.forEach(u -> u.getPlayer().sendMessage(message));
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public Set<User> getPlayers() {
        return everyone.stream().filter(u -> !u.getPlayer().getGameMode().equals(GameMode.SPECTATOR)).collect(Collectors.toSet());
    }

    public void addPlayer(User user) {
        everyone.add(user);
    }

    public void removePlayer(User user) {
        bossBar.removePlayer(user.getPlayer());
        everyone.remove(user);
    }

    public Set<User> getEveryone() {
        return everyone;
    }

    public User getEnder() {
        return ender;
    }

    public void setEnder(User ender) {
        this.ender = ender;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public GameState getGamestate() {
        return gamestate;
    }

    public void setGamestate(GameState gamestate) {
        this.gamestate = gamestate;
    }

    public Location getLobby() {
        return lobby;
    }

    public void setSpectator(User user) {
        user.getPlayer().setGameMode(GameMode.SPECTATOR);
    }

    public int getPagesFound() {
        return pages;
    }

    public void addPage() {
        this.pages += 1;
    }

    public int getTotalPages() {
        return totalPages;
    }

}
