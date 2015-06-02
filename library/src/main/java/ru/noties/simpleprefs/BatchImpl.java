package ru.noties.simpleprefs;

/**
 * Created by Dimitry Ivanov on 08.05.2015.
 */
public class BatchImpl implements Batch {

    private final SimplePref mPref;

    public BatchImpl(SimplePref pref) {
        this.mPref = pref;
    }

    @Override
    public Batch set(String key, String value) {
        mPref.setBatch(key, value);
        return this;
    }

    @Override
    public Batch set(String key, int value) {
        mPref.setBatch(key, value);
        return this;
    }

    @Override
    public Batch set(String key, long value) {
        mPref.setBatch(key, value);
        return this;
    }

    @Override
    public Batch set(String key, float value) {
        mPref.setBatch(key, value);
        return this;
    }

    @Override
    public Batch set(String key, boolean value) {
        mPref.setBatch(key, value);
        return this;
    }

    @Override
    public void apply() {
        mPref.apply();
    }
}
