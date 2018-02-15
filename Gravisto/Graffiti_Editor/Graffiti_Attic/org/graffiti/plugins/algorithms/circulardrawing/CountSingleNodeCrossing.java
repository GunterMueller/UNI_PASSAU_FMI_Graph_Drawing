package org.graffiti.plugins.algorithms.circulardrawing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * Enumerate the number of the crossing of a single node.
 * 
 * @author demirci Created on Mar 1, 2005
 */
public class CountSingleNodeCrossing {

    private List longestPathNodes;

    private Map initialEdgeOrd;

    /**
     * Konstruktur
     * 
     * @param path
     * @param initialEdgeOrd
     */
    public CountSingleNodeCrossing(List path, Map initialEdgeOrd) {
        this.longestPathNodes = path;
        this.initialEdgeOrd = initialEdgeOrd;

    }

    /**
     * @param list
     * @return
     */
    private List reverseList(List list) {
        List reverse = new ArrayList();
        for (int i = list.size() - 1; i >= 0; i--) {
            reverse.add(list.get(i));
        }
        return reverse;
    }

    /**
     * @param v
     * @param location
     * @param dir
     * @return
     */
    private int cases(Node v, int location, boolean dir) {
        int x = 0;
        int vPos = longestPathNodes.indexOf(v);
        // Fall 1
        if (vPos > location && dir) {
            x = 1;
        }
        // Fall 2
        else if (vPos > location && !dir) {
            x = 2;
        }
        // fall 3
        else if (vPos < location && !dir) {
            x = 3;
        }
        // Fall 4
        else if (vPos < location && dir) {
            x = 4;
        }
        return x;
    }

    /**
     * @param oldLoc
     * @param newLoc
     * @param e
     * @return true if e is a pertinent edge, false otherwise
     */
    private boolean isPertinentEdge(int oldLoc, int newLoc, Edge e) {

        int tmp1 = 0;
        int tmp2 = 0;
        if (oldLoc < newLoc) {
            tmp1 = oldLoc;
            tmp2 = newLoc;
        } else {
            tmp1 = newLoc;
            tmp2 = oldLoc;
        }
        boolean bol = false;
        Node source = e.getSource();
        Node target = e.getTarget();
        int sourcePos = longestPathNodes.indexOf(source);
        int targetPos = longestPathNodes.indexOf(target);
        if ((tmp1 <= sourcePos && sourcePos <= tmp2)
                || (tmp1 <= targetPos && targetPos <= tmp2)) {
            bol = true;
        }
        return bol;
    }

    /**
     * @param v
     * @param location
     * @param dir
     * @return
     */
    private List arcNodes(Node v, int location, boolean dir) {

        List arcNodes = new ArrayList();
        int vPos = longestPathNodes.indexOf(v);
        int pathSize = longestPathNodes.size();
        // System.out.print("&&&&&&& oldLocation " + vPos + " new location " +
        // location);
        // System.out.print(" und Richtung ist " + dir);

        // Fall 1
        if (vPos > location && dir) {
            // System.out.println(" => Fall 1");
            int tmpPos = vPos;
            boolean bol = false;

            while (!bol) {
                Node node = (Node) longestPathNodes.get(tmpPos);
                node.setBoolean("endPoint", true);
                arcNodes.add(node);
                tmpPos = (tmpPos - 1 + pathSize) % pathSize;
                if (tmpPos == (location - 1 + pathSize) % pathSize) {
                    bol = true;
                }
            }
            // printNodeOrdering(arcNodes);
        }

        // Fall 2
        else if (vPos > location && !dir) {
            // System.out.println(" => Fall 2 &&&&&&&&&&&&");
            int tmpPos = vPos;
            boolean bol = false;
            while (!bol) {
                Node node = (Node) longestPathNodes.get(tmpPos);
                node.setBoolean("endPoint", true);
                arcNodes.add(node);
                tmpPos = (tmpPos + 1) % pathSize;
                if (tmpPos == location) {
                    bol = true;
                }
            }
            // printNodeOrdering(arcNodes);
        }

        // fall 3
        else if (vPos < location && !dir) {
            // System.out.println(" => Fall 3");
            int tmpPos = vPos;
            boolean bol = false;
            while (!bol) {
                Node node = (Node) longestPathNodes.get(tmpPos);
                node.setBoolean("endPoint", true);
                arcNodes.add(node);
                tmpPos = (tmpPos + 1) % pathSize;
                if (tmpPos == (location + 1) % pathSize) {
                    bol = true;
                }
            }
            // printNodeOrdering(arcNodes);
        }

        // Fall 4
        else if (vPos < location && dir) {
            System.out.println(" => Fall 4");
            int tmpPos = vPos;
            boolean bol = false;
            while (!bol) {
                Node node = (Node) longestPathNodes.get(tmpPos);
                // node.setBoolean("endPoint", true);
                arcNodes.add(node);
                tmpPos = (tmpPos - 1 + pathSize) % pathSize;
                if (tmpPos == location) {
                    bol = true;
                }
            }
            // printNodeOrdering(arcNodes);
        }
        return arcNodes;
    }

