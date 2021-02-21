package me.hazedev.hapi.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class InventoryOverflowEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Inventory inventory;
    private final Collection<ItemStack> overflowItems;

    public InventoryOverflowEvent(Inventory inventory, Collection<ItemStack> overflowItems) {
        this.inventory = inventory;
        this.overflowItems = overflowItems;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Collection<ItemStack> getOverflowItems() {
        return overflowItems;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
