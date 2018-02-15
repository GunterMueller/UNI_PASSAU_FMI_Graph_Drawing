package org.graffiti.plugins.scripting.delegates;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

/**
 * Abstract delegate representing a collection of graph elements.
 * 
 * @author Andreas Glei&szlig;ner
 * @scripted A collection of graph elements.
 */
@DocumentedDelegate(DefaultDocumentation.class)
public abstract class CollectionDelegate extends ObjectDelegate {
    private Map<Integer, WeakReference<GraphElementDelegate>> indexMap;

    protected CollectionDelegate(Scope scope) {
        super(scope);
    }

    /**
     * Adds the specified {@code GraphElementDelegate}s.
     * 
     * @param elements
     *            the elements to add.
     * @scripted Adds the specified graph elements.
     */
    @ScriptedMethod
    public final void add(GraphElementDelegate... elements) {
        for (GraphElementDelegate element : elements) {
            if (element instanceof NodeDelegate) {
                addImpl((NodeDelegate) element);
            } else if (element instanceof EdgeDelegate) {
                addImpl((EdgeDelegate) element);
            }
        }
        changed();
    }

    /**
     * Adds all {@code GraphElementDelegate}s of the specified collection.
     * 
     * @param collection
     *            the collection whose elements are added.
     * @scripted Adds all graph elements of the specified collection.
     */
    @ScriptedMethod
    public final void add(CollectionDelegate collection) {
        for (NodeDelegate node : collection.getNodeCollection()) {
            addImpl(node);
        }

        for (EdgeDelegate edge : collection.getEdgeCollection()) {
            addImpl(edge);
        }
        changed();
    }

    protected abstract void addImpl(NodeDelegate node);

    protected abstract void addImpl(EdgeDelegate edge);

    protected void changed() {
    }

    /**
     * Clears this collection.
     * 
     * @scripted Removes all graph elements.
     */
    @ScriptedMethod
    public abstract void clear();

    /**
     * Clones this collection delegate.
     * 
     * @return a clone of this collection delegate.
     * @scripted Clones this collection.
     */
    @Override
    @ScriptedMethod
    public final CollectionDelegate clone() {
        return new BufferedCollectionDelegate(scope, getNodeCollection(),
                getEdgeCollection());
    }

    /**
     * Returns if all of the specified {@code GraphElementDelegate}s are
     * contained in this collection delegate.
     * 
     * @param elements
     *            the elements tested if all of them are contained in this
     *            collection.
     * @return {@code true} if all of the specified {@code GraphElementDelegate}
     *         s are contained in this collection delegate.
     * @scripted Returns if this contains all of the specified graph elements.
     */
    @ScriptedMethod
    public final boolean contains(GraphElementDelegate... elements) {
        for (GraphElementDelegate element : elements) {
            if (element instanceof NodeDelegate) {
                if (!containsImpl((NodeDelegate) element))
                    return false;
            } else if (element instanceof EdgeDelegate) {
                if (!containsImpl((EdgeDelegate) element))
                    return false;
            }
        }
        return true;
    }

    /**
     * Returns if all {@code GraphElementDelegate}s of the specified collection
     * are contained in this collection delegate.
     * 
     * @param collection
     *            the collection whose elements are tested wether they are
     *            contained in this collection delegate.
     * @return {@code true}, if all {@code GraphElementDelegate}s of the
     *         specified collection are contained in this collection delegate.
     * @scripted Returns if this contains all graph elements of the specified
     *           collection.
     */
    @ScriptedMethod
    public final boolean contains(CollectionDelegate collection) {
        for (NodeDelegate node : collection.getNodeCollection()) {
            if (!containsImpl(node))
                return false;
        }

        for (EdgeDelegate edge : collection.getEdgeCollection()) {
            if (!containsImpl(edge))
                return false;
        }

        return true;
    }

    protected abstract boolean containsImpl(NodeDelegate node);

    protected abstract boolean containsImpl(EdgeDelegate edge);

