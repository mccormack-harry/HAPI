package me.hazedev.hapi.stats.stat;

import me.hazedev.hapi.chat.Formatter;
import me.hazedev.hapi.stats.LongStatistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayTimeStatistic extends LongStatistic implements Listener {

    Map<UUID, Long> sessions = new HashMap<>(0);

    public PlayTimeStatistic() {
        super("playtime", true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID player = event.getPlayer().getUniqueId();
        sessions.put(player, System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID player = event.getPlayer().getUniqueId();
        set(player, getPlayTime(player));
    }

    public long getPlayTime(UUID uniqueId) {
        long now = System.currentTimeMillis();
        long sessionLength = now - sessions.get(uniqueId);
        return super.getRawValue(uniqueId) + sessionLength; // Super will get playtime from memory
    }

    @Override
    public Long getRawValue(UUID uniqueId) { // Don't get playtime from memory, get Live play time.
        return getPlayTime(uniqueId);
    }

    @Override
    public String format(Long value, boolean isShort) {
        return Formatter.formatTimeRemaining(value, 3);
    }

}
