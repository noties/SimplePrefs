package ru.noties.simpleprefs.processor;

/**
 * Created by Dimitry Ivanov on 31.05.2015.
 */
public class MethodNameUtils {

    private static final String SET = "set";
    private static final String GET = "get";

    private MethodNameUtils() {}

    public static String createSetter(String value) {
        return SET + Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public static String createGetter(String value) {
        return GET + Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
