package me.hazedev.hapi.inventory;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.enchanting.EnchantmentInfo;
import me.hazedev.hapi.enchanting.EnchantmentManager;
import me.hazedev.hapi.logging.Log;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// TODO implement serialization!
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ItemBuilder {

    private String name = null;
    private Material material = Material.STONE;
    private int amount = 1;
    private List<String> lore = null;
    private Map<Enchantment, Integer> enchantments = new HashMap<>(0);
    private Set<ItemFlag> flags = new HashSet<>(0);
    private boolean glow = false;
    @SuppressWarnings("rawtypes")
    private final Set<PersistantTag> tags = new HashSet<>(0);


    public ItemBuilder() {}

    public ItemBuilder(ItemStack itemStack) {
        material = itemStack.getType();
        amount = itemStack.getAmount();
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                if (itemMeta.hasDisplayName()) {
                    name = itemMeta.getDisplayName();
                }
                if (itemMeta.hasLore()) {
                    lore = itemMeta.getLore();
                }
                if (itemMeta.hasEnchants()) {
                    enchantments = itemMeta.getEnchants();
                }
                flags = itemMeta.getItemFlags();
            } else {
                Log.warning(null, "ItemMeta is null after hasItemMeta check???");
            }
        }
    }

    public ItemBuilder(ConfigurationSection section) {
        name = section.getString("name");
        material = Objects.requireNonNull(Material.getMaterial(section.getString("material")), "Invalid material");
        amount = section.getInt("amount");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be a positive integer");
        }
        glow = section.getBoolean("glow", false);
        lore = section.getStringList("lore");
        if (lore.isEmpty()) lore = null;
        if (section.isConfigurationSection("enchantments")) {
            ConfigurationSection enchantments = section.getConfigurationSection("enchantments");
            for (String key: enchantments.getKeys(false)) {
                Enchantment enchantment = EnchantmentManager.getEnchantment(key);
                if (enchantment != null) {
                    int level = enchantments.getInt(key);
                    if (level > 0)
                        this.enchantments.put(enchantment, level);
                }
            }
        }
        List<String> flags = section.getStringList("flags");
        for (String flag: flags) {
            this.flags.add(ItemFlag.valueOf(flag));
        }

    }

//    public ItemBuilder(JsonObject jsonObject) {} TODO json deserialization

    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder material(Material material) {
        if (material != null)
            this.material = material;
        return this;
    }


    public ItemBuilder amount(int amount) {
//        this.amount = Math.min(64, Math.abs(amount));
        this.amount = Math.abs(amount);
        return this;
    }


    public ItemBuilder lore(String... loreArray) {
        if (loreArray != null && loreArray.length != 0) {
            this.lore = Arrays.asList(loreArray);
        }
        return this;
    }


    public ItemBuilder lore(List<String> lore) {
        if (lore != null && !lore.isEmpty()) {
            this.lore = lore;
        }
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (enchantment != null && level >= 0) {
            enchantments.put(enchantment, level);
        }
        return this;
    }

    public ItemBuilder hideFlag(ItemFlag flag) {
        if (flag != null && !flags.contains(flag)) {
            this.flags.add(flag);
        }
        return this;
    }

    public ItemBuilder hideAllFlags() {
        return this.hideFlag(ItemFlag.HIDE_ENCHANTS)
                .hideFlag(ItemFlag.HIDE_ATTRIBUTES)
                .hideFlag(ItemFlag.HIDE_UNBREAKABLE)
                .hideFlag(ItemFlag.HIDE_DESTROYS)
                .hideFlag(ItemFlag.HIDE_PLACED_ON)
                .hideFlag(ItemFlag.HIDE_POTION_EFFECTS)
                .hideFlag(ItemFlag.HIDE_DYE);
    }

    public ItemBuilder glow(boolean glow) {
        this.glow = glow;
        return this;
    }

    public <T, Z> ItemBuilder addPersistentTag(NamespacedKey namespacedKey, PersistentDataType<T, Z> type, Z value) {
        this.tags.add(new PersistantTag<>(namespacedKey, type, value));
        return this;
    }

    public ItemStack build() {
        ItemStack result = new ItemStack(material, amount);
        ItemMeta resultMeta = result.getItemMeta();
        if (resultMeta != null) {
            if (this.name != null) {
                resultMeta.setDisplayName(CCUtils.addColor(this.name));
            }
            if (this.lore != null) {
                resultMeta.setLore(CCUtils.addColor(this.lore));
            }
            if (this.enchantments != null && !this.enchantments.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> entry : this.enchantments.entrySet()) {
                    resultMeta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
            }
            if (this.flags != null && !this.flags.isEmpty()) {
                resultMeta.addItemFlags(flags.toArray(new ItemFlag[0]));
            }
            if (this.glow) {
                if (this.enchantments != null && this.enchantments.isEmpty()) {
                    resultMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    resultMeta.addEnchant(Enchantment.LURE, 1, false);
                }
            }
            if (!this.tags.isEmpty()) {
                PersistentDataContainer container = resultMeta.getPersistentDataContainer();
                for (@SuppressWarnings("rawtypes") PersistantTag tag: this.tags) {
                    //noinspection unchecked
                    container.set(tag.getNamespacedKey(), tag.getType(), tag.getValue());
                }
            }
            result.setItemMeta(resultMeta);
        }
        return result;
    }

    // TODO JSON SERIALIZATION
    public ConfigurationSection toConfigurationSection(ConfigurationSection section) {
        section.set("name", name);
        section.set("material", material.name());
        section.set("amount", amount);
        section.set("lore", lore);
        section.set("glow", glow);
        if (!this.flags.isEmpty()) {
            List<String> flags = new ArrayList<>(0);
            for (ItemFlag flag: this.flags) {
                flags.add(flag.name());
            }
            section.set("flags", flags);
        }
        if (!this.enchantments.isEmpty()) {
            Map<String, Integer> enchantments = new HashMap<>(0);
            for (Map.Entry<Enchantment, Integer> enchantment: this.enchantments.entrySet()) {
                enchantments.put(EnchantmentInfo.getInfoFor(enchantment.getKey()).getFullId(), enchantment.getValue());
            }
            section.set("enchantments", enchantments);
        }
        // TODO persistant tags
        return section;
    }

    public static <T, Z> void addPersistentTag(ItemStack itemStack, @NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> type, Z value) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            container.set(namespacedKey, type, value);
            itemStack.setItemMeta(itemMeta);
        }
    }

    public static <T> T getPersistentTag(ItemStack itemStack, @NotNull NamespacedKey key, @NotNull PersistentDataType<?, T> type) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
                return persistentDataContainer.get(key, type);
            }
        }
        return null;
    }

    // Inventory util items

    public static ItemStack getBackItem() {
        return new ItemBuilder()
                .name("&c&l<- Back")
                .material(Material.GRAY_STAINED_GLASS_PANE)
                .build();
    }

    public static ItemStack getExitItem() {
        return new ItemBuilder()
                .name("&c&lExit")
                .material(Material.GRAY_STAINED_GLASS_PANE)
                .build();
    }

    private static class PersistantTag<T, Z> {

        private final NamespacedKey namespacedKey;
        private final PersistentDataType<T, Z> type;
        private final Z value;

        public PersistantTag(NamespacedKey namespacedKey, PersistentDataType<T, Z> type, Z value) {
            this.namespacedKey = namespacedKey;
            this.type = type;
            this.value = value;
        }

        public NamespacedKey getNamespacedKey() {
            return namespacedKey;
        }

        public PersistentDataType<T, Z> getType() {
            return type;
        }

        public Z getValue() {
            return value;
        }
    }

}
