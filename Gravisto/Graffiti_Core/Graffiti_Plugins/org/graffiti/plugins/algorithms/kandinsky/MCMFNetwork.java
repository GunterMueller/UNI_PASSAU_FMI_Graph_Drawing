package org.graffiti.plugins.algorithms.kandinsky;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.fpp.Face;
import org.graffiti.plugins.algorithms.kandinsky.MCMFNode.Type;

/**
 * Constructs the MCMF-network, which is used for calculating the number of
 * bends and the shape of the orthogonal planar graph.
 */
public class MCMFNetwork {

    /** Complete list of all network nodes: NODE, FACE, HELP and ANGLE. */
    protected LinkedList<MCMFNode> elementList;

    /** List of the network graph nodes. */
    private LinkedList<GraphNode> nodeList;

    /** HashMap of the network graph nodes. */
    private Hashtable<Node, GraphNode> nodeTable;

    /** HashMap of the network arcs. */
    private Hashtable<String, MCMFArc> arcTable;

    /** HashMap of the graph faces as network nodes. */
    private Hashtable<Face, FaceNode> faceTable;

    /** List of the face nodes. */
    private LinkedList<FaceNode> faces;

    /** HashMap of the help nodes. */
    private Hashtable<String, HelpNode> helpTable;

    /** List of the angle nodes. */
    private LinkedList<AngleNode> angleList;

    /** List of the bend nodes. */
    private Hashtable<String, BendNode> bendTable;

    /** List of the bend nodes. */
    protected LinkedList<Device> deviceList;

    /** The source. */
    private MCMFNode s;

    /** The sink. */
    private MCMFNode t;

    /** Counts the number of devices. */
    protected int deviceCounter = 0;

    /** Each vertex gets a unique ID. */
    private int id = 0;

    /**
     * Constructor of the MCMF-network, which is used for calculating the number
     * of bends and the shape of the orthogonal planar graph.
     */
    public MCMFNetwork() {
        this.elementList = new LinkedList<MCMFNode>();
        this.nodeList = new LinkedList<GraphNode>();
        this.nodeTable = new Hashtable<Node, GraphNode>();
        this.faceTable = new Hashtable<Face, FaceNode>();
        this.helpTable = new Hashtable<String, HelpNode>();
        this.angleList = new LinkedList<AngleNode>();
        this.bendTable = new Hashtable<String, BendNode>();
        this.arcTable = new Hashtable<String, MCMFArc>();
        this.deviceList = new LinkedList<Device>();
    }

    /**
     * Searches for a <code>GraphNode</code> with a given name.
     * 
     * @param label
     *            The label of the node.
     * @return the <code>GraphNode</code>.
     */
    public GraphNode searchGraphNode(String label) {
        for (Object o : getNodeList()) {
            GraphNode k = (GraphNode) o;
            if (k.getLabel().equals(label))
                return k;
        }
        return null;
    }

    /**
     * Searches for a <code>FaceNode</code> with <code>Node</code> e.
     * 
     * @param label
     *            The label of the node.
     * @return the <code>FaceNode</code>.
     */
    public FaceNode searchFaceNode(String label) {
        for (Object k : faceTable.values()) {
            FaceNode f = (FaceNode) k;
            if (f.getLabel().equals(label))
                return f;
        }
        return null;
    }

    /**
     * Searches for a <code>HelpNode</code> with a given <code>GraphNode</code>,
     * <code>FaceNode</code> and <code>Edge</code>.
     * 
     * @param n
     *            The <code>GraphNode</code> for which the HelpNode is
     *            constructed.
     * @param f
     *            The <code>FaceNode</code> in which the HelpNode is.
     * @param e
     *            The <code>Edge</code> for which the HelpNode is constructed.
     * @return the <code>HelpNode</code>.
     */
    public HelpNode searchHelpNode(GraphNode n, FaceNode f, Edge e) {
        HelpNode h = n.getHelpNode(e);
        if (h.getFace() == f)
            return h;
        else
            return h.getOther(e);
    }

    /**
     * Searches for a <code>HelpNode</code> with a given <code>GraphNode</code>
     * and <code>Edge</code>.
     * 
     * @param n
     *            The GraphNode for which the HelpNode is constructed.
     * @param e
     *            The Edge for which the HelpNode is constructed.
     * @return the <code>HelpNode</code>.
     */
    public HelpNode searchHelpNode(GraphNode n, Edge e) {
        return n.getHelpNode(e);
    }

