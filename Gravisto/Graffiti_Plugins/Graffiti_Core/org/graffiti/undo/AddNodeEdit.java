// =============================================================================
//
//   AddNodeEdit.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AddNodeEdit.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.undo;

import java.util.Map;

import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;

/**
 * Class <code>AddNodeEdit</code> makes the add node action undoable.
 * 
 * @author Walter Wirch
 * @version $Revision: 5767 $
 */
public class AddNodeEdit extends GraphElementsEdit {

    /**
     * 
     */
    private static final long serialVersionUID = 8130286199378756838L;
    /** added node */
    private Node node;

    /**
     * Constructor for AddNodeEdit.
     * 
     * @param node
     * @param graph
     * @param geMap
     */
    public AddNodeEdit(Node node, Graph graph,
            Map<GraphElement, GraphElement> geMap) {
        super(graph, geMap);
        this.node = node;
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
        return coreBundle.getString("undo.addNode");
    }

    /*
     * @see org.graffiti.undo.GraffitiAbstractUndoableEdit#execute()
     */
    @Override
    public void execute() {
    }

    /**
     * Adds the same node that was added through the method that created this
     * edit.
     */
    @Override
    public void redo() {
        super.redo();

        Node newNode = graph.addNodeCopy(node);
        geMap.put(node, newNode);
    }

    /**
     * Deletes the node that is stored in this edit.
     */
    @Override
    public void undo() {
        super.undo();

        node = (Node) getCurrentGraphElement(node);
        graph.deleteNode(node);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
