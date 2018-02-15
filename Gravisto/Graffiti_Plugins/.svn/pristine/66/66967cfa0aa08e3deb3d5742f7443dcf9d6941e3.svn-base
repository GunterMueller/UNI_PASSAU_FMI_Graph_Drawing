package org.graffiti.plugins.scripting.js;

import org.graffiti.plugins.scripting.delegate.ReflectiveDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.mozilla.javascript.Scriptable;

public class RhinoWrapper implements Scriptable {
    protected abstract class GuardedMethod<T> {
        public abstract T execute() throws ScriptingException;
    }

    protected final ReflectiveDelegate delegate;
    protected final JavaScriptEngine engine;

    /**
     * The parent scope.
     */
    private Scriptable parentScope;

    /**
     * The prototype.
     */
    private Scriptable prototype;

    public RhinoWrapper(ReflectiveDelegate delegate, JavaScriptEngine engine) {
        this.delegate = delegate;
        this.engine = engine;
    }

    /**
     * {@inheritDoc}
     */
    public final Scriptable getParentScope() {
        return parentScope;
    }

    /**
     * {@inheritDoc}
     */
    public final Scriptable getPrototype() {
        return prototype;
    }

    /**
     * {@inheritDoc}
     */
    public final void setParentScope(Scriptable parentScope) {
        this.parentScope = parentScope;
    }

    /**
     * {@inheritDoc}
     */
    public final void setPrototype(Scriptable prototype) {
        this.prototype = prototype;
    }

    /**
     * {@inheritDoc} This implementation does nothing.
     */
    public void delete(final String name) {
        guard(new GuardedMethod<Object>() {
            @Override
            public Object execute() throws ScriptingException {
                delegate.delete(name);
                return null;
            }
        });
    }

    /**
     * {@inheritDoc} This implementation does nothing.
     */
    public void delete(final int index) {
        guard(new GuardedMethod<Object>() {
            @Override
            public Object execute() throws ScriptingException {
                delegate.delete(index);
                return null;
            }
        });
    }

    /**
     * {@inheritDoc} This implementation returns {@link Scriptable#NOT_FOUND}.
     */
    public Object get(final String name, Scriptable start) {
        return guard(new GuardedMethod<Object>() {
            @Override
            public Object execute() throws ScriptingException {
                return engine.wrap(delegate.get(name));
            }
        });
    }

    /**
     * {@inheritDoc} This implementation returns {@link Scriptable#NOT_FOUND}.
     */
    public Object get(final int index, Scriptable start) {
        return guard(new GuardedMethod<Object>() {
            @Override
            public Object execute() throws ScriptingException {
                return engine.wrap(delegate.get(index));
            }
        });
    }

    /**
     * {@inheritDoc} This implementation returns an empty array.
     */
    public Object[] getIds() {
        return delegate.getIndices().toArray();
    }

    protected <T> T guard(GuardedMethod<T> method) {
        try {
            return method.execute();
        } catch (ScriptingException e) {
            throw engine.wrapException(e);
        } catch (Throwable e) {
            throw engine.wrapException(e);
        }
    }

    /**
     * {@inheritDoc} This implementation returns {@code false}.
     */
    public boolean has(String name, Scriptable start) {
        return delegate.has(name);
    }

    /**
     * {@inheritDoc} This implementation returns {@code false}.
     */
    public boolean has(int index, Scriptable start) {
        return delegate.has(index);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasInstance(Scriptable instance) {
        return delegate.getClass().isInstance(engine.unwrap(instance));
    }

    /**
     * {@inheritDoc} This implementation does nothing.
     */
    public void put(final String name, Scriptable start, final Object value) {
        guard(new GuardedMethod<Object>() {
            @Override
            public Object execute() throws ScriptingException {
                delegate.put(name, engine.unwrap(value));
                return null;
            }
        });
    }

    /**
     * {@inheritDoc} This implementation does nothing.
     */
    public void put(final int index, Scriptable start, final Object value) {
        guard(new GuardedMethod<Object>() {
            @Override
            public Object execute() throws ScriptingException {
                delegate.put(index, engine.unwrap(value));
                return null;
            }
        });
    }

    public String getClassName() {
        return delegate.getClassName();
    }

    @SuppressWarnings("unchecked")
    public Object getDefaultValue(Class hint) {
        return delegate.toString();
    }

    public ScriptingDelegate getDelegate() {
        return delegate;
    }
}
