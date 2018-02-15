//=============================================================================
//
//   SugiyamaEdge.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.incremental;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.ObjectAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.core.DeepCopy;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * A <tt>SugiyamaEdge</tt> represents an <tt>Edge</tt> object in the data
 * structures used for the incremental sugiyama algorithm.<br>
 * It stores the <tt>SugiyamaNode</tt>s and <tt>SugiyamaDummyNode</tt>s which
 * belong to this edge and handles the bends of the underlying edge.
 */
public class SugiyamaEdge implements DeepCopy {

    /**
     * @uml.property name="edge" readOnly="true"
     */
    private Edge edge;

    /**
     * All nodes and dummy nodes of the edge ordered from lower to higher level.
     * 
     * @uml.property name="dummyNodes"
     */
    private LinkedList<SugiyamaNode> nodes = new LinkedList<SugiyamaNode>();

    /**
     * @uml.property name="bends" multiplicity="(0 -1)" dimension="1"
     */
    private SugiyamaDummyNode[] bends = new SugiyamaDummyNode[] { null, null };

    /* Indicates if the source of the edge has a lower level then it's target. */
    private boolean upwards;

    /*
     * Indicates if the edge is placed in-between the columns of it's start and
     * end node. If it is to the right of both columns it should be placed
     * further left and therefore it's value is TO_LEFT. If it is to the left of
     * both columns it's value is TO_RIGHT and if it is in-between the value is
     * STAY.
     */
    private int centeredPosition = 0;

    protected static final int TO_LEFT = -1;

    protected static final int TO_RIGHT = 1;

    protected static final int STAY = 0;

    // private static final String PATH_OF_BENDS = "graphics.bends";

    private static final String PATH_OF_SHAPE = "graphics.shape";

    private static final String PATH_OF_POLY_SHAPE = "org.graffiti.plugins.views.defaults.PolyLineEdgeShape";

    private static final String PATH_OF_SMOOTH_SHAPE = "org.graffiti.plugins.views.defaults.SmoothLineEdgeShape";

    /**
     * Constructs the <tt>SugiyamaEdge</tt> adds the <tt>SugiyamaNode</tt>s of
     * the edge's source and target nodes to it's node list and adds itself to
     * the correct edge lists of these <tt>SugiyamaNode</tt>s.
     * 
     * @param edge
     */
    public SugiyamaEdge(Edge edge) {
        this.edge = edge;

        IncrementalSugiyama.addSugiyamaAttribute(edge);
        ObjectAttribute att = new ObjectAttribute(
                SugiyamaConstants.SUBPATH_INC_EDGE);
        att.setObject(this);
        edge.addAttribute(att, SugiyamaConstants.PATH_SUGIYAMA);

        Node firstNode = edge.getSource();
        Node secondNode = edge.getTarget();

        try {
            ObjectAttribute firstLevelObject = (ObjectAttribute) firstNode
                    .getAttribute(SugiyamaConstants.PATH_INC_NODE);

            ObjectAttribute secondLevelObject = (ObjectAttribute) secondNode
                    .getAttribute(SugiyamaConstants.PATH_INC_NODE);

            SugiyamaNode firstLevelNode = (SugiyamaNode) firstLevelObject
                    .getObject();
            int firstLevel = firstLevelNode.getLevelNumber();
            SugiyamaNode secondLevelNode = (SugiyamaNode) secondLevelObject
                    .getObject();
            int secondLevel = secondLevelNode.getLevelNumber();

            if (firstLevel < secondLevel) {
                upwards = false;
                nodes.addFirst(firstLevelNode);
                firstLevelNode.getEdgesToHigherLevel().add(this);
                nodes.addLast(secondLevelNode);
                secondLevelNode.getEdgesToLowerLevel().add(this);
            } else if (firstLevel > secondLevel) {
                upwards = true;
                nodes.addFirst(secondLevelNode);
                firstLevelNode.getEdgesToLowerLevel().add(this);
                nodes.addLast(firstLevelNode);
                secondLevelNode.getEdgesToHigherLevel().add(this);
            } else {
                // edge is self loop or temporarily between nodes on the same
                // level
                upwards = false;
                nodes.addFirst(firstLevelNode);
                nodes.addLast(secondLevelNode);
            }
        } catch (AttributeNotFoundException e) {
            System.err
                    .println("No SugiyamaNodes found! You have to add the nodes"
                            + " before you can add the edge.");
        }
    }

