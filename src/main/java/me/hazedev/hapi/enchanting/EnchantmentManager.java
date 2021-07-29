package me.hazedev.hapi.enchanting;

import me.hazedev.hapi.component.Component;
import me.hazedev.hapi.enchanting.toggle.EnchantToggleCommand;
import me.hazedev.hapi.enchanting.toggle.ToggleableEnchantment;
import me.hazedev.hapi.event.EnchantEvent;
import me.hazedev.hapi.inventory.DynamicItems;
import me.hazedev.hapi.logging.Log;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantmentManager {

    private static final List<CustomEnchantment> enchantments = new ArrayList<>(0);

    private final Component component;
    private final EnchantmentLoreUpdater loreUpdater;
    private boolean acceptingNew = false;

    public EnchantmentManager(Component component) {
        this.component = component;
        loreUpdater = new EnchantmentLoreUpdater(this);
        DynamicItems dynamicItems = getComponent().verifySoftDependency(DynamicItems.class);
        if (dynamicItems != null) {
            dynamicItems.registerItemUpdater(loreUpdater);
        }
    }

    public Component getComponent() {
        return component;
    }

    public void registerEnchantment(@NotNull CustomEnchantment enchantment) {
        enchantment.manager = this;
        if (enchantment.onEnable()) {
            enchantments.add(enchantment);
            try {
                if (!acceptingNew) {
                    Field f = Enchantment.class.getDeclaredField("acceptingNew");
                    f.setAccessible(true);
                    f.set(null, true);
                    acceptingNew = true;
                }
                Enchantment.registerEnchantment(enchantment);
            } catch (Exception e) {
                Log.error(component, e, "Failed to register enchantment: " + enchantment.getName());
                return;
            }
            if (enchantment instanceof Listener) {
                component.registerListener((Listener) enchantment);
            }
            if (enchantment instanceof ToggleableEnchantment) {
                component.registerCommand(new EnchantToggleCommand<>((EnchantmentInfo & ToggleableEnchantment) enchantment));
            }
        }
    }

    /**
     * @param id Enchantment ID
     * @return CEnchantment or vanilla Enchantment or null, in that order.
     */
    public static Enchantment getEnchantment(String id) {
        if (id.contains(":")) {
            String[] fullId = id.split(":");
            if (fullId.length != 2) {
                throw new IllegalArgumentException("Invalid id format: " + id);
            }
            String namespace = fullId[0];
            String key = fullId[1];
            NamespacedKey namespacedKey;
            if ("minecraft".equalsIgnoreCase(namespace)) {
                namespacedKey =  NamespacedKey.minecraft(key);
            } else {
                namespacedKey = new NamespacedKey(namespace, key);
            }
            return Enchantment.getByKey(namespacedKey);
        } else {
            CustomEnchantment customEnchantment = getCEnchantment(id);
            if (customEnchantment != null) {
                return customEnchantment;
            } else {
                return Enchantment.getByKey(NamespacedKey.minecraft(id));
            }
        }
    }

    public static CustomEnchantment getCEnchantment(String id) {
        for (CustomEnchantment enchantment: enchantments) {
            if (id.equalsIgnoreCase(enchantment.getId())) {
                return enchantment;
            }
        }
        return null;
    }

    public List<Enchantment> getEnchantments(EnchantmentTarget target) {
        List<Enchantment> result = new ArrayList<>(0);
        result.addAll(Arrays.asList(target.getVanillaEnchantments())); // Vanilla Enchants
        for (CustomEnchantment enchantment: enchantments) {
            if (enchantment.isTarget(target)) {
                result.add(enchantment);
            }
        }
        return result;
    }

    public int getEnchantmentLevel(ItemStack itemStack, Enchantment enchantment) {
        ItemMeta itemMeta;
        if (enchantment != null && itemStack != null && (itemMeta = itemStack.getItemMeta()) != null) {
            return itemMeta.getEnchantLevel(enchantment);
        }
        return 0;
    }

    public int getEnchantmentLevel(Player player, Enchantment enchantment) {
        return getEnchantmentLevel(player.getInventory().getItemInMainHand(), enchantment);
    }

    public int getEnchantmentLevel(ItemStack itemStack, String enchantmentId) {
        Enchantment enchantment = getEnchantment(enchantmentId);
        return getEnchantmentLevel(itemStack, enchantment);
    }

    public int getEnchantmentLevel(Player player, String enchantmentId) {
        return getEnchantmentLevel(player.getInventory().getItemInMainHand(), enchantmentId);
    }

    public void addEnchantment(Player player, ItemStack item, Enchantment enchantment, int level, long tokenCost, long levelCost) {
        if (item == null) {
            if (player != null) {
                item = player.getInventory().getItemInMainHand();
            } else {
                throw new NullPointerException("item & player can't both be null");
            }
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta is null");
        int oldLevel = meta.getEnchantLevel(enchantment);
        if (level <= 0) {
            meta.removeEnchant(enchantment);
        } else {
            meta.addEnchant(enchantment, level, true);
        }
        item.setItemMeta(meta);
        Bukkit.getPluginManager().callEvent(new EnchantEvent(player, item, enchantment, oldLevel, level, tokenCost, levelCost));
        if (player != null) player.updateInventory();
    }



}
