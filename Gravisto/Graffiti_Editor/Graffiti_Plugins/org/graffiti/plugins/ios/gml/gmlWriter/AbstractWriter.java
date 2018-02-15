// =============================================================================
//
//   AbstractWriter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractWriter.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.plugins.ios.gml.attributemapping.AttributeMapping;
import org.graffiti.plugins.ios.gml.gmlWriter.gmlAttribute.GmlAttributeHierarchy;
import org.graffiti.plugins.ios.gml.gmlWriter.gmlAttribute.GmlAttributeString;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Class <code>AbstractWriter</code> provides generic functionality for the
 * different writers.
 * 
 * @author ruediger
 */
abstract class AbstractWriter {

    /** The logger for this class. */
    private static final Logger logger = Logger.getLogger(AbstractWriter.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The indentation steps when writing the data. */
    protected static final int OFFSET = 2;

    /** The output stream to which to write the data. */
    protected OutputStream os;

    /**
     * Constructs a new <code>AbstractWriter</code> for a given output stream.
     * 
     * @param os
     *            the output stream to which to write the data.
     */
    AbstractWriter(OutputStream os) {
        super();
        this.os = os;
    }

    /**
     * Returns the attribute mapping for this writer.
     * 
     * @return the attribute mapping for this writer.
     */
    abstract AttributeMapping getMapping();

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
    abstract String getSpecialValue(Attribute attr)
            throws NoSpecialValueException;

    /**
     * Returns a String containing a base 16 encoding of the color of the
     * specified attribute.
     * 
     * @param attr
     *            the attribute of which to determine the color value.
     * 
     * @return a String containing a base 16 encoding of the color of the
     *         specified attribute.
     * 
     * @throws NoSpecialValueException
     *             if the attribute does not contain a color attribute.
     */
    String getColor(Attribute attr) throws NoSpecialValueException {
        try {
            ColorAttribute colAttr = (ColorAttribute) attr;
            String value = "\"#";
            value += Integer.toHexString(colAttr.getRed() / 16);
            value += Integer.toHexString(colAttr.getRed() % 16);
            value += Integer.toHexString(colAttr.getGreen() / 16);
            value += Integer.toHexString(colAttr.getGreen() % 16);
            value += (Integer.toHexString(colAttr.getBlue() / 16));
            value += (Integer.toHexString(colAttr.getBlue() % 16) + "\"");
            logger.info("Color value \"" + value + "\" determined.");

            return value.toUpperCase();
        } catch (ClassCastException cce) {
            throw new NoSpecialValueException("Could not determine color.");
        }
    }

    /**
     * Writes whitespace of the specified length to the output stream
     * 
     * @param offset
     *            the number of spaces to be writen.
     * 
     * @throws IOException
     *             if an error occurrs accessing the output stream.
     */
    void indent(int offset) throws IOException {
        StringBuffer s = new StringBuffer("");

        for (int i = 0; i < offset; ++i) {
            s.append(" ");
        }

        os.write(s.toString().getBytes());
    }

    /**
     * Invokes writing of the specified <code>CollectionAttribute</code> with
     * the given indentation.
     * 
     * @param ca
     *            the <code>CollectionAttribute</code> to be written.
     * @param indent
     *            the indentation level at which to start writing.
     * 
     * @throws IOException
     *             DOCUMENT ME!
     */
    void writeAttributes(CollectionAttribute ca, int indent) throws IOException {
        GmlAttributeHierarchy gh = new GmlAttributeHierarchy(OFFSET);

        // walk through the attribute hierarchy and put the attributes to gh
        writeCollectionAttribute(ca, gh);

        gh.printGML(os, indent);
    }

    /**
     * Adds the specified <code>Attribute</code> to the
     * <code>GmlAttributeHierarchy</code> for writing it.
     * 
     * @param attr
     *            the <code>Attribute</code> to be written.
     * @param gh
     *            the <code>GmlAttributeHierarchy</code> to which the data to be
     *            written will be added.
     */
    private void writeAttribute(Attribute attr, GmlAttributeHierarchy gh) {
        assert attr != null;
        assert gh != null;

        String gravistoPath = attr.getPath();
        String gmlPath = getMapping().getGMLPath(gravistoPath);

        // logger.info("writing attribute at path \"" + attr.getPath() + "\".");
        // if the mapping is ignorable there is nothing to do
        if (getMapping().isIgnorableGravisto(gravistoPath)) {
            logger.warning("Attribute at path \"" + gravistoPath
                    + "\" will be ignored.");

            return;
        } else if (gmlPath == null) // there is no corresponding GML path
        {
            // use the gravisto path, make sure it does not start with a '.'
            if (gravistoPath.trim().charAt(0) == '.') {
                gmlPath = gravistoPath.substring(1, gravistoPath.length());
            } else {
                gmlPath = gravistoPath;
            }

            logger.info("Attribute at path \"" + gravistoPath
                    + "\" will be written at the corresponding GML path \""
                    + gmlPath + "\".");

            String value = attr.getValue().toString();

            // make sure string attributes are surounded by "
            if (attr instanceof StringAttribute) {
                if (value.equals("")) {
                    value = "\"\"";
                } else {
                    if (value.charAt(0) != '"') {
                        value = "\"" + value;
                    }

                    if (!value.endsWith("\"")) {
                        value += "\"";
                    }
                }
            }

            // GML wants booleans to be 0 or 1 instead of true or false
            else if (attr instanceof BooleanAttribute) {
                BooleanAttribute ba = (BooleanAttribute) attr;
                value = ba.getBoolean() ? "0" : "1";
            }

            GmlAttributeString gs = new GmlAttributeString(value);
            assert gmlPath.charAt(0) != '.';
            gh.add(gmlPath, gs);
        }

        // gmlPath != null, i.e. there is a corresponding GML path
        else {
            assert (gmlPath != null) && !gmlPath.equals("");

            String value = "";

            if (getMapping().requiresSpecialTreatment(gmlPath)) {
                logger.info("special treatment for attribute at gravisto "
                        + "path " + gravistoPath + ".");

                try {
                    value = getSpecialValue(attr);
                } catch (NoSpecialValueException nsve) {
                    logger.warning(nsve.getMessage());
                    value = attr.getValue().toString();
                }
            } else {
                value = attr.getValue().toString();
            }

            assert !value.equals("");

            logger.info("attribute to be written at gmlPath " + gmlPath
                    + " with value " + value + ".");

            GmlAttributeString gs = new GmlAttributeString(value);
            gh.add(gmlPath, gs);
        }
    }

    /**
     * Writes the specified <code>CollectionAttribute</code> to the output
     * stream using the specified indentation level.
     * 
     * @param ca
     *            the <code>CollectionAttribute</code> to be written.
     * @param gh
     *            the gml attribute hierachy the attributes shall be added to.
     */
    private void writeCollectionAttribute(CollectionAttribute ca,
            GmlAttributeHierarchy gh) {
        assert ca != null;
        assert gh != null;

        logger.setLevel(Level.OFF);

        for (String id : ca.getCollection().keySet()) {
            Attribute attr = ca.getAttribute(id);

            String gravistoPath = attr.getPath();
            if (getMapping().isIgnorableGravisto(gravistoPath)) {
                logger.warning("Attribute at path \"" + gravistoPath
                        + "\" will be ignored.");

                continue;
            }

            if (attr instanceof ColorAttribute) {
                ColorAttribute colorAttr = (ColorAttribute) attr;
                String gmlPath = getMapping().getGMLPath(colorAttr.getPath());

                // add the color value
                try {
                    if (gmlPath == null) {
                        gmlPath = colorAttr.getPath();
                    }

                    if (!getMapping().isIgnorableGML(gmlPath)) {
                        String colorString = getColor(colorAttr);
                        assert colorString != null;
                        assert gmlPath != null : "no gmlPath for path "
                                + colorAttr.getPath() + ".";
                        gh.add(gmlPath, new GmlAttributeString(colorString));
                    }
                } catch (NoSpecialValueException nsve) {
                    logger.warning("could not write color attribute at path "
                            + colorAttr.getPath() + ".");
                }

                // add the transparency value
                gmlPath += "_transparency";

                GmlAttributeString gs = new GmlAttributeString(""
                        + colorAttr.getOpacity());

                // colorAttr.getAttribute("transparency").getValue().toString());
                gh.add(gmlPath, gs);
            }

            // usual CollectionAttributes are processed recursively
            else if (attr instanceof CollectionAttribute) {
                CollectionAttribute coll = (CollectionAttribute) attr;
                String gmlPath = getMapping().getGMLPath(coll.getPath());

                if ((gmlPath != null)
                        && getMapping().requiresSpecialTreatment(gmlPath)) {
                    try {
                        String value = getSpecialValue(coll);
                        GmlAttributeString gs = new GmlAttributeString(value);
                        gh.add(gmlPath, gs);
                        logger.info("writing special value collection "
                                + "attribute at path \"" + attr.getPath()
                                + "\".");
                    } catch (NoSpecialValueException e) {
                        logger.warning("no special value for attribute at "
                                + "path " + attr.getPath() + ".");
                    }
                } else {
                    logger.info("writing collection attribute at path \""
                            + attr.getPath() + "\".");
                    writeCollectionAttribute(coll, gh);
                }
            } else {
                logger.info("write noncollection attribute at path"
                        + attr.getPath() + " of type "
                        + attr.getClass().getName() + ".");
                writeAttribute(attr, gh);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
