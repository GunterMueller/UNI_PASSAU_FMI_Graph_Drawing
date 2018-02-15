// =============================================================================
//
//   MinimumFillinMatrixGraph.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

//   MinimumFillin.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeWidth;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;

/**
 * Implements the Minimum Fill-in Method and Minimum Fill-in excluding one
 * neighbor Method.
 * 
 * @author wangq
 * @version $Revision$ $Date$
 */
public class MinimumFillIn {
    /**
     * the treeWidthNode
     */
    private ArrayList<TreeWidthNode> twNodes;
    /**
     * the matrixGraph
     */
    private MatrixGraph matrixGraph;
    /**
     * the original graph
     */
    private Graph graph;
    /**
     * the low bound.
     */
    private int lw = 0;
    /**
     * the upper bound.
     */
    private int uw = 0;

    /**
     * Constructs a new instance.
     * 
     * @param mag
     *            is matrixgraph
     * @param graph
     *            is the graph
     */
    public MinimumFillIn(MatrixGraph mag, Graph graph) {
        this.matrixGraph = mag;
        this.twNodes = new ArrayList<TreeWidthNode>();
        this.graph = graph;

    }

    /**
     * Returns the twNodes.
     * 
     * @return the twNodes.
     */
    public ArrayList<TreeWidthNode> getTwNodes() {
        return this.twNodes;
    }

    /**
     * Sets the twNodes.
     * 
     * @param twNodes
     *            the twNodes to set.
     */
    public void setTwNodes(ArrayList<TreeWidthNode> twNodes) {
        this.twNodes = twNodes;
    }

    /**
     * If the node degree is one, the node and his neighbor are in the same
     * treeWidthNode.
     * 
     */
    public void degreeOne() {
        for (int i = 0; i < matrixGraph.getNodeSize(); i++) {
            Node node = (graph.getNodes().get(i));
            if (node.getOutDegree() == 1) {
                update((matrixGraph.getSuNodes().get(i)), "Sim");
            }
        }
    }

    /**
     * Calculate the upper bound and the run time with Minimum Fill-in Method.
     * 
     * @return the value of the upper bound and the run time.
     */
    public ArrayList<Object> calculateFillin() {
        ArrayList<Object> result = new ArrayList<Object>();
        long current = System.currentTimeMillis();

        if (graph.getNodes().size() > 2) {
            degreeOne();
        }
        searchSimplicialNode();
        while (!this.matrixGraph.isEmpty()) {
            searchNode();
        }
        result.add(uw);
        result.add(System.currentTimeMillis() - current);
        return result;
    }

