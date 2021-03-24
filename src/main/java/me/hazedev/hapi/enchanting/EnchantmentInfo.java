package me.hazedev.hapi.enchanting;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public interface EnchantmentInfo {

    String getId();

    String getFullId();

    String getName();

    default String getDisplayName() {
        return applyColor(getName());
    }

    String getDescription();

    ChatColor getDisplayColor();

    String applyColor(String text);

    int getStartLevel();

    int getMaxLevel();

    Material getDisplayMaterial();

    /**
     * @param level
     * @return Cost to upgrade to next level
     */
    long getCost(int level);

    /**
     * @param level Current level
     * @param levelsToAdd Levels to add
     * @return total cost
     */
    default long getCost(int level, int levelsToAdd) {
        long cost = 0;
        for (int i = 0; i < levelsToAdd; i++) {
            cost += getCost(level + i);
        }
        return cost;
    }

    default int getLevelsToAdd(int level, long balance) {
        int max = getMaxLevel() - level;
        long totalCost = 0;
        for (int i = 0; i < max; i++) {
            totalCost += getCost(level + i);
            if (balance < totalCost) {
                return i;
            }
        }
        return max;
    }

    static EnchantmentInfo getInfoFor(Enchantment enchantment) {
        if (enchantment instanceof EnchantmentInfo) {
            return (EnchantmentInfo) enchantment;
        } else {
            return VanillaEnchantmentInfo.getByEnchantment(enchantment);
        }
    }

}
