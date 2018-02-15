package org.graffiti.plugins.algorithms.fas;

import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.SchnyderRealizer.HashList;

/**
 * @author hofmeier
 * @version $Revision$ $Date$
 */
public class FindElementaryCircuits {
    private Graph graph;
    private FASRelatedAlgorithms alg;
    private HashList<Node> currPath;
    private boolean[][] finishedNodes;
    private int[] circuitsPerEdge;
    private int counter = 0;

    public FindElementaryCircuits(Graph g, FASRelatedAlgorithms a) {
        this.graph = g;
        this.alg = a;
        this.circuitsPerEdge = new int[this.graph.getEdges().size()];
    }

    public int[] getResult() {
        return circuitsPerEdge;
    }

    private void reset() {
        this.currPath = new HashList<Node>();
        int numOfNodes = this.graph.getNodes().size();
        this.finishedNodes = new boolean[numOfNodes][numOfNodes];
    }

    private void storeCircuit() {
        this.counter++;
        boolean contains = false;
        Iterator<Node> nodesIt = this.currPath.iterator();
        while (nodesIt.hasNext()) {
            int nodeNumber = alg.nodeToNumber(nodesIt.next());
            if (nodeNumber == 0) {
                contains = true;
            }
        }
        if (contains) {
            while (nodesIt.hasNext()) {
                Node node = nodesIt.next();
                Node predecessor = this.currPath.getPredecessor(node);
                Edge e = this.graph.getEdges(node, predecessor).iterator()
                        .next();
                this.circuitsPerEdge[alg.edgeToNumber(e)]++;
            }
        }
    }

    private boolean extendPath() {
        Node node = this.currPath.getPredecessor(this.currPath.getFirst());
        Iterator<Node> nodesIt = node.getOutNeighborsIterator();
        while (nodesIt.hasNext()) {
            Node neighbor = nodesIt.next();
            if (this.currPath.contains(neighbor)) {
                continue;
            }
            int numberFirstNode = alg.nodeToNumber(this.currPath.getFirst());
            int numberNeighbor = alg.nodeToNumber(neighbor);
            int numberNode = alg.nodeToNumber(node);
            if (numberNeighbor < numberFirstNode) {
                continue;
            }
            if (this.finishedNodes[numberNode][numberNeighbor]) {
                continue;
            }
            this.currPath.append(neighbor);
            return true;
        }
        return false;
    }

    private void reducePath() {
        this.outputCircuit();
        Node last = this.currPath.getPredecessor(this.currPath.getFirst());
        int lastNumber = alg.nodeToNumber(last);
        int lastPredNumber = alg.nodeToNumber(this.currPath
                .getPredecessor(last));
        for (int i = 0; i < this.graph.getNodes().size(); i++) {
            this.finishedNodes[lastNumber][i] = false;
        }
        this.finishedNodes[lastPredNumber][lastNumber] = true;
        this.currPath.remove(last);

    }

    private void outputCircuit() {
        Node first = this.currPath.getFirst();
        Node last = this.currPath.getPredecessor(first);
        if (!this.graph.getEdges(first, last).isEmpty()) {
            Iterator<Edge> edgesIt = this.graph.getEdges(first, last)
                    .iterator();
            while (edgesIt.hasNext()) {
                Edge e = edgesIt.next();
                if (e.getSource().equals(last)) {
                    this.storeCircuit();
                    break;
                }
            }
        }
    }

    public void execute() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < this.graph.getNodes().size(); i++) {
            this.reset();
            this.currPath.append(alg.numberToNode(i));
            while (!this.currPath.isEmpty()) {
                while (this.extendPath()) {
                }
                this.reducePath();
            }
        }
        System.out.println(counter);
        long runTime = System.currentTimeMillis() - start;
        System.out.println("Zeit: " + runTime);
    }
}
