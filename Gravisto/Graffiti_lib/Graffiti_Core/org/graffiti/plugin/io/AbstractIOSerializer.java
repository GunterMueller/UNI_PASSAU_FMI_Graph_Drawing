// =============================================================================
//
//   AbstractIOSerializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractIOSerializer.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;

/**
 * Provides additional methods to read and write from/to different kinds of
 * targets.
 * 
 * @version $Revision: 5767 $
 */
public abstract class AbstractIOSerializer implements InputSerializer,
        OutputSerializer {

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
    public void read(String filename, Graph g) throws IOException {
        read(new FileInputStream(filename), g);
    }

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
    public void read(URL url, Graph g) throws IOException {
        read(url.openStream(), g);
    }

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
    public abstract void read(InputStream in, Graph g) throws IOException;

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
    public Graph read(InputStream in) throws IOException {
        Graph g = new AdjListGraph();
        read(in, g);
        return g;
    }

    /**
     * Writes the contents of the given graph to a file.
     * 
     * @param filename
     *            The name of the file to save the graph to.
     * @param g
     *            The graph to save.
     * 
     * @exception IOException
     *                If an IO error occurs.
     */
    public void write(String filename, Graph g) throws IOException {
        write(new FileOutputStream(filename), g);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
