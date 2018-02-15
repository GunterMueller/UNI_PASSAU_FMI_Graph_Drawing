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
public class EdgeDirectedEdit extends GraffitiAbstractUndoableEdit {

    /**
     * 
     */
    private static final long serialVersionUID = 569494812043326640L;
    private Edge edge;

    /**
     * @param geMap
     */
    public EdgeDirectedEdit(Edge edge, Map<GraphElement, GraphElement> geMap) {
        super(geMap);

        this.edge = edge;

    }

    /*
     * @see org.graffiti.undo.GraffitiAbstractUndoableEdit#execute()
     */
    @Override
    public void execute() {
    }

    /**
     * @see javax.swing.undo.UndoableEdit#redo()
     */
    @Override
    public void redo() {
        super.redo();

        edge.setDirected(!edge.isDirected());
    }

    /**
     * @see javax.swing.undo.UndoableEdit#undo()
     */
    @Override
    public void undo() {
        super.undo();

        edge.setDirected(!edge.isDirected());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
