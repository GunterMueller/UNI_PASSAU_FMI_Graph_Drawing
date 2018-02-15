// =============================================================================
//
//   SugiyamaNode.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.incremental;

import java.util.HashSet;
import java.util.LinkedList;

import org.graffiti.attributes.ObjectAttribute;
import org.graffiti.core.DeepCopy;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * A <code>SugiyamaNode</code> represents one node of a leveled graph.
 * 
 * @author Christian Brunnermeier
 * @version $Revision$ $Date$
 */
public class SugiyamaNode implements DeepCopy {
    /** Corresponding node of the graph. */
    private Node node;

    /**
     * @uml.property name="level"
     */
    private Plane level;

    /**
     * @uml.property name="column"
     */
    private Plane column;

    protected boolean isDummy = false;

    private LinkedList<SugiyamaEdge> edgesToLowerLevel = new LinkedList<SugiyamaEdge>();

    private LinkedList<SugiyamaEdge> edgesToHigherLevel = new LinkedList<SugiyamaEdge>();

    /*  ************ functions and methods *************** */

    /**
     * Void constructor of <tt>SugiyamaNode</tt> used for subclasses.
     */
    public SugiyamaNode() {
    }

    /**
     * Standard constructor of <code>SugiyamaNode</code>
     * 
     * @param node
     *            The corresponding node of the graph
     */
    public SugiyamaNode(Node node) {
        this.node = node;
        IncrementalSugiyama.addSugiyamaAttribute(node);
        ObjectAttribute att = new ObjectAttribute(
                SugiyamaConstants.SUBPATH_INC_NODE);
        att.setObject(this);

        node.addAttribute(att, SugiyamaConstants.PATH_SUGIYAMA);

        // IncrementalSugiyama.debug("New node attribute: " +
        // node.getAttribute(SugiyamaConstants.PATH_INC_NODE).getValue());
    }

    /*
     * @see org.graffiti.core.DeepCopy#copy()
     */
    public Object copy() {
        try {
            return this.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Update the coordinates of the node if necessary.
     */
    public void updateCoordinates() {
        CoordinateAttribute coords = (CoordinateAttribute) node
                .getAttribute("graphics.coordinate");
        double newCoord = getX();
        if (coords.getX() != newCoord) {
            coords.setX(newCoord);
        }
        newCoord = getY();
        if (coords.getY() != newCoord) {
            coords.setY(newCoord);
        }
    }

    /**
     * Calls the function checkPosition() of all edges of this node.
     */
    public void checkEdgePositions(HashSet<Plane> columnsToCheck) {
        for (SugiyamaEdge edge : edgesToLowerLevel) {
            edge.checkPosition(columnsToCheck);
        }
        for (SugiyamaEdge edge : edgesToHigherLevel) {
            edge.checkPosition(columnsToCheck);
        }
    }

    /**
     * Returns a <tt>String</tt> representation of this node containing the x
     * and y coordinates.
     */
    @Override
    public String toString() {
        return "SugiyamaNode(" + getX() + ", " + getY() + ")";
    }

    /*  ************** Getter and Setter **************** */

    /**
     * Getter of the property <tt>level</tt>
     * 
     * @return Returns the level.
     * @uml.property name="level"
     */
    public Plane getLevel() {
        return level;
    }

    public int getLevelNumber() {
        if (level != null)
            return level.getNumber();
        else {
            String dummy = (isDummy) ? "Dummy" : "";
            System.err.println("Sugiyama" + dummy + "Node is not attached to a"
                    + " level so no level number can be returned");
            return 0;
        }
    }

    /**
     * Setter of the property <tt>level</tt>
     * 
     * @param level
     *            The level to set.
     * @uml.property name="level"
     */
    public void setLevel(Plane level) {
        this.level = level;
    }

    /**
     * Getter of the property <tt>column</tt>
     * 
     * @return Returns the column.
     * @uml.property name="column"
     */
    public Plane getColumn() {
        return column;
    }

    public int getColumnNumber() {
        if (column != null)
            return column.getNumber();
        else {
            String dummy = (isDummy) ? "Dummy" : "";
            System.err.println("Sugiyama" + dummy
                    + "Node is not attached to a "
                    + "column so no column number can be returned.");
            return 0;
        }
    }

    /**
     * Setter of the property <tt>column</tt>
     * 
     * @param column
     *            The column to set.
     * @uml.property name="column"
     */
    public void setColumn(Plane column) {
        this.column = column;
    }

    /**
     * @return the node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Returns the isDummy.
     * 
     * @return the isDummy.
     */
    public boolean isDummy() {
        return isDummy;
    }

    /**
     * Returns the x coordinate of the column this node belongs to.<br>
     * If it doesn't belong to a column, -1 is returned.
     */
    public double getX() {
        if (column != null)
            return column.getCoordinate();
        else {
            String dummy = (isDummy) ? "Dummy" : "";
            System.err.println("Sugiyama" + dummy + "Node is not attached to a"
                    + " column so the x coordinate cannot be returned.");
            return -1;
        }
    }

    /**
     * Returns the y coordinate of the level this node belongs to.<br>
     * If it doesn't belong to a level, -1 is returned.
     */
    public double getY() {
        if (level != null)
            return level.getCoordinate();
        else {
            String dummy = (isDummy) ? "Dummy" : "";
            System.err.println("Sugiyama" + dummy
                    + "Node is not attached to a "
                    + "level so the y coordinate cannot be returned.");
            return -1;
        }
    }

    public LinkedList<SugiyamaEdge> getEdgesToLowerLevel() {
        return edgesToLowerLevel;
    }

    public LinkedList<SugiyamaEdge> getEdgesToHigherLevel() {
        return edgesToHigherLevel;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
