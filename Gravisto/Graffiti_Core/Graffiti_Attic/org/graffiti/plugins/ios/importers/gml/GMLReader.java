// =============================================================================
//
//   GMLReader.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GMLReader.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.ios.importers.gml;

import java.io.IOException;
import java.io.InputStream;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugin.io.ParserException;

/**
 * This class provides a reader for graphs in gml format.
 * 
 * @see org.graffiti.plugin.io.AbstractIOSerializer
 */
public class GMLReader extends AbstractInputSerializer {

    /** The supported extensions. */
    private String[] extensions = { ".gml" };

    /** The parser for reading in the graph. */
    private parser p;

    /**
     * Constructs a new <code>GMLReader</code>
     */
    public GMLReader() {
    }

    /**
     * Returns the extensions supported by this reader.
     * 
     * @return the extensions supported by this reader.
     */
    public String[] getExtensions() {
        return this.extensions;
    }

    /**
     * Reads in a graph from the given input stream. <code>GraphElements</code>
     * read are <b>cloned</b> when added to the graph. Consider using the
     * <code>read(InputStream)</code> method when you start with an empty graph.
     * 
     * @param in
     *            the <code>InputStream</code> from which to read in the graph.
     * @param g
     *            the graph in which to read in the file.
     * 
     * @exception ParserException
     *                if an error occurs while parsing the .gml file.
     */
    @Override
    public void read(InputStream in, Graph g) throws ParserException {
        p = new parser(new Yylex(in));

        try {
            p.parse();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ParserException(e.getMessage());
        }

        g.addGraph(p.getGraph());
    }

    /**
     * Reads in a graph from the given input stream. This implementation returns
     * an instance of <code>OptAdjListGraph</code> (that's what the parser
     * returns).
     * 
     * @param in
     *            The input stream to read the graph from.
     * 
     * @return The newly read graph (an instance of <code>OptAdjListGraph</code>
     *         ).
     * 
     * @exception IOException
     *                If an IO error occurs.
     * @throws ParserException
     *             DOCUMENT ME!
     */
    @Override
    public Graph read(InputStream in) throws IOException {
        p = new parser(new Yylex(in));

        try {
            p.parse();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ParserException(e.getMessage());
        }

        in.close();

        return p.getGraph();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
