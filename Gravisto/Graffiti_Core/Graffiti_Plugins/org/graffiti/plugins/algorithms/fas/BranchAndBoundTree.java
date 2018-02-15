package org.graffiti.plugins.algorithms.fas;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;

public class BranchAndBoundTree {

    private HashMap<Node, Integer> nodeNumber = new HashMap<Node, Integer>();
    private HashMap<Edge, Integer> edgeNumber = new HashMap<Edge, Integer>();
    private Graph graph;
    private LinkedList<Edge> result = new LinkedList<Edge>();
    private int[][] incMatrix;
    private int[][] adjMatrix;

    public BranchAndBoundTree(Graph g, FASRelatedAlgorithms alg) {
        this.graph = g;
        this.createMatrix();
    }

    public LinkedList<Edge> getResult() {
        return result;
    }

    public void execute() {
        int counter = 0;
        LinkedList<BBNode> queue = new LinkedList<BBNode>();
        BBNode source = new BBNode(new LinkedList<Edge>());
        queue.add(source);
        while (!queue.isEmpty()) {
            counter++;
            BBNode next = queue.removeFirst();
            if (counter < 25) {
                this.printEdges(next.removedEdges);
            }
            this.prepareGraph(next.removedEdges);
            if (!this.containsCycle()) {
                this.result = next.removedEdges;
            }
            this.restoreGraph(next.removedEdges, queue);
        }
    }

    private void prepareGraph(LinkedList<Edge> edges) {
        Iterator<Edge> edgesIt = edges.iterator();
        while (edgesIt.hasNext()) {
            this.removeEdge(edgesIt.next());
        }
    }

    private void restoreGraph(LinkedList<Edge> edges, LinkedList<BBNode> queue) {
        Iterator<Edge> graphEdgesIt = this.graph.getEdgesIterator();
        while (graphEdgesIt.hasNext()) {
            LinkedList<Edge> removedEdgesNew = new LinkedList<Edge>();
            removedEdgesNew.addAll(edges);
            removedEdgesNew.add(graphEdgesIt.next());
            queue.add(new BBNode(removedEdgesNew));
        }
        Iterator<Edge> edgesIt = edges.iterator();
        while (edgesIt.hasNext()) {
            this.insertEdge(edgesIt.next());
        }
    }

    private void printEdges(LinkedList<Edge> edges) {
        Iterator<Edge> graphEdgesIt = edges.iterator();
        while (graphEdgesIt.hasNext()) {
            Edge e = graphEdgesIt.next();

            EdgeLabelAttribute edgeLabel = new EdgeLabelAttribute("label");
            try {
                edgeLabel = (EdgeLabelAttribute) e.getAttributes()
                        .getAttribute("label");
                System.out.print(edgeLabel.getLabel() + " - ");
            } catch (AttributeNotFoundException ex) {
            }
        }
        System.out.print("\n");
    }

    private boolean containsCycle() {
        int[] inDegree = new int[this.graph.getNodes().size()];
        LinkedList<Integer> sources = new LinkedList<Integer>();
        for (int i = 0; i < this.graph.getNodes().size(); i++) {
            for (int j = 0; j < this.incMatrix[i].length; j++) {
                if (this.incMatrix[i][j] == -1) {
                    inDegree[i]++;
                }
            }
            if (inDegree[i] == 0) {
                sources.add(new Integer(i));
            }
        }

        for (int i = 0; i < this.graph.getNodes().size(); i++) {

            if (sources.isEmpty())
                return true;
            Integer source = sources.remove(0);
            for (int j = 0; j < this.adjMatrix.length; j++) {
                if (this.adjMatrix[source.intValue()][j] == 1) {
                    inDegree[j]--;
                    if (inDegree[j] == 0) {
                        sources.add(new Integer(j));
                    }
                }
            }
        }
        return false;
    }

    private void createMatrix() {
        this.incMatrix = new int[this.graph.getNodes().size()][this.graph
                .getEdges().size()];
        this.adjMatrix = new int[this.graph.getNodes().size()][this.graph
                .getNodes().size()];
        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        while (edgesIt.hasNext()) {
            Edge e = edgesIt.next();
            Node source = e.getSource();
            Node target = e.getTarget();
            this.incMatrix[this.nodeNumber.get(source)][this.edgeNumber.get(e)] = -1;
            this.incMatrix[this.nodeNumber.get(target)][this.edgeNumber.get(e)] = 1;
            this.adjMatrix[this.nodeNumber.get(source)][this.nodeNumber
                    .get(target)] = 1;
            this.adjMatrix[this.nodeNumber.get(target)][this.nodeNumber
                    .get(source)] = 1;
        }
    }

    private void insertEdge(Edge e) {
        int source = this.nodeNumber.get(e.getSource()).intValue();
        int target = this.nodeNumber.get(e.getTarget()).intValue();
        int edge = this.edgeNumber.get(e).intValue();

        this.adjMatrix[source][target] = 1;
        this.adjMatrix[target][source] = 1;

        this.incMatrix[source][edge] = -1;
        this.incMatrix[target][edge] = 1;
    }

    private void removeEdge(Edge e) {
        int source = this.nodeNumber.get(e.getSource()).intValue();
        int target = this.nodeNumber.get(e.getTarget()).intValue();
        int edge = this.edgeNumber.get(e).intValue();

        this.adjMatrix[source][target] = 0;
        this.adjMatrix[target][source] = 0;

        this.incMatrix[source][edge] = 0;
        this.incMatrix[target][edge] = 0;
    }

    private class BBNode {

        private LinkedList<Edge> removedEdges;

        public BBNode(LinkedList<Edge> rE) {
            this.removedEdges = rE;
        }
    }
}
