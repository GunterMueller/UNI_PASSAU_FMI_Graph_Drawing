// =============================================================================
//
//   DFSCircuitCounter.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.fas;

import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.SchnyderRealizer.HashList;

/**
 * @author hofmeier
 * @version $Revision$ $Date$
 */
public class DFSCircuitCounter {

    private Graph graph;
    private FASRelatedAlgorithms fra;

    private HashList<Edge> treeEdges = new HashList<Edge>();
    private HashList<Edge> backEdges = new HashList<Edge>();
    private HashList<Edge> forwardEdges = new HashList<Edge>();
    private HashList<Edge> crossEdges = new HashList<Edge>();
    private HashList<Node> unmarkedNodes = new HashList<Node>();
    private int[] dfsNum;
    private int[] compNum;
    private int currDFSNum = 0;
    private int currCompNum = 0;

    private int[] circuitsPerEdge;
    private DFSTree tree;

    public DFSCircuitCounter(Graph g, FASRelatedAlgorithms f) {
        this.graph = g;
        this.fra = f;
        this.dfsNum = new int[this.graph.getNodes().size()];
        this.compNum = new int[this.graph.getNodes().size()];
        this.circuitsPerEdge = new int[this.graph.getEdges().size()];
        this.tree = new DFSTree(this.graph, this.fra);
    }

    private void performDFS() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            unmarkedNodes.append(nodesIt.next());
        }
        while (!unmarkedNodes.isEmpty()) {
            Node startNode = unmarkedNodes.getFirst();
            dfs(startNode);
        }
        this.classifyRestOfEdges();
    }

    private void dfs(Node n) {
        this.unmarkedNodes.remove(n);
        this.dfsNum[fra.nodeToNumber(n)] = currDFSNum;
        currDFSNum++;
        Iterator<Node> neighborIt = n.getOutNeighborsIterator();
        while (neighborIt.hasNext()) {
            Node neighbor = neighborIt.next();
            if (this.unmarkedNodes.contains(neighbor)) {
                Edge e = this.graph.getEdges(n, neighbor).iterator().next();
                this.treeEdges.append(e);
                this.tree.addEdge(e);
                dfs(neighbor);
            }
        }
        this.compNum[fra.nodeToNumber(n)] = currCompNum;
        currCompNum++;
    }

    private void classifyRestOfEdges() {
        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        while (edgesIt.hasNext()) {
            Edge e = edgesIt.next();
            if (!this.treeEdges.contains(e)) {
                int sourceDFSNum = this.dfsNum[fra.nodeToNumber(e.getSource())];
                int targetDFSNum = this.dfsNum[fra.nodeToNumber(e.getTarget())];
                int sourceCompNum = this.compNum[fra
                        .nodeToNumber(e.getSource())];
                int targetCompNum = this.compNum[fra
                        .nodeToNumber(e.getTarget())];
                if (sourceDFSNum < targetDFSNum) {
                    this.forwardEdges.append(e);
                } else if (sourceCompNum < targetCompNum) {
                    this.backEdges.append(e);
                } else {
                    this.crossEdges.append(e);
                }
            }
        }
    }

    private void countCircuitsPerEdge() {
        this.countCircuitsForBackEdges();
    }

    private void countCircuitsForBackEdges() {
        Iterator<Edge> backEdgesIt = this.backEdges.iterator();
        while (backEdgesIt.hasNext()) {
            Edge backEdge = backEdgesIt.next();
            this.circuitsPerEdge[fra.edgeToNumber(backEdge)]++;
            LinkedList<Node> pathToRoot = this.tree.getPathToRoot(backEdge
                    .getSource());
            Iterator<Node> pathIt = pathToRoot.iterator();
            Node current = pathIt.next();
            while (pathIt.hasNext()) {
                Node n = pathIt.next();
                Edge e = this.graph.getEdges(current, n).iterator().next();
                this.circuitsPerEdge[fra.edgeToNumber(e)]++;
                current = n;
                if (n.equals(backEdge.getTarget())) {
                    break;
                }
            }
        }
    }

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
        this.performDFS();
        this.countCircuitsPerEdge();

    }
}
