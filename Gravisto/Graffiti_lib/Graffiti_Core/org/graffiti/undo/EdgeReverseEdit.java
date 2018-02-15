// =============================================================================
//
//   EdgeReverseEdit.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.undo;

import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;

/**
 * @author Kathrin Hanauer
 * @version $Revision$ $Date$
 */
public class EdgeReverseEdit extends GraffitiAbstractUndoableEdit {

    /**
     * 
     */
    private static final long serialVersionUID = -5670524757420319152L;
    private Edge edge;

    /**
     * @param geMap
     */
    public EdgeReverseEdit(Edge edge, Map<GraphElement, GraphElement> geMap) {
        super(geMap);

        this.edge = edge;

    }

    /*
     * @see org.graffiti.undo.GraffitiAbstractUndoableEdit#execute()
     */
    @Override
    public void execute() {
    }

    private void reverse() {
        if (edge.getGraph() == null)
            return;

        edge.reverse();
    }

    /**
     * @see javax.swing.undo.UndoableEdit#redo()
     */
    @Override
    public void redo() {
        super.redo();

        reverse();
    }

    /**
     * @see javax.swing.undo.UndoableEdit#undo()
     */
    @Override
    public void undo() {
        super.undo();

        reverse();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
