// =============================================================================
//
//   AbstractOutputSerializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractOutputSerializer.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.io;

import java.io.FileOutputStream;
import java.io.IOException;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.AbstractParametrizable;

/**
 * Provides additional methods to write a graph object.
 * 
 * @version $Revision: 5767 $
 */
public abstract class AbstractOutputSerializer extends AbstractParametrizable
        implements OutputSerializer {

    /**
     * Writes the contents of the given graph to a file.
     * 
     * @param g
     *            The graph to save.
     * @param filename
     *            The name of the file to save the graph to.
     * 
     * @exception IOException
     *                If an IO error occurs.
     */
    public void write(Graph g, String filename) throws IOException {
        write(new FileOutputStream(filename), g);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
