package me.hazedev.hapi.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FirstJoinEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;

    public FirstJoinEvent(Player player) {
        this.player = player;
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
