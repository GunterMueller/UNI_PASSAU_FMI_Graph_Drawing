// =============================================================================
//
//   AttributeConsumer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeConsumer.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

/**
 * Interfaces an object, which depends on the presence of a set of attributes.
 * An attribute consumer specifies, which attributes should be available in a
 * node, edge and graph object. Each time, a node or an edge is created, the
 * specified attributes will be created by the graph instance. E.g.: a 2D view
 * depends on some graphics attributes (&quot;graphics&quot;) in every node and
 * edge object. It implements this interface and returns a
 * <code>CollectionAttribute</code> of these attributes and their default
 * values:
 * 
 * <pre>
 * public CollectionAttribute getNodeAttribute() {
 *     return new NodeGraphicAttribute();
 * }
 * 
 * public CollectionAttribute getEdgeAttribute() {
 *     return new EdgeGraphicAttribute();
 * }
 * </pre>
 * 
 * Every time, a new node- or edge-object is created, it will contain
 * (deep-)copies of these collection attributes.
 * 
 * @version $Revision: 5767 $
 */
public interface AttributeConsumer {
    /**
     * Returns the attribute, which should be available in an undirected edge
     * object. May return <code>null</code>.
     * 
     * @return DOCUMENT ME!
     */
    CollectionAttribute getUndirectedEdgeAttribute();

    /**
     * Returns the attribute, which should be available in a directed edge
     * object. May return <code>null</code>.
     * 
     * @return DOCUMENT ME!
     */
    CollectionAttribute getDirectedEdgeAttribute();

    /**
     * Returns the attribute, which should be available in a graph object. May
     * return <code>null</code>.
     * 
     * @return DOCUMENT ME!
     */
    CollectionAttribute getGraphAttribute();

    /**
     * Returns the attribute, which should be available in a node object. May
     * return <code>null</code>.
     * 
     * @return DOCUMENT ME!
     */
    CollectionAttribute getNodeAttribute();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
