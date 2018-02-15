// =============================================================================
//
//   ShiftData.java
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

import org.graffiti.plugins.algorithms.sugiyama.util.EvaluationUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.EvaluationUtil.EdgeSegment;

/**
 * This class is intended to be used to store what changes would occur when a
 * node is shifted to another level. As this shift can inflict several following
 * shifts of other nodes there has to be stored quite a bit of data.
 */
class ShiftData {
    /* the level the first node is moved from */
    private int startLevel;

    /* the node which shall be shifted from the start level */
    private SugiyamaNode startNode;

    /* the new edge which causes this shifting */
    private SugiyamaEdge newEdge;

    /* indicates if the first node is moved downwards or upwards */
    private boolean downwards;

    /* the grid structure of the graph */
    private GridStructure grid;

    private EvaluationUtil eUtil = new EvaluationUtil();

    /* the levels which are involved in the shifting */
    private LinkedList<Level> shiftLevels = new LinkedList<Level>();

    /* the numbers of new columns that would be needed for the shifts */
    private LinkedList<Integer> newColumnNumbers = new LinkedList<Integer>();

    /* contains for each SugiyamaNode the new column number if it has to change */
    private HashMap<SugiyamaNode, Integer> newColumnNumberForNodes = new HashMap<SugiyamaNode, Integer>();

    /* contains the new dummy node for each edge if a new dummy would be needed */
    private HashMap<SugiyamaEdge, SugiyamaDummyNode> newDummyOnEdge = new HashMap<SugiyamaEdge, SugiyamaDummyNode>();

    private int crossingsToStartLevelBefore = 0;

    private int crossingsToStartLevelAfter = 0;

    /* the additional crossings this shifting would inflict. Can be < 0 */
    private int additionalCrossings = 0;

    private int numberOfAffectedNodes = 0;

    private boolean newLevelNeeded = false;

    /*
     * an abstract int to evaluate the quality of this shift. It is calculated
     * including several parameters like the additional crossings, the number of
     * new level and columns and the number of affected nodes. A higher value
     * means a better solution. Quality can be < 0.
     */
    private int quality = 0;

    /*  ********************** FUNCTIONS ************************* */

    /**
     * Constructs a <tt>ShiftData</tt> object which computes and stores all
     * necessary changes to the graph if the given start node would be shifted
     * in the given direction.
     * 
     * @param startNode
     *            the node which shall be shifted from it's level to prohibit
     *            the new edge to be between nodes on the same level
     * @param downwards
     *            indicates whether the node shall be moved to a higher level
     *            (true) or to a lesser level (false)
     * @param newEdge
     *            the new edge that causes the shifting of the start node
     * @param grid
     *            the <tt>GridStructure</tt> of the graph
     */
    ShiftData(SugiyamaNode startNode, boolean downwards, SugiyamaEdge newEdge,
            GridStructure grid) {
        this.startLevel = startNode.getLevelNumber();
        this.startNode = startNode;
        this.downwards = downwards;
        this.newEdge = newEdge;
        this.grid = grid;
        calculateShiftData();

        // calculate crossings to start level
        Plane toStartLevel = (downwards) ? grid
                .getPrev(shiftLevels.getFirst().level) : grid
                .getNext(shiftLevels.getFirst().level);
        if (toStartLevel == null) {
            // start level is the first or last level so there are no crossings
            // to it
        } else {
            crossingsToStartLevelBefore = EvaluationUtil.calculateCrossings(
                    toStartLevel, downwards);
            // calculate the crossings to the start level after shifting
            // compute EdgeSegments
            LinkedList<EdgeSegment> segments = new LinkedList<EdgeSegment>();
            for (SugiyamaNode node : toStartLevel.getNodes()) {
                int fromCol = getNewPosition(node.getColumnNumber());
                SugiyamaNode nextNode;
                if (node.isDummy) {
                    SugiyamaEdge edge = ((SugiyamaDummyNode) node).getEdge();
                    nextNode = (downwards) ? edge.getNextNode(node) : edge
                            .getPrevNode(node);
                    if (nextNode == startNode) {
                        // as the start node will be shifted there has to be a
                        // new dummy taking it's place
                        nextNode = newDummyOnEdge.get(edge);
                        assert nextNode != null;
                    }
                    int toCol = (newColumnNumberForNodes.containsKey(nextNode)) ? newColumnNumberForNodes
                            .get(nextNode)
                            : getNewPosition(nextNode.getColumnNumber());
                    segments.add(eUtil.new EdgeSegment(fromCol, toCol));
                } else {
                    LinkedList<SugiyamaEdge> edgeList = (downwards) ? node
                            .getEdgesToHigherLevel() : node
                            .getEdgesToLowerLevel();
                    for (SugiyamaEdge edge : edgeList) {
                        nextNode = (downwards) ? edge.getNextNode(node) : edge
                                .getPrevNode(node);
                        if (nextNode == startNode) {
                            // as the start node will be shifted there has to be
                            // a
                            // new dummy taking it's place
                            nextNode = newDummyOnEdge.get(edge);
                            assert nextNode != null;
                        }
                        int toCol = (newColumnNumberForNodes
                                .containsKey(nextNode)) ? newColumnNumberForNodes
                                .get(nextNode)
                                : getNewPosition(nextNode.getColumnNumber());
                        segments.add(eUtil.new EdgeSegment(fromCol, toCol));
                    }
                }
            }
            crossingsToStartLevelAfter = EvaluationUtil
                    .calculateCrossings(segments);
            additionalCrossings += (crossingsToStartLevelAfter - crossingsToStartLevelBefore);
        }
        // compute how many nodes are affected by this shifting
        LinkedList<SugiyamaNode> affectedNodes = new LinkedList<SugiyamaNode>();
        LinkedList<SugiyamaNode> deletedNodes = new LinkedList<SugiyamaNode>();

        for (Level level : shiftLevels) {
            affectedNodes.addAll(level.addList);
            deletedNodes.addAll(level.delList);
        }
        // remove nodes which are shifted and are therefore in the addList and
        // delList
        affectedNodes.removeAll(deletedNodes);
        affectedNodes.addAll(deletedNodes);
        numberOfAffectedNodes = affectedNodes.size();

        newLevelNeeded = shiftLevels.getLast().level == null;
        computeQuality();

    }

