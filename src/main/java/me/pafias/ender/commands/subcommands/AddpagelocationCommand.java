package me.pafias.ender.commands.subcommands;

import me.pafias.ender.commands.ICommand;
import me.pafias.ender.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AddpagelocationCommand extends ICommand {

    public AddpagelocationCommand() {
        super("addpagelocation", "ender.addpagelocation", "apl");
    }

    @Override
    public String getArgs() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Add a page location to the config";
    }


    public static Set<UUID> selecting = new HashSet<>();

    @Override
    public void execute(String mainCommand, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.t("&cOnly players!"));
            return;
        }
        if (!selecting.contains(((Player) sender).getUniqueId())) {
            selecting.add(((Player) sender).getUniqueId());
            sender.sendMessage(CC.t("&6Right-click the block where the ItemFrame should be placed."));
            sender.sendMessage(CC.t("&6Don't mind two messages showing up with one click, only the first one counts."));
        } else {
            selecting.remove(((Player) sender).getUniqueId());
            sender.sendMessage(CC.t("&6No longer adding pages."));
        }
    }

}
