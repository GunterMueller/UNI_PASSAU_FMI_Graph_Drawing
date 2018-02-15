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
import org.graffiti.graph.Node;

/**
 * @author Kathrin Hanauer
 * @version $Revision$ $Date$
 */
public class EdgeTargetNodeEdit extends GraffitiAbstractUndoableEdit {

    /**
     * 
     */
    private static final long serialVersionUID = 7434811695198103807L;

    private Edge edge;

    private Node targetNode;

    /**
     * @param geMap
     */
    public EdgeTargetNodeEdit(Edge edge, Map<GraphElement, GraphElement> geMap) {
        super(geMap);

        this.edge = edge;
        this.targetNode = edge.getTarget();
    }

    /*
     * @see org.graffiti.undo.GraffitiAbstractUndoableEdit#execute()
     */
    @Override
    public void execute() {
    }

    private void changeTarget() {
        Node targetNode = edge.getTarget();
        edge.setTarget(this.targetNode);
        this.targetNode = targetNode;
    }

    /**
     * @see javax.swing.undo.UndoableEdit#redo()
     */
    @Override
    public void redo() {
        super.redo();

        changeTarget();
    }

    /**
     * @see javax.swing.undo.UndoableEdit#undo()
     */
    @Override
    public void undo() {
        super.undo();

        changeTarget();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
