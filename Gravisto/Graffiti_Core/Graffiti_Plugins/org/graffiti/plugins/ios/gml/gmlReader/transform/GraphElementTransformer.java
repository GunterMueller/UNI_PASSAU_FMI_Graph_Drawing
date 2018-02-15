// =============================================================================
//
//   GraphElementTransformer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphElementTransformer.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.transform;

import java.awt.Color;
import java.util.logging.Logger;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.AbstractEdge;
import org.graffiti.graph.AbstractNode;
import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.graphics.PositionAttribute;
import org.graffiti.plugins.ios.gml.GmlConstants;
import org.graffiti.plugins.ios.gml.attributemapping.AbstractAttributeMapping;
import org.graffiti.plugins.ios.gml.attributemapping.AttributeMapping;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlInt;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlKey;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlList;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlReal;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlString;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlStyle;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlValuable;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlValue;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Class <code>GraphElementTransformer</code> provides some general
 * functionality for transforming graph elements in GML representation into the
 * graffiti representation.
 * 
 * @author ruediger
 */
abstract class GraphElementTransformer extends AbstractTransformer {

    /** The logger for development purposes. */
    private static final Logger logger = Logger
            .getLogger(GraphElementTransformer.class.getName());

    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The style to be applied upon every creation of a new graph element. */
    GmlList gmlStyle;

    /**
     * Creates a new <code>GraphElementTransformer</code>.
     * 
     * @param style
     *            the style to be used for creating a new graph element.
     * 
     * @throws GmlToGraffitiException
     *             if an error occurs during the transformation from GML to
     *             graffiti.
     */
    GraphElementTransformer(GmlStyle style) throws GmlToGraffitiException {
        super();

        if (style != null) {
            GmlValue val = style.getValue();
            assert !(val.isInt() || val.isReal() || val.isString());
            logger.info("start reading style.");

            // make sure is's a collection
            assert !(val.isInt() || val.isReal() || val.isString());

            GmlList list = (GmlList) val;

            if (list != null) {
                logger.info("nonempty style");

                // find style attributes
                while (list != null) {
                    GmlValuable gv = list.getHead();
                    assert !(gv.isNode() || gv.isEdge() || gv.isGraph());

                    GmlKey gk = (GmlKey) gv;
                    String id = gk.getId();

                    if (id.equals(GmlConstants.STYLE)) {
                        GmlValue value = gv.getValue();
                        assert !(value.isInt() || value.isReal() || value
                                .isReal());

                        GmlList styles = (GmlList) value;
                        this.gmlStyle = styles;
                    } else if (id.equals(GraphicAttributeConstants.NAME)) {
                        logger.fine("id \"name\" detected for style - "
                                + "ignored.");
                    } else
                        throw new GmlToGraffitiException(
                                "Unexpected style id \"" + id + "\" (line "
                                        + gk.getLine() + ").");

                    list = list.getTail();
                }
            } else {
                logger.info("empty style");
            }
        } else {
            logger.info("no style declared.");
        }
    }

    /**
     * Returns a <code>PositionAttribute</code> which contains the information
     * of where to place <code>LabelAttribute</code>s at this graph element.
     * 
     * @return a <code>PositionAttribute</code> which contains the information
     *         of where to place <code>LabelAttribute</code>s.
     */
    protected abstract PositionAttribute getPositionAttribute();

    /**
     * Creates a special attribute from a GML attribute declaration that is
     * specific, i.e. individual, to nodes and edges and therefore cannot be
     * handeled here.
     * 
     * @param attbl
     *            the <code>Attributable</code> to which to add the attributes.
     * @param path
     *            the path at which to add the attribute.
     * @param key
     *            the GML attribute declaration.
     */
    protected abstract void createIndividualSpecialAttribute(
            Attributable attbl, String path, GmlKey key);

    /**
     * Returns the attribute map for this graph element containing the mapping
     * from GML paths to graffiti paths.
     * 
     * @return the attribute map for this graph element.
     */
    abstract AbstractAttributeMapping getAttributeMap();

    /**
     * Returns <code>true</code> if the attribute with GML path
     * <code>path</code> can be ignored, i.e. does not need to be transformed
     * and added to the graffiti path, <code>false</code> otherwise. This method
     * is used to filter the GML key words <code>graph, node,
     * edge</code> etc.
     * 
     * @param path
     *            the GML path of the attribute to be checked.
     * 
     * @return <code>true</code> if the attribute with the specified GML path
     *         can be ignored for the transformation, <code>false</code>
     *         otherwise.
     */
    abstract boolean isIgnorable(String path);

