// =============================================================================
//
//   FRGraphLabeling.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.labeling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.EdgeLabelPositionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;
import org.graffiti.plugin.view.View;
import org.graffiti.plugins.algorithms.springembedderFR.FREdge;
import org.graffiti.plugins.algorithms.springembedderFR.FRNode;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.label.Label;
import org.graffiti.plugins.views.fast.label.LabelManager;
import org.graffiti.selection.Selection;

/**
 * Class where the FRNodes and FREdges are saved
 * <p>This class originates from  
 * <code>org.graffiti.plugins.algorithms.springembedderFR.FRGraph</code>. 
 * A few addons have been made to fit labeling porposes.  
 * 
 * @author scholz
 * @see org.graffiti.plugins.algorithms.springembedderFR.FRGraph
 */

/**
 * @author Administrator
 * @version $Revision$ $Date$
 */
public class FRGraphLabeling {

    /**
     * FRNodes of the FRGraph
     */
    protected HashSet<FRNode> fRNodes;

    /**
     * FREdges of the FRGraph
     */
    protected HashSet<FREdge> fREdges;

    /**
     * Map where original nodes are saved as Nodes and values are FRNodes
     * <p>
     * As the spring embedder implementation aligns only FRNodes, labels of
     * original nodes are represented as FRnodes. These nodes are stored with
     * the same key as their original node.
     * <p>
     * <code>fRNodesMap.get(node).getFirst</code> always retrieves the FRNode
     * rerpresenting the original node. On the other hand, in the current
     * implementation all other nodes are FRLabelNodes representing the original
     * node's labels.
     */
    protected HashMap<Node, LinkedList<FRNode>> fRNodesMap;

    /**
     * Maps original edges to FRNodes belonging to them.
     * <p>
     * As the spring embedder implementation aligns only FRNodes, labels of
     * original edges are represented as FRLabelNodes. These nodes are stored
     * with their original edge as key. When the changes done to the
     * <code>FRGraphLabeling</code> are applied to the original graph, the
     * positions of edge labels are to be applied.
     * <p>
     * Other than the <code>fRNodesMap</code>, this map does not store the
     * original edges, as they are not needed. Edges are not touched by the
     * spring embedder implementation.
     */
    protected HashMap<Edge, LinkedList<FREdgeLabelNode>> fREdgeNodesMap;

    /**
     * Maps FR nodes to FR edges emerging from them. <i>Artificial</i> edges are
     * not allowed, i.e. edges that connect a <code>LabelNode</code>.
     * <p>
     * This is needed mostly for <code>NodeLabelNodes</code>, as collision with
     * their corresponding node's edges needs to be parameterized independently.
     */
    protected HashMap<FRNode, LinkedList<FREdge>> fRNodeFREdgesMap;

    protected HashMap<Edge, FREdge> fREdgesMap;

    /** used for special forces affecting only non-label nodes */
    protected ArrayList<FRNode> notLabelNodes;

    /** used for special forces affecting only label nodes */
    protected ArrayList<FRLabelNode> labelNodes;

    /** used for special forces affecting only node label nodes */
    protected ArrayList<FRNodeLabelNode> nodeLabelNodes;

    /** used for special forces affecting only edge label nodes */
    protected ArrayList<FREdgeLabelNode> edgeLabelNodes;

    /** used for most forces affecting only original edges */
    protected ArrayList<FREdge> notLabelEdges;

    /**
     * As a <code>FRGraph</code> emerges from an original <code>Graph</code>, it
     * has to be embedded again once the layouting is done.
     * <p>
     * The idea is to take the routine <code>extractFRGraphToGraph</code> into
     * this class' code and this is why a reference to the original graph is to
     * be kept.
     */
    protected Graph graph;

