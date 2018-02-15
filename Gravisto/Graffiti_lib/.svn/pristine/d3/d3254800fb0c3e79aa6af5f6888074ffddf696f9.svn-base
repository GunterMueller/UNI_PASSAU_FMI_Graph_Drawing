// =============================================================================
//
//   Tool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Tool.java 1002 2006-01-03 13:21:54Z forster $

package org.graffiti.plugins.modes.deprecated;

import java.util.prefs.Preferences;

import javax.swing.event.MouseInputListener;

import org.graffiti.graph.Graph;

/**
 * A <code>Tool</code> executes a specified action on a
 * <code>ConstrainedGraph</code>.
 * 
 * @see MouseInputListener
 * @deprecated
 */
@Deprecated
public interface Tool extends MouseInputListener {

    /**
     * Returns true if the tool is active.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isActive();

    /**
     * States whether this class wants to be registered as a
     * <code>SelectionListener</code>.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isSelectionListener();

    /**
     * States whether this class wants to be registered as a
     * <code>SessionListener</code>.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isSessionListener();

    /**
     * States whether this class wants to be registered as a
     * <code>ViewListener</code>, i.e. if it wants to get informed when another
     * view in the same session becomes active. This method is not called when
     * another session is activated. Implement <code>SessionListener</code> if
     * you are interested in session changed events.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isViewListener();

    /**
     * Called when the tool is activated.
     */
    public void activate();

    /**
     * Resets the state of the tool. Called when another tool is activated.
     */
    public void deactivate();

    /**
     * Sets the graph this tool works on.
     * 
     * @param g
     *            the graph this tool should work on.
     */
    void setGraph(Graph g);

    /**
     * Sets the preferences of this tool.
     * 
     * @param p
     *            the preferences of this tool.
     */
    void setPrefs(Preferences p);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
