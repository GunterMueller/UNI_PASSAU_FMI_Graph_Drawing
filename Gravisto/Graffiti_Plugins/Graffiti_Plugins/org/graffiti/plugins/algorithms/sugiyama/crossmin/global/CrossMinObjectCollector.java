// =============================================================================
//
//   CrossMinObjectCollector.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CrossMinObjectCollector.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.crossmin.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements a collector that iterates over the <code>Nodes</code>
 * in a <code>Graph</code> and creates <code>CrossMinObjects</code> for the
 * <code>Nodes</code> in the graph.
 * 
 * @see org.graffiti.plugins.algorithms.sugiyama.crossmin.global.CrossMinObject
 *      Take a look at <code>CrossMinObject</code> for a complete description of
 *      <code>CrossMinObjects</code>.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class CrossMinObjectCollector {
    /** Sugiyama bean to access various sugiyama-specific stuff */
    private SugiyamaData sData;

    /** The <code>Graph</code> to process */
    private Graph graph;

    private int innerSegments;
    protected HashMap<Node, CrossMinObject> nodeToObjectMap;
    protected int maxDegree;

    public int getNumberOfInnerSegments() {
        return innerSegments;
    }

    /**
     * Default constructur for a <code>CrossMinObjectCollector</code>.
     * 
     * @param g
     *            The <code>Graph</code> to process
     * @param data
     *            The <code>SugiyamaData</code> bean that contains objects
     *            specific to the sugiyama algorithm.
     */
    public CrossMinObjectCollector(Graph g, SugiyamaData data) {
        this.graph = g;
        this.sData = data;
        innerSegments = 0;
    }

    /**
     * This method is used to start collecting all <code>CrossMinObjects</code>
     * in the attached graph.
     * 
     * @return Returns an <code>ArrayList</code> of all
     *         <code>CrossMinObjects</code> in the <code>Graph</code>.
     */
    public ArrayList<CrossMinObject> collectObjects() {
        ArrayList<CrossMinObject> objects = new ArrayList<CrossMinObject>();

        // only dummy nodes might be part of an inner segment - create new
        // objects for all real nodes first
        LinkedList<Node> nodes;

        nodes = new LinkedList<Node>(graph.getNodes());

        HashSet<Node> dummyNodes = sData.getDummyNodes();
        CrossMinObject o;
        int xPos = 0;
        Iterator<Node> iter = nodes.iterator();
        Node currentNode;

        while (iter.hasNext()) {
            currentNode = iter.next();
            if (!dummyNodes.contains(currentNode)) {
                o = new CrossMinObject(xPos, false, xPos);
                o.setNode(currentNode);
                xPos++;
                objects.add(o);
            }
        }

        // dummy nodes might be part of an inner segment
        Iterator<Node> nodeIter = dummyNodes.iterator();
        Node upperNeighbor, lowerNeighbor;
        Node dummy;
        // keep track of dummy nodes that have already been processed (they
        // might be part of an inner segment); otherwise there might be
        // duplicate crossminobjects
        HashSet<Node> collectedDummies = new HashSet<Node>();

        while (nodeIter.hasNext()) {
            dummy = nodeIter.next();

            if (collectedDummies.contains(dummy)) {
                continue;
            } else {
                collectedDummies.add(dummy);
            }

            // in theory, there could be dummy nodes without any neighbors
            // such a dummy node would be its own object
            if (dummy.getInNeighbors().size() < 1) {
                upperNeighbor = null;
            } else {
                upperNeighbor = dummy.getAllInNeighbors().iterator().next();
            }

            if (dummy.getOutNeighbors().size() < 1) {
                lowerNeighbor = null;
            } else {
                lowerNeighbor = dummy.getAllOutNeighbors().iterator().next();
            }

            // isolated dummy node
            if (upperNeighbor == null && lowerNeighbor == null) {
                o = new CrossMinObject(xPos, false, xPos);
                o.setNode(dummy);
                xPos++;
                objects.add(o);
            }
            // if both neighbors are no dummy nodes, the dummy node is one
            // object
            else if (!dummyNodes.contains(upperNeighbor)
                    && !dummyNodes.contains(lowerNeighbor)) {
                o = new CrossMinObject(xPos, false, xPos);
                o.setNode(dummy);
                xPos++;
                objects.add(o);
            }
            // otherwise, create a new inner segment
            else {
                LinkedList<Node> innerNodes = new LinkedList<Node>();
                o = new CrossMinObject(xPos, true, xPos);

                if (dummyNodes.contains(upperNeighbor)) {
                    innerNodes.addAll(0, getUpperDummyNodes(dummy));
                }

                innerNodes.add(dummy);

                if (dummyNodes.contains(lowerNeighbor)) {
                    innerNodes.addAll(getLowerDummyNodes(dummy));
                }

                collectedDummies.addAll(innerNodes);
                o.setInnerNodes(innerNodes);
                xPos++;
                objects.add(o);
                ++innerSegments;
            }
        }
        nodeToObjectMap = new HashMap<Node, CrossMinObject>((int) (graph
                .getNumberOfNodes() * 1.25));

        for (CrossMinObject obj : objects) {
            for (Node n : obj.getNodes()) {
                nodeToObjectMap.put(n, obj);
            }
        }

        initializeObjects(objects, nodeToObjectMap);

        return objects;
    }

    private void initializeObjects(ArrayList<CrossMinObject> objects,
            HashMap<Node, CrossMinObject> nodeToObjectMap) {
        int OBJECTS_SIZE = objects.size();
        maxDegree = 0;
        CrossMinObject current, neighbor;
        Node node;

        // create adjacency lists
        for (int i = 0; i < OBJECTS_SIZE; i++) {
            current = objects.get(i);

            if (!current.isInnerSegment()) {
                node = current.getNode();
            } else {
                node = current.getInnerNodes().getFirst();
            }

            if (node.getInDegree() > maxDegree) {
                maxDegree = node.getInDegree();
            }

            for (Node n : node.getInNeighbors()) {
                neighbor = nodeToObjectMap.get(n);
                neighbor.outNeighbors.add(current);
            }

            if (current.isInnerSegment()) {
                node = current.getInnerNodes().getLast();
            }

            if (node.getOutDegree() > maxDegree) {
                maxDegree = node.getOutDegree();
            }

            for (Node n : node.getOutNeighbors()) {
                neighbor = nodeToObjectMap.get(n);
                neighbor.inNeighbors.add(current);
            }
        }
        // create indices
        for (int i = 0; i < OBJECTS_SIZE; i++) {
            current = objects.get(i);

            for (int j = 0; j < current.outNeighbors.elementCount; j++) {
                current.outNeighbors.get(j).inNeighborPositions.add(j);
            }
            for (int j = 0; j < current.inNeighbors.elementCount; j++) {
                current.inNeighbors.get(j).outNeighborPositions.add(j);
            }
        }
    }

    /**
     * Private helper method to get a <code>LinkedList</code> of all dummy nodes
     * on the long edge that are above the current <code>Node n</code>.
     * 
     * @return Returns a <code>LinkedList</code> of dummy nodes on the long edge
     *         that are above <code>Node n</code>.
     */
    private LinkedList<Node> getUpperDummyNodes(Node n) {
        LinkedList<Node> upperDummies = new LinkedList<Node>();
        Node next;
        HashSet<Node> dummyNodes = sData.getDummyNodes();

        next = n.getAllInNeighbors().iterator().next();
        while (dummyNodes.contains(next)) {
            upperDummies.addFirst(next);
            next = next.getAllInNeighbors().iterator().next();
        }

        return upperDummies;
    }

    /**
     * Private helper method to get a <code>LinkedList</code> of all dummy nodes
     * on the long edge that are below the current <code>Node n</code>.
     * 
     * @return Returns a <code>LinkedList</code> of dummy nodes on the long edge
     *         that are below <code>Node n</code>.
     */
    private LinkedList<Node> getLowerDummyNodes(Node n) {
        LinkedList<Node> lowerDummies = new LinkedList<Node>();
        Node next;
        HashSet<Node> dummyNodes = sData.getDummyNodes();

        next = n.getAllOutNeighbors().iterator().next();
        while (dummyNodes.contains(next)) {
            lowerDummies.addLast(next);
            next = next.getAllOutNeighbors().iterator().next();
        }

        return lowerDummies;
    }

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
