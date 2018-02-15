package org.graffiti.plugins.algorithms.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Represents the coordinate assignment phase of the Sugiyama-algorithm using
 * the Brandes/K�pf-algorithm.
 * 
 * @author Matthias H�llm�ller
 * 
 */
public class CoordinateAssignment {

    /**
     * the graph
     */
    private Graph graph;

    /**
     * stores the current order of each level
     */
    private LinkedList<Node>[] order;

    /**
     * store the offsets
     */
    // private HashMap<Edge, Integer> offset = new HashMap<Edge, Integer>();

    /**
     * stores the dummy nodes for each level
     */
    private HashSet<Node> dummies;

    /**
     * constants for direction
     */
    private static final int LEFT_UP = 0;
    private static final int LEFT_DOWN = 1;
    private static final int RIGHT_UP = 2;
    private static final int RIGHT_DOWN = 3;

    /**
     * the constructor
     * 
     * @param graph
     *            the graph
     * @param order
     *            the layout of the graph
     * @param offset
     *            the offsets of the edges
     * @param dummies
     *            the dummy nodes
     */
    public CoordinateAssignment(Graph graph, LinkedList<Node>[] order,
            HashMap<Edge, Integer> offset, HashSet<Node> dummies) {
        this.graph = graph;
        this.order = order;
        // this.offset = offset;
        this.dummies = dummies;
    }

    /**
     * computes coordinates for each direction using the Brandes/K�pf-algorithm
     * and balances the four results to one horizontal coordinate
     * 
     * @return x-coordinates horizontal coordinates
     */
    @SuppressWarnings("unchecked")
    public HashMap<Node, Integer> getCoordinates() {

        // to store coordinates for each direction
        HashMap<Node, Integer>[] xCoordinates = new HashMap[4];

        // returned coordinates
        HashMap<Node, Integer> coordinates = new HashMap<Node, Integer>();

        // not used because of better practical results
        // eliminates type 3 conflicts
        // unwind();
        // eliminates the remaining crossings of inner segments with the ray
        // rotate();

        // create a new BrandesKoepfAlgorithm object
        BrandesKoepfAlgorithm bk = new BrandesKoepfAlgorithm(this.graph);

        // for each direction
        for (int i = 0; i <= 3; i++) {

            // update the order according to current direction
            LinkedList<Node>[] newOrder = updateOrder(i);

            // compute relative x-coordinates
            coordinates = bk.computeRelativeCoordinates(newOrder, this.dummies);

            // calculate absolute x-coordinates
            xCoordinates[i] = bk.getAbsoluteCoordinates(coordinates);

            // for direction right to left we changed horizontal order before
            // computing the coordinates - so update to the original order
            if (i == RIGHT_DOWN || i == RIGHT_UP) {

                // get the maximum coordinate
                int maxCoordinate = 0;
                for (Integer x : xCoordinates[i].values()) {
                    if (x != null && x > maxCoordinate) {
                        maxCoordinate = x;
                    }
                }

                // subtract each coordinate from maximum coordinate to get the
                // original order
                for (Node v : xCoordinates[i].keySet()) {
                    if (xCoordinates[i].get(v) != null) {
                        xCoordinates[i].put(v, maxCoordinate
                                - xCoordinates[i].get(v));
                    }
                }
            }
        }

        // balance coordinates of each direction
        coordinates = balance(xCoordinates);

        return coordinates;
    }

