package org.graffiti.plugins.scripting.js;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

/**
 * {@code ContextFactory}, which creates instances of {@code RhinoContext}.
 * 
 * @see RhinoContext
 */
public class RhinoContextFactory extends ContextFactory {
    /**
     * Denotes the maximum execution time of a script.
     */
    private static final long TIMEOUT = 5000;

    /**
     * The elapsed execution is observed every INSTRUCTION_OBSERVER_THRESHOLD
     * instructions.
     */
    private static final int INSTRUCTION_OBSERVER_THRESHOLD = 10000;

    static {
        // Sets global context factory to an instance of this class.
        // TODO: not necessary
        // RhinoContext.initGlobal(new RhinoContextFactory());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doTopCall(Callable callable, Context cx, Scriptable scope,
            Scriptable thisObj, Object[] args) {
        ((RhinoContext) cx).setStartTime(System.currentTimeMillis());
        return super.doTopCall(callable, cx, scope, thisObj, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasFeature(Context cx, int featureIndex) {
        switch (featureIndex) {
        case Context.FEATURE_NON_ECMA_GET_YEAR:
            return false;
            // TODO...
        }
        return super.hasFeature(cx, featureIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RhinoContext makeContext() {
        RhinoContext cx = new RhinoContext();
        cx.setOptimizationLevel(-1);
        cx.setInstructionObserverThreshold(INSTRUCTION_OBSERVER_THRESHOLD);
        return cx;
    }

    /**
     * {@inheritDoc} This implementation interrupts the script execution if the
     * running time exceeds some defined limits.
     */
    @Override
    protected void observeInstructionCount(Context cx, int instructionCount) {
        long currentTime = System.currentTimeMillis();
        long startTime = ((RhinoContext) cx).getStartTime();
        if (currentTime - startTime > TIMEOUT)
            throw new ScriptingTimeoutError((currentTime - startTime) / 1000.0);
    }
}
