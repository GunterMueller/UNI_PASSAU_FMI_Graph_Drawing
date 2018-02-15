// =============================================================================
//
//   IncrementalSugiyamaAnimation.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SugiyamaAnimation.java 2147 2007-11-19 23:13:16Z brunner $

package org.graffiti.plugins.algorithms.sugiyama.incremental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.ObjectAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAnimation;
import org.graffiti.plugins.algorithms.sugiyama.decycling.DecyclingAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.BigNode;
import org.graffiti.plugins.algorithms.sugiyama.util.CoordinatesUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.DummyNodeUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.EdgeUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.EvaluationUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.IncrementalSugiyamaData;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class handles the execution of the phase algorithms of the sugiyama
 * algorithm and adds the handling of incremental changes to the graph
 * afterwards.<br>
 * <br>
 * 
 * If animation of the phase-algorithm is turned on, each algorithm is executed
 * in one step and if the algorithm supports animation itself a "sub-animation"
 * will be executed.<br>
 * <br>
 * 
 * Having dealt with the four phases this class creates data structures to
 * support incremental changes and handles the changes every time
 * <tt>nextStep()</tt> is called.
 * 
 * @author Christian Brunnermeier
 */
public class IncrementalSugiyamaAnimation extends SugiyamaAnimation {

    /* indicated whether the first 4 phases should be animated as well */
    private boolean animated;

    /* the <tt>GridStructure</tt> containing all nodes for the incremental phase */
    private GridStructure grid = new GridStructure();

    /*
     * indicates if there have been relevant changes to the graph since the last
     * processing.
     */
    private boolean graphHasChanged = true;

    /* Data structures to store changes which still have to be processed. */
    private ArrayList<Node> newNodes = new ArrayList<Node>();
    private ArrayList<Node> delNodes = new ArrayList<Node>();
    private ArrayList<Edge> newEdges = new ArrayList<Edge>();
    private ArrayList<Edge> delEdges = new ArrayList<Edge>();

    /*
     * stores which column have to be checked if there are edges to shift from
     * them
     */
    private HashSet<Plane> colsToCheck = new HashSet<Plane>();

    /*
     * The maximal difference of crossings of a column and of the space for a
     * new column so that the column will still be preferred.
     */
    static final int MAX_DIFF_FOR_CROSSINGS = 2;

    /*
     * The maximal number of columns a new node is move to the left or right to
     * be inserted on a free place instead of adding a new column.
     */
    static final int MAX_DIST_FOR_NEW_NODE = 2;

    /* Indicates if the algorithm is in a test mode */
    private final boolean testAlgorithm = false;

    int testing_numberOfNewNodes = 5;
    int testing_numberOfNewEdgesPerNode = 3;

    int testing_numberOfNodesToDelete = 5;

    /*
     * If the algorithm runs in a test mode, it is the following: -1: just
     * output the evaluation 0: testing_numberOfNewNodes steps, per step one new
     * node and testing_numberOfNewEdgesPerNode new edges to this node 1:
     * testing_numberOfNodesToDelete steps, per step delete one node with it's
     * edges
     */
    private static int testMode = 0;

    private static int testStep = -1;

    /**
     * Default constructor for a <tt>IncrementalSugiyamaAnimation</tt>. It calls
     * the constructor of <tt>SugiyamaAnimation</tt>.
     * 
     * @param algorithms
     *            The selected <tt>SugiyamaAlgorithm</tt>s for each phase
     * @param data
     *            The <tt>SugiyamaData</tt>-Bean that stores essential data
     * @param graph
     *            The <tt>Graph</tt> that is used in the phases
     *            <tt>Sugiyama</tt>-algorithm. This is needed to reset
     *            <tt>Sugiyama</tt> after the animation is finished.
     */
    public IncrementalSugiyamaAnimation(SugiyamaAlgorithm[] algorithms,
            SugiyamaData data, Graph graph) {
        super(algorithms, data, graph);
        animated = data.isAnimated();
        if (graphModificationPolicy == null) {
            graphModificationPolicy = getGraphModificationPolicy();
        }
    }