    /**
     * Returns the <code>AttributeMapping</code> for this
     * <code>Attributable</code>.
     * 
     * @return the <code>AttributeMapping</code> for this
     *         <code>Attributable</code>.
     */
    abstract AttributeMapping getMapping();

    /**
     * Sets the value of the color attribute at the specified path of the
     * specified Attributable to the specified value.
     * 
     * @param attbl
     *            the <code>Attributable</code> at which to set the attribute
     *            value.
     * @param path
     *            the path specifying the attribute to be set.
     * @param gs
     *            the GML string value representing the color.
     */
    void setColor(Attributable attbl, String path, GmlString gs) {
        String colorString = (String) gs.getValue();
        Color c = null;

        if (colorString.equals(GmlConstants.RED)) {
            c = Color.RED;
        } else if (colorString.equals(GmlConstants.BLUE)) {
            c = Color.BLUE;
        } else if (colorString.equals(GmlConstants.GREEN)) {
            c = Color.GREEN;
        } else {
            c = Color.decode(colorString);
        }

        assert c != null;

        if (!path.endsWith(Attribute.SEPARATOR)) {
            path += Attribute.SEPARATOR;
        }

        attbl.setInteger(path + GmlConstants.RED, c.getRed());
        attbl.setInteger(path + GmlConstants.GREEN, c.getGreen());
        attbl.setInteger(path + GmlConstants.BLUE, c.getBlue());
    }

    /**
     * Adds the attributes declared in the GML list of attributes to the
     * specified graph element at the specified path.
     * 
     * @param ge
     *            the <code>GraphElement</code> the attribute shall be attatched
     *            to.
     * @param gmlPath
     *            the path at which the attribute shall be created.
     * @param atts
     *            the list of GML attributes to be added.
     * 
     * @throws GmlToGraffitiException
     *             if something fails during the transformation from GML to
     *             graffiti.
     */
    void createAttributes(GraphElement ge, String gmlPath, GmlList atts)
            throws GmlToGraffitiException {
        assert ge != null;
        assert gmlPath != null;
        logger.info("processing gmlPath \"" + gmlPath + "\".");

        CollectionAttribute ca = ge.getAttributes();
        assert ca != null;

        // iterate over the list of GML attributes and create graffiti
        // attributes
        while (atts != null) {
            GmlValuable head = atts.getHead();

            // make sure we really get an attribute
            assert !(head.isGraph() || head.isNode() || head.isEdge());

            GmlKey key = (GmlKey) head;
            GmlValue val = key.getValue();
            String id = key.getId();
            String newGmlPath = "";

            if (gmlPath.equals("")) {
                newGmlPath = id;
            } else {
                newGmlPath = gmlPath + Attribute.SEPARATOR + id;
            }

            String graffitiPath = "";

            if (newGmlPath.endsWith(GmlConstants._TRANSPARENCY)) {
                graffitiPath = getMapping().getGravistoPath(
                        newGmlPath.substring(0, newGmlPath.length() - 13))
                        + GmlConstants.TRANSPARENCY_PATH;
            } else {
                graffitiPath = getMapping().getGravistoPath(newGmlPath);
            }

            if (!getMapping().isIgnorableGravisto(newGmlPath)
                    && !getMapping().isIgnorableGML(newGmlPath)) {
                // check is there is a mapping from gml path to graffiti path
                // AbstractAttributeMapping am = getAttributeMap();
                // String graffitiPath = am.getGravistoPath(newGmlPath);
                // boolean special = am.requiresSpecialTreatment(newGmlPath);
                boolean special = getMapping().requiresSpecialTreatment(
                        newGmlPath);

                if (graffitiPath == null) {
                    logger.info("no corresponding graffiti path - using \""
                            + newGmlPath + "\".");
                    graffitiPath = newGmlPath;
                } else {
                    logger.info("graffiti path corresponding to GML path \""
                            + newGmlPath + "\": \"" + graffitiPath + "\".");
                }

                assert graffitiPath != null;

                if (special
                        || graffitiPath
                                .startsWith(GraphicAttributeConstants.LINE_POINT_PATH)) {
                    createSpecialAttribute(ge, graffitiPath, key);
                } else {
                    // according to the kind of value create a new attribute
                    // val may be null in the case of an empty list
                    if (val == null) {
                        // addAttribute(node, graffitiPath, l);
                    } else if (val.isInt()) {
                        GmlInt gint = (GmlInt) val;
                        addAttribute(ge, graffitiPath, gint);
                    } else if (val.isReal()) {
                        GmlReal greal = (GmlReal) val;
                        addAttribute(ge, graffitiPath, greal);
                    } else if (val.isString()) {
                        GmlString gstring = (GmlString) val;
                        addAttribute(ge, graffitiPath, gstring);
                    } else {
                        logger.info("GmlList at path \"" + newGmlPath + "\".");

                        GmlList glist = (GmlList) val;

                        // addAttribute(node, graffitiPath, glist);
                        createAttributes(ge, newGmlPath, glist);
                    }
                }
            }

            atts = atts.getTail();
        }
    }

