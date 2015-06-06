package ru.noties.simpleprefs.processor;

import java.util.List;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Created by Dimitry Ivanov on 06.06.2015.
 */
public class GSONLibraryGenerator extends AbsJsonLibraryGenerator {

    private static final String VAR = "mGson";
    private static final String VAR_STATIC = "sGson";

    private static final String GSON_NAME = "com.google.gson.Gson";
    private static final String GSON_BUILDER_NAME = "com.google.gson.GsonBuilder";
    private static final String GSON_REGISTER_TYPE_ADAPTER = "registerTypeAdapter";
    private static final String TYPE_TOKEN_PATTERN = "new com.google.gson.reflect.TypeToken<%1$s>(){}.getType()";

    GSONLibraryGenerator(Logger logger, Types types, boolean isStatic) {
        super(logger, types, isStatic);
    }

    @Override
    public String getJsonVariableName() {
        if (isStatic) {
            return VAR_STATIC;
        }
        return VAR;
    }

    @Override
    public String initializeJsonVariable(Indent indent, String[] types, String[] typeAdapters) {

        final StringBuilder builder = new StringBuilder();

        builder.append(indent)
                .append("private ");
        if (isStatic) {
            builder.append("static ");
        }
        builder.append("final ")
                .append(GSON_NAME)
                .append(' ')
                .append(getJsonVariableName())
                .append(" = ");

        if (types == null
                || typeAdapters == null) {
            builder.append("new ")
                    .append(GSON_NAME)
                    .append("();");
        } else {
            builder.append("new ")
                    .append(GSON_BUILDER_NAME)
                    .append("()\n");
            indent.increment();

            for (int i = 0, size = types.length; i < size; i++) {
                builder.append(indent)
                        .append('.')
                        .append(GSON_REGISTER_TYPE_ADAPTER)
                        .append("(")
                                .append(types[i])
                                .append(".class, ")
                                .append("new ")
                                .append(typeAdapters[i])
                                .append("()")
                        .append(")\n");
            }

            builder.append(indent)
                    .append(".create();");

            indent.decrement();
        }


        return builder.toString();
    }

    @Override
    public String toJson(String varName) {
        return getJsonVariableName() + ".toJson(" + varName + ");";
    }

    @Override
    public String fromJson(String jsonVar, TypeMirror typeMirror) {
        return getJsonVariableName() + ".fromJson(" + jsonVar + ", " + getType(typeMirror) + ");";
    }

    private static String getType(TypeMirror mirror) {

        if (mirror instanceof DeclaredType) {
            final DeclaredType declaredType = (DeclaredType) mirror;
            final List<? extends TypeMirror> args = declaredType.getTypeArguments();
            if (args != null
                    && args.size() > 0) {
                return String.format(TYPE_TOKEN_PATTERN, mirror.toString());
            }
        }

        return mirror.toString() + ".class";
    }
}
