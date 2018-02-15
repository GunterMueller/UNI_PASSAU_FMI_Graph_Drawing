// =============================================================================
//
//   MacroTimeout.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ScriptingTimeout extends Error {
    /**
     * 
     */
    private static final long serialVersionUID = 30459503392772005L;
    private double elapsedTime;

    public ScriptingTimeout(double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public String getMessage() {
        return String.format(ScriptingPlugin.getString("error.timeout"),
                elapsedTime);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
