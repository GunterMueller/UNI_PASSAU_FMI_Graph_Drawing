package org.graffiti.plugins.algorithms.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Calculates x-coordinates. For details see Alg. 1, 2, and 3 of the following
 * publication:
 * 
 * U. Brandes, B. K�pf, Fast and Simple Horizontal Coordinate Assignment. In: P.
 * Mutzel, M. J�nger, and S. Leipert (Eds.): GD 2001, LNCS 2265, pp. 31�44,
 * 2002, Springer 2002.
 */
public class BrandesKoepfAlgorithm {

    /**
     * the graph
     */
    private Graph graph;

    /**
     * stores the current order of each level
     */
    private LinkedList<Node>[] order;

    /**
     * stores the dummy nodes for each level
     */
    private HashSet<Node> dummies;

    /**
     * stores marked segments
     */
    private HashSet<Edge> marked = new HashSet<Edge>();

    /**
     * the minimum horiontal distance between two vertices
     */
    private static final int MIN_DIST = 1;

    /**
     * stores the root of each node
     */
    private HashMap<Node, Node> root = new HashMap<Node, Node>();

    /**
     * stores the alignment of each node
     */
    private HashMap<Node, Node> align = new HashMap<Node, Node>();

    /**
     * stores the x-coordinate of each node
     */
    private HashMap<Node, Integer> x = new HashMap<Node, Integer>();

    /**
     * stores the sink of each node
     */
    private HashMap<Node, Node> sink = new HashMap<Node, Node>();

    /**
     * stores the shift of each node
     */
    private HashMap<Node, Integer> shift = new HashMap<Node, Integer>();

    /**
     * stores level number for each node
     */
    private HashMap<Node, Integer> level = new HashMap<Node, Integer>();

    /**
     * the constructor
     * 
     * @param graph
     *            the graph
     */
    public BrandesKoepfAlgorithm(Graph graph) {
        this.graph = graph;
    }

    /**
     * computes a x coordinate relative to its sink
     * 
     * @param order
     *            layout of the graph
     * @param dummies
     *            dummy nodes of the graph
     * @return relative x coordinates
     */
    public HashMap<Node, Integer> computeRelativeCoordinates(
            LinkedList<Node>[] order, HashSet<Node> dummies) {

        this.order = order;
        this.dummies = dummies;

        // initialize HashMap to store level numbers
        for (int i = 0; i < this.order.length; i++) {
            for (Node n : this.order[i]) {
                this.level.put(n, i);
            }
        }

        // mark type 1 conflicts
        markConflicts();

        // initialize data structures
        init();

        // determine edges, which should be vertically aligned
        verticalAlignment();

        // compute x coordinates for each node
        horizontalCompactation();

        return this.x;
    }

