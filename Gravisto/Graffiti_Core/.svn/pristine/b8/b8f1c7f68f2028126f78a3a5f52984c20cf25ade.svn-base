// =============================================================================
//
//   Face.java
//
//   Copyright (c) 2001-2013, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.oneplanar;

import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * A face in a graph, storing its nodes and edges
 * 
 * @author Thomas Kruegl
 * @version $Revision$ $Date$
 */
public class FaceWithComponents {
    
    // ~ Instance fields
    // ========================================================

    private LinkedList<Node> nodelist;

    private LinkedList<Edge> edgelist;

    // ~ Constructors
    // ================================================================
    /**
     * 
     * Create a face, which contains the nodes of the listNode and the edges of
     * the listEdge
     * 
     * @param listNode
     *            <code>LinkedList</code>
     * @param listEdge
     *            <code>LinkedList</code>
     */
    public FaceWithComponents(LinkedList<Node> listNode, LinkedList<Edge> listEdge) {
        this.nodelist = listNode;
        this.edgelist = listEdge;
    }

    /** Create a new face without nodes and edges */
    public FaceWithComponents() {
        nodelist = new LinkedList<Node>();
        edgelist = new LinkedList<Edge>();
    }

    // ~ Methods
    // ================================================================


    /** @return the listNode <code>LinkedList</code> */
    public LinkedList<Node> getNodelist() {
        return nodelist;
    }

    /** @return the edgeNode <code>LinkedList</code> */
    public LinkedList<Edge> getEdgelist() {
        return edgelist;
    }
    

    /** @return the number of nodes <code>int</code> */
    protected int nodeSize() {
        return nodelist.size();
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
