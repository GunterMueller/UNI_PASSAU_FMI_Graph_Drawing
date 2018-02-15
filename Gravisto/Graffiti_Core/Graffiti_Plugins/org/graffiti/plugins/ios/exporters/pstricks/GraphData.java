// =============================================================================
//
//   GraphData.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.pstricks;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.grid.GridAttribute;

/**
 * @author lessenic
 * @version $Revision$ $Date$
 */
public class GraphData implements GraphicElement {
    private Rectangle2D boundingBox;

    private List<EdgeData> edges = new LinkedList<EdgeData>();

    private GridData grid;

    private List<NodeData> nodes = new LinkedList<NodeData>();

    /**
     * Creates a new instance of GraphData
     * 
     * @param g
     *            graph
     */
    public GraphData(Graph g) {

        HashMap<Node, NodeData> map = new HashMap<Node, NodeData>();
        NodeData.resetNodeId();

        boundingBox = null;

        // calculate the bounding box

        for (Node node : g.getNodes()) {
            NodeData nodeData = new NodeData(node);
            nodes.add(nodeData);
            map.put(node, nodeData);
            boundingBox = boundingBox == null ? nodeData.getShape()
                    .getRealBounds2D() : boundingBox.createUnion(nodeData
                    .getShape().getRealBounds2D());
        }

        for (Edge edge : g.getEdges()) {
            EdgeData edgeData = new EdgeData(edge, map.get(edge.getSource()),
                    map.get(edge.getTarget()));
            edges.add(edgeData);
            boundingBox = boundingBox == null ? edgeData.getShape()
                    .getRealBounds2D() : boundingBox.createUnion(edgeData
                    .getShape().getRealBounds2D());
        }

        grid = new GridData(((GridAttribute) g.getAttribute("graphics.grid"))
                .getGrid(), boundingBox);
    }

    /*
     * @see
     * org.graffiti.plugins.ios.exporters.pstricks.GraphicElement#accept(org
     * .graffiti.plugins.ios.exporters.pstricks.ExportVisitor)
     */
    @Override
    public void accept(ExportVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the bounding box.
     * 
     * @return the bounding box.
     */
    public Rectangle2D getBoundingBox() {
        return boundingBox;
    }

    /**
     * Returns the edges.
     * 
     * @return the edges.
     */
    public List<EdgeData> getEdges() {
        return edges;
    }

    /**
     * Returns the grid.
     * 
     * @return the grid.
     */
    public GridData getGrid() {
        return grid;
    }

    /**
     * Returns the nodes.
     * 
     * @return the nodes.
     */
    public List<NodeData> getNodes() {
        return nodes;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
