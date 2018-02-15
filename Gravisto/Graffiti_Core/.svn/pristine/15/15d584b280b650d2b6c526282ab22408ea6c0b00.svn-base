package org.graffiti.plugins.algorithms.kandinsky;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.fpp.Face;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * Calculates all faces of a connected graph. The graph is loopfree and has no
 * multi edges. This class calculates also the left/right faces for each edge.
 * 
 * @author Christof K�nig
 */

public class CalculateFace {

    /**
     * Mapping between nodes and internal edges.
     */
    LinkedHashMap<Pair<Node>, IntEdge> mapping;

    /**
     * Set of calculated faces of the graph.
     */
    Face[] faces;

    /**
     * Mapping between edges and their left faces.
     */
    Hashtable<Edge, Face> edgeLeftFace = new Hashtable<Edge, Face>();

    /**
     * Mapping between edges and their right faces.
     */
    Hashtable<Edge, Face> edgeRightFace = new Hashtable<Edge, Face>();

    /**
     * Creates a new instance to calculate all faces of a connected graph.
     * 
     * @param graph
     *            Graph to calculate faces for.
     * @param tGraph
     *            Provides adjacency lists for each node in the graph.
     */
    public CalculateFace(Graph graph, TestedGraph tGraph) {
        // since there is no way to check whether tGraph belongs to graph
        // we just have to believe that

        // check for loops
        if (tGraph.getNumberOfLoops() > 0)
            throw new IllegalArgumentException("Graph is not loopfree.");
        // check for multi edges
        if (tGraph.getNumberOfDoubleEdges() > 0)
            throw new IllegalArgumentException("Multi-Edges are not allowed.");

        // initialize all internal edges
        mapping = new LinkedHashMap<Pair<Node>, IntEdge>();

        for (Edge e : graph.getEdges()) {
            Node source = e.getSource();
            Node target = e.getTarget();
            IntEdge st = new IntEdge(e, source);
            IntEdge ts = new IntEdge(e, target);
            st.setReverse(ts);
            ts.setReverse(st);
            mapping.put(getKey(source, target), st);
            mapping.put(getKey(target, source), ts);
        }

        // iterate through all nodes and create our internal representation
        // of the graph
        for (Node n : graph.getNodes()) {
            IntNode in = new IntNode(n);

            for (Object o : tGraph.getAdjacencyList(n)) {
                Node a = (Node) o;
                in.add(mapping.get(getKey(a, n)));
            }
        }
    }

    // public methods which are used from outside

    /**
     * Get all faces of the graph.
     * 
     * @return Array of all faces.
     */
    public synchronized Face[] getFaces() {
        if (faces != null)
            return faces;

        Set<IntFace> intFaces = calculateFaces();
        faces = new Face[intFaces.size()];

        int index = 0;
        for (IntFace face : intFaces) {
            faces[index++] = transform(face);
        }

        return faces;
    }

    /**
     * By default use the first face as outer face.
     * 
     * @return Return index of the first face.
     */
    public int getOutIndex() {
        assert faces != null;
        int index = 0;
        int max = faces[index].getNodelist().size();

        for (int i = 1; i < faces.length; i++)
            if (max < faces[i].getNodelist().size()) {
                index = i;
                max = faces[i].getNodelist().size();
            }

        return index;
    }

    /**
     * Return a mapping between edges and their left faces.
     * 
     * @return Mapping between edges and their left faces.
     */
    public Hashtable<Edge, Face> getEdgeLeftFace() {
        assert faces != null;
        return edgeLeftFace;
    }

    /**
     * Return a mapping between edges and their right faces.
     * 
     * @return Mapping between edges and their right faces.
     */
    public Hashtable<Edge, Face> getEdgeRightFace() {
        assert faces != null;
        return edgeRightFace;
    }

    // internal methods to compute the faces.

    /**
     * Transform an internal face into a Face object.
     * 
     * @param face
     *            Internal face object.
     * @return Face object for internal face object.
     */
    private Face transform(IntFace face) {
        LinkedList<Node> nodes = new LinkedList<Node>();
        LinkedList<Edge> edges = new LinkedList<Edge>();

        for (IntEdge edge : face.edges) {
            edges.add(edge.originalEdge);
            nodes.add(edge.source);
        }

        Face result = new Face(nodes, edges);

        for (IntEdge edge : face.edges) {
            if (edge.isReversed()) {
                edgeLeftFace.put(edge.originalEdge, result);
            } else {
                edgeRightFace.put(edge.originalEdge, result);
            }
        }

        return result;
    }

