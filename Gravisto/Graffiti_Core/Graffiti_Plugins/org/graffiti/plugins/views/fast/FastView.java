// =============================================================================
//
//   FastView.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.BorderLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.EdgeEvent;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.TransactionEvent;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.GraphGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.NoGrid;
import org.graffiti.plugin.view.Viewport;
import org.graffiti.plugin.view.interactive.GraphElementFinder;
import org.graffiti.plugin.view.interactive.UserGestureListener;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.plugins.scripting.ConsoleComponent;
import org.graffiti.plugins.scripting.ConsoleOutput;
import org.graffiti.plugins.scripting.ScriptingRegistry;
import org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler;
import org.graffiti.plugins.views.fast.java2d.Java2DEngine;
import org.graffiti.plugins.views.fast.label.LabelManager;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;

/**
 * The default graphical view. The drawing is performed by the graphic engine
 * passed to the constructor. Diagram 1 overviews the structure of a {@code
 * FastView} employing the {@link Java2DEngine}. The view reacts to attribute
 * changes by calling the appropriate {@link AttributeHandler}, which on its
 * part calls a change listener of the graphic engine and causes a refresh.
 * Calling {@link #refresh()} frequently does not degrade the performance, as
 * the refresh is not directly executed but rather packed in an AWT event, which
 * may combine several updates.
 * 
 * <p>
 * <center> <img src="doc-files/FastView-1.png"></img><br />
 * <b>Diagram 1: FastView using the Java2D graphics engine.</b></center>
 * </p>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see GraphicsEngine
 */
public abstract class FastView extends ViewAdapter {
    /**
     * 
     */
    private static final long serialVersionUID = -4443492424245142285L;

    /**
     * The common view family of all {@code FastView}s.
     */
    public static final FastViewFamily FAST_VIEW_FAMILY = new FastViewFamily();

    /**
     * Format string for the name.
     */
    private static final String VIEW_NAME_PATTERN = FastViewPlugin
            .getString("fastview.namepattern");

    /**
     * The display quality.
     */
    private OptimizationPolicy optimizationPolicy;

    /**
     * The displayed graph.
     */
    private Graph graph;

    /**
     * The graph.
     */
    private final GraphicsEngine<?, ?> engine;

    /**
     * Listener of the graphic engine, which is called when nodes or edges are
     * added or removed.
     */
    private GraphChangeListener<?> graphChangeListener;

    /**
     * Listener of the graphic engine, which is called when the appearance of a
     * node shall change.
     */
    private NodeChangeListener<?> nodeChangeListener;

    /**
     * Listener of the graphic engine, which is called when the appearance of an
     * edge shall change.
     */
    private EdgeChangeListener<?> edgeChangeListener;

    /**
     * Manager of the graphic engine for labels.
     */
    private LabelManager<?, ?> labelManager;

    /**
     * Manager of the graphic engine for images.
     */
    private ImageManager<?, ?> imageManager;

    /**
     * Denotes if there currently is an AWT event scheduled to redraw the
     * display.
     */
    private boolean pendingRefresh;

    /**
     * Handles changes to the selection.
     */
    private SelectionHandler selectionHandler;

    /**
     * Handles changes to the currently visible area.
     */
    private ScrollManager scrollManager;

    /**
     * Reacts to key events by generating key user gestures.
     */
    private KeyHandler keyHandler;

    /**
     * Reacts to mouse events by generating mouse user gestures.
     */
    protected MouseHandler mouseHandler;

    /**
     * The receives the user gestures generated by this view.
     */
    private UserGestureListener dispatcher;

    /**
     * The session this view belongs to.
     */
    private EditorSession editorSession;

    /**
     * Panel containing the actual drawing component, the scrollbars and the
     * button for switching the console.
     */
    private JPanel scrollingPanel;

    /**
     * The console. Is {@code null} until the console is shown the first time.
     */
    private ConsoleComponent console;

    /**
     * The grid of the graph to display.
     */
    protected Grid grid;

