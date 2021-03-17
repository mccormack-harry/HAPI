package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

public class DoubleProperty extends PrimitiveProperty<Double> {

    public DoubleProperty(String id) {
        super(id);
    }

    @Override
    public Double fromJsonElement(@NotNull JsonElement element) {
        return element.getAsJsonPrimitive().getAsDouble();
    }

    @Override
    public JsonPrimitive toJsonElement(@NotNull Double value) {
        return new JsonPrimitive(value);
    }

}
