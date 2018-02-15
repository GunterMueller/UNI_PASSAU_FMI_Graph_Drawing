package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * This class calculates all neccessary data of the realizers of a graph (e.g.
 * depth of the trees) and of the resulting drawings (e.g. size of the angles),
 * creates a database connection (and if not yet done creates the tables) and
 * writes the data to the database. To avoid mixed data of all algorithms, the
 * output only works for the <code>BrehmAllRealizers</code>-algorithm.
 * 
 * @author hofmeier
 */
public class DataToDB {
    /** The graph whose realizers are calculated */
    private Graph graph;

    /** The outer nodes of the graph. */
    private Node[] outerNodes;

    /** The realizers of the graph */
    private LinkedList<Realizer> realizers;

    /** The faces of the graph */
    private HashList<Face> faces;

    /** The number of realizers of the graph */
    private int numOfRealizers;

    /** The current realizer whose data is calculated */
    private Realizer realizer;

    /** The barycentric representation of the current realizer */
    private BarycentricRepresentation barRep;

    /**
     * Saves the sum of free rows AND columns in the drawings of every realizer
     */
    private int[] sumOfFreeCols;

    /** Saves the depth of the green tree of every realizer */
    private double[] depthOfGreenTree;

    /** Saves the depth of the blue tree of every realizer */
    private double[] depthOfBlueTree;

    /** Saves the depth of the red tree of every realizer */
    private double[] depthOfRedTree;

    /** Saves the average depth of the trees of every realizer */
    private double[] averageDepth;

    /**
     * Saves the largest distance of a trees depth to the average depth of all
     * trees of every realizer.
     */
    private double[] maxDistToAverageDepth;

    /**
     * Saves the average distance of the trees depth to the average depth of all
     * trees of every realizer.
     */
    private double[] avgDistToAvgDepth;

    /** Saves the size of the smallest face of every realizer */
    private double[] smallestFaceSize;

    /**
     * Saves the average face size of the smallest face of every realizer (is
     * identical for every realizer).
     */
    private double[] avgFaceSize;

    /**
     * Saves the largest distance of the face`s size to the average face size of
     * every realizer
     */
    private double[] maxDistToAvgFaceSize;

    /**
     * Saves the average distance of the face`s size to the average face size of
     * every realizer
     */
    private double[] avgDistToAvgFaceSize;

    /** Saves the size of the smallest angle of every realizer */
    private double[] smallestAngle;

    /**
     * Saves the average distance of an angle`s size to the average size of the
     * angles(60.0) of every realizer
     */
    private double[] avgDistToAvgAngle;

    /**
     * Saves the largest distance of an angle`s size to the average size of the
     * angles(60.0) of every realizer
     */
    private double[] maxDistToAvgAngle;

    /** Saves the average length of the edges of every realizer */
    private double[] avgEdgeLength;

    /** Saves the length of the smallest edge of every realizer */
    private double[] minEdgeLength;

    /**
     * Saves the largest distance of an edge`s length to the average edge length
     * of every realizer.
     */
    private double[] maxDistToAvgEdgeLength;

    /**
     * Saves the average distance of an edge`s length to the average edge length
     * of every realizer.
     */
    private double[] avgDistToAvgEdgeLength;

    /** Saves the number of cyclic faces of every realizer */
    private int[] cyclicFaces;

    /** Saves the number of cw faces of every realizer */
    private int[] cwFaces;

    /** Saves the number of ccw faces of every realizer */
    private int[] ccwFaces;

    /** Saves the number of three-colored faces of every realizer */
    private int[] threeColoredFaces;

    /** ENTER YOUR DATABASE DRIVER HERE */
    public static final String dbDriver = "org.postgresql.Driver";

    /** ENTER YOUR DATABASE HOST HERE */
    public static final String dbHost = "nuts.fmi.uni-passau.de";

    /** ENTER YOUR DATABASE NAME HERE */
    public static final String dbName = "hofmeier";

    /** ENTER YOUR DATABASE USERNAME HERE */
    public static final String dbUser = "";

    /** ENTER YOUR DATABASE PASSWORD HERE */
    public static final String dbPassword = "";

    /** The logger to inform and warn the user */
    private static final Logger logger = Logger.getLogger(DataToDB.class
            .getName());

