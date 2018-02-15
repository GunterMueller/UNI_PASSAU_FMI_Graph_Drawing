// =============================================================================
//
//   PlanarTriconnectedGraphGenerator.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.generators;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree.Vector;

/**
 * This generator creates a planar, triconnected graph. It is used for the
 * measurement of the execution time of isSDLayout in the class SDlayout to
 * generate 100 graphs for every number of nodes between 10 and 100.
 * 
 * @author Christina Ehrlinger
 * @version $Revision$ $Date$
 */
public class PlanarTriconnectedGraphGenerator extends AbstractGenerator {

    /**
     * attribute for the parameter for the number of the created nodes
     */
    private IntegerParameter maxNodeNumberParam;

    /**
     * attribute for the number of the created nodes
     */
    private int maxNodeNumber;

    /**
     * attribute for the current created nodes
     */
    private int currentNodeNumber;

    /**
     * attribute for the list of the current faces
     */
    private List<Face> faces;

    /**
     * attribute for the zoom of the drawing
     */
    private double zoom;

    /**
     * The constructor creates an instance of a PlanarTriconnectedGraphGenerator
     * with the IntegerParameter for the number of nodes and an empty list for
     * the faces.
     */
    public PlanarTriconnectedGraphGenerator() {
        faces = new ArrayList<Face>();

        parameterList = new LinkedList<Parameter<?>>();
        maxNodeNumberParam = new IntegerParameter(9, "Number of nodes",
                "maximum number of the nodes, which are created (Minimum is 9)");
        parameterList.addFirst(maxNodeNumberParam);
    }

