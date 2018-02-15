// =============================================================================
//
//   SectorUtil.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.usergestures;

import java.awt.geom.Point2D;

import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugins.modes.fast.Sector;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SectorUtil {
    private static final double TOLERANCE = 5;

    public static Pair<Sector, Sector> getSectors(GraphElement element,
            Point2D mousePos) {
        if (!(element instanceof Node))
            return Pair.create(Sector.IGNORE, Sector.IGNORE);
        Node node = (Node) element;
        Point2D nodePos = AttributeUtil.getPosition(node);
        Point2D nodeSize = AttributeUtil.getDimension(node);
        Point2D relPos = new Point2D.Double((mousePos.getX() - nodePos.getX())
                / (nodeSize.getX() == 0 ? 1.0 : nodeSize.getX()) * 2.0,
                (mousePos.getY() - nodePos.getY())
                        / (nodeSize.getY() == 0 ? 1.0 : nodeSize.getY()) * 2.0);
        double xTolerance = TOLERANCE
                / (nodeSize.getX() == 0 ? 1.0 : nodeSize.getX()) * 2.0;
        double yTolerance = TOLERANCE
                / (nodeSize.getY() == 0 ? 1.0 : nodeSize.getY()) * 2.0;
        Point2D relPos2 = new Point2D.Double(relPos.getY(), relPos.getX());
        return Pair.create(getSector(relPos, xTolerance), getSector(relPos2,
                yTolerance));
    }

    private static Sector getSector(Point2D relPos, double xTolerance) {
        if (relPos.getY() < 0)
            return getSector(-relPos.getX() / relPos.getY(), xTolerance);
        else if (relPos.getY() > 0)
            return getSector(relPos.getX() / relPos.getY(), xTolerance);
        else {
            if (relPos.getX() < 0)
                return Sector.LOW;
            else if (relPos.getX() > 0)
                return Sector.HIGH;
            else
                return Sector.CENTER;
        }
    }

    private static Sector getSector(double px, double xTolerance) {
        if (Math.abs(px) < xTolerance)
            return Sector.CENTER;
        else if (px < -1 + xTolerance)
            return Sector.LOW;
        else if (px > 1 - xTolerance)
            return Sector.HIGH;
        else
            return Sector.CENTER;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
