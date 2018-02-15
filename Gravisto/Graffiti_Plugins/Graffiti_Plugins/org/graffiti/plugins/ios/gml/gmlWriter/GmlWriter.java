// =============================================================================
//
//   GmlWriter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlWriter.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter;

import java.io.IOException;
import java.io.OutputStream;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.AbstractOutputSerializer;

/**
 * This class provides the main interface for writing files in the GML format.
 * 
 * @author ruediger
 */
public class GmlWriter extends AbstractOutputSerializer {

    /** Writes the graph. */
    private GraphWriter gw;

    /** The supported file extension. */
    private String[] extensions = { ".gml" };

    /**
     * Constructs a new <code>GmlWriter</code>.
     */
    public GmlWriter() {
        super();
    }

    /**
     * Returns the file extensions the serializer can write.
     * 
     * @return the file extensions the serializer can write.
     */
    public String[] getExtensions() {
        return this.extensions;
    }

    /**
     * Writes the contents of the given graph to a stream.
     * 
     * @param stream
     *            The output stream to save the graph to.
     * @param g
     *            The graph to save.
     * 
     * @throws IOException
     *             if an error occurs while writing the graph.
     */
    public void write(OutputStream stream, Graph g) throws IOException {
        this.gw = new GraphWriter(stream);
        gw.write(g);
        stream.flush();
        stream.close();
    }

    public String getName() {
        return "GML Exporter";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
