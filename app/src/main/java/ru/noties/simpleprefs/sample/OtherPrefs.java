package ru.noties.simpleprefs.sample;

import android.content.Context;

import ru.noties.simpleprefs.annotations.Getter;
import ru.noties.simpleprefs.annotations.Key;
import ru.noties.simpleprefs.annotations.OnUpdate;
import ru.noties.simpleprefs.annotations.Preference;
import ru.noties.simpleprefs.annotations.Setter;
import ru.noties.simpleprefs.obj.PrefsObject;

/**
 * Created by Dimitry Ivanov on 01.06.2015.
 */
@Preference("other_pref")
public class OtherPrefs extends PrefsObject {

    public interface OnTokenChangedListener {
        void onTokenChange(String newToken);
    }

    @Key(name = "tkn")
    private String token;

    @Key(name = "ifl")
    private boolean isFirstLaunch;

    @Key(name = "lvc")
    private int lastVersionCode;

    @Key(name = "lvn")
    private String lastVersionName;

    @Key(name = "lst")
    private long lastSessionTime;

    private OnTokenChangedListener mOnTokenChangeListener;

    public OtherPrefs(Context context, String name) {
        super(context, name);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getLastVersionCode() {
        return lastVersionCode;
    }

    public void setLastVersionCode(int lastVersionCode) {
        this.lastVersionCode = lastVersionCode;
    }

    @Getter("ifl")
    public boolean isFirstLaunch() {
        return isFirstLaunch;
    }


    @Setter("ifl")
    public void setFirstLaunch(boolean isFirstLaunch) {
        this.isFirstLaunch = isFirstLaunch;
    }

    public String getLastVersionName() {
        return lastVersionName;
    }

    public void setLastVersionName(String lastVersionName) {
        this.lastVersionName = lastVersionName;
    }

    public long getLastSessionTime() {
        return lastSessionTime;
    }

    public void setLastSessionTime(long lastSessionTime) {
        this.lastSessionTime = lastSessionTime;
    }

    @OnUpdate("tkn")
    public void onTokenUpdate(String update) {
        if (mOnTokenChangeListener != null) {
            mOnTokenChangeListener.onTokenChange(update);
        }
    }


    @OnUpdate("lst")
    public void onLastSessionTimeUpdate(long value) {

    }

    public void setOnTokenChangeListener(OnTokenChangedListener mOnTokenChangeListener) {
        this.mOnTokenChangeListener = mOnTokenChangeListener;
    }
}
