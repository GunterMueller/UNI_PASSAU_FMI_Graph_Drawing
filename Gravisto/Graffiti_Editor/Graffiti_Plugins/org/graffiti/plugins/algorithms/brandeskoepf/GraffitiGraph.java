//==============================================================================
//
//   GraffitiGraph.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: GraffitiGraph.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf;

import java.util.Collection;
import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * This class is a interface for all the actions on a gravisto graph. Modify
 * this class if you want to use another data-structure
 * 
 * @author Florian Fischer
 */
public final class GraffitiGraph {
    // ~ Methods
    // ================================================================

    /**
     * Returns the number of sampling points for this angle on level i
     * 
     * @param diff
     *            The pixel difference
     * @param i
     *            The level
     * 
     * @return The number of sampling points
     */
    public static final double getSamplingPointStep(double diff, int i) {
        if (BKConst.getSAMPLING_TYPE() == 1)
            return 1.0 / Math.max(5.0, (diff / 50.0) * i / 4.0);
        else
            return 1.0 / 5.0;
    }

    /**
     * Is the edge a cut edge, 'true'= yes it is
     * 
     * @param e
     *            The edge
     * 
     * @return The result
     */
    public static final boolean getEdgeIsCutEdge(Edge e) {
        if (e.getInteger(BKConst.getPATH_CUTEDGE()) == 1)
            return true;
        else
            return false;
    }

    /**
     * Sets the attribute 'cutEdge' at a edge
     * 
     * @param e
     *            The edge
     * @param b
     *            The value
     */
    public static final void setEdgeIsCutEdge(Edge e, boolean b) {
        if (b) {
            e.setInteger(BKConst.getPATH_CUTEDGE(), 1);
        } else {
            e.setInteger(BKConst.getPATH_CUTEDGE(), 0);
        }
    }

    /**
     * Sets the attribute marked. True, when the edge is a typ1-conflict edge
     * 
     * @param e
     *            The edge
     * @param b
     *            The value
     */
    public static final void setEdgeIsMarked(Edge e, boolean b) {
        if (b) {
            e.setInteger(BKConst.getPATH_EDGEMARKED_GET(), 1);
        } else {
            e.setInteger(BKConst.getPATH_EDGEMARKED_GET(), 0);
        }
    }

    /**
     * Is the edge marked, because of an crossing with an inner segment, 'true'=
     * yes it is
     * 
     * @param e
     *            The edge
     * 
     * @return The result
     */
    public static final boolean getEdgeIsMarked(Edge e) {
        return 1 == e.getInteger(BKConst.getPATH_EDGEMARKED_GET());
    }

    /**
     * Sets the X-coordinate
     * 
     * @param n
     *            The Node
     * @param x
     *            The X-coordinate
     */
    public static final void setNodeCoordX(Node n, double x) {
        n.setDouble(BKConst.getPATH_COORD_X(), x);
    }

    /**
     * Sets the value of the coordInital attribute
     * 
     * @param n
     *            The Node
     * @param x
     *            false= coordinate is set, true=not set
     */
    public static final void setNodeCoordInitial(Node n, boolean x) {
        n.setBoolean(BKConst.getPATH_COORD_INITIAL(), x);
    }

    /**
     * Returns the X-coordinate
     * 
     * @param n
     *            The Node
     * 
     * @return The X-coordinate
     */
    public static final double getNodeCoordX(Node n) {
        return n.getDouble(BKConst.getPATH_COORD_X());
    }

    /**
     * Returns the information, if the coordinate is already not assigned
     * 
     * @param n
     *            The Node
     * 
     * @return true=coordinate not set, false=set
     */
    public static final boolean getNodeCoordInitial(Node n) {
        return n.getBoolean(BKConst.getPATH_COORD_INITIAL());
    }

    /**
     * Sets the Y-coordinate
     * 
     * @param n
     *            The Node
     * @param y
     *            The value
     */
    public static final void setNodeCoordY(Node n, double y) {
        n.setDouble(BKConst.getPATH_COORD_Y(), y);
    }

    /**
     * Returns the Y-coordinate
     * 
     * @param n
     *            The Node
     * 
     * @return The coordinate
     */
    public static final double getNodeCoordY(Node n) {
        return n.getDouble(BKConst.getPATH_COORD_Y());
    }

    /**
     * Is the node a dummy node, 'true'= yes it is
     * 
     * @param n
     *            The node
     * 
     * @return The result
     */
    public static final boolean getNodeIsDummy(Node n) {
        if (n.getInteger(BKConst.getPATH_DUMMY()) == 1)
            return true;
        else

            return false;
    }

    /**
     * Sets the attribute shiftSet
     * 
     * @param n
     *            The Node
     * @param b
     *            The value
     */
    public static final void setNodeIsShiftSet(Node n, boolean b) {
        if (b) {
            n.setInteger(BKConst.getPATH_SHIFTSET_GET(), 1);
        } else {
            n.setInteger(BKConst.getPATH_SHIFTSET_GET(), 0);
        }
    }

    /**
     * Is already a shift calculated, 'true'= yes it is
     * 
     * @param n
     *            The Node
     * 
     * @return The result
     */
    public static final boolean getNodeIsShiftSet(Node n) {
        return 1 == n.getInteger(BKConst.getPATH_SHIFTSET_GET());
    }

    /**
     * Returns the number of the level of the node
     * 
     * @param n
     *            The node
     * 
     * @return The level number
     */
    public static final int getNodeLevel(Node n) {
        return n.getInteger(BKConst.getPATH_LEVEL());
    }

    /**
     * Returns the sequence number of the node in its level
     * 
     * @param n
     *            The node
     * 
     * @return The order number
     */
    public static final int getNodeOrder(Node n) {
        return n.getInteger(BKConst.getPATH_ORDER());
    }

    /**
     * Returns the number of levels
     * 
     * @param nodes
     *            The node collection, from the gravisto graph
     * 
     * @return The number of levels
     * 
     * @throws AttributeNotFoundException
     *             Throws this exception, if the alltribute 'level' is not
     *             assigned
     */
    public static final int countLevel(Collection<Node> nodes)
            throws AttributeNotFoundException {
        int maxLevel = 0;

        // search in all nodes for the highest level number
        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            Node tempNode = it.next();
            int tempLevelNum = -1;

            // try to get the level information.
            try {
                tempLevelNum = ((IntegerAttribute) tempNode
                        .getAttribute(BKConst.getPATH_LEVEL())).getInteger();
            } catch (AttributeNotFoundException anfe) {
                throw anfe;
            }

            // is the level number of the actual node higher, then store it
            if (tempLevelNum > maxLevel) {
                maxLevel = tempLevelNum;
            }
        }

        return maxLevel;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
