package org.graffiti.plugin.view;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

/**
 * A {@code Grid} class, which represents an empty grid without any grid lines.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class NoGrid extends AbstractGrid {
    /**
     * {@inheritDoc} This implementation returns an empty list.
     */
    @Override
    public List<Shape> getShapes(Rectangle2D area) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc} This implementation simply rounds {@code point} to integer
     * coordinates.
     */
    @Override
    public Point2D snap(Point2D point, double tolerance) {
        return new Point2D.Double(Math.floor(point.getX()), Math.floor(point
                .getY()));
    }
}
