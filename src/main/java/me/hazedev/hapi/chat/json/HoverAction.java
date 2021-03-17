package me.hazedev.hapi.chat.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.hazedev.hapi.ReflectionUtils;
import me.hazedev.hapi.logging.Log;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class HoverAction {

    final String action;
    final JsonElement contents;

    public HoverAction(String action, JsonElement contents) {
        this.action = action;
        this.contents = contents;
    }

    public static HoverAction showText(JsonElement contents) {
        return new HoverAction("show_text", contents);
    }

    public static HoverAction showText(String contents) {
        return new HoverAction("show_text", new JsonComponent(contents).serialize());
    }

    public static HoverAction showItem(ItemStack itemStack) {
        return new HoverAction("show_item", ItemStackSerializer.serialize(itemStack));
    }

    public JsonObject serialize() {
        JsonObject serialized = new JsonObject();
        serialized.addProperty("action", action);
        serialized.add("contents", contents);
        return serialized;
    }

    @Override
    public String toString() {
        return serialize().toString();
    }

    private static class ItemStackSerializer {

        private static final Constructor<?> NBT_TAG_COMPOUND_CONSTRUCTOR;

        private static final Method AS_NMS_COPY;
        private static final Method SAVE;

        static {
            Class<?> craftItemStack = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack");
            AS_NMS_COPY = ReflectionUtils.getMethod(craftItemStack, "asNMSCopy", ItemStack.class);

            Class<?> nmsItemStack = ReflectionUtils.getNMSClass("ItemStack");
            Class<?> nbtTagCompound = ReflectionUtils.getNMSClass("NBTTagCompound");

            NBT_TAG_COMPOUND_CONSTRUCTOR = ReflectionUtils.getConstructor(nbtTagCompound);
            SAVE = ReflectionUtils.getMethod(nmsItemStack, "save", nbtTagCompound);
        }


        public static JsonObject serialize(ItemStack itemStack) {
            String tag = null;
            try {
                tag = SAVE.invoke(AS_NMS_COPY.invoke(null, itemStack), NBT_TAG_COMPOUND_CONSTRUCTOR.newInstance()).toString();
            } catch (Exception e) {
                Log.error(e);
            }
            Log.debug(tag);
            JsonObject jsonItem = new JsonObject();
            jsonItem.addProperty("id", itemStack.getType().name().toLowerCase());
            jsonItem.addProperty("count", itemStack.getAmount());
            if (tag != null) {
                jsonItem.addProperty("tag", tag);
            }
            return jsonItem;
        }

    }

}
