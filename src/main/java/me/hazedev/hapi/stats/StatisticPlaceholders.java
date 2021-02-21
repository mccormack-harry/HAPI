package me.hazedev.hapi.stats;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class StatisticPlaceholders extends PlaceholderExpansion {

    private final AbstractStatisticManager manager;

    public StatisticPlaceholders(AbstractStatisticManager manager) {
        this.manager = manager;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "stat";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "haz8989";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1";
    }

    @Override
    public boolean canRegister() {
        return !PlaceholderAPI.isRegistered(getIdentifier());
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player != null) {
            String statName = params;
            boolean isShort = true;
            if (params.endsWith("_long")) {
                isShort = false;
                statName = statName.replace("_long", "");
            }
            Statistic<?> statistic = manager.getStatistic(statName);
            if (statistic != null) {
                return statistic.getValue(player.getUniqueId(), isShort);
            }
        }
        return null;
    }

}
