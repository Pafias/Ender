package me.pafias.ender.commands.subcommands;

import me.pafias.ender.commands.ICommand;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.GameState;
import me.pafias.ender.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends ICommand {

    public JoinCommand() {
        super("create", "ender.create", "c");
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Create a game";
    }

    @Override
    public void execute(String mainCommand, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.t("&cOnly players!"));
            return;
        }
        String input;
        if (args.length < 2)
            input = null;
        else
            input = args[1];
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) {
            sender.sendMessage(CC.t("&cGame not found."));
            return;
        }
        if (!game.getState().equals(GameState.LOBBY)) {
            sender.sendMessage(CC.t("&cThis game has already started."));
            return;
        }
        plugin.getSM().getGameManager().addPlayer((Player) sender);
    }

}
