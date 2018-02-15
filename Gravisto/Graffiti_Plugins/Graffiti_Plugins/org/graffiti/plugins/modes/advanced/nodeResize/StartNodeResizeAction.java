// =============================================================================
//
//   StartNodeResizeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StartNodeResizeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.nodeResize;

import java.awt.Point;
import java.util.HashMap;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.plugins.views.defaults.NodeComponent;

/**
 * StartNodeResizeAction.java Created: Tue Jun 8 17:34:01 2004
 * 
 * @author <a href="mailto:">Wolfgang Pausch </a>
 * @version 1.0
 * @deprecated
 */
@Deprecated
public class StartNodeResizeAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1469241833628655907L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(StartNodeResizeAction.class.getName());

    /** Reference to the NodeResizeTool */
    private NodeResizeTool nodeResizeTool;

    /**
     * Creates a new StartNodeResizeAction object.
     * 
     * @param nodeResizeTool
     *            The given NodeResizeTool
     */
    public StartNodeResizeAction(NodeResizeTool nodeResizeTool) {
        this.nodeResizeTool = nodeResizeTool;
    }

    /**
     * The action of this class
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        Point position = e.getPosition();

        // needed for the undo/redo
        nodeResizeTool.setAttributesMap(new HashMap<Attribute, Object>());

        if (position == null) {
            logger.finer("Warning: Can't operate without position!!!");

            return;
        }

        // the component under mouse position
        NodeComponent nodeComponent = nodeResizeTool
                .getTopNodeComponent(position);

        // tool is in default mode
        if (nodeResizeTool.isInDefaultMode()) {
            // there is a component under mouse-position
            if (nodeComponent != null) {
                // start resizing
                nodeResizeTool.unmarkAll();

                NodeGraphicAttribute nodeAttribute = nodeComponent
                        .getNodeGraphicAttribute();
                nodeResizeTool.startResizing(nodeAttribute, position);
            }

            // no component under mouse-position
            else {
                // stop resize
                nodeResizeTool.unmarkAll();
                nodeResizeTool.stopResizing();
            }
        }

        // nodeResizeTool is in resize-mode
        else {
            // not during node-resize and underlying component exists
            if (!nodeResizeTool.isDuringNodeResize() && (nodeComponent != null)) {
                NodeGraphicAttribute nodeAttribute = nodeComponent
                        .getNodeGraphicAttribute();
                nodeResizeTool.startResizing(nodeAttribute, position);
            }

            // no component underlying
            else if (!nodeResizeTool.isDuringNodeResize()
                    && (nodeComponent == null)) {
                nodeResizeTool.stopResizing();
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