    /**
     * This routine adds a new FRNode to the graph. Do not use the lists' own
     * routines, as they do not care about graph validity.
     * <p>
     * Use this routine for:
     * <ul>
     * <li>FRNodes representing a node of the original graph
     * </ul>
     * <p>
     * Adds a given <code>FRNode</code> to:
     * <ul>
     * <li> <code>fRNodes</code> list of nodes
     * <li> <code>fRNodesMap</code> list hash map
     * <p>
     * If the slot of the map designated by 'key' is empty, a new linked list
     * containing the FRNode is inserted.
     * </ul>
     * 
     * @param newNode
     * @param key
     */
    protected void addNode(FRNode newNode, Node key) {
        // add to fRNodes
        fRNodes.add(newNode);

        // add to fRNodesMap
        LinkedList<FRNode> nodeList = fRNodesMap.get(key);
        if (nodeList == null) {
            nodeList = new LinkedList<FRNode>();
        }
        nodeList.add(newNode);
        fRNodesMap.put(key, nodeList);
        notLabelNodes.add(newNode);
    }

    /**
     * This routine adds a new FRNode to the graph. Do not use the lists' own
     * routines, as they do not care about graph validity.
     * <p>
     * Use this routine for:
     * <ul>
     * <li>FRNodes representing a node label of the original graph
     * </ul>
     * <p>
     * Adds a given <code>FRNode</code> to:
     * <ul>
     * <li> <code>fRNodes</code> list of nodes
     * <li> <code>fRNodesMap</code> hash map of lists of nodes (original graph ->
     * FRGraph)
     * <li> <code>nodeLabelNodes</code> list of node label nodes
     * <p>
     * If the slot of the map designated by 'key' is empty, a new linked list
     * containing the FRNode is inserted.
     * </ul>
     * 
     * @param newNode
     * @param key
     */
    protected void addNodeLabelNode(FRNodeLabelNode newNode, Node key) {
        // add to fRNodes
        fRNodes.add(newNode);

        // add to fRNodesMap
        LinkedList<FRNode> nodeList = fRNodesMap.get(key);
        if (nodeList == null) {
            nodeList = new LinkedList<FRNode>();
        }
        nodeList.add(newNode);
        fRNodesMap.put(key, nodeList);
        labelNodes.add(newNode);
        nodeLabelNodes.add(newNode);
    }

    /**
     * This routine adds a new FRNode to the graph. Do not use the lists' own
     * routines, as they do not care about graph validity.
     * <p>
     * Use this routine for:
     * <ul>
     * <li>FRNodes representing an edge label of the original graph
     * </ul>
     * <p>
     * Adds a given <code>FRNode</code> to:
     * <ul>
     * <li> <code>fRNodes</code> list of nodes
     * <li> <code>fREdgeNodesMap</code> hash map of lists of edges (original
     * graph -> FRGraph)
     * <li> <code>nodeLabelNodes</code> list of edge label nodes
     * <p>
     * If the slot of the map designated by 'key' is empty, a new linked list
     * containing the FRNode is inserted.
     * </ul>
     * 
     * @param newNode
     * @param key
     */
    protected void addEdgeLabelNode(FREdgeLabelNode newNode, Edge key) {
        // add to fRNodes
        fRNodes.add(newNode);

        // add to fREdgeNodesMap
        LinkedList<FREdgeLabelNode> nodeList = fREdgeNodesMap.get(key);
        if (nodeList == null) {
            nodeList = new LinkedList<FREdgeLabelNode>();
        }
        nodeList.add(newNode);
        fREdgeNodesMap.put(key, nodeList);
        labelNodes.add(newNode);
        edgeLabelNodes.add(newNode);
    }

