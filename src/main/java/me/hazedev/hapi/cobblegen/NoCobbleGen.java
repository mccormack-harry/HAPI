package me.hazedev.hapi.cobblegen;

import me.hazedev.hapi.component.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class NoCobbleGen extends Component implements Listener {

    public NoCobbleGen() {
        super("no-cobblegen");
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Material material = event.getToBlock().getType();
        if (material == Material.COBBLESTONE || material == Material.STONE || material == Material.OBSIDIAN) {
            event.setCancelled(true);
        }
    }

}