    /**
     * This method calculates all necessary changes to the graph when the start
     * node would be shifted in the given direction.<br>
     * Therefore in a first phase all nodes which have to be shifted to another
     * level and all dummy nodes which have to be removed or added to further
     * support the constraints of vertical edges are calculated and stored.<br>
     * In a second step the new and best possible position of each node that has
     * to be added or shifted is computed which can cause new columns to be
     * needed.<br>
     * Finally the additional number of crossings between the affected levels is
     * computed as well.
     */
    void calculateShiftData() {

        /*  ** PHASE 1 ** */
        /*
         * Phase one calculates which nodes have to be shifted to the next level
         * and are therefore put in the delList of their old levels and the
         * addList of their new levels. Dummy nodes are never shifted to a new
         * level. If a dummy nodes becomes useless because a real node of their
         * edge is put on it's level, it is put in the delList of it's level. If
         * an edge needs a new dummy node because the end node of the edge is
         * shifted a new dummy node will be constructed, put in newDummyOnEdge
         * and the addList of the level.
         */

        int actLevelNumber = startLevel;
        int nextLevelNumber = (downwards) ? actLevelNumber + 1
                : actLevelNumber - 1;
        Level actLevel = new Level(actLevelNumber);
        shiftLevels.add(actLevel);

        // start node has to be shifted from the start level
        actLevel.delList.add(startNode);

        // do as long as no node on the following level has to be shifted
        while (actLevel.delList.size() > 0) {
            Iterator<SugiyamaNode> it = actLevel.delList.iterator();
            while (it.hasNext()) {
                // we will need a next level so create one if there doesn't
                // exist one, yet
                if (actLevel.nextLevel == null) {
                    // create the next level
                    Level nextLevel = new Level(nextLevelNumber);
                    shiftLevels.add(nextLevel);
                    actLevel.nextLevel = nextLevel;
                    nextLevel.prevLevel = actLevel;
                }

                SugiyamaNode node = it.next();
                if (!node.isDummy) {
                    // for each real node in the delList check what effect it's
                    // removal from this level has to the next nodes on it's
                    // edges

                    // edges to a higher (lower) level if downwards is true
                    // (false)
                    Iterator<SugiyamaEdge> edgeIt = (downwards) ? node
                            .getEdgesToHigherLevel().iterator() : node
                            .getEdgesToLowerLevel().iterator();
                    while (edgeIt.hasNext()) {
                        SugiyamaEdge edge = edgeIt.next();
                        SugiyamaNode nextNode = (downwards) ? edge
                                .getNextNode(node) : edge.getPrevNode(node);

                        /*
                         * Add the next node of the edge to the delList of the
                         * next level (if it isn't in it already) because the
                         * actual node will be moved to this level and therefore
                         * the next node can't stay there.
                         */
                        if (!actLevel.nextLevel.delList.contains(nextNode)) {
                            actLevel.nextLevel.delList.add(nextNode);
                        }
                    }

                    // edges to a lower (higher) level if downwards is true
                    // (false)
                    edgeIt = (downwards) ? node.getEdgesToLowerLevel()
                            .iterator() : node.getEdgesToHigherLevel()
                            .iterator();
                    while (edgeIt.hasNext()) {
                        SugiyamaEdge edge = edgeIt.next();
                        SugiyamaNode prevNode = (downwards) ? edge
                                .getPrevNode(node) : edge.getNextNode(node);

                        if (prevNode.isDummy
                                || !actLevel.addList.contains(prevNode)) {
                            /*
                             * Either the previous node is a dummy (which won't
                             * be shifted) or it is a real node and is not added
                             * to this level but stays at the previous level. In
                             * each case we need a new dummy node on this level
                             * as the actual node will be shifted to the next
                             * level .
                             */
                            SugiyamaDummyNode newDummy = new SugiyamaDummyNode(
                                    edge);
                            newDummyOnEdge.put(edge, newDummy);
                            actLevel.addList.add(newDummy);
                        }
                    }

                    // node is shifted to the next level so add it to it's
                    // addList
                    actLevel.nextLevel.addList.add(node);
                }
            }

            actLevel = actLevel.nextLevel;
            actLevelNumber = actLevel.levelNumber;
            nextLevelNumber = (downwards) ? actLevelNumber + 1
                    : actLevelNumber - 1;
        }

        /*  ** Phase 2 ** */
        /*
         * Now we know what level each node would be after the shifting so phase
         * 2 calculates in which column each moved node would be placed and if
         * and where new column would have to be added. In addition the new
         * number of crossings per level are computed.
         */

        // actLevel is now the first level on which no nodes are deleted but
        // there are nodes in the addList.

        while (actLevel != null) {
            /*
             * Get occupancy of this level. In the first execution of the while
             * loop this could be a new level if a node of the first or last
             * level of the graph had to be shifted. In this case the occupancy
             * has only free places.
             */
            boolean[] occupancy;
            if (actLevel.level != null) {
                occupancy = grid.getOccupancy(actLevel.level);
            } else {
                occupancy = new boolean[grid.getSize(GridStructure.COLUMN)];
            }

            /*
             * The nodes in delList will be removed so their place won't be
             * occupied any more.
             */
            for (SugiyamaNode node : actLevel.delList) {
                occupancy[node.getColumnNumber()] = false;
            }

            // The occupancy has to be updated to account for the new columns
            occupancy = shiftOccupancy(occupancy, newColumnNumbers);

            // Dummy nodes of long edges could have been moved to a new column
            // already so check if any node which stays on this level has been
            // moved
            if (actLevel.level != null) {
                LinkedList<SugiyamaNode> nodes = actLevel.level.getNodes();
                nodes.removeAll(actLevel.delList);
                for (SugiyamaNode node : nodes) {
                    if (newColumnNumberForNodes.containsKey(node)) {
                        // update occupancy
                        occupancy[getNewPosition(node.getColumnNumber())] = false;
                        occupancy[newColumnNumberForNodes.get(node)] = true;
                    }
                }
            }

            // Now try to find new free positions for the nodes in addList.
            @SuppressWarnings("unchecked")
            LinkedList<SugiyamaNode> nodesNotPlaced = (LinkedList<SugiyamaNode>) actLevel.addList
                    .clone();
            LinkedList<SugiyamaNode> nodesPlaced = new LinkedList<SugiyamaNode>();

            if (nodesNotPlaced.size() > 0) {
                // first place all dummy nodes of long edges
                for (SugiyamaNode node : nodesNotPlaced) {
                    if (node.isDummy) {
                        /*
                         * a dummy node in an addList is always a newly added
                         * one which isn't in the node list of the edge, yet and
                         * which was created because the end node of the edge
                         * had to be shifted. It's next node is therefore the
                         * end node and it's previous node is the node next to
                         * this end node.
                         */
                        SugiyamaEdge edge = ((SugiyamaDummyNode) node)
                                .getEdge();
                        SugiyamaNode prevNode = (downwards) ? edge
                                .getPrevNode(edge.getNodes().getLast()) : edge
                                .getNextNode(edge.getNodes().getFirst());
                        if (prevNode.isDummy) {
                            // edge has at least two dummies so it is long
                            int prevNodePosition = getNewPosition(prevNode
                                    .getColumnNumber());
                            if (!occupancy[prevNodePosition]) {
                                // node can be placed in the same column
                                occupancy[prevNodePosition] = true;
                                newColumnNumberForNodes.put(node,
                                        prevNodePosition);
                                // System.out.println(node + " put in column " +
                                // prevNodePosition);
                                nodesPlaced.add(node);
                            } else {
                                // move long edge to a new column next to the
                                // old one
                                int newColumnNumber = prevNodePosition + 1;
                                addColumnNumber(newColumnNumber);
                                LinkedList<Integer> tmp = new LinkedList<Integer>();
                                tmp.add(newColumnNumber);
                                occupancy = shiftOccupancy(occupancy, tmp);
                                occupancy[newColumnNumber] = true;
                                newColumnNumberForNodes.put(node,
                                        newColumnNumber);
                                // System.out.println(node + " put in column " +
                                // newColumnNumber);

                                Iterator<SugiyamaNode> it = edge.getNodes()
                                        .iterator();
                                while (it.hasNext()) {
                                    SugiyamaNode edgeNode = it.next();
                                    if (edgeNode.isDummy) {
                                        newColumnNumberForNodes.put(edgeNode,
                                                newColumnNumber);
                                        // System.out.println(edgeNode +
                                        // " put in column " + newColumnNumber);
                                    }
                                }
                                nodesPlaced.add(node);
                            }
                        }
                    }
                }
            }
            nodesNotPlaced.removeAll(nodesPlaced);
            // nodesPlaced.clear();

            if (nodesNotPlaced.size() > 0) {
                // now try to place all remaining nodes close to the position
                // they like best
                for (SugiyamaNode node : nodesNotPlaced) {
                    int bestPosition;
                    if (node.isDummy) {
                        // node is a dummy node between two real nodes so the
                        // best place is in the middle of the two real nodes
                        SugiyamaEdge edge = ((SugiyamaDummyNode) node)
                                .getEdge();
                        SugiyamaNode node1 = edge.getNodes().getFirst();
                        int posNode1 = (newColumnNumberForNodes
                                .containsKey(node1)) ? newColumnNumberForNodes
                                .get(node1) : getNewPosition(node1
                                .getColumnNumber());

                        SugiyamaNode node2 = edge.getNodes().getLast();
                        int posNode2 = (newColumnNumberForNodes
                                .containsKey(node2)) ? newColumnNumberForNodes
                                .get(node2) : getNewPosition(node2
                                .getColumnNumber());

                        bestPosition = (posNode1 + posNode2) / 2;
                    } else {
                        // node is real node so the best place would be the same
                        // row it was before
                        bestPosition = getNewPosition(node.getColumnNumber());
                    }

                    // check if there is a free place close to the new node
                    int newPosition = Integer.MIN_VALUE;
                    int colToCheck = bestPosition;
                    for (int i = 0; i < 1 + 2 * IncrementalSugiyamaAnimation.MAX_DIST_FOR_NEW_NODE; i++) {
                        colToCheck += (i % 2 == 0) ? i : -i;
                        if (colToCheck >= 0 && colToCheck < occupancy.length) {
                            // colToCheck refers to a column and is not out of
                            // bound
                            if (!occupancy[colToCheck]) {
                                // put node in this column
                                occupancy[colToCheck] = true;
                                newColumnNumberForNodes.put(node, colToCheck);
                                newPosition = colToCheck;
                                break;
                            }
                        }
                    }

                    if (newPosition == Integer.MIN_VALUE) {
                        // no free place was found so add a new column to the
                        // right of the best position
                        bestPosition++;
                        addColumnNumber(bestPosition);
                        LinkedList<Integer> tmp = new LinkedList<Integer>();
                        tmp.add(bestPosition);
                        occupancy = shiftOccupancy(occupancy, tmp);
                        occupancy[bestPosition] = true;
                        newColumnNumberForNodes.put(node, bestPosition);
                    }
                }
            }
            actLevel.occupancyAfterShifting = occupancy;
            actLevel.calculateCrossingsAfterShifting();
            actLevel = actLevel.prevLevel;
        }
        /*  ** PHASE 3 ** */
        /*
         * Finally compute the number of additional crossings we get
         */

        for (Level level : shiftLevels) {
            additionalCrossings += (level.crossingsAfter - level.crossingsBefore);
        }

    }

