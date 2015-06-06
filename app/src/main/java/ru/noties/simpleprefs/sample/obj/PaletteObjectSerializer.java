package ru.noties.simpleprefs.sample.obj;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import ru.noties.simpleprefs.sample.obj.PaletteObject;

/**
 * Created by Dimitry Ivanov on 06.06.2015.
 */
public class PaletteObjectSerializer implements JsonSerializer<PaletteObject>, JsonDeserializer<PaletteObject> {

    @Override
    public PaletteObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }

    @Override
    public JsonElement serialize(PaletteObject src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