    /**
     * This method checks, if the graph is null or the given number of the nodes
     * is less than 9.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }
        if (maxNodeNumberParam.getValue() < 9) {
            errors.add("Minimum number of nodes is 9.");
        }

        if (!errors.isEmpty()) {
            throw errors;
        }
    }

    /**
     * This method selects one shape for the base frame and places the nodes
     * randomly in the created faces.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    @Override
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        graph.clear();
        faces.clear();
        maxNodeNumber = maxNodeNumberParam.getValue();

        if (maxNodeNumber <= 20) {
            int formRandom = (int) (Math.random() * 3);
            if (formRandom == 0) {
                createTriangle();
            } else if (formRandom == 1) {
                createSquares();
            } else {
                createOctagons();
            }
        } else if (maxNodeNumber <= 40) {
            int formRandom = (int) (Math.random() * 2);
            if (formRandom == 0) {
                createSquares();
            } else {
                createOctagons();
            }
        } else {
            createOctagons();
        }

        // while the number of nodes is not reached, a new node is placed in the
        // center of a randomly choosed face.
        while (currentNodeNumber < maxNodeNumber) {
            int nextFaceIndex = (int) (Math.random() * faces.size() / 5);
            Face face = faces.remove(nextFaceIndex);

            Node n9 = graph.addNode();
            addGraphicAttributetoNode(n9);

            CoordinateAttribute ca9 = (CoordinateAttribute) n9
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);

            if (face.getNodes().size() == 3) {
                ca9.setCoordinate(calculateCenterTriangle(face));

                Face f1 = new Face(face.getNodeAtIndex(0),
                        face.getNodeAtIndex(1), n9);
                Face f2 = new Face(face.getNodeAtIndex(0),
                        face.getNodeAtIndex(2), n9);
                Face f3 = new Face(face.getNodeAtIndex(1),
                        face.getNodeAtIndex(2), n9);

                faces.add(f1);
                faces.add(f2);
                faces.add(f3);

                Edge ee1 = graph.addEdge(face.getNodeAtIndex(0), n9, false);
                addGraphicAttributeToEdges(ee1);

                Edge ee2 = graph.addEdge(face.getNodeAtIndex(1), n9, false);
                addGraphicAttributeToEdges(ee2);

                Edge ee3 = graph.addEdge(face.getNodeAtIndex(2), n9, false);
                addGraphicAttributeToEdges(ee3);
            } else if (face.getNodes().size() == 4) {
                ca9.setCoordinate(calculateCenterTrapez(face));

                faces.add(new Face(face.getNodeAtIndex(0), face
                        .getNodeAtIndex(1), n9));
                faces.add(new Face(face.getNodeAtIndex(1), face
                        .getNodeAtIndex(2), n9));
                faces.add(new Face(face.getNodeAtIndex(2), face
                        .getNodeAtIndex(3), n9));
                faces.add(new Face(face.getNodeAtIndex(3), face
                        .getNodeAtIndex(0), n9));

                Edge ee1 = graph.addEdge(face.getNodeAtIndex(0), n9, false);
                addGraphicAttributeToEdges(ee1);

                Edge ee2 = graph.addEdge(face.getNodeAtIndex(1), n9, false);
                addGraphicAttributeToEdges(ee2);

                Edge ee3 = graph.addEdge(face.getNodeAtIndex(2), n9, false);
                addGraphicAttributeToEdges(ee3);

                Edge ee4 = graph.addEdge(face.getNodeAtIndex(3), n9, false);
                addGraphicAttributeToEdges(ee4);
            } else {
                ca9.setCoordinate(new Point2D.Double(0, 0));

                for (int i = 0; i < face.getNodes().size(); i++) {
                    Edge eNew = graph
                            .addEdge(face.getNodeAtIndex(i), n9, false);
                    addGraphicAttributeToEdges(eNew);
                }
                for (int i = 1; i < face.getNodes().size(); i++) {
                    faces.add(new Face(face.getNodeAtIndex(i - 1), face
                            .getNodeAtIndex(i), n9));
                }
                faces.add(new Face(face.getNodeAtIndex(0), face
                        .getNodeAtIndex(7), n9));
            }

            currentNodeNumber++;
        }
        graph.getListenerManager().transactionFinished(this);

    }

    /**
     * This method creates a octagon shaped base frame. The number of the
     * octagons is not constant and depends on the number of the maximal number.
     */
    private void createOctagons() {

        zoom = maxNodeNumber / 20.0;

        int randomOctagon = (int) (Math.random() * maxNodeNumber / 4.0);
        int numberOfOctagons = maxNodeNumber / (12 + randomOctagon);
        int maxNumberOfOctagon = maxNodeNumber / (12 + randomOctagon);
        int numberCurrentOctagons = 0;
        // the eight nodes at the convex hull
        Node n1 = graph.addNode();
        addGraphicAttributetoNode(n1);

        Node n2 = graph.addNode();
        addGraphicAttributetoNode(n2);

        Node n3 = graph.addNode();
        addGraphicAttributetoNode(n3);

        Node n4 = graph.addNode();
        addGraphicAttributetoNode(n4);

        Node n5 = graph.addNode();
        addGraphicAttributetoNode(n5);

        Node n6 = graph.addNode();
        addGraphicAttributetoNode(n6);

        Node n7 = graph.addNode();
        addGraphicAttributetoNode(n7);

        Node n8 = graph.addNode();
        addGraphicAttributetoNode(n8);

        List<Node> nodes = new LinkedList<Node>();
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        nodes.add(n5);
        nodes.add(n6);
        nodes.add(n7);
        nodes.add(n8);

        CoordinateAttribute ca1 = (CoordinateAttribute) n1
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca1.setCoordinate(new Point2D.Double(-140 * zoom, -410 * zoom));

        CoordinateAttribute ca2 = (CoordinateAttribute) n2
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca2.setCoordinate(new Point2D.Double(-410 * zoom, -140 * zoom));

        CoordinateAttribute ca3 = (CoordinateAttribute) n3
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca3.setCoordinate(new Point2D.Double(-410 * zoom, 140 * zoom));

        CoordinateAttribute ca4 = (CoordinateAttribute) n4
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca4.setCoordinate(new Point2D.Double(-140 * zoom, 410 * zoom));

        CoordinateAttribute ca5 = (CoordinateAttribute) n5
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca5.setCoordinate(new Point2D.Double(140 * zoom, 410 * zoom));

        CoordinateAttribute ca6 = (CoordinateAttribute) n6
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca6.setCoordinate(new Point2D.Double(410 * zoom, 140 * zoom));

        CoordinateAttribute ca7 = (CoordinateAttribute) n7
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca7.setCoordinate(new Point2D.Double(410 * zoom, -140 * zoom));

        CoordinateAttribute ca8 = (CoordinateAttribute) n8
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca8.setCoordinate(new Point2D.Double(140 * zoom, -410 * zoom));

        for (int i = 1; i < nodes.size(); i++) {
            Edge e = graph.addEdge(nodes.get(i - 1), nodes.get(i), false);
            addGraphicAttributeToEdges(e);
        }

        Edge e = graph
                .addEdge(nodes.get(nodes.size() - 1), nodes.get(0), false);
        addGraphicAttributeToEdges(e);

        faces.add(new Face(nodes));
        currentNodeNumber = 8;
        numberCurrentOctagons++;

        // foundation has the shape of octagons
        while (numberOfOctagons > 1) {
            Face outerSquare = faces.remove((faces.size() - 1));

            Node nn1 = graph.addNode();
            addGraphicAttributetoNode(nn1);

            Node nn2 = graph.addNode();
            addGraphicAttributetoNode(nn2);

            Node nn3 = graph.addNode();
            addGraphicAttributetoNode(nn3);

            Node nn4 = graph.addNode();
            addGraphicAttributetoNode(nn4);

            Node nn5 = graph.addNode();
            addGraphicAttributetoNode(nn5);

            Node nn6 = graph.addNode();
            addGraphicAttributetoNode(nn6);

            Node nn7 = graph.addNode();
            addGraphicAttributetoNode(nn7);

            Node nn8 = graph.addNode();
            addGraphicAttributetoNode(nn8);

            List<Node> innerNodes = new LinkedList<Node>();
            innerNodes.add(nn1);
            innerNodes.add(nn2);
            innerNodes.add(nn3);
            innerNodes.add(nn4);
            innerNodes.add(nn5);
            innerNodes.add(nn6);
            innerNodes.add(nn7);
            innerNodes.add(nn8);

            Vector v1 = new Vector((140 * zoom) / (maxNumberOfOctagon),
                    (410 * zoom) / (maxNumberOfOctagon));
            CoordinateAttribute can1 = (CoordinateAttribute) nn1
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            can1.setCoordinate(new Point2D.Double(-140 * zoom
                    + numberCurrentOctagons * v1.getX(), -410 * zoom
                    + numberCurrentOctagons * v1.getY()));

            Vector v2 = new Vector((410 * zoom) / (maxNumberOfOctagon),
                    (140 * zoom) / (maxNumberOfOctagon));
            CoordinateAttribute can2 = (CoordinateAttribute) nn2
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            can2.setCoordinate(new Point2D.Double(-410 * zoom
                    + numberCurrentOctagons * v2.getX(), -140 * zoom
                    + numberCurrentOctagons * v2.getY()));

            Vector v3 = new Vector((410 * zoom) / (maxNumberOfOctagon),
                    (-140 * zoom) / (maxNumberOfOctagon));
            CoordinateAttribute can3 = (CoordinateAttribute) nn3
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            can3.setCoordinate(new Point2D.Double(-410 * zoom
                    + numberCurrentOctagons * v3.getX(), 140 * zoom
                    + numberCurrentOctagons * v3.getY()));

            Vector v4 = new Vector((140 * zoom) / (maxNumberOfOctagon),
                    (-410 * zoom) / (maxNumberOfOctagon));
            CoordinateAttribute can4 = (CoordinateAttribute) nn4
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            can4.setCoordinate(new Point2D.Double(-140 * zoom
                    + numberCurrentOctagons * v4.getX(), 410 * zoom
                    + numberCurrentOctagons * v4.getY()));

            Vector v5 = new Vector((-140 * zoom) / (maxNumberOfOctagon),
                    (-410 * zoom) / (maxNumberOfOctagon));
            CoordinateAttribute can5 = (CoordinateAttribute) nn5
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            can5.setCoordinate(new Point2D.Double(140 * zoom
                    + numberCurrentOctagons * v5.getX(), 410 * zoom
                    + numberCurrentOctagons * v5.getY()));

            Vector v6 = new Vector((-410 * zoom) / (maxNumberOfOctagon),
                    (-140 * zoom) / (maxNumberOfOctagon));
            CoordinateAttribute can6 = (CoordinateAttribute) nn6
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            can6.setCoordinate(new Point2D.Double(410 * zoom
                    + numberCurrentOctagons * v6.getX(), 140 * zoom
                    + numberCurrentOctagons * v6.getY()));

            Vector v7 = new Vector((-410 * zoom) / (maxNumberOfOctagon),
                    (140 * zoom) / (maxNumberOfOctagon));
            CoordinateAttribute can7 = (CoordinateAttribute) nn7
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            can7.setCoordinate(new Point2D.Double(410 * zoom
                    + numberCurrentOctagons * v7.getX(), -140 * zoom
                    + numberCurrentOctagons * v7.getY()));

            Vector v8 = new Vector((-140 * zoom) / (maxNumberOfOctagon),
                    (410 * zoom) / (maxNumberOfOctagon));
            CoordinateAttribute can8 = (CoordinateAttribute) nn8
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            can8.setCoordinate(new Point2D.Double(140 * zoom
                    + numberCurrentOctagons * v8.getX(), -410 * zoom
                    + numberCurrentOctagons * v8.getY()));

            for (int i = 0; i < innerNodes.size(); i++) {
                Edge ee = graph.addEdge(outerSquare.getNodeAtIndex(i),
                        innerNodes.get(i), false);
                addGraphicAttributeToEdges(ee);
            }

            for (int i = 1; i < innerNodes.size(); i++) {
                Edge eee = graph.addEdge(innerNodes.get(i - 1),
                        innerNodes.get(i), false);
                addGraphicAttributeToEdges(eee);
            }

            Edge eee = graph.addEdge(innerNodes.get(innerNodes.size() - 1),
                    innerNodes.get(0), false);
            addGraphicAttributeToEdges(eee);

            for (int i = 1; i < innerNodes.size(); i++) {
                faces.add(new Face(outerSquare.getNodeAtIndex(i - 1),
                        outerSquare.getNodeAtIndex(i), innerNodes.get(i),
                        innerNodes.get(i - 1)));
            }
            faces.add(new Face(outerSquare.getNodeAtIndex(7), outerSquare
                    .getNodeAtIndex(0), innerNodes.get(0), innerNodes.get(7)));
            faces.add(new Face(innerNodes));

            numberOfOctagons--;
            numberCurrentOctagons++;
            currentNodeNumber += 8;
        }
    }

