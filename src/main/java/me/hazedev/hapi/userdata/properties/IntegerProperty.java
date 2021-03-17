package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class IntegerProperty extends PrimitiveProperty<Integer> {

    public IntegerProperty(String id) {
        super(id);
    }

    @Override
    public Integer fromJsonElement(@NotNull JsonElement element) {
        return element.getAsJsonPrimitive().getAsInt();
    }

    @Override
    public JsonPrimitive toJsonElement(@NotNull Integer value) {
        return new JsonPrimitive(value);
    }

}
