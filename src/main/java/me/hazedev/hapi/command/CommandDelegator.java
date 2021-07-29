package me.hazedev.hapi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CommandDelegator extends Command {

    private final CommandExecutor commandExecutor;
    private final TabCompleter tabCompleter;

    public CommandDelegator(@NotNull TabExecutor tabExecutor, @NotNull String name, @NotNull String description, @NotNull String usageMessage, @Nullable List<String> aliases) {
        this(tabExecutor, tabExecutor, name, description, usageMessage, aliases);
    }

    public CommandDelegator(@Nullable TabCompleter tabCompleter, @NotNull String name, @NotNull String description, @NotNull String usageMessage, @Nullable List<String> aliases) {
        this(null, tabCompleter, name, description, usageMessage, aliases);
    }

    public CommandDelegator(@Nullable CommandExecutor commandExecutor, @NotNull String name, @NotNull String description, @NotNull String usageMessage, @Nullable List<String> aliases) {
        this(commandExecutor, null, name, description, usageMessage, aliases);
    }

    public CommandDelegator(@Nullable CommandExecutor commandExecutor, @Nullable TabCompleter tabCompleter, @NotNull String name, @NotNull String description, @NotNull String usageMessage, @Nullable List<String> aliases) {
        super(name, description, usageMessage, aliases != null ? aliases : Collections.emptyList());
        this.commandExecutor = commandExecutor;
        this.tabCompleter = tabCompleter;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return commandExecutor.onCommand(sender, this, alias, args);
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        List<String> result = tabCompleter.onTabComplete(sender, this, alias, args);
        return result != null ? result : Collections.emptyList();
    }
}
