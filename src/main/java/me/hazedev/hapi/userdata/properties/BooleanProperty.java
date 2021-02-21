package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanProperty extends PrimitiveProperty<Boolean> {

    public BooleanProperty(String id) {
        super(id);
    }

    @Override
    public Boolean fromJsonElement(JsonElement element) {
        return element.getAsJsonPrimitive().getAsBoolean();
    }

    @Override
    public JsonPrimitive toJsonElement(Boolean value) {
        return new JsonPrimitive(value);
    }

}
