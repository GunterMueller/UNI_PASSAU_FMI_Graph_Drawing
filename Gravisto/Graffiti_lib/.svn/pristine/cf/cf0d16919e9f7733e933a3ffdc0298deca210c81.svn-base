package org.graffiti.plugins.ios.importers.treeml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeLabelAttribute;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class extending the SAX {@link DefaultHandler} in order to parse TreeML
 * files.
 */
public class TreeMLSAXHandler extends DefaultHandler {
    /**
     * The graph into which the tree is to be loaded.
     */
    private Graph graph;

    /**
     * Parsing stack containing the list from the last saved Node to the root
     * Node.
     */
    private LinkedList<Node> nodes;

    /**
     * Contains the declared attributes and their type.
     */
    private Map<String, String> attributes;

    /**
     * Creates a new {@link TreeMLSAXHandler} that loads the parsed treeML tree
     * into the given Graph.
     * 
     * @param graph
     *            The graph into which the tree is to be saved.
     */
    public TreeMLSAXHandler(Graph graph) {
        this.graph = graph;
        this.nodes = new LinkedList<Node>();

        this.attributes = new HashMap<String, String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        if (qName.equals("attributeDecl")) {
            String attributeName = atts.getValue("name");
            if (attributeName == null || attributes.containsKey(attributeName))
                throw new SAXException("invalid or duplicate name "
                        + "in attr declaration");

            String attributeType = atts.getValue("type");
            if (attributeType == null) {
                attributeType = "Int"; // default
            }

            attributes.put(attributeName, attributeType);
        } else if (isNode(qName)) {
            Node newNode = graph.addNode();

            if (!nodes.isEmpty()) {
                Node parentNode = nodes.peekLast();
                graph.addEdge(parentNode, newNode, true);
            }

            String label = atts.getValue("label");
            if (label != null) {
                addLabel(newNode, label);
            }

            nodes.add(newNode);
        } else if (qName.equals("attribute")) {
            String attrName = atts.getValue("name");
            String attrValue = atts.getValue("value");

            if (attrName != null && attrValue != null) {
                Node currentNode = nodes.peekLast();
                addAttribute(currentNode, attrName, attrValue);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (isNode(qName)) {
            nodes.removeLast();
        }
    }

    /**
     * Sets the given attribute value in the given Node. The attribute is parsed
     * according to the type defined in the file. If the attribute has not been
     * declared or the type cannot be parsed according to the given rules, a
     * {@link SAXException} is thrown.
     * 
     * @param node
     *            The Node, whose attribute is to be set.
     * @param attrName
     *            The name of the attribute. This is being used as the path of
     *            the attribute.
     * @param attrValue
     *            The value of the attribute.
     * @throws SAXException
     *             Thrown, if the name of the attribute has not been declared or
     *             the given value cannot be parsed to the declared type.
     */
    private void addAttribute(Node node, String attrName, String attrValue)
            throws SAXException {
        if (!attributes.containsKey(attrName))
            throw new SAXException("attribute name not set");

        String attrType = attributes.get(attrName);

        if (attrType.equals("Int") || attrType.equals("Integer")) {
            try {
                int value = Integer.parseInt(attrValue);
                node.setInteger(attrName, value);
            } catch (NumberFormatException e) {
                throw new SAXException("wrong type in attribute " + attrName);
            }
        } else if (attrType.equals("Long")) {
            try {
                long value = Long.parseLong(attrValue);
                node.setLong(attrName, value);
            } catch (NumberFormatException e) {
                throw new SAXException("wrong type in attribute " + attrName);
            }
        } else if (attrType.equals("Float") || attrType.equals("Real")) {
            try {
                double value = Double.parseDouble(attrValue);
                node.setDouble(attrName, value);
            } catch (NumberFormatException e) {
                throw new SAXException("wrong type in attribute " + attrName);
            }
        } else {
            // attrType = String|Date|Category

            // special case: no label set and attribute "name" is given
            // => use as label
            if (attrName.equals("name")
                    && !node.containsAttribute(GraphicAttributeConstants.LABEL)) {
                // set as label
                addLabel(node, attrValue);
            } else {
                node.setString(attrName, attrValue);
            }
        }

    }

    /**
     * Sets the label of a given Node to a given String.
     * 
     * @param node
     *            The Node, whose label is to be set. The Node must not have an
     *            already set Label.
     * @param label
     *            The String to be set as a label in the given Node.
     */
    private void addLabel(Node node, String label) {
        assert !node.containsAttribute(GraphicAttributeConstants.LABEL);

        NodeLabelAttribute labelAttr = new NodeLabelAttribute(
                GraphicAttributeConstants.LABEL, label);

        node.addAttribute(labelAttr,
                GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);
    }

    /**
     * Returns true, if the given String is either the qualified name of a leaf
     * or a branch.
     * 
     * @param qName
     *            The qualified Name of an attribute.
     * @return <code>true</code> if the given attribute's Name is a Node.
     */
    private boolean isNode(String qName) {
        return (qName.equals("leaf") || qName.equals("branch"));
    }
}