    /**
     * Adds the given dummy node at the correct position according to it's
     * level.
     * 
     * @param newNode
     *            node to be added
     */
    public void addDummyNode(SugiyamaDummyNode newNode) {
        addDummyNodeWithoutBendUpdate(newNode);
        updateBends();
        checkPosition();
    }

    /**
     * Adds the given dummy node at the correct position according to it's level
     * but doesn't update the bends of this edge. This function is meant to be
     * used if several dummy nodes are to be inserted and unnecessary updating
     * of the bends shall be avoided.
     * 
     * @param newNode
     *            node to be added
     */
    public void addDummyNodeWithoutBendUpdate(SugiyamaDummyNode newNode) {
        int level = newNode.getLevelNumber();

        // level has to be between the levels of the source and target nodes.
        assert level > nodes.getFirst().getLevelNumber();
        assert level < nodes.getLast().getLevelNumber();

        int insertAt = 1;

        while (nodes.get(insertAt).getLevelNumber() < level) {
            insertAt++;
        }
        nodes.add(insertAt, newNode);
        checkPosition();
    }

    /**
     * On each level between the start node and the target node a
     * <tt>SugiyamaDummyNode</tt> will be constructed as a place holder for the
     * edge. The first and the last dummy node - if existing - will define the y
     * coordinates of the bends the edge will have. All dummy nodes as well as
     * the bends have to have the same x coordinate as they are placed in one
     * vertical line. Therefore the x coordinate of the first bend is used for
     * all.
     * 
     * @param columnMap
     *            A <tt>HashMap</tt> containing the columns with their y
     *            coordinate as a key.
     * @param grid
     *            The <tt>GridStructure</tt> containing the graph.
     */
    public void createDummyNodes(HashMap<Double, Plane> columnMap,
            GridStructure grid) {
        int fromLevel = nodes.getFirst().getLevelNumber();
        int toLevel = nodes.getLast().getLevelNumber();

        if (toLevel - fromLevel > 1) { // there is at least one dummy node
            Plane[] levels = grid.getPlanes(GridStructure.LEVEL);

            // get the x coordinate of the dummy nodes
            CollectionAttribute attributes = edge.getAttributes();
            EdgeGraphicAttribute edgeGraphicAtt = (EdgeGraphicAttribute) attributes
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            SortedCollectionAttribute drawnBends = edgeGraphicAtt.getBends();
            double x = 0;
            try {
                CoordinateAttribute co = (CoordinateAttribute) drawnBends
                        .getAttribute("bend0");
                x = co.getX();
            } catch (AttributeNotFoundException e) {
                // There is no bend. Therefore all nodes of the edge have to
                // be in a vertical line.
                x = nodes.getFirst().getX();
            }

            // Get the column corresponding to this x coordinate or create a
            // new one if there isn't any with this coordinate.
            Plane column = columnMap.get(new Double(x));
            if (column == null) {
                column = new Plane(grid, GridStructure.COLUMN);
                column.setUncheckedCoordinate(x);
                columnMap.put(new Double(x), column);
            }

            for (int level = fromLevel + 1; level < toLevel; level++) {
                SugiyamaDummyNode newNode = new SugiyamaDummyNode(this);
                levels[level].addNode(newNode);
                column.addNode(newNode);
                addDummyNodeWithoutBendUpdate(newNode);
            }
            updateBends();
        }
    }

    /**
     * Removes the given dummy node.
     * 
     * @param node
     *            node to remove
     */
    public void removeDummyNode(SugiyamaDummyNode node) {
        // O(k), k=#(dummy)nodes on this edge
        int position = -1;

        int counter = 0;
        for (SugiyamaNode actNode : nodes) {
            if (node.equals(actNode)) {
                position = counter;
                break;
            }
            counter++;
        }

        if (position != -1) {
            nodes.remove(position);
        }
        checkPosition();
    }

