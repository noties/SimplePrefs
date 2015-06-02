package ru.noties.simpleprefs.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import ru.noties.simpleprefs.annotations.Getter;
import ru.noties.simpleprefs.annotations.Key;
import ru.noties.simpleprefs.annotations.OnUpdate;
import ru.noties.simpleprefs.annotations.Preference;
import ru.noties.simpleprefs.annotations.Setter;

public class Processor extends AbstractProcessor implements Logger {

    private static final String STRING_NAME = "java.lang.String";

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(
                Preference.class.getCanonicalName(),
                Key.class.getCanonicalName(),
                Getter.class.getCanonicalName(),
                Setter.class.getCanonicalName(),
                OnUpdate.class.getCanonicalName()
        ));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_6;
    }

    private Types mTypeUtils;
    private Elements mElementsUtils;
    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        mTypeUtils = env.getTypeUtils();
        mElementsUtils = env.getElementUtils();
        mFiler = env.getFiler();
        mMessager = env.getMessager();
    }

    @Override
    public void log(Diagnostic.Kind kind, String message, Object... args) {
        final String out;
        if (args == null
                || args.length == 0) {
            out = message;
        } else {
            out = String.format(message, args);
        }
        mMessager.printMessage(kind, out);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            return processInner(annotations, roundEnv);
        } catch (Throwable t) {
            t.printStackTrace();

        }
        return false;
    }

    private boolean processInner(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Throwable {

        final Set<? extends Element> preferences = roundEnv.getElementsAnnotatedWith(Preference.class);
        if (preferences == null
                || preferences.size() == 0) {
            return false;
        }

        final List<PreferenceHolder> preferenceHolders = new ArrayList<>();
        PreferenceHolder preferenceHolder;

        for (Element element: preferences) {
            preferenceHolder = processPreference(roundEnv, element);
            if (preferenceHolder != null) {
                preferenceHolders.add(preferenceHolder);
            }
        }

        if (preferenceHolders.size() > 0) {
            final PreferenceFileWriter fileWriter = new PreferenceFileWriter(mElementsUtils, mFiler);
            for (PreferenceHolder holder: preferenceHolders) {
                fileWriter.write(holder);
            }
        }

        return true;
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

    private PreferenceHolder processPreference(RoundEnvironment env, Element preference) {

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
        for (Element element: preferenceEnclosedElements) {
            if (element.getAnnotation(Key.class) != null) {
                keys.add(element);
            }
        }

        if (keys.size() == 0) {
            // nothing we could do
            log(Diagnostic.Kind.ERROR, "No @Key fields are found");
            return null;
        }

        final String prefName = getPreferenceName(preference);
        if (prefName == null) {
            log(Diagnostic.Kind.ERROR, "Could not find a preference name for a class: %s", preference.getSimpleName());
            return null;
        }

        final AbsFinder setterMethodFinder      = new SetterMethodNameFinder(this, mTypeUtils);
        final AbsFinder getterMethodFinder      = new GetterMethodNameFinder(this, mTypeUtils);
        final AbsFinder onUpdateMethodFinder    = new OnUpdateMethodNameFinder(this, mTypeUtils);

        final TypeElement preferenceElement = (TypeElement) preference;
        final PreferenceHolder holder = new PreferenceHolder(prefName, preferenceElement);

        TypeMirror typeMirror;
        TypeKind typeKind;

        for (Element key: keys) {

            typeMirror  = key.asType();
            typeKind    = typeMirror.getKind();

            if (!isTypeSupported(typeKind, typeMirror)) {
                continue;
            }

            final Key keyAnno = key.getAnnotation(Key.class);
            if (keyAnno == null) {
                log(Diagnostic.Kind.ERROR, "Unexpected...");
                continue;
            }

            final String keyName;
            if (Key.DEFAULT_STRING.equals(keyAnno.name())) {
                keyName = key.getSimpleName().toString();
            } else {
                keyName = keyAnno.name();
            }

            final String setterMethodName = setterMethodFinder.findName(keyName, key, preferenceEnclosedElements);
            if (setterMethodName == null) {
                continue;
            }

            final String getterMethodName = getterMethodFinder.findName(keyName, key, preferenceEnclosedElements);
            if (getterMethodName == null) {
                continue;
            }

            final String onUpdateMethodName = onUpdateMethodFinder.findName(keyName, key, preferenceEnclosedElements);

            final String defaultValue = keyAnno.defaultValue();
            final String outDefault;
            if (Key.DEFAULT_STRING.equals(defaultValue)) {
                outDefault = null;
            } else {
                outDefault = defaultValue;
            }

            holder.keyHolders.add(
                    new KeyHolder(
                            keyName,
                            outDefault,
                            key,
                            setterMethodName,
                            getterMethodName,
                            onUpdateMethodName
                    )
            );
        }

        if (holder.keyHolders.size() == 0) {
            return null;
        }

        return holder;
    }

    private String getPreferenceName(Element element) {
        final Preference preference = element.getAnnotation(Preference.class);
        if (preference == null) {
            return null;
        }

        final String annoValue = preference.value();
        if (Preference.DEFAULT.equals(annoValue)) {
            return element.getSimpleName().toString();
        }

        return annoValue;
    }
}
