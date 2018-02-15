// =============================================================================
//
//   AddEdgeEdit.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AddEdgeEdit.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.undo;

import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;

/**
 * Class <code>AddNodeEdit</code> makes the add edge action undoable.
 * 
 * @author Walter Wirch
 * @version $Revision: 5767 $
 */
public class AddEdgeEdit extends GraphElementsEdit {

    /**
     * 
     */
    private static final long serialVersionUID = 8891254550008800643L;
    /** added edge */
    private Edge edge;

    /**
     * Constructor for AddEdgeEdit.
     * 
     * @param edge
     * @param graph
     * @param geMap
     */
    public AddEdgeEdit(Edge edge, Graph graph,
            Map<GraphElement, GraphElement> geMap) {
        super(graph, geMap);
        this.edge = edge;
    }

    /**
     * Used to display the name for this edit.
     * 
     * @return the name of this edit.
     * 
     * @see javax.swing.undo.UndoableEdit
     */
    @Override
    public String getPresentationName() {
        return coreBundle.getString("undo.addEdge");
    }

    /*
     * @see org.graffiti.undo.GraffitiAbstractUndoableEdit#execute()
     */
    @Override
    public void execute() {
    }

    /**
     * Adds the same edge that was added through the method that created this
     * edit.
     */
    @Override
    public void redo() {
        super.redo();

        Node source = (Node) getCurrentGraphElement(edge.getSource());
        Node target = (Node) getCurrentGraphElement(edge.getTarget());
        Edge newEdge = graph.addEdgeCopy(edge, source, target);
        geMap.put(edge, newEdge);
    }

    /**
     * Deletes the edge whose addition is stored in this edit.
     */
    @Override
    public void undo() {
        super.undo();

        edge = (Edge) getCurrentGraphElement(edge);
        graph.deleteEdge(edge);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
