// =============================================================================
//
//   PythonScopeWrapper.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting.python;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.plugins.scripting.ResultCallback;
import org.graffiti.plugins.scripting.ScopeWrapper;
import org.graffiti.plugins.scripting.ScriptingContext;
import org.graffiti.plugins.scripting.delegate.ConstructorDelegate;
import org.graffiti.util.Callback;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyType;
import org.python.util.InteractiveConsole;
import org.python.util.PythonInterpreter;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class PythonScopeWrapper extends ScopeWrapper {
    public class PyWrapper extends PyObject {
        /**
         * 
         */
        private static final long serialVersionUID = 195527844992046168L;

        /**
         * {@inheritDoc}
         */
        @Override
        public void __delitem__(PyObject key) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PyObject __finditem__(PyObject key) {
            return find(key.toString());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void __setitem__(PyObject key, PyObject value) {
            String skey = key.toString();

            if (!isUnmodifiable(skey)) {
                variables.put(skey, value);
            }
        }

        @Override
        public PyObject __findattr_ex__(String name) {
            // Presence of __getitem__ and __setitem__ is checked
            // before this can be used as a dictionary for
            // local and global variables. Any value != null suffices.
            if (name.equals("__getitem__") || name.equals("__setitem__"))
                return Py.None;
            else
                return super.__findattr_ex__(name);
        }
    }

    private PythonEngine engine;
    private PythonScopeWrapper parent;
    private PyWrapper pyw;
    private PythonInterpreter interpreter;
    private Map<String, PyObject> constants;
    private Map<String, PyObject> variables;
    private boolean isSealed;

    protected PythonScopeWrapper(PythonEngine engine, PythonScopeWrapper parent) {
        super(engine);
        this.engine = engine;
        this.parent = parent;
        constants = new HashMap<String, PyObject>();
        variables = new HashMap<String, PyObject>();
        pyw = new PyWrapper();
        interpreter = new PythonInterpreter(pyw);
        isSealed = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addConstructor(ConstructorDelegate constructor) {
        throw new UnsupportedOperationException("addConstructor");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addNativeJavaClass(String name, Class<?> clazz) {
        PyType type = PyType.fromClass(clazz);
        constants.put(name, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PythonScopeWrapper createChildScope() {
        return engine
                .execute(new Callback<PythonScopeWrapper, ScriptingContext>() {
                    public PythonScopeWrapper call(ScriptingContext t) {
                        return new PythonScopeWrapper(engine,
                                PythonScopeWrapper.this);
                    }
                });
    }

    private boolean isUnmodifiable(String name) {
        return isSealed || constants.containsKey(name) || parent != null
                && parent.find(name) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void putWrapped(String name, Object wrappedValue) {
        if (!isUnmodifiable(name)) {
            variables.put(name, (PyObject) wrappedValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void putWrappedConst(String name, Object wrappedValue) {
        if (!isUnmodifiable(name)) {
            constants.put(name, (PyObject) wrappedValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void seal() {
        isSealed = true;
    }

    protected void exec(String source, ResultCallback callback) {
        try {
            PyObject pyobj = interpreter.eval(source);
            if (pyobj == null || pyobj == Py.None) {
                callback.reportResult();
            } else {
                callback.reportResult(pyobj.toString());
            }
        } catch (Exception e) {
            callback.reportError(e.toString());
        }
    }

    public InteractiveConsole createConsole() {
        return new InteractiveConsole(pyw);
    }

    private PyObject find(String key) {
        PyObject pyo = variables.get(key);

        if (pyo != null)
            return pyo;

        pyo = constants.get(key);

        if (pyo != null)
            return pyo;
        else if (parent != null)
            return parent.find(key);
        else
            return null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