    /**
     * eliminate type-3 conflicts (inner segment is cut segment) - level i is
     * unwound by rotating the whole outer graph by multiples of 360�
     */
    /*
     * private void unwind() {
     * 
     * // between level 1 and 2 (order.length-1 and order.length) there is no //
     * inner segment for (int i = 2; i < this.order.length; i++) {
     * unwindLevel(i); } }
     */
    /**
     * unwind current level by rotating the
     * 
     * @param i
     *            current level
     */
    /*
     * private void unwindLevel(int i) {
     * 
     * // get minimum offset of an inner segment (means an edge from a dummy //
     * node on level i to a dummy node on level i-1 int m = Integer.MAX_VALUE;
     * for (Node u : this.order[i - 1]) { for (Node v : this.order[i]) { if
     * (this.dummies.contains(u) && this.dummies.contains(v)) { LinkedList<Edge>
     * edges = (LinkedList<Edge>) this.graph .getEdges(u, v); for (Edge e :
     * edges) { if (this.offset.get(e) < m) { m = this.offset.get(e); } } } } }
     * 
     * // reduce offsets of all edges between i and i-1 by m - after this each
     * // offset of an inner segment is 0 or +1 (Lemma 2 radial Coordiate //
     * Assignment) if (m != Integer.MAX_VALUE) { for (Node u : this.order[i -
     * 1]) { for (Node v : this.order[i]) { LinkedList<Edge> edges =
     * (LinkedList<Edge>) this.graph .getEdges(u, v); for (Edge e : edges) {
     * this.offset.put(e, this.offset.get(e) - m); } } } }
     * 
     * }
     */
    /**
     * eliminates the remaining crossings of inner segments with the ray
     */
    /*
     * private void rotate() {
     * 
     * // rotate each level for (int i = 2; i < this.order.length; i++) {
     * 
     * // get maximum position of nodes of current level, which belongs to // an
     * inner segment and has offset 1 int pos = -1; for (Node u : this.order[i -
     * 1]) { for (Node v : this.order[i]) { if (this.dummies.contains(u) &&
     * this.dummies.contains(v)) { LinkedList<Edge> edges = (LinkedList<Edge>)
     * this.graph .getEdges(u, v); for (Edge e : edges) { if (this.offset.get(e)
     * == 1) { if (this.order[i].indexOf(v) > pos) { pos =
     * this.order[i].indexOf(v); } } } } } }
     * 
     * // rotate all nodes until the maximum position pos for (int j = 0; j <=
     * pos; j++) {
     * 
     * // current node Node v = this.order[i].get(j);
     * 
     * // update offsets of incoming edges for (Node u : this.order[i - 1]) {
     * LinkedList<Edge> edges = (LinkedList<Edge>) this.graph .getEdges(u, v);
     * for (Edge e : edges) { this.offset.put(e, this.offset.get(e) - 1); } }
     * 
     * // update offsets of outgoing edges for (Node w : this.order[i + 1]) {
     * LinkedList<Edge> edges = (LinkedList<Edge>) this.graph .getEdges(v, w);
     * for (Edge e : edges) { this.offset.put(e, this.offset.get(e) + 1); } } }
     * 
     * }
     * 
     * }
     */
    /**
     * updates the order according to the current direction
     * 
     * @param direction
     *            the current direction
     * @return updated order
     */
    @SuppressWarnings("unchecked")
    private LinkedList<Node>[] updateOrder(int direction) {

        // initialize data structure for new order
        LinkedList<Node>[] newOrder = new LinkedList[this.order.length];
        for (int i = 0; i < newOrder.length; ++i) {
            newOrder[i] = new LinkedList<Node>();
        }

        // update the order according to current direction
        switch (direction) {
        case LEFT_UP:
            newOrder = this.order.clone();
            break;
        case LEFT_DOWN:
            updateVertical(newOrder);
            break;
        case RIGHT_UP:
            updateHorizontal(newOrder);
            break;
        case RIGHT_DOWN:
            updateHorizontalVertical(newOrder);
            break;
        default:
            break;
        }

        return newOrder;
    }

    /**
     * changes the order horizontal
     * 
     * @param newOrder
     */
    private void updateHorizontal(LinkedList<Node>[] newOrder) {
        for (int i = 0; i < this.order.length; ++i) {
            for (int j = 0; j < this.order[i].size(); ++j) {
                newOrder[i].addFirst(this.order[i].get(j));
            }
        }
    }

    /**
     * changes the order vertical
     * 
     * @param newOrder
     */
    private void updateVertical(LinkedList<Node>[] newOrder) {
        for (int i = 0; i < this.order.length; ++i) {
            newOrder[newOrder.length - 1 - i] = this.order[i];
        }
    }

