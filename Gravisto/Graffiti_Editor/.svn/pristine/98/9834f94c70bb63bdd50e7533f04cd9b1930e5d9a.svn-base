// =============================================================================
//
//   ResizeNode.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.Sector;
import org.graffiti.plugins.modes.fast.UndoUtil;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("resizeNode")
public class ResizeNode extends FastViewAction {
    @InSlot
    public static final Slot<Point2D> deltaSlot = Slot.create("delta",
            Point2D.class);

    private Sector horizontalSector;
    private Sector verticalSector;
    private Node node;
    private boolean wasUsed;

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Point2D delta = in.get(deltaSlot);
        if (node == null
                || horizontalSector == Sector.IGNORE
                || horizontalSector == Sector.IGNORE
                || (horizontalSector == Sector.CENTER && verticalSector == Sector.CENTER))
            return;
        Point2D nodeSize = AttributeUtil.getDimension(node);
        Point2D nodePos = AttributeUtil.getPosition(node);
        double xSize = nodeSize.getX();
        double ySize = nodeSize.getY();
        double xPos = nodePos.getX();
        double yPos = nodePos.getY();
        if (horizontalSector == Sector.LOW) {
            double mc = Math.min(delta.getX(), xSize - 10);
            xSize -= mc;
            xPos += mc / 2.0;
        } else if (horizontalSector == Sector.HIGH) {
            double mc = Math.max(delta.getX(), 10 - xSize);
            xSize += mc;
            xPos += mc / 2.0;
        }
        if (verticalSector == Sector.LOW) {
            double mc = Math.min(delta.getY(), ySize - 10);
            ySize -= mc;
            yPos += mc / 2.0;
        } else if (verticalSector == Sector.HIGH) {
            double mc = Math.max(delta.getY(), 10 - ySize);
            ySize += mc;
            yPos += mc / 2.0;
        }
        if (!wasUsed) {
            UndoUtil undoUtil = new UndoUtil(session);
            undoUtil.preChange(node
                    .getAttribute(GraphicAttributeConstants.COORD_PATH));
            undoUtil.preChange(node
                    .getAttribute(GraphicAttributeConstants.DIM_PATH));
            undoUtil.close();
            wasUsed = true;
        }
        AttributeUtil.setPosition(node, new Point2D.Double(xPos, yPos));
        AttributeUtil.setDimension(node, xSize, ySize);
    }

    protected void setCurrentData(Sector horizontalSector,
            Sector verticalSector, Node node) {
        this.horizontalSector = horizontalSector;
        this.verticalSector = verticalSector;
        this.node = node;
        wasUsed = false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
