// =============================================================================
//
//   ChangeProcessor.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.graph.Node;
import org.graffiti.plugins.views.fast.opengl.buffer.RebuildBufferException;
import org.graffiti.plugins.views.fast.opengl.buffer.TriangleBuffer;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ChangeProcessor {
    private List<AbstractNodeRep> changedNodes;
    private List<AbstractEdgeRep> changedEdges;
    private TriangleBuffer nodeBuffer;
    private TriangleBuffer edgeBuffer;
    private OpenGLEngine engine;
    private Map<Node, AbstractNodeRep> nodes;
    private Renderer renderer;

    protected ChangeProcessor(TriangleBuffer nodeBuffer,
            TriangleBuffer edgeBuffer, OpenGLEngine engine,
            Map<Node, AbstractNodeRep> nodes) {
        this.nodeBuffer = nodeBuffer;
        this.edgeBuffer = edgeBuffer;
        this.engine = engine;
        this.nodes = nodes;
        changedNodes = new LinkedList<AbstractNodeRep>();
        changedEdges = new LinkedList<AbstractEdgeRep>();
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public void processChanges() {
        Object stamp = new Object();
        if (!changedNodes.isEmpty() || !changedEdges.isEmpty()) {
            renderer.raiseRedrawLevel(Renderer.REDRAW_LEVEL_REDRAW);
        }
        try {
            for (AbstractNodeRep nodeRep : changedNodes) {
                if (nodeRep.isUpdated(stamp)) {
                    continue;
                }
                nodeRep.process(nodeBuffer, nodes, engine);
            }
            for (AbstractEdgeRep edgeRep : changedEdges) {
                if (edgeRep.isUpdated(stamp)) {
                    continue;
                }
                edgeRep.process(edgeBuffer, nodes, engine);
            }
        } catch (RebuildBufferException e) {
            renderer.raiseRedrawLevel(Renderer.REDRAW_LEVEL_REBUILD);
        }
        changedNodes.clear();
        changedEdges.clear();
    }

    public void onShape(AbstractNodeRep nodeRep) {
        nodeRep.setShapeChangeFlag();
        changedNodes.add(nodeRep);
    }

    public void onTesselation(AbstractNodeRep nodeRep) {
        nodeRep.setTesselationChangeFlag();
        changedNodes.add(nodeRep);
    }

    public void onPosition(AbstractNodeRep nodeRep) {
        nodeRep.setPositionChangeFlag();
        changedNodes.add(nodeRep);
    }

    public void onDepth(AbstractNodeRep nodeRep, double depth) {
        nodeRep.setDepth(depth);
        nodeRep.setDepthChangeFlag();
        changedNodes.add(nodeRep);
    }

    public void onColor(AbstractNodeRep nodeRep) {
        nodeRep.setColorChangeFlag();
        changedNodes.add(nodeRep);
    }

    public void onSelection(AbstractNodeRep nodeRep, boolean isSelected) {
        nodeRep.setSelected(isSelected);
        changedNodes.add(nodeRep);
    }

    public void onHover(AbstractNodeRep nodeRep, boolean isHover) {
        nodeRep.setHover(isHover);
        changedNodes.add(nodeRep);
    }

    public void onDelete(AbstractNodeRep nodeRep) {
        if (nodeRep == null)
            return;
        nodeRep.free();
        changedNodes.add(nodeRep);
    }

    // Retrieve shape.
    public void onShape(AbstractEdgeRep edgeRep) {
        edgeRep.setShapeChangeFlag();
        changedEdges.add(edgeRep);
    }

    // Tesselate again.
    public void onTesselation(AbstractEdgeRep edgeRep) {
        edgeRep.setTesselationChangeFlag();
        changedEdges.add(edgeRep);
    }

    public void onDepth(AbstractEdgeRep edgeRep, double depth) {
        edgeRep.setDepth(depth);
        edgeRep.setDepthChangeFlag();
        changedEdges.add(edgeRep);
    }

    // Change color.
    public void onColor(AbstractEdgeRep edgeRep) {
        edgeRep.setColorChangeFlag();
        changedEdges.add(edgeRep);
    }

    public void onSelection(AbstractEdgeRep edgeRep, boolean isSelected) {
        edgeRep.setSelected(isSelected);
        changedEdges.add(edgeRep);
    }

    public void onHover(AbstractEdgeRep edgeRep, boolean isHover) {
        edgeRep.setHover(isHover);
        changedEdges.add(edgeRep);
    }

    public void onDelete(AbstractEdgeRep edgeRep) {
        if (edgeRep == null)
            return;
        edgeRep.free();
        changedEdges.add(edgeRep);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