    /**
     * This routine adds a new FREdge to the graph. Do not use the lists' own
     * routines, as they do not care about graph validity.
     * <p>
     * Use this routine for:
     * <ul>
     * <li>FREdges representing an edge of the original graph
     * </ul>
     * <p>
     * Adds a given <code>FREdge</code> to:
     * <ul>
     * <li> <code>fREdges</code> list of edges
     * <li> <code>notLabelEdges</code> list of edges (<i>non-artificial</i>)
     * <li> <code>fREdgeMap</code> hash map (original graph -> FRGraph)
     * <li> <code>fREdgeNodesMap</code> hash map of lists of nodes (original
     * graph (edge) -> FRGraph (edge label nodes)); entry creation only
     * <li> <code>fRNodeFREdgesMap</code> hash map of lists of FREdges (FRGraph
     * (node) -> FRGraph (edges)) for both source and target FRNode;
     * <p>
     * If the slot of the map designated by 'key' is empty, a new linked list
     * containing the FREdge is inserted.
     * </ul>
     */
    protected void addEdge(FREdge newEdge, Edge originalEdge) {
        this.fREdges.add(newEdge);
        fREdgesMap.put(originalEdge, newEdge);
        // create fREdgeNodesMap entry
        fREdgeNodesMap.put(originalEdge, new LinkedList<FREdgeLabelNode>());
        notLabelEdges.add(newEdge);

        // add to HashMap<FRNode, LinkedList<FREdge>> fRNodeFREdgesMap
        LinkedList<FREdge> edgeList;
        // source
        edgeList = fRNodeFREdgesMap.get(newEdge.getSource());
        if (edgeList == null) {
            edgeList = new LinkedList<FREdge>();
        }
        edgeList.add(newEdge);
        fRNodeFREdgesMap.put(newEdge.getSource(), edgeList);
        // target
        edgeList = fRNodeFREdgesMap.get(newEdge.getTarget());
        if (edgeList == null) {
            edgeList = new LinkedList<FREdge>();
        }
        edgeList.add(newEdge);
        fRNodeFREdgesMap.put(newEdge.getTarget(), edgeList);
    }

    /**
     * This routine adds a new <i>artificial</i> FREdge to the graph. Do not use
     * the lists' own routines, as they do not care about graph validity.
     * <p>
     * Use this routine for:
     * <ul>
     * <li>FREdges not being in the original graph
     * </ul>
     * <p>
     * Adds a given <code>FREdge</code> to:
     * <ul>
     * <li> <code>fREdges</code> list of edges
     * <li> <code>fREdgeNodesMap</code> hash map of lists of nodes (original
     * graph (edge) -> FRGraph (edge label nodes)); entry creation only
     * <li> <code>fRNodeFREdgesMap</code> hash map of lists of FREdges (FRGraph
     * (node) -> FRGraph (edges)) for both source and target FRNode;
     * <p>
     * If the slot of the map designated by 'key' is empty, a new linked list
     * containing the FREdge is inserted.
     * </ul>
     */
    protected void addLabelEdge(FREdge newEdge) {
        this.fREdges.add(newEdge);
    }