    /*
     * Computes the new occupied positions on a level if the new columns would
     * be added.
     */
    private boolean[] shiftOccupancy(boolean[] oldOccupancy,
            LinkedList<Integer> newColumnNumbers) {
        /*
         * System.out.print("ShiftOccupancy: \t"); for (boolean occ :
         * oldOccupancy) { System.out.print(occ + "\t"); }
         * System.out.print("\nnew col numbers:\t"); for (Integer numb:
         * newColumnNumbers) { System.out.print(numb + "\t"); }
         */
        boolean[] result = new boolean[oldOccupancy.length
                + newColumnNumbers.size()];

        Iterator<Integer> it = newColumnNumbers.iterator();
        int nextInsertedCol = (it.hasNext()) ? it.next() : Integer.MAX_VALUE;
        int newCol = 0;
        for (int oldCol = 0; oldCol < oldOccupancy.length; oldCol++) {
            while (newCol == nextInsertedCol) {
                // this is a column number where a new column shall be inserted
                // therefore set the occupancy false and increase newCol
                result[newCol] = false;
                newCol++;
                nextInsertedCol = (it.hasNext()) ? it.next()
                        : Integer.MAX_VALUE;
            }
            result[newCol] = oldOccupancy[oldCol];
            newCol++;
        }

        /*
         * System.out.print("\nNew occupancy:\t"); for (boolean occ : result) {
         * System.out.print(occ + "\t"); } System.out.println("\n");
         */

        return result;
    }

