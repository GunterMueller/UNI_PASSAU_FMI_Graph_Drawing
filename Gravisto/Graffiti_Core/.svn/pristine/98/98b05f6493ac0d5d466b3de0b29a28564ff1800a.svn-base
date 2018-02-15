package org.graffiti.plugins.scripting.delegate;

import org.graffiti.plugins.scripting.exceptions.FieldAccessException;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;

public class FieldDelegate<T> {
    protected final Class<T> type;

    public FieldDelegate(Class<T> type) {
        this.type = type;
    }

    public boolean accepts(Object object) {
        return type.isInstance(object);
    }

    public final Class<?> getType() {
        return type;
    }

    public void set(T value) throws ScriptingException {
        throw new FieldAccessException(false);
    }

    public Object get() throws ScriptingException {
        throw new FieldAccessException(true);
    }
}
