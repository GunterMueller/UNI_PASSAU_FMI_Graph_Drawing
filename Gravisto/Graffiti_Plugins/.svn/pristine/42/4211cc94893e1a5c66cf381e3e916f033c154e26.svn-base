package org.graffiti.plugins.scripting.delegates;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.DelegateFactory;
import org.graffiti.plugins.scripting.delegate.FieldDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedField;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.delegate.Unwrappable;
import org.graffiti.plugins.scripting.delegates.attribute.AttributePath;
import org.graffiti.plugins.scripting.delegates.attribute.DoubleAttributeFieldDelegate;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

/**
 * @scripted An edge.
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public class EdgeDelegate extends GraphElementDelegate implements
        Unwrappable<Edge> {
    public static class Factory extends DelegateFactory<EdgeDelegate, Edge> {
        public Factory(Scope scope) {
            super(scope, EdgeDelegate.class);
        }

        @Override
        public EdgeDelegate create(Edge edge) {
            return new EdgeDelegate(scope, edge);
        }
    }

    private Edge edge;

    @AttributePath(GraphicAttributeConstants.THICKNESS_PATH)
    @ScriptedField
    protected DoubleAttributeFieldDelegate thickness;

    @AttributePath(GraphicAttributeConstants.DEPTH_PATH)
    @ScriptedField
    protected DoubleAttributeFieldDelegate depth;

    @ScriptedField
    protected FieldDelegate<NodeDelegate> source = new FieldDelegate<NodeDelegate>(
            NodeDelegate.class) {
        @Override
        public NodeDelegate get() {
            return wrap(edge.getSource());
        }

        @Override
        public void set(NodeDelegate node) {
            edge.setSource(node.unwrap());
        }
    };

    @ScriptedField
    protected FieldDelegate<NodeDelegate> target = new FieldDelegate<NodeDelegate>(
            NodeDelegate.class) {
        @Override
        public NodeDelegate get() {
            return wrap(edge.getTarget());
        }

        @Override
        public void set(NodeDelegate node) {
            edge.setTarget(node.unwrap());
        }
    };

    public EdgeDelegate(Scope scope, Edge edge) {
        super(scope, edge);
        this.edge = edge;
    }

    @ScriptedMethod
    public NodeDelegate getSource() {
        return wrap(edge.getSource());
    }

    @ScriptedMethod
    public NodeDelegate getTarget() {
        return wrap(edge.getTarget());
    }

    @ScriptedMethod
    public void setSource(NodeDelegate node) {
        edge.setSource(node.unwrap());
    }

    @ScriptedMethod
    public void setTarget(NodeDelegate node) {
        edge.setTarget(node.unwrap());
    }

    @Override
    public String toString() {
        return "[Edge]";
    }

    public Edge unwrap() {
        return edge;
    }
}
