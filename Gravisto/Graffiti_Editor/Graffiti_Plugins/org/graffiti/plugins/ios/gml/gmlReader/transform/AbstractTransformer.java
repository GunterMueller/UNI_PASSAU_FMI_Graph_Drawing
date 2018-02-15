// =============================================================================
//
//   AbstractTransformer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractTransformer.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.transform;

import java.util.logging.Logger;

import org.graffiti.attributes.Attributable;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlInt;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlReal;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlString;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * This class provides an abstract implementation for a transformation from the
 * GML representation to Gravisto.
 * 
 * @author ruediger
 */
abstract class AbstractTransformer {

    /** The logger for development purposes. */
    private static final Logger logger = Logger
            .getLogger(AbstractTransformer.class.getName());

    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /**
     * Constructs a new <code>AbstractTransformer</code>.
     */
    public AbstractTransformer() {
        super();
    }

    /**
     * Sets the value of the integer attribute at the specified path of the
     * specified <code>Attributable</code> to the specified value.
     * 
     * @param attbl
     *            the <code>Attributable</code> at which to set the attribute
     *            value.
     * @param path
     *            the path specifying the attribute to be set.
     * @param gint
     *            the GML integer value.
     */
    public void addAttribute(Attributable attbl, String path, GmlInt gint) {
        int intValue = ((Integer) gint.getValue()).intValue();
        logger.info("adding IntegerAttr at path \"" + path + "\" with value "
                + intValue + ".");
        attbl.setInteger(path, intValue);
    }

    /**
     * Sets the value of the double attribute at the specified path of the
     * specified <code>Attributable</code> to the specified value.
     * 
     * @param attbl
     *            the <code>Attributable</code> at which to set the attribute
     *            value.
     * @param path
     *            the path specifying the attribute to be set.
     * @param greal
     *            the GML real value.
     */
    public void addAttribute(Attributable attbl, String path, GmlReal greal) {
        double doubleValue = ((Double) greal.getValue()).doubleValue();
        logger.info("adding DoubleAttr at path \"" + path + "\" with value "
                + doubleValue + ".");
        attbl.setDouble(path, doubleValue);
    }

    /**
     * Sets the value of the string attribute at the specified path of the
     * specified <code>Attributable</code> to the specified value.
     * 
     * @param attbl
     *            the <code>Attributable</code> at which to set the attribute
     *            value.
     * @param path
     *            the path specifying the attribute to be set.
     * @param gstr
     *            the GML string value.
     */
    public void addAttribute(Attributable attbl, String path, GmlString gstr) {
        String stringValue = (String) gstr.getValue();
        logger.info("adding IntegerAttr at path \"" + path + "\" with value "
                + stringValue + ".");
        attbl.setString(path, stringValue);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
