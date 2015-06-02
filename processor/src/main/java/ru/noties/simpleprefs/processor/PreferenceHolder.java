package ru.noties.simpleprefs.processor;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;

/**
 * Created by Dimitry Ivanov on 31.05.2015.
 */
public class PreferenceHolder {

    final String preferenceName;
    final TypeElement annotatedClass;
    final List<KeyHolder> keyHolders;

    public PreferenceHolder(String preferenceName, TypeElement annotatedClass) {
        this.preferenceName = preferenceName;
        this.annotatedClass = annotatedClass;
        this.keyHolders = new ArrayList<>();
    }
}
