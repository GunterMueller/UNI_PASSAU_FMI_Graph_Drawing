package org.graffiti.plugins.algorithms.mnn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;

/*
 * This class calculates a 4-canonical ordering for an embedding of
 * a given planar graph
 */
public class ExtendedCanonicalOrdering extends CanonicalOrdering {

    private boolean debug = true;

    private static final String PATH = GraphicAttributeConstants.LABEL
            + Attribute.SEPARATOR + GraphicAttributeConstants.LABEL;

    // The graph
    private Graph graph;

    // An embedding of the planar graph
    private EmbeddedGraph embeddedGraph;

    // private HashSet<Node> otherBorder = new HashSet<Node>();

    private Node u1 = null;

    private Node u2 = null;

    private Node u3 = null;

    private Node u4 = null;

    private int orderingNumber = 1;

    private HashMap<Node, Collection<Node>> g = new HashMap<Node, Collection<Node>>();

    private LinkedList<Node> contour = new LinkedList<Node>();

    // The canonical ordering
    private ArrayList<CanonicalOrderingNode> canonicalOrdering = new ArrayList<CanonicalOrderingNode>();

    private ArrayList<CanonicalOrderingNode> canOrdFirst = new ArrayList<CanonicalOrderingNode>();

    private ArrayList<CanonicalOrderingNode> canOrdSecond = new ArrayList<CanonicalOrderingNode>();

    private static final int FIRST_HALF = 1;

    private static final int SECOND_HALF = 2;

    private static final int NORMAL = 0;

    private int mode = 0;

    private int currentPosition = 0;

    /*
     * Constructor
     */
    public ExtendedCanonicalOrdering(EmbeddedGraph embeddedGraph) {

        this.graph = embeddedGraph.getGraph();
        this.embeddedGraph = embeddedGraph;
        calculate();
    }

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

        if (debug) {
            System.out.print("Exterior Face: ");
        }

        for (Node n : contour) {

            if (u4 == null) {
                u4 = n;
                if (debug) {
                    System.out.print("u4: ");
                }
            } else if (u1 == null) {
                u1 = n;
                if (debug) {
                    System.out.print("u1: ");
                }
            } else if (u2 == null) {
                u2 = n;
                if (debug) {
                    System.out.print("u2: ");
                }
            } else if (u3 == null) {
                u3 = n;
                if (debug) {
                    System.out.print("u3: ");
                }
            }
            u3 = n;
            if (debug) {
                System.out.print(n.getString(PATH) + " ");
            }
        }
        if (debug) {
            System.out.println("");
        }

        init();

        while (!finished()) {
            getNextNode();
        }

