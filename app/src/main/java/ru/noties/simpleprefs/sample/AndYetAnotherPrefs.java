package ru.noties.simpleprefs.sample;

import android.content.Context;

import ru.noties.simpleprefs.annotations.Key;
import ru.noties.simpleprefs.annotations.Preference;
import ru.noties.simpleprefs.obj.PrefsObject;

/**
 * Created by Dimitry Ivanov on 01.06.2015.
 */
@Preference
public class AndYetAnotherPrefs extends PrefsObject {

    @Key
    private String wrongString;


    @Key
    private boolean isBoolean;

    @Key
    private boolean someBool;

    @Key
    private boolean simpleBoolean;

    public AndYetAnotherPrefs(Context context, String name) {
        super(context, name);
    }

    public void setWrongString(String wrongString) {
        this.wrongString = wrongString;
    }

    public boolean isBoolean() {
        return isBoolean;
    }

    public void setIsBoolean(boolean isBoolean) {
        this.isBoolean = isBoolean;
    }

    public boolean isSomeBool() {
        return someBool;
    }

    public void setSomeBool(boolean someBool) {
        this.someBool = someBool;
    }

    public String getWrongString() {
        return wrongString;
    }

    public boolean getSimpleBoolean() {
        return simpleBoolean;
    }

    public void setSimpleBoolean(boolean simpleBoolean) {
        this.simpleBoolean = simpleBoolean;
    }

    @Preference("inner_preference")
    public static class InnerPrefs extends PrefsObject {

        @Key
        private String innerKey;

        @Key
        private long dateTime;

        public InnerPrefs(Context context, String name) {
            super(context, name);
        }

        public String getInnerKey() {
            return innerKey;
        }

        public void setInnerKey(String innerKey) {
            this.innerKey = innerKey;
        }

        public long getDateTime() {
            return dateTime;
        }

        public void setDateTime(long dateTime) {
            this.dateTime = dateTime;
        }
    }
}
