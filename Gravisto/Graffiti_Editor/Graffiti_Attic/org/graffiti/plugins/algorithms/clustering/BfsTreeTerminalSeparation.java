/*
 * Created on 13.10.2004
 */

package org.graffiti.plugins.algorithms.clustering;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.util.Queue;

/**
 * This is a subalgorithm for the <code> GomoryHuTreeTerminalSeparationAlgorithm
 * </code>. In solves the
 * TerminalSeparation-Problem for tree-networks in O(|V|) which is much better
 * than every Algorithm, approximating or solving this problem on common graphs.
 * In a bottom-up manner in climbs from the leaves up to the root, alway
 * collecting the cheapest edges on the walked paths. When reaching terminals or
 * crossings, where more paths arrive at the same node, it calculates the cut
 * edges and stores them. Afterward it also computes the clusters and the
 * cutsize.
 * 
 * @author Markus K�ser
 * @version $Revision 1.1 $
 */

public class BfsTreeTerminalSeparation {
    /**
     * @author Markus K�ser
     * @version $Revision 1.0 $
     * 
     *          This inner class stands for a path in the tree. At the start of
     *          the algorithm, paths begin at the leafes and climb up, following
     *          the bfs- numbers. When reaching terminals, those are explicitly
     *          stored. Paths can be temporarily locked, when they reach an open
     *          crossing and unlocked when the crossing is finished. If they are
     *          terminated, they are no longer needed for the algorithm.
     */
    private class Path {

        // it would be working, if only the last node was stored
        /** The nodes found by the path ordered by the time of their finding. */
        private LinkedList elements;

        /** The first terminal found in this path. */
        private Node firstTerminalInPath;

        /** The second terminal found in this path. */
        private Node secondTerminalInPath;

        /** The cheapest edge encountered on the path. */
        private Edge cheapest;

        /**
         * Flag that determines if the path is terminated. Terminated paths will
         * be deleted.
         */
        private boolean terminated;

        /**
         * Flag that determines if the path is temporary locked. Locked paths
         * can not be walked further until they are unlocked again.
         */
        private boolean locked;

        /** Forbidden constructor. */
        private Path() {
        }

        /**
         * Constructs a new path beginning at the given node.
         * 
         * @param firstNode
         *            the first node of the path
         */
        Path(Node firstNode) {
            elements = new LinkedList();
            elements.addLast(firstNode);
            if (isTerminal(firstNode)) {
                firstTerminalInPath = firstNode;
            } else {
                firstTerminalInPath = null;
            }
            secondTerminalInPath = null;
            cheapest = null;
            terminated = false;
            locked = false;
        }

        /**
         * Returns an <code>Iterator</code> to the elements of this path,
         * beginning at the first encountered element and ending at the last.
         * 
         * @return an iterator
         */
        Iterator iterator() {
            return elements.iterator();
        }

        /**
         * Adds a terminal to the path.
         * 
         * @param terminal
         *            the terminal
         */
        private void addTerminalInPath(Node terminal) {
            if (firstTerminalInPath == null) {
                firstTerminalInPath = terminal;
            } else {
                secondTerminalInPath = terminal;
            }
        }

        /**
         * Checks if there are two terminals stored in this path.
         * 
         * @return true if there are two terminals in this path, false otherwise
         */
        boolean containsTwoTerminals() {
            return (firstTerminalInPath != null && secondTerminalInPath != null);
        }

        /**
         * Checks, if the path contains a terminal, which is not the last node
         * in the path.
         * 
         * @return true, if it is, false otherwise
         */
        boolean containsNoTerminalNotBeeingTheLastNode() {
            return ((firstTerminalInPath == null) || (firstTerminalInPath == ((Node) elements
                    .getLast())));

        }

        /**
         * Sets the cheapest edge of the path to the given edge.
         * 
         * @param edge
         *            the new cheapest edge
         */
        private void setCheapestEdge(Edge edge) {
            cheapest = edge;
        }

        /**
         * Returns the last node of this path.
         * 
         * @return the last node.
         */
        Node getLast() {
            return (Node) elements.getLast();
        }

        /**
         * Checks if the last node in the path is a crossing or a terminal.
         * 
         * @return truem if it is, false otherwise
         */
        boolean foundCrossingOrTerminal() {
            return (foundCrossing() || foundTerminal());
        }

