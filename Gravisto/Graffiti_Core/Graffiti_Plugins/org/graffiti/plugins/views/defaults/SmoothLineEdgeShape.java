// =============================================================================
//
//   SmoothLineEdgeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SmoothLineEdgeShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;

/**
 * A class that represents line shapes that are "smooth" in the sense GML uses
 * it.
 * 
 * @version $Revision: 5766 $
 */
public class SmoothLineEdgeShape extends PolyLineEdgeShape {
    /** Saves if the bends have to be modified or not. */
    private boolean mustExpandBends;

    /**
     * Default constructor. Used to ensure that next time
     * <code>buildShape</code> is called, the bends are modified.
     */
    public SmoothLineEdgeShape() {
        super();
        this.mustExpandBends = true;
    }

    /**
     * This method sets all necessary properties of an edge using the values
     * contained within the <code>CollectionAttribute</code> (like coordinates
     * etc.). It also uses information about ports. It attaches arrows if there
     * are any. When <code>mustExpandBends</code> is true, i.e. it is started
     * the very first time for an object, all line segments except the first and
     * last ones are divided into two equally long segments. That is to ensure
     * that the single quadric splines fit together.
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

        // add centers of segments (except first and last)
        if (this.mustExpandBends) {
            // System.out.println("expanding bends");
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();

            int cnt = 0;

            if (!this.bends.isEmpty()) {
                cnt++;

                Iterator<Attribute> it = this.bends.iterator();
                CoordinateAttribute coordAttr = (CoordinateAttribute) it.next();
                map.put(coordAttr.getId(), (Attribute) coordAttr.copy());

                CoordinateAttribute sndCoordAttr;

                while (it.hasNext()) {
                    sndCoordAttr = (CoordinateAttribute) it.next();

                    Point2D start = coordAttr.getCoordinate();
                    Point2D end = sndCoordAttr.getCoordinate();
                    Point2D center = new Point2D.Double((end.getX() + start
                            .getX()) / 2d, (end.getY() + start.getY()) / 2d);

                    coordAttr = new CoordinateAttribute("auxBend" + cnt);
                    coordAttr.setCoordinate(center);
                    map.put(coordAttr.getId(), (Attribute) coordAttr.copy());
                    map.put(sndCoordAttr.getId(), (Attribute) sndCoordAttr
                            .copy());
                    cnt++;
                    coordAttr = sndCoordAttr;
                }
            }

            this.bends = map.values();
        }

        LinkedList<Point2D> bendsList = new LinkedList<Point2D>();
        for (Attribute a : bends) {
            bendsList.add(((CoordinateAttribute) a).getCoordinate());
        }
        bendsList.addFirst(getSourceDockingCoords(edgeAttr, sourceShape));
        bendsList.addLast(getTargetDockingCoords(edgeAttr, targetShape));
        boolean odd = trimToActualStartAndEndPoints(edgeAttr, sourceShape,
                targetShape, bendsList);

        this.linePath = new GeneralPath();
        if (bendsList.size() == 2) {
            Point2D first = bendsList.getFirst();
            Point2D last = bendsList.getLast();
            this.linePath.moveTo((float) first.getX(), (float) first.getY());
            this.linePath.lineTo((float) last.getX(), (float) last.getY());
        }
        if (bendsList.size() > 2) {

            Iterator<Point2D> it = bendsList.iterator();

            Point2D middle = it.next();
            this.linePath.moveTo((float) middle.getX(), (float) middle.getY());

            if (odd) {
                middle = it.next();
                this.linePath.lineTo((float) middle.getX(), (float) middle
                        .getY());

            }
            Point2D next = null;

            while (it.hasNext()) {
                middle = it.next();
                if (it.hasNext()) {
                    next = it.next();
                    this.linePath.quadTo((float) middle.getX(), (float) middle
                            .getY(), (float) next.getX(), (float) next.getY());
                } else {
                    this.linePath.lineTo((float) middle.getX(), (float) middle
                            .getY());

                }
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
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
