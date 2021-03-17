package me.hazedev.hapi.stats;

import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.userdata.UserDataManager;
import me.hazedev.hapi.validation.Validators;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StatCommandHandler implements TabExecutor {

    private final AbstractStatisticManager manager;

    public StatCommandHandler(AbstractStatisticManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if ("stat".equalsIgnoreCase(command.getName())) {
            if (args.length > 0) {

                if (args.length > 1) {
                    if ("reset".equalsIgnoreCase(args[1]) && sender.hasPermission("stat.reset")) {
                        Statistic<?> statistic = manager.getStatistic(args[0]);
                        if (statistic != null) {
                            manager.reset(statistic);
                            ChatUtils.sendMessage(sender, "&aSuccess");
                        }
                        return true;
                    } else if ("set".equalsIgnoreCase(args[1]) && sender.hasPermission("stat.set")) {
                        Statistic<?> statistic = manager.getStatistic(args[0]);
                        if (statistic instanceof LongStatistic) {
                            if (args.length > 3) {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[2]);
                                if (offlinePlayer.hasPlayedBefore()) {
                                    ((LongStatistic) statistic).set(offlinePlayer.getUniqueId(), Validators.POSITIVE_LONG.validateValue(args[3]));
                                    ChatUtils.sendMessage(sender, "&aSuccess");
                                }
                            }
                        }
                        return true;
                    }
                }

                OfflinePlayer player = null;
                if (args.length > 1) {
                    player = manager.verifyHardDependency(UserDataManager.class).getOfflinePlayer(args[1]);
                }
                if (player == null && sender instanceof Player) {
                    player = (OfflinePlayer) sender;
                }
                if (player != null) {
                    Statistic<?> statistic = manager.getStatistic(args[0]);
                    if (statistic != null) {
                        String playerPrefix = "";
                        if (player != sender) {
                            playerPrefix = "&7(" + player.getName() + ") ";
                        }
                        ChatUtils.sendMessage(sender, playerPrefix + "&a" + statistic.getName() + ": &f" + statistic.getValue(player.getUniqueId(), false));
                        return true;
                    }
                }
            }
            ChatUtils.sendMessage(sender, "&cUsage: &7/stat <stat> [player]");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            List<String> result = new ArrayList<>();
            String search = args[args.length - 1];
            if (args.length == 1) {
                for (Statistic<?> statistic: manager.getStatistics()) {
                    if (statistic.getId().startsWith(search)) {
                        result.add(statistic.getId());
                    }
                }
            } else if (args.length == 2) {
                return Validators.PLAYER.getPossibleValues(search);
            }
            return result;
        }
        return null;
    }

}
