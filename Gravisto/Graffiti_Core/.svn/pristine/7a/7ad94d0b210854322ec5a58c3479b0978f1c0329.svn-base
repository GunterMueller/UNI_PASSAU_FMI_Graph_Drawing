package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Class implements a version of SchnyderAlgorithm by Brehm, which calculates
 * the (left-most) canonical order and the realizer of a graph in one step.
 * 
 * @author hofmeier
 */
public class BrehmOneRealizer extends AbstractDrawingAlgorithm {
    /** Counts the common neighbors of every node with the third outer node */
    protected int[] neighborCounter = new int[this.graph.getNodes().size()];

    /** The neighbors of the third outer node */
    protected HashList<Node> neighbors = new HashList<Node>();

    /** Nodes that are already done */
    protected HashSet<Node> finishedNodes = new HashSet<Node>();

    /**
     * Creates a new instance of this class.
     * 
     * @param g
     *            the graph the algorithm is adapted on
     * @param m
     *            the maximum number of realizers (not used in here)
     */
    public BrehmOneRealizer(Graph g, int m) {
        super(g, m);
    }

    /**
     * Calculates the leftmost canonical order and the leftmost realizer of the
     * graph in one step. The canonical order is calculated the same way as in
     * Schnyders algorithm, but when removing a node x from the list of
     * neighbors, do the the following: - add all edges from x to already
     * finished nodes to the red tree of the realizer - add the edge from x to
     * its right neighbor in the neighbor list to the green tree of the realizer
     * - add the edge from x to its left neighbor in the neighbor list to the
     * blue tree of the realizer
     */
    protected Realizer createRealizer() {
        this.initialize();
        Realizer realizer = new Realizer(this);
        while (this.canonicalOrder.size() < (this.graph.getNodes().size() - 2)) {
            // Find the next node to remove and its left and its right neighbor
            Node toRemove = this.findLeftMostNode();
            Node pre = this.neighbors.getPredecessor(toRemove);
            Node next = this.neighbors.getNextNeighbor(toRemove);
            this.removeFromList(toRemove);
            this.canonicalOrder.add(toRemove);
            // Add the edge from the current Node to its left neighbor to the
            // blue tree
            if (!this.isOuterNode(pre) || !(toRemove == this.outerNodes[2])) {
                realizer.addBlue(toRemove, next);
            }
            // Add the edge from the current Node to its right neighbor to the
            // green tree
            if (!this.isOuterNode(next) || !(toRemove == this.outerNodes[2])) {
                realizer.addGreen(toRemove, pre);
            }

            // Add every edge from the current node to a finished node to the
            // red tree
            Node toAdd = this.adjacenceLists.get(toRemove).getPredecessor(pre);
            while (!toAdd.equals(next)) {
                if (!this.isOuterNode(toAdd)) {
                    this.addToList(toAdd);
                    this.neighbors.addBefore(next, toAdd);
                    realizer.addRed(toAdd, toRemove);
                }
                toAdd = this.adjacenceLists.get(toRemove).getPredecessor(toAdd);
            }
        }
        return realizer;
    }

    /**
     * Initializes the neighbors list and the list of finished nodes.
     */
    private void initialize() {
        this.finishedNodes.add(this.outerNodes[0]);
        this.finishedNodes.add(this.outerNodes[1]);
        this.neighbors.append(this.outerNodes[0]);
        this.neighbors.append(this.outerNodes[1]);
        this.neighbors.addAfter(this.outerNodes[0], this.outerNodes[2]);
        this.addToList(this.outerNodes[2]);
    }

    /**
     * Walks through the list of neighbors and finds the first one with two
     * common neighbors with the third outer node
     * 
     * @return the leftmost node to be "removed" from the graph.
     */
    private Node findLeftMostNode() {
        Node neighbor = this.neighbors.getPredecessor(this.outerNodes[1]);
        while (!neighbor.equals(this.outerNodes[1])) {
            if (this.neighborCounter[this.getIndex(neighbor)] == 2)
                return neighbor;
            neighbor = this.neighbors.getPredecessor(neighbor);
        }

        // Will not happen
        return null;
    }

    /**
     * Adds a new node to the list of neighbors and updates the neighbor
     * counter.
     * 
     * @param toAdd
     *            the node to add to the list of neighbors.
     */
    private void addToList(Node toAdd) {
        Iterator<Node> neighborsIt = this.neighbors.iterator();
        while (neighborsIt.hasNext()) {
            Node neighbor = neighborsIt.next();
            if ((this.graph.getEdges(toAdd, neighbor).size() > 0)) {
                if (!this.isOuterNode(neighbor)) {
                    this.neighborCounter[this.getIndex(neighbor)]++;
                }
                this.neighborCounter[this.getIndex(toAdd)]++;
            }
        }
    }

    /**
     * Removes a node from the list of neighbors and updates the neighbor
     * counter.
     * 
     * @param toRemove
     *            the node to add to the list of neighbors.
     */
    private void removeFromList(Node toRemove) {
        Iterator<Node> neighborsIt = this.neighbors.iterator();
        while (neighborsIt.hasNext()) {
            Node neighbor = neighborsIt.next();
            if ((this.graph.getEdges(toRemove, neighbor).size() > 0)
                    && (!this.isOuterNode(neighbor))) {
                this.neighborCounter[this.getIndex(neighbor)]--;
            }
        }
        this.neighborCounter[this.getIndex(toRemove)] = 0;
        this.finishedNodes.add(toRemove);
        this.neighbors.remove(toRemove);
    }

    /**
     * Helper method indicating if a given node is the first or the second outer
     * node. (CAUTION: Does not (and must not) check if it is the third outer
     * node).
     * 
     * @param n
     *            the node to check.
     */
    protected boolean isOuterNode(Node n) {
        return (n == this.outerNodes[0] || n == this.outerNodes[1]);
    }

    /**
     * Executes the algorithm by calculating a valid canonical order and
     * creating a realizer in one step.
     */
    @Override
    public void execute() {
        // Perform the algorithm
        Realizer realizer = this.createRealizer();

        this.realizers.add(realizer);
        this.barycentricReps.add(new BarycentricRepresentation(realizer,
                this.graph, this.outerNodes));
    }
}
