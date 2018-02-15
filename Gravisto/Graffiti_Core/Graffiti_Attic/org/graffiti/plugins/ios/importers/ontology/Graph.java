package org.graffiti.plugins.ios.importers.ontology;

import java.util.NoSuchElementException;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.util.ext.HashMap;
import org.graffiti.util.ext.Map;

/**
 * Adapter class to provide a simplified interface for
 * {@link org.graffiti.graph.Graph}.
 * 
 * @author Harald Frankenberger
 */
public class Graph {

    private final org.graffiti.graph.Graph graph;

    private final Map<Object, Node> nodeCache;

    /**
     * Adapts the given graph.
     * 
     * @param g
     *            the graph to adapt.
     */
    public Graph(org.graffiti.graph.Graph g) {
        graph = g;
        nodeCache = new HashMap<Object, Node>();
    }

    /**
     * Adapts a {@link org.graffiti.graph.AdjListGraph}.
     */
    public Graph() {
        this(new AdjListGraph());
    }

    /* Nodes */

    /**
     * Adds a node with the given id and label to the adapted graph, if it does
     * not already exist.
     * 
     * @param id
     *            the id of the new node.
     * @param label
     *            the label of the new node.
     */
    public void addNode(Object id, String label) {
        if (containsNode(id))
            return;
        Node node = graph.addNode();
        node.addAttribute(new NodeLabelAttribute("label", label), "");
        NodeGraphicAttribute nodeGraphicAttribute = new NodeGraphicAttribute(
                GraphicAttributeConstants.GRAPHICS);
        double width = computeWidthFor(label);
        nodeGraphicAttribute.getDimension().setWidth(width);
        node.addAttribute(nodeGraphicAttribute, "");
        node.addAttribute(new ClassIdAttribute("classId", id), "");
    }

    private double computeWidthFor(String label) {
        return label.length() * 11;
    }

    /**
     * Returns <tt>true</tt> if the adapted graph contains a node with the given
     * id.
     * 
     * @return <tt>true</tt> if the adapted graph contains a node with the given
     *         id.
     */
    public boolean containsNode(Object id) {
        return findNode(id) != null;
    }

    /**
     * Returns the adapted graph's number of nodes.
     * 
     * @return the adapted graph's number of nodes.
     */
    public int nodeCount() {
        return graph.getNumberOfNodes();
    }

    /* Edges */

    /**
     * Adds an edge to the adapted graph if it does not already exist.
     * 
     * @param source
     *            the new edge's source node
     * @param id
     *            the new edge's id
     * @param label
     *            the new edge's label
     * @param target
     *            the new edge's target node
     */
    public void addEdge(Object source, String id, String label, Object target) {
        if (!(containsNode(source) && containsNode(target)))
            return;
        if (containsEdge(id)) {
            removeEdge(id);
        }
        boolean isSuperClassProperty = false;
        String propertyId = id;
        addEdge(propertyId, findNode(source), label, findNode(target),
                isSuperClassProperty);
    }

    /**
     * Removes the edge with the given id from the adapted graph.
     * 
     * @param id
     *            the id of the edge to be removed
     */
    public void removeEdge(String id) {
        graph.deleteEdge(findEdge(id));
    }

    /**
     * Returns <tt>true</tt> if the adapted graph contains an edge with the
     * given id.
     * 
     * @return <tt>true</tt> if the adapted graph contains an edge with the
     *         given id.
     */
    public boolean containsEdge(String id) {
        return findEdge(id) != null;
    }

    /**
     * Returns the edge with the given id.
     * 
     * @return the edge with the given id.
     * @throws NoSuchElementException
     *             if no such edge exists.
     */
    public Edge edge(String id) {
        if (!containsEdge(id))
            throw new NoSuchElementException();
        return findEdge(id);
    }

    /**
     * Returns the adapted graph's number of edges.
     * 
     * @return the adapted graph's number of edges.
     */
    public int edgeCount() {
        return graph.getNumberOfEdges();
    }

    /* Super-class-edges */

    /**
     * Adds a super class edge to the adapted graph if does not already exist.
     * Note that the current implementation adds an edge <i>from the
     * super-class-node to the sub-class node</i>. This guarantees to some
     * degree acceptable drawing results with Sugiyama's algorithm.
     * 
     * @param subClass
     *            the target node of the new super-class-edge.
     * @param superClass
     *            the source node of the new super-class-edge.
     */
    public void addSuperClassEdge(Object subClass, Object superClass) {
        if (!containsNode(subClass) || !containsNode(superClass))
            return;
        if (containsSuperClassEdge(subClass, superClass))
            return;
        Node source = findNode(superClass);
        Node target = findNode(subClass);
        boolean isSuperClassProperty = true;
        String propertyId = new Object().toString();
        String label = "is-a";
        addEdge(propertyId, source, label, target, isSuperClassProperty);
    }

