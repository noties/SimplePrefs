package ru.noties.simpleprefs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dimitry Ivanov on 30.05.2015.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Preference {

    String value() default Constants.DEF_STRING;

    boolean isSingleton() default false;

    JsonLibrary jsonLibrary() default JsonLibrary.GSON;
    boolean isJsonVariableStatic() default false;

    boolean catchJsonExceptions() default false;

    Class[] jsonTypes() default {};
    Class[] jsonTypeSerializers() default {};
}
