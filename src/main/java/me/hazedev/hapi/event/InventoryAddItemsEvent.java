package me.hazedev.hapi.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryAddItemsEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Inventory inventory;
    private final ItemStack[] itemStack;

    public InventoryAddItemsEvent(Inventory inventory, ItemStack... itemStack) {
        this.inventory = inventory;
        this.itemStack = itemStack;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack[] getItemStack() {
        return itemStack;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
