// =============================================================================
//
//   GrandParentalAttributeHandler.java
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
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class AncestorAttributeHandler<T extends Attribute> extends
        AttributeHandler<T> {
    private int generations;

    public AncestorAttributeHandler(String... postfixes) {
        this(1, postfixes);
    }

    public AncestorAttributeHandler(int generations, String... postfixes) {
        super(postfixes);
        this.generations = generations;
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#preAcceptsAttribute(
     * org.graffiti.graph.Edge, org.graffiti.attributes.Attribute)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected boolean preAcceptsAttribute(Edge edge, Attribute attribute) {
        attribute = walkUp(attribute);
        if (attribute == null)
            return false;
        else {
            try {
                return acceptsAttribute(edge, (T) attribute);
            } catch (ClassCastException e) {
                return false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean preAcceptsAttribute(Node node, Attribute attribute) {
        attribute = walkUp(attribute);
        if (attribute == null)
            return false;
        else {
            try {
                return acceptsAttribute(node, (T) attribute);
            } catch (ClassCastException e) {
                return false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean preAcceptsAttribute(Graph graph, Attribute attribute) {
        attribute = walkUp(attribute);
        if (attribute == null)
            return false;
        else {
            try {
                return acceptsAttribute(graph, (T) attribute);
            } catch (ClassCastException e) {
                return false;
            }
        }
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#preOnAdd(org.graffiti
     * .graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void preOnAdd(Edge edge, Attribute attribute, FastView fastView) {
        // for (int i = 0; i < gen)
        onAdd(edge, (T) walkUp(attribute), fastView);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void preOnAdd(Node node, Attribute attribute, FastView fastView) {
        onAdd(node, (T) walkUp(attribute), fastView);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void preOnAdd(Graph graph, Attribute attribute, FastView fastView) {
        onAdd(graph, (T) walkUp(attribute), fastView);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void preOnChange(Edge edge, Attribute attribute, FastView fastView) {
        onChange(edge, (T) walkUp(attribute), fastView);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void preOnChange(Node node, Attribute attribute, FastView fastView) {
        onChange(node, (T) walkUp(attribute), fastView);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void preOnChange(Graph graph, Attribute attribute,
            FastView fastView) {
        onChange(graph, (T) walkUp(attribute), fastView);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void preOnDelete(Edge edge, Attribute attribute, FastView fastView) {
        onDelete(edge, (T) walkUp(attribute), fastView);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void preOnDelete(Node node, Attribute attribute, FastView fastView) {
        onDelete(node, (T) walkUp(attribute), fastView);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void preOnDelete(Graph graph, Attribute attribute,
            FastView fastView) {
        onDelete(graph, (T) walkUp(attribute), fastView);
    }

    private Attribute walkUp(Attribute attribute) {
        for (int i = 0; i < generations && attribute != null; i++) {
            attribute = attribute.getParent();
        }
        return attribute;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