    /**
     * Creates a new instance of the class
     * 
     * @param a
     *            the algorithm, which calculated the realizers
     */
    public DataToDB(BrehmAllRealizers a) {
        this.graph = a.graph;
        this.outerNodes = a.outerNodes;
        this.realizers = a.realizers;
        this.faces = a.getFaces();
        this.numOfRealizers = this.realizers.size();

        // Initialize all arrays
        this.sumOfFreeCols = new int[this.numOfRealizers];

        this.depthOfGreenTree = new double[this.numOfRealizers];
        this.depthOfBlueTree = new double[this.numOfRealizers];
        this.depthOfRedTree = new double[this.numOfRealizers];
        this.averageDepth = new double[this.numOfRealizers];
        this.maxDistToAverageDepth = new double[this.numOfRealizers];
        this.avgDistToAvgDepth = new double[this.numOfRealizers];

        this.smallestFaceSize = new double[this.numOfRealizers];
        this.avgFaceSize = new double[this.numOfRealizers];
        this.maxDistToAvgFaceSize = new double[this.numOfRealizers];
        this.avgDistToAvgFaceSize = new double[this.numOfRealizers];

        this.smallestAngle = new double[this.numOfRealizers];
        this.avgDistToAvgAngle = new double[this.numOfRealizers];
        this.maxDistToAvgAngle = new double[this.numOfRealizers];

        this.avgEdgeLength = new double[this.numOfRealizers];
        this.minEdgeLength = new double[this.numOfRealizers];
        this.maxDistToAvgEdgeLength = new double[this.numOfRealizers];
        this.avgDistToAvgEdgeLength = new double[this.numOfRealizers];

        this.cyclicFaces = new int[this.numOfRealizers];
        this.cwFaces = new int[this.numOfRealizers];
        this.ccwFaces = new int[this.numOfRealizers];
        this.threeColoredFaces = new int[this.numOfRealizers];

        // For each realizer of the graph...
        for (int i = 0; i < this.numOfRealizers; i++) {
            // ...calculate all test data
            this.realizer = this.realizers.get(i);
            this.barRep = new BarycentricRepresentation(this.realizer,
                    this.graph, this.outerNodes);

            this.sumOfFreeCols[i] = this.countEmptyRowsAndColumns();

            this.depthOfGreenTree[i] = this.getDepthOfTree(this.realizer
                    .getGreen());
            this.depthOfBlueTree[i] = this.getDepthOfTree(this.realizer
                    .getBlue());
            this.depthOfRedTree[i] = this
                    .getDepthOfTree(this.realizer.getRed());
            this.averageDepth[i] = (depthOfGreenTree[i] + depthOfBlueTree[i] + depthOfRedTree[i]) / 3;
            this.maxDistToAverageDepth[i] = Math.max(Math.max(Math
                    .abs(depthOfGreenTree[i] - averageDepth[i]), Math
                    .abs(depthOfBlueTree[i] - averageDepth[i])), Math
                    .abs(depthOfRedTree[i] - averageDepth[i]));
            this.avgDistToAvgDepth[i] = (Math.abs(depthOfGreenTree[i]
                    - averageDepth[i])
                    + Math.abs(depthOfBlueTree[i] - averageDepth[i]) + Math
                    .abs(depthOfRedTree[i] - averageDepth[i])) / 3;

            this.smallestFaceSize[i] = this.getSmallestFaceSize();
            this.avgFaceSize[i] = ((0.5 * (this.graph.getNumberOfNodes() * this.graph
                    .getNumberOfNodes()) / (2 * this.graph.getNumberOfNodes() - 5)));
            this.maxDistToAvgFaceSize[i] = this.getMaxDistToAvgFaceSize();
            this.avgDistToAvgFaceSize[i] = this.getAvgDistToAvgFaceSize();

            this.smallestAngle[i] = this.getSmallestAngle();
            this.maxDistToAvgAngle[i] = this.getMaxDistToAvgAngle();
            this.avgDistToAvgAngle[i] = this.getAvgDistToAvgAngle();

            this.avgEdgeLength[i] = this.getAvgInnerEdgeLength();
            this.minEdgeLength[i] = this.getMinInnerEdgeLength();
            this.maxDistToAvgEdgeLength[i] = this.getMaxDistToAvgEdgeLength(i);
            this.avgDistToAvgEdgeLength[i] = this.getAvgDistToAvgEdgeLength(i);

            this.cyclicFaces[i] = this.getNumberOfCyclicFaces();
            this.cwFaces[i] = this.getNumberOfCWFaces();
            this.ccwFaces[i] = this.getNumberOfCCWFaces();
            this.threeColoredFaces[i] = this.getNumberOfThreeColoredFaces();
        }
    }

