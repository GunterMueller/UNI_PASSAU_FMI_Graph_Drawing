package org.graffiti.plugins.algorithms.circulardrawing.benchmark;

import java.awt.geom.Point2D;
import java.util.NoSuchElementException;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * Die Klasse erzeugt einen zweifach zusammenh�ngenden Graph mit random layout.
 * Es wird mit einer initialen Kreis angefangen und mit zuf�llig erzeugten
 * einfachen Wege erweitert. Die Kanten werden ausser initialen Kreis und Weg
 * Knoten, zuf�llig zwischen den Kanten eingef�gt.
 * 
 * @author demirci Created on Jul 18, 2005
 */
public class RandomBiconGraphGenerator extends AbstractAlgorithm {

    private IntegerParameter nodesParam;

    private IntegerParameter numberOfEdges;

    public RandomBiconGraphGenerator() {
        nodesParam = new IntegerParameter(5, "number of nodes",
                "the number of nodes to generate");
        numberOfEdges = new IntegerParameter(
                nodesParam.getInteger().intValue(), "number of edges",
                "the number of edges to generate");
    }

    /**
     * 
     * @uml.property name="nodesParam"
     */
    public void setNodesParam(IntegerParameter i) {
        nodesParam = i;
    }

    /**
     * 
     * @uml.property name="numberOfEdges"
     */
    public void setNumberOfEdges(IntegerParameter i) {
        numberOfEdges = i;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "RandomBiconGGenerator";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter<?>[] { nodesParam, numberOfEdges };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (nodesParam.getInteger().compareTo(new Integer(0)) < 0) {
            errors.add("The number of nodes may not be smaller than zero.");
        }
        if (numberOfEdges.getInteger().compareTo(nodesParam.getInteger()) < 0) {
            errors
                    .add("The number of edges may not be smaller than number of nodes.");
        }
        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;

    }

    /**
     * @param length
     * @return random number
     */
    public int getRandomPos(int length) {
        double random = Math.random();
        Float flo = new Float((length - 1) * random);
        int pos = Math.round(flo.floatValue());
        return pos;
    }

    /**
     * @param size
     * @return a random position for the node
     */
    public double getRandomNodePos(int size) {
        double random = Math.random();
        double pos = random * size;
        return pos;
    }

    /**
     * generate a circle Graph
     * 
     * @param circleNodeNumber
     */
    public void generateCircle(int circleNodeNumber) {
        Node[] circleNodes = new Node[circleNodeNumber];
        for (int i = 0; i < circleNodeNumber; i++) {
            Node node = graph.addNode();
            circleNodes[i] = node;
        }

        int length = circleNodes.length;
        for (int i = 0; i < length; i++) {
            Node node1 = circleNodes[i];
            Node node2 = circleNodes[(i + 1) % length];
            graph.addEdge(node1, node2, false);
        }
        graph.setDirected(false);
    }

    /**
     * add the generete path to the graph at the random nodes in the Graph
     * 
     * @param pathLength
     */
    public void addPathToGraph(int pathLength) {
        int graphNodeNumber = graph.getNumberOfNodes();
        int firstNodePos = getRandomPos(graphNodeNumber);
        int secondNodePos = getRandomPos(graphNodeNumber);
        boolean bol = firstNodePos == secondNodePos;
        while (bol) {
            secondNodePos = getRandomPos(graphNodeNumber);
            bol = firstNodePos == secondNodePos;
        }
        Object[] graphNodes = graph.getNodes().toArray();
        Node node1 = (Node) graphNodes[firstNodePos];
        Node node2 = (Node) graphNodes[secondNodePos];

        Node[] pathNodes = new Node[pathLength];
        for (int i = 0; i < pathLength; i++) {
            Node pathNode = graph.addNode();
            pathNodes[i] = pathNode;
        }

        for (int i = 0; i < pathLength - 1; i++) {
            Node pathNode1 = pathNodes[i];
            Node pathNode2 = pathNodes[i + 1];
            graph.addEdge(pathNode1, pathNode2, false);
        }

        Node pathStartNode = pathNodes[0];
        Node pathEndNode = pathNodes[pathLength - 1];
        graph.addEdge(node1, pathStartNode, false);
        graph.addEdge(pathEndNode, node2, false);
    }

