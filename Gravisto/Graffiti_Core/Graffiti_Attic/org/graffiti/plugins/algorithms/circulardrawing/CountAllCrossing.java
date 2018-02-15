package org.graffiti.plugins.algorithms.circulardrawing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * The class enumerate all crossing in a circle drawing.
 * 
 * @author demirci Created on Feb 22, 2005
 */
public class CountAllCrossing {

    private List longestPathNodes;

    /**
     * @param longestPathList
     *            ordering of the nodes after the phase 1.
     */
    public CountAllCrossing(List longestPathNodes) {
        this.longestPathNodes = longestPathNodes;
    }

    /**
     * @param e
     *            the egde
     * @return true if the edge on the circumference of the embedding circle,
     *         false otherwise.
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
     * @param e1
     * @param e2
     * @return true if e1 cross e2, false otherwise.
     */
    private boolean isOpenEdge(Edge e1, Edge e2) {
        boolean bol = false;
        if (e1.getSource().equals(e2.getSource())
                || e1.getSource().equals(e2.getTarget())
                || e1.getTarget().equals(e2.getSource())
                || e1.getTarget().equals(e2.getTarget())) {

            bol = true;
        }

        return bol;
    }

    /**
     * @param colckWiseEdgeList
     *            a list of the edges which are in the current laout clockwise
     *            ordered.
     * @return number of all crossings in the current layout.
     */
    public int calculateNumberOfCrossing(List colckWiseEdgeList) {
        List openEdgeList = new ArrayList();
        Iterator it = colckWiseEdgeList.iterator();

        while (it.hasNext()) {
            Edge e = (Edge) it.next();
            e.setBoolean("in.openEdgeList", false);
            e.setBoolean("test", false);
        }
        int numberOfCrossing = 0;
        // Step 3
        Iterator edgeIt = colckWiseEdgeList.iterator();
        while (edgeIt.hasNext()) {
            Edge e = (Edge) edgeIt.next();

            // Step 4
            if (!e.getBoolean("in.openEdgeList")) {
                e.setBoolean("in.openEdgeList", true);
                openEdgeList.add(e);
            }

            // Step 5
            else {
                for (int i = openEdgeList.size() - 1;; i--) {
                    Edge openEdge = (Edge) openEdgeList.get(i);

                    // Step 5 b
                    if (e.equals(openEdge)) {
                        openEdgeList.remove(e);
                        break;
                    }

                    // Step 5 a
                    else {
                        if (!isOpenEdge(e, openEdge)) {
                            numberOfCrossing++;
                        }
                    }
                }
            }
        }
        return numberOfCrossing;
    }
}
