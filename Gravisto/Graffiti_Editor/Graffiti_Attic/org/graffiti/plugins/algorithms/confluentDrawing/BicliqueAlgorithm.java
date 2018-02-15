// =============================================================================
//
//   BicliqueAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BicliqueAlgorithm.java 5772 2010-05-07 18:47:22Z gleissner $

/**
 *
 */

package org.graffiti.plugins.algorithms.confluentDrawing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * DOCUMENT ME!
 * 
 * @author Xiaolei Zhang
 */
public class BicliqueAlgorithm {

    /** the sorted nodes with ascending degree */
    // private ArrayList sortedNode = null;

    /** the graph to execute */
    private Graph graph = null;

    /** the bicliques that can be created for the max degree vertix */
    private ArrayList bicliques = null;

    /** Document me */
    // private ArrayList notNgOfidx = null;
    /** Document me */
    private Collection edges = null;

    private HashMap graphMap;

    private long biCliqueRuntime;

    /**
     * Creates a new CliqueAlgorithm object.
     * 
     * @param g
     *            the graph
     */
    public BicliqueAlgorithm(Graph g, HashMap map) {
        this.graph = g;
        this.graphMap = map;
        this.bicliques = new ArrayList();
        // this.sortedNode = Heapsort.heapsort(g.getNodes(), new
        // DegreeComp(DegreeComp.DES));
    }

    public ArrayList getBicliques() {
        return this.bicliques;
    }

    public boolean isEmpty() {
        return (this.bicliques.isEmpty());
    }

