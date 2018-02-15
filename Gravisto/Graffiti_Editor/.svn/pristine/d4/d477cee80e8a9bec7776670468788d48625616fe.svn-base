// =============================================================================
//
//   CalculateFaceWrapper.java
//
//   Copyright (c) 2001-2013, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.oneplanar;

import java.util.Arrays;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.fpp.CalculateFace;
import org.graffiti.plugins.algorithms.fpp.Face;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * Wrapper class for fpp.CalculateFace, to use the canonical ordering
 * implemented there in a slightly different way, deciding the outer face in
 * advance.
 * 
 * @author Thomas Kruegl
 * @version $Revision$ $Date$
 */
public class CalculateFaceWrapper extends CalculateFace {

    private Node[] outerFaceNodes;

    /**
     * Creates a new wrapper, initialising grapg, tested graph and outer face
     * nodes
     * 
     * @param graph
     *            the graph to examine
     * @param tGraph
     *            tested graph from planarity package
     * @param outerFaceNodes
     *            the nodes of the future outer face for graph drawing
     */
    public CalculateFaceWrapper(Graph graph, TestedGraph tGraph,
            Node[] outerFaceNodes) {
        super(graph, tGraph);
        this.outerFaceNodes = outerFaceNodes;
        outerfaceIndex = getOuterfaceIndex();
    }

    /** @return face Array <code>Face[]</code> */
    protected Face[] getFaces() {
        return super.getFaces();
    }

    /** @return outerfaceindex <code>int</code> */
    protected int getOutIndex() {
        return super.getOutIndex();
    }

    /**
     * @return the index <code>int</code> of the outerface, as given by
     *         outerFaceNodes
     */
    private int getOuterfaceIndex() {
        for (int i = 0; i < faces.length; i++) {
            if (faces[i].getNodelist().containsAll(
                    Arrays.asList(outerFaceNodes))) {
                return i;
            }
        }
        throw new RuntimeException("Outer face not found!");
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
