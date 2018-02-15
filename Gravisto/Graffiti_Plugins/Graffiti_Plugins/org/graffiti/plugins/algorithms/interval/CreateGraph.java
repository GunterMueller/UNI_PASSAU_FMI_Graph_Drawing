// =============================================================================
//
//   CreateGraph.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.springembedderFR.FRSpringAlgorithmStandard;
import org.graffiti.selection.Selection;
import org.graffiti.session.Session;

/**
 * This class is used to compute and draw an interval graph. To draw the graph,
 * an implementation of SpringEmbedder is called.
 * 
 * @author struckmeier
 */
public class CreateGraph {

    /**
     * Constructor of the class.
     * 
     * @param g
     * @return the graph that is calculated by createGraph().
     */
    public Graph create(Graph g) {
        return createGraph(g);
    }

    /**
     * This method compares intervals represented by nodes using their
     * coordinates. If the coordinates overlap, the method adds an edge to the
     * corresponding nodes.
     * 
     * @param oldGraph
     * @return an interval graph computed using the interval representation.
     */
    private Graph createGraph(Graph oldGraph) {
        Graph newGraph = null;
        try {
            MainFrame mf = GraffitiSingleton.getInstance().getMainFrame();
            Session s = mf.addNewSession();
            newGraph = s.getGraph();
        } catch (Exception e) {
            newGraph = new FastGraph();
        }

        Iterator<Node> it = oldGraph.getNodesIterator();
        LinkedList<Node> newNodes = new LinkedList<Node>();
        Node[] nodes = new Node[oldGraph.getNumberOfNodes()];

        int n = 0;
        while (it.hasNext()) {
            Node oldNode = it.next();
            CollectionAttribute col = (CollectionAttribute) oldNode
                    .getAttributes().copy();
            Node newNode = newGraph.addNode(col);
            newNodes.add(newNode);
            nodes[n] = newNode;
            n++;
        }
        for (int i = 0; i < newGraph.getNodes().size(); i++) {
            for (int j = i - 1; j >= 0; j--) {
                if (compareCoords(nodes[i], nodes[j])) {
                    newGraph.addEdge(nodes[i], nodes[j], false);
                }
            }
        }

        List<Node> allNodes = newGraph.getNodes();
        Iterator<Node> iterate = allNodes.iterator();
        while (iterate.hasNext()) {
            Node node = iterate.next();
            NodeGraphicAttribute nga = (NodeGraphicAttribute) node
                    .getAttribute("graphics");
            DimensionAttribute dim = new DimensionAttribute("Dimesnion", 25.0,
                    25.0);
            nga.setDimension(dim);
        }

        FRSpringAlgorithmStandard fr = new FRSpringAlgorithmStandard();
        fr.attach(newGraph);
        Parameter<?>[] parameters = new Parameter[2];
        parameters[0] = new SelectionParameter(new Selection("_temp_"), "", "");
        parameters[1] = new BooleanParameter(false, "", "");
        fr.setParameters(parameters);
        fr.execute();

        return newGraph;
    }

    /**
     * This method is used to compare the coordinates of two intervals
     * represented by nodes.
     * 
     * @param a
     * @param b
     * @return true if the intervals overlap.
     */
    private Boolean compareCoords(Node a, Node b) {
        NodeGraphicAttribute aNga = (NodeGraphicAttribute) a
                .getAttribute("graphics");
        CoordinateAttribute aCoord = aNga.getCoordinate();
        double aCenter = aCoord.getX();
        double aWidth = aNga.getDimension().getWidth();

        NodeGraphicAttribute bNga = (NodeGraphicAttribute) b
                .getAttribute("graphics");
        CoordinateAttribute bCoord = bNga.getCoordinate();
        double bCenter = bCoord.getX();
        double bWidth = bNga.getDimension().getWidth();

        Boolean haveEdge = false;

        double aplus = aCenter + aWidth / 2;
        double aminus = aCenter - aWidth / 2;
        double bplus = bCenter + bWidth / 2;
        double bminus = bCenter - bWidth / 2;
        if ((aminus >= bminus) && (aminus <= bplus)) {
            haveEdge = true;
        } else if ((aplus >= bminus) && (aplus <= bminus)) {
            haveEdge = true;
        } else if ((bplus <= aplus) && (bplus >= aminus)) {
            haveEdge = true;
        } else if ((bminus >= aminus) && (bminus <= aplus)) {
            haveEdge = true;
        }
        return haveEdge;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
