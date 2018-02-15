package org.graffiti.plugins.algorithms.kandinsky;

import java.util.Hashtable;
import java.util.LinkedList;

import org.graffiti.graph.Edge;

/**
 * Normalizes the orthogonal representation of a graph by replacing the bends by
 * dummy nodes and dummy arcs.
 */
public class NormalizeRepresentation {
    /** The network which is used for calculating the coordinates. */
    private NormNetwork network;

    /** Stores the orthogonal representation of the graph. */
    private ComputeOrthRepresentation orthRep;

    /** Hashtable with the label of the faces and their edges. */
    private Hashtable<String, LinkedList<NormArc>> faces;

    /** Stores the dummy nodes of each edge. */
    private Hashtable<Edge, LinkedList<NormNode>> dummyNodes;

    /** Stores the already created NormArcs for the opposite direction. */
    private Hashtable<String, NormArc> oppositeArcs;

    /**
     * Normalizes the orthogonal representation.
     * 
     * @param orthRep
     *            The orthogonal representation of the graph.
     */
    NormalizeRepresentation(ComputeOrthRepresentation orthRep) {
        this.network = new NormNetwork();
        this.orthRep = orthRep;
        this.faces = new Hashtable<String, LinkedList<NormArc>>();
        this.dummyNodes = new Hashtable<Edge, LinkedList<NormNode>>();
        this.oppositeArcs = new Hashtable<String, NormArc>();
    }

    /**
     * Computes the representation for each face. Each bend is replaced with a
     * dummy node and dummy arcs.
     */
    public void computeNormalizedRep() {
        OrthFace[] repFaces = orthRep.getFaces();
        for (int i = 0; i < repFaces.length; i++) {
            OrthFace face = repFaces[i];
            LinkedList<OrthEdge> edges = face.getEdges();
            LinkedList<NormArc> arcs = new LinkedList<NormArc>();
            for (int k = 0; k < edges.size(); k++) {
                OrthEdge edge = edges.get(k);
                OrthEdge next = null;
                int n = k + 1;
                if (n == edges.size()) {
                    n = 0;
                }
                next = edges.get(n);
                NormNode start = network.createNormNode(edge.getStart());
                NormNode previous = start;
                LinkedList<Boolean> bends = edge.getBends();
                int angle = 0;
                int total = 0; // Gesamtzahl der Knicke
                int pos = 0; // Nummer des aktuellen Knicks
                if (bends.size() > 0) // Kante mit Knicken
                {
                    total = bends.size() + 1;
                    if (!dummyNodes.containsKey(edge.getEdge()))
                    // taucht erstmalig auf
                    {
                        LinkedList<NormNode> dummies = new LinkedList<NormNode>();
                        dummies.addFirst(previous);
                        for (int count = 0; count < bends.size(); count++) {
                            String label = "D(" + edge.getStart().getLabel()
                                    + ", " + edge.getEnd().getLabel() + ")";
                            pos = count + 1;
                            if (count > 0) {
                                label += "_" + count;
                            }
                            NormNode dummy = network.createDummyNode(label);
                            // Erstelle Liste mit den f�r diese Kante
                            // verwendeten
                            // Dummy-Knoten in umgekehrter Reihenfolge
                            dummies.addFirst(dummy);
                            NormArc a = createDummyArcs(arcs, edge, previous,
                                    bends, total, pos, count, dummy);
                            // Kante mu� eingef�gt werden
                            oppositeArcs.put(a.getLabel(), a);
                            previous = dummy;
                            dummyNodes.put(edge.getEdge(), dummies);
                            pos++;
                        }
                    } else {
                        LinkedList<NormNode> dummies = dummyNodes.get(edge
                                .getEdge());
                        for (int count = 0; count < bends.size(); count++) {
                            pos = count + 1;
                            NormNode dummy = dummies.get(count);
                            NormArc a = createDummyArcs(arcs, edge, previous,
                                    bends, total, pos, count, dummy);
                            // Kante in andere Richtung existiert bereits
                            String label = oppositeNormArcLabel(previous, dummy);
                            NormArc other = oppositeArcs.get(label);
                            other.setOpposite(a);
                            a.setOpposite(other);
                            previous = dummy;
                            pos++;
                        }
                    }
                }
                angle = next.getAngle();
                NormNode end = network.createNormNode(edge.getEnd());
                NormArc a = network.createNormArc(previous, end, edge, edge
                        .getDirection(), angle, total, pos);
                // Verweis auf den Nachfolger
                if (arcs.size() != 0) {
                    NormArc tmp = arcs.getLast();
                    tmp.setNext(a);
                }
                arcs.add(a);
                String label = oppositeNormArcLabel(previous, end);
                if (oppositeArcs.containsKey(label)) {
                    // Kante in andee Richtung existiert bereits
                    NormArc other = oppositeArcs.get(label);
                    other.setOpposite(a);
                    a.setOpposite(other);
                } else {
                    // Kante mu� eingef�gt werden
                    oppositeArcs.put(a.getLabel(), a);
                }
            }
            // Verweis auf den Nachfolger f�r den letzten NormArc
            NormArc last = arcs.getLast();
            last.setNext(arcs.getFirst());
            faces.put(face.getName(), arcs);
        }
    }

    /**
     * Creates a dummy arc for a bend.
     * 
     * @param arcs
     *            the list of dummy arcs
     * @param edge
     *            the orthogonal representation of the edge
     * @param previous
     *            the previous NormNode
     * @param bends
     *            the list of bends which are to be replaced
     * @param total
     *            the total numbers of bends
     * @param pos
     *            the number of the arc in the chain of dummy arcs
     * @param count
     *            the position of the actual bend which is to be replaced
     * @param dummy
     *            the dummy node
     * @return the dummy arc
     */
    private NormArc createDummyArcs(LinkedList<NormArc> arcs, OrthEdge edge,
            NormNode previous, LinkedList<Boolean> bends, int total, int pos,
            int count, NormNode dummy) {
        int angle;
        if (bends.get(count)) {
            angle = 1;
        } else {
            angle = 3;
        }
        NormArc a = network.createNormArc(previous, dummy, edge, edge
                .getDirection(), angle, total, pos);
        // Verweis auf den Nachfolger
        if (arcs.size() != 0) {
            NormArc tmp = arcs.getLast();
            tmp.setNext(a);
        }
        arcs.add(a);
        return a;
    }

    /**
     * Gets the label of the NormArc in the opposite direction.
     * 
     * @param from
     *            starting node of edge
     * @param to
     *            target node of edge
     * @return label The label of the <code>NormArc</code>.
     */
    public String oppositeNormArcLabel(NormNode from, NormNode to) {
        String label = to.getLabel() + " --> " + from.getLabel();
        return label;
    }

    /**
     * Returns the normalized network.
     * 
     * @return the network.
     */
    protected NormNetwork getNetwork() {
        return network;
    }

    /**
     * Returns the faces with their NormArcs.
     * 
     * @return the names of the faces with a LinkedList of their
     *         <code>NormArc</code>s.
     */
    protected Hashtable<String, LinkedList<NormArc>> getFaces() {
        return faces;
    }
}
