// =============================================================================
//
//   ReverseEdge.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarfas.attributes;

import org.graffiti.attributes.AbstractAttribute;
import org.graffiti.attributes.IllegalIdException;
import org.graffiti.graph.Edge;

/**
 * Attribute to save an edge.
 * 
 * @author Barbara Eckl
 * @version $Revision$ $Date$
 */
public class EdgeAtt extends AbstractAttribute {
    
    /**
     * value of the attribut
     */
    private Edge edge; 

    /**
     * Constructor of the edge attribute.
     * 
     * @param id
     *          name of the attribute
     * @throws IllegalIdException
     */
    public EdgeAtt(String id) {
        super(id); 
    }
    
    /**
     * Constructor of the edge attribute.
     * 
     * @param id
     *          name of the attribute
     * @param e
     *          value of the attribute
     */
    public EdgeAtt(String id, Edge e) {
        super(id);
        this.edge = e;
        setDescription("This is an example attribute");
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaultValue() {
        edge = null;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return edge.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Edge getValue() {
        return edge;
    }

    /**
     * {@inheritDoc}
     */
    protected void doSetValue(Object o)
        throws IllegalArgumentException
    {
        assert o != null;

        try
        {
            edge = (Edge) o;
        }
        catch(ClassCastException cce)
        {
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
//   end of file
// -----------------------------------------------------------------------------
