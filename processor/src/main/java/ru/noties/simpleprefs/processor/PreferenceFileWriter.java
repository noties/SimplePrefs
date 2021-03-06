package ru.noties.simpleprefs.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

/**
 * Created by Dimitry Ivanov on 02.06.2015.
 */
public class PreferenceFileWriter {

    private static final Pattern EVAULATE_PATTERN = Pattern.compile("(\\$\\{)(.+)(\\})");
    private static final String ON_UPDATE_NAME = "mOnUpdateListener";
    private static final String SINGLETON_NAME = "sInstance";

    private final Logger mLogger;
    private final Types mTypeUtils;
    private final Elements mElementsUtils;
    private final Filer mFiler;

    public PreferenceFileWriter(Logger logger, Types types, Elements elementUtils, Filer filer) {
        this.mLogger = logger;
        this.mTypeUtils = types;
        this.mElementsUtils = elementUtils;
        this.mFiler = filer;
    }

    public void write(PreferenceHolder holder) throws IOException {
        final String className = holder.annotatedClass.getSimpleName().toString() + "$$SP";
        final JavaFileObject jfo = mFiler.createSourceFile(className);
        final Writer writer = jfo.openWriter();

        final boolean hasOnUpdate = hasOnUpdate(holder);
        final boolean hasJson = hasJson(holder);

        final JsonLibraryGenerator jsonLibraryGenerator = JsonLibraryGeneratorFactory
                .create(mLogger, mTypeUtils, hasJson ? holder.jsonLibrary : null, holder.isStatic);

        final Indent indent = new Indent();

        final StringBuilder builder = new StringBuilder();
        builder.append("package ")
                .append(mElementsUtils.getPackageOf(holder.annotatedClass))
                .append(";\n\n");

        builder.append("// This file is autogenerated by SimplePrefs library at ")
                .append(new Date())
                .append("\n\n");

        builder.append("public final class ")
                .append(className)
                .append(" extends ")
                .append(holder.annotatedClass.getQualifiedName().toString())
                .append(" {\n\n");

        indent.increment();

        builder.append(createCreateStatement(indent, holder, className));

        // gson
        if (hasJson) {
            builder.append(jsonLibraryGenerator.initializeJsonVariable(indent, holder.jsonTypes, holder.jsonTypeSerializers))
                    .append("\n\n");
        }

        // create default constructor
        builder.append(indent)
                .append("private ")
                .append(className)
                .append("(")
                .append("android.content.Context context")
                .append(") { \n");

        builder.append(indent.increment())
                .append("super(context, \"")
                .append(holder.preferenceName)
                .append("\"); \n");

        if (hasOnUpdate) {
            builder.append(indent)
                    .append("mPref.getWrappedSharedPreferences().registerOnSharedPreferenceChangeListener(")
                    .append(ON_UPDATE_NAME)
                    .append(");\n");
        }

        builder.append(indent.decrement())
                .append("}\n\n");

        final List<KeyHolder> onUpdateKeys = new ArrayList<>();

        // override all getters & setters
        for (KeyHolder keyHolder: holder.keyHolders) {

            final String type = keyHolder.keyElement.asType().toString();

            // setter
            final String setterName = keyHolder.setterMethodName;
            builder.append(indent)
                    .append("public void ")
                    .append(setterName)
                    .append("(")
                    .append(type)
                    .append(" value) {\n")
                    .append(indent.increment())
                    .append(createSet(indent, jsonLibraryGenerator, keyHolder, holder.catchJsonExceptions))
                    .append("\n")
                    .append(indent.decrement())
                    .append("}\n\n");

            // getter
            final String getterName = keyHolder.getterMethodName;
            builder.append(indent)
                    .append("public ")
                    .append(type)
                    .append(" ")
                    .append(getterName)
                    .append("() {\n")
                    .append(indent.increment())
                    .append(createGet(indent, jsonLibraryGenerator, keyHolder, holder.catchJsonExceptions))
                    .append("\n")
                    .append(indent.decrement())
                    .append("}\n\n");

            if (!TextUtils.isEmpty(keyHolder.onUpdateMethodName)) {
                onUpdateKeys.add(keyHolder);
            }
        }

        if (hasOnUpdate && onUpdateKeys.size() > 0) {

            // write onUpdate logic
            builder.append(indent)
                    .append("private final android.content.SharedPreferences.OnSharedPreferenceChangeListener ")
                    .append(ON_UPDATE_NAME)
                    .append(" = new android.content.SharedPreferences.OnSharedPreferenceChangeListener() {\n")
                    .append(indent.increment())
                    .append("public void onSharedPreferenceChanged(android.content.SharedPreferences prefs, String key) {");

            indent.increment();

            for (KeyHolder key: onUpdateKeys) {
                builder.append('\n')
                        .append(indent)
                        .append(String.format("if (key.equals(\"%s\")) { %s(%s()); return; }", key.keyName, key.onUpdateMethodName, key.getterMethodName));
            }

            builder.append("\n")
                    .append(indent.decrement())
                    .append("}\n")
                    .append(indent.decrement())
                    .append("};");
        }

        builder.append("\n}");

        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }

