// =============================================================================
//
//   SheepShape.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.shapes.nodes.dfa;

import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import org.graffiti.plugins.shapes.nodes.ArbitraryNodeShape;

public class EndNodeShape extends ArbitraryNodeShape {

    @Override
    protected GeneralPath getShape() {
        GeneralPath shape = new GeneralPath(getBoundary());
        shape.append(new Ellipse2D.Double(-0.7, -0.7, 1.4, 1.4), false);
        return shape;
    }

    @Override
    protected GeneralPath getBoundary() {
        GeneralPath boundary = new GeneralPath();
        boundary.append(new Ellipse2D.Double(-1, -1, 2, 2), false);
        return boundary;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
