package org.graffiti.plugins.algorithms.cyclicLeveling;

import java.util.Comparator;
import java.util.TreeSet;

import org.graffiti.graph.Node;

public class SEHeap extends TreeSet<Node> {
    /**
     * 
     */
    private static final long serialVersionUID = -3185250478540108531L;

    /**
     * Constructor
     */
    public SEHeap(Comparator<Node> comp) {
        super(comp);
    }

    /**
     * @return the node with smallest force
     */
    public Node removeMax() {
        Node last = this.last();

        boolean removed = this.remove(last);

        if (removed)
            return last;
        else
            return null;
    }

    /**
     * Changes the force for node. The node will be removed from the heap and
     * inserted again after the force has been changed.
     * 
     * @param force
     * @param node
     */
    public void update(Node node, double force) {
        boolean isInHeap = this.remove(node);
        node.setDouble("force", force);
        if (isInHeap) {
            this.add(node);
        }
    }
}
