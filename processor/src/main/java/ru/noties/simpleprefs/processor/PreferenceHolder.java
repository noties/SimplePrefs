package ru.noties.simpleprefs.processor;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;

import ru.noties.simpleprefs.annotations.JsonLibrary;

/**
 * Created by Dimitry Ivanov on 31.05.2015.
 */
public class PreferenceHolder {

    final String preferenceName;
    final TypeElement annotatedClass;
    final List<KeyHolder> keyHolders;

    final boolean isSingleton;

    final JsonLibrary jsonLibrary;
    final boolean isStatic;

    final boolean catchJsonExceptions;

    final String[] jsonTypes;
    final String[] jsonTypeSerializers;

    private PreferenceHolder(Builder builder) {
        this.preferenceName = builder.preferenceName;
        this.annotatedClass = builder.annotatedClass;
        this.keyHolders = new ArrayList<>();
        this.isSingleton = builder.isSingleton;
        this.jsonLibrary = builder.jsonLibrary;
        this.catchJsonExceptions = builder.catchJsonExceptions;
        this.isStatic = builder.isStatic;
        this.jsonTypes = builder.jsonTypes;
        this.jsonTypeSerializers = builder.jsonTypeSerializers;
    }

    public static class Builder {

        String preferenceName;
        TypeElement annotatedClass;

        boolean isSingleton;

        JsonLibrary jsonLibrary;
        boolean isStatic;

        boolean catchJsonExceptions;

        String[] jsonTypes;
        String[] jsonTypeSerializers;

        public Builder setPreferenceName(String preferenceName) {
            this.preferenceName = preferenceName;
            return this;
        }

        public Builder setAnnotatedClass(TypeElement annotatedClass) {
            this.annotatedClass = annotatedClass;
            return this;
        }

        public Builder setJsonLibrary(JsonLibrary jsonLibrary) {
            this.jsonLibrary = jsonLibrary;
            return this;
        }

        public Builder setIsStatic(boolean isStatic) {
            this.isStatic = isStatic;
            return this;
        }

        public Builder setJsonTypes(String[] jsonTypes) {
            this.jsonTypes = jsonTypes;
            return this;
        }

        public Builder setJsonTypeSerializers(String[] jsonTypeSerializers) {
            this.jsonTypeSerializers = jsonTypeSerializers;
            return this;
        }

        public Builder setIsSingleton(boolean isSingleton) {
            this.isSingleton = isSingleton;
            return this;
        }

        public Builder setCatchJsonExceptions(boolean catchJsonExceptions) {
            this.catchJsonExceptions = catchJsonExceptions;
            return this;
        }

        public PreferenceHolder build() {
            return new PreferenceHolder(this);
        }
    }
}
