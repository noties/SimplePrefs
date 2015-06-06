package ru.noties.simpleprefs.obj;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import ru.noties.simpleprefs.SimplePref;

/**
 * Created by Dimitry Ivanov on 30.05.2015.
 */
public class PrefsObject {

    private static final Map<Class, PrefsObjectCreator> sCache = new HashMap<>();

    public static <PO> PO create(Class<PO> prefObjectClass, Context context) {
        PrefsObjectCreator creator = sCache.get(prefObjectClass);
        if (creator == null) {
            creator = PrefsObjectCreator.init(prefObjectClass);
            sCache.put(prefObjectClass, creator);
        }
        return creator.create(context);
    }

    protected static final int DEF_INT = -1;
    protected static final long DEF_LONG = -1L;
    protected static final float DEF_FLOAT = -1.F;
    protected static final boolean DEF_BOOL = false;

    protected final SimplePref mPref;

    public PrefsObject(Context context, String name) {
        mPref = new SimplePref(context, name);
    }

    public String getWrappedPreferencesName() {
        return mPref.getSharedPreferencesName();
    }

    protected void onJsonExceptionHandled(Throwable t) {

    }
}
