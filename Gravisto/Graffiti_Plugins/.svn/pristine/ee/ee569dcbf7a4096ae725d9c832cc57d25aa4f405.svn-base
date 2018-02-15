package org.graffiti.plugins.scripting.delegate;

import org.graffiti.plugins.scripting.ScriptingEngine;

/**
 * Delegate wrapping an object without providing reflection.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class BlackBoxDelegate<T> extends ScriptingDelegate {
    /**
     * The wrapped object.
     */
    private T object;

    /**
     * Creates a delgate wrapping the specified object.
     * 
     * @param object
     *            the object to wrap.
     */
    public BlackBoxDelegate(T object) {
        this.object = object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object createWrapper(ScriptingEngine engine) {
        return engine.createWrapper(this);
    }

    /**
     * Returns the wrapped object.
     * 
     * @return the wrapped object.
     */
    public T getObject() {
        return object;
    }
}
