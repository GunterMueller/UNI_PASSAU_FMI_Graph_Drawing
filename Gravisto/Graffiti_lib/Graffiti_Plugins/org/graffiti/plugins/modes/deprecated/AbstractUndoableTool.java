// =============================================================================
//
//   AbstractUndoableTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractUndoableTool.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.deprecated;

import java.util.Map;

import javax.swing.undo.UndoableEditSupport;

import org.graffiti.graph.GraphElement;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;
import org.graffiti.undo.Undoable;

/**
 * Superclass for all tools that provide undo information for their actions.
 * 
 * @version $Revision: 5766 $
 * @deprecated
 */
@Deprecated
public abstract class AbstractUndoableTool extends AbstractTool implements
        Undoable {

    /**
     * The reference for the map between graph elements recreated after undo
     * processing and original graph elements
     */
    protected Map<GraphElement, GraphElement> geMap;

    /** This object helps doing undo properly. */
    protected UndoableEditSupport undoSupport;

    /**
     * Specifies if this tool wants to receive selectionChanged events.
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isSelectionListener() {
        return true;
    }

    /**
     * Specifies if this tool wants to receive sessionChanged events.
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isSessionListener() {
        return true;
    }

    /**
     * Sets the undo support object this object uses.
     * 
     * @param us
     *            the undo support object this object uses.
     */
    public void setUndoSupport(UndoableEditSupport us) {
        this.undoSupport = us;
    }

    /**
     * @see org.graffiti.session.SessionListener#sessionChanged(Session)
     */
    @Override
    public void sessionChanged(Session s) {
        super.sessionChanged(s);

        if (s != null) {
            this.geMap = ((EditorSession) s).getGraphElementsMap();
        }
    }

    /**
     * Empty method.
     * 
     * @see org.graffiti.session.SessionListener#sessionDataChanged(Session)
     */
    public void sessionDataChanged(Session s) {
        // nothing to do here
    }

    /**
     * Returne a new graph element reference through the mapping from old ones
     * 
     * @param ge
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected GraphElement getCurrentGraphElement(GraphElement ge) {
        GraphElement newGE = ge;

        if (geMap.containsKey(ge)) {
            newGE = geMap.get(ge);

            while (geMap.containsKey(newGE)) {
                newGE = geMap.get(newGE);
            }
        }

        return newGE;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
