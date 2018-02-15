package org.graffiti.plugins.algorithms.mst.adapters;

import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.mst.Heap;

/**
 * Proxy object for Heap objects; provides customized methods for minimum
 * spanning tree algorithms and their animations.
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class HeapAdapter {

    /**
     * The heap this proxy stands for.
     */
    private Heap<Node, Float> heap = null;

    private EdgeAdapterFactory edgeAdapterFactory = null;

    private NodeAdapterFactory nodeAdapterFactory = null;

    /**
     * Creates a new heap proxy for the specified heap with the specified proxy
     * builder.
     * 
     * @param h
     *            the heap this proxy stands for.
     */
    public HeapAdapter(Heap<Node, Float> h) {
        heap = h;
    }

    /**
     * Sets this heap's edge adapter factory to the specified value.
     * 
     * @param f
     *            the edge adapter factory to be used with this heap.
     */
    void setEdgeAdapterFactory(EdgeAdapterFactory f) {
        checkIsNotSet(edgeAdapterFactory);
        edgeAdapterFactory = f;
    }

    /**
     * Checks whether the specified Object is <tt>null</tt>. If not so, throws
     * <tt>IllegalStateException</tt>.
     * 
     * @param o
     *            the object to be checked for equality with <tt>null</tt>.
     */
    private void checkIsNotSet(Object o) {
        if (o != null)
            throw new IllegalStateException();
    }

    /**
     * Adds the specified node to this heap.
     * 
     * @param n
     *            the node to be added to this heap.
     */
    public void add(NodeAdapter n) {
        n.addTo(heap);
    }

    /**
     * Removes the peek node (i.e. the node with the smallest key) from this
     * heap.
     * 
     * @return the peek node (i.e. the node with the smallest key) from this
     *         heap.
     */
    public NodeAdapter removePeek() {
        return nodeAdapterFactory.createNodeAdapter(heap.removePeek(),
                edgeAdapterFactory);
    }

    /**
     * Returns <tt>true</tt> if this heap is empty.
     * 
     * @return <tt>true</tt> if this heap is empty.
     */
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Removes all entries from this heap.
     */
    public void clear() {
        heap.clear();
        edgeAdapterFactory = null;
        nodeAdapterFactory = null;
    }

    /**
     * Sets the node adapter factory of this heap to the specified value.
     * 
     * @param f
     *            the node adapter factory to be used with this heap.
     */
    public void setNodeAdapterFactory(NodeAdapterFactory f) {
        checkIsNotSet(nodeAdapterFactory);
        nodeAdapterFactory = f;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
