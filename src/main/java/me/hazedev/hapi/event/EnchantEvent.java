package me.hazedev.hapi.event;


import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Triggered after an item is enchanted through PrisonReimagined
 */
public class EnchantEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final ItemStack itemStack;
    private final Enchantment enchantment;
    private final int oldLevel;
    private final int currentLevel;
    private final long tokenCost;
    private final long levelCost;

    public EnchantEvent(Player player, ItemStack itemStack, Enchantment enchantment, int oldLevel, int currentLevel, long tokenCost, long levelCost) {
        this.player = player;
        this.itemStack = itemStack;
        this.enchantment = enchantment;
        this.oldLevel = oldLevel;
        this.currentLevel = currentLevel;
        this.tokenCost = tokenCost;
        this.levelCost = levelCost;
    }

    public Player getPlayer() {
        return player;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public ItemStack getItem() {
        return itemStack;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public long getTokenCost() {
        return tokenCost;
    }

    public long getLevelCost() {
        return levelCost;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
