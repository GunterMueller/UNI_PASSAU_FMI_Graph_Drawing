// =============================================================================
//
//   NodeTransformer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeTransformer.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.transform;

import java.util.logging.Logger;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;
import org.graffiti.graphics.PositionAttribute;
import org.graffiti.plugins.ios.gml.GmlConstants;
import org.graffiti.plugins.ios.gml.attributemapping.AbstractAttributeMapping;
import org.graffiti.plugins.ios.gml.attributemapping.AttributeMapping;
import org.graffiti.plugins.ios.gml.attributemapping.NodeAttributeMapping;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlKey;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlList;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlNode;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlNodeStyle;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlString;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlValue;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * This class contains the code for transforming nodes specified in the GML
 * format into the representation of graffiti.
 * 
 * @author ruediger
 */
class NodeTransformer extends GraphElementTransformer {

    /** The logger for development purposes. */
    private static final Logger logger = Logger.getLogger(NodeTransformer.class
            .getName());

    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The mapping of GML to graffiti attribute paths. */
    private NodeAttributeMapping attributeMap;

    /**
     * Creates a new <code>NodeTransformer</code>.
     * 
     * @param style
     *            the style to be applied to every new node.
     * 
     * @throws GmlToGraffitiException
     *             if something fails during the transformation from GML to
     *             graffiti.
     */
    NodeTransformer(GmlNodeStyle style) throws GmlToGraffitiException {
        super(style);
        this.attributeMap = new NodeAttributeMapping();
    }

    /**
     * Returns a <code>PositionAttribute</code> which contains the information
     * of where to place labels at nodes.
     * 
     * @return a <code>PositionAttribute</code> which contains the information
     *         of where to place labels for nodes.
     */
    @Override
    protected PositionAttribute getPositionAttribute() {
        return new NodeLabelPositionAttribute("position");
    }

    /**
     * Creates a special attribute from a GML attribute declaration that is
     * specific, i.e. individual, to nodes.
     * 
     * @param attbl
     *            the <code>Attributable</code> to which to add the attributes.
     * @param path
     *            the path at which to add the attribute.
     * @param key
     *            the GML attribute declaration.
     */
    @Override
    protected void createIndividualSpecialAttribute(Attributable attbl,
            String path, GmlKey key) {
        logger
                .info("creating special node attribute at path \"" + path
                        + "\".");

        if (path.equals(GmlConstants.SHAPE_PATH)) {
            assert key.getValue().isString();

            GmlString gs = (GmlString) key.getValue();
            String val = (String) gs.getValue();
            val = val.toLowerCase();

            if ((val.equals("rectangle")) || (val.equals("rectangular"))) {
                attbl.setString(GmlConstants.SHAPE_PATH,
                        GraphicAttributeConstants.RECTANGLE_CLASSNAME);
            } else if (val.equals("circle")) {
                attbl.setString(GmlConstants.SHAPE_PATH,
                        GraphicAttributeConstants.CIRCLE_CLASSNAME);
            } else if (val.equals("circular") || val.equals("oval")) {
                attbl.setString(GmlConstants.SHAPE_PATH,
                        GraphicAttributeConstants.ELLIPSE_CLASSNAME);
            }

            // polygonal node shape:
            // (org.graffiti.plugins.views.defaults.PolygonalNodeShape)
            else {
                logger.warning("No corresponding node shape available - "
                        + "using rectangle.");

                // set corresponding shape available - take rectangle
                attbl.setString(GmlConstants.SHAPE_PATH,
                        GraphicAttributeConstants.RECTANGLE_CLASSNAME);
            }
        }
    }

    /*
     * 
     */
    @Override
    AbstractAttributeMapping getAttributeMap() {
        return this.attributeMap;
    }

    /**
     * Returns <code>true</code> if the specified GML path contains an ignorable
     * attribute, <code>false</code> otherwise.
     * 
     * @param path
     *            the GML path to be checked for ignorability.
     * 
     * @return <code>true</code> if the specified GML path contains an ignorable
     *         attribute, <code>false</code> otherwise.
     */
    @Override
    boolean isIgnorable(String path) {
        if (path.equals(GmlConstants.ID))
            return true;
        else
            return false;
    }

    /*
     * 
     */
    @Override
    AttributeMapping getMapping() {
        return this.attributeMap;
    }

    /**
     * Creates and returns a new node in the graph <code>g</code>. The node will
     * be created from the GML declaration and the attributes will be assigned
     * accordingly.
     * 
     * @param g
     *            the graph in which to create the node.
     * @param n
     *            the node declaration to be processed for creating the node.
     * 
     * @return the newly created node.
     * 
     * @throws GmlToGraffitiException
     *             if a fatal error occurs during the transformation.
     */
    Node createNode(Graph g, GmlNode n) throws GmlToGraffitiException {
        // make sure we get a GmlList
        GmlValue gv = n.getValue();
        assert !(gv.isInt() || gv.isReal() || gv.isString());

        GmlList atts = (GmlList) gv.getValue();

        // create a new attribute hierarchy with some default values
        CollectionAttribute ca = new NodeGraphicAttribute();

        // create a node and attach the attributes
        Node node = g.addNode();
        node.addAttribute(ca, "");

        // make sure the templates are used ...
        createAttributes(node, "", gmlStyle);

        // ... and add the attributes
        createAttributes(node, "", atts);

        return node;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
