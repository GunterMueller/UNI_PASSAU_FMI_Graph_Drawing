package org.graffiti.plugins.algorithms.circulardrawing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author demirci Created on Oct 2, 2005
 */
public class ClockwiseEdgeOrdering {
    Graph graph;

    List nodeOrdering;

    /**
     * @param graph
     * @param nodeOrdering
     *            on the embedding circle
     */
    public ClockwiseEdgeOrdering(Graph graph, List nodeOrdering) {
        this.graph = graph;
        this.nodeOrdering = nodeOrdering;
    }

    /**
     * @return a list of the edges which are clockwise orderd.
     */
    public List edgeOrdering() {

        Map list1Map = new HashMap();
        Map list2Map = new HashMap();
        Map ordAdjMap = new HashMap();

        Iterator it = nodeOrdering.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            List list1 = new ArrayList();
            List list2 = new ArrayList();
            list1Map.put(node, list1);
            list2Map.put(node, list2);
        }

        it = nodeOrdering.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            Iterator neighbors = node.getInNeighborsIterator();
            while (neighbors.hasNext()) {
                Node neighbor = (Node) neighbors.next();
                int nodePos = nodeOrdering.indexOf(node);
                int neighborPos = nodeOrdering.indexOf(neighbor);
                if (nodePos < neighborPos) {
                    List list1 = (List) list1Map.remove(neighbor);
                    list1.add(0, node);
                    list1Map.put(neighbor, list1);
                } else {
                    List list2 = (List) list2Map.remove(neighbor);
                    list2.add(0, node);
                    list2Map.put(neighbor, list2);
                }
            }
        }

        List edgeEndPoints = new ArrayList();
        it = nodeOrdering.iterator();
        while (it.hasNext()) {
            List orderedNeigbors = new ArrayList();
            Node node = (Node) it.next();
            List list2 = (List) list2Map.get(node);
            List list1 = (List) list1Map.get(node);
            for (int i = 0; i < list1.size(); i++) {
                orderedNeigbors.add(list1.get(i));
            }
            for (int i = 0; i < list2.size(); i++) {
                orderedNeigbors.add(list2.get(i));
            }
            ordAdjMap.put(node, orderedNeigbors);
        }

        List clockwiseEdgeOedering = new ArrayList();
        Iterator lpIt = nodeOrdering.iterator();
        while (lpIt.hasNext()) {
            Node no = (Node) lpIt.next();
            List ordAdjList = (List) ordAdjMap.get(no);
            List nodeOrderedEdges = new ArrayList();
            for (int t = 0; t < ordAdjList.size(); t++) {
                Node noo = (Node) ordAdjList.get(t);
                Edge e = graph.getEdges(no, noo).iterator().next();
                nodeOrderedEdges.add(e);
                clockwiseEdgeOedering.add(e);
            }
        }
        return clockwiseEdgeOedering;
    }

    /**
     * debug the edge ordering in the circular drawing
     * 
     * @param edgeOrdering
     */
    private void printEdgeOrdering(List edgeOrdering) {

        Iterator orderedEdgesIt = edgeOrdering.iterator();
        System.out.print("Clocwise ordnung der Kanten: [");
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

}
