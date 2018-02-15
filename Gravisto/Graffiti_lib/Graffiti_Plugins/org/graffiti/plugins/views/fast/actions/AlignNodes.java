// =============================================================================
//
//   AlignNodes.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;
import java.util.Set;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.UndoUtil;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("alignNodes")
public class AlignNodes extends FastViewAction {
    @InSlot
    public static final Slot<Node> anchorSlot = Slot.create("anchor",
            Node.class);

    @InSlot
    public static final Slot<Set<Node>> nodesSlot = Slot.createSetSlot("nodes",
            Node.class);

    @InSlot
    public static final Slot<String> alignSlot = Slot.create("align",
            String.class);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        UndoUtil undoUtil = new UndoUtil(session);
        Node anchor = in.get(anchorSlot);
        Set<Node> nodes = in.get(nodesSlot);
        String align = in.get(alignSlot);
        if (align.length() != 2)
            return;
        Point2D aPos = AttributeUtil.getPosition(anchor);
        Point2D aSize = AttributeUtil.getDimension(anchor);
        for (Node node : nodes) {
            Point2D nPos = AttributeUtil.getPosition(node);
            Point2D nSize = AttributeUtil.getDimension(node);
            double x = nPos.getX();
            switch (align.charAt(0)) {
            case 'l':
                x = aPos.getX() + (nSize.getX() - aSize.getX()) / 2.0;
                break;
            case 'c':
                x = aPos.getX();
                break;
            case 'h':
                x = aPos.getX() + (aSize.getX() - nSize.getX()) / 2.0;
            }
            double y = nPos.getY();
            switch (align.charAt(1)) {
            case 'l':
                y = aPos.getY() + (nSize.getY() - aSize.getY()) / 2.0;
                break;
            case 'c':
                y = aPos.getY();
                break;
            case 'h':
                y = aPos.getY() + (aSize.getY() - nSize.getY()) / 2.0;
            }
            AttributeUtil.setPosition(node, new Point2D.Double(x, y), undoUtil);
        }
        undoUtil.close();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
