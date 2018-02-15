// =============================================================================
//
//   FinishSelectRectAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FinishSelectRectAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.rotate;

import java.awt.Point;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;

/**
 * Implementation of an action to finish the selection with a rectangle
 * 
 * @author MH Implementation of an action to finish the selection with a
 *         rectangle
 * @deprecated
 */
@Deprecated
public class FinishSelectRectAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 5785044700225115064L;

    /** Reference to the AbstractEditingTool */
    public RotationTool rotationTool;

    private static final Logger logger = Logger
            .getLogger(FinishSelectRectAction.class.getName());

    /**
     * Creates a new FinishSelectRectAction
     * 
     * @param rotationTool
     *            the given AbstractEditingTool
     */
    public FinishSelectRectAction(RotationTool rotationTool) {
        this.rotationTool = rotationTool;
    }

    /**
     * Performs the action of this class
     * 
     * @param e
     *            the given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        Point position = e.getPosition();

        // Check if the mouse position is inside the editor
        if (position == null) {
            logger.finer("Warning: " + "Can't operate without position!");
        } else {
            // Check if the tool is in the selection-rectangle-mode
            if (rotationTool.getMode() == RotationTool.RECT) {
                rotationTool.setStartSelectRectPosition(null);
                rotationTool.setCurrentSelectRectPosition(null);

                rotationTool.getCurrentSelectedElements().clear();
                rotationTool.setModeToDefault();

                JComponent viewComponent = rotationTool.getViewComponent();
                viewComponent.paintImmediately(viewComponent.getVisibleRect());
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
