package org.graffiti.plugins.algorithms.kandinsky;

import java.util.Hashtable;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.plugins.algorithms.fpp.Face;

/**
 * Creates a network node for the faces of the graph.
 */
public class FaceNode extends MCMFNode {
    /** The graph element is a <Code>Face</Code>. */
    private Face element;

    /** The capacity for the edge from the source to the <Code>FaceNode</Code>. */
    private int cap;

    /** The id of the face. */
    public int idF;

    /** Stores the edges of the face with the adjacent neighbour faces. */
    private Hashtable<Edge, FaceNode> neighbours = new Hashtable<Edge, FaceNode>();

    /** Stores the edges of the face with the adjacent neighbour faces. */
    private Hashtable<FaceNode, Edge> edgeBetweenFaces = new Hashtable<FaceNode, Edge>();

    /** List of adjacent ingoing <code>MCMFArcs</code> between two faces. */
    private LinkedList<MCMFArc> listInFFArcs;

    /** List of adjacent ingoing <code>MCMFArcs</code> between two faces. */
    private LinkedList<MCMFArc> listOutFFArcs;

    /**
     * Creates a network node for the faces of the graph.
     * 
     * @param label
     *            Label of node.
     */
    public FaceNode(String label, int idF, Face face, int id) {
        super(label, Type.FACE, id);
        super.setElement(face);
        this.element = face;
        this.idF = idF;
        listInFFArcs = new LinkedList<MCMFArc>();
        listOutFFArcs = new LinkedList<MCMFArc>();
    }

    /**
     * Returns the face element.
     * 
     * @return the element.
     */
    @Override
    public Face getElement() {
        return element;
    }

    /**
     * Calculates the capacity for a face.
     * 
     * @return the capacity of an edge from the source to a face node.
     */
    protected int getCapSourceFace() {
        int deg = element.getEdgelist().size();
        if (getLabel().equals("Face 0")) {
            cap = -4 - deg;
        } else {
            cap = 4 - deg;
        }
        return cap;
    }

    /**
     * Adds an (Edge, FaceNode)-entry to the Hashtable, which stores the edges
     * of the face with the adjacent neighbour faces and an (FaceNode,
     * Edge)-entry to the Hashtable, which stores the adjacent faces whith the
     * shared edge.
     * 
     * @param edge
     *            The edge to be stored.
     * @param face
     *            The FaceNode to be stored.
     */
    protected void addNeighbourFace(Edge edge, FaceNode face) {
        neighbours.put(edge, face);
        edgeBetweenFaces.put(face, edge);
    }

    /**
     * Returns the adjacent face, which depends on the edge.
     * 
     * @param edge
     *            The Edge which adjacent face is looked for.
     * 
     * @return The FaceNode, which depends on the edge.
     */
    protected FaceNode getNeighbourFace(Edge edge) {
        return neighbours.get(edge);
    }

    /**
     * Returns the edges of the face.
     * 
     * @return the list of edges
     */
    protected LinkedList<Edge> getEdges() {
        return this.element.getEdgelist();
    }

    /**
     * Returns the adjacent face, which depends on the edge.
     * 
     * @param face
     *            The other face, which shares an edge with this face.
     * 
     * @return The shared edge.
     */
    protected Edge getSharedEdge(FaceNode face) {
        return edgeBetweenFaces.get(face);
    }

    /**
     * Sets the list of the adjacent ingoing <code>MCMFArcs</code>.
     * 
     * @param edge
     *            the adjacent ingoing <code>MCMFArcs</code> to set.
     */
    public void addInFFArc(MCMFArc edge) {
        this.listInFFArcs.add(edge);
    }

    /**
     * Sets the list of the adjacent outgoing <code>MCMFArcs</code>.
     * 
     * @param edge
     *            the adjacent outgoing <code>MCMFArcs</code> to set.
     */
    public void addOutFFArc(MCMFArc edge) {
        this.listOutFFArcs.add(edge);
    }

    /**
     * Gets the list of the adjacent out-going <code>MCMFArcs</code> between two
     * faces.
     */
    public LinkedList<MCMFArc> getOutFFArcs() {
        return listOutFFArcs;
    }

    /**
     * Gets the list of the adjacent in-going <code>MCMFArcs</code> between two
     * faces.
     */
    public LinkedList<MCMFArc> getInFFArcs() {
        return listInFFArcs;
    }
}
