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
public class EdgeSourceNodeEdit extends GraffitiAbstractUndoableEdit {

    /**
     * 
     */
    private static final long serialVersionUID = 5441136528006686532L;

    private Edge edge;

    private Node sourceNode;

    /**
     * @param geMap
     */
    public EdgeSourceNodeEdit(Edge edge, Map<GraphElement, GraphElement> geMap) {
        super(geMap);

        this.edge = edge;
        this.sourceNode = edge.getSource();
    }

    /*
     * @see org.graffiti.undo.GraffitiAbstractUndoableEdit#execute()
     */
    @Override
    public void execute() {
    }

    private void changeSource() {
        Node sourceNode = edge.getSource();
        edge.setSource(this.sourceNode);
        this.sourceNode = sourceNode;
    }

    /**
     * @see javax.swing.undo.UndoableEdit#redo()
     */
    @Override
    public void redo() {
        super.redo();

        changeSource();
    }

    /**
     * @see javax.swing.undo.UndoableEdit#undo()
     */
    @Override
    public void undo() {
        super.undo();

        changeSource();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
