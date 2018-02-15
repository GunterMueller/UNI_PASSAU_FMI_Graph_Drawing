// =============================================================================
//
//   DFSCircuitCounter.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.fas;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.FastNode;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.SchnyderRealizer.HashList;

/**
 * @author hofmeier
 * @version $Revision$ $Date$
 */
public class BFSCircuitCounter {

    private Graph graph;
    private FASRelatedAlgorithms fra;

    private HashList<Edge> treeEdges = new HashList<Edge>();
    private HashList<Edge> backEdges = new HashList<Edge>();
    private HashList<Edge> crossEdges = new HashList<Edge>();
    private HashList<Edge> forwardEdges = new HashList<Edge>();
    private HashList<Edge> insertedEdges = new HashList<Edge>();

    private int[] currentNodeValue;
    private int[] bfsLevel;
    private int[] circuitsPerEdge;

    public BFSCircuitCounter(Graph g, FASRelatedAlgorithms f) {
        this.graph = g;
        this.fra = f;
        this.circuitsPerEdge = new int[this.graph.getEdges().size()];
        this.currentNodeValue = new int[this.graph.getNodes().size()];
        this.bfsLevel = new int[this.graph.getNodes().size()];
    }

    // private void performBFS() {
    // Iterator<Node> nodesIt = this.graph.getNodesIterator();
    // while (nodesIt.hasNext()) {
    // Node n = nodesIt.next();
    // bfs(n);
    // calculateCurrentNodeValue(n);
    // }
    // }

    private void bfs(Node n) {
        Node dummy = new FastNode(this.graph);
        LinkedList<Node> queue = new LinkedList<Node>();
        HashSet<Node> done = new HashSet<Node>();
        done.add(n);
        queue.add(n);
        queue.add(dummy);
        int currentBFSLevel = 0;
        this.bfsLevel[fra.nodeToNumber(n)] = currentBFSLevel;
        while (!queue.isEmpty()) {

            Node node = queue.removeFirst();
            if (node.equals(dummy)) {
                if (queue.size() > 0) {
                    queue.add(dummy);
                    currentBFSLevel++;
                }
            } else {

                Iterator<Node> nodesIt = node.getOutNeighborsIterator();
                while (nodesIt.hasNext()) {
                    Node neighbor = nodesIt.next();
                    Edge e = this.graph.getEdges(node, neighbor).iterator()
                            .next();
                    if (!done.contains(neighbor)) {
                        done.add(neighbor);
                        queue.add(neighbor);
                        this.bfsLevel[fra.nodeToNumber(neighbor)] = currentBFSLevel;
                        this.treeEdges.append(e);
                    } else if (done.contains(neighbor)) {
                        if (this.bfsLevel[fra.nodeToNumber(node)] > this.bfsLevel[fra
                                .nodeToNumber(neighbor)]) {
                            this.backEdges.append(e);
                        } else if (this.bfsLevel[fra.nodeToNumber(node)] < this.bfsLevel[fra
                                .nodeToNumber(neighbor)]) {
                            this.forwardEdges.append(e);
                        } else {
                            this.crossEdges.append(e);
                        }
                    }
                }
            }
        }
        this.calculateCurrentNodeValue(n);
    }

    private void calculateCurrentNodeValue(Node n) {
        this.calculateForTreeEdges(n);
        this.calculateForForwardEdges(n);
        this.calculateForCrossEdges(n);
        this.calculateForBackEdges(n);
        this.calculateNumOfCycles(n);
        // calculateResult(n);
        for (int i = 0; i < this.currentNodeValue.length; i++) {
            System.out.print(this.currentNodeValue[i] + " - ");
        }
        System.out.print("\n");
    }

    private void calculateForTreeEdges(Node n) {
        Iterator<Edge> edgesIt = this.treeEdges.iterator();
        while (edgesIt.hasNext()) {
            Edge e = edgesIt.next();
            this.insertedEdges.append(e);
            Node target = e.getTarget();
            this.currentNodeValue[fra.nodeToNumber(target)]++;
        }
    }

    private void calculateForForwardEdges(Node n) {
        Iterator<Edge> edgesIt = this.forwardEdges.iterator();
        while (edgesIt.hasNext()) {
            Edge e = edgesIt.next();
            this.insertedEdges.append(e);
            this.findNodesForIncrement(e.getSource(), e.getTarget(), n);
        }
    }

    private void calculateForCrossEdges(Node n) {
        Iterator<Edge> edgesIt = this.crossEdges.iterator();
        while (edgesIt.hasNext()) {
            Edge e = edgesIt.next();
            this.insertedEdges.append(e);
            this.findNodesForIncrement(e.getSource(), e.getTarget(), n);
        }
    }

    private void calculateForBackEdges(Node n) {
        Iterator<Edge> edgesIt = this.backEdges.iterator();
        while (edgesIt.hasNext()) {
            Edge e = edgesIt.next();
            this.insertedEdges.append(e);
            this.findNodesForIncrement(e.getSource(), e.getTarget(), n);
        }
    }

    private void calculateNumOfCycles(Node n) {
        Iterator<Node> neighborIt = n.getInNeighborsIterator();
        while (neighborIt.hasNext()) {
            Node neighbor = neighborIt.next();
            int neighborValue = this.currentNodeValue[fra
                    .nodeToNumber(neighbor)];
            this.currentNodeValue[fra.nodeToNumber(n)] += neighborValue;
        }
    }

    private void findNodesForIncrement(Node source, Node target, Node n) {
        LinkedList<Node> queue = new LinkedList<Node>();
        HashList<Node> done = new HashList<Node>();
        if (target.equals(n))
            return;
        int increase = this.currentNodeValue[fra.nodeToNumber(source)];
        queue.add(target);
        done.append(source);
        done.append(target);
        while (!queue.isEmpty()) {
            Node node = queue.removeFirst();
            this.currentNodeValue[fra.nodeToNumber(node)] += increase;
            Iterator<Edge> neighborIt = node.getAllOutEdges().iterator();
            while (neighborIt.hasNext()) {
                Edge edge = neighborIt.next();
                if (!(done.contains(edge.getTarget()))
                        && this.insertedEdges.contains(edge)) {
                    if (!edge.getTarget().equals(n)) {
                        queue.add(edge.getTarget());
                        done.append(edge.getTarget());
                    }
                }
            }
        }
    }

    // private void calculateResult(Node n) {
    // LinkedList<Node> queue = new LinkedList<Node>();
    // queue.add(n);
    // while (!queue.isEmpty()) {
    // Node node = queue.removeFirst();
    // Iterator<Edge> inEdgeIt = node.getDirectedInEdgesIterator();
    // while (inEdgeIt.hasNext()) {
    // Edge e = inEdgeIt.next();
    // Node source = e.getSource();
    // this.circuitsPerEdge[fra.edgeToNumber(e)] +=
    // this.currentNodeValue[fra.nodeToNumber(source)];
    // if (!source.equals(n)) {
    // queue.add(source);
    // }
    // }
    // }
    // }

    public HashList<Edge> getBackEdges() {
        return backEdges;
    }

    public HashList<Edge> getTreeEdges() {
        return treeEdges;
    }

    public HashList<Edge> getCrossEdges() {
        return crossEdges;
    }

    public HashList<Edge> getForwardEdges() {
        return forwardEdges;
    }

    public int[] getResult() {
        return this.circuitsPerEdge;
    }

    public void execute() {
        this.bfs(fra.numberToNode(0));

    }
}
