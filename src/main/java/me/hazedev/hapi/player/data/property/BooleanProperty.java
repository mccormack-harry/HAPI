package me.hazedev.hapi.player.data.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class BooleanProperty extends PrimitiveProperty<Boolean> {

    public BooleanProperty(String id) {
        super(id);
    }

    @Override
    public Boolean fromJsonElement(@NotNull JsonElement element) {
        return element.getAsJsonPrimitive().getAsBoolean();
    }

    @Override
    public JsonPrimitive toJsonElement(@NotNull Boolean value) {
        return new JsonPrimitive(value);
    }

}
