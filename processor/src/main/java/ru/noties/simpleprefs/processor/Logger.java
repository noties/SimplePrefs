package ru.noties.simpleprefs.processor;

import javax.tools.Diagnostic;

/**
 * Created by Dimitry Ivanov on 02.06.2015.
 */
public interface Logger {
    void log(Diagnostic.Kind kind, String message, Object... args);
}
