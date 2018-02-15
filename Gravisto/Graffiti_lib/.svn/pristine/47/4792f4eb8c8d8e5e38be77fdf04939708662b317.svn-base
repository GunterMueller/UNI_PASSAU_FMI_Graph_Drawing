package org.graffiti.plugins.scripting.delegates;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.graffiti.graph.GraphElement;
import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ScriptedConstructor;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.delegate.Unwrappable;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

/**
 * @scripted A collection of graph elements.
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public class BufferedCollectionDelegate extends CollectionDelegate implements
        Unwrappable<Collection<GraphElement>> {
    private Set<NodeDelegate> nodes;
    private Set<EdgeDelegate> edges;

    public BufferedCollectionDelegate(Scope scope) {
        super(scope);
        nodes = new HashSet<NodeDelegate>();
        edges = new HashSet<EdgeDelegate>();
    }

    public BufferedCollectionDelegate(Scope scope,
            Collection<NodeDelegate> nodes, Collection<EdgeDelegate> edges) {
        super(scope);
        this.nodes = new HashSet<NodeDelegate>(nodes);
        this.edges = new HashSet<EdgeDelegate>(edges);
    }

    @ScriptedConstructor("Collection")
    public BufferedCollectionDelegate(Scope scope,
            GraphElementDelegate... elements) {
        this(scope);
        for (GraphElementDelegate element : elements) {
            add(element);
        }
    }

    @Override
    protected void addImpl(NodeDelegate node) {
        nodes.add(node);
    }

    @Override
    protected void addImpl(EdgeDelegate edge) {
        edges.add(edge);
    }

    @Override
    public void clear() {
        nodes.clear();
        edges.clear();
    }

    @Override
    protected boolean containsImpl(NodeDelegate node) {
        return nodes.contains(node);
    }

    @Override
    protected boolean containsImpl(EdgeDelegate edge) {
        return edges.contains(edge);
    }

    @Override
    protected Collection<NodeDelegate> getNodeCollection() {
        return nodes;
    }

    @Override
    protected Collection<EdgeDelegate> getEdgeCollection() {
        return edges;
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty() && edges.isEmpty();
    }

    @Override
    protected void removeImpl(NodeDelegate node) {
        nodes.remove(node);
    }

    @Override
    protected void removeImpl(EdgeDelegate edge) {
        edges.remove(edge);
    }

    @ScriptedMethod
    @Override
    public int size() {
        return nodes.size() + edges.size();
    }

    public Collection<GraphElement> unwrap() {
        Set<GraphElement> elements = new HashSet<GraphElement>();
        for (NodeDelegate nodeWrapper : getNodeCollection()) {
            elements.add(nodeWrapper.unwrap());
        }
        for (EdgeDelegate edgeWrapper : getEdgeCollection()) {
            elements.add(edgeWrapper.unwrap());
        }
        return elements;
    }
}
