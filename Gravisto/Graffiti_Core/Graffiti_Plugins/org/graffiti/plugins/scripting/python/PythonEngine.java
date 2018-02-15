// =============================================================================
//
//   PythonEngine.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting.python;

import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.Script;
import org.graffiti.plugins.scripting.ScriptingContext;
import org.graffiti.plugins.scripting.ScriptingEngine;
import org.graffiti.plugins.scripting.delegate.BlackBoxDelegate;
import org.graffiti.plugins.scripting.delegate.ConstructorDelegate;
import org.graffiti.plugins.scripting.delegate.FunctionDelegate;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.Undefined;
import org.graffiti.util.Callback;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class PythonEngine extends ScriptingEngine {
    public static final String LANGUAGE_ID = "Python2.5";

    private static final String NAME = PythonPlugin.getString("name");

    /**
     * The plugin providing this engine.
     */
    protected static PythonPlugin plugin;

    /**
     * Returns the {@code PythonEngine} singleton.
     * 
     * @return the {@code PythonEngine} singleton.
     * @throws IllegalStateException
     *             if the {@link PythonPlugin} is not loaded.
     */
    public static PythonEngine get() {
        if (plugin != null) {
            PythonEngine engine = plugin.getEngine();
            if (engine != null)
                return engine;
        }
        throw new IllegalStateException("Requires PythonEngine plugin.");
    }

    protected PythonEngine(PythonPlugin plugin) {
        PythonEngine.plugin = plugin;
        // factory = new RhinoContextFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PythonScopeWrapper createRootScope() {
        return execute(new Callback<PythonScopeWrapper, ScriptingContext>() {
            @Override
            public PythonScopeWrapper call(ScriptingContext t) {
                return new PythonScopeWrapper(PythonEngine.this, null);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Script createScript(String source, Scope scope) {
        return new PythonScript(this, source, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PythonConsole createConsole(Scope scope) {
        return new PythonConsole((PythonScopeWrapper) scope
                .getScopeWrapper(this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ScriptingContext enterContext() {
        return new PythonContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void exitContext() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object unwrap(Object object) {
        if (object instanceof PythonWrapper)
            return ((PythonWrapper) object).getDelegate();
        else
            return ((PyObject) object).__tojava__(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PyObject wrap(Boolean bool) {
        return Py.newBoolean(bool);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PyObject wrap(Number number) {
        if (number instanceof Byte || number instanceof Short
                || number instanceof Integer)
            return Py.newInteger(number.intValue());
        else if (number instanceof Double)
            return Py.newFloat(number.doubleValue());
        else if (number instanceof Float)
            return Py.newFloat(number.floatValue());
        else if (number instanceof Long)
            return Py.newLong(number.longValue());
        else
            return Py.java2py(number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PyObject wrap(String string) {
        return Py.newString(string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PyObject wrap(Undefined undefined) {
        return Py.None;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PyObject wrapNull() {
        return Py.None;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> PyObject createWrapper(BlackBoxDelegate<T> delegate) {
        return Py.java2py(delegate.getObject());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PyObject createWrapper(ConstructorDelegate delegate) {
        return new PythonConstructorWrapper(delegate, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PythonFunctionWrapper createWrapper(FunctionDelegate delegate) {
        return new PythonFunctionWrapper(delegate, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PyObject createWrapper(ObjectDelegate delegate) {
        return new PythonWrapper(delegate, this);
    }

    public PyException wrapException(Throwable e) {
        return Py.JavaError(e);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
