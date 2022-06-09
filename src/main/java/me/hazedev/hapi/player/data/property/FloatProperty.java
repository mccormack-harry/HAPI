package me.hazedev.hapi.player.data.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class FloatProperty extends PrimitiveProperty<Float> {

    public FloatProperty(String id) {
        super(id);
    }

    @Override
    public Float fromJsonElement(@NotNull JsonElement element) {
        return null;
    }

    @Override
    public JsonPrimitive toJsonElement(@NotNull Float value) {
        return null;
    }

}
