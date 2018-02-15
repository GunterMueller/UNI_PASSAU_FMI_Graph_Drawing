// =============================================================================
//
//   CliqueAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CliqueAlgorithm.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.confluentDrawing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * list all the cliques in graph in O(a(G)m) time per clique, where a(G) is the
 * arboricity of graph and m is the number of edges.
 * 
 * @author Xiaolei Zhang
 * @version 0.0.1 21.08.2005
 */
public class CliqueAlgorithm {

    /** the sorted nodes with ascending degree */
    private ArrayList sortedNode = null;

    /** the graph to execute */
    private Graph graph = null;

    /** the resulting maximal complete subgraph */
    private Set clique = null;

    /** the edges between nodes in clique */
    private Collection edges = null;

    /** for testing whether the Clique is the lexicographically largest clique */
    private int[] lexico = null;

    /** for testing whether the candidate of a new clique */
    private int[] maximality = null;

    /** the flag for updating the current clique */
    private boolean flag;

    /**
     * Creates a new CliqueAlgorithm object.
     * 
     * @param g
     *            the graph
     */
    public CliqueAlgorithm(Graph g) {
        this.graph = g;
    }

    /**
     * return the clique.
     * 
     * @return Returns the clique.
     */
    public Set getClique() {
        return clique;
    }

    /**
     * return edges in the clique.
     * 
     * @return Returns edges in the clique.
     */
    public Collection getEdges() {
        this.edges = new ArrayList();
        Iterator edgeItr = graph.getEdgesIterator();

        while (edgeItr.hasNext()) {
            Edge edge = (Edge) edgeItr.next();
            if (clique.contains(edge.getSource())
                    && clique.contains(edge.getTarget())) {
                this.edges.add(edge);
            }
        }
        return this.edges;
    }

    /**
     * verify whether the graph contains the clique.
     * 
     * @return <code>True</code> if contains the clique. <code>False</code> if
     *         not.
     */
    public boolean hasMaxClique() {
        searchClique();
        return (this.clique.size() > 3);
    }

    private void searchClique() {
        // ------ to Print and remove it after success
        /*
         * Iterator myNodeItr = this.graph.getNodesIterator(); int
         * myNodeItrLogger = 1;
         * 
         * System.out.println( "unsorted with Format(node | inDegree | outDegree
         * | label):");
         * 
         * while(myNodeItr.hasNext()) { Node toPri = (Node) myNodeItr.next();
         * 
         * System.out.println("\t" + toPri.toString() + " | " +
         * toPri.getInDegree() + " | " + toPri.getOutDegree() + " | " +
         * ((NodeLabelAttribute) toPri.getAttribute("label")).getLabel()); }
         */
        // -------------------
        // sortNodeDegree();
        // intial the instance of fields
        this.sortedNode = new ArrayList(this.graph.getNodes().size() + 1);
        this.clique = new HashSet(this.graph.getNodes().size() + 1);
        this.maximality = new int[this.graph.getNodes().size() + 1];
        this.lexico = new int[this.graph.getNodes().size() + 1];

        // sorte the vertics with ascending degree
        this.sortedNode = Heapsort.heapsort(this.graph.getNodes(),
                new DegreeComp(DegreeComp.ASC));

        // --- to print and remove it after success
        /*
         * System.out.println( "sorted with Format(node | inDegree | outDegree |
         * label):");
         * 
         * for(int i = 0; i < this.sortedNode.size(); i++) { Node toPri = (Node)
         * this.sortedNode.get(i); if(toPri != null) System.out.println("\t" +
         * toPri.toString() + " | " + toPri.getInDegree() + " | " +
         * toPri.getOutDegree() + " | " + ((NodeLabelAttribute)
         * toPri.getAttribute("label")).getLabel()); }
         */
        // ----
        // intial the clique
        HashSet auxClique = new HashSet(this.graph.getNodes().size() + 1);
        int auxNodeIdx = 1;

        for (int i = 1; i < this.sortedNode.size(); i++) {
            if ((((Node) this.sortedNode.get(i)).getInDegree() == 1)
                    && (i == (this.sortedNode.size() - 1))) {
                auxNodeIdx = i;

                break;
            }

            if (((Node) this.sortedNode.get(i)).getInDegree() >= 2) {
                auxNodeIdx = i;

                break;
            }
        }

        auxClique.add(this.sortedNode.get(auxNodeIdx));

        update(2, auxClique);

        // --- to print and remove it after success
        System.out.println("\t\t\tfinished?????? :-) clique size= "
                + this.clique.size());

        // ----------

    }

