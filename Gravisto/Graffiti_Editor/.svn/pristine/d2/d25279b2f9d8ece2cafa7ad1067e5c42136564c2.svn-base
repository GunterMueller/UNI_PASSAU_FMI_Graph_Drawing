package org.graffiti.plugins.algorithms.mnn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/*
 * This class calculates a 4-canonical ordering for an embedding of
 * a given planar graph
 */
public class CanonicalDepomposition extends CanonicalOrdering {

    // The graph
    private Graph graph;

    // An embedding of the planar graph
    private EmbeddedGraph embeddedGraph;

    // 4 nodes on the exterior face
    private Node u1 = null;
    private Node u2 = null;
    private Node u3 = null;
    private Node u4 = null;

    private int orderingNumber = 1;

    private HashMap<Node, Collection<Node>> g = new HashMap<Node, Collection<Node>>();

    // the countour of the subgraph with the first nodes of the ordering
    private LinkedList<Node> contour = new LinkedList<Node>();

    // The canonical ordering
    private ArrayList<CanonicalOrderingNode> canonicalOrdering = new ArrayList<CanonicalOrderingNode>();
    private ArrayList<CanonicalOrderingNode> canOrdFirst = new ArrayList<CanonicalOrderingNode>();
    private ArrayList<CanonicalOrderingNode> canOrdSecond = new ArrayList<CanonicalOrderingNode>();

    // constants for the mode
    private static final int FIRST_HALF = 1;
    private static final int SECOND_HALF = 2;
    private static final int NORMAL = 0;

    // first-half, second half or all-node-mode
    private int mode = 0;

    // pointer to the current node
    private int currentPosition = 0;

    /*
     * Constructor
     */
    public CanonicalDepomposition(EmbeddedGraph embeddedGraph) {

        this.graph = embeddedGraph.getGraph();
        this.embeddedGraph = embeddedGraph;
        calculate();
    }

    /**
     * Calculates the 4-canonical decomposition
     */
    private void calculate() {

        // Initialise all nodes
        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node current = it.next();
            current.setBoolean("mnn.mark", false);
            current.setInteger("mnn.ordering", 0);
            current.setInteger("mnn.degree", current.getNeighbors().size());
        }

        for (Node n : graph.getNodes()) {
            g.put(n, n.getNeighbors());
        }

        // Start with the exterior face
        Face exteriorface = embeddedGraph.getExteriorFace();
        for (Node n : exteriorface.getNodelist()) {
            contour.addFirst(n);
        }

        // save the four nodes on the exterior face
        for (Node n : contour) {
            if (u4 == null) {
                u4 = n;
            } else if (u1 == null) {
                u1 = n;
            } else if (u2 == null) {
                u2 = n;
            } else if (u3 == null) {
                u3 = n;
            }
            u3 = n;
        }

        // do some initialisation
        init();

        // add all nodes
        while (!finished()) {
            getNextNode();
        }

        /*
         * when finished, split the first Canonical Ordering Node into 3 nodes
         * (u1, u2 and the remaining) ant do the same for the last node, for the
         * MNN Algorithm
         */
        CanonicalOrderingNode conFirst = canonicalOrdering.remove(0);

        LinkedList<Node> f = conFirst.getNodes();

        f.remove(u1);
        f.remove(u2);

        canonicalOrdering.add(0, new CanonicalOrderingNode(f));
        canonicalOrdering.add(0, new CanonicalOrderingNode(u2));
        canonicalOrdering.add(0, new CanonicalOrderingNode(u1));

        CanonicalOrderingNode conLast = canonicalOrdering
                .remove(canonicalOrdering.size() - 1);

        f = conLast.getNodes();

        f.remove(u3);
        f.remove(u4);

        canonicalOrdering.add(canonicalOrdering.size(),
                new CanonicalOrderingNode(f));
        canonicalOrdering.add(canonicalOrdering.size(),
                new CanonicalOrderingNode(u3));
        canonicalOrdering.add(canonicalOrdering.size(),
                new CanonicalOrderingNode(u4));

