package org.graffiti.plugins.scripting.delegates;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.delegate.Unwrappable;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;

/**
 * Delegate, which represents the current selection of a session. As the
 * {@link Selection} and {@link SelectionModel} objects of a session may change,
 * but every instance of {@code SelectionDelegate} always refers to the
 * <i>current</i> selection, they are obtained from the session each time they
 * are needed.
 * 
 * @scripted The current selection.
 * @author Andreas Glei&szlig;ner
 * @see EditorSession#getSelectionModel()
 */
@DocumentedDelegate(DefaultDocumentation.class)
public class SelectionDelegate extends CollectionDelegate implements
        Unwrappable<Collection<GraphElement>> {
    private EditorSession session;

    /**
     * Constructs a {@code SelectionDelegate} in the specified scope for the
     * specified session.
     * 
     * @param scope
     *            the scope.
     * @param session
     *            the session the current selection of which to represent.
     */
    public SelectionDelegate(Scope scope, EditorSession session) {
        super(scope);
        this.session = session;
    }

    @Override
    protected void addImpl(NodeDelegate node) {
        SelectionModel model = session.getSelectionModel();
        if (model != null) {
            Selection selection = model.getActiveSelection();
            if (selection == null) {
                selection = new Selection();
                model.setActiveSelection(selection);
            }
            if (!containsImpl(node)) {
                selection.add(node.unwrap());
            }
        }
    }

    @Override
    protected void addImpl(EdgeDelegate edge) {
        SelectionModel model = session.getSelectionModel();
        if (model != null) {
            Selection selection = model.getActiveSelection();
            if (selection == null) {
                selection = new Selection();
                model.setActiveSelection(selection);
            }
            if (!containsImpl(edge)) {
                selection.add(edge.unwrap());
            }
        }
    }

    @Override
    protected void changed() {
        SelectionModel model = session.getSelectionModel();
        if (model != null) {
            model.selectionChanged();
        }
    }

    @ScriptedMethod
    @Override
    public void clear() {
        SelectionModel model = session.getSelectionModel();
        if (model != null) {
            Selection selection = model.getActiveSelection();
            if (selection != null) {
                selection.clear();
                model.selectionChanged();
            }
        }
    }

    @Override
    protected boolean containsImpl(NodeDelegate node) {
        Selection selection = getSelection();
        return selection == null ? false : selection.contains(node.unwrap());
    }

    @Override
    protected boolean containsImpl(EdgeDelegate edge) {
        Selection selection = getSelection();
        return selection == null ? false : selection.contains(edge.unwrap());
    }

    @Override
    protected Collection<NodeDelegate> getNodeCollection() {
        Selection selection = getSelection();
        if (selection == null)
            return Collections.emptyList();
        else {
            CanonicalDelegate<NodeDelegate, Node> cd = new CanonicalDelegate<NodeDelegate, Node>(
                    new NodeDelegate.Factory(scope), NodeDelegate.class);
            LinkedList<NodeDelegate> nodes = new LinkedList<NodeDelegate>();
            for (Node node : selection.getNodes()) {
                nodes.add(cd.create(node));
            }
            return nodes;
        }
    }

    @Override
    protected Collection<EdgeDelegate> getEdgeCollection() {
        Selection selection = getSelection();
        if (selection == null)
            return Collections.emptyList();
        else {
            CanonicalDelegate<EdgeDelegate, Edge> cd = new CanonicalDelegate<EdgeDelegate, Edge>(
                    new EdgeDelegate.Factory(scope), EdgeDelegate.class);
            LinkedList<EdgeDelegate> edges = new LinkedList<EdgeDelegate>();
            for (Edge edge : selection.getEdges()) {
                edges.add(cd.create(edge));
            }
            return edges;
        }
    }

    /**
     * Returns the current selection of the session passed to the constructor.
     * 
     * @return the current selection. May be {@code null}.
     * @see SelectionModel#getActiveSelection()
     */
    private Selection getSelection() {
        SelectionModel model = session.getSelectionModel();
        return model != null ? model.getActiveSelection() : null;
    }

    @ScriptedMethod
    @Override
    public boolean isEmpty() {
        Selection selection = getSelection();
        return selection == null || selection.isEmpty();
    }

    @Override
    protected void removeImpl(NodeDelegate node) {
        Selection selection = getSelection();
        if (selection != null) {
            selection.remove(node.unwrap());
        }
    }

    @Override
    protected void removeImpl(EdgeDelegate edge) {
        Selection selection = getSelection();
        if (selection != null) {
            selection.remove(edge.unwrap());
        }
    }

    @ScriptedMethod
    @Override
    public int size() {
        Selection selection = getSelection();
        return selection == null ? 0 : selection.getNodes().size()
                + selection.getEdges().size();
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
