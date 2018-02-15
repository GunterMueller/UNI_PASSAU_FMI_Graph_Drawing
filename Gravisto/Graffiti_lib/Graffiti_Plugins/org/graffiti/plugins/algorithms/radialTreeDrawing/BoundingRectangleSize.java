// =============================================================================
//
//   GraphSize.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import java.awt.geom.Point2D;
import java.util.Collection;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * This class calculates the size of a minimal rectangle surrounding a graph
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public class BoundingRectangleSize extends GraphSize {

    /**
     * calculates the area size of a minimal rectangle surrounding the graph
     * 
     * @param graph
     *            a graph
     * @return the area size
     */
    @Override
    public double getGraphSize(Graph graph) {

        double minX = Integer.MAX_VALUE;
        double minY = Integer.MAX_VALUE;

        for (Node node : graph.getNodes()) {

            CoordinateAttribute ca = (CoordinateAttribute) node
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            DimensionAttribute da = (DimensionAttribute) node
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.DIMENSION);

            // get the minimal sum of a nodes coordinates and it's dimension
            minX = Math.min(ca.getX() - da.getWidth() / 2d, minX);
            minY = Math.min(ca.getY() - da.getHeight() / 2d, minY);
        }

        for (Edge edge : graph.getEdges()) {

            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            Collection<Attribute> bendsColl = ega.getBends().getCollection()
                    .values();
            for (Attribute attr : bendsColl) {

                CoordinateAttribute ca = (CoordinateAttribute) attr;
                minX = Math.min(ca.getX(), minX);
                minY = Math.min(ca.getY(), minY);
            }
        }

        double maxX = Integer.MIN_VALUE;
        double maxY = Integer.MIN_VALUE;

        for (Node node : graph.getNodes()) {

            CoordinateAttribute ca = (CoordinateAttribute) node
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            DimensionAttribute da = (DimensionAttribute) node
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.DIMENSION);

            // move the node to the left and top border
            final Point2D p = ca.getCoordinate();
            p.setLocation(ca.getX() - minX, ca.getY() - minY);
            ca.setCoordinate(p);

            // get the maximal sum of a nodes coordinates and it's dimension
            maxX = Math.max(p.getX() + da.getWidth() / 2d, maxX);
            maxY = Math.max(p.getY() + da.getHeight() / 2d, maxY);
        }

        return (maxX - minX) * (maxY - minY);
    }
}
