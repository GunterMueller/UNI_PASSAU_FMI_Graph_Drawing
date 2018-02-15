// =============================================================================
//
//   NodeData.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.pstricks;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;

/**
 * @author lessenic
 * @version $Revision$ $Date$
 */
public class NodeData extends GraphElementData {

    private static int currentNodeId = 0;

    /**
     * 
     */
    public static void resetNodeId() {
        currentNodeId = 0;
    }

    private List<LabelData> labels;

    private int nodeId;

    /**
     * @param node
     */
    public NodeData(Node node) {
        super(node);
        try {
            ((NodeShape) shape)
                    .buildShape((NodeGraphicAttribute) graphElementGraphicAttribute);
        } catch (ShapeNotFoundException e) {
            e.printStackTrace();
        }

        labels = new LinkedList<LabelData>();

        CollectionAttribute attribute = node.getAttributes();

        Collection<Attribute> attrs = attribute.getCollection().values();

        for (Attribute nextAttribute : attrs) {
            if (nextAttribute instanceof NodeLabelAttribute) {
                labels.add(new LabelData((LabelAttribute) nextAttribute, node
                        .getDouble("graphics.coordinate.x"), node
                        .getDouble("graphics.coordinate.y")));
            }
        }

        nodeId = currentNodeId++;
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
     * Returns the nodeId.
     * 
     * @return the nodeId.
     */
    public int getNodeId() {
        return nodeId;
    }

    /*
     * @see org.graffiti.plugins.ios.exporters.pstricks.GraphElementData#getZ()
     */
    @Override
    public double getZ() {
        return ((NodeGraphicAttribute) graphElementGraphicAttribute)
                .getCoordinate().getZ();
    }

    /**
     * Returns the labels.
     * 
     * @return labels
     */
    public List<LabelData> getLabels() {
        return labels;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
