package ru.noties.simpleprefs.processor;

import java.util.Arrays;

/**
 * Created by Dimitry Ivanov on 31.05.2015.
 */
public class Indent {

    private static final char[] sChars = new char[4];
    static {
        Arrays.fill(sChars, ' ');
    }

    private int value;

    public Indent increment() {
        value++;
        return this;
    }

    public Indent decrement() {
        value--;
        return this;
    }

    @Override
    public String toString() {
        if (value == 0) {
            return "";
        }

        final StringBuilder builder = new StringBuilder(value * 4);
        for (int i = 0; i < value; i++) {
            builder.append(sChars);
        }

        return builder.toString();
    }
}
