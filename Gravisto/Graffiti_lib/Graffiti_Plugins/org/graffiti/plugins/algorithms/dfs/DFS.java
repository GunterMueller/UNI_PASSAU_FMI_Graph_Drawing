package org.graffiti.plugins.algorithms.dfs;

import java.util.HashSet;
import java.util.Set;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * An implementation of the DFS algorithm.
 * 
 * @author Hilmi
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */

public class DFS extends AbstractAlgorithm {

    /**
     * is the start node
     */
    private Node sourceNode = null;

    /**
     * actual NodeLabeler
     */
    private DFSNodeLabeler nodeLabeler;

    /**
     * contains the start node
     */
    private Selection selection;

    public DFS(DFSNodeLabeler labeler) {
        this.nodeLabeler = labeler;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "DFS";
    }

    /**
     * set the start node
     * 
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
    }

    /**
     * select the start node
     * 
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "DFS will start with the only selected node.");

        return new Parameter[] { selParam };
    }

    /**
     * check whether graph is not empty and a start node is selected
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run DFS.");

        if ((selection == null) || (selection.getNodes().size() != 1))
            throw new PreconditionException(
                    "DFS needs exactly one selected node.");

        sourceNode = selection.getNodes().get(0);
    }

    /**
     * execute dfs
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute() The given graph
     *      must have at least one node.
     */
    public void execute() {
        if (sourceNode == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");
        nodeLabeler.reset();
        Set<Node> visited = new HashSet<Node>();
        visited.add(sourceNode);
        graph.getListenerManager().transactionStarted(this);
        nodeLabeler.processNeighbor(sourceNode);
        processNode(sourceNode, visited);
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * execute dfs recursivly for a given node
     * 
     * @param current
     *            is the actual node
     * @param visited
     *            are all nodes which are already visited from dfs
     */
    private void processNode(Node current, Set<Node> visited) {
        nodeLabeler.processNode(current);

        // mark all neighbours and add all unmarked neighbours
        for (Node neighbour : current.getNeighbors()) {
            if (!visited.contains(neighbour)) {
                nodeLabeler.processNeighbor(neighbour);
                visited.add(neighbour);
                processNode(neighbour, visited);
                nodeLabeler.processNeighborFinally(neighbour);
            }
        }
        nodeLabeler.processNodeFinally(current);

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
    }

}