    /**
     * Counts all empty rows and all empty columns in the drawing of the current
     * realizer by creating a boolean array position for each column and row and
     * set it true if there is a node`s coordinate in the corresponding row /
     * column.
     * 
     * @return the sum of the empty rows and columns
     */
    private int countEmptyRowsAndColumns() {
        boolean[] rows = new boolean[this.graph.getNumberOfNodes() - 1];
        boolean[] columns = new boolean[this.graph.getNumberOfNodes() - 1];

        int sumOfEmptyColumns = 0;

        Iterator<Node> it = this.graph.getNodesIterator();
        while (it.hasNext()) {
            Node node = it.next();
            int green = this.barRep.getCoordinate(node,
                    AbstractDrawingAlgorithm.GREEN);
            int blue = this.barRep.getCoordinate(node,
                    AbstractDrawingAlgorithm.BLUE);
            columns[green] = true;
            rows[blue] = true;
        }

        for (int i = 0; i < columns.length; i++) {
            if (!columns[i]) {
                sumOfEmptyColumns++;
            }
        }
        for (int i = 0; i < rows.length; i++) {
            if (!rows[i]) {
                sumOfEmptyColumns++;
            }
        }
        return sumOfEmptyColumns;
    }

    /**
     * Calculates the depth of a tree of the current realizer by walking up to
     * the root of the tree from each node and counting the number of steps.
     * 
     * @param tree
     *            the tree whose depth is calculated
     * @return the depth of the tree
     */
    private int getDepthOfTree(HashMap<Node, Node> tree) {
        int depth = 0;
        HashSet<Node> visitedNodes = new HashSet<Node>();
        Iterator<Node> it = tree.keySet().iterator();
        while (it.hasNext()) {
            int currentDepth = 0;
            Node currentNode = it.next();
            if (!visitedNodes.contains(currentNode)) {
                visitedNodes.add(currentNode);
                while (tree.get(currentNode) != null) {
                    currentNode = tree.get(currentNode);
                    currentDepth++;
                }
            }
            depth = Math.max(depth, currentDepth);
        }
        return depth;
    }

