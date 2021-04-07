package me.hazedev.hapi.command;

import me.hazedev.hapi.chat.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommandHelper {

    String NO_PERM = "&cYou don't have permission to use this command!";
    String NOT_A_PLAYER = "&cYou must be a player to use this command!";

    static Player validatePlayer(@NotNull CommandSender sender) {
        return validatePlayer(sender, NOT_A_PLAYER);
    }

    static Player validatePlayer(@NotNull CommandSender sender, @Nullable String message) {
        if (sender instanceof Player) {
            return (Player) sender;
        } else if (message != null) {
            ChatUtils.sendMessage(sender, message);
        }
        return null;
    }

    static boolean checkPermission(@NotNull CommandSender sender, @NotNull String permission) {
        return checkPermission(sender, permission, NO_PERM);
    }

    static boolean checkPermission(@NotNull CommandSender sender, @NotNull String permission, @Nullable String message) {
        if (sender.hasPermission(permission)) {
            return true;
        } else if (message != null) {
            ChatUtils.sendMessage(sender, message);
        }
        return false;
    }

}