    /*
     * Adds the position of a new column to the list of positions of columns
     * which also have to be added and updates the new column numbers of nodes
     * which shall be moved on their levels.
     */
    void addColumnNumber(int newNumber) {
        LinkedList<Integer> newNumbers = new LinkedList<Integer>();
        Iterator<Integer> it = newColumnNumbers.iterator();

        boolean inserted = false;
        while (it.hasNext()) {
            int number = it.next();
            if (!inserted && newNumber <= number) {
                // new column number has to be inserted here
                newNumbers.add(newNumber);
                inserted = true;
            }
            // If the new column number is inserted, yet, all following numbers
            // have to be increased by one.
            if (inserted) {
                number++;
            }

            newNumbers.add(number);
        }
        if (!inserted) {
            // insert at the end
            newNumbers.add(newNumber);
        }
        newColumnNumbers = newNumbers;

        // update new column numbers for nodes which are moved on their level
        LinkedList<SugiyamaNode> nodesToChange = new LinkedList<SugiyamaNode>();
        for (SugiyamaNode entry : newColumnNumberForNodes.keySet()) {
            if (newColumnNumberForNodes.get(entry) >= newNumber) {
                nodesToChange.add(entry);
            }
        }
        for (SugiyamaNode entry : nodesToChange) {
            newColumnNumberForNodes.put(entry, newColumnNumberForNodes
                    .get(entry) + 1);
        }
    }

