// =============================================================================
//
//   GraphWriter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphWriter.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.ios.gml.attributemapping.AttributeMapping;
import org.graffiti.plugins.ios.gml.attributemapping.GraphAttributeMapping;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Class <code>GraphWriter</code> is responsible for writing the graph along
 * with its nodes and edges. This class cares about the former while the latter
 * will be delegated.
 * 
 * @author ruediger
 */
class GraphWriter extends AbstractWriter {

    /** The logger for this class. */
    private static final Logger logger = Logger.getLogger(GraphWriter.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The edge writer for writing out edges. */
    private EdgeWriter ew;

    /** The attribute mapping for graphs. */
    private GraphAttributeMapping mapping;

    /** The node writer for writing out nodes. */
    private NodeWriter nw;

    /**
     * Constructs a new <code>GraphWriter</code>.
     * 
     * @param os
     *            the output stream the graph shall be written to.
     */
    GraphWriter(OutputStream os) {
        super(os);

        NodeMap nodeMap = new NodeMap();
        this.nw = new NodeWriter(os, nodeMap);
        this.ew = new EdgeWriter(os, nodeMap);
        this.mapping = new GraphAttributeMapping();
    }

    /**
     * Returns the attribute mapping for graphs.
     * 
     * @return the attribute mapping for graphs.
     */
    @Override
    AttributeMapping getMapping() {
        return this.mapping;
    }

    /**
     * Returns a String representation of the value of the specified attribute
     * which is not the trivial representation that
     * <code>attr.getValue().toString()</code> would return.
     * 
     * @param attr
     *            the attribute of which to compute the non-trivial string
     *            representation.
     * 
     * @return a non-trivial string represenation of the specified attribute.
     * 
     * @throws NoSpecialValueException
     *             if there is no handling provided for this attribute.
     */
    @Override
    String getSpecialValue(Attribute attr) throws NoSpecialValueException {
        String path = attr.getPath();

        if (path.equals(""))
            return "";
        else
            throw new NoSpecialValueException(
                    "no special treatment for attribute " + "at path \""
                            + attr.getPath() + "\" available.");
    }

    /**
     * Writes the given graph to the corresponding output stream.
     * 
     * @param g
     *            the graph to be written.
     * 
     * @throws IOException
     *             if an error occurs while writing the graph.
     */
    void write(Graph g) throws IOException {
        logger.setLevel(Level.OFF);
        // logger.info("start writing graph.");
        os.write(new String("graph [\n").getBytes());
        indent(OFFSET);
        os.write(new String("creator \"Gravisto GML writer\"\n").getBytes());
        indent(OFFSET);

        // is the graph directed?
        if (g.isDirected()) {
            os.write(new String("directed 1\n").getBytes());
        } else if (g.isUndirected()) {
            os.write(new String("directed 0\n").getBytes());
        } else {
            // logger.warning("graph contains directed and undirected edges.");
        }

        // write graph attributes
        // logger.info("start writing graph attributes.");

        CollectionAttribute ca = g.getAttributes();
        writeAttributes(ca, OFFSET);
        // logger.info("finished writing graph attributes.");

        // write the nodes
        // logger.info("start writing nodes.");

        for (Iterator<Node> itr = g.getNodesIterator(); itr.hasNext();) {
            Node n = itr.next();
            nw.write(n, OFFSET);
        }

        // logger.info("finished writing nodes.");

        // write the edges
        // logger.info("start writing edges.");

        for (Iterator<Edge> itr = g.getEdgesIterator(); itr.hasNext();) {
            Edge e = itr.next();
            ew.write(e, OFFSET);
        }

        // logger.info("finished writing edges.");

        os.write(new String("]\n").getBytes());
        // logger.info("finished writing graph.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
