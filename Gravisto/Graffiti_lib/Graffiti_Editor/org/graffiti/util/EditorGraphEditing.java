// =============================================================================
//
//   EditorGraphEditing.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditorGraphEditing.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.util;

import java.util.Collection;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeLabelAttribute;

/**
 * This class offers some static methods for graph editing.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5768 $ $Date: 2009-04-15 14:08:30 +0200 (Mi, 15 Apr 2009)
 *          $
 */
public class EditorGraphEditing {
    private EditorGraphEditing() {
    }

    /**
     * Removes all bends of the specified edges.
     * 
     * @param edges
     *            These edges' bends will be removed.
     */
    public static void removeBends(Collection<Edge> edges) {
        for (Edge edge : edges) {
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            ListenerManager lm = edge.getGraph().getListenerManager();
            lm.transactionStarted(edge);
            ega.setBends(new LinkedHashMapAttribute(ega.getBends().getId()));

            edge.changeString(GraphicAttributeConstants.SHAPE_PATH,
                    GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME);

            lm.transactionFinished(edge);
        }
    }

    /**
     * Removes all labels of the specified edges.
     * 
     * @param edges
     *            These edges' labels will be removed.
     */
    public static void removeEdgeLabels(Collection<Edge> edges) {
        for (Edge edge : edges) {
            Collection<Attribute> attrColl = edge.getAttributes()
                    .getCollection().values();
            for (Attribute attr : attrColl) {
                if (attr.getId().startsWith(GraphicAttributeConstants.LABEL)) {
                    edge.getGraph().getListenerManager().transactionStarted(
                            edge);
                    edge.removeAttribute(attr.getPath());
                    edge.getGraph().getListenerManager().transactionFinished(
                            edge);
                }
            }
        }
    }

    /**
     * Removes all labels of the specified nodes.
     * 
     * @param nodes
     *            These nodes' labels will be removed.
     */
    public static void removeNodeLabels(Collection<Node> nodes) {
        for (Node node : nodes) {
            Collection<Attribute> attrColl = node.getAttributes()
                    .getCollection().values();
            for (Attribute attr : attrColl) {
                if (attr.getId().startsWith(GraphicAttributeConstants.LABEL)) {
                    node.getGraph().getListenerManager().transactionStarted(
                            node);
                    node.removeAttribute(attr.getPath());
                    node.getGraph().getListenerManager().transactionFinished(
                            node);
                }
            }
        }
    }

    /**
     * Sets the node labels to the particular node degree (= number of incident
     * edges).
     * 
     * @param nodes
     *            These nodes' labels will be set to their node degree.
     */
    public static void labelNodesByDegree(Collection<Node> nodes) {
        for (Node node : nodes) {
            setNodeLabel(node, String.valueOf(node.getEdges().size()));

        }
    }

    /**
     * Sets the node labels to the particular node in-degree (= number of
     * incoming edges).
     * 
     * @param nodes
     *            These nodes' labels will be set to their node in-degree.
     */
    public static void labelNodesByInDegree(Collection<Node> nodes) {
        for (Node node : nodes) {
            setNodeLabel(node, String.valueOf(node.getAllInEdges().size()));

        }
    }

    /**
     * Sets the node labels to the particular node out-degree (= number of
     * outgoing edges).
     * 
     * @param nodes
     *            These nodes' labels will be set to their node out-degree.
     */
    public static void labelNodesByOutDegree(Collection<Node> nodes) {
        for (Node node : nodes) {
            setNodeLabel(node, String.valueOf(node.getAllOutEdges().size()));

        }
    }

    /**
     * Sets distinct integer node labels
     * 
     * @param nodes
     *            These nodes will be labeled.
     */
    public static void setDistinctIntegerNodeLabels(Collection<Node> nodes) {
        int i = 1;
        for (Node node : nodes) {
            setNodeLabel(node, String.valueOf(i));
            i++;
        }
    }

    private static void setNodeLabel(Node node, String label) {
        node.getGraph().getListenerManager().transactionStarted(node);
        NodeLabelAttribute labelAttr;
        try {
            labelAttr = (NodeLabelAttribute) node
                    .getAttribute(GraphicAttributeConstants.LABEL);
            labelAttr.setLabel(label);

        } catch (AttributeNotFoundException anfe) {
            // no label - associate one
            labelAttr = new NodeLabelAttribute(GraphicAttributeConstants.LABEL,
                    String.valueOf(label));
            node.addAttribute(labelAttr,
                    GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);
        }
        node.getGraph().getListenerManager().transactionFinished(node);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
