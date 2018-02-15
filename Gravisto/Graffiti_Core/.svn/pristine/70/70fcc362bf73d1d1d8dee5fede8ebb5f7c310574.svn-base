// =============================================================================
//
//   PlanarFAS.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarfas;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.planarfas.attributes.EdgeAtt;
import org.graffiti.plugins.algorithms.planarfas.attributes.NodeSet;
import org.graffiti.plugins.algorithms.planarity.*;

/**
 * @author Barbara Eckl
 * @version $Revision$ $Date$
 */
public class PlanarFAS extends AbstractAlgorithm {

    /**
     * print out debug statements
     */
    private static final boolean debugMode = false;

    /**
     * use the improvements of Mendoncas' and Eades' algorithm
     */
    private boolean unablePreprocessing;

    /**
     * if GUIMode is true, the feedback arc set was colored
     */
    private boolean GUIMode = true;

    /**
     * value to increase a potential
     */
    private static final int DELTA = 1;

    /**
     * dualgraph of the given graph
     */
    private Graph dual;

    /**
     * instance for planarity algorithm
     */
    private PlanarityAlgorithm planarity;

    /**
     * instance of BFS Algorithm
     */
    private BFSAlgorithm bfsAlgo;

    /**
     * If executed is true, the algorithm to calculate the feedback arc set on
     * planar graphs was already executed
     */
    private boolean executed = false;

    /**
     * save the number of the original cover
     */
    private int sizeOriginalCover;

    /**
     * save the number of the cover after preprocessing
     */
    private int sizeCoverAfterPreprocessing;

    /**
     * constructor of the class
     * 
     * @param unablePreprocessing
     *            the algorithm use the preprocessing
     * @param GUIMode
     *            the edges in the feedback arc set were colored
     */
    public PlanarFAS(boolean unablePreprocessing, boolean GUIMode) {
        this.unablePreprocessing = unablePreprocessing;
        this.GUIMode = GUIMode;
        bfsAlgo = new BFSAlgorithm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        DualgraphBuilder dualgraphBuilder = new DualgraphBuilder();
        dualgraphBuilder.attach(graph);
        dualgraphBuilder.setParams(debugMode);
        dualgraphBuilder.execute();
        dual = dualgraphBuilder.getDualgraph();

        initializeAlgorithm();

        // Edges in the cover, which not satisfy d <= 0
        List<Edge> edgesWithoutSatisfysCriteria = notSatisfyCriteria();
        sizeOriginalCover = edgesWithoutSatisfysCriteria.size();

        if (unablePreprocessing) {
            Set<Node> reachableNodes;
            for (Edge edge : edgesWithoutSatisfysCriteria) {
                calculateRSet(edge.getTarget());
                NodeSet att = (NodeSet) edge.getTarget().getAttribute(
                        "reachableNodes");
                reachableNodes = att.getValue();
                if (reachableNodes.contains(edge.getSource())) {
                    edge.changeBoolean("planarFAS.inCurrentCovering", false);
                }
            }
            edgesWithoutSatisfysCriteria = notSatisfyCriteria();
            sizeCoverAfterPreprocessing = edgesWithoutSatisfysCriteria.size();
        }

        for (Node currentNode : dual.getNodes()) {
            calculateRSet(currentNode);
        }

        // Begin algorithm
        while (!edgesWithoutSatisfysCriteria.isEmpty()) {
            if (debugMode) {
                printDebugInformations();
            }

            Edge editedEdge = edgesWithoutSatisfysCriteria.remove(0);
            constructAuxiliary();
            setIgnoreOnEdges();

            for (Edge edge : dual.getEdges()) {
                if (edge.getBoolean("planarFAS.inAuxiliary")) {
                    if (edge.containsAttribute("planarFAS.ignore")) {
                        edge.changeBoolean("planarFAS.ignore", false);
                    } else {
                        edge.setBoolean("planarFAS.ignore", false);
                    }
                }
            }

            bfsAlgo.setParams("planarFAS.inT", editedEdge.getTarget(), false,
                    true, debugMode);
            bfsAlgo.execute();

            if (debugMode) {
                printDebugInformationsAboutT(editedEdge);
            }

            Set<Edge> path = null;
            boolean pathFound = false;
            if (editedEdge.getSource().getBoolean("planarFAS.inT")) {
                pathFound = true;
                path = getPath(editedEdge.getSource(), editedEdge.getTarget());
                path.add(editedEdge);
            }

            if (pathFound) {
                changeCover(path);
            } else {
                calculateNewPotential();
                calculateNewDifferential();
            }

            // remove all red edges
            for (Iterator<Edge> it = dual.getEdgesIterator(); it.hasNext();) {
                Edge edge = it.next();
                if (edge.containsAttribute("planarFAS.auxColor")
                        && edge.getString("planarFAS.auxColor").equals("red")) {
                    dual.deleteEdge(edge);
                }
            }

            edgesWithoutSatisfysCriteria = notSatisfyCriteria();

            // calculate R-Set
            if (pathFound && !edgesWithoutSatisfysCriteria.isEmpty()) {
                for (Node currentNode : dual.getNodes()) {
                    calculateRSet(currentNode);
                }
            }
        }

        if (debugMode) {
            printDebugInformations();
            System.out.println("====================");
        }

        executed = true;
        if (GUIMode) {
            colorEdges(getEdgesInFAS());
        }
    }