    /**
     * Returns <tt>true</tt> if the adapted graph contains the given
     * super-class-edge.
     * 
     * See {@link #addSuperClassEdge(Object, Object)} for details about the
     * current representation of super-class-edges.
     * 
     * @param subClass
     *            the target node of the super-class-edge.
     * @param superClass
     *            the source node of the super-class-edge.
     */
    public boolean containsSuperClassEdge(Object subClass, Object superClass) {
        return findSuperClassEdge(subClass, superClass) != null;
    }

    /**
     * Returns the super-class-edge matching the given parameters.
     * 
     * See {@link #addSuperClassEdge(Object, Object)} for details about the
     * current representation of super-class-edges.
     * 
     * @return the super-class-edge matching the given parameters.
     * @throws NoSuchElementException
     *             if no such edge exists.
     */
    public Edge superClassEdge(Object subClass, Object superClass) {
        if (!containsSuperClassEdge(subClass, superClass))
            throw new NoSuchElementException();
        return findSuperClassEdge(subClass, superClass);
    }

    /* Helper methods */

    private void addEdge(String propertyId, Node source, String label,
            Node target, boolean isSuperClassProperty) {
        Edge newEdge = graph.addEdge(source, target, true);
        newEdge.addAttribute(new BooleanAttribute("isSuperClassProperty",
                isSuperClassProperty), "");
        newEdge.addAttribute(new StringAttribute("propertyId", propertyId), "");
        // newEdge.addAttribute(new EdgeLabelAttribute("label", label), "");
        EdgeGraphicAttribute edgeGraphicAttribute = new EdgeGraphicAttribute(
                true);
        if (isSuperClassProperty) {
            edgeGraphicAttribute.setFrameThickness(3.0);
        }
        newEdge.addAttribute(edgeGraphicAttribute, "");
    }

    private Edge findSuperClassEdge(Object subClass, Object superClass) {
        Edge superClassEdge = null;
        for (Edge edge : graph.getEdges()) {
            try {
                Boolean isSuperClassProperty = ((BooleanAttribute) edge
                        .getAttribute("isSuperClassProperty")).getBoolean();
                if (isSuperClassProperty) {
                    Node source = edge.getSource();
                    Node target = edge.getTarget();
                    try {
                        Object sourceId = ((ClassIdAttribute) source
                                .getAttribute("classId")).getValue();
                        Object targetId = ((ClassIdAttribute) target
                                .getAttribute("classId")).getValue();
                        if (subClass.equals(targetId)
                                && superClass.equals(sourceId)) {
                            superClassEdge = edge;
                        }
                    } catch (AttributeNotFoundException e) {
                    }
                }
            } catch (AttributeNotFoundException e) {
            }
        }
        return superClassEdge;
    }

    private Node findNode(Object s) {
        if (nodeCache.containsKey(s))
            return nodeCache.get(s);
        for (Node node : graph.getNodes()) {
            try {
                Object classId = ((ClassIdAttribute) node
                        .getAttribute("classId")).getValue();
                if (s.equals(classId)) {
                    nodeCache.put(s, node);
                    return node;
                }
            } catch (AttributeNotFoundException e) {
            }
        }
        return null;
    }

    private Edge findEdge(String id) {
        Edge found = null;
        for (Edge edge : graph.getEdges()) {
            try {
                String propertyId = ((StringAttribute) edge
                        .getAttribute("propertyId")).getString();
                if (propertyId.equals(id)) {
                    found = edge;
                }
            } catch (AttributeNotFoundException e) {
            }
        }
        return found;
    }

    private class ClassIdAttribute extends StringAttribute {
        private static final String NO_STRING = "___NO_STRING___";

        private Object classId;

        public ClassIdAttribute(String id, Object value) {
            super(id);
            setValue(value);
        }

        @Override
        public String getString() {
            String s = super.getString();
            if (NO_STRING.equals(s))
                return classId.toString();
            else
                return s;
        }

        @Override
        public Object getValue() {
            Object v = super.getValue();
            if (NO_STRING.equals(v))
                return classId;
            else
                return v;
        }

        @Override
        public void setDefaultValue() {
            super.setDefaultValue();
            super.setValue(NO_STRING);
            classId = new Object();
        }

        @Override
        public void setValue(Object v) throws IllegalArgumentException {
            if (v instanceof String) {
                super.setValue(v);
                classId = v;
            } else {
                super.setValue(v);
                super.setValue(NO_STRING);
                classId = v;
            }
        }

        @Override
        public void setString(String value) {
            if (NO_STRING.equals(value)) {
                super.setString(value);
                classId = new Object();
            } else {
                super.setString(value);
                classId = value;
            }
        }
    }
}
