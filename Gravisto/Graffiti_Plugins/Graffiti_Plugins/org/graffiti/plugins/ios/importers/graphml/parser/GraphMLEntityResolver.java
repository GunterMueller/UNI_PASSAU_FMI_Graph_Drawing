// =============================================================================
//
//   GraphMLEntityResolver.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphMLEntityResolver.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.importers.graphml.parser;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.util.logging.GlobalLoggerSetting;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Class <code>GraphMLEntityResolver</code> implements the
 * <code>EntityResolver</code> interface to resolve external entities. In
 * particular, the XML schemas of graphML referred to in the schema location of
 * a graphML file shall be resolved using the locally cached version.
 * 
 * @author ruediger
 */
public class GraphMLEntityResolver implements EntityResolver {

    /** The logger for this class. */
    private static final Logger logger = Logger
            .getLogger(GraphMLEntityResolver.class.getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /**
     * Constructs a new <code>GraphMLEntityResolver</code>.
     */
    public GraphMLEntityResolver() {
    }

    /**
     * Redirects the external system identifiers pointing to graphML schema
     * locations to the locally cached schemas.
     * 
     * @param publicId
     *            the public identifier of the external entity being referenced,
     *            <code>null</code> if none was supplied.
     * @param systemId
     *            the system identifier of the external entity being referenced.
     * 
     * @return the <code>InputSource</code> object describing the alternative
     *         input source, <code>null</code> to fall back to the default
     *         behavior.
     */
    public InputSource resolveEntity(String publicId, String systemId) {
        // the graphML namespace URLs that are supported
        String baseURL = "http://graphml.graphdrawing.org/xmlns/";
        String graphmlURL = baseURL + "graphml/";
        String graphmlURL2 = baseURL + "1.0/";
        String dummyURL = "dummy:///";

        // system id for the dtd file
        String dtdURL = "http://www.graphdrawing.org/dtds/graphml.dtd";

        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("publicId: \"" + publicId + "\"");
            logger.finest("systemId: \"" + systemId + "\"");
        }

        InputSource is = null;

        String path = systemId.substring(0, systemId.lastIndexOf("/") + 1);
        String file = systemId.substring(systemId.lastIndexOf("/") + 1);

        // the ressources for the graphML namespace
        // http://graphml.graphdrawing.org/xmlns/graphml
        // since an earlier version of the graphml writer used the wrong
        // schema location, we have to du quite ugly hacks to be able
        // read the old and new files correctly.
        if (path.equals(graphmlURL) || path.equals(dummyURL)) {
            if (file.equals("graphml-attributes-1.0rc.xsd")) {
                is = getSource("graphml-attributes-1.0rc.xsd", dummyURL + file);
            } else if (file.equals("graphml-structure-1.0rc.xsd")) {
                if (path.equals(dummyURL)) {
                    is = getSource("graphml-structure-1.0rc.xsd", dummyURL
                            + file);
                } else {
                    is = getSource("graphml-attributes-1.0rc.xsd", dummyURL
                            + file);
                    logger
                            .severe("This graphml file seems to be created with an "
                                    + "earlier version of this plugin and contains a wrong "
                                    + "schema location. Therefor please save this graph and "
                                    + "the error will be fixed.");
                }
            } else if (file.equals("graphml-parseinfo-1.0rc.xsd")) {
                is = getSource("graphml-parseinfo-1.0rc.xsd", dummyURL + file);
            } else if (file.equals("xlink.xsd")) {
                is = getSource("xlink.xsd", dummyURL + file);
            } else {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning("unknown schema location: " + systemId);
                }
            }
        } else if (path.equals(graphmlURL2)) {
            if (file.equals("graphml.xsd")) {
                is = getSource("graphml.xsd", dummyURL + file);
            } else if (file.equals("graphml-structure.xsd")) {
                is = getSource("graphml-structure.xsd", dummyURL + file);
            } else if (file.equals("xlink.xsd")) {
                is = getSource("xlink.xsd", dummyURL + file);
            }

        }
        if (systemId.equals(dtdURL)) {
            is = getSource("graphml.dtd", systemId);
        }

        if (is == null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("no cached file available for systemID\n\t"
                        + systemId);
            }
        }

        return is;
    }

    /**
     * Determines the <code>InputSource</code> for the specified resource and
     * retuns it.
     * 
     * @param resource
     *            the resource to be determined.
     * 
     * @return the <code>InputSource</code> corresponding to the specified
     *         ressource.
     */
    private InputSource getSource(String resource, String systemId) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("searching ressource " + resource);
        }

        InputStream istr = this.getClass().getResourceAsStream(resource);
        assert istr != null;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("using cached file \"" + resource + "\".");
        }

        InputSource is = new InputSource(istr);
        assert is != null : "input source is null";

        is.setSystemId(systemId);
        return is;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
