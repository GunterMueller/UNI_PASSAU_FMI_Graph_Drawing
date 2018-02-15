// =============================================================================
//
//   FinishMoveAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FinishMoveAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.selection;

import java.awt.Point;
import java.util.logging.Logger;

import org.graffiti.plugins.modes.advanced.AbstractEditingTool;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * To finish the movement of elements.
 * 
 * @author MH
 * @deprecated
 */
@Deprecated
public class FinishMoveAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 8195483914528093241L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ChangeAttributesEdit.class.getName());

    /** Reference to the SelectionTool */
    private SelectionTool selectionTool;

    /**
     * Creates a new FinishMoveAction
     * 
     * @param selectionTool
     *            The given SelectionTool
     */
    public FinishMoveAction(SelectionTool selectionTool) {
        this.selectionTool = selectionTool;
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
            if ((selectionTool.getMode() != AbstractEditingTool.DEFAULT)
                    && (selectionTool.getMode() != SelectionTool.RECT)) {
                // resets the 'helper' - variables and flags
                selectionTool.setMoveElement(null);
                selectionTool.setDuring_update_move(false);
                selectionTool.setDuring_update_move_bend(false);
                selectionTool.setDuring_update_move_only_node(false);
                selectionTool.setModeToDefault();
                selectionTool.setAttributesMap(null);
            }
        } else {
            logger.finer("Can't operate without position!");
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