    /**
     * When true, the view will zoom to fit the graph after the next redraw.
     */
    private boolean zoomToFitAfterRedraw = false;

    /**
     * Constructs a view using the specified graphic engine.
     * 
     * @param engine
     *            the graphic engine to employ.
     */
    public FastView(GraphicsEngine<?, ?> engine) {
        optimizationPolicy = GlobalFastViewOptions.get()
                .getDefaultOptimizationPolicy();
        this.engine = engine;
        engine.initialize(this, optimizationPolicy);
        graphChangeListener = engine.getGraphChangeListener();
        nodeChangeListener = engine.getNodeChangeListener();
        edgeChangeListener = engine.getEdgeChangeListener();
        labelManager = engine.getLabelManager();
        imageManager = engine.getImageManager();
        pendingRefresh = false;
        setLayout(new BorderLayout());
        setFocusable(true);
        scrollingPanel = new JPanel(new BorderLayout());
        add(scrollingPanel, BorderLayout.CENTER);
        JComponent drawingComponent = engine.getDrawingComponent();
        scrollManager = new ScrollManager(this, scrollingPanel,
                drawingComponent);
        scrollingPanel.add(drawingComponent, BorderLayout.CENTER);
        drawingComponent.setFocusable(false);
        selectionHandler = new SelectionHandler(this);
        keyHandler = new KeyHandler(this);
        mouseHandler = new MouseHandler(this);
        grid = new NoGrid();
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        selectionHandler.close();
    }

    /**
     * {@inheritDoc} This implementation returns this.
     */
    public JPanel getViewComponent() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public String getViewName() {
        return String.format(VIEW_NAME_PATTERN, engine.getName());
    }

    /**
     * {@inheritDoc}
     */
    public void setGraph(Graph graph) {
        if (graph != this.graph) {
            this.graph = graph;
            rebuild();
        }
    }

