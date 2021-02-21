package me.hazedev.hapi.chat;

import me.hazedev.hapi.chat.json.JsonComponent;
import me.hazedev.hapi.chat.json.JsonMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ChatUtils {

    static final Map<String, Map<String, Long>> messageCooldowns = new HashMap<>(0);

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(CCUtils.addColor(message));
    }

    public static void sendRawMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    public static void sendMessage(CommandSender sender, String[] message) {
        sender.sendMessage(CCUtils.addColor(message));
    }

    public static void sendMessage(CommandSender sender, List<String> message) {
        sendMessage(sender, message.toArray(new String[0]));
    }

    public static void sendCooldownMessage(CommandSender sender, String id, String message, int cooldownMillis) {
        if (!messageCooldowns.containsKey(id)) {
            messageCooldowns.put(id, new HashMap<>());
        }
        Map<String, Long> cooldowns = messageCooldowns.get(id);
        if (cooldowns.containsKey(sender.getName())) {
            long cooldownTime = cooldowns.get(sender.getName());
            if (System.currentTimeMillis() - cooldownTime >= cooldownMillis) {
                ChatUtils.sendMessage(sender, message);
            } else {
                return;
            }
        } else {
            ChatUtils.sendMessage(sender, message);
        }
        cooldowns.put(sender.getName(), System.currentTimeMillis());
    }

    public static void sendMessage(CommandSender sender, JsonMessage jsonMessage) {
        BaseComponent[] baseComponents = ComponentSerializer.parse(jsonMessage.toString());
        sender.spigot().sendMessage(baseComponents);
    }

    public static void sendMessage(CommandSender sender, JsonComponent jsonComponent) {
        BaseComponent[] baseComponents = ComponentSerializer.parse(jsonComponent.toString());
        sender.spigot().sendMessage(baseComponents);
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(CCUtils.addColor(message));
    }

    public static void broadcast(JsonMessage message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            sendMessage(player, message);
        });
    }

    public static void broadcast(JsonComponent message) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            sendMessage(player, message);
        });
    }

    public static void broadcast(String message, Predicate<Player> predicate) {
        for (Player player: Bukkit.getOnlinePlayers()) {
            if (predicate.test(player)) {
                ChatUtils.sendMessage(player, message);
            }
        }
    }

}
