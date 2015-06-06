package ru.noties.simpleprefs.processor;

import javax.lang.model.element.Element;

/**
 * Created by Dimitry Ivanov on 31.05.2015.
 */
public class KeyHolder {

    final String keyName;
    final String keyDefaultValue;

    final Element keyElement;

    final String setterMethodName;
    final String getterMethodName;
    final String onUpdateMethodName;

    final boolean isJson;

    private KeyHolder(Builder builder) {
        this.keyName = builder.keyName;
        this.keyDefaultValue = builder.keyDefaultValue;
        this.keyElement = builder.keyElement;
        this.setterMethodName = builder.setterMethodName;
        this.getterMethodName = builder.getterMethodName;
        this.onUpdateMethodName = builder.onUpdateMethodName;
        this.isJson = builder.isJson;
    }

    public static class Builder {

        String keyName;
        String keyDefaultValue;
        Element keyElement;
        String setterMethodName;
        String getterMethodName;
        String onUpdateMethodName;
        boolean isJson;

        public Builder setKeyDefaultValue(String keyDefaultValue) {
            this.keyDefaultValue = keyDefaultValue;
            return this;
        }

        public Builder setKeyName(String keyName) {
            this.keyName = keyName;
            return this;
        }

        public Builder setKeyElement(Element keyElement) {
            this.keyElement = keyElement;
            return this;
        }

        public Builder setSetterMethodName(String setterMethodName) {
            this.setterMethodName = setterMethodName;
            return this;
        }

        public Builder setGetterMethodName(String getterMethodName) {
            this.getterMethodName = getterMethodName;
            return this;
        }

        public Builder setOnUpdateMethodName(String onUpdateMethodName) {
            this.onUpdateMethodName = onUpdateMethodName;
            return this;
        }

        public Builder setIsJson(boolean isJson) {
            this.isJson = isJson;
            return this;
        }

        public KeyHolder build() {
            return new KeyHolder(this);
        }
    }
}
