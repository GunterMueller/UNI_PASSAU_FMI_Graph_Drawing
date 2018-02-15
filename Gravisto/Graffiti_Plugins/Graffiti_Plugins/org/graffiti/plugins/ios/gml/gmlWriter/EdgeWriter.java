// =============================================================================
//
//   EdgeWriter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeWriter.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugins.ios.gml.GmlConstants;
import org.graffiti.plugins.ios.gml.attributemapping.AttributeMapping;
import org.graffiti.plugins.ios.gml.attributemapping.EdgeAttributeMapping;

/**
 * Class <code>EdgeWriter</code> is responsible for writing out edges and their
 * associated attributes.
 * 
 * @author ruediger
 */
class EdgeWriter extends AbstractWriter {

    /** The attribute mapping for edges. */
    private AttributeMapping attributeMapping;

    /** The mapping from nodes to ids. */
    private NodeMap nodeMap;

    /**
     * Constructs a new <code>EdgeWriter</code>.
     * 
     * @param os
     *            the output stream to write the edges to.
     * @param nodeMap
     *            the mapping from nodes to ids.
     */
    public EdgeWriter(OutputStream os, NodeMap nodeMap) {
        super(os);
        this.nodeMap = nodeMap;
        this.attributeMapping = new EdgeAttributeMapping();
    }

    /**
     * Writes the given edge to the output steam using the given indentation
     * level.
     * 
     * @param edge
     *            the edge to be written.
     * @param indent
     *            the indentation level.
     * 
     * @throws IOException
     *             if an error occurrs while writing out the edge.
     */
    public void write(Edge edge, int indent) throws IOException {
        Node source = edge.getSource();
        int sourceId = nodeMap.getId(source);
        Node target = edge.getTarget();
        int targetId = nodeMap.getId(target);
        indent(indent);
        os
                .write(new String(GraphicAttributeConstants.EDGE + " [\n")
                        .getBytes());
        indent(indent + OFFSET);
        os.write(new String(GraphicAttributeConstants.SOURCE + " " + sourceId
                + "\n").getBytes());
        indent(indent + OFFSET);
        os.write(new String(GraphicAttributeConstants.TARGET + " " + targetId
                + "\n").getBytes());

        // write attributes

        /*
         * two bends have to be added to every edge. the first edge has the same
         * coordinates as the source node, the second has the target's
         * coordinates. This has to be done because of Graphlet behaviour here,
         * which does the same. The two added bends are ignored when loading the
         * graph.
         */

        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        SortedCollectionAttribute tempBends = new LinkedHashMapAttribute(
                GraphicAttributeConstants.BENDS);
        SortedCollectionAttribute realBends = (SortedCollectionAttribute) ega
                .getBends().copy();
        Collection<Attribute> values = realBends.getCollection().values();

        CoordinateAttribute caSource = (CoordinateAttribute) edge.getSource()
                .getAttribute(
                        GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
        CoordinateAttribute caTarget = (CoordinateAttribute) edge.getTarget()
                .getAttribute(
                        GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
        tempBends.add(new CoordinateAttribute(GraphicAttributeConstants.BEND
                + "0", caSource.getX(), caSource.getY()));
        int counter = 1;
        for (Attribute attribute : values) {
            CoordinateAttribute cat = (CoordinateAttribute) attribute;
            tempBends.add(new CoordinateAttribute(
                    GraphicAttributeConstants.BEND + counter, cat.getX(), cat
                            .getY()));
            counter++;
        }
        tempBends.add(new CoordinateAttribute(GraphicAttributeConstants.BEND
                + counter, caTarget.getX() - 1, caTarget.getY() - 1));
        ega.setBends(tempBends);
        writeAttributes(edge.getAttributes(), indent + OFFSET);
        ega.setBends(realBends);
        indent(indent);
        os.write(new String("]\n").getBytes());
    }

    /*
     * 
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
            String shape = attr.getValue().toString();

            if (shape.endsWith(GraphicAttributeConstants.QUAD_CURVE_EDGE_SHAPE)
                    || shape
                            .endsWith(GraphicAttributeConstants.SMOOTH_LINE_EDGE_SHAPE))
                return "1";
            else
                return "0";
        } else if (path.equals(GmlConstants.LABEL_TEXTCOLOR_PATH))
            return getColor(attr);
        else if (path.equals(GmlConstants.LABEL_LABEL_PATH)) {
            try {
                LabelAttribute l = (LabelAttribute) attr.getParent();

                return GmlConstants.GML_QUOTES + l.getLabel()
                        + GmlConstants.GML_QUOTES;
            } catch (ClassCastException cce) {
                String value = attr.getValue().toString();

                return GmlConstants.GML_QUOTES + value
                        + GmlConstants.GML_QUOTES;
            }
        } else if (path.equals(GmlConstants.WEIGHT_LABEL_PATH)) {
            try {
                LabelAttribute l = (LabelAttribute) attr.getParent();

                return GmlConstants.GML_QUOTES + l.getLabel()
                        + GmlConstants.GML_QUOTES;
            } catch (ClassCastException cce) {
                String value = attr.getValue().toString();

                return GmlConstants.GML_QUOTES + value
                        + GmlConstants.GML_QUOTES;
            }
        } else if (path.equals(GmlConstants.CAPACITY_LABEL_PATH)) {
            try {
                LabelAttribute l = (LabelAttribute) attr.getParent();

                return GmlConstants.GML_QUOTES + l.getLabel()
                        + GmlConstants.GML_QUOTES;
            } catch (ClassCastException cce) {
                String value = attr.getValue().toString();

                return GmlConstants.GML_QUOTES + value
                        + GmlConstants.GML_QUOTES;
            }
        } else
            throw new NoSpecialValueException("no special treatment for "
                    + "attribute at path \"" + attr.getPath() + "\" available.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
