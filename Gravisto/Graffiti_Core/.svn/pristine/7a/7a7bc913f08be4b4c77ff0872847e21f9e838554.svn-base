/*
 * Created on Jan 14, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package org.graffiti.plugins.algorithms.circulardrawing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

// import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * @author demirci
 */
public class SymmetrischTest {

    public static void main(String[] args) {

        Graph graph = new AdjListGraph();
        int h = 12;
        int nodeNumber = (int) Math.pow(2, h) - 1;
        Node[] binTree1 = new Node[nodeNumber];
        Node[] binTree2 = new Node[nodeNumber];
        Map bTree1 = new HashMap();
        Map bTree2 = new HashMap();

        for (int i = 0; i < h; i++) {
            List levelList = new ArrayList();
            List levelList2 = new ArrayList();
            bTree1.put(new Integer(i), levelList);
            bTree2.put(new Integer(i), levelList2);
        }

        for (int i = 0; i < h; i++) {
            int k = (int) Math.pow(2, i);
            List levelList = (List) bTree1.remove(new Integer(i));
            List levelList2 = (List) bTree2.remove(new Integer(i));
            for (int j = 0; j < k; j++) {
                Node n = graph.addNode();
                levelList.add(n);
                Node v = graph.addNode();
                levelList2.add(v);
            }
            bTree1.put(new Integer(i), levelList);
            bTree2.put(new Integer(i), levelList2);
        }

        for (int i = 0; i < h - 1; i++) {

            List levelListI = (List) bTree1.get(new Integer(i));
            List levelListII = (List) bTree1.get(new Integer(i + 1));

            List levelListIII = (List) bTree2.get(new Integer(i));
            List levelListIV = (List) bTree2.get(new Integer(i + 1));

            int k = 0;
            for (int j = 0; j < levelListI.size(); j++) {
                Node n = (Node) levelListI.get(j);
                Node v = (Node) levelListIII.get(j);

                for (int t = k; t <= k; t++) {

                    Node n1 = (Node) levelListII.get(k);
                    Node n2 = (Node) levelListII.get(k + 1);
                    graph.addEdge(n, n1, false);
                    graph.addEdge(n, n2, false);

                    Node v1 = (Node) levelListIV.get(k);
                    Node v2 = (Node) levelListIV.get(k + 1);
                    graph.addEdge(v, v1, false);
                    graph.addEdge(v, v2, false);
                    k = t;
                }
                if (k >= levelListII.size() - 2) {
                    break;
                } else {
                    k = k + 2;
                }
            }
        }
        int level = h - 1;
        List levelList1 = (List) bTree1.get(new Integer(level));
        List levelList2 = (List) bTree2.get(new Integer(level));
        for (int i = 0; i < levelList1.size(); i++) {
            Node b1 = (Node) levelList1.get(i);
            Node b2 = (Node) levelList2.get(i);
            graph.addEdge(b1, b2, false);
        }

        graph.setDirected(false);
        String s = "symmetrisch" + new Integer(h).toString();
        GmlWriter gw = new GmlWriter();
        File file1 = new File(
                "/home/cip/demirci/Graffiti_CircularDrawing/symmetrischTestGraphs/"
                        + s + ".gml");
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

        // Eingabe Graph ist ungerichtet.
        // graph.setDirected(false);
        String filename = "";
        filename = "figur1_paper";
        // filename = "grossebsp2";
        // filename = "presentation";
        // filename = "komplexebsp";
        // filename = "presentation_festNodeRF";
        // filename = "data";
        GmlReader gr = new GmlReader();
        File file = new File("/home/cip/demirci/"
                + "Graffiti_CircularDrawing/bspgraphs/" + filename + ".gml");

        InputStream is = null;

        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException exp) {
            exp.printStackTrace();
        }

        try {
            graph = gr.read(is);
        } catch (IOException exp) {
            exp.printStackTrace();
        }

        Circular circular = new Circular();
        // circular.attach(graph);
        DFSCircular dfsCircular = new DFSCircular();
        CircularConst.TEST = 1;
        // dfsCircular.attach(graph);
        /*
         * try { //circular.check(); dfsCircular.check(); } catch
         * (PreconditionException e){ e.printStackTrace(); }
         */

        // Fuehre den Algorithmus aus.
        // circular.execute();
        // dfsCircular.execute();
        /*
         * Graph circularLayout =
         * (Graph)circular.getResult().getResult().get("circularLayout"); List l
         * = (List)circular.getResult().getResult().get("nodeOrdering");
         * 
         * CircularPostprocessing circularPostprocessing = new
         * CircularPostprocessing();
         * circularPostprocessing.attach(circularLayout); try {
         * circularPostprocessing.check(); } catch (PreconditionException e){
         * e.printStackTrace(); }
         * 
         * circularPostprocessing.execute();
         */
    }
}
