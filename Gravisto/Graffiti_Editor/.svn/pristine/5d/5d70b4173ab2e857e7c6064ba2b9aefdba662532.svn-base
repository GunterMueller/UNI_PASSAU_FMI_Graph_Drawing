// =============================================================================
//
//   PythonConsole.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting.python;

import java.io.ByteArrayOutputStream;

import org.graffiti.plugins.scripting.Console;
import org.graffiti.plugins.scripting.ResultCallback;
import org.python.util.InteractiveConsole;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class PythonConsole implements Console {
    private InteractiveConsole console;

    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;

    public PythonConsole(PythonScopeWrapper psw) {
        console = psw.createConsole();

        out = new ByteArrayOutputStream();
        err = new ByteArrayOutputStream();
        console.setOut(out);
        console.setErr(err);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addLine(String line, ResultCallback resultCallback) {
        boolean isComplete = !console.push(line);

        if (isComplete) {
            if (err.size() > 0) {
                resultCallback.reportError(err.toString());
            } else if (out.size() > 0) {
                resultCallback.reportResult(out.toString());
            } else {
                resultCallback.reportResult();
            }
            out.reset();
            err.reset();
        }

        return isComplete;
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        console.resetbuffer();
        out.reset();
        err.reset();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
