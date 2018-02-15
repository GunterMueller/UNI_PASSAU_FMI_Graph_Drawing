// =============================================================================
//
//   PythonFunctionWrapper.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting.python;

import org.graffiti.plugins.scripting.delegate.FunctionDelegate;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.python.core.Py;
import org.python.core.PyObject;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class PythonFunctionWrapper extends PythonWrapper {
    /**
     * 
     */
    private static final long serialVersionUID = 5899021635442864881L;
    private FunctionDelegate functionDelegate;

    public PythonFunctionWrapper(FunctionDelegate delegate, PythonEngine engine) {
        super(delegate, engine);
        functionDelegate = delegate;
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
                return (PyObject) engine.wrap(functionDelegate.call(engine
                        .unwrap(args)));
            }
        });
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
