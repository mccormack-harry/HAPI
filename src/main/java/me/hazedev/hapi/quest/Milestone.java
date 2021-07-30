package me.hazedev.hapi.quest;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.chat.Formatter;
import me.hazedev.hapi.event.LongStatisticIncrementEvent;
import me.hazedev.hapi.stats.LongStatistic;
import me.hazedev.hapi.player.data.PlayerDataManager;
import me.hazedev.hapi.player.data.property.IntegerProperty;
import me.hazedev.hapi.player.data.property.LongProperty;
import me.hazedev.hapi.player.data.property.Property;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public abstract class Milestone implements Listener {

    QuestManager manager;

    private final LongStatistic statistic;
    private final String id;
    private final Property<Integer> awardedTierProperty;
    private final Property<Long> startProgressProperty;
    private final String name;
    private final Tier[] tiers;

    public Milestone(LongStatistic statistic) {
        this.statistic = statistic;
        this.id = statistic.getId();
        this.awardedTierProperty = new IntegerProperty("tier");
        this.startProgressProperty = new LongProperty("progress");
        this.name = statistic.getName();
        this.tiers = createTiers();
    }

    protected abstract Tier[] createTiers();

    public Tier[] getTiers() {
        return tiers;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return name;
    }

    public int getAchievedTier(Player player) {
        return getCurrentTier(player) - 1;
    }

    public int getCurrentTier(Player player) {
        long progress = getProgress(player);
        int currentTier = 1;
        for (Tier tier: tiers) {
            if (progress >= tier.getGoal()) {
                currentTier++;
            } else {
                break;
            }
        }
        return currentTier;
    }

    private String getPlayerdataPath() {
        return manager.getId() + "." + getId();
    }

    private int getAwardedTier(Player player) {
        return manager.verifyHardDependency(PlayerDataManager.class).getPlayerData(player).getProperty(getPlayerdataPath(), awardedTierProperty, 0);
    }

    private void setAwardedTier(Player player, int tier) {
        manager.verifyHardDependency(PlayerDataManager.class).getPlayerData(player).setProperty(getPlayerdataPath(), awardedTierProperty, tier);
    }

    private Long getStartProgress(Player player) {
        return manager.verifyHardDependency(PlayerDataManager.class).getPlayerData(player).getProperty(getPlayerdataPath(), startProgressProperty, null);
    }

    private void setStartProgress(Player player, long progress) {
        if (getStartProgress(player) == null) {
            manager.verifyHardDependency(PlayerDataManager.class).getPlayerData(player).setProperty(getPlayerdataPath(), startProgressProperty, progress);
        }
    }

    public Tier getTier(Player player) {
        return getTier(getCurrentTier(player));
    }

    public Tier getTier(int value) throws IndexOutOfBoundsException {
       return tiers[value - 1];
    }

    public long getGoal(Player player) {
        return getTier(player).getGoal();
    }

    public String format(long value) {
        return Formatter.formatLong(value);
    }

    public long getProgress(Player player) {
        long progress = statistic.getRawValue(player.getUniqueId());
        Long startProgress = getStartProgress(player);
        if (startProgress != null) {
            progress -= startProgress;
        }
        return progress;
    }

    public void updateStatus(Player player) {
        setStartProgress(player, getProgress(player));
        int awardedTier = getAwardedTier(player);
        int achievedTier = getAchievedTier(player);
        if (awardedTier < achievedTier) {
            for (int intTierToAward = awardedTier + 1; intTierToAward <= achievedTier; intTierToAward++) {
                Tier tierToAward = getTier(intTierToAward);
                QuestManager.sendMessage(player, "You completed milestone &f" + getDisplayName() + " &7(" + intTierToAward + "/" + tiers.length + ") &rand received &f" + String.join(", ", tierToAward.getRewards()));
                tierToAward.onFinish(player);
            }
            setAwardedTier(player, achievedTier);
        }
    }

    public QuestStatus getQuestStatus(Player player) {
        if (getAchievedTier(player) < tiers.length) {
            return QuestStatus.ACTIVE;
        } else {
            return QuestStatus.FINISHED;
        }
    }

    public String[] getRewards(Player player) {
        return getTier(player).getRewards();
    }

    public List<String> getFormattedDescription(Player player) {
        return CCUtils.addColor(Formatter.splitLines(getDescription(player), 30, "&7"));
    }

    public abstract String getDescription(Player player);

    @EventHandler(ignoreCancelled = true)
    public void onLongStatisticIncrement(LongStatisticIncrementEvent event) {
        if (event.getStatistic() == statistic) {
            Player player = event.getPlayer();
            if (player != null)
                updateStatus(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(manager.getPlugin(), () -> updateStatus(event.getPlayer()), 20);
    }

    public abstract static class Tier {

        final long goal;
        final String[] rewards;

        public Tier(long goal, String[] rewards) {
            this.goal = goal;
            this.rewards = rewards;
        }

        public long getGoal() {
            return goal;
        }

        public String[] getRewards() {
            return rewards;
        }

        public abstract void onFinish(Player player);
    }

}
