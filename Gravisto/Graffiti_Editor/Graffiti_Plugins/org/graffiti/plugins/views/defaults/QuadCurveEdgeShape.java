// =============================================================================
//
//   QuadCurveEdgeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: QuadCurveEdgeShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 5766 $
 */
public class QuadCurveEdgeShape extends PolyLineEdgeShape {

    /**
     * This method sets all necessary properties of an edge using the values
     * contained within the <code>CollectionAttribute</code> (like coordinates
     * etc.). It also uses information about ports. It attaches arrows if there
     * are any.
     * 
     * @param edgeAttr
     *            the attribute that contains all necessary information to
     *            construct a line.
     * @param sourceShape
     *            DOCUMENT ME!
     * @param targetShape
     *            DOCUMENT ME!
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    @Override
    public void buildShape(EdgeGraphicAttribute edgeAttr,
            NodeShape sourceShape, NodeShape targetShape)
            throws ShapeNotFoundException {

        this.graphicsAttr = edgeAttr;

        CollectionAttribute bendsCollection = edgeAttr.getBends();
        this.bends = bendsCollection.getCollection().values();
        // docking
        Point2D start = getSourceDockingCoords(edgeAttr, sourceShape);
        Point2D end = getTargetDockingCoords(edgeAttr, targetShape);

        if (!bends.isEmpty()) {
            LinkedList<Attribute> bendsList = new LinkedList<Attribute>();
            bendsList.addAll(bends);
            CoordinateAttribute caFirstBend = (CoordinateAttribute) bendsList
                    .getFirst();
            CoordinateAttribute caLastBend = (CoordinateAttribute) bendsList
                    .getLast();

            start = calculateActualStartPoint(edgeAttr, sourceShape, start,
                    caFirstBend.getCoordinate());
            end = calculateActualEndPoint(edgeAttr, targetShape, end,
                    caLastBend.getCoordinate());

        } else {
            start = calculateActualStartPoint(edgeAttr, sourceShape, start, end);
            end = calculateActualEndPoint(edgeAttr, targetShape, end, start);
        }

        this.linePath = new GeneralPath();
        this.linePath.moveTo((float) start.getX(), (float) start.getY());
        if (bends.isEmpty()) {
            this.linePath.lineTo((float) end.getX(), (float) end.getY());
        } else {
            Iterator<Attribute> it = this.bends.iterator();

            CoordinateAttribute caFst = (CoordinateAttribute) it.next();
            CoordinateAttribute caSnd = null;

            if (!it.hasNext()) {
                this.linePath.quadTo((float) caFst.getX(),
                        (float) caFst.getY(), (float) end.getX(), (float) end
                                .getY());
            } else {
                caSnd = (CoordinateAttribute) it.next();
                this.linePath.quadTo((float) caFst.getX(),
                        (float) caFst.getY(), (float) caSnd.getX(),
                        (float) caSnd.getY());

                while (it.hasNext()) {
                    caFst = (CoordinateAttribute) it.next();
                    if (it.hasNext()) {
                        caSnd = (CoordinateAttribute) it.next();
                        this.linePath.quadTo((float) caFst.getX(),
                                (float) caFst.getY(), (float) caSnd.getX(),
                                (float) caSnd.getY());
                    } else {
                        this.linePath.quadTo((float) caFst.getX(),
                                (float) caFst.getY(), (float) end.getX(),
                                (float) end.getY());
                    }
                }
                this.linePath.lineTo((float) end.getX(), (float) end.getY());
            }
        }

        getThickBounds(this.linePath, edgeAttr);

        if (headArrow != null) {
            this.realBounds.add(headArrow.getBounds2D());
        }

        if (tailArrow != null) {
            this.realBounds.add(tailArrow.getBounds2D());
        }

    }

    /**
     * Returns true if the edge has been hit.
     * 
     * @param x
     *            x coordinate relative to the coordinates of this shape.
     * @param y
     *            y coordinate relative to the coordinates of this shape.
     * 
     * @return true if the edge has been hit else false.
     */
    @Override
    public boolean contains(double x, double y) {
        return pathContains(this.linePath, x + 1, y + 1);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