        int count = 1;
        for (CanonicalOrderingNode con : canonicalOrdering) {
            con.setNumber(count);
            count++;
        }

        calculateMnnOrdering();

    }

    /**
     * returns true, if all the nodes are marked
     * 
     * @return true, if all the nodes are marked
     */
    private boolean finished() {
        for (Node n : graph.getNodes()) {
            if (!n.getBoolean("mnn.mark"))
                return false;
        }
        return true;
    }

    /**
     * get the first contour
     */
    private void init() {
        LinkedList<Node> firstInnerFace = null;
        for (Edge e : graph.getEdges(u1, u2)) {
            firstInnerFace = embeddedGraph.getInnerFace(e);
            break;
        }
        updateContour(firstInnerFace);
    }

    /**
     * returns the next node to calculate the new contour
     * 
     * @param n
     * @param l
     * @param last
     * @return the next node
     */
    private Node getNextNNode(Node n, Collection<Node> l, Node last) {

        // get the neighburs of the node n
        LinkedList<Node> neigh = embeddedGraph.getAdjacentNodes(n);

        // get the reverse ordering of the neighbour list...
        LinkedList<Node> neighbours = new LinkedList<Node>();
        for (Node node : neigh) {
            neighbours.addFirst(node);
        }
        // ... and add it again
        neighbours.addAll(neighbours);

        boolean b = false;

        for (Node current : neighbours) {
            if (b && !l.contains(current) && g.get(current) != null)
                return current;

            if (current == last) {
                b = true;
            }
        }
        return null;
    }

    /**
     * updates the contour of G'
     * 
     * @param newNodes
     *            list of the nodes added to the ordering
     */
    private void updateContour(LinkedList<Node> newNodes) {

        for (Node n : newNodes) {
            n.setBoolean("mnn.mark", true);
            n.setInteger("mnn.ordering", orderingNumber);
            n.setInteger("mnn.subordering", orderingNumber);

            g.remove(n);

            for (Node neighbour : n.getNeighbors()) {
                Collection<Node> l = g.get(neighbour);

                if (l != null) {
                    l.remove(n);
                }
            }
        }

        // Calculate the new degree
        for (Node n : graph.getNodes()) {
            int cooo = 0;
            for (Node nei : n.getNeighbors()) {
                if (!nei.getBoolean("mnn.mark")) {
                    cooo++;
                }
            }
            n.setInteger("mnn.degree", cooo);
        }

        CanonicalOrderingNode con = new CanonicalOrderingNode(newNodes);
        canonicalOrdering.add(con);

        // calculate the new contour
        LinkedList<Node> tmpContour = new LinkedList<Node>();
        tmpContour.add(u4);
        Node next = u4;
        Node last = u3;
        Node tmp = u3;

        while (next != u3) {
            tmp = next;

            next = getNextNNode(next, newNodes, last);
            last = tmp;

            if (next != null) {
                tmpContour.add(next);
            }
        }

        contour = tmpContour;

        last = u3;

        for (Node n : contour) {

            // del contour edges for BFS
            g.get(n).remove(last);
            g.get(last).remove(n);

            last = n;

        }

    }

    /**
     * calculates a minimal chord
     * 
     * @return a sorted list with the nodes of the chord
     */
    private ArrayList<Node> calcMinChord() {
        ArrayList<Node> result = new ArrayList<Node>();

        Collection<Collection<Node>> chords = new HashSet<Collection<Node>>();

        List<Face> innerFaces = embeddedGraph.getInnerFaces();

        HashMap<Node, Integer> num = new HashMap<Node, Integer>();
        int count = 1;
        for (Node n : contour) {
            num.put(n, count);
            count++;
        }

        for (Face f : innerFaces) {

            Collection<Node> chord = new HashSet<Node>();
            boolean mark = false;
            for (Node n : f.getNodelist()) {

                // m�ssen noch im restgrapgen sein
                if (n.getBoolean("mnn.mark")) {
                    mark = true;
                    break;
                }
                if (contour.contains(n)) {
                    chord.add(n);
                }

            }
            if (chord.size() >= 2 && !mark) {
                chords.add(chord);
            }
        }

        int mindiff = Integer.MAX_VALUE;

        for (Collection<Node> chord : chords) {

            int min = 0;
            // int max = Integer.MAX_VALUE;
            Node start = null;
            Node end = null;

            for (Node n : contour) {

                if (chord.contains(n)) {

                    if (min == 0 || min + 1 == num.get(n)) {
                        min = num.get(n);
                        start = n;
                    }

                    if (num.get(n) > min + 1) {
                        end = n;
                        break;
                    }

                }
            }

            // wenn kein abstand ist, ersten und letzten nehmen...
            if (start == null || end == null) {
                if (chord.size() > 2) {
                    start = null;
                    end = null;

                    for (Node n : contour) {

                        if (chord.contains(n)) {

                            if (start == null) {
                                start = n;

                            }

                            if (start != null) {
                                end = n;
                            }
                        }
                    }
                }
            }

            if (start != null && end != null) {

                if (Math.abs(num.get(start) - num.get(end)) < mindiff) {
                    mindiff = Math.abs(num.get(start) - num.get(end));
                    boolean add = false;
                    result = new ArrayList<Node>();

                    for (Node n : contour) {

                        if (end.equals(n)) {
                            // result.add(n);
                            break;
                        }

                        if (add) {
                            result.add(n);
                        }

                        if (start.equals(n)) {
                            add = true;
                        }

                    }
                }
            }
        }

        if (result.size() == 0) {

            for (Node n : contour) {
                result.add(n);
            }
            result.remove(result.size() - 1);
            result.remove(0);
        }

        return result;
    }

    /**
     * finds and adds the next node to the ordering
     */
    private void getNextNode() {
        // a minimal chord
        ArrayList<Node> chord = calcMinChord();

        // the nodes of the next CON
        ArrayList<Node> w = new ArrayList<Node>();

        // the set w with the last nodes of the ordering
        ArrayList<Node> wfinished = new ArrayList<Node>();

        boolean case2a = true;
        boolean case2b = false;

        // if the minimal chord is no outer chain, case 2a is false
        for (int i = 0; i < chord.size(); i++) {
            if (chord.get(i).getInteger("mnn.degree") != 2) {
                case2a = false;
            }
        }

        /*
         * if the minimal chord is no outer chain, but there is al least one
         * with a degree >= 3, the case 2b is true
         */
        for (int i = 0; i < chord.size(); i++) {
            if (chord.get(i).getNeighbors().size()
                    - chord.get(i).getInteger("mnn.degree") >= 2) {
                case2b = true;
                w.add(chord.get(i));
                break;
            }
        }

        // Case 1 - the last nodes
        boolean cycle = true;
        for (Node n : graph.getNodes()) {
            // if all remaining nodes have the degree 2
            if (!n.getBoolean("mnn.mark") && n.getInteger("mnn.degree") != 2) {
                cycle = false;
            } else if (!n.getBoolean("mnn.mark")) {
                wfinished.add(n);
            }
        }

        // Case 1
        if (cycle) {
            LinkedList<Node> newNodes = new LinkedList<Node>();
            for (Node n : wfinished) {
                n.setBoolean("mnn.mark", true);
                newNodes.add(n);
            }
            CanonicalOrderingNode con = new CanonicalOrderingNode(newNodes);
            canonicalOrdering.add(con);
            return;

        }

        // Subcase 2a
        if (case2a) {
            w = chord;
            LinkedList<Node> l = new LinkedList<Node>();
            for (Node n : w) {
                l.add(n);
            }
            updateContour(l);
        }
        // Subcase 2b
        else if (case2b) {
            LinkedList<Node> l = new LinkedList<Node>();
            for (Node n : w) {
                l.add(n);
            }
            updateContour(l);

        }
        // Subcase 2c
        else {
            LinkedList<Node> l = new LinkedList<Node>();

            Node smallerneig = null;

            for (Node n : chord) {
                if (!l.isEmpty()) {
                    if (n.getNeighbors().size() - n.getInteger("mnn.degree") == 1) {
                        l.add(n);

                        if (!smallerneig.equals(getSmallerNeighbour(n))) {
                            break;
                        } else {
                            l = new LinkedList<Node>();
                            l.add(n);
                            smallerneig = getSmallerNeighbour(n);
                        }

                    } else if (n.getNeighbors().size()
                            - n.getInteger("mnn.degree") == 0) {
                        l.add(n);
                    }
                } else if (n.getNeighbors().size() - n.getInteger("mnn.degree") == 1) {
                    l.add(n);
                    smallerneig = getSmallerNeighbour(n);
                }
            }
            updateContour(l);
        }
    }

    /**
     * Calculates a ordering for the mnn algorithm (devides it into the first
     * ant the second half) and devides some Canonical Ordeing Node into
     * seperate one for the MNN Algorithm (when a CON has more the 2 smaller
     * neighbours)
     */
    private void calculateMnnOrdering() {

        ArrayList<CanonicalOrderingNode> tmpCanOrd = new ArrayList<CanonicalOrderingNode>();

        int numOfNodes = graph.getNodes().size();

        // the nodes that are already finished
        HashSet<Node> finished = new HashSet<Node>();

        // iterate over all Canonical Ordeing Nodes
        for (CanonicalOrderingNode con : canonicalOrdering) {
            // the first half
            if (finished.size() < numOfNodes / 2) {

                // if there is more than one node
                if (con.getNumberOfNodes() > 1) {

                    /*
                     * if there are more than 2 smaller neighbours, then the
                     * canonical ordering node has to be devided into several
                     * nodes for the mnn-algorithm
                     */
                    HashSet<Node> smallerNeigh = new HashSet<Node>();

                    for (Node n : con.getNodes()) {
                        for (Node neigh : n.getNeighbors()) {
                            if (finished.contains(neigh)) {
                                smallerNeigh.add(neigh); // Fehler
                            }
                        }

                    }

                    if (smallerNeigh.size() > 2) {
                        // devide it
                        for (Node current : con.getNodes()) {
                            CanonicalOrderingNode conNew = new CanonicalOrderingNode(
                                    current);
                            canOrdFirst.add(conNew);
                        }
                    } else {
                        canOrdFirst.add(con);
                    }
                    finished.addAll(con.getNodes());

                } else {
                    finished.addAll(con.getNodes());
                    canOrdFirst.add(con);
                }

            }
            // the second half
            else {
                tmpCanOrd.add(0, con);
            }
        }

        finished = new HashSet<Node>();

        // do the same for the second half
        for (CanonicalOrderingNode con : tmpCanOrd) {

            if (con.getNumberOfNodes() > 1) {
                HashSet<Node> smallerNeigh = new HashSet<Node>();
                for (Node n : con.getNodes()) {
                    for (Node neigh : n.getNeighbors()) {
                        if (finished.contains(neigh)) {
                            smallerNeigh.add(neigh); // Fehler
                        }
                    }
                }

                if (smallerNeigh.size() > 2) {
                    for (Node current : con.getNodes()) {
                        CanonicalOrderingNode conNew = new CanonicalOrderingNode(
                                current);
                        canOrdSecond.add(conNew);
                    }
                } else {
                    canOrdSecond.add(con);
                }

                finished.addAll(con.getNodes());
            } else {
                finished.addAll(con.getNodes());
                canOrdSecond.add(con);
            }
        }
    }

    /**
     * returns all the smaller neighbours of a CON
     */
    @Override
    public Collection<Node> getSmallerNeighbours(CanonicalOrderingNode con) {
        HashSet<Node> result = new HashSet<Node>();
        for (Node n : con.getNodes()) {
            for (Node current : n.getNeighbors()) {
                if (current.getInteger("mnn.ordering") < n
                        .getInteger("mnn.ordering")) {
                    result.add(current);
                }
            }
        }
        return result;
    }

    /**
     * returns the smaller neighbours of a node
     * 
     * @param n
     * @return the smaller neighbours
     */
    public Node getSmallerNeighbour(Node n) {
        for (Node current : n.getNeighbors()) {
            if (current.getBoolean("mnn.mark"))
                return current;
        }
        return null;

    }

    /**
     * true, if there is another one CON (for the iterator)
     */
    @Override
    public boolean hasNext() {

        if (mode == NORMAL) {
            if (currentPosition < canonicalOrdering.size()
                    && currentPosition >= 0)
                return true;
        } else if (mode == FIRST_HALF) {

            if (currentPosition < canOrdFirst.size() && currentPosition >= 0)
                return true;

        } else if (mode == SECOND_HALF) {

            if (currentPosition < canOrdSecond.size() && currentPosition >= 0)
                return true;

        }

        return false;
    }

    /**
     * returns the next Canonical Ordering Node
     */
    @Override
    public CanonicalOrderingNode next() {
        CanonicalOrderingNode con = null;

        if (mode == NORMAL) {
            con = canonicalOrdering.get(currentPosition);
            currentPosition++;
        } else if (mode == FIRST_HALF) {
            con = canOrdFirst.get(currentPosition);
            currentPosition++;
        } else if (mode == SECOND_HALF) {
            con = canOrdSecond.get(currentPosition);
            currentPosition++;
        }

        return con;
    }

    /**
     * for the iterator interface
     */
    @Override
    public void remove() {
    }

    /**
     * returns the canonical ordering
     */
    @Override
    public ArrayList<CanonicalOrderingNode> getCanonicalOrdering() {

        if (mode == NORMAL)
            return canonicalOrdering;
        else if (mode == FIRST_HALF)
            return canOrdFirst;
        else if (mode == SECOND_HALF)
            return canOrdSecond;

        return null;
    }

    /**
     * Switches the iterator to return the first half of the canonical ordering
     */
    @Override
    public void getFirstHalf() {
        // switch mode to "first half"
        mode = 1;
        currentPosition = 0;

        // set ordering number of the second half to infinity
        for (CanonicalOrderingNode con : canOrdSecond) {
            for (Node n : con.getNodes()) {
                n.setInteger("mnn.ordering", Integer.MAX_VALUE);
            }
        }

        // set the correct ordering number of the first half
        for (int i = 0; i < canOrdFirst.size(); i++) {
            CanonicalOrderingNode con = canOrdFirst.get(i);
            for (Node n : con.getNodes()) {
                n.setInteger("mnn.ordering", i + 1);
            }
        }
    }

    /**
     * Switches the iterator to return the second half of the canonical ordering
     */
    @Override
    public void getSecondHalf() {
        // switch mode to "first half"
        mode = 2;
        currentPosition = 0;

        // set ordering number of the first half to infinity
        for (CanonicalOrderingNode con : canOrdFirst) {
            for (Node n : con.getNodes()) {
                n.setInteger("mnn.ordering", Integer.MAX_VALUE);
            }
        }

        // set the correct ordering number of the second half
        for (int i = 0; i < canOrdSecond.size(); i++) {
            CanonicalOrderingNode con = canOrdSecond.get(i);
            for (Node n : con.getNodes()) {
                n.setInteger("mnn.ordering", i + 1);
            }
        }

    }

    /**
     * toString()
     */
    @Override
    public String toString() {

        String string = "Canonical ordering (first half):\n";
        for (CanonicalOrderingNode con : canOrdFirst) {
            string += con.toString();
        }

        string += "\nCanonical ordering (second half):\n";
        for (CanonicalOrderingNode con : canOrdSecond) {
            string += con.toString();
        }
        return string;
    }

}
