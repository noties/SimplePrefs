package ru.noties.simpleprefs;

/**
 * An interface to chain calls to {@link SimplePref} set()
 * without applying to {@link android.content.SharedPreferences.Editor}.
 * After you are done with setting values do not forget to call {@link #apply()}
 *
 * Created by Dimitry Ivanov on 08.05.2015.
 */
public interface Batch {

    Batch set(String key, String value);
    Batch set(String key, int value);
    Batch set(String key, long value);
    Batch set(String key, float value);
    Batch set(String key, boolean value);

    void apply();
}
