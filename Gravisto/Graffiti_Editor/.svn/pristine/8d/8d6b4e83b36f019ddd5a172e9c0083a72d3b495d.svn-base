/*
 * Created on Dec 14, 2005
 *
 */

package org.graffiti.plugins.algorithms.springembedderFR;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.selection.Selection;

/**
 * Class where the FRNodes and FREdges are saved
 * 
 * @author matzeder
 */
public class FRGraph {

    /**
     * FRNodes of the FRGraph
     */
    private HashSet<FRNode> fRNodes;

    /**
     * FREdges of the FRGraph
     */
    private HashSet<FREdge> fREdges;

    /**
     * Map where original nodes are saved as Nodes and values are FRNodes
     */
    private HashMap<Node, FRNode> fRNodesMap;

    /**
     * Creates a new FRGraph with empty HashSets nodes and edges
     * 
     * @param graph
     */
    public FRGraph(Graph graph, Selection sel) {
        // longestEdge = 0.0;
        fRNodesMap = new HashMap<Node, FRNode>();

        this.fRNodes = new HashSet<FRNode>();

        // adds all nodes of the original graph to this FRGraph
        Iterator<Node> nodesIt = graph.getNodesIterator();
        while (nodesIt.hasNext()) {

            // original node
            Node originalNode = nodesIt.next();

            boolean movable = false;

            // keine Knoten markiert, also auf ganzen graphen anwenden
            if (sel.getElements().size() == 0) {
                movable = true;
            } else {
                // this node is movable
                if (sel.contains(originalNode)) {
                    movable = true;
                }
            }

            // new FRNode
            FRNode fRNode = new FRNode(originalNode, movable);
            // inserts the original node as key and the FRNode as value
            fRNodesMap.put(originalNode, fRNode);
            // adds the FRNode to HashSet fRNodes
            fRNodes.add(fRNode);

        }

        this.fREdges = new HashSet<FREdge>();
        // adds all edges of the original graph to this FRGraph
        Iterator<Edge> edgesIt = graph.getEdgesIterator();
        while (edgesIt.hasNext()) {

            // original edge
            Edge originalEdge = edgesIt.next();

            // source
            Node source = originalEdge.getSource();
            // appropriate FRNode
            FRNode fRSource = fRNodesMap.get(source);

            // target
            Node target = originalEdge.getTarget();
            // appropriate FRNode
            FRNode fRTarget = fRNodesMap.get(target);

            FREdge fREdge = new FREdge(originalEdge, fRSource, fRTarget);

            // double deltaX = fRTarget.getXPos() - fRSource.getXPos();
            // double deltaY = fRTarget.getYPos() - fRSource.getYPos();
            //
            // if (longestEdge < deltaX * deltaX + deltaY * deltaY)
            // {
            // longestEdge = deltaX * deltaX + deltaY * deltaY;
            // }

            // adds the FREdge to HashSet fREdges
            fREdges.add(fREdge);

        }
    }

    /**
     * Returns the Iterator of the HashSet fRNodes
     * 
     * @return Iterator over the nodes of the FRGraph
     */
    public Iterator<FRNode> getFRNodesIterator() {

        return fRNodes.iterator();

    }

    /**
     * Returns the HashSet fRNodes
     * 
     * @return fRNodes
     */
    public HashSet<FRNode> getFRNodes() {

        return fRNodes;
    }

    /**
     * Returns the Iterator of the HashSet fREdges
     * 
     * @return fREdges
     */
    public Iterator<FREdge> getFREdgesIterator() {

        return fREdges.iterator();

    }

    /**
     * Returns the HashSet fREdges
     * 
     * @return fREdges
     */
    public HashSet<FREdge> getFREdges() {

        return fREdges;

    }

    /**
     * Returns the HashMap fRNodesMap (mapping between original nodes and
     * FRNodes)
     * 
     * @return fRNodesMap
     */
    public HashMap<Node, FRNode> getFRNodesMap() {
        return fRNodesMap;
    }

    // /**
    // * Returns the longestEdge.
    // *
    // * @return the longestEdge.
    // */
    // public double getLongestEdge()
    // {
    // return longestEdge;
    // }
    //
    // /**
    // * Sets the longestEdge.
    // *
    // * @param longestEdge the longestEdge to set.
    // */
    // public void setLongestEdge(double longestEdge)
    // {
    // this.longestEdge = longestEdge;
    // }
    //
    // /**
    // * Resets the length of the longest edge.
    // */
    // public void resetLongestEdge()
    // {
    // setLongestEdge(0.0);
    // }

}
