// =============================================================================
//
//   DualgraphBuilder.java
//
//   Copyright (c) 2001-2015, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarfas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.planarfas.attributes.EdgeAtt;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedComponent;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;
import org.graffiti.plugins.algorithms.planarity.faces.Dart;
import org.graffiti.plugins.algorithms.planarity.faces.Face;
import org.graffiti.plugins.algorithms.planarity.faces.Faces;

/**
 * Construct the dualgraph of a given graph.
 * 
 * @author Barbara Eckl
 * @version $Revision$ $Date$
 */
public class DualgraphBuilder extends AbstractAlgorithm {

    /**
     * Dualgraph is the constructed graph.
     */
    private Graph dualgraph;

    /**
     * DebugMode says, if debug informations are printed.
     */
    private boolean debugMode;

    /**
     * Planarity is used to get the faces of the Graph.
     */
    private PlanarityAlgorithm planarity;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        dualgraph = new AdjListGraph();
        Map<Face, Node> faceToNode = new HashMap<Face, Node>();

        // Get faces from the planarity algorithm
        TestedGraph testgraph = getPlanarity().getTestedGraph();
        List<TestedComponent> components = testgraph.getTestedComponents();
        Set<Face> faces = new HashSet<Face>();
        for (Iterator<TestedComponent> it = components.iterator(); it.hasNext();) {
            Faces compFaces = it.next().getFaces();
            faces.addAll(compFaces.getFaces());
        }

        // Add node in the dualgraph for each face in the graph
        int i = 0; // for debugMode
        for (Face face : faces) {
            Node node = dualgraph.addNode();
            faceToNode.put(face, node);
            if (debugMode) {
                node.setInteger("name", i);
                i++;
            }
        }

        // Add egde in the dualgraph for each edge in the graph
        for (Face face : faces) {
            List<Dart> darts = face.getDarts();
            for (Dart dart : darts) {
                Edge orgEdge = dart.getEdge();
                Node source = faceToNode.get(face);
                Node target = faceToNode.get(dart.getReverse().getFace());

                if (orgEdge.getSource().equals(dart.getSource())) {
                    boolean insert = true;
                    Edge equalEdge;
                    for (Iterator<Edge> it = source
                            .getDirectedOutEdgesIterator(); it.hasNext()
                            && insert;) {
                        equalEdge = it.next();
                        insert = !equalEdge.getTarget().equals(target);
                    }

                    if (insert) {
                        Edge dualEdge = dualgraph.addEdge(source, target, true);
                        dualEdge.setBoolean("planarFAS.ignore", true);
                        dualEdge.setBoolean("planarFAS.inCurrentCovering",
                                false);
                        Attribute dualEdgeAtt = new EdgeAtt("dualEdge");
                        dualEdgeAtt.setValue(dualEdge);

                        if (!orgEdge.containsAttribute("dualEdge")) {
                            orgEdge.addAttribute(dualEdgeAtt, "");
                        }

                        Attribute originalEdgeAtt = new EdgeAtt("orgEdge");
                        originalEdgeAtt.setValue(orgEdge);
                        dualEdge.addAttribute(originalEdgeAtt, "");
                    }
                }
            }
        }

        if (debugMode) {
            System.out.println("Edges in the dualgraph:");
            for (Iterator<Edge> it = dualgraph.getEdgesIterator(); it.hasNext();) {
                Edge edge = it.next();
                System.out.println(edge.getSource().getInteger("name") + "->"
                        + edge.getTarget().getInteger("name"));
            }
            System.out.println();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Construct a dualgraph";
    }

    /**
     * Returns the constructed dualgraph
     * 
     * @return dualgraph of the graph
     */
    public Graph getDualgraph() {
        return dualgraph;
    }

    /**
     * Sets the parameters
     * 
     * @param debugMode
     *            if debugMode is true, debug informations are printed out
     */
    public void setParams(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        BooleanParameter debugMode = new BooleanParameter(false, "debugMode",
                "Print debug informations");

        return new Parameter[] { debugMode };
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        debugMode = ((BooleanParameter) params[0]).getBoolean();
    }

    /**
     * Returns an instance of the planarity algorithm with an executed algorithm
     * 
     * @return instance of the planarity algorithm
     */
    private PlanarityAlgorithm getPlanarity() {
        if (planarity == null) {
            planarity = new PlanarityAlgorithm();
            planarity.attach(graph);
            planarity.execute();
        }
        return planarity;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
