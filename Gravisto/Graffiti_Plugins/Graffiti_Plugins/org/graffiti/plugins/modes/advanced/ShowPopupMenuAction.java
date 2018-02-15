// =============================================================================
//
//   ShowPopupMenuAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ShowPopupMenuAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.Cursor;
import java.awt.Point;

import javax.swing.JComponent;

import org.graffiti.graph.Node;
import org.graffiti.plugins.modes.advanced.nodeResize.NodeResizeTool;
import org.graffiti.plugins.modes.advanced.selection.align.AlignTool;

/**
 * ShowPopupMenuAction.java
 * 
 * @deprecated
 */
@Deprecated
public class ShowPopupMenuAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -6974554540129073274L;
    /** Reference to the active Tool */
    private AbstractEditingTool tool;

    /**
     * Creates a new ShowPopupMenuAction object.
     * 
     * @param tool
     *            Constructor of the class
     */
    public ShowPopupMenuAction(AbstractEditingTool tool) {
        this.tool = tool;
    }

    /**
     * The action of the class.
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        JComponent viewComponent = tool.getViewComponent();
        Point position = e.getPosition();

        if (position != null) {
            if (tool.getMode() == AbstractEditingTool.DEFAULT) {
                if ((tool.getTopNode(position) != null)
                        && !(tool instanceof AlignTool)) {
                    Node node = tool.getTopNode(position);

                    if (tool instanceof NodeResizeTool) {
                        Cursor c = new Cursor(Cursor.DEFAULT_CURSOR);
                        tool.getActiveJComponent().setCursor(c);
                    }

                    if (!tool.getSelection().contains(node)) {
                        tool.unmarkAll();
                        tool.mark(node);
                    }
                }

                position = tool.getZoomedPosition(position);
                tool.getPopupMenu().show(viewComponent, (int) position.getX(),
                        (int) position.getY());
            }
        } else {
            System.out
                    .println("ShowPopupMenuAction says: "
                            + "Warning: Can't operate without position assigned to event!");
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
