package org.graffiti.plugins.scripting.js;

import org.graffiti.plugins.scripting.ScriptingContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * {@code ScriptingContext} represents the runtime context of an executing
 * script.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class RhinoContext extends Context implements ScriptingContext {
    /**
     * The time when the currently executed script started.
     */
    private long startTime;

    /**
     * Constructs a {@code ScriptingContext}. The deprecation warning is safely
     * ignored as the default constructor of {@link Context} is deprecated only
     * to call from outside a {@link ContextFactory}.
     */
    @SuppressWarnings("deprecation")
    public RhinoContext() {
    }

    /**
     * Sets the time when the currently executed script started.
     * 
     * @param startTime
     *            time to set in milliseconds.
     * @see System#currentTimeMillis()
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the time when the currently executed script started.
     * 
     * @return the time in milliseconds when the currently executed script
     *         started.
     * @see System#currentTimeMillis()
     */
    public long getStartTime() {
        return startTime;
    }
}
