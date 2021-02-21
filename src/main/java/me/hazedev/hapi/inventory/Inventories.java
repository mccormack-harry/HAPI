package me.hazedev.hapi.inventory;

import me.hazedev.hapi.event.InventoryAddItemsEvent;
import me.hazedev.hapi.event.InventoryOverflowEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Inventories {

    public static void addItemToInventory(@NotNull Inventory inventory, @NotNull ItemStack... items) {
        Bukkit.getPluginManager().callEvent(new InventoryAddItemsEvent(inventory, items));
        Map<Integer, ItemStack> overflow = inventory.addItem(items);
        if (!overflow.isEmpty()) {
            Bukkit.getPluginManager().callEvent(new InventoryOverflowEvent(inventory, overflow.values()));
        }
    }



    public static void addItemToInventory(Player player, boolean update, ItemStack... items) {
        addItemToInventory(player.getInventory(), items);
        if (update) player.updateInventory();
    }

}
