// =============================================================================
//
//   RemoveBendsAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RemoveBendsAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.editor.MainFrame;
import org.graffiti.editor.actions.SelectGraphElementActionListener;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;

/**
 * This is a special action for the popup menu. It removes all bends from one or
 * more selected edges. If the mouse points on a selected edge, this edge's
 * bends is removed. If the mouse does not point on an edge, the bends of all
 * selected edges is removed.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class RemoveBendsAction extends SelectionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 6996365735873009058L;
    private AbstractEditingTool editingTool;

    /**
     * Creates a new Remove Bends Action.
     * 
     * @param mf
     *            The mainframe.
     * @param editingTool
     *            The editing tool.
     */
    public RemoveBendsAction(MainFrame mf, AbstractEditingTool editingTool) {
        super("Remove Bends", mf);
        this.editingTool = editingTool;
        mainFrame.addSelectionListener(new SelectGraphElementActionListener(
                this));
    }

    /**
     * Checks the enabled flag, which tells if the Remove Bends button in the
     * popup menu should be enabled
     * 
     * @return the enabled flag
     */
    @Override
    public boolean isEnabled() {
        try {
            return !mainFrame.getActiveEditorSession().getSelectionModel()
                    .getActiveSelection().getEdges().isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /*
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /*
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Point mousePos = editingTool.getPositionInfo().getLastPopupPosition();
        Edge topEdge = editingTool.getTopEdge(mousePos);

        LinkedList<Edge> edges = new LinkedList<Edge>();

        if (topEdge == null) {
            edges.addAll(mainFrame.getActiveEditorSession().getSelectionModel()
                    .getActiveSelection().getEdges());
        } else {
            edges.add(topEdge);
        }
        for (Edge edge : edges) {
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            ListenerManager lm = edge.getGraph().getListenerManager();
            lm.transactionStarted(this);
            ega.setBends(new LinkedHashMapAttribute(ega.getBends().getId()));

            edge.changeString(GraphicAttributeConstants.SHAPE_PATH,
                    GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME);

            lm.transactionFinished(this);
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