    /**
     * Makes to graphic engine to completely rebuild its representation of the
     * graph after the graph has been cleared or a new graph has been set.
     * 
     * @see #postGraphCleared(GraphEvent)
     * @see #setGraph(Graph)
     */
    public void rebuild() {
        graphChangeListener.onClear();
        AttributeHandler.triggerAll(graph, this);
        for (Node node : graph.getNodes()) {
            graphChangeListener.onAdd(node);
            AttributeHandler.triggerAll(node, this);
        }
        for (Edge edge : graph.getEdges()) {
            graphChangeListener.onAdd(edge);
            AttributeHandler.triggerAll(edge, this);
        }
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void postEdgeAdded(GraphEvent e) {
        Edge edge = e.getEdge();
        graphChangeListener.onAdd(edge);
        AttributeHandler.triggerAll(edge, this);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void postEdgeRemoved(GraphEvent e) {
        Edge edge = e.getEdge();
        graphChangeListener.onRemove(edge);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void postGraphCleared(GraphEvent e) {
        rebuild();
        setGrid(new NoGrid());
    }

    /**
     * {@inheritDoc}
     */
    public void postNodeAdded(GraphEvent e) {
        Node node = e.getNode();
        graphChangeListener.onAdd(node);
        AttributeHandler.triggerAll(node, this);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void postNodeRemoved(GraphEvent e) {
        Node node = e.getNode();
        graphChangeListener.onRemove(node);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void transactionFinished(TransactionEvent e) {
        boolean isCompleteGraphAffected = onTransactionFinished(e);
        if (isCompleteGraphAffected) {
            rebuild();
        } else {
            refresh();
        }
    }

    private boolean onTransactionFinished(TransactionEvent e) {
        Collection<Node> nodes = graph.getNodes();
        Collection<Edge> edges = graph.getEdges();

        for (Object obj : e.getChangedObjects()) {
            if (obj instanceof Node) {
                Node node = (Node) obj;
                if (nodes.contains(node)) {
                    if (!engine.knows(node)) {
                        graphChangeListener.onAdd(node);
                    }
                    AttributeHandler.triggerAll(node, this);
                }
            } else if (obj instanceof Edge) {
                Edge edge = (Edge) obj;
                if (!edges.contains(edge) && engine.knows(edge)) {
                    graphChangeListener.onRemove(edge);
                }
            } else if (obj instanceof Graph)
                return true;
        }
        for (Object obj : e.getChangedObjects()) {
            if (obj instanceof Edge) {
                Edge edge = (Edge) obj;
                if (edges.contains(edge)) {
                    if (!engine.knows(edge)) {
                        graphChangeListener.onAdd(edge);
                    }
                    AttributeHandler.triggerAll(edge, this);
                }
            } else if (obj instanceof Node) {
                Node node = (Node) obj;
                if (!nodes.contains(node) && engine.knows(node)) {
                    graphChangeListener.onRemove(node);
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc} This implementation does nothing.
     */
    public void transactionStarted(TransactionEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preSourceNodeChanged(EdgeEvent e) {
        updateShape(e.getEdge());
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void preTargetNodeChanged(EdgeEvent e) {
        updateShape(e.getEdge());
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void postAttributeAdded(AttributeEvent e) {
        // Bugfix while transactionsystem not working TODO:
        // Begin
        Attributable attributable = e.getAttribute().getAttributable();
        if (attributable instanceof Node) {
            if (!engine.knows((Node) attributable))
                return;
        } else if (attributable instanceof Edge) {
            if (!engine.knows((Edge) attributable))
                return;
        }
        // End
        AttributeHandler.onAdd(e.getAttribute(), this);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void postAttributeChanged(AttributeEvent e) {
        // Bugfix while transactionsystem not working TODO:
        // Begin
        Attributable attributable = e.getAttribute().getAttributable();
        if (attributable instanceof Node) {
            if (!engine.knows((Node) attributable))
                return;
        } else if (attributable instanceof Edge) {
            if (!engine.knows((Edge) attributable))
                return;
        }
        // End

        AttributeHandler.onChange(e.getAttribute(), this);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void postEdgeReversed(EdgeEvent e) {
        Edge edge = e.getEdge();
        edgeChangeListener.onReverse(edge);
        AttributeHandler.triggerAll(edge, this);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public void postAttributeRemoved(AttributeEvent e) {
        AttributeHandler.onDelete(e.getAttribute(), this);
        refresh();
    }

    /**
     * {@inheritDoc}
     */
    public CollectionAttribute getGraphAttribute() {
        return new GraphGraphicAttribute();
    }

    /**
     * {@inheritDoc}
     */
    public CollectionAttribute getNodeAttribute() {
        return new NodeGraphicAttribute();
    }

    /**
     * Returns the graphic engine used by this view.
     * 
     * @return the graphic engine used by this view.
     */
    public GraphicsEngine<?, ?> getGraphicsEngine() {
        return engine;
    }

    /**
     * Returns a listener of the graphic engine, which is called when nodes or
     * edges are added or removed.
     * 
     * @return a listener of the graphic engine, which is called when nodes or
     *         edges are added or removed.
     */
    public GraphChangeListener<?> getGraphChangeListener() {
        return graphChangeListener;
    }

    /**
     * Returns a listener of the graphic engine, which is called when the
     * appearance of a node shall change.
     * 
     * @return a listener of the graphic engine, which is called when the
     *         appearance of a node shall change.
     */
    public NodeChangeListener<?> getNodeChangeListener() {
        return nodeChangeListener;
    }

    /**
     * Returns a listener of the graphic engine, which is called when the
     * appearance of an edge shall change.
     * 
     * @return a listener of the graphic engine, which is called when the
     *         appearance of an edge shall change.
     */
    public EdgeChangeListener<?> getEdgeChangeListener() {
        return edgeChangeListener;
    }

    /**
     * Redraws the view. Calling {@code refresh()} frequently does not degrade
     * the performance, as the refresh is not directly executed but rather
     * packed in an AWT event, which may combine several updates.
     */
    void refresh() {
        if (!pendingRefresh) {
            pendingRefresh = true;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    pendingRefresh = false;
                    engine.getDrawingComponent().repaint();
                    if (zoomToFitAfterRedraw) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                zoomToFitAfterRedraw = false;
                                zoomToFit();
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * Returns the display quality.
     * 
     * @return the display quality.
     */
    public OptimizationPolicy getOptimizationPolicy() {
        return optimizationPolicy;
    }

    /**
     * Is called when the shape of the specified node has changed.
     * 
     * @param node
     *            the node whose shape has changed.
     * @param attribute
     *            the graphic attribute of the node.
     */
    public void updateShape(Node node, NodeGraphicAttribute attribute) {
        if (engine.getShape(node) == null) {
            AttributeHandler.onChange(node
                    .getAttribute(GraphicAttributeConstants.SHAPE_PATH), this);
        }
        nodeChangeListener.onChangeShape(node, attribute);
        AttributeHandler.triggerAll(node, this, NodeLabelAttribute.class);
        updateIncidentEdgeShapes(node);
    }

    /**
     * Is called when the shape of the specified edge has changed.
     * 
     * @param edge
     *            the edge whose shape has changed.
     * @param attribute
     *            the graphic attribute of the edge.
     */
    public void updateShape(Edge edge, EdgeGraphicAttribute attribute) {
        if (engine.getShape(edge) == null) {
            AttributeHandler.onChange(edge
                    .getAttribute(GraphicAttributeConstants.SHAPE_PATH), this);
        }
        edgeChangeListener.onChangeShape(edge, attribute);
        AttributeHandler.triggerAll(edge, this, EdgeLabelAttribute.class);
    }

    /**
     * Updates the shape of the specified edge.
     * 
     * @param edge
     *            the edge whose shape has changed.
     */
    private void updateShape(Edge edge) {
        if (engine.knows(edge)) {
            updateShape(edge, (EdgeGraphicAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS));
        }
    }

    /**
     * Updates the shapes of all edges incident to the specified node.
     * 
     * @param node
     *            the node, the shapes of the edges of which have changed.
     */
    public void updateIncidentEdgeShapes(Node node) {
        for (Edge edge : node.getEdges()) {
            updateShape(edge);
        }
    }

    /**
     * Returns a manager of the graphic engine for labels.
     * 
     * @return a manager of the graphic engine for labels.
     */
    public LabelManager<?, ?> getLabelManager() {
        return labelManager;
    }

    /**
     * Returns a manager of the graphic engine for images.
     * 
     * @return a manager of the graphic engine for images.
     */
    public ImageManager<?, ?> getImageManager() {
        return imageManager;
    }

    /**
     * {@inheritDoc} This implementation returns {@code false}, as this view
     * provides scroll bars.
     * 
     * @see ScrollManager
     */
    public boolean embedsInJScrollPane() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectionModel(SelectionModel selectionModel) {
        selectionHandler.setSelectionModel(selectionModel);
    }

    /**
     * {@inheritDoc}
     */
    public void setEditorSession(EditorSession editorSession) {
        this.editorSession = editorSession;
    }

    /**
     * {@inheritDoc}
     */
    public EditorSession getEditorSession() {
        return editorSession;
    }

    /**
     * Shows or hides the console.
     * 
     * @param visible
     *            denotes if the console shall be visible.
     */
    protected void setConsoleVisible(boolean visible) {
        if (console == null) {
            if (visible) {
                createConsole();
            }
        } else {
            console.setVisible(visible);
        }
    }

    /**
     * Returns if the console is currently visible.
     * 
     * @return {@code true}, if the console is currently visible.
     */
    protected boolean isConsoleVisible() {
        return console != null && console.isVisible();
    }

    /**
     * Creates the console.
     */
    private void createConsole() {
        console = new ConsoleComponent(ScriptingRegistry.get().getViewScope(
                this), GlobalFastViewOptions.get().getDefaultConsoleLanguage());

        add(console, BorderLayout.SOUTH);
        validate();
    }

    /**
     * {@inheritDoc}
     */
    public void setUserGestureDispatcher(UserGestureListener dispatcher) {
        this.dispatcher = dispatcher;
        keyHandler.setUserGestureDispatcher(dispatcher);
        mouseHandler.setUserGestureDispatcher(dispatcher);
        engine.getGestureFeedbackProvider()
                .setUserGestureDispatcher(dispatcher);
    }

    /**
     * {@inheritDoc}
     */
    public UserGestureListener getUserGestureDispatcher() {
        return dispatcher;
    }

    /**
     * Returns the graph element finder of the graphic engine. It is used to
     * determine the graph element located at a specific point or area.
     * 
     * @return the graph element finder of the graphic engine.
     */
    public GraphElementFinder getGraphElementFinder() {
        return engine.getGraphElementFinder();
    }

    /**
     * {@inheritDoc}
     */
    public FastViewGestureFeedbackProvider getGestureFeedbackProvider() {
        return engine.getGestureFeedbackProvider();
    }

    /**
     * Outputs the specified string in the console.
     * 
     * @param string
     *            the string to print.
     * @param kind
     *            the output type.
     * @param ignoreVisibility
     *            denotes if the string must be printed even if the console is
     *            currently not shown. Note that the string will not be printed
     *            anyways if the console has not been created yet.
     */
    protected void printToConsole(String string, ConsoleOutput kind,
            boolean ignoreVisibility) {
        if (console != null && (console.isVisible() || ignoreVisibility)) {
            console.print(string, kind);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Grid getGrid() {
        return grid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGrid(Grid grid) {
        this.grid = grid;
        refresh();
    }

    /**
     * {@inheritDoc} This implementation returns {@code true}.
     */
    @Override
    public boolean supportsGrid() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public AffineTransform getZoomTransform() {
        return scrollManager.getZoomTransform();
    }

    /**
     * {@inheritDoc}
     */
    public double getZoom() {
        return scrollManager.getZoom();
    }

    /**
     * {@inheritDoc}
     */
    public void setZoom(double factor) {
        scrollManager.setZoom(factor);
    }

    /**
     * {@inheritDoc}
     */
    public ScrollManager getViewport() {
        return scrollManager;
    }

    /**
     * {@inheritDoc} This implementation returns {@link #FAST_VIEW_FAMILY}.
     */
    public ViewFamily<FastView> getFamily() {
        return FAST_VIEW_FAMILY;
    }

    public void zoomToFitAfterRedraw() {
        zoomToFitAfterRedraw = true;
    }

    private void zoomToFit() {
        Viewport currentViewport = scrollManager;
        if (currentViewport == null)
            return;
        Rectangle2D logicalElementBounds = currentViewport
                .getLogicalElementsBounds();
        Rectangle2D rotatedElementBounds = MathUtil.getTransformedBounds(
                logicalElementBounds, AffineTransform
                        .getRotateInstance(currentViewport.getRotation()));
        Rectangle2D physicalDisplayBounds = currentViewport.getDisplayBounds();
        double zoom = Math.min((physicalDisplayBounds.getWidth() - 2 * 50)
                / rotatedElementBounds.getWidth(), (physicalDisplayBounds
                .getHeight() - 2 * 50)
                / rotatedElementBounds.getHeight());
        zoom = Math.min(zoom, 1.0);
        currentViewport.setZoom(zoom);
        Rectangle2D zrElementBounds = MathUtil.getTransformedBounds(
                logicalElementBounds, currentViewport
                        .getZoomRotationTransform());
        Point2D pan = new Point2D.Double(-zrElementBounds.getCenterX()
                + physicalDisplayBounds.getCenterX(), -zrElementBounds
                .getCenterY()
                + physicalDisplayBounds.getCenterY());
        currentViewport.setTranslation(pan);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
