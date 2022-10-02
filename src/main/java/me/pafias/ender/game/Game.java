package me.pafias.ender.game;

import me.pafias.ender.Ender;
import me.pafias.ender.game.jumpscares.Jumpscare;
import me.pafias.ender.game.jumpscares.JumpscareManager;
import me.pafias.ender.game.pages.PageManager;
import me.pafias.ender.game.sounds.SoundManager;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.CC;
import me.pafias.ender.util.Countdown;
import me.pafias.ender.util.RandomUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Game {

    private final Ender plugin = Ender.get();

    private final UUID uuid;
    private GameWorld lobby;
    private GameWorld world;
    private GameState state;
    private EnderPlayer ender;
    private Set<EnderPlayer> players;
    private int maxPlayers;
    private int gameDuration;
    private Map<String, Countdown> countdownTasks;
    private Map<String, BukkitTask> repeatingTasks;
    private Scoreboard gameScoreboard;
    private Team enderTeam, humansTeam;
    private KeyedBossBar bossBar;
    private PageManager pageManager;
    private JumpscareManager jumpscareManager;
    private SoundManager soundManager;
    private LobbyManager lobbyManager;

    public Game() throws IOException {
        uuid = UUID.randomUUID();
        lobby = new GameWorld(plugin.getSM().getVariables().gameLobby, uuid.toString().split("-")[0]);
        lobbyManager = new LobbyManager(this);
        world = new GameWorld(plugin.getSM().getVariables().gameSpawn, uuid.toString().split("-")[0]);
        state = GameState.LOBBY;
        players = new HashSet<>();
        maxPlayers = plugin.getSM().getVariables().maxPlayers;
        gameDuration = plugin.getSM().getVariables().gameDuration;
        jumpscareManager = new JumpscareManager(plugin, this);
        soundManager = new SoundManager(plugin, this);
        countdownTasks = new HashMap<>();
        repeatingTasks = new HashMap<>();
    }

    public void start() {
        setGameState(GameState.INGAME);
        pageManager = new PageManager(plugin, this);
        handleEnder();
        getPlayers().forEach(pp -> {
            pp.getPlayer().setLevel(0);
            pp.getPlayer().setExp(0);
            pp.getPlayer().setFoodLevel(5);
            handleTeams();
            handleGamescoreboard();
            handleBossbar();
            handleJumpscares();
            handleSounds();
            handlePages();
            handleDamage();
        });
        getHumans().forEach(p -> {
            p.getPlayer().teleport(world.getGameWorld().getSpawnLocation().clone().add(new Random().nextDouble(), 0, new Random().nextDouble()));
            p.setEnder(false);
            p.getPlayer().sendTitle(CC.t("&6You are a &b&lHuman!"), CC.t("&7Don't look at him"));
            p.getPlayer().sendMessage(CC.t("&6You are a &b&lHuman!"));
            p.getPlayer().sendMessage(CC.tf("&6Collect all %d pages to win the game! Avoid looking into the eyes of the Ender!", pageManager.getTotalPages()));
            handleTorch(p);
            p.getPlayer().playSound(p.getPlayer().getEyeLocation(), "custom.effect.intro", 1f, 1f);
        });
        ender.getPlayer().sendTitle(CC.t("&6You are the &c&lEnder!"), CC.t("&7Hunt the humans"));
        ender.getPlayer().sendMessage(CC.t("&6You are the &c&lEnder!"));
        ender.getPlayer().sendMessage(CC.t("&6Hunt down humans and stare into their eyes! Use your abilities in your hotbar to help you."));
        getCountdownTasks().put("enderrelease", new Countdown(plugin, 10, () -> {
        }, () -> {
            ender.setFrozen(false);
            ender.getPlayer().setExp(0);
            ender.getPlayer().setLevel(0);
        }, (t) -> {
            boolean sound = false;
            if (t.getSecondsLeft() == 10 || t.getSecondsLeft() <= 5) {
                ender.getPlayer().sendMessage(CC.tf("&6You will be released in &b%d seconds", (int) t.getSecondsLeft()));
                sound = true;
            }
            boolean finalSound = sound;
            getPlayers().forEach(pp -> {
                pp.getPlayer().setLevel((int) t.getSecondsLeft());
                pp.getPlayer().setExp(t.getSecondsLeft() / t.getTotalSeconds());
                if (finalSound)
                    pp.getPlayer().playSound(pp.getPlayer().getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f, 1f);
            });
        }).scheduleTimer());
        new BukkitRunnable() {
            @Override
            public void run() {
                lobby.deleteGameWorld();
            }
        }.runTaskLater(plugin, 40);
    }

    private void handleEnder() {
        if (ender == null) ender = RandomUtils.getRandom(getPlayers());
        ender.setEnder(true);
        ender.setFrozen(true);
        RandomUtils.getEnderOutfit().thenAccept(outfit -> ender.getPlayer().getInventory().setArmorContents(outfit));
        ender.getPlayer().getInventory().setStorageContents(RandomUtils.getEnderTools());
        double nX, nZ;
        float nAng = world.getGameWorld().getSpawnLocation().getYaw();
        if (nAng < 0) nAng += 360;
        nX = Math.cos(Math.toRadians(nAng));
        nZ = Math.sin(Math.toRadians(nAng));
        Location loc = world.getGameWorld().getSpawnLocation().clone();
        loc.setX(loc.getX() - nX);
        loc.setZ(loc.getZ() - nZ);
        loc.setYaw(loc.getYaw() + 180);
        ender.getPlayer().teleport(loc);
        ender.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 11 * 20, 3, false, false));
    }

    private void handleTorch(EnderPlayer p) {
        p.setTorch(new Torch(p));
        p.getTorch().setOn(false);
        p.getTorch().changeInventoryItem();
    }

    private void handleTeams() {
        gameScoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        enderTeam = gameScoreboard.registerNewTeam("ender");
        enderTeam.setDisplayName(CC.t("&c"));
        enderTeam.setPrefix(CC.t("&c"));
        enderTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        enderTeam.addPlayer(ender.getPlayer());
        humansTeam = gameScoreboard.registerNewTeam("humans");
        humansTeam.setDisplayName(CC.t("&b"));
        humansTeam.setPrefix(CC.t("&b"));
        humansTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        getHumans().forEach(human -> humansTeam.addPlayer(human.getPlayer()));
    }

    private void handleGamescoreboard() {
        Team sbPages = gameScoreboard.registerNewTeam("SB_pages");
        sbPages.addEntry(ChatColor.WHITE + "");
        Team sbHumans = gameScoreboard.registerNewTeam("SB_humans");
        sbHumans.addEntry(ChatColor.BLUE + "");
        Objective objective = gameScoreboard.registerNewObjective("ender", "dummy");
        objective.setDisplayName(CC.t("&e&lEnder"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        repeatingTasks.put("gamescoreboard", new BukkitRunnable() {
            @Override
            public void run() {
                sbPages.setPrefix(CC.tf("&a%d/%d", pageManager.getPagesFound(), pageManager.getTotalPages()));
                sbHumans.setPrefix(CC.tf("&a%d", getLivingHumans().size()));
                objective.getScore(CC.t("&5Pages:")).setScore(7);
                objective.getScore(ChatColor.WHITE + "").setScore(6);
                objective.getScore(" ").setScore(5);
                objective.getScore(CC.t("&5Humans:")).setScore(4);
                objective.getScore(ChatColor.BLUE + "").setScore(3);
                objective.getScore("").setScore(2);
                objective.getScore(CC.t("&6not cubecraft.net :(")).setScore(1);
                getPlayers().forEach(p -> p.getPlayer().setScoreboard(gameScoreboard));
            }
        }.runTaskTimerAsynchronously(plugin, 2, 50));
    }

    private void handleBossbar() {
        bossBar = plugin.getServer().createBossBar(new NamespacedKey(plugin, "ender_" + uuid), "", BarColor.PURPLE, BarStyle.SEGMENTED_20);
        new BukkitRunnable() {
            @Override
            public void run() {
                countdownTasks.put("gametimer", new Countdown(plugin, gameDuration * 60, () -> {
                    getPlayers().forEach(p -> bossBar.addPlayer(p.getPlayer()));
                }, () -> {
                    endGame(GameEndReason.TIME_UP);
                }, (t) -> {
                    bossBar.setProgress(t.getSecondsLeft() / t.getTotalSeconds());
                    bossBar.setTitle(CC.tf("&dTime remaining: &a%02d:%02d", (int) (t.getSecondsLeft() / 60), (int) (t.getSecondsLeft() % 60)));
                    if (t.getSecondsLeft() == t.getTotalSeconds() / 2) {
                        broadcastf("&c%d minutes remaining!", (int) (t.getSecondsLeft() / 60));
                        getLivingHumans().forEach(human -> {
                            human.getTorch().setPower(human.getTorch().getPower() + 25);
                            human.getPlayer().sendMessage(CC.t("&aYour torch has gained some power!"));
                        });
                    }
                }).scheduleTimer());
            }
        }.runTaskLater(plugin, 10);
    }

    private void handleJumpscares() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!getState().equals(GameState.INGAME)) return;
                Set<EnderPlayer> humans = getLivingHumans();
                if (humans.isEmpty()) return;
                Jumpscare jumpscare = jumpscareManager.getRandomJumpscare();
                int random;
                if (humans.size() == 1) random = 1;
                else random = new Random().nextInt(humans.size() - 1) + 1;
                for (int i = 0; i < random; i++)
                    jumpscare.execute(RandomUtils.getRandom(humans).getPlayer());
            }
        }.runTaskTimerAsynchronously(plugin, 30 * 20, (new Random().nextInt(60 - 20) + 20) * 20);
    }

    private void handleSounds() {
        repeatingTasks.put("sounds", new BukkitRunnable() {
            @Override
            public void run() {
                getPlayers().stream().filter(p -> !soundManager.isPlayingSound(p.getPlayer())).forEach(p -> soundManager.playRandomAmbient(p.getPlayer()));
            }
        }.runTaskTimerAsynchronously(plugin, (30 * 20), 90));


        // TODO play certain sounds when player is at certain places

    }

    private void handlePages() {
        repeatingTasks.put("pages", new BukkitRunnable() {
            @Override
            public void run() {
                if (!getState().equals(GameState.INGAME)) return;
                for (int i = 0; i < 5; i++) {
                    try {
                        ItemStack map = new ItemStack(Material.FILLED_MAP, 1);
                        Location location = pageManager.getRandomLocation();
                        if (!location.getBlock().getType().equals(Material.AIR)) return;
                        BlockFace face = pageManager.getLocations().get(location);
                        ItemFrame frame = world.getGameWorld().spawn(location, ItemFrame.class, f -> {
                            f.setFacingDirection(face, true);
                        });
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                frame.setItem(map);
                            }
                        }.runTaskLater(plugin, 10);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                frame.remove();
                            }
                        }.runTaskLater(plugin, 15 * 20);
                    } catch (IllegalArgumentException ex) {
                        if (!ex.getMessage().contains("no free face")) ex.printStackTrace();
                    }
                }
            }
        }.runTaskTimer(plugin, 10 * 20, 16 * 20));
    }

    private void handleDamage() {
        repeatingTasks.put("damage", new BukkitRunnable() {
            @Override
            public void run() {
                if (!getState().equals(GameState.INGAME)) return;

                /*
                RayTraceResult result = world.getGameWorld().rayTraceEntities(ender.getPlayer().getEyeLocation(), ender.getPlayer().getEyeLocation().getDirection(), 5);
                if (result == null) return;
                Entity target = result.getHitEntity();
                if (target == null) return;
                if (!(target instanceof Player)) return;
                RayTraceResult result2 = world.getGameWorld().rayTraceEntities(((Player) target).getEyeLocation(), ((Player) target).getEyeLocation().getDirection(), 3);
                if (result2 == null) return;
                Entity target2 = result2.getHitEntity();
                if (target2 == null) return;
                if (!(target2 instanceof Player)) return;
                if (((Player) target2) != ender.getPlayer()) return;
                 */

                /*
                double distance = 5;
                org.bukkit.util.Vector eS = ender.getPlayer().getEyeLocation().toVector();
                org.bukkit.util.Vector eD = ender.getPlayer().getEyeLocation().getDirection();
                org.bukkit.util.Vector eE = eS.add(new org.bukkit.util.Vector(eD.getX() * distance, eD.getY() * distance, eD.getZ() * distance));
                Set<Player> targets = new HashSet<>();
                List<Entity> nearby = ender.getPlayer().getNearbyEntities(distance * 2, distance * 2, distance * 2);
                nearby.forEach(entity -> {
                    if (!(entity instanceof Player)) return;
                    Player target = (Player) entity;
                    org.bukkit.util.Vector tS = target.getEyeLocation().toVector();
                    org.bukkit.util.Vector tD = target.getEyeLocation().getDirection();
                    org.bukkit.util.Vector tE = tS.add(new org.bukkit.util.Vector(tD.getX() * distance, tD.getY() * distance, tD.getZ() * distance));
                    if (tE.isInAABB(eS, eE))
                        targets.add(target);
                });
                 */

                Entity target = ender.getPlayer().getTargetEntity(5, false);
                if (target == null) return;
                if (!(target instanceof Player)) return;
                Entity target2 = ((Player) target).getTargetEntity(3, false);
                if (target2 == null) return;
                if (!(target2 instanceof Player)) return;
                if (((Player) target2) != ender.getPlayer()) return;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // targets.forEach(target -> target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 2)));
                        ((Player) target).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 2));
                        // ((Player) target).damage(1);
                    }
                }.runTask(plugin);
            }
        }.runTaskTimer(plugin, 2, 5));
    }

    public void endGame(@Nonnull GameEndReason reason) {
        if (getState().equals(GameState.POSTGAME)) return;
        setGameState(GameState.POSTGAME);
        switch (reason) {
            case TIME_UP:
                broadcast("&6The time is up!");
                broadcast("&c&lNobody &r&6has won the game.");
                break;
            case PAGES_FOUND:
                broadcast("&7-----------------------------------");
                broadcast("&eThe Humans &awon the game!");
                broadcast("&7All pages have been collected!");
                broadcast("&7-----------------------------------");
                break;
            case HUMANS_KILLED:
                broadcast("&7-----------------------------------");
                broadcast("&eThe Ender &awon the game!");
                broadcast("&7All players were eliminated");
                broadcast("&7-----------------------------------");
                break;
            case ENDER_LEFT:
                broadcast("&6The Ender left the game.");
                broadcast("&c&lNobody &r&6has won the game.");
                break;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(plugin, 5 * 20);
    }

    public void stop() {
        new BukkitRunnable() {
            @Override
            public void run() {
                countdownTasks.values().forEach(Countdown::cancel);
                repeatingTasks.values().forEach(BukkitTask::cancel);
                try {
                    if (enderTeam != null) enderTeam.unregister();
                    if (humansTeam != null) humansTeam.unregister();
                    if (bossBar != null) {
                        bossBar.removeAll();
                        bossBar.hide();
                        plugin.getServer().removeBossBar(new NamespacedKey(plugin, "ender_" + uuid));
                    }
                    gameScoreboard.getObjectives().forEach(Objective::unregister);
                } catch (Exception ignored) {
                }
                for (EnderPlayer p : getPlayers()) {
                    p.setTorch(null);
                    p.setEnder(false);
                    p.getPlayer().getInventory().clear();
                    p.getPlayer().setExp(0);
                    p.getPlayer().setLevel(0);
                    p.getPlayer().getActivePotionEffects().forEach(pe -> p.getPlayer().removePotionEffect(pe.getType()));
                    p.getPlayer().setGameMode(GameMode.SURVIVAL);
                    p.getPlayer().setFoodLevel(20);
                    p.getPlayer().setHealth(p.getPlayer().getMaxHealth());
                    p.getPlayer().setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());
                    p.getPlayer().getInventory().clear();
                    p.getPlayer().getActivePotionEffects().forEach(pe -> p.getPlayer().removePotionEffect(pe.getType()));
                    p.getPlayer().teleport(plugin.getSM().getVariables().serverLobby);
                    // p.getPlayer().setResourcePack("https://www.dropbox.com/s/swf39bbwjqogyj4/empty.zip?dl=1");
                }
                getPlayers().clear();
                world.deleteGameWorld();
            }
        }.runTask(plugin);
        plugin.getSM().getGameManager().removeGame(this);
    }

    public UUID getUUID() {
        return uuid;
    }

    public GameState getState() {
        return state;
    }

    public void setGameState(GameState state) {
        this.state = state;
    }

    public Set<EnderPlayer> getPlayers() {
        return players;
    }

    public Set<EnderPlayer> getHumans() {
        return getPlayers().stream().filter(p -> !p.isEnder()).collect(Collectors.toSet());
    }

    public Set<EnderPlayer> getLivingHumans() {
        return getPlayers().stream().filter(p -> !p.isEnder() && !p.getPlayer().getGameMode().equals(GameMode.SPECTATOR)).collect(Collectors.toSet());
    }

    public void addPlayer(EnderPlayer player) {
        getPlayers().add(player);
    }

    public void removePlayer(EnderPlayer player) {
        getPlayers().remove(player);
        if (player.isEnder()) endGame(GameEndReason.ENDER_LEFT);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Map<String, Countdown> getCountdownTasks() {
        return countdownTasks;
    }

    public void cancelTask(String name) {
        Countdown task = getCountdownTasks().get(name);
        if (task == null) return;
        task.cancel();
        getCountdownTasks().remove(name, task);
    }

    public void broadcast(String message) {
        getPlayers().forEach(p -> p.getPlayer().sendMessage(CC.t(message)));
    }

    public void broadcastf(String message, Object... obj) {
        getPlayers().forEach(p -> p.getPlayer().sendMessage(CC.tf(message, obj)));
    }

    public GameWorld getWorld() {
        return world;
    }

    public PageManager getPageManager() {
        return pageManager;
    }

    public GameWorld getLobby() {
        return lobby;
    }

    public void setEnder(EnderPlayer target) {
        ender = target;
    }

}
