// =============================================================================
//
//   PlanarMapGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PlanarMapGenerator.java 6342 2015-02-05 12:36:12Z hanauer $

package org.graffiti.plugins.algorithms.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.graffiti.attributes.UnificationException;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * This generator creates a planar map or a planar graph. They can be maximal or
 * random. A maximal planar map with n nodes has 3n - 6 edges. Every edge has a
 * reversal edge, that means: If there is an directed edge from node a to node
 * b, there is also an directed edge from b to a. Undirected edges are not
 * allowed. In a planar graph, only one of two reversal edges is kept. Which one
 * this will be, is decided randomly. In a random planar map x edges are
 * randomly deleted. In a random planar graph x edges of the remaining edges are
 * deleted. A map or a graph is planar, if the nodes can be placed graphically
 * on a two-dimensional area with no edge crossing each other.
 * 
 * @author $Marek Piorkowski$
 * @version $1.0$ $1.9.2005$
 */
public class PlanarMapGenerator extends AbstractGenerator {

    /** Build a map */
    private static final String MAP = "Map";

    /** Build a graph */
    private static final String GRAPH = "Graph";

    private static final String PLANAR_DRAWING = "Planar Drawing";

    /** number of edges nodes to delete */
    private IntegerParameter edgesToDeleteParam;

    /** number of nodes */
    private IntegerParameter nodesParam;

    /** Selection parameter */
    private StringSelectionParameter map_graph_sel;

    /**
     * Creates a new PlanarGraphGenerator object.
     */
    public PlanarMapGenerator() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        addEdgeBendingOption();
        addFormSelOption();

        String[] options = { MAP, GRAPH };

