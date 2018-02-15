//=============================================================================
//
//   SugiyamaNode.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.incremental;

/**
 * This class represents a dummy node as a <tt>SugiyamaNode</tt> and stores the
 * <tt>SugiyamaEdge</tt> it belongs to.
 * 
 * @author Christian Brunnermeier
 */
public class SugiyamaDummyNode extends SugiyamaNode {

    /**
     * @uml.property name="edge"
     * @uml.associationEnd readOnly="true" multiplicity="(1 1)"
     */
    private SugiyamaEdge edge = null;

    /*  *************** functions and methods ************** */

    /**
     * Constructor of this object.
     */
    public SugiyamaDummyNode(SugiyamaEdge edge) {
        super();
        this.edge = edge;
        isDummy = true;
    }

    /**
     * Returns a <tt>String</tt> representation of this dummy node containing
     * it's x and y coordinates.
     */
    @Override
    public String toString() {
        return "SugiyamaDummyNode(" + getX() + ", " + getY() + ")";
    }

    /**
     * Overrides the function updateCoordinates() of <tt>SugiyamaNode</tt> as
     * nothing has to be done when updating the coordinates of a dummy node.
     */
    @Override
    public void updateCoordinates() {
        // do nothing
    }

    /*  ************* Getter and Setter ************* */

    /**
     * Getter of the property <tt>edge</tt>
     * 
     * @return Returns the edge.
     * @uml.property name="edge"
     * @uml.associationEnd readOnly="true" multiplicity="(1 1)"
     */
    public SugiyamaEdge getEdge() {
        return edge;
    }
}