    /**
     * Creates a new FRGraph with empty HashSets nodes and edges
     * 
     * @param graph
     */
    public FRGraphLabeling(Graph graph, Selection sel) {
        // Extract label manager (used to compute label widths)
        View activeView = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveSession().getActiveView();
        LabelManager<?, ?> labelManager = null;
        if (activeView instanceof FastView) {
            labelManager = ((FastView) activeView).getGraphicsEngine()
                    .getLabelManager();
        } else {
            System.err
                    .println("Label widths will not be computed correctly (FastView needed).");
        }

        this.fRNodesMap = new HashMap<Node, LinkedList<FRNode>>();
        this.fREdgeNodesMap = new HashMap<Edge, LinkedList<FREdgeLabelNode>>();
        this.fREdgesMap = new HashMap<Edge, FREdge>();
        this.fRNodes = new HashSet<FRNode>();
        this.fREdges = new HashSet<FREdge>();
        this.notLabelNodes = new ArrayList<FRNode>();
        this.labelNodes = new ArrayList<FRLabelNode>();
        this.nodeLabelNodes = new ArrayList<FRNodeLabelNode>();
        this.edgeLabelNodes = new ArrayList<FREdgeLabelNode>();
        this.fRNodeFREdgesMap = new HashMap<FRNode, LinkedList<FREdge>>();
        this.notLabelEdges = new ArrayList<FREdge>();

        this.graph = graph;

        // add all nodes of the original graph to this FRGraph
        {
            Iterator<Node> nodesIt = graph.getNodesIterator();
            Node originalNode;
            FRNodeLabelNode labelNode;
            FREdge labelEdge;
            while (nodesIt.hasNext()) {

                // original node
                originalNode = nodesIt.next();

                boolean movable = false;

                // marked nodes are movable
                if (sel.getElements().size() > 0) {
                    if (sel.contains(originalNode)) {
                        movable = true;
                    }
                }

                // new FRNode
                FRNode fRNode = new FRNode(originalNode, movable);
                // insert the node into graph's data structures
                addNode(fRNode, originalNode);

                // Add node labels to FRGraph
                for (Attribute attr : originalNode.getAttributes()
                        .getCollection().values()) {
                    if (attr instanceof NodeLabelAttribute) {
                        NodeLabelAttribute labelAttr = ((NodeLabelAttribute) attr);
                        // add node label to FR graph
                        labelNode = new FRNodeLabelNode(originalNode, fRNode,
                                true, labelAttr.getLabel(),
                                labelAttr.getFont(), labelAttr.getTextcolor());

                        // set label node's size accourding to text size
                        if (labelManager != null) {
                            Label<?, ?> label = labelManager.acquireLabel(
                                    originalNode, labelAttr);
                            labelNode.setWidth(label.getWidth());
                            labelNode.setHeight(label.getHeight());
                        }

                        addNodeLabelNode(labelNode, originalNode);

                        // set initial position
                        // Note: only the "absolute offset" is recognized
                        // Rant: FRNodes and absolute offsets seem to have
                        // mirrored
                        // axes (at least they share the same scaling).
                        labelNode.setXPos(labelNode.getXPos()
                                + labelAttr.getPosition().getAbsoluteXOffset());
                        labelNode.setYPos(labelNode.getYPos()
                                + labelAttr.getPosition().getAbsoluteYOffset());

                        // System.out.print("LabelNode created: " +
                        // labelNode.getLabel());
                        // System.out.print(" (" + labelNode.getXPos() + ", ");
                        // System.out.println(labelNode.getYPos() + ")");

                        // link parent FR node and label node with a new edge
                        // TODO: momentarily, the original edge field is set to
                        // null.
                        // Maybe some code relies on original edge being valid.
                        labelEdge = new FREdge(null, labelNode, fRNode);

                        addLabelEdge(labelEdge);
                    }
                }

            }
        }

        // add all edges of the original graph to this FRGraph
        {
            Iterator<Edge> edgesIt = graph.getEdgesIterator();
            Edge originalEdge;
            FREdgeLabelNode labelNode;
            FREdge labelEdge;
            while (edgesIt.hasNext()) {

                // original edge
                originalEdge = edgesIt.next();

                // source
                Node source = originalEdge.getSource();
                // appropriate FRNode
                FRNode fRSource = fRNodesMap.get(source).getFirst(); // first is
                                                                     // original
                                                                     // node

                // target
                Node target = originalEdge.getTarget();
                // appropriate FRNode
                FRNode fRTarget = fRNodesMap.get(target).getFirst(); // first is
                                                                     // original
                                                                     // node

                FREdge fREdge = new FREdge(originalEdge, fRSource, fRTarget);

                // adds the FREdge to the FR graph's data structures
                addEdge(fREdge, originalEdge);

                // Add edge labels to FRGraph
                for (Attribute attr : originalEdge.getAttributes()
                        .getCollection().values()) {
                    if (attr instanceof EdgeLabelAttribute) {
                        // add edge label to FR graph
                        // the original node field is set to the edge's dource
                        // node.
                        EdgeLabelAttribute labelAttr = (EdgeLabelAttribute) attr;
                        labelNode = new FREdgeLabelNode(source, fREdge, true,
                                labelAttr.getLabel(), labelAttr.getFont(),
                                labelAttr.getTextcolor());

                        // set label node's size accourding to text size
                        if (labelManager != null) {
                            Label<?, ?> label = labelManager.acquireLabel(
                                    originalEdge, labelAttr);
                            labelNode.setWidth(label.getWidth());
                            labelNode.setHeight(label.getHeight());
                        }

                        // set label node's position
                        labelNode
                                .setOriginalXPos(labelNode.getXPos()
                                        + labelAttr.getPosition()
                                                .getRelativeAlignment()
                                        * (((NodeGraphicAttribute) target
                                                .getAttributes()
                                                .getAttribute(
                                                        GraphicAttributeConstants.GRAPHICS))
                                                .getCoordinate().getX() - ((NodeGraphicAttribute) source
                                                .getAttributes()
                                                .getAttribute(
                                                        GraphicAttributeConstants.GRAPHICS))
                                                .getCoordinate().getX())
                                        + labelAttr.getPosition()
                                                .getAbsoluteXOffset());
                        labelNode
                                .setOriginalYPos(labelNode.getYPos()
                                        + labelAttr.getPosition()
                                                .getRelativeAlignment()
                                        * (((NodeGraphicAttribute) target
                                                .getAttributes()
                                                .getAttribute(
                                                        GraphicAttributeConstants.GRAPHICS))
                                                .getCoordinate().getY() - ((NodeGraphicAttribute) source
                                                .getAttributes()
                                                .getAttribute(
                                                        GraphicAttributeConstants.GRAPHICS))
                                                .getCoordinate().getY())
                                        + labelAttr.getPosition()
                                                .getAbsoluteYOffset());
                        // System.out.println(labelNode.getLabel() +
                        // ": set 'original' position to (" +
                        // labelNode.getXPos() + ", " +
                        // labelNode.getYPos() + ")" +
                        // labelNode.getPosition());

                        addEdgeLabelNode(labelNode, originalEdge); // add to
                                                                   // special
                                                                   // structure

                        // System.out.print("LabelNode: " +
                        // labelNode.getLabel());
                        // System.out.print(labelNode.getWidth() + ", ");
                        // System.out.println(labelNode.getHeight());

                        // link label node with parent FR edge's linked nodes.
                        // TODO: momentarily, the original edge field is set to
                        // null.
                        // Maybe some code relies on original edge being valid.
                        labelEdge = new FREdge(originalEdge, labelNode,
                                fRNodesMap.get(originalEdge.getSource())
                                        .getFirst());
                        addLabelEdge(labelEdge);
                        labelEdge = new FREdge(originalEdge, labelNode,
                                fRNodesMap.get(originalEdge.getTarget())
                                        .getFirst());
                        addLabelEdge(labelEdge);
                    }
                }
            }
        }
    }

