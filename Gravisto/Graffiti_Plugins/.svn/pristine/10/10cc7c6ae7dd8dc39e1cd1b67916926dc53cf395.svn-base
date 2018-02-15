package org.graffiti.plugins.algorithms.kandinsky;

import java.util.Collection;
import java.util.Hashtable;

import org.graffiti.plugins.algorithms.kandinsky.NormArc.Status;

/**
 * This class constructs a network for the normalized orthogonal representation
 * of the graph. It is used to compact the representation and calculate the
 * relative coordinates.
 * 
 * @author Sonja
 * @version $Revision$ $Date$
 */
public class NormNetwork {

    /** HashMap of the network graph nodes. */
    private Hashtable<String, NormNode> normNodeTable;

    /** HashMap of the network NormArcs. */
    private Hashtable<String, NormArc> arcTable;

    public NormNetwork() {
        this.normNodeTable = new Hashtable<String, NormNode>();
        this.arcTable = new Hashtable<String, NormArc>();
    }

    /**
     * Searches for a NormNode with <code>GraphNode</code> node.
     * 
     * @param node
     *            The node-element of the node.
     * @return the NormNode.
     */
    public NormNode searchNormNode(GraphNode node) {
        String label = node.getLabel();
        return normNodeTable.get(label);
    }

    /**
     * Searches for an <code>NormArc</code> between two NormNodes.
     * 
     * @param from
     *            The starting point of the <code>NormArc</code>.
     * @param to
     *            The target point of the <code>NormArc</code>.
     * @return the <code>NormArc</code>.
     */
    public NormArc seachNormArc(NormNode from, NormNode to) {
        for (NormArc a : from.getOutArcs()) {
            if (a.getTo() == to)
                return a;
        }
        return null;
    }

    /**
     * Creates a new node of the network for a node of the graph.
     * 
     * @param node
     *            the graph element: Node
     * @return the node which was constucted for the GraphNode
     */
    public NormNode createNormNode(GraphNode node) {
        NormNode knoten = searchNormNode(node);
        if (knoten != null)
            return knoten;
        else {
            knoten = new NormNode(node);
            normNodeTable.put(node.getLabel(), knoten);
            return knoten;
        }
    }

    /**
     * Creates a dummy node for a bend of the graph.
     * 
     * @return the dummy node
     */
    public NormNode createDummyNode(String label) {
        NormNode dummy = new NormNode(label);
        normNodeTable.put(label, dummy);
        return dummy;
    }

    /**
     * Creates a new <code>NormArc</code> of the network.
     * 
     * @param from
     *            starting node of the <code>NormArc</code>.
     * @param to
     *            target node of the <code>NormArc</code>.
     * @param edge
     *            the <code>OrthEdge</code>
     * @param dir
     *            direction of the dart
     * @param angle
     *            angle to the next edge.
     * @param total
     *            the total number of bends
     * @param pos
     *            the current bend
     * @return the created arc
     */
    public NormArc createNormArc(NormNode from, NormNode to, OrthEdge edge,
            boolean dir, int angle, int total, int pos) {
        assert from != null : "from existiert nicht";
        assert to != null : "to existiert nicht";
        String label = labelNormArc(from, to);
        NormArc arc = new NormArc(label, from, to, edge, dir, angle, total, pos);
        from.addOutArc(arc);
        to.addInArc(arc);
        arcTable.put(label, arc);
        return arc;
    }

    /**
     * Creates a new Dummy-<code>NormArc</code> of the network.
     * 
     * @param from
     *            starting node of the <code>NormArc</code>.
     * @param to
     *            target node of the <code>NormArc</code>.
     * @param dir
     *            direction of the dart
     * @param angle
     *            angle to the next edge.
     * @param status
     *            the status of the previous <code>NormArc</code>.
     * @return the created arc
     */
    public NormArc createDummyArc(NormNode from, NormNode to, boolean dir,
            int angle, Status status) {
        NormArc dummy = createNormArc(from, to, null, dir, angle, 1, 2);
        dummy.setStatus(status);
        dummy.getFrom().addStatusArc(dummy);
        return dummy;
    }

    /**
     * Removes an <code>NormArc</code> between two NormNodes.
     * 
     * @param from
     *            the starting node of the <code>NormArc</code>.
     * @param to
     *            the target point of the <code>NormArc</code>.
     */
    public void removeNormArc(NormNode from, NormNode to) {
        String label = from.getLabel() + " --> " + to.getLabel();
        NormArc NormArc = arcTable.get(label);
        removeNormArc(NormArc);
    }

    /**
     * Removes an <code>NormArc</code> from the network.
     * 
     * @param arc
     *            The <code>NormArc</code> to remove.
     */
    public void removeNormArc(NormArc arc) {
        if (arc != null) {
            arc.getFrom().getOutArcs().remove(arc);
            arc.getFrom().removeStatusArc(arc);
            arc.getTo().getInArcs().remove(arc);
            arc.getTo().removeStatusArc(arc);
            arcTable.remove(arc.getLabel());
        }
    }

    /**
     * Reduces the label of a node.
     * 
     * @param from
     *            starting node of edge
     * @param to
     *            target node of edge
     * @return label
     */
    private String labelNormArc(NormNode from, NormNode to) {
        String label = from.getLabel() + " --> " + to.getLabel();
        return label;
    }

    /**
     * Returns the collection of NormNodes.
     * 
     * @return the collection of NormNodes.
     */
    public Collection<NormNode> getNormNodes() {
        return normNodeTable.values();
    }

    /**
     * Returns the collection of NormArcs.
     * 
     * @return the collection of NormArcs.
     */
    public Collection<NormArc> getNormArcs() {
        return arcTable.values();
    }
}
