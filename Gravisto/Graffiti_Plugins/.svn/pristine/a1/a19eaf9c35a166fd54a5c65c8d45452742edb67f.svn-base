// =============================================================================
//
//   GraphIO.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.graffiti.graph.Graph;

/**
 * @author Gerg�
 * @version $Revision$ $Date$
 */
public class GraphIO {

    /**
     * Loads a graph from a file
     * 
     * @param fileName
     */
    public static Graph loadGraph(String fileName) {

        GraphMLReader gr = new GraphMLReader();
        File file = new File(Config.testDataLocation + fileName);
        InputStream is = null;
        Graph g = null;

        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException exp) {
            exp.printStackTrace();
        }

        try {
            g = gr.read(is);
        } catch (IOException exp) {
            exp.printStackTrace();
        }

        return g;
    }

    /**
     * saves a graph
     * 
     * @param graph
     * @param fileName
     */
    public static void saveGraph(Graph graph, String fileName) {

        GraphMLWriter gw = new GraphMLWriter();
        File file1 = new File(Config.testDataLocation + fileName);
        OutputStream os = null;

        try {
            os = new FileOutputStream(file1);
        } catch (FileNotFoundException exp) {
            exp.printStackTrace();
        }

        try {
            gw.write(os, graph);
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
