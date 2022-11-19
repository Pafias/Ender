package me.pafias.ender.commands.subcommands;

import me.pafias.ender.commands.ICommand;
import me.pafias.ender.game.Game;
import me.pafias.ender.game.GameState;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetenderCommand extends ICommand {

    public SetenderCommand() {
        super("setender", "ender.setender", "ender");
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Set the ender";
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
        if(!game.getState().equals(GameState.LOBBY)){
            sender.sendMessage(CC.t("&cYou can only do that when the game hasn't started yet!"));
            return;
        }
        if (plugin.getServer().getPlayer(args[1]) == null) {
            sender.sendMessage(CC.t("&cPlayer not found!"));
            return;
        }
        EnderPlayer target = plugin.getSM().getPlayerManager().getPlayer(args[1]);
        if (!game.getPlayers().contains(target)) {
            sender.sendMessage(CC.t("&cThat player is not in this game!"));
            return;
        }
        game.setEnder(target);
        sender.sendMessage(CC.t("&aEnder set."));
    }

}
