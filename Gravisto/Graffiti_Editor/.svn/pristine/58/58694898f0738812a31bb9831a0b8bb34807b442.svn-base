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
@ActionId("startZoomRotation")
public class StartZoomRotation extends FastViewAction {
    @InSlot
    public static final Slot<Point2D> center = Slot.create("center",
            Point2D.class);

    @InSlot
    public static final Slot<Point2D> rawCenter = Slot.create("rawCenter",
            Point2D.class);

    private ZoomRotation zoomRotationAction;

    public StartZoomRotation(ZoomRotation zoomRotationAction) {
        this.zoomRotationAction = zoomRotationAction;
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        ScrollManager scrollManager = view.getViewport();
        zoomRotationAction.setCurrentData(in.get(center), in.get(rawCenter),
                scrollManager.getRotation(), scrollManager.getZoom());
    }
}
