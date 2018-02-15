// =============================================================================
//
//   FastViewGestureFeedbackProvider.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.interactive.MouseCursorProvider;
import org.graffiti.plugin.view.interactive.PopupMenuCompatibleProvider;
import org.graffiti.plugin.view.interactive.PopupMenuSelectionGesture;
import org.graffiti.plugin.view.interactive.TranslationProvider;
import org.graffiti.plugin.view.interactive.UserGestureListener;
import org.graffiti.plugins.scripting.ConsoleOutput;
import org.graffiti.plugins.scripting.ConsoleProvider;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class FastViewGestureFeedbackProvider implements
        ConsoleProvider, TranslationProvider, PopupMenuCompatibleProvider,
        MouseCursorProvider {
    public static final int HUB_SIZE = 23;
    public static final int COMPASS_CIRCLE_SIZE = 5;
    public static final Stroke COMPASS_STROKE = new BasicStroke(2.0f);
    public static final GeneralPath COMPASS;
    static {
        float line = 50.0f;
        float arrow = 10.0f;
        COMPASS = new GeneralPath();
        COMPASS.moveTo(line - arrow, -arrow);
        COMPASS.lineTo(line, 0.0f);
        COMPASS.moveTo(line - arrow, arrow);
        COMPASS.lineTo(line, 0.0f);
        COMPASS.lineTo(0.0f, 0.0f);
        COMPASS.lineTo(0.0f, line);
        COMPASS.lineTo(arrow, line - arrow);
        COMPASS.moveTo(0.0f, line);
        COMPASS.lineTo(-arrow, line - arrow);
    }

    protected FastView fastView;
    private GraphElement hoveredElement;
    private NodeChangeListener<?> nodeChangeListener;
    private EdgeChangeListener<?> edgeChangeListener;
    private UserGestureListener dispatcher;
    protected Point2D hubPosition;
    protected Point2D compassPosition;
    protected Point2D lastMousePosition;
    private boolean isDraggingHub;
    protected boolean isHoveringHub;
    private Cursor currentCursor;
    protected Rectangle2D selectionRectangle;
    protected LinkedList<Point2D> dummyEdgePoints;
    protected boolean isDummyEdgeHovered;

    protected FastViewGestureFeedbackProvider(FastView fastView,
            NodeChangeListener<?> nodeChangeListener,
            EdgeChangeListener<?> edgeChangeListener) {
        this.fastView = fastView;
        this.nodeChangeListener = nodeChangeListener;
        this.edgeChangeListener = edgeChangeListener;
        lastMousePosition = new Point2D.Double();
    }

    public void print(String string, ConsoleOutput kind) {
        fastView.printToConsole(string, kind, false);
    }

    public void setHoveredElement(GraphElement element) {
        if (hoveredElement == element)
            return;
        if (hoveredElement != null) {
            if (hoveredElement instanceof Node) {
                nodeChangeListener.onChangeHover((Node) hoveredElement, false);
            } else if (hoveredElement instanceof Edge) {
                edgeChangeListener.onChangeHover((Edge) hoveredElement, false);
            }
        }
        hoveredElement = element;
        if (hoveredElement != null) {
            if (hoveredElement instanceof Node) {
                nodeChangeListener.onChangeHover((Node) hoveredElement, true);
            } else if (hoveredElement instanceof Edge) {
                edgeChangeListener.onChangeHover((Edge) hoveredElement, true);
            }
        }
        refreshView();
    }

    protected void refreshView() {
        fastView.refresh();
    }

    public void reset() {
        doReset();
        selectionRectangle = null;
        dummyEdgePoints = null;
        if (hoveredElement != null) {
            if (hoveredElement instanceof Node) {
                Node hoveredNode = (Node) hoveredElement;
                if (fastView.getGraphicsEngine().knows(hoveredNode)) {
                    nodeChangeListener.onChangeHover(hoveredNode, false);
                }
            } else if (hoveredElement instanceof Edge) {
                Edge hoveredEdge = (Edge) hoveredElement;
                if (fastView.getGraphicsEngine().knows(hoveredEdge)) {
                    edgeChangeListener.onChangeHover(hoveredEdge, false);
                }
            }
            hoveredElement = null;
        }
        hubPosition = null;
        isDraggingHub = false;
        isHoveringHub = false;
        compassPosition = null;
        if (currentCursor != null) {
            fastView.getGraphicsEngine().getDrawingComponent().setCursor(
                    Cursor.getDefaultCursor());
            currentCursor = null;
        }
        refreshView();
    }

    public String translate(String id) {
        return FastViewPlugin.getString(id);
    }

    protected void setUserGestureDispatcher(UserGestureListener dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void relayMenuSelectionGesture(PopupMenuSelectionGesture pmsg) {
        if (dispatcher != null) {
            dispatcher.gesturePerformed(fastView, pmsg);
        }
    }

    public void setHub(boolean isEnabled) {
        if (isEnabled) {
            GraphicsEngine<?, ?> engine = fastView.getGraphicsEngine();
            JComponent component = engine.getDrawingComponent();
            Rectangle rectangle = component.getBounds();
            hubPosition = engine.inverseTransform(new Point2D.Double(rectangle
                    .getCenterX(), rectangle.getCenterY()));
        } else {
            hubPosition = null;
        }
    }

    public void setCompass(Point2D compassPosition) {
        this.compassPosition = compassPosition;
    }

    public Point2D getHubPosition() {
        return hubPosition;
    }

    public boolean onMousePress(MouseEvent e, Point2D position) {
        if (hubPosition != null) {
            if (isMouseOverHub(position)) {
                isDraggingHub = true;
                lastMousePosition = position;
                return true;
            }
        }
        return false;
    }

    public boolean onMouseRelease(MouseEvent e, Point2D position) {
        if (isDraggingHub && e.getButton() == MouseEvent.BUTTON1) {
            isDraggingHub = false;
            return true;
        }
        return false;
    }

    public boolean onMouseDrag(MouseEvent e, Point2D position) {
        if (isDraggingHub && hubPosition != null) {
            double dx = position.getX() - lastMousePosition.getX();
            double dy = position.getY() - lastMousePosition.getY();
            hubPosition.setLocation(hubPosition.getX() + dx, hubPosition.getY()
                    + dy);
            lastMousePosition = position;
            refreshView();
            return true;
        }
        return false;
    }

    public boolean onMouseMove(MouseEvent e, Point2D position) {
        if (hubPosition != null) {
            boolean hovering = isMouseOverHub(position);
            if (isHoveringHub != hovering) {
                isHoveringHub = hovering;
                refreshView();
            }
        }
        return false;
    }

    private boolean isMouseOverHub(Point2D mousePosition) {
        return hubPosition.distanceSq(mousePosition) < (HUB_SIZE * HUB_SIZE) >> 2;
    }

    public void setCursor(Cursor cursor) {
        if (cursor != currentCursor) {
            currentCursor = cursor;
            fastView.getGraphicsEngine().getDrawingComponent()
                    .setCursor(cursor);
        }
    }

    protected void doReset() {
    };

    public final Point2D snapNode(Point2D position) {
        return fastView.getGrid().snapNode(position);
    }

    public final Point2D snapBend(Point2D position) {
        return fastView.getGrid().snapBend(position);
    }

    public void show(JPopupMenu menu, double x, double y) {
        GraphicsEngine<?, ?> engine = fastView.getGraphicsEngine();
        Point2D p = engine.transform(new Point2D.Double(x, y));
        menu.show(engine.getDrawingComponent(), (int) p.getX(), (int) p.getY());
    }

    public void setSelectionRectangle(Rectangle2D rectangle) {
        this.selectionRectangle = rectangle;
        refreshView();
    }

    public Rectangle2D getSelectionRectangle() {
        return selectionRectangle;
    }

    public void setDummyEdgePoints(LinkedList<Point2D> points, boolean isHover) {
        dummyEdgePoints = points;
        isDummyEdgeHovered = isHover;
        refreshView();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