    /**
     * Converts the calculated results from FRGraph (used in algorithm) to the
     * original graph.
     * <p>
     * Besides of the layout, no changes are applied to the graph. It is the
     * same (rather: an equivalent) graph given to the <code>FRGraph</code>
     * constructor, besides some position changes.
     * 
     */
    public Graph extractFRGraphToGraph() {
        // System.out.println();
        // System.out.println("Extracting FR graph to original graph...");

        boolean debug = false; // TODO: remove debug
        if (debug) {

            // BEGIN DEBUG

            // show all label nodes
            int nodeCount = 0;
            int labelNodeCount = 0;
            for (Node node : graph.getNodes()) {
                HashMap<Node, LinkedList<FRNode>> nodesMap = this
                        .getFRNodesMap();
                FRNode fRNode = nodesMap.get(node).getFirst();
                NodeGraphicAttribute ngaNode = (NodeGraphicAttribute) node
                        .getAttributes().getAttribute(
                                GraphicAttributeConstants.GRAPHICS);

                ngaNode.getCoordinate().setX(fRNode.getXPos());
                ngaNode.getCoordinate().setY(fRNode.getYPos());
                nodeCount++;
            }
            // System.out.println("Original nodes extracted: " + nodeCount);

            for (FRNode labelNode : this.fRNodes) {
                Node newNode;
                NodeGraphicAttribute graphicAttribute;
                if (labelNode instanceof FRLabelNode) {
                    newNode = graph.addNode();
                    // Note: adding a node to the original graph will
                    // lead to an exception in the next iteration, as
                    // there are no corresponding FRNodes created.
                    // Use just for debug purposes and don't wonder.
                    graphicAttribute = (NodeGraphicAttribute) newNode
                            .getAttributes().getAttribute(
                                    GraphicAttributeConstants.GRAPHICS);

                    graphicAttribute.getCoordinate().setX(labelNode.getXPos());
                    graphicAttribute.getCoordinate().setY(labelNode.getYPos());
                    graphicAttribute.getDimension().setWidth(
                            labelNode.getWidth());
                    graphicAttribute.getDimension().setHeight(
                            labelNode.getHeight());
                    labelNodeCount++;
                }
            }
            // System.out.println("Label nodes extracted: " + labelNodeCount);

            // END DEBUG

        } else {

            Iterator<FRNode> fRNodesIt;
            FRLabelNode labelNode;
            // Nodes
            {
                FRNode fRNode;
                NodeLabelAttribute labelAttr;
                for (Node node : graph.getNodes()) {
                    // Retrieve all FRNodes that belong to the given node
                    fRNodesIt = fRNodesMap.get(node).iterator();

                    // Apply position change for the node itself
                    // (first in list is original node)
                    if (!fRNodesIt.hasNext())
                        throw new RuntimeException(
                                "FR Graph out of date or corrupted: no corresponding FRNode for Node "
                                        + node);
                    fRNode = fRNodesIt.next();

                    if (fRNode.isMovable()) {
                        /*
                         * ^^^ this if clause is a bugfix and should not really
                         * be there. There is this known problem that nodes are
                         * not selected by the user, but nevertheless they are
                         * moved in the first algorithm step. The reason for
                         * this is unknown. The problem with this bugfix is that
                         * all the forces were calculated with the FRNodes'
                         * position, but the actual position is not set. Indeed,
                         * the movement of the "not movables" has always been
                         * minor (a few pixels), thus the problem is not urgent.
                         */
                        NodeGraphicAttribute graphicAttribute = (NodeGraphicAttribute) node
                                .getAttributes().getAttribute(
                                        GraphicAttributeConstants.GRAPHICS);

                        graphicAttribute.getCoordinate().setX(fRNode.getXPos());
                        graphicAttribute.getCoordinate().setY(fRNode.getYPos());
                    }

                    // Apply position changes for the node's labels
                    for (Attribute attr : node.getAttributes().getCollection()
                            .values()) {
                        if (attr instanceof NodeLabelAttribute) {
                            labelAttr = (NodeLabelAttribute) attr;
                            if (!fRNodesIt.hasNext())
                                throw new RuntimeException(
                                        "FR Graph out of date or corrupted: no corresponding "
                                                + "FRLabelNode for NodeLabelAttribute "
                                                + labelAttr);
                            labelNode = (FRLabelNode) fRNodesIt.next();
                            // <- if cast does not work, FRGraph is corrupted

                            // Apply position of label node to label attribute
                            // TODO: check here
                            labelAttr
                                    .setPosition(new NodeLabelPositionAttribute(
                                            "",
                                            GraphicAttributeConstants.CENTERED,
                                            GraphicAttributeConstants.CENTERED,
                                            0d, 0d,
                                            (int) (labelNode.getXPos() - fRNode
                                                    .getXPos()),
                                            (int) (labelNode.getYPos() - fRNode
                                                    .getYPos())));
                            // TODO: localAlign for y-Coord: fRNode.getYPos()+
                            // labelNode.getYPos()

                            // FREdges from or to FRLabelNodes are ignored, as
                            // they have
                            // no representation in the original graph.
                        }
                    }
                }
            }
            // Edges
            {
                FREdge fREdge;
                Iterator<FREdgeLabelNode> fREdgeLabelNodesIt;
                EdgeLabelAttribute labelAttr;
                for (Edge edge : graph.getEdges()) {
                    // Retrieve corresponding fREdge
                    fREdge = fREdgesMap.get(edge);

                    // Retrieve all FRNodes that belong to the given edge
                    fREdgeLabelNodesIt = fREdgeNodesMap.get(edge).iterator();

                    // Apply position changes for the edge's labels
                    for (Attribute attr : edge.getAttributes().getCollection()
                            .values()) {
                        if (attr instanceof EdgeLabelAttribute) {
                            labelAttr = (EdgeLabelAttribute) attr;
                            if (!fREdgeLabelNodesIt.hasNext())
                                throw new RuntimeException(
                                        "FR Graph out of date or corrupted: no corresponding "
                                                + "FRLabelNode for NodeLabelAttribute "
                                                + labelAttr);
                            labelNode = fREdgeLabelNodesIt.next();
                            // <- if cast does not work, FRGraph is corrupted

                            // Apply position of label node to label attribute

                            labelAttr
                                    .setPosition(new EdgeLabelPositionAttribute(
                                            "",
                                            0.5d,
                                            0,
                                            (int) Math
                                                    .round(labelNode.getXPos()
                                                            - (fREdge
                                                                    .getSource()
                                                                    .getXPos() + 0.5 * (fREdge
                                                                    .getTarget()
                                                                    .getXPos() - fREdge
                                                                    .getSource()
                                                                    .getXPos()))),
                                            (int) Math
                                                    .round(labelNode.getYPos()
                                                            - (fREdge
                                                                    .getSource()
                                                                    .getYPos() + 0.5 * (fREdge
                                                                    .getTarget()
                                                                    .getYPos() - fREdge
                                                                    .getSource()
                                                                    .getYPos())))));
                            // System.out.println(
                            // "node x: " + ((int)Math.round(labelNode.getXPos()
                            // - (fREdge.getSource().getXPos() +
                            // (fREdge.getTarget().getXPos()
                            // - fREdge.getSource().getXPos()))))
                            // + "     FR xpos: " + labelNode.getXPos()
                            // + "     source xpos: " +
                            // fREdge.getSource().getXPos()
                            // + "     target xpos: " +
                            // fREdge.getTarget().getXPos()
                            // );

                            // FREdges from or to FRLabelNodes are ignored, as
                            // they have
                            // no representation in the original graph.
                        }
                    }

                }
            }

        }

        return graph;
    }