    /**
     * verify whether the graph contains the clique.
     * 
     * @return <code>True</code> if contains the clique. <code>False</code> if
     *         not.
     */
    public boolean hasMaxBiclique() {
        // this.sortedNode = Heapsort.heapsort(this.graph.getNodes(),
        // new DegreeComp(DegreeComp.DES));
        long current = System.currentTimeMillis();
        // update(this.sortedNode);
        update();
        this.biCliqueRuntime = System.currentTimeMillis() - current;
        // System.out.println("\t\t\t\t\t\t the runtime of listing of Biclique is:"
        // +(System.currentTimeMillis()-current));
        // --- to print and remove it after success
        /*
         * System.out.println("\t\t\t finished?????? :-) Biclique size= " +
         * ((Biclique)this.bicliques.get(0)).size());
         */
        // ---------
        // return true;
        // this.bicliques = Heapsort.heapsort(this.bicliques, new DegreeComp(
        // DegreeComp.BICLIQUES));
        /*
         * if(this.bicliques.isEmpty()) return false; else return
         * (((Biclique)this.bicliques.get(0)).getBipartition_Y().size() >= 2 &&
         * ((Biclique)this.bicliques .get(0)).getBipartition_X().size() >= 2);
         */
        return !this.bicliques.isEmpty();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodeIdx
     * @param idx
     */
    public void update() {
        // intializing for all of the nodes in graph
        // ArrayList sortedNode = new ArrayList();
        // this.bicliques = new ArrayList();
        // sorte the vertics with descending degree
        // sortedNode = Heapsort.heapsort(this.graph.getNodes(), new
        // DegreeComp(DegreeComp.DES));

        Iterator itr = this.graph.getNodesIterator();
        while (itr.hasNext())
        // for (int nodeNr = 0; nodeNr < sortedNode.size(); nodeNr++)
        {
            // Node idxNode = (Node)sortedNode.get(nodeNr);
            Node idxNode = (Node) itr.next();
            Collection neighborsOfIdx = idxNode.getNeighbors();
            ArrayList notNgOfidx = this.nodeSetDiff(this.graph.getNodes(),
                    neighborsOfIdx);
            notNgOfidx.remove(idxNode);

            ArrayList sortedNgOfIdx = new ArrayList();
            Biclique bic = new Biclique(this.graph.getNodes().size(),
                    this.graphMap);

            // initial
            // create the maximal K1,n
            bic.addX(idxNode);
            Iterator ngItr = idxNode.getNeighborsIterator();
            while (ngItr.hasNext()) {
                Node ng = (Node) ngItr.next();
                MyNode mNode = new MyNode(ng);
                for (int j = 0; j < notNgOfidx.size(); j++) {
                    if (isNeighborhood(ng, (Node) notNgOfidx.get(j))) {
                        mNode.addNg((Node) notNgOfidx.get(j));
                    }
                }
                sortedNgOfIdx.add(mNode);
            }
            sortedNgOfIdx = Heapsort.heapsort(sortedNgOfIdx, new DegreeComp(
                    DegreeComp.INT));
            if (sortedNgOfIdx.isEmpty()) {
                continue;
            }
            bic.addY(((MyNode) sortedNgOfIdx.get(0)).getNode());
            Iterator ngItrs = ((MyNode) sortedNgOfIdx.get(0)).getNgs()
                    .iterator();
            while (ngItrs.hasNext()) {
                Node toCheck = (Node) ngItrs.next();
                Iterator xItr = bic.getBipartition_X().iterator();
                if (this.isConnectedEachOther(xItr, toCheck)) {
                    bic.addX(toCheck);
                }
            }

            // ----------------------------------------------------------

            for (int i = 1; i < sortedNgOfIdx.size(); i++) {
                Node ngNode = ((MyNode) sortedNgOfIdx.get(i)).getNode();
                Iterator xItr = bic.getBipartition_X().iterator();
                ArrayList toDel = new ArrayList();
                boolean flag1 = true;
                while (xItr.hasNext()) {
                    Node xNode = (Node) xItr.next();
                    if (!isNeighborhood(ngNode, xNode)) {
                        flag1 = false;
                        toDel.add(xNode);
                    }
                }
                if (flag1) {
                    Iterator yItr = bic.getBipartition_Y().iterator();
                    if (this.isConnectedEachOther(yItr, ngNode)) {
                        bic.addY(ngNode);
                    }
                } else if (toDel.size() <= 1) {
                    Iterator yItr = bic.getBipartition_Y().iterator();
                    if (this.isConnectedEachOther(yItr, ngNode)) {
                        bic.removeX((Node) toDel.get(0));
                        bic.addY(ngNode);
                    }
                }
            }
            if ((bic.getBipartition_X().size() >= 2 && bic.getBipartition_Y()
                    .size() > 2)
                    || (bic.getBipartition_Y().size() >= 2 && bic
                            .getBipartition_X().size() > 2)) {
                this.bicliques.add(bic);
            }
        }
        // this.bicliques = Heapsort.heapsort(this.bicliques, new
        // DegreeComp(DegreeComp.BICLIQUES));
    }

    /**
     * @param itr
     * @param tmp
     */
    private boolean isConnectedEachOther(Iterator itr, Node toCheck) {
        boolean flag = true;
        while (itr.hasNext()) {
            if (isNeighborhood(toCheck, (Node) itr.next())) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private boolean isNeighborhood(Node node, Node node2) {
        // Collection neigbors = node.getNeighbors();
        // HashSet ngHash = new HashSet(node.getNeighbors());
        // return ngHash.contains(node2);
        return (this.graphMap.containsKey(node) && ((HashMap) this.graphMap
                .get(node)).containsKey(node2));
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
    private ArrayList nodeSetDiff(Collection nodeList1, Collection nodeList2) {
        ArrayList diff = new ArrayList();

        HashSet node2Hash = new HashSet(nodeList2);

        Iterator nodeSetDiffItr = nodeList1.iterator();

        while (nodeSetDiffItr.hasNext()) {
            Object elem = nodeSetDiffItr.next();

            // System.err.println("nodeSet Diff NodeSet: "+elem);
            if (elem == null) {
                continue;
            }

            if (!node2Hash.contains(elem)) {
                diff.add(elem);

                // System.out.println(elem);
            }
        }

        return diff;
    }

    /**
     * @return the runtime of listing biclique
     */
    public long getBicliqueRT() {
        return this.biCliqueRuntime;
    }

    // rabbish
    /**
     * DOCUMENT ME!
     * 
     * @param nodeIdx
     * @param idx
     */
    private void myupdate(Node idxNode) {
        Collection neighborsOfIdx = idxNode.getNeighbors();
        ArrayList sortedNgOfIdx = Heapsort.heapsort(neighborsOfIdx,
                new DegreeComp(DegreeComp.DES));
        for (int i = 0; i < sortedNgOfIdx.size(); i++) {
            ArrayList bip_Y_elems = this.nodeSetDiff(neighborsOfIdx,
                    ((Node) sortedNgOfIdx.get(i)).getNeighbors());
            Biclique bic = new Biclique(this.graph.getNodes().size(),
                    this.graphMap);
            bic.addX(idxNode);
            bic.addY((Node) sortedNgOfIdx.get(i));
            bip_Y_elems.remove(sortedNgOfIdx.get(i));
            /*
             * if(bip_Y_elems.get(0) != null) {
             * bic.addY((Node)bip_Y_elems.get(0));
             * bip_Y_elems.remove(bip_Y_elems.get(0)); }
             */
            if (!bip_Y_elems.isEmpty()) {
                bic.addY((Node) bip_Y_elems.get(0));
                bip_Y_elems.remove(bip_Y_elems.get(0));

                for (int j = 0; j < bip_Y_elems.size(); j++) {
                    Iterator yItr = bic.getBipartition_Y().iterator();
                    boolean flag = true;
                    while (yItr.hasNext()) {
                        if (this.isNeighborhood((Node) yItr.next(),
                                (Node) bip_Y_elems.get(j))) {
                            flag = false;
                            break;
                        }
                    }

                    if (flag) {
                        bic.addY((Node) bip_Y_elems.get(j));
                    }
                }
            }

            this.bicliques.add(bic);
        }
        this.bicliques = Heapsort.heapsort(this.bicliques, new DegreeComp(
                DegreeComp.CLIQUES));
    }

    private void listBiclique() {
        /*
         * //------ to Print and remove it after success Iterator myNodeItr =
         * this.graph.getNodesIterator();
         * 
         * System.out.println( "unsorted with Format(node | inDegree | outDegree
         * | label):");
         * 
         * while(myNodeItr.hasNext()) { Node toPri = (Node) myNodeItr.next();
         * 
         * System.out.println("\t" + toPri.toString() + " | " +
         * toPri.getInDegree() + " | " + toPri.getOutDegree() + " | " +
         * ((NodeLabelAttribute) toPri.getAttribute("label")).getLabel()); }
         * 
         * //-------------------
         */

        // --- to print and remove it after success
        /*
         * System.out.println( "sorted with Format(node | inDegree | outDegree |
         * label):");
         * 
         * for(int i = 0; i < this.sortedNode.size(); i++) { Node toPri = (Node)
         * this.sortedNode.get(i); System.out.println("\t" + toPri.toString() +
         * " | " + toPri.getInDegree() + " | " + toPri.getOutDegree() + " | " +
         * ((NodeLabelAttribute) toPri.getAttribute("label")).getLabel()); }
         */
        // ----
        this.bicliques = Heapsort.heapsort(this.bicliques, new DegreeComp(
                DegreeComp.BICLIQUES));

        // --- to print and remove it after success
        // System.out.println("\t\t\t finished?????? :-) Biclique size= " +
        // ((Biclique)this.bicliques.get(0)).size());
        // ----------
    }

    /**
     * return edges in the clique.
     * 
     * @return Returns edges in the clique.
     */
    public Collection getEdges() {
        this.edges = new ArrayList();
        Iterator edgeItr = graph.getEdgesIterator();
        Set bic = getBiclique();

        while (edgeItr.hasNext()) {
            Edge edge = (Edge) edgeItr.next();
            if (bic.contains(edge.getSource())
                    && bic.contains(edge.getTarget())) {
                this.edges.add(edge);
            }
        }
        return this.edges;
    }

    /**
     * return the clique.
     * 
     * @return Returns the clique.
     */
    public Set getBiclique() {
        Set res = new HashSet();
        if (!this.bicliques.isEmpty()) {
            Iterator itr = ((Biclique) this.bicliques.get(0))
                    .getBipartition_Y().iterator();
            while (itr.hasNext()) {
                res.add(itr.next());
            }
            Iterator itr1 = ((Biclique) this.bicliques.get(0))
                    .getBipartition_X().iterator();
            while (itr1.hasNext()) {
                res.add(itr1.next());
            }
        }
        return res;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