    /**
     * changes the order horizontal and vertical
     * 
     * @param newOrder
     */
    private void updateHorizontalVertical(LinkedList<Node>[] newOrder) {
        for (int i = 0; i < this.order.length; ++i) {
            for (int j = 0; j < this.order[i].size(); ++j) {
                newOrder[newOrder.length - 1 - i]
                        .addFirst(this.order[i].get(j));
            }
        }
    }

    /**
     * aligns the assignments to the one with smallest width and computes the
     * average median
     * 
     * @param xCoordinates
     *            horizontal coordinates for each direction
     * @return balanced coordinates
     */
    private HashMap<Node, Integer> balance(HashMap<Node, Integer>[] xCoordinates) {

        HashMap<Node, Integer> coordinates = new HashMap<Node, Integer>();

        int minWidth = Integer.MAX_VALUE;
        int smallestWidthLayout = 0;
        int[] min = new int[4];
        int[] max = new int[4];

        // get the layout with smallest width and set minimum and maximum value
        // for each direction
        for (int i = 0; i <= 3; ++i) {
            min[i] = Integer.MAX_VALUE;
            max[i] = 0;
            for (Integer x : xCoordinates[i].values()) {
                if (x < min[i]) {
                    min[i] = x;
                }
                if (x > max[i]) {
                    max[i] = x;
                }
            }
            int width = max[i] - min[i];
            if (width < minWidth) {
                minWidth = width;
                smallestWidthLayout = i;
            }
        }

        // align the layouts to the one with smallest width
        for (int i = 0; i <= 3; ++i) {
            if (i != smallestWidthLayout) {

                // align the left to right layouts to the left border of the
                // smallest layout
                if (i == LEFT_UP || i == LEFT_DOWN) {
                    int diff = min[i] - min[smallestWidthLayout];
                    for (Node n : xCoordinates[i].keySet()) {
                        if (diff > 0) {
                            xCoordinates[i].put(n, xCoordinates[i].get(n)
                                    - diff);
                        } else {
                            xCoordinates[i].put(n, xCoordinates[i].get(n)
                                    + diff);
                        }
                    }

                    // align the right to left layouts to the right border of
                    // the smallest layout
                } else {
                    int diff = max[i] - max[smallestWidthLayout];
                    for (Node n : xCoordinates[i].keySet()) {
                        if (diff > 0) {
                            xCoordinates[i].put(n, xCoordinates[i].get(n)
                                    - diff);
                        } else {
                            xCoordinates[i].put(n, xCoordinates[i].get(n)
                                    + diff);
                        }
                    }
                }
            }
        }

        // get the minimum coordinate value
        int minValue = Integer.MAX_VALUE;
        for (int i = 0; i <= 3; ++i) {
            for (Integer x : xCoordinates[i].values()) {
                if (x < minValue) {
                    minValue = x;
                }
            }
        }

        // set left border to 0
        if (minValue != 0) {
            for (int i = 0; i <= 3; ++i) {
                for (Node n : xCoordinates[i].keySet()) {
                    xCoordinates[i].put(n, xCoordinates[i].get(n) - minValue);
                }
            }
        }

        // get the average median of each coordinate
        for (Node n : this.graph.getNodes()) {
            int[] values = new int[4];
            for (int i = 0; i < 4; i++) {
                values[i] = xCoordinates[i].get(n);
            }
            Arrays.sort(values);
            int average = (values[1] + values[2]) / 2;
            coordinates.put(n, average);
        }

        // get the minimum coordinate value
        minValue = Integer.MAX_VALUE;

        for (Integer x : coordinates.values()) {
            if (x < minValue) {
                minValue = x;
            }
        }

        // set left border to 0
        if (minValue != 0) {

            for (Node n : coordinates.keySet()) {
                coordinates.put(n, coordinates.get(n) - minValue);
            }

        }

        return coordinates;
    }

}