    /**
     * @param v
     * @param location
     * @param dir
     * @return a list of the endpoints of pertinent eges.
     */
    private List orderPertinentEdgeEndpoints(Node v, int location, boolean dir) {

        List ordEndpoints = new ArrayList();
        List arcNodes = new ArrayList();

        SortedMap sm = new TreeMap();
        int vPos = longestPathNodes.indexOf(v);
        int pathSize = longestPathNodes.size();
        List notArcNodes = new ArrayList();

        ordEndpoints = arcNodes(v, location, dir);
        int size = ordEndpoints.size();
        for (int i = 0; i < size; i++) {
            Node node = (Node) ordEndpoints.get(i);
            Iterator neighbors = node.getNeighborsIterator();
            while (neighbors.hasNext()) {
                Node neighbor = (Node) neighbors.next();
                if (!ordEndpoints.contains(neighbor)) {
                    neighbor.setBoolean("endPoint", true);
                    notArcNodes.add(neighbor);
                }
            }
        }

        Iterator notArcNodesIt = notArcNodes.iterator();
        while (notArcNodesIt.hasNext()) {
            Node node = (Node) notArcNodesIt.next();
            int nodePos = longestPathNodes.indexOf(node);
            int pos = (nodePos - vPos + pathSize) % pathSize;
            sm.put(new Integer(pos), node);
        }

        if (dir) {
            Object[] o = sm.values().toArray();
            for (int j = o.length - 1; j >= 0; j--) {
                Node node = (Node) o[j];
                if (!ordEndpoints.contains(node)) {
                    ordEndpoints.add(node);
                }
            }
        } else {
            Iterator it = sm.values().iterator();
            for (int j = 0; it.hasNext(); j++) {
                Node node = (Node) it.next();
                if (!ordEndpoints.contains(node)) {
                    ordEndpoints.add(node);
                }
            }
        }
        return ordEndpoints;
    }

