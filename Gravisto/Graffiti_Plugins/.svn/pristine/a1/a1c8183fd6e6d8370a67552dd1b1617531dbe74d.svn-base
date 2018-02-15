// =============================================================================
//
//   QualityNodeRep.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.graffiti.graph.Node;
import org.graffiti.plugins.views.defaults.RectangleNodeShape;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.opengl.buffer.BufferBlock;
import org.graffiti.plugins.views.fast.opengl.buffer.RebuildBufferException;
import org.graffiti.plugins.views.fast.opengl.buffer.TriangleBuffer;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class QualityNodeRep extends AbstractNodeRep {
    private static class TessData {
        public TesselationData fillData;
        public TesselationData frameData;
        public TesselationDataList selectionData;

        public TessData(TesselationData fillData, TesselationData frameData,
                TesselationDataList selectionData) {
            this.fillData = fillData;
            this.frameData = frameData;
            this.selectionData = selectionData;
        }
    }

    private TessData tesselationData;
    private BufferBlock fillBlock;
    private BufferBlock frameBlock;
    private BufferBlock selectionBlock;

    protected QualityNodeRep(Node node) {
        super(node);
    }

    @Override
    protected void allocate(TriangleBuffer buffer)
            throws RebuildBufferException {
        if (fillBlock != null && fillBlock.isValid()) {
            fillBlock.free();
            frameBlock.free();
            selectionBlock.free();
        }
        TesselationData fillData = tesselationData.fillData;
        fillBlock = buffer.allocate(fillData.getVertexCount(), fillData
                .getIndexCount());
        fillBlock.fillWith(fillData, depth);
        TesselationData frameData = tesselationData.frameData;
        frameBlock = buffer.allocate(frameData.getVertexCount(), frameData
                .getIndexCount());
        frameBlock.fillWith(frameData, depth);
        TesselationDataList selectionData = tesselationData.selectionData;
        selectionBlock = buffer.allocate(selectionData.getVertexCount(),
                selectionData.getIndexCount());
        selectionBlock.fillWith(selectionData, depth);
        tesselationData = null;
        Rectangle2D bounds = shape.getBounds2D();
        setPosition(new Point2D.Double(bounds.getCenterX(), bounds.getCenterY()));
    }

    @Override
    protected void delete() {
        if (fillBlock != null && fillBlock.isValid()) {
            fillBlock.free();
            frameBlock.free();
            selectionBlock.free();
        }
    }

    @Override
    protected void estimate(TriangleBuffer buffer) {
        TesselationData fillData = tesselationData.fillData;
        TesselationData frameData = tesselationData.frameData;
        TesselationDataList selectionData = tesselationData.selectionData;
        buffer.incEstimate(fillData.getVertexCount()
                + frameData.getVertexCount() + selectionData.getVertexCount(),
                fillData.getIndexCount() + frameData.getIndexCount()
                        + selectionData.getIndexCount());
    }

    @Override
    protected boolean hasTesselationData() {
        return tesselationData != null;
    }

    @Override
    protected void setSelectionColor(Color color) {
        selectionBlock.setColor(color);
    }

    @Override
    protected void setSelectionDepth(double depth) {
        selectionBlock.setDepth(depth);
    }

    @Override
    protected void tesselate(OpenGLEngine engine) {
        double frameThickness = AttributeUtil.getFrameThickness(node);
        Rectangle2D bounds = shape.getBounds2D();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        Shape actualShape = shape;
        if (shape instanceof RectangleNodeShape) {
            actualShape = new Rectangle2D.Double(frameThickness / 2.0,
                    frameThickness / 2.0, width - frameThickness, height
                            - frameThickness);
        }
        Stroke stroke = engine.acquireStroke(frameThickness, AttributeUtil
                .getDash(node));
        Tesselator tesselator = Tesselator.get();
        TesselationData fillData = tesselator.tesselate(actualShape);
        TesselationData frameData = tesselator.tesselate(actualShape, stroke);
        TesselationDataList selectionData = new TesselationDataList();
        int hs = FastViewPlugin.NODE_HANDLE_SIZE + 1;
        double x2 = width - hs;
        double y2 = height - hs;
        selectionData.add(tesselator.tesselate(new Rectangle2D.Double(0, 0, hs,
                hs)));
        selectionData.add(tesselator.tesselate(new Rectangle2D.Double(x2, 0,
                hs, hs)));
        selectionData.add(tesselator.tesselate(new Rectangle2D.Double(0, y2,
                hs, hs)));
        selectionData.add(tesselator.tesselate(new Rectangle2D.Double(x2, y2,
                hs, hs)));
        tesselationData = new TessData(fillData, frameData, selectionData);
    }

    @Override
    protected void updateColor() {
        fillBlock.setColor(AttributeUtil.getFillColor(node));
        frameBlock.setColor(AttributeUtil.getFrameColor(node));
    }

    @Override
    protected void updateElementDepth() {
        fillBlock.setDepth(depth);
        frameBlock.setDepth(depth);
        // TODO: selected/hover -> selectionBlock.setDepth(depth);
    }

    @Override
    protected void updatePosition() {
        Point2D newPosition = AttributeUtil.getPosition(node);
        double dx = newPosition.getX() - position.getX();
        double dy = newPosition.getY() - position.getY();
        fillBlock.translate(dx, dy);
        frameBlock.translate(dx, dy);
        selectionBlock.translate(dx, dy);
        setPosition(newPosition);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