        // erste und letzte knoten!

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
        System.out.println(this.toString());

    }

    private boolean finished() {
        for (Node n : graph.getNodes()) {
            if (!n.getBoolean("mnn.mark"))
                return false;
        }
        return true;
    }

    private void init() {
        LinkedList<Node> firstInnerFace = null;
        for (Edge e : graph.getEdges(u1, u2)) {
            firstInnerFace = embeddedGraph.getInnerFace(e);
            break;
        }
        updateContour(firstInnerFace);
    }

    /*
     * returns the next node on the contour
     */
    private Node getNextNNode(Node n, Collection<Node> l, Node last) {

        LinkedList<Node> neigh = embeddedGraph.getAdjacentNodes(n);

        LinkedList<Node> neighbours = new LinkedList<Node>();
        for (Node node : neigh) {
            neighbours.addFirst(node);
        }
        neighbours.addAll(neighbours);

        if (debug) {
            System.out.println("DELETED NODES ");
            for (Node no : l) {
                System.out.print(" " + no.getString(PATH));
            }
            System.out.println();
        }

        boolean b = false;

        if (debug) {
            System.out.println("NODE (neighbours) " + n.getString(PATH));
            System.out.println("NODE (last) " + last.getString(PATH));
        }

        for (Node current : neighbours) {
            if (debug) {
                System.out.println("Neigh " + current.getString(PATH));
            }
            if (b && !l.contains(current) && g.get(current) != null) {
                if (debug) {
                    System.out.println("RET");
                }
                return current;
            }

            if (current == last) {
                b = true;
            }
        }
        return null;
    }

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

            // System.out.println("NextNODE " + next.getString(PATH));

            next = getNextNNode(next, newNodes, last);
            last = tmp;

            if (next != null) {
                tmpContour.add(next);
            }

        }

        // System.out.println("Calculate new contour");

        contour = tmpContour;

        if (debug) {
            System.out.print("New Contour: ");
        }
        last = u3;

        for (Node n : contour) {
            if (debug) {
                System.out.print(n.getString(PATH) + " ");
            }
            // del contour edges for BFS
            g.get(n).remove(last);
            g.get(last).remove(n);

            last = n;

        }
        if (debug) {
            System.out.println();
        }
        // for (Node n: contour)
        // {
        // // System.out.print("N von ");
        // // System.out.print(n.getString(PATH) + ": ");
        //
        // for (Node sss: g.get(n))
        // {
        // System.out.print(sss.getString(PATH) + " ");
        // }
        //
        // // System.out.println();
        // }
        // System.out.println();
    }

    private ArrayList<Node> calcMinChord2() {
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

        // System.out.println("\n-------NCC-------");
        for (Collection<Node> chord : chords) {

            // System.out.print("ALL ");
            //            
            // for (Node n : chord) {
            // System.out.print(n.getString(PATH) + " ");
            // }
            // System.out.println("");

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
                // System.out.println("CH " + start.getString(PATH) +
                // " to " + end.getString(PATH));

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

        // System.out.println("--> NEXT CHORD <-- ");
        // for (Node n : result) {
        // System.out.print(" " + n.getString(PATH));
        // }
        // System.out.println();

        return result;
    }

    private void getNextNode() {

        ArrayList<Node> chord = calcMinChord2();

        ArrayList<Node> w = new ArrayList<Node>();

        if (debug) {
            System.out.print("\nNext chord: ");

            for (Node n : chord) {
                System.out.print(n.getString(PATH) + " ("
                        + n.getInteger("mnn.degree") + ", "
                        + n.getNeighbors().size() + ") ");
            }
            System.out.println();
        }

        boolean case1 = true;
        boolean case2 = false;

        for (int i = 0; i < chord.size(); i++) {

            if (chord.get(i).getInteger("mnn.degree") != 2) {
                case1 = false;
            }
        }

        for (int i = 0; i < chord.size(); i++) {

            if (chord.get(i).getNeighbors().size()
                    - chord.get(i).getInteger("mnn.degree") >= 2) {
                case2 = true;
                w.add(chord.get(i));
                break;
            }

        }

        ArrayList<Node> wfertig = new ArrayList<Node>();

        // Case 1
        boolean cycle = true;

        for (Node n : graph.getNodes()) {
            if (!n.getBoolean("mnn.mark") && n.getInteger("mnn.degree") != 2) {
                cycle = false;

            } else if (!n.getBoolean("mnn.mark")) {

                wfertig.add(n);
            }
        }

        if (cycle) {

            // TODO: Fertigmachen
            LinkedList<Node> newNodes = new LinkedList<Node>();

            for (Node n : wfertig) {
                // if (!n.getBoolean("mnn.mark")) {
                n.setBoolean("mnn.mark", true);
                newNodes.add(n);
                // }
            }

            CanonicalOrderingNode con = new CanonicalOrderingNode(newNodes);
            canonicalOrdering.add(con);
            return;

        }

        // Subcase 2a
        if (case1) {
            if (debug) {
                System.out.println("Case 2a");
            }
            w = chord;

            LinkedList<Node> l = new LinkedList<Node>();
            if (debug) {
                System.out.print("Next Nodes of the Ordering: ");
            }
            for (Node n : w) {
                if (debug) {
                    System.out.print(n.getString(PATH) + " ");
                }
                l.add(n);
            }
            if (debug) {
                System.out.println();
            }
            updateContour(l);
            // Subcase 2b
        } else if (case2) {
            if (debug) {
                System.out.println("Case 2b");
            }
            LinkedList<Node> l = new LinkedList<Node>();
            if (debug) {
                System.out.print("Next Nodes of the Ordering: ");
            }
            for (Node n : w) {
                if (debug) {
                    System.out.print(n.getString(PATH) + " ");
                }
                l.add(n);
            }
            if (debug) {
                System.out.println();
            }

            updateContour(l);
            // Subcase 2c
        } else {

            // TODO nicht gleicher endknoten
            LinkedList<Node> l = new LinkedList<Node>();
            if (debug) {
                System.out.println("Case 2c");
            }
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

            if (debug) {
                System.out.print("Next Nodes of the Ordering: ");
                for (Node n : l) {
                    System.out.print(n.getString(PATH) + " ");

                }
                System.out.println();
            }

            updateContour(l);

        }

    }

    /**
     * Calculates a ordring for the mnn algorithm
     * 
     */
    private void calculateMnnOrdering() {

        ArrayList<CanonicalOrderingNode> tmpCanOrd = new ArrayList<CanonicalOrderingNode>();

        int numOfNodes = graph.getNodes().size();
        HashSet<Node> fertig = new HashSet<Node>();

        for (CanonicalOrderingNode con : canonicalOrdering) {

            if (fertig.size() < numOfNodes / 2) {

                System.out.println("CON " + con.toString());

                if (con.getNumberOfNodes() > 1) {

                    HashSet<Node> smallerNeigh = new HashSet<Node>();

                    for (Node n : con.getNodes()) {

                        System.out.println("Con Node " + n.getString(PATH));
                        System.out.print("Neighbours:");
                        for (Node neigh : n.getNeighbors()) {
                            System.out.print(neigh.getString(PATH) + " ");
                            if (fertig.contains(neigh)) {
                                System.out.print("ADD!");
                                smallerNeigh.add(neigh); // Fehler
                            }
                        }
                        System.out.println();
                    }

                    System.out.print("SmallerNeigh ");
                    for (Node m : smallerNeigh) {
                        System.out.print(m.getString(PATH) + " ");
                    }
                    System.out.println();

                    if (smallerNeigh.size() > 2) {
                        // Zerlegen!!!
                        System.out.println("Zerlege!");
                        for (Node current : con.getNodes()) {

                            CanonicalOrderingNode conNew = new CanonicalOrderingNode(
                                    current);
                            canOrdFirst.add(conNew);
                        }

                    } else {
                        canOrdFirst.add(con);
                    }

                    fertig.addAll(con.getNodes());

                } else {
                    fertig.addAll(con.getNodes());
                    canOrdFirst.add(con);
                }

            } else {
                tmpCanOrd.add(0, con);
            }
        }

        fertig = new HashSet<Node>();

        for (CanonicalOrderingNode con : tmpCanOrd) {

            System.out.println("CON " + con.toString());

            if (con.getNumberOfNodes() > 1) {

                HashSet<Node> smallerNeigh = new HashSet<Node>();

                for (Node n : con.getNodes()) {

                    System.out.println("Con Node " + n.getString(PATH));
                    System.out.print("Neighbours:");
                    for (Node neigh : n.getNeighbors()) {
                        System.out.print(neigh.getString(PATH) + " ");
                        if (fertig.contains(neigh)) {
                            System.out.print("ADD!");
                            smallerNeigh.add(neigh); // Fehler
                        }
                    }
                    System.out.println();
                }

                System.out.print("SmallerNeigh ");
                for (Node m : smallerNeigh) {
                    System.out.print(m.getString(PATH) + " ");
                }
                System.out.println();

                if (smallerNeigh.size() > 2) {
                    // Zerlegen!!!
                    System.out.println("Zerlege!");
                    for (Node current : con.getNodes()) {

                        CanonicalOrderingNode conNew = new CanonicalOrderingNode(
                                current);
                        canOrdSecond.add(conNew);
                    }

                } else {
                    canOrdSecond.add(con);
                }

                fertig.addAll(con.getNodes());

            } else {
                fertig.addAll(con.getNodes());
                canOrdSecond.add(con);
            }

        }

    }

    /**
     * returns all the smaller neighbours of a Con
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
     * returns all the smaller neighbours of a Con
     */
    public Node getSmallerNeighbour(Node n) {

        for (Node current : n.getNeighbors()) {
            if (current.getBoolean("mnn.mark"))
                return current;
        }

        return null;

    }

    /**
     * true, if there is another one Con
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