    /**
     * Finds simplicial node. The simpicial node and his neighbors lie in a
     * treeWidthNode.
     * 
     */
    public void searchSimplicialNode() {

        ArrayList<SuperNode> tmpSuNodes = new ArrayList<SuperNode>();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            SuperNode tmpNode = matrixGraph.getSuNodes().get(i);
            if (tmpNode.getLackOfEdgeSim() == 0 && !tmpNode.isFinish()) {
                update(tmpNode, "Sim");
            }
        }

    }

    /**
     * Finds the node, which has minimal fill-in edges. Inserts the fill-in
     * edges.
     * 
     */
    public void searchNode() {
        ArrayList<Object> sortedNodes1 = new ArrayList<Object>();
        ArrayList<Object> sortedNodes2 = new ArrayList<Object>();
        ArrayList<Object> tmpSuNodes = new ArrayList<Object>();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            SuperNode tmpNode = matrixGraph.getSuNodes().get(i);
            if (tmpNode.isFinish() == false) {
                tmpSuNodes.add(tmpNode);
            }
        }
        sortedNodes1 = Heapsort.heapsort(tmpSuNodes, new DegreeComp(
                DegreeComp.LACK));
        sortedNodes2 = Heapsort.heapsort(sortedNodes1, new DegreeComp(
                DegreeComp.DEG));
        SuperNode suNode = (SuperNode) sortedNodes2.get(0);
        update(suNode, "Sim");
    }

    /**
     * Calculate the upper bound and the run time with Minimum Fill-in excluding
     * one neighbor Method.
     * 
     * @return the values of the low and upper bound and the run time.
     */
    public ArrayList<Object> calculateFillin1() {
        ArrayList<Object> result = new ArrayList<Object>();
        MatrixGraph graphMap = new MatrixGraph(this.graph);
        graphMap.createMatrixGraph();
        LowSetting l = new LowSetting(graphMap);
        l.calculatelow();
        lw = l.getLowDegree();
        result.add(lw);
        long current = System.currentTimeMillis();
        if (this.graph.getNodes().size() > 2) {
            degreeOne();
        }

        uw = lw;
        while (!this.matrixGraph.isEmpty()) {
            searchNode1();
        }
        result.add(uw);
        result.add(System.currentTimeMillis() - current);
        return result;
    }

    /**
     * Finds the node, which has fill-in excluding one neighbor an whose edges
     * are smaller than the minimal fill-in edges. Inserts the fill-in excluding
     * one neighbor edges.
     * 
     */
    public void searchNode1() {
        ArrayList<Object> sortedNodes = new ArrayList<Object>();
        // case 1 the vertex is a simpicial vertex
        ArrayList<Object> tmpSuNodes1 = new ArrayList<Object>();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            SuperNode tmpNode1 = matrixGraph.getSuNodes().get(i);
            if (!tmpNode1.isFinish()) {
                tmpSuNodes1.add(tmpNode1);
            }
        }
        sortedNodes = Heapsort.heapsort(tmpSuNodes1, new DegreeComp(
                DegreeComp.LACK));
        SuperNode suNode1 = (SuperNode) sortedNodes.get(0);
        // case 2 the vertex is an almost simplicial vertex
        ArrayList<Object> tmpSuNodes2 = new ArrayList<Object>();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            SuperNode tmpNode = matrixGraph.getSuNodes().get(i);
            if (!tmpNode.isFinish()) {
                tmpSuNodes2.add(tmpNode);
            }
        }
        sortedNodes = Heapsort.heapsort(tmpSuNodes2, new DegreeComp(
                DegreeComp.LACKNEIGH));
        SuperNode suNode2 = (SuperNode) sortedNodes.get(0);

        if (suNode1.getLackOfEdgeSim() == 0) {

            update(suNode1, "Sim");
        } else if ((suNode2.getLackOfEdgeFaSim() == 0)
                && (suNode2.getDegree() <= lw)) {
            update(suNode2, "FastSim");
        } else if ((suNode2.getLackOfEdgeFaSim() <= suNode1.getLackOfEdgeSim())
                && (suNode2.getDegree() <= lw)) {
            // case 3 the node has fill-in excluding one neighbor
            // edges smaller than the minimal fill-in edges.
            update(suNode2, "FastSim");
        } else {
            // case 4 the node has fill-in excluding one neighbor
            // edges bigger than the minimal fill-in edges.
            update(suNode1, "Sim");
        }
    }

    /**
     * Updates the matrix of edges. Inserts the fill-in edges in the matrix.
     * Computes the fill-in edges and fill-in excluding one neighbor edges in
     * the new matrix.
     * 
     * @param suNode
     * @param NodeTyp
     */
    public void update(SuperNode suNode, String NodeTyp) {

        TreeWidthNode twNode = new TreeWidthNode(this.matrixGraph, this.graph);
        twNode.setMainNode(suNode);
        this.twNodes.add(twNode);
        if (uw < twNode.getSize() - 1) {
            uw = twNode.getSize() - 1;
        }

        if (suNode.getLackOfEdgeSim() != 0) {
            Node node = suNode.getNode();
            for (int m = 0; m < graph.getNodes().size(); m++) {
                if (matrixGraph.getMatrixEdge()[graph.getNodes().indexOf(node)][m] != 0) {
                    for (int n = 0; n < graph.getNodes().size(); n++) {
                        if (matrixGraph.getMatrixEdge()[graph.getNodes()
                                .indexOf(node)][n] != 0) {
                            if (matrixGraph.getMatrixEdge()[m][n] == 0
                                    && m != n) {
                                matrixGraph.getMatrixEdge()[m][n] = 1;

                                Edge e = this.graph
                                        .addEdge(graph.getNodes().get(m), graph
                                                .getNodes().get(n), false);

                                EdgeGraphicAttribute ega = (EdgeGraphicAttribute) e
                                        .getAttribute("graphics");
                                ega.getFillcolor().setColor(Color.blue);
                                ega.getFramecolor().setColor(Color.blue);

                            }
                        }
                    }
                }
            }
            Iterator<SuperNode> it = matrixGraph.getSuNodes().iterator();
            while (it.hasNext()) {
                SuperNode ss = it.next();
                if (!ss.isFinish()) {
                    matrixGraph.degrees(ss);
                    matrixGraph.neighbors(ss);
                    matrixGraph.comLackOfEdgesSim(ss);
                    if (NodeTyp == "FastSim") {
                        matrixGraph.comLackOfEdgesFaSim(ss);
                    }
                }
            }
        }

        ArrayList<SuperNode> cliqueNodes = new ArrayList<SuperNode>();

        // cliqueNode
        for (int i = 0; i < suNode.getNeighbors().size(); i++) {
            Node snode = (suNode.getNeighbors().get(i)).getNode();
            SuperNode nehSuNode = matrixGraph.getSuNodes().get(
                    graph.getNodes().indexOf(snode));

            if (nehSuNode.getDegree() == suNode.getDegree()
                    && nehSuNode.getLackOfEdgeSim() == 0) {

                cliqueNodes.add(nehSuNode);
                twNode.getCliqueNodes().add(nehSuNode.getNode());
            }
        }
        cliqueNodes.add(suNode);
        twNode.getCliqueNodes().add(suNode.getNode());

        // eliminate the clique node
        for (int j = 0; j < cliqueNodes.size(); j++) {
            SuperNode cliq = cliqueNodes.get(j);
            for (int k = 0; k < graph.getNodes().size(); k++) {
                matrixGraph.getMatrixEdge()[matrixGraph.getSuNodes().indexOf(
                        cliq)][k] = 0;
                matrixGraph.getMatrixEdge()[k][matrixGraph.getSuNodes()
                        .indexOf(cliq)] = 0;
            }
            cliq.setFinish(true);
            cliq.setDegree(0);
            cliq.setNeighbors(null);
        }

        for (int i = 0; i < graph.getNodes().size(); i++) {
            SuperNode superNode = matrixGraph.getSuNodes().get(i);
            if (!superNode.isFinish()) {

                matrixGraph.degrees(superNode);
                matrixGraph.neighbors(superNode);
                matrixGraph.comLackOfEdgesSim(superNode);
                if (NodeTyp == "FastSim") {
                    matrixGraph.comLackOfEdgesFaSim(superNode);
                }
            }
        }
    }

    /**
     * Returns the uw.
     * 
     * @return the uw.
     */
    public int getUw() {
        return uw;
    }

    /**
     * Sets the uw.
     * 
     * @param uw
     *            the uw to set.
     */
    public void setUw(int uw) {
        this.uw = uw;
    }

    /**
     * Returns the lw.
     * 
     * @return the lw.
     */
    public int getLw() {
        return lw;
    }

    /**
     * Sets the lw.
     * 
     * @param lw
     *            the lw to set.
     */
    public void setLw(int lw) {
        this.lw = lw;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
