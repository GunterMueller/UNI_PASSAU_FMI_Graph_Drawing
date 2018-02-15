// =============================================================================
//
//   OrderedStackQueueSat.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

import org.graffiti.graph.Graph;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.Clause;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.CnfFormula;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.MiniSat;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.Variable;
import org.graffiti.plugins.algorithms.sugiyama.util.SimpleGraph;
import org.graffiti.plugins.tools.math.Permutation;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OrderedStackQueueSat {
    private MiniSat miniSat;
    private SimpleGraph graph;
    private int nodeCount;
    private int edgeCount;
    
    public OrderedStackQueueSat(Graph g) {
        graph = new SimpleGraph(g);
        nodeCount = graph.getNodeCount();
        edgeCount = graph.getEdgeCount();
        miniSat = new MiniSat();
        //graph.get
    }
    
    public int countPermutations() {
        int count = 0;
        Permutation.Set set = new Permutation.Set(nodeCount);
        
        for (Permutation permutation : set) {
            if (isFeasible(permutation)) count++;
        }
        
        return count;
    }
    
    public boolean isFeasible(Permutation permutation) {
        Variable[] stackVariables = new Variable[edgeCount];
        CnfFormula formula = miniSat.createFormula();
        
        for (int i = 0; i < edgeCount; i++) {
            stackVariables[i] = formula.addVariable();
        }
        
        for (int j = 0; j < edgeCount; j++) {
            for (int i = 0; i < j; i++) {
                Variable vi = stackVariables[i];
                Variable vj = stackVariables[j];
                
                int iSource = permutation.get(graph.getSource(i));
                int iTarget = permutation.get(graph.getTarget(i));
                int jSource = permutation.get(graph.getSource(j));
                int jTarget = permutation.get(graph.getTarget(j));
                
                int iMin = Math.min(iSource, iTarget);
                int iMax = Math.max(iSource, iTarget);
                int jMin = Math.min(jSource, jTarget);
                int jMax = Math.max(jSource, jTarget);
                
                if (iMax <= jMin || jMax <= iMin || iMin == jMin || iMax == jMax) continue;
                
                Clause clause = formula.addClause();
                
                if (iMin < jMin && iMax > jMax || iMin > jMin && iMax < jMax) {
                    // Both cannot be in queue => at least one must be in stack
                    clause.addLiteral(vi, true);
                    clause.addLiteral(vj, true);
                } else {
                    // Both cannot be in stack => at least one must be in queue
                    clause.addLiteral(vi, false);
                    clause.addLiteral(vj, false);
                }
            }
        }
        
        try {
            return formula.solve() != null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
