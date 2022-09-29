package me.pafias.ender.commands.subcommands;

import me.pafias.ender.commands.ICommand;
import org.bukkit.command.CommandSender;

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
        // TODO
    }

}