    /**
     * Returns the number of elements in this collection.
     * 
     * @return the number of elements in this collection.
     * @scripted Returns the number of elements in this collection.
     */
    @ScriptedMethod
    public final int count() {
        return size();
    }

    private void fillIndexMap() {
        indexMap = new HashMap<Integer, WeakReference<GraphElementDelegate>>();
        int index = 0;
        for (NodeDelegate node : getNodeCollection()) {
            indexMap.put(index, new WeakReference<GraphElementDelegate>(node));
            index++;
        }
        for (EdgeDelegate edge : getEdgeCollection()) {
            indexMap.put(index, new WeakReference<GraphElementDelegate>(edge));
            index++;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object get(int index) {
        if (indexMap == null) {
            fillIndexMap();
        }
        WeakReference<GraphElementDelegate> ref = indexMap.get(index);
        if (ref == null)
            return ScriptingDelegate.UNDEFINED;
        GraphElementDelegate element = ref.get();
        return element == null ? ScriptingDelegate.UNDEFINED : element;
    }

    protected abstract Collection<NodeDelegate> getNodeCollection();

    protected abstract Collection<EdgeDelegate> getEdgeCollection();

    /**
     * Returns a {@code BufferedCollectionDelegate} containing the {@code
     * NodeDelegate}s of this collection delegate.
     * 
     * @return a {@code BufferedCollectionDelegate} containing the {@code
     *         NodeDelegate}s of this collection delegate.
     * @scripted Returns the nodes of this collection.
     */
    @ScriptedMethod
    public final CollectionDelegate getNodes() {
        return new BufferedCollectionDelegate(scope, getNodeCollection(),
                new LinkedList<EdgeDelegate>());
    }

    /**
     * Returns a {@code BufferedCollectionDelegate} containing the {@code
     * EdgeDelegate}s of this collection delegate.
     * 
     * @return a {@code BufferedCollectionDelegate} containing the {@code
     *         EdgeDelegate}s of this collection delegate.
     * @scripted Returns the edges of this collection.
     */
    @ScriptedMethod
    public final CollectionDelegate getEdges() {
        return new BufferedCollectionDelegate(scope,
                new LinkedList<NodeDelegate>(), getEdgeCollection());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<Integer> getIndices() {
        fillIndexMap();
        return indexMap.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean has(int index) {
        if (indexMap == null) {
            fillIndexMap();
        }
        WeakReference<GraphElementDelegate> ref = indexMap.get(index);
        return ref != null && ref.get() != null;
    }

    /**
     * Returns if this collection delegate contains no {@code
     * GraphElementDelegate}s.
     * 
     * @return {@code true} if this collection delegate contains no {@code
     *         GraphElementDelegate}s.
     * @scripted Returns if this collection contains no elements.
     */
    @ScriptedMethod
    public abstract boolean isEmpty();

    @ScriptedMethod
    public final IteratorDelegate iterator() {
        return new IteratorDelegate(scope, this);
    }

    @ScriptedMethod
    public final void remove(GraphElementDelegate... elements) {
        for (GraphElementDelegate element : elements) {
            if (element instanceof NodeDelegate) {
                removeImpl((NodeDelegate) element);
            } else if (element instanceof EdgeDelegate) {
                removeImpl((EdgeDelegate) element);
            }
        }
        changed();
    }

    @ScriptedMethod
    public final void remove(CollectionDelegate collection) {
        for (NodeDelegate node : collection.getNodeCollection()) {
            removeImpl(node);
        }

        for (EdgeDelegate edge : collection.getEdgeCollection()) {
            removeImpl(edge);
        }
        changed();
    }

    protected abstract void removeImpl(NodeDelegate node);

    protected abstract void removeImpl(EdgeDelegate edge);

    @ScriptedMethod
    public final void set(GraphElementDelegate... elements) {
        clear();
        add(elements);
    }

    @ScriptedMethod
    public final void set(CollectionDelegate collection) {
        clear();
        add(collection);
    }

    @ScriptedMethod
    public abstract int size();

    @Override
    public String toString() {
        return "[Collection size=" + size() + "]";
    }
}
