// =============================================================================
//
//   PlanarFASWithSCC.java
//
//   Copyright (c) 2001-2015, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarfas;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.algorithms.strongconnectivity.SCCAlgorithm;

/**
 * Execute the <code> SCCAlgorithm <code> to get the strong connected 
 * components. Then execute the <code> PlanarFAS<code> algorithm to get the 
 * feedback arc sets of the components.
 * 
 * @author Barbara Eckl
 * @version $Revision$ $Date$
 */
public class PlanarFASWithSCC extends AbstractAlgorithm {

    /**
     * Use Preprocessing in the PlanarFAS algorithm.
     */
    private boolean unablePreprocessing;

    /**
     * Color the edges in the feedback arc set.
     */
    private boolean GUIMode;

    /**
     * Set of edges, which are in the feedback arc set.
     */
    private Set<Edge> fasInGraph;

    /**
     * Sum of the number of edges in the original covers of the components.
     */
    private int sizeOriginalCovers;

    /**
     * Sum of the number of edges in the cover after the execute of Medonca's
     * und Eades' preprocessing.
     */
    private int sizeCoversAfterPreprocessing;

    /**
     * Constructor of the PlanarFASWithSCC
     * 
     * @param unablePreprocessing
     *            the algorithm use the preprocessing
     * @param GUIMode
     *            the edges in the feedback arc set were colored
     */
    public PlanarFASWithSCC(boolean unablePreprocessing, boolean GUIMode) {
        this.unablePreprocessing = unablePreprocessing;
        this.GUIMode = GUIMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        SCCAlgorithm sccAlgorithm = new SCCAlgorithm();
        sccAlgorithm.attach(graph);
        sccAlgorithm.execute();

        HashMap<Node, Node> nodesMap = new HashMap<Node, Node>();
        HashMap<Edge, Edge> edgesMap = new HashMap<Edge, Edge>();

        sizeOriginalCovers = 0;
        sizeCoversAfterPreprocessing = 0;

        List<Graph> strongConnectedComponents = sccAlgorithm
                .getStrongSubgraphs(nodesMap, edgesMap);

        PlanarFAS fasAlgorithm = new PlanarFAS(unablePreprocessing, false);
        Set<Edge> fasEdgesInComponents = new HashSet<Edge>();

        for (Graph currentGraph : strongConnectedComponents) {
            if (!currentGraph.isEmpty()) {
                fasAlgorithm.attach(currentGraph);
                fasAlgorithm.execute();
                fasEdgesInComponents.addAll(fasAlgorithm.getEdgesInFAS());
                sizeOriginalCovers = sizeOriginalCovers
                        + fasAlgorithm.getSizeOriginalCover();
                if (unablePreprocessing) {
                    sizeCoversAfterPreprocessing = sizeCoversAfterPreprocessing
                            + fasAlgorithm.getSizeCoverAfterPreprocessing();
                }
                fasAlgorithm.reset();
            }
        }

        fasInGraph = new HashSet<Edge>();

        for (Edge fasEdge : fasEdgesInComponents) {
            fasInGraph.add(edgesMap.get(fasEdge));
        }

        if (GUIMode) {
            colorEdges(fasInGraph);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "FAS with SCC";
    }

    /**
     * Returns the number of edges in the feedback arc set.
     * 
     * @return number of edges in the feedback arc set
     */
    public int getNumberOfEdgesInFAS() {
        return fasInGraph.size();
    }

    /**
     * Returns the number of edges in the original cover.
     * 
     * @return number of edges in the original cover
     */
    public int getSizeOriginalCover() {
        return sizeOriginalCovers;
    }

    /**
     * Returns the number of edges in the covers after Medonca's und Eades
     * preprocessing.
     * 
     * @return sum of number of edges in the covers
     */
    public int getSizeOriginalCoverWithPreprocessing() {
        return sizeCoversAfterPreprocessing;
    }

    /**
     * Colors the edges in the edgeSet
     * 
     * @param edgeSet
     *            set of edges, which has to be colored
     */
    private void colorEdges(Set<Edge> edgeSet) {
        for (Edge edge : edgeSet) {
            ColorAttribute framecolorAtt = (ColorAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FRAMECOLOR);
            ColorAttribute fillcolorAtt = (ColorAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FILLCOLOR);
            framecolorAtt.setColor(Color.RED);
            fillcolorAtt.setColor(Color.RED);
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