        /**
         * Checks if the last node in the path is a crossing.
         * 
         * @return truem if it is, false otherwise
         */
        boolean foundCrossing() {
            return (getLast().getEdges().size() > 2);
        }

        /**
         * Checks if the last node in the path is a terminal.
         * 
         * @return truem if it is, false otherwise
         */
        boolean foundTerminal() {
            return (isTerminal(getLast()));
        }

        /**
         * Adds a new node to the path and checks if the edge is a new cheapest
         * edge.
         * 
         * @param newEdge
         *            the possible new cheapest edge
         * @param newNode
         *            the new node of the path
         */
        void addTo(Edge newEdge, Node newNode) {
            elements.addLast(newNode);
            refreshCheapestEdge(newEdge);
            if (isTerminal(newNode)) {
                addTerminalInPath(newNode);
            }
        }

        /**
         * Checks if the path is terminated.
         * 
         * @return true if the path is terminated, false otherwise
         */
        boolean isTerminated() {
            return terminated;
        }

        /**
         * Terminates a path.
         */
        void terminate() {
            terminated = true;
            lock();
        }

        /**
         * Locks a path.
         */
        void lock() {
            locked = true;
        }

        /**
         * Unlocks a path.
         */
        void unlock() {
            locked = false;
        }

        /**
         * Checks if a path is locked.
         * 
         * @return true if the path is locked, false otherwise.
         */
        boolean isLocked() {
            return locked;
        }

        /**
         * Finishes a path and resets it. The cheapest edge will be stored in
         * the cut and the object can be used for a new path in the graph.
         */
        void finishAndReset() {
            // finish
            cut.add(cheapest);

            // reseting all data
            reset();
        }

        /**
         * finishes a path and terminates it. The cheapest edge will be stored
         * in the cut, then the object will no longer be needed.
         */
        void finishAndTerminate() {
            // finish
            cut.add(cheapest);

            terminate();
        }

        /**
         * Resets the object. The only element, that is not deleted is the last
         * found terminal.
         */
        private void reset() {
            elements.clear();
            if (containsTwoTerminals()) {
                elements.addLast(secondTerminalInPath);
                firstTerminalInPath = secondTerminalInPath;
            } else {
                // there is only one terminal in the path
                elements.addLast(firstTerminalInPath);
            }
            secondTerminalInPath = null;
            setCheapestEdge(null);
        }

        /**
         * Checks if the new edge is cheaper than the stored cheapest edge. If
         * it is, the new edge takes the place as cheapest edge.
         * 
         * @param newEdge
         *            the edge to be tested as cheapest edge.
         */
        private void refreshCheapestEdge(Edge newEdge) {
            if (cheapest == null) {
                setCheapestEdge(newEdge);
            } else {
                double oldCap = nsa.getCapacity(cheapest);
                double newCap = nsa.getCapacity(newEdge);
                if (newCap < oldCap) {
                    setCheapestEdge(newEdge);
                }
            }
        }

        /**
         * Returns the cheapest edge.
         * 
         * @return the cheapest edge
         */
        Edge getCheapestEdge() {
            return cheapest;
        }

        /**
         * Returns the capacity value of the cheapest edge.
         * 
         * @return Capacity of cheapest edge.
         */
        double getCheapestEdgeValue() {
            return nsa.getCapacity(cheapest);
        }
    };

    /**
     * @author Markus K�ser
     * @version $Revision 1.0 $
     * 
     *          Crossing objects stand for open crossings in the tree, that
     *          means nodes with a degree > 2 that are not terminals. At these
     *          points the paths have to be locked until all paths have arrived.
     *          Then all but the most expensive path will be finished and
     *          terminated. The remainig path will be unlocked again.
     */
    private class Crossing {

        /** The crossing node. */
        private Node crossing;

        /** The list of paths, that have arrived at this crossing. */
        private LinkedList pathsToCrossing;

        /** Forbidden constructor. */
        private Crossing() {
        }

        /**
         * Constructs a new instance of a crossing.
         * 
         * @param node
         *            the crossing node
         * @param path
         *            the first path arriving at this crossing
         */
        Crossing(Node node, Path path) {
            this.crossing = node;
            pathsToCrossing = new LinkedList();
            pathsToCrossing.add(path);
        }

