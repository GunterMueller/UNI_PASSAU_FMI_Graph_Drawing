package org.graffiti.plugins.algorithms.fas;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.SchnyderRealizer.HashList;

public class FASRelatedAlgorithms extends AbstractAlgorithm {

    private HashMap<Node, Integer> nodeNumber = new HashMap<Node, Integer>();
    private HashMap<Integer, Node> numberNode = new HashMap<Integer, Node>();
    private HashMap<Edge, Integer> edgeNumber = new HashMap<Edge, Integer>();
    private HashMap<Integer, Edge> numberEdge = new HashMap<Integer, Edge>();

    public int nodeToNumber(Node n) {
        return this.nodeNumber.get(n).intValue();
    }

    public Node numberToNode(int x) {
        return this.numberNode.get(new Integer(x));
    }

    public int edgeToNumber(Edge e) {
        return this.edgeNumber.get(e).intValue();
    }

    public Edge numberToEdge(int x) {
        return this.numberEdge.get(new Integer(x));
    }

    private void createNodeIndex() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        int i = 0;
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            this.nodeNumber.put(n, new Integer(i));
            this.numberNode.put(new Integer(i), n);
            i++;
        }
    }

    private void createEdgeIndex() {
        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        int i = 0;
        while (edgesIt.hasNext()) {
            Edge e = edgesIt.next();
            this.edgeNumber.put(e, new Integer(i));
            this.numberEdge.put(new Integer(i), e);
            i++;
        }
    }

    private void labelNodes() {

        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node nodeToLabel = nodesIt.next();
            String labelString = (this.nodeNumber.get(nodeToLabel) + "");

            NodeLabelAttribute nodeLabel = new NodeLabelAttribute("label");
            try {
                nodeLabel = (NodeLabelAttribute) nodeToLabel.getAttributes()
                        .getAttribute("label");

                nodeLabel.setLabel(labelString);
            } catch (AttributeNotFoundException e) {
                nodeLabel.setLabel(labelString);
                nodeToLabel.getAttributes().add(nodeLabel);
            }
        }
    }

    /**
     * Returns the name of the algorithm
     * 
     * @return name of the algorithm
     */
    public String getName() {
        return "FAS Branch and Bound";
    }

    /**
     * Sets the parameters of the algorithm
     * 
     * @param params
     *            the parameters of the algorithm
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
    }

    /**
     * Gets the parameters of the algorithm
     * 
     * @return the parameters of the algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        Parameter<?>[] parameter = new Parameter[0];
        return parameter;
    }

    /**
     * Checks the algorithms preconditions: - graph is directed
     * 
     * @throws PreconditionException
     *             if any of the preconditions is not satisfied.
     */
    @Override
    public void check() throws PreconditionException {

        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        while (edgesIt.hasNext()) {
            if (!edgesIt.next().isDirected())
                throw new PreconditionException(
                        "The graph is not dircected. Can't solve the minimum Feedback Arc Set Problem");
        }
    }

    public void execute() {
        this.removeMultiedgesLoops();
        this.createNodeIndex();
        this.createEdgeIndex();
        this.labelNodes();

        BFSCircuitCounter dcc = new BFSCircuitCounter(this.graph, this);
        dcc.execute();

        this.colorEdges(dcc.getBackEdges(), Color.GREEN);
        this.colorEdges(dcc.getForwardEdges(), Color.RED);
        this.colorEdges(dcc.getTreeEdges(), Color.BLACK);
        this.colorEdges(dcc.getCrossEdges(), Color.MAGENTA);
        this.labelEdges(dcc.getResult());

        // Performs an algorithm that reports every elementary circuit
        // FindElementaryCircuits fec = new FindElementaryCircuits(this.graph,
        // this);
        // fec.execute();
        // this.labelEdges(fec.getResult());
        //        

        // Performs an branch and bound algorithm to solve the Feedback Arc Set
        // Problem
        // BranchAndBoundTree bTree = new BranchAndBoundTree(this.graph, this);
        // bTree.execute();
        // this.colorEdges(bTree.getResult(), Color.RED);

    }

    /**
     * Checks every edge if there are other edges connecting the same nodes and
     * if so removes them.
     */
    private void removeMultiedgesLoops() {
        LinkedList<Edge> edgesToDelete = new LinkedList<Edge>();
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();

            if (e.getSource() == e.getTarget()) {
                edgesToDelete.add(e);
            }
        }
        edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            Collection<Edge> edges = this.graph.getEdges(e.getSource(), e
                    .getTarget());
            if (edges.size() > 1) {
                Iterator<Edge> multiEdgeIt = edges.iterator();
                multiEdgeIt.next();
                while (multiEdgeIt.hasNext()) {
                    Edge multiEdge = multiEdgeIt.next();
                    edgesToDelete.add(multiEdge);
                }
            }
        }
        edgeIt = edgesToDelete.iterator();
        while (edgeIt.hasNext()) {
            this.graph.deleteEdge(edgeIt.next());
        }
    }

    /**
     * All nodes are labeled with ascending integer numbers. The outer nodes are
     * labeled with "1", "n-1" and "n", where n is the number nodes in the
     * graph. Existing labels are overwritten. This method is only executed if
     * <code>SchnyderRealizerAdministration.label</code> is set true.
     */
    private void labelEdges(int[] circuitsPerEdges) {

        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        while (edgesIt.hasNext()) {
            Edge edgeToLabel = edgesIt.next();
            String labelString = (circuitsPerEdges[this
                    .edgeToNumber(edgeToLabel)] + "");

            EdgeLabelAttribute edgeLabel = new EdgeLabelAttribute("label");
            // Check if there is an existing label
            try {
                edgeLabel = (EdgeLabelAttribute) edgeToLabel.getAttributes()
                        .getAttribute("label");

                edgeLabel.setLabel(labelString);
            } catch (AttributeNotFoundException e) {
                edgeLabel.setLabel(labelString);
                edgeToLabel.getAttributes().add(edgeLabel);
            }
        }
    }

    protected void colorEdges(Collection<Edge> toColor, Color color) {
        Iterator<Edge> edgesIt = toColor.iterator();
        while (edgesIt.hasNext()) {
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edgesIt.next()
                    .getAttribute("graphics");
            ega.getFramecolor().setColor(color);
            ega.getFillcolor().setColor(color);

        }
    }

    protected void colorEdges(HashList<Edge> toColor, Color color) {
        LinkedList<Edge> list = new LinkedList<Edge>();
        Iterator<Edge> edgesIt = toColor.iterator();
        while (edgesIt.hasNext()) {
            list.add(edgesIt.next());
        }
        this.colorEdges(list, color);
    }

}