    /**
     * Executes the next of the four phases of the sugiyama algorithm or handles
     * all changes that occurred since the last step if the algorithm is in the
     * incremental part.
     */
    @Override
    public void nextStep() {
        IncrementalSugiyama.debug("CurrentPhase: " + currentPhase);

        // Don't allow the 'next' button to have any effect as long as this
        // method is running.
        ready = false;

        // changes to the graph in this method should not be noticed by the
        // GraphModificationPolicy.
        graphModificationPolicy.disable();

        if (currentPhase < 4) { // non-incremental phase

            try {
                GraffitiSingleton.getInstance().getMainFrame().getStatusBar()
                        .showInfo(
                                "Currently: "
                                        + algorithms[currentPhase].getName());
            } catch (Exception e) {
                // no gui so do nothing
            }

            // Delegate execution of the first phases
            executeFirstFourPhases();

            if (!this.inSubAnimation) {
                currentPhase++;
            }

            if (currentPhase == 4) {
                // clean up after the first four phases are done

                graph.getListenerManager().transactionStarted(this);

                // collect coordinate-attributes in the sugiyama-attribute-tree
                // and
                // set the "real" coordinates in the graphics-tree
                CoordinatesUtil.updateRealCoordinates(graph);

                // remove all big nodes
                Iterator<BigNode> bNodes = data.getBigNodes().iterator();
                while (bNodes.hasNext()) {
                    bNodes.next().removeDummyElements();
                }

                // remove all dummies that had been inserted into the graph
                DummyNodeUtil.removeDummies(data, graph);

                // reverse or undelete edges that would have created cycles in
                // the graph
                ((DecyclingAlgorithm) algorithms[0]).undo();

                // insert the self loops again
                EdgeUtil.insertSelfLoops(data);

                // initialize the data structure for the incremental part
                initIncremental();

                // update the self loops according to the coordinates of the
                // incremental part
                EdgeUtil.updateSelfLoops(data);

                EdgeUtil.addPorts(data);

                // the graph has to be changed before 'next' can be pressed
                // again
                graphHasChanged = false;

                graph.getListenerManager().transactionFinished(this);
                currentPhase++;

                if (testAlgorithm) {
                    // testStep = -1;
                    testIncrementalAlgorithm();
                }

            }

        } else { // incremental part of the algorithm

            /*  ****************** EDGES TO DELETE **************** */
            if (delEdges.size() > 0) {
                for (Edge edge : delEdges) {
                    // if edge is a self loop remove it from data.selfLoops
                    if (edge.getSource() == edge.getTarget()) {
                        // edge is a self loop
                        data.getSelfLoops().remove(edge);
                    }

                    SugiyamaEdge delEdge = null;

                    try {
                        delEdge = (SugiyamaEdge) ((ObjectAttribute) edge
                                .getAttribute(SugiyamaConstants.PATH_INC_EDGE))
                                .getValue();

                        Plane columnOfDummyNodes = delEdge.removeDummies();
                        if (columnOfDummyNodes != null) {
                            columnOfDummyNodes.checkForDeletion();
                        }

                        /*
                         * As the edge is removed there are new free places on
                         * this column so edges on the adjacent columns have to
                         * be checked if it wouldn't be possible and better to
                         * shift them to this column.
                         */
                        if (columnOfDummyNodes != null
                                && columnOfDummyNodes.getPrev() != null) {
                            debug("Column "
                                    + columnOfDummyNodes.getPrev().getNumber()
                                    + " added to colsToCheck");
                            colsToCheck.add(columnOfDummyNodes.getPrev());
                        }
                        if (columnOfDummyNodes != null
                                && columnOfDummyNodes.getNext() != null) {
                            debug("Column "
                                    + columnOfDummyNodes.getNext().getNumber()
                                    + " added to colsToCheck");
                            colsToCheck.add(columnOfDummyNodes.getNext());
                        }

                        // delete edge from the edge lists of it's start and end
                        // node
                        delEdge.getNodes().getFirst().getEdgesToHigherLevel()
                                .remove(delEdge);
                        delEdge.getNodes().getLast().getEdgesToLowerLevel()
                                .remove(delEdge);

                    } catch (AttributeNotFoundException e) {
                        // no attached SugiyamaEdge can be found
                        System.err.println("The edge " + edge
                                + " can't be removed" + " as it's attribute '"
                                + SugiyamaConstants.PATH_INC_EDGE
                                + "' can't be found!");
                    }

                }
                delEdges.clear();
            }

            /*  ****************** NODES TO DELETE **************** */
            if (delNodes.size() > 0) {
                for (Node n : delNodes) {
                    SugiyamaNode delNode = null;
                    try {
                        delNode = (SugiyamaNode) ((ObjectAttribute) n
                                .getAttribute(SugiyamaConstants.PATH_INC_NODE))
                                .getValue();
                        Plane level = delNode.getLevel();
                        level.deleteNode(delNode);
                        level.checkForDeletion();

                        Plane column = delNode.getColumn();
                        column.deleteNode(delNode);
                        column.checkForDeletion();

                        /*
                         * As the node is removed there is a new free place on
                         * this column so edges on the adjacent columns have to
                         * be checked if it wouldn't be possible and better to
                         * shift them to this column.
                         */
                        if (column.getPrev() != null) {
                            colsToCheck.add(column.getPrev());
                        }
                        if (column.getNext() != null) {
                            colsToCheck.add(column.getNext());
                        }
                    } catch (AttributeNotFoundException e) {
                        // no attached SugiyamaNode can be found
                        System.err.println("The node " + n
                                + " can't be removed" + " as it's attribute '"
                                + SugiyamaConstants.PATH_INC_NODE
                                + "' can't be found!");
                    }
                }
                delNodes.clear();
            }

            /*  ************* NEW NODES ************************** */
            addNodes();

            /*  ***************** NEW EDGES ******************* */
            addEdges();

            grid.checkForCombinableColumns();
            checkEdgeShifting();
            updateCoordinates();
            EdgeUtil.updateSelfLoops(data);
            EdgeUtil.addPorts(data);
            graphHasChanged = false;

            currentPhase++;

            if (testAlgorithm) {
                testIncrementalAlgorithm();
            }

        }

        if (currentPhase >= 4) {
            graphModificationPolicy.enable();
        }
        ready = true;
        if (currentPhase < 4 && !animated) {
            nextStep();
        }
    }

    /* ######### Utility classes for adding new nodes and edges ########### */

    /*
     * Adds new nodes to the graph trying to find a free place next to the place
     * the node was added. If no free place in the close surrounding is found a
     * new column is inserted and the new node is placed on it.
     */
    private void addNodes() {
        if (newNodes.size() > 0) {
            for (Node n : newNodes) {
                SugiyamaNode newNode = new SugiyamaNode(n);
                CoordinateAttribute co = ((NodeGraphicAttribute) n
                        .getAttribute("graphics")).getCoordinate();
                int closestLevelNumber = grid.getClosest(GridStructure.LEVEL,
                        co.getY());
                Plane closestLevel = null;

                if (closestLevelNumber == -1
                        || closestLevelNumber == grid
                                .getSize(GridStructure.LEVEL)) {
                    /*
                     * The closest level is lesser then the first or higher then
                     * the last level, therefore we create a new level and
                     * insert the node there.
                     */
                    Plane newLevel = new Plane(grid, GridStructure.LEVEL);
                    newLevel.addNode(newNode);
                    if (closestLevelNumber == -1) {
                        grid.add(newLevel, 0);
                    } else {
                        grid.add(newLevel, grid.getSize(GridStructure.LEVEL));
                    }

                    if (grid.getSize(GridStructure.COLUMN) == 0) {
                        // there is no column, yet, so create a new one and
                        // insert the node
                        Plane newColumn = new Plane(grid, GridStructure.COLUMN);
                        newColumn.addNode(newNode);
                        grid.add(newColumn, 0);
                    } else {
                        // Get the closest already existing column
                        int closestColumnNumber = grid.getClosest(
                                GridStructure.COLUMN, co.getX());
                        if (closestColumnNumber == grid
                                .getSize(GridStructure.COLUMN)) {
                            closestColumnNumber--;
                        } else if (closestColumnNumber == -1) {
                            closestColumnNumber++;
                        }
                        Plane closestPlane = grid.getPlane(
                                GridStructure.COLUMN, closestColumnNumber);
                        closestPlane.addNode(newNode);
                    }
                } else {
                    closestLevel = grid.getPlane(GridStructure.LEVEL,
                            closestLevelNumber);
                    boolean[] levelOccupancy = closestLevel.getOccupancy();

                    // Get the closest already existing column
                    int colNumb = grid.getClosest(GridStructure.COLUMN, co
                            .getX());
                    if (colNumb == grid.getSize(GridStructure.COLUMN)) {
                        colNumb--;
                    } else if (colNumb == -1) {
                        colNumb++;
                    }
                    Plane closestColumn = grid.getPlane(GridStructure.COLUMN,
                            colNumb);

                    assert colNumb < levelOccupancy.length;

                    Plane newColumn = null;

                    for (int tested = 0; tested < Math.min(
                            levelOccupancy.length,
                            MAX_DIST_FOR_NEW_NODE * 2 + 1); tested++) {
                        int diff = (tested % 2 == 0) ? tested : -tested;
                        colNumb += diff;
                        if (colNumb >= 0 && colNumb < levelOccupancy.length
                                && levelOccupancy[colNumb] == false) {
                            // there is a column with number colNumb
                            // and it has room left
                            newColumn = grid.getPlane(GridStructure.COLUMN,
                                    colNumb);
                            break;
                        }
                    }

                    if (newColumn == null) {
                        // no room left so construct a new Plane as close to
                        // the y coordinate of the inserted node as possible.
                        newColumn = new Plane(grid, GridStructure.COLUMN);
                        int newPosition = closestColumn.getNumber();
                        if (grid.getPlane(GridStructure.COLUMN, newPosition)
                                .getCoordinate() < co.getX()) {
                            // the new node was placed to the right side of
                            // the closest column so the new column should
                            // be inserted right of this closest column as well
                            newPosition++;
                        }
                        IncrementalSugiyama.debug("new Position: "
                                + newPosition);

                        grid.add(newColumn, newPosition);
                    }

                    closestLevel.addNode(newNode);
                    newColumn.addNode(newNode);
                }
            }
            newNodes.clear();
        }
    }

