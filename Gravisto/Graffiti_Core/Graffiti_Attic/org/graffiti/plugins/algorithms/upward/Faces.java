/**
 * In the class compute all faces of graph.
 * 
 * @author jin
 */
package org.graffiti.plugins.algorithms.upward;

import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

public class Faces {
    /**
     * visit[i] is true, when all faces with the node ( with i-te number) have
     * found.
     */
    private boolean[] visit;

    /**
     * list of face
     */
    private LinkedList<MyFace> faces;

    /**
     * list of super nodes
     */
    private SuperNode[] sequences;

    /**
     * constructor
     */
    public Faces() {
    }

    /**
     * constructor with parameter
     * 
     * @param sequences
     *            list of super nodes
     */
    public Faces(SuperNode[] sequences) {
        this.faces = new LinkedList<MyFace>();
        this.sequences = sequences;
        this.visit = new boolean[sequences.length];
    }

    /**
     * Get list of the faces.
     * 
     * @return list of all faces
     */
    public LinkedList<MyFace> getFaces() {
        return this.faces;
    }

    /**
     * compute all faces of this graph with list of super nodes
     */
    public void execute() {
        for (int i = 0; i < this.visit.length; i++) {
            this.visit[i] = false;
        }

        SuperNode[] superNodes = this.sequences;
        LinkedList<Integer>[] list = new LinkedList[this.sequences.length];

        for (int i = 0; i < sequences.length; i++) {
            int numberOfNeighbors = this.sequences[i].getEdges().size();
            if (list[numberOfNeighbors] == null) {
                list[numberOfNeighbors] = new LinkedList();
            }
            list[numberOfNeighbors].add(new Integer(i));
        }
        int num = 0;
        for (int i = 0; i < list.length; i++) {
            LinkedList<Integer> indexes = list[i];
            if (indexes != null) {
                for (int j = 0; j < indexes.size(); j++) {
                    int index = indexes.get(j).intValue();
                    num++;
                    SuperNode node = this.sequences[index];
                    LinkedList<Edge> edgesList = node.getEdges();
                    for (int k = 0; k < edgesList.size(); k++) {
                        Edge edge = edgesList.get(k);
                        this.calculateFace(node, edge);
                    }
                    this.visit[index] = true;
                }
                if (num == this.sequences.length) {
                    break;
                }
            }
        }
    }

    /**
     * Calculate a face with given edge at the node with the edge.
     * 
     */
    public MyFace calFace(SuperNode[] sequences, SuperNode node, Edge edge) {
        MyFace face = new MyFace();

        Node currentNode = node.getNode();
        Edge currentEdge = edge;
        do {
            Node nodeNew = currentNode;
            face.addNode(nodeNew);

            if (currentNode.equals(currentEdge.getSource())) {
                currentNode = currentEdge.getTarget();
            } else {
                currentNode = currentEdge.getSource();
            }

            LinkedList<Edge> edges = sequences[currentNode.getInteger("number")]
                    .getEdges();

            for (int i = 0; i < edges.size(); i++) {
                Edge target = edges.get(i);
                if (target.equals(currentEdge)) {
                    currentEdge = edges.get((i + 1) % edges.size());
                    break;
                }
            }
        } while (!node.getNode().equals(currentNode));
        return face;
    }

    /**
     * Calculate a face with given edge at the node.
     * 
     */
    private void calculateFace(SuperNode node, Edge edge) {
        boolean result = true;
        MyFace face = new MyFace();

        Node currentNode = node.getNode();
        Edge currentEdge = edge;
        do {
            if (this.visit[currentNode.getInteger("number")]) {
                result = false;
                break;
            }
            Node nodeNew = currentNode;
            Edge edgeNew = currentEdge;

            face.addEdge(edgeNew);
            face.addNode(nodeNew);
            if (currentNode.equals(currentEdge.getSource())) {
                currentNode = currentEdge.getTarget();
            } else {
                currentNode = currentEdge.getSource();
            }
            currentEdge = this.nextEdge(this.sequences[currentNode
                    .getInteger("number")], currentEdge);

        } while (!node.getNode().equals(currentNode));

        if (result && (face.getEdges().size() > 2)) {
            this.faces.add(face);
        }
    }

    /**
     * return the next edge of the face.
     * 
     * @return the next edge of the face
     */
    private Edge nextEdge(SuperNode node, Edge edge) {
        Edge result = null;
        for (int i = 0; i < node.getEdges().size(); i++) {
            if (edge.equals(node.getEdges().get(i))) {
                if (i == node.getEdges().size() - 1) {
                    i = -1;
                }
                result = node.getEdges().get(i + 1);
                break;
            }
        }
        return result;
    }
}
