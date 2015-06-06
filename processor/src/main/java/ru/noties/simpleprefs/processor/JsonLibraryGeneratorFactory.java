package ru.noties.simpleprefs.processor;

import javax.lang.model.util.Types;

import ru.noties.simpleprefs.annotations.JsonLibrary;

/**
 * Created by Dimitry Ivanov on 06.06.2015.
 */
public class JsonLibraryGeneratorFactory {

    private JsonLibraryGeneratorFactory() {}

    public static JsonLibraryGenerator create(Logger logger, Types types, JsonLibrary library, boolean isStatic) {

        if (library == null) {
            return null;
        }

        switch (library) {

            case GSON:
                return new GSONLibraryGenerator(logger, types, isStatic);

            default:
                throw new IllegalStateException("Unknown type of JsonLibrary: " + library);
        }
    }
}
