package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class FloatProperty extends PrimitiveProperty<Float> {

    public FloatProperty(String id) {
        super(id);
    }

    @Override
    public Float fromJsonElement(JsonElement element) {
        return null;
    }

    @Override
    public JsonPrimitive toJsonElement(Float value) {
        return null;
    }

}
