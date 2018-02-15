package org.graffiti.plugins.algorithms.mnn;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * This class calculates an embedding of the graph
 * 
 * @author Thomas Ortmeier
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class EmbeddedGraph {
    // The graph
    private Graph graph;

    // List of all faces of the embedding
    private List<Face> faces = new LinkedList<Face>();

    // The tested graph of the planarity algorithm
    private TestedGraph testedGraph;

    // The exterior face
    private Face exteriorface = null;

    // The added egdes if the graph hab been triangulated
    private List<Edge> addedEdges = new LinkedList<Edge>();

    /**
     * Constructor
     * 
     * @param graph
     */
    public EmbeddedGraph(Graph graph) {

        this.graph = graph;

        PlanarityAlgorithm planarityAlgorithm = new PlanarityAlgorithm();

        planarityAlgorithm.attach(graph);
        testedGraph = planarityAlgorithm.getTestedGraph();

        calculateFaces();

    }

    /**
     * returns a list of all inner faces
     * 
     * @return the inner faces
     */
    public List<Face> getInnerFaces() {
        List<Face> result = new LinkedList<Face>();

        for (Face f : faces) {
            if (!f.equals(exteriorface)) {
                result.add(f);
            }
        }
        return result;
    }

    /**
     * returns a list of all inner faces for a special node
     * 
     * @param n
     *            the node
     * @return the inner faces
     */
    public List<Face> getInnerFaces(Node n) {
        List<Face> result = new LinkedList<Face>();

        for (Face f : faces) {
            if (!f.equals(exteriorface) && f.contains(n)) {
                result.add(f);
            }
        }
        return result;
    }

    /**
     * resets the
     * 
     */
    private void reset() {
        faces = new LinkedList<Face>();
        exteriorface = null;
        PlanarityAlgorithm planarityAlgorithm = new PlanarityAlgorithm();
        planarityAlgorithm.attach(graph);
        testedGraph = planarityAlgorithm.getTestedGraph();
    }

    /**
     * returns the nodes of an inner face for a given edge
     * 
     * @param e
     *            the edge
     * @return the nodes on the inner face for a given edge
     */
    public LinkedList<Node> getInnerFace(Edge e) {

        Face[] f = getFace(e);

        if (f[0].equals(exteriorface))
            return f[1].getNodelist();
        else
            return f[0].getNodelist();

    }

    /**
     * Returns the number of faces of the embedding of the graph
     * 
     * @return the number of faces
     */
    public int getNumberOfFaces() {
        return graph.getNumberOfEdges() + 2 - graph.getNumberOfNodes();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String string = "Number of faces: " + getNumberOfFaces() + "\n";
        string += "Exterior face: " + exteriorface + "\n\n";
        Iterator<Face> it = faces.iterator();
        while (it.hasNext()) {
            Face face = it.next();
            string += face.toString() + "\n";
        }
        return string;
    }

    /**
     * Returns the exterior face
     */
    public Face getExteriorFace() {
        return exteriorface;
    }

    public boolean isExteriorFace(Face f) {

        if (f == exteriorface)
            return true;
        return false;
    }

    /**
     * returns the graph
     * 
     * @return the graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Returns a LinkedList of the adjacent nodes of a node
     * 
     * @param node
     * @return the adjazent nodes in the clockwise order
     */
    public LinkedList<Node> getAdjacentNodes(Node node) {
        return ((LinkedList<Node>) (testedGraph.getTestedComponents().get(0))
                .getAdjacencyList(node));
    }

    /**
     * Returs a list of all added edges for the triangulation
     * 
     * @return the added edges
     */
    public List<Edge> getAddedEdges() {
        return addedEdges;
    }

    /**
     * Calculates all faces of a planar Graph
     */
    public void calculateFaces() {
        Iterator<Edge> it = graph.getEdgesIterator();

        // Number the faces for debugging
        int number = 1;

        // for all edges...
        while (it.hasNext()) {
            Edge current = it.next();

            // ... calculte the face to the left of the direction of the edge
            Face face = getFace(current, true);
            if (!faces.contains(face)) {
                // set one face with at least 4 nodes as the exterior face
                if (exteriorface == null && face.getNumberOfNodes() >= 4) {
                    exteriorface = face;
                }

                // add the face to the facelist
                faces.add(face);
                face.setNumber(number);
                number++;
            }

            // ... and the on to the right
            face = getFace(current, false);
            if (!faces.contains(face)) {
                // set one face with at least 4 nodes as the exterior face
                if (exteriorface == null && face.getNumberOfNodes() >= 4) {
                    exteriorface = face;
                }

                // add the face to the facelist
                faces.add(face);
                face.setNumber(number);
                number++;
            }
        }
    }

    /**
     * Returns true, if the graph is internally triangulated
     * 
     * @return true, if the graph is (internally) triangulated
     */
    public boolean isTriangulated() {
        return false;
    }

    /**
     * Triagulates the planar Graph internally
     */
    public void triangulate() {
        exteriorface = null;

        Iterator<Face> it = faces.iterator();
        while (it.hasNext()) {

            Face face = it.next();

            // is not already a triangle
            if (face.getNumberOfNodes() > 3) {

                LinkedList<Node> nodeList = face.getNodelist();

                // while (nodelist.size() > 3) {
                for (int i = 0; i < nodeList.size(); i++) {

                    // set one face with 4 nodes as the exterior face
                    if (exteriorface == null && nodeList.size() == 4) {
                        exteriorface = new Face(nodeList);
                        break;

                    } else {
                        Node node1 = nodeList.get(i);
                        Node node3 = nodeList.get((i + 2) % nodeList.size());

                        Collection<Edge> edgeNode1Node3 = graph.getEdges(node1,
                                node3);

                        if (edgeNode1Node3.size() == 0) {

                            // add an undireced edge between node 1 and node 3
                            Edge e = graph.addEdge(node1, node3, false);
                            addedEdges.add(e);
                            nodeList.remove((i + 1) % nodeList.size());
                            i--;

                        }

                    }

                }

            }

        }
        reset();
        calculateFaces();
    }

    /**
     * Returns the face adjacent to an edge in counterclockwise orientation
     * 
     * @param edge
     * @param ahead
     * @return the face
     */
    private Face getFace(Edge edge, boolean ahead) {

        Node source;
        Node next;
        Node current;

        LinkedList<Node> nodeList = new LinkedList<Node>();
        LinkedList<Edge> edgeList = new LinkedList<Edge>();
        LinkedList<Node> adjacentNodes;

        if (ahead) {
            source = edge.getSource();
            next = edge.getTarget();
        } else {
            next = edge.getSource();
            source = edge.getTarget();
        }

        current = source;
        nodeList.add(source);

        edgeList.add(edge);

        // iterate around the face
        while (source != next) {
            nodeList.add(next);

            // Find next node
            adjacentNodes = getAdjacentNodes(next);

            Iterator<Node> i = adjacentNodes.iterator();
            Node node = null;
            while (i.hasNext() && (node != current)) {
                node = i.next();
            }

            current = next;

            if (i.hasNext()) {
                next = i.next();
            } else {
                next = adjacentNodes.getFirst();
            }

            for (Edge e : graph.getEdges(current, next)) {
                edgeList.add(e);
            }

        }

        return new Face(nodeList, edgeList);
    }

    /**
     * returns the two faces lying on the Edge e
     * 
     * @param e
     *            the edge
     * @return the two faces of e
     */
    public Face[] getFace(Edge e) {
        Face[] f = new Face[2];
        f[0] = getFace(e, true);
        f[1] = getFace(e, false);
        return f;
    }
}
