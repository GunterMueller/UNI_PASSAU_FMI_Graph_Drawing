// =============================================================================
//
//   StandardArrowShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StandardArrowShape.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 5766 $
 */
public class StandardArrowShape extends AbstractArrowShape {

    /**
     * Constructs a new arrow. Creates the <code>GeneralPath</code> representing
     * the arrow and sets head and anchor.
     */
    public StandardArrowShape() {
        super();

        // arrow points horizontally to the right
        GeneralPath arrow = new GeneralPath();
        arrow.moveTo(0f, 0f);
        arrow.lineTo(0f, SIZE);
        arrow.lineTo(SIZE, SIZE / 2f);

        // arrow.lineTo(0f, 0f);
        arrow.closePath();

        this.head = new Point2D.Double(SIZE, SIZE / 2d);
        this.anchor = new Point2D.Double(0, SIZE / 2d);
        this.arrowShape = arrow;
        this.arrowWidth = this.arrowShape.getBounds2D().getHeight();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
