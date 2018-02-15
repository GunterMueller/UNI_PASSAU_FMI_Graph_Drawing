// =============================================================================
//
//   AbstractOpenGLRep.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.util.Map;

import javax.media.opengl.GL;

import org.graffiti.graph.Node;
import org.graffiti.plugins.views.fast.AbstractRep;
import org.graffiti.plugins.views.fast.opengl.buffer.RebuildBufferException;
import org.graffiti.plugins.views.fast.opengl.buffer.TriangleBuffer;
import org.graffiti.plugins.views.fast.opengl.label.OpenGLLabel;
import org.graffiti.plugins.views.fast.opengl.label.commands.OpenGLLabelCommand;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class AbstractOpenGLRep extends
        AbstractRep<OpenGLLabel, OpenGLLabelCommand> {
    private static final short SELECTION_FLAG = 1;
    private static final short HOVER_FLAG = 2;
    private static final short CHANGE_SELECTIONHOVER_FLAG = 4;
    private static final short CHANGE_POSITION_FLAG = 8;
    private static final short CHANGE_DEPTH_FLAG = 16;
    private static final short CHANGE_COLOR_FLAG = 32;
    private static final short CHANGE_TESSELATION_FLAG = 64;
    private static final short CHANGE_SHAPE_FLAG = 128;
    private static final short PENDING_DELETION_FLAG = 256;
    private static final short DELETED_FLAG = 512;

    private Object stamp;
    private short changeStatus;

    protected AbstractOpenGLRep() {
        super();
    }

    // To call from ChangeProcessor
    public boolean isUpdated(Object currentStamp) {
        boolean result = stamp == currentStamp;
        stamp = currentStamp;
        return result;
    }

    public void free() {
        changeStatus |= PENDING_DELETION_FLAG;
    }

    public void setSelected(boolean isSelected) {
        if (((changeStatus & SELECTION_FLAG) == 0) == isSelected) {
            changeStatus = (short) ((changeStatus ^ SELECTION_FLAG) | CHANGE_SELECTIONHOVER_FLAG);
        }
    }

    public void setHover(boolean isHover) {

        if (((changeStatus & HOVER_FLAG) == 0) == isHover) {
            changeStatus = (short) ((changeStatus ^ HOVER_FLAG) | CHANGE_SELECTIONHOVER_FLAG);
        }
    }

    protected void setSelectionHoverChangeFlag() {
        changeStatus |= CHANGE_SELECTIONHOVER_FLAG;
    }

    protected boolean isSelected() {
        return (changeStatus & SELECTION_FLAG) != 0;
    }

    protected boolean isHover() {
        return (changeStatus & HOVER_FLAG) != 0;
    }

    protected boolean resetSelectionChangeFlag() {
        boolean hasChanged = (changeStatus & CHANGE_SELECTIONHOVER_FLAG) != 0;
        changeStatus &= ~CHANGE_SELECTIONHOVER_FLAG;
        return hasChanged;
    }

    public void setShapeChangeFlag() {
        changeStatus |= CHANGE_SHAPE_FLAG;
    }

    public boolean resetShapeChangeFlag() {
        boolean result = (changeStatus & CHANGE_SHAPE_FLAG) != 0;
        changeStatus &= ~CHANGE_SHAPE_FLAG;
        return result;
    }

    public void setPositionChangeFlag() {
        changeStatus |= CHANGE_POSITION_FLAG;
    }

    public boolean resetPositionChangeFlag() {
        boolean result = (changeStatus & CHANGE_POSITION_FLAG) != 0;
        changeStatus &= ~CHANGE_POSITION_FLAG;
        return result;
    }

    public void setDepthChangeFlag() {
        changeStatus |= CHANGE_DEPTH_FLAG;
    }

    public boolean resetDepthChangeFlag() {
        boolean result = (changeStatus & CHANGE_DEPTH_FLAG) != 0;
        changeStatus &= ~CHANGE_DEPTH_FLAG;
        return result;
    }

    public void setColorChangeFlag() {
        changeStatus |= CHANGE_COLOR_FLAG;
    }

    public boolean resetColorChangeFlag() {
        boolean result = (changeStatus & CHANGE_COLOR_FLAG) != 0;
        changeStatus &= ~CHANGE_COLOR_FLAG;
        return result;
    }

    public void setTesselationChangeFlag() {
        changeStatus |= CHANGE_TESSELATION_FLAG;
    }

    public boolean resetTesselationChangeFlag() {
        boolean result = (changeStatus & CHANGE_TESSELATION_FLAG) != 0;
        changeStatus &= ~CHANGE_TESSELATION_FLAG;
        return result;
    }

    private void processDeletionIfFlagged(OpenGLEngine engine) {
        if ((changeStatus & PENDING_DELETION_FLAG) != 0) {
            delete();
            engine.removeRep(this);
            changeStatus = (short) ((changeStatus ^ PENDING_DELETION_FLAG) | DELETED_FLAG);
        }
    }

    private boolean isDeleted() {
        return (changeStatus & DELETED_FLAG) != 0;
    }

    public void estimate(TriangleBuffer buffer,
            Map<Node, AbstractNodeRep> nodes, OpenGLEngine engine) {
        if (isDeleted())
            return;
        if (resetShapeChangeFlag()) {
            retrieveShape(nodes);
        }
        resetTesselationChangeFlag();
        tesselate(engine);
        estimate(buffer);
    }

    public void process(TriangleBuffer buffer,
            Map<Node, AbstractNodeRep> nodes, OpenGLEngine engine)
            throws RebuildBufferException {
        processDeletionIfFlagged(engine);
        if (isDeleted())
            return;
        boolean hasShapeChanged = resetShapeChangeFlag();
        if (hasShapeChanged) {
            retrieveShape(nodes);
        }
        if (resetTesselationChangeFlag() || hasShapeChanged) {
            tesselate(engine);
        }
        if (hasTesselationData()) {
            allocate(buffer);
            setPositionChangeFlag();
            resetDepthChangeFlag();
            setColorChangeFlag();
            setSelectionHoverChangeFlag();
        }
        if (resetPositionChangeFlag()) {
            updatePosition();
        }
        if (resetColorChangeFlag()) {
            updateColor();
        }
        if (resetDepthChangeFlag()) {
            updateElementDepth();
        }
        if (resetSelectionChangeFlag()) {
            if (isSelected()) {
                setAsSelected();
            } else if (isHover()) {
                setAsHovered();
            } else {
                setAsUnselected();
            }
        }
    }

    protected abstract void retrieveShape(Map<Node, AbstractNodeRep> nodes);

    protected abstract void tesselate(OpenGLEngine engine);

    protected abstract void estimate(TriangleBuffer buffer);

    protected abstract boolean hasTesselationData();

    /**
     * ... Free old buffer blocks. Allocate new buffer blocks and fill them. Set
     * {@link TesselationData} properties to null.
     * 
     * @param buffer
     */
    protected abstract void allocate(TriangleBuffer buffer)
            throws RebuildBufferException;

    protected abstract void updateElementDepth();

    protected abstract void updatePosition();

    protected abstract void updateColor();

    protected abstract void setAsSelected();

    protected abstract void setAsHovered();

    protected abstract void setAsUnselected();

    protected abstract void delete();

    protected void drawLabels(GL gl, TextBuffer textBuffer) {
        if (labels == null)
            return;
        for (OpenGLLabel label : labels) {
            label.draw(gl, depth);
            label.addTextsToBuffer(textBuffer, depth);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
