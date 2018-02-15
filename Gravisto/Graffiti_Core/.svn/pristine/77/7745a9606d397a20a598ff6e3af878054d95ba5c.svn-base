// =============================================================================
//
//   StackQueueLayoutAlgorithm.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.Clause;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.CnfFormula;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.MiniSat;
import org.graffiti.plugins.algorithms.stackqueuelayout.minisat.Variable;
import org.graffiti.plugins.grids.OrthogonalGrid;
import org.graffiti.plugins.views.defaults.SmoothLineEdgeShape;
import org.graffiti.session.Session;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class StackQueueLayoutAlgorithm extends AbstractAlgorithm {
    private static final String NAME = StackQueueLayoutPlugin.getString("name");
    // private static final String INDEX_ATTRIBUTE = "minisatIndex";
    private static final int QUEUE_HEIGHT = 200;
    private static final int NODE_DIST = 100;

    private Map<GraphElement, Integer> graphElementIndices;
    private Map<Integer, Variable> orderVariables;
    private Node[] indexedNodes;
    private Node[] sortedNodes;
    private int nodeCount;
    private Edge[] indexedEdges;
    private int edgeCount;
    private Map<Variable, Boolean> assignment;
    private Map<Node, Pair<Node, Node>> nodesToNew;
    private Variable[] stackVariables;

    @Override
    public void check() throws PreconditionException {
        PreconditionException preconditionException = new PreconditionException();

        if (graph.isEmpty()) {
            preconditionException.add("Graph must not be empty.");
        }

        if (!preconditionException.isEmpty())
            throw preconditionException;
    }

    private final int getOrderIndex(int i, int j) {
        if (i >= j)
            return getOrderIndex(j, i);

        return i * nodeCount + j;
    }

    private final Variable getOrderVariable(int i, int j) {
        return orderVariables.get(getOrderIndex(i, j));
    }

    private void cleanup() {
        graphElementIndices = null;
        orderVariables = null;
        indexedNodes = null;
        sortedNodes = null;
        assignment = null;
    }

    @Override
    public void execute() {
        graphElementIndices = new HashMap<GraphElement, Integer>();

        MiniSat miniSat = new MiniSat();
        CnfFormula formula = miniSat.createFormula();

        nodeCount = graph.getNumberOfNodes();
        indexedNodes = new Node[nodeCount];
        sortedNodes = new Node[nodeCount];

        int index = 0;

        for (Node node : graph.getNodes()) {
            graphElementIndices.put(node, index);
            indexedNodes[index] = node;
            sortedNodes[index] = node;
            index++;
        }

        edgeCount = graph.getNumberOfEdges();
        indexedEdges = new Edge[edgeCount];
        stackVariables = new Variable[edgeCount];

        index = 0;

        for (Edge edge : graph.getEdges()) {
            graphElementIndices.put(edge, index);
            indexedEdges[index] = edge;
            stackVariables[index] = formula.addVariable();
            index++;
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

        for (int ej = 0; ej < edgeCount; ej++) {
            for (int ei = 0; ei < ej; ei++) {
                Node eiNode1 = indexedEdges[ei].getSource();
                Node eiNode2 = indexedEdges[ei].getTarget();
                Node ejNode1 = indexedEdges[ej].getSource();
                Node ejNode2 = indexedEdges[ej].getTarget();

                if (eiNode1 == ejNode1 || eiNode1 == ejNode2
                        || eiNode2 == ejNode1 || eiNode2 == ejNode2) {
                    continue;
                }

                int ei1 = graphElementIndices.get(eiNode1);
                int ei2 = graphElementIndices.get(eiNode2);
                int ej1 = graphElementIndices.get(ejNode1);
                int ej2 = graphElementIndices.get(ejNode2);

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

                Variable s1 = stackVariables[ei];
                Variable s2 = stackVariables[ej];

                // Stack conditions

                List<Clause> clauses = new LinkedList<Clause>();

                Variable[] edgeVars = new Variable[] { ac, ad, bc, bd };
                boolean[] edgeInv = new boolean[] { acInv, adInv, bcInv, bdInv };

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

                /*
                 * Clause clause = formula.addClause(); clause.addLiteral(ac,
                 * true ^ acInv); clause.addLiteral(ad, false ^ adInv);
                 * clauses.add(clause);
                 * 
                 * clause = formula.addClause(); clause.addLiteral(ad, true ^
                 * adInv); clause.addLiteral(ac, false ^ acInv);
                 * clauses.add(clause);
                 * 
                 * clause = formula.addClause(); clause.addLiteral(bc, true ^
                 * bcInv); clause.addLiteral(bd, false ^ bdInv);
                 * clauses.add(clause);
                 * 
                 * clause = formula.addClause(); clause.addLiteral(bd, true ^
                 * bdInv); clause.addLiteral(bc, false ^ bcInv);
                 * clauses.add(clause);
                 */

                for (Clause clause2 : clauses) {
                    // One can ignore stack conditions if at least one of the
                    // edges rather use the queue.
                    clause2.addLiteral(s1, false);
                    clause2.addLiteral(s2, false);
                }

                // Queue conditions

                // (~Xac v ~Xad v Xbc v Xbd) & (Xac v Xad v ~Xbc v ~Xbd) & (Xac
                // v ~Xad v Xbc v ~Xbd) & (~Xac v Xad v ~Xbc v Xbd)

                clauses = new LinkedList<Clause>();

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

                for (Clause clause2 : clauses) {
                    // One can ignore queue conditions if at least one of the
                    // edges rather use the stack.
                    clause2.addLiteral(s1, true);
                    clause2.addLiteral(s2, true);
                }
            }
        }

        /*
         * for (Edge edge : graph.getEdges()) { Clause clause =
         * formula.addClause();
         * clause.addLiteral(stackVariables[edge.getInteger(INDEX_ATTRIBUTE)],
         * false); }
         */

        miniSat.setVerboseOutput(System.out);

        try {
            assignment = formula.solve();
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, StackQueueLayoutPlugin
                    .getString("interrupted"));
            return;
        }

        if (assignment == null) {
            JOptionPane.showMessageDialog(null, StackQueueLayoutPlugin
                    .getString("nolayout"));
            return;
        }

        graph.getListenerManager().transactionStarted(this);

        colorizeOldGraph();

        graph.getListenerManager().transactionFinished(this);

        nodesToNew = new HashMap<Node, Pair<Node, Node>>();

        sortNodes();

        MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
        Session newSession = mainFrame.addNewSession();

        Graph newGraph = newSession.getGraph();

        for (int i = 0; i < nodeCount; i++) {
            Node org = sortedNodes[i];
            Node n1 = newGraph.addNodeCopy(org);
            Node n2 = newGraph.addNodeCopy(org);
            setCoordinate(n1, new Point2D.Double(NODE_DIST * i, 0));
            setCoordinate(n2, new Point2D.Double(NODE_DIST * i, QUEUE_HEIGHT));
            nodesToNew.put(org, Pair.create(n1, n2));
        }

        for (int i = 0; i < edgeCount; i++) {
            Edge edge = indexedEdges[i];
            Node n1 = edge.getSource();
            Node n2 = edge.getTarget();

            Edge edgeCopy;
            EdgeGraphicAttribute edgeAttribute;

            if (assignment.get(stackVariables[i])) {
                Node nn1 = nodesToNew.get(n1).getFirst();
                Node nn2 = nodesToNew.get(n2).getFirst();
                edgeCopy = newGraph.addEdgeCopy(edge, nn1, nn2);

                double x1 = nn1
                        .getDouble(GraphicAttributeConstants.COORDX_PATH);
                double x2 = nn2
                        .getDouble(GraphicAttributeConstants.COORDX_PATH);

                edgeAttribute = (EdgeGraphicAttribute) edgeCopy
                        .getAttribute("graphics");
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        "bends");
                bends.add(new CoordinateAttribute("b1", new Point2D.Double(
                        (x1 + x2) / 2.0, -0.5 * Math.abs(x1 - x2))));
                edgeAttribute.setBends(bends);
                edgeAttribute.setShape(SmoothLineEdgeShape.class.getName());
            } else {

                int comp = compare(n1, n2);
                if (comp < 0) {
                    Node nn1 = nodesToNew.get(n1).getFirst();
                    Node nn2 = nodesToNew.get(n2).getSecond();
                    edgeCopy = newGraph.addEdgeCopy(edge, nn1, nn2);
                } else {
                    Node nn2 = nodesToNew.get(n2).getFirst();
                    Node nn1 = nodesToNew.get(n1).getSecond();
                    edgeCopy = newGraph.addEdgeCopy(edge, nn2, nn1);
                }

                edgeAttribute = (EdgeGraphicAttribute) edgeCopy
                        .getAttribute("graphics");
            }

            edgeCopy.setDirected(false);
            edgeAttribute.setArrowhead("");
            edgeAttribute.setArrowtail("");
        }

        GridAttribute gga = (GridAttribute) newGraph
                .getAttribute(GraphicAttributeConstants.GRID_PATH);
        OrthogonalGrid orthogonalGrid = new OrthogonalGrid();
        orthogonalGrid.cellWidth = 100;
        orthogonalGrid.cellHeight = QUEUE_HEIGHT;
        gga.setGrid(orthogonalGrid);

        cleanup();
    }

    private void colorizeOldGraph() {
        for (int i = 0; i < edgeCount; i++) {
            Edge edge = indexedEdges[i];

            ColorAttribute ca = (ColorAttribute) edge
                    .getAttribute(GraphicAttributeConstants.OUTLINE_PATH);
            if (assignment.get(stackVariables[i])) {
                ca.setColor(Color.RED);
            } else {
                ca.setColor(Color.BLUE);
            }
        }
    }

    private void setCoordinate(Node node, Point2D coordinate) {
        ((CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.COORD_PATH))
                .setCoordinate(coordinate);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private void sortNodes() {
        Arrays.sort(sortedNodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return StackQueueLayoutAlgorithm.this.compare(o1, o2);
            }
        });
    }

    private int compare(Node o1, Node o2) {
        int i1 = graphElementIndices.get(o1);
        int i2 = graphElementIndices.get(o2);

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

    /*
     * private int compare(Edge e1, Edge e2) { Node e1s; Node e1t;
     * 
     * if (compare(e1.getSource(), e1.getTarget()) < 0) { e1s = e1.getSource();
     * e1t = e1.getTarget(); } else { e1s = e1.getTarget(); e1t =
     * e1.getSource(); }
     * 
     * Node e2s; Node e2t;
     * 
     * if (compare(e2.getSource(), e2.getTarget()) < 0) { e2s = e2.getSource();
     * e2t = e2.getTarget(); } else { e2s = e2.getTarget(); e2t =
     * e2.getSource(); }
     * 
     * int comp = compare(e1s, e2s); if (comp != 0) return comp;
     * 
     * return compare(e1t, e2t); }
     */
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
