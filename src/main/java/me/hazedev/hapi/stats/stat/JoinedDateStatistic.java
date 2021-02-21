package me.hazedev.hapi.stats.stat;

import me.hazedev.hapi.stats.LongStatistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;

public class JoinedDateStatistic extends LongStatistic implements Listener {

    public JoinedDateStatistic() {
        super("joined_date", true);
    }

    @Override
    public String getValue(UUID uniqueId, boolean isShort) {
        OffsetDateTime joinedDate = OffsetDateTime.ofInstant(Instant.ofEpochMilli(getRawValue(uniqueId)), ZoneId.systemDefault());
        return joinedDate.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        setIfAbsent(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public int compare(Long t1, Long t2) {
        return Math.negateExact(super.compare(t1, t2));
    }
}
