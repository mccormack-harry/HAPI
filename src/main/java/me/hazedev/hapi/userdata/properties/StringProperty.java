package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class StringProperty extends PrimitiveProperty<String> {

    public StringProperty(String id) {
        super(id);
    }

    @Override
    public String fromJsonElement(JsonElement element) {
        return element.getAsJsonPrimitive().getAsString();
    }

    @Override
    public JsonPrimitive toJsonElement(String value) {
        return new JsonPrimitive(value);
    }

}
