// =============================================================================
//
//   BigNode.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BigNode.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.algorithms.sugiyama.constraints.HorizontalConstraintWithTwoNodes;

/**
 * This class is a wrapper for big nodes.
 * 
 * @author Ferdinand H�bner
 */
public class BigNode {
    /** the original node */
    private Node originalNode;
    /** a list of dummy-nodes */
    private ArrayList<Node> dummyNodes;
    /** the edges connected to the original node */
    private HashSet<Edge> originalEdges;
    /** dummy-edges to connect dummy-nodes */
    private HashSet<Edge> dummyEdges;
    /** the graph */
    private Graph graph;
    /** the last dummy in the "chain" of dummies */
    private Node lastDummy;

    private SugiyamaData data;

    /**
     * Getter to access the original node
     * 
     * @return Returns the original node
     */
    public Node getOriginalNode() {
        return this.originalNode;
    }

    /**
     * Getter to access the dummy-nodes
     * 
     * @return the dummy-nodes
     */
    public ArrayList<Node> getDummyNodes() {
        return this.dummyNodes;
    }

    /**
     * Getter to access the edges connected to the original node
     * 
     * @return the edges connected to the original node
     */
    public HashSet<Edge> getOriginalEdges() {
        return this.originalEdges;
    }

    /**
     * Getter to access the dummy-edges that connect the dummy-nodes
     * 
     * @return the dummy-edges that connect the dummy-nodes
     */
    public HashSet<Edge> getDummyEdges() {
        return this.dummyEdges;
    }

    /**
     * Create a new BigNode. Dummy-nodes and dummy-edges are inserted into the
     * graph.
     * 
     * @param orig
     * @param startLevel
     * @param endLevel
     * @param graph
     */
    public BigNode(Node orig, int startLevel, int endLevel, Graph graph,
            SugiyamaData data) {
        this.graph = graph;
        this.data = data;
        originalNode = orig;
        originalEdges = new HashSet<Edge>();
        String label = null;
        try {
            label = originalNode.getString(SugiyamaConstants.PATH_LABEL);
        } catch (AttributeNotFoundException anfe) {
            label = "" + Math.random();
            try {
                originalNode.addString(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_LABEL, label);
            } catch (Exception e) {

            }
        }
        // collect all edges from the node
        Iterator<Edge> edges = originalNode.getEdgesIterator();
        while (edges.hasNext()) {
            originalEdges.add(edges.next());
        }

        dummyNodes = new ArrayList<Node>();
        Node dummy;
        lastDummy = null;
        // add a dummy-node on every level
        for (int i = startLevel; i < endLevel; i++) {
            dummy = graph.addNode();
            try {
                dummy.getAttribute("graphics");
            } catch (Exception e) {
                dummy.addAttribute(new NodeGraphicAttribute(), "");
            }

            dummyNodes.add(dummy);
            data.getDummyNodes().add(dummy);
            data.getLayers().getLayer(i).add(dummy);
            addConstraint(dummy, label, label + "_dummy" + i);
            if (i == endLevel - 1) {
                lastDummy = dummy;
            }
        }
        // set the new target of all out-edges to the last dummy-node
        edges = originalNode.getDirectedOutEdgesIterator();
        HashSet<Edge> outEdges = new HashSet<Edge>();
        while (edges.hasNext()) {
            outEdges.add(edges.next());
        }

        edges = outEdges.iterator();
        while (edges.hasNext()) {
            edges.next().setSource(lastDummy);
        }
        // add dummy-edges
        dummyEdges = new HashSet<Edge>();
        dummyEdges.add(graph.addEdge(originalNode, dummyNodes.get(0), true));
        for (int i = 0; i < (dummyNodes.size() - 1); i++) {
            dummyEdges.add(graph.addEdge(dummyNodes.get(i), dummyNodes
                    .get(i + 1), true));
        }
        for (Edge e : dummyEdges) {
            try {
                e.getAttribute("graphics");
            } catch (Exception ex) {
                e.addAttribute(new EdgeGraphicAttribute(), "");
            }
        }
    }

    private void addConstraint(Node node, String originalLabel, String label) {
        HorizontalConstraintWithTwoNodes c = new HorizontalConstraintWithTwoNodes();
        try {
            node.addAttribute(new HashMapAttribute(
                    SugiyamaConstants.PATH_SUGIYAMA), "");
            node.addString(SugiyamaConstants.PATH_SUGIYAMA,
                    SugiyamaConstants.SUBPATH_LABEL, label);
        } catch (Exception ex) {
            return;
        }
        node.addAttribute(new HashMapAttribute(
                SugiyamaConstants.SUBPATH_CONSTRAINTS),
                SugiyamaConstants.PATH_SUGIYAMA);
        node.addString(SugiyamaConstants.PATH_CONSTRAINTS,
                "sugiyamaConstraint0",
                "HORIZONTAL_TWO_NODES_MANDATORY_EQUAL_X_" + originalLabel);
        data.getConstraints().add(
                c.isConstraint("HORIZONTAL_TWO_NODES_MANDATORY_EQUAL_X_"
                        + originalLabel, node, data));

    }

    public void removeDummyElements() {
        // remove all dummy-edges
        Iterator<Edge> edges = dummyEdges.iterator();
        while (edges.hasNext()) {
            graph.deleteEdge(edges.next());
        }
        // reset the source of the original out-edges to the original node
        edges = originalEdges.iterator();
        Edge tmp;
        while (edges.hasNext()) {
            tmp = edges.next();
            if (tmp.getSource() == lastDummy) {
                tmp.setSource(originalNode);
            }
        }
        // remove all dummy-nodes
        Iterator<Node> dummies = dummyNodes.iterator();
        while (dummies.hasNext()) {
            graph.deleteNode(dummies.next());
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
