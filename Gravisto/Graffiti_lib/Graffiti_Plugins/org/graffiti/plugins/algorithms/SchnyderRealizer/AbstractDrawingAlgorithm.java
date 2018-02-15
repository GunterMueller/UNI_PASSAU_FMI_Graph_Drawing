package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * This class is the abstract superclass of all algorithms by Schnyder and
 * Brehm. It does some preparation, which does not belong to the algorithms
 * itself, but is neccessary for all of them. That is: - creating the adjacence
 * lists for every node - triangulating the graph - initializing the faces of
 * the graph Furthermore it saves necessary information and results of the
 * algorithms.
 * 
 * @author hofmeier
 */
public abstract class AbstractDrawingAlgorithm {
    /** The graph, the algorithm is adapted on. */
    protected Graph graph;

    /** Three nodes forming the outer face of the drawing. */
    protected Node[] outerNodes = new Node[3];

    /** The edges added during the triangulation of the graph. */
    private LinkedList<Edge> addedEdges = new LinkedList<Edge>();

    /**
     * The adjacence list of every node (this is the planar embedding for the
     * algorithm)
     */
    protected HashMap<Node, HashList<Node>> adjacenceLists = new HashMap<Node, HashList<Node>>();

    /** The faces of the graph */
    protected HashList<Face> faces = new HashList<Face>();

    /**
     * The faces of the graph: saves a list of faces for every node to which it
     * belongs
     */
    protected HashMap<Node, LinkedList<Face>> facesByNodes = new HashMap<Node, LinkedList<Face>>();

    /**
     * The faces of the graph: saves a list of faces for every edge to which it
     * belongs
     */
    protected HashMap<Edge, LinkedList<Face>> facesByEdges = new HashMap<Edge, LinkedList<Face>>();

    /** A mapping of every node to a unique integer value */
    protected HashMap<Node, Integer> nodeIndex = new HashMap<Node, Integer>();

    /** The integer key for green color */
    public static final int GREEN = 1;

    /** The integer key for blue color */
    public static final int BLUE = 2;

    /** The integer key for red color */
    public static final int RED = 3;

    /** The message to be shown if the maximum number of realizers is reached */
    public static final String MAX_MESSAGE = "Maximum of realizers / canonical orders reached. The algorithm will "
            + "proceed with these but calculate no more";

    /** The algorithm to calculate the planar embedding */
    protected PlanarityAlgorithm pAlgorithm = new PlanarityAlgorithm();

    /**
     * The calculated canonical order of the graph (not used in every algorithm)
     */
    protected LinkedList<Node> canonicalOrder = new LinkedList<Node>();

    /** Saves all found realizers */
    protected LinkedList<Realizer> realizers = new LinkedList<Realizer>();

    /** To every found realizer its barycentric representation is saved here */
    protected LinkedList<BarycentricRepresentation> barycentricReps = new LinkedList<BarycentricRepresentation>();

    /** The maximum number of realizers that will be calculated */
    protected int maxNumberOfRealizers;

    /**
     * Creates a new algorithm and does all the necessary preparations mentioned
     * above.
     * 
     * @param g
     *            the graph, the algorithm is adapted on.
     * @param m
     *            the maximum number of realizers.
     */
    public AbstractDrawingAlgorithm(Graph g, int m) {
        this.graph = g;
        this.maxNumberOfRealizers = m;
        this.createAdjacenceLists();
        this.triangulate();
        this.initializeFaces();
        this.createNodeIndex();
    }

    /**
     * This method will execute the algorithm in the subclasses. As this is an
     * abstract class there is nothing to execute here.
     */
    public abstract void execute();

    /**
     * Gets the integer key for every node of the graph, which is saved in
     * <code>this.nodeIndex</code>
     * 
     * @param n
     *            the node whose index will be returned
     * @return the index of n
     */
    protected int getIndex(Node n) {
        return this.nodeIndex.get(n).intValue();
    }

    /**
     * Gets the graph the algorithm is adapted on.
     * 
     * @return the graph the algorithm is adapted on
     */
    protected Graph getGraph() {
        return graph;
    }

    /**
     * Gets the planar embedding of the graph.
     * 
     * @return the planar embedding of the graph
     */
    protected HashMap<Node, HashList<Node>> getAdjacenceLists() {
        return adjacenceLists;
    }

    /**
     * Gets the outer nodes of the graph.
     * 
     * @return the outer nodes of the graph
     */
    protected Node[] getOuterNodes() {
        return outerNodes;
    }

    /**
     * Gets the faces of the graph.
     * 
     * @return the faces of the graph
     */
    public HashList<Face> getFaces() {
        return faces;
    }

    /**
     * Gets the edges added during thr triangulation of the graph.
     * 
     * @return the edges added during thr triangulation of the graph
     */
    public LinkedList<Edge> getAddedEdges() {
        return addedEdges;
    }

