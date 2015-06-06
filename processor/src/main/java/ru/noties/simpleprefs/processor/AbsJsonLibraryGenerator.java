package ru.noties.simpleprefs.processor;

import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by Dimitry Ivanov on 06.06.2015.
 */

public abstract class AbsJsonLibraryGenerator implements JsonLibraryGenerator {

    protected final Logger mLogger;
    protected final Types mTypeUtils;
    protected final boolean isStatic;

    AbsJsonLibraryGenerator(Logger logger, Types types, boolean isStatic) {
        this.isStatic = isStatic;
        this.mTypeUtils = types;
        this.mLogger = logger;
    }

    protected void log(Diagnostic.Kind kind, String pattern, Object... args) {
        mLogger.log(kind, pattern, args);
    }
}
