package me.hazedev.hapi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CommandDelegator extends Command {

    private final TabExecutor tabExecutor;

    public CommandDelegator(@NotNull TabExecutor tabExecutor, @NotNull String name, @NotNull String description, @NotNull String usageMessage, @Nullable List<String> aliases) {
        super(name, description, usageMessage, aliases != null ? aliases : Collections.emptyList());
        this.tabExecutor = tabExecutor;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return tabExecutor.onCommand(sender, this, alias, args);
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        List<String> result = tabExecutor.onTabComplete(sender, this, alias, args);
        return result != null ? result : Collections.emptyList();
    }
}