    /**
     * RETURNS THE RESULT OF THE ALGORITHM After the execution of the algorithm
     * this method returns the calculated realizer(s).
     * 
     * @return the calculated realizer(s)
     */
    public LinkedList<BarycentricRepresentation> getBarycentricReps() {
        return barycentricReps;
    }

    /**
     * RETURNS THE RESULT OF THE ALGORITHM After the execution of the algorithm
     * this method returns the barycentric representation(s) of every calculated
     * realizer(s).
     * 
     * @return the calculated realizer(s)
     */
    public LinkedList<Realizer> getRealizers() {
        return realizers;
    }

    /**
     * By using the <code>TestedGraph</code> from a <code>PlanarityAlgorithm
     * </code> the adjacence lists
     * of every node are copied into a special data structure, called
     * <code>HashList</code>
     */
    private void createAdjacenceLists() {
        pAlgorithm.attach(this.graph);
        pAlgorithm.testPlanarity();
        TestedGraph tGraph = pAlgorithm.getTestedGraph();
        Iterator<Node> nodesIt = tGraph.getNodes().iterator();
        while (nodesIt.hasNext()) {
            Node node = nodesIt.next();
            HashList<Node> al = new HashList<Node>();
            Iterator<Node> neighborsIt = tGraph.getAdjacencyList(node)
                    .iterator();
            while (neighborsIt.hasNext()) {
                al.append(neighborsIt.next());
            }
            this.adjacenceLists.put(node, al);
        }
    }

    /**
     * The graph is triangulated the following way: - Check the adjacence list
     * of every node, if every neighbor is connected to the following neighbor.
     * - If not, insert an edge between them. - Every time inserting an edge,
     * update the adjacence lists of the neighbors. Additionally gets the outer
     * nodes of the graph.
     */
    private void triangulate() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node currentNode = nodesIt.next();
            HashList<Node> al = this.adjacenceLists.get(currentNode);
            Iterator<Node> iterator = al.iterator();
            while (iterator.hasNext()) {
                Node node = iterator.next();
                Node neighbor = al.getNextNeighbor(node);

                if (this.graph.getEdges(node, neighbor).isEmpty()) {
                    this.addedEdges.add(this.graph.addEdge(node, neighbor,
                            false));
                    this.adjacenceLists.get(node).addBefore(currentNode,
                            neighbor);
                    this.adjacenceLists.get(neighbor).addAfter(currentNode,
                            node);
                }
            }
        }
    }

    /**
     * The faces of the graph are calculated and saved in two
     * <code>HashMap</code>s (<code>facesByNodes</code> and
     * <code>facesByEdges</code>)so that every node has acces to all faces he
     * belongs to and every edge has acces to both faces it belongs to. Finally
     * all faces are saved in a <code>HashList</code> (<code>faces</code>)
     */
    private void initializeFaces() {
        // Initialize the lists of faces for every node and edge
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            this.facesByNodes.put(n, new LinkedList<Face>());
        }
        while (edgesIt.hasNext()) {
            Edge e = edgesIt.next();
            this.facesByEdges.put(e, new LinkedList<Face>());
        }

        // Check the neighbors of every node and create the faces
        HashSet<Node> finishedNodes = new HashSet<Node>();
        nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n1 = nodesIt.next();
            HashList<Node> n1List = this.adjacenceLists.get(n1);
            Iterator<Node> alIterator = n1List.iterator();
            while (alIterator.hasNext()) {
                Node n2 = alIterator.next();
                Node n3 = n1List.getNextNeighbor(n2);
                // If there is no outer face, save this one as the outer face
                if (this.outerNodes[0] == null) {
                    this.outerNodes[0] = n1;
                    this.outerNodes[1] = n2;
                    this.outerNodes[2] = n3;
                }
                // check if this faces was already saved before
                if (!(finishedNodes.contains(n1))
                        && !(finishedNodes.contains(n2))
                        && !(finishedNodes.contains(n3))) {
                    Edge e1 = this.graph.getEdges(n1, n2).iterator().next();
                    Edge e2 = this.graph.getEdges(n2, n3).iterator().next();
                    Edge e3 = this.graph.getEdges(n3, n1).iterator().next();
                    Face f = new Face(n1, n2, n3, e1, e2, e3, this);
                    this.facesByNodes.get(n1).add(f);
                    this.facesByNodes.get(n2).add(f);
                    this.facesByNodes.get(n3).add(f);
                    this.facesByEdges.get(e1).add(f);
                    this.facesByEdges.get(e2).add(f);
                    this.facesByEdges.get(e3).add(f);
                    this.faces.append(f);
                }
            }
            finishedNodes.add(n1);
        }
    }

    /**
     * As it happens quite often during the algorithm that counters for each
     * node have to be saved e.g. in an array, this method creates a mapping of
     * each node towards an unique integer value, which indicates its position
     * in the array.
     */
    protected void createNodeIndex() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        int i = 0;
        while (nodesIt.hasNext()) {
            this.nodeIndex.put(nodesIt.next(), new Integer(i));
            i++;
        }
    }
}
