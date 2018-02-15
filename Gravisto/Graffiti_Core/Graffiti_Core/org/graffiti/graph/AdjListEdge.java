// =============================================================================
//
//   AdjListEdge.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AdjListEdge.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.graph;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.CollectionAttribute;

/**
 * Implementation of the <code>Edge</code> interface for a <code>Graph</code>
 * with adjacency list representation.
 * 
 * @version $Revision: 5779 $
 * 
 * @see AdjListGraph
 * @see AdjListNode
 */
public class AdjListEdge extends AbstractEdge implements Edge, GraphElement {

    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(AdjListEdge.class
            .getName());
    
    static {
        logger.setLevel(Level.SEVERE);
    }

    /** The source <code>Node</code> of this <code>Edge</code>. */
    private Node source;

    /** The target <code>Node</code> of this <code>Edge</code>. */
    private Node target;

    /** Indicates whether the <code>Edge</code> is directed or not. */
    private boolean directed;

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
    protected AdjListEdge(Graph graph, Node source, Node target,
            boolean directed) throws GraphElementNotFoundException {
        super(graph);
        this.source = source;
        this.target = target;
        this.directed = directed;
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
    protected AdjListEdge(Graph graph, Node source, Node target,
            boolean directed, CollectionAttribute coll)
            throws GraphElementNotFoundException {
        super(graph, coll);
        this.source = source;
        this.target = target;
        this.directed = directed;
    }

    /**
     * Returns <code>true</code>, if the <code>Edge</code> is directed,
     * <code>false</code> otherwise.
     * 
     * @return <code>true</code>, if the <code>Edge</code> is directed,
     *         <code>false</code> otherwise.
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * Returns the source of the <code>Edge</code>.
     * 
     * @return the source of the <code>Edge</code>.
     */
    public Node getSource() {
        return source;
    }

    /**
     * Returns the target of the <code>Edge</code>.
     * 
     * @return the target of the <code>Edge</code>.
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Sets the target of the current <code>Edge</code> to target. Target must
     * be contained in the same <code>Graph</code> as the <code>Edge</code>.
     * Informs the ListenerManager about the change.
     * 
     * @param target
     *            the target to be set.
     * 
     * @exception GraphElementNotFoundException
     *                if the target cannot be found in the <code>Graph</code>.
     * @exception IllegalArgumentException
     *                if target is not of type <code>AdjListNode</code>.
     */
    @Override
    public void doSetTarget(Node target) throws GraphElementNotFoundException,
            IllegalArgumentException {
        assert target != null;

        if (target instanceof AdjListNode) {
            if (this.getGraph() == target.getGraph()) {
                // removing the edge from the old target node
                ((AdjListNode) this.getTarget()).removeInEdge(this);

                // setting the new target
                this.target = target;

                // adding the edge to the new target node
                ((AdjListNode) target).addInEdge(this);
            } else
                throw new GraphElementNotFoundException(
                        "The node is not in the same graph as the edge!");
        } else
            throw new IllegalArgumentException(
                    "The node is not of type 'AdjListNode' which the edge "
                            + "requires");
    }

    /**
     * Swaps source and target of the edge.
     */
    @Override
    protected void doReverse() {
        AdjListNode oldSource = (AdjListNode) this.getSource();
        logger.finest("setting the new source");
        this.setSource(this.getTarget());
        logger.finest("setting the new target");
        this.setTarget(oldSource);
    }

    /**
     * Determines if an <code>Edge</code> is directed (<code>true</code>) or
     * not.
     * 
     * @param directed
     *            <code>true</code>, if the <code>Edge</code> is destined to be
     *            directed, <code>false</code> otherwise.
     */
    @Override
    protected void doSetDirected(boolean directed) {
        if (directed != this.directed) {
            AdjListNode source = (AdjListNode) getSource();
            AdjListNode target = (AdjListNode) getTarget();
            source.removeOutEdge(this);
            target.removeInEdge(this);
            this.directed = directed;
            source.addOutEdge(this);
            target.addInEdge(this);
        }
    }

    /**
     * Sets the source of the current <code>Edge</code> to <code>source</code>.
     * <code>source</code> must be contained in the same <code>Graph</code> as
     * the current <code>Edge</code>.
     * 
     * @param source
     *            the source to be set.
     * 
     * @exception GraphElementNotFoundException
     *                if source cannot be found in the <code>Graph</code>.
     * @exception IllegalArgumentException
     *                if source is not of type <code>AdjListNode</code>
     */
    @Override
    protected void doSetSource(Node source)
            throws GraphElementNotFoundException, IllegalArgumentException {
        assert source != null;

        if (source instanceof AdjListNode) {
            if (this.getGraph() == source.getGraph()) {
                // removing the edge in the old source node
                ((AdjListNode) this.getSource()).removeOutEdge(this);

                // setting the new source
                this.source = source;

                // adding the edge to the new source node
                ((AdjListNode) source).addOutEdge(this);
            } else
                throw new GraphElementNotFoundException(
                        "The node is not in the same graph as the edge!");
        } else
            throw new IllegalArgumentException(
                    "The node is not of type 'AdjListNode' which the edge "
                            + "requires");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
