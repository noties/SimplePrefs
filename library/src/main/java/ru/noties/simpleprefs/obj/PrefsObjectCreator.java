package ru.noties.simpleprefs.obj;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Dimitry Ivanov on 01.06.2015.
 */
class PrefsObjectCreator {

    public static PrefsObjectCreator init(Class<?> clazz) {
        Class<?> implClass;
        try {
            implClass = Class.forName(clazz.getCanonicalName() + "$$SP");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find a prefs helper class", e);
        }

        try {
            final Method method = implClass.getMethod("create", Context.class);
            return new PrefsObjectCreator(method);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Generated class has no static method `create(Context)", e);
        }
    }

    final Method method;

    PrefsObjectCreator(Method method) {
        this.method = method;
    }

    public <T> T create(Context context) {
        try {
            //noinspection unchecked
            return (T) method.invoke(null, context);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
