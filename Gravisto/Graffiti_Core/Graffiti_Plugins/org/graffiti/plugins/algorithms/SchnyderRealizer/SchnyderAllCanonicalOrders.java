package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Class implements a primitive algorithm to calculate all valid canonical
 * orders (via the "edge contraction"-method). This is done by calcualting one
 * canonical order, then tracking back to a point of the calculation where
 * different nodes could have been chosen. By repeating this for all
 * possibilities a tree is constrcuted that contains every possible canonical
 * order. For details on the "edge contraction"-method see
 * <code>SchnyderOneRealizer</code>.
 * 
 * @author hofmeier
 */
public class SchnyderAllCanonicalOrders extends SchnyderOneRealizer {
    /** Saves all found canonical orders */
    protected LinkedList<LinkedList<Node>> orders = new LinkedList<LinkedList<Node>>();

    /**
     * Counts the number of common neighbors between the first outer node and
     * every other node
     */
    protected int[] neighborCounter;

    /** Saves nodes that are already "removed" from the graph */
    protected HashSet<Node> finishedNodes = new HashSet<Node>();

    /** All neighbors of the first outer node */
    protected HashList<Node> neighbors = new HashList<Node>();

    /**
     * All neighbors of the first outer node, that have exactly two common
     * neighbors
     */
    protected HashList<Node> possibleToRemove = new HashList<Node>();

    /** The current position in the tree of canonical orders */
    protected ECNode position;

    /**
     * The depth of the current position. Necessary to know if the position is a
     * leave and a new canonical order has to be saved.
     */
    protected int depthOfPosition = 0;

    /** The logger to inform and warn the user */
    private static final Logger logger = Logger
            .getLogger(SchnyderAllCanonicalOrders.class.getName());

    /**
     * Creates a new instance of the class.
     * 
     * @param g
     *            the graph to be drawn.
     * @param m
     *            the maximum number of realizers (used for canonical orders in
     *            here).
     */
    public SchnyderAllCanonicalOrders(Graph g, int m) {
        super(g, m);
    }

    /**
     * Method performs the creation of the tree of all canonical orders as
     * desribed above.
     */
    protected void createEdgeContractionTree() {
        this.neighborCounter = new int[this.graph.getNodes().size()];
        this.position = new ECNode(this.outerNodes[0], null);
        stepDown();
        while (true) {
            // Do not calculate further canonical orders if the there are
            // more than a given value already
            if (this.orders.size() > this.maxNumberOfRealizers) {
                logger.fine(AbstractDrawingAlgorithm.MAX_MESSAGE);
                return;
            }
            // Chose a node that can be removed
            Node next = possibleToRemove.getFirst();
            boolean up = false;
            // If there is no one step up one level in the tree...
            if (next == null) {
                up = true;
            }
            // ...otherwise check if this path in the tree has already been
            // calculated...
            else {
                // .. if yes, chose another node that can be removed
                while (position.children.contains(next)) {
                    next = possibleToRemove.getNextNeighbor(next);
                    // if all nodes that can be removed were already calculated
                    // before, step up.
                    if (next == possibleToRemove.getFirst()) {
                        up = true;
                        break;
                    }
                }
            }
            if (up) {
                // If stepping up at the root, the algorithm is finished.
                if (position.isRoot()) {
                    break;
                }
                // If stepping up at the maximum depth, save the found caonical
                // order
                if (depthOfPosition == (this.graph.getNodes().size() - 3)) {
                    createOrder(position);
                    if (this.orders.size() + 1 >= this.maxNumberOfRealizers) {
                        logger.finest(AbstractDrawingAlgorithm.MAX_MESSAGE);
                        return;
                    }
                }
                stepUp();
                this.position = this.position.father;
                depthOfPosition--;

            } else {
                ECNode nextNode = new ECNode(next, position);
                this.position.children.add(next);
                this.position = nextNode;
                depthOfPosition++;
                stepDown();
            }
        }
    }

    /**
     * Performs the process of stepping down one level in the tree of canonical
     * orders.
     */
    protected void stepDown() {
        // Remove the current node (saved in position) from the graph
        // (not reallyremove it but simulate it)
        this.removeFromList(this.position.wrapped);
        finishedNodes.add(this.position.wrapped);
        Iterator<Node> ait = this.adjacenceLists.get(this.position.wrapped)
                .iterator();
        // For each neighbor of the removed node that is not already a neighbor
        // of the first outer node add it to the neighbors
        while (ait.hasNext()) {
            Node n = ait.next();
            if (!finishedNodes.contains(n) && !neighbors.contains(n)) {
                position.appendedNeighbors.add(n);
                addToList(n);
            }
        }
    }

    /**
     * Performs the process of stepping up one level in the tree of canonical
     * orders.
     */
    protected void stepUp() {
        // Readd the current node (saved in position) to the neighbors and...
        finishedNodes.remove(this.position.wrapped);
        Iterator<Node> it = this.position.appendedNeighbors.iterator();
        // ...for each neighbor that was appended to the neighbor list by
        // removing the current node (remove it from the list of neighbors
        while (it.hasNext()) {
            Node n = it.next();
            removeFromList(n);
        }
        this.addToList(this.position.wrapped);
    }

