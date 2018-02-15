// =============================================================================
//
//   GraphMLParser.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphMLParser.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.graphml.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.graffiti.graph.Graph;
import org.graffiti.plugins.ios.importers.graphml.GraphMLException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Class <code>GraphMLParser</code> is responsible for setting up the XML
 * parsing environment. It instantiates an XML parser, sets the desired
 * properties and attatches the event handlers to the parser.
 * 
 * @author ruediger
 */
public class GraphMLParser {

    /** The validation feature property string. */
    private static final String VALIDATION_FEATURE = "http://apache.org/xml/"
            + "features/validation/schema";

    /**
     * Constructs a new <code>GraphMLParser</code>.
     */
    public GraphMLParser() {
    }

    /**
     * Parses the given <code>InputStream</code> and adds the read in data to
     * the given <code>Graph</code>.
     * 
     * @param in
     *            the <code>InputStream</code> from which to read.
     * @param g
     *            the <code>Graph</code> to which to add the parsed data.
     * 
     * @throws IOException
     *             if something fails during parsing.
     * @throws GraphMLException
     *             if something fails during parsing.
     */
    public void parse(InputStream in, Graph g) throws IOException {
        // instantiate a SAXParserFactory that creates SAX parsers that are
        // validating, support namespaces and XML schema validation
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(true);

        // set the validation feature
        try {
            spf.setFeature(VALIDATION_FEATURE, true);
        } catch (SAXNotRecognizedException snre) {
            throw new GraphMLException(snre);
        } catch (SAXNotSupportedException snse) {
            throw new GraphMLException(snse);
        } catch (ParserConfigurationException pce) {
            throw new GraphMLException(pce);
        }

        // make sure the features are supported
        assert spf.isNamespaceAware() : "parser is not namespace aware.";
        assert spf.isValidating() : "parser is not validating.";

        // instantiate a parser
        SAXParser saxParser;

        try {
            saxParser = spf.newSAXParser();
        } catch (ParserConfigurationException pe) {
            throw new GraphMLException(pe);
        } catch (SAXException se) {
            throw new GraphMLException(se);
        }

        // configure the different readers
        XMLReader parser;

        try {
            parser = saxParser.getXMLReader();
        } catch (SAXException se) {
            throw new GraphMLException(se);
        }

        // create a filter for filtering character events
        XMLFilterImpl charFilter = new CharFilter(parser);

        // create a filter to filter the content supported by Gravisto
        // XMLFilterImpl gravistoFilter = new GraphMLGravistoFilter(parser, g);
        XMLFilterImpl gravistoFilter = new GraphMLGravistoFilter(charFilter);

        // create a filter for processing the relevant content
        XMLFilterImpl graphMLParser = new GraphMLFilter(gravistoFilter, g);
        graphMLParser.setEntityResolver(new GraphMLEntityResolver());

        // run the parser
        try {
            graphMLParser.parse(new InputSource(in));
        } catch (SAXException se) {
            throw new GraphMLException(se);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
