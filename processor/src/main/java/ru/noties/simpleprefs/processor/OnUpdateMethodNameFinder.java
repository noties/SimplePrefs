package ru.noties.simpleprefs.processor;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import ru.noties.simpleprefs.annotations.OnUpdate;

/**
 * Created by Dimitry Ivanov on 02.06.2015.
 */
public class OnUpdateMethodNameFinder extends SetterMethodNameFinder {

    public OnUpdateMethodNameFinder(Logger logger, Types types) {
        super(logger, types);
    }

    @Override
    public String getFindingMethodName() {
        return "@OnUpdate";
    }

    @Override
    public String findName(String keyName, Element key, List<? extends Element> enclosedElements) {

        final TypeMirror keyType = key.asType();

        OnUpdate onUpdate;

        String name;
        String annotationValue;
        TypeMirror enclosedType;

        for (Element element: enclosedElements) {

            onUpdate = element.getAnnotation(OnUpdate.class);
            if (onUpdate == null) {
                continue;
            }

            annotationValue = onUpdate.value();
            if (keyName.equals(annotationValue)) {

                name = element.getSimpleName().toString();
                enclosedType = element.asType();

                if (getChecker().isValid(name, keyType, enclosedType)) {
                    return name;
                }
            }
        }

        return null;
    }
}
