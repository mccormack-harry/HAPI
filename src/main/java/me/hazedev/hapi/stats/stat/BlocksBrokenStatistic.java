
package me.hazedev.hapi.stats.stat;

import me.hazedev.hapi.stats.LongStatistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlocksBrokenStatistic extends LongStatistic implements Listener {

    public BlocksBrokenStatistic() {
        super("blocks_broken", false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        increment(event.getPlayer().getUniqueId(), 1L);
    }

}
