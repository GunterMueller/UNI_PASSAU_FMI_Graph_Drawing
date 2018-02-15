// =============================================================================
//
//   GraphicsEngine.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.interactive.GraphElementFinder;
import org.graffiti.plugins.views.fast.label.Label;
import org.graffiti.plugins.views.fast.label.LabelCommand;
import org.graffiti.plugins.views.fast.label.LabelManager;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class GraphicsEngine<L extends Label<L, LC>, LC extends LabelCommand> {
    protected FastView fastView;

    protected AffineTransform viewingMatrix;

    protected AffineTransform inverseViewingMatrix;

    protected double zoom;

    protected Rectangle2D logicalDisplayBounds;

    protected OptimizationPolicy optimizationPolicy;

    protected boolean calculatesBounds;

    protected Map<Pair<Double, Dash>, Stroke> strokes;

    protected GraphicsEngine() {
        strokes = new HashMap<Pair<Double, Dash>, Stroke>();
    }

    protected void initialize(FastView fastView,
            OptimizationPolicy optimizationPolicy) {
        this.fastView = fastView;
        this.optimizationPolicy = optimizationPolicy;
        viewingMatrix = new AffineTransform();
        inverseViewingMatrix = new AffineTransform();
        logicalDisplayBounds = new Rectangle2D.Double(-0.5, -0.5, 1.0, 1.0);
        calculatesBounds = true;
    }

    public abstract JComponent getDrawingComponent();

    public abstract String getName();

    public abstract void onChangeViewingMatrix();

    public abstract void onChangeOptimizationPolicy();

    public abstract boolean knows(Node node);

    public abstract boolean knows(Edge edge);

    public abstract FontManager<?> getFontManager();

    public abstract ImageManager<L, LC> getImageManager();

    public abstract NodeShape getShape(Node node);

    public abstract EdgeShape getShape(Edge edge);

    public abstract LabelManager<L, LC> getLabelManager();

    public abstract GraphChangeListener<?> getGraphChangeListener();

    public abstract NodeChangeListener<?> getNodeChangeListener();

    public abstract EdgeChangeListener<?> getEdgeChangeListener();

    public abstract GraphElementFinder getGraphElementFinder();

    public abstract FastViewGestureFeedbackProvider getGestureFeedbackProvider();

    public abstract JPanel createConfigurationPanel();

    void setViewingMatrix(AffineTransform viewingMatrix,
            AffineTransform inverseViewingMatrix, double zoom,
            Rectangle2D logicalDisplayBounds) {
        this.viewingMatrix = viewingMatrix;
        this.inverseViewingMatrix = inverseViewingMatrix;
        this.zoom = zoom;
        this.logicalDisplayBounds = logicalDisplayBounds;
        onChangeViewingMatrix();
    }

    void setOptimizationPolicy(OptimizationPolicy optimizationPolicy) {
        boolean notify = this.optimizationPolicy != optimizationPolicy;
        this.optimizationPolicy = optimizationPolicy;
        if (notify) {
            onChangeOptimizationPolicy();
        }
    }

    public OptimizationPolicy getOptimizationPolicy() {
        return optimizationPolicy;
    }

    void setBoundsCalculation(boolean calculateBounds) {
        this.calculatesBounds = calculateBounds;
    }

    public Point2D transform(Point2D p) {
        return viewingMatrix.transform(p, new Point2D.Double());
    }

    public Point2D inverseTransform(Point2D p) {
        return inverseViewingMatrix.transform(p, new Point2D.Double());
    }

    public Stroke acquireStroke(double frameThickness, Dash dash) {
        Pair<Double, Dash> key = new Pair<Double, Dash>(frameThickness, dash);
        Stroke stroke = strokes.get(key);
        if (stroke == null) {
            stroke = new BasicStroke((float) frameThickness,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    GraphicAttributeConstants.DEFAULT_MITER, dash
                            .getDashArray(), dash.getDashPhase());
        }
        return stroke;
    }

    protected abstract Color getBackgroundColor();

    protected abstract void setBackgroundColor(Color color);

    protected abstract Color getGridColor();

    protected abstract void setGridColor(Color color);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
