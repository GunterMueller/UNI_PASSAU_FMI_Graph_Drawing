// =============================================================================
//
//   CalculateFacesFromDrawing.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import java.util.ArrayList;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * Calculate faces of a graph according to its current drawing. Therefore the
 * drawing has to be plane.
 * 
 * @author koenig
 * @version $Revision$ $Date$
 */
public class CalculateFacesFromDrawing extends CalculateFaces {
    protected TestPlanarDrawing planarDrawing;

    /**
     * Create a new instance.
     * 
     * @param graph
     *            Graph to calculate faces for.
     * @param testedGraph
     *            Current embedding of graph.
     */
    public CalculateFacesFromDrawing(Graph graph, TestedGraph testedGraph,
            TestPlanarDrawing planarDrawing) {
        super(graph, testedGraph);
        this.planarDrawing = planarDrawing;
        // initMapping();
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.planarAngleGraph.CalculateFaces#getNode
     * (org.graffiti.graph.Node, int)
     */
    @Override
    protected Node getNode(Node current, int index) {
        return planarDrawing.getNodeAdjacencylist().get(current).get(index);
    }

    /**
     * Calculate the adjacency list for a <code>node</code> depending on its
     * current plane drawing.
     * 
     * @param node
     *            Node to get adjacency list for.
     * @return Adjacency list of <code>node</code>.
     */
    @Override
    protected ArrayList<Node> getAdjacencyListFor(Node node) {
        return planarDrawing.getAdjacencyListFor(node);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
