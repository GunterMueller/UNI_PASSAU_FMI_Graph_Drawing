// =============================================================================
//
//   EdgeUtil.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeUtil.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.PortAttribute;
import org.graffiti.plugins.algorithms.sugiyama.constraints.VerticalConstraintWithTwoNodes;
import org.graffiti.plugins.algorithms.sugiyama.levelling.AbstractCyclicLevelingAlgorithm;
import org.graffiti.util.CoreGraphEditing;

/**
 * This class provides methods to manipulate edges of a graph
 */
public class EdgeUtil {

    public static void addPorts(SugiyamaData data) {

        // distinguish between the different algorithm types
        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA))
            return; // don't create any ports

        for (Node node : data.getGraph().getNodes()) {
            CollectionAttribute ports = (CollectionAttribute) node
                    .getAttribute(GraphicAttributeConstants.PORTS_PATH
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COMMON);
            try {
                // check if attribute does already exist
                ports.getAttribute("top_center");
            } catch (AttributeNotFoundException e) {
                // port doesn't exist
                ports
                        .add(new PortAttribute("top_center", "top_center", 0,
                                -0.9));
            }
            try {
                ports.getAttribute("bottom_center");
            } catch (AttributeNotFoundException e) {
                ports.add(new PortAttribute("bottom_center", "bottom_center",
                        0, 0.9));
            }
        }
        for (Edge edge : data.getGraph().getEdges()) {
            if (((CoordinateAttribute) edge.getSource().getAttribute(
                    GraphicAttributeConstants.COORD_PATH)).getY() < ((CoordinateAttribute) edge
                    .getTarget().getAttribute(
                            GraphicAttributeConstants.COORD_PATH)).getY()) {
                edge.setString(GraphicAttributeConstants.DOCKING_PATH
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.SOURCE, "bottom_center");
                edge.setString(GraphicAttributeConstants.DOCKING_PATH
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.TARGET, "top_center");
            } else {
                edge.setString(GraphicAttributeConstants.DOCKING_PATH
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.SOURCE, "top_center");
                edge.setString(GraphicAttributeConstants.DOCKING_PATH
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.TARGET, "bottom_center");

            }
        }
    }

    public static void removeSelfLoops(SugiyamaData data) {
        data.setSelfLoops(CoreGraphEditing.removeSelfLoops(data.getGraph()
                .getEdges()));
    }

    public static void insertSelfLoops(SugiyamaData data) {
        Collection<Edge> newSelfLoops = new LinkedList<Edge>();
        for (Edge edge : data.getSelfLoops()) {
            Edge newEdge = data.getGraph().addEdgeCopy(edge, edge.getSource(),
                    edge.getTarget());
            newSelfLoops.add(newEdge);

            NodeGraphicAttribute nodeAttributes = (NodeGraphicAttribute) newEdge
                    .getSource().getAttribute(
                            GraphicAttributeConstants.GRAPHICS);

            CoordinateAttribute ca = nodeAttributes.getCoordinate();
            DimensionAttribute da = nodeAttributes.getDimension();

            CollectionAttribute attributes = newEdge.getAttributes();

            EdgeGraphicAttribute edgeAttributes = (EdgeGraphicAttribute) attributes
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);

            SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                    GraphicAttributeConstants.BENDS);

            bends.add(new CoordinateAttribute("bend0", new Point((int) (ca
                    .getX() + da.getWidth() / 2) + 20, (int) ca.getY() - 12)));

            bends.add(new CoordinateAttribute("bend1", new Point((int) (ca
                    .getX() + da.getWidth() / 2) + 20, (int) ca.getY() + 12)));
            edgeAttributes.setShape(GraphicAttributeConstants.SMOOTH_CLASSNAME);

            edgeAttributes.setBends(bends);
        }
        data.setSelfLoops(newSelfLoops);
    }

    /**
     * Adds bends to a self loop if it hasn't any, yet and updates the
     * coordinates of existing bends if necessary.
     */
    public static void updateSelfLoops(SugiyamaData data) {
        for (Edge edge : data.getSelfLoops()) {

            NodeGraphicAttribute nodeAttributes = (NodeGraphicAttribute) edge
                    .getSource().getAttribute(
                            GraphicAttributeConstants.GRAPHICS);

            CoordinateAttribute ca = nodeAttributes.getCoordinate();
            DimensionAttribute da = nodeAttributes.getDimension();

            CollectionAttribute attributes = edge.getAttributes();

            EdgeGraphicAttribute edgeAttributes = (EdgeGraphicAttribute) attributes
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);

            SortedCollectionAttribute bends = edgeAttributes.getBends();
            try {
                CoordinateAttribute bend0 = (CoordinateAttribute) bends
                        .getAttribute("bend0");
                CoordinateAttribute bend1 = (CoordinateAttribute) bends
                        .getAttribute("bend1");

                // update bends if necessary
                int newX = (int) (ca.getX() + da.getWidth() / 2) + 20;
                int newY = (int) ca.getY() - 12;
                if (bend0.getX() != newX) {
                    bend0.setX(newX);
                }
                if (bend0.getY() != newY) {
                    bend0.setY(newY);
                }

                newY = (int) ca.getY() + 12;
                if (bend1.getX() != newX) {
                    bend1.setX(newX);
                }
                if (bend1.getY() != newY) {
                    bend1.setY(newY);
                }

            } catch (AttributeNotFoundException e) {
                // add the bends to the edge
                bends = new LinkedHashMapAttribute(
                        GraphicAttributeConstants.BENDS);

                bends.add(new CoordinateAttribute("bend0",
                        new Point((int) (ca.getX() + da.getWidth() / 2) + 20,
                                (int) ca.getY() - 12)));

                bends.add(new CoordinateAttribute("bend1",
                        new Point((int) (ca.getX() + da.getWidth() / 2) + 20,
                                (int) ca.getY() + 12)));

                edgeAttributes
                        .setShape(GraphicAttributeConstants.SMOOTH_CLASSNAME);
                edgeAttributes.setBends(bends);
            }
        }
    }

    /**
     * This method reverses edges that contain bends.
     * 
     * The bends have to be inverted, otherwise the edge will look completely
     * different. The attached bean must have the following data stored:
     * <ul>
     * <li>ReversedEdges
     * </ul>
     * 
     * @param data
     *            The <code>SugiyamaData</code>-Bean that stores all neccessary
     *            information.
     */
    public static void reverseBendedEdge(SugiyamaData data) {

        Iterator<Edge> edgeIterator = data.getReversedEdges().iterator();
        SortedCollectionAttribute bends;
        Edge edge;
        String bendPrefix = "bend";
        int bendCounter;
        ArrayList<Attribute> attributes;

        // Process each edge
        while (edgeIterator.hasNext()) {

            edge = edgeIterator.next();

            try {
                bends = (SortedCollectionAttribute) edge
                        .getAttribute(GraphicAttributeConstants.BENDS_PATH);
            } catch (AttributeNotFoundException anfe) {
                bends = null;
            }
            bendCounter = 0;
            attributes = new ArrayList<Attribute>();

            // Extract all bends from the SortedCollectionAttribute and
            // store them temporarily in an ArrayList
            while (true) {
                if (bends == null)
                    return;

                if (bends.getCollection().containsKey(bendPrefix + bendCounter)) {
                    attributes.add(bends.getCollection().get(
                            bendPrefix + bendCounter));
                    bendCounter++;
                } else {
                    break;
                }
            }

            // Remove all bends
            for (int i = 0; i < attributes.size(); i++) {
                try {
                    bends.remove(attributes.get(i));
                } catch (AttributeNotFoundException anfe) {
                    System.err.println("WARNING: Tried to remove a bend that "
                            + "does not exist in this edge!");
                }
            }

            Point2D coordinates;
            bendCounter = 0;
            // Add the bends again - in reversed order
            for (int i = attributes.size() - 1; i >= 0; i--) {

                coordinates = ((CoordinateAttribute) attributes.get(i))
                        .getCoordinate();
                try {
                    bends.add(new CoordinateAttribute("bend" + bendCounter,
                            (Point2D) coordinates.clone()));
                    bendCounter++;
                } catch (AttributeExistsException aee) {
                    System.err.println("WARNING: " + aee.getCause());
                    bendCounter--;
                }

            }
            try {
                edge.reverse();
            } catch (NullPointerException npe) {

            }
        }
    }

    /**
     * This method removes all bends from the edges of a graph and sets the
     * edge's shape to polyline
     * 
     * @param data
     *            The <code>SugiyamaData</code>-Bean, that stores the graph
     */
    public static void removeBends(SugiyamaData data) {
        Iterator<Edge> edgeIterator = data.getGraph().getEdgesIterator();
        Edge tmp;
        SortedCollectionAttribute bends;

        while (edgeIterator.hasNext()) {
            tmp = edgeIterator.next();
            try {
                bends = (SortedCollectionAttribute) tmp
                        .getAttribute(GraphicAttributeConstants.BENDS_PATH);
                // at least one bend exists
                if (bends.getCollection().containsKey("bend0")) {
                    // delete all bends
                    for (int j = 0; j <= bends.getCollection().size(); j++) {
                        bends.remove("bend" + j);
                    }
                }
                // make the shape of the ede a straight line if it isn't already
                // a straight line
                if (!tmp
                        .getAttribute(GraphicAttributeConstants.SHAPE_PATH)
                        .getValue()
                        .equals(
                                GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME)) {

                    tmp.changeString(GraphicAttributeConstants.SHAPE_PATH,
                            GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME);
                }
            } catch (AttributeNotFoundException anfe) {
                // seems like there are no bends
            }
        }
    }

    /**
     * This method inserts constraints for nodes that had been connected through
     * an edge that was deleted from the graph. These nodes musn't be placed on
     * the same level by a levelling-algorithm.
     * 
     * @param data
     *            The <tt>SugiyamaData</tt> that stores the graph and the
     *            deleted edges.
     */
    public static void insertConstraintsForDeletedEdges(SugiyamaData data) {
        HashSet<Edge> delEdges = data.getDeletedEdges();

        if (delEdges.size() == 0)
            return;

        Iterator<Edge> edgeIterator = delEdges.iterator();
        Edge currentEdge;
        Node source;
        Node target;
        String identifierTarget;
        VerticalConstraintWithTwoNodes constraint;

        while (edgeIterator.hasNext()) {
            currentEdge = edgeIterator.next();
            source = currentEdge.getSource();
            target = currentEdge.getTarget();

            identifierTarget = target.getString(SugiyamaConstants.PATH_LABEL);

            source.addString(SugiyamaConstants.PATH_CONSTRAINTS,
                    "sugiyamaConstraint_deletedEdge_" + identifierTarget,
                    "VERTICAL_TWO_NODES_MANDATORY_NONEQUAL_Y_"
                            + identifierTarget);

            constraint = new VerticalConstraintWithTwoNodes();
            constraint.setSource(source);
            constraint.setTarget(target);
            constraint.setNonequalY();
            constraint.setMandatory(true);
            data.getConstraints().add(constraint);

        }
    }

    /**
     * This method inserts edges that had been deleted from the graph. Note that
     * this method might reverse these edges.
     * 
     * @param data
     *            The SugiyamaData-bean that stores the edges that had been
     *            deleted from the graph in the decycling-phase.
     */
    public static void insertDeletedEdges(SugiyamaData data) {
        Graph graph = data.getGraph();
        HashSet<Edge> deletedEdges = data.getDeletedEdges();
        Iterator<Edge> edgeIterator = deletedEdges.iterator();
        Edge current;
        Node source;
        Node target;
        int level_source;
        int level_target;
        boolean reversed;

        while (edgeIterator.hasNext()) {
            current = edgeIterator.next();
            source = current.getSource();
            target = current.getTarget();
            Node swap;
            int level_swap;
            level_source = 0;
            level_target = 0;
            reversed = false;
            Edge newEdge = null;
            Edge lastEdge;
            for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
                if (data.getLayers().getLayer(i).contains(source)) {
                    level_source = i;
                }
                if (data.getLayers().getLayer(i).contains(target)) {
                    level_target = i;
                }
            }
            // System.out.print("Re-inserted deleted edge: ");
            // add the edge to the graph
            if (level_source <= level_target) {
                lastEdge = graph.addEdge(source, target, true);
                try {
                    lastEdge.getAttribute("graphics");
                } catch (Exception e) {
                    EdgeGraphicAttribute ega = new EdgeGraphicAttribute();
                    lastEdge.addAttribute(ega, "");
                    ega.setArrowhead("org.graffiti.plugins.views.defaults."
                            + "StandardArrowShape");

                }
            } else {
                newEdge = graph.addEdge(target, source, true);
                try {
                    newEdge.getAttribute("graphics");
                } catch (Exception e) {
                    EdgeGraphicAttribute ega = new EdgeGraphicAttribute();
                    newEdge.addAttribute(ega, "");
                    ega.setArrowhead("org.graffiti.plugins.views.defaults."
                            + "StandardArrowShape");
                }
                swap = source;
                source = target;
                target = swap;
                level_swap = level_source;
                level_source = level_target;
                level_target = level_swap;
                reversed = true;
                lastEdge = newEdge;
            }
            if (reversed) {
                data.getReversedEdges().add(newEdge);
            }

            Node dummy;
            Node lastTarget;
            // System.out.print(level_target - level_source + " Dummy-Nodes\n");
            // add dummy-nodes if the edge spans more than one level
            if (level_target - level_source > 1) {
                for (int i = (level_source) + 1; i < level_target; i++) {
                    // System.out.println("Added dummy-node...");
                    dummy = graph.addNode();
                    try {
                        dummy.getAttribute("graphics");
                    } catch (Exception e) {
                        dummy.addAttribute(new NodeGraphicAttribute(), "");
                    }

                    lastTarget = lastEdge.getTarget();
                    lastEdge.setTarget(dummy);
                    lastEdge = graph.addEdge(dummy, lastTarget, true);
                    try {
                        lastEdge.getAttribute("graphics");
                    } catch (Exception e) {
                        lastEdge.addAttribute(new EdgeGraphicAttribute(), "");
                    }
                    data.getLayers().getLayer(i).add(dummy);
                    data.getDummyNodes().add(dummy);
                    AbstractCyclicLevelingAlgorithm.setDummyShape(dummy);
                    dummy.addAttribute(new HashMapAttribute(
                            SugiyamaConstants.PATH_SUGIYAMA), "");
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
