package me.hazedev.hapi.enchanting;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum EnchantmentTarget {

    PICKAXE {
        @Override
        public boolean includes(Material material) {
            return material == Material.NETHERITE_PICKAXE || material == Material.DIAMOND_PICKAXE || material == Material.GOLDEN_PICKAXE || material == Material.IRON_PICKAXE || material == Material.STONE_PICKAXE || material == Material.WOODEN_PICKAXE;
        }
        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.DIG_SPEED};
        }
    },
    AXE {
        @Override
        public boolean includes(Material material) {
            return material == Material.NETHERITE_AXE || material == Material.DIAMOND_AXE || material == Material.GOLDEN_AXE || material == Material.IRON_AXE || material == Material.STONE_AXE || material == Material.WOODEN_AXE;
        }
        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH};
        }
    },
    SHOVEL {
        @Override
        public boolean includes(Material material) {
            return material == Material.NETHERITE_SHOVEL || material == Material.DIAMOND_SHOVEL || material == Material.GOLDEN_SHOVEL || material == Material.IRON_SHOVEL || material == Material.STONE_SHOVEL || material == Material.WOODEN_SHOVEL;
        }
        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH};
        }
    },
    HOE {
        @Override
        public boolean includes(Material material) {
            return material == Material.NETHERITE_HOE || material == Material.DIAMOND_HOE || material == Material.GOLDEN_HOE || material == Material.IRON_HOE || material == Material.STONE_HOE || material == Material.WOODEN_HOE;
        }
        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.DIG_SPEED, Enchantment.SILK_TOUCH};
        }
    },
    SWORD {
        @Override
        public boolean includes(Material material) {
            return material == Material.NETHERITE_SWORD || material == Material.DIAMOND_SWORD || material == Material.GOLDEN_SWORD || material == Material.IRON_SWORD || material == Material.STONE_SWORD || material == Material.WOODEN_SWORD;
        }
        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.DAMAGE_ALL, Enchantment.FIRE_ASPECT, Enchantment.LOOT_BONUS_MOBS, Enchantment.DURABILITY};
        }
    },
    HELMET {
        @Override
        public boolean includes(Material material) {
            return material == Material.NETHERITE_HELMET || material == Material.DIAMOND_HELMET || material == Material.GOLDEN_HELMET || material == Material.IRON_HELMET || material == Material.CHAINMAIL_HELMET || material == Material.LEATHER_HELMET;
        }
        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_PROJECTILE};
        }
    },
    CHESTPLATE {
        @Override
        public boolean includes(Material material) {
            return material == Material.NETHERITE_CHESTPLATE || material == Material.DIAMOND_CHESTPLATE || material == Material.GOLDEN_CHESTPLATE || material == Material.IRON_CHESTPLATE || material == Material.CHAINMAIL_CHESTPLATE || material == Material.LEATHER_CHESTPLATE;
        }
        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS};
        }
    },
    LEGGINGS {
        @Override
        public boolean includes(Material material) {
            return material == Material.NETHERITE_LEGGINGS || material == Material.DIAMOND_LEGGINGS || material == Material.GOLDEN_LEGGINGS || material == Material.IRON_LEGGINGS || material == Material.CHAINMAIL_LEGGINGS || material == Material.LEATHER_LEGGINGS;
        }
        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FIRE};
        }
    },
    BOOTS {
        @Override
        public boolean includes(Material material) {
            return material == Material.NETHERITE_BOOTS || material == Material.DIAMOND_BOOTS || material == Material.GOLDEN_BOOTS || material == Material.IRON_BOOTS || material == Material.CHAINMAIL_BOOTS || material == Material.LEATHER_BOOTS;
        }
        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_FALL};
        }
    },
    BOW {
        @Override
        public boolean includes(Material material) {
            return material == Material.BOW;
        }
        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.ARROW_DAMAGE, Enchantment.ARROW_KNOCKBACK, Enchantment.ARROW_INFINITE, Enchantment.ARROW_FIRE};
        }
    },
    CROSSBOW {
        @Override
        public boolean includes(Material material) {
            return material == Material.CROSSBOW;
        }
    },
    FISHING_ROD {
        @Override
        public boolean includes(Material material) {
            return material == Material.FISHING_ROD;
        }
    },
    TRIDENT {
        @Override
        public boolean includes(Material material) {
            return material == Material.TRIDENT;
        }
    },
    SHIELD {
        @Override
        public boolean includes(Material material) {
            return material == Material.SHIELD;
        }
    },
    ELYTRA {
        @Override
        public boolean includes(Material material) {
            return material == Material.ELYTRA;
        }

        @Override
        public Enchantment[] getVanillaEnchantments() {
            return new Enchantment[]{Enchantment.DURABILITY};
        }
    };

    public static final EnchantmentTarget[] ALL = values();
    public static final EnchantmentTarget[] TOOLS = new EnchantmentTarget[]{PICKAXE, AXE, SHOVEL, HOE};
    public static final EnchantmentTarget[] ARMOR = new EnchantmentTarget[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS};
    public static final EnchantmentTarget[] BOWS = new EnchantmentTarget[]{BOW, CROSSBOW};

    public String getName() {
        return WordUtils.capitalizeFully(name());
    }

    public static EnchantmentTarget get(Material material) {
        for (EnchantmentTarget enchantmentTarget: values()) {
            if (enchantmentTarget.includes(material)) {
                return enchantmentTarget;
            }
        }
        return null;
    }

    public boolean includes(Material material) {
        return false;
    }

    public boolean includes(ItemStack itemStack) {
        return includes(itemStack.getType());
    }

    public Enchantment[] getVanillaEnchantments() {
        return new Enchantment[0];
    }

}
