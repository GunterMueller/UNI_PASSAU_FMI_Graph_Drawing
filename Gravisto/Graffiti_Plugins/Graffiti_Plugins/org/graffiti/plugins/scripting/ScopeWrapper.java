package org.graffiti.plugins.scripting;

import org.graffiti.plugins.scripting.delegate.ConstructorDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;

/**
 * Wrapper of a scope for a scripting engine.
 * 
 * @author Andreas Glei&szlig;ner
 */
public abstract class ScopeWrapper {
    /**
     * The engine using this wrapper.
     */
    private ScriptingEngine engine;

    /**
     * Constructs a scope wrapper for the specified scripting engine.
     * 
     * @param engine
     *            the scripting engine of the wrapper to construct.
     */
    protected ScopeWrapper(ScriptingEngine engine) {
        this.engine = engine;
    }

    /**
     * Creates the scope wrapper for a future child scope of the scope wrapped
     * by this scope wrapper.
     */
    protected abstract ScopeWrapper createChildScope();

    protected abstract void addNativeJavaClass(String name, Class<?> clazz);

    protected abstract void addConstructor(ConstructorDelegate constructor);

    protected void put(String name, Boolean bool) {
        putWrapped(name, engine.wrap(bool));
    }

    protected void put(String name, Number number) {
        putWrapped(name, engine.wrap(number));
    }

    protected void put(String name, String string) {
        putWrapped(name, engine.wrap(string));
    }

    protected void put(String name, ScriptingDelegate delegate) {
        putWrapped(name, engine.wrap(delegate));
    }

    protected final void put(String name, Object value) {
        if (value instanceof Boolean) {
            put(name, (Boolean) value);
        } else if (value instanceof Number) {
            put(name, (Number) value);
        } else if (value instanceof String) {
            put(name, (String) value);
        } else if (value instanceof ScriptingDelegate) {
            put(name, (ScriptingDelegate) value);
        } else
            throw new IllegalArgumentException();
    }

    protected void putConst(String name, Boolean bool) {
        putWrappedConst(name, engine.wrap(bool));
    }

    protected void putConst(String name, Number number) {
        putWrappedConst(name, engine.wrap(number));
    }

    protected void putConst(String name, String string) {
        putWrappedConst(name, engine.wrap(string));
    }

    protected void putConst(String name, ScriptingDelegate delegate) {
        putWrappedConst(name, engine.wrap(delegate));
    }

    protected final void putConst(String name, Object value) {
        if (value instanceof Boolean) {
            putConst(name, (Boolean) value);
        } else if (value instanceof Number) {
            putConst(name, (Number) value);
        } else if (value instanceof String) {
            putConst(name, (String) value);
        } else if (value instanceof ScriptingDelegate) {
            putConst(name, (ScriptingDelegate) value);
        } else
            throw new IllegalArgumentException();
    }

    protected abstract void putWrapped(String name, Object wrappedValue);

    protected abstract void putWrappedConst(String name, Object wrappedValue);

    protected abstract void seal();
}
