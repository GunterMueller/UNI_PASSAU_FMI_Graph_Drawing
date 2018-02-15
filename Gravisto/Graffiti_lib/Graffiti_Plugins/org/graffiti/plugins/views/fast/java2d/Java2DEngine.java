// =============================================================================
//
//   Java2DEngine.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.interactive.GraphElementFinder;
import org.graffiti.plugins.views.fast.AbstractRepDepthComparator;
import org.graffiti.plugins.views.fast.EdgeChangeListener;
import org.graffiti.plugins.views.fast.FastFont;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewGestureFeedbackProvider;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.FontManager;
import org.graffiti.plugins.views.fast.GraphChangeListener;
import org.graffiti.plugins.views.fast.GraphicsEngine;
import org.graffiti.plugins.views.fast.NodeChangeListener;
import org.graffiti.plugins.views.fast.OptimizationPolicy;
import org.graffiti.plugins.views.fast.java2d.label.Java2DLabel;
import org.graffiti.plugins.views.fast.java2d.label.Java2DLabelManager;
import org.graffiti.plugins.views.fast.java2d.label.commands.Java2DLabelCommand;
import org.graffiti.plugins.views.fast.label.LabelManager;

/**
 * {@code GraphicsEngine} using Java2D to draw the graph.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DEngine extends
        GraphicsEngine<Java2DLabel, Java2DLabelCommand> {
    private AbstractRepDepthComparator<Java2DLabel, Java2DLabelCommand> abstractRepDepthComparator;
    private static final String ENGINE_NAME = FastViewPlugin
            .getString("fastview.engines.java2d.name");
    private JComponent drawingComponent;
    protected Map<Node, AbstractNodeRep> nodes;
    protected SortedSet<AbstractNodeRep> sortedNodes;
    protected DrawingSet drawingSet;
    protected Map<Edge, AbstractEdgeRep> edges;
    protected SortedSet<AbstractEdgeRep> sortedEdges;
    private Map<Edge, EdgeGraphicAttribute> pendingEdgeShapeChanges;
    private FontRenderContext fontRenderContext;
    private FontManager<FastFont> fontManager;
    private Java2DImageManager imageManager;

    private Java2DGraphChangeListener graphChangeListener;
    private Java2DNodeChangeListener nodeChangeListener;
    private Java2DEdgeChangeListener edgeChangeListener;
    protected Java2DLabelManager labelManager;
    protected Java2DGraphElementFinder graphElementFinder;
    protected Java2DGestureFeedbackProvider gestureFeedbackProvider;

    protected Color backgroundColor;
    protected Color gridColor;

    public Java2DEngine() {
        abstractRepDepthComparator = new AbstractRepDepthComparator<Java2DLabel, Java2DLabelCommand>();
        nodes = new HashMap<Node, AbstractNodeRep>();
        sortedNodes = new TreeSet<AbstractNodeRep>(abstractRepDepthComparator);
        edges = new HashMap<Edge, AbstractEdgeRep>();
        sortedEdges = new TreeSet<AbstractEdgeRep>(abstractRepDepthComparator);
        drawingSet = new DrawingSet();
        pendingEdgeShapeChanges = new HashMap<Edge, EdgeGraphicAttribute>();

        NodeRepFactory nodeRepFactory = null;
        if (optimizationPolicy == OptimizationPolicy.QUALITY_SPACE) {
            nodeRepFactory = new NodeRepFactory() {
                @Override
                public AbstractNodeRep create(Node node) {
                    return new SpaceNodeRep(node);
                }
            };
        } else {
            nodeRepFactory = new NodeRepFactory() {
                @Override
                public AbstractNodeRep create(Node node) {
                    return new SpeedNodeRep(node);
                }
            };
        }

        EdgeRepFactory edgeRepFactory = new EdgeRepFactory() {
            @Override
            public AbstractEdgeRep create(Edge edge) {
                return new SpeedEdgeRep(edge);
            }
        };

        graphChangeListener = new Java2DGraphChangeListener(this, nodes,
                sortedNodes, edges, sortedEdges, strokes, nodeRepFactory,
                edgeRepFactory);
        nodeChangeListener = new Java2DNodeChangeListener(this, nodes,
                sortedNodes);
        edgeChangeListener = new Java2DEdgeChangeListener(this, edges,
                sortedEdges, pendingEdgeShapeChanges, edgeRepFactory);
        fontManager = new FontManager<FastFont>() {
            @Override
            protected FastFont createFont(Font font) {
                return new FastFont(font);
            }
        };
        labelManager = new Java2DLabelManager(this, nodes, edges);
        imageManager = new Java2DImageManager(labelManager);
        labelManager.setImageManager(imageManager);
        graphElementFinder = new Java2DGraphElementFinder(nodes, edges);
        backgroundColor = FastViewPlugin.DEFAULT_BACKGROUND_COLOR;
        gridColor = FastViewPlugin.DEFAULT_GRID_COLOR;
    }

    /**
     * @{inheritDoc
     */
    @Override
    protected void initialize(FastView fastView,
            OptimizationPolicy optimizationPolicy) {
        super.initialize(fastView, optimizationPolicy);
        gestureFeedbackProvider = new Java2DGestureFeedbackProvider(fastView,
                nodeChangeListener, edgeChangeListener);
        drawingSet.optimizationPolicy = optimizationPolicy;
        drawingSet.defaultStroke = acquireStroke(1.0,
                FastViewPlugin.DEFAULT_DASH);
        drawingComponent = new JPanel(true) {
            /**
             * 
             */
            private static final long serialVersionUID = -44671002692393321L;

            @Override
            public void paint(Graphics g) {
                Graphics2D graphics = (Graphics2D) g;
                fontRenderContext = graphics.getFontRenderContext();
                Java2DEngine.this
                        .paint(graphics, getWidth(), getHeight(), true);
                g.dispose();
            }
        };
    }

    /**
     * @{inheritDoc
     */
    @Override
    public JComponent getDrawingComponent() {
        return drawingComponent;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String getName() {
        return ENGINE_NAME;
    }

    protected void paint(Graphics2D g, int width, int height, boolean isGui) {
        processPendingEdgeShapeChanges();
        labelManager.processChanges();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setBackground(backgroundColor);
        if (isGui) {
            g.clearRect(0, 0, width, height);
        }
        AffineTransform initialTransform = g.getTransform();
        AffineTransform transform = new AffineTransform(initialTransform);
        transform.concatenate(viewingMatrix);
        drawingSet.prepare(transform, g.getClip(), optimizationPolicy);
        Grid grid = fastView.getGrid();
        g.setTransform(transform);

        g.setColor(gridColor);
        Stroke oldStroke = g.getStroke();
        g.setStroke(FastViewPlugin.DEFAULT_GRID_STROKE);
        for (Shape shape : grid.getShapes(isGui ? logicalDisplayBounds
                : fastView.getViewport().getLogicalElementsBounds())) {
            g.draw(shape);
        }
        g.setStroke(oldStroke);
        for (AbstractEdgeRep edgeRep : sortedEdges) {
            edgeRep.draw(g, drawingSet);
        }
        gestureFeedbackProvider.drawDummyEdge(g, drawingSet);
        for (AbstractNodeRep nodeRep : sortedNodes) {
            nodeRep.draw(g, drawingSet);
        }
        gestureFeedbackProvider.drawDummyHub(g, drawingSet);
        gestureFeedbackProvider.drawSelectionRectangle(g, drawingSet);
        gestureFeedbackProvider.drawCompass(g, drawingSet, width, height,
                initialTransform);
        if (isGui) {
            g.dispose();
        }
        if (calculatesBounds) {
            fastView.getViewport().setLogicalBounds(drawingSet.getBounds());
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void onChangeViewingMatrix() {
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean knows(Node node) {
        return nodes.containsKey(node);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean knows(Edge edge) {
        return edges.containsKey(edge);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void onChangeOptimizationPolicy() {
        drawingSet.optimizationPolicy = optimizationPolicy;
        fastView.rebuild();
    }

    protected void processPendingEdgeShapeChanges() {
        for (Map.Entry<Edge, EdgeGraphicAttribute> entry : pendingEdgeShapeChanges
                .entrySet()) {
            Edge edge = entry.getKey();
            AbstractEdgeRep edgeRep = edges.get(edge);
            if (edgeRep == null) {
                continue;
            }
            AbstractNodeRep sourceRep = nodes.get(edge.getSource());
            if (sourceRep == null) {
                continue;
            }
            AbstractNodeRep targetRep = nodes.get(edge.getTarget());
            if (targetRep == null) {
                continue;
            }
            edges.get(edge).buildShape(entry.getValue(), sourceRep.getShape(),
                    targetRep.getShape());
        }
        pendingEdgeShapeChanges.clear();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public FontManager<?> getFontManager() {
        return fontManager;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Java2DImageManager getImageManager() {
        return imageManager;
    }

    protected FontRenderContext getFontRenderContext() {
        return fontRenderContext;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public NodeShape getShape(Node node) {
        AbstractNodeRep rep = nodes.get(node);
        if (rep == null)
            return null;
        return rep.getShape();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public EdgeShape getShape(Edge edge) {
        AbstractEdgeRep rep = edges.get(edge);
        if (rep == null)
            return null;
        return rep.getShape();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public LabelManager<Java2DLabel, Java2DLabelCommand> getLabelManager() {
        return labelManager;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public GraphChangeListener<?> getGraphChangeListener() {
        return graphChangeListener;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public NodeChangeListener<?> getNodeChangeListener() {
        return nodeChangeListener;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public EdgeChangeListener<?> getEdgeChangeListener() {
        return edgeChangeListener;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public GraphElementFinder getGraphElementFinder() {
        return graphElementFinder;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public FastViewGestureFeedbackProvider getGestureFeedbackProvider() {
        return gestureFeedbackProvider;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public JPanel createConfigurationPanel() {
        return new JPanel();
    }

    /**
     * @{inheritDoc
     */
    @Override
    protected Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @{inheritDoc
     */
    @Override
    protected Color getGridColor() {
        return gridColor;
    }

    /**
     * @{inheritDoc
     */
    @Override
    protected void setBackgroundColor(Color color) {
        backgroundColor = color;
    }

    /**
     * @{inheritDoc
     */
    @Override
    protected void setGridColor(Color color) {
        gridColor = color;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
