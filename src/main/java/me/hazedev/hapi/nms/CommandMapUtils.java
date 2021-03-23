package me.hazedev.hapi.nms;

import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.logging.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class CommandMapUtils {

    private static CommandMap commandMap = null;

    public static void reloadCommandMap() throws IllegalAccessException, NoSuchFieldException {
        final Field commandMapField = ReflectionUtils.getPrivateField(Bukkit.getServer().getClass(), "commandMap");
        if (commandMapField == null) {
            throw new NoSuchFieldException();
        } else {
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        }
    }

    public static boolean attemptReloadCommandMap() {
        try {
            reloadCommandMap();
            return true;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Log.warning("Failed to load CommandMap");
            Log.error(e);
            return false;
        }
    }

    @Nullable
    public static CommandMap getCommandMap() {
        if (commandMap == null) {
            attemptReloadCommandMap();
        }
        return commandMap;
    }

    private static boolean register(@NotNull String fallbackPrefix, @NotNull BukkitCommand command) {
        if (attemptReloadCommandMap()) {
            commandMap.register(fallbackPrefix, command);
            return true;
        }
        return false;
    }

    public static boolean register(@NotNull Component component, @NotNull BukkitCommand command) {
        return register(component.getId(), command);
    }

    public static boolean register(@NotNull Plugin plugin, @NotNull BukkitCommand command) {
        return register(plugin.getName(), command);
    }

}
