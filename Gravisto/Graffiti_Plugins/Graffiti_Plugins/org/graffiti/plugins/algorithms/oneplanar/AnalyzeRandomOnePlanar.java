// =============================================================================
//
//   RandomOnePlanarFromTriangulated.java
//
//   Copyright (c) 2001-2013, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.oneplanar;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.ProbabilityParameter;
import org.graffiti.plugins.algorithms.connectivity.Triconnect;
import org.graffiti.plugins.algorithms.fpp.CalculateOrder;
import org.graffiti.plugins.algorithms.fpp.Face;
import org.graffiti.plugins.algorithms.fpp.LMCOrdering;
import org.graffiti.plugins.algorithms.fpp.OrderNode;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

//import org.graffiti.graphics.EdgeLabelAttribute;

/**
 * Creates a random graph, executes an adapted Chrobak Kant algorithm and
 * analyzes angles for crossing edges
 * 
 * 
 * @author Thomas Kruegl
 * @version $Revision$ $Date$
 */
public class AnalyzeRandomOnePlanar extends AbstractAlgorithm {

    /** The algorithm's parameters */
    protected LinkedList<Parameter<?>> parameterList;

    /** The number of nodes. */
    private IntegerParameter nodesParam;
    private int numberOfNodes;

    /** The density of crossing edges. */
    private ProbabilityParameter densityParam;
    private double density;

    /** Generator for random numbers */
    private Random randomGenerator = new Random();

    /** logger */
    private static final Logger logger = Logger
            .getLogger(AnalyzeRandomOnePlanar.class.getName());

    /** canonical ordered list of nodes */
    private ArrayList<ArrayList<Node>> canonicalOrdering;

    /** List of crossing edge pairs **/
    private ArrayList<CrossingEdgePair> crossingEdgePairs;

    /** maximum value for y coordinates */
    private int ymax;

    /** pixels per coordinate or something like that */
    private double stretchFactor = 30;

    private Node[] outerNodes;

