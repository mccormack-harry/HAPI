package me.hazedev.hapi.enchanting;

import me.hazedev.hapi.chat.CCUtils;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;


public class CustomEnchantment extends Enchantment implements EnchantmentInfo {

    private static final String NAMESPACE = "cenchantment";

    EnchantmentManager manager;

    final String description;
    final ChatColor mainColour;
    final ChatColor secondaryColor;
    final Material displayMaterial;
    final EnchantmentTarget[] targets;
    final CostAlgorithm costAlgorithm;
    final int startLevel = 1;
    final int maxLevel;

    public CustomEnchantment(String id, String description, ChatColor mainColour, ChatColor secondaryColour, Material displayMaterial, EnchantmentTarget[] targets, CostAlgorithm costAlgorithm, int maxLevel) {
        super(new NamespacedKey(NAMESPACE, id));
        this.description = description;
        this.mainColour = mainColour;
        this.secondaryColor = secondaryColour;
        this.displayMaterial = displayMaterial;
        this.targets = targets;
        this.costAlgorithm = costAlgorithm;
        this.maxLevel = maxLevel;
    }

    public CustomEnchantment(String id, String description, ChatColor mainColour, ChatColor secondaryColour, Material displayMaterial, EnchantmentTarget target, CostAlgorithm costAlgorithm, int maxLevel) {
        this(id, description, mainColour, secondaryColour, displayMaterial, new EnchantmentTarget[]{target}, costAlgorithm, maxLevel);
    }

    public CustomEnchantment(String id, String description, ChatColor displayColor, Material displayMaterial, EnchantmentTarget[] targets, CostAlgorithm costAlgorithm, int maxLevel) {
        this(id, description, displayColor, displayColor, displayMaterial, targets, costAlgorithm, maxLevel);
    }

    public CustomEnchantment(String id, String description, ChatColor displayColor, Material displayMaterial, EnchantmentTarget target, CostAlgorithm costAlgorithm, int maxLevel) {
        this(id, description, displayColor, displayColor, displayMaterial, target, costAlgorithm, maxLevel);
    }

    public boolean onEnable() {
        return true;
    }

    protected EnchantmentManager getManager() {
        return manager;
    }

    @Override
    public String getId() {
        return getKey().getKey();
    }

    @Override
    public String getFullId() {
        return getKey().toString();
    }

    @Override
    public String getName() {
        return WordUtils.capitalizeFully(getId().replace('_', ' '));
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ChatColor getDisplayColor() {
        return mainColour;
    }

    public ChatColor getSecondaryColor() {
        return secondaryColor;
    }

    @Override
    public String applyColor(String text) {
        if (mainColour == secondaryColor) {
            return mainColour + text;
        } else {
            return CCUtils.applyFade(text, mainColour, secondaryColor);
        }
    }

    @Override
    public long getCost(int level) {
        return costAlgorithm.getCost(level);
    }

    @Override
    public int getStartLevel() {
        return startLevel;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    public boolean isTarget(EnchantmentTarget target) {
        for (EnchantmentTarget included: targets) {
            if (target == included) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        for (EnchantmentTarget target: targets) {
            if (target.includes(itemStack)) {
                return true;
            }
        }
        return false;
    }

    @Override @Deprecated
    public org.bukkit.enchantments.EnchantmentTarget getItemTarget() {
        return org.bukkit.enchantments.EnchantmentTarget.ALL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }
}
