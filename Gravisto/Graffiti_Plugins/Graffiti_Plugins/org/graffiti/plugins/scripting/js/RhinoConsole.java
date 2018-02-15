// =============================================================================
//
//   RhinoConsole.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting.js;

import org.graffiti.plugins.scripting.Console;
import org.graffiti.plugins.scripting.ResultCallback;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.Script;

/**
 * Implements the console for the {@code JavaScriptEngine}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see JavaScriptEngine
 */
public class RhinoConsole implements Console {
    private JavaScriptEngine engine;
    private Scope scope;
    private StringBuilder builder;

    public RhinoConsole(JavaScriptEngine engine, Scope scope) {
        this.engine = engine;
        this.scope = scope;
        builder = new StringBuilder();
    }

    /**
     * {@inheritDoc}
     */
    public boolean addLine(String line, ResultCallback resultCallback) {
        if (builder.length() > 0) {
            builder.append('\n');
        }
        builder.append(line);
        boolean isValid = engine.isValid(builder.toString(), scope);

        if (isValid) {
            Script script = engine.createScript(builder.toString(), scope);
            script.execute(resultCallback);
            builder.setLength(0);
        }

        return isValid;
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        builder.setLength(0);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
