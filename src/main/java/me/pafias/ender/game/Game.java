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
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.potion.PotionEffect;
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
    private BossBar bossBar;
    private PageManager pageManager;
    private JumpscareManager jumpscareManager;
    private SoundManager soundManager;

    public Game() throws IOException {
        uuid = UUID.randomUUID();
        world = new GameWorld(plugin.getSM().getVariables().gameSpawn.getWorld(), uuid.toString().split("-")[0]);
        state = GameState.LOBBY;
        players = new HashSet<>();
        maxPlayers = plugin.getSM().getVariables().maxPlayers;
        gameDuration = plugin.getSM().getVariables().gameDuration;
        gameScoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        pageManager = new PageManager(plugin, this);
        jumpscareManager = new JumpscareManager(plugin, this);
        soundManager = new SoundManager(plugin, this);
        countdownTasks = new HashMap<>();
        repeatingTasks = new HashMap<>();
    }

    public void start() {
        setGameState(GameState.PREGAME);
        handleEnder();
        getHumans().forEach(p -> {
            p.getPlayer().teleport(plugin.getSM().getVariables().gameSpawn.clone().add(new Random().nextDouble(), 0, new Random().nextDouble()));
            p.setEnder(false);
        });
        getCountdownTasks().put("start2", new Countdown(plugin, 5, () -> {
        }, () -> {
            getPlayers().forEach(pp -> {
                pp.getPlayer().setLevel(0);
                pp.getPlayer().setExp(0);
                pp.getPlayer().setFoodLevel(5);
                handleTorch(pp);
                if (!pp.isEnder())
                    pp.getPlayer().playSound(pp.getPlayer().getEyeLocation(), "custom.effect.intro", 1f, 1f);
                handleTeams();
                handleGamescoreboard();
                handleBossbar();
                handleJumpscares();
                handleSounds();
            });
        }, (t) -> {
            boolean sound = false;
            if (t.getSecondsLeft() <= 3) {
                broadcastf("&6Starting in &7%s &6seconds", t.getSecondsLeft());
                sound = true;
            }
            boolean finalSound = sound;
            getPlayers().forEach(pp -> {
                pp.getPlayer().setLevel((int) t.getSecondsLeft());
                pp.getPlayer().setExp(t.getSecondsLeft() / t.getTotalSeconds());
                if (finalSound)
                    pp.getPlayer().playSound(pp.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f, 1f);
            });
        }));
    }

    private void handleEnder() {
        ender = RandomUtils.getRandom(getPlayers());
        ender.setEnder(true);
        ender.getPlayer().getInventory().setArmorContents(RandomUtils.getEnderOutfit());
        ender.getPlayer().getInventory().setContents(RandomUtils.getEnderTools());
        double nX, nZ;
        float nAng = plugin.getSM().getVariables().gameSpawn.getYaw();
        if (nAng < 0)
            nAng += 360;
        nX = Math.cos(Math.toRadians(nAng));
        nZ = Math.sin(Math.toRadians(nAng));
        Location loc = plugin.getSM().getVariables().gameSpawn.clone();
        loc.setX(loc.getX() - nX);
        loc.setZ(loc.getZ() - nZ);
        loc.setYaw(loc.getYaw() + 180);
        ender.getPlayer().teleport(loc);
    }

    private void handleTorch(EnderPlayer p) {
        p.setTorch(new Torch(p));
        p.getTorch().changeInventoryItem();
    }

    private void handleTeams() {
        enderTeam = gameScoreboard.registerNewTeam("ender");
        enderTeam.setDisplayName(CC.t("&c"));
        enderTeam.setPrefix(CC.t("&c"));
        enderTeam.setColor(ChatColor.RED);
        enderTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        enderTeam.addPlayer(ender.getPlayer());
        humansTeam = gameScoreboard.registerNewTeam("humans");
        humansTeam.setDisplayName(CC.t("&b"));
        humansTeam.setPrefix(CC.t("&b"));
        humansTeam.setColor(ChatColor.AQUA);
        humansTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        getHumans().forEach(human -> humansTeam.addPlayer(human.getPlayer()));
    }

    private void handleGamescoreboard() {
        Team sbPages = gameScoreboard.registerNewTeam("SB_pages");
        sbPages.addEntry(ChatColor.WHITE + "");
        Team sbHumans = gameScoreboard.registerNewTeam("SB_humans");
        sbHumans.addEntry(ChatColor.BLUE + "");
        Objective objective = gameScoreboard.registerNewObjective("ender", "dummy", CC.t("&e&lEnder"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        repeatingTasks.put("gamescoreboard", new BukkitRunnable() {
            @Override
            public void run() {
                sbPages.setPrefix(CC.tf("&a%d/%d", pageManager.getPagesFound(), pageManager.getTotalPages()));
                sbHumans.setPrefix(CC.tf("&a%d", getPlayers().size()));
                objective.getScore(CC.t("&5Pages:")).setScore(7);
                objective.getScore(ChatColor.WHITE + "").setScore(6);
                objective.getScore(" ").setScore(5);
                objective.getScore(CC.t("&5Humans:")).setScore(4);
                objective.getScore(ChatColor.BLUE + "").setScore(3);
                objective.getScore("").setScore(2);
                objective.getScore(CC.t("&6not cubecraft.net :(")).setScore(1);
                getPlayers().forEach(p -> p.getPlayer().setScoreboard(gameScoreboard));
            }
        }.runTaskTimerAsynchronously(plugin, 2, 20));
    }

    private void handleBossbar() {
        countdownTasks.put("gametimer", new Countdown(plugin, gameDuration * 60, () -> {
            bossBar = plugin.getServer().createBossBar(CC.t("&dTime remaining"), BarColor.PURPLE, BarStyle.SEGMENTED_20);
            bossBar.setProgress(1);
            getPlayers().forEach(p -> bossBar.addPlayer(p.getPlayer()));
        }, () -> {
            endGame(GameEndReason.TIME_UP);
        }, (t) -> {
            bossBar.setTitle(CC.tf("&dTime remaining: &a%02d:%02d", t.getSecondsLeft() / 60, t.getSecondsLeft() % 60));
            bossBar.setProgress(t.getSecondsLeft() / t.getTotalSeconds());
        }));
    }

    private void handleJumpscares() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Jumpscare jumpscare = jumpscareManager.getRandomJumpscare();
                int min, max;
                min = Math.round((getHumans().size() / 100f) * 5f);
                max = Math.round((getHumans().size() / 100f) * 70f);
                int random = new Random().nextInt(max - min) + max;
                for (int i = 0; i < random; i++)
                    jumpscare.execute(RandomUtils.getRandom(getHumans()).getPlayer());
            }
        }.runTaskTimerAsynchronously(plugin, 30 * 20, (new Random().nextInt(60 - 20) + 20) * 20);
    }

    private void handleSounds() {
        new BukkitRunnable() {
            @Override
            public void run() {
                getPlayers().stream().filter(p -> !soundManager.isPlayingSound(p.getPlayer())).forEach(p -> soundManager.playRandomAmbient(p.getPlayer()));
            }
        }.runTaskTimerAsynchronously(plugin, (30 * 20), (5 * 20));


        // TODO play certain sounds when player is at certain places

    }

    private void endGame(@Nonnull GameEndReason reason) {
        setGameState(GameState.POSTGAME);
        switch (reason) {
            case TIME_UP:
                broadcast("&6The time is up!");
                broadcast("&c&lNobody &r&6has won the game.");
                break;
            case PAGES_FOUND:
                broadcast("&6All pages have been found!");
                broadcast("&6The &b&lHumans &r&6have won the game!");
                break;
            case HUMANS_KILLED:
                broadcast("&7-----------------------------------");
                broadcast("&eThe Ender &awon the game!");
                broadcast("&7All players were eliminated");
                broadcast("&7-----------------------------------");
                break;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(plugin, 5 * 20);
    }

    private void stop() {
        enderTeam.unregister();
        humansTeam.unregister();
        bossBar.removeAll();
        bossBar.hide();
        getPlayers().forEach(p -> {
            p.getPlayer().getInventory().clear();
            p.getPlayer().setExp(0);
            p.getPlayer().setLevel(0);
            for (PotionEffect pe : p.getPlayer().getActivePotionEffects()) {
                p.getPlayer().removePotionEffect(pe.getType());
            }
            p.getPlayer().setGameMode(GameMode.SURVIVAL);
            p.getPlayer().setFoodLevel(20);
            p.getPlayer().setHealth(p.getPlayer().getMaxHealth());
            p.getPlayer().setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());
            p.getPlayer().getInventory().clear();
            p.getPlayer().getActivePotionEffects().forEach(pe -> p.getPlayer().removePotionEffect(pe.getType()));
            p.getPlayer().teleport(plugin.getSM().getVariables().serverLobby);
        });
        // TODO remove all pages and stuff
        countdownTasks.values().forEach(Countdown::cancel);
        repeatingTasks.values().forEach(BukkitTask::cancel);
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

    public void addPlayer(EnderPlayer player) {
        getPlayers().add(player);
    }

    public void removePlayer(EnderPlayer player) {
        getPlayers().remove(player);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Map<String, Countdown> getCountdownTasks() {
        return countdownTasks;
    }

    public void cancelTask(String name) {
        Countdown task = getCountdownTasks().get(name);
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
}
