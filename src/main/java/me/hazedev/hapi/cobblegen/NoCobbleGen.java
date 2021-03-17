package me.hazedev.hapi.cobblegen;

import me.hazedev.hapi.component.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

// https://stackoverflow.com/questions/44096564/how-would-i-detect-cobblestone-being-generated-with-bukkit
public class NoCobbleGen extends Component implements Listener {

    public NoCobbleGen() {
        super("no-cobblegen");
    }

    private final BlockFace[] faces = new BlockFace[] {BlockFace.SELF, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Material type = event.getBlock().getType();
        if (type == Material.WATER || type == Material.LAVA){
            Block block = event.getToBlock();
            if (block.getType() == Material.AIR) {
                    event.setCancelled(generatesStone(type, block));
            }
        }
    }

    public boolean generatesStone(Material type, Block block) {
        Material mirrorType = (type == Material.WATER ? Material.LAVA : Material.WATER);
        for (BlockFace face : faces) {
            Block adjacent = block.getRelative(face, 1);
            if (adjacent.getType() == mirrorType) {
                return true;
            }
        }
        return false;
    }

}
