// =============================================================================
//
//   GraphChangeListener.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class GraphChangeListener<T extends GraphicsEngine<?, ?>>
        implements CollectionChangeListener<GraphElement> {
    protected T engine;

    protected GraphChangeListener(T engine) {
        this.engine = engine;
    }

    public final void onAdd(GraphElement element) {
        if (element instanceof Node) {
            onAdd((Node) element);
        } else if (element instanceof Edge) {
            onAdd((Edge) element);
        }
    }

    public abstract void onAdd(Node node);

    public abstract void onAdd(Edge edge);

    public final void onRemove(GraphElement element) {
        if (element instanceof Node) {
            onRemove((Node) element);
        } else if (element instanceof Edge) {
            onRemove((Edge) element);
        }
    }

    public abstract void onRemove(Node node);

    public abstract void onRemove(Edge edge);

    public abstract void onClear();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
