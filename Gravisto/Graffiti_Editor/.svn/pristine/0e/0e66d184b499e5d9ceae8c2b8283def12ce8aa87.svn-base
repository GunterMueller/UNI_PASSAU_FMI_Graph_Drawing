// =============================================================================
//
//   SheepShape.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.shapes.nodes.dfa;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import org.graffiti.plugins.shapes.nodes.ArbitraryNodeShape;
import org.graffiti.plugins.views.defaults.StandardArrowShape;

public class StartNodeShape extends ArbitraryNodeShape {

    @Override
    protected GeneralPath getShape() {
        GeneralPath shape = new GeneralPath();
        shape.append(new Ellipse2D.Double(-1, -1, 2, 2), false);
        shape.append(new Line2D.Double(-1, 0, -2, 0), false);

        AffineTransform at = new AffineTransform();
        at.scale(0.04, 0.04);
        at.translate(-35.5, -5);
        shape.append((new StandardArrowShape()).getPathIterator(at), false);

        return shape;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
