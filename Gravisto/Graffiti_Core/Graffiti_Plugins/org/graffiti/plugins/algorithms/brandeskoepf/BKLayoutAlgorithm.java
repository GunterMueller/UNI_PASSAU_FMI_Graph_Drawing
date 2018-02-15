//==============================================================================
//
//   BKLayoutAlgorithm.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: BKLayoutAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;

/**
 * The Brandes/Koepf algorithm<br>
 * An implementation of a simple algorithm plugin example which generates a
 * horizontal node chain with a user defined number of nodes.
 * 
 * @author Florian Fischer
 */
public class BKLayoutAlgorithm extends AbstractAlgorithm {
    // ~ Instance fields
    // ========================================================

    /** the handler for the controlling of the direction */
    private BKDirectionHandler direction;

    // ========================================================

    /** all edges for internal data structuring */
    private Collection<Edge> edges;

    /** all nodes for internal data structuring */
    private Collection<Node> nodes;

    /** the align-nodes of the nodes */
    private Matrix2Dim<Node> align;

    /** the internal data structre for the embedding */
    private Matrix2Dim<Node> level;

    /** the root nodes of the nodes */
    private Matrix2Dim<Node> root;

    /** the shift distance of the nodes */
    private Matrix2Dim<Double> shift;

    /** the sinks of the nodes */
    private Matrix2Dim<Node> sink;

    /** the different coordinates of the nodes */
    private DoubleMatrix3Dim coordinates;

    /** the edges to the lower neighbours of the nodes */
    private Matrix3Dim<Edge> edgesToLowerNeighbours;

    /** the edges to the upper neighbours of the nodes */
    private Matrix3Dim<Edge> edgesToUpperNeighbours;

    /** the lower neighbours of the nodes */
    private Matrix3Dim<Node> lowerNeighbours;

    /** the upper neighbours of the nodes */
    private Matrix3Dim<Node> upperNeighbours;

    /** number of levels */
    private int levelNum = 0;

    /** delete edges/nodes from the graph or not - defaults to true */
    private boolean deleteGraphElements = true;

    // ~ Constructors
    // ===========================================================

    // ===========================================================

    /**
     * Constructs a new instance.
     */
    public BKLayoutAlgorithm() {
        // logger.log(Level.OFF,"Brandes/K�pf started");
        BKConst.setDefault();
    }

    // ~ Methods
    // ================================================================

