//=============================================================================
//
//   GridStructure.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.incremental;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.plugins.algorithms.sugiyama.util.NumberedTree;

/**
 * A <tt>GridStructure</tt> is a data structure which stores nodes in levels and
 * columns so that the coordinates of the nodes are arranged like the crossings
 * of a grid.<br>
 * All levels as well as all columns are stored in <tt>NumberedTree</tt>s and
 * therefore are numbered serially.<br>
 * For each level and column it's y coordinate respectively it's x coordinate is
 * managed.
 */
public class GridStructure {

    /** Different types a <tt>Plane</tt> may have. **/
    public static final int LEVEL = 0;
    public static final int COLUMN = 1;

    /**
     * @uml.property name="levels" readOnly="true"
     */
    private NumberedTree<Plane>[] planes;

    /* distance between two levels */
    private double yDistance = 50;

    /* distance between two columns */
    private double xDistance = 50;

    /* stores the occupancies of each Plane as long as they are up to date */
    private HashMap<Plane, boolean[]>[] occupancys;

    /*  ************** functions and methods ********** */

    /**
     * Constructs a new <tt>GridStructure</tt>.
     */
    public GridStructure() {
        @SuppressWarnings("unchecked")
        NumberedTree<Plane>[] planesC = new NumberedTree[2];
        planes = planesC;
        planes[LEVEL] = new NumberedTree<Plane>();
        planes[COLUMN] = new NumberedTree<Plane>();

        @SuppressWarnings("unchecked")
        HashMap<Plane, boolean[]>[] occupancysC = (HashMap<Plane, boolean[]>[]) new HashMap<?, ?>[2];
        occupancys = occupancysC;
        occupancys[LEVEL] = new HashMap<Plane, boolean[]>();
        occupancys[COLUMN] = new HashMap<Plane, boolean[]>();
    }

    /**
     * Adds a <tt>Plane</tt> at the given position. Depending on the type of the
     * <tt>Plane</tt> it is inserted to the levels or the columns.
     */
    public void add(Plane plane, int position) {
        // O(log(k)), k=#levels/columns
        int type = plane.getType();
        planes[type].add(plane, position);
        occupancys[1 - type] = new HashMap<Plane, boolean[]>();
    }

    /**
     * Removes the given <tt>Plane</tt>. Depending on the type of the
     * <tt>Plane</tt> it is removed from the levels or the columns.
     */
    public void remove(Plane plane) {
        // O(1) - O(log(k)), k=#levels/columns
        int type = plane.getType();
        planes[type].remove(plane);
        occupancys[1 - type] = new HashMap<Plane, boolean[]>();
    }

    /**
     * The number of the <tt>Plane</tt> closest to the given coordinate is
     * looked up and returned. If the coordinate is more then half the size of a
     * level or column smaller then the first (or bigger then the last) plane,
     * -1 (respectively a number one higher then that one of the last plane) is
     * given back.
     * 
     * @param type
     *            GridStructure.LEVEL if the <tt>Plane</tt> is a level, <br>
     *            GridStructure.COLUMN if the <tt>Plane</tt> is a column.
     * @param coord
     *            The x or y coordinate (depending on being a level or a column)
     *            for which the closest <tt>Plane</tt> should be found.
     * @return The number of the closest <tt>Plane</tt> found or -1 if there is
     *         no plane of the given type in this <tt>GridStructure</tt>. See
     *         description for more details.
     */

    public int getClosest(int type, double coord) {
        // O(k), k=#levels/columns
        NumberedTree<Plane> tree = planes[type];
        double dist = coord; // distance from 0
        Plane actPlane = tree.getFirst();
        int result = -1;

        while (actPlane != null) {
            double newDist = Math.abs(actPlane.getCoordinate() - coord);
            if (newDist > dist)
                return result;
            dist = newDist;
            result++;
            actPlane = tree.getNext(actPlane);
        }

        // If we reach this point, result is either -1 or the number of the last
        // plane.
        // Check if the best distance is more then half a plane distance
        double planeDist = (type == LEVEL) ? yDistance : xDistance;
        if (dist > planeDist / 2) {
            result++;
        }

        return result;
    }

