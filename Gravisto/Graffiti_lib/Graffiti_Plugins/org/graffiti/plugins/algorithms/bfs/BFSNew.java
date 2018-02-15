// =============================================================================
//
//   BFS.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BFSNew.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.bfs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;
import org.graffiti.util.Queue;

/**
 * An implementation of the BFS algorithm.
 * 
 * @version $Revision: 5766 $
 */
public class BFSNew extends AbstractAlgorithm {
    /** DOCUMENT ME! */
    private Node sourceNode = null;

    private BFSNodeVisitor nodeVisitor;

    /** DOCUMENT ME! */
    private Selection selection;

    public BFSNew(BFSNodeVisitor visitor) {
        this.nodeVisitor = visitor;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "BFS";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "BFS will start with the only selected node.");

        return new Parameter[] { selParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run BFS.");

        if ((selection == null) || (selection.getNodes().size() != 1))
            throw new PreconditionException(
                    "BFS needs exactly one selected node.");

        sourceNode = selection.getNodes().get(0);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute() The given graph
     *      must have at least one node.
     */
    public void execute() {
        if (sourceNode == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");
        nodeVisitor.reset();
        Queue q = new Queue();

        // d contains a mapping from node to an integer, the bfsnum
        Set<Node> visited = new HashSet<Node>();

        q.addLast(sourceNode);
        visited.add(sourceNode);
        graph.getListenerManager().transactionStarted(this);
        nodeVisitor.processNeighbor(sourceNode);
        while (!q.isEmpty()) {
            Node v = (Node) q.removeFirst();
            nodeVisitor.processNode(v);
            // mark all neighbours and add all unmarked neighbours
            // of v to the queue
            for (Iterator<Node> neighbours = v.getNeighborsIterator(); neighbours
                    .hasNext();) {
                Node neighbour = neighbours.next();

                if (!visited.contains(neighbour)) {
                    nodeVisitor.processNeighbor(neighbour);
                    visited.add(neighbour);
                    q.addLast(neighbour);
                }
            }
        }

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
    }

}