    /**
     * Removes the dummy nodes of this edge out of their levels and columns.
     * 
     * @return The column the dummy nodes had been placed.<br>
     *         <tt>null</tt> if the edge did not have dummy nodes.
     */
    public Plane removeDummies() {
        Plane result = (nodes.get(1).isDummy()) ? nodes.get(1).getColumn()
                : null;
        Iterator<SugiyamaNode> it = nodes.iterator();
        LinkedList<SugiyamaNode> toDelete = new LinkedList<SugiyamaNode>();

        while (it.hasNext()) {
            SugiyamaNode node = it.next();
            if (node.isDummy) {
                node.getLevel().deleteNode(node);
                node.getColumn().deleteNode(node);
                toDelete.add(node);
            }
        }
        nodes.remove(toDelete);

        return result;
    }

    /**
     * Updates the bends of the edge so that the coordinates of the first and
     * the last dummy node of this edge (if existing) are set as bends if they
     * are not in the same column as the source or target node. Also changes the
     * shape of the edge if necessary.
     */
    public void updateBends() {
        if (nodes.getFirst() == nodes.getLast())
            // this is a self loop so do not update the bends
            return;

        Graph graph = edge.getGraph();
        graph.getListenerManager().transactionStarted(this);

        EdgeGraphicAttribute edgeGraphicAtt = (EdgeGraphicAttribute) edge
                .getAttribute("graphics");
        SortedCollectionAttribute drawnBends = edgeGraphicAtt.getBends();

        int bendsNeeded = 0;
        // If the first node after the source node is a dummy node and it is in
        // a different column then the source node, then there is a bend.
        if (nodes.get(1).isDummy()
                && (nodes.get(0).getColumnNumber() != nodes.get(1)
                        .getColumnNumber())) {
            bends[0] = (SugiyamaDummyNode) nodes.get(1);
            bendsNeeded++;
        } else {
            bends[0] = null;
        }

        // If the second last node is a dummy node and differs from the node
        // defining the first bend and is in a different column then the target
        // node, then there is a bend.
        if (nodes.get(nodes.size() - 2).isDummy
                && nodes.get(nodes.size() - 2) != bends[0]
                && (nodes.get(nodes.size() - 2).getColumnNumber() != nodes.get(
                        nodes.size() - 1).getColumnNumber())) {
            bends[1] = (SugiyamaDummyNode) nodes.get(nodes.size() - 2);
            bendsNeeded++;
        } else {
            bends[1] = null;
        }

        CoordinateAttribute[] bendCoords = new CoordinateAttribute[] { null,
                null };
        for (int i = 0; i < bendsNeeded; i++) {
            bendCoords[i] = (CoordinateAttribute) drawnBends.getCollection()
                    .get("bend" + i);
            if (bendCoords[i] == null) {
                bendCoords[i] = new CoordinateAttribute("bend" + i, Double.NaN,
                        Double.NaN);
                drawnBends.add(bendCoords[i]);
            }
        }

        // update bend attributes of the edge
        for (int i = 0; i < bendsNeeded; i++) {

            if (bendsNeeded == 2 && upwards) {
                // there are two bends but they have to be inserted in
                // inverse order
                double newCoord = bends[1 - i].getX();
                if (bendCoords[i].getX() != newCoord) {
                    bendCoords[i].setX(newCoord);
                }
                newCoord = bends[1 - i].getY();
                if (bendCoords[i].getY() != newCoord) {
                    bendCoords[i].setY(newCoord);
                }
            } else if (bends[0] == null) {
                // the upper bend is not needed, so the lower bend is 'bend0'.
                double newCoord = bends[1].getX();
                if (bendCoords[0].getX() != newCoord) {
                    bendCoords[0].setX(newCoord);
                }
                newCoord = bends[1].getY();
                if (bendCoords[0].getY() != newCoord) {
                    bendCoords[0].setY(newCoord);
                }
            } else {
                double newCoord = bends[i].getX();
                if (bendCoords[i].getX() != newCoord) {
                    bendCoords[i].setX(newCoord);
                }
                newCoord = bends[i].getY();
                if (bendCoords[i].getY() != newCoord) {
                    bendCoords[i].setY(newCoord);
                }
            }
        }

        // delete unwanted bends
        Iterator<String> it = drawnBends.getCollection().keySet().iterator();
        if (bends[0] != null && it.hasNext()) {
            it.next();
        }
        if (bends[1] != null && it.hasNext()) {
            it.next();
        }

        while (it.hasNext()) {
            drawnBends.remove(it.next());
        }

        // set the correct shape of the edge
        if (bends[0] == null && bends[1] == null) {
            // smooth shape
            if (edge.getString(PATH_OF_SHAPE).compareTo(PATH_OF_SMOOTH_SHAPE) != 0) {
                edge.setString(PATH_OF_SHAPE, PATH_OF_SMOOTH_SHAPE);
            }
        } else {
            // poly shape
            if (edge.getString(PATH_OF_SHAPE).compareTo(PATH_OF_POLY_SHAPE) != 0) {
                edge.setString(PATH_OF_SHAPE, PATH_OF_POLY_SHAPE);
            }
        }
        graph.getListenerManager().transactionFinished(this);
    }

