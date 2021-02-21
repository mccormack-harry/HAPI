package me.hazedev.hapi.event;

import me.hazedev.hapi.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuestFinishedEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    final Player player;
    final Quest quest;

    public QuestFinishedEvent(Player player, Quest quest) {
        this.player = player;
        this.quest = quest;
    }

    public Player getPlayer() {
        return player;
    }

    public Quest getQuest() {
        return quest;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
