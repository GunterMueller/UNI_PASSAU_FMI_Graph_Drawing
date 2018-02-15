// =============================================================================
//
//   PlanarGraphWithEdgeCross.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//   Created on Jun 25, 2005
// =============================================================================

package org.graffiti.plugins.algorithms.GeoThickness;

import java.util.ArrayList;
import java.util.HashMap;

import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * @author ma
 * 
 *         An Implementation for heap sort of Algorithmus swap-Line, in that
 *         nodes or edges with certain sequence are sotiert.
 */
public class HeapList {

    /** the elements are sorted according to rising sequence */
    private final int SORT_MIN = 1;

    /** the elements are sorted according to not rising sequence */
    private final int SORT_MAX = 2;

    /** the list contains all element */
    private ArrayList<Object> heapList;

    /** the sequence of sorted list */
    private int type = 1;

    /***/
    private GeometricAlgorithms geoAlgorithms;

    /**
     * with which keys type is two element compared 1. x coordinate of node 2. y
     * coordinate of node
     */
    private int keyTyp = 1;

    /**
     * 
     */
    private HashMap<? extends Object, HashMap<LocalEdge, Boolean>> keyMap;

    /**
     * Constructor for heap sort
     * 
     * @param type
     *            sorted type
     * @param keyTyp
     *            key type
     */
    public HeapList(int type, int keyTyp) {
        this.heapList = new ArrayList<Object>();
        this.heapList.add(new Integer(1));
        this.type = type;
        this.keyTyp = keyTyp;

    }

    /**
     * it returns the first element in list
     * 
     * @return returned element
     */
    public Object getElement() {
        Object result;
        if (this.heapList.size() > 2) {
            result = this.heapList.get(1);
            this.heapList
                    .set(1, this.heapList.remove(this.heapList.size() - 1));
            siftDown(1);
        } else {
            result = this.heapList.remove(1);
        }
        return result;
    }

    /**
     * insert an element in list
     * 
     * @param object
     *            inserted element
     */
    public void setElement(Object object) {
        this.heapList.add(this.heapList.size(), object);
        siftUp(this.heapList.size() - 1);
    }

    /**
     * is the list empty
     * 
     * @return boolean true list is empty
     */
    public boolean isEmpty() {
        return this.heapList.size() == 1;
    }

    /*
     * bottom-up procedure in heap @param position of index
     */
    private void siftUp(int i) {
        if (i == 1)
            return;

        int antecessor = i / 2;

        if ((this.type == this.SORT_MIN)
                && compare(this.heapList.get(antecessor), this.heapList.get(i))) {
            swap(i, antecessor);
            siftUp(antecessor);
        } else if ((this.type == this.SORT_MAX)
                && compare(this.heapList.get(i), this.heapList.get(antecessor))) {
            swap(i, antecessor);
            siftUp(antecessor);
        }
    }

    /*
     * top-down procedure in heap @param position of index
     */
    private void siftDown(int i) {
        int successor;

        successor = searchSuccessor(i);

        if (successor == -1)
            return;

        if ((this.type == this.SORT_MIN)
                && compare(this.heapList.get(i), this.heapList.get(successor))) {
            swap(i, successor);
            siftDown(successor);
        } else if ((this.type == this.SORT_MAX)
                && compare(this.heapList.get(successor), this.heapList.get(i))) {
            swap(i, successor);
            siftDown(successor);
        }
    }

    /*
     * change the position of two element in heap @param position of element
     */
    private void swap(int i, int j) {
        Object x = this.heapList.get(i);
        this.heapList.set(i, this.heapList.get(j));
        this.heapList.set(j, x);
    }

