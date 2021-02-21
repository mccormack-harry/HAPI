package me.hazedev.hapi.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface ItemUpdater {

    void updateItem(ItemStack itemStack);

    default void onInventoryClick(InventoryClickEvent event) {}

}
