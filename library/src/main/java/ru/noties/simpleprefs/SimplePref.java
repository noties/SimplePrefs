package ru.noties.simpleprefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Simple wrapper around {@link SharedPreferences}
 * The main goal was to reduce boilerplate code aka prefs.edit().putSomeThing().apply();
 * Now we have only two basic methods get() & set()
 * We overload it with value type. So to get a string from SharedPreference we call {@code pref.get("key", null)}
 * (we can specify <i>null</i> as a default value for a string as long as it is the only object type supported by SimplePref)
 *
 * <h3>String</h3>
 * {@code
 *     get(key) & get(key, String) // get("key", "some def value");
 *     set(key, String) // set("key", "some value");
 * }
 *
 * <h3>int</h3>
 * <code>
 *     get(key, int) // get("key", -1);
 *     set(key, int) // set("key", 101);
 * </code>
 *
 * <h3>long</h3>
 * <code>
 *     get(key, long) // get("key", -1L); (note `L`)
 *     set(key, long) // set("key", 1001L);
 * </code>
 *
 * <h3>float</h3>
 * <code>
 *     get(key, float) // get("key", -1.F); (note `F`)
 *     set(key, float) // set("key", 10.5F);
 * </code>
 *
 * <h3>boolean</h3>
 * <code>
 *     get(key, boolean) // get("key", false);
 *     set(key, boolean) // set("key", true);
 * </code>
 *
 * Note that after each call to `set()` the editor will immediately call `apply()`.
 * In order to <i>batch</i> setting values there is a {@link #batch()} method. See {@link Batch}
 *
 * @see SharedPreferences
 *
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

    /**
     * @param key key for a preference value
     * @return string value stored with a <i>key</i> or null if not present
     */
    public String get(String key) {
        return mPrefs.getString(key, null);
    }

    /**
     * @param key key for a preference value
     * @param defValue to return if preferences have no value stored with specified <i>key</i>
     * @return string value stored with a <i>key</i> or <i>defValue</i> if not present
     */
    public String get(String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }

    /**
     * @param key key for a preference value
     * @param defValue to return if preferences have no value stored with specified <i>key</i>
     * @return int value stored with a <i>key</i> or <i>defValue</i> if not present
     */
    public int get(String key, int defValue) {
        return mPrefs.getInt(key, defValue);
    }

    /**
     * @param key key for a preference value
     * @param defValue to return if preferences have no value stored with specified <i>key</i>
     * @return long value stored with a <i>key</i> or <i>defValue</i> if not present
     */
    public long get(String key, long defValue) {
        return mPrefs.getLong(key, defValue);
    }

    /**
     * @param key key for a preference value
     * @param defValue to return if preferences have no value stored with specified <i>key</i>
     * @return float value stored with a <i>key</i> or <i>defValue</i> if not present
     */
    public float get(String key, float defValue) {
        return mPrefs.getFloat(key, defValue);
    }

    /**
     * @param key key for a preference value
     * @param defValue to return if preferences have no value stored with specified <i>key</i>
     * @return boolean value stored with a <i>key</i> or <i>defValue</i> if not present
     */
    public boolean get(String key, boolean defValue) {
        return mPrefs.getBoolean(key, defValue);
    }

    /**
     * @param key key for a preference value
     * @param value value to be stored
     */
    public void set(String key, String value) {
        mEditor.putString(key, value).apply();
    }

    /**
     * @param key key for a preference value
     * @param value value to be stored
     */
    public void set(String key, int value) {
        mEditor.putInt(key, value).apply();
    }

    /**
     * @param key key for a preference value
     * @param value value to be stored
     */
    public void set(String key, long value) {
        mEditor.putLong(key, value).apply();
    }

    /**
     * @param key key for a preference value
     * @param value value to be stored
     */
    public void set(String key, float value) {
        mEditor.putFloat(key, value).apply();
    }

    /**
     * @param key key for a preference value
     * @param value value to be stored
     */
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

    /**
     * Clears wrapped preferences.
     * @see SharedPreferences.Editor#clear()
     */
    public void clear() {
        mEditor.clear().apply();
    }

    /**
     * @return {@link Batch} implementation to batch setting values
     */
    public Batch batch() {
        return new BatchImpl(this);
    }

    /**
     * @return wrapped {@link SharedPreferences}
     * @see SharedPreferences
     */
    public SharedPreferences getWrappedSharedPreferences() {
        return mPrefs;
    }

    /**
     * @return wrapped {@link android.content.SharedPreferences.Editor}
     * @see android.content.SharedPreferences.Editor
     */
    public SharedPreferences.Editor getWrapperSharedPreferencesEditor() {
        return mEditor;
    }

    /**
     * @return name linked with this preference file
     */
    public String getSharedPreferencesName() {
        return mName;
    }
}