    /**
     * @param o
     */
    public void printGraphMatrix(Object[] o) {
        graph.getListenerManager().transactionStarted(this);

        System.out.print("  ");
        for (int i = 0; i < o.length; i++) {
            System.out.print(i + "  ");
        }
        System.out.println();
        for (int i = 0; i < o.length; i++) {
            Node n = (Node) o[i];
            System.out.print(i + " ");
            for (int j = 0; j < o.length; j++) {
                Node node = (Node) o[j];
                try {
                    Edge e = graph.getEdges(n, node).iterator().next();
                    System.out.print(e.getString("label.label") + " ");
                } catch (NoSuchElementException e) {
                    System.out.print("-  ");
                }
            }
            System.out.println();
        }
        // stop a transaction
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
        nodesParam.setValue(new Integer(5));
        numberOfEdges.setValue(new Integer(5));
    }

    public void reset2() {
        graph = null;
        nodesParam.setValue(new Integer(0));
        numberOfEdges.setValue(new Integer(0));
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {

        int nodeNumber = nodesParam.getInteger().intValue();
        graph.getListenerManager().transactionStarted(this);

        int circleNodeNumber = 3;
        int randomCircleNodeNumber = getRandomPos(nodeNumber);
        if (circleNodeNumber < randomCircleNodeNumber) {
            circleNodeNumber = randomCircleNodeNumber;
        }

        // System.out.println("circle node number ist " + circleNodeNumber);
        generateCircle(circleNodeNumber);

        int graphNodeNumber = graph.getNumberOfNodes();
        // System.out.println("graphNodeNumber ist " + graphNodeNumber);
        while (graphNodeNumber < nodeNumber) {
            if (circleNodeNumber != nodeNumber) {
                int pathNodeNumber = 1;
                int possibleNodeNumber = nodeNumber - graphNodeNumber;
                // System.out.println("possibleNodeNumber ist " +
                // possibleNodeNumber);
                int randomPathNodeNumber = getRandomPos(possibleNodeNumber);
                // System.out.println("randomPathNodeNumber ist " +
                // randomPathNodeNumber);
                if (pathNodeNumber < randomPathNodeNumber) {
                    pathNodeNumber = randomPathNodeNumber;
                }
                // System.out.println("path l�nge ist " + pathNodeNumber);
                addPathToGraph(pathNodeNumber);
                // System.out.println("path wurde an graph eingef�gt ");
                graphNodeNumber = graph.getNumberOfNodes();
                // System.out.println("nach dem einf�gen des pfades hat der
                // graph " +
                // graphNodeNumber + " knoten");
            }
        }

        Object[] graphNodes = graph.getNodes().toArray();
        int edgeNumber = graph.getNumberOfEdges();
        int allEdgesNumber = nodeNumber * (nodeNumber - 1) * 1 / 2;
        int maxEdges = numberOfEdges.getInteger().intValue();
        while (edgeNumber <= maxEdges - 1) {
            int j = getRandomPos(nodeNumber);
            int k = getRandomPos(nodeNumber);
            if (j != k) {
                Node no1 = (Node) graphNodes[j];
                // System.out.println("no1 ist " + no1);

                Node no2 = (Node) graphNodes[k];
                // System.out.println("no2 ist " + no2);
                if (edgeNumber != allEdgesNumber) {
                    try {
                        graph.getEdges(no1, no2).iterator().next();
                    } catch (NoSuchElementException e) {
                        edgeNumber++;
                        graph.addEdge(no1, no2, false);
                        // System.out.println("Kante wurde eingef�gt");
                        if (edgeNumber == allEdgesNumber) {
                            break;
                        }
                    }
                }
            }
        }
        edgeNumber = graph.getNumberOfEdges();
        int areaLength = 500 + (nodeNumber * 10) / 100 + (edgeNumber * 10)
                / 100;
        for (int i = 0; i < nodeNumber; ++i) {
            Node node = (Node) graphNodes[i];

            CoordinateAttribute ca = (CoordinateAttribute) node
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            double x = getRandomNodePos(areaLength) + 25.0;
            double y = getRandomNodePos(areaLength) + 25.0;
            ca.setCoordinate(new Point2D.Double(x, y));

        }

        System.out.println("Graph wurde erzeugt");
        System.out.print("Erzeugte Graph hat " + graph.getNumberOfNodes()
                + " Knoten");
        System.out.println(" und " + graph.getNumberOfEdges() + " Kanten");

        graph.getListenerManager().transactionFinished(this);
    }
}