    /*
     * Calculates the position a column would have after the new columns would
     * have been inserted.
     */
    int getNewPosition(int oldPosition) {
        Iterator<Integer> it = newColumnNumbers.iterator();
        while (it.hasNext()) {
            int nextNewColumn = it.next();
            if (nextNewColumn <= oldPosition) {
                oldPosition++;
            } else
                return oldPosition;
        }
        return oldPosition;
    }

    /*
     * Executes all changes stored in this ShiftData.
     */
    void executeShift(HashSet<Plane> colsToCheck) {
        // add additional columns
        for (Integer newColNumber : newColumnNumbers) {
            Plane newCol = new Plane(grid, GridStructure.COLUMN);
            grid.add(newCol, newColNumber);

            /*
             * As a new column is inserted the adjacent columns have to be
             * checked if it wouldn't be possible and better to shift an edge
             * them to this column.
             */
            if (newCol.getPrev() != null) {
                colsToCheck.add(newCol.getPrev());
            }
            if (newCol.getNext() != null) {
                colsToCheck.add(newCol.getNext());
            }
        }

        // add additional level if needed
        if (newLevelNeeded) {
            Plane newLevel = new Plane(grid, GridStructure.LEVEL);
            int newPosition = (downwards) ? grid.getSize(GridStructure.LEVEL)
                    : 0;
            grid.add(newLevel, newPosition);
            shiftLevels.getLast().level = newLevel;
        }

        // direct the start edge correctly
        if ((newEdge.getNodes().getFirst() == startNode && downwards)
                || (newEdge.getNodes().getLast() == startNode && !downwards)) {
            // swap nodes in the node list
            newEdge.swapDirection();
        }

        // set the new edge as out or in edges for the nodes
        newEdge.getNodes().getFirst().getEdgesToHigherLevel().add(newEdge);
        newEdge.getNodes().getLast().getEdgesToLowerLevel().add(newEdge);

        // stores the affected edges to update their bends after the shifting
        HashSet<SugiyamaEdge> affectedEdges = new HashSet<SugiyamaEdge>();

        // stores added dummies as they can't be added correctly to their edge
        // before the end node of their edge had been moved to the new level.
        LinkedList<SugiyamaDummyNode> addDummies = new LinkedList<SugiyamaDummyNode>();

        for (Level level : shiftLevels) {
            // remove nodes of the delList
            for (SugiyamaNode node : level.delList) {
                if (node.isDummy) {
                    SugiyamaEdge edge = ((SugiyamaDummyNode) node).getEdge();
                    edge.removeDummyNode((SugiyamaDummyNode) node);
                    affectedEdges.add(edge);
                }
                // remove the node from it's old level and column
                node.getLevel().deleteNode(node);
                Plane delColumn = node.getColumn();
                delColumn.deleteNode(node);

                /*
                 * As the node is removed the adjacent columns have to be
                 * checked if it wouldn't be possible and better to shift an
                 * edge to this column.
                 */
                if (delColumn.getPrev() != null) {
                    colsToCheck.add(delColumn.getPrev());
                }
                if (delColumn.getNext() != null) {
                    colsToCheck.add(delColumn.getNext());
                }

            }

            // add nodes of the addList to their new level
            for (SugiyamaNode node : level.addList) {
                if (node.isDummy) {
                    addDummies.add((SugiyamaDummyNode) node);
                }

                level.level.addNode(node);
                grid.getPlane(GridStructure.COLUMN,
                        newColumnNumberForNodes.get(node)).addNode(node);
                newColumnNumberForNodes.remove(node);
            }
        }

        // add new dummies to their edges
        for (SugiyamaDummyNode node : addDummies) {
            SugiyamaEdge edge = node.getEdge();
            edge.addDummyNodeWithoutBendUpdate(node);
            affectedEdges.add(edge);
        }

        // add new, moved or shifted nodes to their column
        for (SugiyamaNode node : newColumnNumberForNodes.keySet()) {
            grid.getPlane(GridStructure.COLUMN,
                    newColumnNumberForNodes.get(node)).addNode(node);
        }

        // update bends of the affected edges
        for (SugiyamaEdge edge : affectedEdges) {
            edge.updateBends();
            edge.checkPosition(colsToCheck);
        }
    }

