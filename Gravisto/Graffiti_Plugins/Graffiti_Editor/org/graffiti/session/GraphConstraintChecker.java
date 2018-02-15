// =============================================================================
//
//   GraphConstraintChecker.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphConstraintChecker.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.session;

import java.util.HashSet;
import java.util.NoSuchElementException;

import org.graffiti.event.AttributeEvent;
import org.graffiti.event.AttributeListener;
import org.graffiti.event.EdgeEvent;
import org.graffiti.event.EdgeListener;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.GraphListener;
import org.graffiti.event.NodeEvent;
import org.graffiti.event.NodeListener;
import org.graffiti.event.TransactionEvent;
import org.graffiti.event.TransactionListener;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.mode.GraphConstraint;

/**
 * This class checks constraints on the graph. Constraints can arbitrarily added
 * an removed. <code>GraphConstraintChecker</code> is a listener to graph,
 * nodes, edges and attributes and checks the constraints after every change on
 * these objects.
 * 
 * @see org.graffiti.plugin.mode.GraphConstraint
 * @see org.graffiti.event.TransactionListener
 * @see org.graffiti.event.GraphListener
 * @see org.graffiti.event.EdgeListener
 * @see org.graffiti.event.NodeListener
 * @see org.graffiti.event.GraphEvent
 * @see org.graffiti.event.EdgeEvent
 * @see org.graffiti.event.NodeEvent
 * @see org.graffiti.event.TransactionEvent
 */