    /**
     * Searches for a <code>AngleNode</code> with a given name.
     * 
     * @param label
     *            The label of the node.
     * @return the <code>AngleNode</code>.
     */
    public AngleNode searchAngleNode(String label) {
        for (AngleNode k : angleList) {
            if (k.getLabel().equals(label))
                return k;
        }
        return null;
    }

    /**
     * Searches for a <code>BendNode</code> with a given name and type.
     * 
     * @param label
     *            The label of the node.
     * @return the <code>BendNode</code>.
     */
    public BendNode searchBendNode(String label) {
        return bendTable.get(label);
    }

    /**
     * Searches for a <code>GraphNode</code> which was constructed for a
     * <code>Node</code>.
     * 
     * @param e
     *            The node-element of the node.
     * @return the <code>GraphNode</code>.
     */
    public GraphNode searchNode(Node e) {
        return nodeTable.get(e);
    }

    /**
     * Searches for a <code>FaceNode</code> with <code>Face</code> f.
     * 
     * @param f
     *            The face-element of the node.
     * @return the <code>FaceNode</code>.
     */
    public FaceNode searchFace(Face f) {
        return faceTable.get(f);
    }

    /**
     * Searches for an <code>MCMFArc</code> between two NetworkNodes.
     * 
     * @param from
     *            The starting point of the <code>MCMFArc</code>.
     * @param to
     *            The target point of the <code>MCMFArc</code>.
     * @return the <code>MCMFArc</code>.
     */
    public MCMFArc searchArc(MCMFNode from, MCMFNode to) {
        assert from != null : " from existiert nicht";
        assert to != null : "to existiert nicht";
        for (MCMFArc a : from.getOutArcs()) {
            if ((a.getTo() == to))
                return a;
        }
        return null;
    }

    /**
     * Searches for an <code>MCMFArc</code> between two NetworkNodes.
     * 
     * @param from
     *            The starting point of the <code>MCMFArc</code>.
     * @param to
     *            The target point of the <code>MCMFArc</code>.
     * @param e
     *            The <code>Edge</code> of the arc.
     * @return the <code>MCMFArc</code>.
     */
    public MCMFArc searchArc(MCMFNode from, MCMFNode to, Edge e) {
        assert from != null : " from existiert nicht";
        assert to != null : "to existiert nicht";
        for (MCMFArc a : from.getOutArcs()) {
            if ((a.getTo() == to) && (a.getEdge() == e))
                return a;
        }
        return null;
    }

    /**
     * Searches for an <code>MCMFArc_FF</code> between two faces.
     * 
     * @param from
     *            The starting <code>FaceNode</code> of the <code>MCMFArc</code>
     *            .
     * @param to
     *            The target <code>FaceNode</code> of the <code>MCMFArc</code>.
     * @param edge
     *            The shared <code>Edge</code> between the two faces.
     * @return the <code>MCMFArc_FF</code>.
     */
    public MCMFArc searchFFArc(FaceNode from, FaceNode to, Edge edge) {
        for (MCMFArc a : from.getOutFFArcs()) {
            if ((a.getTo() == to) && (a.getEdge() == edge))
                return a;
        }
        return null;
    }

    /**
     * Creates a new node of the network for a node of the graph.
     * 
     * @param label
     *            the name of the new node
     * @param node
     *            the graph element: Node
     * @return the node GraphNode
     */
    public GraphNode createGraphNode(String label, Node node) {
        GraphNode knoten = new GraphNode(label, node, id);
        id++;
        nodeList.add(knoten);
        nodeTable.put(node, knoten);
        elementList.add(knoten);
        return knoten;
    }

    /**
     * Creates a new node of the network for a node of the graph.
     * 
     * @param label
     *            the name of the new node
     * @param i
     *            The number of the face. It is part of the label.
     * @param o
     *            the graph element: Face
     * @return the node FaceNode
     */
    public FaceNode createFaceNode(String label, int i, Face o) {
        FaceNode knoten = new FaceNode(label, i, o, id);
        id++;
        elementList.add(knoten);
        faceTable.put(o, knoten);
        return knoten;
    }

    /**
     * Creates a new help node of the network.
     * 
     * @param start
     *            The GraphNode where the edge starts and for which the HelpNode
     *            is constructed.
     * @param end
     *            The GraphNode where the edge ends.
     * @param edge
     *            The edge.
     * @param direction
     *            The it is the direction of the edge.
     * @return The HelpNode.
     */
    public HelpNode createHelpNode(GraphNode start, GraphNode end, Edge edge,
            boolean direction) {
        String label = "H_" + start.getLabel() + "_" + end.getLabel();
        HelpNode node = new HelpNode(label, start, edge, id);
        id++;
        helpTable.put(label, node);
        elementList.add(node);
        return node;
    }

