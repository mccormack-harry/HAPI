package me.hazedev.hapi.stats;

import me.hazedev.hapi.chat.ChatUtils;
import me.hazedev.hapi.userdata.UserDataManager;
import me.hazedev.hapi.validation.Validators;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandStats extends Command {

    private final AbstractStatisticManager manager;

    public CommandStats(AbstractStatisticManager manager) {
        super("stats", "View a players stats", "/stats [player]", Collections.emptyList());
        this.manager = manager;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        OfflinePlayer player = null;
        if (args.length > 0) {
            player = manager.verifyHardDependency(UserDataManager.class).getOfflinePlayer(args[0]);
        }
        if (player == null && sender instanceof Player) {
            player = (OfflinePlayer) sender;
        }
        if (player != null) {
            List<String> stats = new ArrayList<>(0);
            stats.add("&a--- &2&lStats &a-- &f" + player.getName() + " &a---");
            for (Statistic<?> statistic : manager.getStatistics()) {
                stats.add("&a" + statistic.getName() + ": &f" + statistic.getValue(player.getUniqueId(), false));
            }
            ChatUtils.sendMessage(sender, stats);
        } else {
            ChatUtils.sendMessage(sender, "&cUsage: &7/stats [player]");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Validators.PLAYER.getPossibleValues(args[0]);
        }
        return null;
    }

}