    private void computeQuality() {
        /*
         * The following criteria are considered: - source node will end up
         * below target node: -100 - new level: -20 - new column: -20 - new
         * crossing: -5 - affected node: -1
         */

        // startNode == newEdge.getNodes().getFirst()
        int correctDirection = ((startNode == newEdge.getNodes().getFirst() && downwards)
        // the source node would be moved downwards
        || (startNode == newEdge.getNodes().getLast() && !downwards))
        // the target node would be moved upwards
        ? 1 // source node is below target node
                : 0; // source node is above target node

        int newLevel = (newLevelNeeded) ? 1 : 0;
        quality = -100 * correctDirection - 20 * newLevel - 20
                * newColumnNumbers.size() - 5 * additionalCrossings - 1
                * numberOfAffectedNodes;
    }

    public int getQuality() {
        return quality;
    }

    @Override
    public String toString() {
        String result = "\n\nShiftData for " + startNode + " ";
        result += (downwards) ? "downwards\n" : "upwards\n";
        result += "Additional crossings: " + additionalCrossings + "\n";
        result += "Affected nodes: " + numberOfAffectedNodes + "\n";
        result += "A new level is ";
        result += (newLevelNeeded) ? "needed.\n" : "not needed.\n";
        /*
         * for (Level level : shiftLevels) { result += level + "\n"; }
         */
        result += "New columns at:\t";
        for (Integer col : newColumnNumbers) {
            result += col + "\t";
        }
        /*
         * result += "\nNew column number for moved dummy nodes:\t";
         * Set<SugiyamaNode> movedDummies = newColumnNumberForNodes.keySet();
         * for (Level level: shiftLevels) {
         * movedDummies.removeAll(level.addList); } for (SugiyamaNode node :
         * movedDummies) { result += "(" + node + ", " +
         * newColumnNumberForNodes.get(node) + ")\t"; }
         */
        result += "\nQuality: " + quality;
        result += "\n";

        return result;
    }