    /**
     * Calculates the size of all faces by methds of vector geometry
     * 
     * @return the size of the smallest face
     */
    private double getSmallestFaceSize() {
        double smallestFaceSize = Double.MAX_VALUE;
        Iterator<Face> it = this.faces.iterator();
        while (it.hasNext()) {
            Face face = it.next();
            if (!face.isOuterFace()) {
                Node n1 = face.getNodes()[0];
                Node n2 = face.getNodes()[1];
                Node n3 = face.getNodes()[2];

                double x1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.GREEN);
                double y1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.BLUE);
                double x2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.GREEN);
                double y2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.BLUE);
                double x3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.GREEN);
                double y3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.BLUE);

                double diffX = x1 - x2;
                double diffY = y1 - y2;

                double orthX = 1;
                double orthY;
                if (diffY == 0.0) {
                    orthX = 0.0;
                    orthY = 1.0;
                } else {
                    orthY = -(diffX * orthX) / diffY;
                }
                double lambda = ((x3 - x2) * (-orthY) - (-orthX) * (y3 - y2))
                        / (diffX * (-orthY) - (-orthX) * diffY);
                double cutPointX = x2 + lambda * diffX;
                double cutPointY = y2 + lambda * diffY;

                double baseLength = Math.sqrt(Math.pow((x2 - x1), 2)
                        + Math.pow((y2 - y1), 2));
                double heightLength = Math.sqrt(Math.pow((x3 - cutPointX), 2)
                        + Math.pow((y3 - cutPointY), 2));

                double faceSize = 0.5 * (baseLength * heightLength);
                smallestFaceSize = Math.min(smallestFaceSize, faceSize);
            }
        }
        return smallestFaceSize;
    }

    /**
     * Calculates distance of each face`s size to the average face size
     * 
     * @return the largest distance to the average face size
     */
    private double getMaxDistToAvgFaceSize() {
        double maxDistToAvgFaceSize = 0;
        double avgFaceSize = ((0.5 * this.graph.getNumberOfNodes() * this.graph
                .getNumberOfNodes()) / (2 * this.graph.getNumberOfNodes() - 5));
        Iterator<Face> it = this.faces.iterator();
        while (it.hasNext()) {
            Face face = it.next();
            if (!face.isOuterFace()) {
                Node n1 = face.getNodes()[0];
                Node n2 = face.getNodes()[1];
                Node n3 = face.getNodes()[2];

                double x1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.GREEN);
                double y1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.BLUE);
                double x2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.GREEN);
                double y2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.BLUE);
                double x3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.GREEN);
                double y3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.BLUE);

                double diffX = x1 - x2;
                double diffY = y1 - y2;

                double orthX = 1;
                double orthY;
                if (diffY == 0.0) {
                    orthX = 0.0;
                    orthY = 1.0;
                } else {
                    orthY = -(diffX * orthX) / diffY;
                }
                double lambda = ((x3 - x2) * (-orthY) - (-orthX) * (y3 - y2))
                        / (diffX * (-orthY) - (-orthX) * diffY);
                double cutPointX = x2 + lambda * diffX;
                double cutPointY = y2 + lambda * diffY;

                double baseLength = Math.sqrt(Math.pow((x2 - x1), 2)
                        + Math.pow((y2 - y1), 2));
                double heightLength = Math.sqrt(Math.pow((x3 - cutPointX), 2)
                        + Math.pow((y3 - cutPointY), 2));

                double faceSize = 0.5 * (baseLength * heightLength);
                maxDistToAvgFaceSize = Math.max(maxDistToAvgFaceSize, Math
                        .abs(avgFaceSize - faceSize));
            }
        }
        return maxDistToAvgFaceSize;
    }

    /**
     * Sums up the distance of each face`s size to the average face size
     * 
     * @return the average distance to the average face size
     */
    private double getAvgDistToAvgFaceSize() {
        double avgDistToAvgFaceSize = 0;
        double avgFaceSize = ((0.5 * this.graph.getNumberOfNodes() * this.graph
                .getNumberOfNodes()) / (2 * this.graph.getNumberOfNodes() - 5));
        Iterator<Face> it = this.faces.iterator();
        while (it.hasNext()) {
            Face face = it.next();
            if (!face.isOuterFace()) {
                Node n1 = face.getNodes()[0];
                Node n2 = face.getNodes()[1];
                Node n3 = face.getNodes()[2];

                double x1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.GREEN);
                double y1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.BLUE);
                double x2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.GREEN);
                double y2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.BLUE);
                double x3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.GREEN);
                double y3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.BLUE);

                double diffX = x1 - x2;
                double diffY = y1 - y2;

                double orthX = 1;
                double orthY;
                if (diffY == 0.0) {
                    orthX = 0.0;
                    orthY = 1.0;
                } else {
                    orthY = -(diffX * orthX) / diffY;
                }
                double lambda = ((x3 - x2) * (-orthY) - (-orthX) * (y3 - y2))
                        / (diffX * (-orthY) - (-orthX) * diffY);
                double cutPointX = x2 + lambda * diffX;
                double cutPointY = y2 + lambda * diffY;

                double baseLength = Math.sqrt(Math.pow((x2 - x1), 2)
                        + Math.pow((y2 - y1), 2));
                double heightLength = Math.sqrt(Math.pow((x3 - cutPointX), 2)
                        + Math.pow((y3 - cutPointY), 2));

                double faceSize = 0.5 * (baseLength * heightLength);
                avgDistToAvgFaceSize += Math.abs(avgFaceSize - faceSize);
            }
        }
        return (avgDistToAvgFaceSize / (2 * this.graph.getNumberOfNodes() - 5));
    }

    /**
     * Calculates the size of each angle in the drawing of the current realizer
     * by methods from vector geometry
     * 
     * @return the size of the smallest angle
     */
    private double getSmallestAngle() {
        double smallestAngle = Double.MAX_VALUE;
        Iterator<Face> it = this.faces.iterator();
        while (it.hasNext()) {
            Face face = it.next();
            if (!face.isOuterFace()) {
                Node n1 = face.getNodes()[0];
                Node n2 = face.getNodes()[1];
                Node n3 = face.getNodes()[2];

                double x1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.GREEN);
                double y1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.BLUE);
                double x2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.GREEN);
                double y2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.BLUE);
                double x3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.GREEN);
                double y3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.BLUE);

                double length12 = Math.sqrt(Math.pow((x1 - x2), 2)
                        + Math.pow((y1 - y2), 2));
                double length23 = Math.sqrt(Math.pow((x2 - x3), 2)
                        + Math.pow((y2 - y3), 2));
                double length31 = Math.sqrt(Math.pow((x3 - x1), 2)
                        + Math.pow((y3 - y1), 2));

                double angle1 = Math.toDegrees(Math.acos((Math.pow(length23, 2)
                        - Math.pow(length31, 2) - Math.pow(length12, 2))
                        / ((-2) * length12 * length31)));
                double angle2 = Math.toDegrees(Math.acos((Math.pow(length31, 2)
                        - Math.pow(length12, 2) - Math.pow(length23, 2))
                        / ((-2) * length12 * length23)));
                double angle3 = Math.toDegrees(Math.acos((Math.pow(length12, 2)
                        - Math.pow(length23, 2) - Math.pow(length31, 2))
                        / ((-2) * length31 * length23)));

                smallestAngle = Math.min(smallestAngle, Math.min(angle1, Math
                        .min(angle2, angle3)));
            }
        }
        return smallestAngle;
    }

    /**
     * Calculates the distance of the size of each angle in the drawing of the
     * current realizer to the average angle size (60.0)
     * 
     * @return the largest distance to the average angle size
     */
    private double getMaxDistToAvgAngle() {
        double maxDistToAvgAngle = 0;
        Iterator<Face> it = this.faces.iterator();
        while (it.hasNext()) {
            Face face = it.next();
            if (!face.isOuterFace()) {
                Node n1 = face.getNodes()[0];
                Node n2 = face.getNodes()[1];
                Node n3 = face.getNodes()[2];

                double x1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.GREEN);
                double y1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.BLUE);
                double x2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.GREEN);
                double y2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.BLUE);
                double x3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.GREEN);
                double y3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.BLUE);

                double length12 = Math.sqrt(Math.pow((x1 - x2), 2)
                        + Math.pow((y1 - y2), 2));
                double length23 = Math.sqrt(Math.pow((x2 - x3), 2)
                        + Math.pow((y2 - y3), 2));
                double length31 = Math.sqrt(Math.pow((x3 - x1), 2)
                        + Math.pow((y3 - y1), 2));

                double angle1 = Math.toDegrees(Math.acos((Math.pow(length23, 2)
                        - Math.pow(length31, 2) - Math.pow(length12, 2))
                        / ((-2) * length12 * length31)));
                double angle2 = Math.toDegrees(Math.acos((Math.pow(length31, 2)
                        - Math.pow(length12, 2) - Math.pow(length23, 2))
                        / ((-2) * length12 * length23)));
                double angle3 = Math.toDegrees(Math.acos((Math.pow(length12, 2)
                        - Math.pow(length23, 2) - Math.pow(length31, 2))
                        / ((-2) * length31 * length23)));

                maxDistToAvgAngle = Math.max(maxDistToAvgAngle, Math.max(Math
                        .abs(60.0 - angle1), Math.max(Math.abs(60.0 - angle2),
                        Math.abs(60.0 - angle3))));
            }
        }
        return maxDistToAvgAngle;
    }

    /**
     * Sums up the distances of the size of each angle in the drawing of the
     * current realizer to the average angle size (60.0)
     * 
     * @return the average distance to the average angle size
     */
    private double getAvgDistToAvgAngle() {
        double avgDistToAvgAngle = 0;
        Iterator<Face> it = this.faces.iterator();
        while (it.hasNext()) {
            Face face = it.next();
            if (!face.isOuterFace()) {
                Node n1 = face.getNodes()[0];
                Node n2 = face.getNodes()[1];
                Node n3 = face.getNodes()[2];

                double x1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.GREEN);
                double y1 = this.barRep.getCoordinate(n1,
                        AbstractDrawingAlgorithm.BLUE);
                double x2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.GREEN);
                double y2 = this.barRep.getCoordinate(n2,
                        AbstractDrawingAlgorithm.BLUE);
                double x3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.GREEN);
                double y3 = this.barRep.getCoordinate(n3,
                        AbstractDrawingAlgorithm.BLUE);

                double length12 = Math.sqrt(Math.pow((x1 - x2), 2)
                        + Math.pow((y1 - y2), 2));
                double length23 = Math.sqrt(Math.pow((x2 - x3), 2)
                        + Math.pow((y2 - y3), 2));
                double length31 = Math.sqrt(Math.pow((x3 - x1), 2)
                        + Math.pow((y3 - y1), 2));

                double angle1 = Math.toDegrees(Math.acos((Math.pow(length23, 2)
                        - Math.pow(length31, 2) - Math.pow(length12, 2))
                        / ((-2) * length12 * length31)));
                double angle2 = Math.toDegrees(Math.acos((Math.pow(length31, 2)
                        - Math.pow(length12, 2) - Math.pow(length23, 2))
                        / ((-2) * length12 * length23)));
                double angle3 = Math.toDegrees(Math.acos((Math.pow(length12, 2)
                        - Math.pow(length23, 2) - Math.pow(length31, 2))
                        / ((-2) * length31 * length23)));

                avgDistToAvgAngle += (Math.abs(60.0 - angle1)
                        + Math.abs(60.0 - angle2) + Math.abs(60.0 - angle3));
            }
        }
        return (avgDistToAvgAngle / (6 * this.graph.getNumberOfNodes() - 15));
    }

    /**
     * Gets the average edge length of the inner edges of each realizer
     * 
     * @return the average edge length
     */
    private double getAvgInnerEdgeLength() {
        double sumOfEdgeLength = 0;
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            Node source = e.getSource();
            Node target = e.getTarget();

            if (!this.isOuterNode(source) && !this.isOuterNode(target)) {
                double x1 = this.barRep.getCoordinate(source,
                        AbstractDrawingAlgorithm.GREEN);
                double y1 = this.barRep.getCoordinate(source,
                        AbstractDrawingAlgorithm.BLUE);
                double x2 = this.barRep.getCoordinate(target,
                        AbstractDrawingAlgorithm.GREEN);
                double y2 = this.barRep.getCoordinate(target,
                        AbstractDrawingAlgorithm.BLUE);

                double edgeLength = Math.sqrt(Math.pow(x1 - x2, 2)
                        + Math.pow(y1 - y2, 2));
                sumOfEdgeLength += edgeLength;
            }
        }
        return (sumOfEdgeLength / this.graph.getNumberOfEdges());

    }

    /**
     * Gets the smallest edge length of the inner edges of each realizer
     * 
     * @return the smallest edge length
     */
    private double getMinInnerEdgeLength() {
        double minInnerEdgeLength = Double.MAX_VALUE;
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            Node source = e.getSource();
            Node target = e.getTarget();

            if (!this.isOuterNode(source) && !this.isOuterNode(target)) {
                double x1 = this.barRep.getCoordinate(source,
                        AbstractDrawingAlgorithm.GREEN);
                double y1 = this.barRep.getCoordinate(source,
                        AbstractDrawingAlgorithm.BLUE);
                double x2 = this.barRep.getCoordinate(target,
                        AbstractDrawingAlgorithm.GREEN);
                double y2 = this.barRep.getCoordinate(target,
                        AbstractDrawingAlgorithm.BLUE);

                double edgeLength = Math.sqrt(Math.pow(x1 - x2, 2)
                        + Math.pow(y1 - y2, 2));
                minInnerEdgeLength = Math.min(minInnerEdgeLength, edgeLength);
            }
        }
        return minInnerEdgeLength;
    }

    /**
     * Calculates the maximum distance to the average edge length. As the
     * average edge length is needed, the position of the realizer in the array
     * must be given.
     * 
     * @param i
     *            the position of the realizer in the array.
     * @return the maximum distance to the average edge length.
     */
    private double getMaxDistToAvgEdgeLength(int i) {
        double maxDistToAvgEdgeLength = 0;
        double avgEdgeLength = this.avgEdgeLength[i];
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            Node source = e.getSource();
            Node target = e.getTarget();

            if (!this.isOuterNode(source) && !this.isOuterNode(target)) {
                double x1 = this.barRep.getCoordinate(source,
                        AbstractDrawingAlgorithm.GREEN);
                double y1 = this.barRep.getCoordinate(source,
                        AbstractDrawingAlgorithm.BLUE);
                double x2 = this.barRep.getCoordinate(target,
                        AbstractDrawingAlgorithm.GREEN);
                double y2 = this.barRep.getCoordinate(target,
                        AbstractDrawingAlgorithm.BLUE);

                double edgeLength = Math.sqrt(Math.pow(x1 - x2, 2)
                        + Math.pow(y1 - y2, 2));
                maxDistToAvgEdgeLength = Math.max(maxDistToAvgEdgeLength, Math
                        .abs(avgEdgeLength - edgeLength));
            }
        }
        return maxDistToAvgEdgeLength;
    }

    /**
     * Calculates the average distance to the average edge length. As the
     * average edge length is needed, the position of the realizer in the array
     * must be given.
     * 
     * @param i
     *            the position of the realizer in the array.
     * @return the average distance to the average edge length.
     */
    private double getAvgDistToAvgEdgeLength(int i) {
        double avgDistToAvgEdgeLength = 0;
        double avgEdgeLength = this.avgEdgeLength[i];
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            Node source = e.getSource();
            Node target = e.getTarget();
            if (!this.isOuterNode(source) && !this.isOuterNode(target)) {
                double x1 = this.barRep.getCoordinate(source,
                        AbstractDrawingAlgorithm.GREEN);
                double y1 = this.barRep.getCoordinate(source,
                        AbstractDrawingAlgorithm.BLUE);
                double x2 = this.barRep.getCoordinate(target,
                        AbstractDrawingAlgorithm.GREEN);
                double y2 = this.barRep.getCoordinate(target,
                        AbstractDrawingAlgorithm.BLUE);

                double edgeLength = Math.sqrt(Math.pow(x1 - x2, 2)
                        + Math.pow(y1 - y2, 2));
                avgDistToAvgEdgeLength += Math.abs(avgEdgeLength - edgeLength);
            }
        }
        return (avgDistToAvgEdgeLength / this.graph.getNumberOfEdges());
    }

    /**
     * Gets the number of cyclic faces in the realizer.
     * 
     * @return the number of cyclic faces in the realizer.
     */
    private int getNumberOfCyclicFaces() {
        int numOfCyclicFaces = 0;
        Iterator<Face> it = this.faces.iterator();
        while (it.hasNext()) {
            Face f = it.next();
            if (!f.isOuterFace()) {
                if (this.realizer.isClockwise(f)
                        || (this.realizer.isCounterClockwise(f))) {
                    numOfCyclicFaces++;
                }
            }
        }
        return numOfCyclicFaces;
    }

    /**
     * Gets the number of cw faces in the realizer.
     * 
     * @return the number of cw faces in the realizer.
     */
    private int getNumberOfCWFaces() {
        int numOfCWFaces = 0;
        Iterator<Face> it = this.faces.iterator();
        while (it.hasNext()) {
            Face f = it.next();
            if (!f.isOuterFace()) {
                if (this.realizer.isClockwise(f)) {
                    numOfCWFaces++;
                }
            }
        }
        return numOfCWFaces;
    }

    /**
     * Gets the number of ccw faces in the realizer.
     * 
     * @return the number of ccw faces in the realizer.
     */
    private int getNumberOfCCWFaces() {
        int numOfCCWFaces = 0;
        Iterator<Face> it = this.faces.iterator();
        while (it.hasNext()) {
            Face f = it.next();
            if (!f.isOuterFace()) {
                if (this.realizer.isCounterClockwise(f)) {
                    numOfCCWFaces++;
                }
            }
        }
        return numOfCCWFaces;
    }

    /**
     * Gets the number of three-colored faces in the realizer.
     * 
     * @return the number of three-colored faces in the realizer.
     */
    private int getNumberOfThreeColoredFaces() {
        int numOfThreeColoredFaces = 0;
        Iterator<Face> it = this.faces.iterator();
        while (it.hasNext()) {
            Face f = it.next();
            if (!f.isOuterFace()) {
                if (this.realizer.isThreeColored(f)) {
                    numOfThreeColoredFaces++;
                }
            }
        }
        return numOfThreeColoredFaces;
    }

    /**
     * Writes the output to the database specified by the class attributes
     * above. If necessary calls a method to create the tables.
     */
    public void writeToDB() {
        Connection conn = null;

        // Load the database driver.
        try {
            logger.info("Loading JDBC Driver");
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found");
            return;
        }

        // Open a database connection.
        try {
            logger.info("Opening Database Connection");
            conn = DriverManager.getConnection("jdbc:postgresql://" + dbHost
                    + "/" + dbName, dbUser, dbPassword);

            this.createTable(conn);
            int primaryKey = this.getNextPrimaryKey(conn);
            this.write(conn, primaryKey);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close the database connection in every case.
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.fine("Exception while closing Database Connection");
            }
        }
    }

    /**
     * Makes a "test"-SQL statement to check if the tables already exist. If not
     * creates them.
     * 
     * @param conn
     *            the database connection.
     * @throws SQLException
     *             for errors during the creation of the tables.
     */
    private void createTable(Connection conn) throws SQLException {
        boolean tablesExist = true;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeQuery("SELECT * FROM graphs;");
            stmt.close();
            stmt = conn.createStatement();
            stmt.executeQuery("SELECT * FROM realizers;");
            stmt.close();
            logger
                    .info("Tables already exist and therefore will not be created.");
        } catch (SQLException e) {
            Statement stmt = conn.createStatement();
            try {
                stmt.executeQuery("DROP TABLE graphs;");
                stmt.close();
            } catch (SQLException s) {
            }
            try {
                stmt = conn.createStatement();
                stmt.executeQuery("DROP TABLE graphs;");
                stmt.close();
            } catch (SQLException s) {
            }
            logger.fine("No proper tables exist in database. Tables will be "
                    + "created");
            tablesExist = false;
        }
        if (!tablesExist) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE graphs ( "
                    + "  graphid INTEGER Primary Key, "
                    + "  numOfNodes INTEGER not null, "
                    + "  numOfRealizers INTEGER not null, "
                    + "  avgFaceSize REAL not null" + ")");
            stmt.close();
            stmt = conn.createStatement();
            stmt
                    .executeUpdate("CREATE TABLE realizers ( "
                            + "  graphid INTEGER not null, "
                            + "  idOfRealizer INTEGER not null, "
                            + "  sumOfFreeCols INTEGER not null, "
                            + "  depthOfGreenTree INTEGER not null, "
                            + "  depthOfBlueTree INTEGER not null, "
                            + "  depthOfRedTree INTEGER not null, "
                            + "  averageDepth REAL not null, "
                            + "  maxDistToAverageDepth REAL not null, "
                            + "  avgDistToAvgDepth REAL not null, "
                            + "  smallestFaceSize REAL not null, "
                            + "  maxDistToAvgFaceSize REAL not null, "
                            + "  avgDistToAvgFaceSize REAL not null, "
                            + "  smallestAngle REAL not null, "
                            + "  avgDistToAvgAngle REAL not null, "
                            + "  maxDistToAvgAngle REAL not null, "
                            + "  avgEdgeLength REAL not null, "
                            + "  minEdgeLength REAL not null, "
                            + "  maxDistToAvgEdgeLength REAL not null, "
                            + "  avgDistToAvgEdgeLength REAL not null,"
                            + "  cyclicFaces INTEGER not null,"
                            + "  cwFaces INTEGER not null,"
                            + "  ccwFaces INTEGER not null,"
                            + "  threeColoredFaces INTEGER not null,"
                            + "  FOREIGN KEY(graphid) REFERENCES graphs(graphid)"
                            + ")");
            stmt.close();
        }
    }

    /**
     * Gets the database entry with the highest primary key, adds one and
     * returns the value for the next primary key.
     * 
     * @param conn
     *            the database connection.
     * @return the value for the next primary key.
     * @throws SQLException
     *             for errors while getting the highest primary key.
     */
    private int getNextPrimaryKey(Connection conn) throws SQLException {
        int primaryKey = -1;
        Statement stmt = conn.createStatement();
        ResultSet rst = stmt.executeQuery("SELECT Max(graphid) FROM graphs");

        while (rst.next()) {
            primaryKey = rst.getInt(1);
        }
        stmt.close();
        return ++primaryKey;
    }

    /**
     * Writes a graphs data to the database.
     * 
     * @param conn
     *            the database connection.
     * @param key
     *            the primary key.
     * @throws SQLException
     *             for errors while writing to the database.
     */
    private void write(Connection conn, int key) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO graphs (graphid, numOfNodes, "
                + "numOfRealizers, avgFaceSize) VALUES (" + key + ","
                + this.graph.getNumberOfNodes() + ", " + this.numOfRealizers
                + "," + this.avgFaceSize[0] + ");");
        stmt.close();

        for (int i = 0; i < this.numOfRealizers; i++) {
            stmt = conn.createStatement();
            stmt
                    .executeUpdate("INSERT INTO realizers (graphid, idOfRealizer, "
                            + "sumOfFreeCols, depthOfGreenTree, depthOfBlueTree, "
                            + " depthOfRedTree, averageDepth, maxDistToAverageDepth, "
                            + "avgDistToAvgDepth, smallestFaceSize, "
                            + "maxDistToAvgFaceSize, avgDistToAvgFaceSize, "
                            + "smallestAngle, avgDistToavgAngle, maxDistToAvgAngle, "
                            + "avgEdgeLength, minEdgeLength, maxDistToAvgEdgeLength, "
                            + "avgDistToAvgEdgeLength, cyclicFaces, cwFaces, ccwFaces, "
                            + "threeColoredFaces) VALUES ("
                            + key
                            + ","
                            + i
                            + ","
                            + this.sumOfFreeCols[i]
                            + ","
                            + this.depthOfGreenTree[i]
                            + ","
                            + this.depthOfBlueTree[i]
                            + ","
                            + this.depthOfRedTree[i]
                            + ","
                            + this.averageDepth[i]
                            + ","
                            + this.maxDistToAverageDepth[i]
                            + ","
                            + this.avgDistToAvgDepth[i]
                            + ","
                            + this.smallestFaceSize[i]
                            + ","
                            + this.maxDistToAvgFaceSize[i]
                            + ","
                            + this.avgDistToAvgFaceSize[i]
                            + ","
                            + this.smallestAngle[i]
                            + ","
                            + this.avgDistToAvgAngle[i]
                            + ","
                            + this.maxDistToAvgAngle[i]
                            + ","
                            + this.avgEdgeLength[i]
                            + ","
                            + this.minEdgeLength[i]
                            + ","
                            + this.maxDistToAvgEdgeLength[i]
                            + ","
                            + this.avgDistToAvgEdgeLength[i]
                            + ","
                            + this.cyclicFaces[i]
                            + ","
                            + this.cwFaces[i]
                            + ","
                            + this.ccwFaces[i]
                            + ","
                            + this.threeColoredFaces[i] + ");");

            stmt.close();

        }
    }

    /**
     * Checks a given node if it is one of the outer nodes.
     * 
     * @param n
     *            the node to check.
     * @return true if the node is an outer node.
     */
    protected boolean isOuterNode(Node n) {
        return ((n == this.outerNodes[0]) || (n == this.outerNodes[1]) || (n == this.outerNodes[2]));
    }
}