    /**
     * Gives the Feedback Arc Set of a plane graph.
     * 
     * @return a set of edges, which is the feedback arc set of the graph
     */
    public Set<Edge> getEdgesInFAS() {
        if (!executed) {
            this.execute();
        }

        Set<Edge> fas = new HashSet<Edge>();
        for (Iterator<Edge> it = dual.getEdgesIterator(); it.hasNext();) {
            Edge edge = it.next();
            if (edge.getBoolean("planarFAS.inCurrentCovering")
                    && edge.containsAttribute("orgEdge")) {
                fas.add((Edge) edge.getAttribute("orgEdge").getValue());
                if (debugMode) {
                    System.out.println(edge.getSource().getInteger("name")
                            + "->" + edge.getTarget().getInteger("name"));
                }
            }
        }

        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(edge.getTarget())) {
                fas.add(edge);
            }
        }

        return fas;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return (unablePreprocessing ? "Medonca's and Eades 's Algorithm"
                : "Frank's Algorithm");
    }

    /**
     * This method checks, if the graph is null, empty, undirected or not
     * planar.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        } else if (graph.isEmpty()) {
            errors.add("The graph may not be emtpy.");
        } else if (graph.isUndirected()) {
            errors.add("The graph may not be directed.");
        } else if (!getPlanarity().isPlanar()) {
            errors.add("The graph may not be planar.");
        }

        if (!errors.isEmpty()) {
            throw errors;
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void reset() {
        if (dual != null) {
            dual = null;
        }

        for (Edge edge : graph.getEdges()) {
            if (edge.containsAttribute("dualEdge")) {
                edge.removeAttribute("dualEdge");
            }
        }

        planarity = null;
        executed = false;
        super.reset();
    }

    /**
     * returns the number of edges in the cover after the preprocessing.
     * 
     * @return number of edges in the cover
     */
    public int getSizeCoverAfterPreprocessing() {
        return sizeCoverAfterPreprocessing;
    }

    /**
     * Return the number of edges of the original cover
     * 
     * @return size of the original cover
     */
    public int getSizeOriginalCover() {
        return sizeOriginalCover;
    }

    /**
     * Changes the cover; it removes the blue edges on the path and add the
     * backward edges of the white edges to the cover
     * 
     * @param path
     *            set of edges, which are on the path between target node and
     *            start node of the current edge
     * 
     */
    private void changeCover(Set<Edge> path) {
        Set<Edge> whiteEdgesInPath = new HashSet<Edge>();
        Set<Edge> blueEdgesInPath = new HashSet<Edge>();

        for (Edge pathEdge : path) {
            if (pathEdge.containsAttribute("planarFAS.auxColor")
                    && (pathEdge.getString("planarFAS.auxColor"))
                            .equals("white")) {
                whiteEdgesInPath.add(pathEdge);
            }
            if (pathEdge.containsAttribute("planarFAS.auxColor")
                    && (pathEdge.getString("planarFAS.auxColor"))
                            .equals("blue")) {
                blueEdgesInPath.add(pathEdge);
            }
        }

        for (Edge whiteEdge : whiteEdgesInPath) {
            ((Edge) whiteEdge.getAttribute("originalEdge").getValue())
                    .changeBoolean("planarFAS.inCurrentCovering", true);
        }

        for (Edge blueEdge : blueEdgesInPath) {
            blueEdge.changeBoolean("planarFAS.inCurrentCovering", false);
        }
    }

    /**
     * calculate the differentials of all edges in the dualgraph
     */
    private void calculateNewDifferential() {
        for (Iterator<Edge> it = dual.getEdgesIterator(); it.hasNext();) {
            Edge edge = it.next();
            if (edge.containsAttribute("planarFAS.differential")) {
                edge.changeInteger("planarFAS.differential",
                        calculateDifferential(edge));
            }
        }
    }

    /**
     * increase the potential of the nodes in the set T
     */
    private void calculateNewPotential() {
        for (Iterator<Node> it = dual.getNodesIterator(); it.hasNext();) {
            Node node = it.next();
            if (node.getBoolean("planarFAS.inT")) {
                node.changeInteger("planarFAS.potential",
                        node.getInteger("planarFAS.potential") + DELTA);
            }
        }
    }

    /**
     * prints debug informations about T
     * 
     * @param editedEdge
     *            edge, which currently is edited
     */
    private void printDebugInformationsAboutT(Edge editedEdge) {
        System.out.println("Current edge: "
                + editedEdge.getSource().getInteger("name") + "->"
                + editedEdge.getTarget().getInteger("name"));
        System.out.print("Nodes in T: ");
        for (Node node : dual.getNodes()) {
            if (node.getBoolean("planarFAS.inT")) {
                System.out.print(node.getInteger("name") + ", ");
            }
        }
        System.out.println("Edges in T with color:");
        String color = "";
        for (Edge edge : dual.getEdges()) {
            if (edge.containsAttribute("planarFAS.auxColor")) {
                color = edge.getString("planarFAS.auxColor");
            }

            if (edge.containsAttribute("planarFAS.inT")
                    && edge.getBoolean("planarFAS.inT")) {
                System.out.println(edge.getSource().getInteger("name") + "->"
                        + edge.getTarget().getInteger("name") + ", " + color);
            }
        }
        System.out.println();
    }

    /**
     * Calculate the path from the source node to the target node.
     * 
     * @param target
     *            node node, where the path ends
     * @param source
     *            node node, where the path starts
     * @return a set of edges, which are part of the path form the source node
     *         to the target
     */
    private Set<Edge> getPath(Node source, Node target) {
        Set<Edge> path = new HashSet<Edge>();

        if (debugMode) {
            System.out.println("Path from " + source.getInteger("name")
                    + "- -> " + target.getInteger("name") + ":");
        }

        Node currentNode = source;
        Edge edge = null;
        while (currentNode != target) {
            if (currentNode.containsAttribute("previousEdge")) {
                edge = (Edge) currentNode.getAttribute("previousEdge")
                        .getValue();
                path.add(edge);
                currentNode = edge.getSource();
                if (debugMode) {
                    System.out.print(edge.getSource().getInteger("name")
                            + " -> " + edge.getTarget().getInteger("name")
                            + "\n");
                }
            }
        }
        return path;
    }

    /**
     * Print debug informations about edges in the cover and their differentials
     */
    private void printDebugInformations() {
        System.out.println("Edges with differential und Cover:");
        int sizeOfCurrentCover = 0;
        for (Iterator<Edge> it = dual.getEdgesIterator(); it.hasNext();) {
            Edge edge = it.next();
            if (edge.containsAttribute("planarFAS.differential")) {
                System.out.println(edge.getSource().getInteger("name") + " -> "
                        + edge.getTarget().getInteger("name") + ": "
                        + edge.getInteger("planarFAS.differential") + ", "
                        + edge.getBoolean("planarFAS.inCurrentCovering"));
            }
            if (edge.getBoolean("planarFAS.inCurrentCovering")) {
                ++sizeOfCurrentCover;
            }
        }
        System.out.println("Number of edges in the current cover: "
                + sizeOfCurrentCover);
        System.out.println();
    }

    /**
     * The methode returns an edge list, which include all cover edges with
     * differential > 0.
     * 
     * @return edge list with edges in the current cover, which does not satisfy
     *         differential =< 0
     */
    private List<Edge> notSatisfyCriteria() {
        List<Edge> edgesWithoutSatisfysCriteria = new LinkedList<Edge>();
        for (Edge edge : dual.getEdges()) {
            if (edge.getBoolean("planarFAS.inCurrentCovering")
                    && edge.getBoolean("planarFAS.originalGraph")
                    && edge.getInteger("planarFAS.differential") > 0) {
                edgesWithoutSatisfysCriteria.add(edge);
            }
        }
        return edgesWithoutSatisfysCriteria;
    }

    /**
     * Calculate the reachability from the startnode to the other nodes, if the
     * dualgraph was completed with the reverse edges of the current cover edges
     * except one reverse edge.
     * 
     * @param startnode
     *            node, from which the algorithms starts searching reachable
     *            nodes
     */
    private void calculateRSet(Node startnode) {
        Set<Node> reachable = null;
        for (Edge edge : dual.getEdges()) {
            if (edge.getBoolean("planarFAS.inCurrentCovering")) {
                edge.changeBoolean("planarFAS.ignore", false);
                for (Edge cedge : dual.getEdges()) {
                    if (cedge.getBoolean("planarFAS.originalGraph")) {
                        cedge.changeBoolean("planarFAS.ignore", false);
                    }
                    if (cedge.getBoolean("planarFAS.inCurrentCovering")
                            && !edge.equals(cedge)) {
                        ((Edge) cedge.getAttribute("reverseEdge").getValue())
                                .changeBoolean("planarFAS.ignore", false);
                    }
                }

                bfsAlgo.setParams("reachableNode", startnode, false, false,
                        debugMode);
                bfsAlgo.execute();
                Set<Node> nodes = new HashSet<Node>();
                for (Node node : dual.getNodes()) {
                    if (node.getBoolean("reachableNode")) {
                        nodes.add(node);
                    }
                }

                // reset
                for (Edge cedge : dual.getEdges()) {
                    cedge.changeBoolean("reachableNode", false);
                }
                for (Node node : dual.getNodes()) {
                    node.changeBoolean("reachableNode", false);
                }
                setIgnoreOnEdges();

                if (reachable == null) {
                    reachable = new HashSet<Node>();
                    reachable.addAll(nodes);
                } else {
                    reachable.retainAll(nodes);
                }
            }
        }

        if (debugMode) {
            System.out.println("Reachable nodes from: "
                    + startnode.getInteger("name"));
            for (Node node : reachable) {
                System.out.print(node.getInteger("name") + ", ");
            }
            System.out.println();
        }

        Attribute nodeSet = new NodeSet("reachableNodes");
        nodeSet.setValue(reachable);
        if (startnode.containsAttribute("reachableNodes")) {
            startnode.removeAttribute("reachableNodes");
        }
        startnode.addAttribute(nodeSet, "");
    }

    /**
     * Construct an auxiliary graph.
     */
    private void constructAuxiliary() {
        for (Iterator<Edge> it = dual.getEdgesIterator(); it.hasNext();) {
            Edge edge = it.next();
            edge.setBoolean("planarFAS.inAuxiliary", false);
            if (edge.containsAttribute("planarFAS.auxColor")) {
                edge.removeAttribute("planarFAS.auxColor");
            }
        }

        for (Iterator<Node> nodeIt = dual.getNodesIterator(); nodeIt.hasNext();) {
            Node node = nodeIt.next();
            node.setBoolean("planarFAS.inAuxiliary", true);
        }

        // add blue edges to the auxiliary graph
        for (Iterator<Edge> edgeIt = dual.getEdgesIterator(); edgeIt.hasNext();) {
            Edge currentEdge = edgeIt.next();
            if (currentEdge.containsAttribute("planarFAS.inCurrentCovering")
                    && currentEdge.getBoolean("planarFAS.inCurrentCovering")
                    && currentEdge.containsAttribute("planarFAS.differential")
                    && currentEdge.getInteger("planarFAS.differential") >= 0) {
                currentEdge.changeBoolean("planarFAS.inAuxiliary", true);
                currentEdge.setString("planarFAS.auxColor", "blue");

            }
        }

        // add white edges to the auxiliary graph
        for (Iterator<Edge> edgeIt = dual.getEdgesIterator(); edgeIt.hasNext();) {
            Edge currentEdge = edgeIt.next();
            if (!currentEdge.getBoolean("planarFAS.inCurrentCovering")
                    && currentEdge.getBoolean("planarFAS.originalGraph")
                    && currentEdge.getInteger("planarFAS.differential") <= 0) {
                Edge reverseEdge = (Edge) currentEdge.getAttribute(
                        "reverseEdge").getValue();
                reverseEdge.changeBoolean("planarFAS.inAuxiliary", true);
                reverseEdge.setString("planarFAS.auxColor", "white");
            }
        }

        // add red Edges to the auxiliary graph
        for (Iterator<Node> nodeIt = dual.getNodesIterator(); nodeIt.hasNext();) {
            Node node = nodeIt.next();
            NodeSet att = (NodeSet) node.getAttribute("reachableNodes");
            Set<Node> reachableNodeSet = att.getValue();
            for (Node reachableNode : reachableNodeSet) {
                if (node.getInteger("planarFAS.potential") == reachableNode
                        .getInteger("planarFAS.potential")) {
                    Edge redEdge = dual.addEdge(node, reachableNode, true);
                    redEdge.setBoolean("planarFAS.originalGraph", false);
                    redEdge.setBoolean("planarFAS.inCurrentCovering", false);
                    redEdge.setBoolean("planarFAS.inAuxiliary", true);
                    redEdge.setBoolean("planarFAS.ignore", false);
                    redEdge.setString("planarFAS.auxColor", "red");
                }
            }
        }

        if (debugMode) {

            System.out.println("ConstructAuxiliary: Edges in auxiliary:");
            System.out.println("Number of Edges in the dualgraph: "
                    + dual.getNumberOfEdges());
            String color = "";
            for (Edge edge : dual.getEdges()) {
                if (edge.containsAttribute("planarFAS.auxColor")) {
                    color = edge.getString("planarFAS.auxColor");
                }

                if (edge.getBoolean("planarFAS.inAuxiliary")) {
                    System.out.println(edge.getSource().getInteger("name")
                            + "->" + edge.getTarget().getInteger("name") + ", "
                            + color);
                }
            }
            System.out.println();
        }
    }

    /**
     * Initialization of the algoithms before start
     */
    private void initializeAlgorithm() {
        Node startnode = null;

        // set potential of each node to 0
        for (Iterator<Node> it = dual.getNodesIterator(); it.hasNext();) {
            Node currentNode = it.next();
            currentNode.setInteger("planarFAS.potential", 0);
            currentNode.setBoolean("planarFAS.originalGraph", true);
            if (startnode == null) {
                startnode = currentNode;
            }
        }

        // Attribute reverse = new ReverseEdge("reverseEdge");
        for (Iterator<Edge> it = dual.getEdgesIterator(); it.hasNext();) {
            Edge currentEdge = it.next();

            // set differential of each edge to 1
            currentEdge.setInteger("planarFAS.differential", 1);
            currentEdge.setBoolean("planarFAS.inCurrentCovering", false);
            currentEdge.setBoolean("planarFAS.originalGraph", true);
            currentEdge.changeBoolean("planarFAS.ignore", false);
        }

        Collection<Edge> edges = dual.getEdges();
        for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
            Edge currentEdge = it.next();
            if (currentEdge.getBoolean("planarFAS.originalGraph")) {
                // add reverse edge of each edge in the dualgraph
                Edge reverseEdge = dual.addEdge(currentEdge.getTarget(),
                        currentEdge.getSource(), true);
                reverseEdge.setBoolean("planarFAS.ignore", true);
                reverseEdge.setBoolean("planarFAS.originalGraph", false);
                reverseEdge.setBoolean("planarFAS.inCurrentCovering", false);

                Attribute reverseEdgeAtt = new EdgeAtt("reverseEdge");
                reverseEdgeAtt.setValue(reverseEdge);
                currentEdge.addAttribute(reverseEdgeAtt, "");

                Attribute orgEdgeAtt = new EdgeAtt("originalEdge");
                orgEdgeAtt.setValue(currentEdge);
                reverseEdge.addAttribute(orgEdgeAtt, "");
            }
        }

        // Calculate start covering as spanning tree
        dual.getListenerManager().transactionStarted(this);
        bfsAlgo.attach(dual);
        bfsAlgo.setParams("planarFAS.inCurrentCovering", startnode, true,
                false, debugMode);
        dual.getListenerManager().transactionFinished(this);
        bfsAlgo.execute();
    }

    /**
     * Colors the edges in the Feedback Arc Set
     */
    private void colorEdges(Set<Edge> edgeSet) {
        for (Edge edge : graph.getEdges()) {
            ColorAttribute framecolorAtt = (ColorAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FRAMECOLOR);
            ColorAttribute fillcolorAtt = (ColorAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FILLCOLOR);
            framecolorAtt.setColor(Color.BLACK);
            fillcolorAtt.setColor(Color.BLACK);
        }

        for (Edge edge : edgeSet) {
            ColorAttribute framecolorAtt = (ColorAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FRAMECOLOR);
            ColorAttribute fillcolorAtt = (ColorAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FILLCOLOR);
            framecolorAtt.setColor(Color.RED);
            fillcolorAtt.setColor(Color.RED);
        }
    }

    /**
     * Calculate the differential of a given edge.
     * 
     * @param edge
     *            : edge, which differential should be calculated
     * @return value of the differential
     */
    private int calculateDifferential(Edge edge) {
        return 1 - edge.getTarget().getInteger("planarFAS.potential")
                + edge.getSource().getInteger("planarFAS.potential");
    }

    /**
     * Returns an instance of the planarity algorithm with an executed algorithm
     * 
     * @return instance of the planarity algorithm
     */
    private PlanarityAlgorithm getPlanarity() {
        if (planarity == null) {
            planarity = new PlanarityAlgorithm();
            planarity.attach(graph);
            planarity.execute();
        }
        return planarity;
    }

    /**
     * Ssets the ignore parameter of each edge to true.
     */
    private void setIgnoreOnEdges() {
        for (Edge edge : dual.getEdges()) {
            edge.setBoolean("planarFAS.ignore", true);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