        /**
         * Returns the crossing node.
         * 
         * @return the crossing node
         */
        Node getNode() {
            return crossing;
        }

        /**
         * Adds the given path to the list of arrived paths.
         * 
         * @param path
         *            the new path
         */
        void addPath(Path path) {
            pathsToCrossing.add(path);
        }

        /**
         * Returns all paths leading to this crossing.
         * 
         * @return all paths
         */
        LinkedList getPathsToCrossing() {
            return pathsToCrossing;
        }

        /**
         * Checks if all possible paths have arrived at this crossing yet.
         * 
         * @return true if all paths have arrived, false otherwise
         */
        boolean isFoundByAllPaths() {
            return (findNrOfWaysDown() <= pathsToCrossing.size());
        }

        /**
         * Finds the possilbe number of ways leading downward from the crossing
         * node.
         * 
         * @return the number of ways
         */
        private int findNrOfWaysDown() {
            int numberOfWays = 0;

            int bfs = getBfsNumber(crossing);
            Node target;
            int targetBfs;
            // counts the number of neighbours with greater bfs number
            for (Iterator it = crossing.getNeighborsIterator(); it.hasNext();) {
                target = (Node) it.next();
                targetBfs = getBfsNumber(target);
                if (targetBfs > bfs) {
                    numberOfWays++;
                }
            }
            return numberOfWays;
        }
    };

    /**
     * The path for the storage of the marked flag on nodes and edges. Marked
     * Graphelements have been found before by a path during the algorithm.
     */
    private static final String MARKED = ClusteringSupportAlgorithms.BASE
            + "WalkMarked";

    /** The path for the storage of the terminal number on the terminal nodes. */
    private static final String TERMINAL_NUMBER = ClusteringSupportAlgorithms.BASE
            + "numberOfTerminals";

    /** The path for the storage of the bfs number on the nodes */
    private static final String BFS_NUMBER = ClusteringSupportAlgorithms.BASE
            + "BfsTreeTerminalSeparationBfsNummer";

    /** error message */
    private static final String ALGORITHM_NOT_RUN_ERROR = "The algorithm has"
            + " to be executed before the results can be obtained.";

    /** error message */
    private static final String UP_EDGE_IS_NULL_ERROR = "No edge with a "
            + "lesser bfs value could be computed";

    /** the singleton object of <code>FlowNetworkSupportAlgorithms </code> */
    private FlowNetworkSupportAlgorithms nsa = FlowNetworkSupportAlgorithms
            .getFlowNetworkSupportAlgorithms();

    /** the singleton object of <code>ClusteringSupportAlgorithms </code> */
    private ClusteringSupportAlgorithms csa = ClusteringSupportAlgorithms
            .getClusteringSupportAlgorithms();

    /** The tree, the whole algorithm works on. */
    private Graph tree;

    /** The terminals to be separated by the algorithm. */
    private Node[] terminals;

    /** Flag that determines if the algorithm has already been executed. */
    private boolean algorithmRun;

    /** The list of the still open paths, that can possilby be walked further. */
    private LinkedList openPaths;

    /** The list of open crossings, that still have to be handled. */
    private LinkedList openCrossings;

    /** The mapping from crossing nodes to their crossing objects. */
    private HashMap nodesToCrossings;

    /** The terminal separation cut to be calculated by this algorithm */
    private Collection cut;

    /** The clusters to be calculated by this algorithm */
    private Collection[] clusters;

    /** The cutsize to be calculated by this algorithm */
    private double cutSize;

    /** Forbidden constructor */
    private BfsTreeTerminalSeparation() {
    }

    /**
     * Constructs a new instance of the <code>BfsTreeTerminalSeparation</code>.
     * 
     * @param tree
     *            the tree, the algorithm works on
     * @param terminals
     *            the terminals to be separated
     */
    public BfsTreeTerminalSeparation(Graph tree, Node[] terminals) {
        reset(tree, terminals);
    }

