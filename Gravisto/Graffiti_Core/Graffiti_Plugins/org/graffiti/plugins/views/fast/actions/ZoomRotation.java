package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.ScrollManager;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("zoomRotation")
public class ZoomRotation extends FastViewAction {
    private static final double DEAD_ZONE = 30.0;

    @InSlot
    public static final Slot<Point2D> rawPositionSlot = Slot.create(
            "rawPosition", Point2D.class);

    @InSlot
    public static final Slot<Boolean> snapSlot = Slot.create("snap",
            Boolean.class);

    private Point2D center;
    private Point2D rawCenter;
    private double initialRotation;
    private double initialZoom;

    protected void setCurrentData(Point2D center, Point2D rawCenter,
            double initialRotation, double initialZoom) {
        this.center = center;
        this.rawCenter = rawCenter;
        this.initialRotation = initialRotation;
        this.initialZoom = initialZoom;
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Point2D rawPosition = in.get(rawPositionSlot);
        boolean snap = in.get(snapSlot);
        double deltaX = rawPosition.getX() - rawCenter.getX();
        double deltaY = rawPosition.getY() - rawCenter.getY();
        ScrollManager scrollManager = view.getViewport();
        if (Math.abs(deltaX) < DEAD_ZONE) {
            deltaX = 0;
        } else {
            deltaX -= Math.signum(deltaX) * DEAD_ZONE;
        }
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            deltaX -= Math.signum(deltaX) * DEAD_ZONE;
            scrollManager.setRotation(initialRotation + deltaX / (2.5 * 180.0)
                    * Math.PI, center, snap);
            scrollManager.setZoom(initialZoom, center, false);
        } else {
            scrollManager.setZoom(initialZoom * Math.exp(deltaY / 200.0),
                    center, snap);
            scrollManager.setRotation(initialRotation, center, false);
        }
    }
}
