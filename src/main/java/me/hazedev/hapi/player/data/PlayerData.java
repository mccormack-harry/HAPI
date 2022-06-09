package me.hazedev.hapi.player.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.hazedev.hapi.player.data.property.Property;
import me.hazedev.hapi.player.data.property.StringProperty;
import me.hazedev.hapi.player.data.property.UUIDProperty;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerData {

    public static final Property<UUID> UNIQUE_ID = new UUIDProperty("uuid");
    public static final Property<String> NAME = new StringProperty("name");

    private final JsonObject root;

    public PlayerData(Player player) {
        root = new JsonObject();
        setProperty(UNIQUE_ID, player.getUniqueId());
        setProperty(NAME, player.getName());
    }

    public PlayerData(JsonObject root) {
        this.root = root;
    }

    public JsonObject getRoot() {
        return root;
    }

    public JsonObject getJsonObject(@Nullable String path, boolean create) {
        JsonObject pathObject = root;
        if (path != null) {
            for (String subPath : StringUtils.split(path, '.')) {
                JsonElement subPathElement = pathObject.get(subPath);
                if (subPathElement != null) {
                    pathObject = subPathElement.getAsJsonObject();
                } else {
                    if (create) {
                        JsonObject subPathObject = new JsonObject();
                        pathObject.add(subPath, subPathObject);
                        pathObject = subPathObject;
                    } else {
                        return null;
                    }
                }
            }
        }
        return pathObject;
    }

    public <T> T getProperty(@Nullable String path, Property<T> property, T defaultValue) {
        JsonObject pathObject = getJsonObject(path, false);
        if (pathObject != null) {
            JsonElement value = pathObject.get(property.getId());
            if (value != null) {
                return property.fromJsonElement(value);
            }
        }
        return defaultValue;
    }

    public <T> T getProperty(@Nullable String path, @NotNull Property<T> property) {
        return getProperty(path, property, null);
    }

    public <T> T getProperty(@NotNull Property<T> property, T defaultValue) {
        return getProperty(null, property, defaultValue);
    }

    public <T> T getProperty(@NotNull Property<T> property) {
        return getProperty(null, property, null);
    }

    public <T> void setProperty(@Nullable String path, @NotNull Property<T> property, @Nullable T value) {
        JsonObject pathObject = getJsonObject(path, true);
        if (value != null) {
            pathObject.add(property.getId(), property.toJsonElement(value));
        } else {
            pathObject.remove(property.getId());
        }
    }

    public <T> void setProperty(@NotNull Property<T> property, @Nullable T value) {
        setProperty(null, property, value);
    }

    public void unsetProperty(@Nullable String path, @NotNull Property<?> property) {
        unsetProperty(path, property.getId());
    }

    public void unsetProperty(@NotNull Property<?> property) {
        unsetProperty(property.getId());
    }

    public void unsetProperty(@NotNull String id) {
        unsetProperty(null, id);
    }

    public void unsetProperty(@Nullable String path, @NotNull String id) {
        JsonObject pathObject = getJsonObject(path, false);
        if (pathObject != null) {
            pathObject.remove(id);
        }
    }

    public UUID getUniqueId() {
        return getProperty(UNIQUE_ID);
    }

    public String getName() {
        return getProperty(NAME);
    }

}
