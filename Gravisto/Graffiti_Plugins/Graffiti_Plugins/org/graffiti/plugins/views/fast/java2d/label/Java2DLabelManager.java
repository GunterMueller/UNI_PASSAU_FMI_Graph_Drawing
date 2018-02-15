// =============================================================================
//
//   Java2DLabelManager.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.java2d.label;

import java.util.LinkedList;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.views.fast.java2d.AbstractEdgeRep;
import org.graffiti.plugins.views.fast.java2d.AbstractNodeRep;
import org.graffiti.plugins.views.fast.java2d.Java2DEngine;
import org.graffiti.plugins.views.fast.java2d.label.commands.Java2DLabelCommand;
import org.graffiti.plugins.views.fast.label.LabelManager;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Java2DLabelManager extends
        LabelManager<Java2DLabel, Java2DLabelCommand> {
    private Map<Node, AbstractNodeRep> nodes;
    private Map<Edge, AbstractEdgeRep> edges;

    public Java2DLabelManager(Java2DEngine engine,
            Map<Node, AbstractNodeRep> nodes, Map<Edge, AbstractEdgeRep> edges) {
        super(engine, new Java2DCommandFactory());
        this.nodes = nodes;
        this.edges = edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Java2DLabel createLabel(Node node, NodeLabelAttribute attribute) {
        return new Java2DLabel(node, attribute, this);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.label.LabelFactory#createLabel(org.graffiti
     * .graph.GraphElement)
     */
    @Override
    public Java2DLabel createLabel(Edge edge, EdgeLabelAttribute attribute) {
        return new Java2DLabel(edge, attribute, this);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.label.LabelFactory#deleteLabel(org.graffiti
     * .plugins.views.fast.label.Label)
     */
    @Override
    public void onDeleteLabel(Node node, NodeLabelAttribute attribute) {
        nodes.get(node).removeLabel(attribute);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.label.LabelFactory#deleteLabel(org.graffiti
     * .plugins.views.fast.label.Label)
     */
    @Override
    public void onDeleteLabel(Edge edge, EdgeLabelAttribute attribute) {
        edges.get(edge).removeLabel(attribute);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.label.LabelFactory#getLabel(org.graffiti
     * .graph.Node, org.graffiti.graphics.NodeLabelAttribute)
     */
    @Override
    protected Java2DLabel getLabel(Node node, NodeLabelAttribute attribute) {
        return nodes.get(node).getLabel(attribute);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.label.LabelFactory#getLabel(org.graffiti
     * .graph.Edge, org.graffiti.graphics.EdgeLabelAttribute)
     */
    @Override
    protected Java2DLabel getLabel(Edge edge, EdgeLabelAttribute attribute) {
        return edges.get(edge).getLabel(attribute);
    }

    @Override
    protected void onChangePosition(GraphElement element,
            LabelAttribute attribute, Java2DLabel label) {
        if (element instanceof Node) {
            AbstractNodeRep rep = nodes.get(element);
            if (rep == null)
                return;
            rep.onChangeLabel(label);

        } else if (element instanceof Edge) {
            AbstractEdgeRep rep = edges.get(element);
            if (rep == null)
                return;
            rep.onChangeLabel(label);
        }

    }

    @Override
    protected void onChangeFormat(GraphElement element,
            LabelAttribute attribute, Java2DLabel label,
            LinkedList<Java2DLabelCommand> commands) {
        label.setCommands(commands);
        if (element instanceof Node) {
            AbstractNodeRep rep = nodes.get(element);
            if (rep == null)
                return;
            rep.onChangeLabel(label);
        } else if (element instanceof Edge) {
            AbstractEdgeRep rep = edges.get(element);
            if (rep == null)
                return;
            rep.onChangeLabel(label);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAddLabel(Node node, Java2DLabel label) {
        nodes.get(node).addLabel(label);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.label.LabelManager#addLabel(org.graffiti
     * .graph.Edge, org.graffiti.plugins.views.fast.label.Label)
     */
    @Override
    protected void onAddLabel(Edge edge, Java2DLabel label) {
        edges.get(edge).addLabel(label);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