    /**
     * Resets the data of the algorithm.
     * 
     * @param theTree
     *            the new tree
     * @param theTerminals
     *            the new terminals
     * 
     */
    public void reset(Graph theTree, Node[] theTerminals) {
        tree = theTree;
        terminals = theTerminals;
        algorithmRun = false;
        setTerminalNumbers();
        // start bfs on the first terminal
        bfs(terminals[0]);
        constructLeafPaths(findLeavesOfTree());
        openCrossings = new LinkedList();
        nodesToCrossings = new HashMap();
        cut = new LinkedList();
        clusters = null;
        cutSize = 0.0;
    }

    /**
     * Executes the algorithm and computes the terminal-separation cut, the
     * clusters and the cutsize.
     */
    public void execute() {
        try {
            algorithm();
            postAlgorithm();
        } catch (Exception e) {
            removeAllTemporaryData();
            // reset();
        }
    }

    /**
     * Computations to be done after the run of the algorithm.
     */
    private void postAlgorithm() {
        clusters = csa.computeClustersFromCut(tree, cut);
        cutSize = csa.computeMultiwayCutSize(cut);
        algorithmRun = true;
        // csa.colorClustersAndCutEdges(clusters,cut);
        removeAllTemporaryData();
    }

    /**
     * The main algorithm.
     */
    private void algorithm() {
        Path path;
        Iterator pathIt;
        Crossing crossing;
        Iterator crossIt;

        // while not both the open paths and the open crossings are empty
        while (!(openPaths.isEmpty() && openCrossings.isEmpty())) {
            pathIt = openPaths.iterator();
            while (pathIt.hasNext()) {
                path = (Path) pathIt.next();
                if (!path.isLocked()) {
                    walkPath(path);
                }
                if (path.isTerminated()) {
                    pathIt.remove();
                }
            }

            boolean walked;
            crossIt = openCrossings.iterator();
            while (crossIt.hasNext()) {
                crossing = (Crossing) crossIt.next();

                walked = walkOpenCrossing(crossing);
                if (walked) {
                    crossIt.remove();
                }
            }
        }

        removeAllTemporaryData();
    }

    /**
     * Walks a path, until a open crossing is found. Every node on the way
     * except the crossing node are added to the path in the order of their
     * finding. If a terminal is found on the way the path is reseted. On a
     * second terminal on the way, the path ist finished and reseted. Crossings
     * on terminal nodes are non-open crossings. Only the first path arriving
     * here can walk further. All paths leading to the crossing will be either
     * reseted or finished and reseted depending on the number of terminals
     * found on the way. If paths lead to open crossings, they will bei locked
     * until the open crossing is handled.
     * 
     * @param path
     *            the path.
     */
    private void walkPath(Path path) {
        boolean stopWalking = false;
        Node lastPathNode = path.getLast();
        Edge nextEdge;
        Node nextNode;

        while (!stopWalking) {
            // getting upper node and edge
            nextEdge = findUpwardEdge(lastPathNode);
            nextNode = nsa.getOtherEdgeNode(lastPathNode, nextEdge);

            // add next node in any case (store it, refresh cheapest edge)
            path.addTo(nextEdge, nextNode);

            if (path.foundCrossingOrTerminal()) {
                stopWalking = handleCrossingsAndTerminals(path);
            }

            markAsFound(nextEdge, nextNode);

            if (!stopWalking) {
                lastPathNode = nextNode;
            }
        }
    }

    /**
     * Finds the leaves of the tree.
     * 
     * @return the leaves.
     */
    private Node[] findLeavesOfTree() {
        Collection leaves = new LinkedList();

        int leavesNr = 0;

        Node node;
        for (Iterator it = tree.getNodesIterator(); it.hasNext();) {
            node = (Node) it.next();
            // If the node has only one neighbour, it is a leaf.
            // the root of the bfs will NOT be added
            if (node.getNeighbors().size() == 1 && getBfsNumber(node) > 0) {
                leavesNr++;
                leaves.add(node);
            }
        }
        return (Node[]) leaves.toArray(new Node[0]);
    }

    /**
     * Constructs trivial paths out of the leaves of the tree, each path only
     * containing the leaf itself.
     * 
     * @param leaves
     *            the leaves of the tree
     */
    private void constructLeafPaths(Node[] leaves) {
        openPaths = new LinkedList();
        Node leaf;
        for (int i = 0; i < leaves.length; i++) {
            leaf = leaves[i];
            openPaths.add(new Path(leaf));
        }
    }

