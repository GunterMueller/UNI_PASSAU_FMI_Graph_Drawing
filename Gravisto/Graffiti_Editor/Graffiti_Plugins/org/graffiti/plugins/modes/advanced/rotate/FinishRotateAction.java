// =============================================================================
//
//   FinishRotateAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FinishRotateAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.rotate;

import java.awt.Cursor;
import java.awt.Point;
import java.util.logging.Logger;

import org.graffiti.plugins.modes.advanced.AbstractEditingTool;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.plugins.modes.advanced.selection.SelectionTool;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class FinishRotateAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -1550209773932435947L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ChangeAttributesEdit.class.getName());

    /** Reference to the SelectionTool */
    private RotationTool rotationTool;

    /**
     * Creates a new FinishMoveAction
     * 
     * @param rotationTool
     *            The given SelectionTool
     */
    public FinishRotateAction(RotationTool rotationTool) {
        this.rotationTool = rotationTool;
    }

    /**
     * The action of this class
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        // the actual position of the mouse
        Point position = e.getPosition();

        // checks if the mouse position exists
        if (position != null) {
            // checks if the tool is in a "move" - mode
            if ((rotationTool.getMode() != AbstractEditingTool.DEFAULT)
                    && (rotationTool.getMode() != SelectionTool.RECT)) {
                // resets the 'helper' - variables and flags
                rotationTool.setDuring_update_rotation(false);
                rotationTool.setDuring_update_bend_rotation(false);
                rotationTool.setDuring_update_move(false);
                rotationTool.setModeToDefault();
                rotationTool.setAttributesMap(null);
                rotationTool.getActiveJComponent().setCursor(
                        new Cursor(Cursor.DEFAULT_CURSOR));
            }
        } else {
            logger.finer("Can't operate without position!");
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