    /* ##################### CLASS LEVEL ################################ */

    /* represents one level which is affected by the shift */
    private class Level {
        private int levelNumber;

        /* contains all nodes which have to be added to this level */
        private LinkedList<SugiyamaNode> addList = new LinkedList<SugiyamaNode>();

        /* contains all nodes which have to be deleted from this level */
        private LinkedList<SugiyamaNode> delList = new LinkedList<SugiyamaNode>();

        /* number of crossings before shifting the nodes */
        private int crossingsBefore = 0;

        /* number of crossings after shifting the nodes */
        private int crossingsAfter = 0;

        private Level prevLevel = null;

        private Level nextLevel = null;

        private Plane level;

        // for debugging
        boolean[] occupancyAfterShifting;

        /*  ********* functions **************** */

        private Level(int levelNumber) {
            this.levelNumber = levelNumber;
            if (levelNumber < grid.getSize(GridStructure.LEVEL)
                    && levelNumber >= 0) {
                level = grid.getPlane(GridStructure.LEVEL, levelNumber);
                crossingsBefore = EvaluationUtil.calculateCrossings(level,
                        downwards);
            }
        }

        /*
         * Calculates how many crossing there will be after the shifting between
         * this level and that level next to it that is further away from the
         * start level.
         */
        void calculateCrossingsAfterShifting() {
            if (level == null) {
                // this level is a new level on top of the first or below the
                // last
                // level and therefore has no edges and crossings to a more far
                // out level
            } else {
                LinkedList<SugiyamaNode> nodes = level.getNodes();
                nodes.removeAll(delList);
                nodes.addAll(addList);

                LinkedList<EdgeSegment> segments = new LinkedList<EdgeSegment>();

                // create EdgeSegments
                Iterator<SugiyamaNode> it = nodes.iterator();
                while (it.hasNext()) {
                    SugiyamaNode node = it.next();

                    int from = (newColumnNumberForNodes.get(node) == null) ? getNewPosition(node
                            .getColumnNumber())
                            : newColumnNumberForNodes.get(node);

                    if (node.isDummy()) {
                        SugiyamaEdge edge = ((SugiyamaDummyNode) node)
                                .getEdge();

                        int to;
                        if (addList.contains(node)) {
                            /*
                             * This dummy node is a new one. Therefore the next
                             * node has to be a real node which had to be moved
                             * and is the last (or first) node of the edge
                             * (depending on downwards).
                             */
                            SugiyamaNode nextNode = (downwards) ? edge
                                    .getNodes().getLast() : edge.getNodes()
                                    .getFirst();
                            to = (newColumnNumberForNodes.get(nextNode) == null) ? getNewPosition(nextNode
                                    .getColumnNumber())
                                    : newColumnNumberForNodes.get(nextNode);

                        } else {
                            SugiyamaNode nextNode = (downwards) ? edge
                                    .getNextNode(node) : edge.getPrevNode(node);
                            // check if the nextNode is to be shifted. In this
                            // case
                            // there would be a new dummy which would be in the
                            // same
                            // column as node
                            if (nextLevel != null
                                    && nextLevel.delList.contains(nextNode)) {
                                to = from;
                            } else if (newColumnNumberForNodes.get(nextNode) != null) {
                                to = newColumnNumberForNodes.get(nextNode);
                            } else {
                                to = getNewPosition(nextNode.getColumnNumber());
                            }

                        }
                        segments.add(eUtil.new EdgeSegment(from, to));

                    } else {
                        // it's a real node
                        Iterator<SugiyamaEdge> edgeIt = (downwards) ? node
                                .getEdgesToHigherLevel().iterator() : node
                                .getEdgesToLowerLevel().iterator();
                        while (edgeIt.hasNext()) {
                            SugiyamaEdge nextEdge = edgeIt.next();
                            SugiyamaNode nextNode = (downwards) ? nextEdge
                                    .getNextNode(node) : nextEdge
                                    .getPrevNode(node);
                            int to = 0;

                            if (addList.contains(node)) {
                                // this node was shifted to this level so next
                                // node
                                // had been on this level before.
                                if (nextNode.isDummy) {
                                    // nextNode is a dummy so it had been
                                    // deleted and
                                    // so we need the node after nextNode
                                    nextNode = (downwards) ? nextEdge
                                            .getNextNode(node) : nextEdge
                                            .getPrevNode(node);
                                }
                            }

                            if (nextLevel != null
                                    && nextLevel.delList.contains(nextNode)) {
                                // nextNode was on the next level and shall be
                                // shifted
                                // so there has to be a new dummy node on the
                                // next level
                                // which we will use for the calculations
                                nextNode = newDummyOnEdge.get(nextEdge);
                                if (nextNode == null) {
                                    // this case should never occur
                                    System.err
                                            .println("There should be a new dummy node "
                                                    + "for the edge "
                                                    + nextEdge
                                                    + " but "
                                                    + "none was found.");
                                } else {
                                    to = newColumnNumberForNodes.get(nextNode);
                                }
                            } else if (nextLevel != null) {
                                to = (newColumnNumberForNodes.get(nextNode) != null) ? newColumnNumberForNodes
                                        .get(nextNode)
                                        : getNewPosition(nextNode
                                                .getColumnNumber());
                            } else {
                                to = getNewPosition(nextNode.getColumnNumber());
                            }

                            segments.add(eUtil.new EdgeSegment(from, to));
                        }
                    }
                }

                if (levelNumber == startLevel) {
                    // for the start level we have to add the edge segment of
                    // the new
                    // edge
                    int to = newColumnNumberForNodes.get(startNode);
                    SugiyamaNode endNode = (newEdge.getNextNode(startNode) == null) ? newEdge
                            .getPrevNode(startNode)
                            : newEdge.getNextNode(startNode);
                    int from = getNewPosition(endNode.getColumnNumber());
                    segments.add(eUtil.new EdgeSegment(from, to));
                }

                /*
                 * System.out.print("Crossing segments after shifting between level "
                 * + levelNumber + " and "); if (downwards) {
                 * System.out.print((levelNumber + 1)); } else {
                 * System.out.print((levelNumber - 1)); }
                 * System.out.print(" : ");
                 */

                crossingsAfter = EvaluationUtil.calculateCrossings(segments);
            }
        }

        @Override
        public String toString() {
            String result = "Level " + levelNumber + ":\n";
            result += "AddList:\t";

            for (SugiyamaNode node : addList) {
                result += node;
                result += (newColumnNumberForNodes.get(node) != null) ? " at column "
                        + newColumnNumberForNodes.get(node) + "\t"
                        : "\t";
            }
            result += "\nDelList:\t";

            for (SugiyamaNode node : delList) {
                result += node + "\t";
            }
            result += "\nNew occupancy: ";
            for (boolean occ : occupancyAfterShifting) {
                result += occ + "\t";
            }
            result += "\nCrossings before: " + crossingsBefore + " and after: "
                    + crossingsAfter;

            result += "\n";

            return result;
        }
    }

}
