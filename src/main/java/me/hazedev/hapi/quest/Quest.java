package me.hazedev.hapi.quest;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.chat.Formatter;
import me.hazedev.hapi.event.QuestFinishedEvent;
import me.hazedev.hapi.event.QuestStartEvent;
import me.hazedev.hapi.player.data.PlayerData;
import me.hazedev.hapi.player.data.property.LongProperty;
import me.hazedev.hapi.player.data.property.Property;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class Quest {

    QuestManager manager;

    private final String id;
    private final String displayName;
    private final long goal;
    private final Predicate<Player> prerequisite;
    private final QuestPriority priority;

    private final Property<Long> progressProperty;
    private final Property<Long> finishedTimeProperty;

    public Quest(String id, String displayName, long goal, Predicate<Player> prerequisite, QuestPriority priority) {
        this.id = Objects.requireNonNull(id, "id cannot be null").toLowerCase();
        this.displayName = displayName != null && !displayName.isEmpty() ? CCUtils.addColor(QuestManager.COLOR + displayName) : CCUtils.WHITE + WordUtils.capitalizeFully(id.replace("-", " "));
        this.goal = goal;
        this.prerequisite = prerequisite == null ? player -> true : prerequisite;
        this.priority = priority == null ? QuestPriority.NORMAL : priority;

        this.progressProperty = new LongProperty("progress");
        this.finishedTimeProperty = new LongProperty("finished");
    }

    public final String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public final long getGoal() {
        return goal;
    }

    public final QuestPriority getPriority() {
        return priority;
    }

    private String getPath() {
        return manager.getId() + "." + getId();
    }

    public final Long getProgress(Player player) {
        return getProgress(player.getUniqueId());
    }

    public final Long getProgress(UUID uniqueId) {
        PlayerData playerData = manager.getPlayerdataManager().getPlayerData(uniqueId);
        return playerData.getProperty(getPath(), progressProperty);
    }

    private void setProgress(UUID uniqueId, Long progress) {
        manager.getPlayerdataManager().getPlayerData(uniqueId).setProperty(getPath(), progressProperty, progress);
    }

    public final Long getFinishedTime(Player player) {
        return getFinishedTime(player.getUniqueId());
    }

    public final Long getFinishedTime(UUID uniqueId) {
        PlayerData playerData = manager.getPlayerdataManager().getPlayerData(uniqueId);
        return playerData.getProperty(getPath(), finishedTimeProperty);
    }

    private void setFinishedTime(UUID uniqueId, Long finishTime) {
        manager.getPlayerdataManager().getPlayerData(uniqueId).setProperty(getPath(), finishedTimeProperty, finishTime);
    }

    public final boolean checkPrerequisites(Player player) {
        return prerequisite.test(player);
    }

    public void updateStatus(Player player) {
        QuestStatus questStatus = getQuestStatus(player);
        if (questStatus == QuestStatus.AVAILABLE) {
            start(player);
        } else if (questStatus == QuestStatus.ACTIVE) {
            checkFinished(player);
        }
    }

    public QuestStatus getQuestStatus(UUID uniqueId) {
        Long finishedTime = getFinishedTime(uniqueId);
        if (finishedTime != null) {
            return QuestStatus.FINISHED;
        } else {
            Long progress = getProgress(uniqueId);
            if (progress != null) {
                return QuestStatus.ACTIVE;
            } else {
                return QuestStatus.UNAVAILABLE;
            }
        }
    }

    public QuestStatus getQuestStatus(Player player) {
        Long finishedTime = getFinishedTime(player);
        if (finishedTime != null) {
            return QuestStatus.FINISHED;
        } else {
            Long progress = getProgress(player);
            if (progress != null) {
                return QuestStatus.ACTIVE;
            } else {
                return checkPrerequisites(player) ? QuestStatus.AVAILABLE : QuestStatus.UNAVAILABLE;
            }
        }
    }

    private void start(Player player) {
        setProgress(player.getUniqueId(), 0L);
        onStart(player);
        Bukkit.getPluginManager().callEvent(new QuestStartEvent(player, this));
    }

    private void checkFinished(Player player) {
        if (getProgress(player) >= goal) {
            QuestManager.sendMessage(player, getFinishMessage());
            setFinishedTime(player.getUniqueId(), System.currentTimeMillis());
            setProgress(player.getUniqueId(), null);
            onFinish(player);
            Bukkit.getPluginManager().callEvent(new QuestFinishedEvent(player, this));
        }
    }

    public final void addProgress(Player player, long amount) {
        QuestStatus status = getQuestStatus(player);
        if (status == QuestStatus.ACTIVE || status == QuestStatus.AVAILABLE) {
            addProgress(player.getUniqueId(), amount);
            updateStatus(player);
        }
    }

    public final void addProgress(UUID uniqueId, long amount) {
        QuestStatus status = getQuestStatus(uniqueId);
        if (status == QuestStatus.ACTIVE) {
            setProgress(uniqueId, getProgress(uniqueId) + amount);
        }
    }

    public final void setFinished(Player player) {
        QuestStatus status = getQuestStatus(player);
        if (status == QuestStatus.ACTIVE || status == QuestStatus.AVAILABLE) {
            setFinished(player.getUniqueId());
            updateStatus(player);
        }
    }

    public void setFinished(UUID uniqueId) {
        QuestStatus status = getQuestStatus(uniqueId);
        if (status == QuestStatus.ACTIVE) {
            setProgress(uniqueId, goal);
        }
    }

    public String[] getRewards() {
        return new String[0];
    }

    public final List<String> getFormattedDescription() {
        return CCUtils.addColor(Formatter.splitLines(getDescription(), 30, "&7"));
    }

    public abstract String getDescription();

    public String getFinishMessage() {
        return "You have completed quest: " + getDisplayName();
    }

    public abstract void onStart(Player player);

    public abstract void onFinish(Player player);

}
