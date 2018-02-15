package org.graffiti.plugins.scripting;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import org.graffiti.plugins.scripting.delegate.BlackBoxDelegate;
import org.graffiti.plugins.scripting.delegate.ConstructorDelegate;
import org.graffiti.plugins.scripting.delegate.FunctionDelegate;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;
import org.graffiti.plugins.scripting.delegate.Undefined;
import org.graffiti.plugins.scripting.reflect.HelpFormatter;
import org.graffiti.util.Callback;

public abstract class ScriptingEngine {
    private WeakHashMap<ScriptingDelegate, WeakReference<Object>> wrappers;

    protected final ThreadLocal<ScriptingContext> currentContext;

    protected ScriptingEngine() {
        wrappers = new WeakHashMap<ScriptingDelegate, WeakReference<Object>>();
        currentContext = new ThreadLocal<ScriptingContext>();
    }

    protected abstract ScopeWrapper createRootScope();

    public abstract Script createScript(String source, Scope scope);

    public abstract Console createConsole(Scope scope);

    public <T> Object createWrapper(BlackBoxDelegate<T> delegate) {
        throw new UnsupportedOperationException();
    }

    public Object createWrapper(ConstructorDelegate delegate) {
        throw new UnsupportedOperationException();
    }

    public Object createWrapper(FunctionDelegate delegate) {
        throw new UnsupportedOperationException();
    }

    public Object createWrapper(ObjectDelegate delegate) {
        throw new UnsupportedOperationException();
    }

    protected abstract ScriptingContext enterContext();

    protected abstract void exitContext();

    /**
     * Executes the specified callback. The {@link ScriptingContext} passed to
     * {@link Callback#call(Object)} must only be accessed during the execution
     * of {@code call}. Nested calls to {@code execute} are allowed and do not
     * degrade performance.
     * 
     * @param callback
     *            the callback to call.
     * @return the return value of the specified callback.
     */
    public final <T> T execute(Callback<T, ScriptingContext> callback) {
        T result = null;

        ScriptingContext context = currentContext.get();
        if (context != null) {
            // Nested calls to execute.
            // We are within a context, so simply run the action.
            result = callback.call(context);
        } else {
            // Outermost call to execute. Imitate ContextFactory.call.
            try {
                context = enterContext();
                currentContext.set(context);
                result = callback.call(context);
            } finally {
                currentContext.set(null);
                exitContext();
            }
        }
        return result;
    }

    public HelpFormatter getHelpFormatter() {
        return new HelpFormatter();
    }

    public abstract String getName();

    public abstract Object wrap(Boolean bool);

    public abstract Object wrap(Number number);

    public abstract Object wrap(String string);

    public Object wrap(ScriptingDelegate delegate) {
        WeakReference<Object> ref = wrappers.get(delegate);
        if (ref != null) {
            Object wrapper = ref.get();
            if (wrapper != null)
                return wrapper;
        }
        Object wrapper = delegate.createWrapper(this);
        wrappers.put(delegate, new WeakReference<Object>(wrapper));
        return wrapper;
    }

    public abstract Object wrap(Undefined undefined);

    protected abstract Object wrapNull();

    public final Object wrap(Object object) {
        if (object == null)
            return wrapNull();
        if (object instanceof Boolean)
            return wrap((Boolean) object);
        else if (object instanceof Number)
            return wrap((Number) object);
        else if (object instanceof String)
            return wrap((String) object);
        else if (object instanceof ScriptingDelegate)
            return wrap((ScriptingDelegate) object);
        else if (object instanceof Undefined)
            return wrap((Undefined) object);
        else
            throw new IllegalArgumentException("Illegal delegate type.");
    }

    public abstract Object unwrap(Object object);

    public final Object[] unwrap(Object[] objects) {
        int len = objects.length;
        Object[] result = new Object[len];
        for (int i = 0; i < len; i++) {
            result[i] = unwrap(objects[i]);
        }
        return result;
    }
}