    /**
     * Calculate all faces of the graph.
     * 
     * @return Set of all faces.
     */
    private Set<IntFace> calculateFaces() {
        Set<IntFace> faces = new LinkedHashSet<IntFace>();

        for (IntEdge e : mapping.values()) {
            if (e.isDone()) {
                continue;
            }
            faces.add(calculateFace(e));
        }

        return faces;
    }

    /**
     * Calculate face starting at the given edge.
     * 
     * @param start
     *            Starting edge of face.
     * @return Face starting at the given edge.
     */
    private IntFace calculateFace(IntEdge start) {
        IntFace face = new IntFace();
        IntEdge current = start;

        do {
            face.addEdge(current);
            current.setDone(true);
            current = current.nextEdge();
        } while (current != start);

        return face;
    }

    /**
     * Create a new pair used as key for the mapping.
     * 
     * @param source
     *            Source node.
     * @param target
     *            Target node.
     * @return Pair containing source and target.
     */
    private final Pair<Node> getKey(Node source, Node target) {
        return new Pair<Node>(source, target);
    }

    /**
     * Internal representation of a face.
     */
    private class IntFace {
        /** List of edges definig a face. */
        private List<IntEdge> edges = new LinkedList<IntEdge>();

        /**
         * Add a new edge to the list of edges defining this face.
         * 
         * @param edge
         *            Additional edge for this face.
         */
        void addEdge(IntEdge edge) {
            edges.add(edge);
        }
    }

    /**
     * Internal representation of a node.
     */
    private class IntNode {
        /** Ordered list of edges around the node. */
        private ArrayList<IntEdge> adjList;

        /**
         * Create a new internal node.
         * 
         * @param node
         *            Node represented by this internal node.
         */
        IntNode(Node node) {
            adjList = new ArrayList<IntEdge>(node.getEdges().size());
        }

        /** Add new edge to this node. */
        void add(IntEdge edge) {
            adjList.add(edge);
            edge.added(this, adjList.size() - 1);
        }

        /** Get predecessor in list of edges. */
        IntEdge getPred(int position) {
            assert position >= 0 && position < adjList.size();
            if (position == 0) {
                position = adjList.size();
            }
            int pred = position - 1;
            return adjList.get(pred);
        }
    }

    /**
     * Internal representation of an edge.
     */
    private class IntEdge {
        /** Reference to original edge. */
        private Edge originalEdge;

        /** The reverse edge. */
        private IntEdge reverse;

        /** Source of internal edge. */
        private Node source;

        /** Face for edge already calculated? */
        private boolean done;

        /** Internal target node. */
        private IntNode targetNode;

        /** Position of edge in target node. */
        private int targetPosition;

        /** Create a new internal edge. */
        IntEdge(Edge edge, Node source) {
            done = false;
            originalEdge = edge;
            this.source = source;
        }

        /**
         * Check whether internal edge is an original edge or a reversed one.
         */
        boolean isReversed() {
            return originalEdge.getSource() != source;
        }

        // Node getTarget()
        // {
        // return isReversed() ? originalEdge.getSource() : originalEdge
        // .getTarget();
        // }

        boolean isDone() {
            return done;
        }

        void setDone(boolean done) {
            this.done = done;
        }

        void setReverse(IntEdge reverse) {
            this.reverse = reverse;
        }

        void added(IntNode node, int position) {
            targetNode = node;
            targetPosition = position;
        }

        IntEdge nextEdge() {
            IntNode sourceNode = reverse.targetNode;
            int sourcePosition = reverse.targetPosition;
            return sourceNode.getPred(sourcePosition);
        }
    }

    public class Pair<T> {
        private T fst;

        private T snd;

        public Pair(T fst, T snd) {
            this.fst = fst;
            this.snd = snd;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null)
                return false;
            if (this.getClass() != o.getClass())
                return false;
            Pair<?> other = (Pair<?>) o;
            return this.fst.equals(other.fst) && this.snd.equals(other.snd);
        }

        @Override
        public int hashCode() {
            return fst.hashCode() + snd.hashCode();
        }
    }
}
