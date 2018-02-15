package org.graffiti.plugins.algorithms.mnn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * This class calculates a 4-canonical ordering for an embedding of a given
 * planar graph
 */
public class CanonicalOrdering implements Iterator<CanonicalOrderingNode> {
    private static final String PATH = GraphicAttributeConstants.LABEL
            + Attribute.SEPARATOR + GraphicAttributeConstants.LABEL;

    // The graph
    private Graph graph;

    // An embedding of the planar graph
    private EmbeddedGraph embeddedGraph;

    // The canonical ordering
    private ArrayList<CanonicalOrderingNode> canonicalOrdering = new ArrayList<CanonicalOrderingNode>();

    private HashSet<Node> otherBorder = new HashSet<Node>();

    // constants for the mode
    private static final int FIRST_HALF = 1;
    private static final int SECOND_HALF = 2;
    private static final int NORMAL = 0;

    // first-half, second half or all-node-mode
    private int mode = 0;

    // pointer to the current node
    private int currentPosition = 0;

    /**
     * Constructor
     */
    public CanonicalOrdering(EmbeddedGraph embeddedGraph) {
        this.graph = embeddedGraph.getGraph();
        this.embeddedGraph = embeddedGraph;
        calculate();
    }

    /**
     * Constructor
     */
    public CanonicalOrdering() {
    }

    /**
     * calculates the 4-canonical ordering
     */
    private void calculate() {
        int n = graph.getNumberOfNodes();

        // Initialise all nodes
        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node current = it.next();
            current.setBoolean("mnn.mark", false);
            current.setInteger("mnn.visited", 0);
            current.setInteger("mnn.chords", Integer.MAX_VALUE);
            current.setInteger("mnn.ordering", 0);
        }

        // Start with the exterior face
        Face exteriorface = embeddedGraph.getExteriorFace();
        List<Node> nodes = exteriorface.getNodelist();

        Iterator<Node> i = nodes.iterator();

        // Last Node of the Ordering
        if (i.hasNext()) {
            Node current = i.next();
            current.setInteger("mnn.ordering", n);
            current.setInteger("mnn.visited", 2);
            current.setBoolean("mnn.mark", true);
            current.setInteger("mnn.chords", 0);
            updateVariables(current);

            CanonicalOrderingNode coNode = new CanonicalOrderingNode(current);
            canonicalOrdering.add(0, coNode);
        }

        n--;

        // The Node before the last node of the Canonical Ordering
        if (i.hasNext()) {
            Node current = i.next();
            current.setInteger("mnn.ordering", n);
            current.setInteger("mnn.visited", 2);
            current.setBoolean("mnn.mark", true);
            current.setInteger("mnn.chords", 0);
            updateVariables(current);
            CanonicalOrderingNode coNode = new CanonicalOrderingNode(current);
            canonicalOrdering.add(0, coNode);
        }
        n--;

        // First and second node
        Node first = null;
        Node second = null;

        if (i.hasNext()) {
            second = i.next();
            second.setInteger("mnn.ordering", 2);
            second.setBoolean("mnn.mark", true);
        }

        if (i.hasNext()) {
            first = i.next();
            first.setInteger("mnn.ordering", 1);
            first.setBoolean("mnn.mark", true);
        }

        // Main Algorithm
        for (; n >= 3; n--) {
            Node current = findNextNode();
            updateVariables(current);
            current.setInteger("mnn.ordering", n);
            CanonicalOrderingNode coNode = new CanonicalOrderingNode(current);
            canonicalOrdering.add(0, coNode);
        }

