// =============================================================================
//
//   AttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class AttributeHandler<T extends Attribute> implements
        GraphicAttributeConstants {
    private static Map<String, LinkedList<AttributeHandler<?>>> handlers = new HashMap<String, LinkedList<AttributeHandler<?>>>();
    private static Map<String, LinkedList<AttributeHandler<?>>> prefixHandlers = new HashMap<String, LinkedList<AttributeHandler<?>>>();
    private static Map<Class<? extends Attribute>, LinkedList<AttributeHandler<?>>> typeHandlers = new HashMap<Class<? extends Attribute>, LinkedList<AttributeHandler<?>>>();
    private static AttributeHandler<Attribute> unknownAttributeHandler;

    static {
        // There is no proper way of finding all subclasses of
        // AttributeHandler, so manually force them to load.
        new UnknownAttributeHandler();

        new BendsAttributeHandler();
        new CoordinateAttributeHandler();
        new DepthAttributeHandler();
        new DimensionAttributeHandler();
        new DockingAttributeHandler();
        new EdgeLabelAlignmentAttributeHandler();
        new EdgeLabelAttributeHandler();
        new EdgeLabelFormatHandler();
        new EdgeLabelPositionAttributeHandler();
        new EdgeLabelTextcolorAttributeHandler();
        new EdgeShapeAttributeHandler();
        new FillColorAttributeHandler();
        new FrameColorAttributeHandler();
        new FrameThicknessAttributeHandler();
        ImageAttributeHandler imageAttributeHandler = new ImageAttributeHandler();
        new RenderedImageAttributeHandler(imageAttributeHandler);
        new LineModeAttributeHandler();
        new NodeLabelAlignmentAttributeHandler();
        new NodeLabelAttributeHandler();
        new NodeLabelFormatAttributeHandler();
        new NodeLabelPositionAttributeHandler();
        new NodeLabelFormatAttributeHandler();
        new NodeLabelTextcolorAttributeHandler();
        new ShapeAttributeHandler();
        new ThicknessAttributeHandler();

        new GridAttributeHandler();
    }

    public static void onAdd(Attribute attribute, FastView fastView) {
        Attributable attributable = attribute.getAttributable();
        if (attributable instanceof Node) {
            getHandler(attributable, attribute).preOnAdd((Node) attributable,
                    attribute, fastView);
        } else if (attributable instanceof Edge) {
            getHandler(attributable, attribute).preOnAdd((Edge) attributable,
                    attribute, fastView);
        } else if (attributable instanceof Graph) {
            // System.out.println("ADD: " + attribute.getPath() + "(" +
            // attribute.getClass().getCanonicalName() + ")");//TODO:
            getHandler(attributable, attribute).preOnAdd((Graph) attributable,
                    attribute, fastView);
        }
    }

    public static void onChange(Attribute attribute, FastView fastView) {
        Attributable attributable = attribute.getAttributable();
        if (attributable instanceof Node) {
            getHandler(attributable, attribute).preOnChange(
                    (Node) attributable, attribute, fastView);
        } else if (attributable instanceof Edge) {
            getHandler(attributable, attribute).preOnChange(
                    (Edge) attributable, attribute, fastView);
        } else if (attributable instanceof Graph) {
            // System.out.println("CHG: " + attribute.getPath() + "(" +
            // attribute.getClass().getCanonicalName() + ")");//TODO:
            getHandler(attributable, attribute).preOnChange(
                    (Graph) attributable, attribute, fastView);
        }
    }

    public static void onDelete(Attribute attribute, FastView fastView) {
        Attributable attributable = attribute.getAttributable();
        if (attributable instanceof Node) {
            getHandler(attributable, attribute).preOnDelete(
                    (Node) attributable, attribute, fastView);
        } else if (attributable instanceof Edge) {
            getHandler(attributable, attribute).preOnDelete(
                    (Edge) attributable, attribute, fastView);
        } else if (attributable instanceof Graph) {
            // System.out.println("DEL: " + attribute.getPath() + "(" +
            // attribute.getClass().getCanonicalName() + ")");//TODO:
            getHandler(attributable, attribute).preOnDelete(
                    (Graph) attributable, attribute, fastView);
        }
    }

    public static void triggerAll(Attributable attributable, FastView fastView) {
        triggerAll(attributable, attributable.getAttributes(), fastView,
                Attribute.class);
    }

    public static void triggerAll(Attributable attributable, FastView fastView,
            Class<? extends Attribute> attributeClass) {
        triggerAll(attributable, attributable.getAttributes(), fastView,
                attributeClass);
    }

    protected static void triggerAll(Attributable attributable,
            CollectionAttribute collectionAttribute, FastView fastView,
            Class<? extends Attribute> attributeClass) {
        for (Attribute attribute : collectionAttribute.getCollection().values()) {
            if (attribute instanceof CollectionAttribute) {
                triggerAll(attributable, (CollectionAttribute) attribute,
                        fastView, attributeClass);
            }
            if (attributeClass.isAssignableFrom(attribute.getClass())) {
                onChange(attribute, fastView);
            }
        }
    }

    protected static boolean equalsPath(Attribute attribute, String path) {
        String attributePath = attribute.getPath();
        return attributePath.equals(Attribute.SEPARATOR + path)
                || attribute.equals(path);
    }

    private static AttributeHandler<?> getHandler(Attributable attributable,
            Attribute attribute) {
        AttributeHandler<?> handler = getHandler(attributable, attribute,
                handlers.get(getPostfix(attribute)));
        if (handler != null)
            return handler;
        handler = getPrefixHandler(attributable, attribute);
        if (handler != null)
            return handler;
        handler = getHandler(attributable, attribute, typeHandlers
                .get(attribute.getClass()));
        if (handler != null)
            return handler;
        return unknownAttributeHandler;
    }

    private static AttributeHandler<?> getHandler(Attributable attributable,
            Attribute attribute, LinkedList<AttributeHandler<?>> handlerList) {
        if (handlerList != null) {
            for (AttributeHandler<?> handler : handlerList) {
                if (((attributable instanceof Node) && handler
                        .preAcceptsAttribute((Node) attributable, attribute))
                        || ((attributable instanceof Edge) && handler
                                .preAcceptsAttribute((Edge) attributable,
                                        attribute))
                        || ((attributable instanceof Graph) && handler
                                .preAcceptsAttribute((Graph) attributable,
                                        attribute)))
                    return handler;
            }
        }
        return null;
    }

    private static AttributeHandler<?> getPrefixHandler(
            Attributable attributable, Attribute attribute) {
        String path = attribute.getPath();
        if (path.startsWith(".")) {
            path = path.substring(1);
        }
        for (Map.Entry<String, LinkedList<AttributeHandler<?>>> entry : prefixHandlers
                .entrySet()) {
            String key = entry.getKey();
            if (path.startsWith(key)) {
                AttributeHandler<?> handler = getHandler(attributable,
                        attribute, entry.getValue());
                if (handler != null)
                    return handler;
            }
        }
        return null;
    }

    private static String getPostfix(Attribute attribute) {
        String path = attribute.getPath();
        int index = path.lastIndexOf('.');
        if (index == -1)
            return "";
        else
            return path.substring(index + 1);
    }

    @SuppressWarnings("unchecked")
    protected AttributeHandler(boolean dummy) {
        unknownAttributeHandler = (AttributeHandler<Attribute>) this;
    }

    protected AttributeHandler(String... postfixes) {
        this(true, postfixes);
    }

    protected AttributeHandler(Class<? extends Attribute> type,
            String... postfixes) {
        this(postfixes);
        LinkedList<AttributeHandler<?>> handlerList = typeHandlers.get(type);
        if (handlerList == null) {
            handlerList = new LinkedList<AttributeHandler<?>>();
            typeHandlers.put(type, handlerList);
        }
        handlerList.add(this);
    }

    protected AttributeHandler(boolean isPostfix, String... fixes) {
        Map<String, LinkedList<AttributeHandler<?>>> ha = isPostfix ? handlers
                : prefixHandlers;
        for (String postfix : fixes) {
            LinkedList<AttributeHandler<?>> handlerList = ha.get(postfix);
            if (handlerList == null) {
                handlerList = new LinkedList<AttributeHandler<?>>();
                ha.put(postfix, handlerList);
            }
            handlerList.add(this);
        }
    }

    public void triggerAllAccepted(Attributable attributable,
            FastView fastView, Class<T> attributeClass) {
        triggerAllAccepted(attributable, attributable.getAttributes(),
                fastView, attributeClass);
    }

    @SuppressWarnings("unchecked")
    private void triggerAllAccepted(Attributable attributable,
            CollectionAttribute collectionAttribute, FastView fastView,
            Class<T> attributeClass) {
        for (Attribute attribute : collectionAttribute.getCollection().values()) {
            if (attribute instanceof CollectionAttribute) {
                triggerAllAccepted(attributable,
                        (CollectionAttribute) attribute, fastView,
                        attributeClass);
            }
            if (attributeClass.isAssignableFrom(attribute.getClass())) {
                if (attributable instanceof Node) {
                    triggerIfAccepted((Node) attributable, (T) attribute,
                            fastView);
                } else if (attributable instanceof Edge) {
                    triggerIfAccepted((Edge) attributable, (T) attribute,
                            fastView);
                } else if (attributable instanceof Graph) {
                    triggerIfAccepted((Graph) attributable, (T) attribute,
                            fastView);
                }
            }
        }
    }

    private void triggerIfAccepted(Node node, T attribute, FastView fastView) {
        if (acceptsAttribute(node, attribute)) {
            onChange(node, attribute, fastView);
        }
    }

    private void triggerIfAccepted(Edge edge, T attribute, FastView fastView) {
        if (acceptsAttribute(edge, attribute)) {
            onChange(edge, attribute, fastView);
        }
    }

    private void triggerIfAccepted(Graph graph, T attribute, FastView fastView) {
        if (acceptsAttribute(graph, attribute)) {
            onChange(graph, attribute, fastView);
        }
    }

    protected boolean acceptsAttribute(Node node, T attribute) {
        return false;
    }

    protected boolean acceptsAttribute(Edge edge, T attribute) {
        return false;
    }

    protected boolean acceptsAttribute(Graph graph, T attribute) {
        return false;
    }

    protected void onAdd(Node node, T attribute, FastView fastView) {
    }

    protected void onAdd(Edge edge, T attribute, FastView fastView) {
    }

    protected void onAdd(Graph graph, T attribute, FastView fastView) {
    }

    protected void onChange(Node node, T attribute, FastView fastView) {
    }

    protected void onChange(Edge edge, T attribute, FastView fastView) {
    }

    protected void onChange(Graph graph, T attribute, FastView fastView) {
    }

    protected void onDelete(Node node, T attribute, FastView fastView) {
    }

    protected void onDelete(Edge edge, T attribute, FastView fastView) {
    }

    protected void onDelete(Graph graph, T attribute, FastView fastView) {
    }

    @SuppressWarnings("unchecked")
    protected boolean preAcceptsAttribute(Node node, Attribute attribute) {
        try {
            return acceptsAttribute(node, (T) attribute);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    protected boolean preAcceptsAttribute(Edge edge, Attribute attribute) {
        try {
            return acceptsAttribute(edge, (T) attribute);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    protected boolean preAcceptsAttribute(Graph graph, Attribute attribute) {
        try {
            return acceptsAttribute(graph, (T) attribute);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    protected void preOnAdd(Node node, Attribute attribute, FastView fastView) {
        onAdd(node, (T) attribute, fastView);
    }

    @SuppressWarnings("unchecked")
    protected void preOnAdd(Edge edge, Attribute attribute, FastView fastView) {
        onAdd(edge, (T) attribute, fastView);
    }

    @SuppressWarnings("unchecked")
    protected void preOnAdd(Graph graph, Attribute attribute, FastView fastView) {
        onAdd(graph, (T) attribute, fastView);
    }

    @SuppressWarnings("unchecked")
    protected void preOnChange(Node node, Attribute attribute, FastView fastView) {
        onChange(node, (T) attribute, fastView);
    }

    @SuppressWarnings("unchecked")
    protected void preOnChange(Edge edge, Attribute attribute, FastView fastView) {
        onChange(edge, (T) attribute, fastView);
    }

    @SuppressWarnings("unchecked")
    protected void preOnChange(Graph graph, Attribute attribute,
            FastView fastView) {
        onChange(graph, (T) attribute, fastView);
    }

    @SuppressWarnings("unchecked")
    protected void preOnDelete(Node node, Attribute attribute, FastView fastView) {
        onDelete(node, (T) attribute, fastView);
    }

    @SuppressWarnings("unchecked")
    protected void preOnDelete(Edge edge, Attribute attribute, FastView fastView) {
        onDelete(edge, (T) attribute, fastView);
    }

    @SuppressWarnings("unchecked")
    protected void preOnDelete(Graph graph, Attribute attribute,
            FastView fastView) {
        onDelete(graph, (T) attribute, fastView);
    }

    protected final String getNormalizedPath(String string) {
        if (string.charAt(0) == '.')
            return string.substring(1);
        else
            return string;
    }

    protected final String getNormalizedPath(Attribute attribute) {
        return getNormalizedPath(attribute.getPath());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