    /**
     * Constructs a new instance.
     */
    public AnalyzeRandomOnePlanar() {
        parameterList = new LinkedList<Parameter<?>>();
        nodesParam = new IntegerParameter(new Integer(5), new Integer(5),
                new Integer(1000), "number of nodes",
                "the number of nodes to generate");
        parameterList.addFirst(nodesParam);
        densityParam = new ProbabilityParameter(0.5, "density",
                "probability for crossing edges");
        parameterList.add(densityParam);
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
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(
     *      Parameter<?>[])
     */
    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
        int i = 0;
        for (Parameter<?> p : parameterList) {
            @SuppressWarnings("unchecked")
            Parameter<Object> pp = (Parameter<Object>) p;
            pp.setValue(params[i].getValue());
            i++;
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Random 1-planar Graph Generator";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        // get parameters
        numberOfNodes = nodesParam.getValue().intValue();
        density = densityParam.getProbability();

        // inform listener
        graph.getListenerManager().transactionStarted(this);

        // initialise crossingEdgePairs
        crossingEdgePairs = new ArrayList<CrossingEdgePair>();

        /***************************************************/
        // create a random graph
        createRandomOnePlanarGraph();

        /***************************************************/
        // precaution test: graph still 3-connected? Proof exists.
        Triconnect triconnect = new Triconnect();
        triconnect.attach(graph);
        triconnect.testTriconnect();
        if (!triconnect.isTriconnected()) {
            throw new RuntimeException(
                    "Removing crossing edges somehow destroyed 3-connectivity");
        }

        /***************************************************/
        // using canonical ordering from fpp
        // calculate faces
        PlanarityAlgorithm planar = new PlanarityAlgorithm();
        planar.attach(graph);
        TestedGraph tGraph = planar.getTestedGraph();
        CalculateFaceWrapper calculatefaces = new CalculateFaceWrapper(graph,
                tGraph, outerNodes);
        Face[] faces = calculatefaces.getFaces();
        int outerfaceIndex = calculatefaces.getOutIndex();
        // lmc
        CalculateOrder calculateorder = new CalculateOrder(faces,
                outerfaceIndex, graph, calculatefaces, tGraph);
        OrderNode[] reverseInduction = calculateorder.getReverseInduction();
        LMCOrdering lmc = new LMCOrdering(reverseInduction, graph.getNodes()
                .size(), calculateorder);
        OrderNode[] lmcOrdering = lmc.getLMCOrdering();

        /***************************************************/
        // convert result from OrderNodes (fpp) to ArrayList<Node>, and label
        // nodes
        canonicalOrdering = new ArrayList<ArrayList<Node>>();
        int number = 1;
        OrderNode toAdd;
        Node nodeToAdd;
        LinkedList<Node> handleToAdd;
        ArrayList<Node> rank = new ArrayList<Node>();
        // first node
        nodeToAdd = lmcOrdering[0].getOrderNode();
        labelNode(nodeToAdd, number);
        number++;
        rank.add(nodeToAdd);
        // next node or nodes
        toAdd = lmcOrdering[2];
        if (toAdd.getHandle()) {
            handleToAdd = toAdd.getOrderList();
            for (Node node : handleToAdd) {
                labelNode(node, number);
                number++;
            }
            rank.addAll(handleToAdd);
        } else {
            nodeToAdd = toAdd.getOrderNode();
            labelNode(nodeToAdd, number);
            number++;
            rank.add(nodeToAdd);
        }
        // second node of fpp is last node of first rank
        nodeToAdd = lmcOrdering[1].getOrderNode();
        labelNode(nodeToAdd, number);
        number++;
        rank.add(nodeToAdd);
        canonicalOrdering.add(rank);

        // next ranks
        for (int i = 3; i < lmcOrdering.length; i++) {
            rank = new ArrayList<Node>();
            toAdd = lmcOrdering[i];
            if (toAdd.getHandle()) {
                handleToAdd = toAdd.getOrderList();
                for (Node node : handleToAdd) {
                    labelNode(node, number);
                    number++;
                }
                rank.addAll(handleToAdd);
            } else {
                nodeToAdd = toAdd.getOrderNode();
                labelNode(nodeToAdd, number);
                number++;
                rank.add(nodeToAdd);
            }
            canonicalOrdering.add(rank);
        }

        /***************************************************/
        // Chrobak Kant algorithm
        adaptedChrobakKant();

        /***************************************************/
        // insert removed crossings
        for (CrossingEdgePair cp : crossingEdgePairs) {
            insertEdges(cp);
            logger.log(Level.INFO, "Winkel: " + cp.getAngle());
        }

        // TODO evaluate angles

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Randomly creates a graph. First, a random triangulated 3-connected graph
     * is generated. Then some edges depending on density are chosen to be
     * crossed by an additional edge, resulting in a 1-planar graph where each
     * edge may be crossed at most once. Both the new edge and the crossed edge
     * are then stored as crossing edge pair in a field variable, and removed
     * from the graph.
     * 
     * The result actually is a planar graph, not triangulated anymore because
     * of the removed edges, and information about the removed crossing edges
     * stored in crossingEdgePairs.
     */
    private void createRandomOnePlanarGraph() {

        // edge management
        Edge edgeToAdd;
        Edge previousEdge;
        Edge contourEdge;
        ArrayList<Edge> unblockedEdges = new ArrayList<Edge>();

        // node management
        Node nodeToAdd;

        // face management
        LinkedList<Node> faceNodelist;
        LinkedList<Edge> faceEdgelist;
        FaceWithComponents newFace;

        // contour management
        ArrayList<Node> contour = new ArrayList<Node>();
        int leftContourIndex;
        int rightContourIndex;
        int numberOfCoveredNodes;

        /***************************************************/
        // starting nodes

        Node node1 = graph.addNode();
        Node node2 = graph.addNode();
        Node node3 = graph.addNode();

        // starting contour from left to right, 1-3-2

        contour.add(node1);
        contour.add(node3);
        contour.add(node2);

        // starting edges

        edgeToAdd = graph.addEdge(node1, node2, false);
        contourEdge = edgeToAdd;

        edgeToAdd = graph.addEdge(node1, node3, false);
        unblockedEdges.add(edgeToAdd);
        previousEdge = edgeToAdd;

        edgeToAdd = graph.addEdge(node2, node3, false);
        unblockedEdges.add(edgeToAdd);

        // starting face
        faceNodelist = new LinkedList<Node>(Arrays.asList(node1, node3, node2));
        faceEdgelist = new LinkedList<Edge>(Arrays.asList(previousEdge,
                edgeToAdd, contourEdge));
        newFace = new FaceWithComponents(faceNodelist, faceEdgelist);
        edgeToAdd.addAttribute(new FaceAttribute("face1", newFace), "");
        previousEdge.addAttribute(new FaceAttribute("face1", newFace), "");
        contourEdge.addAttribute(new FaceAttribute("face1", newFace), "");

        /***************************************************/
        // add more nodes

        for (int i = 4; i <= numberOfNodes; i++) {
            // find lower neighbours for new node
            if (i == numberOfNodes) {
                // last node covers all
                leftContourIndex = 0;
                rightContourIndex = contour.size() - 1;

            } else {
                // else choose at random
                leftContourIndex = randomGenerator.nextInt(contour.size() - 1);
                rightContourIndex = randomGenerator.nextInt(contour.size()
                        - leftContourIndex - 1)
                        + 1 + leftContourIndex;
            }

            numberOfCoveredNodes = rightContourIndex - leftContourIndex - 1;

            // create and initialise new node
            nodeToAdd = graph.addNode();
            if (i == numberOfNodes) {
                // save outer nodes when finished
                outerNodes = new Node[] { node1, node2, nodeToAdd };
            }

            // add edges from new node to contour nodes, manage faces
            for (int j = leftContourIndex; j <= rightContourIndex; j++) {
                edgeToAdd = graph.addEdge(nodeToAdd, contour.get(j), false);
                if ((i == numberOfNodes) && (j == 0 || j == contour.size() - 1)) {
                    // then do not add outer face edges to unblocked Edges
                } else {
                    // else do
                    unblockedEdges.add(edgeToAdd);
                }
                // from second edge on, a new face forms
                if (j > leftContourIndex) {
                    // find contour edge part of this face
                    contourEdge = graph
                            .getEdges(contour.get(j), contour.get(j - 1))
                            .iterator().next();

                    // create face
                    faceNodelist = new LinkedList<Node>(Arrays.asList(
                            contour.get(j - 1), nodeToAdd, contour.get(j)));
                    faceEdgelist = new LinkedList<Edge>(Arrays.asList(
                            previousEdge, edgeToAdd, contourEdge));
                    newFace = new FaceWithComponents(faceNodelist, faceEdgelist);

                    // add face to all 3 adjacent edges. Each edge will have 2
                    // faces in the end
                    addFaceAttributeToEdge(edgeToAdd, newFace);
                    addFaceAttributeToEdge(previousEdge, newFace);
                    addFaceAttributeToEdge(contourEdge, newFace);
                }
                // remember added edge for next face
                previousEdge = edgeToAdd;
            } // end inner loop edges

            // update contour
            contour.add(leftContourIndex + 1, nodeToAdd);
            for (int j = 0; j < numberOfCoveredNodes; j++) {
                contour.remove(leftContourIndex + 2);
            }
        } // end for nodes

        // now adding more edges, destroying planarity
        Edge randomEdge;
        boolean addCrossing;
        Node source;
        Node target;
        Node newSource;
        Node newTarget;
        Iterator<Node> nodeIterator;
        FaceWithComponents face1;
        FaceWithComponents face2;

        // as long as there are edges not marked blocked for crossings:
        while (unblockedEdges.size() > 0) {
            // choose one of them at random
            randomEdge = unblockedEdges.get(randomGenerator
                    .nextInt(unblockedEdges.size()));
            target = randomEdge.getTarget();
            source = randomEdge.getSource();

            // find surrounding faces
            face1 = (FaceWithComponents) randomEdge.getAttribute("face1")
                    .getValue();
            face2 = (FaceWithComponents) randomEdge.getAttribute("face2")
                    .getValue();

            // depending on density factor, decide if crossing edge is added
            addCrossing = randomGenerator.nextDouble() < density;
            if (addCrossing) {
                // search for new source node
                nodeIterator = face1.getNodelist().listIterator();
                do {
                    if (nodeIterator.hasNext()) {
                        newSource = nodeIterator.next();
                    } else {
                        throw new RuntimeException(
                                "No node other than source and target found in face1");
                    }

                } while ((newSource == source) || (newSource == target));

                // search for new target node
                nodeIterator = face2.getNodelist().listIterator();
                do {
                    if (nodeIterator.hasNext()) {
                        newTarget = nodeIterator.next();
                    } else {
                        throw new RuntimeException(
                                "No node other than source and target found in face1");
                    }

                } while ((newTarget == source) || (newTarget == target));

                // check if desired edge already exists, may happen with outer
                // edges
                if (graph.getEdges(newSource, newTarget).size() == 0) {
                    // delete edges from future crossings
                    unblockedEdges.removeAll(face1.getEdgelist());
                    unblockedEdges.removeAll(face2.getEdgelist());
                    // now insert new edge, create edge pair and delete both
                    // edges
                    Edge newEdge = graph.addEdge(newSource, newTarget, false);
                    CrossingEdgePair cp = new CrossingEdgePair(randomEdge,
                            newEdge);
                    crossingEdgePairs.add(cp);
                    graph.deleteEdge(randomEdge);
                    graph.deleteEdge(newEdge);
                    // precaution test: graph still 3-connected? Proof exists..
                    Triconnect triconnect = new Triconnect();
                    triconnect.attach(graph);
                    triconnect.testTriconnect();
                    if (!triconnect.isTriconnected()) {
                        // undo changes
                        graph.addEdge(source, target, false);
                        crossingEdgePairs.remove(cp);
                        logger.log(Level.WARNING,
                                "3-connectivity violated when removing crossing edge pair");
                        // re-inserted edge does not have face paramater, but it
                        // is not required any more
                    }
                    // faces for new edge not added - can be changed here if
                    // required
                } else {
                    unblockedEdges.remove(randomEdge);
                }
                // end if crossing
            } else {
                // due to density, no edge added, but edges have to be removed
                // from unblockedEdges
                unblockedEdges.removeAll(face1.getEdgelist());
                unblockedEdges.removeAll(face2.getEdgelist());
            }
        } // end while unblockedEdges
    }

    /**
     * Executes a slightly adapted version of the chrobak kant algorithm.
     * Requires the field variable canonicalOrdering to be initialised in the
     * following way: first index ArrayList consists of all nodes belonging to
     * the first face, from left to right. Following ArrayLists each contain all
     * nodes of the next rank of the canonical ordering, again from left to
     * right.
     * 
     * Adaption according to Alam, Brandenburg and Kobourov, adding an extra
     * shift when inserting nodes where a crossing may have been.
     */
    private void adaptedChrobakKant() {
        // contour management
        ArrayList<ChrobakKantNode> contour = new ArrayList<ChrobakKantNode>();
        ArrayList<ChrobakKantNode> insertedChrobakKantNodes = new ArrayList<ChrobakKantNode>();
        int rankStartIndex;

        // contour indices
        int p;
        int q;
        int alpha;
        int beta;

        // node management
        ArrayList<Node> nodesToInsert;
        ChrobakKantNode newNode;
        HashSet<ChrobakKantNode> nodesToShift;
        ArrayList<Node> insertedNodes = new ArrayList<Node>();

        // manage coordinates
        int epsilon;
        int xp;
        // int yp; not used
        int xq;
        int yq;

        // start with first subset of nodes, has to have at least 3 nodes
        nodesToInsert = canonicalOrdering.get(0);

        for (int i = 0; i < nodesToInsert.size(); i++) {
            // first and last node at y = 0, others at y = 1
            // x = i for all
            int y = ((i == 0) || (i == nodesToInsert.size() - 1)) ? 0 : 1;
            newNode = new ChrobakKantNode(nodesToInsert.get(i), i, y, 0);
            insertedChrobakKantNodes.add(newNode);
            insertedNodes.add(newNode.getGraphNode());
            contour.add(newNode);
        }
        // add next subsets one by one
        for (int rank = 1; rank < canonicalOrdering.size(); rank++) {
            nodesToInsert = canonicalOrdering.get(rank);
            // holds first index of to be created ChrobakKant nodes
            rankStartIndex = insertedChrobakKantNodes.size();

            // find index p and q for new node
            p = 0;
            while (!contour.get(p).getGraphNode().getNeighbors()
                    .contains(nodesToInsert.get(0))) {
                p++;
            }
            q = contour.size() - 1;
            while (!contour.get(q).getGraphNode().getNeighbors()
                    .contains(nodesToInsert.get(nodesToInsert.size() - 1))) {
                q--;
            }

            // search for alpha and beta of Chrobak Kant
            alpha = p;
            beta = q;

            int alphafinder = 1;
            while (isSaturated(contour.get(p + alphafinder), insertedNodes)) {
                // "<" to find left node when there are nodes with equal rank
                if (contour.get(p + alphafinder).getRank() < contour.get(alpha)
                        .getRank()) {
                    alpha = p + alphafinder;
                }
                alphafinder++;
            }

            int betafinder = 1;
            while (isSaturated(contour.get(q - betafinder), insertedNodes)) {
                // "<=" to find left node when there are nodes with equal rank
                if (contour.get(q - betafinder).getRank() <= contour.get(beta)
                        .getRank()) {
                    beta = q - betafinder;
                }
                betafinder++;
            }

            // update under subsets
            for (int i = p; i <= alpha; i++) {
                contour.get(p).addToUnder(contour.get(i).getUnder());
            }
            for (int i = beta + 1; i <= q; i++) {
                contour.get(q).addToUnder(contour.get(i).getUnder());
            }

            // create first new node
            newNode = new ChrobakKantNode(nodesToInsert.get(0), rank);
            insertedChrobakKantNodes.add(newNode);
            insertedNodes.add(newNode.getGraphNode());
            for (int i = alpha + 1; i <= beta; i++) {
                newNode.addToUnder(contour.get(i).getUnder());
            }

            // create other new nodes if necessary
            for (int more = 1; more < nodesToInsert.size(); more++) {
                newNode = new ChrobakKantNode(nodesToInsert.get(more), rank);
                insertedChrobakKantNodes.add(newNode);
                insertedNodes.add(newNode.getGraphNode());
            }

            // shift
            nodesToShift = new HashSet<ChrobakKantNode>();
            for (int i = q; i < contour.size(); i++) {
                nodesToShift.addAll(contour.get(i).getUnder());
            }
            for (ChrobakKantNode node : nodesToShift) {
                node.incX(nodesToInsert.size());
            }

            // calculate coordinates for new nodes
            epsilon = isSaturated(contour.get(p), insertedNodes) ? 0 : 1;
            xp = contour.get(p).getX();
            // yp = contour.get(p).getY(); not used
            xq = contour.get(q).getX();
            yq = contour.get(q).getY();
            // index differs by 1 compared to CK algorithm, therefor a "-1" is
            // omitted in the first formula
            for (int i = 0; i < nodesToInsert.size(); i++) {
                insertedChrobakKantNodes.get(rankStartIndex + i).setX(
                        xp + i + epsilon);
                insertedChrobakKantNodes.get(rankStartIndex + i).setY(
                        yq + xq - xp - nodesToInsert.size() + 1 - epsilon);
            }

            /***************************************************/
            // additional testing for strict convex drawing, not part of
            // original chrobak kant
            if (q < contour.size() - 1) {
                for (CrossingEdgePair cp : crossingEdgePairs) {
                    if (cp.getNodes()
                            .containsAll(
                                    Arrays.asList(contour.get(q), contour
                                            .get(q + 1), nodesToInsert
                                            .get(nodesToInsert.size() - 1)))) {
                        // found place for crossing edge pair, extra shift
                        for (ChrobakKantNode node : contour.get(q + 1)
                                .getUnder()) {
                            node.incX(1);
                        }
                        break;
                    }
                }
            }
            /***************************************************/

            // update contour
            // remove covered nodes
            for (int i = p + 1; i < q; i++) {
                contour.remove(p + 1);
            }
            // insert new nodes into contour
            for (int i = 0; i < nodesToInsert.size(); i++) {
                contour.add(p + 1 + i,
                        insertedChrobakKantNodes.get(rankStartIndex + i));
            }
        } // end for rank

        // write coordinates to graph
        ymax = insertedChrobakKantNodes
                .get(insertedChrobakKantNodes.size() - 1).getY();
        writeCoordinates(insertedChrobakKantNodes);
    }

    /**
     * Inserts the edges of the given crossing edge pair into the graph.
     * 
     * @param cp
     *            the edge pair to be inserted into the graph
     */
    private void insertEdges(CrossingEdgePair cp) {
        // remove comments to mark crossing edges with an X
        // including a commented import, EdgeLabelAttribute
        Edge e1 = cp.getEdge1();
        Edge e2 = cp.getEdge2();
        Edge toAdd = graph.addEdge(e1.getSource(), e1.getTarget(), false);
        // EdgeLabelAttribute labelAttr = new EdgeLabelAttribute("crosstest");
        // labelAttr.setLabel("X");
        // toAdd.addAttribute(labelAttr, "");
        toAdd = graph.addEdge(e2.getSource(), e2.getTarget(), false);
        // labelAttr = new EdgeLabelAttribute("crosstest");
        // labelAttr.setLabel("X");
        // toAdd.addAttribute(labelAttr, "");
    }

    /**
     * Writes coordinates from ChrobakKant nodes to graph nodes
     * 
     * @param nodes
     *            Collection of ChrobakKant nodes to work with
     */
    private void writeCoordinates(Collection<ChrobakKantNode> nodes) {
        double x;
        double y;
        ChrobakKantNode current;
        for (Iterator<ChrobakKantNode> i = nodes.iterator(); i.hasNext();) {
            CoordinateAttribute ca;
            current = i.next();
            try {
                ca = (CoordinateAttribute) current.getGraphNode().getAttribute(
                        GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
            } catch (Exception e) {
                NodeGraphicAttribute ngAttribute = new NodeGraphicAttribute();
                current.getGraphNode().addAttribute(ngAttribute, "");
                ca = (CoordinateAttribute) current.getGraphNode().getAttribute(
                        GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
            }
            // adapting for better readability
            x = current.getX() * stretchFactor;
            y = ymax - current.getY();
            y = y * stretchFactor;
            Point2D point = new Point2D.Double(x, y);
            ca.setCoordinate(point);
        }
    }

    /**
     * Checks if a given node has all its neighbours in the given collection
     * 
     * @param node
     *            the node to check
     * @param currentNodes
     *            collection of nodes to check
     * @return true if all neighbours of node are in currentNodes
     */
    private static boolean isSaturated(ChrobakKantNode node,
            ArrayList<Node> currentNodes) {
        Collection<Node> neighbours = node.getGraphNode().getNeighbors();
        return currentNodes.containsAll(neighbours);
    }

    /**
     * Adds a face parameter to a graph edge, either as face 1 or if it already
     * exists as face2
     * 
     * @param edge
     *            the edge that should gat the parameter
     * @param face
     *            the face to be attached
     */
    private static void addFaceAttributeToEdge(Edge edge,
            FaceWithComponents face) {
        if (!edge.containsAttribute("face1")) {
            edge.addAttribute(new FaceAttribute("face1", face), "");
        } else if (!edge.containsAttribute("face2")) {
            edge.addAttribute(new FaceAttribute("face2", face), "");
        } else {
            throw new RuntimeException("third face for an edge found!");
        }
    }

    /**
     * Adds an integer label for a node
     * 
     * @param node
     *            the node to be labeled
     * @param number
     *            the label
     */
    private static void labelNode(Node node, int number) {
        NodeLabelAttribute labelAttr = new NodeLabelAttribute(
                GraphicAttributeConstants.LABEL, String.valueOf(number));
        node.addAttribute(labelAttr,
                GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
