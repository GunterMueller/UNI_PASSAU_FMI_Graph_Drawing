// =============================================================================
//
//   NodeAdapterFactory.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.mst.adapters;

import org.graffiti.graph.Node;

/**
 * Factory for node adapters.
 * 
 * @author Harald Frankenberger
 * @version $Revision$ $Date$
 */
public class NodeAdapterFactory {

    private EdgeAdapterFactory edgeAdapterFactory = null;

    /**
     * Creates a new node adapter factory using a default edge adapter factory.
     * 
     */
    public NodeAdapterFactory() {
        this(new EdgeAdapterFactory());
    }

    /**
     * Creates a new node adapter factory using the specified edge adapter
     * factory.
     * 
     * @param ef
     *            the edge adapter factory to be used with this node adapter
     *            factory.
     */
    public NodeAdapterFactory(EdgeAdapterFactory ef) {
        edgeAdapterFactory = ef;
    }

    /**
     * Creates a new node adapter with the specified adaptee.
     * 
     * @param n
     *            the adaptee of the node adapter to be created.
     * @return an adapter for the specified node.
     */
    public NodeAdapter createNodeAdapter(Node n) {
        return new NodeAdapter(n, edgeAdapterFactory);
    }

    /**
     * Creates a new node adapter with the specfied adaptee and edge adapter
     * factory.
     * 
     * @param n
     *            the adaptee of the node adapter to be created.
     * @param f
     *            the factory to be used with the node adapter to be created.
     * @return a new node adapter with the specified adaptee and edge adapter
     *         factory.
     */
    public NodeAdapter createNodeAdapter(Node n, EdgeAdapterFactory f) {
        return new NodeAdapter(n, f);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
