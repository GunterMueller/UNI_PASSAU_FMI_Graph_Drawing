// =============================================================================
//
//   Queue.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import java.util.LinkedList;

import org.graffiti.graph.Node;

/**
 * A <code>Queue</code> object is used for the breadth first search in the
 * <LineGraph>.
 * 
 * @author Mirka Kossak
 */
public class Queue {
    // the queue is a linkedlist of nodes.
    private LinkedList<Node> queue;

    /**
     * Constructs a new (empty) Queue.
     * 
     */
    public Queue() {
        queue = new LinkedList<Node>();
    }

    /**
     * Removes the first element (node) of the queue.
     * 
     */
    public void remove() {
        queue.removeFirst();
    }

    /**
     * Adds an new element (node) to the queue.
     * 
     * @param node
     */
    public void enqueue(Node node) {
        queue.addLast(node);
    }

    /**
     * Returns the first element (node) of the queue.
     * 
     * @return the first element (node) of the queue.f
     */
    public Node dequeue() {
        if (queue.isEmpty())
            return null;
        Node result = queue.getFirst();
        queue.removeFirst();
        return result;
    }

    /**
     * Returns whether the queue is empty.
     * 
     * @return whether the queue is empty.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
