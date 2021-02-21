package me.hazedev.hapi.chat.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemFlag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * Serializes an item stack for json storage
 * Follows structure documented here: https://minecraft.gamepedia.com/Player.dat_format#Item_structure
 *
 */
public class JsonSerializer {

    public static JsonObject serialize(Map<String, Object> object) {

        JsonObject serialized = new JsonObject();

        for (Map.Entry<String, Object> entry: object.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            addProperty(serialized, key, value);
        }

        return serialized;
    }

    public static Map<String, Object> deserialize(JsonObject jsonObject) throws NoSuchMethodException {

        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {

        }

        return null;
    }

    public static JsonObject addProperty(JsonObject object, String key, Object value) {
        if (value instanceof ConfigurationSerializable) {
            object.add(key, serialize(((ConfigurationSerializable) value).serialize()));
        } else if (value instanceof Boolean) {
            object.addProperty(key, (Boolean) value);
        } else if (value instanceof Number) {
            object.addProperty(key, (Number) value);
        } else if (value instanceof Character) {
            object.addProperty(key, (Character) value);
        } else if (value instanceof String) {
            object.addProperty(key, (String) value);
        }
        return object;
    }

    public static int calculateItemFlags(Set<ItemFlag> flags) {
        int hideFlags = 0;
        for (ItemFlag itemFlag: flags) {
            switch (itemFlag) {
                case HIDE_ENCHANTS:
                    hideFlags += 1;
                    continue;
                case HIDE_ATTRIBUTES:
                    hideFlags += 2;
                    continue;
                case HIDE_UNBREAKABLE:
                    hideFlags += 4;
                    continue;
                case HIDE_DESTROYS:
                    hideFlags += 8;
                    continue;
                case HIDE_PLACED_ON:
                    hideFlags += 16;
                    continue;
                case HIDE_POTION_EFFECTS:
                    hideFlags += 32;
                    continue;
                case HIDE_DYE:
                    hideFlags += 64;
                    continue;
            }
        }
        return hideFlags;
    }

}
