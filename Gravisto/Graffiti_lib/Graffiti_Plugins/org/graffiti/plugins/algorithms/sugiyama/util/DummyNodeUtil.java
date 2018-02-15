// =============================================================================
//
//   DummyNodeUtil.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DummyNodeUtil.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.AbstractNode;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * This class acts as a helper to remove dummy-nodes in a graph and collect
 * nodes, that have an attribute "isDummyNode" in their sugiyama attribute-tree.
 * 
 * @author Ferdinand H�bner
 * 
 */
public class DummyNodeUtil {

    /**
     * Remove all dummy-nodes in the graph
     * 
     * @param data
     *            The SugiyamaData-bean that stores the dummy-nodes
     * @param graph
     *            The graph that contains the dummy-nodes
     */
    public static void removeDummies(SugiyamaData data, Graph graph) {
        collectDummies(data, graph);

        HashSet<Node> dummies = data.getDummyNodes();
        HashSet<Edge> edges = new HashSet<Edge>();
        Edge tmpEdge;
        Iterator<Edge> edgeIterator = graph.getEdgesIterator();
        Iterator<Node> nodeIterator;
        Node nextTarget;
        boolean targetReached;
        Node source;
        int bendCounter;
        double xpos;
        double ypos;
        Edge origEdge;
        SortedCollectionAttribute bends;

        double x1, x2, x3;

        // Find all edges, for which the following is true:
        // source is no dummy-node and target is a dummy-node
        while (edgeIterator.hasNext()) {
            tmpEdge = edgeIterator.next();

            if (!dummies.contains(tmpEdge.getSource())) {
                if (dummies.contains(tmpEdge.getTarget())) {
                    edges.add(tmpEdge);
                }
            }
        }

        edgeIterator = edges.iterator();

        // Delete all dummy-nodes in the path to the real target
        while (edgeIterator.hasNext()) {

            tmpEdge = edgeIterator.next();
            origEdge = tmpEdge;
            targetReached = false;
            source = tmpEdge.getSource();
            bendCounter = 0;

            // check the x-coordinate of the original edge, so we don't add
            // unneeded bends to the edge
            try {
                x1 = source.getDouble(SugiyamaConstants.PATH_X_COORD);
            } catch (AttributeNotFoundException anfe) {
                x1 = 0;
            }

            try {
                bends = (SortedCollectionAttribute) origEdge
                        .getAttribute(GraphicAttributeConstants.BENDS_PATH);
            } catch (AttributeNotFoundException anfe) {
                bends = null;
            }

            while (!targetReached) {

                nextTarget = tmpEdge.getTarget();
                if (dummies.contains(nextTarget)) {

                    // get x- and y-coordinates of current dummy-node
                    try {
                        xpos = nextTarget
                                .getDouble(SugiyamaConstants.PATH_X_COORD);
                        ypos = nextTarget
                                .getDouble(SugiyamaConstants.PATH_Y_COORD);
                    } catch (AttributeNotFoundException anfe) {
                        xpos = 0;
                        ypos = 0;
                    }

                    // Save the x-coordinate of the current dummy-node
                    x2 = xpos;

                    // look at the next edge (outgoing edge of the dummy)
                    tmpEdge = nextTarget.getAllOutEdges().iterator().next();

                    nextTarget = tmpEdge.getTarget();

                    // Save the x-coordinate of the next target
                    try {
                        x3 = nextTarget
                                .getDouble(SugiyamaConstants.PATH_X_COORD);
                    } catch (AttributeNotFoundException anfe) {
                        x3 = 0;
                    }

                    // add a new bend to the original edge where the dummy
                    // node had been placed, but only if the three "points"
                    // are not on a straight line
                    if (x1 != x2 || x2 != x3) {
                        if (bends != null) {
                            bends.add(new CoordinateAttribute("bend"
                                    + bendCounter, new Point2D.Double(xpos,
                                    ypos)));
                            bendCounter++;
                        }
                    }
                    // update x1
                    x1 = x2;

                    // set the original edge's target to be the target
                    // of the next edge - tmpEdge
                    origEdge.setTarget(tmpEdge.getTarget());

                } else {

                    // we have reached the target-node
                    targetReached = true;

                    // Make the edge a polyline if it isn't already one and
                    // only if we did add bends to the edge
                    if (bendCounter != 0) {
                        try {
                            if (origEdge
                                    .getAttribute(
                                            GraphicAttributeConstants.SHAPE_PATH)
                                    .getValue()
                                    .equals(
                                            GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME)) {

                                origEdge
                                        .changeString(
                                                GraphicAttributeConstants.SHAPE_PATH,
                                                GraphicAttributeConstants.POLYLINE_CLASSNAME);
                            }
                        } catch (AttributeNotFoundException anfe) {
                            // No GUI
                        }
                    }
                    if (origEdge.getString(
                            "graphics." + GraphicAttributeConstants.ARROWHEAD)
                            .equals("none")
                            || origEdge
                                    .getString(
                                            "graphics."
                                                    + GraphicAttributeConstants.ARROWHEAD)
                                    .equals("")) {
                        origEdge.setString("graphics."
                                + GraphicAttributeConstants.ARROWHEAD,
                                GraphicAttributeConstants.ARROWSHAPE_CLASSNAME);
                    }
                }
            }
        }
        // Delete all dummy-nodes - catch a GraphElementException in case
        // that an external algorithm (e.g. brandeskoepf) already removed the
        // dummies
        nodeIterator = dummies.iterator();
        while (nodeIterator.hasNext()) {
            try {
                graph.deleteNode(nodeIterator.next());
            } catch (GraphElementNotFoundException genfe) {
                System.err
                        .println("WARNING: Tried to remove a dummy-node that "
                                + "does not exist.");
            }
        }

        // Delete dummy node in the NodeLayer structure. Dummy nodes are
        // recognized as they have been deleted out of the graph and therefore
        // their graph attribute is null
        NodeLayers layers = data.getLayers();
        for (int i = 0; i < layers.getNumberOfLayers(); i++) {
            ArrayList<Node> layer = layers.getLayer(i);
            LinkedList<Node> toDelete = new LinkedList<Node>();
            Iterator<Node> it = layer.iterator();
            while (it.hasNext()) {
                Node node = it.next();
                if (((AbstractNode) node).getGraph() == null) {
                    // this node is a dummy node and therefore has to be deleted
                    toDelete.add(node);
                }
            }
            layer.removeAll(toDelete);
        }
    }

    /**
     * This method collects <tt>Node</tt>s in the <tt>Graph</tt> that are marked
     * as dummy-nodes and adds them to SugiyamaData.
     * 
     * @param data
     *            The <tt>SugiyamaData</tt>-bean, that stores a list of
     *            dummy-nodes
     * @param graph
     *            The <tt>Graph</tt> in which to look for dummy-nodes
     */
    public static void collectDummies(SugiyamaData data, Graph graph) {
        Iterator<Node> iter = graph.getNodesIterator();
        Node current;

        while (iter.hasNext()) {
            current = iter.next();
            try {
                if (current.getBoolean(SugiyamaConstants.PATH_DUMMY)) {
                    if (!data.getDummyNodes().contains(current)) {
                        data.getDummyNodes().add(current);
                    }
                }
            } catch (AttributeNotFoundException anfe) {
                // don't do anything - the dummy just is no dummy-node
            }
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