    /*
     * Adds new edges to the data structure. If a new edge spans over more then
     * one layer, all dummy nodes on levels between the source and the target
     * node have to be in the same column. Therefore a column or a space next to
     * a column is looked for where inserting this edge would inflict as less as
     * possible crossings with existing edges.
     */
    private void addEdges() {
        LinkedList<SugiyamaEdge> newEdgesOnSameLevel = new LinkedList<SugiyamaEdge>();

        if (newEdges.size() > 0) {
            for (Edge edge : newEdges) {
                SugiyamaEdge newEdge = new SugiyamaEdge(edge);

                SugiyamaNode n1 = (SugiyamaNode) edge.getSource().getAttribute(
                        SugiyamaConstants.PATH_INC_NODE).getValue();
                SugiyamaNode n2 = (SugiyamaNode) edge.getTarget().getAttribute(
                        SugiyamaConstants.PATH_INC_NODE).getValue();

                int layer1 = n1.getLevelNumber();
                int layer2 = n2.getLevelNumber();

                if (layer1 == layer2) {
                    // check if the edge is a self loop
                    if (n1 == n2) {
                        data.getSelfLoops().add(edge);
                    } else {
                        // both nodes are on the same layer so one has to be
                        // shifted up or down.
                        newEdgesOnSameLevel.add(newEdge);
                    }
                } else {
                    addEdgeBetweenDifferentLevels(n1, n2, layer1, layer2,
                            newEdge);
                }
            }
            newEdges.clear();

            Iterator<SugiyamaEdge> edgeIt = newEdgesOnSameLevel.iterator();
            while (edgeIt.hasNext()) {
                SugiyamaEdge edge = edgeIt.next();
                SugiyamaNode n1 = edge.getNodes().getFirst();
                SugiyamaNode n2 = edge.getNodes().getLast();
                if (n1.getLevel() != n2.getLevel()) {
                    // the two nodes are not on the same level anymore
                    addEdgeBetweenDifferentLevels(n1, n2, n1.getLevelNumber(),
                            n2.getLevelNumber(), edge);
                } else {
                    addEdgeOnSameLevel(edge);
                }
            }
        }
    }

    private void addEdgeBetweenDifferentLevels(SugiyamaNode n1,
            SugiyamaNode n2, int layer1, int layer2, SugiyamaEdge newEdge) {
        if (layer1 > layer2) {
            SugiyamaNode tmp = n1;
            n1 = n2;
            n2 = tmp;
            int tmp2 = layer1;
            layer1 = layer2;
            layer2 = tmp2;
        }
        // now layer1 < layer2

        if (layer2 - layer1 == 1) {
            // The nodes are on layers next to each other so we are
            // done as no dummy nodes have to be inserted.
        } else {
            // all arrays used here are [column][level]
            boolean[][] occupancies = grid.getOccupancies(GridStructure.COLUMN);
            SugiyamaNode[][] nodes = grid.getNodes(GridStructure.COLUMN);
            int[][] moveTo = new int[grid.getSize(GridStructure.COLUMN)][grid
                    .getSize(GridStructure.LEVEL)];

            // check which columns are free to use
            boolean[] columnsFree = checkForFreeColumns(layer1, layer2,
                    occupancies, nodes, moveTo);

            // check how many crossings would occur for each column
            // and each space next to a column
            int[] crossings = calculateCrossingsForNewEdge(n1, n2, layer1,
                    layer2, nodes, moveTo);

            // check which existing column and which space between
            // columns can be used and have minimal crossings
            int middle = (n1.getColumnNumber() + n2.getColumnNumber()) / 2;

            int minCrossingsCols = Integer.MAX_VALUE;
            int bestCol = -1;

            int minCrossingsSpaces = Integer.MAX_VALUE;
            int bestSpace = -1;

            for (int i = 0; i < crossings.length; i++) {
                if (i % 2 == 0) {
                    // this is a space between columns
                    if (crossings[i] < minCrossingsSpaces
                            || (crossings[i] == minCrossingsSpaces && Math
                                    .abs((float) (bestSpace / 2 - 0.5 - middle)) > Math
                                    .abs((float) (i / 2 - 0.5 - middle)))) {
                        // this space is better
                        bestSpace = i;
                        minCrossingsSpaces = crossings[bestSpace];
                    }
                } else {
                    // this is a column
                    if (columnsFree[i / 2]
                            && (crossings[i] < minCrossingsCols || (crossings[i] == minCrossingsCols && Math
                                    .abs((bestCol - 1) / 2 - middle) > Math
                                    .abs((i - 1) / 2 - middle)))) {
                        // this column is better
                        bestCol = i;
                        minCrossingsCols = crossings[i];
                    }
                }
            }

            if (IncrementalSugiyama.DEBUG) {
                System.out.println("Best column is " + bestCol + " with "
                        + minCrossingsCols + " crossings.");
                System.out.println("Best space is " + bestSpace + " with "
                        + minCrossingsSpaces + " crossings.");
            }

            // Decide whether to take an existing column or to
            // construct a new one.
            boolean takeExistingColumn = bestCol != -1
                    && minCrossingsCols - MAX_DIFF_FOR_CROSSINGS <= minCrossingsSpaces;

            IncrementalSugiyama.debug("Taking an existing column is "
                    + takeExistingColumn);

            // insert edge
            if (takeExistingColumn) {
                bestCol = (bestCol - 1) / 2;
                // insert edge in column 'bestCol'
                Plane insertColumn = grid.getPlane(GridStructure.COLUMN,
                        bestCol);
                Plane insertLevel = null;
                for (int j = layer1 + 1; j < layer2; j++) {
                    // make place in 'bestCol' if necessary
                    insertLevel = (insertLevel == null) ? grid.getPlane(
                            GridStructure.LEVEL, j) : insertLevel.getNext();

                    if (occupancies[bestCol][j] == true) {
                        /*
                         * There is a node in this column, so it has to be moved
                         */
                        Plane to = (moveTo[bestCol][j] == -1) ? insertColumn
                                .getPrev() : insertColumn.getNext();
                        SugiyamaNode node = nodes[bestCol][j];
                        insertColumn.deleteNode(node);
                        to.addNode(node);

                        // as node has been moved check if it's edges should be
                        // moved as well
                        node.checkEdgePositions(colsToCheck);
                    }

                    SugiyamaDummyNode newDummy = new SugiyamaDummyNode(newEdge);
                    insertColumn.addNode(newDummy);
                    insertLevel.addNode(newDummy);
                    newEdge.addDummyNodeWithoutBendUpdate(newDummy);
                }
                newEdge.updateBends();
            } else {
                int newColumnPosition = bestSpace / 2;
                Plane newColumn = new Plane(grid, GridStructure.COLUMN);
                grid.add(newColumn, newColumnPosition);

                Plane insertLevel = null;
                for (int j = layer1 + 1; j < layer2; j++) {
                    insertLevel = (insertLevel == null) ? grid.getPlane(
                            GridStructure.LEVEL, j) : insertLevel.getNext();
                    SugiyamaDummyNode newDummy = new SugiyamaDummyNode(newEdge);
                    newColumn.addNode(newDummy);
                    insertLevel.addNode(newDummy);
                    newEdge.addDummyNodeWithoutBendUpdate(newDummy);
                }

                /*
                 * As a new column is inserted the adjacent columns have to be
                 * checked if it wouldn't be possible and better to shift an
                 * edge them to this column.
                 */
                if (newColumn.getPrev() != null) {
                    colsToCheck.add(newColumn.getPrev());
                }
                if (newColumn.getNext() != null) {
                    colsToCheck.add(newColumn.getNext());
                }

                newEdge.updateBends();
            }
        }
    }

