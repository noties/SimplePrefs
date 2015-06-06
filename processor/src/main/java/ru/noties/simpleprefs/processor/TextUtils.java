package ru.noties.simpleprefs.processor;

/**
 * Created by Dimitry Ivanov on 02.06.2015.
 */
public class TextUtils {

    private TextUtils() {}

    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static String capFirstLetter(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
