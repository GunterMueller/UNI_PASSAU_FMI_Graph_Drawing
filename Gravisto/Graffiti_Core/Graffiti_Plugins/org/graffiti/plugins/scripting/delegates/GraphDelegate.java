package org.graffiti.plugins.scripting.delegates;

import java.util.Collection;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.DelegateFactory;
import org.graffiti.plugins.scripting.delegate.FieldAccess;
import org.graffiti.plugins.scripting.delegate.ScriptedField;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.delegate.Unwrappable;
import org.graffiti.plugins.scripting.delegates.attribute.CollectionAttributeDelegate;

/**
 * @scripted The graph.
 * @author Andreas Glei&szlig;ner
 */
public class GraphDelegate extends CollectionDelegate implements
        Unwrappable<Graph> {
    public static class Factory extends DelegateFactory<GraphDelegate, Graph> {
        public Factory(Scope scope) {
            super(scope, GraphDelegate.class);
        }

        @Override
        public GraphDelegate create(Graph graph) {
            return new GraphDelegate(scope, graph);
        }
    }

    @ScriptedField(access = FieldAccess.Get)
    protected CollectionAttributeDelegate attribute;

    private Graph graph;

    public GraphDelegate(Scope scope, Graph graph) {
        super(scope);
        this.graph = graph;

        attribute = new CollectionAttributeDelegate(scope, graph
                .getAttributes());
    }

    @Override
    protected void addImpl(NodeDelegate node) {
    }

    @Override
    protected void addImpl(EdgeDelegate edge) {
    }

    @ScriptedMethod
    public NodeDelegate addNode() {
        return scope.getCanonicalDelegate(graph.addNode(),
                new NodeDelegate.Factory(scope));
    }

    @ScriptedMethod
    public EdgeDelegate addEdge(NodeDelegate source, NodeDelegate target,
            Boolean directed) {
        return scope.getCanonicalDelegate(graph.addEdge(source.unwrap(), target
                .unwrap(), directed), new EdgeDelegate.Factory(scope));
    }

    @ScriptedMethod
    @Override
    public void clear() {
        graph.clear();
    }

    @Override
    protected boolean containsImpl(NodeDelegate node) {
        return graph.containsNode(node.unwrap());
    }

    @Override
    protected boolean containsImpl(EdgeDelegate edge) {
        return graph.containsEdge(edge.unwrap());
    }

    @ScriptedMethod
    public boolean containsNode(NodeDelegate node) {
        return containsImpl(node);
    }

    @ScriptedMethod
    public boolean containsEdge(EdgeDelegate edge) {
        return containsImpl(edge);
    }

    @ScriptedMethod
    public void deleteNode(NodeDelegate node) {
        removeImpl(node);
    }

    @ScriptedMethod
    public void deleteEdge(EdgeDelegate edge) {
        removeImpl(edge);
    }

    /**
     * Returns the delegate representing the grid of the graph represented by
     * this delegate.
     * 
     * @return the delegate representing the grid of the graph represented by
     *         this delegate.
     * @scripted Returns the grid.
     */
    @ScriptedMethod
    public GridDelegate getGrid() {
        return scope.getCanonicalDelegate(((GridAttribute) graph
                .getAttribute(GraphicAttributeConstants.GRID_PATH)).getGrid(),
                new GridDelegate.Factory(scope));
    }

    @Override
    protected Collection<NodeDelegate> getNodeCollection() {
        CanonicalDelegate<NodeDelegate, Node> cw = new CanonicalDelegate<NodeDelegate, Node>(
                new NodeDelegate.Factory(scope), NodeDelegate.class);
        LinkedList<NodeDelegate> nodes = new LinkedList<NodeDelegate>();
        for (Node node : graph.getNodes()) {
            nodes.add(cw.create(node));
        }
        return nodes;
    }

    @Override
    protected Collection<EdgeDelegate> getEdgeCollection() {
        CanonicalDelegate<EdgeDelegate, Edge> cw = new CanonicalDelegate<EdgeDelegate, Edge>(
                new EdgeDelegate.Factory(scope), EdgeDelegate.class);
        LinkedList<EdgeDelegate> edges = new LinkedList<EdgeDelegate>();
        for (Edge edge : graph.getEdges()) {
            edges.add(cw.create(edge));
        }
        return edges;
    }

    @ScriptedMethod
    public final CollectionDelegate getEdges(NodeDelegate node1,
            NodeDelegate node2) {
        CanonicalDelegate<EdgeDelegate, Edge> cw = new CanonicalDelegate<EdgeDelegate, Edge>(
                new EdgeDelegate.Factory(scope), EdgeDelegate.class);
        LinkedList<EdgeDelegate> edges = new LinkedList<EdgeDelegate>();
        for (Edge edge : graph.getEdges(node1.unwrap(), node2.unwrap())) {
            edges.add(cw.create(edge));
        }
        return new BufferedCollectionDelegate(scope,
                new LinkedList<NodeDelegate>(), edges);
    }

    @ScriptedMethod
    @Override
    public boolean isEmpty() {
        return graph.isEmpty();
    }

    @ScriptedMethod
    public boolean isDirected() {
        return graph.isDirected();
    }

    @ScriptedMethod
    public boolean isModified() {
        return graph.isModified();
    }

    @Override
    protected void removeImpl(NodeDelegate node) {
        graph.deleteNode(node.unwrap());
    }

    @Override
    protected void removeImpl(EdgeDelegate edge) {
        graph.deleteEdge(edge.unwrap());
    }

    @ScriptedMethod
    public void setDirected(Boolean directed) {
        graph.setDirected(directed);
    }

    /**
     * Sets the grid of the graph represented by this delegate to the grid
     * wrapped by the specified grid delegate.
     * 
     * @param grid
     *            the grid to set.
     * @scripted Sets the grid.
     */
    @ScriptedMethod
    public void setGrid(GridDelegate grid) {
        ((GridAttribute) graph
                .getAttribute(GraphicAttributeConstants.GRID_PATH))
                .setGrid(grid.unwrap());
    }

    @ScriptedMethod
    public void setModified(Boolean modified) {
        graph.setModified(modified);
    }

    @ScriptedMethod
    @Override
    public int size() {
        return graph.getNumberOfNodes() + graph.getNumberOfEdges();
    }

    @Override
    public String toString() {
        return "[Graph]";
    }

    public Graph unwrap() {
        return graph;
    }
}
