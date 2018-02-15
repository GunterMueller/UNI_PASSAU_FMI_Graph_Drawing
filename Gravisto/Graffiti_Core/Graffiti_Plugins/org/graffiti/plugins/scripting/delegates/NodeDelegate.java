package org.graffiti.plugins.scripting.delegates;

import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.DelegateFactory;
import org.graffiti.plugins.scripting.delegate.ScriptedField;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.delegate.Unwrappable;
import org.graffiti.plugins.scripting.delegates.attribute.AttributePath;
import org.graffiti.plugins.scripting.delegates.attribute.CoordinateAttributeDelegate;
import org.graffiti.plugins.scripting.delegates.attribute.DimensionAttributeDelegate;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

/**
 * @scripted A graph node.
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public class NodeDelegate extends GraphElementDelegate implements
        Unwrappable<Node> {
    public static class Factory extends DelegateFactory<NodeDelegate, Node> {
        public Factory(Scope scope) {
            super(scope, NodeDelegate.class);
        }

        @Override
        public NodeDelegate create(Node node) {
            return new NodeDelegate(scope, node);
        }
    }

    @AttributePath(GraphicAttributeConstants.COORD_PATH)
    @ScriptedField(names = { "coordinate", "position" })
    protected CoordinateAttributeDelegate coordinate;

    @AttributePath(GraphicAttributeConstants.DIM_PATH)
    @ScriptedField(names = { "dimension", "size" })
    protected DimensionAttributeDelegate dimension;

    private Node node;

    public NodeDelegate(Scope scope, Node node) {
        super(scope, node);
        this.node = node;
    }

    @ScriptedMethod
    public CollectionDelegate getAllInEdges() {
        return wrap(null, node.getAllInEdges());
    }

    @ScriptedMethod
    public CollectionDelegate getAllInNeighbors() {
        return wrap(node.getAllInNeighbors(), null);
    }

    @ScriptedMethod
    public CollectionDelegate getAllOutEdges() {
        return wrap(null, node.getAllOutEdges());
    }

    /**
     * f.
     * 
     * @scripted
     */
    @ScriptedMethod
    public CollectionDelegate getAllOutNeighbors() {
        return wrap(node.getAllOutNeighbors(), null);
    }

    @ScriptedMethod
    public CollectionDelegate getDirectedInEdges() {
        return wrap(null, node.getDirectedInEdges());
    }

    @ScriptedMethod
    public CollectionDelegate getDirectedOutEdges() {
        return wrap(null, node.getDirectedOutEdges());
    }

    @ScriptedMethod
    public CollectionDelegate getEdges() {
        return wrap(null, node.getEdges());
    }

    @ScriptedMethod
    public CollectionDelegate getInNeighbors() {
        return wrap(node.getInNeighbors(), null);
    }

    @ScriptedMethod
    public CollectionDelegate getNeighbors() {
        return wrap(node.getNeighbors(), null);
    }

    @ScriptedMethod
    public CollectionDelegate getOutNeighbors() {
        return wrap(node.getOutNeighbors(), null);
    }

    @ScriptedMethod
    public CollectionDelegate getUndirectedEdges() {
        return wrap(null, node.getUndirectedEdges());
    }

    @ScriptedMethod
    public CollectionDelegate getUndirectedNeighbors() {
        return wrap(node.getUndirectedNeighbors(), null);
    }

    @Override
    public String toString() {
        return "[Node]";
    }

    public Node unwrap() {
        return node;
    }
}
