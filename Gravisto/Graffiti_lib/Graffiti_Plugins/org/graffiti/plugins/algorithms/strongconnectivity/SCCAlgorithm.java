// =============================================================================
//
//   SCCAlgorithm.java
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.strongconnectivity;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * @author hanauer
 * @version $Revision$ $Date$
 */
public class SCCAlgorithm extends AbstractAlgorithm implements CalculatingAlgorithm {
    
    private static final boolean debugMode = false;
    
    private List<Set<Node>> sccs = new LinkedList<Set<Node>>();
    
    private Selection selection;
    
    private boolean colorSCCs = false;
    
    private boolean colorSCCsOnlyIf = false;
    
    
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
        colorSCCs = ((BooleanParameter) params[1]).getBoolean();
        colorSCCsOnlyIf = ((BooleanParameter) params[2]).getBoolean();
    }
    
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "Node to start with.");
        BooleanParameter color = 
                new BooleanParameter(false, "Color SCCs", "Color the strongly connected components.");
        BooleanParameter colorOnlyIf = 
                new BooleanParameter(false, "Color strong graph", "Color SCCs only if graph is not strongly connected.");

        
        return new Parameter[] { selParam, color, colorOnlyIf };
    }
    
    @Override
    public void check() throws PreconditionException {
        if (!graph.isDirected()) {
            throw new PreconditionException("The graph must be directed");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        
        graph.getListenerManager().transactionStarted(this);
        getStrongComponents();
        maybeColorSCCs();
        
        graph.getListenerManager().transactionFinished(this);
    }
    
    /**
     * Computes the strongly connected components (SCCs).
     * 
     * @return a list of SCCs.
     */
    public List<Set<Node>> getStrongComponents() {
        
        if (!sccs.isEmpty()) {
            return sccs;
        }
        
        List<Node> nodes = graph.getNodes();

        HashMap<Node,Integer> vindex = new HashMap<Node,Integer>();
        HashMap<Node,Integer> vlowlink = new HashMap<Node,Integer>();
        HashMap<Node,Boolean> inStack = new HashMap<Node,Boolean>();

        int index = 0;
        Deque<Node> stack = new ArrayDeque<Node>();
        
        if (!selection.getNodes().isEmpty()) {
            index = tarjan(selection.getNodes().get(0), vindex, vlowlink, index, stack, inStack);
        }

        for (Node node : nodes) {
            if (!vindex.containsKey(node)) {
                index = tarjan(node, vindex, vlowlink, index, stack, inStack);
            }   
        } 
        
        return sccs;
    }
    
    /**
     * Constructs the subgraph for each SCC.
     * 
     * @param nodesMap
     *            maps a node in a subgraph to the original node
     * @param edgesMap
     *            maps an edge in a subgraph to the original edge
     * @return the list of subgraphs
     */
    public List<Graph> getStrongSubgraphs(HashMap<Node,Node> nodesMap, 
            HashMap<Edge,Edge> edgesMap) {
        List<Set<Node>> sccs = getStrongComponents();
        HashMap<Node,Node> revMap = new HashMap<Node,Node>();

        List<Graph> graphs = new LinkedList<Graph>();
        int sum = 0;
        for (Set<Node> scc : sccs) {
            sum += scc.size();
            FastGraph g = new FastGraph();
            
            for (Node node : scc) {
                Node n = g.addNode();
                nodesMap.put(n, node);
                revMap.put(node, n);
            }
            
            for (Node node : scc) {
                for (Edge edge : node.getAllOutEdges()) {
                    Node other = edge.getTarget();
                    if (scc.contains(other)) {
                        Edge e = g.addEdge(revMap.get(node),  revMap.get(other), true);
                        edgesMap.put(e, edge);
                    }
                }
            }

            graphs.add(g);
        }

        assert (sum == graph.getNumberOfNodes());
        
        return graphs;
    }
    
    public boolean isStronglyConnected() {
        return (getStrongComponents().size() == 1);
    }
    
    /**
     * Recursive helper method that implements Tarjan's SCC test.
     * 
     * @param node
     * @param vindex
     * @param vlowlink
     * @param index
     * @param stack
     * @param inStack
     */
    private int tarjan(Node node, HashMap<Node, Integer> vindex,
            HashMap<Node, Integer> vlowlink, int index, Deque<Node> stack,
            HashMap<Node, Boolean> inStack) {

        debug("Tarjan on node " + getNodeLabel(node) + " with index " + index);
        vindex.put(node, index);
        vlowlink.put(node, index);
        index++;
        stack.push(node);
        inStack.put(node, true);

        for (Node w : node.getOutNeighbors()) {
            debug("Considering out-neighbor w = " + getNodeLabel(w));
            if (!vindex.containsKey(w)) {
                debug("No index set yet, recursing.");
                index = tarjan(w, vindex, vlowlink, index, stack, inStack);
                debug("Back at node v = " + getNodeLabel(node) + " and out-neighbor w = " + getNodeLabel(w));
                int wlow = vlowlink.get(w);
                debug("w.lowlink = " + wlow + ", v.lowlink = " + vlowlink.get(node));
                if (vlowlink.get(node) > wlow) {
                    vlowlink.put(node, wlow);
                    debug("Setting v.lowlink = " + vlowlink.get(node));
                }
            } else if (inStack.getOrDefault(w, false)) {
                int wind = vindex.get(w);
                debug("w already on stack with index " + wind);
                if (wind < vlowlink.get(node)) {
                    vlowlink.put(node, wind);
                    debug("Setting v.lowlink = " + vlowlink.get(node));
                }
            }
        }

        if (vindex.get(node) == vlowlink.get(node)) {
            debug("v.index = " + vindex.get(node) + " equals v.lowlink = " + vlowlink.get(node) + ", creating new SCC.");
            Set<Node> scc = new HashSet<Node>();
            Node w;
            do {
                w = stack.pop();
                inStack.put(w, false);
                scc.add(w);
                debug("Adding node " + getNodeLabel(w));
            } while (w != node);
            sccs.add(scc);
        }
        debug("Returning for v = " + getNodeLabel(node) + " with index " + index);
        return index;
    }
    
    private void maybeColorSCCs() {
        if (!colorSCCs || sccs.size() == 1 && colorSCCsOnlyIf) {
            return;
        }
        
        int numComponents = sccs.size();
        int i = 0;
        for (Set<Node> scc : sccs) {
            float hue = ((float) i) / numComponents;
            Color c = Color.getHSBColor(hue, 1.0f, 1.0f);
            for (Node node : scc) {
                colorNode(node, c);
                for (Edge edge : node.getAllOutEdges()) {
                    if (scc.contains(edge.getTarget())) {
                        colorEdge(edge, c);
                    } else {
                        colorEdge(edge, Color.BLACK);
                    }
                }
            }
            i++;
        }
    }
    
    private void colorNode(Node node, Color c) {
        ColorAttribute ca = (ColorAttribute)
                node.getAttribute(GraphicAttributeConstants.FILLCOLOR_PATH);
        ca.setColor(c);
    }
    
    private void colorEdge(Edge edge, Color c) {
        ColorAttribute ca = (ColorAttribute)
                edge.getAttribute(GraphicAttributeConstants.OUTLINE_PATH);
        ca.setColor(c);
    }
    
    /**
     * Tries to find a label for the given node.
     * 
     * @param node the node to find a label for.
     * @return the node's label if present, else "unlabeled node".
     */
    private String getNodeLabel(Node node) {
        if (node.containsAttribute("label")) {
            return ((LabelAttribute) node.getAttribute("label")).getLabel(); 
        }
        String label = "label";
        for (int i = 0; i < 10; i++) {
            String labelI = label + i;
            if (node.containsAttribute(labelI)) {
                return ((LabelAttribute) node.getAttribute(labelI)).getLabel(); 
            }
        }
        
        return "unlabeled node";
    }
    
    private void debug(String debug) {
        if (debugMode) {
            System.out.println(debug);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Find Strongly Connected Components";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlgorithmResult getResult() {
        AlgorithmResult result = new DefaultAlgorithmResult();
        String text = "The graph is " + (isStronglyConnected() ? "" : "not ")
                + "strongly connected.";
        result.setComponentForJDialog(text);
        result.addToResult("strongly connected", isStronglyConnected());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        sccs.clear();
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
