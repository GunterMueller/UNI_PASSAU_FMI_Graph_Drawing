// =============================================================================
//
//   NodeWriter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeWriter.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugins.ios.gml.GmlConstants;
import org.graffiti.plugins.ios.gml.attributemapping.AttributeMapping;
import org.graffiti.plugins.ios.gml.attributemapping.NodeAttributeMapping;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Class <code>NodeWriter</code> writes nodes and their attributes.
 * 
 * @author ruediger
 */
class NodeWriter extends AbstractWriter {

    /** The logger for this class. */
    private static final Logger logger = Logger.getLogger(NodeWriter.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The attribute mapping for nodes. */
    private AttributeMapping attributeMapping;

    /** A mapping from nodes to (generated) ids. */
    private NodeMap nodeMap;

    /**
     * Constucts a new <code>NodeWriter</code>.
     * 
     * @param os
     *            the output stream to write the nodes to.
     * @param nodeMap
     *            the mapping from nodes to ids.
     */
    public NodeWriter(OutputStream os, NodeMap nodeMap) {
        super(os);
        this.nodeMap = nodeMap;
        this.attributeMapping = new NodeAttributeMapping();
    }

    /**
     * Writes the given node to the output stream.
     * 
     * @param node
     *            the node to be written.
     * @param indent
     *            the level of indentation.
     * 
     * @throws IOException
     *             if an error occurrs while writing the node.
     */
    public void write(Node node, int indent) throws IOException {
        int id = this.nodeMap.add(node);
        indent(indent);
        os
                .write(new String(GraphicAttributeConstants.NODE + " [\n")
                        .getBytes());
        indent(indent + OFFSET);
        os.write(new String(GmlConstants.ID + " " + id + "\n").getBytes());

        // write attributes
        writeAttributes(node.getAttributes(), indent + OFFSET);

        indent(indent);
        os.write(new String("]\n").getBytes());
    }

    /**
     * Returns the attribute mapping.
     * 
     * @return the attribute mapping.
     */
    @Override
    AttributeMapping getMapping() {
        return this.attributeMapping;
    }

    /**
     * Returns a String representation of the value of the specified attribute
     * which is not the trivial representation that
     * <code>attr.getValue().toString()</code> would return.
     * 
     * @param attr
     *            the attribute of which to compute the non-trivial string
     *            representation.
     * 
     * @return a non-trivial string represenation of the specified attribute.
     * 
     * @throws NoSpecialValueException
     *             if there is no handling provided for this attribute.
     */
    @Override
    String getSpecialValue(Attribute attr) throws NoSpecialValueException {
        String path = attr.getPath();

        if (path.equals(GmlConstants.FILLCOLOR_PATH))
            return getColor(attr);
        else if (path.equals(GmlConstants.FRAMECOLOR_PATH))
            return getColor(attr);
        else if (path.equals(GmlConstants.BACKGROUND_PATH))
            return getColor(attr);
        else if (path.equals(GmlConstants.FOREGROUND_PATH))
            return getColor(attr);
        else if (path.equals(GmlConstants.SHAPE_PATH)) {
            String shape = attr.getValue().toString().toLowerCase();

            if ((shape.indexOf("rectangle") > 0)
                    || (shape.indexOf("rectangular") > 0))
                return "\"rectangle\"";
            else if (shape.indexOf("circle") > 0)
                return "\"oval\"";
            else if (shape.indexOf("circular") > 0)
                return "\"circular\"";
            else if (shape.indexOf("ellipse") > 0)
                return "\"oval\"";
            else {
                logger.warning("No corresponding node shape available - "
                        + "using rectangle.");

                return "\"rectangle\"";
            }
        } else if (path.equals(GmlConstants.LABEL_TEXTCOLOR_PATH))
            return getColor(attr);
        else if (path.equals(GmlConstants.LABEL_LABEL_PATH)
                || path.equals(".label0.label")
                || path.equals(GmlConstants.WEIGHT_LABEL_PATH)
                || path.equals(GmlConstants.CAPACITY_LABEL_PATH)) {
            try {
                LabelAttribute l = (LabelAttribute) attr.getParent();

                return GmlConstants.GML_QUOTES + l.getLabel()
                        + GmlConstants.GML_QUOTES;
            } catch (ClassCastException cce) {
                String value = attr.getValue().toString();

                return GmlConstants.GML_QUOTES + value
                        + GmlConstants.GML_QUOTES;
            }
        }

        throw new NoSpecialValueException("no special treatment for attribute "
                + "at path \"" + attr.getPath() + "\" available.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
