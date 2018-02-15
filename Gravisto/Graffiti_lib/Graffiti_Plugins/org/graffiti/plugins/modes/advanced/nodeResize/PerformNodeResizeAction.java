// =============================================================================
//
//   PerformNodeResizeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PerformNodeResizeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.nodeResize;

import java.awt.Point;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.undo.UndoableEditSupport;

import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * Action for function perform-node-resize.
 * 
 * @deprecated
 */
@Deprecated
public class PerformNodeResizeAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -2363482861691806384L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ChangeAttributesEdit.class.getName());

    /** Reference to the NodeResizeTool */
    private NodeResizeTool nodeResizeTool;

    /**
     * Creates a new PerformNodeResizeAction object.
     * 
     * @param nodeResizeTool
     *            DOCUMENT ME!
     */
    public PerformNodeResizeAction(NodeResizeTool nodeResizeTool) {
        this.nodeResizeTool = nodeResizeTool;
    }

    /**
     * The action of this class.
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        Point position = e.getPosition();

        if (position == null) {
            logger.finer("Warning: Can't operate without position!!!");

            return;
        }

        // checks if tool is in resize-mode
        if (nodeResizeTool.isInResizeMode()) {
            double minNodeSize = nodeResizeTool.getMinNodeSize();

            NodeResizer nodeResizer = nodeResizeTool.getNodeResizer();

            if (nodeResizer != null) {
                // for undo/redo
                if (!nodeResizeTool.isDuringNodeResize()) {
                    ChangeAttributesEdit edit;
                    Map<GraphElement, GraphElement> geMap = nodeResizeTool
                            .getGEMap();
                    UndoableEditSupport undoSupport = nodeResizeTool
                            .getUndoSupport();

                    NodeGraphicAttribute nodeGraphicAttribute = nodeResizeTool
                            .getResizeNode();
                    NodeGraphicAttribute nodeGraphicAttCopy = (NodeGraphicAttribute) nodeGraphicAttribute
                            .copy();
                    Object nodeGraphicAttCopyValue = nodeGraphicAttCopy
                            .getValue();
                    nodeResizeTool.addAttributesToMap(nodeGraphicAttribute,
                            nodeGraphicAttCopyValue);

                    edit = new ChangeAttributesEdit(nodeResizeTool
                            .getAttributesMap(), geMap);

                    undoSupport.postEdit(edit);
                    nodeResizeTool.setDuringNodeResize(true);
                }

                // the resizing
                nodeResizeTool.getNodeResizer().updateNode(minNodeSize,
                        position);
            } else {
                logger.info("Please begin near the border!");
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
