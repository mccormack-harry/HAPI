package me.hazedev.hapi.inventory;

import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.event.EnchantEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class DynamicItems extends Component implements Listener {

    Set<ItemUpdater> itemUpdaters = new HashSet<>(0);

    public DynamicItems() {
        super("dynamic-items");
    }

    public void registerItemUpdater(ItemUpdater itemUpdater) {
        itemUpdaters.add(itemUpdater);
    }

    public void updateItem(ItemStack... items) {
        for (ItemStack itemStack: items) {
            if (itemStack != null) {
                for (ItemUpdater itemUpdater : itemUpdaters) {
                    itemUpdater.updateItem(itemStack);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Entity whoClicked = event.getWhoClicked();
        if (whoClicked instanceof Player) {
            Player player = (Player) whoClicked;
            itemUpdaters.forEach(itemUpdater -> itemUpdater.onInventoryClick(event));
            int clickedSlot = event.getRawSlot();
            Bukkit.getScheduler().runTask(getPlugin(), () -> {
                updateItem(player.getItemOnCursor());
                updateItem(player.getOpenInventory().getItem(clickedSlot));
                player.updateInventory();
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchant(EnchantEvent event) {
        updateItem(event.getItem());
        event.getPlayer().updateInventory();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        Bukkit.getScheduler().runTask(getPlugin(), () -> {
            updateItem(event.getItem());
            event.getPlayer().updateInventory();
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        updateItem(event.getPlayer().getInventory().getItemInMainHand());
        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            updateItem(event.getItem().getItemStack());
            ((Player) entity).updateInventory();
        }
    }

}
