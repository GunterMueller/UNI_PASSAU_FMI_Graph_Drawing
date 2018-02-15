// =============================================================================
//
//   Java2DEngine.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.torical;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.graffiti.plugin.view.Grid;
import org.graffiti.plugins.grids.ToricalGrid;
import org.graffiti.plugins.views.fast.java2d.AbstractEdgeRep;
import org.graffiti.plugins.views.fast.java2d.AbstractNodeRep;
import org.graffiti.plugins.views.fast.java2d.Java2DEngine;

/**
 * {@code GraphicsEngine} using Java2D to draw the graph.
 * 
 * @author Wolfgang Brunner
 * @version $Revision$ $Date$
 */
public final class ToricalEngine extends Java2DEngine {

    private int torusWidth = 0;
    private int torusHeight = 0;

    // private ToricalFastView view;

    public ToricalEngine() {
        super();
        // this.view = null;
        graphElementFinder = new ToricalGraphElementFinder(nodes, edges);
    }

    public void setView(ToricalFastView view) {
        // this.view = view;
        ((ToricalGraphElementFinder) graphElementFinder).setView(view);
    }

    @Override
    protected void paint(Graphics2D g, int width, int height, boolean isGui) {
        Grid grid = fastView.getGrid();
        if (!(grid instanceof ToricalGrid)) {
            super.paint(g, width, height, isGui);
            return;
        }

        processPendingEdgeShapeChanges();
        labelManager.processChanges();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setBackground(backgroundColor);
        if (isGui) {
            g.clearRect(0, 0, width, height);
        }
        AffineTransform initialTransform = g.getTransform();
        AffineTransform transform = new AffineTransform(initialTransform);
        transform.concatenate(viewingMatrix);
        drawingSet.prepare(transform, g.getClip(), optimizationPolicy);
        g.setTransform(transform);

        ToricalGrid toricalGrid = (ToricalGrid) grid;
        torusWidth = toricalGrid.cellWidth * toricalGrid.horizontalCells;
        torusHeight = toricalGrid.cellHeight * toricalGrid.verticalCells;

        g.setColor(gridColor);
        for (Shape shape : grid.getShapes(isGui ? logicalDisplayBounds
                : fastView.getViewport().getLogicalElementsBounds())) {
            g.draw(shape);
        }

        Rectangle rec = null;
        double startX = 0;
        double endX = 0;
        double startY = 0;
        double endY = 0;

        AffineTransform actualTransform = new AffineTransform(
                drawingSet.affineTransform);
        AffineTransform currentTransform = new AffineTransform(
                drawingSet.affineTransform);

        for (AbstractEdgeRep edgeRep : sortedEdges) {
            rec = edgeRep.getShape().getBounds();
            startX = Math
                    .floor((logicalDisplayBounds.getMinX() - rec.getMaxX())
                            / torusWidth)
                    * torusWidth;
            endX = Math.ceil((logicalDisplayBounds.getMaxX() - rec.getMinX())
                    / torusWidth)
                    * torusWidth;
            startY = Math
                    .floor((logicalDisplayBounds.getMinY() - rec.getMaxY())
                            / torusHeight)
                    * torusHeight;
            endY = Math.ceil((logicalDisplayBounds.getMaxY() - rec.getMinY())
                    / torusHeight)
                    * torusHeight;
            for (double dx = startX; dx <= endX; dx += torusWidth) {
                for (double dy = startY; dy <= endY; dy += torusHeight) {
                    currentTransform.setTransform(actualTransform);
                    currentTransform.translate(dx, dy);
                    drawingSet.affineTransform.setTransform(currentTransform);
                    g.setTransform(currentTransform);
                    edgeRep.draw(g, drawingSet);
                    g.setTransform(transform);
                    drawingSet.affineTransform.setTransform(transform);
                }
            }
        }
        Shape shape = gestureFeedbackProvider.drawDummyEdge(g, drawingSet);
        if (shape != null) {
            rec = shape.getBounds();
            startX = Math
                    .floor((logicalDisplayBounds.getMinX() - rec.getMaxX())
                            / torusWidth)
                    * torusWidth;
            endX = Math.ceil((logicalDisplayBounds.getMaxX() - rec.getMinX())
                    / torusWidth)
                    * torusWidth;
            startY = Math
                    .floor((logicalDisplayBounds.getMinY() - rec.getMaxY())
                            / torusHeight)
                    * torusHeight;
            endY = Math.ceil((logicalDisplayBounds.getMaxY() - rec.getMinY())
                    / torusHeight)
                    * torusHeight;
            for (double dx = startX; dx <= endX; dx += torusWidth) {
                for (double dy = startY; dy <= endY; dy += torusHeight) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }
                    g.translate(dx, dy);
                    gestureFeedbackProvider.drawDummyEdge(g, drawingSet);
                    g.setTransform(transform);
                }
            }
        }

        for (AbstractNodeRep nodeRep : sortedNodes) {
            rec = nodeRep.getShape().getBounds();
            startX = Math
                    .floor((logicalDisplayBounds.getMinX() - rec.getMaxX())
                            / torusWidth)
                    * torusWidth;
            endX = Math.ceil((logicalDisplayBounds.getMaxX() - rec.getMinX())
                    / torusWidth)
                    * torusWidth;
            startY = Math
                    .floor((logicalDisplayBounds.getMinY() - rec.getMaxY())
                            / torusHeight)
                    * torusHeight;
            endY = Math.ceil((logicalDisplayBounds.getMaxY() - rec.getMinY())
                    / torusHeight)
                    * torusHeight;
            for (double dx = startX; dx <= endX; dx += torusWidth) {
                for (double dy = startY; dy <= endY; dy += torusHeight) {
                    currentTransform.setTransform(actualTransform);
                    currentTransform.translate(dx, dy);
                    drawingSet.affineTransform.setTransform(currentTransform);
                    g.setTransform(currentTransform);
                    nodeRep.draw(g, drawingSet);
                    g.setTransform(transform);
                    drawingSet.affineTransform.setTransform(transform);
                }
            }
        }
        gestureFeedbackProvider.drawDummyHub(g, drawingSet);
        Rectangle2D selectionRectangle = gestureFeedbackProvider
                .getSelectionRectangle();
        if (selectionRectangle != null) {
            rec = selectionRectangle.getBounds();
            startX = Math
                    .floor((logicalDisplayBounds.getMinX() - rec.getMaxX())
                            / torusWidth)
                    * torusWidth;
            endX = Math.ceil((logicalDisplayBounds.getMaxX() - rec.getMinX())
                    / torusWidth)
                    * torusWidth;
            startY = Math
                    .floor((logicalDisplayBounds.getMinY() - rec.getMaxY())
                            / torusHeight)
                    * torusHeight;
            endY = Math.ceil((logicalDisplayBounds.getMaxY() - rec.getMinY())
                    / torusHeight)
                    * torusHeight;
            for (double dx = startX; dx <= endX; dx += torusWidth) {
                for (double dy = startY; dy <= endY; dy += torusHeight) {
                    g.translate(dx, dy);
                    gestureFeedbackProvider.drawSelectionRectangle(g,
                            drawingSet);
                    g.setTransform(transform);
                }
            }
        }
        gestureFeedbackProvider.drawCompass(g, drawingSet, width, height,
                initialTransform);
        if (isGui) {
            g.dispose();
        }
        if (calculatesBounds) {
            fastView.getViewport().setLogicalBounds(drawingSet.getBounds());
        }
    }

    public double getTorusWidth() {
        return torusWidth;
    }

    public double getTorusHeight() {
        return torusHeight;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
