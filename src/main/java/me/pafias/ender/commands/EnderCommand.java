package me.pafias.ender.commands;

import me.pafias.ender.Ender;
import me.pafias.ender.User;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.GameState;
import me.pafias.ender.listeners.SetupListener;
import me.pafias.ender.utils.CC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EnderCommand implements CommandExecutor {

    private final Ender plugin;

    public EnderCommand(Ender plugin) {
        this.plugin = plugin;
    }

    private void help(CommandSender sender, String label) {
        if (sender.isOp() || sender.hasPermission("ender.admin")) {
            sender.sendMessage(CC.translate("&9/" + label + " help &f- Display the help menu"));
            sender.sendMessage(CC.translate("&9/" + label + " stats &f- See your stats"));
            sender.sendMessage(CC.translate("&9/" + label + " leave &f- Leave the game"));
            sender.sendMessage(CC.translate("&9/" + label + " forcestart &f- Forcestart current game"));
            sender.sendMessage(CC.translate("&9/" + label + " setlobby &f- Set the lobby"));
            sender.sendMessage(CC.translate("&9/" + label + " setplayersspawnlocation &f- Set the players spawn location"));
            sender.sendMessage(CC.translate("&9/" + label + " sethub &f- Set the hub"));
            sender.sendMessage(CC.translate("&9/" + label + " setpagelocation &f- Add a page location to the config"));
            sender.sendMessage(CC.translate("&9/" + label + " stop &f- Stop the game"));
            sender.sendMessage(CC.translate("&9/" + label + " triggerjumpscare &f- Trigger the jumpscares for the players"));
            sender.sendMessage(CC.translate("&9/" + label + " join &f- Join a game"));
            sender.sendMessage(CC.translate("&9/" + label + " create &f- Create a game"));
            sender.sendMessage(CC.translate("&9/" + label + " setender &f- Set the ender for the game"));
            sender.sendMessage(CC.translate("&9/" + label + " setflashlightlevel &f- Set the ender for the game"));
        } else {
            plugin.getSM().getVariables().help.forEach(s -> sender.sendMessage(CC.translate(s)));
        }
    }

    private boolean checkPerm(CommandSender sender) {
        if (sender.hasPermission("ender.admin") || sender.isOp()) {
            return true;
        } else {
            sender.sendMessage(plugin.getSM().getVariables().no_permission);
            return false;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ender")) {
            if (args.length == 0) {
                help(sender, label);
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    help(sender, label);
                    return true;
                } else if (args[0].equalsIgnoreCase("leave")) {
                    User user = plugin.getSM().getUserManager().getUser((Player) sender);
                    if (!plugin.getSM().getGameManager().isInGame(user)) {
                        sender.sendMessage(ChatColor.RED + "You are not in a game!");
                        return true;
                    }
                    Game game = plugin.getSM().getGameManager().getGame(user);
                    plugin.getSM().getGameManager().removePlayer(user);
                    game.broadcast(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.RED + " left the game.");
                    sender.sendMessage(ChatColor.RED + "You left the game.");
                    return true;
                } else if (args[0].equalsIgnoreCase("forcestart")) {
                    if (checkPerm(sender)) {
                        User user = plugin.getSM().getUserManager().getUser((Player) sender);
                        if (!plugin.getSM().getGameManager().isInGame(user)) {
                            sender.sendMessage(ChatColor.RED + "You're not in a game!");
                            return true;
                        }
                        Game game = plugin.getSM().getGameManager().getGame(user);
                        game.start(true);
                        sender.sendMessage(ChatColor.GREEN + "You force-started the game.");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("setlobby")) {
                    if (checkPerm(sender)) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatColor.RED + "Only players!");
                            return true;
                        }
                        Player player = (Player) sender;
                        plugin.getConfig().set("lobby.x", player.getLocation().getX());
                        plugin.getConfig().set("lobby.y", player.getLocation().getY());
                        plugin.getConfig().set("lobby.z", player.getLocation().getZ());
                        plugin.getConfig().set("lobby.yaw", player.getLocation().getYaw());
                        plugin.getConfig().set("lobby.pitch", player.getLocation().getPitch());
                        plugin.getConfig().set("lobby.world", player.getLocation().getWorld().getName());
                        plugin.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Lobby has been set!");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("setplayersspawnlocation")) {
                    if (checkPerm(sender)) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatColor.RED + "Only players!");
                            return true;
                        }
                        Player player = (Player) sender;
                        plugin.getConfig().set("playersspawnlocation.x", player.getLocation().getX());
                        plugin.getConfig().set("playersspawnlocation.y", player.getLocation().getY());
                        plugin.getConfig().set("playersspawnlocation.z", player.getLocation().getZ());
                        plugin.getConfig().set("playersspawnlocation.yaw", player.getLocation().getYaw());
                        plugin.getConfig().set("playersspawnlocation.pitch", player.getLocation().getPitch());
                        plugin.getConfig().set("playersspawnlocation.world", player.getLocation().getWorld().getName());
                        plugin.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Players spawn location has been set!");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("sethub")) {
                    if (checkPerm(sender)) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatColor.RED + "Only players!");
                            return true;
                        }
                        Player player = (Player) sender;
                        plugin.getConfig().set("hub.x", player.getLocation().getX());
                        plugin.getConfig().set("hub.y", player.getLocation().getY());
                        plugin.getConfig().set("hub.z", player.getLocation().getZ());
                        plugin.getConfig().set("hub.yaw", player.getLocation().getYaw());
                        plugin.getConfig().set("hub.pitch", player.getLocation().getPitch());
                        plugin.getConfig().set("hub.world", player.getLocation().getWorld().getName());
                        plugin.saveConfig();
                        player.sendMessage(ChatColor.GREEN + "Hub has been set!");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("setpagelocation")) {
                    if (checkPerm(sender)) {
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(ChatColor.RED + "Only players!");
                            return true;
                        }
                        Player player = (Player) sender;
                        if (!SetupListener.setupPages.isEmpty()) {
                            player.sendMessage(ChatColor.RED
                                    + "Someone is already doing this. Please wait until he/she has finished.");
                            return true;
                        }
                        SetupListener.setupPages.add(player.getName());
                        player.sendMessage(ChatColor.GOLD
                                + "Right-click on the block where you want the page to be.");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (checkPerm(sender)) {
                        User user = plugin.getSM().getUserManager().getUser((Player) sender);
                        if (!plugin.getSM().getGameManager().isInGame(user)) {
                            sender.sendMessage(ChatColor.RED + "You're not in a game!");
                            return true;
                        }
                        Game game = plugin.getSM().getGameManager().getGame(user);
                        game.stop();
                        sender.sendMessage(ChatColor.GREEN + "You stopped the game.");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("triggerjumpscare")) {
                    if (checkPerm(sender)) {
                        User user = plugin.getSM().getUserManager().getUser((Player) sender);
                        if (!plugin.getSM().getGameManager().isInGame(user)) {
                            sender.sendMessage(ChatColor.RED + "You're not in a game!");
                            return true;
                        }
                        Game game = plugin.getSM().getGameManager().getGame(user);
                        game.jumpscare1();
                        game.jumpscare2();
                        game.jumpscare3();
                        sender.sendMessage(ChatColor.GREEN + "Jumpscares triggered.");
                        return true;
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("join")) {
                    User user = plugin.getSM().getUserManager().getUser((Player) sender);
                    String s = args[1];
                    Game game = plugin.getSM().getGameManager().getGame(s);
                    if (game == null) {
                        sender.sendMessage(ChatColor.RED + "Game not found.");
                        return true;
                    }
                    if (plugin.getSM().getGameManager().isInGame(user)) {
                        sender.sendMessage(ChatColor.RED + "You are already in a game!");
                        return true;
                    }
                    if (game.getPlayers().size() >= game.getMaxPlayers()) {
                        sender.sendMessage(ChatColor.RED + "This game is full!");
                        return true;
                    }
                    if (game.getGamestate() != GameState.LOBBY) {
                        sender.sendMessage(ChatColor.RED + "You cannot join this game.");
                        return true;
                    }
                    plugin.getSM().getGameManager().addPlayer(user, game);
                    // sender.sendMessage(ChatColor.GREEN + "You joined the game.");
                    return true;
                } else if (args[0].equalsIgnoreCase("create")) {
                    if (checkPerm(sender)) {
                        if (args[1] != null) {
                            if (plugin.getSM().getGameManager().getGame(args[1]) != null) {
                                sender.sendMessage(ChatColor.RED + "That game already exists!");
                                return true;
                            }
                            Game game = new Game(args[1]);
                            sender.sendMessage(ChatColor.GREEN + "Game " + args[1] + " created.");
                            return true;
                        }
                    }
                } else if (args[0].equalsIgnoreCase("setender")) {
                    if (checkPerm(sender)) {
                        User user = plugin.getSM().getUserManager().getUser((Player) sender);
                        if (!plugin.getSM().getGameManager().isInGame(user)) {
                            sender.sendMessage(ChatColor.RED + "You're not in a game!");
                            return true;
                        }
                        Game game = plugin.getSM().getGameManager().getGame(user);
                        if (game.getGamestate().equals(GameState.LOBBY)
                                || game.getGamestate().equals(GameState.PREGAME)) {
                            Player args1 = Bukkit.getPlayer(args[1]);
                            if (args1 != null && args1.isOnline()) {
                                User target = plugin.getSM().getUserManager().getUser(args1);
                                game.setEnder(target);
                                sender.sendMessage(ChatColor.GREEN + "The ender in your current game has been set to "
                                        + target.getPlayer().getName());
                            } else {
                                sender.sendMessage(ChatColor.RED + "Player not found.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "You cannot change this now.");
                        }
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("addplayer")) {
                    if (args[1].equalsIgnoreCase("*")) {
                        plugin.getSM().getUserManager().getUsers().forEach(user -> plugin.getSM().getGameManager().addPlayer(user));
                        sender.sendMessage(org.bukkit.ChatColor.GREEN + "Players added");
                        return true;
                    }
                    User user = plugin.getSM().getUserManager().getUser(args[1]);
                    plugin.getSM().getGameManager().addPlayer(user);
                    sender.sendMessage(org.bukkit.ChatColor.GREEN + "Player added");
                } else if (args[0].equalsIgnoreCase("removeplayer")) {
                    if (args[1].equalsIgnoreCase("*")) {
                        plugin.getSM().getUserManager().getUsers().forEach(user -> plugin.getSM().getGameManager().removePlayer(user));
                        sender.sendMessage(org.bukkit.ChatColor.GREEN + "Players removed");
                        return true;
                    }
                    User user = plugin.getSM().getUserManager().getUser(args[1]);
                    plugin.getSM().getGameManager().removePlayer(user);
                    sender.sendMessage(org.bukkit.ChatColor.GREEN + "Player removed");
                }
            } else if (args.length > 2) {
                if (args[0].equalsIgnoreCase("setflashlightlevel")) {
                    if (checkPerm(sender)) {
                        User user = plugin.getSM().getUserManager().getUser(args[1]);
                        if (user == null) {
                            sender.sendMessage(CC.translate("&cUser not found!"));
                            return true;
                        }
                        if (!plugin.getSM().getGameManager().isInGame(user)) {
                            sender.sendMessage(ChatColor.RED + "That player is not in a game!");
                            return true;
                        }
                        Game game = plugin.getSM().getGameManager().getGame(user);
                        if (game.getGamestate().equals(GameState.INGAME)) {
                            if (game.getEnder() == user) {
                                sender.sendMessage(CC.translate("&cThat player is the ender!"));
                                return true;
                            }
                            try {
                                user.setFlashlightbattery(Integer.parseInt(args[2]));
                            } catch (NumberFormatException ex) {
                                sender.sendMessage(CC.translate("&cInvalid number!"));
                                return true;
                            }
                            sender.sendMessage(CC.translate("&aSuccessfully changed user's battery level!"));
                            return true;
                        } else {
                            sender.sendMessage(ChatColor.RED + "You cannot change this now.");
                        }
                        return true;
                    }
                }
            }
        } else {
            sender.sendMessage(plugin.getSM().getVariables().no_permission);
            return true;
        }
        return true;
    }

}
