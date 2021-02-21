package me.hazedev.hapi.enchanting;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.inventory.ItemUpdater;
import org.apache.commons.lang.WordUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EnchantmentLoreUpdater implements ItemUpdater {

    private final EnchantmentManager manager;

    public EnchantmentLoreUpdater(EnchantmentManager manager) {
        this.manager = manager;
    }

    public void updateItem(ItemStack item) {
        EnchantmentTarget target = EnchantmentTarget.get(item.getType());
        if (target != null && item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                List<String> lore = new ArrayList<>();
                // Enchants
                List<String> customEnchantments = new ArrayList<>();
                Set<Map.Entry<Enchantment, Integer>> enchantments;
                if (target == EnchantmentTarget.PICKAXE) {
                    Map<Enchantment, Integer> enchantmentsMap = new LinkedHashMap<>();
                    List<Enchantment> pickaxeEnchantments = manager.getEnchantments(EnchantmentTarget.PICKAXE);
                    for (Enchantment enchantment: pickaxeEnchantments) {
                        int level = itemMeta.getEnchantLevel(enchantment);
                        if (level > 0) enchantmentsMap.put(enchantment, level);
                    }
                    enchantments = enchantmentsMap.entrySet();
                } else {
                    enchantments = itemMeta.getEnchants().entrySet();
                }
                for (Map.Entry<Enchantment, Integer> ench : enchantments) {
                    int level = ench.getValue();
                    if (level > 0) {
                        String line;
                        Enchantment enchantment = ench.getKey();
                        EnchantmentInfo info = EnchantmentInfo.getInfoFor(enchantment);
                        if (info != null) {
                            String name = info.getName();
                            int maxLevel = info.getMaxLevel();
                            String strMaxLevel = CCUtils.DARK_GRAY;
                            if (level >= maxLevel) {
                                strMaxLevel += "MAX";
                            } else {
                                strMaxLevel += "/ " + maxLevel;
                            }
                            line = info.applyColor(name + " " + level) + " " + strMaxLevel;
                        } else {
                            String name = WordUtils.capitalizeFully(enchantment.getKey().getKey().replace("_", " "));
                            line = CCUtils.addColor("&a" + name + " " + level);
                        }
                        if (enchantment instanceof CustomEnchantment) {
                            customEnchantments.add(line);
                        } else {
                            lore.add(line);
                        }
                    }
                }
                lore.addAll(customEnchantments); // Add custom enchantments after vanilla

                // Durability
                if (itemMeta instanceof Damageable) {
                    Damageable damageable = (Damageable) itemMeta;
                    if (damageable.hasDamage()) {
                        int damage = damageable.getDamage();
                        int durability = item.getType().getMaxDurability();
                        String durabilityLore = CCUtils.addColor("&8(" + (durability - damage) + "/" + durability + ")");
                        lore.add("");
                        lore.add(durabilityLore);
                    }
                }
                // Update Item
                itemMeta.setLore(lore);
                itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON);
                item.setItemMeta(itemMeta);
            }
        }
    }

}
