// =============================================================================
//
//   BFS.java
//
//   Copyright (c) 2001-2015, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarfas;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugins.algorithms.planarfas.attributes.EdgeAtt;

/**
 * Execute the DFS-Algorithm on a given graph. Set the boolean attribute with
 * the name <code>attributeName<code> <code>true<code> for each edge 
 * and each node, which can be reached.
 * 
 * @author Barbara Eckl
 * @version $Revision$ $Date$
 */
public class BFSAlgorithm extends AbstractAlgorithm {

    /**
     * Save, whether the parameter are set.
     */
    private boolean paramsSet = false;

    /**
     * Name of a boolean attribute, which is set, as soon as an edge or a node
     * has been reached.
     */
    private String attributeName;

    /**
     * Node, where the algorithm starts the search.
     */
    private Node startNode;

    /**
     * Save, whether the direction of the edges should be ignored.
     */
    private boolean ignoreDirection;

    /**
     * Save, whether a node should save the in going edge, from where it has
     * been reached at the first time.
     */
    private boolean usePreviousEdge;

    /**
     * Save, whether debug information should printed.
     */
    private boolean debugMode;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        if (paramsSet) {
            initialize();
            Queue<Node> queue = new LinkedList<Node>();
            queue.offer(startNode);
            startNode.changeBoolean(attributeName, true);
            Node currentNode;
            Edge currentEdge;
            Node neighbourNode;
            while (!queue.isEmpty()) {
                currentNode = queue.poll();

                Iterator<Edge> it = null;
                if (ignoreDirection) {
                    it = currentNode.getEdgesIterator();
                } else {
                    it = currentNode.getDirectedOutEdgesIterator();
                }
                while (it.hasNext()) {
                    currentEdge = it.next();
                    if (!currentEdge.getBoolean("planarFAS.ignore")
                            && !currentEdge.getBoolean(attributeName)) {
                        if (currentEdge.getSource() == currentNode) {
                            neighbourNode = currentEdge.getTarget();
                        } else {
                            neighbourNode = currentEdge.getSource();
                        }
                        if (!neighbourNode.getBoolean(attributeName)) {
                            currentEdge.changeBoolean(attributeName, true);
                            neighbourNode.changeBoolean(attributeName, true);
                            if (usePreviousEdge
                                    && !neighbourNode
                                            .containsAttribute("previousEdge")) {
                                if (debugMode) {
                                    System.out
                                            .println("executeBFS: current neighbourNode "
                                                    + neighbourNode
                                                            .getInteger("name")
                                                    + ": previous Edge"
                                                    + currentEdge.getSource()
                                                            .getInteger("name")
                                                    + "-> "
                                                    + currentEdge.getTarget()
                                                            .getInteger("name"));
                                }
                                Attribute previousEdge = new EdgeAtt(
                                        "previousEdge");
                                previousEdge.setValue(currentEdge);
                                neighbourNode.addAttribute(previousEdge, "");
                            }
                            queue.offer(neighbourNode);
                        }
                    }
                }

            }
            paramsSet = false;
        }
    }

    /**
     * Is called by execute to initialize the algorithm
     */
    private void initialize() {
        for (Iterator<Node> it = graph.getNodesIterator(); it.hasNext();) {
            Node node = it.next();
            node.setBoolean(attributeName, false);
            if (node.containsAttribute("previousEdge")) {
                node.removeAttribute("previousEdge");
            }
        }

        for (Iterator<Edge> it = graph.getEdgesIterator(); it.hasNext();) {
            Edge edge = it.next();
            if (edge.containsAttribute(attributeName)) {
                edge.removeAttribute(attributeName);
            }
            edge.setBoolean(attributeName, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Special BFS";
    }

    /**
     * Sets the following parameter of the algorithm:
     * 
     * @param attributeName
     *            is name of the attribut, which is set to the founded edges and
     *            nodes
     * @param startNode
     *            is the node, where the search starts
     * @param ignoreDirection
     *            if the parameter is true, the direction is ignored
     * @param usePreviousEdge
     *            if the parameter is true, the nodes save their privious edges
     * @param debugMode
     *            if the parameter is true, debug informations are printed
     */
    public void setParams(String attributeName, Node startNode,
            boolean ignoreDirection, boolean usePreviousEdge, boolean debugMode) {
        if (startNode != null) {
            this.attributeName = attributeName;
            this.startNode = startNode;
            this.ignoreDirection = ignoreDirection;
            this.usePreviousEdge = usePreviousEdge;
            this.debugMode = debugMode;
            paramsSet = true;
        }
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        attributeName = ((StringParameter) params[0]).getString();
        startNode = (((SelectionParameter) params[1]).getSelection())
                .getNodes().get(0);
        ignoreDirection = ((BooleanParameter) params[2]).getBoolean();
        usePreviousEdge = ((BooleanParameter) params[3]).getBoolean();
        debugMode = ((BooleanParameter) params[4]).getBoolean();

        paramsSet = true;
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        StringParameter attributeName = new StringParameter("planarFAS",
                "Name of the attribute", "Name of the attribute, which are set");
        SelectionParameter selParam = new SelectionParameter("Start node",
                "Node to start with.");
        BooleanParameter ignoreDirection = new BooleanParameter(false,
                "ignore direction",
                "Should the direction of the edges be ignored?");
        BooleanParameter usePreviousEdge = new BooleanParameter(false,
                "save the privous edge of a node",
                "Save the edge of a node, when the node is reached "
                        + "the first time ");
        BooleanParameter debugMode = new BooleanParameter(false,
                "print debug information",
                "Print debug informations on the consol.");

        return new Parameter[] { attributeName, selParam, ignoreDirection,
                usePreviousEdge, debugMode };
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