    /**
     * see Algorithm 1 of publication. mark type 1 conflicts - when an inner
     * segment crosses a non-inner segment; the difference: we have type 2
     * conflicts, but just mark type 1 conflicts and treat type 2 conflicts like
     * type 0 conflicts in the following algorithms
     */
    private void markConflicts() {

        this.marked.clear();

        // between first and last two levels, there are no inner segments
        for (int i = 1; i < this.order.length - 2; i++) {

            // run from left to right in current lower level
            for (int l1 = 0; l1 < this.order[i + 1].size(); l1++) {

                // get current node and its upper neighbours
                Node currentNode = this.order[i + 1].get(l1);
                LinkedList<Node> neighbours = getNeighbours(currentNode, i);

                // if existing - get incident dummy node
                Node incidentNode = null;
                for (Node n : neighbours) {
                    if (this.dummies.contains(n)) {
                        incidentNode = n;
                    }
                }

                // inner segment?
                if (this.dummies.contains(currentNode) && incidentNode != null) {

                    // get pos of upper node
                    int pos = this.order[i].indexOf(incidentNode);

                    // mark all edges left from lower node to right of upper
                    // node
                    for (int l = 0; l < l1; l++) {
                        Node leftNode = this.order[i + 1].get(l);
                        LinkedList<Node> neighboursOfLeft = getNeighbours(
                                leftNode, i);
                        for (Node n : neighboursOfLeft) {
                            int posN = this.order[i].indexOf(n);
                            if (posN > pos) {
                                if (!(this.dummies.contains(leftNode) && this.dummies
                                        .contains(n))) {
                                    for (Edge e : this.graph.getEdges(leftNode,
                                            n)) {
                                        this.marked.add(e);
                                    }
                                }
                            }
                        }
                    }

                    // mark all edges right from lower node to left of upper
                    // node
                    for (int r = l1; r < this.order[i + 1].size(); r++) {
                        Node rightNode = this.order[i + 1].get(r);
                        LinkedList<Node> neighboursOfRight = getNeighbours(
                                rightNode, i);
                        for (Node n : neighboursOfRight) {
                            int posN = this.order[i].indexOf(n);
                            if (pos > posN) {
                                if (!(this.dummies.contains(rightNode) && this.dummies
                                        .contains(n))) {
                                    for (Edge e : this.graph.getEdges(
                                            rightNode, n)) {
                                        this.marked.add(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * initialize data structures to store values
     */
    private void init() {

        // initialize root, align and sink with itsself
        // initialize shift with infinite
        // initialize x with undefined
        this.root = new HashMap<Node, Node>();
        this.align = new HashMap<Node, Node>();
        this.sink = new HashMap<Node, Node>();
        this.shift = new HashMap<Node, Integer>();
        this.x = new HashMap<Node, Integer>();
        for (Node n : this.graph.getNodes()) {
            this.root.put(n, n);
            this.align.put(n, n);
            this.sink.put(n, n);
            this.shift.put(n, Integer.MAX_VALUE);
            this.x.put(n, null);
        }
    }

    /**
     * See Algorithm 2 of publication: aligns edges to left median of upper
     * neighbours
     */
    private void verticalAlignment() {

        // run from start to end level
        for (int i = 1; i < this.order.length; i++) {

            // set barriere position to left end of the list
            int r = -1;

            // run from left to right
            for (int k = 0; k < this.order[i].size(); k++) {

                // get current node and its upper neighbours
                Node currentNode = this.order[i].get(k);
                LinkedList<Node> neighbours = getNeighbours(currentNode, i - 1);

                int d = neighbours.size();
                if (d > 0) {

                    // two medians: first take the left then the right
                    // one median: just take the one
                    for (int m = (d - 1) / 2; m <= d / 2; ++m) {

                        if (this.align.get(currentNode).equals(currentNode)) {

                            // get median and the edge e from currentNode to
                            // median
                            Node um = neighbours.get(m);
                            // System.out.println("um: " + um.getString(PATH));
                            for (Edge e : this.graph.getEdges(currentNode, um)) {

                                // get position of median
                                int pos = this.order[i - 1].indexOf(um);

                                // if e is not marked and position is bigger
                                // as barriere position - set cyclic align
                                // and root lists
                                if (!this.marked.contains(e) && r < pos) {
                                    this.align.put(um, currentNode);
                                    this.root.put(currentNode, this.root
                                            .get(um));
                                    this.align.put(currentNode, this.root
                                            .get(currentNode));

                                    // set barriere position
                                    r = pos;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * See Algorithm 3 of publication: compute relative x-coordinates for all
     * roots
     */
    private void horizontalCompactation() {

        for (Node v : this.graph.getNodes()) {

            // get root coordinates relative to sink
            if (this.root.get(v).equals(v)) {
                placeBlock(v);
            }
        }
    }

    /**
     * See Algorithm 3 of publication: computes a relative x-coordinate for the
     * current node and if necessary a shift to the next block
     * 
     * @param v
     *            current node
     */
    private void placeBlock(Node v) {

        // x-coordinate not set yet
        if (this.x.get(v) == null) {

            // initialize x-coordinate with 0
            this.x.put(v, 0);
            Node w = v;

            do {

                // get position of current node
                int level = this.level.get(w);
                int pos = this.order[level].indexOf(w);

                if (pos > 0) {

                    // get left neighbour and its root
                    Node neighbour = this.order[level].get(pos - 1);
                    Node u = this.root.get(neighbour);

                    // recursive method call
                    placeBlock(u);

                    // set sink
                    if (this.sink.get(v) == v) {
                        this.sink.put(v, this.sink.get(u));
                    }

                    // if sink was already set - compute the shift
                    // else compute x-coordinate
                    if (this.sink.get(v) != this.sink.get(u)) {
                        this.shift.put(this.sink.get(u), Math.min(this.shift
                                .get(this.sink.get(u)), this.x.get(v)
                                - this.x.get(u) - MIN_DIST));
                    } else {
                        this.x.put(v, Math.max(this.x.get(v), this.x.get(u)
                                + MIN_DIST));
                    }
                }

                // get node to align under w
                w = this.align.get(w);
            } while (w != v);
        }

    }

    /**
     * absolute coordinates - last step of horizontal compactation
     * 
     * @param coordinates
     * @return ablolute coordinates
     */
    public HashMap<Node, Integer> getAbsoluteCoordinates(
            HashMap<Node, Integer> coordinates) {

        for (Node v : this.graph.getNodes()) {

            coordinates.put(v, coordinates.get(this.root.get(v)));
            int currentShift = this.shift.get(this.sink.get(this.root.get(v)));
            if (currentShift < Integer.MAX_VALUE) {
                coordinates.put(v, coordinates.get(v) + currentShift);
            }
        }

        return coordinates;
    }

    /**
     * returns neighbours of the node in the upper/lower level
     * 
     * @param n
     *            current node
     * @param level
     *            current level
     * @return neighbours of upper/lower level
     */
    private LinkedList<Node> getNeighbours(Node n, int level) {

        // direction up - get neighbours from upper level
        // direction down - get neighbours from lower level
        LinkedList<Node> neighbours = new LinkedList<Node>();
        for (Node u : this.order[level]) {
            if (this.graph.getEdges(n, u).size() != 0) {
                neighbours.addLast(u);
            }
        }

        return neighbours;
    }

}
