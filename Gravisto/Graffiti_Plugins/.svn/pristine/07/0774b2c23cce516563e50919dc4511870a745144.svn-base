package org.graffiti.plugins.scripting.delegate;

import java.lang.reflect.Array;

/**
 * Common superclass of {@code FunctionDelegate} and {@code ConstructorDelegate}
 * .
 * 
 * @author Andreas Glei&szlig;ner
 * @see FunctionDelegate
 * @see ConstructorDelegate
 */
public abstract class CallableDelegate extends ReflectiveDelegate {
    protected final String name;

    protected CallableDelegate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected Object[] match(Class<?>[] types, Object[] args, int prefixLength) {
        int metLen = types.length - prefixLength;
        int argLen = args.length;
        if (metLen > argLen + 1)
            return null;
        Object[] result = new Object[metLen + prefixLength];
        if (metLen == 0) {
            if (argLen == 0)
                return result;
            else
                return null;
        }
        for (int i = 0; i < metLen - 1; i++) {
            if (types[i + prefixLength].isInstance(args[i])) {
                result[i + prefixLength] = args[i];
            } else
                return null;
        }
        if (!types[metLen - 1 + prefixLength].isArray()) {
            if (metLen == argLen
                    && types[metLen - 1 + prefixLength]
                            .isInstance(args[metLen - 1])) {
                result[metLen - 1 + prefixLength] = args[metLen - 1];
                return result;
            } else
                return null;
        }
        Class<?> arrayType = types[metLen - 1 + prefixLength]
                .getComponentType();
        int varArgCount = argLen - metLen + 1;
        Object varArgs = Array.newInstance(arrayType, varArgCount);
        for (int i = 0; i < varArgCount; i++) {
            Object value = args[metLen - 1 + i];
            if (arrayType.isInstance(value)) {
                Array.set(varArgs, i, value);
            } else
                return null;
        }
        result[metLen - 1 + prefixLength] = varArgs;
        return result;
    }
}
