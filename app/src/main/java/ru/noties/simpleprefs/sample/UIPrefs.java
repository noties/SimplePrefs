package ru.noties.simpleprefs.sample;

import android.content.Context;

import ru.noties.simpleprefs.annotations.Key;
import ru.noties.simpleprefs.annotations.Preference;
import ru.noties.simpleprefs.obj.PrefsObject;

/**
 * Created by Dimitry Ivanov on 30.05.2015.
 */

@Preference
public class UIPrefs extends PrefsObject {

    @Key(defaultValue = "I'm a no value placeholder")
    private String someString;

    @Key(name = "some_int")
    private int someInt;

    @Key(defaultValue = "100L")
    private long someLong;

    @Key(defaultValue = "1.F")
    private float someFloat;

    @Key
    private boolean someBoolean;

    protected UIPrefs(Context context, String name) {
        super(context, name);
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    public int getSomeInt() {
        return someInt;
    }

    public void setSomeInt(int someInt) {
        this.someInt = someInt;
    }

    public long getSomeLong() {
        return someLong;
    }

    public void setSomeLong(long someLong) {
        this.someLong = someLong;
    }

    public float getSomeFloat() {
        return someFloat;
    }

    public void setSomeFloat(float someFloat) {
        this.someFloat = someFloat;
    }

    public boolean isSomeBoolean() {
        return someBoolean;
    }

    public void setSomeBoolean(boolean someBoolean) {
        this.someBoolean = someBoolean;
    }
}
