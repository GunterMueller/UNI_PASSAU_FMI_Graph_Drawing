// =============================================================================
//
//   StackQueueSat.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.Clause;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.CnfFormula;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.MiniSat;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.Variable;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class StackQueueSat {
    private Graph graph;
    private Collection<Node> nodes;
    private Collection<Edge> edges;

    private static final Color[] STACK_COLORS = new Color[] { Color.RED,
            Color.ORANGE, Color.YELLOW, Color.MAGENTA };
    private static final Color[] QUEUE_COLORS = new Color[] { Color.BLUE,
            Color.GREEN, Color.CYAN, new Color(0, 128, 0) };

    private int nodeCount;
    private int edgeCount;

    private Node[] indexedNodes;
    private Edge[] indexedEdges;
    private int[] sources;
    private int[] targets;

    private Map<GraphElement, Integer> graphElementIndices;
    private Map<Integer, Variable> orderVariables;

    private Integer[] sortedNodes;

    private Map<Variable, Boolean> assignment;
    private Variable[] datVariables;
    private int logDat; // Count of variables encoding the data structure index.
    private int stackCount;
    // private int queueCount;
    private int dataStructureCount;

    private boolean isColorizing;
    private boolean isNumbering;
    private boolean isLayouting;
    
    private List<Integer[]> forbiddenPermutations;

    public StackQueueSat(Graph graph, Collection<Node> nodes,
            Collection<Edge> edges, int stackCount, int queueCount) {
        this.graph = graph;
        this.nodes = nodes;
        this.edges = edges;
        this.stackCount = stackCount;
        // this.queueCount = queueCount;
        dataStructureCount = stackCount + queueCount;
        nodeCount = nodes.size();
        edgeCount = edges.size();

        isColorizing = true;
        isNumbering = true;
        isLayouting = false;
    }

    private boolean execCore() throws InterruptedException {
        sortedNodes = new Integer[nodeCount];
        MiniSat miniSat = new MiniSat();
        CnfFormula formula = miniSat.createFormula();

        for (int n = 0; n < nodeCount; n++) {
            sortedNodes[n] = n;
        }

        for (int e = 0; e < edgeCount; e++) {
            for (int rr = 0; rr < logDat; rr++) {
                datVariables[getDatIndex(e, rr)] = formula.addVariable();
            }
        }

        orderVariables = new HashMap<Integer, Variable>();

        for (int j = 0; j < nodeCount; j++) {
            for (int i = 0; i < j; i++) {
                Variable variable = formula.addVariable();
                orderVariables.put(getOrderIndex(i, j), variable);
            }
        }

        for (int k = 0; k < nodeCount; k++) {
            for (int j = 0; j < k; j++) {
                for (int i = 0; i < j; i++) {
                    Variable ij = getOrderVariable(i, j);
                    Variable jk = getOrderVariable(j, k);
                    Variable ik = getOrderVariable(i, k);

                    Clause clause = formula.addClause();
                    clause.addLiteral(ij, false);
                    clause.addLiteral(jk, false);
                    clause.addLiteral(ik, true);

                    clause = formula.addClause();
                    clause.addLiteral(ij, true);
                    clause.addLiteral(jk, true);
                    clause.addLiteral(ik, false);
                }
            }
        }

        boolean[] dual = new boolean[logDat];

        for (int ej = 0; ej < edgeCount; ej++) {
            for (int ei = 0; ei < ej; ei++) {
                int ei1 = sources[ei];
                int ei2 = targets[ei];
                int ej1 = sources[ej];
                int ej2 = targets[ej];

                if (ei1 == ej1 || ei1 == ej2 || ei2 == ej1 || ei2 == ej2) {
                    continue;
                }

                int a = Math.min(ei1, ei2);
                int b = Math.max(ei1, ei2);
                int c = Math.min(ej1, ej2);
                int d = Math.max(ej1, ej2);

                boolean acInv = a > c;
                Variable ac = getOrderVariable(a, c);

                boolean adInv = a > d;
                Variable ad = getOrderVariable(a, d);

                boolean bcInv = b > c;
                Variable bc = getOrderVariable(b, c);

                boolean bdInv = b > d;
                Variable bd = getOrderVariable(b, d);

                for (int r = 0; r < dataStructureCount; r++) {
                    intToDual(r, dual, logDat);

                    List<Clause> clauses = new LinkedList<Clause>();

                    if (r < stackCount) {
                        // Stack conditions
                        Variable[] edgeVars = new Variable[] { ac, ad, bc, bd };
                        boolean[] edgeInv = new boolean[] { acInv, adInv,
                                bcInv, bdInv };

                        for (int v1 = 0; v1 < 4; v1++) {
                            Clause clause1 = formula.addClause();
                            Clause clause2 = formula.addClause();

                            for (int v2 = 0; v2 < 4; v2++) {
                                clause1.addLiteral(edgeVars[v2], (v2 == v1)
                                        ^ edgeInv[v2]);
                                clause2.addLiteral(edgeVars[v2], (v2 != v1)
                                        ^ edgeInv[v2]);
                            }

                            clauses.add(clause1);
                            clauses.add(clause2);
                        }
                    } else {
                        // Queue conditions
                        // (~Xac v ~Xad v Xbc v Xbd) & (Xac v Xad v ~Xbc v ~Xbd)
                        // & (Xac v ~Xad v Xbc v ~Xbd) & (~Xac v Xad v ~Xbc v
                        // Xbd)
                        Clause clause = formula.addClause();
                        clause.addLiteral(ac, false ^ acInv);
                        clause.addLiteral(ad, false ^ adInv);
                        clause.addLiteral(bc, true ^ bcInv);
                        clause.addLiteral(bd, true ^ bdInv);
                        clauses.add(clause);

                        clause = formula.addClause();
                        clause.addLiteral(ac, true ^ acInv);
                        clause.addLiteral(ad, true ^ adInv);
                        clause.addLiteral(bc, false ^ bcInv);
                        clause.addLiteral(bd, false ^ bdInv);
                        clauses.add(clause);

                        clause = formula.addClause();
                        clause.addLiteral(ac, true ^ acInv);
                        clause.addLiteral(ad, false ^ adInv);
                        clause.addLiteral(bc, true ^ bcInv);
                        clause.addLiteral(bd, false ^ bdInv);
                        clauses.add(clause);

                        clause = formula.addClause();
                        clause.addLiteral(ac, false ^ acInv);
                        clause.addLiteral(ad, true ^ adInv);
                        clause.addLiteral(bc, false ^ bcInv);
                        clause.addLiteral(bd, true ^ bdInv);
                        clauses.add(clause);
                    }

                    for (Clause clause : clauses) {
                        // For the case that one of the edges is not in that
                        // data structure...
                        for (int rr = 0; rr < logDat; rr++) {
                            clause.addLiteral(
                                    datVariables[getDatIndex(ei, rr)],
                                    !dual[rr]);
                            clause.addLiteral(
                                    datVariables[getDatIndex(ej, rr)],
                                    !dual[rr]);
                        }
                    }
                }
            }
        }

        // Prevent the edges from hiding in nonexistant data structures.
        for (int e = 0; e < edgeCount; e++) {
            for (int r = dataStructureCount; r < (1 << logDat); r++) {
                intToDual(r, dual, logDat);
                Clause clause = formula.addClause();
                for (int rr = 0; rr < logDat; rr++) {
                    clause.addLiteral(datVariables[getDatIndex(e, rr)],
                            !dual[rr]);
                }
            }
        }
        
        // Forbidden permutations.
        for (Integer[] permutation : forbiddenPermutations) {
            Clause clause = formula.addClause();
            
            for (int i = 0; i < nodeCount - 1; i++) {
                int a = permutation[i];
                int b = permutation[i + 1];
                int min = Math.min(a, b);
                int max = Math.max(a, b);
                boolean inv = a > b;
                clause.addLiteral(getOrderVariable(min, max), inv);
            }
        }

        assignment = formula.solve();

        return assignment != null;
    }

    public boolean execute() throws InterruptedException {
        if (edgeCount == 0)
            return true;
        int val = 0;
        logDat = 0;
        while (val < dataStructureCount - 1) {
            val <<= 1;
            val |= 1;
            logDat++;
        }
        
        forbiddenPermutations = new LinkedList<Integer[]>();

        graphElementIndices = new HashMap<GraphElement, Integer>();

        indexedNodes = new Node[nodeCount];

        int index = 0;

        for (Node node : nodes) {
            graphElementIndices.put(node, index);
            indexedNodes[index] = node;
            index++;
        }

        indexedEdges = new Edge[edgeCount];
        sources = new int[edgeCount];
        targets = new int[edgeCount];

        index = 0;

        for (Edge edge : edges) {
            graphElementIndices.put(edge, index);
            indexedEdges[index] = edge;
            sources[index] = graphElementIndices.get(edge.getSource());
            targets[index] = graphElementIndices.get(edge.getTarget());

            index++;
        }

        datVariables = new Variable[edgeCount * logDat];

        return execCore();
    }

    private final void intToDual(int value, boolean[] dual, int len) {
        for (int i = 0; i < len; i++) {
            dual[i] = (value & (1 << i)) != 0;
        }
    }

    private final int getOrderIndex(int i, int j) {
        if (i >= j)
            return getOrderIndex(j, i);

        return i * nodeCount + j;
    }

    private final Variable getOrderVariable(int i, int j) {
        return orderVariables.get(getOrderIndex(i, j));
    }

    private final int getDatIndex(int e, int rr) {
        return e * logDat + rr;
    }
    
    public int calculateAllPermuations() throws InterruptedException {
        String[] labels = new String[nodeCount];
        
        for (int n = 0; n < nodeCount; n++) {
            try {
                labels[n] = indexedNodes[n].getString("label0.label");
            } catch (AttributeNotFoundException e) {
                labels[n] = String.valueOf(n);
            }
        }
        
        while (assignment != null) {
            sortNodes();
            
            /*StringBuilder builder = new StringBuilder();
            
            for (int i = 0; i < nodeCount; i++) {
                builder.append(labels[sortedNodes[i]]).append(' ');
            }
            
            System.out.println(builder.toString());*/
            
            forbiddenPermutations.add(sortedNodes);
            forbiddenPermutations.add(createInverseSortedNodes());
            
            execCore();
        }
        
        //System.out.println(forbiddenPermutations.size());
        
        return forbiddenPermutations.size();
    }
    
    private Integer[] createInverseSortedNodes() {
        Integer[] result = new Integer[nodeCount];
        
        for (int i = 0; i < nodeCount; i++) {
            result[nodeCount - i - 1] = sortedNodes[i];
        }
        
        return result;
    }

    public void apply() {
        if (isColorizing || isNumbering) {
            graph.getListenerManager().transactionStarted(this);
        }

        if (isNumbering || isLayouting) {
            sortNodes();
        }

        if (isNumbering) {
            for (int i = 0; i < nodeCount; i++) {
                int ni = sortedNodes[i];
                Node n = indexedNodes[ni];
                if (n.containsAttribute("label0.label")) {
                    n.setString("label0.label", "" + i);
                } else {
                    n
                            .addAttribute(new NodeLabelAttribute("label0", ""
                                    + i), "");
                }
            }
        }

        if (isColorizing) {
            colorizeOldGraph();
        }

        if (isColorizing || isNumbering) {
            graph.getListenerManager().transactionFinished(this);
        }
    }

    private void colorizeOldGraph() {
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = indexedEdges[i];

            ColorAttribute ca = (ColorAttribute) edge
                    .getAttribute(GraphicAttributeConstants.OUTLINE_PATH);
            int value = 0;
            for (int rr = 0; rr < logDat; rr++) {
                if (assignment.get(datVariables[getDatIndex(i, rr)])) {
                    value |= 1 << rr;
                }
            }
            if (value < stackCount) {
                ca.setColor(STACK_COLORS[value % 4]);
            } else {
                ca.setColor(QUEUE_COLORS[(value - stackCount) % 4]);
            }
        }
    }

    private void sortNodes() {
        Arrays.sort(sortedNodes, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return StackQueueSat.this.compare(o1, o2);
            }
        });
    }

    private int compare(int i1, int i2) {
        if (i1 == i2)
            return 0;

        boolean isInverted = i1 > i2;

        boolean order = assignment.get(getOrderVariable(Math.min(i1, i2), Math
                .max(i1, i2)));

        if (isInverted ^ order)
            return -1;
        else
            return 1;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
