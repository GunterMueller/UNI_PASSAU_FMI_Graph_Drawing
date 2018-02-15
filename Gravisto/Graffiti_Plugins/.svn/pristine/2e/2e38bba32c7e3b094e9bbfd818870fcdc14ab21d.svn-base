package org.graffiti.plugins.scripting.delegate;

import org.graffiti.plugins.scripting.ScriptingEngine;
import org.graffiti.plugins.scripting.ScriptingRegistry;

/**
 * Delegate wrapping an object for scripting.
 * 
 * @see ScriptingRegistry
 */
public abstract class ScriptingDelegate {
    /**
     * Represents an undefined value.
     */
    public static final Undefined UNDEFINED = new Undefined();

    /**
     * Creates an wrapper of this delegate for the specified scripting engine.
     * 
     * @param engine
     *            the engine for which to create a wrapper of this delegate.
     */
    public abstract Object createWrapper(ScriptingEngine engine);
}