    /**
     * This method creates a square shaped base frame. Only possible for less
     * than 41 nodes.
     */
    private void createSquares() {

        zoom = maxNodeNumber / 20.0;

        Node n1 = graph.addNode();
        addGraphicAttributetoNode(n1);

        Node n2 = graph.addNode();
        addGraphicAttributetoNode(n2);

        Node n3 = graph.addNode();
        addGraphicAttributetoNode(n3);

        Node n4 = graph.addNode();
        addGraphicAttributetoNode(n4);

        List<Node> nodes = new LinkedList<Node>();
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);

        CoordinateAttribute ca1 = (CoordinateAttribute) n1
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca1.setCoordinate(new Point2D.Double(-410 * zoom, -410 * zoom));

        CoordinateAttribute ca2 = (CoordinateAttribute) n2
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca2.setCoordinate(new Point2D.Double(-410 * zoom, 410 * zoom));

        CoordinateAttribute ca3 = (CoordinateAttribute) n3
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca3.setCoordinate(new Point2D.Double(410 * zoom, 410 * zoom));

        CoordinateAttribute ca4 = (CoordinateAttribute) n4
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca4.setCoordinate(new Point2D.Double(410 * zoom, -410 * zoom));

        for (int i = 1; i < nodes.size(); i++) {
            Edge e = graph.addEdge(nodes.get(i - 1), nodes.get(i), false);
            addGraphicAttributeToEdges(e);
        }

