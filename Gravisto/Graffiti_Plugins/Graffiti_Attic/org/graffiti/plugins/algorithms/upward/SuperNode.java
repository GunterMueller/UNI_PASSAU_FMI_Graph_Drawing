/**
 * the class Supernode is a special node
 * the class contain list of incident edges of the node and list of incident incoming edges of the node
 * 
 * @author jin
 */
package org.graffiti.plugins.algorithms.upward;

import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

public class SuperNode {
    /**
     * list of incident incoming edges of the node
     */
    private LinkedList<Edge> incomingEdges;

    /**
     * list of incident edges of the node
     */
    private LinkedList<Edge> edges;

    /**
     * list of value that the angle between first node of edges and i-te node of
     * edges.
     */
    private LinkedList<Double> values;

    /**
     * the node
     */
    private Node node;

    /**
     * constructor with the node
     * 
     * @param node
     *            the node
     */
    public SuperNode(Node node) {
        this.node = node;
        this.incomingEdges = new LinkedList<Edge>();
        this.edges = new LinkedList<Edge>();
        this.values = new LinkedList<Double>();
    }

    /**
     * list of value of incident edges of the node
     * 
     * @return list of value of incident edges of the node
     */
    public LinkedList<Double> getValues() {
        return this.values;
    }

    /**
     * list of incident edges of the node
     * 
     * @return list of incident edges of the node
     */
    public LinkedList<Edge> getEdges() {
        return this.edges;
    }

    /**
     * get the node
     * 
     * @return the node
     */
    public Node getNode() {
        return this.node;
    }

    /**
     * get incident incoming edges of the node
     * 
     * @return incident incoming edges of the node
     */
    public LinkedList<Edge> getIncomingEdges() {
        return this.incomingEdges;
    }

    /**
     * add a incoming edge of the node in list of incident incoming edges of the
     * node
     * 
     * @param edge
     *            add the edge of the node
     */
    public void addIncomingEdge(Edge edge) {
        this.incomingEdges.add(edge);
    }

    /**
     * the list of incident edges of the node is same with the list of incident
     * incoming edges of the node
     */
    public void imcomingEdgesForSink() {
        this.incomingEdges = this.edges;
    }

    /**
     * add a edge of the node in list of incident edges of the node
     * 
     * @param edge
     *            add the edge of the node
     */
    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    /**
     * add a edge of the node in list of incident edges of the node in the index
     * 
     * @param edge
     *            add the edge of the node
     * @param index
     *            the index
     */
    public void addEdge(Edge edge, int index) {
        this.edges.add(index, edge);
    }

    /**
     * add a value of incident edges of the node
     * 
     * @param value
     */
    public void addValue(Double value) {
        this.values.add(value);
    }

    /**
     * add a value of incident edges of the node in the index
     * 
     * @param value
     * @param index
     */
    public void addValue(Double value, int index) {
        this.values.add(index, value);
    }

    /**
     * quick sort
     * 
     * @param left
     * @param right
     */
    public void quickSort(int left, int right) {
        if (left < right) {
            int divider = part(left, right);
            quickSort(left, divider - 1);
            quickSort(divider + 1, right);
        }
    }

    /**
     * a part of quick sort
     * 
     * @param left
     * @param right
     */
    private int part(int left, int right) {
        int i = left;
        int j = right - 1;
        Double pivot = this.values.get(right);
        while (i < j) {
            while ((this.values.get(i).doubleValue() <= pivot.doubleValue())
                    && (i < right)) {
                i++;
            }
            while ((this.values.get(j).doubleValue() > pivot.doubleValue())
                    && (j > left)) {
                j--;
            }
            if (i < j) {
                Double swap = this.values.get(i);
                this.values.set(i, this.values.get(j));
                this.values.set(j, swap);

                Edge swap2 = this.edges.get(i);
                this.edges.set(i, this.edges.get(j));
                this.edges.set(j, swap2);
            }
        }
        if (this.values.get(i).doubleValue() > pivot.doubleValue()) {
            Double swap = this.values.get(i);
            this.values.set(i, this.values.get(right));
            this.values.set(right, swap);

            Edge swap2 = this.edges.get(i);
            this.edges.set(i, this.edges.get(right));
            this.edges.set(right, swap2);
        }
        return i;
    }
}
