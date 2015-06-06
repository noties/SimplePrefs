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
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import ru.noties.simpleprefs.annotations.Getter;
import ru.noties.simpleprefs.annotations.Key;
import ru.noties.simpleprefs.annotations.OnUpdate;
import ru.noties.simpleprefs.annotations.Preference;
import ru.noties.simpleprefs.annotations.Setter;

public class Processor extends AbstractProcessor implements Logger {

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
            final PreferenceFileWriter fileWriter = new PreferenceFileWriter(this, mTypeUtils, mElementsUtils, mFiler);
            for (PreferenceHolder holder: preferenceHolders) {
                fileWriter.write(holder);
            }
        }

        return true;
    }

    private PreferenceHolder processPreference(RoundEnvironment env, Element preference) {

        final PreferenceProcessor preferenceProcessor = new PreferenceProcessor(this, mTypeUtils, mElementsUtils);
        final PreferenceHolder holder = preferenceProcessor.getPreferenceHolder(preference);

        if (holder == null) {
            return null;
        }

        final KeyProcessor keyProcessor = new KeyProcessor(this, mTypeUtils);

        KeyHolder keyHolder;

        for (Element key: preferenceProcessor.getKeys()) {
            keyHolder = keyProcessor.getKeyHolder(key, preferenceProcessor.getPrefEnclosedElements());
            if (keyHolder != null) {
                holder.keyHolders.add(keyHolder);
            }
        }

        if (holder.keyHolders.size() == 0) {
            return null;
        }

        return holder;
    }
}
