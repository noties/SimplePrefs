package ru.noties.simpleprefs.sample;

import android.content.Context;

import java.util.Date;
import java.util.List;
import java.util.Set;

import ru.noties.simpleprefs.annotations.Key;
import ru.noties.simpleprefs.annotations.Preference;
import ru.noties.simpleprefs.obj.PrefsObject;
import ru.noties.simpleprefs.sample.obj.ColorObject;
import ru.noties.simpleprefs.sample.obj.ColorObjectSerializer;
import ru.noties.simpleprefs.sample.obj.DateSerializer;
import ru.noties.simpleprefs.sample.obj.GenericObject;
import ru.noties.simpleprefs.sample.obj.PaletteObject;
import ru.noties.simpleprefs.sample.obj.PaletteObjectSerializer;

/**
 * Created by Dimitry Ivanov on 06.06.2015.
 */
@Preference(
        isSingleton = true,
        catchJsonExceptions = true,
        jsonTypes = { Date.class, ColorObject.class, PaletteObject.class },
        jsonTypeSerializers = {DateSerializer.class, ColorObjectSerializer.class, PaletteObjectSerializer.class }
)
public class PrefWithJsonSerialization extends PrefsObject {


    @Key(isJson = true)
    private Date someDate;

    @Key(isJson = true)
    private PaletteObject[] paletteObjects;

    @Key(isJson = true)
    private List<String> someStringList;

    @Key(isJson = true)
    private Set<String> someStringSet;

    @Key(isJson = true)
    private GenericObject<ColorObject> genericObject;

    public PrefWithJsonSerialization(Context context, String name) {
        super(context, name);
    }

    public Date getSomeDate() {
        return someDate;
    }

    public void setSomeDate(Date someDate) {
        this.someDate = someDate;
    }

    public PaletteObject[] getPaletteObjects() {
        return paletteObjects;
    }

    public void setPaletteObjects(PaletteObject[] paletteObjects) {
        this.paletteObjects = paletteObjects;
    }

    public List<String> getSomeStringList() {
        return someStringList;
    }

    public void setSomeStringList(List<String> someStringList) {
        this.someStringList = someStringList;
    }

    public Set<String> getSomeStringSet() {
        return someStringSet;
    }

    public void setSomeStringSet(Set<String> someStringSet) {
        this.someStringSet = someStringSet;
    }

    public GenericObject<ColorObject> getGenericObject() {
        return genericObject;
    }

    public void setGenericObject(GenericObject<ColorObject> genericObject) {
        this.genericObject = genericObject;
    }
}
