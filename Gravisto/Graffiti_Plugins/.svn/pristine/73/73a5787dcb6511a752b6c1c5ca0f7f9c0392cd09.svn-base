// =============================================================================
//
//   AddBend.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.event.AttributeEvent;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.UndoUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewGestureFeedbackProvider;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("addBend")
public class AddBend extends FastViewAction {
    private static final Pattern BEND_PATTERN = Pattern.compile("\\Q"
            + GraphicAttributeConstants.BEND + "\\E(\\d+)");

    @InSlot
    public static final Slot<Set<Edge>> edgesSlot = Slot.createSetSlot("edges",
            Edge.class);

    @InSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        FastViewGestureFeedbackProvider gfp = view.getGestureFeedbackProvider();
        Set<Edge> edges = in.get(edgesSlot);
        Point2D position = gfp.snapBend(in.get(positionSlot));
        graph.getListenerManager().transactionStarted(this);
        UndoUtil undoUtil = new UndoUtil(session);
        for (Edge edge : edges) {
            SortedCollectionAttribute sca = (SortedCollectionAttribute) edge
                    .getAttribute(GraphicAttributeConstants.BENDS_PATH);
            undoUtil.preChange(sca);
            int maxIndex = -1;
            for (String bendStr : sca.getCollection().keySet()) {
                Matcher matcher = BEND_PATTERN.matcher(bendStr);
                if (matcher.matches()) {
                    maxIndex = Math.max(Integer.valueOf(matcher.group(1)),
                            maxIndex);
                }
            }
            maxIndex++;
            CoordinateAttribute ca = new CoordinateAttribute(
                    GraphicAttributeConstants.BEND + maxIndex, position);
            sca.add(ca);
            AttributeEvent event = new AttributeEvent(ca.getAttribute("x"));
            graph.getListenerManager().postAttributeAdded(event);
            StringAttribute sa = (StringAttribute) edge
                    .getAttribute(GraphicAttributeConstants.SHAPE_PATH);
            if (sa.getString().equals(
                    GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME)) {
                undoUtil.preChange(sa);
                sa.setString(GraphicAttributeConstants.POLYLINE_CLASSNAME);
            }
        }
        undoUtil.close();
        graph.getListenerManager().transactionFinished(this);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
