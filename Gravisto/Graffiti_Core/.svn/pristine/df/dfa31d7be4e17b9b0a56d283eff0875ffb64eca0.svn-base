package org.graffiti.plugins.scripting.delegates;

import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphElementGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.FieldAccess;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedField;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.delegates.attribute.AttributePath;
import org.graffiti.plugins.scripting.delegates.attribute.CollectionAttributeDelegate;
import org.graffiti.plugins.scripting.delegates.attribute.ColorAttributeDelegate;
import org.graffiti.plugins.scripting.delegates.attribute.DoubleAttributeFieldDelegate;
import org.graffiti.plugins.scripting.delegates.attribute.LineModeAttributeDelegate;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

/**
 * @scripted A graph element.
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public abstract class GraphElementDelegate extends ObjectDelegate {
    private GraphElement element;

    @AttributePath(GraphicAttributeConstants.FILLCOLOR_PATH)
    @ScriptedField(names = { "fillColor", "fillcolor" })
    protected ColorAttributeDelegate fillColor;

    @AttributePath(GraphicAttributeConstants.GRAPHICS + Attribute.SEPARATOR
            + GraphicAttributeConstants.FRAMECOLOR)
    @ScriptedField(names = { "frameColor", "framecolor" })
    protected ColorAttributeDelegate frameColor;

    @AttributePath(GraphicAttributeConstants.FRAMETHICKNESS_PATH)
    @ScriptedField
    protected DoubleAttributeFieldDelegate frameThickness;

    @AttributePath(GraphicAttributeConstants.LINEMODE_PATH)
    @ScriptedField(names = { "lineMode", "linemode" })
    protected LineModeAttributeDelegate lineMode;

    @ScriptedField(access = FieldAccess.Get)
    protected CollectionAttributeDelegate attribute;

    @ScriptedField(access = FieldAccess.Get)
    protected CollectionAttributeDelegate graphics;

    public GraphElementDelegate(Scope scope, GraphElement element) {
        super(scope);
        this.element = element;
        attribute = new CollectionAttributeDelegate(scope, element
                .getAttributes());
        try {
            graphics = new CollectionAttributeDelegate(scope,
                    (GraphElementGraphicAttribute) element
                            .getAttribute(GraphicAttributeConstants.GRAPHICS));
        } catch (AttributeNotFoundException e) {
        }
    }

    protected NodeDelegate wrap(Node node) {
        return scope
                .getCanonicalDelegate(node, new NodeDelegate.Factory(scope));
    }

    protected EdgeDelegate wrap(Edge edge) {
        return scope
                .getCanonicalDelegate(edge, new EdgeDelegate.Factory(scope));
    }

    protected CollectionDelegate wrap(Collection<Node> nodes,
            Collection<Edge> edges) {
        LinkedList<NodeDelegate> nodeDelegates = new LinkedList<NodeDelegate>();
        if (nodes != null && !nodes.isEmpty()) {
            CanonicalDelegate<NodeDelegate, Node> cnd = new CanonicalDelegate<NodeDelegate, Node>(
                    new NodeDelegate.Factory(scope), NodeDelegate.class);

            for (Node node : nodes) {
                nodeDelegates.add(cnd.create(node));
            }
        }

        LinkedList<EdgeDelegate> edgeDelegates = new LinkedList<EdgeDelegate>();
        if (edges != null && !edges.isEmpty()) {
            CanonicalDelegate<EdgeDelegate, Edge> ced = new CanonicalDelegate<EdgeDelegate, Edge>(
                    new EdgeDelegate.Factory(scope), EdgeDelegate.class);

            for (Edge edge : edges) {
                edgeDelegates.add(ced.create(edge));
            }
        }

        return new BufferedCollectionDelegate(scope, nodeDelegates,
                edgeDelegates);
    }

    @ScriptedMethod
    public GraphDelegate getGraph() {
        return scope.getCanonicalDelegate(element.getGraph(),
                new GraphDelegate.Factory(scope));
    }
}
