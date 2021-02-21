package me.hazedev.hapi.quest;

import me.hazedev.hapi.event.QuestFinishedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class QuestUpdater implements Listener {

    private static final long COOLDOWN = 5 * 20;

    private final QuestManager manager;
    BukkitTask task = null;

    public QuestUpdater(QuestManager manager) {
        this.manager = manager;
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(manager::updateQuests);
        }
    };

    public void updateTaskState(int playerCount) {
        if (playerCount >= 1) {
            if (task == null) {
                task = Bukkit.getScheduler().runTaskTimer(manager.getPlugin(), runnable, 20, COOLDOWN);
            }
        } else {
            if (task != null) {
                task.cancel();
                task = null;
            }
        }
    }

    @EventHandler
    public void onQuestFinish(QuestFinishedEvent event) {
        manager.updateQuests(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateTaskState(Bukkit.getOnlinePlayers().size());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        updateTaskState(Bukkit.getOnlinePlayers().size() - 1);
    }

}