    /**
     * 
     * @param v
     *            actual processed node
     * @param location
     *            new location
     * @param direction
     *            false clocwise, true anticlocwise
     * @return List the pertinent edges in the arc between the old and new
     *         locations of v.
     */
    private List orderPertinentEdges(Node v, int location, boolean dir) {

        List pertinentEdges = new LinkedList();
        int vPos = longestPathNodes.indexOf(v);

        // System.out.print("&&&&&&& oldLocation " + vPos + " new location " +
        // location);
        // System.out.print(" und Richtung ist " + dir);

        int pathSize = longestPathNodes.size();

        // fall 3
        if (vPos < location && !dir) {
            // System.out.println(" => Fall 3");

            int tmpPos = vPos;
            boolean bol = false;

            while (!bol) {
                Node n = (Node) longestPathNodes.get(tmpPos);
                List orderedEdges = (List) initialEdgeOrd.get(n);
                Object[] o = orderedEdges.toArray();
                for (int i = 0; i < o.length; i++) {
                    Edge e = (Edge) o[i];
                    if (!lieOnPerimeterOfCircle(e)
                            && isPertinentEdge(vPos, location, e)) {
                        e.setBoolean("seen", false);
                        pertinentEdges.add(e);
                    }
                }
                tmpPos = (tmpPos + 1) % pathSize;
                if (tmpPos == (location + 1) % pathSize) {
                    bol = true;
                }
            }

            bol = false;
            while (!bol) {
                Node node = (Node) longestPathNodes.get(tmpPos);
                List orderedEdges = (List) initialEdgeOrd.get(node);
                Object[] o = orderedEdges.toArray();
                Iterator edgeIt = orderedEdges.iterator();
                for (int i = 0; i < o.length; i++) {
                    Edge e = (Edge) o[i];
                    if (pertinentEdges.contains(e)) {
                        if (!lieOnPerimeterOfCircle(e)
                                && isPertinentEdge(vPos, location, e)) {
                            e.setBoolean("seen", false);
                            pertinentEdges.add(e);
                        }
                    }
                }

                tmpPos = (tmpPos + 1) % pathSize;
                if (tmpPos == vPos) {
                    bol = true;
                }
            }
        }

        // Fall 4
        else if (vPos < location && dir) {
            // System.out.println(" => Fall 4");
            int tmpPos = vPos;
            boolean bol = false;

            while (!bol) {
                Node node = (Node) longestPathNodes.get(tmpPos);
                List orderedEdges = (List) initialEdgeOrd.get(node);
                Object[] o = orderedEdges.toArray();
                for (int i = o.length - 1; i >= 0; i--) {
                    Edge e = (Edge) o[i];
                    if (!lieOnPerimeterOfCircle(e)
                            && isPertinentEdge(vPos, location, e)) {
                        e.setBoolean("seen", false);
                        pertinentEdges.add(e);
                    }
                }
                tmpPos = (tmpPos - 1 + pathSize) % pathSize;
                if (tmpPos == location) {
                    bol = true;
                    break;
                }
            }
            bol = false;
            while (!bol) {
                Node node = (Node) longestPathNodes.get(tmpPos);
                List orderedEdges = (List) initialEdgeOrd.get(node);
                Object[] o = orderedEdges.toArray();
                for (int i = o.length - 1; i >= 0; i--) {
                    Edge e = (Edge) o[i];
                    if (pertinentEdges.contains(e)) {
                        if (!lieOnPerimeterOfCircle(e)
                                && isPertinentEdge(vPos, location, e)) {
                            e.setBoolean("seen", false);
                            pertinentEdges.add(e);
                        }
                    }
                }
                tmpPos = (tmpPos - 1 + pathSize) % pathSize;
                if (tmpPos == vPos) {
                    bol = true;
                }
            }
        }

        // Fall 1
        else if (vPos > location && dir) {
            // System.out.println(" => Fall 1");
            int tmpPos = vPos;
            boolean bol = false;

            while (!bol) {
                Node n = (Node) longestPathNodes.get(tmpPos);
                List orderedEdges = (List) initialEdgeOrd.get(n);
                Object[] o = orderedEdges.toArray();
                for (int i = o.length - 1; i >= 0; i--) {
                    Edge e = (Edge) o[i];
                    if (!lieOnPerimeterOfCircle(e)
                            && isPertinentEdge(vPos, location, e)) {
                        e.setBoolean("seen", false);
                        pertinentEdges.add(e);
                    }
                }
                tmpPos = (tmpPos - 1 + pathSize) % pathSize;
                if (tmpPos == (location - 1 + pathSize) % pathSize) {
                    bol = true;
                }
            }
            bol = false;
            while (!bol) {
                Node node = (Node) longestPathNodes.get(tmpPos);
                List orderedEdges = (List) initialEdgeOrd.get(node);

                Object[] o = orderedEdges.toArray();
                for (int i = o.length - 1; i >= 0; i--) {
                    Edge e = (Edge) o[i];
                    if (pertinentEdges.contains(e)) {
                        if (!lieOnPerimeterOfCircle(e)
                                && isPertinentEdge(vPos, location, e)) {
                            e.setBoolean("seen", false);
                            pertinentEdges.add(e);
                        }
                    }
                }

                tmpPos = (tmpPos - 1 + pathSize) % pathSize;

                if (tmpPos == vPos) {
                    bol = true;
                }
            }
        }

        // Fall 2
        else {
            // System.out.println(" => Fall 2 &&&&&&&&&&&&");
            int tmpPos = vPos;
            boolean bol = false;

            while (!bol) {
                Node n = (Node) longestPathNodes.get(tmpPos);
                List edges = (List) initialEdgeOrd.get(n);
                Object[] o = edges.toArray();
                for (int i = 0; i < o.length; i++) {
                    Edge e = (Edge) o[i];
                    if (!lieOnPerimeterOfCircle(e)
                            && isPertinentEdge(vPos, location, e)) {
                        e.setBoolean("seen", false);
                        pertinentEdges.add(e);
                    }
                }
                tmpPos = (tmpPos + 1) % pathSize;
                if (tmpPos == location) {
                    bol = true;
                }
            }
            bol = false;

            while (!bol) {
                Node node = (Node) longestPathNodes.get(tmpPos);
                List orderedEdges = (List) initialEdgeOrd.get(node);
                Object[] o = orderedEdges.toArray();
                Iterator edgeIt = orderedEdges.iterator();
                for (int i = 0; i < o.length; i++) {
                    Edge e = (Edge) o[i];
                    if (pertinentEdges.contains(e)) {
                        if (!lieOnPerimeterOfCircle(e)
                                && isPertinentEdge(vPos, location, e)) {
                            e.setBoolean("seen", false);
                            pertinentEdges.add(e);
                        }
                    }
                }
                tmpPos = (tmpPos + 1) % pathSize;
                if (tmpPos == vPos) {
                    bol = true;
                }
            }
        }
        return pertinentEdges;
    }

