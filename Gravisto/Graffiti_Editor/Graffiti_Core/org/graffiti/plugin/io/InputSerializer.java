// =============================================================================
//
//   InputSerializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: InputSerializer.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.Parametrizable;

/**
 * Interfaces a serializer, which is able to reconstruct a graph from a given
 * input.
 * 
 * @version $Revision: 5767 $
 */
public interface InputSerializer extends Serializer, Parametrizable {

    /**
     * Reads in a graph from the given filename.
     * 
     * @param filename
     *            The name of the file to read the graph from.
     * @param g
     *            The graph to add the newly read graph to.
     * 
     * @exception IOException
     *                If an IO error occurs.
     */
    public void read(String filename, Graph g) throws IOException;

    /**
     * Reads in the graph from the given url.
     * 
     * @param url
     *            The URL to read the graph from.
     * @param g
     *            The graph to add the newly read graph to.
     * 
     * @exception IOException
     *                If an IO error occurs.
     */
    public void read(URL url, Graph g) throws IOException;

    /**
     * Reads in a graph from the given input stream.
     * 
     * @param in
     *            The input stream to read the graph from.
     * @param g
     *            The graph to add the newly read graph to.
     * 
     * @exception IOException
     *                If an IO error occurs.
     */
    public void read(InputStream in, Graph g) throws IOException;

    /**
     * Reads in a graph from the given input stream.
     * 
     * @param in
     *            The input stream to read the graph from.
     * @return The newly read graph.
     * 
     * @exception IOException
     *                If an IO error occurs.
     */
    public Graph read(InputStream in) throws IOException;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
