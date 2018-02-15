// =============================================================================
//
//   GraphTest.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphTest.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.datastructure.test;

import java.util.Iterator;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.util.GeneralEditorUtils;

/**
 * This is a class which tests the graph data structure
 * 
 * @author chris
 */
public class GraphTest {

    /**
     * This is the main method.
     * 
     * @param args
     *            Paramters from console
     */
    public static void main(String[] args) {
        Graph g = new AdjListGraph();
        Node n1 = g.addNode();
        LabelAttribute lA1 = new NodeLabelAttribute("label");
        lA1.setLabel(Integer.toString(3));
        n1.addAttribute(lA1, "");

        Node n2 = g.addNode();
        LabelAttribute lA2 = new NodeLabelAttribute("label");
        lA2.setLabel(Integer.toString(4));
        n2.addAttribute(lA2, "");

        Node n3 = g.addNode();
        GeneralEditorUtils.setLabel(n3, "5");

        g.addEdge(n1, n2, true);
        g.addEdge(n2, n1, true);

        System.out.println(g.isDirected());

        Iterator<Node> nodeIt = g.getNodesIterator();
        int i = 0;

        while (nodeIt.hasNext()) {
            i++;

            Node n = nodeIt.next();

            n.setInteger("chris.meinAttr", 5);
            System.out.print("[");
            System.out.print(n);
            System.out.println("]");
        }

        System.out.println("\nnach Labelling");
        nodeIt = g.getNodesIterator();

        while (nodeIt.hasNext()) {
            Node n = nodeIt.next();
            String label = n.getString("label.label");
            System.out.println(label);
        }

        /*
         * Preferences prefs =
         * Preferences.userNodeForPackage(GraffitiEditor.class); PluginManager
         * pm = new DefaultPluginManager(prefs.node("pluginmgr")); //
         * AttributeTypesManager attributeTypesManager = // new
         * AttributeTypesManager(); //
         * pm.addPluginManagerListener(attributeTypesManager); try {
         * pm.loadStartupPlugins(); } catch(PluginManagerException e) {
         * e.printStackTrace(); } System.out.println("GML Plugin installed: " +
         * pm.isInstalled("Default GML Writer")); File file = new
         * File("/temp/test.gml"); IOManager ioManager = new DefaultIOManager();
         * try { OutputSerializer os = ioManager.createOutputSerializer(".gml");
         * System.out.println("ioManager has output serializer: " +
         * ioManager.hasOutputSerializer()); os.write(new
         * FileOutputStream(file), g); } catch(Exception e) {
         * e.printStackTrace(); }
         */
        /*
         * Algorithm bfs = new BFS(); // Parameter[] params = new Parameter[1];
         * // params[0] = new NodeParameter(n1, "s", "start node"); //
         * bfs.setParameters(params); ((BFS)bfs).setSourceNode(n1);
         * bfs.attach(g); try { bfs.check(); } catch (PreconditionException e) {
         * e.printStackTrace(); } bfs.execute();
         * 
         * 
         * System.out.println("\nnach BFS"); nodeIt = g.getNodesIterator();
         * while (nodeIt.hasNext()) { Node n = (Node)nodeIt.next(); String label
         * = n.getString("label.label"); System.out.println(label); }
         */
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
