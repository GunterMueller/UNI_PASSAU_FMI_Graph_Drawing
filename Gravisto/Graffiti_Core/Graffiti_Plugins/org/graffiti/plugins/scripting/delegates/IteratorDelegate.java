package org.graffiti.plugins.scripting.delegates;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

/**
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public final class IteratorDelegate extends ObjectDelegate {
    private Iterator<GraphElementDelegate> iter;
    private CollectionDelegate collectionDelegate;
    private GraphElementDelegate lastReturnedElement;

    protected IteratorDelegate(Scope scope, CollectionDelegate collectionWrapper) {
        super(scope);
        this.collectionDelegate = collectionWrapper;
        Set<GraphElementDelegate> elements = new HashSet<GraphElementDelegate>();
        elements.addAll(collectionWrapper.getNodeCollection());
        elements.addAll(collectionWrapper.getEdgeCollection());
        iter = elements.iterator();
    }

    @ScriptedMethod
    public boolean hasNext() {
        return iter.hasNext();
    }

    @ScriptedMethod
    public GraphElementDelegate next() {
        lastReturnedElement = iter.next();
        return lastReturnedElement;
    }

    @ScriptedMethod
    public void remove() {
        collectionDelegate.remove(lastReturnedElement);
    }

    @Override
    public String toString() {
        return "[Iterator]";
    }
}
