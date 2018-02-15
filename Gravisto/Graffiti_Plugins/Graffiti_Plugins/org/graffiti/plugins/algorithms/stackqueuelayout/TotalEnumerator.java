// =============================================================================
//
//   TotalEnumerator.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

import java.util.Iterator;

import org.graffiti.graph.Graph;
import org.graffiti.plugins.algorithms.stackqueuelayout.plantri.Plantri;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class TotalEnumerator {
    
    private int testedGraphCounter;
    
    private static final int[] NUMBERS = new int[] { 1, 1, 1, 1, 1, 1, 2, 5, 14, 50, 233, 1249, 7595, 49566 };
    
    public static void main(String[] args) {
        TotalEnumerator te = new TotalEnumerator();
        te.enumerate(8, 8);
    }
    
    public TotalEnumerator() {
    }
    
    public void enumerate(int minNodeCount, int maxNodeCount) {
        for (int nodeCount = minNodeCount; nodeCount <= maxNodeCount; nodeCount++) {
            int result = calcMinPermCount(nodeCount);
            System.out.println("nodeCount = " + nodeCount + "; result = " + result);
        }
    }
    
    private int calcMinPermCount(int nodeCount) {
        testedGraphCounter = 0;
        int minResult = Integer.MAX_VALUE;
        
        Iterator<Graph> iter = new Plantri(nodeCount);
        while (iter.hasNext()) {
            Graph graph = iter.next();
            int result = evalGraph(graph);
            minResult = Math.min(minResult, result);
            
        }
        
        return minResult;
    }
    
    private int evalGraph(Graph graph) {
        testedGraphCounter++;
        System.out.println(testedGraphCounter + "/" + NUMBERS[graph.getNumberOfNodes()]);
        
        OrderedStackQueueSat osqs = new OrderedStackQueueSat(graph);
        return osqs.countPermutations();
        
        /*List<Node> nodes = new LinkedList<Node>(graph.getNodes());
        List<Edge> edges = new LinkedList<Edge>(graph.getEdges());
        
        StackQueueSat sqs = new StackQueueSat(graph, nodes, edges, 1, 1);
        
        try {
            sqs.execute();
            int result = sqs.calculateAllPermuations();
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/
//        return 100;
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
