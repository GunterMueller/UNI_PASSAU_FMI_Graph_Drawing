// =============================================================================
//
//   EdgeData.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.pstricks;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;

/**
 * @author lessenic
 * @version $Revision$ $Date$
 */
public class EdgeData extends GraphElementData {

    private NodeData sourceNode;

    private NodeData targetNode;

    private ArrowData headArrow;

    private ArrowData tailArrow;

    /**
     * Creates a new instance of EdgeData.
     * 
     * @param edge
     */
    public EdgeData(Edge edge, NodeData sourceNode, NodeData targetNode) {
        super(edge);

        this.sourceNode = sourceNode;

        this.targetNode = targetNode;

        try {
            ((EdgeShape) shape).buildShape(
                    (EdgeGraphicAttribute) graphElementGraphicAttribute,
                    (NodeShape) sourceNode.getShape(), (NodeShape) targetNode
                            .getShape());
            headArrow = new ArrowData(((EdgeShape) shape).getHeadArrow(), this);
            tailArrow = new ArrowData(((EdgeShape) shape).getTailArrow(), this);
        } catch (ShapeNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * @see
     * org.graffiti.plugins.ios.exporters.pstricks.GraphicElement#accept(org
     * .graffiti.plugins.ios.exporters.pstricks.ExportVisitor)
     */
    @Override
    public void accept(ExportVisitor visitor) {
        tailArrow.accept(visitor);
        visitor.visit(this);
        headArrow.accept(visitor);
    }

    /**
     * Returns the sourceNode.
     * 
     * @return the sourceNode.
     */
    public NodeData getSourceNode() {
        return sourceNode;
    }

    /**
     * Returns the targetNode.
     * 
     * @return the targetNode.
     */
    public NodeData getTargetNode() {
        return targetNode;
    }

    /*
     * @see org.graffiti.plugins.ios.exporters.pstricks.GraphElementData#getZ()
     */
    @Override
    public double getZ() {
        return ((EdgeGraphicAttribute) graphElementGraphicAttribute).getDepth();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
