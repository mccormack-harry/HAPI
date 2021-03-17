package me.hazedev.hapi.userdata.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
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
    public List<T> fromJsonElement(@NotNull JsonElement arrayAsElement) {
        JsonArray array = arrayAsElement.getAsJsonArray();
        List<T> result = new ArrayList<>();
        for (JsonElement element: array) {
            if (element != null && !(element instanceof JsonNull)) {
                result.add(converter.fromJsonElement(element));
            } else {
                result.add(null);
            }
        }
        return result;
    }

    @Override
    public JsonArray toJsonElement(@NotNull List<T> value) {
        JsonArray array = new JsonArray();
        value.stream().map(value1 -> value1 == null ? JsonNull.INSTANCE : converter.toJsonElement(value1)).forEach(array::add);
        return array;
    }

}
