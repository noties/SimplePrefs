package ru.noties.simpleprefs.processor;

import javax.lang.model.type.TypeMirror;

/**
 * Created by Dimitry Ivanov on 02.06.2015.
 */
public interface TypeChecker {
    boolean isValid(String name, TypeMirror keyType, TypeMirror enclosedType);
}
