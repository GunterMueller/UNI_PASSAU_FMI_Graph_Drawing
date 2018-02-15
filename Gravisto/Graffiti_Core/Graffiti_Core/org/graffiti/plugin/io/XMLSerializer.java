// =============================================================================
//
//   XMLSerializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: XMLSerializer.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.parameter.Parameter;

/**
 * Reads and Writes a graph in XML. TODO: specify the XML format.
 * 
 * @version $Revision: 5767 $
 */
public class XMLSerializer extends AbstractIOSerializer {

    /**
     * Returns the extensions the serializer provides.
     * 
     * @return DOCUMENT ME!
     */
    public String[] getExtensions() {
        return null;
    }

    /**
     * Reads in a graph from the given input stream.
     * 
     * @param in
     *            The input stream to read the graph from.
     * @param g
     *            The graph to add the newly read graph to.
     */
    @Override
    public void read(InputStream in, Graph g) {
        // TODO
    }

    /**
     * Writes the contents of the given graph to a stream.
     * 
     * @param stream
     *            The stream to save the graph to.
     * @param g
     *            The graph to save.
     */
    public void write(OutputStream stream, Graph g) {
        // TODO
    }

    /*
     * @see org.graffiti.plugin.io.InputSerializer#read(java.io.InputStream)
     */
    @Override
    public Graph read(InputStream in) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * @see org.graffiti.plugin.Parametrizable#getDefaultParameters()
     */
    @Override
    public Parameter<?>[] getDefaultParameters() {
        return new Parameter[0];
    }

    /*
     * @see org.graffiti.plugin.Parametrizable#getName()
     */
    @Override
    public String getName() {
        return "XML Exporter";
    }

    /*
     * @see org.graffiti.plugin.Parametrizable#getParameters()
     */
    @Override
    public Parameter<?>[] getParameters() {
        return new Parameter[0];
    }

    /*
     * @see
     * org.graffiti.plugin.Parametrizable#setParameters(org.graffiti.plugin.
     * parameter.Parameter<?>[])
     */
    @Override
    public void setParameters(Parameter<?>[] params) {
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
