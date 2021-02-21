package me.hazedev.hapi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public abstract class SubCommand {

    protected final String name;
    protected final String[] aliases;
    protected final Argument<?>[] arguments;
    protected final Flag[] flags;

    public SubCommand(String name, String[] aliases, Argument<?>[] arguments, Flag[] flags) {
        this.name = Objects.requireNonNull(name, "Command name is null!");
        this.aliases = aliases == null ? new String[0] : aliases;
        this.arguments = arguments == null ? new Argument[0] : arguments;
        this.flags = flags == null ? new Flag[0] : flags;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public Argument<?>[] getArguments() {
        return arguments;
    }

    public String getCustomNoPermissionMessage() {
        return null;
    }

    public void onCommand(CommandSender sender, String label, List<String> args, List<String> flags) {}

    public void onCommand(CommandSender sender, Command command, String label, List<String> args, List<String> flags) {
        onCommand(sender, label, args, flags);
    }

    public List<String> onTabComplete(CommandSender sender, String[] args, int argIndex) {
        return null;
    }

    // Util methods below here

    public Player validatePlayer(CommandSender sender) throws IllegalArgumentException {
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            throw new IllegalArgumentException("You must be a player!");
        }
    }

    public ConsoleCommandSender validateConsole(CommandSender sender) throws IllegalArgumentException {
        if (sender instanceof ConsoleCommandSender) {
            return (ConsoleCommandSender) sender;
        } else {
            throw new IllegalArgumentException("This command can only be ran from console!");
        }
    }

}
