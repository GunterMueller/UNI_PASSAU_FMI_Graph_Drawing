package org.graffiti.plugins.algorithms.mst.adapters;

import java.util.Collection;
import java.util.Iterator;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.mst.Heap;
import org.graffiti.plugins.algorithms.mst.adapters.attribute.HeapEntryAttribute;
import org.graffiti.plugins.algorithms.mst.adapters.attribute.NodeAttribute;

/**
 * Proxy object for {@link org.graffiti.graph.Node} objects; provides customized
 * methods for minimum spanning tree algorithms and their animations.
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class NodeAdapter {

    /**
     * The path to the collection attribute containing all attributes.
     */
    public static final String EMPTY_PATH = "";

    /**
     * The path to the collection attribute containing all attributes used by
     * minimum spanning tree algorithms.
     */
    public static final String PATH_TO_ROOT_ATTRIBUTE = "mst";

    /**
     * The id of the attribute storing a node's parent node.
     */
    public static final String PARENT_ATTRIBUTE = "parent";

    /**
     * The id of the attribute storing a node's selection flag.
     */
    public static final String SELECTION_FLAG_ATTRIBUTE = "selectionFlag";

    /**
     * The id of the attribute storing a node's heap entry.
     */
    public static final String HEAP_ENTRY_ATTRIBUTE = "heapEntry";

    /**
     * The node this node proxy stands for.
     */
    private Node node = null;

    private EdgeAdapterFactory edgeAdapterFactory = null;

    /**
     * The proxy builder instance to be used for creating new proxies.
     */

    /**
     * Default constructor; does nothing.
     * <p>
     * Note that instances acquired using this constructor will throw
     * <tt>NullPointerException</tt> on every attempt to call an instance
     * method.
     */
    protected NodeAdapter() {
    }

    /**
     * Initializes this node proxy with the given node and proxy builder.
     * 
     * @param n
     *            the node this proxy will stand for.
     */
    protected NodeAdapter(Node n, EdgeAdapterFactory f) {
        node = n;
        edgeAdapterFactory = f;
    }

    /**
     * Checks if this node's adaptee is null. If so, throws
     * <tt>NullPointerException</tt>.
     * 
     */
    private void checkNodeIsNotNull() {
        if (node == null)
            throw new NullPointerException();
    }

    /**
     * Returns the key of this node adapter.
     * 
     * @return the key of this node adapter.
     */
    public float getKey() {
        return heapEntry().getKey();
    }

    /**
     * Returns the heap entry of the <tt>Node</tt> this proxy stands for.
     * 
     * @see org.graffiti.graph.Node
     * 
     * @return the heap entry of the <tt>Node</tt> this proxy stands for.
     */
    @SuppressWarnings("unchecked")
    private Heap.Entry<Node, Float> heapEntry() {
        return (Heap.Entry<Node, Float>) heapEntryAttribute().getValue();
    }

    /**
     * Returns the attribute storing the heap entry of the <tt>Node</tt> this
     * proxy stands for.
     * 
     * @see org.graffiti.graph.Node
     * @return the attribute storing the heap entry of the <tt>Node</tt> this
     *         proxy stands for.
     */
    private Attribute heapEntryAttribute() {
        checkNodeIsNotNull();
        return node.getAttribute("mst.heapEntry");
    }

    /**
     * Sets the key of this node proxy to the specified value.
     * 
     * @param key
     *            the new key of this node proxy.
     */
    public void setKey(float key) {
        heapEntry().setKey(key);
    }

    // Parent property

    /**
     * Sets this node proxy's parent to the specified value.
     * 
     * @param p
     *            the new parent of this node proxy.
     */
    public void setParent(NodeAdapter p) {
        p.setAsParentOf(this);
    }

    /**
     * Returns the parent of this node proxy.
     * 
     * @return the parent of this node proxy.
     */
    public NodeAdapter getParent() {
        return new NodeAdapter(parent(), edgeAdapterFactory);
    }

    /**
     * Sets this node proxy as the parent of the specified node proxy.
     * 
     * @param n
     *            the node proxy whose parent is to be set.
     */
    public void setAsParentOf(NodeAdapter n) {
        n.setParentNode(node);
    }

    /**
     * Sets the parent node of this node proxy to the specified value.
     * 
     * @param p
     *            the new parent of this node proxy.
     */
    public void setParentNode(Node p) {
        parentAttribute().setValue(p);
    }

    // Selection

    /**
     * Returns the parent node of this node proxy.
     * 
     * @return the parent node of this node proxy.
     */
    private Node parent() {
        return (Node) parentAttribute().getValue();
    }

    /**
     * Returns the attribute storing the parent node of this node proxy.
     * 
     * @return the attribute storing the parent node of this node proxy.
     */
    private Attribute parentAttribute() {
        checkNodeIsNotNull();
        return node.getAttribute("mst.parent");
    }

    /**
     * Returns <tt>true</tt> if this node proxy's selection flag is set.
     * 
     * @return <tt>true</tt> if this node proxy's selection flag is set.
     */
    public boolean isSelected() {
        return selectionFlagAttribute().getBoolean();
    }

    /**
     * Returns the boolean attribute storing the selection flag of the node this
     * node proxy stands for.
     * 
     * @return the boolean attribute storing the selection flag of the node this
     *         node proxy stands for.
     * 
     */
    private BooleanAttribute selectionFlagAttribute() {
        checkNodeIsNotNull();
        return (BooleanAttribute) node.getAttribute("mst.selectionFlag");
    }

    /**
     * Sets this node proxy's selection flag to <tt>true</tt>.
     * 
     */
    public void select() {
        selectionFlagAttribute().setBoolean(true);
    }

    /**
     * Returns <tt>true</tt> if this node proxy is selected and its parent node
     * is <tt>null</tt>.
     * 
     * @return <tt>true</tt> if this node proxy is selected and its parent node
     *         is <tt>null</tt>.
     */
    public boolean isRoot() {
        return isSelected() && parent() == null;
    }

    /**
     * Sets this node proxy's selection flag to <tt>false</tt>.
     * 
     */
    public void unselect() {
        selectionFlagAttribute().setBoolean(false);
    }

    // Adjacent nodes

    /**
     * Returns the adjacent nodes of this node proxy.
     * 
     * @return the adjacent nodes of this node proxy.
     */
    public Collection<NodeAdapter> adjacentNodes() {
        checkNodeIsNotNull();
        return new java.util.AbstractCollection<NodeAdapter>() {
            @Override
            public int size() {
                return node.getAllOutNeighbors().size();
            }

            @Override
            public Iterator<NodeAdapter> iterator() {
                return new Iterator<NodeAdapter>() {
                    Iterator<Node> i = node.getAllOutNeighbors().iterator();

                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    public NodeAdapter next() {
                        return new NodeAdapter(i.next(), edgeAdapterFactory);
                    }

                    public void remove() {
                        throw new java.lang.UnsupportedOperationException();
                    }
                };
            }
        };
    }

    // Adjacent edges

    /**
     * Returns the edge from this node proxy to the specified target.
     * 
     * @return the edge from this node proxy to the specified target.
     */
    public EdgeAdapter edgeTo(NodeAdapter target) {
        checkNodeIsNotNull();
        return target.edgeFrom(node);
    }

    // Callback

    /**
     * Returns the edge from the specified node to this node proxy.
     * 
     * @return the edge from the specified node to this node proxy.
     */
    EdgeAdapter edgeFrom(Node n) {
        checkNodeIsNotNull();
        assert n != null;
        Collection<Edge> edges = n.getGraph().getEdges(n, node);
        if (edges.isEmpty())
            throw new java.util.NoSuchElementException();
        else if (edges.size() == 1)
            return edgeAdapterFactory.createAdapter(edges.iterator().next());
        else
            throw new AssertionError();
    }

    /**
     * Adds this node proxy to the specified heap.
     * 
     * @param heap
     *            the heap this node proxy is to be added to.
     */
    void addTo(Heap<Node, Float> heap) {
        checkNodeIsNotNull();
        heapEntryAttribute().setValue(heap.add(node, Float.NaN));
    }

    /**
     * Initializes this node proxy.
     * <p>
     * This implementation first removes all attributes found at the location
     * specified by <tt>ROOT_ATTRIBUTE</tt>. Then it adds a
     * <tt>CollectionAttribute</tt> to the path specified by <tt>EMPTY_PATH</tt>
     * . Then it adds three attributes to this collection attribute:
     * <ol>
     * <li>A <tt>NodeAttribute</tt> using the id specified by
     * {@link #PARENT_ATTRIBUTE}. This attribute stores the parent node of this
     * node proxy.
     * <li>A <tt>HeapEntryAttribute</tt> using the id specified by
     * {@link #HEAP_ENTRY_ATTRIBUTE}. This attribute stores the heap entry
     * belonging to this node proxy.
     * <li>A <tt>BooleanAttribute</tt> using the id specified by
     * {@link #SELECTION_FLAG_ATTRIBUTE}. This attribute stores the selection
     * flag of this node proxy.
     * </ol>
     * After setting up all attributes, this node proxy is added to the
     * specified heap and unselected. Its key is set to
     * {@link java.lang.Float#POSITIVE_INFINITY} and its parent is set to
     * <tt>null</tt>.
     * 
     * @see HeapAdapter#add(NodeAdapter)
     * @see #unselect()
     * @see #setKey(float)
     * @see #setParentNode(Node)
     * 
     * @param h
     *            the heap this node is to be added to.
     */
    public void init(HeapAdapter h) {
        try {
            node.removeAttribute(PATH_TO_ROOT_ATTRIBUTE);
        } catch (AttributeNotFoundException ignored) {
        }
        node.addAttribute(new HashMapAttribute(PATH_TO_ROOT_ATTRIBUTE),
                EMPTY_PATH);
        node.addAttribute(new NodeAttribute(PARENT_ATTRIBUTE),
                PATH_TO_ROOT_ATTRIBUTE);
        node.addAttribute(new HeapEntryAttribute(HEAP_ENTRY_ATTRIBUTE),
                PATH_TO_ROOT_ATTRIBUTE);
        node.addAttribute(new BooleanAttribute(SELECTION_FLAG_ATTRIBUTE),
                PATH_TO_ROOT_ATTRIBUTE);
        h.add(this);
        unselect();
        setKey(Float.POSITIVE_INFINITY);
        setParentNode(null);

    }

    /**
     * Clears this node proxy.
     * <p>
     * This implementation removes the <tt>CollectionAttribute</tt> found at
     * {@link #PATH_TO_ROOT_ATTRIBUTE}.
     * 
     */
    public void clear() {
        checkNodeIsNotNull();
        try {
            node.removeAttribute(PATH_TO_ROOT_ATTRIBUTE);
        } catch (AttributeNotFoundException ignored) {
        }
    }

    public boolean equalsNode(Node n) {
        if (n == null)
            return false;
        return n.equals(node);
    }

    /**
     * Returns <tt>true</tt> if this node adapter's adaptee is <tt>null</tt>.
     * 
     * @return <tt>true</tt> if this node adapter's adaptee is <tt>null</tt>.
     * 
     */
    public boolean isNull() {
        return node == null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
