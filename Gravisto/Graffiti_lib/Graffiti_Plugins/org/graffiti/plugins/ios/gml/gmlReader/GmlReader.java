// =============================================================================
//
//   GmlReader.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlReader.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugins.ios.gml.gmlReader.gml.Gml;
import org.graffiti.plugins.ios.gml.gmlReader.parser.GmlParser;
import org.graffiti.plugins.ios.gml.gmlReader.transform.GmlToGraffiti;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Class <code>GmlReader</code> provides the interface for reading in graphs
 * specified in the GML format. Graphs can be read in from an arbitrary
 * <code>InputStream</code>.
 * 
 * @author ruediger
 */
public class GmlReader extends AbstractInputSerializer {

    /** Logger for development purposes. */
    private static final Logger logger = Logger.getLogger(GmlReader.class
            .getName());

    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The parser for reading the gml input. */
    private GmlParser gmlParser;

    /** The supported extension. */
    private String[] extensions = { ".gml" };

    /**
     * Constructs a new <code>GmlReader</code>.
     */
    public GmlReader() {
        super();
        gmlParser = new GmlParser();
    }

    /**
     * Returns the supported extensions.
     * 
     * @return the supported extensions.
     */
    public String[] getExtensions() {
        return this.extensions;
    }

    /**
     * Reads the graph specified by <code>in</code> and adds it to
     * <code>g</code>.
     * 
     * @param in
     *            the <code>InputStream</code> from which to read the graph.
     * @param graph
     *            the <code>Graph</code> to which the read in graph shall be
     *            added.
     * 
     * @throws IOException
     *             if something fails while reading in the graph.
     */
    @Override
    public void read(InputStream in, Graph graph) throws IOException {
        assert graph != null;

        gmlParser = new GmlParser();

        try {
            logger.fine("invoking the parser");
            assert gmlParser != null;

            Gml g = gmlParser.parse(in);
            assert g != null;
            logger.fine("creating transformer");

            GmlToGraffiti g2g = new GmlToGraffiti(g, graph);
            assert g2g != null;
            g2g.transform();
        } catch (Exception e) {
            logger.severe("reading caused exception (" + e.getClass().getName()
                    + ": " + e.getMessage());
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        in.close();
    }

    public String getName() {
        return "GML Importer";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
