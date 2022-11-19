package me.pafias.ender.commands.subcommands;

import me.pafias.ender.commands.ICommand;
import me.pafias.ender.game.Game;
import me.pafias.ender.objects.EnderPlayer;
import me.pafias.ender.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetpowerCommand extends ICommand {

    public SetpowerCommand() {
        super("setpower", "ender.setpower", "settorchpower");
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Set your torch power level";
    }

    @Override
    public void execute(String mainCommand, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.t("&cOnly players!"));
            return;
        }
        double power = 100;
        if (args.length >= 2)
            try {
                power = Double.parseDouble(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(CC.t("&cInvalid amount."));
                return;
            }
        EnderPlayer player = plugin.getSM().getPlayerManager().getPlayer((Player) sender);
        Game game = plugin.getSM().getGameManager().getGame();
        if (game == null) {
            sender.sendMessage(CC.t("&cYou are not in a game!"));
            return;
        }
        if (player.getTorch() != null)
            player.getTorch().setPower(power);
        sender.sendMessage(CC.t("&aTorch power changed."));
    }

}