    void swapDirection() {
        // only swap direction if there are no dummy nodes in between
        assert nodes.size() == 2;
        getNodes().addFirst(getNodes().removeLast());
        upwards = !upwards;
    }

    /**
     * Checks if the horizontal position of the edge is in-between the columns
     * of the start and the end node of the edge.
     */
    public void checkPosition() {
        checkPosition(null);
    }

    /**
     * Checks if the horizontal position of the edge is in-between the columns
     * of the start and the end node of the edge. If it isn't in-between but has
     * been before the column is added to the given HashSet.
     */
    public void checkPosition(HashSet<Plane> columnsToCheck) {
        int colTop = nodes.getFirst().getColumnNumber();
        int colBottom = nodes.getLast().getColumnNumber();
        int colLeft = (colTop <= colBottom) ? colTop : colBottom;
        int colRight = (colBottom >= colTop) ? colBottom : colTop;

        if (nodes.size() == 2) {
            // there is no dummy node and therefore the edge is straight between
            // the start and the end node
            centeredPosition = 0;
            return;
        }
        int colEdge = nodes.get(1).getColumnNumber();
        if (colEdge < colLeft) {
            // edge is too far left and should be moved to the right
            if (centeredPosition != TO_RIGHT && columnsToCheck != null) {
                columnsToCheck.add(nodes.get(1).getColumn());
            }
            centeredPosition = TO_RIGHT;
        } else if (colEdge > colRight) {
            // edge is too far right and should be moved to the left
            if (centeredPosition != TO_LEFT && columnsToCheck != null) {
                columnsToCheck.add(nodes.get(1).getColumn());
            }
            centeredPosition = TO_LEFT;
        } else {
            centeredPosition = STAY;
        }
        IncrementalSugiyama.debug("Shift direction for edge in column "
                + colEdge + ": " + centeredPosition);

    }

    /*
     * @see org.graffiti.core.DeepCopy#copy()
     */
    public Object copy() {
        try {
            return this.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /*  ************* Getter and Setter ************ */

    /**
     * Getter of the property <tt>edge</tt>
     * 
     * @return Returns the edge.
     * @uml.property name="edge" readOnly="true"
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Returns the <tt>SugiyamaNode</tt> or <tt>SugiyamaDummyNode</tt> on the
     * next higher level in respect of the given node.<br>
     * If there is no such node, <code>null</code> is returned.
     */
    public SugiyamaNode getNextNode(SugiyamaNode node) {
        // O(n), n=#nodes in this edge
        int position = nodes.indexOf(node);
        if (position == -1 || position == nodes.size() - 1)
            // the given node is not in this edge or is the last node of this
            // edge
            return null;
        return nodes.get(position + 1);
    }

    /**
     * Returns the <tt>SugiyamaNode</tt> or <tt>SugiyamaDummyNode</tt> on the
     * next smaller level in respect of the given node.<br>
     * If there is no such node, <code>null</code> is returned.
     */
    public SugiyamaNode getPrevNode(SugiyamaNode node) {
        // O(n), n=#nodes in this edge
        int position = nodes.indexOf(node);
        if (position == -1 || position == 0)
            // the given node is not in this edge or is the first node of this
            // edge
            return null;
        return nodes.get(position - 1);
    }

    /**
     * Getter of the property <tt>dummyNodes</tt>
     * 
     * @return Returns the dummyNodes.
     * @uml.property name="dummyNodes"
     */
    public LinkedList<SugiyamaNode> getNodes() {
        return nodes;
    }

    /**
     * Returns the upwards.
     * 
     * @return the upwards.
     */
    public boolean isUpwards() {
        return upwards;
    }

    public int getCenteredPosition() {
        return centeredPosition;
    }
}
