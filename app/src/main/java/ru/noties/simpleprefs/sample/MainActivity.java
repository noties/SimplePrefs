package ru.noties.simpleprefs.sample;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import java.util.UUID;

import ru.noties.debug.Debug;
import ru.noties.simpleprefs.SimplePref;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Debug.init(true);

        showcaseSimple();

        showcaseBatch();

        showcaseOnUpdateListener();
    }

    private void showcaseSimple() {

        final SimplePref simplePref = new SimplePref(this, "simple");

        simplePref.set("key1", "value"); // string
        simplePref.set("key2", 1);       // int
        simplePref.set("key3", 1L);      // long
        simplePref.set("key4", 1.F);     // float
        simplePref.set("key5", true);    // boolean
    }

    private void showcaseBatch() {

        final SimplePref simplePref = new SimplePref(this, "batch");

        simplePref.batch()
                .set("bv", true)
                .set("iv", 42)
                .set("lv", 42L)
                .set("fv", .5F)
                .set("sv", null)
                .apply();
    }

    private void showcaseOnUpdateListener() {
        final Context context = getApplicationContext();
        final OtherPrefs[] prefs = new OtherPrefs[5];
        String prefsName = null;

        for (int i = 0, size = prefs.length; i < size; i++) {

            prefs[i] = OtherPrefs.create(OtherPrefs.class, context);

            if (prefsName == null) {
                prefsName = prefs[i].getWrappedPreferencesName();
            }

            final int currentIndex = i;
            prefs[i].setOnTokenChangeListener(new OtherPrefs.OnTokenChangedListener() {
                @Override
                public void onTokenChange(String newToken) {
                    Debug.i("pref #%d, value: %s", currentIndex, newToken);
                }
            });
        }

        final Handler handler = new Handler();
        final SimplePref simplePref = new SimplePref(context, prefsName);
        final Runnable updateRunnable = new Runnable() {

            int count = 5;

            @Override
            public void run() {
                if (--count > 0) {
                    final String settingValue = UUID.randomUUID().toString();
                    Debug.i("setting value: %s, current: %s", settingValue, simplePref.get("tkn", null));
                    simplePref.set("tkn", settingValue);

                    handler.postDelayed(this, 1000L);
                }
            }
        };

        handler.post(updateRunnable);
    }
}
