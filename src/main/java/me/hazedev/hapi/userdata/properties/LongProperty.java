package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class LongProperty extends PrimitiveProperty<Long> {

    public LongProperty(String id) {
        super(id);
    }

    @Override
    public Long fromJsonElement(@NotNull JsonElement element) {
        return element.getAsJsonPrimitive().getAsLong();
    }

    @Override
    public JsonPrimitive toJsonElement(@NotNull Long value) {
        return new JsonPrimitive(value);
    }

}
