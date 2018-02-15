// =============================================================================
//
//   PythonConstructorWrapper.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting.python;

import org.graffiti.plugins.scripting.delegate.ConstructorDelegate;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.python.core.Py;
import org.python.core.PyObject;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class PythonConstructorWrapper extends PythonWrapper {
    /**
     * 
     */
    private static final long serialVersionUID = 4697063154188677346L;
    private ConstructorDelegate constructorDelegate;

    public PythonConstructorWrapper(ConstructorDelegate delegate,
            PythonEngine engine) {
        super(delegate, engine);
        constructorDelegate = delegate;
    }

    @Override
    public PyObject __call__(final PyObject args[], String keywords[]) {
        if (keywords.length > 0)
            throw Py.TypeError(String.format(
                    "'%s' object does not support keyword arguments", getType()
                            .fastGetName()));

        return guard(new GuardedMethod<PyObject>() {
            @Override
            public PyObject execute() throws ScriptingException {
                return (PyObject) engine.wrap(constructorDelegate
                        .construct(engine.unwrap(args)));
            }
        });
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