        Edge e = graph
                .addEdge(nodes.get(nodes.size() - 1), nodes.get(0), false);
        addGraphicAttributeToEdges(e);

        faces.add(new Face(nodes));
        currentNodeNumber = 4;
    }

    /**
     * This mehtod creates a triangle shaped base frame. Only possible for less
     * than 21 nodes.
     */
    private void createTriangle() {

        zoom = maxNodeNumber / 10.0;

        Node n1 = graph.addNode();
        addGraphicAttributetoNode(n1);

        Node n2 = graph.addNode();
        addGraphicAttributetoNode(n2);

        Node n3 = graph.addNode();
        addGraphicAttributetoNode(n3);

        List<Node> nodes = new LinkedList<Node>();
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);

        CoordinateAttribute ca1 = (CoordinateAttribute) n1
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca1.setCoordinate(new Point2D.Double(0, -410 * zoom));

        CoordinateAttribute ca2 = (CoordinateAttribute) n2
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca2.setCoordinate(new Point2D.Double(-410 * zoom, 410 * zoom));

        CoordinateAttribute ca3 = (CoordinateAttribute) n3
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca3.setCoordinate(new Point2D.Double(410 * zoom, 410 * zoom));

        for (int i = 1; i < nodes.size(); i++) {
            Edge e = graph.addEdge(nodes.get(i - 1), nodes.get(i), false);
            addGraphicAttributeToEdges(e);
        }

        Edge e = graph
                .addEdge(nodes.get(nodes.size() - 1), nodes.get(0), false);
        addGraphicAttributeToEdges(e);

        faces.add(new Face(nodes));
        currentNodeNumber = 3;
    }

    /**
     * The method calculates the center of the chosen face, which is a
     * triangle.
     * 
     * @param face
     *            : the chosen face
     * @return : the point at the center of the face
     */
    private Point2D calculateCenterTriangle(Face face) {
        double xCoord = 0;
        double yCoord = 0;
        for (Node node : face.getNodes()) {
            CoordinateAttribute coord = (CoordinateAttribute) node
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            xCoord = xCoord + coord.getX();
            yCoord = yCoord + coord.getY();
        }

        return new Point2D.Double(xCoord / 3, yCoord / 3);
    }

    /**
     * The method calculates the center of the chosen face, which is a trapez.
     * 
     * @param face
     *            : the chosen face
     * @return : the point at the center of the face
     */
    private Point2D calculateCenterTrapez(Face face) {
        CoordinateAttribute c1 = (CoordinateAttribute) face.getNodeAtIndex(0)
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        double x1 = c1.getX();
        double y1 = c1.getY();

        CoordinateAttribute c2 = (CoordinateAttribute) face.getNodeAtIndex(1)
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        double x2 = c2.getX();
        double y2 = c2.getY();

        CoordinateAttribute c4 = (CoordinateAttribute) face.getNodeAtIndex(3)
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        double x4 = c4.getX();
        double y4 = c4.getY();

        Vector v1 = new Vector(0.5 * (x2 - x1), 0.5 * (y2 - y1));
        Vector v2 = new Vector(0.5 * (x4 - x1), 0.5 * (y4 - y1));

        double newX, newY;
        newX = x1 + v1.getX();
        newY = y1 + v1.getY();

        double length2 = Math.sqrt(v2.getX() * v2.getX() + v2.getY()
                * v2.getY());

        Vector v3 = new Vector(-newX, -newY);
        double length3 = Math.sqrt(v3.getX() * v3.getX() + v3.getY()
                * v3.getY());
        v3 = new Vector(v3.getX() / length3, v3.getY() / length3);
        newX = newX + (v3.getX() * length2);
        newY = newY + (v3.getY() * length2);

        return new Point2D.Double(newX, newY);
    }

    /**
     * The method creates the graphic attributes for a node
     * 
     * @param node
     *            : the node, for which the attributes are created
     */
    private void addGraphicAttributetoNode(Node node) {
        if (!node.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
            node.addAttribute(new NodeGraphicAttribute(), "");
        }
    }

    /**
     * The method creates the graphic attributes for an edge
     * 
     * @param edge
     *            : the edge, for which the attributes are created
     */
    private void addGraphicAttributeToEdges(Edge edge) {
        if (!edge.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
            edge.addAttribute(new EdgeGraphicAttribute(edge.isDirected()), "");
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    @Override
    public String getName() {
        return "Planar Triconnected Graph Generator";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {

        parameters = new Parameter[parameterList.size()];
        return parameterList.toArray(parameters);
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        maxNodeNumberParam = (IntegerParameter) params[0];
    }

    /**
     * This class represents a area in the graph.
     * 
     * @author Christina Ehrlinger
     * @version $Revision$ $Date$
     */
    public class Face {

        /**
         * attribute for the list of the nodes
         */
        private List<Node> nodes;

        /**
         * The constructor creates an instance of a face with the given nodes
         * 
         * @param n1
         *            : first node of the face
         * @param n2
         *            : second node of the face
         * @param n3
         *            : third node of the face
         */
        public Face(Node n1, Node n2, Node n3) {
            nodes = new ArrayList<Node>();
            nodes.add(n1);
            nodes.add(n2);
            nodes.add(n3);
        }

        /**
         * The constructor creates an instance of a face with the given nodes
         * 
         * @param n1
         *            : first node of the face
         * @param n2
         *            : second node of the face
         * @param n3
         *            : third node of the face
         * @param n4
         *            : fourth node of the face
         */
        public Face(Node n1, Node n2, Node n3, Node n4) {
            nodes = new ArrayList<Node>();
            nodes.add(n1);
            nodes.add(n2);
            nodes.add(n3);
            nodes.add(n4);
        }

        /**
         * The constructor creates an instance of a face with the given nodes
         * 
         * @param nodes
         *            : list of the nodes of the face
         */
        public Face(List<Node> nodes) {
            this.nodes = nodes;
        }

        /**
         * The method gives the list of the nodes
         * 
         * @return : the list of the nodes
         */
        public List<Node> getNodes() {
            return nodes;
        }

        /**
         * The method overwrites the nodes with the given nodes
         * 
         * @param set
         *            : a new list of nodes
         */
        public void setNodes(List<Node> set) {
            nodes = set;
        }

        /**
         * The method returns the node at the given index
         * 
         * @param index
         *            : the index of the node
         * @return : the node at the given index
         */
        public Node getNodeAtIndex(int index) {
            return nodes.get(index);
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
