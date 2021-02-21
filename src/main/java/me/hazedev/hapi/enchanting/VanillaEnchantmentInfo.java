package me.hazedev.hapi.enchanting;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.HashSet;
import java.util.Set;

public class VanillaEnchantmentInfo implements EnchantmentInfo {

    private static final Set<VanillaEnchantmentInfo> VANILLA_ENCHANTMENT_INFO_SET = new HashSet<>();

    final NamespacedKey key;
    final String name;
    final ChatColor displayColor;
    final Material displayMaterial;
    final CostAlgorithm costAlgorithm;
    final int startLevel = 1;
    final int maxLevel;

    private VanillaEnchantmentInfo(NamespacedKey key, String name, ChatColor displayColor, Material displayMaterial, CostAlgorithm costAlgorithm, int maxLevel) {
        this.key = key;
        this.name = name;
        this.displayColor = displayColor;
        this.displayMaterial = displayMaterial;
        this.costAlgorithm = costAlgorithm;
        this.maxLevel = maxLevel;
    }

    public static void addInfoFor(Enchantment enchantment, String name, ChatColor displayColor, Material displayMaterial, CostAlgorithm costAlgorithm, int maxLevel) {
        VANILLA_ENCHANTMENT_INFO_SET.add(new VanillaEnchantmentInfo(enchantment.getKey(), name, displayColor, displayMaterial, costAlgorithm, maxLevel));
    }

    public static void addInfoFor(Enchantment enchantment, String name, ChatColor displayColor, Material displayMaterial, int maxLevel) {
        addInfoFor(enchantment, name, displayColor, displayMaterial, null, maxLevel);
    }

    public static void addInfoFor(Enchantment enchantment, String name, ChatColor displayColor, int maxLevel) {
        addInfoFor(enchantment, name, displayColor, null, null, maxLevel);
    }

    public static VanillaEnchantmentInfo getByKey(String key) {
        for (VanillaEnchantmentInfo info: VANILLA_ENCHANTMENT_INFO_SET) {
            if (info.getId().equalsIgnoreCase(key)) return info;
        }
        return null;
    }

    public static VanillaEnchantmentInfo getByEnchantment(Enchantment enchantment) {
        return getByKey(enchantment.getKey().getKey());
    }

    @Override
    public String getId() {
        return key.getKey();
    }

    @Override
    public String getFullId() {
        return key.toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Vanilla Enchantment";
    }

    @Override
    public ChatColor getDisplayColor() {
        return displayColor;
    }

    @Override
    public String applyColor(String text) {
        return getDisplayColor() + text;
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

    @Override
    public long getCost(int level) {
        return costAlgorithm.getCost(level);
    }
}