    /**
     * @param e
     * @return true if the edge e lie on the perimeter of the embedding circle,
     *         flase otherwise.
     */
    private boolean lieOnPerimeterOfCircle(Edge e) {
        int pathSize = longestPathNodes.size();
        boolean bol = false;
        Node source = e.getSource();
        Node target = e.getTarget();

        if (longestPathNodes.indexOf(source) == (longestPathNodes
                .indexOf(target) - 1 + pathSize)
                % pathSize
                || longestPathNodes.indexOf(source) == (longestPathNodes
                        .indexOf(target) + 1)
                        % pathSize) {

            bol = true;
        }
        return bol;
    }

    /**
     * output the edge ordering.
     * 
     * @param edgeOrdering
     */
    private void printPertinentEdges(List edgeOrdering) {
        Iterator orderedEdgesIt = edgeOrdering.iterator();
        System.out.print("Ordnung der Kanten: [");
        while (orderedEdgesIt.hasNext()) {
            Edge edge = (Edge) orderedEdgesIt.next();
            if (orderedEdgesIt.hasNext()) {
                System.out.print(edge.getInteger("label.label") + " , ");
            } else {
                System.out.print(edge.getInteger("label.label"));
            }
        }
        System.out.println("]");
    }

    /**
     * output the edge ordering.
     * 
     * @param edgeOrdering
     */
    private void printNodeOrdering(List nodeOrdering) {
        Iterator orderedNodesIt = nodeOrdering.iterator();
        System.out.print("Ordnung der Knoten: [");
        while (orderedNodesIt.hasNext()) {
            Node node = (Node) orderedNodesIt.next();
            if (orderedNodesIt.hasNext()) {
                System.out.print(node.getInteger("dfsParam.dfsNum") + " , ");
            } else {
                System.out.print(node.getInteger("dfsParam.dfsNum"));
            }
        }
        System.out.println("]");
    }

    /**
     * @param v
     *            actual processed node.
     * @param newLoc
     *            the new position of v to be moved.
     * @param dir
     *            direction either clocwise or anticlockwise.
     * @return number of corssing caused of v between its old and new location.
     */
    public int calculateSingleNodeCrossing(Node v, int newLoc, boolean dir) {

        // Step 1
        int ctr = 0;

        // Step 2
        int numberOfCrossing = 0;

        // Step 3
        // Order the pertinent edge endpoints as there are encountered around
        // the embedding circle.
        List pertinentEdges = orderPertinentEdges(v, newLoc, dir);
        // output of the pertinent edges in the arc between the old and nea
        // position of v
        // printPertinentEdges(pertinentEdges);
        /*
         * List pertinentEdges = new ArrayList(); List pertinentEdgeEndPoints =
         * orderPertinentEdgeEndpoints(v, newLoc, dir); for (int i = 0; i <
         * pertinentEdgeEndPoints.size(); i++) { Node node =
         * (Node)pertinentEdgeEndPoints.get(i); List edgeList =
         * (List)initialEdgeOrd.get(node); if(dir) { edgeList =
         * reverseList(edgeList); } Iterator edges = edgeList.iterator(); while
         * (edges.hasNext()) { Edge edge = (Edge)edges.next(); if
         * (!lieOnPerimeterOfCircle(edge)) { if (isPertinentEdge(vPos, newLoc,
         * edge)) { System.out.println("Die Kante " +
         * edge.getInteger("label.label") + " ist eine pertinent edge"); }
         * edge.setBoolean("seen", false); pertinentEdges.add(edge); } } } //
         * output of the pertinent edges in the arc between the old and new
         * position of v printNodeOrdering(pertinentEdgeEndPoints);
         */
        // printPertinentEdges(pertinentEdges);
        Collection incidentEdges = v.getEdges();

        for (int i = 0; i < pertinentEdges.size(); i++) {
            Edge e = (Edge) pertinentEdges.get(i);
            if (incidentEdges.contains(e)) {
                numberOfCrossing = numberOfCrossing + ctr;
            } else if (e.getBoolean("seen")) {
                ctr--;
            } else {
                ctr++;
                e.setBoolean("seen", true);
            }
        }

        // System.out.println( " " + numberOfCrossing + " Kreuzungen ");

        for (int i = 0; i < pertinentEdges.size(); i++) {
            Edge e = (Edge) pertinentEdges.get(i);
            e.setBoolean("seen", false);
        }

        return numberOfCrossing;
    }
}