    // ================================================================

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Brandes/K�pf Algorithm";
    }

    /**
     * @see AbstractAlgorithm#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return null;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        // The graph is inherited from AbstractAlgorithm.
        if (this.graph == null) {
            errors.add("The graph instance may not be null.");
        } else {
            nodes = this.graph.getNodes();
            edges = this.graph.getEdges();

            // Test for an existing graph
            if ((nodes == null) || (edges == null)) {
                errors.add("First you have to create or load a graph!");
            } else {

                // check for existing bends
                int counter = 0;

                for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
                    Edge tempEdge = it.next();
                    try {
                        tempEdge.getDouble("graphics.bends.bend0.x");
                    } catch (AttributeNotFoundException anfe) {
                        counter++;
                    }
                }

                if (counter < edges.size()) {
                    errors
                            .add("The algorithm cannot handle bends in edges. Please delete them.");
                }

                if (!errors.isEmpty())
                    throw errors;

                int maxLevel = 0;

                // Test for the attribute 'level'
                boolean neg = false;

                try {
                    for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
                        Node tempNode = it.next();

                        int levelNr = tempNode.getInteger(BKConst
                                .getPATH_LEVEL());

                        // store values for the test of continuous numbers
                        if (levelNr > maxLevel) {
                            maxLevel = levelNr;
                        }

                        if (levelNr < 0) {
                            neg = true;
                        }
                    }
                } catch (AttributeNotFoundException anfe) {
                    errors
                            .add("The attribute 'level' was not found at a node."
                                    + " Please add the node attribute 'level' to all nodes.");
                } catch (ClassCastException cce) {
                    errors
                            .add("Attribute 'level' has to be an IntegerAttribute");
                }

                if (neg) {
                    errors
                            .add("Attribute 'level' is < 0. It has to be numerized continuous"
                                    + " from 0 to h-1 (h = number of levels)");
                }

                if (!errors.isEmpty())
                    throw errors;

                // Test for the attribute 'order'
                Matrix2Dim<Node> testLevel = new Matrix2Dim<Node>(maxLevel);
                neg = false;

                try {
                    for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
                        Node tempNode = it.next();

                        int orderNr = tempNode.getInteger(BKConst
                                .getPATH_ORDER());

                        if (orderNr < 0) {
                            neg = true;
                        }

                        testLevel.set(GraffitiGraph.getNodeLevel(tempNode),
                                GraffitiGraph.getNodeOrder(tempNode), tempNode);
                    }
                } catch (AttributeNotFoundException anfe) {
                    errors
                            .add("The attribute 'order' was not found at a node."
                                    + " Please add the node attribute 'order' to all nodes.");
                } catch (ClassCastException cce) {
                    errors
                            .add("Attribute 'order' has to be an IntegerAttribute");
                }

                if (neg) {
                    errors
                            .add("Attribute 'order' is < 0. It has to be numerized continuous"
                                    + " from 0 to m-1 (m = number of nodes in the level)");
                }

                if (!errors.isEmpty())
                    throw errors;

                // test for continuous numbering
                boolean notContinuous = false;
                int iTest = 0;

                for (int i = 0; i < testLevel.lines(); i++) {
                    int jTest = 0;

                    for (int j = 0; j < testLevel.elementsOfLine(i); j++) {
                        // Is the level and order number continuous?
                        if (testLevel.get(i, j) == null) {
                            notContinuous = true;
                        } else if ((GraffitiGraph.getNodeOrder(testLevel.get(i,
                                j)) != jTest)
                                || (GraffitiGraph.getNodeLevel(testLevel.get(i,
                                        j)) != iTest)) {
                            notContinuous = true;
                        }

                        jTest++;
                    }

                    iTest++;
                }

                if (notContinuous) {
                    errors
                            .add("Attribute 'level' has to be numerized continuous"
                                    + " from 0 to h-1 (h = number of levels)");
                    errors
                            .add("Attribute 'order' has to be numerized continuous"
                                    + " from 0 to m-1 (m = number of nodes in the level)");
                }

                if (!errors.isEmpty())
                    throw errors;

                // Test for the attribute 'dummy'
                neg = false;

                boolean dummyMisplace = false;
                boolean inUnEqualOut = false;

                try {
                    for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
                        Node tempNode = it.next();

                        int dummyInfo = tempNode.getInteger(BKConst
                                .getPATH_DUMMY());

                        if ((dummyInfo < 0) || (dummyInfo > 1)) {
                            neg = true;
                        }

                        // there mustnt be nodes on the first an last level
                        if ((dummyInfo == 1)
                                && ((0 == tempNode.getInteger(BKConst
                                        .getPATH_LEVEL())) || (maxLevel == tempNode
                                        .getInteger(BKConst.getPATH_LEVEL())))) {
                            dummyMisplace = true;
                        }

                        // a dummy node can only have one upper and one lower
                        // neighbour
                        if ((dummyInfo == 1)
                                && (((tempNode.getAllInEdges()).size() > 1) || ((tempNode
                                        .getAllOutEdges()).size() > 1))) {
                            inUnEqualOut = true;
                        }
                    }
                } catch (AttributeNotFoundException anfe) {
                    errors
                            .add("The attribute 'dummy' was not found at a node."
                                    + " Please add the node attribute 'dummy' to all nodes.");
                } catch (ClassCastException cce) {
                    errors
                            .add("Attribute 'dummy' has to be an IntegerAttribute");
                }

                if (neg) {
                    errors
                            .add("Attribute 'dummy' has to be 1 for 'dummy node' and"
                                    + " 0 for 'no dummy node'");
                }

                if (dummyMisplace) {
                    errors
                            .add("Dummy nodes must'nt be placed on the first and the last level");
                }

                if (inUnEqualOut) {
                    errors
                            .add("Dummy nodes can have only exact one upper and exact one lower neighbour");
                }

                if (!errors.isEmpty())
                    throw errors;

                // Test for the attribute 'cutEdge', but only if the layout
                // should be a radial one
                if (BKConst.getDRAW() == 5) {
                    neg = false;

                    try {
                        for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
                            Edge tempEdge = it.next();
                            tempEdge.getInteger(BKConst.getPATH_CUTEDGE());
                        }
                    } catch (AttributeNotFoundException anfe) {
                        errors
                                .add("The attribute 'cutEdge' was not found at a edge."
                                        + " Please add the edge attribute 'cutEdge' to all edge.");
                    } catch (ClassCastException cce) {
                        errors
                                .add("Attribute 'cutEdge' has to be an IntegerAttribute");
                    }

                    if (neg) {
                        errors
                                .add("Attribute 'cutEdge' has to be 1 for 'cut edge' and"
                                        + " 0 for 'no cut edge'");
                    }
                }

                // clean up
                testLevel = null;
                nodes = null;
                edges = null;
            }
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // Initialise the data structures
        initialise();

        // logger.log(Level.OFF,"Data structure constructed");
        // Mark type1 conflicts
        markConflicts();

        try {
            // 0: o->u, l->r
            // 1: o->u, r->l
            // 2: u->o, l->r
            // 3: u->o, r->l
            for (int dir = 0; dir < 4; dir++) {
                verticalAlignment(dir);

                horizontalCompaction(dir);

                // ===============================================================
                // Clean up for the next direction run --- BEGIN
                // ===============================================================
                root.cloneMatrix(level);
                align.cloneMatrix(level);
                sink.cloneMatrix(level);
                shift.assignMatrixSize(level);

                // Reset the data structures
                for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
                    Node tempNode = it.next();
                    GraffitiGraph.setNodeIsShiftSet(tempNode, false);
                    GraffitiGraph.setNodeCoordInitial(tempNode, true);
                }

                // ===============================================================
                // Clean up for the next direction run --- END
                // ===============================================================
            }
        } catch (ArrayIndexOutOfBoundsException a) {
            throw new RuntimeException("Data structure failure. Wrong index. "
                    + a.getCause());
        } catch (ClassCastException a) {
            throw new RuntimeException(
                    "Data structure failure. Wrong object found. "
                            + a.getCause());
        } catch (AttributeNotFoundException a) {
            throw new RuntimeException(
                    "Data structure failure. Attribute not found. "
                            + a.getCause());
        } catch (NullPointerException a) {
            throw new RuntimeException(
                    "Data structure failure. Null pointer exception. "
                            + a.getCause());
        } finally {
            // Clean edge and node attributes, added from this algorithm
            for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
                Edge tempEdge = it.next();

                // remove the attribute
                tempEdge.removeAttribute(BKConst.getPATH_EDGEMARKED_GET());

                // remove the attribute folders
                tempEdge.removeAttribute(BKConst.getPATH_OWN_ATTRIBUTES1());
                tempEdge.removeAttribute(BKConst.getPATH_OWN_ATTRIBUTES2());
            }

            for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
                Node tempNode = it.next();

                // remove the attributes
                tempNode.removeAttribute(BKConst.getPATH_SHIFTSET_GET());
                tempNode.removeAttribute(BKConst.getPATH_COORD_INITIAL());

                // remove the attribute folders
                tempNode.removeAttribute(BKConst.getPATH_OWN_ATTRIBUTES1());
                tempNode.removeAttribute(BKConst.getPATH_OWN_ATTRIBUTES2());
            }
        }

        try {
            // ===============================================================
            // The coordinate assignment--- BEGIN
            // ===============================================================
            switch (BKConst.getDRAW()) {
            case 0:
                drawStep(0);

                break;

            case 1:
                drawStep(1);

                break;

            case 2:
                drawStep(2);

                break;

            case 3:
                drawStep(3);

                break;

            case 4:
                drawHorizontal();

                break;

            case 5:
                drawRadial();

                break;
            }

            // ===============================================================
            // The coordinate assignment--- END
            // ===============================================================
        } catch (ArrayIndexOutOfBoundsException a) {
            throw new RuntimeException("Data structure failure. Wrong index. "
                    + a.getMessage());
        } catch (ClassCastException a) {
            throw new RuntimeException(
                    "Data structure failure. Wrong object found. "
                            + a.getMessage());
        } catch (AttributeNotFoundException a) {
            throw new RuntimeException(
                    "Data structure failure. Attribute not found. "
                            + a.getMessage());
        } catch (NullPointerException a) {
            throw new RuntimeException(
                    "Data structure failure. Null pointer exception. "
                            + a.getMessage());
        } finally {
            // clean data stuctures
            nodes = null;
            edges = null;
            level = null;
            align = null;
            sink = null;
            shift = null;
            root = null;
            coordinates = null;
            edgesToLowerNeighbours = null;
            edgesToUpperNeighbours = null;
            lowerNeighbours = null;
            upperNeighbours = null;
            direction = null;
        }

        // start a transaction
        graph.getListenerManager().transactionStarted(this);

        // stop a transaction
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
    }

    /**
     * Sets for each node his upper neighbours
     */
    private void setNeighbours() {
        // Go through every line, expect of the last, because these nodes have
        // no down neighbours
        for (int i = 0; i < level.lines(); i++) {
            for (int j = 0; j < level.elementsOfLine(i); j++) {
                Node tempNode = level.get(i, j);

                Collection<Edge> downEdges = tempNode.getAllOutEdges();
                Collection<Edge> upEdges = tempNode.getAllInEdges();

                // Go through these edges an add the viewed node as an upper
                // neighbour to the lower neighbours
                for (Iterator<Edge> it2 = downEdges.iterator(); it2.hasNext();) {
                    Edge tempEdge = it2.next();
                    Node tempDownNeighbourNode = tempEdge.getTarget();

                    upperNeighbours.add(GraffitiGraph
                            .getNodeLevel(tempDownNeighbourNode), GraffitiGraph
                            .getNodeOrder(tempDownNeighbourNode), tempNode);

                    edgesToUpperNeighbours.add(GraffitiGraph
                            .getNodeLevel(tempDownNeighbourNode), GraffitiGraph
                            .getNodeOrder(tempDownNeighbourNode), tempEdge);
                }

                // Go through these edges an add the viewed node as an lower
                // neighbour to the upper neighbours
                for (Iterator<Edge> it2 = upEdges.iterator(); it2.hasNext();) {
                    Edge tempEdge = it2.next();
                    Node tempUpNeighbourNode = tempEdge.getSource();

                    lowerNeighbours.add(GraffitiGraph
                            .getNodeLevel(tempUpNeighbourNode), GraffitiGraph
                            .getNodeOrder(tempUpNeighbourNode), tempNode);

                    edgesToLowerNeighbours.add(GraffitiGraph
                            .getNodeLevel(tempUpNeighbourNode), GraffitiGraph
                            .getNodeOrder(tempUpNeighbourNode), tempEdge);
                }
            }
        }
    }

    /**
     * This function realises the calibration and the coordinate assignment
     */
    private void calibrateCoordAssign() {
        // this is the common part of the calibration. for each kind of
        // calibration, this has to be done first.
        // take everything in the center
        for (int dir = 0; dir < 4; dir++) {
            // the width of the draft
            double maxValue = 0;

            // the l->r layouts
            if ((dir == 0) || (dir == 2)) {
                // find the maximum width
                for (int i = 0; i < level.lines(); i++) {
                    if (coordinates.get(i, level.elementsOfLine(i) - 1, dir)
                            .doubleValue() > maxValue) {
                        maxValue = coordinates.get(i,
                                level.elementsOfLine(i) - 1, dir).doubleValue();
                    }
                }
            }

            // the r->l layouts
            else {
                // find the maximum width, here it is a node with order number
                // 0, because of the rotation of the algorithm
                for (int i = 0; i < level.lines(); i++) {
                    if (coordinates.get(i, 0, dir).doubleValue() > maxValue) {
                        maxValue = coordinates.get(i, 0, dir).doubleValue();
                    }
                }
            }

            // calculate the center of the draft
            maxValue = ((maxValue - BKConst.getLEFT_DIST()) / 2)
                    + BKConst.getLEFT_DIST();

            // shift the center of the draft to zero
            for (int i = 0; i < level.lines(); i++) {
                for (int j = 0; j < level.elementsOfLine(i); j++) {
                    coordinates.set(i, j, dir, new Double(coordinates.get(i, j,
                            dir).doubleValue()
                            - maxValue));
                }
            }
        }

        // turn the r->l drafts around
        for (int i = 0; i < level.lines(); i++) {
            for (int j = 0; j < level.elementsOfLine(i); j++) {
                // turn around t->b, r->l
                coordinates.set(i, j, 1, new Double(coordinates.get(i, j, 1)
                        .doubleValue()
                        * (-1)));

                // turn around b->t, r->l
                coordinates.set(i, j, 3, new Double(coordinates.get(i, j, 3)
                        .doubleValue()
                        * (-1)));
            }
        }

        switch (BKConst.getCALIBRATION()) {
        // move the center of the layouts to 0 and rotate the r->l layouts
        case 0:

            // nothing else is to do
            break;

        // align to assignment of smallest width
        case 1:

            // find the smallest layout
            int smallestLayout = -1;
            double smallestLayoutValue = 100000;
            Matrix2Dim<Double> extremValues = new Matrix2Dim<Double>(4);

            // go through all of the four layouts
            for (int dir = 0; dir < 4; dir++) {
                // the width of the draft
                double maxValue = 0;
                double minValue = 0;

                // find the width
                for (int i = 0; i < level.lines(); i++) {
                    // a minimum value can only be on the left border of the
                    // graph
                    if (coordinates.get(i, 0, dir) < minValue) {
                        minValue = coordinates.get(i, 0, dir).doubleValue();
                    }

                    // a maximum value can only be on the right border of
                    // the graph
                    if (coordinates.get(i, level.elementsOfLine(i) - 1, dir) > maxValue) {
                        maxValue = coordinates.get(i,
                                level.elementsOfLine(i) - 1, dir).doubleValue();
                    }
                }

                // save the extrem values for later
                extremValues.set(dir, 0, minValue);
                extremValues.set(dir, 1, maxValue);

                // we have found a smaller layout
                if (smallestLayoutValue > (maxValue - minValue)) {
                    smallestLayoutValue = maxValue - minValue;
                    smallestLayout = dir;
                }
            }

            // align the layouts to the layout with the smallest width
            for (int dir = 0; dir < 4; dir++) {
                if (dir != smallestLayout) {
                    double difference = 0;

                    // the l->r layouts have to be assigned to the left
                    // border of the smallest layout
                    if ((dir == 0) || (dir == 2)) {
                        // calculate the differece between the two left
                        // borders
                        difference = extremValues.get(smallestLayout, 0)
                                - extremValues.get(dir, 0);
                    }

                    // the r->l layouts have to be assigned to the right
                    // border of the smallest layout
                    else {
                        // calculate the differece between the two right
                        // borders
                        difference = extremValues.get(smallestLayout, 1)
                                - extremValues.get(dir, 1);
                    }

                    // shift the layout
                    if (difference != 0) {
                        for (int i = 0; i < level.lines(); i++) {
                            for (int j = 0; j < level.elementsOfLine(i); j++) {
                                coordinates.set(i, j, dir, coordinates.get(i,
                                        j, dir)
                                        + difference);
                            }
                        }
                    }
                }
            }

            break;

        case 2:

            // balance center to 0
            for (int dir = 0; dir < 4; dir++) {
                double balancePoint = 0;
                int nodesNum = 0;

                // go to every node and sum up the coordinates
                for (int i = 0; i < level.lines(); i++) {
                    for (int j = 0; j < level.elementsOfLine(i); j++) {
                        balancePoint += coordinates.get(i, j, dir);
                        nodesNum++;
                    }
                }

                // compute the balance point
                balancePoint = balancePoint / nodesNum;

                // go to every node and shift the balance point to 0
                for (int i = 0; i < level.lines(); i++) {
                    for (int j = 0; j < level.elementsOfLine(i); j++) {
                        coordinates.set(i, j, dir, coordinates.get(i, j, dir)
                                - balancePoint);
                        nodesNum++;
                    }
                }
            }

            break;
        }

        switch (BKConst.getCOORD_ASSIGNMENT()) {
        // average of medians
        case 0:

            // calculate the average median and assign the value to the
            // X-Coordinate
            for (int i = 0; i < level.lines(); i++) {
                for (int j = 0; j < level.elementsOfLine(i); j++) {
                    ArrayList<Double> coordList = new ArrayList<Double>(4);

                    double min = 100000;
                    double max = -100000;

                    int minIndex = 0;
                    int maxIndex = 0;

                    for (int v = 0; v < 4; v++) {
                        double temp = coordinates.get(i, j, v);

                        if (temp >= max) {
                            max = temp;
                            maxIndex = v;
                        }

                        if (temp < min) {
                            min = temp;
                            minIndex = v;
                        }

                        coordList.add(v, new Double(temp));
                    }

                    // delete the extrem values
                    coordList.set(minIndex, new Double(0));
                    coordList.set(maxIndex, new Double(0));

                    // assign the average median to the first value of the
                    // coordinate matrix
                    double tempCoord = 0;

                    for (int v = 0; v < coordList.size(); v++) {
                        tempCoord += coordList.get(v);
                    }

                    coordinates.set(i, j, 0, tempCoord / 2);
                }
            }

            break;

        // average of all
        case 1:

            for (int i = 0; i < level.lines(); i++) {
                for (int j = 0; j < level.elementsOfLine(i); j++) {
                    // sum up the coordinates
                    for (int v = 1; v < 4; v++) {
                        coordinates.set(i, j, 0, coordinates.get(i, j, v)
                                + coordinates.get(i, j, 0));
                    }

                    // calculate the average
                    coordinates.set(i, j, 0, coordinates.get(i, j, 0) / 4);
                }
            }

            break;
        }
    }

    /**
     * This function realises the horizontal layout
     */
    private void drawHorizontal() {
        // logger.log(Level.OFF,"The graph will be drawn with the average
        // median. The calibration is: the middle shift to zero");
        calibrateCoordAssign();

        // shift the final values to the apparent right side an assign the right
        // Y-Coordinates
        // find the minimum value
        double minValue = 0;

        for (int i = 0; i < level.lines(); i++) {
            if (coordinates.get(i, 0, 0) < minValue) {
                minValue = coordinates.get(i, 0, 0);
            }
        }

        // shift and do the Y-Coordinates
        double y = BKConst.getTOP_DIST();

        for (int i = 0; i < level.lines(); i++) {
            for (int j = 0; j < level.elementsOfLine(i); j++) {
                Node tempNode = level.get(i, j);
                GraffitiGraph.setNodeCoordX(tempNode, coordinates.get(i, j, 0)
                        + (-minValue) + BKConst.getLEFT_DIST());
                GraffitiGraph.setNodeCoordY(tempNode, y);
            }

            y += BKConst.getLEVEL_DIST();
        }

        // draw edges
        if (BKConst.getCOMPUT_LONG_SPAN_EDGES() == 0) {
            // find long span edges and compute them
            for (int i = 0; i < level.lines(); i++) {
                for (int j = 0; j < level.elementsOfLine(i); j++) {
                    Node node = level.get(i, j);

                    // only not dummy nodes can be the source of long span edges
                    if (!GraffitiGraph.getNodeIsDummy(node)) {
                        // look at all following down nodes
                        Collection<Edge> outEgdes = node.getAllOutEdges();

                        for (Iterator<Edge> iter = outEgdes.iterator(); iter
                                .hasNext();) {
                            // the edge for the first node
                            Edge edge = iter.next();
                            Node target = edge.getTarget();

                            // do, if the target node is a dummy node. Then we
                            // have a long span edge
                            if (GraffitiGraph.getNodeIsDummy(target)) {
                                // logger.log(Level.OFF,"Knoten "+i+"_"+j+" hat
                                // einen dummy Knoten als unteren Nachbarn.");
                                Edge newEdge = null;
                                ArrayList<Double> bends = new ArrayList<Double>();
                                ArrayList<Edge> edgesToDelete = new ArrayList<Edge>();
                                ArrayList<Node> nodesToDelete = new ArrayList<Node>();

                                // the first edge has to be deleted
                                edgesToDelete.add(edge);

                                // the target node has to be a dummy node, if it
                                // is a long span edge
                                // search until a not dummy node, the target
                                // node of the long span edge, is found
                                while (GraffitiGraph.getNodeIsDummy(target)) {
                                    // logger.log(Level.OFF,"Knoten
                                    // "+GraffitiGraph.getNodeLevel(target)+"_"+GraffitiGraph.getNodeOrder(target)+"
                                    // ist ein dummy Knoten");
                                    // this node can only have one out going
                                    // edge, because it is a dummy node
                                    Collection<Edge> outEdgesOfTarget = target
                                            .getAllOutEdges();
                                    Iterator<Edge> iter2 = outEdgesOfTarget
                                            .iterator();
                                    Edge tempEdge = iter2.next();
                                    Node targetOfTarget = tempEdge.getTarget();

                                    // logger.log(Level.OFF,"Dessen
                                    // Nachfolgerknoten ist
                                    // "+GraffitiGraph.getNodeLevel(targetOfTarget)+"_"+GraffitiGraph.getNodeOrder(targetOfTarget));
                                    // collect the bends
                                    bends.add(new Double(GraffitiGraph
                                            .getNodeCoordX(target)));
                                    bends.add(new Double(GraffitiGraph
                                            .getNodeCoordY(target)));

                                    // collect the node an edge for deleting
                                    edgesToDelete.add(tempEdge);
                                    nodesToDelete.add(target);

                                    // focus the next node
                                    target = targetOfTarget;
                                }

                                if (deleteGraphElements) {
                                    // add a new edge, the long span edge to the
                                    // graph
                                    newEdge = this.graph.addEdge(node, target,
                                            true);

                                    // add bends
                                    int bendIndex = 0;

                                    for (int k = 0; k < bends.size(); k++) {
                                        CoordinateAttribute ca = new CoordinateAttribute(
                                                BKConst.getBEND_I() + bendIndex,
                                                bends.get(k), bends.get(k + 1));
                                        newEdge.addAttribute(ca, BKConst
                                                .getPATH_BENDS());
                                        k++;
                                        bendIndex++;
                                    }

                                    // give the edge a polyline shape
                                    newEdge.setString(BKConst.getPATH_SHAPE(),
                                            BKConst.getPATH_SHAPE_POLY());

                                    // delete edges
                                    for (int k = 0; k < edgesToDelete.size(); k++) {
                                        this.graph.deleteEdge(edgesToDelete
                                                .get(k));
                                    }

                                    // delete nodes
                                    for (int k = 0; k < nodesToDelete.size(); k++) {
                                        this.graph.deleteNode(nodesToDelete
                                                .get(k));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Update the nodes and edges collection
        nodes = this.graph.getNodes();
        edges = this.graph.getEdges();
    }

    /**
     * This function realises the radial layout
     */
    private void drawRadial() {
        calibrateCoordAssign();

        // shift the final values to the apparent right side
        double minValue = 0;
        double maxValue = 0;
        double width = 0;
        double placeCentre = 0.0;

        if (level.elementsOfLine(0) > 1) {
            placeCentre = 1.0;
        }

        // find the extrem values and the widest level
        for (int i = 0; i < level.lines(); i++) {
            // find the minimum value
            if (coordinates.get(i, 0, 0) < minValue) {
                minValue = coordinates.get(i, 0, 0).doubleValue();
            }

            // find the maximum value
            if (coordinates.get(i, level.elementsOfLine(i) - 1, 0) > maxValue) {
                maxValue = coordinates.get(i, level.elementsOfLine(i) - 1, 0);
            }

            // find the widest level
            if (width < (coordinates.get(i, level.elementsOfLine(i) - 1, 0) - coordinates
                    .get(i, 0, 0))) {
                width = coordinates.get(i, level.elementsOfLine(i) - 1, 0)
                        - coordinates.get(i, 0, 0);
            }
        }

        // add the minimum node distance
        width += BKConst.getMINDIST();

        // calculate width of the graph, plus the minimum distance between
        // nodes, because of the connection between 360 and 0 degree
        // logger.log(Level.OFF,"maxValue: "+maxValue+" minValue: "+minValue);
        // shift
        for (int i = 0; i < level.lines(); i++) {
            for (int j = 0; j < level.elementsOfLine(i); j++) {
                level.get(i, j);
                coordinates
                        .set(i, j, 0, coordinates.get(i, j, 0) + (-minValue));
            }
        }

        // Check the distance between the nodes on the circle arc and scale if
        // it is neccessary
        double scaleAddOn = 0;

        for (int i = 0; i < level.lines(); i++) {
            for (int j = 0; j < level.elementsOfLine(i); j++) {
                // the last node in the line, then ceck distance with the first,
                // but only if the node is not allone on the level
                if ((j == (level.elementsOfLine(i) - 1)) && (j != 0)) {
                    double value1 = coordinates.get(i, 0, 0);
                    double value2 = coordinates.get(i, j, 0);

                    // The length of the circular arc
                    double circularArc = 2.0
                            * ((i + placeCentre) * (BKConst
                                    .getRADIAL_LEVEL_DIST() + scaleAddOn))
                            * (1.0 - ((value2 - value1) / width)) * Math.PI;

                    // make the distance big enough
                    if (circularArc < BKConst.getRADIAL_MINDIST()) {
                        // scale
                        scaleAddOn += (BKConst.getRADIAL_MINDIST() - circularArc)
                                / (2.0 * (i + placeCentre)
                                        * (1.0 - ((value2 - value1) / width)) * Math.PI);
                    }
                }

                // check the distance to the next node, if there are more than
                // one node
                else if (1 != level.elementsOfLine(i)) {
                    double value1 = coordinates.get(i, j, 0);
                    double value2 = coordinates.get(i, j + 1, 0);

                    // The length of the circular arc
                    double circularArc = 2.0
                            * ((i + placeCentre) * (BKConst
                                    .getRADIAL_LEVEL_DIST() + scaleAddOn))
                            * (1.0 - ((value2 - value1) / width)) * Math.PI;

                    // make the distance big enough
                    if (circularArc < BKConst.getRADIAL_MINDIST()) {
                        // scale old
                        // scaleAddOn += BKConst.getRADIAL_SCALE_STEP();
                        // circularArc += (2.0 * (((double) i + placeCentre) *
                        // BKConst.getRADIAL_SCALE_STEP()) * (1.0 -
                        // ((value2 - value1) / width)) * Math.PI);

                        // scale
                        scaleAddOn += (BKConst.getRADIAL_MINDIST() - circularArc)
                                / (2.0 * (i + placeCentre)
                                        * (1.0 - ((value2 - value1) / width)) * Math.PI);
                    }
                }
            }
        }

        // calculate center and place nodes
        double centerX = ((level.lines() - 1 + placeCentre) * (BKConst
                .getRADIAL_LEVEL_DIST() + scaleAddOn))
                + BKConst.getTOP_DIST();
        double centerY = ((level.lines() - 1 + placeCentre) * (BKConst
                .getRADIAL_LEVEL_DIST() + scaleAddOn))
                + BKConst.getLEFT_DIST();

        // logger.log(Level.OFF,"width: "+width);
        // logger.log(Level.OFF,"expand: "+expand);
        // place nodes, find long span edges and draw the edges
        for (int i = 0; i < level.lines(); i++) {
            for (int j = 0; j < level.elementsOfLine(i); j++) {
                Node node = level.get(i, j);

                // only not dummy nodes can be the source of long span edges or
                // have to be treaten with edge-transformation
                if (!GraffitiGraph.getNodeIsDummy(node)
                        || (BKConst.getCOMPUT_LONG_SPAN_EDGES() == 1)) {
                    // set coordinates of the node times the compaction factor
                    GraffitiGraph
                            .setNodeCoordX(
                                    node,
                                    ((i + placeCentre)
                                            * (BKConst.getRADIAL_LEVEL_DIST() + scaleAddOn) * Math
                                            .cos((coordinates.get(i, j, 0) * (2 * Math.PI))
                                                    / width))
                                            + centerX);
                    GraffitiGraph
                            .setNodeCoordY(
                                    node,
                                    ((i + placeCentre)
                                            * (BKConst.getRADIAL_LEVEL_DIST() + scaleAddOn) * Math
                                            .sin((coordinates.get(i, j, 0)
                                                    .doubleValue() * (2 * Math.PI))
                                                    / width))
                                            + centerY);

                    // look at all following down nodes
                    Collection<Edge> outEgdes = node.getAllOutEdges();

                    for (Iterator<Edge> iter = outEgdes.iterator(); iter
                            .hasNext();) {
                        // the edge for the first node
                        Edge edge = iter.next();

                        // the target of this edge
                        Node target = edge.getTarget();

                        // the list of the bends to be generated
                        ArrayList<Double> bends = new ArrayList<Double>();

                        // do, if the target node is a dummy node. Then we have
                        // a long span edge
                        if (GraffitiGraph.getNodeIsDummy(target)
                                && (BKConst.getCOMPUT_LONG_SPAN_EDGES() == 0)) {
                            // logger.log(Level.OFF,"Knoten "+i+"_"+j+" hat
                            // einen dummy Knoten als unteren Nachbarn.");
                            Edge newEdge = null;
                            ArrayList<Edge> edgesToDelete = new ArrayList<Edge>();
                            ArrayList<Node> nodesToDelete = new ArrayList<Node>();

                            // the first edge has to be deleted
                            edgesToDelete.add(edge);

                            boolean firstEdgeSector = true;
                            boolean lastEdgeSector = false;

                            // the target node has to be a dummy node, if it is
                            // a long span edge
                            // search until a not dummy node, the target node of
                            // the long span edge, is found
                            while (GraffitiGraph.getNodeIsDummy(target)) {
                                // logger.log(Level.OFF,"Knoten
                                // "+GraffitiGraph.getNodeLevel(target)+"_"+GraffitiGraph.getNodeOrder(target)+"
                                // ist ein dummy Knoten");
                                // this node can only have one out going edge,
                                // because it is a dummy node
                                Collection<Edge> outEdgesOfTarget = target
                                        .getAllOutEdges();
                                Iterator<Edge> iter2 = outEdgesOfTarget
                                        .iterator();
                                Edge tempEdge = iter2.next();
                                Node targetOfTarget = tempEdge.getTarget();

                                if (!GraffitiGraph
                                        .getNodeIsDummy(targetOfTarget)) {
                                    lastEdgeSector = true;
                                }

                                // logger.log(Level.OFF,"Dessen Nachfolgerknoten
                                // ist
                                // "+GraffitiGraph.getNodeLevel(targetOfTarget)+"_"+GraffitiGraph.getNodeOrder(targetOfTarget));
                                // for the first sector of the long span edge
                                // sampling points have to be generated, but
                                // only when the X-Coordinates are different
                                if (firstEdgeSector
                                        && (coordinates.get(i, j, 0) != coordinates
                                                .get(
                                                        GraffitiGraph
                                                                .getNodeLevel(target),
                                                        GraffitiGraph
                                                                .getNodeOrder(target),
                                                        0).doubleValue())) {
                                    // the X- and Y-Coordinates of the nodes
                                    // times the compaction factor
                                    double xStart = coordinates.get(i, j, 0);
                                    double xEnd = coordinates.get(
                                            GraffitiGraph.getNodeLevel(target),
                                            GraffitiGraph.getNodeOrder(target),
                                            0).doubleValue();
                                    double yStart = ((i + placeCentre) * (BKConst
                                            .getRADIAL_LEVEL_DIST() + scaleAddOn));
                                    double yEnd = ((i + 1.0 + placeCentre) * (BKConst
                                            .getRADIAL_LEVEL_DIST() + scaleAddOn));

                                    if (GraffitiGraph.getEdgeIsCutEdge(edge)) {
                                        if (!GraffitiGraph.getNodeIsDummy(edge
                                                .getSource())
                                                && !GraffitiGraph
                                                        .getNodeIsDummy(edge
                                                                .getTarget())) {
                                            if (xStart > xEnd) {
                                                xEnd += width;
                                            } else {
                                                xStart += width;
                                            }
                                        }
                                    }

                                    double samplingStep = GraffitiGraph
                                            .getSamplingPointStep(Math
                                                    .abs(xStart - xEnd), i);

                                    // Dont calculate sampling points for a
                                    // spiral segment form for the node on level
                                    // 0, if there is only one.
                                    if ((i != 0)
                                            || (level.elementsOfLine(0) > 1)) {
                                        // generate sampling points
                                        while (samplingStep < 1.0) {
                                            double xSampling = (xStart + (samplingStep * (xEnd - xStart)));
                                            double ySampling = yStart
                                                    + (samplingStep * (yEnd - yStart));

                                            // collect the bends
                                            // the X-Coordinate of the bend
                                            bends
                                                    .add(new Double(
                                                            (ySampling * Math
                                                                    .cos((xSampling * (2 * Math.PI))
                                                                            / width))
                                                                    + centerX));

                                            // the Y-Coordinate of the bend
                                            bends
                                                    .add(new Double(
                                                            (ySampling * Math
                                                                    .sin((xSampling * (2 * Math.PI))
                                                                            / width))
                                                                    + centerY));
                                            samplingStep += GraffitiGraph
                                                    .getSamplingPointStep(
                                                            Math.abs(xStart
                                                                    - xEnd), i);
                                        }
                                    }

                                    firstEdgeSector = false;
                                }

                                // the coordiantes of the target nodes (in this
                                // loop always a dummy node) has to be added
                                bends
                                        .add(new Double(
                                                (((GraffitiGraph
                                                        .getNodeLevel(target) + placeCentre) * (BKConst
                                                        .getRADIAL_LEVEL_DIST() + scaleAddOn)) * Math
                                                        .cos(((coordinates
                                                                .get(
                                                                        GraffitiGraph
                                                                                .getNodeLevel(target),
                                                                        GraffitiGraph
                                                                                .getNodeOrder(target),
                                                                        0)
                                                                .doubleValue()) * (2 * Math.PI))
                                                                / width))
                                                        + centerX));
                                bends
                                        .add(new Double(
                                                (((GraffitiGraph
                                                        .getNodeLevel(target) + placeCentre) * (BKConst
                                                        .getRADIAL_LEVEL_DIST() + scaleAddOn)) * Math
                                                        .sin(((coordinates
                                                                .get(
                                                                        GraffitiGraph
                                                                                .getNodeLevel(target),
                                                                        GraffitiGraph
                                                                                .getNodeOrder(target),
                                                                        0)
                                                                .doubleValue()) * (2 * Math.PI))
                                                                / width))
                                                        + centerY));

                                // the last sector must also be computed, but
                                // only when the X-Coordinates are different
                                if (lastEdgeSector
                                        && (coordinates
                                                .get(
                                                        GraffitiGraph
                                                                .getNodeLevel(targetOfTarget),
                                                        GraffitiGraph
                                                                .getNodeOrder(targetOfTarget),
                                                        0).doubleValue() != coordinates
                                                .get(
                                                        GraffitiGraph
                                                                .getNodeLevel(target),
                                                        GraffitiGraph
                                                                .getNodeOrder(target),
                                                        0))) {
                                    // the X- and Y-Coordinates of the nodes
                                    double xStart = coordinates.get(
                                            GraffitiGraph.getNodeLevel(target),
                                            GraffitiGraph.getNodeOrder(target),
                                            0).doubleValue();
                                    double xEnd = coordinates
                                            .get(
                                                    GraffitiGraph
                                                            .getNodeLevel(targetOfTarget),
                                                    GraffitiGraph
                                                            .getNodeOrder(targetOfTarget),
                                                    0).doubleValue();
                                    double yStart = ((GraffitiGraph
                                            .getNodeLevel(target) + placeCentre) * (BKConst
                                            .getRADIAL_LEVEL_DIST() + scaleAddOn));
                                    double yEnd = ((GraffitiGraph
                                            .getNodeLevel(targetOfTarget) + placeCentre) * (BKConst
                                            .getRADIAL_LEVEL_DIST() + scaleAddOn));

                                    if (GraffitiGraph.getEdgeIsCutEdge(edge)) {
                                        if (!GraffitiGraph.getNodeIsDummy(edge
                                                .getSource())
                                                && !GraffitiGraph
                                                        .getNodeIsDummy(edge
                                                                .getTarget())) {
                                            if (xStart > xEnd) {
                                                xEnd += width;
                                            } else {
                                                xStart += width;
                                            }
                                        }

                                    }

                                    double samplingStep = GraffitiGraph
                                            .getSamplingPointStep(
                                                    Math.abs(xStart - xEnd),
                                                    GraffitiGraph
                                                            .getNodeLevel(target));

                                    // generate sampling points
                                    while (samplingStep < 1.0) {
                                        double xSampling = xStart
                                                + (samplingStep * (xEnd - xStart));
                                        double ySampling = yStart
                                                + (samplingStep * (yEnd - yStart));

                                        // collect the bends
                                        // the X-Coordinate of the bend
                                        bends
                                                .add(new Double(
                                                        (ySampling * Math
                                                                .cos((xSampling * (2 * Math.PI))
                                                                        / width))
                                                                + centerX));

                                        // the Y-Coordinate of the bend
                                        bends
                                                .add(new Double(
                                                        (ySampling * Math
                                                                .sin((xSampling * (2 * Math.PI))
                                                                        / width))
                                                                + centerY));
                                        samplingStep += GraffitiGraph
                                                .getSamplingPointStep(Math
                                                        .abs(xStart - xEnd), i);
                                    }

                                    lastEdgeSector = false;
                                }

                                // collect the node an edge for deleting
                                edgesToDelete.add(tempEdge);
                                nodesToDelete.add(target);

                                // focus the next node
                                target = targetOfTarget;
                            }

                            // add a new edge, the long span edge to the graph
                            newEdge = this.graph.addEdge(node, target, true);

                            // add bends
                            int bendIndex = 0;

                            for (int k = 0; k < bends.size(); k++) {
                                CoordinateAttribute ca = new CoordinateAttribute(
                                        BKConst.getBEND_I() + bendIndex, bends
                                                .get(k), bends.get(k + 1));
                                newEdge.addAttribute(ca, BKConst
                                        .getPATH_BENDS());
                                k++;
                                bendIndex++;
                            }

                            // give the edge a smoothline shape
                            newEdge.setString(BKConst.getPATH_SHAPE(), BKConst
                                    .getPATH_SHAPE_SMOOTH());

                            // delete edges
                            for (int k = 0; k < edgesToDelete.size(); k++) {
                                this.graph.deleteEdge(edgesToDelete.get(k));
                            }

                            // delete nodes
                            for (int k = 0; k < nodesToDelete.size(); k++) {
                                this.graph.deleteNode(nodesToDelete.get(k));
                            }
                        }

                        // the target is not a dummy node or the long span edges
                        // should not be computes,
                        // so simple transform the edge
                        else {
                            // the X- and Y-Coordinates of the nodes
                            double xStart = coordinates.get(i, j, 0);
                            double xEnd = coordinates.get(
                                    GraffitiGraph.getNodeLevel(target),
                                    GraffitiGraph.getNodeOrder(target), 0)
                                    .doubleValue();
                            double yStart = ((i + placeCentre) * (BKConst
                                    .getRADIAL_LEVEL_DIST() + scaleAddOn));
                            double yEnd = ((i + 1 + placeCentre) * (BKConst
                                    .getRADIAL_LEVEL_DIST() + scaleAddOn));

                            // Cut-Edge treatment
                            if (GraffitiGraph.getEdgeIsCutEdge(edge)) {
                                if (!GraffitiGraph.getNodeIsDummy(edge
                                        .getSource())
                                        && !GraffitiGraph.getNodeIsDummy(edge
                                                .getTarget())) {
                                    if (xStart > xEnd) {
                                        xEnd += width;
                                    } else {
                                        xStart += width;
                                    }
                                }
                            }

                            double samplingStep = GraffitiGraph
                                    .getSamplingPointStep(Math.abs(xStart
                                            - xEnd), i);

                            // Dont calculate sampling points for a spiral
                            // segment form for the node on level 0, if there is
                            // only one.
                            if ((i != 0) || (level.elementsOfLine(0) > 1)) {
                                // generate sampling points
                                while ((samplingStep < 1.0) && (xStart != xEnd)) {
                                    double xSampling = xStart
                                            + (samplingStep * (xEnd - xStart));
                                    double ySampling = yStart
                                            + (samplingStep * (yEnd - yStart));

                                    // collect the bends
                                    // the X-Coordinate of the bend
                                    bends.add(new Double((ySampling * Math
                                            .cos((xSampling * (2 * Math.PI))
                                                    / width))
                                            + centerX));

                                    // the Y-Coordinate of the bend
                                    bends.add(new Double((ySampling * Math
                                            .sin((xSampling * (2 * Math.PI))
                                                    / width))
                                            + centerY));
                                    samplingStep += GraffitiGraph
                                            .getSamplingPointStep(Math
                                                    .abs(xStart - xEnd), i);
                                }

                                // add bends to the edge
                                int bendIndex = 0;

                                for (int k = 0; k < bends.size(); k++) {
                                    CoordinateAttribute ca = new CoordinateAttribute(
                                            BKConst.getBEND_I() + bendIndex,
                                            bends.get(k), bends.get(k + 1));
                                    edge.addAttribute(ca, BKConst
                                            .getPATH_BENDS());
                                    k++;
                                    bendIndex++;
                                }

                                // give the edge a smoothline shape
                                edge.setString(BKConst.getPATH_SHAPE(), BKConst
                                        .getPATH_SHAPE_SMOOTH());
                            }
                        }
                    }
                }
            }
        }

        // Update the nodes and edges collection
        nodes = this.graph.getNodes();
        edges = this.graph.getEdges();
    }

    /**
     * This function draws a single step of the brandes/koepf algorithm
     * 
     * @param step
     *            The step 0=the first, 3=the last
     */
    private void drawStep(int step) {
        // logger.log(Level.OFF,"The graph will be drawn with in his stage
        // "+step);
        if ((BKConst.getDRAW() == 0) || (BKConst.getDRAW() == 2)) {
            double y = BKConst.getTOP_DIST();

            for (int i = 0; i < level.lines(); i++) {
                for (int j = 0; j < level.elementsOfLine(i); j++) {
                    Node tempNode = level.get(i, j);

                    GraffitiGraph.setNodeCoordY(tempNode, y);
                    GraffitiGraph.setNodeCoordX(tempNode, coordinates.get(i, j,
                            BKConst.getDRAW()));
                }

                y += BKConst.getLEVEL_DIST();
            }
        } else {
            // the width of the draft
            double maxValue = 0;

            // find the maximum width
            for (int i = 0; i < level.lines(); i++) {
                if (coordinates.get(i, 0, BKConst.getDRAW()) > maxValue) {
                    maxValue = coordinates.get(i, 0, BKConst.getDRAW());
                }
            }

            // turn the r->l drafts around
            for (int i = 0; i < level.lines(); i++) {
                for (int j = 0; j < level.elementsOfLine(i); j++) {
                    // turn around
                    coordinates.set(i, j, BKConst.getDRAW(), new Double(
                            coordinates.get(i, j, BKConst.getDRAW()) * (-1)));
                }
            }

            // shift the graph to positive coordinates
            for (int i = 0; i < level.lines(); i++) {
                for (int j = 0; j < level.elementsOfLine(i); j++) {
                    coordinates.set(i, j, BKConst.getDRAW(), coordinates.get(i,
                            j, BKConst.getDRAW())
                            + maxValue + BKConst.getLEFT_DIST());
                }
            }

            double y = BKConst.getTOP_DIST();

            for (int i = 0; i < level.lines(); i++) {
                for (int j = 0; j < level.elementsOfLine(i); j++) {
                    Node tempNode = level.get(i, j);

                    GraffitiGraph.setNodeCoordY(tempNode, y);
                    GraffitiGraph.setNodeCoordX(tempNode, coordinates.get(i, j,
                            BKConst.getDRAW()).doubleValue());
                }

                y += BKConst.getLEVEL_DIST();
            }
        }
    }

    /**
     * This function realises the horizontal compaction of the graph
     * 
     * @param dir
     *            The direction of the run
     */
    private void horizontalCompaction(int dir) {
        // ===============================================================
        // Horizontal compaction --- BEGIN
        // ===============================================================
        // assign Integers to the shift array
        for (int i = 0; i < shift.lines(); i++) {
            for (int j = 0; j < shift.elementsOfLine(i); j++) {
                shift.set(i, j, BKConst.getSHIFT_INITIAL());
            }
        }

        // initialize x-coordinates of the nodes
        for (int i = 0; i < level.lines(); i++) {
            for (int j = 0; j < level.elementsOfLine(i); j++) {
                coordinates.set(i, j, dir, BKConst.getCOORD_INITIAL());
            }
        }

        // go to every node and evaluate the shift
        for (int i = direction.verticalDirectionInitial(dir, level); direction
                .verticalDirectionCompare(dir, level, i); i = direction
                .verticalDirectionGoThrough(dir, i)) {
            for (int j = direction.horizontalDirectionInitial(dir, level, i); direction
                    .horizontalDirectionCompare(dir, level, j, i); j = direction
                    .horizontalDirectionGoThrough(dir, j)) {
                if (level.get(i, j) == root.get(i, j)) {
                    place_block(level.get(i, j), dir);
                }
            }
        }

        // sum the shifts
        for (int i = direction.verticalDirectionInitial(dir, level); direction
                .verticalDirectionCompare(dir, level, i); i = direction
                .verticalDirectionGoThrough(dir, i)) {
            /*
             * for(int j = direction.horizontalDirectionInitial(dir,level,i);
             * direction.horizontalDirectionCompare(dir,level,j,i);
             * j=direction.horizontalDirectionGoThrough(dir,j))
             */

            // r->l rotates the graph, so sum up the shifts l->r
            for (int j = 0; j < level.elementsOfLine(i); j++) {
                Node v = level.get(i, j);
                Node tempRoot = root.get(i, j);
                Node tempSinkOfRoot = sink.get(GraffitiGraph
                        .getNodeLevel(tempRoot), GraffitiGraph
                        .getNodeOrder(tempRoot));

                coordinates.set(i, j, dir, coordinates.get(GraffitiGraph
                        .getNodeLevel(tempRoot), GraffitiGraph
                        .getNodeOrder(tempRoot), dir));

                // && (tempRoot!=tempSinkOfRoot || v==tempSinkOfRoot), is it
                // neccessary, did not seem so??
                if (GraffitiGraph.getNodeIsShiftSet(tempSinkOfRoot)
                        && (v == tempRoot)) {
                    coordinates
                            .set(
                                    i,
                                    j,
                                    dir,
                                    coordinates.get(GraffitiGraph
                                            .getNodeLevel(v), GraffitiGraph
                                            .getNodeOrder(v), dir)
                                            + (shift
                                                    .get(
                                                            GraffitiGraph
                                                                    .getNodeLevel(tempSinkOfRoot),
                                                            GraffitiGraph
                                                                    .getNodeOrder(tempSinkOfRoot))));
                }
            }
        }

        // ===============================================================
        // Horizontal compaction --- END
        // ===============================================================
    }

    /**
     * This function initialises all neccessary data-structures, attributes etc.
     */
    private void initialise() {
        direction = new BKDirectionHandler();

        nodes = this.graph.getNodes();
        edges = this.graph.getEdges();

        System.out.println("Graphgr��e: " + nodes.size() + " Knoten, "
                + edges.size() + " Kanten. Gesamt: N="
                + (edges.size() + nodes.size()));

        // logger.log(Level.OFF,"Nodes imported");
        // Number of levels
        levelNum = GraffitiGraph.countLevel(nodes);

        // Create the skeleton for my data structur
        level = new Matrix2Dim<Node>(levelNum);
        root = new Matrix2Dim<Node>(levelNum);
        shift = new Matrix2Dim<Double>(levelNum);
        sink = new Matrix2Dim<Node>(levelNum);
        align = new Matrix2Dim<Node>(levelNum);
        upperNeighbours = new Matrix3Dim<Node>(levelNum);
        lowerNeighbours = new Matrix3Dim<Node>(levelNum);
        edgesToUpperNeighbours = new Matrix3Dim<Edge>(levelNum);
        edgesToLowerNeighbours = new Matrix3Dim<Edge>(levelNum);

        // Fill the data structures and add necessary attributes
        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            Node tempNode = it.next();

            tempNode.setInteger(BKConst.getPATH_SHIFTSET_GET(), 0);
            tempNode.setBoolean(BKConst.getPATH_COORD_INITIAL(), true);

            level.set(GraffitiGraph.getNodeLevel(tempNode), GraffitiGraph
                    .getNodeOrder(tempNode), tempNode);
        }

        // Fill the data structures for the alignment
        setNeighbours();

        // Add the attribute "marked" and "innerSegment" to the edges for later
        // steps
        for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
            Edge tempEdge = it.next();

            // tempEdge.addInteger(BKConst.getPATH_SEPERATOR(),
            // BKConst.getPATH_EDGEMARKED_GET(), 0);
            tempEdge.setInteger(BKConst.getPATH_EDGEMARKED_GET(), 0);

            // Is an inner segment a cut egde, delete the information
            /*
             * if(GraffitiGraph.getEdgeIsCutEdge(tempEdge)) {
             * if(GraffitiGraph.getNodeIsDummy(tempEdge.getSource()) &&
             * GraffitiGraph.getNodeIsDummy(tempEdge.getTarget())) {
             * GraffitiGraph.setEdgeIsCutEdge(tempEdge, false); } }
             */

        }

        root.cloneMatrix(level);
        align.cloneMatrix(level);
        sink.cloneMatrix(level);
        shift.assignMatrixSize(level);

        coordinates = new DoubleMatrix3Dim(levelNum);
    }

    /**
     * This function marks the typ1 conflicts
     */
    private void markConflicts() {
        // Go through all levels
        for (int i = 1; i < (levelNum - 1); i++) {
            int k = -1;
            int l = 0;

            // Look at every node one level deeper
            for (int l1 = 0; l1 < level.elementsOfLine(i + 1); l1++) {
                Node tempNode = level.get(i + 1, l1);

                if ((l1 == (level.elementsOfLine(i + 1) - 1))
                        || (GraffitiGraph.getNodeIsDummy(tempNode) && GraffitiGraph
                                .getNodeIsDummy(upperNeighbours.get(i + 1, l1,
                                        0)))) {
                    // number of nodes in level i
                    int k1 = level.elementsOfLine(i);

                    // if tempNode is a dummy node, and the incident edge is an
                    // inner segment store the order number of the upper dummy
                    // node
                    if (GraffitiGraph.getNodeIsDummy(tempNode)) {
                        if (GraffitiGraph.getNodeIsDummy(upperNeighbours.get(
                                i + 1, l1, 0))) {
                            Collection<Node> myUpperNeighbours = tempNode
                                    .getAllInNeighbors();
                            Iterator<Node> it = myUpperNeighbours.iterator();

                            // order number of the upper neighbour of the dummy
                            // node tempNode, he has certainly only one
                            k1 = GraffitiGraph.getNodeOrder(it.next());
                        }
                    }

                    // check every node left hand of the tempNode, has he a
                    // upper neighbour right hand of the dummy node in level i,
                    // upper neighbour of tempNode
                    while (l < l1) {
                        Node vl = level.get(i + 1, l);
                        Collection<Edge> inEdges = vl.getAllInEdges();

                        for (Iterator<Edge> it = inEdges.iterator(); it
                                .hasNext();) {
                            Edge tempEdge = it.next();
                            Node tempNode2 = tempEdge.getSource();

                            // has the node left hand of the tempNode a upper
                            // neighbour left hand of the last inner segment ->
                            // typ1
                            // or a upper neighbour right hand of the upper
                            // neighbour of tempNode -> typ1
                            if ((GraffitiGraph.getNodeOrder(tempNode2) < k)
                                    || (GraffitiGraph.getNodeOrder(tempNode2) > k1)) {
                                GraffitiGraph.setEdgeIsMarked(tempEdge, true);
                            }
                        }

                        l++;
                    }

                    // store the order number of the
                    k = k1;
                }
            }
        }
    }

    /**
     * Computes the shifts of the nodes
     * 
     * @param v
     *            The node
     * @param dir
     *            The direction of the run
     */
    private void place_block(Node v, int dir) {
        // logger.log(Level.OFF,"place_block("+GraffitiGraph.getNodeLevel(v)+"_"+GraffitiGraph.getNodeOrder(v)+")");
        if (GraffitiGraph.getNodeCoordInitial(v)) {
            GraffitiGraph.setNodeCoordInitial(v, false);
            coordinates.set(GraffitiGraph.getNodeLevel(v), GraffitiGraph
                    .getNodeOrder(v), dir, new Double(BKConst.getLEFT_DIST()));

            // logger.log(Level.OFF,"X-Koordinate vom Knoten
            // "+GraffitiGraph.getNodeLevel(v)+"_"+GraffitiGraph.getNodeOrder(v)+"
            // auf 20 gesetzt.");
            Node w = v;

            do {
                // logger.log(Level.OFF,"Schleife l�uft:
                // v("+GraffitiGraph.getNodeLevel(v)+"_"+GraffitiGraph.getNodeOrder(v)+")
                // !=w("+GraffitiGraph.getNodeLevel(w)+"_"+GraffitiGraph.getNodeOrder(w)+")");
                // do only for nodes, who are not candidates for a sink
                if (!direction.isSinkCandidate(dir, level, GraffitiGraph
                        .getNodeOrder(w), GraffitiGraph.getNodeLevel(w))) {
                    // get the neighbour, the left or right, depending on the
                    // direction
                    Node tempPredNode = direction.getNeighbourHorizontal(dir,
                            level, w);

                    // logger.log(Level.OFF,"Vorg�nger vom Knoten
                    // v="+GraffitiGraph.getNodeLevel(v)+"_"+GraffitiGraph.getNodeOrder(v)+"
                    // ist
                    // tempPredNode="+GraffitiGraph.getNodeLevel(tempPredNode)+"_"+GraffitiGraph.getNodeOrder(tempPredNode));
                    // get the root node of the neighbour
                    Node u = root.get(GraffitiGraph.getNodeLevel(tempPredNode),
                            GraffitiGraph.getNodeOrder(tempPredNode));

                    // logger.log(Level.OFF,"Die Wurzel vom Vorg�nger
                    // (tempPredNode) ist
                    // u="+GraffitiGraph.getNodeLevel(u)+"_"+GraffitiGraph.getNodeOrder(u));
                    place_block(u, dir);

                    // do, if v is his own sink
                    if (sink.get(GraffitiGraph.getNodeLevel(v), GraffitiGraph
                            .getNodeOrder(v)) == v) {
                        // logger.log(Level.OFF,"Die Senke von
                        // v="+GraffitiGraph.getNodeLevel(v)+"_"+GraffitiGraph.getNodeOrder(v)+"
                        // ist der Knoten selbst");
                        sink.set(GraffitiGraph.getNodeLevel(v), GraffitiGraph
                                .getNodeOrder(v), sink
                                .get(GraffitiGraph.getNodeLevel(u),
                                        GraffitiGraph.getNodeOrder(u)));
                    }

                    // do, if the sink of v is unequal the sink of u
                    if (sink.get(GraffitiGraph.getNodeLevel(v), GraffitiGraph
                            .getNodeOrder(v)) != sink.get(GraffitiGraph
                            .getNodeLevel(u), GraffitiGraph.getNodeOrder(u))) {
                        // get the sink of u
                        Node tempSinkOfU = sink
                                .get(GraffitiGraph.getNodeLevel(u),
                                        GraffitiGraph.getNodeOrder(u));

                        // Set the shift for the sink of u
                        double tempShift = direction.minMax(dir, shift.get(
                                GraffitiGraph.getNodeLevel(tempSinkOfU),
                                GraffitiGraph.getNodeOrder(tempSinkOfU)),
                                coordinates.get(GraffitiGraph.getNodeLevel(v),
                                        GraffitiGraph.getNodeOrder(v), dir)
                                        - coordinates.get(GraffitiGraph
                                                .getNodeLevel(u), GraffitiGraph
                                                .getNodeOrder(u), dir)
                                        - BKConst.getMINDIST());
                        shift.set(GraffitiGraph.getNodeLevel(tempSinkOfU),
                                GraffitiGraph.getNodeOrder(tempSinkOfU),
                                tempShift);

                        GraffitiGraph.setNodeIsShiftSet(tempSinkOfU, true);
                    } else {
                        coordinates.set(GraffitiGraph.getNodeLevel(v),
                                GraffitiGraph.getNodeOrder(v), dir, direction
                                        .maxMin(dir, coordinates.get(
                                                GraffitiGraph.getNodeLevel(v),
                                                GraffitiGraph.getNodeOrder(v),
                                                dir), coordinates.get(
                                                GraffitiGraph.getNodeLevel(u),
                                                GraffitiGraph.getNodeOrder(u),
                                                dir)
                                                + BKConst.getMINDIST()));
                    }
                }

                w = align.get(GraffitiGraph.getNodeLevel(w), GraffitiGraph
                        .getNodeOrder(w));
            } while (w != v);
        }
    }

    /**
     * This function realises the vertical alignment
     * 
     * @param dir
     *            The direction of the run
     */
    private void verticalAlignment(int dir) {
        // ===============================================================
        // Vertical alignment --- BEGIN
        // ===============================================================
        // Go through every line
        for (int i = direction.verticalDirectionInitial(dir, level); direction
                .verticalDirectionCompare(dir, level, i); i = direction
                .verticalDirectionGoThrough(dir, i)) {
            // the default initialisation of the barrier variable
            int r = 0;

            // no alignment across this variable, though that no alignments can
            // cross
            if (direction.compareForInitialiseBarrierVariable(dir, level, i)) {
                r = direction.initialiseBarrierVariable(dir, level, i);
            }

            // Look at every node in a line
            for (int k = direction.horizontalDirectionInitial(dir, level, i); direction
                    .horizontalDirectionCompare(dir, level, k, i); k = direction
                    .horizontalDirectionGoThrough(dir, k)) {
                int numberOfNeighbours = direction.getNumberOfNeighbours(dir,
                        upperNeighbours, lowerNeighbours, i, k);

                if (numberOfNeighbours > 0) {
                    // numberOfNeighbours is even, we have to look at both
                    // medians
                    if ((numberOfNeighbours % 2) == 0) {
                        // look at the left and right median
                        for (int m = direction
                                .horizontalDirectionNeigboursInitial(dir,
                                        numberOfNeighbours); direction
                                .horizontalDirectionNeigboursCompare(dir,
                                        numberOfNeighbours, m); m = direction
                                .horizontalDirectionNeigboursGoThrough(dir, m)) {
                            // the median
                            Node tempU = direction.getNeighbourVertical(dir,
                                    upperNeighbours, lowerNeighbours, i, k, m);

                            // if the alignment is already not set
                            if (level.get(i, k) == align.get(i, k)) {
                                // dont look at marked edges and the last
                                // alignment has to be left hand, in the
                                // direction r->l
                                if (!direction.isEdgeMarked(dir,
                                        edgesToUpperNeighbours,
                                        edgesToLowerNeighbours, i, k, m)
                                        && (direction
                                                .compareBarrierVariable(
                                                        dir,
                                                        GraffitiGraph
                                                                .getNodeOrder(tempU),
                                                        r))) {
                                    // create the cyclical align and root lists
                                    align.set(
                                            GraffitiGraph.getNodeLevel(tempU),
                                            GraffitiGraph.getNodeOrder(tempU),
                                            level.get(i, k));
                                    root.set(i, k, root.get(GraffitiGraph
                                            .getNodeLevel(tempU), GraffitiGraph
                                            .getNodeOrder(tempU)));
                                    align.set(i, k, root.get(GraffitiGraph
                                            .getNodeLevel(tempU), GraffitiGraph
                                            .getNodeOrder(tempU)));

                                    // set the barrier variable to the order
                                    // number of the actual aligned node
                                    r = direction.setBarrierVariable(dir,
                                            GraffitiGraph.getNodeOrder(tempU));
                                }
                            }
                        }
                    }

                    // numberOfNeighbours is not even
                    else {
                        int m = (numberOfNeighbours - 1) / 2;
                        Node tempU = direction.getNeighbourVertical(dir,
                                upperNeighbours, lowerNeighbours, i, k, m);

                        if (level.get(i, k) == align.get(i, k)) {
                            if (!direction.isEdgeMarked(dir,
                                    edgesToUpperNeighbours,
                                    edgesToLowerNeighbours, i, k, m)
                                    && (direction.compareBarrierVariable(dir,
                                            GraffitiGraph.getNodeOrder(tempU),
                                            r))) {
                                align.set(GraffitiGraph.getNodeLevel(tempU),
                                        GraffitiGraph.getNodeOrder(tempU),
                                        level.get(i, k));
                                root.set(i, k, root.get(GraffitiGraph
                                        .getNodeLevel(tempU), GraffitiGraph
                                        .getNodeOrder(tempU)));
                                align.set(i, k, root.get(GraffitiGraph
                                        .getNodeLevel(tempU), GraffitiGraph
                                        .getNodeOrder(tempU)));

                                r = direction.setBarrierVariable(dir,
                                        GraffitiGraph.getNodeOrder(tempU));
                            }
                        }
                    }
                }
            }
        }

        // ===============================================================
        // Vertical alignment --- END
        // ===============================================================
    }

    /**
     * This method sets the boolean "deleteGraphElements" to false. This is
     * needed for algorithms that need their original edges in the graph, as
     * this implementation replaces edges connected to dummy-nodes with new
     * edges.
     * 
     * @author Ferdinand H�bner
     */
    public void dontDeleteGraphElements() {
        deleteGraphElements = false;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
