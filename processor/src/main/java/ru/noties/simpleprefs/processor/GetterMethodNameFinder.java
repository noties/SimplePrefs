package ru.noties.simpleprefs.processor;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import ru.noties.simpleprefs.annotations.Getter;

/**
 * Created by Dimitry Ivanov on 02.06.2015.
 */
public class GetterMethodNameFinder extends AbsFinder {

    private final TypeChecker mChecker;

    private boolean mSecondPass;

    public GetterMethodNameFinder(Logger logger, Types types) {
        super(logger, types);
        mChecker = new GetterChecker();
    }

    @Override
    public String findName(String keyName, Element key, List<? extends Element> enclosedElements) {

        final String fieldName = key.getSimpleName().toString();
        final TypeMirror keyType = key.asType();
        final TypeKind keyTypeKind = keyType.getKind();

        final boolean isBoolean = keyTypeKind.isPrimitive() && keyTypeKind == TypeKind.BOOLEAN;
        final boolean isSecondPassBoolean = !mSecondPass && isBoolean;
        mSecondPass = false;

        final String getterMethod;
        if (!isSecondPassBoolean) {
            getterMethod = MethodNameUtils.createBooleanGetter(fieldName);
        } else {
            getterMethod = MethodNameUtils.createGetter(fieldName);
        }

        String name;
        Getter getter;

        TypeMirror enclosedType;

        for (Element enclosed: enclosedElements) {
            if (enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }

            enclosedType = enclosed.asType();

            name = enclosed.getSimpleName().toString();

            if (getterMethod.equals(name)) {

                // check for type parameters
                if (mChecker.isValid(name, keyType, enclosedType)) {
                    return name;
                }
            }

            getter = enclosed.getAnnotation(Getter.class);
            if (getter != null) {

                final String getterKey = getter.value();
                if (keyName.equals(getterKey)) {

                    if (mChecker.isValid(name, keyType, enclosedType)) {
                        return name;
                    }
                }
            }
        }

        if (!isSecondPassBoolean) {
            mSecondPass = true;
            return findName(keyName, key, enclosedElements);
        }

        mLogger.log(Diagnostic.Kind.ERROR, "Could not find getter method for a key: %s", key.getSimpleName());
        return null;
    }

    @Override
    public TypeChecker getChecker() {
        return mChecker;
    }

    @Override
    public String getFindingMethodName() {
        return "getter";
    }

    private class GetterChecker implements TypeChecker {

        @Override
        public boolean isValid(String name, TypeMirror keyType, TypeMirror enclosedType) {

            final ExecutableType executableType = (ExecutableType) enclosedType;
            final List<? extends TypeMirror> methodParameters = executableType.getParameterTypes();
            final int size = methodParameters == null ? 0 : methodParameters.size();
            if (size != 0) {
                mLogger.log(Diagnostic.Kind.WARNING, "%s method: %s() should have no parameters", getFindingMethodName(), name);
                return false;
            }

            final TypeMirror getterReturnType = executableType.getReturnType();

            if (!mTypeUtils.isSubtype(keyType, getterReturnType)) {
                mLogger.log(Diagnostic.Kind.WARNING, "%s method %s() has different return type: %s", getFindingMethodName(), name, getterReturnType);
                return false;
            }

            return true;
        }
    }
}