    /**
     * Finds the upward edge from a given node using the bfs-numbers.
     * 
     * @param node
     *            the node
     * @return the upward edge
     */
    private Edge findUpwardEdge(Node node) {
        Edge result = null;
        Edge edge;
        Node targetNode;
        int bfsNumber = getBfsNumber(node);
        int otherNodeBfsNumber;

        Collection edges = node.getEdges();
        for (Iterator it = edges.iterator(); it.hasNext();) {
            edge = (Edge) it.next();
            targetNode = nsa.getOtherEdgeNode(node, edge);
            otherNodeBfsNumber = getBfsNumber(targetNode);
            if (otherNodeBfsNumber < bfsNumber) {
                result = edge;
                break;
            }
        }
        if (result == null)
            throw new ClusteringException(UP_EDGE_IS_NULL_ERROR);
        return result;
    }

    /**
     * Handles terminals and crossings on terminals while walking a path. If it
     * finds an open crossing it generates a crossing object and places it in
     * the corresponding list. In this case the walk up will be stopped
     * 
     * @param path
     *            the path, on which crossings and terminals should be handled
     * @return true if the walking of the path was stopped, false otherwise
     */
    private boolean handleCrossingsAndTerminals(Path path) {
        boolean stopWalking = false;
        Node lastNode = path.getLast();
        if (path.foundCrossing()) {
            if (path.foundTerminal()) {
                if (path.containsTwoTerminals()) {
                    // if crossNode is the second terminal in the path
                    path.finishAndReset();
                } else {
                    // if crossNode is the first terminal in the path
                    path.reset();
                }
                if (isMarkedAsFound(lastNode)) {
                    // terminated the path forever, because another path walks
                    // up
                    // further
                    path.terminate();
                    stopWalking = true;
                }
            } else {
                // if the crossing is not a terminal. It is not clear, which
                // path
                // can walk further -> temporary stop path.
                stopWalking = true;
                path.lock();
                Crossing crossing;

                // store this path at the crossig, perhaps create one first
                if (!isMarkedAsFound(lastNode)) {
                    // create new open crossing and add the actual path
                    crossing = new Crossing(lastNode, path);
                    // put the crossing in the crossing list
                    openCrossings.addLast(crossing);
                    // store the link between the crossing node and the crossing
                    nodesToCrossings.put(lastNode, crossing);
                } else {
                    // take the crossing out of the HashMap
                    crossing = (Crossing) nodesToCrossings.get(lastNode);
                    // put the new path in
                    crossing.addPath(path);
                }
            }
        } else {
            // found no crossing
            if (path.containsTwoTerminals()) {
                // if crossNode is the second terminal in the path
                path.finishAndReset();
            } else {
                // if crossNode is the first terminal in the path, reset it
                path.reset();
            }
        }
        // if the actual terminal is the last node
        if (getBfsNumber(lastNode) == 0) {
            path.terminate();
            stopWalking = true;
        }
        return stopWalking;
    }

    /**
     * Given a list of paths, this method computes the one with the most
     * expensive cheapest edge.
     * 
     * @param paths
     *            the list of paths
     * @return the path with the most expensive cheapest edge
     */
    private Path getMostExpensivePath(LinkedList paths) {
        Path tempPath;
        double tempValue;
        Path mostExpensivePath = null;
        double mostExpensiveValue = -1.0;
        for (Iterator it = paths.iterator(); it.hasNext();) {
            tempPath = (Path) it.next();
            tempValue = tempPath.getCheapestEdgeValue();
            if (tempValue > mostExpensiveValue) {
                mostExpensiveValue = tempValue;
                mostExpensivePath = tempPath;
            }
        }
        return mostExpensivePath;
    }

