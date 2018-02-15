package org.graffiti.plugins.algorithms.triangulation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.SchnyderRealizer.HashList;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * This class traingulates a graph the following way: - Save the planar
 * embedding of the graph in <code>HashList</code>s (to perform update
 * operations in constant time)- Check the adjacence list of every node, if
 * every neighbor is connected to the following neighbor. - If not, insert an
 * edge between them. - Every time inserting an edge, update the adjacence lists
 * of the neighbors. Additionally gets the outer nodes of the graph.
 * 
 * @author hofmeier
 */
public class BushyTriangulation implements TriangulationAlgorithm {

    /** The graph to be traingulated */
    private Graph graph;

    /** The algorithm, that calculates a planar embedding */
    private PlanarityAlgorithm pAlgorithm;

    /** The planar embedding is saved in here */
    private HashMap<Node, HashList<Node>> adjacenceLists = new HashMap<Node, HashList<Node>>();

    /**
     * Creates a new instance of the class
     * 
     * @param g
     *            the graph to be triangulated
     * @param p
     *            the algorithm, that calculates a planar embedding
     */
    public BushyTriangulation(Graph g, PlanarityAlgorithm p) {
        this.graph = g;
        this.pAlgorithm = p;
    }

    /**
     * Performs the triangulation as described above
     * 
     * @return returns all inserted edges
     */
    public LinkedList<Edge> triangulate() {
        LinkedList<Edge> addedEdges = new LinkedList<Edge>();

        // Save the planar embeddings in HashLists
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

        // Triangulate the graph
        nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node currentNode = nodesIt.next();
            HashList<Node> al = this.adjacenceLists.get(currentNode);
            Iterator<Node> iterator = al.iterator();
            while (iterator.hasNext()) {
                Node node = iterator.next();
                Node neighbor = al.getNextNeighbor(node);

                if (this.graph.getEdges(node, neighbor).isEmpty()) {
                    addedEdges.add(this.graph.addEdge(node, neighbor, false));
                    this.adjacenceLists.get(node).addBefore(currentNode,
                            neighbor);
                    this.adjacenceLists.get(neighbor).addAfter(currentNode,
                            node);
                }
            }
        }
        return addedEdges;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
