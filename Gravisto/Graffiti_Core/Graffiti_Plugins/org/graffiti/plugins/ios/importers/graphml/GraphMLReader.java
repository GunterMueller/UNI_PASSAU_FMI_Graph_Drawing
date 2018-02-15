// =============================================================================
//
//   GraphMLReader.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphMLReader.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.graphml;

import java.io.IOException;
import java.io.InputStream;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugin.io.InputSerializer;
import org.graffiti.plugins.ios.importers.graphml.parser.GraphMLParser;

/**
 * This class implements the interface to invoke the reading of graphML files.
 * 
 * @author ruediger
 */
public class GraphMLReader extends AbstractInputSerializer implements
        InputSerializer {

    /** The parser for reading the graphml input. */
    private GraphMLParser graphmlParser;

    /** The supported extension. */
    private String[] extensions = { ".graphml", ".xml" };

    /**
     * Constructs a new <code>GraphMLReader</code>.
     */
    public GraphMLReader() {
        super();
        this.graphmlParser = new GraphMLParser();
    }

    /*
     * 
     */
    public String[] getExtensions() {
        return this.extensions;
    }

    /*
     * 
     */
    @Override
    public void read(InputStream in, Graph g) throws IOException {
        graphmlParser = new GraphMLParser();
        this.graphmlParser.parse(in, g);
        in.close();
    }

    public String getName() {
        return "GraphML Reader";
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
