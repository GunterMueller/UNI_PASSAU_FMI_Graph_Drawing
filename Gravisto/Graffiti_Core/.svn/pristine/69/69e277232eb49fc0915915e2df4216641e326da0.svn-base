package org.graffiti.plugins.algorithms.kandinsky;

import java.util.LinkedList;

/**
 * The nodes of the MCMF-network. Implements the <code>Comparable</code>
 * -interface.
 */
public class MCMFNode implements Comparable<MCMFNode> {

    /**
     * The new label on edges which arise as result of this algorithm.
     */
    private String label = "";

    /** The graph element, can be a <Code>Node</Code> or a <Code>Face</Code>. */
    private Object element;

    /** The unique ID of the node. */
    private int id;

    /** Type of node. */
    private Type type;

    /** Capacity of the in-going edge on the shortest path. */
    private int edgeCap;

    /** List of adjacent Arcs of a node. */
    private LinkedList<MCMFArc> listArcs;

    /** List of adjacent ingoing Arcs of a node. */
    private LinkedList<MCMFArc> listInArcs;

    /** List of adjacent ingoing Arcs of a node. */
    private LinkedList<MCMFArc> listOutArcs;

    /** Dijkstra distance, cost for reaching. */
    private int dist;

    /** Previous vertex on shortest path. */
    private MCMFNode prev;

    /** Is true, if the node is already part of the shortest path. */
    private boolean known;

    /** Counts the number of nodes between this node and the source. */
    private int count = Integer.MAX_VALUE;

    /** The type of node a MCMFNode can be. */
    public enum Type {
        NODE, FACE, HELP, ANGLE, BEND
    };

    /**
     * Constructor of a MCMFNode.
     * 
     * @param label
     *            Label of node.
     * @param type
     *            The type of the node.
     * @param id
     *            The unique ID of the node.
     */
    public MCMFNode(String label, Type type, int id) {
        this.label = label;
        listArcs = new LinkedList<MCMFArc>();
        listInArcs = new LinkedList<MCMFArc>();
        listOutArcs = new LinkedList<MCMFArc>();
        this.element = null;
        this.type = type;
        dist = Integer.MAX_VALUE;
        prev = null;
        known = false;
        this.id = id;
    }

    /**
     * Returns the label.
     * 
     * @return The label of the node.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the list of the adjacent ingoing Arcs.
     * 
     * @param edge
     *            the adjacent ingoing MCMFArc to set.
     */
    public void addInArc(MCMFArc edge) {
        this.listInArcs.add(edge);
    }

    /**
     * Adds an arc to the list of the adjacent outgoing <code>MCMFArc</code>s.
     * 
     * @param edge
     *            the adjacent outgoing <code>MCMFArc</code> to add.
     */
    public void addOutArc(MCMFArc edge) {
        this.listOutArcs.add(edge);
    }

    /**
     * Gets the list of the adjacent <code>MCMFArc</code>s.
     */
    @SuppressWarnings("unchecked")
    public LinkedList<MCMFArc> getArcs() {
        listArcs = (LinkedList<MCMFArc>) listOutArcs.clone();
        listArcs.addAll((LinkedList<MCMFArc>) listInArcs.clone());
        return listArcs;
    }

    /**
     * Gets the list of the adjacent out-going <code>MCMFArc</code>s.
     */
    public LinkedList<MCMFArc> getOutArcs() {
        return listOutArcs;
    }

    /**
     * Gets the list of the adjacent in-going <code>MCMFArc</code>s.
     */
    public LinkedList<MCMFArc> getInArcs() {
        return listInArcs;
    }

    /**
     * Returns the type of a node in the network.
     * 
     * @return the type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the element of the graph for which the MCMFNode was constructed.
     * 
     * @return the element.
     */
    public Object getElement() {
        return element;
    }

    /**
     * Sets the element of the graph for which the MCMFNode was constructed.
     * 
     * @param element
     *            the element to set.
     */
    public void setElement(Object element) {
        this.element = element;
    }

    /**
     * Fulfills the contract of the Comparable interface.
     * 
     * @param rhs
     *            The MCMFNode to compare.
     */
    /*
     * Geht nach Kosten des Weges, Abstand von der Quelle und der ID -->
     * eindeutige Einordung jedes Knotens; wird verwendet im TreeSet beim
     * Dijkstra f�r die Berechnung des billigsten Weges.
     */
    public int compareTo(MCMFNode rhs) {
        MCMFNode other = rhs;
        if (this.dist < other.dist)
            return -1;
        else {
            if (this.dist > other.dist)
                return 1;
            else {
                if (this.count < other.count)
                    return -1;
                else if (this.count > other.count)
                    return 1;
                else if (this.id < other.id)
                    return -1;
                else if (this.id > other.id)
                    return 1;
                else

                    return 0;
            }
        }
    }

    /**
     * Returns the capacity of the in-going <code>MCMFArc</code> on the shortest
     * path.
     * 
     * @return the capacity of the in-going MCMFArc on the shortest path.
     */
    public int getArcCap() {
        return edgeCap;
    }

    /**
     * Sets the capacity of the in-going <code>MCMFArc</code> on the shortest
     * path.
     * 
     * @param edgeCap
     *            the capacity of the in-going MCMFArc on the shortest path to
     *            set.
     */
    public void setArcCap(int edgeCap) {
        this.edgeCap = edgeCap;
    }

    /**
     * Returns the Dijkstra distance: the costs for a path from the source to
     * this node.
     * 
     * @return the distance.
     */
    public int getDist() {
        return dist;
    }

    /**
     * Sets the Dijkstra dist: the costs for a path from the source to this
     * node.
     * 
     * @param dist
     *            the distance to set.
     */
    public void setDist(int dist) {
        this.dist = dist;
    }

    /**
     * Returns true, if the node is already part of the shortest path.
     * 
     * @return true, if the node is already part of the shortest path.
     */
    public boolean isKnown() {
        return known;
    }

    /**
     * Sets boolean true, if the node is already part of the shortest path.
     * 
     * @param known
     *            status of the node to set.
     */
    public void setKnown(boolean known) {
        this.known = known;
    }

    /**
     * Returns the previous vertex on the shortest path.
     * 
     * @return the previous vertex on the shortest path.
     */
    public MCMFNode getPrev() {
        return prev;
    }

    /**
     * Sets the previous vertex on the shortest path.
     * 
     * @param prev
     *            the previous vertex on the shortest path to set.
     */
    public void setPrev(MCMFNode prev) {
        this.prev = prev;
    }

    /**
     * Returns the number of nodes between this node and the source.
     * 
     * @return the number of nodes between this node and the source.
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the number of nodes between this node and the source.
     * 
     * @param count
     *            the number of nodes to set.
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Returns the label, distance, count and id of a MCMFNode.
     */
    @Override
    public String toString() {
        String l = label + ", Dist. " + dist + ", Count " + count + ", ID: "
                + id;
        return l;
    }

    /**
     * Returns the id of the node.
     * 
     * @return the id.
     */
    public int getId() {
        return id;
    }
}
