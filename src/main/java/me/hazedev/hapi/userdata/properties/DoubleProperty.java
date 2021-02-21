package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class DoubleProperty extends PrimitiveProperty<Double> {

    public DoubleProperty(String id) {
        super(id);
    }

    @Override
    public Double fromJsonElement(JsonElement element) {
        return element.getAsJsonPrimitive().getAsDouble();
    }

    @Override
    public JsonPrimitive toJsonElement(Double value) {
        return new JsonPrimitive(value);
    }

}