    /**
     * Creates a new angle node of the network.
     * 
     * @param n
     *            The GraphNode for which the AngleNode is constructed.
     * @param f
     *            The FaceNode for which the AngleNode is constructed.
     * @param edge
     *            The Edge which is changed by the AngleNode.
     * @return the node AngleNode
     */
    public AngleNode createAngleNode(GraphNode n, FaceNode f, Edge edge) {
        String label = "Angle_" + n.getLabel() + "_" + f.getLabel();
        AngleNode knoten = new AngleNode(label, n, f, edge, id);
        id++;
        angleList.add(knoten);
        elementList.add(knoten);
        return knoten;
    }

    /**
     * Creates a new bend node of the network.
     * 
     * @param label
     *            the name of the new node
     * @param edge
     *            the Edge for which the new node is constructed
     * @return the BendNode
     */
    public BendNode createBendNode(String label, Edge edge) {
        BendNode knoten = new BendNode(label, edge, id);
        id++;
        bendTable.put(label, knoten);
        elementList.add(knoten);
        return knoten;
    }

    /**
     * Creates a new <code>MCMFArc</code> of the network.
     * 
     * @param from
     *            starting node of the <code>MCMFArc</code>.
     * @param to
     *            target node of the <code>MCMFArc</code>.
     * @param cap
     *            int cost capacity of the <code>MCMFArc</code>
     * @param cost
     *            cost for one unit flow
     * @return the <code>MCMFArc</code>
     */
    public MCMFArc createArc(MCMFNode from, MCMFNode to, int cap, int cost) {
        assert from != null : "from existiert nicht";
        assert to != null : "to existiert nicht";
        String label = labelArc(from, to);
        MCMFArc arc = new MCMFArc(label, from, to, cap, cost);
        from.addOutArc(arc);
        to.addInArc(arc);
        arcTable.put(label, arc);
        return arc;
    }

    /**
     * Creates a new <code>MCMFArc</code> of the network.
     * 
     * @param from
     *            The starting node of the <code>MCMFArc_FF</code>.
     * @param to
     *            The target node of the <code>MCMFArc_FF</code>.
     * @param edge
     *            The shared <code>Edge</code> between the two faces.
     * @param cap
     *            The capacity of the <code>MCMFArc</code>.
     * @param start
     *            The label of the starting node of the <code>Edge</code>.
     * @param end
     *            The label of the target node of the <code>Edge</code>.
     * @return the <code>MCMFArc</code>
     */
    public MCMFArc createVFArc(MCMFNode from, MCMFNode to, Edge edge, int cap,
            String start, String end) {
        assert from != null : "from existiert nicht";
        assert to != null : "to existiert nicht";
        String label = labelArc(from, to) + "_(" + start + ", " + end + ")";
        MCMFArc arc = new MCMFArc(label, from, to, cap, 0);
        arc.setEdge(edge);
        from.addOutArc(arc);
        to.addInArc(arc);
        arcTable.put(label, arc);
        return arc;
    }

    /**
     * Creates a new <code>MCMFArc</code> between two faces of the network.
     * 
     * @param from
     *            The starting node of the <code>MCMFArc</code>.
     * @param to
     *            The target node of the <code>MCMFArc</code>.
     * @param edge
     *            The shared <code>Edge</code> between the two faces.
     * @param start
     *            The label of the starting node of the <code>Edge</code>.
     * @param end
     *            The label of the target node of the <code>Edge</code>.
     */
    public MCMFArc createFFArc(FaceNode from, FaceNode to, Edge edge,
            String start, String end) {
        assert from != null : "from existiert nicht";
        assert to != null : "to existiert nicht";
        String label = labelArc(from, to) + "_(" + start + ", " + end + ")";
        MCMFArc arc = new MCMFArc(label, from, to, Integer.MAX_VALUE, 1);
        arc.setEdge(edge);
        from.addOutFFArc(arc);
        to.addInFFArc(arc);
        arcTable.put(label, arc);
        return arc;
    }

    /**
     * Removes an <code>MCMFArc</code> between two <code>MCMFNode</code>s.
     * 
     * @param from
     *            the starting node of the <code>MCMFArc</code>.
     * @param to
     *            the target point of the <code>MCMFArc</code>.
     * @return The <code>MCMFArc</code>.
     */
    public MCMFArc removeArc(MCMFNode from, MCMFNode to) {
        assert from != null : "from existiert nicht";
        assert to != null : "to existiert nicht";
        String label = from.getLabel() + " --> " + to.getLabel();
        MCMFArc arc = arcTable.get(label);
        removeArc(arc);
        return arc;
    }