    /*
     * Check for each column if there is place on the levels layer1 + 1 to
     * layer2 - 1 or if you can easily make place on these positions. If a node
     * has to be moved, the preferred direction is stored in 'moveTo'.
     */
    private boolean[] checkForFreeColumns(int layer1, int layer2,
            boolean[][] occupancies, SugiyamaNode[][] nodes, int[][] moveTo) {
        boolean[] columnsFree = new boolean[grid.getSize(GridStructure.COLUMN)];

        for (int i = 0; i < grid.getSize(GridStructure.COLUMN); i++) {
            columnsFree[i] = true;
            for (int j = layer1 + 1; j < layer2; j++) {
                if (occupancies[i][j] == true) {
                    /*
                     * There is a node in this column, so check whether it can
                     * be moved
                     */

                    if (!nodes[i][j].isDummy ||
                    // node is a real node
                            ((SugiyamaDummyNode) nodes[i][j]).getEdge()
                                    .getNodes().size() == 3) {
                        // node is a dummy node but the only dummy node of this
                        // edge

                        boolean leftFree = (i > 0 && occupancies[i - 1][j] == false);
                        boolean rightFree = (i < occupancies.length - 1 && occupancies[i + 1][j] == false);
                        if (leftFree && !rightFree) {
                            // only the left column has a free place
                            moveTo[i][j] = -1;
                        } else if (!leftFree && rightFree) {
                            // the right column has a free place
                            moveTo[i][j] = +1;
                        } else if (!leftFree && !rightFree) {
                            // The places next to this one
                            // are occupied, so the node
                            // cannot be moved.
                            columnsFree[i] = false;
                            break;
                        } else if (leftFree && rightFree) {
                            // check which direction would
                            // inflict less crossings
                            int moveDirection = 0;
                            SugiyamaNode actNode = nodes[i][j];

                            if (actNode.isDummy) {
                                int prevColumn = ((SugiyamaDummyNode) actNode)
                                        .getEdge().getPrevNode(actNode)
                                        .getColumnNumber();

                                if (prevColumn != i) {
                                    // move towards the column of the
                                    // predecessor
                                    moveDirection += (prevColumn < i) ? -1 : 1;
                                } else {
                                    // move to the same direction as the
                                    // predecessor
                                    moveDirection += moveTo[prevColumn][j - 1];
                                }

                                int succColumn = ((SugiyamaDummyNode) actNode)
                                        .getEdge().getNextNode(actNode)
                                        .getColumnNumber();
                                if (succColumn != i) {
                                    moveDirection += (succColumn < i) ? -1 : 1;
                                } else {
                                    // ignore following node as it
                                    // has no moveTo direction, yet.
                                }
                            } else {
                                // real node so check all ingoing and outgoing
                                // edges
                                Iterator<Edge> edgeIt = actNode.getNode()
                                        .getEdgesIterator();
                                while (edgeIt.hasNext()) {
                                    Edge nextEdge = edgeIt.next();
                                    try {
                                        SugiyamaEdge nextSugiEdge = (SugiyamaEdge) nextEdge
                                                .getAttribute(
                                                        SugiyamaConstants.PATH_INC_EDGE)
                                                .getValue();
                                        boolean edgeUpwards = nextSugiEdge
                                                .getPrevNode(actNode) != null;
                                        int toColumn = (edgeUpwards) ? nextSugiEdge
                                                .getPrevNode(actNode)
                                                .getColumnNumber()
                                                : nextSugiEdge.getNextNode(
                                                        actNode)
                                                        .getColumnNumber();
                                        if (toColumn != i) {
                                            moveDirection += (toColumn < i) ? -1
                                                    : 1;
                                        } else if (edgeUpwards) {
                                            moveDirection += moveTo[toColumn][j - 1];
                                        }
                                    } catch (AttributeNotFoundException e) {
                                        // edge has not been added, yet, so
                                        // ignore it
                                    }
                                }
                            }
                            moveTo[i][j] = (moveDirection <= 0) ? -1 : 1;
                        }

                    } else {
                        // Node is a dummy node in an edge
                        // spanning over more then 2 layers.
                        // So it cannot be moved easily.
                        columnsFree[i] = false;
                        break;
                    }
                }
            }
        }

        if (IncrementalSugiyama.DEBUG) {
            System.out.println("Free columns: ");
            for (int i = 0; i < columnsFree.length; i++) {
                System.out.print(columnsFree[i] + "\t");
            }
            System.out.println();
            System.out.println();

            System.out.println("Move to:");
            for (int j = 0; j < moveTo[0].length; j++) {
                for (int i = 0; i < moveTo.length; i++) {
                    System.out.print(moveTo[i][j] + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
        return columnsFree;
    }

    /*
     * Now calculate the crossings an insertion of the edge would produce for
     * each column and each space next to a column.
     */
    private int[] calculateCrossingsForNewEdge(SugiyamaNode n1,
            SugiyamaNode n2, int layer1, int layer2, SugiyamaNode[][] nodes,
            int[][] moveTo) {

        int[] crossingsLeft = new int[grid.getSize(GridStructure.COLUMN) * 2 + 1];
        int[] crossingsRight = new int[grid.getSize(GridStructure.COLUMN) * 2 + 1];

        // first check for all possible crossings between
        // layers layer1 and layer1 + 1
        for (int col = 0; col < grid.getSize(GridStructure.COLUMN); col++) {
            SugiyamaNode node = nodes[col][layer1];
            if (node == null) {
                // no node so no new crossings to compute
            } else if (node.isDummy) {
                int toColumn = ((SugiyamaDummyNode) node).getEdge()
                        .getNextNode(node).getColumnNumber();
                calculateCrossingsForOneEdgePart_Top(col, toColumn, layer1, n1,
                        moveTo, crossingsLeft, crossingsRight);
            } else {
                // node is real node so check every edge to
                // a higher level
                Iterator<Edge> it = node.getNode().getEdgesIterator();
                while (it.hasNext()) {
                    Edge nextEdge = it.next();
                    try {
                        SugiyamaEdge sugiEdge = (SugiyamaEdge) (nextEdge
                                .getAttribute(SugiyamaConstants.PATH_INC_EDGE)
                                .getValue());
                        SugiyamaNode nextNode = sugiEdge.getNextNode(node);
                        if (nextNode != null) {
                            // there is a node on a higher level
                            // then the source node so the edge
                            // has to go to a higher level
                            int toColumn = nextNode.getColumnNumber();
                            calculateCrossingsForOneEdgePart_Top(col, toColumn,
                                    layer1, n1, moveTo, crossingsLeft,
                                    crossingsRight);
                        }
                    } catch (AttributeNotFoundException e) {
                        // if no SugiyamaEdge is attached to
                        // this edge then it has not been
                        // added to the graph and therefore
                        // has not to be considered.
                    }
                }
            }
        }

        // now check for all possible crossings between
        // layers layer1 + 1 and layer2 - 1
        for (int layer = layer1 + 1; layer < layer2 - 1; layer++) {
            for (int col = 0; col < grid.getSize(GridStructure.COLUMN); col++) {
                SugiyamaNode node = nodes[col][layer];

                // check crossings with edges to lower level
                if (node == null) {
                    // no node so no new crossings to compute
                } else if (node.isDummy) {
                    int toColumn = ((SugiyamaDummyNode) node).getEdge()
                            .getNextNode(node).getColumnNumber();

                    calculateCrossingsForOneEdgePart_Middle(col, toColumn,
                            layer, moveTo, crossingsLeft);

                } else {
                    // node is real node so check every edge to
                    // a higher level
                    Iterator<Edge> it = node.getNode().getEdgesIterator();
                    while (it.hasNext()) {
                        Edge nextEdge = it.next();
                        try {
                            SugiyamaEdge sugiEdge = (SugiyamaEdge) (nextEdge
                                    .getAttribute(SugiyamaConstants.PATH_INC_EDGE)
                                    .getValue());
                            SugiyamaNode nextNode = sugiEdge.getNextNode(node);
                            if (nextNode != null) {
                                // there is a node on a higher level
                                // then the source node so the edge
                                // has to go to a higher level
                                int toColumn = nextNode.getColumnNumber();
                                calculateCrossingsForOneEdgePart_Middle(col,
                                        toColumn, layer, moveTo, crossingsLeft);
                            }
                        } catch (AttributeNotFoundException e) {
                            // if no SugiyamaEdge is attached to
                            // this edge then it has not been
                            // added to the graph and therefore
                            // has not to be considered.
                        }
                    }
                }
            }
        }

        // finally check for all possible crossings between
        // layers layer2-1 and layer2
        for (int col = 0; col < grid.getSize(GridStructure.COLUMN); col++) {
            SugiyamaNode node = nodes[col][layer2];
            if (node == null) {
                // no node so no new crossings to compute
            } else if (node.isDummy) {
                int fromColumn = ((SugiyamaDummyNode) node).getEdge()
                        .getPrevNode(node).getColumnNumber();
                // the edge goes from 'fromColumn' to col
                calculateCrossingsForOneEdgePart_Bottom(col, fromColumn,
                        layer2, n2, moveTo, crossingsLeft, crossingsRight);
            } else {
                // node is real node so check every edge from
                // a lesser level
                Iterator<Edge> it = node.getNode().getEdgesIterator();
                while (it.hasNext()) {
                    Edge nextEdge = it.next();
                    try {
                        SugiyamaEdge sugiEdge = (SugiyamaEdge) (nextEdge
                                .getAttribute(SugiyamaConstants.PATH_INC_EDGE)
                                .getValue());
                        SugiyamaNode prevNode = sugiEdge.getPrevNode(node);
                        if (prevNode != null) {
                            // there is a node on a lesser level
                            // then the source node so the edge
                            // has to come from a lesser level
                            int fromColumn = prevNode.getColumnNumber();

                            calculateCrossingsForOneEdgePart_Bottom(col,
                                    fromColumn, layer2, n2, moveTo,
                                    crossingsLeft, crossingsRight);
                        }
                    } catch (AttributeNotFoundException e) {
                        // if no SugiyamaEdge is attached to
                        // this edge then it has not been
                        // added to the graph and therefore
                        // has not to be considered.
                    }
                }
            }
        }
        int[] crossings = new int[grid.getSize(GridStructure.COLUMN) * 2 + 1];

        // add up the crossings
        int tmp = 0;
        for (int i = 0; i < crossingsRight.length; i++) {
            tmp += crossingsRight[i];
            crossings[i] += tmp;
        }

        tmp = 0;
        for (int i = crossingsLeft.length - 1; i >= 0; i--) {
            tmp += crossingsLeft[i];
            crossings[i] += tmp;
        }

        if (IncrementalSugiyama.DEBUG) {
            System.out.print("Crossings: ");
            for (int i = 0; i < crossings.length; i++) {
                if (i % 2 == 1) {
                    System.out.print("[");
                }
                System.out.print(crossings[i]);
                if (i % 2 == 1) {
                    System.out.print("]");
                }
                System.out.print("\t");
            }
            System.out.println();
        }
        return crossings;
    }

    private void calculateCrossingsForOneEdgePart_Top(int col, int toColumn,
            int layer1, SugiyamaNode n1, int[][] moveTo, int[] crossingsLeft,
            int[] crossingsRight) {
        // the edge goes from column col to 'toColumn'
        if (col < n1.getColumnNumber()) {
            /*
             * the edge starts left from the new one so when the new one will be
             * placed left of 'toColumn' there is a crossing. If it is placed at
             * 'toColumn' and the node there is moved to the right, then there
             * is a crossing as well.
             */
            int movedRight = (moveTo[toColumn][layer1 + 1] > 0) ? 1 : 0;

            crossingsLeft[toColumn * 2 + movedRight]++;
        } else if (col > n1.getColumnNumber()) {
            /*
             * the edge starts right from the new one so when the new one will
             * be placed right of 'toColumn' there is a crossing If it is placed
             * at 'toColumn' and the node there is moved to the left, then there
             * is a crossing as well.
             */
            int movedLeft = (moveTo[toColumn][layer1 + 1] < 0) ? -1 : 0;

            crossingsRight[toColumn * 2 + 2 + movedLeft]++;
        }
    }

    private void calculateCrossingsForOneEdgePart_Middle(int col, int toColumn,
            int layer, int[][] moveTo, int[] crossingsLeft) {
        // the edge goes from column col to 'toColumn'
        if (col < toColumn) {
            /*
             * the edge goes from left to right so on each place in between col
             * and 'toColumn' would be a crossing. If it is placed at 'toColumn'
             * and the node there is moved to the right, then there is a
             * crossing as well.
             */
            int movedRight = (moveTo[toColumn][layer + 1] > 0) ? 1 : 0;
            crossingsLeft[toColumn * 2 + movedRight]++;

            /*
             * If it is placed at 'col' and the node there is moved to the left,
             * then there is a crossing as well.
             */
            int movedLeft = (moveTo[col][layer] < 0) ? -1 : 0;
            crossingsLeft[col * 2 + 1 + movedLeft]--;
        } else if (col > toColumn) {
            /*
             * the edge goes from right to left so on each place in between col
             * and 'toColumn' would be a crossing. If it is placed at 'col' and
             * the node there is moved to the right, then there is a crossing as
             * well.
             */
            int movedRight = (moveTo[col][layer] > 0) ? 1 : 0;
            crossingsLeft[col * 2 + movedRight]++;

            /*
             * If it is placed at 'toColumn' and the node there is moved to the
             * left, then there is a crossing as well.
             */
            int movedLeft = (moveTo[toColumn][layer + 1] < 0) ? -1 : 0;
            crossingsLeft[toColumn * 2 + 1 + movedLeft]--;
        } else if (moveTo[col][layer] * moveTo[col][layer + 1] == -1) {
            /*
             * This case occurs when col == toColumn and one of the two nodes
             * would be moved to the left and the other to the right. Now there
             * is a crossing if you place the new edge in this column.
             */
            crossingsLeft[col * 2 + 1]++;
            crossingsLeft[col * 2]--;
        }
    }

    private void calculateCrossingsForOneEdgePart_Bottom(int col,
            int fromColumn, int layer2, SugiyamaNode n2, int[][] moveTo,
            int[] crossingsLeft, int[] crossingsRight) {
        if (col < n2.getColumnNumber()) {
            /*
             * the edge ends left from the new one so when the new one will be
             * placed left of 'fromColumn' there is a crossing. If it is placed
             * at 'fromColumn' and the node there is moved to the right, then
             * there is a crossing as well.
             */
            int movedRight = (moveTo[fromColumn][layer2 - 1] > 0) ? 1 : 0;
            crossingsLeft[fromColumn * 2 + movedRight]++;
        } else if (col > n2.getColumnNumber()) {
            /*
             * the edge ends right from the new one so when the new one will be
             * placed right of 'fromColumn' there is a crossing If it is placed
             * at 'fromColumn' and the node there is moved to the left, then
             * there is a crossing as well.
             */
            int movedLeft = (moveTo[fromColumn][layer2 - 1] < 0) ? -1 : 0;

            crossingsRight[fromColumn * 2 + 2 + movedLeft]++;
        }
    }

    /*
     * If a new edge shall be added between nodes of the new level, one of these
     * nodes has to be moved to a higher or lesser level. This can inflict
     * multiple shifts of nodes to other levels and the insertion of new
     * columns.<br> This method calculates the four possible shifting options,
     * chooses the best one and executes it.
     */
    private void addEdgeOnSameLevel(SugiyamaEdge edge) {
        SugiyamaNode n1 = edge.getNodes().getFirst();
        SugiyamaNode n2 = edge.getNodes().getLast();

        assert n1.getLevel() == n2.getLevel();
        ShiftData[] possibleShifts = new ShiftData[4];
        possibleShifts[0] = new ShiftData(n1, false, edge, grid);
        possibleShifts[1] = new ShiftData(n1, true, edge, grid);
        possibleShifts[2] = new ShiftData(n2, false, edge, grid);
        possibleShifts[3] = new ShiftData(n2, true, edge, grid);
        debug("" + possibleShifts[0] + possibleShifts[1] + possibleShifts[2]
                + possibleShifts[3]);

        // choose best shift possibility
        int bestSolution = 0;
        for (int i = 1; i < 4; i++) {
            if (possibleShifts[i].getQuality() > possibleShifts[bestSolution]
                    .getQuality()) {
                bestSolution = i;
            }
        }

        // execute best shift
        possibleShifts[bestSolution].executeShift(colsToCheck);
    }

    /*
     * This method takes the columns stored in colsToCheck and checks for each
     * column if an edge in this column wants to and can be shifted to a column
     * next to it. If so it shifts the edge respectively it's dummy nodes to
     * this column. This will be done until no further columns have to be
     * checked.
     */
    private void checkEdgeShifting() {
        LinkedList<Plane> columnsToCheck = new LinkedList<Plane>(colsToCheck);
        colsToCheck.clear();
        debug("Check " + columnsToCheck.size() + " column for shiftable edges.");

        checkColumns: for (Plane column : columnsToCheck) {
            if (column == null) {
                continue checkColumns;
            }

            Plane colLeft = null;
            Plane colRight = null;

            LinkedList<SugiyamaEdge> edgesToCheck = new LinkedList<SugiyamaEdge>();

            // add all edges that want to be moved to edgesToCheck
            LinkedList<SugiyamaNode> nodes = column.getNodes();
            for (SugiyamaNode node : nodes) {
                if (node.isDummy()) {
                    SugiyamaEdge edge = ((SugiyamaDummyNode) node).getEdge();
                    debug("edge wants to be shifted: "
                            + edge.getCenteredPosition());
                    if (edge.getCenteredPosition() != SugiyamaEdge.STAY
                            && !edgesToCheck.contains(edge)) {
                        edgesToCheck.add(edge);
                    }
                }
            }

            // move edges if possible
            for (SugiyamaEdge edge : edgesToCheck) {
                Plane toPlane = null;
                int moveDirection = edge.getCenteredPosition();
                if (moveDirection == SugiyamaEdge.TO_LEFT) {
                    if (colLeft == null) {
                        colLeft = column.getPrev();
                    }
                    toPlane = colLeft;
                } else {
                    if (colRight == null) {
                        colRight = column.getNext();
                    }
                    toPlane = colRight;
                }

                debug("Test shifting from " + column.getNumber() + " to "
                        + toPlane.getNumber());

                // check if edge can be moved
                boolean[] occupancyToPlane = grid.getOccupancy(toPlane);
                boolean canMove = true;
                for (int level = edge.getNodes().get(1).getLevelNumber(); level <= edge
                        .getNodes().get(edge.getNodes().size() - 2)
                        .getLevelNumber(); level++) {
                    if (occupancyToPlane[level] == true) {
                        canMove = false;
                    }
                    debug("Level " + level + " occupied: "
                            + occupancyToPlane[level]);
                }

                // edge can be moved so move it
                if (canMove) {
                    debug("Edge moved from column " + column.getNumber()
                            + " to column " + toPlane.getNumber());

                    for (SugiyamaNode node : edge.getNodes()) {
                        if (node.isDummy()) {
                            column.deleteNode(node);
                            toPlane.addNode(node);
                        }
                    }
                    // as this edge has been moved there could be edges that
                    // want to take it's place in the next column
                    if (moveDirection == SugiyamaEdge.TO_LEFT
                            && column.getNext() != null) {
                        colsToCheck.add(column.getNext());
                    } else if (moveDirection == SugiyamaEdge.TO_RIGHT
                            && column.getPrev() != null) {
                        colsToCheck.add(column.getPrev());
                    }
                    edge.checkPosition();
                    if (edge.getCenteredPosition() != SugiyamaEdge.STAY) {
                        // if this edge wants to be shifted even more check it's
                        // new column later.
                        colsToCheck.add(toPlane);
                    }
                }
            }
        }

        // if new empty places were made by shifting edges check again for
        // shifting
        if (!colsToCheck.isEmpty()) {
            checkEdgeShifting();
        }
    }

    /**
     * Returns <tt>true</tt> if the next step can be executed, <tt>false</tt>
     * otherwise-
     */
    @Override
    public boolean isReady() {
        if (this.inSubAnimation)
            return this.subAnimation.isReady();
        else
            return ready;
    }

    /**
     * The incremental sugiyama algorithm has a next step if there occurred a
     * relevant change of the graph.
     * 
     * @return true
     */
    @Override
    public boolean hasNextStep() {
        return graphHasChanged;
    }

    @Override
    public void clear() {
        // don't do anything useful here
    }

    /**
     * Reset any algorithm depending data.
     */
    public void reset() {
        grid = new GridStructure();
    }

    /*
     * Initializes several data structures used for the incremental part of the
     * algorithm.
     */
    private void initIncremental() {
        try {
            GraffitiSingleton.getInstance().getMainFrame().getStatusBar()
                    .showInfo("Currently: support of incremental changes");
        } catch (Exception e) {
            // no gui so do nothing
        }
        IncrementalSugiyamaData incData = (IncrementalSugiyamaData) data;

        IncrementalSugiyama.debug("Offsets: " + incData.getMinimal_offset_x()
                + ", " + incData.getMinimal_offset_y());
        if (incData.getMinimal_offset_x() == 0) {
            grid.setXDistance(100);
        } else {
            grid.setXDistance(incData.getMinimal_offset_x());
        }
        if (incData.getMinimal_offset_y() == 0) {
            grid.setYDistance(100);
        } else {
            grid.setYDistance(incData.getMinimal_offset_y());
        }
        // Fill the GridStructure:
        NodeLayers nodeLayers = data.getLayers();
        HashMap<Double, Plane> columnMap = new HashMap<Double, Plane>();

        // - fill levels:
        for (int i = 0; i < nodeLayers.getNumberOfLayers(); i++) {
            ArrayList<Node> nodes = nodeLayers.getLayer(i);
            Plane newPlane = new Plane(grid, GridStructure.LEVEL);

            for (Node node : nodes) {
                SugiyamaNode newNode = new SugiyamaNode(node);
                newPlane.addNode(newNode);

                // Add the node to the Plane corresponding to the node's
                // x-coordinates
                CoordinateAttribute ca = (CoordinateAttribute) node
                        .getAttribute(SugiyamaConstants.PATH_COORDINATE);
                double x = ca.getX();

                Plane column = columnMap.get(new Double(x));
                if (column != null) {
                    column.addNode(newNode);
                } else {
                    column = new Plane(grid, GridStructure.COLUMN);
                    column.addNode(newNode);
                    column.setUncheckedCoordinate(x);
                    columnMap.put(new Double(x), column);
                }
            }

            grid.add(newPlane, i);
        }

        // - add edges and dummy nodes
        Plane[] levels = grid.getPlanes(GridStructure.LEVEL);

        for (int i = 0; i < levels.length; i++) {
            Plane plane = levels[i];
            Iterator<SugiyamaNode> itNode = plane.iterator();
            while (itNode.hasNext()) { // check all nodes on this level
                SugiyamaNode node = itNode.next();
                if (!node.isDummy) {
                    Iterator<Edge> itEdge = node.getNode()
                            .getDirectedOutEdgesIterator();
                    while (itEdge.hasNext()) {
                        Edge edge = itEdge.next();
                        SugiyamaEdge newEdge = new SugiyamaEdge(edge);
                        newEdge.createDummyNodes(columnMap, grid);
                    }
                }
            }
        }

        // - sort and insert the columns into the grid structure

        Iterator<Double> it = columnMap.keySet().iterator();
        Double[] xCoords = new Double[columnMap.size()];
        int i = 0;
        while (it.hasNext()) {
            xCoords[i] = it.next();
            i++;
        }

        sortDoubleArray(xCoords);
        for (i = 0; i < xCoords.length; i++) {
            grid.add(columnMap.get(xCoords[i]), i);
        }

        grid.checkForCombinableColumns();

        // update where edges want to be shifted
        for (Edge edge : graph.getEdges()) {
            try {
                SugiyamaEdge sugiEdge = (SugiyamaEdge) ((ObjectAttribute) edge
                        .getAttribute(SugiyamaConstants.PATH_INC_EDGE))
                        .getValue();
                sugiEdge.checkPosition();
            } catch (AttributeNotFoundException e) {
                System.err.println("Attribute SugiyamaEdge not found for edge "
                        + edge);
            }
        }

        for (Plane plane : grid.getPlanes(GridStructure.COLUMN)) {
            colsToCheck.add(plane);
        }
        checkEdgeShifting();

        updateCoordinates();
        // System.out.println(grid);
    }

    private void sortDoubleArray(Double[] array) {
        for (int i = 1; i < array.length; i++) {
            checkLeft(array, i);
        }
    }

    private void checkLeft(Double[] array, int position) {
        if (position > 0 && array[position] < array[position - 1]) {
            swap(array, position, position - 1);
            checkLeft(array, position - 1);
        }
    }

    private void swap(Double[] array, int doubleOne, int doubleTwo) {
        Double tmp = array[doubleOne];
        array[doubleOne] = array[doubleTwo];
        array[doubleTwo] = tmp;
    }

    /*
     * Updates the real coordinates of all nodes and bends according to the
     * coordinates of the grid.
     */
    private void updateCoordinates() {
        LinkedList<Edge> edges = new LinkedList<Edge>();
        Plane[] planes = grid.getPlanes(GridStructure.LEVEL);
        for (int i = 0; i < planes.length; i++) {
            Plane plane = planes[i];
            Iterator<SugiyamaNode> it = plane.iterator();
            while (it.hasNext()) {
                SugiyamaNode node = it.next();
                if (!node.isDummy) {
                    node.updateCoordinates();
                    edges.addAll(node.getNode().getDirectedOutEdges());
                }
            }
        }
        Iterator<Edge> edgeIt = edges.iterator();
        while (edgeIt.hasNext()) {
            Edge edge = edgeIt.next();
            try {
                SugiyamaEdge actEdge = (SugiyamaEdge) edge.getAttribute(
                        SugiyamaConstants.PATH_INC_EDGE).getValue();
                if (actEdge != null) {
                    actEdge.updateBends();
                }
            } catch (AttributeNotFoundException e) {
                // do nothing
                // this edge is not attached to a SugiyamaEdge, yet
            }
        }
    }

    private void testIncrementalAlgorithm() {

        if (testStep == -1) {
            // System.out.println("Graph before inkremental part:");
            EvaluationUtil.evaluteGridStructure(grid);
            EvaluationUtil.calculateCorrelationBefore(grid);
            System.out.println();
            graphHasChanged = true;
            testStep++;
            return;
        }

        switch (testMode) {
        case -1:
            // System.out.println("Graph with static Sugiyama:");
            EvaluationUtil.evaluteGridStructure(grid);
            break;
        case 0:

            if (testStep >= testing_numberOfNewNodes) {
                /*
                 * System.out.println("Graph after adding " +
                 * testing_numberOfNewNodes + " nodes and " +
                 * (testing_numberOfNewNodes * testing_numberOfNewEdgesPerNode)
                 * + " edges:");
                 */
                EvaluationUtil.evaluteGridStructure(grid);
                testMode = -1;
            } else {
                Graph graph = data.getGraph();

                // select three existing nodes as neighbors of the new one
                int[] numbersInList = new int[testing_numberOfNewEdgesPerNode];
                SugiyamaNode[] neighbors = new SugiyamaNode[testing_numberOfNewEdgesPerNode];
                List<Node> nodes = graph.getNodes();
                double newX = 0;
                double newY = 0;

                for (int i = 0; i < testing_numberOfNewEdgesPerNode; i++) {
                    numbersInList[i] = (int) (Math.random() * graph.getNodes()
                            .size());
                    neighbors[i] = (SugiyamaNode) ((ObjectAttribute) nodes.get(
                            numbersInList[i]).getAttribute(
                            SugiyamaConstants.PATH_INC_NODE)).getObject();
                    newX += neighbors[i].getX();
                    newY += neighbors[i].getY();
                }
                newX /= testing_numberOfNewEdgesPerNode;
                newY /= testing_numberOfNewEdgesPerNode;

                Node newNode = graph.addNode();
                try {
                    newNode.getAttribute("graphics");
                } catch (Exception e) {
                    newNode.addAttribute(new NodeGraphicAttribute(), "");
                }
                newNode.setDouble("graphics.coordinate.x", newX);
                newNode.setDouble("graphics.coordinate.y", newY);
                // System.out.println("Added node at (" + newX + ", " + newY +
                // ")");
                newNodes.add(newNode);

                // now add the edges
                for (int i = 0; i < testing_numberOfNewEdgesPerNode; i++) {
                    boolean toNewEdge = (Math.random() * 2) > 1;
                    Edge newEdge = null;
                    if (toNewEdge) {
                        newEdge = graph.addEdge(neighbors[i].getNode(),
                                newNode, true);
                    } else {
                        newEdge = graph.addEdge(newNode,
                                neighbors[i].getNode(), true);
                    }
                    try {
                        newEdge.getAttribute("graphics");
                    } catch (Exception e) {
                        EdgeGraphicAttribute ega = new EdgeGraphicAttribute();
                        newEdge.addAttribute(ega, "");
                        ega.setArrowhead("org.graffiti.plugins.views.defaults."
                                + "StandardArrowShape");
                    }

                    newEdges.add(newEdge);
                }
                graphHasChanged = true;
            }
            break;
        case 1:
            if (testStep >= testing_numberOfNewNodes) {
                EvaluationUtil.evaluteGridStructure(grid);
                testMode = -1;
            } else {
                Graph graph = data.getGraph();

                // select an existing node to delete
                List<Node> nodes = graph.getNodes();

                Node delNode = nodes.get((int) (Math.random() * nodes.size()));

                LinkedList<Edge> tmpDelEdges = new LinkedList<Edge>();
                for (Edge e : delNode.getEdges()) {
                    // delete all edges of this node
                    tmpDelEdges.add(e);
                }

                Edge[] delEdgeArray = new Edge[1];
                delEdgeArray = tmpDelEdges.toArray(delEdgeArray);
                for (int i = 0; i < delEdgeArray.length; i++) {
                    if (delEdgeArray[i] != null) {
                        graph.deleteEdge(delEdgeArray[i]);
                        delEdges.add(delEdgeArray[i]);
                    }
                }

                graph.deleteNode(delNode);
                delNodes.add(delNode);

                graphHasChanged = true;
            }
            break;

        }

        testStep++;
    }

    /**
     * Returns the graph modification policy of this animation.
     * <p>
     * The returned graph modification policy will clear this animation whenever
     * the underlying graph is modified.
     * 
     * @return the graph modification policy of this animation.
     */
    @Override
    public GraphModificationPolicy getGraphModificationPolicy() {
        if (this.graphModificationPolicy == null) {
            graphModificationPolicy = new GraphModificationPolicy() {
                private boolean isEnabled = false;

                public void edgeAdded(Edge e) {
                    if (isEnabled) {
                        debug("Edge added");
                        graphHasChanged = true;
                        newEdges.add(e);
                    }
                }

                public void edgeModified(Edge e) {
                    // debug("Edge modified");
                    // graphHasChanged = true;
                }

                public void edgeRemoved(Edge e) {
                    if (isEnabled) {
                        debug("Edge removed");
                        graphHasChanged = true;

                        if (newEdges.contains(e)) {
                            newEdges.remove(e);
                        } else {
                            delEdges.add(e);
                        }
                    }
                }

                public void nodeAdded(Node n) {
                    if (isEnabled) {
                        debug("Node added");
                        graphHasChanged = true;

                        newNodes.add(n);
                    }
                }

                public void nodeModified(Node n) {
                    if (isEnabled) {
                        debug("Node modified");

                        // check if the coordinates of the node have been
                        // modified
                        try {
                            SugiyamaNode sugNode = (SugiyamaNode) n
                                    .getAttribute(
                                            SugiyamaConstants.PATH_INC_NODE)
                                    .getValue();
                            if (sugNode.getX() != ((DoubleAttribute) n
                                    .getAttribute("graphics.coordinate.x"))
                                    .getDouble()
                                    || sugNode.getY() != ((DoubleAttribute) n
                                            .getAttribute("graphics.coordinate.y"))
                                            .getDouble()) {
                                graphHasChanged = true;
                            }

                        } catch (AttributeNotFoundException e) {
                            // As no attached SugiyamaNode has been found, n
                            // must be
                            // a new node and will be handled by the nodeAdded
                            // event.
                        }
                    }
                }

                public void nodeRemoved(Node n) {
                    if (isEnabled) {
                        debug("Node removed");
                        graphHasChanged = true;

                        if (newNodes.contains(n)) {
                            newNodes.remove(n);
                        } else {
                            delNodes.add(n);
                        }
                    }
                }

                public void graphModified(Graph g) {
                    if (isEnabled) {
                        debug("Graph modified");
                        // graphHasChanged = true;
                    }
                }

                public void graphCleared(Graph g) {
                    if (isEnabled) {
                        debug("Graph cleared");
                        // graphHasChanged = true;
                    }
                }

                public void enable() {
                    isEnabled = true;
                }

                public void disable() {
                    isEnabled = false;
                }
            };
        }
        return graphModificationPolicy;
    }

    private void debug(String text) {
        if (IncrementalSugiyama.DEBUG && currentPhase > 4) {
            System.out.println(text);
        }
    }

    public GridStructure getGrid() {
        return grid;
    }

    public int getTesting_numberOfNewNodes() {
        return testing_numberOfNewNodes;
    }

    public void setTesting_numberOfNewNodes(int testing_numberOfNewNodes) {
        this.testing_numberOfNewNodes = testing_numberOfNewNodes;
    }

    public int getTesting_numberOfNewEdgesPerNode() {
        return testing_numberOfNewEdgesPerNode;
    }

    public void setTesting_numberOfNewEdgesPerNode(
            int testing_numberOfNewEdgesPerNode) {
        this.testing_numberOfNewEdgesPerNode = testing_numberOfNewEdgesPerNode;
    }

    public static void setTestMode(int testMode) {
        IncrementalSugiyamaAnimation.testMode = testMode;
        testStep = -1;
    }

    public int getTesting_numberOfNodesToDelete() {
        return testing_numberOfNodesToDelete;
    }

    public void setTesting_numberOfNodesToDelete(
            int testing_numberOfNodesToDelete) {
        this.testing_numberOfNodesToDelete = testing_numberOfNodesToDelete;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
