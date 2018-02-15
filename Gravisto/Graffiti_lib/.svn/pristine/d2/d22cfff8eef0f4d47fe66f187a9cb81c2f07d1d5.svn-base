// =============================================================================
//
//   PlanarGraphSeek.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//   Created on Jun 28, 2005
// =============================================================================

package org.graffiti.plugins.algorithms.GeoThickness;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author ma
 * 
 *         a heuristic for the computation of the geometrical thickness of a
 *         graph
 * 
 */
public abstract class PlanarGraphSeek<T> {

    /** the class, which contains geometrical algorithms */
    protected GeometricAlgorithms geoAlgorithms = new GeometricAlgorithms();

    /** a HashMap, which contains information about the edge crossing */
    protected HashMap<LocalEdge, HashMap<LocalEdge, Boolean>> edgeAttribute = new HashMap<LocalEdge, HashMap<LocalEdge, Boolean>>();

    /** the list of the edges */
    protected HashMap<Integer, LocalEdge> edgeList = null;

    /** the list of the nodes saved in heap */
    protected HeapList nodeList = null;

    /** the two-three-tree saved the edges */
    protected TwoThreeTree edgeTree;

    /** nummber of node */
    public int numberofinsertkante = 0;

    /** nummber of node */
    public int numberofdeleteKante = 0;

    /** nummber of node */
    public int numberofswapedge = 0;

    public long timeforinsert = 0;

    public long timefordelete = 0;

    public long timeforswap = 0;

    /** constractor */
    public PlanarGraphSeek(HashMap<Integer, LocalEdge> edgeList) {
        this.edgeList = edgeList;
    }

    /**
     * insert the nodes in the heap
     */
    public void setNodeList() {
        this.nodeList = null;

        this.nodeList = new HeapList(1, 1);

        Iterator<Integer> itEdge = this.edgeList.keySet().iterator();

        while (itEdge.hasNext()) {
            LocalEdge edge = this.edgeList.get(itEdge.next());
            LocalNode leftNode = new LocalNode(edge.getLeftX(),
                    edge.getLeftY(), edge, null);
            LocalNode rightNode = new LocalNode(edge.getRightX(), edge
                    .getRightY(), null, edge);
            this.nodeList.setElement(leftNode);
            this.nodeList.setElement(rightNode);
        }
    }

    /**
     * returnd an collection of edges, the graph, which contains these edges, is
     * planar.
     * 
     * @return a set of edges
     */
    public abstract Collection<T> getPlanarGraph();

    /**
     * whether the program runs
     * 
     * @return true the Program is run
     */
    public boolean isRun() {
        return !this.edgeList.isEmpty();
    }

    /**
     * a change of the method intersection enumerating problem, all edge
     * crossing are discovered and as attributes in edge stores.
     */
    public int setCrossNummber() {
        this.edgeTree = new TwoThreeTree();
        this.geoAlgorithms.setCompareKey(this.edgeAttribute);
        this.edgeTree.setGeoAlgorithms(this.geoAlgorithms);
        this.nodeList.setGeoAlgorithms(this.geoAlgorithms);

        LocalEdge edgeLeft = null;
        LocalEdge edgeRight = null;

        LocalEdge[] edgeLeftAndRight = null;

        while (!this.nodeList.isEmpty()) {

            LocalNode node = (LocalNode) this.nodeList.getElement();

            if (node.isCrossNode()) {

                long anfang = System.currentTimeMillis();

                edgeLeft = node.getBottomEdge();
                edgeRight = node.getTopEdge();

                // System.out.println("crossnode: " + node.getXCoordiante() +
                // "edge: " + newString(edgeLeft) + " edge:" +
                // newString(edgeRight));
                // System.out.println("before swap: " +
                // this.edgeTree.toString());
                LocalEdge[] edges = this.edgeTree.swapEdge(edgeLeft, edgeRight);

                this.numberofswapedge++;

                // System.out.println("after swap: " +
                // this.edgeTree.toString());
                setSwapFlag(edgeLeft, edgeRight);

                if (geoAlgorithms.crossEdge(edges[0], edgeRight)
                        && noCross(edges[0], edgeRight)) {
                    addEdgeAttribute(edges[0], edgeRight);
                    this.nodeList.setElement(geoAlgorithms.getCrossNode(
                            edgeRight, edges[0]));
                }

                if (geoAlgorithms.crossEdge(edges[1], edgeLeft)
                        && noCross(edges[1], edgeLeft)) {
                    addEdgeAttribute(edges[1], edgeLeft);
                    this.nodeList.setElement(geoAlgorithms.getCrossNode(
                            edges[1], edgeLeft));
                }

                long end = System.currentTimeMillis();
                this.timeforswap += (end - anfang);
            } else {
                if (node.isLeftNode()) {
                    LocalEdge edge = node.getTopEdge();

                    long anfang = System.currentTimeMillis();

                    EdgeVertex edgeVertex = new EdgeVertex(edge);

                    LocalEdge[] edges = this.edgeTree.insert(edgeVertex);

                    this.numberofinsertkante++;

                    // System.out.println("insert: " + "edge is: "+
                    // newString(edge) + " " + this.edgeTree.toString());
                    if (geoAlgorithms.crossEdge(edge, edges[0])
                            && noCross(edge, edges[0])) {
                        addEdgeAttribute(edge, edges[0]);
                        this.nodeList.setElement(geoAlgorithms.getCrossNode(
                                edge, edges[0]));
                    }

                    if (geoAlgorithms.crossEdge(edge, edges[1])
                            && noCross(edge, edges[1])) {
                        addEdgeAttribute(edge, edges[1]);
                        this.nodeList.setElement(geoAlgorithms.getCrossNode(
                                edges[1], edge));
                    }

                    long end = System.currentTimeMillis();

                    this.timeforinsert += (end - anfang);

                } else {
                    LocalEdge edge = node.getBottomEdge();

                    long anfang = System.currentTimeMillis();

                    // System.out.println("before delete: " +
                    // this.edgeTree.toString());
                    edgeLeftAndRight = this.edgeTree.delete(edge);

                    // System.out.println("delete edge: " + newString(edge) +
                    // "-----" + this.edgeTree.toString());
                    this.numberofdeleteKante++;

                    if (geoAlgorithms.crossEdge(edgeLeftAndRight[0],
                            edgeLeftAndRight[1])
                            && noCross(edgeLeftAndRight[0], edgeLeftAndRight[1])) {
                        addEdgeAttribute(edgeLeftAndRight[0],
                                edgeLeftAndRight[1]);
                        this.nodeList.setElement(geoAlgorithms.getCrossNode(
                                edgeLeftAndRight[1], edgeLeftAndRight[0]));
                    }

                    long end = System.currentTimeMillis();

                    this.timefordelete += (end - anfang);
                }
            }
        }

        // System.out.println("insertedge is: " + this.numberofinsertkante);
        // System.out.println("deleteedge is: " + this.numberofdeleteKante);
        // System.out.println("swapedge is: " + this.numberofswapedge);
        // System.out.println("timeforinsert: " + this.timeforinsert);
        // System.out.println("timefordelete: " + this.timefordelete);
        // System.out.println("timeforswap: " + this.timeforswap);

        return this.numberofswapedge;
    }

