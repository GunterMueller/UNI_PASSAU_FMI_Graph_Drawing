// =============================================================================
//
//   OpenGLLabelManager.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.label;

import java.util.LinkedList;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.views.fast.label.LabelManager;
import org.graffiti.plugins.views.fast.opengl.AbstractEdgeRep;
import org.graffiti.plugins.views.fast.opengl.AbstractNodeRep;
import org.graffiti.plugins.views.fast.opengl.OpenGLEngine;
import org.graffiti.plugins.views.fast.opengl.Renderer;
import org.graffiti.plugins.views.fast.opengl.label.commands.CommandContext;
import org.graffiti.plugins.views.fast.opengl.label.commands.FirstPhaseContext;
import org.graffiti.plugins.views.fast.opengl.label.commands.OpenGLLabelCommand;
import org.graffiti.plugins.views.fast.opengl.label.commands.SecondPhaseContext;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLLabelManager extends
        LabelManager<OpenGLLabel, OpenGLLabelCommand> {
    private Map<Node, AbstractNodeRep> nodes;
    private Map<Edge, AbstractEdgeRep> edges;
    private Renderer renderer;

    public OpenGLLabelManager(OpenGLEngine engine,
            Map<Node, AbstractNodeRep> nodes, Map<Edge, AbstractEdgeRep> edges) {
        super(engine, new OpenGLCommandFactory());
        this.nodes = nodes;
        this.edges = edges;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public OpenGLLabel createLabel(Node node, NodeLabelAttribute attribute) {
        return new OpenGLLabel(node, attribute, this);
    }

    @Override
    public OpenGLLabel createLabel(Edge edge, EdgeLabelAttribute attribute) {
        return new OpenGLLabel(edge, attribute, this);
    }

    @Override
    protected OpenGLLabel getLabel(Node node, NodeLabelAttribute attribute) {
        return nodes.get(node).getLabel(attribute);
    }

    @Override
    protected OpenGLLabel getLabel(Edge edge, EdgeLabelAttribute attribute) {
        return edges.get(edge).getLabel(attribute);
    }

    @Override
    protected void onAddLabel(Node node, OpenGLLabel label) {
        nodes.get(node).addLabel(label);
    }

    @Override
    protected void onAddLabel(Edge edge, OpenGLLabel label) {
        edges.get(edge).addLabel(label);
    }

    @Override
    protected void onChangeFormat(GraphElement element,
            LabelAttribute attribute, OpenGLLabel label,
            LinkedList<OpenGLLabelCommand> commands) {
        CommandContext commandContext = new CommandContext();
        for (OpenGLLabelCommand command : commands) {
            command.execute((FirstPhaseContext) commandContext);
        }
        commandContext.finishFirstPhase();
        for (OpenGLLabelCommand command : commands) {
            command.execute((SecondPhaseContext) commandContext);
        }
        label.setBufferManager(commandContext.getBufferManager());
        label.setBuffer(commandContext.getBuffer());
        label.setTextList(commandContext.getTextList());
        if (element instanceof Node) {
            nodes.get(element).onChangeLabel(label);
        } else if (element instanceof Edge) {
            edges.get(element).onChangeLabel(label);
        }
        renderer.raiseRedrawLevel(Renderer.REDRAW_LEVEL_REDRAW);
    }

    @Override
    protected void onChangePosition(GraphElement element,
            LabelAttribute attribute, OpenGLLabel label) {
        if (element instanceof Node) {
            nodes.get(element).onChangeLabel(label);
        } else if (element instanceof Edge) {
            edges.get(element).onChangeLabel(label);
        }
        renderer.raiseRedrawLevel(Renderer.REDRAW_LEVEL_REDRAW);
    }

    @Override
    public void onDeleteLabel(Node node, NodeLabelAttribute attribute) {
        nodes.get(node).removeLabel(attribute);
        renderer.raiseRedrawLevel(Renderer.REDRAW_LEVEL_REDRAW);
    }

    @Override
    public void onDeleteLabel(Edge edge, EdgeLabelAttribute attribute) {
        edges.get(edge).removeLabel(attribute);
        renderer.raiseRedrawLevel(Renderer.REDRAW_LEVEL_REDRAW);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
