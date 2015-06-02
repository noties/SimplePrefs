package ru.noties.simpleprefs.sample;

import android.content.Context;

import java.util.Date;

import ru.noties.simpleprefs.annotations.Key;
import ru.noties.simpleprefs.annotations.Preference;
import ru.noties.simpleprefs.obj.PrefsObject;

/**
 * Created by Dimitry Ivanov on 01.06.2015.
 */
@Preference
public class WrongPrefs extends PrefsObject {

    @Key
    private String wrongString;

    public WrongPrefs(Context context, String name) {
        super(context, name);
    }

    public void setWrongString(String wrongString) {
        this.wrongString = wrongString;
    }


    public String getWrongString() {
        return wrongString;
    }
}