    /**
     * reset the list of edges
     * 
     * @param edgeList
     *            list of edges
     */
    public void resetEdgeList(HashMap<Integer, LocalEdge> edgeList) {
        this.edgeList = edgeList;
    }

    /** reset the variable edgeAttribute */
    public void resetHashMap() {
        edgeAttribute = new HashMap<LocalEdge, HashMap<LocalEdge, Boolean>>();
    }

    /**
     * the Nummber of cross and the crossed edge saved as attributes in current
     * edge
     * 
     * @param edgeLeft
     *            left edge
     * @param edgeRight
     *            right edge.
     */
    private void addEdgeAttribute(LocalEdge edgeLeft, LocalEdge edgeRight) {
        HashMap<LocalEdge, Boolean> edgeLeftMap = this.edgeAttribute
                .get(edgeLeft);
        HashMap<LocalEdge, Boolean> edgeRightMap = this.edgeAttribute
                .get(edgeRight);

        Boolean isSwap = new Boolean(false);

        if (edgeLeftMap != null) {
            if (!edgeLeftMap.containsKey(edgeRight)) {
                edgeLeftMap.put(edgeRight, isSwap);
            }
        } else {
            edgeLeftMap = new HashMap<LocalEdge, Boolean>();
            edgeLeftMap.put(edgeRight, isSwap);
            this.edgeAttribute.put(edgeLeft, edgeLeftMap);
        }

        if (edgeRightMap != null) {
            if (!edgeRightMap.containsKey(edgeLeft)) {
                edgeRightMap.put(edgeLeft, isSwap);
            }
        } else {
            edgeRightMap = new HashMap<LocalEdge, Boolean>();
            edgeRightMap.put(edgeLeft, isSwap);
            this.edgeAttribute.put(edgeRight, edgeRightMap);
        }
    }

    /**
     * add the flag of the Characteristic of edges
     * 
     * @param edgeLeft
     * @param edgeRight
     *            two edges, which cut themselves
     */
    private void setSwapFlag(LocalEdge edgeLeft, LocalEdge edgeRight) {
        HashMap<LocalEdge, Boolean> swapEdge;
        Boolean isSwap = new Boolean(true);

        swapEdge = this.edgeAttribute.get(edgeLeft);
        swapEdge.remove(edgeRight);
        swapEdge.put(edgeRight, isSwap);

        swapEdge = this.edgeAttribute.get(edgeRight);
        swapEdge.remove(edgeLeft);
        swapEdge.put(edgeLeft, isSwap);
    }

    /**
     * two edge have crossing or not
     * 
     * @param edge1
     *            edge2.
     * @return true two edge have cross.
     */
    private boolean noCross(LocalEdge edge1, LocalEdge edge2) {
        HashMap<LocalEdge, Boolean> hashmap = null;

        try {
            hashmap = this.edgeAttribute.get(edge1);
        } catch (Exception e) {
        }

        if ((hashmap != null) && hashmap.containsKey(edge2))
            return false;
        else
            return true;
    }

    protected String newString(LocalEdge edge) {
        return "(" + edge.getLeftX() + ", " + edge.getLeftY() + ")" + "   ("
                + edge.getRightX() + ", " + edge.getRightY() + ") -------";
    }

    protected HashMap<LocalEdge, HashMap<LocalEdge, Boolean>> getEdgeAttribute() {
        return this.edgeAttribute;
    }

    protected void setEdgeAttribute(
            HashMap<LocalEdge, HashMap<LocalEdge, Boolean>> attribute) {
        this.edgeAttribute = attribute;
    }
}
