package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class StringProperty extends PrimitiveProperty<String> {

    public StringProperty(String id) {
        super(id);
    }

    @Override
    public String fromJsonElement(@NotNull JsonElement element) {
        return element.getAsJsonPrimitive().getAsString();
    }

    @Override
    public JsonPrimitive toJsonElement(@NotNull String value) {
        return new JsonPrimitive(value);
    }

}
