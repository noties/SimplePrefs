package ru.noties.simpleprefs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dimitry Ivanov on 30.05.2015.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Key {

    String DEFAULT_STRING = "";

    String name()           default DEFAULT_STRING;
    String defaultValue()   default DEFAULT_STRING;

}
