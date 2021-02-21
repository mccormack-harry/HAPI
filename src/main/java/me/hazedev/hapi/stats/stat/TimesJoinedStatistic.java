package me.hazedev.hapi.stats.stat;

import me.hazedev.hapi.stats.LongStatistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TimesJoinedStatistic extends LongStatistic implements Listener {

    public TimesJoinedStatistic() {
        super("times_joined", true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        increment(event.getPlayer().getUniqueId(), 1L);
    }

}
