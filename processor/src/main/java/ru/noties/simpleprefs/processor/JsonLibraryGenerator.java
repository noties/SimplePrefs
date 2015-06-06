package ru.noties.simpleprefs.processor;

import javax.lang.model.type.TypeMirror;

/**
 * Created by Dimitry Ivanov on 06.06.2015.
 */
public interface JsonLibraryGenerator {
    String getJsonVariableName();
    String initializeJsonVariable(Indent indent, String[] types, String[] typeAdapters);

    String toJson(String varName);
    String fromJson(String jsonVar, TypeMirror typeMirror);
}
