// =============================================================================
//
//   MouseHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.interactive.GraphElementFinder;
import org.graffiti.plugin.view.interactive.UserGestureListener;
import org.graffiti.plugins.modes.fast.Sector;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMouseDrag;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMouseMove;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMousePress;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMouseRelease;
import org.graffiti.plugins.views.fast.usergestures.MouseGestureFactory;
import org.graffiti.plugins.views.fast.usergestures.SectorUtil;
import org.graffiti.util.Callback;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MouseHandler extends MouseInputAdapter implements
        MouseWheelListener {
    private final double EDGE_TOLERANCE = 3.0;
    private final double BORDER_TOLERANCE = 5.0;
    private final double BEND_TOLERANCE = 2.0;
    private FastView fastView;
    private ScrollManager scrollManager;
    private GraphElementFinder elementFinder;
    private UserGestureListener dispatcher;
    private Point2D previousPosition;
    private Point2D previousRawPosition;
    private FastViewGestureFeedbackProvider provider;

    protected MouseHandler(FastView fastView) {
        this.fastView = fastView;
        scrollManager = fastView.getViewport();
        elementFinder = fastView.getGraphElementFinder();
        GraphicsEngine<?, ?> engine = fastView.getGraphicsEngine();
        JComponent drawingComponent = engine.getDrawingComponent();
        provider = engine.getGestureFeedbackProvider();
        drawingComponent.addMouseListener(this);
        drawingComponent.addMouseMotionListener(this);
        drawingComponent.addMouseWheelListener(this);
    }

    protected void setUserGestureDispatcher(UserGestureListener dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        previousPosition = null;
        previousRawPosition = null;
    }

    private void handleMouseEvent(final MouseEvent event,
            final MouseGestureFactory factory, final Point2D position) {
        if (previousPosition == null) {
            previousPosition = position;
            previousRawPosition = new Point2D.Double(event.getX(), event.getY());
        }
        if (dispatcher != null) {
            elementFinder.deferredTellElementAt(position, EDGE_TOLERANCE,
                    new Callback<Object, GraphElement>() {
                        public Object call(GraphElement element) {
                            boolean isOnBorder = element instanceof Node
                                    && elementFinder.isOnShapeBorder(position,
                                            (Node) element, BORDER_TOLERANCE);
                            String bend = element instanceof Edge ? elementFinder
                                    .getBend(position, (Edge) element,
                                            BEND_TOLERANCE)
                                    : "";
                            Pair<Sector, Sector> sectors;
                            if (isOnBorder) {
                                sectors = SectorUtil.getSectors(element,
                                        position);
                            } else {
                                sectors = Pair.create(Sector.IGNORE,
                                        Sector.IGNORE);
                            }
                            dispatcher.gesturePerformed(fastView, factory
                                    .createGesture(position, previousPosition,
                                            previousRawPosition, event,
                                            element, isOnBorder, bend, sectors
                                                    .getFirst(), sectors
                                                    .getSecond()));
                            return null;
                        }
                    });
        }
        previousPosition = position;
        previousRawPosition = new Point2D.Double(event.getX(), event.getY());
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        Point2D position = transformPosition(event);
        if (provider.onMouseMove(event, position))
            return;
        handleMouseEvent(event, ExtendedMouseMove.getFactory(), position);
    }

    @Override
    public void mousePressed(MouseEvent event) {
        Point2D position = transformPosition(event);
        if (provider.onMousePress(event, position))
            return;
        handleMouseEvent(event, ExtendedMousePress.getFactory(), position);
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        Point2D position = transformPosition(event);
        if (provider.onMouseDrag(event, position))
            return;
        handleMouseEvent(event, ExtendedMouseDrag.getFactory(), position);
    }

    /**
     * @{inheritdoc
     */
    @Override
    public void mouseReleased(MouseEvent event) {
        Point2D position = transformPosition(event);
        if (provider.onMouseRelease(event, position))
            return;
        handleMouseEvent(event, ExtendedMouseRelease.getFactory(), position);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // scrollManager.rotate(e.getWheelRotation() * Math.PI * 2.0 / 180);
    }

    private Point2D transformPosition(MouseEvent event) {
        return scrollManager.inverseTransform(new Point2D.Double(event.getX(),
                event.getY()));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
