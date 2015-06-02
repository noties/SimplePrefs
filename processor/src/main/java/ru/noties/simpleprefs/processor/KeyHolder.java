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

    public KeyHolder(
            String keyName,
            String keyDefaultValue,
            Element keyElement,
            String setterMethodName,
            String getterMethodName,
            String onUpdateMethodName
    ) {
        this.keyName = keyName;
        this.keyDefaultValue = keyDefaultValue;
        this.keyElement = keyElement;
        this.setterMethodName = setterMethodName;
        this.getterMethodName = getterMethodName;
        this.onUpdateMethodName = onUpdateMethodName;
    }
}
