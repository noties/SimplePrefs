package ru.noties.simpleprefs.sample.obj;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Dimitry Ivanov on 06.06.2015.
 */
public class ColorObjectSerializer
        implements JsonSerializer<ColorObject>, JsonDeserializer<ColorObject> {

    @Override
    public ColorObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(ColorObject src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
