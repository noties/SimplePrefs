package ru.noties.simpleprefs.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import ru.noties.simpleprefs.annotations.Constants;
import ru.noties.simpleprefs.annotations.Key;
import ru.noties.simpleprefs.annotations.Preference;

/**
 * Created by Dimitry Ivanov on 06.06.2015.
 */
public class PreferenceProcessor {

    interface ClassesProvider {
        Class[] provide() throws MirroredTypesException;
    }

    private final Logger mLogger;
    private final Types mTypeUtils;
    private final Elements mElementsUtils;

    private List<? extends Element> mKeys;
    private List<? extends Element> mPrefEnclosedElements;

    PreferenceProcessor(Logger logger, Types types, Elements elements) {
        mLogger = logger;
        mTypeUtils = types;
        mElementsUtils = elements;
    }

    PreferenceHolder getPreferenceHolder(Element preference) {

        mKeys = null;
        mPrefEnclosedElements = null;

        log(Diagnostic.Kind.NOTE, "Processing @Preference: %s", preference.getSimpleName());

        if (preference.getKind() != ElementKind.CLASS) {
            log(Diagnostic.Kind.ERROR, "Only class objects might be annotated with @Preference");
            return null;
        }

        final Set<Modifier> modifiers = preference.getModifiers();
        if (modifiers.contains(Modifier.ABSTRACT)
                || modifiers.contains(Modifier.FINAL)
                || !modifiers.contains(Modifier.PUBLIC)) {
            log(Diagnostic.Kind.ERROR, "@Preference class must be public, not final, not abstract");
            return null;
        }

        // check if a subclass of PrefsObject
        final TypeMirror prefsObjectType = mElementsUtils.getTypeElement("ru.noties.simpleprefs.obj.PrefsObject").asType();
        final TypeMirror prefType = preference.asType();
        if (!mTypeUtils.isSubtype(prefType, prefsObjectType)) {
            log(Diagnostic.Kind.ERROR, "A class annotated with @Preference must be a subclass of a PrefsObject");
            return null;
        }

        final List<? extends Element> preferenceEnclosedElements = preference.getEnclosedElements();
        final List<Element> keys = new ArrayList<>();

        boolean hasKey;

        for (Element element: preferenceEnclosedElements) {

            hasKey  = element.getAnnotation(Key.class) != null;

            if (hasKey) {
                keys.add(element);
            }
        }

        if (keys.size() == 0) {
            // nothing we could do
            log(Diagnostic.Kind.ERROR, "No @Key fields are found");
            return null;
        }

        final PreferenceHolder.Builder builder = new PreferenceHolder.Builder();

        mKeys = keys;
        mPrefEnclosedElements = preferenceEnclosedElements;

        final Preference annoPref = preference.getAnnotation(Preference.class);

        final String prefName = getPreferenceName(preference, annoPref);
        if (prefName == null) {
            log(Diagnostic.Kind.ERROR, "Could not find a preference name for a class: %s", preference.getSimpleName());
            return null;
        }

        final TypeElement preferenceElement = (TypeElement) preference;

        builder.setAnnotatedClass(preferenceElement)
                .setPreferenceName(prefName);

        final List<String> annoTypes = getAnnotationTypes(new ClassesProvider() {
            @Override
            public Class[] provide() throws MirroredTypesException {
                return annoPref.jsonTypes();
            }
        });

        final List<String> annoTypeSerializers = getAnnotationTypes(new ClassesProvider() {
            @Override
            public Class[] provide() throws MirroredTypesException {
                return annoPref.jsonTypeSerializers();
            }
        });

        final int annoTypesCount = annoTypes != null ? annoTypes.size() : 0;
        final int annoTypeSerializersCount = annoTypeSerializers != null ? annoTypeSerializers.size() : 0;

        if (annoTypesCount != annoTypeSerializersCount) {
            log(Diagnostic.Kind.ERROR, "jsonTypes & jsonTypeSerializers for a @Preference must have equal length, preference: " + preference.toString());
            return null;
        }

        if (annoTypesCount > 0) {
            final String[] types = new String[annoTypesCount];
            final String[] serializers = new String[annoTypesCount];

            annoTypes.toArray(types);
            annoTypeSerializers.toArray(serializers);

            builder.setJsonTypes(types)
                    .setJsonTypeSerializers(serializers);
        }

        builder.setJsonLibrary(annoPref.jsonLibrary())
                .setIsStatic(annoPref.isJsonVariableStatic())
                .setIsSingleton(annoPref.isSingleton())
                .setCatchJsonExceptions(annoPref.catchJsonExceptions());

        return builder.build();
    }

    public List<? extends Element> getPrefEnclosedElements() {
        return mPrefEnclosedElements;
    }

    public List<? extends Element> getKeys() {
        return mKeys;
    }

    private void log(Diagnostic.Kind kind, String pattern, Object... args) {
        mLogger.log(kind, pattern, args);
    }

    private String getPreferenceName(Element element, Preference preference) {

        if (preference == null) {
            return null;
        }

        final String annoValue = preference.value();
        if (Constants.DEF_STRING.equals(annoValue)) {
            return element.getSimpleName().toString();
        }

        return annoValue;
    }

    private static List<String> getAnnotationTypes(ClassesProvider provider) {

        final List<String> types = new ArrayList<>();

        try {
            final Class[] classes = provider.provide();
            for (Class clazz: classes) {
                types.add(clazz.getCanonicalName());
            }
        } catch (MirroredTypesException e) {
            types.clear();
            final List<? extends TypeMirror> typeMirrors = e.getTypeMirrors();
            for (TypeMirror mirror: typeMirrors) {
                types.add(mirror.toString());
            }
        }

        if (types.size() == 0) {
            return null;
        }

        return types;
    }
}
