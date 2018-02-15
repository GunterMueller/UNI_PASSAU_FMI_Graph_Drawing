// =============================================================================
//
//   NodeList.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarfas.attributes;

import java.util.HashSet;
import java.util.Set;

import org.graffiti.attributes.AbstractAttribute;
import org.graffiti.attributes.IllegalIdException;
import org.graffiti.graph.Node;

/**
 * Attribute to save a set of nodes.
 * 
 * @author Barbara Eckl
 * @version $Revision$ $Date$
 */
public class NodeSet extends AbstractAttribute {

    /**
     * set of nodes
     */
    private Set<Node> nodes;

    /**
     * Constructor of node set attribute.
     * 
     * @param id
     *            name of the attribute
     * @throws IllegalIdException
     */
    public NodeSet(String id) {
        super(id);
    }

    /**
     * Constructor of the node set attribute.
     * 
     * @param id
     *            name of the attribute
     * @param set
     *            value of the attribute
     */
    public NodeSet(String id, Set<Node> set) {
        super(id);
        this.nodes = set;
        setDescription("This is an example attribute");
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultValue() {
        nodes = new HashSet<Node>();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer string = new StringBuffer();
        for (Node node : nodes) {
            string.append(node.toString());
        }
        return string.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Set<Node> getValue() {
        return nodes;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    protected void doSetValue(Object o) throws IllegalArgumentException {
        assert o != null;

        try {
            nodes = (Set<Node>) o;
        } catch (ClassCastException cce) {
            throw new IllegalArgumentException("Invalid value type.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object copy() {
        throw new UnsupportedOperationException("Not possible to implemente.");
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
