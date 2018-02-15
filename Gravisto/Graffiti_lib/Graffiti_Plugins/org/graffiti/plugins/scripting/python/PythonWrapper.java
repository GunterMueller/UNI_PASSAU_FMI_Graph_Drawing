// =============================================================================
//
//   PythonWrapper.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting.python;

import java.util.Iterator;

import org.graffiti.plugins.scripting.delegate.ReflectiveDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.python.core.PyException;
import org.python.core.PyObject;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class PythonWrapper extends PyObject {
    /**
     * 
     */
    private static final long serialVersionUID = -3022354714982876351L;

    protected abstract class GuardedMethod<T> {
        public abstract T execute() throws ScriptingException;
    }

    protected final ReflectiveDelegate delegate;
    protected final PythonEngine engine;

    public PythonWrapper(ReflectiveDelegate delegate, PythonEngine engine) {
        this.delegate = delegate;
        this.engine = engine;
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

    public ScriptingDelegate getDelegate() {
        return delegate;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public PyObject __finditem__(final PyObject key) {
        return guard(new GuardedMethod<PyObject>() {
            @Override
            public PyObject execute() throws ScriptingException {
                Integer index = null;
                try {
                    index = key.asInt();
                } catch (PyException e) {
                    index = null;
                }
                if (index != null)
                    return (PyObject) engine.wrap(delegate.get(index));
                else
                    return (PyObject) engine.wrap(delegate.get(key.toString()));
            }
        });
    }

    @Override
    public void __setitem__(final PyObject key, final PyObject value) {
        guard(new GuardedMethod<PyObject>() {
            @Override
            public PyObject execute() throws ScriptingException {
                Integer index = null;
                try {
                    index = key.asInt();
                } catch (PyException e) {
                    index = null;
                }
                if (index != null) {
                    delegate.put(index, engine.unwrap(value));
                } else {
                    delegate.put(key.toString(), engine.unwrap(value));
                }
                return null;
            }
        });
    }

    @Override
    public void __setattr__(final String name, final PyObject value) {
        guard(new GuardedMethod<Object>() {
            @Override
            public Object execute() throws ScriptingException {
                delegate.put(name, engine.unwrap(value));
                return null;
            }
        });
    }

    @Override
    public PyObject __findattr_ex__(final String name) {
        return guard(new GuardedMethod<PyObject>() {
            @Override
            public PyObject execute() throws ScriptingException {
                return (PyObject) engine.wrap(delegate.get(name));
            }
        });
    }

    @Override
    public void __delattr__(final String name) {
        guard(new GuardedMethod<Object>() {
            @Override
            public Object execute() throws ScriptingException {
                delegate.delete(name);
                return null;
            }
        });
    }

    @Override
    public void __delitem__(final PyObject key) {
        guard(new GuardedMethod<PyObject>() {
            @Override
            public PyObject execute() throws ScriptingException {
                Integer index = null;
                try {
                    index = key.asInt();
                } catch (PyException e) {
                    index = null;
                }
                if (index != null) {
                    delegate.delete(index);
                } else {
                    delegate.delete(key.toString());
                }
                return null;
            }
        });
    }

    @Override
    public boolean __contains__(final PyObject key) {
        return guard(new GuardedMethod<Boolean>() {
            @Override
            public Boolean execute() throws ScriptingException {
                Integer index = null;
                try {
                    index = key.asInt();
                } catch (PyException e) {
                    index = null;
                }
                if (index != null)
                    return delegate.has(index);
                else
                    return delegate.has(key.toString());
            }
        });
    }

    @Override
    public PyObject __iter__() {
        final Iterator<Integer> iterator = delegate.getIndices().iterator();
        return new PyObject() {
            /**
             * 
             */
            private static final long serialVersionUID = 5352710279354751173L;

            @Override
            public PyObject __iternext__() {
                if (iterator.hasNext())
                    return guard(new GuardedMethod<PyObject>() {
                        @Override
                        public PyObject execute() throws ScriptingException {
                            return (PyObject) engine.wrap(delegate.get(iterator
                                    .next()));
                        }
                    });
                else
                    return null;
            }
        };
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
