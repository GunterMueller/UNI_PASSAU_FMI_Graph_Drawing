package org.graffiti.plugins.algorithms.confluentDrawing;

import java.util.ArrayList;

import org.graffiti.graph.Node;

public class MyNode {
    private Node node;

    private ArrayList Neigbors;

    private int number;

    public MyNode(Node n) {
        Neigbors = new ArrayList();
        node = n;
    }

    public void setNumber(int nu) {
        this.number = nu;
    }

    public int getNumber() {
        return this.Neigbors.size();
    }

    public void setNode(Node n) {
        this.node = n;
    }

    public Node getNode() {
        return this.node;
    }

    public void addNg(Node n) {
        this.Neigbors.add(n);
    }

    public ArrayList getNgs() {
        return this.Neigbors;
    }
}
