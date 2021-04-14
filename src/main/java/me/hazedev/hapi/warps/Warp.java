package me.hazedev.hapi.warps;

import me.hazedev.hapi.chat.CCUtils;
import me.hazedev.hapi.chat.Formatter;
import me.hazedev.hapi.event.PlayerWarpEvent;
import me.hazedev.hapi.io.YamlFileHandler;
import me.hazedev.hapi.logging.Log;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.IOException;
import java.util.List;

public class Warp {

    final YamlFileHandler fileHandler;

    final String identifier;
    Material displayMaterial = Material.STONE;
    String description = null;
    int order = 56;
    boolean mine;

    String world;
    double x;
    double y;
    double z;
    float pitch = 0;
    float yaw = 0;

    public Warp(String identifier, YamlFileHandler fileHandler) {
        this.identifier = identifier;
        this.fileHandler = fileHandler;
    }

    public void loadFromConfig() {
        YamlConfiguration c = fileHandler.getConfiguration();

        if (c.isString("display-material")) {
            Material material = Material.getMaterial(c.getString("display-material").toUpperCase());
            if (material != null) {
                displayMaterial = material;
            }
        }
        
        description = c.getString("description", null);
        
        if (c.isInt("order")) {
            order = c.getInt("order");
        }

        mine = c.getBoolean("mine", false);
        
        if (c.isString("world")) {
            world = c.getString("world");
        } else {
            throw new IllegalArgumentException("world isn't set");
        }

        if (c.isDouble("x")) {
            x = c.getDouble("x");
        } else {
            throw new IllegalArgumentException("x isn't set");
        }

        if (c.isDouble("y")) {
            y = c.getDouble("y");
        } else {
            throw new IllegalArgumentException("y isn't set");
        }

        if (c.isDouble("z")) {
            z = c.getDouble("z");
        } else {
            throw new IllegalArgumentException("z isn't set");
        }

        if (c.isDouble("pitch")) {
            pitch = (float) c.getDouble("pitch");
        }

        if (c.isDouble("yaw")) {
            yaw = (float) c.getDouble("yaw");
        }
        
    }

    public void save() {
        YamlConfiguration c = fileHandler.getConfiguration();
        c.set("display-material", displayMaterial.name());
        c.set("description", description);
        c.set("order", order);
        c.set("world", world);
        c.set("x", x);
        c.set("y", y);
        c.set("z", z);
        c.set("pitch", pitch);
        c.set("yaw", yaw);
        try {
            fileHandler.saveConfig();
        } catch (IOException e) {
            Log.error(e);
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return WordUtils.capitalizeFully(identifier.replace("_", " "));
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    public List<String> getFormattedDescription() {
        if (description == null || description.isEmpty()) {
            return null;
        } else {
            return CCUtils.addColor(Formatter.splitLines(description, 30, "&7"));
        }
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(this.world);
        if (world != null) {
            return new Location(world, x, y, z, yaw, pitch);
        } else {
            Log.warning("Invalid world '" + this.world + "' for warp '" + identifier + "'");
        }
        return null;
    }

    public void teleport(Player player) {
        player.teleport(getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        Bukkit.getPluginManager().callEvent(new PlayerWarpEvent(player, this));
    }

    public void setLocation(Location location) {
        world = location.getWorld().getName();
        x = location.getX();
        y = location.getY();
        z = location.getZ();
        pitch = location.getPitch();
        yaw = location.getYaw();
    }

    public int getOrder() {
        return order;
    }

    public boolean isMine() {
        return mine;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

}
