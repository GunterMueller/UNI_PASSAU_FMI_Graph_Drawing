//=============================================================================
//
//   Plane.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.incremental;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 * A <tt>Plane</tt> represents one level or column in a <tt>GridStructure</tt>
 * and contains all of it's nodes.<br>
 * Whether it is a level or a column can be distinguished by the attribute
 * <code>
 * type</code> which is either <code>GridStructure.COLUMN</code> or
 * <code>GridStructure.LEVEL</code>.<br>
 * 
 * @author Christian Brunnermeier
 */
public class Plane {

    /* HashSet containing all nodes of this Plane. */
    private HashSet<SugiyamaNode> nodes = new HashSet<SugiyamaNode>();

    /* The GridStructure this Plane belongs to. */
    private GridStructure grid;

    /*
     * The type of this Plane: either GridStructure.LEVEL or
     * GridStructure.COLUMN
     */
    private int type;

    /*
     * A random integer number to identify this column as long as it isn't added
     * to the GridStructure.
     */
    private int randomIdentifier;

    /*
     * The coordinate used as long as this Plane isn't added to the
     * GridStructure.
     */
    double uncheckedCoordinate = 0;

    /*  ****************** functions and methods ************* */

    /**
     * Constructor storing the <tt>GridStructure</tt> this plane is a part of as
     * well as it's type (level or column).
     */
    public Plane(GridStructure iGrid, int iType) {
        grid = iGrid;
        type = iType;
        randomIdentifier = (int) (Math.random() * 10000);
    }

    /**
     * Adds the given node to the plane and updates the reference of the node to
     * this plane.
     * 
     * @param node
     *            Node to add
     */
    public void addNode(SugiyamaNode node) {
        // O(1)
        if (!nodes.contains(node)) {
            nodes.add(node);
            if (type == GridStructure.COLUMN) {
                node.setColumn(this);
            } else {
                node.setLevel(this);
            }
        }
        grid.occupancyOutDated(this);
    }

    /**
     * Removes the given node from the plane.
     * 
     * @param node
     *            Node to remove
     */
    public void deleteNode(SugiyamaNode node) {
        // O(1)
        nodes.remove(node);
        if (type == GridStructure.COLUMN) {
            node.setColumn(null);
        } else {
            node.setLevel(null);
        }
        grid.occupancyOutDated(this);
    }

    /**
     * Returns an <tt>Iterator</tt> of the <tt>SugiyamaNode</tt>s of this
     * <tt>Plane</tt>.
     * 
     * @return Iterator<SugiyamaNode>
     */
    public Iterator<SugiyamaNode> iterator() {
        return nodes.iterator();
    }

    /**
     * Returns all <tt>SugiyamaNode</tt>s of this <tt>Plane</tt> in a
     * <tt>LinkedList</tt>.
     */
    public LinkedList<SugiyamaNode> getNodes() {
        LinkedList<SugiyamaNode> result = new LinkedList<SugiyamaNode>();
        Iterator<SugiyamaNode> it = iterator();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

    /**
     * Returns the coordinate of this <tt>Plane</tt>. Depending on what type of
     * plane this is (column or level) the coordinate is meant to be a x or a y
     * coordinate.
     */
    public double getCoordinate() {
        // O(1) - O(k), k=#levels/columns
        return grid.getCoordinate(this);
    }

    /**
     * Returns an array of boolean values that indicate which levels/columns of
     * this column/level are occupied by (dummy) nodes and which are not.
     * 
     * @return boolean[] array of occupancies. An entry with the value
     *         <code>true</code> indicates that this place is occupied.
     */
    public boolean[] getOccupancy() {
        // O(m), m=#levels/columns
        return grid.getOccupancy(this);

    }

    /**
     * If the <tt>Plane</tt> is a level and all remaining nodes of this plane
     * are dummy nodes, then these dummy nodes can be removed and the level can
     * be removed.<br>
     * If the <tt>Plane</tt> is a column and there are no nodes left on this
     * plane, then it can be removed as well.
     */
    public void checkForDeletion() {
        // del col: O(log k); del lev: O(log(m) + k*log(k)), k=#colums, m=#level
        if (type == GridStructure.LEVEL) {
            boolean delete = true;
            Iterator<SugiyamaNode> it = iterator();
            while (it.hasNext()) {
                SugiyamaNode node = it.next();
                if (!node.isDummy) {
                    delete = false;
                    break;
                }
            }
            if (delete) {
                it = iterator();
                while (it.hasNext()) {
                    SugiyamaDummyNode node = (SugiyamaDummyNode) it.next();
                    SugiyamaEdge edge = node.getEdge();
                    edge.removeDummyNode(node);
                    Plane column = node.getColumn();
                    column.deleteNode(node);
                    column.checkForDeletion();
                }
                grid.remove(this);
            }
        } else {
            if (nodes.size() == 0) {
                grid.remove(this);
            }
        }
    }

    /**
     * Returns a <tt>String</tt> representation of this <tt>Plane</tt> including
     * the <tt>String</tt> representations of all nodes in this plane.
     */
    @Override
    public String toString() {
        Object[] drawNodes = nodes.toArray();
        String result = "Plane[";
        int i;
        for (i = 0; i < drawNodes.length - 1; i++) {
            result += drawNodes[i].toString() + ", ";
        }
        if (drawNodes.length > 0) {
            result += drawNodes[i];
        }
        result += "]\n";
        return result;
    }

    /*  *************** Getter and Setter ************** */

    /**
     * Getter of the property <tt>size</tt>
     * 
     * @return Returns the size.
     * @uml.property name="size" readOnly="true"
     */
    public int getSize() {
        // O(1)
        return nodes.size();
    }

    /**
     * Getter of the property <tt>number</tt>
     * 
     * @return Returns the number.
     * @uml.property name="number" readOnly="true"
     */
    public int getNumber() {
        // O(1) - O(k), k=#levels/columns
        return grid.getNumber(this);
    }

    /**
     * Get <tt>Plane</tt> preceding this one.
     */
    public Plane getPrev() {
        // O(1)
        return grid.getPrev(this);
    }

    /**
     * Get <tt>Plane</tt> succeeding this one.
     */
    public Plane getNext() {
        // O(1)
        return grid.getNext(this);
    }

    /**
     * Returns the uncheckedCoordinate.
     * 
     * @return the uncheckedCoordinate.
     */
    public double getUncheckedCoordinate() {
        return uncheckedCoordinate;
    }

    /**
     * Sets the uncheckedCoordinate.
     * 
     * @param uncheckedCoordinate
     *            the uncheckedCoordinate to set.
     */
    public void setUncheckedCoordinate(double uncheckedCoordinate) {
        this.uncheckedCoordinate = uncheckedCoordinate;
    }

    /**
     * Returns the randomIdentifier.
     * 
     * @return the randomIdentifier.
     */
    public int getRandomIdentifier() {
        return randomIdentifier;
    }

    public GridStructure getGrid() {
        return grid;
    }

    public int getType() {
        return type;
    }

}
