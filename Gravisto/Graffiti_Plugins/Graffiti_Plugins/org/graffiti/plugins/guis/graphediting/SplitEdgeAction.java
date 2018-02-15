// =============================================================================
//
//   SplitEdgeAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SplitEdgeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;

/**
 * Processed on all selected edges or all edges, if no edge selected. Splits the
 * according edge into two edges inserting a new node in between them.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2009-11-06 23:43:41 +0100 (Fr, 06 Nov 2009)
 *          $
 */
public class SplitEdgeAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = -8806182123707992264L;

    /**
     * Creates a new action.
     * 
     */
    public SplitEdgeAction() {
        super(GraphEditingBundle.getString("menu.splitEdgeAction"),
                GraffitiSingleton.getInstance().getMainFrame());
        mainFrame.addSelectionListener(new SelectGraphElementActionListener(
                this));
    }

    /**
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Graph graph = mainFrame.getActiveEditorSession().getGraph();

        Selection selection = mainFrame.getActiveEditorSession()
                .getSelectionModel().getActiveSelection();

        List<Edge> edges = new LinkedList<Edge>(selection.getEdges());
        if (edges.isEmpty()) {
            edges.addAll(graph.getEdges());
        }

        graph.getListenerManager().transactionStarted(this);

        for (Edge edge : edges) {
            Node source = edge.getSource();
            Node target = edge.getTarget();

            if (source == target) {
                continue;
            }

            CoordinateAttribute sourceCoordAttr = (CoordinateAttribute) source
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            CoordinateAttribute targetCoordAttr = (CoordinateAttribute) target
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            Node newNode = graph.addNode();
            CoordinateAttribute newNodeCoordAttr = (CoordinateAttribute) newNode
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            double x = (sourceCoordAttr.getX() + targetCoordAttr.getX()) / 2.0;
            double y = (sourceCoordAttr.getY() + targetCoordAttr.getY()) / 2.0;
            newNodeCoordAttr.setCoordinate(new Point2D.Double(x, y));

            Edge newEdge1 = graph.addEdge(source, newNode, edge.isDirected());
            Edge newEdge2 = graph.addEdge(newNode, target, edge.isDirected());
            setEdgeArrows(newEdge1);
            setEdgeArrows(newEdge2);

            graph.deleteEdge(edge);
        }
        graph.getListenerManager().transactionFinished(this);

    }

    /*
     * sets the edge arrows to make them visible. @param edge hte edge to
     * handle.
     */
    private void setEdgeArrows(Edge edge) {
        if (edge.isDirected()) {
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            ega.setArrowhead(GraphicAttributeConstants.ARROWSHAPE_CLASSNAME);
        }
    }

    /**
     * @see org.graffiti.plugin.actions.SelectionAction#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        try {
            return !mainFrame.getActiveEditorSession().getGraph().getEdges()
                    .isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
