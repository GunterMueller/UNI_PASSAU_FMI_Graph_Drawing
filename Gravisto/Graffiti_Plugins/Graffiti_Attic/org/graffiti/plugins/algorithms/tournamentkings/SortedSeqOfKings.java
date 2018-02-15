// =============================================================================
//
//   SortedSeqOfKings.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SortedSeqOfKings.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.tournamentkings;

import java.awt.Color;
import java.util.HashMap;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * Finds a sorted sequence of kings in a tournament in &Theta;(|V|^2). <br>
 * A king is a node that beats every other node in the tournament graph. <br>
 * A tournament is a complete directed graph, that means there is exact one edge
 * between every node u and every node v.<br>
 * A node u beats a node v, if there is an edge (u, v) or there is a node w with
 * edges (u, w) and (w, v). <br>
 * Every tournament has a king (at least one). The node with the maximal out
 * degree is a king in the tournament.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5772 $ $Date: 2006-04-27 23:05:20 +0200 (Do, 27 Apr 2006)
 *          $
 */
public class SortedSeqOfKings extends AbstractAlgorithm {

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Sorted Sequence Of Kings";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (!Utils.checkIfGraphIsTournament(graph)) {
            errors.add("The given graph is not a tournament.");
        }

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        Node[] nodes = new Node[graph.getNodes().size()];

        // OUT-DEGREE
        HashMap<Node, Integer> nodeMap = Utils.getNodeOutDegrees(graph
                .getNodes());

        // KING-SEQUENCE
        for (int i = 0; i < nodes.length; i++) {
            Node king = Utils.getNodeWithMaxOutDegree(nodeMap);
            nodes[i] = king;
            nodeMap.put(king, -1);
            for (Node node : king.getAllInNeighbors()) {
                int outDegree = nodeMap.get(node);
                if (outDegree > 0) {
                    outDegree--;
                    nodeMap.put(node, outDegree);
                }
            }
        }

        // KING-SORT

        for (int i = nodes.length - 1; i >= 0; i--) {
            Node king = nodes[i];
            for (int j = i + 1; j < nodes.length; j++) {
                if (edgeExists(nodes[j], king)) {
                    nodes[j - 1] = nodes[j];
                } else {
                    nodes[j - 1] = king;
                    break;
                }
            }
        }

        int i = 1;
        for (Node node : nodes) {
            // label the nodes
            NodeLabelAttribute nla;
            try {
                nla = (NodeLabelAttribute) node
                        .getAttribute(GraphicAttributeConstants.LABEL);
            } catch (AttributeNotFoundException e) {
                nla = new NodeLabelAttribute(GraphicAttributeConstants.LABEL);
                node.addAttribute(nla, GraphicAttributeConstants.LABEL);
            }
            nla.setLabel("" + i);
            i++;

            // color the nodes
            try {
                NodeGraphicAttribute nga = (NodeGraphicAttribute) node
                        .getAttribute(GraphicAttributeConstants.GRAPHICS);
                ColorAttribute ca = nga.getFillcolor();
                int factor = (i - 1) * (255 / nodes.length);
                ca.setColor(new Color(255, factor, factor));
            } catch (AttributeNotFoundException e) {
                // do nothing
            }
        }
    }

    /**
     * @param node
     * @param king
     * @return <code>true</code> if an edge from node to king exists,
     *         <code>false</code> otherwise.
     */
    private boolean edgeExists(Node node, Node king) {
        for (Node target : node.getAllOutNeighbors()) {
            if (target == king)
                return true;
        }
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
