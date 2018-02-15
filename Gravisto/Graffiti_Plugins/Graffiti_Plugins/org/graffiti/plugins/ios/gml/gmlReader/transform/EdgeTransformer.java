// =============================================================================
//
//   EdgeTransformer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeTransformer.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.transform;

import java.util.Collection;
import java.util.logging.Logger;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.EdgeLabelPositionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.PositionAttribute;
import org.graffiti.plugins.ios.gml.GmlConstants;
import org.graffiti.plugins.ios.gml.attributemapping.AbstractAttributeMapping;
import org.graffiti.plugins.ios.gml.attributemapping.AttributeMapping;
import org.graffiti.plugins.ios.gml.attributemapping.EdgeAttributeMapping;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlEdge;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlEdgeStyle;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlInt;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlKey;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlList;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlValuable;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlValue;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Class <code>EdgeTransformer</code> implements the special needs for the
 * transformation of edges from the GML declaration to the corresponding
 * Gravisto representation.
 * 
 * @author ruediger
 */
public class EdgeTransformer extends GraphElementTransformer {

    /** The logger for development purposes. */
    private static final Logger logger = Logger.getLogger(NodeTransformer.class
            .getName());

    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }
    
    /**
     * The edge attribute mapping for mapping GML and Gravisto edge attribute
     * paths.
     */
    private EdgeAttributeMapping attributeMap;

    /**
     * Constructs a new <code>EdgeTransformer</code>.
     * 
     * @param style
     *            the GML style declaration for edges.
     * 
     * @throws GmlToGraffitiException
     *             if the transformation of the style fails.
     */
    EdgeTransformer(GmlEdgeStyle style) throws GmlToGraffitiException {
        super(style);
        this.attributeMap = new EdgeAttributeMapping();
    }

    /**
     * Returns a <code>PositionAttribute</code> which contains the information
     * of where to place labels at edges.
     * 
     * @return a <code>PositionAttribute</code> which contains the information
     *         of where to place labels for edges.
     */
    @Override
    protected PositionAttribute getPositionAttribute() {
        return new EdgeLabelPositionAttribute("position");
    }

    /**
     * Creates a special attribute from a GML attribute declaration that is
     * specific, i.e. individual, to edges.
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
                .info("creating special edge attribute at path \"" + path
                        + "\".");

        if (path.equals(GmlConstants.SHAPE_PATH)) {
            assert key.getValue().isInt();

            GmlInt gint = (GmlInt) key.getValue();
            int val = ((Integer) gint.getValue()).intValue();

            // setColor(attbl, ".graphics.foreground", gs);
            String destPath = GmlConstants.SHAPE_PATH;

            if (val == 1) {
                attbl.setString(destPath,
                        GraphicAttributeConstants.SMOOTH_CLASSNAME);
            } else {
                attbl.setString(destPath,
                        GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME);
            }
        } else if (path.startsWith(GraphicAttributeConstants.LINE_POINT_PATH)) {
            SortedCollectionAttribute bends = (SortedCollectionAttribute) attbl
                    .getAttribute(GraphicAttributeConstants.BENDS_PATH);
            String id = GraphicAttributeConstants.BEND
                    + bends.getCollection().size();
            CoordinateAttribute ca = new CoordinateAttribute(id);
            bends.add(ca);

            GmlList list = (GmlList) key.getValue();

            // get the x- and y-coordinate
            while (list != null) {
                GmlValuable gv = list.getHead();
                assert !(gv.isNode() || gv.isEdge() || gv.isGraph());

                GmlKey gmlKey = (GmlKey) gv;

                if (gmlKey.getId().equals(GraphicAttributeConstants.X)) {
                    GmlValue xValue = gv.getValue();
                    Double xCoord = (Double) xValue.getValue();

                    // set the attribute values
                    ca.setX(xCoord.doubleValue());
                } else if (gmlKey.getId().equals(GraphicAttributeConstants.Y)) {
                    GmlValue yValue = gv.getValue();
                    Double yCoord = (Double) yValue.getValue();

                    // set the attribute values
                    ca.setY(yCoord.doubleValue());
                } else {
                    // logger.warning("unsupported attribute " + gmlKey.getId()
                    // + " at path graphics.Line.point.");
                }

                list = list.getTail();
            }

            // the existencs of bends might also change the edge shape
            String shape = attbl
                    .getString(GraphicAttributeConstants.SHAPE_PATH);

            if (shape
                    .endsWith(GraphicAttributeConstants.STRAIGHT_LINE_EDGE_SHAPE)) {
                attbl.setString(GraphicAttributeConstants.SHAPE_PATH,
                        GraphicAttributeConstants.POLYLINE_CLASSNAME);
            }
        } else {
            logger.warning("no individual special attribute for edge "
                    + "attribute at path " + path + ".");
        }
    }

    /*
     * 
     */
    @Override
    AbstractAttributeMapping getAttributeMap() {
        return this.attributeMap;
    }

    /*
     * 
     */
    @Override
    boolean isIgnorable(String path) {
        if (path.equals(GraphicAttributeConstants.SOURCE)
                || path.equals(GraphicAttributeConstants.TARGET))
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
     * Creates a new edge in the specfied graph and invokes the creation of the
     * corresponding attributes.
     * 
     * @param g
     *            the <code>Graph</code> to which to add the edge.
     * @param source
     *            the source <code>Node</code> of the edge.
     * @param target
     *            the target <code>Node</code> of the edge.
     * @param e
     *            the GML edge declaration.
     * @param directed
     *            <code>true</code> if the edge should be directed, false
     *            otherwise.
     * 
     * @throws GmlToGraffitiException
     *             if the transformation fails for some reason.
     */
    void createEdge(Graph g, Node source, Node target, GmlEdge e,
            boolean directed) throws GmlToGraffitiException {
        // make sure we get a GmlList
        GmlValue gv = e.getValue();
        assert !(gv.isInt() || gv.isReal() || gv.isString());

        GmlList atts = (GmlList) gv.getValue();

        // create a new attribute hierarchy with some default values
        CollectionAttribute ca = new EdgeGraphicAttribute();

        // create a node and attach the attributes
        Edge edge = g.addEdge(source, target, directed);
        edge.addAttribute(ca, "");

        // for directed edges the arrowhead must be set explicitly
        if (directed) {
            edge.setString(GmlConstants.ARROWHEAD,
                    GraphicAttributeConstants.ARROWSHAPE_CLASSNAME);
        }

        // make sure the templates are used ...
        createAttributes(edge, "", gmlStyle);

        // ... and add the attributes
        createAttributes(edge, "", atts);

        /*
         * Remove abundent edge bends added by Graphlet. These bends have the
         * coordinates of the source node and the target node.
         */
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        SortedCollectionAttribute newBends = new LinkedHashMapAttribute(
                GraphicAttributeConstants.BENDS);
        SortedCollectionAttribute oldBends = ega.getBends();
        Collection<Attribute> values = oldBends.getCollection().values();

        int i = 0;
        int counter = 0;
        for (Attribute attribute : values) {
            CoordinateAttribute cat = (CoordinateAttribute) attribute;
            if (i > 0 && i < values.size() - 1) {
                newBends.add(new CoordinateAttribute(
                        GraphicAttributeConstants.BEND + counter, cat.getX(),
                        cat.getY()));
                counter++;
            }
            i++;
        }
        ega.setBends(newBends);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