    /**
     * Creates the attribute specified by the GML declaration <code>key</code>.
     * This method is used in case there needs to be a special treatment for
     * this attribute, i.e. the attribute cannot be transformed by just
     * determining the corresponding path an creating a corresponding graffiti
     * attribute.
     * 
     * @param attbl
     *            the <code>Attributable</code> the attribute shall be attatched
     *            to.
     * @param path
     *            the Gml path of the attribute.
     * @param key
     *            the <code>GmlKey</code> containing id and value for the
     *            attribute as specified in GML.
     */
    void createSpecialAttribute(Attributable attbl, String path, GmlKey key) {
        logger.info("creating special graph element attribute at path \""
                + path + "\".");
        if (path.equals(GmlConstants.FILLCOLOR_PATH)) {
            assert key.getValue().isString();

            GmlString gs = (GmlString) key.getValue();
            setColor(attbl, GmlConstants.FILLCOLOR_PATH, gs);
        } else if (path.equals(GmlConstants.FRAMECOLOR_PATH)) {
            assert key.getValue().isString();

            GmlString gs = (GmlString) key.getValue();
            setColor(attbl, GmlConstants.FRAMECOLOR_PATH, gs);
        } else if (path.equals(GmlConstants.BACKGROUND_PATH)) {
            assert key.getValue().isString();

            GmlString gs = (GmlString) key.getValue();
            setColor(attbl, GmlConstants.BACKGROUND_PATH, gs);
        } else if (path.equals(GmlConstants.FOREGROUND_PATH)) {
            assert key.getValue().isString();

            GmlString gs = (GmlString) key.getValue();
            setColor(attbl, GmlConstants.FOREGROUND_PATH, gs);
        } else if (path.equals(GmlConstants.LABEL_LABEL_PATH)) {
            setGraphElementLabels(attbl, GraphicAttributeConstants.LABEL, key);
        } else if (path.equals(GmlConstants.LABEL_TEXTCOLOR_PATH)) {
            assert key.getValue().isString();

            GmlString gs = (GmlString) key.getValue();

            // setColor(attbl, ".label.textcolor", gs);
            ColorAttribute coa = new ColorAttribute(
                    GraphicAttributeConstants.TEXTCOLOR);
            coa.setColor(Color.decode((String) gs.getValue()));

            try {
                attbl.getAttribute(GraphicAttributeConstants.LABEL);
            } catch (AttributeNotFoundException anfe) {
                LabelAttribute la = new LabelAttribute(
                        GraphicAttributeConstants.LABEL);
                la.setTextcolor(coa);
                attbl.addAttribute(la, "");
            }
        } else if (path.equals(GmlConstants.WEIGHT_LABEL_PATH)) {
            setGraphElementLabels(attbl, GmlConstants.WEIGHT, key);
        } else if (path.equals(GmlConstants.CAPACITY_LABEL_PATH)) {
            setGraphElementLabels(attbl, GmlConstants.CAPACITY, key);
        } else if (path.endsWith(GraphicAttributeConstants.LABEL)) {
            setGraphElementLabels(attbl, GraphicAttributeConstants.LABEL, key);
        } else {
            createIndividualSpecialAttribute(attbl, path, key);
        }
    }

    /**
     * Sets the graph elements' labels.
     * 
     * 
     * @param attbl
     *            This attributable object's labels will be set.
     * @param labelName
     *            The labelname to set.
     * @param key
     *            The GmlKey.
     */
    private void setGraphElementLabels(Attributable attbl, String labelName,
            GmlKey key) {
        try {
            LabelAttribute label;
            if (attbl instanceof AbstractNode) {
                label = new NodeLabelAttribute(labelName);
            } else if (attbl instanceof AbstractEdge) {
                label = new EdgeLabelAttribute(labelName);
            } else {
                label = new LabelAttribute(labelName);
            }
            label.setLabel(key.getValue().getValue().toString());
            attbl.addAttribute(label,
                    GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);

            PositionAttribute pa = getPositionAttribute();
            attbl.addAttribute(pa, Attribute.SEPARATOR + labelName);
        } catch (AttributeExistsException aee) {
            attbl.setString(Attribute.SEPARATOR + labelName
                    + Attribute.SEPARATOR + GraphicAttributeConstants.LABEL,
                    key.getValue().getValue().toString());
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
