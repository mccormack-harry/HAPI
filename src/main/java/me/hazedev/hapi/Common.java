package me.hazedev.hapi;

import org.bukkit.Material;
import org.bukkit.block.Block;

// Common util methods
public class Common {

    public static Material getMaterialToDrop(Block block) {
        Material blockMaterial = block.getType();
        switch (blockMaterial) {
            case EMERALD_ORE:
                return Material.EMERALD;
            case DIAMOND_ORE:
                return Material.DIAMOND;
            case REDSTONE_ORE:
                return Material.REDSTONE;
            case COAL_ORE:
                return Material.COAL;
            case STONE:
                return Material.COBBLESTONE;
            default:
                return blockMaterial;
        }
    }

}
