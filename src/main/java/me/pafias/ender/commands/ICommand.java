package me.pafias.ender.commands;

import me.pafias.ender.Ender;
import me.pafias.ender.util.CC;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class ICommand {

    public Ender plugin = Ender.get();

    String name;
    Set<String> aliases;
    String permission;
    String args;
    String description;

    public ICommand(String name) {
        this.name = name;
        this.aliases = new HashSet<>();
        this.permission = null;
    }

    public ICommand(String name, String permission, String... aliases) {
        this.name = name;
        this.aliases = new HashSet<>(Arrays.asList(aliases));
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public String getPermission() {
        return permission;
    }

    public abstract String getArgs();

    public abstract String getDescription();

    public abstract void execute(String mainCommand, CommandSender sender, String[] args);

    public void noPermission(CommandSender sender) {
        sender.sendMessage(CC.t("&cNo permission."));
    }

}
