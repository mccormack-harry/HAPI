package me.hazedev.hapi.essentials;

import me.hazedev.hapi.command.CommandHelper;
import me.hazedev.hapi.component.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class Disposal extends Component implements Listener {

    public Disposal() {
        super("disposal");
        registerCommand(new DisposalCommand());
    }

    public void openDisposalMenu(HumanEntity player) {
        Inventory inventory = Bukkit.createInventory(new DisposalInventoryHolder(), 54, "Disposal");
        player.openInventory(inventory);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder tempHolder = inventory.getHolder();
        if (tempHolder instanceof DisposalInventoryHolder) {
            DisposalInventoryHolder disposalInventoryHolder = (DisposalInventoryHolder) inventory.getHolder();
            disposalInventoryHolder.preventClose = true;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        WeakReference<HumanEntity> playerReference = new WeakReference<>(event.getPlayer());
        Inventory inventory = event.getInventory();
        InventoryHolder tempHolder = inventory.getHolder();
        if (tempHolder instanceof DisposalInventoryHolder) {
            DisposalInventoryHolder disposalInventoryHolder = (DisposalInventoryHolder) tempHolder;
            if (disposalInventoryHolder.preventClose) {
                Bukkit.getScheduler().runTask(getPlugin(), () -> {
                    HumanEntity player = playerReference.get();
                    if (player != null) {
                        openDisposalMenu(player);
                    }
                });
            }
        }
    }

    private class DisposalCommand extends Command {

        protected DisposalCommand() {
            super(Disposal.this.getId(), "Dispose of unwanted items", "/" + Disposal.this.getId(), Arrays.asList("bin", "trash"));
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] strings) {
            Player player = CommandHelper.validatePlayer(sender);
            if (player != null) {
                if (CommandHelper.checkPermission(sender, getName() + ".use")) {
                    Disposal.this.openDisposalMenu(player);
                }
            }
            return true;
        }
    }

    private static class DisposalInventoryHolder implements InventoryHolder {

        public boolean preventClose = false;

        @Deprecated
        @NotNull
        @Override
        public Inventory getInventory() {
            throw new UnsupportedOperationException("Attempted to get inventory of " + getClass().getName());
        }

    }

}
