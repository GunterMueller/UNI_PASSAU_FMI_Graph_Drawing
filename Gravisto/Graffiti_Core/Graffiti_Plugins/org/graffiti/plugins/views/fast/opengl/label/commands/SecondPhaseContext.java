// =============================================================================
//
//   SecondPhaseContext.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label.commands;

import java.awt.Color;

import org.graffiti.plugins.views.fast.opengl.OpenGLFont;
import org.graffiti.plugins.views.fast.opengl.TesselationData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface SecondPhaseContext {
    public void addData(TesselationData data);

    public void addText(String string, double x, double y);

    public void setColor(Color color);

    public void translate(double dx, double dy);

    public void setFont(OpenGLFont font);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
