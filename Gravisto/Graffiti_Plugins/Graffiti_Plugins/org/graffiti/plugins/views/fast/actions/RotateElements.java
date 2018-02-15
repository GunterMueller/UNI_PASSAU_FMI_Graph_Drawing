// =============================================================================
//
//   RotateElements.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Set;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("rotateElements")
public class RotateElements extends FastViewAction {
    @InSlot
    public static final Slot<Set<GraphElement>> elementsSlot = Slot
            .createSetSlot("elements", GraphElement.class);

    @InSlot
    public static final Slot<Point2D> fromSlot = Slot.create("from",
            Point2D.class);

    @InSlot
    public static final Slot<Point2D> toSlot = Slot.create("to", Point2D.class);

    @InSlot
    public static final Slot<Point2D> originSlot = Slot.create("origin",
            Point2D.class);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Point2D from = in.get(fromSlot);
        Point2D to = in.get(toSlot);
        Point2D origin = in.get(originSlot);
        Set<GraphElement> set = in.get(elementsSlot);
        if (from.equals(to) || from.equals(origin) || to.equals(origin))
            return;
        double thetaFrom = Math.atan2(from.getY() - origin.getY(), from.getX()
                - origin.getX());
        double thetaTo = Math.atan2(to.getY() - origin.getY(), to.getX()
                - origin.getX());
        AffineTransform transform = AffineTransform.getRotateInstance(thetaTo
                - thetaFrom, origin.getX(), origin.getY());
        graph.getListenerManager().transactionStarted(this);
        for (GraphElement element : set) {
            if (element instanceof Node) {
                Node node = (Node) element;
                Point2D position = AttributeUtil.getPosition(node);
                transform.transform(position, position);
                AttributeUtil.setPosition(node, position);
            } else if (element instanceof Edge) {
                Edge edge = (Edge) element;
                CollectionAttribute attribute = (CollectionAttribute) edge
                        .getAttribute(GraphicAttributeConstants.BENDS_PATH);
                for (Attribute a : attribute.getCollection().values()) {
                    if (a instanceof CoordinateAttribute) {
                        CoordinateAttribute ca = (CoordinateAttribute) a;
                        Point2D position = ca.getCoordinate();
                        transform.transform(position, position);
                        ca.setCoordinate(position);
                    }
                }
            }
        }
        graph.getListenerManager().transactionFinished(this);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
