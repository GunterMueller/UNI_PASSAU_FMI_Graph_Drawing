// =============================================================================
//
//   ExtendedAdjListEdge.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ExtendedAdjListEdge.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.AdjListEdge;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;

/**
 * This class extends <code>AdjListEdge</code>, it contains a new element, the
 * reversal edge.
 * 
 * @author $Marek Piorkowski$
 * @version $1.0$ $3.9.2005$
 */
public class ExtendedAdjListEdge extends AdjListEdge {

    /** This dege's reversal edge */
    protected Edge reversalEdge;

    /**
     * Creates a new GraphEdge instance going from <code>Node</code> source to
     * <code>Node</code> target being directed or not.
     * 
     * @param graph
     *            the <code>Graph</code> the <code>Edge</code> belongs to.
     * @param source
     *            the source of the <code>Edge</code>.
     * @param target
     *            the target of the <code>Edge</code>.
     * @param directed
     *            indicating if the <code>Edge</code> is directed or not.
     * 
     * @exception GraphElementNotFoundException
     *                of source or target cannot be found in the
     *                <code>Graph</code>.
     */
    protected ExtendedAdjListEdge(Graph graph, Node source, Node target,
            boolean directed) throws GraphElementNotFoundException {
        super(graph, source, target, directed);
    }

    /**
     * Creates a new GraphEdge instance going from <code>Node</code> source to
     * <code>Node</code> target being directed or not.
     * 
     * @param graph
     *            the <code>Graph</code> the <code>Edge</code> belongs to.
     * @param source
     *            the source of the <code>Edge</code>.
     * @param target
     *            the target of the <code>Edge</code>.
     * @param directed
     *            indicating if the <code>Edge</code> is directed or not.
     * @param coll
     *            the <code>CollectionAttribute</code> of the newly created
     *            <code>AdjListNode</code>.
     * 
     * @exception GraphElementNotFoundException
     *                of source or target cannot be found in the
     *                <code>Graph</code>.
     */
    protected ExtendedAdjListEdge(Graph graph, Node source, Node target,
            boolean directed, CollectionAttribute coll)
            throws GraphElementNotFoundException {
        super(graph, source, target, directed, coll);
    }

    /**
     * Sets the reversal edge.
     * 
     * @param e
     *            The new reversal edge.
     */
    public void setReversal(Edge e) {
        reversalEdge = e;
    }

    /**
     * Returns the reversal edge.
     * 
     * @return The reversal edge.
     */
    public Edge getReversal() {
        return reversalEdge;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