    /**
     * Removes a node from the list of neighbors and updates the neighbour
     * counter.
     * 
     * @param currentNode
     *            the node to be removed
     */
    protected void removeFromList(Node currentNode) {
        this.neighbors.remove(currentNode);
        this.possibleToRemove.remove(currentNode);
        this.finishedNodes.remove(currentNode);
        HashList<Node> currentNeighbors = this.adjacenceLists.get(currentNode);
        Iterator<Node> nit = this.neighbors.iterator();
        // For each neighbor of the first outer node ...
        while (nit.hasNext()) {
            Node neighbor = nit.next();

            // ... if it is a neighbor of the removed node, decrement its
            // neighbor counter by one.
            if (currentNeighbors.contains(neighbor)) {
                this.neighborCounter[getIndex(currentNode)]--;
                ;
                this.neighborCounter[getIndex(neighbor)]--;
                // If the neighbor counter is set to 2, mark it as removable.
                if ((this.neighborCounter[getIndex(neighbor)] == 2)
                        && (!isOuterNode(neighbor))) {
                    this.possibleToRemove.append(neighbor);
                }
                // If the neighbor counter is set to 1, mark it as not
                // removable.
                if (this.neighborCounter[getIndex(neighbor)] == 1) {
                    this.possibleToRemove.remove(neighbor);
                }
                // Do the same for the current node.
                if ((this.neighborCounter[getIndex(currentNode)] == 2)
                        && (!isOuterNode(currentNode))) {
                    this.possibleToRemove.append(currentNode);
                }
                if (this.neighborCounter[getIndex(currentNode)] == 1) {
                    this.possibleToRemove.remove(currentNode);
                }
            }
        }
    }

    /**
     * Adds a node to the list of neighbors and updates the neighbour counter.
     * 
     * @param currentNode
     *            the node to be added
     */
    protected void addToList(Node currentNode) {
        this.neighbors.append(currentNode);
        HashList<Node> currentNeighbors = this.adjacenceLists.get(currentNode);
        Iterator<Node> nit = this.neighbors.iterator();
        int numOfNeighbors = 0;
        // For each neighbor of the first outer node...
        while (nit.hasNext()) {
            Node neighbor = nit.next();
            // .. if it is also a neighbor of the added node increase
            // its neighbor counter by one.
            if (currentNeighbors.contains(neighbor)) {
                this.neighborCounter[getIndex(neighbor)]++;
                numOfNeighbors++;
                // If the neighbor counter is set to 2, mark it as removable.
                if ((this.neighborCounter[getIndex(neighbor)] == 2)
                        && (!isOuterNode(neighbor))) {
                    this.possibleToRemove.append(neighbor);
                }
                // If the neighbor counter is set to 3, mark it as not
                // removable.
                if (this.neighborCounter[getIndex(neighbor)] == 3) {
                    this.possibleToRemove.remove(neighbor);
                }
            }
        }
        // Set the neighbor counter of the added node to the number of
        // neighbors found above.
        this.neighborCounter[getIndex(currentNode)] = numOfNeighbors;
        if ((numOfNeighbors == 2) && (!isOuterNode(currentNode))) {
            this.possibleToRemove.append(currentNode);
        }
    }

    /**
     * Creates a canonical order by starting at a leaf and saving all its
     * ancestors in the tree up to the root.
     * 
     * @param leaf
     *            the leaf to start with
     */
    protected void createOrder(ECNode leaf) {
        LinkedList<Node> order = new LinkedList<Node>();
        while (leaf != null) {
            order.addFirst(leaf.wrapped);
            leaf = leaf.father;
        }
        orders.add(order);
    }

    /**
     * Executes the algorithm by creating the edge contraction tree as described
     * above and writing the realizers to the result list.
     */
    @Override
    public void execute() {
        this.createEdgeContractionTree();
        for (int i = 0; i < this.orders.size(); i++) {
            this.canonicalOrder = this.orders.get(i);
            this.enumerateAngles();
            Realizer realizer = this.createRealizer();
            this.realizers.add(realizer);
            this.barycentricReps.add(new BarycentricRepresentation(realizer,
                    this.graph, this.outerNodes));

        }
    }

    /**
     * This class represents a node in the tree of canonical order.
     * 
     * @author hofmeier
     */
    protected class ECNode {
        /** Pointer to the Gravisto node */
        protected Node wrapped;

        /** The direct ancestor in the tree */
        protected ECNode father;

        /**
         * The nodes that were added to the list of neighbors of the first outer
         * node, when the wrapped node was removed from this list
         */
        protected LinkedList<Node> appendedNeighbors = new LinkedList<Node>();

        /** Saves paths that were already calculated in the tree */
        protected HashSet<Node> children = new HashSet<Node>();

        /**
         * Creates a new node in the tree
         * 
         * @param w
         *            the wrapped node
         * @param f
         *            the direct ancestor in the tree
         */
        public ECNode(Node w, ECNode f) {
            this.wrapped = w;
            this.father = f;
        }

        /**
         * Helper method that indicates if this node is the root of the tree
         * 
         * @return true if this node is the root
         */
        public boolean isRoot() {
            return (this.father == null);
        }
    }
}
