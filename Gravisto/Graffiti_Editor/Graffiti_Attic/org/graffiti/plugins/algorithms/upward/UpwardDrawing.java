/**
 * this is a algorithm that drawing a upward planar drawing.
 * 
 *  @author jin
 */
package org.graffiti.plugins.algorithms.upward;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;

public class UpwardDrawing {
    /**
     * the graph
     */
    private Graph graph;

    /**
     * list of super nodes
     */
    private SuperNode[] sequences;

    /**
     * list of added edges that later removed
     */
    private LinkedList<Edge> addedEdges;

    /**
     * minimal distance of the drawing.
     */
    private double minDistance;

    /**
     * external face of the graph
     */
    private MyFace externalFace;

    /**
     * constructor with parameters
     * 
     * @param sequences
     *            list of super nodes
     * @param addedEdges
     *            list of added edges that later removed
     * @param graph
     *            the graph
     */
    public UpwardDrawing(SuperNode[] sequences, LinkedList<Edge> addedEdges,
            Graph graph, int distance, MyFace face) {
        this.sequences = sequences;
        this.addedEdges = addedEdges;
        this.graph = graph;
        this.minDistance = distance * 1.0;
        this.externalFace = face;
    }

    /**
     * initialize
     */
    public void init() {
        Iterator<Node> nodeIt = this.graph.getNodes().iterator();
        Node sink = null;
        while (nodeIt.hasNext()) {
            Node node = nodeIt.next();
            if (node.getAllOutEdges().isEmpty()) {
                sink = node;
                break;
            }
        }
        boolean[] visited = new boolean[this.sequences.length];

        for (int i = 0; i < this.sequences.length; i++) {
            visited[i] = false;
        }

        LinkedList<Node> queue = new LinkedList<Node>();
        queue.add(sink);
        visited[sink.getInteger("number")] = true;

        while (true) {
            Node current = queue.remove(0);
            int index = current.getInteger("number");
            LinkedList<Edge> edges = this.sequences[index].getEdges();

            int leftEdge = this.getIndexOfLeftEdge(this.sequences[index]);

            if (leftEdge == -1) {
                break;
            }

            int num = 0;
            while (true) {
                Edge edge = edges.get(leftEdge % edges.size());
                if (edge.getTarget().equals(current)) {
                    this.sequences[index].addIncomingEdge(edge);
                }
                // insert to queue
                Node source = edge.getSource();
                if (!visited[source.getInteger("number")]) {
                    queue.add(source);
                    visited[source.getInteger("number")] = true;
                }

                leftEdge++;
                num++;
                if (num == edges.size()) {
                    break;
                }
            }
        }
    }

    private int getIndexOfLeftEdge(SuperNode node) {
        // sink
        if (node.getNode().getAllOutEdges().isEmpty()) {
            int re = this.determiteLeft(node);
            return re;
        }
        // source
        else if (node.getNode().getAllInEdges().isEmpty())
            return -1;
        else {
            LinkedList<Edge> edges = node.getEdges();
            Node current = node.getNode();

            boolean findFirstIncoming = false;
            boolean findFirstOutgoing = false;

            int num = 0;
            while (true) {
                Edge edge = edges.get(num % edges.size());
                if (edge.getSource().equals(current)) {
                    findFirstOutgoing = true;
                }
                if (edge.getTarget().equals(current)) {
                    findFirstIncoming = true;
                }

                if ((findFirstOutgoing) && (findFirstIncoming)
                        && (edge.getTarget().equals(current))) {
                    break;
                }
                num++;
            }
            return num % edges.size();
        }
    }

    private int determiteLeft(SuperNode node) {
        List<Node> nodesOfEx = this.externalFace.getNodes();
        Node start = nodesOfEx.get(0);

        int numOfNodes = nodesOfEx.size();
        int result = 0;

        Faces face = new Faces();
        List<Edge> incoming = node.getEdges();
        for (int k = 0; k < incoming.size(); k++) {
            MyFace myFace = face.calFace(this.sequences, node, incoming.get(k));

            List<Node> nodes = myFace.getNodes();

            if (numOfNodes == nodes.size()) {
                int find = -1;
                for (int j = 0; j < nodes.size(); j++) {
                    if (start.equals(nodes.get(j))) {
                        find = j;
                        break;
                    }
                }
                boolean same = true;
                if (find != -1) {
                    for (int j = 0; j < numOfNodes; j++) {
                        if (!nodesOfEx.get(j).equals(
                                nodes.get((j + find) % nodes.size()))) {
                            same = false;
                        }
                    }
                } else {
                    same = false;
                }
                if (same) {
                    result = k;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * remove all prior added edges from graph
     */
    private void removeAddedNewEdges() {
        int size = this.addedEdges.size();
        for (int i = 0; i < size; i++) {
            this.graph.deleteEdge(this.addedEdges.remove());
        }
    }

    /**
     * upward planar drawing
     */
    public void drawing() {
        Iterator<Node> nodes = this.graph.getNodesIterator();
        while (nodes.hasNext()) {
            Node node = nodes.next();
            node.setBoolean("visited", false);
        }

        LinkedList<SuperNode> queue = new LinkedList<SuperNode>();

        for (int i = 0; i < this.sequences.length; i++) {
            this.sequences[i].getNode().setBoolean("visited", false);
        }

        for (int i = 0; i < this.sequences.length; i++) {
            Node node = this.sequences[i].getNode();
            if (node.getOutNeighbors().isEmpty()) {
                node.setDouble(GraphicAttributeConstants.COORDX_PATH,
                        minDistance);
                node.setDouble(GraphicAttributeConstants.COORDY_PATH,
                        minDistance);

                node.setBoolean("visited", true);
                queue.add(this.sequences[i]);
                break;
            }
        }
        double last = 0d;

        while (!queue.isEmpty()) {
            SuperNode target = queue.remove();
            double high = target.getNode().getDouble(
                    GraphicAttributeConstants.COORDY_PATH);

            LinkedList<Edge> inNodes = target.getIncomingEdges();

            if (target.getNode().getDouble(
                    GraphicAttributeConstants.COORDX_PATH) == minDistance) {
                last = 0d;
            }
            for (int index = 0; index < inNodes.size(); index++) {
                Edge edge = inNodes.get(index);
                Node source = edge.getSource();

                if (!source.getBoolean("visited")) {
                    last += minDistance;
                    source.setDouble(GraphicAttributeConstants.COORDX_PATH,
                            last);
                    source.setDouble(GraphicAttributeConstants.COORDY_PATH,
                            high + minDistance);

                    source.setBoolean("visited", true);
                    queue.add(this.sequences[source.getInteger("number")]);
                }
            }
        }
        this.removeAddedNewEdges();
    }
}
