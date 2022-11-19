package me.pafias.ender.commands.subcommands;

import me.pafias.ender.commands.ICommand;
import me.pafias.ender.game.Game;
import me.pafias.ender.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends ICommand {

    public CreateCommand() {
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
        Game game = plugin.getSM().getGameManager().createGame();
        sender.sendMessage(CC.tf("&aCreated game with ID &7%s", game.getUUID().toString()));
    }

}
