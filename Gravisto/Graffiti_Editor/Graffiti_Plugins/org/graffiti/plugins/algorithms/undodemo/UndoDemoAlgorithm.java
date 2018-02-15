// =============================================================================
//
//   UndoDemoAlgorithm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.undodemo;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.StringAttribute;
import org.graffiti.core.Bundle;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * @author Kathrin Hanauer
 * @version $Revision$ $Date$
 */
public class UndoDemoAlgorithm extends AbstractAlgorithm {
    static final Bundle resourceBundle = Bundle
            .getBundle(UndoDemoAlgorithm.class);

    private static enum Action {
        ADD_EDGES, REMOVE_EDGES, ADD_NODES, REMOVE_NODES, CLEAR_GRAPH, CHANGE_EDGES_DIRECTED, REVERSE_EDGES, CHANGE_SRC_NODES, CHANGE_TGT_NODES, ADD_UNDIRECTED_EDGES, REMOVE_UNDIRECTED_EDGES, ADD_INOUTEDGES, REMOVE_INOUTEDGES, ADD_ATTRIBUTES, CHANGE_ATTRIBUTES, REMOVE_ATTRIBUTES
    };

    private Action selectedTest;

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { new StringSelectionParameter(new String[] {
                resourceBundle.getString("parameter.add_edges"),
                resourceBundle.getString("parameter.remove_edges"),
                resourceBundle.getString("parameter.add_nodes"),
                resourceBundle.getString("parameter.remove_nodes"),
                resourceBundle.getString("parameter.clear_graph"),
                resourceBundle.getString("parameter.change_edges_directed"),
                resourceBundle.getString("parameter.reverse_edges"),
                resourceBundle.getString("parameter.change_src_nodes"),
                resourceBundle.getString("parameter.change_tgt_nodes"),
                resourceBundle.getString("parameter.add_undirected_edges"),
                resourceBundle.getString("parameter.remove_undirected_edges"),
                resourceBundle.getString("parameter.add_inoutedges"),
                resourceBundle.getString("parameter.remove_inoutedges"),
                resourceBundle.getString("parameter.add_attributes"),
                resourceBundle.getString("parameter.change_attributes"),
                resourceBundle.getString("parameter.remove_attributes") },
                resourceBundle.getString("parameter.name"), resourceBundle
                        .getString("parameter.description")) };
    }

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        int index = ((StringSelectionParameter) params[0]).getSelectedIndex();
        selectedTest = Action.values()[index];
    }

    /**
     * check conditions
     * 
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#check()
     * 
     */
    @Override
    public void check() throws PreconditionException {
        final PreconditionException exp = new PreconditionException();

        switch (selectedTest) {
        case ADD_EDGES:
        case ADD_UNDIRECTED_EDGES:
        case REMOVE_NODES:
        case ADD_INOUTEDGES:
        case REMOVE_INOUTEDGES:
            if (graph.getNumberOfNodes() == 0) {
                exp.add(resourceBundle.getString("check.no_nodes"));
            }
            break;
        case REMOVE_EDGES:
            if (graph.getNumberOfEdges() == 0) {
                exp.add(resourceBundle.getString("check.no_edges"));
            }
            break;
        case CLEAR_GRAPH:
        case ADD_ATTRIBUTES:
        case CHANGE_ATTRIBUTES:
        case REMOVE_ATTRIBUTES:
            if (graph.isEmpty()) {
                exp.add(resourceBundle.getString("check.empty"));
            }
            break;
        case CHANGE_EDGES_DIRECTED:
        case REVERSE_EDGES:
        case CHANGE_SRC_NODES:
        case CHANGE_TGT_NODES:
            if (graph.getNumberOfDirectedEdges() == 0) {
                exp.add(resourceBundle.getString("check.no_directed_edges"));
            }
            break;
        case REMOVE_UNDIRECTED_EDGES:
            if (graph.getNumberOfUndirectedEdges() == 0) {
                exp.add(resourceBundle.getString("check.no_undirected_edges"));
            }
            break;
        default:
        }

        if (!exp.isEmpty())
            throw exp;
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    @Override
    public void execute() {
        Edge e1, e2;
        graph.getListenerManager().transactionStarted(this);

        switch (selectedTest) {
        case ADD_EDGES:
            if (graph.getNumberOfNodes() == 1) {
                Node n0 = graph.getNodes().get(0);
                graph.addEdge(n0, n0, true);
            } else {
                graph.addEdge(graph.getNodes().get(0), graph.getNodes().get(1),
                        true);
                if (graph.getNumberOfNodes() >= 4) {
                    graph.addEdge(graph.getNodes().get(2), graph.getNodes()
                            .get(3), false);
                }
            }
            break;
        case REMOVE_EDGES:
            List<Edge> edges = new LinkedList<Edge>();
            edges.addAll(graph.getEdges());
            for (Edge e : edges) {
                graph.deleteEdge(e);
            }
            break;
        case ADD_NODES:
            graph.addNode();
            Node n = graph.addNode();
            n.setDouble("graphics.coordinate.x", -10);
            break;
        case REMOVE_NODES:
            List<Node> nodes = new LinkedList<Node>();
            nodes.addAll(graph.getNodes());
            for (Node m : nodes) {
                graph.deleteNode(m);
            }
            break;
        case CLEAR_GRAPH:
            graph.clear();
            break;
        case CHANGE_EDGES_DIRECTED:
            for (Edge e : graph.getEdges()) {
                e.setDirected(!e.isDirected());
            }
            break;
        case REVERSE_EDGES:
            for (Edge e : graph.getEdges()) {
                e.reverse();
            }
            break;
        case CHANGE_SRC_NODES:
            e1 = null;
            e2 = null;
            for (Edge e : graph.getEdges())
                if (e.isDirected())
                    if (e1 == null) {
                        e1 = e;
                    } else {
                        e2 = e;
                        break;
                    }
            if (e2 != null) {
                Node n1 = e1.getSource();
                e1.setSource(e2.getSource());
                e2.setSource(n1);
            } else {
                e1.setSource(e1.getTarget());
            }
            break;
        case CHANGE_TGT_NODES:
            e1 = null;
            e2 = null;
            for (Edge e : graph.getEdges())
                if (e.isDirected())
                    if (e1 == null) {
                        e1 = e;
                    } else {
                        e2 = e;
                        break;
                    }
            if (e2 != null) {
                Node n1 = e1.getTarget();
                e1.setTarget(e2.getTarget());
                e2.setTarget(n1);
            } else {
                e1.setTarget(e1.getSource());
            }
            break;
        case ADD_UNDIRECTED_EDGES:
            if (graph.getNumberOfNodes() == 1) {
                Node n0 = graph.getNodes().get(0);
                graph.addEdge(n0, n0, false);
            } else {
                graph.addEdge(graph.getNodes().get(0), graph.getNodes().get(1),
                        false);
                if (graph.getNumberOfNodes() >= 4) {
                    graph.addEdge(graph.getNodes().get(2), graph.getNodes()
                            .get(3), false);
                }
            }
            break;
        case REMOVE_UNDIRECTED_EDGES:
            List<Edge> undirectedEdges = new LinkedList<Edge>();
            for (Edge e : graph.getEdges()) {
                if (!e.isDirected()) {
                    undirectedEdges.add(e);
                }
            }
            for (Edge e : undirectedEdges) {
                graph.deleteEdge(e);
            }
            break;
        case ADD_INOUTEDGES:
            if (graph.getNumberOfNodes() == 1) {
                Node n0 = graph.getNodes().get(0);
                graph.addEdge(n0, n0, true);
            } else {
                graph.addEdge(graph.getNodes().get(0), graph.getNodes().get(1),
                        true);
                if (graph.getNumberOfNodes() >= 4) {
                    graph.addEdge(graph.getNodes().get(2), graph.getNodes()
                            .get(3), true);
                }
            }
            break;
        case REMOVE_INOUTEDGES:
            List<Edge> directedEdges = new LinkedList<Edge>();
            for (Edge e : graph.getEdges()) {
                if (e.isDirected()) {
                    directedEdges.add(e);
                }
            }
            for (Edge e : directedEdges) {
                graph.deleteEdge(e);
            }
            break;
        case ADD_ATTRIBUTES:
            String addAttrPath = "demo";
            for (GraphElement ge : graph.getGraphElements()) {
                if (!ge.containsAttribute(addAttrPath)) {
                    ge.addAttribute(new StringAttribute("demo",
                            "UndoDemoAlgorithm"), "");
                }
            }
            break;
        case CHANGE_ATTRIBUTES:
            for (GraphElement ge : graph.getGraphElements())
                if (ge.containsAttribute("graphics.framecolor")) {
                    ((ColorAttribute) ge.getAttribute("graphics.framecolor"))
                            .setColor(Color.GREEN);
                }
            break;
        case REMOVE_ATTRIBUTES:
            for (GraphElement ge : graph.getGraphElements())
                if (ge.containsAttribute("graphics.frameThickness")) {
                    ge.removeAttribute("graphics.frameThickness");
                }
            break;
        }

        graph.getListenerManager().transactionFinished(this);
    }

    /*
     * @see org.graffiti.plugin.Parametrizable#getName()
     */
    @Override
    public String getName() {
        return "UndoDemoAlgorithm";
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
