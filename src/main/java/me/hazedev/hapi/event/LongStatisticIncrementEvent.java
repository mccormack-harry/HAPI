package me.hazedev.hapi.event;

import me.hazedev.hapi.stats.LongStatistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LongStatisticIncrementEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final LongStatistic statistic;
    private final Player player;

    public LongStatisticIncrementEvent(LongStatistic statistic, Player player) {
        this.statistic = statistic;
        this.player = player;
    }

    public LongStatistic getStatistic() {
        return statistic;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
