package me.pafias.ender.commands;

import me.pafias.ender.Ender;
import me.pafias.ender.commands.subcommands.ForcestartCommand;
import me.pafias.ender.util.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class EnderCommand implements CommandExecutor {

    private final Ender plugin;

    public EnderCommand(Ender plugin) {
        this.plugin = plugin;
        commands.add(new ForcestartCommand());
    }

    private Set<ICommand> commands = new HashSet<>();

    private boolean help(CommandSender sender, String label) {
        sender.sendMessage(CC.t("&f-------------------- &bFFA &f--------------------"));
        commands.forEach(command -> {
            if (sender.hasPermission(command.getPermission()))
                sender.sendMessage(CC.tf("&3/%s %s %s &9- %s", label, command.getName(), command.getArgs(), command.getDescription()));
        });
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return help(sender, label);
        else {
            ICommand cmd = commands.stream().filter(c -> c.getName().equalsIgnoreCase(args[0]) || c.getAliases().contains(args[0])).findFirst().orElse(null);
            if (cmd == null) return help(sender, label);
            if (cmd.getPermission() != null && !sender.hasPermission(cmd.getPermission())) {
                cmd.noPermission(sender);
                return true;
            } else
                cmd.execute(label, sender, args);
        }
        return true;
    }

}
