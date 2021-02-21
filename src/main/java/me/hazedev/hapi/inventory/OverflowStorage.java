package me.hazedev.hapi.inventory;

import me.hazedev.hapi.component.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OverflowStorage extends Component {

    public OverflowStorage() {
        super("overflow-storage");
    }

    @Override
    public boolean onEnable() {
        return super.onEnable();
    }

    @Override
    public void save() {
        super.save();
    }

    public void addItem(UUID uuid, ItemStack... itemStack) {

    }

    public void addItem(Player player, ItemStack... itemStack) {

    }

}
