// =============================================================================
//
//   Utils.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Utils.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.tournamentkings;

import java.util.Collection;
import java.util.HashMap;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author Marek Piorkowski
 * @version $Revision: 5772 $ $Date: 2006-04-27 20:32:47 +0200 (Do, 27 Apr 2006)
 *          $
 */
public class Utils {

    public static Node getNodeWithMaxOutDegree(Collection<Node> nodes) {
        Node maxDegreeNode = null;
        int maxDegree = -1;
        for (Node node : nodes) {
            if (node.getOutDegree() > maxDegree) {
                maxDegreeNode = node;
                maxDegree = node.getOutDegree();
            }
        }
        return maxDegreeNode;
    }

    public static HashMap<Node, Integer> getNodeOutDegrees(
            Collection<Node> nodes) {
        HashMap<Node, Integer> nodeMap = new HashMap<Node, Integer>();
        for (Node node : nodes) {
            nodeMap.put(node, node.getOutDegree());
        }
        return nodeMap;
    }

    public static Node getNodeWithMaxOutDegree(HashMap<Node, Integer> nodeMap) {
        Node maxDegreeNode = null;
        int maxDegree = -1;
        for (Node node : nodeMap.keySet()) {
            if (nodeMap.get(node) > maxDegree) {
                maxDegreeNode = node;
                maxDegree = nodeMap.get(node);
            }
        }
        return maxDegreeNode;
    }

    /**
     * Trivial solution.
     * 
     * @param graph
     *            Graph to check.
     * @return <code>true</code>, if the graph is a tournament,
     *         <code>false</code> otherwise.
     */
    public static boolean checkIfGraphIsTournament(Graph graph) {
        for (Node node : graph.getNodes()) {
            for (Node otherNode : graph.getNodes()) {
                if (node != otherNode) {
                    int numOfEdges = 0;
                    for (Edge edge : node.getEdges()) {
                        if ((edge.getTarget() == otherNode && edge.getSource() == node)
                                || (edge.getTarget() == node && edge
                                        .getSource() == otherNode)) {
                            numOfEdges++;
                        }
                    }
                    if (numOfEdges != 1)
                        return false;
                } else {
                    for (Edge edge : node.getEdges()) {
                        if ((edge.getTarget() == node && edge.getSource() == otherNode))
                            return false;
                    }
                }
            }

        }
        return true;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
