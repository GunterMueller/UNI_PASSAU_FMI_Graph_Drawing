// =============================================================================
//
//   UnknownAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class UnknownAttributeHandler extends AttributeHandler<Attribute> {
    public UnknownAttributeHandler() {
        super(true);
    }

    /**
     * {@inheritDoc} This implementation returns {@code true}.
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, Attribute attribute) {
        return true;
    }

    /**
     * {@inheritDoc} This implementation returns {@code true}.
     */
    @Override
    protected boolean acceptsAttribute(Node node, Attribute attribute) {
        return true;
    }

    /**
     * {@inheritDoc} This implementation returns {@code true}.
     */
    @Override
    protected boolean acceptsAttribute(Graph graph, Attribute attribute) {
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
