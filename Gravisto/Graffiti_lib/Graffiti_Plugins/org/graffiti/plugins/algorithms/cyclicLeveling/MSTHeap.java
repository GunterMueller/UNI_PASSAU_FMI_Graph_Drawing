package org.graffiti.plugins.algorithms.cyclicLeveling;

import java.util.Comparator;
import java.util.TreeSet;

import org.graffiti.graph.Node;

public class MSTHeap extends TreeSet<Node> {
    /**
     * 
     */
    private static final long serialVersionUID = 3324289232350596496L;

    /**
     * Constructor
     */
    public MSTHeap(Comparator<Node> comp) {
        super(comp);
    }

    /**
     * @return the node with smallest distance
     */
    public Node removeMin() {
        Node first = this.first();

        boolean removed = this.remove(first);

        if (removed)
            return first;
        else
            return null;
    }

    /**
     * Changes the distance for node. The node will be removed from rand and
     * inserted again after the distance has been changed.
     * 
     * @param distance
     * @param node
     */
    public void update(Node node, double distance) {
        this.remove(node);
        node.setDouble("distance", distance);
        this.add(node);
    }
}
