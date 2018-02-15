// =============================================================================
//
//   OpenGLGraphChangeListener.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.views.fast.GraphChangeListener;
import org.graffiti.plugins.views.fast.OptimizationPolicy;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLGraphChangeListener extends
        GraphChangeListener<OpenGLEngine> {
    private Map<Node, AbstractNodeRep> nodes;
    private Map<Edge, AbstractEdgeRep> edges;
    private ChangeProcessor changeProcessor;

    // private OptimizationPolicy optimizationPolicy;

    protected OpenGLGraphChangeListener(OpenGLEngine engine,
            Map<Node, AbstractNodeRep> nodes, Map<Edge, AbstractEdgeRep> edges,
            ChangeProcessor changeProcessor,
            OptimizationPolicy optimizationPolicy) {
        super(engine);
        this.nodes = nodes;
        this.edges = edges;
        this.changeProcessor = changeProcessor;
        // this.optimizationPolicy = optimizationPolicy;
    }

    @Override
    public void onAdd(Node node) {
        AbstractNodeRep nodeRep = new QualityNodeRep(node);
        nodes.put(node, nodeRep);
        changeProcessor.onShape(nodeRep);
    }

    @Override
    public void onAdd(Edge edge) {
        AbstractEdgeRep edgeRep = new QualityEdgeRep(edge);
        edges.put(edge, edgeRep);
        changeProcessor.onShape(edgeRep);
    }

    @Override
    public void onClear() {
        // TODO:
    }

    @Override
    public void onRemove(Node node) {
        changeProcessor.onDelete(nodes.get(node));
    }

    @Override
    public void onRemove(Edge edge) {
        changeProcessor.onDelete(edges.get(edge));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
