package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListProperty<T> extends Property<List<T>> {

    private final PrimitiveProperty<T> converter;

    public ListProperty(String id, @NotNull PrimitiveProperty<T> converter) {
        super(id);
        this.converter = converter;
    }

    @Override
    public List<T> fromJsonElement(JsonElement arrayAsElement) {
        JsonArray array = arrayAsElement.getAsJsonArray();
        List<T> result = new ArrayList<>();
        for (JsonElement element: array) {
            result.add(converter.fromJsonElement(element));
        }
        return result;
    }

    @Override
    public JsonArray toJsonElement(List<T> value) {
        JsonArray array = new JsonArray();
        value.stream().map(converter::toJsonElement).forEach(array::add);
        return array;
    }

}
