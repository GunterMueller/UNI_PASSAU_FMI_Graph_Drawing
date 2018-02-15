// =============================================================================
//
//   PolygonalNodeShape2.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.shapes.nodes.polygon;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.ShapeDescriptionAttribute;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugins.shapes.nodes.ArbitraryNodeShape;

/**
 * @author brunner
 * @version $Revision$ $Date$
 */
public class PolygonalNodeShape extends ArbitraryNodeShape {

    @Override
    protected GeneralPath getShape() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected GeneralPath getBoundary() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void buildShape(NodeGraphicAttribute graphics)
            throws ShapeNotFoundException {
        this.nodeAttr = graphics;

        DimensionAttribute dim = graphics.getDimension();
        double w = dim.getWidth();
        double h = dim.getHeight();

        double ft = Math.min(nodeAttr.getFrameThickness(), Math.min(w / 2d,
                h / 2d));

        ShapeDescriptionAttribute desc;

        try {
            desc = (ShapeDescriptionAttribute) graphics
                    .getAttribute(GraphicAttributeConstants.SHAPEDESCRIPTION);
        } catch (AttributeNotFoundException anfe) {
            shape = getStandardPolygon(w, h, ft);
            boundaryShape = shape;
            setThickShape(w, h);

            return;
        } catch (ClassCastException cce) {
            shape = getStandardPolygon(w, h, ft);
            boundaryShape = shape;
            setThickShape(w, h);

            return;
        }

        Map<String, Attribute> map = desc.getCollection();
        Point2D pt;
        shape = new GeneralPath();

        for (int i = 0; i < map.size(); i++) {
            pt = ((CoordinateAttribute) map.get("coord" + i)).getCoordinate();
            float x = calcXCoordinate(pt.getX(), w, ft);
            float y = calcYCoordinate(pt.getY(), h, ft);
            if (i == 0) {
                shape.moveTo(x, y);
            } else {
                shape.lineTo(x, y);
            }
        }

        shape.closePath();
        boundaryShape = shape;

        setThickShape(w, h);
        return;
    }

    private GeneralPath getStandardPolygon(double w, double h, double ft) {
        GeneralPath polygon = new GeneralPath();
        polygon.moveTo(calcXCoordinate(-1, w, ft), calcYCoordinate(1, h, ft));
        polygon.lineTo(calcXCoordinate(1, w, ft), calcYCoordinate(1, h, ft));
        polygon.lineTo(calcXCoordinate(0, w, ft), calcYCoordinate(-1, h, ft));
        polygon.closePath();
        return polygon;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