    private static boolean hasOnUpdate(PreferenceHolder holder) {
        for (KeyHolder key: holder.keyHolders) {
            if (!TextUtils.isEmpty(key.onUpdateMethodName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasJson(PreferenceHolder holder) {
        for (KeyHolder key: holder.keyHolders) {
            if (key.isJson) {
                return true;
            }
        }
        return false;
    }

    private static String createCreateStatement(Indent indent, PreferenceHolder holder, String className) {

        final StringBuilder builder = new StringBuilder();

        if (holder.isSingleton) {
            builder.append(indent)
                    .append("private static volatile ")
                    .append(className)
                    .append(' ')
                    .append(SINGLETON_NAME)
                    .append(" = null;\n\n");
        }

        builder.append(indent)
                .append("public static Object create(android.content.Context context) {\n")
                .append(indent.increment());

        if (!holder.isSingleton) {
            builder.append("return new ")
                    .append(className)
                    .append("(context);\n");
        } else {

            builder.append(className)
                    .append(' ')
                    .append("local")
                    .append(" = ")
                    .append(SINGLETON_NAME)
                    .append(";\n")
                    .append(indent)
                    .append("if (local == null) {\n")
                        .append(indent.increment())
                        .append("synchronized(")
                        .append(className)
                        .append(".class) {\n")
                            .append(indent.increment())
                            .append("if (local == null) {\n")
                                .append(indent.increment())
                                .append("local = ")
                                .append(SINGLETON_NAME)
                                .append(" = new ")
                                .append(className)
                                .append("(context);\n")
                            .append(indent.decrement())
                            .append("}\n")
                        .append(indent.decrement())
                        .append("}\n")
                    .append(indent.decrement())
                    .append("}\n")
                    .append(indent)
                    .append("return local;\n");
        }

        builder.append(indent.decrement())
                .append("}\n\n");

        return builder.toString();
    }

    private static String createSet(Indent indent, JsonLibraryGenerator jsonLibraryGenerator, KeyHolder key, boolean catchJsonExceptions) {
        if (jsonLibraryGenerator == null
                || !key.isJson) {
            return createSimpleSet(key.keyName, "value");
        }

        final StringBuilder builder = new StringBuilder();

        if (catchJsonExceptions) {
            builder.append("try {\n")
                    .append(indent.increment());
        }

        builder.append("final String json;\n")
                .append(indent)
                .append("if (value != null) { json = ")
                .append(jsonLibraryGenerator.toJson("value"))
                .append(" }\n")
                .append(indent)
                .append("else { ")
                .append("json = null; }\n")
                .append(indent)
                .append(createSimpleSet(key.keyName, "json"));

        if (catchJsonExceptions) {
            builder.append("\n")
                    .append(indent.decrement())
                    .append("} catch(java.lang.Throwable t) {\n")
                    .append(indent.increment())
                    .append("onJsonExceptionHandled(t);\n")
                    .append(indent.decrement())
                    .append("}");

        }

        return builder.toString();
    }

    private static String createSimpleSet(String name, String value) {
        return "mPref.set(\"" + name + "\", " + value + ");";
    }

    private static String createGet(Indent indent, JsonLibraryGenerator jsonLibraryGenerator, KeyHolder key, boolean catchJsonExceptions) {
        if (jsonLibraryGenerator == null
                || !key.isJson) {
            return "return " + createSimpleGet(key.keyElement, key.keyName, key.keyDefaultValue);
        }

        final StringBuilder builder = new StringBuilder();

        if (catchJsonExceptions) {
            builder.append("try {\n")
                    .append(indent.increment());
        }

        builder.append("final String json = ")
                .append(createSimpleGet(key.keyElement, key.keyName, null))
                .append("\n")
                .append(indent)
                .append("if (json == null) { return null; }\n")
                .append(indent)
                .append("return ")
                .append(jsonLibraryGenerator.fromJson("json", key.keyElement.asType()));

        if (catchJsonExceptions) {
            builder.append("\n")
                    .append(indent.decrement())
                    .append("} catch(java.lang.Throwable t) {\n")
                    .append(indent.increment())
                    .append("onJsonExceptionHandled(t);\n")
                    .append(indent)
                    .append("return null;\n")
                    .append(indent.decrement())
                    .append("}");
        }

        return builder.toString();
    }

    private static String createSimpleGet(Element element, String name, String defaultValue) {

        final TypeMirror mirror = element.asType();

        final String def;
        if (defaultValue == null) {

            switch (mirror.getKind()) {
                case INT:
                    def = "DEF_INT";
                    break;

                case LONG:
                    def = "DEF_LONG";
                    break;

                case FLOAT:
                    def = "DEF_FLOAT";
                    break;

                case BOOLEAN:
                    def = "DEF_BOOL";
                    break;

                default:
                    def = "null";
            }

        } else {

            final Matcher matcher = EVAULATE_PATTERN.matcher(defaultValue);
            if (matcher.matches()) {
                def = matcher.group(2);
            } else if (mirror.getKind().isPrimitive()) {
                def = defaultValue;
            } else {
                def = "\"" + defaultValue + "\"";
            }
        }

        return "mPref.get(\"" + name + "\", " + def + ");";
    }
}