    /**
     * Returns an array of boolean values that indicate which levels/columns of
     * this column/level are occupied by (dummy) nodes and which are not.
     * 
     * @return boolean[] array of occupancies. An entry with the value
     *         <code>true</code> indicates that this place is occupied.
     */
    public boolean[] getOccupancy(Plane plane) {
        // O(1) - O(m), m=#levels/columns
        int type = plane.getType();

        // test if the occupancies are still stored
        if (occupancys[type].get(plane) != null)
            return occupancys[type].get(plane);

        int size = getSize(1 - type); // 1-type = level->column; column->level
        boolean[] result = new boolean[size];

        Iterator<SugiyamaNode> it = plane.iterator();
        while (it.hasNext()) {
            SugiyamaNode node = it.next();
            int number = (type == GridStructure.LEVEL) ? node.getColumnNumber()
                    : node.getLevelNumber();
            assert number < size;
            result[number] = true;
        }

        return result;
    }

    /**
     * Returns a two dimensional array of boolean values that indicate which
     * places are occupied in this grid.<br>
     * 
     * @param type
     *            <tt>GridStructure.LEVEL</tt> if the first dimension of the
     *            result should be the levels and the second dimension the
     *            columns.<br>
     *            <tt>GridStructure.COLUMN</tt> if it should be the other way
     *            round.
     */
    public boolean[][] getOccupancies(int type) {
        boolean[][] result = new boolean[getSize(type)][];
        Plane[] planeArray = getPlanes(type);

        for (int i = 0; i < result.length; i++) {
            result[i] = getOccupancy(planeArray[i]);
        }

        return result;
    }

    /**
     * Deletes the occupancy boolean[] of this plane.
     */
    public void occupancyOutDated(Plane plane) {
        // O(1)
        occupancys[plane.getType()].remove(plane);
    }

    /**
     * Returns an array of <tt>SugiyamaNode</tt>s representing the given plane.
     * Each <tt>SugiyamaNode</tt> and <tt>SugiyamaDummyNode</tt> is places on
     * the position in the array according to it's level respectively column. If
     * there is no node on any of the array positions it's value is
     * <tt>null</tt>.
     */
    public SugiyamaNode[] getNodes(Plane plane) {
        // O(m), m = #Columns/Planes
        int type = plane.getType();

        int size = getSize(1 - type); // 1-type = level->column; column->level
        SugiyamaNode[] result = new SugiyamaNode[size];

        Iterator<SugiyamaNode> it = plane.iterator();
        while (it.hasNext()) {
            SugiyamaNode node = it.next();
            int number = (type == GridStructure.LEVEL) ? node.getColumnNumber()
                    : node.getLevelNumber();

            assert number < size;
            result[number] = node;
        }
        return result;
    }

    /**
     * If <code>type</code> is <code>GridStructure.LEVEL</code> the first
     * dimension of the two dimensional array represents the different level
     * whereas the second dimension is an array containing the nodes on this
     * level as described for the function <tt>getNodes(Plane plane)</tt>.<br>
     * If <code>type</code> is <code>GridStructure.COLUMN</code>, it is the
     * other way round.
     */
    public SugiyamaNode[][] getNodes(int type) {
        // O(k * m), k=#levels, m=#columns
        SugiyamaNode[][] result = new SugiyamaNode[planes[type].size()][];

        Plane[] planeArray = getPlanes(type);

        for (int i = 0; i < result.length; i++) {
            result[i] = getNodes(planeArray[i]);
        }

        return result;
    }

