// =============================================================================
//
//   DOTSerializer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DOTSerializer.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.exporters.graphviz;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.io.AbstractOutputSerializer;

/**
 * A very very simple writer for a graph in the dot format for <a
 * href="http://www.research.att.com/sw/tools/graphviz/">Graphviz</a>. The only
 * exported attribute are labels.
 * 
 * @version $Revision: 5766 $ $Date: 2009-03-19 17:53:32 +0100 (Do, 19 Mrz 2009)
 *          $
 */
public class DOTSerializer extends AbstractOutputSerializer {
    /** String representing one indentation level. */
    private static final String TAB = "    ";

    /** Internal map saving the ids assigned to the nodes. */
    private Map<Node, Integer> nodes;

    /** Internal id the next node will be given. */
    private int nodeCount;

    /*
     * @see org.graffiti.plugin.io.Serializer#getExtensions()
     */
    public String[] getExtensions() {
        return new String[] { ".dot" };
    }

    /*
     * @see org.graffiti.plugin.io.OutputSerializer#write(java.io.OutputStream,
     * org.graffiti.graph.Graph)
     */
    public synchronized void write(OutputStream out, Graph g)
            throws IOException {
        PrintStream stream = new PrintStream(out);

        nodes = new HashMap<Node, Integer>();
        nodeCount = 1;

        // open graph
        if (g.isDirected()) {
            stream.print("di");
        }

        stream.println("graph myGraph {");

        // print nodes
        for (Iterator<Node> i = g.getNodesIterator(); i.hasNext();) {
            writeNode(stream, i.next());
        }

        // print edges
        for (Iterator<Edge> i = g.getEdgesIterator(); i.hasNext();) {
            writeEdge(stream, i.next());
        }

        // close graph
        stream.println("}");

        // finished
        stream.close();
    }

    /**
     * Write one edge in dot format.
     * 
     * @param stream
     *            The stream to which the edge is written
     * @param edge
     *            The edge that is written
     */
    private void writeEdge(PrintStream stream, Edge edge) {
        stream.print(TAB + nodes.get(edge.getSource()).toString());

        if (edge.isDirected()) {
            stream.print(" -> ");
        } else {
            stream.print(" -- ");
        }

        stream.print(nodes.get(edge.getTarget()).toString());
        stream.println(";");
    }

    /**
     * Write one node in dot format.
     * 
     * @param stream
     *            The stream to which the node is written
     * @param node
     *            The node that is written
     */
    private void writeNode(PrintStream stream, Node node) {
        stream.print(TAB + nodeCount);

        try {
            String label = node.getString("label.label");

            if ((label != null) && !label.equals("")) {
                stream.print(" [ label = \"" + label.trim() + "\" ]");
            }
        } catch (AttributeNotFoundException e) {
            // no label has been found: ignore
        }

        nodes.put(node, new Integer(nodeCount++));

        stream.println(";");
    }

    public String getName() {
        return "DOT Exporter";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
