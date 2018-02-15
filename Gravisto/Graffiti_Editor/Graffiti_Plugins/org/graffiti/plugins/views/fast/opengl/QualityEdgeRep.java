// =============================================================================
//
//   QualityEdgeRep.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.Dash;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.opengl.buffer.BufferBlock;
import org.graffiti.plugins.views.fast.opengl.buffer.RebuildBufferException;
import org.graffiti.plugins.views.fast.opengl.buffer.TriangleBuffer;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class QualityEdgeRep extends AbstractEdgeRep {
    private static class TessData {
        public TesselationDataList fillData;
        public TesselationDataList frameData;
        public TesselationDataList primarySelectionData;
        public TesselationDataList secondarySelectionData;

        public TessData(TesselationDataList fillData,
                TesselationDataList frameData,
                TesselationDataList primarySelectionData,
                TesselationDataList secondarySelectionData) {
            this.fillData = fillData;
            this.frameData = frameData;
            this.primarySelectionData = primarySelectionData;
            this.secondarySelectionData = secondarySelectionData;
        }
    };

    private TessData tesselationData;
    private BufferBlock fillBlock;
    private BufferBlock frameBlock;
    private BufferBlock primarySelectionBlock;
    private BufferBlock secondarySelectionBlock;

    protected QualityEdgeRep(Edge edge) {
        super(edge);
    }

    @Override
    protected void allocate(TriangleBuffer buffer)
            throws RebuildBufferException {
        if (fillBlock != null && fillBlock.isValid()) {
            fillBlock.free();
            frameBlock.free();
            primarySelectionBlock.free();
            secondarySelectionBlock.free();
        }
        TesselationDataList fillData = tesselationData.fillData;
        fillBlock = buffer.allocate(fillData.getVertexCount(), fillData
                .getIndexCount());
        fillBlock.fillWith(fillData, depth);
        TesselationDataList frameData = tesselationData.frameData;
        frameBlock = buffer.allocate(frameData.getVertexCount(), frameData
                .getIndexCount());
        frameBlock.fillWith(frameData, depth);
        TesselationDataList primarySelectionData = tesselationData.primarySelectionData;
        primarySelectionBlock = buffer.allocate(primarySelectionData
                .getVertexCount(), primarySelectionData.getIndexCount());
        primarySelectionBlock.fillWith(primarySelectionData, depth);
        TesselationDataList secondarySelectionData = tesselationData.secondarySelectionData;
        secondarySelectionBlock = buffer.allocate(secondarySelectionData
                .getVertexCount(), secondarySelectionData.getIndexCount());
        secondarySelectionBlock.fillWith(secondarySelectionData, depth);
        secondarySelectionBlock.setColor(FastViewPlugin.CONTROL_POINT_COLOR);
        tesselationData = null;
    }

    @Override
    protected void delete() {
        if (fillBlock != null && fillBlock.isValid()) {
            fillBlock.free();
            frameBlock.free();
            primarySelectionBlock.free();
            secondarySelectionBlock.free();
        }
    }

    @Override
    protected void estimate(TriangleBuffer buffer) {
        TesselationDataList fillData = tesselationData.fillData;
        TesselationDataList frameData = tesselationData.frameData;
        TesselationDataList primarySelectionData = tesselationData.primarySelectionData;
        TesselationDataList secondarySelectionData = tesselationData.secondarySelectionData;
        buffer.incEstimate(fillData.getVertexCount()
                + frameData.getVertexCount()
                + primarySelectionData.getVertexCount()
                + secondarySelectionData.getVertexCount(), fillData
                .getIndexCount()
                + frameData.getIndexCount()
                + primarySelectionData.getIndexCount()
                + secondarySelectionData.getIndexCount());
    }

    @Override
    protected boolean hasTesselationData() {
        return tesselationData != null;
    }

    @Override
    protected void setPrimarySelectionColor(Color color) {
        primarySelectionBlock.setColor(color);
    }

    @Override
    protected void setPrimarySelectionDepth(double depth) {
        primarySelectionBlock.setDepth(depth);
    }

    @Override
    protected void setSecondarySelectionDepth(double depth) {
        secondarySelectionBlock.setDepth(depth);
    }

    @Override
    protected void tesselate(OpenGLEngine engine) {
        Shape tailArrow = shape.getTailArrow();
        Shape headArrow = shape.getHeadArrow();
        double frameThickness = AttributeUtil.getFrameThickness(edge);
        Dash dash = AttributeUtil.getDash(edge);
        Stroke lineStroke = engine.acquireStroke(frameThickness, dash);
        Stroke arrowStroke = engine.acquireStroke(
                Math.min(1.0, frameThickness), FastViewPlugin.DEFAULT_DASH);
        Tesselator tesselator = Tesselator.get();
        TesselationDataList fillData = new TesselationDataList();
        if (tailArrow != null) {
            fillData.add(tesselator.tesselate(tailArrow));
        }
        if (headArrow != null) {
            fillData.add(tesselator.tesselate(headArrow));
        }
        TesselationDataList frameData = new TesselationDataList();
        frameData.add(tesselator.tesselate(shape, lineStroke));
        if (tailArrow != null) {
            frameData.add(tesselator.tesselate(tailArrow, arrowStroke));
        }
        if (headArrow != null) {
            frameData.add(tesselator.tesselate(headArrow, arrowStroke));
        }
        // TODO: primarySelectionData, secondarySelectionData
        TesselationDataList primarySelectionData = new TesselationDataList();
        TesselationDataList secondarySelectionData = new TesselationDataList();

        final int EBS = FastViewPlugin.EDGE_BEND_SIZE;
        final int ECS = FastViewPlugin.EDGE_CONTROLPOINT_SIZE;
        double coords[] = new double[6];
        for (PathIterator iter = shape.getPathIterator(null); !iter.isDone(); iter
                .next()) {
            int seg = iter.currentSegment(coords);
            switch (seg) {
            case PathIterator.SEG_CUBICTO:
                coords[2] = coords[4];
                coords[3] = coords[5];
                // Fallthrough
            case PathIterator.SEG_QUADTO:
                secondarySelectionData.add(tesselator
                        .tesselate(new Ellipse2D.Double(coords[0] - 1,
                                coords[1] - 1, ECS, ECS)));
                coords[0] = coords[2];
                coords[1] = coords[3];
                // Fallthrough
            case PathIterator.SEG_MOVETO:
                // Fallthrough
            case PathIterator.SEG_LINETO:
                primarySelectionData.add(tesselator
                        .tesselate(new Rectangle2D.Double(coords[0] - 3,
                                coords[1] - 3, EBS, EBS)));
                break;
            }
        }

        tesselationData = new TessData(fillData, frameData,
                primarySelectionData, secondarySelectionData);
    }

    @Override
    protected void updateColor() {
        Color fillColor = AttributeUtil.getFillColor(edge);
        fillBlock.setColor(fillColor);
        Color frameColor = AttributeUtil.getFrameColor(edge);
        frameBlock.setColor(frameColor);
    }

    @Override
    protected void updateElementDepth() {
        fillBlock.setDepth(depth);
        frameBlock.setDepth(depth);
        // TODO: selected/hover -> primarySelectionBlock.setDepth(depth);
        // TODO: selected/hover -> secondarySelectionBlock.setDepth(depth);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
