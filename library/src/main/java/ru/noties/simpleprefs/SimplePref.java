package ru.noties.simpleprefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dimitry Ivanov on 08.05.2015.
 */
public class SimplePref {

    private final SharedPreferences mPrefs;
    private final SharedPreferences.Editor mEditor;

    private final String mName;

    @SuppressLint("CommitPrefEdits")
    public SimplePref(Context context, String name) {
        mName = name;
        mPrefs = context.getSharedPreferences(mName, Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    public String get(String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }

    public int get(String key, int defValue) {
        return mPrefs.getInt(key, defValue);
    }

    public long get(String key, long defValue) {
        return mPrefs.getLong(key, defValue);
    }

    public float get(String key, float defValue) {
        return mPrefs.getFloat(key, defValue);
    }

    public boolean get(String key, boolean defValue) {
        return mPrefs.getBoolean(key, defValue);
    }

    public void set(String key, String value) {
        mEditor.putString(key, value).apply();
    }

    public void set(String key, int value) {
        mEditor.putInt(key, value).apply();
    }

    public void set(String key, long value) {
        mEditor.putLong(key, value).apply();
    }

    public void set(String key, float value) {
        mEditor.putFloat(key, value).apply();
    }

    public void set(String key, boolean value) {
        mEditor.putBoolean(key, value).apply();
    }

    void setBatch(String key, String value) {
        mEditor.putString(key, value);
    }

    void setBatch(String key, int value) {
        mEditor.putInt(key, value);
    }

    void setBatch(String key, long value) {
        mEditor.putLong(key, value);
    }

    void setBatch(String key, float value) {
        mEditor.putFloat(key, value);
    }

    void setBatch(String key, boolean value) {
        mEditor.putBoolean(key, value);
    }

    void apply() {
        mEditor.apply();
    }

    public void clear() {
        mEditor.clear().apply();
    }

    public Batch batch() {
        return new BatchImpl(this);
    }

    public SharedPreferences getWrappedSharedPreferences() {
        return mPrefs;
    }

    public SharedPreferences.Editor getWrapperSharedPreferencesEditor() {
        return mEditor;
    }

    public String getSharedPreferencesName() {
        return mName;
    }
}