public class GraphConstraintChecker implements TransactionListener,
        GraphListener, EdgeListener, NodeListener, AttributeListener {

    /**
     * The <code>ConstraintCheckerListener</code> waiting for unsatisfied
     * constraints.
     */
    private ConstraintCheckerListener ccl;

    /** The <code>Graph</code> on which to check the constraints. */
    private Graph g;

    /** Contains the constraints to be checked. */
    private HashSet<GraphConstraint> constraints;

    /**
     * Constructs a new <code>GraphConstraintChecker</code> checking constraints
     * on the specified <code>Graph</code>.
     * 
     * @param g
     *            the <code>Graph</code> on which to check the constraints.
     * @param ccl
     *            the <code>ConstraintCheckerListener</code> receiving failures
     *            of constraints.
     */
    public GraphConstraintChecker(Graph g, ConstraintCheckerListener ccl) {
        this.g = g;
        this.ccl = ccl;
    }

    /**
     * Returns the state of the constraint checker which contains a message
     * telling which constraints are not satisfied.
     * 
     * @return the state of the constraint checker which contains a message
     *         telling which constraints are not satisfied.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    public String getState() {
        throw new RuntimeException("Implement me.");
    }

    /**
     * Adds another constraint to the set of constraints in this constraint
     * checker.
     * 
     * @param c
     *            the <code>GraphConstraint</code> to be added to the set of
     *            constraints of this constraint checker.
     */
    public void addConstraint(GraphConstraint c) {
        constraints.add(c);
    }

    /**
     * Checks whether all constraints are satisfied on the specified graph.
     */
    public void checkConstraints() {
        if (constraints.isEmpty())
            return;

        String message = "";

        for (GraphConstraint gc : constraints) {
            try {
                gc.validate(g);
            } catch (UnsatisfiedConstraintException e) {
                // TODO save e.getMessage() together with other unsatisfied
                // constraints
                message.concat("\n" + e.getMessage());
            }
        }

        if (!message.equals("")) {
            ccl.checkFailed(message);
        }
    }

    /**
     * Called after an attribute has been added.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void postAttributeAdded(AttributeEvent e) {
        checkConstraints();
    }

    /**
     * Called after an attribute has been changed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void postAttributeChanged(AttributeEvent e) {
        checkConstraints();
    }

    /**
     * Called after an attribute has been removed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void postAttributeRemoved(AttributeEvent e) {
        checkConstraints();
    }

    /**
     * Called after the edge was set directed or undirected.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postDirectedChanged(EdgeEvent e) {
        checkConstraints();
    }

    /**
     * Called after an edge has been added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postEdgeAdded(GraphEvent e) {
        checkConstraints();
    }

    /**
     * Called after an edge has been removed from the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postEdgeRemoved(GraphEvent e) {
        checkConstraints();
    }

    /**
     * Called after the edge has been reversed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postEdgeReversed(EdgeEvent e) {
        checkConstraints();
    }

    /**
     * Called after method <code>clear()</code> has been called on a graph. No
     * other events (like remove events) are generated.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postGraphCleared(GraphEvent e) {
        checkConstraints();
    }

    /**
     * Called just after an incoming edge has been added to the node. (For
     * undirected edges postUndirectedEdgeAdded is called instead.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postInEdgeAdded(NodeEvent e) {
        checkConstraints();
    }

    /**
     * Called after an incoming edge has been removed from the node. (For
     * undirected edges postUndirectedEdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postInEdgeRemoved(NodeEvent e) {
        checkConstraints();
    }

    /**
     * Called after an edge has been added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postNodeAdded(GraphEvent e) {
        checkConstraints();
    }

    /**
     * Called after a node has been removed from the graph. All edges incident
     * to this node have already been removed (preEdgeRemoved and
     * postEdgeRemoved have been called).
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void postNodeRemoved(GraphEvent e) {
        checkConstraints();
    }

    /**
     * Called after an outgoing edge has been added to the node. (For undirected
     * edges postUndirectedEdgeAdded is called instead.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postOutEdgeAdded(NodeEvent e) {
        checkConstraints();
    }

    /**
     * Called after an outgoing edge has been removed from the node. (For
     * undirected edges postUndirectedEdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postOutEdgeRemoved(NodeEvent e) {
        checkConstraints();
    }

    /**
     * Called after the source node of an edge has changed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postSourceNodeChanged(EdgeEvent e) {
        checkConstraints();
    }

    /**
     * Called after the target node of an edge has changed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void postTargetNodeChanged(EdgeEvent e) {
        checkConstraints();
    }

    /**
     * Called after an (undirected) edge has been added to the node. (For
     * directed edges pre- In/Out- EdgeAdded is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postUndirectedEdgeAdded(NodeEvent e) {
        checkConstraints();
    }

    /**
     * Called after an (undirected) edge has been removed from the node. (For
     * directed edges pre- In/Out- EdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void postUndirectedEdgeRemoved(NodeEvent e) {
        checkConstraints();
    }

    /**
     * Called just before an attribute is added.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void preAttributeAdded(AttributeEvent e) {
    }

    /**
     * Called before a change of an attribute takes place.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void preAttributeChanged(AttributeEvent e) {
    }

    /**
     * Called just before an attribute is removed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void preAttributeRemoved(AttributeEvent e) {
    }

    /**
     * Called before the edge is set directed or undirected.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preDirectedChanged(EdgeEvent e) {
    }

    /**
     * Called just before an edge is added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preEdgeAdded(GraphEvent e) {
    }

    /**
     * Called just before an edge is removed from the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preEdgeRemoved(GraphEvent e) {
    }

    /**
     * Called before the edge is going to be reversed.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preEdgeReversed(EdgeEvent e) {
    }

    /**
     * Called before method <code>clear()</code> is called on a graph. No other
     * events (like remove events) are generated.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preGraphCleared(GraphEvent e) {
    }

    /**
     * Called just before an incoming edge is added to the node. (For undirected
     * edges preUndirectedEdgeAdded is called instead.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preInEdgeAdded(NodeEvent e) {
    }

    /**
     * Called just before an incoming edge is removed from the node. (For
     * undirected edges preUndirectedEdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preInEdgeRemoved(NodeEvent e) {
    }

    /**
     * Called just before a node is added to the graph.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preNodeAdded(GraphEvent e) {
    }

    /**
     * Called just before a node is removed from the graph. This method is
     * called before the incident edges are deleted.
     * 
     * @param e
     *            the GraphEvent detailing the changes.
     */
    public void preNodeRemoved(GraphEvent e) {
    }

    /**
     * Called just before an outgoing edge is added to the node. (For undirected
     * edges preUndirectedEdgeAdded is called instead.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preOutEdgeAdded(NodeEvent e) {
    }

    /**
     * Called just before an outgoing edge is removed from the node. (For
     * undirected edges preUndirectedEdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preOutEdgeRemoved(NodeEvent e) {
    }

    /**
     * Called before a change of the source node of an edge takes place.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preSourceNodeChanged(EdgeEvent e) {
    }

    /**
     * Called before a change of the target node of an edge takes place.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void preTargetNodeChanged(EdgeEvent e) {
    }

    /**
     * Called just before an (undirected) edge is added to the node. (For
     * directed edges pre- In/Out- EdgeAdded is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preUndirectedEdgeAdded(NodeEvent e) {
    }

    /**
     * Called just before an (undirected) edge is removed from the node. (For
     * directed edges pre- In/Out- EdgeRemoved is called.)
     * 
     * @param e
     *            The NodeEvent detailing the changes.
     */
    public void preUndirectedEdgeRemoved(NodeEvent e) {
    }

    /**
     * Removes the specified <code>GraphConstraint</code> from the set of
     * constraints of this constraint checker.
     * 
     * @param c
     *            the <code>GraphConstraint</code> to be removed.
     * 
     * @exception NoSuchElementException
     *                if the constraint checker does not contain this
     *                <code>GraphConstraint</code> in his set of constraints.
     */
    public void removeConstraint(GraphConstraint c) {
        if (!constraints.remove(c))
            throw new NoSuchElementException();
    }

    /**
     * Called when a transaction has stopped.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void transactionFinished(TransactionEvent e) {
    }

    /**
     * Called when a transaction has started.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void transactionStarted(TransactionEvent e) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
