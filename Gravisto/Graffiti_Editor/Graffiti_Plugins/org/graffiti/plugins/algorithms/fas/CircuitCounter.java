package org.graffiti.plugins.algorithms.fas;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.SchnyderRealizer.HashList;

public class CircuitCounter extends AbstractAlgorithm {

    private HashMap<Node, Integer> nodeNumber = new HashMap<Node, Integer>();

    private HashMap<Integer, Node> numberNode = new HashMap<Integer, Node>();

    private HashMap<Edge, Integer> edgeNumber = new HashMap<Edge, Integer>();

    private HashMap<Integer, Edge> numberEdge = new HashMap<Integer, Edge>();

    public CircuitCounter() {
    }

    /**
     * Returns the name of the algorithm
     * 
     * @return name of the algorithm
     */
    public String getName() {
        return "Circuit Counter";
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
        if (this.graph.getNodes().size() == 0)
            throw new PreconditionException("Graph contains no nodes.");
    }

    private void createNodeIndex() {
        this.nodeNumber.clear();
        this.numberNode.clear();
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
        this.edgeNumber.clear();
        this.numberEdge.clear();
        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        int i = 0;
        while (edgesIt.hasNext()) {
            Edge e = edgesIt.next();
            this.edgeNumber.put(e, new Integer(i));
            this.numberEdge.put(new Integer(i), e);
            i++;
        }
    }

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

    public void execute() {
        this.createNodeIndex();
        this.createEdgeIndex();
        GetStronglyConnectedComponents gscc = new GetStronglyConnectedComponents(
                this.graph, this);
        gscc.execute();
        this.colorEdges(gscc.getBackEdges(), Color.GREEN);
        this.colorEdges(gscc.getForwardEdges(), Color.RED);
        this.colorEdges(gscc.getTreeEdges(), Color.BLUE);
        this.colorEdges(gscc.getCrossEdges(), Color.MAGENTA);
    }

    protected void colorEdges(HashList<Edge> toColor, Color color) {
        LinkedList<Edge> list = new LinkedList<Edge>();
        Iterator<Edge> edgesIt = toColor.iterator();
        while (edgesIt.hasNext()) {
            list.add(edgesIt.next());
        }
        this.colorEdges(list, color);
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
}