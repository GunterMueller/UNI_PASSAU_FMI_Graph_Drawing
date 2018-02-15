// =============================================================================
//
//   Symmetry.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * This class provides operations for symmetry criterias in the drawing
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public class Symmetry {

    /**
     * centers all father nodes above their sons
     * 
     * @param graph
     *            a graph
     */
    public void localSymmetry(Graph graph) {

        double wedgeFrom;
        double wedgeTo;
        double apexAngle;
        double polarAngle;
        double freeSpace;
        for (Node n : graph.getNodes()) {

            wedgeFrom = n.getDouble(Constants.WEDGE_FROM);
            wedgeTo = n.getDouble(Constants.WEDGE_TO);
            apexAngle = n.getDouble(Constants.APEX_ANGLE);
            polarAngle = n.getDouble(Constants.POLAR_ANGLE);
            freeSpace = (wedgeTo - wedgeFrom) - apexAngle;
            if (freeSpace > Constants.EPSILON) {
                n.setDouble(Constants.POLAR_ANGLE, polarAngle + freeSpace / 2);
            }
        }
    }

    /**
     * distributes free space evenly to sons
     * 
     * @param graph
     *            a graph
     * @param root
     *            the root node
     */
    public void globalSymmetry(Graph graph, Node root) {

        for (Node x : graph.getNodes()) {

            x.setBoolean(Constants.VISITED, false);
        }

        processGlobalSymmetry(root, 0.0);
    }

    /**
     * shifts n about the angle shift and distributes recursivle free space
     * evenly to sons
     * 
     * @param n
     *            the actula node
     * @param shift
     *            an angle
     */
    private void processGlobalSymmetry(Node n, double shift) {

        n.setBoolean(Constants.VISITED, true);

        double wedgeFrom = n.getDouble(Constants.WEDGE_FROM);
        double wedgeTo = n.getDouble(Constants.WEDGE_TO);
        double wedgesize = wedgeTo - wedgeFrom;
        double polarAngle = n.getDouble(Constants.POLAR_ANGLE);

        double minChildWedgeFrom = Double.MAX_VALUE;
        double maxChildWedgeTo = 0.0;
        double childWedgeSize;
        double childWedgeFrom;
        double childWedgeTo;

        double freespace = 0.0;
        double freespacePerChild = 0.0;
        int children = 0;

        // shift all angles in n
        n.setDouble(Constants.WEDGE_FROM, wedgeFrom + shift);
        n.setDouble(Constants.WEDGE_TO, wedgeTo + shift);
        n.setDouble(Constants.POLAR_ANGLE, polarAngle + shift);

        // calculates the childrens sum of wedges
        for (Node x : n.getNeighbors()) {

            if (!x.getBoolean(Constants.VISITED)) {

                children++;

                childWedgeFrom = x.getDouble(Constants.WEDGE_FROM);
                childWedgeTo = x.getDouble(Constants.WEDGE_TO);

                minChildWedgeFrom = Math.min(minChildWedgeFrom, childWedgeFrom);
                maxChildWedgeTo = Math.max(maxChildWedgeTo, childWedgeTo);
            }
        }
        childWedgeSize = maxChildWedgeTo - minChildWedgeFrom;

        // true if there is a child
        if (childWedgeSize > 0) {

            // calculate freespace per child
            if (wedgesize > childWedgeSize) {

                freespace = wedgesize - childWedgeSize;
                freespacePerChild = freespace / children;
            }
        }

        children = 0;
        // process all children recursivly
        for (Node x : n.getNeighbors()) {

            if (!x.getBoolean(Constants.VISITED)) {

                processGlobalSymmetry(x, shift + (children + 0.5)
                        * freespacePerChild);
                children++;
            }
        }
    }
}