    /**
     * Handles an open crossing by finding the most expensive path containing at
     * least one terminal and unlocking it, while the other paths will all be
     * finished and terminated. If there is no path with at least one terminal,
     * a random of the paths will be chosen to be unlocked. If the crossing
     * could not be handled, because it was not reached by all possible paths,
     * nothing happens here.
     * 
     * @param crossing
     *            the crossing to be walked
     * @return true if the crossing was handled, false if not all paths had
     *         reached the crossing
     */
    private boolean walkOpenCrossing(Crossing crossing) {
        boolean isWalked;
        if (crossing.isFoundByAllPaths()) {
            LinkedList pathsToCrossing = filterNonTerminalPaths(crossing
                    .getPathsToCrossing());

            // get the most expensive of the cheapest edges from the paths
            Path mostExpensivePath = getMostExpensivePath(pathsToCrossing);

            finishAllButOnePath(pathsToCrossing, mostExpensivePath);
            mostExpensivePath.unlock();

            isWalked = true;
        } else {
            isWalked = false;
        }
        return isWalked;
    }

    /**
     * Filters out all paths without terminal, except if there is no path with a
     * terminal. Then it lets the last found path in.
     * 
     * @param pathsToCrossing
     * @return DOCUMENT ME!
     */
    private LinkedList filterNonTerminalPaths(LinkedList pathsToCrossing) {
        Path path = null;
        LinkedList filteredPaths = new LinkedList();
        for (Iterator it = pathsToCrossing.iterator(); it.hasNext();) {
            path = (Path) it.next();
            if (path.containsNoTerminalNotBeeingTheLastNode()) {
                filteredPaths.add(path);
                it.remove();
            }
        }
        // if no path to this crossing contains such a terminal => take any one
        // of them
        if (pathsToCrossing.isEmpty()) {
            pathsToCrossing.add(filteredPaths.removeLast());
        }
        // then terminate all filtered paths
        for (Iterator it = filteredPaths.iterator(); it.hasNext();) {
            path = (Path) it.next();
            path.terminate();
        }
        return pathsToCrossing;
    }

    /**
     * Finishes and terminates all paths in the paths list except the keptPath.
     * 
     * @param paths
     * @param keptPath
     */
    private void finishAllButOnePath(LinkedList paths, Path keptPath) {
        Path path;
        for (Iterator it = paths.iterator(); it.hasNext();) {
            path = (Path) it.next();
            if (path != keptPath) {
                path.finishAndTerminate();
            }
        }
    }

    /**
     * Removes the marks from all nodes and edges of the tree.
     */
    private void removeMark() {
        GraphElement e;
        for (Iterator it = tree.getNodesIterator(); it.hasNext();) {
            e = (Node) it.next();
            removeMark(e);
        }
        for (Iterator it = tree.getEdgesIterator(); it.hasNext();) {
            e = (Edge) it.next();
            removeMark(e);
        }
    }

    /**
     * Checks if a given node or edge is marked to be found by the algorithm.
     * 
     * @param e
     *            the node or edge
     * @return true if it is marked, false otherwise
     */
    private boolean isMarkedAsFound(GraphElement e) {
        boolean marked = false;
        try {
            marked = e.getBoolean(MARKED);
        } catch (AttributeNotFoundException anfe) {
        }
        return marked;
    }

    /**
     * Marks a given node or edge to be found by the algorithm.
     * 
     * @param e
     *            the given node or edge
     */
    private void markAsFound(GraphElement e) {
        removeMark(e);
        e.setBoolean(MARKED, true);
    }

    /**
     * Marks a pair of node and edge to be marked as found by the algorithm.
     * 
     * @param e
     *            the edge
     * @param n
     *            the node
     */
    private void markAsFound(Edge e, Node n) {
        markAsFound(e);
        markAsFound(n);
    }

