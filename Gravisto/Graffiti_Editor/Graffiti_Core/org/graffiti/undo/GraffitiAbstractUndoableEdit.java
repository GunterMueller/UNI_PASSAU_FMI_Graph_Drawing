// =============================================================================
//
//   GraffitiAbstractUndoableEdit.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiAbstractUndoableEdit.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.undo;

import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;

import org.graffiti.core.Bundle;
import org.graffiti.graph.GraphElement;

/**
 * GraffitiAbstractUndoableEdit
 * 
 * @version $Revision: 5767 $
 */
public abstract class GraffitiAbstractUndoableEdit extends AbstractUndoableEdit {
    /**
     * 
     */
    private static final long serialVersionUID = 1113948583159005894L;

    /**
     * The reference for the map between graph elements recreated after undo
     * processing and original graph elements.
     */
    protected Map<GraphElement, GraphElement> geMap;

    /** The <code>Bundle</code> of the main frame. */
    protected static final Bundle coreBundle = Bundle.getCoreBundle();

    /**
     * Creates a new <code>GraffitiAbstractUndoableEdit</code> object.
     * 
     * @param geMap
     *            reference to the map supports the undo operations.
     */
    public GraffitiAbstractUndoableEdit(Map<GraphElement, GraphElement> geMap) {
        super();
        this.geMap = geMap;
    }

    /**
     * Executes action for corresponding tools.
     */
    public abstract void execute();

    /**
     * Returns a new graph element reference through the mapping from old ones.
     * 
     * @param ge
     *            a graph element has to be updated.
     * 
     * @return a new existing graph element mapped from the given ones.
     */
    protected GraphElement getCurrentGraphElement(GraphElement ge) {
        while (geMap.containsKey(ge)) {
            ge = geMap.get(ge);
        }
        return ge;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
