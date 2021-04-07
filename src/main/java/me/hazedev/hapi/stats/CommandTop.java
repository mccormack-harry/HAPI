package me.hazedev.hapi.stats;

import me.hazedev.hapi.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandTop extends Command {

    private final AbstractStatisticManager manager;

    public CommandTop(AbstractStatisticManager manager) {
        super("top", "View stats leaderboards", "/top <statistic>", Collections.emptyList());
        this.manager = manager;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1) {
            String id = args[0];
            Statistic<?> statistic = manager.getStatistic(id);
            if (statistic != null) {
                List<String> top = new ArrayList<>();
                top.add("&aTop 10 - &f" + statistic.getName());
                Map<UUID, String> topMap = statistic.getTopFormatted(10, true);
                topMap.forEach((uuid, value) -> {
                    String name = Optional.ofNullable(Bukkit.getOfflinePlayer(uuid).getName()).orElse("?");
                    int pos = top.size();
                    top.add("&c#" + pos + " &7| &f" + value + " &a- " + name);
                });
                ChatUtils.sendMessage(sender, top);
                return true;
            }
        }
        ChatUtils.sendMessage(sender, "&cUsage: &7/top <stat>");
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            String search = args[0];
            for (Statistic<?> statistic: manager.getStatistics()) {
                if (statistic.getId().startsWith(search)) {
                    result.add(statistic.getId());
                }
            }
        }
        return result;
    }

}
