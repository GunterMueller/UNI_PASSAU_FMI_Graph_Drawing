// =============================================================================
//
//   EdgeChangeListener.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.Color;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.EdgeShape;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class EdgeChangeListener<T extends GraphicsEngine<?, ?>> {
    protected T engine;

    protected EdgeChangeListener(T engine) {
        this.engine = engine;
    }

    public abstract void onChangeDepth(Edge edge, double depth);

    public abstract void onSetShape(Edge edge, EdgeShape shape);

    public abstract void onChangeShape(Edge edge, EdgeGraphicAttribute attribute);

    public abstract void onReverse(Edge edge);

    public abstract void onChangeFrameThickness(Edge edge, double frameThickness);

    public abstract void onChangeThickness(Edge edge, double thickness);

    public abstract void onChangeDash(Edge edge, Dash dash);

    public abstract void onChangeFrameColor(Edge edge, Color color);

    public abstract void onChangeFillColor(Edge edge, Color color);

    public abstract void onChangeSelection(Edge edge, boolean isSelected);

    public abstract void onChangeHover(Edge edge, boolean isHover);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
