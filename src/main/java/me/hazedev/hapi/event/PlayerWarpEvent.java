package me.hazedev.hapi.event;

import me.hazedev.hapi.warps.Warp;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerWarpEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Warp warp;

    public PlayerWarpEvent(Player player, Warp warp) {
        this.player = player;
        this.warp = warp;
    }

    public Player getPlayer() {
        return player;
    }

    public Warp getWarp() {
        return warp;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
