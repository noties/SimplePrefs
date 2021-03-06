package ru.noties.simpleprefs.sample;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ru.noties.debug.Debug;
import ru.noties.simpleprefs.SimplePref;
import ru.noties.simpleprefs.obj.PrefsObject;
import ru.noties.simpleprefs.sample.obj.ColorObject;
import ru.noties.simpleprefs.sample.obj.GenericObject;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Debug.init(true);

        showcaseSimple();

        showcaseBatch();

        showcaseOnUpdateListener();

        showcaseSerialization();

        showCaseSingleton();
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

    private void showcaseSerialization() {
        final PrefWithJsonSerialization pref = PrefsObject.create(PrefWithJsonSerialization.class, this);
        final SimplePref simplePref = new SimplePref(this, pref.getWrappedPreferencesName());
        final Date now = pref.getSomeDate();
        pref.setSomeDate(new Date());
        Debug.i("was: %s, now: %s, prefValue: %s", now, pref.getSomeDate(), simplePref.get("someDate"));

        final GenericObject<ColorObject> nowGeneric = pref.getGenericObject();
        final ColorObject colorObject = new ColorObject();
        colorObject.setColor(0xFFff0000);
        colorObject.setName("Blood red");
        final GenericObject<ColorObject> newGeneric = new GenericObject<>();
        newGeneric.value = colorObject;

        pref.setGenericObject(newGeneric);
        Debug.i("was: %s, now: %s, prefValue: %s", nowGeneric, newGeneric, simplePref.get("genericObject"));

        final List<String> nowList = pref.getSomeStringList();
        final List<String> newList = Arrays.asList("one", "two", "three");
        pref.setSomeStringList(newList);
        Debug.i("was: %s, now: %s, prefValue: %s", nowList, newList, simplePref.get("someStringList"));
    }

    private void showCaseSingleton() {
        final PrefWithJsonSerialization pref = PrefWithJsonSerialization.create(PrefWithJsonSerialization.class, this);
        for (int i = 0; i < 5; i++) {
            final int index = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final PrefWithJsonSerialization got = PrefWithJsonSerialization.create(PrefWithJsonSerialization.class, MainActivity.this);
                    Debug.i("Thread #%d, equals: %s", index, pref == got);
                }
            }).start();
        }
    }
}
