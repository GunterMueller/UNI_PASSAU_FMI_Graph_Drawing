// =============================================================================
//
//   Java2DFastView.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Color;
import java.awt.Graphics2D;

import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.GraphicsEngine;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DFastView extends FastView {
    /**
     * 
     */
    private static final long serialVersionUID = 3287662993355845953L;
    protected Java2DEngine engine;

    public Java2DFastView() {
        super(new Java2DEngine());
        engine = (Java2DEngine) getGraphicsEngine();
    }

    protected Java2DFastView(GraphicsEngine<?, ?> engine) {
        super(engine);
    }

    @Override
    public void print(Graphics2D graphics, int width, int height) {
        engine.paint(graphics, width, height, false);
    }

    public Color getBackgroundColor() {
        return engine.getBackgroundColor();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
