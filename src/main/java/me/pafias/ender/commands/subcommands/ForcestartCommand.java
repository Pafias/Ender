package me.pafias.ender.commands.subcommands;

import me.pafias.ender.commands.ICommand;
import me.pafias.ender.game.Game;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForcestartCommand extends ICommand {

    public ForcestartCommand() {
        super("forcestart", "ender.forcestart", "fs");
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Force-start the game";
    }

    @Override
    public void execute(String mainCommand, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.t("&cOnly players!"));
            return;
        }
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer((Player) sender);
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) {
            sender.sendMessage(CC.t("&cYou are not in a game!"));
            return;
        }
        game.cancelTask("start");
        game.start();
        sender.sendMessage(CC.t("&aGame force-started!"));
    }

}