    /**
     * Removes an <code>MCMFArc</code> from the network.
     * 
     * @param arc
     *            The <code>MCMFArc</code> to remove.
     * @return The <code>MCMFArc</code>.
     */
    public MCMFArc removeArc(MCMFArc arc) {
        if (arc != null) {
            String label = arc.getLabel();
            if ((arc.getFrom() instanceof FaceNode)
                    && (arc.getTo() instanceof FaceNode)) {
                ((FaceNode) arc.getFrom()).getOutFFArcs().remove(arc);
                ((FaceNode) arc.getTo()).getInFFArcs().remove(arc);
            } else {
                arc.getFrom().getOutArcs().remove(arc);
                arc.getTo().getInArcs().remove(arc);
            }
            arcTable.remove(label);
        }
        return arc;
    }

    /**
     * Reduces the label of a node.
     * 
     * @param label
     *            the label to reduce
     * @return short label
     */
    public String reduceLabel(String label) {
        // schneidet das "Node " ab
        return label.substring(5);
    }

    /**
     * Creates the label of a <code>MCMFArc</code>.
     * 
     * @param from
     *            starting node of edge
     * @param to
     *            target node of edge
     * @return label
     */
    private String labelArc(MCMFNode from, MCMFNode to) {
        String label = from.getLabel() + " --> " + to.getLabel();
        return label;
    }

    /**
     * Creates the source of the network.
     */
    public void createS() {
        s = new MCMFNode("s", Type.NODE, id);
        id++;
        elementList.add(s);
    }

    /**
     * Creates the sink of the network.
     */
    public void createT() {
        t = new MCMFNode("t", Type.NODE, id);
        id++;
        elementList.add(t);
    }

    /**
     * Returns the source node.
     * 
     * @return the source node.
     */
    public MCMFNode getS() {
        return s;
    }

    /**
     * Returns the sink node.
     * 
     * @return the sink node.
     */
    public MCMFNode getT() {
        return t;
    }

    /**
     * Returns a collection containing all the edges between n1 and n2. There
     * can be more than one Edge between two nodes. The edges returned by this
     * method can go from n1 to n2 or vice versa.
     * 
     * @param current
     *            the first node
     * @param pred
     *            the second node
     * @return a <code>Collection</code> containing all edges between n1 and n2,
     *         an empty collection if there is no <code>MCMFArc</code> between
     *         the two nodes.
     */
    public LinkedList<MCMFArc> getEdges(MCMFNode current, MCMFNode pred) {
        LinkedList<MCMFArc> result = new LinkedList<MCMFArc>();
        LinkedList<MCMFArc> curList = current.getArcs();
        LinkedList<MCMFArc> predList = pred.getArcs();
        for (MCMFArc a : curList) {
            if (predList.contains(a)) {
                result.add(a);
            }
        }
        return result;
    }

    /**
     * Returns the list of network graph nodes.
     * 
     * @return the <code>LinkedList</code> of network graph nodes.
     */
    public LinkedList<GraphNode> getNodeList() {
        return nodeList;
    }

    /**
     * Returns the sorted collection of faces in ascending order (Face 0, Face
     * 1, ...).
     * 
     * @return the sorted <code>Collection</code> of <code>FaceNode</code>s.
     */
    public Collection<FaceNode> sortFaceList() {
        int count = 0;
        faces = new LinkedList<FaceNode>();
        while (faces.size() != faceTable.size()) {
            for (FaceNode f : faceTable.values()) {
                if (f.idF == count) {
                    faces.add(f);
                    count++;
                }
            }
        }
        return faces;
    }

    /**
     * Returns the collection of faces.
     * 
     * @return the <code>Collection</code> of faces.
     */
    public Collection<FaceNode> getFaceList() {
        return faces;
    }

    /**
     * Returns the collection of HelpNodes.
     * 
     * @return the <code>Collection</code> of HelpNodes.
     */
    public Collection<HelpNode> getHelpList() {
        return helpTable.values();
    }

    /**
     * Returns the collection of arcs.
     * 
     * @return the <code>Collection</code> of arcs.
     */
    public Collection<MCMFArc> getArcs() {
        return arcTable.values();
    }

    /**
     * Adds a <code>Device</code> to the list of devices and to the
     * <code>GraphNode</code>.
     * 
     * @param node
     *            the <code>GraphNode</code> the device belongs to.
     * @param device
     *            the device to add.
     */
    public void addDevice(GraphNode node, Device device) {
        node.addDevice(device);
        deviceList.add(device);
    }
}
