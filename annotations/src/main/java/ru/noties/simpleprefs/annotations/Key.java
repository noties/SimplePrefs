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

    String name()           default Constants.DEF_STRING;
    String defaultValue()   default Constants.DEF_STRING;

    // for json key defaultValue would not be considered
    boolean isJson() default false;
}
