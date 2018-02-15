package org.graffiti.plugins.modes.fast;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.session.EditorSession;
import org.graffiti.undo.AddEdgeEdit;
import org.graffiti.undo.AddNodeEdit;
import org.graffiti.undo.ChangeAttributesEdit;
import org.graffiti.undo.GraffitiAbstractUndoableEdit;
import org.graffiti.undo.GraphElementsDeletionEdit;
import org.graffiti.undo.GraphElementsEdit;
import org.graffiti.undo.Undoable;

/**
 * Class that simplifies the use of undo/redo functionality. It supersedes the
 * direct usage of {@link UndoableEditSupport} and the descendants of
 * {@link GraffitiAbstractUndoableEdit}. {@code UndoHelper} objects must be
 * created and closed in a FILO manner. This class may be soon moved to another
 * package.
 * <p>
 * Example usage:
 * 
 * <pre>
 * UndoHelper helper = new UndoHelper(session);
 * helper.close();
 * </pre>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Undoable
 * @see ChangeAttributesEdit
 * @see GraphElementsEdit
 */
public class UndoUtil {
    private static LinkedList<UndoUtil> helperList = new LinkedList<UndoUtil>();

    private UndoableEditSupport undoSupport;

    private Map<GraphElement, GraphElement> graphElementsMap;

    private Map<Attribute, Object> attributeMap;

    private Graph graph;

    private int editCount;

    /**
     * Creates a new {@code UndoHelper} object. Requires the presence of a
     * {@link MainFrame}, as it uses its {@link UndoableEditSupport}. To use a
     * different {@code UndoableEditSupport}, see
     * {@link #UndoUtil(EditorSession, UndoableEditSupport)}. If at least one
     * change was registered, {@link #close()} must be called.
     * 
     * @param session
     *            the session whose graph the undoable changes are performed on.
     * @throws NullPointerException
     *             if {@code session} is {@code null}, there is no {@code
     *             MainFrame} or the {@code MainFrame} does not hold an {@code
     *             UndoableEditSupport}.
     */
    public UndoUtil(EditorSession session) {
        this(session, GraffitiSingleton.getInstance().getMainFrame()
                .getUndoSupport());
    }

    /**
     * Creates a new {@code UndoHelper} object. If at least one change was
     * registered, {@link #close()} must be called.
     * 
     * @param session
     *            the session whose graph the undoable changes are performed on.
     * @param undoSupport
     *            the {@code UndoableEditSupport} to use. In order to use the
     *            default {@code UndoableEditSupport}, see
     *            {@link #UndoUtil(EditorSession)}.
     * @throws NullPointerException
     *             if {@code session} or {@code undoSupport} is {@code null}.
     */
    public UndoUtil(EditorSession session, UndoableEditSupport undoSupport) {
        if (session == null || undoSupport == null)
            throw new NullPointerException();
        this.undoSupport = undoSupport;
        graphElementsMap = session.getGraphElementsMap();
        graph = session.getGraph();
        editCount = 0;
        helperList.addLast(this);
    }

    public Node addNode() {
        if (undoSupport == null)
            throw new IllegalStateException();
        checkAttributeMap();
        Node node = graph.addNode();
        postEdit(new AddNodeEdit(node, graph, graphElementsMap));
        return node;
    }

    public Node addNode(CollectionAttribute attribute) {
        if (undoSupport == null)
            throw new IllegalStateException();
        checkAttributeMap();
        Node node = graph.addNode(attribute);
        postEdit(new AddNodeEdit(node, graph, graphElementsMap));
        return node;
    }

    public Node addNodeCopy(Node node) {
        if (undoSupport == null)
            throw new IllegalStateException();
        checkAttributeMap();
        Node newNode = graph.addNodeCopy(node);
        postEdit(new AddNodeEdit(newNode, graph, graphElementsMap));
        return newNode;
    }

    public Edge addEdge(Node source, Node target, boolean isDirected) {
        if (undoSupport == null)
            throw new IllegalStateException();
        checkAttributeMap();
        Edge edge = graph.addEdge(source, target, isDirected);
        postEdit(new AddEdgeEdit(edge, graph, graphElementsMap));
        return edge;
    }

    public Edge addEdge(Node source, Node target, boolean isDirected,
            CollectionAttribute attribute) {
        if (undoSupport == null)
            throw new IllegalStateException();
        checkAttributeMap();
        Edge edge = graph.addEdge(source, target, isDirected, attribute);
        postEdit(new AddEdgeEdit(edge, graph, graphElementsMap));
        return edge;
    }

    public Edge addEdgeCopy(Edge edge, Node source, Node target) {
        if (undoSupport == null)
            throw new IllegalStateException();
        checkAttributeMap();
        Edge newEdge = graph.addEdgeCopy(edge, source, target);
        postEdit(new AddEdgeEdit(newEdge, graph, graphElementsMap));
        return newEdge;
    }

    public void deleteElements(Collection<? extends GraphElement> elements) {
        if (undoSupport == null)
            throw new IllegalStateException();
        checkAttributeMap();
        GraphElementsDeletionEdit edit = new GraphElementsDeletionEdit.Builder(
                graphElementsMap, graph, elements).build();
        edit.execute();
        postEdit(edit);
    }

    public void deleteElements(Collection<? extends GraphElement> elements,
            String presentationName) {
        if (undoSupport == null)
            throw new IllegalStateException();
        checkAttributeMap();
        GraphElementsDeletionEdit edit = new GraphElementsDeletionEdit.Builder(
                graphElementsMap, graph, elements).presentationName(
                presentationName).build();
        edit.execute();
        postEdit(edit);
    }

    public void preChange(Attribute attribute) {
        if (undoSupport == null)
            throw new IllegalStateException();
        if (attributeMap == null) {
            attributeMap = new HashMap<Attribute, Object>();
        }
        attributeMap.put(attribute, ((Attribute) attribute.copy()).getValue());
    }

    /**
     * Closes this object. {@code UndoHelper} objects must be created and closed
     * in a FILO manner.
     * 
     * @throws IllegalStateException
     *             if this object has already been closed or if there is an
     *             unclosed {@code UndoHelper} that was created after this
     *             object.
     */
    public void close() {
        if (undoSupport == null || helperList.removeLast() != this)
            throw new IllegalStateException();
        checkAttributeMap();
        if (editCount != 0) {
            undoSupport.endUpdate();
        }
        undoSupport = null;
    }

    private void checkAttributeMap() {
        if (attributeMap != null) {
            postEdit(new ChangeAttributesEdit(attributeMap, graphElementsMap));
            attributeMap = null;
        }
    }

    private void postEdit(UndoableEdit undoableEdit) {
        if (editCount == 0) {
            undoSupport.beginUpdate();
        }
        editCount++;
        undoSupport.postEdit(undoableEdit);
    }
}