        // Add First and second node
        CanonicalOrderingNode coNode = new CanonicalOrderingNode(second);
        canonicalOrdering.add(0, coNode);
        coNode = new CanonicalOrderingNode(first);
        canonicalOrdering.add(0, coNode);

    }

    /**
     * returns the smaller neighbours of a node (the nighbours of a node with a
     * smaller ordering number
     * 
     * @param con
     *            the CanonicalOrderingNode
     * @return the smaller neighbours
     */
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
     * returns true, if there is another node in the canonical ordering
     */
    public boolean hasNext() {

        if (mode == NORMAL) {

            if (currentPosition < canonicalOrdering.size()
                    && currentPosition >= 0)
                return true;

        } else if (mode == FIRST_HALF) {

            if (currentPosition < Math.round(Math
                    .ceil(canonicalOrdering.size() / 2))
                    && currentPosition >= 0)
                return true;

        } else if (mode == SECOND_HALF) {

            if (currentPosition >= Math.round(Math.ceil(canonicalOrdering
                    .size() / 2))
                    && currentPosition < canonicalOrdering.size())
                return true;

        }

        return false;
    }

    /**
     * returns the next node of the ordering
     */
    public CanonicalOrderingNode next() {

        CanonicalOrderingNode con = canonicalOrdering.get(currentPosition);

        if (mode == NORMAL) {
            currentPosition++;
        } else if (mode == FIRST_HALF) {
            currentPosition++;
        } else if (mode == SECOND_HALF) {
            currentPosition--;
        }

        return con;
    }

    /**
     * only necessery for the iterator interface
     */
    public void remove() {
    }

    /**
     * returns all the nodes, the nodes of the first or the second half
     * 
     * @return the nodes
     */
    public Collection<Node> getNodes() {

        Collection<Node> nodes = new LinkedList<Node>();

        if (mode == NORMAL) {

            for (int i = 0; i < canonicalOrdering.size(); i++) {
                CanonicalOrderingNode con = canonicalOrdering.get(i);
                nodes.addAll(con.getNodes());
            }

        } else if (mode == FIRST_HALF) {

            for (int i = 0; i < Math.round(Math
                    .ceil(canonicalOrdering.size() / 2)); i++) {
                CanonicalOrderingNode con = canonicalOrdering.get(i);
                nodes.addAll(con.getNodes());
            }

        } else if (mode == SECOND_HALF) {

            for (int i = (int) Math.round(Math
                    .ceil(canonicalOrdering.size() / 2)); i < canonicalOrdering
                    .size(); i++) {
                CanonicalOrderingNode con = canonicalOrdering.get(i);
                nodes.addAll(con.getNodes());
            }

        } else {
            // Error
        }

        return nodes;
    }

    /**
     * switches tho the "first-half"-mode
     * 
     */
    public void getFirstHalf() {
        mode = 1;
        currentPosition = 0;

        for (int i = 0; i < canonicalOrdering.size(); i++) {
            CanonicalOrderingNode con = canonicalOrdering.get(i);
            for (Node n : con.getNodes()) {
                n.setInteger("mnn.ordering", i + 1);
            }
        }
    }

    /**
     * switches tho the "second-half"-mode
     * 
     */
    public void getSecondHalf() {
        mode = 2;
        currentPosition = canonicalOrdering.size() - 1;

        int counter = 1;
        for (int i = canonicalOrdering.size() - 1; i >= 0; i--) {
            CanonicalOrderingNode con = canonicalOrdering.get(i);
            for (Node n : con.getNodes()) {
                n.setInteger("mnn.ordering", counter);
            }
            counter++;
        }

    }

    /**
     * switches tho the "all-nodes"-mode
     * 
     */
    public void getAll() {
        mode = 0;
        currentPosition = 0;

        for (int i = 0; i < canonicalOrdering.size(); i++) {
            CanonicalOrderingNode con = canonicalOrdering.get(i);
            for (Node n : con.getNodes()) {
                n.setInteger("mnn.ordering", i + 1);
            }
        }
    }

    /**
     * returns the nuber of nodes
     * 
     * @return the number of nodes
     */
    public int getNumberOfNodes() {
        return canonicalOrdering.size();
    }

    /**
     * returns a list of canonical ordering nodes in the correct order
     * 
     * @return the canonical ordring of the nodes
     */
    public ArrayList<CanonicalOrderingNode> getCanonicalOrdering() {

        ArrayList<CanonicalOrderingNode> result = new ArrayList<CanonicalOrderingNode>();

        if (mode == NORMAL)
            return canonicalOrdering;
        else if (mode == FIRST_HALF) {
            for (int i = 0; i < Math.round(Math
                    .ceil(canonicalOrdering.size() / 2)); i++) {
                result.add(canonicalOrdering.get(i));
            }
            return result;
        } else if (mode == SECOND_HALF) {

            for (int i = (int) Math.round(Math
                    .ceil(canonicalOrdering.size() / 2)); i < canonicalOrdering
                    .size(); i++) {
                result.add(canonicalOrdering.get(i));
            }
            return result;
        }
        return null;
    }

    private Node findNextNode() {
        Node nextNode = null;
        Iterator<Node> it = graph.getNodesIterator();
        while (it.hasNext()) {
            Node current = it.next();
            if (current.getBoolean("mnn.mark") == false
                    && current.getInteger("mnn.visited") >= 2
                    && current.getInteger("mnn.chords") == 0
                    && current.getInteger("mnn.ordering") == 0) {
                if (nextNode == null) {
                    nextNode = current;
                }
            }
        }
        return nextNode;
    }

    /*
     * Update all variables if a node ist added to the canonical ordering
     */
    private void updateVariables(Node node) {
        node.setBoolean("mnn.mark", true);
        List<Node> adjacentNodes = embeddedGraph.getAdjacentNodes(node);
        Iterator<Node> it = adjacentNodes.iterator();
        while (it.hasNext()) {
            Node current = it.next();
            if (current.getBoolean("mnn.mark") == false) {
                current.setInteger("mnn.visited", 1 + current
                        .getInteger("mnn.visited"));

                otherBorder.add(current);
            }
        }
        otherBorder.remove(node);

        Iterator<Node> it2 = otherBorder.iterator();
        while (it2.hasNext()) {
            Node current = it2.next();
            if (current.getBoolean("mnn.mark") == false) {

                Iterator<Node> it3 = embeddedGraph.getAdjacentNodes(current)
                        .iterator();

                int counter = -2;
                while (it3.hasNext()) {
                    Node current2 = it3.next();

                    if (otherBorder.contains(current2)) {
                        counter++;
                    }
                }
                current.setInteger("mnn.chords", counter);
            }
        }
    }

    /**
     * toString()
     */
    @Override
    public String toString() {

        Iterator<CanonicalOrderingNode> it = canonicalOrdering.iterator();
        String string = "Canonical ordering:\n";

        while (it.hasNext()) {
            Node current = (Node) it.next();
            string += current.getString(PATH) + "("
                    + current.getInteger("mnn.ordering") + ") ";
        }

        return string;
    }

}
