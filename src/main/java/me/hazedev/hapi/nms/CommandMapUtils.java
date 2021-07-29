package me.hazedev.hapi.nms;

import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.logging.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class CommandMapUtils {

    private static SimpleCommandMap commandMap = null;

    public static void reloadCommandMap() throws IllegalAccessException, NoSuchFieldException {
        final Field commandMapField = ReflectionUtils.getPrivateField(Bukkit.getServer().getClass(), "commandMap");
        if (commandMapField == null) {
            throw new NoSuchFieldException();
        } else {
            commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());

        }
    }

    public static boolean loadCommandMap() {
        if (commandMap == null) {
            try {
                reloadCommandMap();
            } catch (IllegalAccessException | NoSuchFieldException e) {
                Log.error(null, e, "Failed to load CommandMap");
            }
        }
        return commandMap != null;
    }

    @Nullable
    public static CommandMap getCommandMap() {
        loadCommandMap();
        return commandMap;
    }

    private static boolean register(@NotNull String fallbackPrefix, @NotNull Command command) {
        if (loadCommandMap()) {
            commandMap.register(fallbackPrefix, command);
            return true;
        }
        return false;
    }

    public static boolean register(@NotNull Component component, @NotNull Command command) {
        return register(component.getId(), command);
    }

    public static boolean register(@NotNull Plugin plugin, @NotNull Command command) {
        return register(plugin.getName(), command);
    }

    public static boolean unregister(@NotNull Command command) {
        if (loadCommandMap()) {
            command.unregister(commandMap);
            return true;
        }
        return false;
    }

}