    /**
     * Tests if any two columns next to each other can be combined without
     * moving nodes to another level. If so it moves all nodes from the left to
     * the right column and removes the left column from the grid.
     */
    public void checkForCombinableColumns() {
        // O(n)
        IncrementalSugiyama.debug("Check for combinable columns...");
        boolean[][] occupancies = getOccupancies(COLUMN);
        Plane[] columns = getPlanes(COLUMN);
        LinkedList<Plane> toCombine = new LinkedList<Plane>();

        /* print occupancies */
        /*
         * System.out.println("Occupancies:"); for (int d = 0; d <
         * occupancies[0].length; d++) { for (int e = 0; e < occupancies.length;
         * e++) { System.out.print(occupancies[e][d] + "\t"); }
         * System.out.println(); }
         */

        for (int i = 0; i < occupancies.length - 1; i++) {
            // check if no level is occupied by both columns
            boolean combinable = true;
            for (int j = 0; j < occupancies[i].length; j++) {
                combinable &= !(occupancies[i][j] & occupancies[i + 1][j]);
            }

            if (combinable) {
                toCombine.add(columns[i]);
                for (int j = 0; j < occupancies[i].length; j++) {
                    occupancies[i + 1][j] |= occupancies[i][j];
                }
            }
        }

        // move nodes from the column which will be deleted to it's right
        // neighbor.
        Object[] it = toCombine.toArray();
        for (int i = 0; i < it.length; i++) {
            Plane fromPlane = (Plane) it[i];
            Plane toPlane = planes[COLUMN].getNext(fromPlane);

            Iterator<SugiyamaNode> nodeIt = fromPlane.iterator();
            SugiyamaNode[] nodes = new SugiyamaNode[fromPlane.getSize()];
            int tmp = 0;
            while (nodeIt.hasNext()) {
                nodes[tmp] = nodeIt.next();
                tmp++;
            }
            for (int j = 0; j < nodes.length; j++) {
                fromPlane.deleteNode(nodes[j]);
                toPlane.addNode(nodes[j]);
            }
            remove(fromPlane);
        }
    }

    /**
     * Returns a <tt>String</tt> representation of this grid containing all
     * levels with their nodes as well as all columns with their nodes.
     */
    @Override
    public String toString() {
        return "GridStructure:\nLevels:\n" + planes[LEVEL] + "\n\nColumns:\n"
                + planes[COLUMN];
    }

    /*  ********* Getter and Setter ************ */

    /**
     * Returns the serial number of the given <tt>Plane</tt>.
     */
    public int getNumber(Plane plane) {
        // O(1) - O(log k), k=#levels/columns
        int type = plane.getType();
        if (type == COLUMN) {
            if (planes[COLUMN].size() > 0)
                return planes[COLUMN].getNumber(plane);
            else
                // the planes have not been added to the grid during the
                // initiation of the incremental sugiyama algorithm
                return plane.getRandomIdentifier();
        } else
            return planes[LEVEL].getNumber(plane);
    }

    /**
     * Returns the number of level or columns depending on the given type.
     */
    public int getSize(int type) {
        // O(1)
        return planes[type].size();
    }

    /**
     * Returns the plane of the given type at the given position.
     */
    public Plane getPlane(int type, int position) {
        // O(log(k)), k=#levels/columns
        assert (type == LEVEL || type == COLUMN);
        return planes[type].get(position);
    }

    /**
     * Returns an array of <tt>Plane</tt>s containing all <tt>Plane<tt>s of the
     * given type.
     */
    public Plane[] getPlanes(int type) {
        // O(k), k=#levels/columns
        Plane[] result = new Plane[planes[type].size()];
        Object[] planeArray = planes[type].toArray();
        for (int i = 0; i < result.length; i++) {
            result[i] = (Plane) planeArray[i];
        }
        return result;
    }

    /**
     * Returns the coordinate of the given plane. Depending on what type the
     * plane is this coordinate can be either a x or a y coordinate.
     */
    public double getCoordinate(Plane plane) {
        // O(1) - O(log(k)), k=#levels/columns
        int type = plane.getType();
        if (type == LEVEL)
            return ((getNumber(plane) + 1) * yDistance);
        else {
            if (planes[COLUMN].size() > 0)
                return ((getNumber(plane) + 1) * xDistance);
            else
                // the planes have not been added to the grid during the
                // initiation of the incremental sugiyama algorithm
                return plane.getUncheckedCoordinate();
        }
    }

    /**
     * Get <tt>Plane</tt> preceding the given <tt>Plane</tt>.
     */
    public Plane getPrev(Plane plane) {
        // O(1)
        return planes[plane.getType()].getPrev(plane);
    }

    /**
     * Get <tt>Plane</tt> succeeding the given <tt>Plane</tt>.
     */
    public Plane getNext(Plane plane) {
        // O(1)
        return planes[plane.getType()].getNext(plane);
    }

    /**
     * Sets the yDistance.
     * 
     * @param distance
     *            the yDistance to set.
     */
    public void setYDistance(double distance) {
        // O(1)
        yDistance = distance;
    }

    /**
     * Sets the xDistance.
     * 
     * @param distance
     *            the xDistance to set.
     */
    public void setXDistance(double distance) {
        // O(1)
        xDistance = distance;
    }

}
