package org.graffiti.plugins.scripting.delegate;

import java.lang.reflect.Field;

import org.graffiti.plugins.scripting.exceptions.ScriptingException;

abstract class FieldDelegateFactory<T> {
    private Field field;

    protected FieldDelegateFactory(Field field) {
        this.field = field;
    }

    public abstract FieldDelegate<T> create(ObjectDelegate thisObject)
            throws ScriptingException;

    public Field getField() {
        return field;
    }
}