        nodesParam = new IntegerParameter(new Integer(5), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");
        map_graph_sel = new StringSelectionParameter(options, "type",
                "create a map or a graph");
        edgesToDeleteParam = new IntegerParameter(
                new Integer(0),
                new Integer(0),
                new Integer(100),
                "edges to delete randomly",
                "The number of edges to delete from the maximal graph/map. If this number is negative, a random number of edges will be deleted.");
        parameterList.addFirst(edgesToDeleteParam);
        parameterList.addFirst(map_graph_sel);
        parameterList.addFirst(nodesParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Planar Map/Graph";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (nodesParam.getValue().intValue() < 0) {
            errors.add("The number of nodes must not be less than zero.");
        }

        if (nodesParam.getValue().intValue() == 1) {
            if (map_graph_sel.getSelectedValue().equals(MAP)) {
                if (edgesToDeleteParam.getValue().intValue() > 2) {
                    errors
                            .add("The maximal number of edges to delete in this map is 2.");
                }
            } else if (map_graph_sel.getSelectedValue().equals(GRAPH)) {
                if (edgesToDeleteParam.getValue().intValue() > 1) {
                    errors
                            .add("The maximal number of edges to delete in this graph is 1.");
                }
            }
        } else if (nodesParam.getValue().intValue() >= 0) {
            if (map_graph_sel.getSelectedValue().equals(MAP)) {
                if (edgesToDeleteParam.getValue().intValue() > ((nodesParam
                        .getValue().intValue() - 2) * 6)) {
                    errors
                            .add("The maximal number of edges to delete in this map is 6 * (number of nodes - 2), in this case: "
                                    + ((nodesParam.getValue().intValue() - 2) * 6));
                }
            } else if (map_graph_sel.getSelectedValue().equals(GRAPH)) {
                if (edgesToDeleteParam.getValue().intValue() > ((nodesParam
                        .getValue().intValue() - 2) * 3)) {
                    errors
                            .add("The maximal number of edges to delete in this graph is 3 * (number of nodes - 2), in this case: "
                                    + ((nodesParam.getValue().intValue() - 2) * 3));
                }
            }
        }

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        ExtendedAdjListGraph myGraph = new ExtendedAdjListGraph(graph
                .getListenerManager());
        try {
            myGraph.addAttributeConsumer(GraffitiSingleton.getInstance()
                    .getMainFrame().getActiveEditorSession().getActiveView());
        } catch (UnificationException e1) {
            e1.printStackTrace();
        }
        int numberOfNodes = nodesParam.getValue().intValue();

        ArrayList<Node> nodes = new ArrayList<Node>(numberOfNodes);
        Collection<Edge> edgeList = new LinkedList<Edge>();

        myGraph.getListenerManager().transactionStarted(this);

        if (numberOfNodes <= 0) {
            myGraph.getListenerManager().transactionFinished(this);

            return;
        }

        Node a = myGraph.addNode();
        nodes.add(a);
        numberOfNodes--;

        if (numberOfNodes == 0) {
            myGraph.getListenerManager().transactionFinished(this);

            drawNodes(nodes);

            return;
        }

        Node b = myGraph.addNode();
        nodes.add(b);
        numberOfNodes--;

        Edge[] edges = new Edge[(numberOfNodes == 0) ? 2 : (6 * numberOfNodes)];

        edges[0] = myGraph.addEdge(a, b, true);
        edges[1] = myGraph.addEdge(b, a, true);
        ((ExtendedAdjListEdge) edges[0]).setReversal(edges[1]);
        ((ExtendedAdjListEdge) edges[1]).setReversal(edges[0]);

        int m = 2;
        int nodePos = 1;

        while (numberOfNodes > 0) {
            int random = (int) (Math.random() * (m - 1));
            Edge e = edges[random];
            Node v = myGraph.addNode();
            nodePos++;
            nodes.add(nodePos, v);

            while (!e.getTarget().equals(v)) {
                Edge x = myGraph.addEdge(v, e.getSource(), true);
                Edge y = myGraph.addEdge(e, v, true, ExtendedAdjListNode.AFTER);
                edges[m] = x;
                m++;
                edges[m] = y;
                m++;
                ((ExtendedAdjListEdge) x).setReversal(y);
                ((ExtendedAdjListEdge) y).setReversal(x);
                e = myGraph.face_cycle_succ((ExtendedAdjListEdge) e);
            }

            numberOfNodes--;
        }

        Collections.addAll(edgeList, edges);

        // random deleting
        int edgesToDelete = edgesToDeleteParam.getValue().intValue();

        if (edgesToDelete < 0) {
            if (map_graph_sel.getSelectedValue().equals(MAP)) {
                edgesToDelete = (int) (Math.random() * edges.length);
            } else if (map_graph_sel.getSelectedValue().equals(GRAPH)) {
                edgesToDelete = (int) (Math.random() * (edges.length / 2));
            }
        }

        if (map_graph_sel.getSelectedValue().equals(MAP)) {
            deleteEdgesRandomly(myGraph, edges, edgeList, edgesToDelete);
        } else if (map_graph_sel.getSelectedValue().equals(GRAPH)) {
            Edge[] restEdges = new Edge[edges.length / 2];

            // delete one of the reversal edges
            int j = 0;

            for (int i = 0; i < edges.length; i += 2) {
                int random = (int) (Math.random() * 2);
                myGraph.deleteEdge(edges[i + random]);
                edgeList.remove(edges[i + random]);
                restEdges[j] = edges[i + (1 - random)];
                j++;
            }

            // delete randomly some of the rest of the nodes
            deleteEdgesRandomly(myGraph, restEdges, edgeList, edgesToDelete);
        }

        drawNodes(nodes);

        // make the edge arrows visible
        setEdgeArrows(myGraph);

        // bend the edges
        if (edgeBendingParam.getBoolean().booleanValue()) {
            bendMultiEdges(edgeList);
        }

        myGraph.getListenerManager().transactionFinished(this);

        graph.addGraph(myGraph);
        
        // label the nodes
        if (nodeLabelParam.getBoolean().booleanValue()) {
            labelNodes(nodes, startNumberParam.getValue().intValue());
        }

        // label the edges
        if (edgeLabelParam.getBoolean().booleanValue()) {
            labelEdges(edgeList, edgeLabelNameParam.getString(), edgeMin
                    .getValue().intValue(), edgeMax.getValue().intValue());
        }

        

//        for (Edge edge : edgeList) {
//            myGraph.deleteEdge(edge);
//        }
//        for (Node node : nodes) {
//            myGraph.deleteNode(node);
//        }
        myGraph = null;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        nodesParam.setValue(new Integer(5));
        map_graph_sel.setSelectedValue(0);
        edgesToDeleteParam.setValue(new Integer(0));
    }

    /**
     * Deletes the <code>numberOfEdgesToDelete</code> from the
     * <code>edges</code> array.
     * 
     * @param edges
     *            An array of edges.
     * @param edgeList
     *            The list of edges.
     * @param numberOfEdgesToDelete
     *            The number of edges to delete from <code>edges</code>.
     */
    private void deleteEdgesRandomly(Graph myGraph, Edge[] edges,
            Collection<Edge> edgeList, int numberOfEdgesToDelete) {
        for (int i = 0; i < numberOfEdgesToDelete; i++) {
            int random = (int) (Math.random() * edges.length);

            if (edges[random] != null) {
                myGraph.deleteEdge(edges[random]);
                edgeList.remove(edges[random]);
                edges[random] = null;
            } else {
                i--;
            }
        }
    }

    /**
     * Adds a form selection option.
     */
    @Override
    protected void addFormSelOption() {
        if (formSelection)
            return;

        formSelection = true;

        String[] options = { DYNAMIC_CIRCLE, STATIC_CIRCLE, RANDOM,
                PLANAR_DRAWING };
        form = new StringSelectionParameter(options, "form",
                "the graphical form");
        parameterList.add(form);
    }

    /**
     * Calls a method to place the nodes graphically.
     * 
     * @param nodes
     *            The nodes to place graphically.
     */
    private void drawNodes(Collection<Node> nodes) {
        if (form.getSelectedValue() == PLANAR_DRAWING) {
            // TODO do the planar drawing
        } else {
            formGraph(nodes, form.getSelectedValue());
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
