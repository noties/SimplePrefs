package ru.noties.simpleprefs.processor;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import ru.noties.simpleprefs.annotations.Setter;

/**
 * Created by Dimitry Ivanov on 02.06.2015.
 */
public class SetterMethodNameFinder extends AbsFinder {

    private final TypeChecker mChecker;

    public SetterMethodNameFinder(Logger logger, Types types) {
        super(logger, types);
        mChecker = new SetterChecker();
    }

    @Override
    public TypeChecker getChecker() {
        return mChecker;
    }

    @Override
    public String getFindingMethodName() {
        return "setter";
    }

    @Override
    public String findName(String keyName, Element key, List<? extends Element> enclosedElements) {

        final TypeMirror keyType = key.asType();

        final String setterName = MethodNameUtils.createSetter(key.getSimpleName().toString());

        Setter setter;
        String name;

        TypeMirror enclosedType;

        for (Element enclosed: enclosedElements) {
            if (enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }

            enclosedType = enclosed.asType();

            name = enclosed.getSimpleName().toString();

            if (setterName.equals(name)) {

                // check for type parameters
                if (mChecker.isValid(name, keyType, enclosedType)) {
                    return name;
                }
            }

            setter  = enclosed.getAnnotation(Setter.class);
            if (setter != null) {
                final String setterKey = setter.value();
                if (keyName.equals(setterKey)) {

                    if (mChecker.isValid(name, keyType, enclosedType)) {
                        return name;
                    }
                }
            }
        }

        mLogger.log(Diagnostic.Kind.ERROR, "Could not find %s method for a key: %s", getFindingMethodName(), key.getSimpleName());
        return null;
    }

    private class SetterChecker implements TypeChecker {

        @Override
        public boolean isValid(String name, TypeMirror keyType, TypeMirror enclosedType) {
            final ExecutableType executableType = (ExecutableType) enclosedType;
            final List<? extends TypeMirror> methodParameters = executableType.getParameterTypes();
            final int size = methodParameters == null ? 0 : methodParameters.size();
            if (size != 1) {
                mLogger.log(Diagnostic.Kind.WARNING, "%s method: %s() should have exactly one parameter", getFindingMethodName(), name);
                return false;
            }

            final TypeMirror setterTypeParameter = methodParameters.get(0);

            if (!mTypeUtils.isSubtype(keyType, setterTypeParameter)) {
                mLogger.log(Diagnostic.Kind.WARNING, "%s method %s() has different parameter type: %s", getFindingMethodName(), name, setterTypeParameter);
                return false;
            }

            return true;
        }
    }
}
