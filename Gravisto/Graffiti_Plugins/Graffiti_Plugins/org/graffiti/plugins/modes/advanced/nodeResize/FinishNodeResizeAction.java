// =============================================================================
//
//   FinishNodeResizeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FinishNodeResizeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.nodeResize;

import java.awt.Cursor;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;

/**
 * FinishNodeResizeAction.java Created: Tue Jun 8 17:42:45 2004
 * 
 * @author <a href="mailto:">Wolfgang Pausch</a>
 * @version 1.0
 * @deprecated
 */
@Deprecated
public class FinishNodeResizeAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -1788019960024699476L;
    /** DOCUMENT ME! */
    private NodeResizeTool nodeResizeTool;

    /**
     * Creates a new FinishNodeResizeAction object.
     * 
     * @param nodeResizeTool
     *            DOCUMENT ME!
     */
    public FinishNodeResizeAction(NodeResizeTool nodeResizeTool) {
        this.nodeResizeTool = nodeResizeTool;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {

        GraffitiSingleton gs = GraffitiSingleton.getInstance();
        Cursor c = new Cursor(Cursor.DEFAULT_CURSOR);
        gs.getMainFrame().setCursor(c);
        nodeResizeTool.stopResizing();
        nodeResizeTool.switchToDefaultMode();
        nodeResizeTool.setDuringNodeResize(false);

        // System.out.println("FinishResizeAction");

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