    /*
     * Comparisons two element @param two object
     */
    private boolean compare(Object o1, Object o2) {
        boolean result = false;
        double xcoor1 = -1, xcoor2 = -1, ycoor1 = -1, ycoor2 = -1;

        if (o1 instanceof Node) {
            if (this.keyTyp == 1) {
                xcoor1 = ((Node) o1)
                        .getDouble(GraphicAttributeConstants.COORDX_PATH);
                xcoor2 = ((Node) o2)
                        .getDouble(GraphicAttributeConstants.COORDX_PATH);
                result = xcoor1 > xcoor2;
            } else {
                ycoor1 = ((Node) o1)
                        .getDouble(GraphicAttributeConstants.COORDY_PATH);
                ycoor2 = ((Node) o2)
                        .getDouble(GraphicAttributeConstants.COORDY_PATH);
                result = ycoor1 > ycoor2;
            }
        } else if (this.keyTyp == 1) {
            result = compareNode((LocalNode) o1, (LocalNode) o2);
        } else if (this.keyTyp == 2) {
            // two object are Node of Graph and sort with y coordinate
            ycoor1 = ((LocalNode) o1).getYCoordiante();
            ycoor2 = ((LocalNode) o2).getYCoordiante();
            result = ycoor1 > ycoor2;
        } else if (this.keyTyp == 3) {
            // two objects are edge sort with Number of cross.
            try {
                xcoor1 = (this.keyMap.get(o1)).size();
            } catch (Exception e) {
            }

            try {
                xcoor2 = (this.keyMap.get(o2)).size();
            } catch (Exception e) {
            }

            result = xcoor1 > xcoor2;

        } else if (this.keyTyp == 4) {
            double lang1 = getLang((LocalEdge) o1);
            double lang2 = getLang((LocalEdge) o2);
            result = lang1 > lang2;
        } else {
            result = false;
        }

        return result;
    }

    /**
     * Look for the successors of current object
     * 
     * @param i
     *            position of current object
     * @return position of successor
     */
    private int searchSuccessor(int i) {
        int result = -1;

        if (((2 * i) + 1) <= this.heapList.size() - 1) {
            if (this.type == this.SORT_MIN) {
                if (compare(this.heapList.get((2 * i) + 1), this.heapList
                        .get(2 * i))) {
                    result = 2 * i;
                } else {
                    result = (2 * i) + 1;
                }
            } else {
                if (compare(this.heapList.get((2 * i) + 1), this.heapList
                        .get(2 * i))) {
                    result = (2 * i) + 1;
                } else {
                    result = 2 * i;
                }
            }
        } else if ((2 * i) <= this.heapList.size() - 1) {
            result = 2 * i;
        }

        return result;
    }

    /**
     * 
     */
    public void setCompareKey(
            HashMap<? extends Object, HashMap<LocalEdge, Boolean>> hashMap) {
        this.keyMap = hashMap;
    }

    public void setGeoAlgorithms(GeometricAlgorithms geoAlgorithms) {
        this.geoAlgorithms = geoAlgorithms;
    }

    @Override
    public String toString() {
        String result = "";

        for (int i = 1; i < this.heapList.size(); i++) {
            LocalNode node = (LocalNode) this.heapList.get(i);
            result = result + new Double(node.getXCoordiante()).toString()
                    + "    ";
        }

        return result;
    }

    private boolean compareNode(LocalNode node1, LocalNode node2) {
        boolean result = false;
        // two object are Node of Graph and sort with x coordinate
        double xcoor1 = node1.getXCoordiante();
        double xcoor2 = node2.getXCoordiante();

        if (xcoor1 > xcoor2) {
            result = true;
        } else if (xcoor1 < xcoor2) {
            result = false;
        } else {
            if (node1.isLeftNode()) {
                // node1 is left Node of the Edge, node2 is left or right Node
                // of then edge
                result = true;
            } else if (node2.isLeftNode()) {
                // node2 is left Node of the Edge, node1 is left or right Node
                // of then edge
                result = false;
            } else if (!node1.isCrossNode()) {
                // node1 is right Node of the Edge.
                result = false;
            } else if (!node2.isCrossNode()) {
                // node2 is right Edge of the Edge
                result = true;
            } else {
                // node1 and node2 are crossNode
                LocalEdge edgeLeftOfNode1 = node1.getBottomEdge();
                LocalEdge edgeRightOfNode1 = node1.getTopEdge();
                LocalEdge edgeLeftOfNode2 = node1.getTopEdge();
                LocalEdge edgeRightOfNode2 = node1.getBottomEdge();
                if (edgeRightOfNode1.equals(edgeRightOfNode2)) {
                    result = this.geoAlgorithms.comp_sect(edgeLeftOfNode1,
                            edgeLeftOfNode2);
                } else if (this.geoAlgorithms.comp_sect(edgeRightOfNode1,
                        edgeRightOfNode2)) {
                    result = true;
                } else {
                    result = false;
                }
            }

        }
        return result;
    }

    private double getLang(LocalEdge edge) {
        double lang = 0.0;
        lang = Math.sqrt(Math.pow(edge.getRightY() - edge.getLeftY(), 2)
                + Math.pow(edge.getRightX() - edge.getLeftX(), 2));
        return lang;
    }
}
