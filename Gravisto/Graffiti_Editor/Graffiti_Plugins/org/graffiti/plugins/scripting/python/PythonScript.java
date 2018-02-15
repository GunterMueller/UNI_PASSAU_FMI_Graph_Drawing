// =============================================================================
//
//   PythonScript.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting.python;

import org.graffiti.plugins.scripting.ResultCallback;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.Script;
import org.graffiti.plugins.scripting.ScriptingContext;
import org.graffiti.util.Callback;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class PythonScript implements Script {
    private final PythonEngine engine;
    private final String source;
    private final Scope scope;

    public PythonScript(final PythonEngine engine, final String source,
            final Scope scope) {
        this.engine = engine;
        this.source = source;
        this.scope = scope;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final ResultCallback callback) {
        engine.execute(new Callback<Object, ScriptingContext>() {
            @Override
            public Object call(ScriptingContext t) {
                PythonScopeWrapper psw = (PythonScopeWrapper) scope
                        .getScopeWrapper(engine);
                psw.exec(source, callback);
                return null;
            }
        });
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
