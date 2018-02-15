// =============================================================================
//
//   AbstractMode.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractMode.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.deprecated;

import java.util.List;

import org.graffiti.plugin.mode.GraphConstraint;

/**
 * This class provides a skeletal implementation of the interface
 * <code>Mode</code>.
 * 
 * @see Mode
 * @deprecated
 */
@Deprecated
public class AbstractMode implements Mode {

    /**
     * The array of <code>Tool</code>s belonging to the current
     * <code>Mode</code>.
     */
    protected List<Tool> tools;

    /**
     * The name of this mode. Also important for tools:
     * 
     * @see #getId()
     */
    protected String id;

    /** The active tool of the current <code>Mode</code>. */

    // needed? - where?
    // protected Tool activeTool;
    /**
     * The array of <code>GraphConstraints</code> belonging to the current
     * <code>Mode</code>.
     */
    protected GraphConstraint[] constraints;

    // cf. Mode.java
    // public void addTool(Tool t){ }
    // public void removeTool(){ }

    /**
     * Constructs an <code>AbstractMode</code>.
     */
    protected AbstractMode() {
    }

    /**
     * Returns an array containing the <code>GraphConstraint</code>s of the
     * current <code>Mode</code>.
     * 
     * @return an array containing the <code>GraphConstraint</code>s of the
     *         current <code>Mode</code>.
     */
    public GraphConstraint[] getConstraints() {
        return constraints;
    }

    /**
     * Returns the name of this mode. Tools can be added to a mode by adding
     * their ToolButtons to the toolbar with the same name as this mode.
     * 
     * @return the name of this mode.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns an array containing all the tools belonging <code>Mode</code>.
     * 
     * @return an array containing all the tools belonging <code>Mode</code>.
     */
    public List<Tool> getTools() {
        return tools;
    }

    /**
     * Adds the given tool to the mode.
     * 
     * @param t
     *            tool to add.
     */
    public void addTool(Tool t) {
        this.tools.add(t);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