    /**
     * DOCUMENT ME!
     * 
     * @param idxNode
     * @param node
     * 
     * @return
     */
    private boolean hasSameDegree(Node idxNode, Node node) {
        return idxNode.getInDegree() == node.getInDegree();
    }

    /**
     * 
     */
    private ArrayList insectCandIdxNode(Iterator itr, Collection neighborsOfIdx) {
        ArrayList insectBetweenCandNb = new ArrayList();

        while (itr.hasNext()) {
            Node tNode = (Node) itr.next();

            if (neighborsOfIdx.contains(tNode)) {
                insectBetweenCandNb.add(tNode);
            }
        }

        return insectBetweenCandNb;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodeIdx
     * @param cDiffNiSize
     * @param sortedCdiffNi
     * @param set
     * @param insectSize
     *            DOCUMENT ME!
     */
    private void lexicoTestCase1(int nodeIdx, int cDiffNiSize,
            ArrayList sortedCdiffNi, Set set, int insectSize) {
        for (int jk = 1; jk <= cDiffNiSize; jk++) {
            Collection lex = nodeSetDiff(((Node) sortedCdiffNi.get(jk))
                    .getNeighbors(), set);
            Iterator lexItr = lex.iterator();

            while (lexItr.hasNext()) {
                int y = this.sortedNode.indexOf(lexItr.next());

                if ((y < nodeIdx) && (this.maximality[y] == insectSize)) {
                    if (y >= jk) {
                        this.lexico[y]--;
                    } else {
                        if (y < jk) // fist satisfies ????
                        {
                            if ((((this.lexico[y] + jk) - 1) == cDiffNiSize)
                                    && (y >= (jk - 1))) {
                                this.flag = false;
                                // System.out.println(
                                // "In Lexico Test1 by node index " + nodeIdx +
                                // " , FLAG is: false.\n");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodeIdx
     * @param cDiffNiSize
     * @param insectSize
     * @param set
     */
    private void lexicoTestCase2(int nodeIdx, int cDiffNiSize, int insectSize,
            Set set) {
        if (insectSize != 0) {
            // compute the C union i
            ArrayList cUnionI = new ArrayList();
            Iterator cItr = set.iterator();

            while (cItr.hasNext()) {
                cUnionI.add(cItr.next());
            }

            cUnionI.add(this.sortedNode.get(nodeIdx));

            // y from {V-cUnionI}
            Collection toRemove = nodeSetDiff(this.sortedNode, cUnionI);
            Iterator yItr = toRemove.iterator();

            while (yItr.hasNext()) {
                Node yNode = (Node) yItr.next();

                // System.out.println("the Set of {V-[C union I]}: "+"sNr.
                // "+yNode.getInteger("sortedLabel")+" "+yNode);
                int yIdx = this.sortedNode.indexOf(yNode);

                if ((yIdx < nodeIdx) && (this.maximality[yIdx] == insectSize)
                        && (this.lexico[yIdx] == 0)) {
                    if (cDiffNiSize < yIdx) {
                        this.flag = false;
                        // System.out.println("In Lexico Test2(IF) by node index
                        // " +
                        // nodeIdx + " , FLAG is: false.\n");
                    }
                }
            }
        } else {
            if (cDiffNiSize < (nodeIdx - 1)) {
                this.flag = false;
                // System.out.println("In Lexico Test2(ELSE) by node index " +
                // nodeIdx + " , FLAG is: false.\n");
            }
        }
    }

    /**
     * 
     */
    private void maximalityTest(int nodeIdx, Iterator nIcItr, ArrayList insect) {
        while (nIcItr.hasNext()) {
            int y = this.sortedNode.indexOf(nIcItr.next());

            if ((y < nodeIdx) && (this.maximality[y] == insect.size())) {
                this.flag = false;
                // System.out.println("In max Test by node index " + nodeIdx +
                // " , FLAG is: false.\n");
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodeList1
     *            DOCUMENT ME!
     * @param nodeList2
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private Collection nodeSetDiff(Collection nodeList1, Collection nodeList2) {
        ArrayList diff = new ArrayList();

        Iterator nodeSetDiffItr = nodeList1.iterator();

        while (nodeSetDiffItr.hasNext()) {
            Object elem = nodeSetDiffItr.next();

            // System.err.println("nodeSet Diff NodeSet: "+elem);
            if (elem == null) {
                continue;
            }

            if (!nodeList2.contains(elem)) {
                diff.add(elem);

                // System.out.println(elem);
            }
        }

        return diff;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodeIdx
     *            DOCUMENT ME!
     * @param set
     * @param nodeItr
     */
    private void reInitalLexico(int nodeIdx, Set set, Iterator nodeItr) {
        while (nodeItr.hasNext()) {
            Node iniNode = (Node) nodeItr.next();
            Collection nXdiffC = nodeSetDiff(iniNode.getNeighbors(), set);
            Iterator nXcItr = nXdiffC.iterator();

            while (nXcItr.hasNext()) {
                Node yNode = (Node) nXcItr.next();
                int yIdx = this.sortedNode.indexOf(yNode);
                // System.out.println("by node Index " + nodeIdx +
                // " before REinitial Lexico[" + yIdx + "]= " +
                // this.maximality[yIdx] + "\n");
                this.lexico[yIdx] = 0;
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodeIdx
     *            DOCUMENT ME!
     * @param set
     *            DOCUMENT ME!
     * @param insectItr1
     */
    private void reInitialMax(int nodeIdx, Set set, Iterator insectItr1) {
        while (insectItr1.hasNext()) {
            Node iniNode = (Node) insectItr1.next();
            Collection nXdiffC = nodeSetDiff(iniNode.getNeighbors(), set);
            ArrayList nodeIdxList = new ArrayList();
            nodeIdxList.add(this.sortedNode.get(nodeIdx));

            Collection nXdiffCdiffIdx = nodeSetDiff(nXdiffC, nodeIdxList);
            Iterator nXidxItr = nXdiffCdiffIdx.iterator();

            while (nXidxItr.hasNext()) {
                Node yNode = (Node) nXidxItr.next();
                int yIdx = this.sortedNode.indexOf(yNode);
                // System.out.println("by node Index " + nodeIdx +
                // " before REinitial Maximality[" + yIdx + "]= " +
                // this.maximality[yIdx] + "\n");
                this.maximality[yIdx] = 0;
            }
        }
    }

    /**
     * remove it after algrithm success
     */
    private void sortNodeDegree() {
        Comparator odering = new DegreeComp('a');
        java.util.TreeSet msortedNode = new java.util.TreeSet(odering);
        msortedNode.addAll(this.graph.getNodes());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodeIdx
     * @param set
     */
    private void update(int nodeIdx, Set set) {
        // note that :::in parameter SET all the maximal clique of Gi are
        // saved:::
        // here compare alle the clique and choose the largest
        if (nodeIdx == this.sortedNode.size()) {
            if (set.size() > this.clique.size()) {
                this.clique.clear();

                Iterator setItr = set.iterator();

                while (setItr.hasNext()) {
                    this.clique.add(setItr.next());
                }
            }
        } else {
            Node idxNode = (Node) this.sortedNode.get(nodeIdx);
            Collection neighborsOfIdx = idxNode.getNeighbors();

            // realize C-N(i)<>empty
            Collection cDiffNi = nodeSetDiff(set, neighborsOfIdx);

            if (!cDiffNi.isEmpty()) {
                update(nodeIdx + 1, set);
            }

            // -------------------------
            // prepare for tests
            // realize C insect N(i)
            // ------------------------
            Iterator setItr = set.iterator();

            ArrayList insectBetweenCandNb = insectCandIdxNode(setItr,
                    neighborsOfIdx);

            // ------------------
            // compute T[y]=|N(y) and C and N(i)| for y aus V-C-{i}
            Iterator insectItr = insectBetweenCandNb.iterator();

            while (insectItr.hasNext()) {
                ArrayList nodeIdxList = new ArrayList();
                nodeIdxList.add(this.sortedNode.get(nodeIdx));

                Node xNode = (Node) insectItr.next();

                // realize N(x)-C-{i}
                Collection NxDiffC = nodeSetDiff(xNode.getNeighbors(), set);
                Collection NxDiffCDiffIdx = nodeSetDiff(NxDiffC, nodeIdxList);

                Iterator nciItr = NxDiffCDiffIdx.iterator();

                while (nciItr.hasNext()) {
                    int idx = this.sortedNode.indexOf(nciItr.next());
                    this.maximality[idx]++;
                }
            }

            // -----------------------------------------------
            // compute S[y]=|N(y) insect (C-N(i))| for y aus V-C
            Collection cDiffNg = nodeSetDiff(set, neighborsOfIdx);
            Iterator cNgItr = cDiffNg.iterator();

            while (cNgItr.hasNext()) {
                Node xNode = (Node) cNgItr.next();
                Collection nxDiffC = nodeSetDiff(xNode.getNeighbors(), set);
                Iterator nxCItr = nxDiffC.iterator();

                while (nxCItr.hasNext()) {
                    int idx = this.sortedNode.indexOf(nxCItr.next());
                    this.lexico[idx]++;
                }
            }

            // ---------------------------------------
            this.flag = true;

            // ------------- maximality test
            Collection nIDiffC = nodeSetDiff(neighborsOfIdx, set);
            Iterator nIcItr = nIDiffC.iterator();
            maximalityTest(nodeIdx, nIcItr, insectBetweenCandNb);

            // end maximality test ---------------------------
            // lexico test
            // sort all the vertics in C - N(i)
            ArrayList sortedCdiffNi = Heapsort.heapsort(cDiffNi,
                    new DegreeComp(DegreeComp.ASC));

            // case S(y)>=1. see Lemma6
            lexicoTestCase1(nodeIdx, cDiffNi.size(), sortedCdiffNi, set,
                    insectBetweenCandNb.size());

            // case S(y) = 0
            lexicoTestCase2(nodeIdx, cDiffNi.size(),
                    insectBetweenCandNb.size(), set);

            // end lexico test----------------------------------------

            /*--- to print and remove it after success
             Iterator verf = sortedCdiffNi.iterator();
             while (verf.hasNext())
             {
             Node verfNode = (Node)verf.next();
             if (verfNode == null){
             System.out.println("listing the sorted C - N(i): sNr. "+verfNode);
             } else {
             System.out.println("listing the sorted C - N(i): sNr. "+verfNode.getInteger("sortedLabel"));
             }
             
             }
             //----
             */

            // reinitialize S and T
            Iterator insectItr1 = insectBetweenCandNb.iterator();
            reInitialMax(nodeIdx, set, insectItr1);

            Collection itrNode = nodeSetDiff(set, neighborsOfIdx);
            Iterator nodeItr = itrNode.iterator();
            reInitalLexico(nodeIdx, set, nodeItr);

            // end reinitialize T and S
            // Flag is true if and only if (C insect N(i)) union {i} is a clique
            // of Gi and C is the lexicographically largest clique of Gi-1
            // containing C insect N(i)
            if (this.flag) {
                Collection auxSAVE = nodeSetDiff(set, neighborsOfIdx);
                insectBetweenCandNb.add(idxNode);
                set.clear();

                Iterator insectItr2 = insectBetweenCandNb.iterator();

                while (insectItr2.hasNext()) {
                    set.add(insectItr2.next());
                }

                insectBetweenCandNb.remove(idxNode);
                update(nodeIdx + 1, set);
                set.remove(idxNode);

                Iterator auxSAVEItr = auxSAVE.iterator();

                while (auxSAVEItr.hasNext()) {
                    set.add(auxSAVEItr.next());
                }
            }
        }

        // --- to print and remove them after success
        /*
         * Iterator setItr = set.iterator();
         * 
         * while(setItr.hasNext()) { Node setNode = (Node) setItr.next();
         * System.err.println("by node index " + nodeIdx + " element of the set:
         * label | node: " + ((NodeLabelAttribute)
         * setNode.getAttribute("label")).getLabel() + " | " + setNode + "\n");
         * }
         */
        // ----------------
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