    /**
     * Removes the mark of a given node or edge.
     * 
     * @param e
     *            the node or edge
     */
    private void removeMark(GraphElement e) {
        try {
            e.removeAttribute(MARKED);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Removes the terminal numbers from the terminal nodes.
     */
    private void removeTerminalNumbers() {
        for (int i = 0; i < terminals.length; i++) {
            removeTerminalNumber(terminals[i]);
        }
    }

    /**
     * Removes the terminal number of a given terminal.
     * 
     * @param terminal
     *            the terminal
     */
    private void removeTerminalNumber(Node terminal) {
        try {
            terminal.removeAttribute(TERMINAL_NUMBER);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Returns the terminal number of a given node, or -1 if the node is no
     * terminal
     * 
     * @param node
     *            the node
     * @return the terminal number
     */
    private int getTerminalNumber(Node node) {
        int number = -1;
        try {
            number = node.getInteger(TERMINAL_NUMBER);
        } catch (AttributeNotFoundException anfe) {
        }
        return number;
    }

    /**
     * Tests if a given node is a terminal.
     * 
     * @param node
     *            the node
     * @return true, if a node is a terminal, false otherwise
     */
    private boolean isTerminal(Node node) {
        return (getTerminalNumber(node) > -1);
    }

    /**
     * Sets a terminal number to the given terminal.
     * 
     * @param node
     *            the terminal
     * @param number
     *            the terminalnumber
     */
    private void setTerminalNumber(Node node, int number) {
        removeTerminalNumber(node);
        node.setInteger(TERMINAL_NUMBER, number);
    }

    /**
     * Sets the indices of the terminals as terminal numbers.
     */
    private void setTerminalNumbers() {
        for (int i = 0; i < terminals.length; i++) {
            setTerminalNumber(terminals[i], i);
        }
    }

    /**
     * Removes the bfs-number from a given <code> GraphElement</code>
     * 
     * @param e
     *            the <code> GraphElement</code>
     */
    private void removeBfsNumber(GraphElement e) {
        try {
            e.removeAttribute(BFS_NUMBER);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    /**
     * Removes the bfs-number from the whole graph.
     */
    private void removeBfsNumber() {
        for (Iterator it = tree.getNodesIterator(); it.hasNext();) {
            removeBfsNumber((Node) it.next());
        }
    }

    /**
     * Sets a bfs-numbers to a given <code> GraphElement</code>.
     * 
     * @param e
     *            the <code> GraphElement</code>
     * @param number
     *            the number
     */
    private void setBfsNumber(GraphElement e, int number) {
        removeBfsNumber(e);
        e.setInteger(BFS_NUMBER, number);
    }

    /**
     * Returns the bfs-number of a <code> GraphElement</code>.
     * 
     * @param e
     *            the <code> GraphElement</code>
     * @return the bfs-number of e
     */
    private int getBfsNumber(GraphElement e) {
        int number = -1;
        try {
            number = e.getInteger(BFS_NUMBER);
        } catch (AttributeNotFoundException anfe) {
        }
        return number;
    }

    /**
     * Starts a bfs starting at the given node and numbering the nodes with
     * bfs-numbers.
     * 
     * @param startNode
     *            the start node for the bfs
     */
    private void bfs(Node startNode) {

        Queue queue = new Queue();
        queue.addLast(startNode);
        setBfsNumber(startNode, 0);

        Node node;
        Edge edge;
        Node targetNode;
        while (!queue.isEmpty()) {
            node = (Node) queue.removeFirst();

            for (Iterator it = node.getEdgesIterator(); it.hasNext();) {
                edge = (Edge) it.next();
                targetNode = nsa.getOtherEdgeNode(node, edge);
                if (getBfsNumber(targetNode) == -1) {
                    queue.addLast(targetNode);
                    setBfsNumber(targetNode, (getBfsNumber(node) + 1));
                }
            }
        }
    }

    /**
     * Removes all temporary data from the algorithm.
     */
    private void removeAllTemporaryData() {
        removeBfsNumber();
        removeMark();
        removeTerminalNumbers();
    }

    /**
     * Returns the terminal-separation cut computed by this algorithm.
     * 
     * @return the cut
     * @throws ClusteringException
     *             if the algorithm was not executed yet.
     */
    public Collection getTerminalSeparationCut() {
        if (algorithmRun)
            return cut;
        else
            throw new ClusteringException(ALGORITHM_NOT_RUN_ERROR);
    }

    /**
     * Returns the array of clusters computed by this algorithm.
     * 
     * @return the clusters
     * @throws ClusteringException
     *             if the algorithm was not executed yet.
     */
    public Collection[] getClusters() {
        if (algorithmRun)
            return clusters;
        else
            throw new ClusteringException(ALGORITHM_NOT_RUN_ERROR);
    }

    /**
     * Returns the cutsize computed by this algorithm.
     * 
     * @return the cutsize
     * @throws ClusteringException
     *             if the algorithm was not executed yet.
     */
    public double getCutSize() {
        if (algorithmRun)
            return cutSize;
        else
            throw new ClusteringException(ALGORITHM_NOT_RUN_ERROR);
    }
}
