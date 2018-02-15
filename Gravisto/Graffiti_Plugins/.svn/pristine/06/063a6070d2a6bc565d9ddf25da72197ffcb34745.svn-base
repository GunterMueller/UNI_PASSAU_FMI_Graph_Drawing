// =============================================================================
//
//   ShapeCloner.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.RectangularShape;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class CloneUtil {
    public static Shape clone(Shape shape) {
        if (shape == null)
            return null;
        else if (shape instanceof RectangularShape)
            return (Shape) ((RectangularShape) shape).clone();
        else if (shape instanceof GeneralPath)
            return (Shape) ((GeneralPath) shape).clone();
        else
            return new GeneralPath(shape);
    }

    public static Stroke clone(Stroke stroke) {
        if (stroke == null)
            return null;
        else if (stroke instanceof BasicStroke) {
            BasicStroke basicStroke = (BasicStroke) stroke;
            return new BasicStroke(basicStroke.getLineWidth(), basicStroke
                    .getEndCap(), basicStroke.getLineJoin(), basicStroke
                    .getMiterLimit(), basicStroke.getDashArray().clone(),
                    basicStroke.getDashPhase());
        } else
            return stroke;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
