package ru.noties.simpleprefs.processor;

import javax.lang.model.util.Types;

/**
 * Created by Dimitry Ivanov on 02.06.2015.
 */
public abstract class AbsFinder implements Finder {

    final Logger mLogger;
    final Types mTypeUtils;

    public AbsFinder(Logger logger, Types types) {
        this.mLogger    = logger;
        this.mTypeUtils = types;
    }

    public abstract TypeChecker getChecker();
}
