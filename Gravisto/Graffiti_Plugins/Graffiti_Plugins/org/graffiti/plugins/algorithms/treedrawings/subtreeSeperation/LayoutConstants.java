// =============================================================================
//
//   LayoutConstants.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.algorithms.treedrawings.Util;

/**
 * Some constants that are used by layout algorithms
 * 
 * @author Andreas
 * @version $Revision$ $Date$
 */
public class LayoutConstants {
    /**
     * minimum width of all nodes in the tree. Important for HVCompositions with
     * dimensions to calculate the proper Port position.
     */
    public static double minNodeWidth;

    /**
     * minimum height of all nodes in the tree. Important for HVCompositions
     * with dimensions to calculate the proper Port position.
     */
    public static double minNodeHeight;

    /**
     * Method to calculate <code>minNodeWidth</code> and
     * <code>minNodeHeight</code>
     * 
     * @param graph
     *            given that as all the nodes considered.
     */
    public static void setMinimumHeightAndWidth(Graph graph) {

        LayoutConstants.minNodeWidth = Double.MAX_VALUE;
        LayoutConstants.minNodeHeight = Double.MAX_VALUE;

        for (Node currentNode : graph.getNodes()) {
            if (Util.isHelperNode(currentNode)) {
                continue;
            }

            DimensionAttribute da = (DimensionAttribute) currentNode
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.DIMENSION);
            if (LayoutConstants.minNodeWidth > da.getWidth()) {
                LayoutConstants.minNodeWidth = da.getWidth();
            }

            if (LayoutConstants.minNodeHeight > da.getHeight()) {
                LayoutConstants.minNodeHeight = da.getHeight();
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
