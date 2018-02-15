// =============================================================================
//
//   OpenGLEngine.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.interactive.GraphElementFinder;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.EdgeChangeListener;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewGestureFeedbackProvider;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.FontManager;
import org.graffiti.plugins.views.fast.GraphChangeListener;
import org.graffiti.plugins.views.fast.GraphicsEngine;
import org.graffiti.plugins.views.fast.NodeChangeListener;
import org.graffiti.plugins.views.fast.OptimizationPolicy;
import org.graffiti.plugins.views.fast.opengl.buffer.BufferManager;
import org.graffiti.plugins.views.fast.opengl.buffer.RebuildBufferException;
import org.graffiti.plugins.views.fast.opengl.buffer.TriangleBuffer;
import org.graffiti.plugins.views.fast.opengl.dialog.ConfigurationPanel;
import org.graffiti.plugins.views.fast.opengl.label.OpenGLLabel;
import org.graffiti.plugins.views.fast.opengl.label.OpenGLLabelManager;
import org.graffiti.plugins.views.fast.opengl.label.commands.OpenGLLabelCommand;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class OpenGLEngine extends
        GraphicsEngine<OpenGLLabel, OpenGLLabelCommand> {
    private static final String ENGINE_NAME = FastViewPlugin
            .getString("fastview.engines.opengl.name");

    private Map<Node, AbstractNodeRep> nodes;
    private Map<Edge, AbstractEdgeRep> edges;
    private GLJPanel drawingPanel;
    private GLU glu;
    private BufferManager bufferManager;
    private TriangleBuffer nodeBuffer;
    private TriangleBuffer edgeBuffer;
    private ChangeProcessor changeProcessor;
    private Renderer renderer;
    private OpenGLGraphChangeListener graphChangeListener;
    private OpenGLNodeChangeListener nodeChangeListener;
    private OpenGLEdgeChangeListener edgeChangeListener;
    private OpenGLFontManager fontManager;
    private OpenGLGestureFeedbackProvider gestureFeedbackProvider;
    private OpenGLGraphElementFinder graphElementFinder;
    private OpenGLLabelManager labelManager;
    private OpenGLImageManager imageManager;

    public OpenGLEngine() {
        nodes = new HashMap<Node, AbstractNodeRep>();
        edges = new HashMap<Edge, AbstractEdgeRep>();
    }

    @Override
    protected void initialize(FastView fastView,
            OptimizationPolicy optimizationPolicy) {
        super.initialize(fastView, optimizationPolicy);

        GLCapabilities glCaps = new GLCapabilities();
        glCaps.setRedBits(8);
        glCaps.setGreenBits(8);
        glCaps.setBlueBits(8);
        glCaps.setAlphaBits(8);
        OpenGLConfiguration config = OpenGLConfiguration.get();
        int sampleBuffers = config.getSampleBuffers();
        glCaps.setSampleBuffers(sampleBuffers != 0);
        glCaps.setNumSamples(sampleBuffers);
        glCaps.setDepthBits(16);
        drawingPanel = new GLJPanel(glCaps);
        fontManager = new OpenGLFontManager();
        bufferManager = new BufferManager();
        nodeBuffer = bufferManager.createBuffer();
        edgeBuffer = bufferManager.createBuffer();

        changeProcessor = new ChangeProcessor(nodeBuffer, edgeBuffer, this,
                nodes);
        labelManager = new OpenGLLabelManager(this, nodes, edges);
        nodeChangeListener = new OpenGLNodeChangeListener(this, nodes,
                changeProcessor);
        edgeChangeListener = new OpenGLEdgeChangeListener(this, edges,
                changeProcessor);
        gestureFeedbackProvider = new OpenGLGestureFeedbackProvider(fastView,
                nodeChangeListener, edgeChangeListener);
        glu = new GLU();
        renderer = new Renderer(this, nodes, edges, bufferManager, nodeBuffer,
                edgeBuffer, glu, changeProcessor, labelManager, fontManager,
                gestureFeedbackProvider);
        changeProcessor.setRenderer(renderer);
        labelManager.setRenderer(renderer);
        gestureFeedbackProvider.setRenderer(renderer);
        drawingPanel.addGLEventListener(renderer);
        graphChangeListener = new OpenGLGraphChangeListener(this, nodes, edges,
                changeProcessor, optimizationPolicy);

        graphElementFinder = new OpenGLGraphElementFinder(nodes, edges, this);
        imageManager = new OpenGLImageManager(labelManager);
    }

    @Override
    public JComponent getDrawingComponent() {
        return drawingPanel;
    }

    @Override
    public String getName() {
        return ENGINE_NAME;
    }

    @Override
    public EdgeChangeListener<?> getEdgeChangeListener() {
        return edgeChangeListener;
    }

    @Override
    public FontManager<?> getFontManager() {
        return fontManager;
    }

    @Override
    public FastViewGestureFeedbackProvider getGestureFeedbackProvider() {
        return gestureFeedbackProvider;
    }

    @Override
    public GraphChangeListener<?> getGraphChangeListener() {
        return graphChangeListener;
    }

    @Override
    public GraphElementFinder getGraphElementFinder() {
        return graphElementFinder;
    }

    @Override
    public OpenGLImageManager getImageManager() {
        return imageManager;
    }

    @Override
    public OpenGLLabelManager getLabelManager() {
        return labelManager;
    }

    @Override
    public NodeChangeListener<?> getNodeChangeListener() {
        return nodeChangeListener;
    }

    @Override
    public NodeShape getShape(Node node) {
        return nodes.get(node).getShape();
    }

    @Override
    public EdgeShape getShape(Edge edge) {
        return edges.get(edge).getShape(this);
    }

    protected EdgeShape retrieveShape(Edge edge) {
        NodeShape sourceShape = nodes.get(edge.getSource()).getShape();
        NodeShape targetShape = nodes.get(edge.getTarget()).getShape();
        return AttributeUtil.getShape(edge, sourceShape, targetShape);
    }

    @Override
    public boolean knows(Node node) {
        return nodes.containsKey(node);
    }

    @Override
    public boolean knows(Edge edge) {
        return edges.containsKey(edge);
    }

    @Override
    public void onChangeOptimizationPolicy() {
    }

    @Override
    public void onChangeViewingMatrix() {
        renderer.setViewingMatrix(viewingMatrix);
    }

    @Override
    public JPanel createConfigurationPanel() {
        return new ConfigurationPanel();
    }

    protected void removeRep(AbstractOpenGLRep rep) {
        if (rep instanceof AbstractNodeRep) {
            nodes.remove(((AbstractNodeRep) rep).getNode());
        } else {
            edges.remove(((AbstractEdgeRep) rep).getEdge());
        }
    }

    // To call from Renderer
    protected void rebuildBuffer() {
        bufferManager.reset();
        for (AbstractNodeRep nodeRep : nodes.values()) {
            nodeRep.estimate(nodeBuffer, nodes, this);
        }
        for (AbstractEdgeRep edgeRep : edges.values()) {
            edgeRep.estimate(edgeBuffer, nodes, this);
        }
        bufferManager.finishEstimate();
        try {
            for (AbstractNodeRep nodeRep : nodes.values()) {
                nodeRep.process(nodeBuffer, nodes, this);
            }
            for (AbstractEdgeRep edgeRep : edges.values()) {
                edgeRep.process(edgeBuffer, nodes, this);
            }
        } catch (RebuildBufferException e2) {
            assert (false);
        }
    }

    @Override
    protected Color getBackgroundColor() {
        return renderer.getBackgroundColor();
    }

    @Override
    protected Color getGridColor() {
        return renderer.getGridColor();
    }

    @Override
    protected void setBackgroundColor(Color color) {
        renderer.setBackgroundColor(color);
    }

    @Override
    protected void setGridColor(Color color) {
        renderer.setGridColor(color);
    }

    protected void calculateBounds() {
        if (!calculatesBounds)
            return;
        Rectangle2D bounds = null;
        Iterator<AbstractNodeRep> nodeIterator = nodes.values().iterator();
        if (nodeIterator.hasNext()) {
            bounds = nodeIterator.next().getBounds();
        }
        while (nodeIterator.hasNext()) {
            bounds.add(nodeIterator.next().getBounds());
        }
        Iterator<AbstractEdgeRep> edgeIterator = edges.values().iterator();
        while (edgeIterator.hasNext()) {
            bounds.add(edgeIterator.next().getShape(this).getBounds2D());
        }
        fastView.getViewport().setLogicalBounds(
                bounds == null ? new Rectangle2D.Double() : bounds);
    }

    protected List<Shape> getGridShapes() {
        return fastView.getGrid().getShapes(logicalDisplayBounds);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