    /**
     * Returns the Iterator of the HashSet fRNodes
     * 
     * @return Iterator over the nodes of the FRGraph
     */
    public Iterator<FRNode> getFRNodesIterator() {

        return fRNodes.iterator();

    }

    /**
     * Returns the HashSet fRNodes
     * 
     * @return fRNodes
     */
    public HashSet<FRNode> getFRNodes() {

        return fRNodes;
    }

    /**
     * Returns the Iterator of the HashSet fREdges
     * 
     * @return fREdges
     */
    public Iterator<FREdge> getFREdgesIterator() {

        return fREdges.iterator();

    }

    /**
     * Returns the HashSet fREdges
     * 
     * @return fREdges
     */
    public HashSet<FREdge> getFREdges() {

        return fREdges;

    }

    /**
     * Returns the HashMap<LinkedList> fRNodesMap (mapping between original
     * nodes and FRNodes and corresponding FRLabelNodes)
     * 
     * @return fRNodesMap
     */
    public HashMap<Node, LinkedList<FRNode>> getFRNodesMap() {
        return fRNodesMap;
    }

    /** @return a list containing all and only edge label nodes */
    public ArrayList<FREdgeLabelNode> getEdgeLabelNodes() {
        return edgeLabelNodes;
    }

    /** @return a list containing all and only node label nodes */
    public ArrayList<FRNodeLabelNode> getNodeLabelNodes() {
        return nodeLabelNodes;
    }

    /** @return a list containing all nodes that are not label nodes */
    public ArrayList<FRNode> getNotLabelNodes() {
        return notLabelNodes;
    }

    /**
     * @return all <i>non-artificial</i> FREdges emerging from given node (i.e.
     *         FREdges linking not-label-nodes)
     *         <p>
     *         <b><i>Important:</i></b> will return an empty list if there are
     *         no emerging edges
     */
    public List<FREdge> getEmergingEdges(FRNode node) {
        List<FREdge> edgesMap = fRNodeFREdgesMap.get(node);
        if (edgesMap == null)
            return new LinkedList<FREdge>(); // debug: better find problem
                                             // source
        return edgesMap;
    }

    public ArrayList<FREdge> getNotLabelEdges() {
        return notLabelEdges;
    }

}
