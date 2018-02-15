// =============================================================================
//
//   Plantri.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout.plantri;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Plantri implements Iterator<Graph> {
    private static final String EXECUTABLE = Plantri.class.getResource(
        "plantri").getPath();
    private static final String HEADER = ">>planar_code<<";
    
    private BufferedInputStream input;
    private Graph nextGraph;
    
    public Plantri(int graphSize, String... options) {
        try {
            String[] ops = new String[options.length + 2];
            System.arraycopy(options, 0, ops, 2, options.length);
            ops[0] = EXECUTABLE;
            ops[1] = String.valueOf(graphSize);
            Process process = Runtime.getRuntime().exec(ops);
            input = new BufferedInputStream(process.getInputStream());
            process.waitFor();
            byte[] header = new byte[HEADER.length()];
            input.read(header);
            if (!HEADER.equals(new String(header))) {
                throw new IOException("Header \"" + HEADER + "\" expected but \"" + new String(header) + "\" found.");
            }
            reloadGraph();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void reloadGraph() {
        try {
            int nodeCount = input.read();
            if (nodeCount == -1) {
                nextGraph = null;
                input.close();
                return;
            } else {
                nextGraph = new FastGraph();
                Node[] nodes = new Node[nodeCount];
                for (int i = 0; i < nodeCount; i++) {
                    nodes[i] = nextGraph.addNode();
                }
                for (int i = 0; i < nodeCount; i++) {
                    int j;
                    
                    while ((j = input.read()) > 0) {
                        j--;
                        if (j > i) {
                            nextGraph.addEdge(nodes[i], nodes[j], false);
                        }
                    }
                    
                    if (j == -1) {
                        throw new IOException("Premature end of stream.");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void drain() {
        
        try {
            //input.re
            int b;
            while ((b = input.read()) != -1) {
                System.out.println(b + " ('" + ((char) b) + "')");
            }
        } catch (IOException e) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return nextGraph != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph next() {
        Graph result = nextGraph;
        
        if (result == null) {
            throw new NoSuchElementException();
        } else {
            reloadGraph();
            return result;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not implemented.");
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
