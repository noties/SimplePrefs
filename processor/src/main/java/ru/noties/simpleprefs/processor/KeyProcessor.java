package ru.noties.simpleprefs.processor;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import ru.noties.simpleprefs.annotations.Constants;
import ru.noties.simpleprefs.annotations.Key;

/**
 * Created by Dimitry Ivanov on 06.06.2015.
 */
public class KeyProcessor {

    private static final String OBJECT_NAME = "java.lang.Object";
    private static final String STRING_NAME = "java.lang.String";

    private final Logger mLogger;
    private final Types mTypeUtils;

    private final AbsFinder mSetterMethodFinder;
    private final AbsFinder mGetterMethodFinder;
    private final AbsFinder mOnUpdateMethodFinder;

    KeyProcessor(Logger logger, Types types) {

        mLogger = logger;
        mTypeUtils = types;

        mSetterMethodFinder = new SetterMethodNameFinder(mLogger, mTypeUtils);
        mGetterMethodFinder = new GetterMethodNameFinder(mLogger, mTypeUtils);
        mOnUpdateMethodFinder = new OnUpdateMethodNameFinder(mLogger, mTypeUtils);
    }

    KeyHolder getKeyHolder(Element key, List<? extends Element> preferenceEnclosedElements) {

        final TypeMirror typeMirror  = key.asType();
        final TypeKind typeKind      = typeMirror.getKind();

        final Key keyAnno = key.getAnnotation(Key.class);
        if (keyAnno == null) {
            log(Diagnostic.Kind.ERROR, "Unexpected...");
            return null;
        }

        final boolean isJson = keyAnno.isJson();

        if (!isJson
                && !isTypeSupported(typeKind, typeMirror)) {
            return null;
        }

        final String keyName = getKeyName(key, keyAnno);

        final String setterMethodName = mSetterMethodFinder.findName(keyName, key, preferenceEnclosedElements);
        if (setterMethodName == null) {
            return null;
        }

        final String getterMethodName = mGetterMethodFinder.findName(keyName, key, preferenceEnclosedElements);
        if (getterMethodName == null) {
            return null;
        }

        final String onUpdateMethodName = mOnUpdateMethodFinder.findName(keyName, key, preferenceEnclosedElements);

        final String outDefault = getKeyDefaultValue(keyAnno);

        final KeyHolder.Builder builder = new KeyHolder.Builder()
                .setKeyName(keyName)
                .setKeyDefaultValue(outDefault)
                .setKeyElement(key)
                .setSetterMethodName(setterMethodName)
                .setGetterMethodName(getterMethodName)
                .setOnUpdateMethodName(onUpdateMethodName)
                .setIsJson(isJson);

//        if (isJson) {
//
//            final List<String> types = new ArrayList<>();
//            final List<String> typeAdapters = new ArrayList<>();
//
//            final String keyComponentName = getKeyComponentName(typeMirror);
//
//            try {
//                if (jsonKeyAnno.typeAdapter() != Object.class) {
//
//                    types.add(keyComponentName);
//                    typeAdapters.add(jsonKeyAnno.typeAdapter().getCanonicalName());
//                }
//            } catch (MirroredTypeException e) {
//                final TypeMirror typeAdapterTypeMirror = e.getTypeMirror();
//
//                if (!OBJECT_NAME.equals(typeAdapterTypeMirror.toString())) {
//
//                    types.add(keyComponentName);
//                    typeAdapters.add(typeAdapterTypeMirror.toString());
//                }
//            }
//
//            final List<String> annoTypes = getAnnotationTypes(new ClassesProvider() {
//                @Override
//                public Class[] provide() {
//                    return jsonKeyAnno.types();
//                }
//            });
//
//            final List<String> annoTypeAdapters = getAnnotationTypes(new ClassesProvider() {
//                @Override
//                public Class[] provide() {
//                    return jsonKeyAnno.typeAdapters();
//                }
//            });
//
//            final int typesCount = annoTypes != null ? annoTypes.size() : 0;
//            final int typeAdaptersCount = annoTypeAdapters != null ? annoTypeAdapters.size() : 0;
//
//            if (typesCount != typeAdaptersCount) {
//
//                return null;
//            }
//
//            if (annoTypes != null) {
//                types.addAll(annoTypes);
//            }
//
//            if (annoTypeAdapters != null) {
//                typeAdapters.addAll(annoTypeAdapters);
//            }
//
//            final String[] jsonTypes;
//            final String[] jsonTypeAdapters;
//            if (types.size() == 0
//                    || typeAdapters.size() == 0) {
//                jsonTypes = null;
//                jsonTypeAdapters = null;
//            } else {
//                jsonTypes = new String[types.size()];
//                jsonTypeAdapters = new String[types.size()];
//
//                types.toArray(jsonTypes);
//                typeAdapters.toArray(jsonTypeAdapters);
//            }
//
//            log(Diagnostic.Kind.NOTE, "key: %s, Types: %s, Adapters: %s", key.getSimpleName(), Arrays.toString(jsonTypes), Arrays.toString(jsonTypeAdapters));
//
//            builder.setJsonTypes(jsonTypes)
//                    .setJsonTypeAdapters(jsonTypeAdapters);
//        }

        return builder.build();
    }

    private boolean isTypeSupported(TypeKind typeKind, TypeMirror typeMirror) {
        if (typeKind.isPrimitive()) {
            if (!isSupportedPrimitive(typeKind)) {
                log(Diagnostic.Kind.ERROR, "Type of %s is not supported", typeKind);
                return false;
            }
        } else if (typeKind == TypeKind.DECLARED) {

            // if it's string - ok
            final String className = typeMirror.toString();

            if (!STRING_NAME.equals(className)) {
                log(Diagnostic.Kind.ERROR, "Objects of type: %s are not supported", typeMirror);
                return false;
            }

        } else {
            log(Diagnostic.Kind.ERROR, "Could not resolve Type: %s", typeMirror);
            return false;
        }
        return true;
    }

    private boolean isSupportedPrimitive(TypeKind kind) {

        switch (kind) {

            case INT:
            case LONG:
            case FLOAT:
            case BOOLEAN:
                return true;

            default:
                return false;
        }
    }

    private static String getKeyName(Element element, Key key) {

        final String annoValue;

        if (key != null) {
            annoValue = key.name();
        } else {
            annoValue = null;
        }

        if (annoValue != null
                && !Constants.DEF_STRING.equals(annoValue)) {
            return annoValue;
        }

        return element.getSimpleName().toString();
    }

    private static String getKeyDefaultValue(Key key) {

        final String annoValue;

        if (key != null) {
            annoValue = key.defaultValue();
        } else {
            annoValue = null;
        }

        if (annoValue != null
                && !Constants.DEF_STRING.equals(annoValue)) {
            return annoValue;
        }

        return null;
    }

    private String getKeyComponentName(TypeMirror mirror) {
        final TypeKind typeKind = mirror.getKind();
        if (typeKind != TypeKind.ARRAY) {
            return mirror.toString();
        }

        final ArrayType arrayType = (ArrayType) mirror;

        final TypeMirror arrayComponent = arrayType.getComponentType();
        return arrayComponent.toString();
    }

    private void log(Diagnostic.Kind kind, String pattern, Object... args) {
        mLogger.log(kind, pattern, args);
    }


}
