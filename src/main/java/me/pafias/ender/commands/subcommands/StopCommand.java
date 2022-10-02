package me.pafias.ender.commands.subcommands;

import me.pafias.ender.commands.ICommand;
import me.pafias.ender.game.Game;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopCommand extends ICommand {

    public StopCommand() {
        super("stop", "ender.stop");
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Stop the game";
    }

    @Override
    public void execute(String mainCommand, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.t("&cOnly players!"));
            return;
        }
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer((Player) sender);
        Game game = plugin.getSM().getGameManager().getGame(player);
        if (game == null) {
            sender.sendMessage(CC.t("&cYou are not in a game!"));
            return;
        }
        game.stop();
        sender.sendMessage(CC.t("&aGame stopped!"));
    }

}
