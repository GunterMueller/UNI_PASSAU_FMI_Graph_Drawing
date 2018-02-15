// =============================================================================
//
//   FirstPhaseContext.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import java.awt.Shape;
import java.awt.Stroke;

import org.graffiti.plugins.views.fast.opengl.TesselationData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface FirstPhaseContext {
    public TesselationData drawShape(Shape shape);

    public TesselationData fillShape(Shape shape);

    public void setClip(Shape clip);

    public void setStroke(Stroke stroke);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
