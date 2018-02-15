// =============================================================================
//
//   OutputSerializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: OutputSerializer.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.io;

import java.io.IOException;
import java.io.OutputStream;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.Parametrizable;

/**
 * Interfaces a serializer, which is able to write a given graph in a special
 * format to a given output stream.
 * 
 * @version $Revision: 5767 $
 */
public interface OutputSerializer extends Serializer, Parametrizable {

    /**
     * Writes the contents of the given graph to a stream.
     * 
     * @param stream
     *            The output stream to save the graph to.
     * @param g
     *            The graph to save.
     */
    public void write(OutputStream stream, Graph g) throws IOException;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
