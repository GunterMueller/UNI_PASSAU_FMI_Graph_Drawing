// =============================================================================
//
//   ST.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DFS.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.dfs2;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * @author Diana Lucic
 */
public class DFS extends AbstractAlgorithm {
    /** the source node where the algorithm starts at */
    private Node sourceNode = null;

    /** the selected node, to set the source */
    private Selection selection;

    /** collection of the tree edges */
    private HashSet<Edge> treeEdges = null;

    /** collection of the forward edges */
    private HashSet<Edge> forwardEdges = null;

    /** collection of the back edges */
    private HashSet<Edge> backEdges = null;

    /** collection of the cross edges */
    private HashSet<Edge> crossEdges = null;

    /** collection of the DFS nodes */
    private HashSet<DFSNode> dfsNodes = null;

    /** the number witch contains the dfs number */
    private int dfsNum;

    /** the number witch contains the completion number */
    private int compNum;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "DFS";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "DFS will start with the only selected node.");

        return new Parameter[] { selParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run DFS.");

        if ((selection == null) || (selection.getNodes().size() != 1))
            throw new PreconditionException(
                    "DFS needs exactly one selected node.");

        // initialize all Hashmaps
        sourceNode = selection.getNodes().get(0);
        treeEdges = new HashSet<Edge>();
        forwardEdges = new HashSet<Edge>();
        backEdges = new HashSet<Edge>();
        crossEdges = new HashSet<Edge>();
        dfsNodes = new HashSet<DFSNode>();
        // initially color all edges black
        Iterator<Edge> edges = graph.getEdgesIterator();
        while (edges.hasNext()) {
            Edge edge = edges.next();
            setEdgeColor(edge.getSource(), edge.getTarget(), Color.BLACK);
        }

    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        dfsNum = 0;
        compNum = 0;
        Iterator<Node> nodes = graph.getNodesIterator();
        DFSNode aktNode = null;
        // initialize all nodes
        while (nodes.hasNext()) {
            Node n = nodes.next();
            DFSNode dn = new DFSNode(n);
            dn.setCompNum(0);
            dn.setDfsNum(0);
            dn.setLowpoint(0);
            dn.setMarked(false);
            if (n.equals(sourceNode)) {
                aktNode = dn;
            }
            dfsNodes.add(dn);
        }
        // execute dfs
        while (aktNode != null) {
            aktNode.setMarked(true);
            dfsNum++;
            aktNode.setDfsNum(dfsNum);
            aktNode.setLowpoint(dfsNum);
            dfs1(aktNode, dfsNum, compNum);
            compNum++;
            aktNode.setCompNum(compNum);
            String label = String.valueOf(aktNode.getDfsNum()) + "/"
                    + String.valueOf(aktNode.getCompNum());
            setLabel(aktNode.getOriginalNode(), label);
            aktNode = unmarked();
        }
        // devide edges in tree/back/cross and forward edges
        classifyEdges();

        if (graph.isUndirected()) {
            printLowpoint();
        }

    }

    /**
     * this method prints all lowpoints, if the graph is undirected
     */
    private void printLowpoint() {
        Iterator<DFSNode> nodes = dfsNodes.iterator();
        while (nodes.hasNext()) {
            DFSNode tmp = nodes.next();
            String label = String.valueOf(tmp.getDfsNum()) + "/"
                    + String.valueOf(tmp.getLowpoint());
            setLabel(tmp.getOriginalNode(), label);
        }

    }

    /**
     * this method classifies all edges regarding the source/targets dfs number
     * and completion number it colors all edges depending on
     * back/forward/cross/
     */
    private void classifyEdges() {
        Iterator<Edge> edges = graph.getEdgesIterator();
        while (edges.hasNext()) {
            Edge edge = edges.next();
            if (!treeEdges.contains(edge)) {
                DFSNode from = getDFSNode(edge.getSource());
                DFSNode to = getDFSNode(edge.getTarget());
                if (from.getDfsNum() <= to.getDfsNum()) {
                    if (edge.isDirected()) {
                        forwardEdges.add(edge);
                        setEdgeColor(edge.getSource(), edge.getTarget(),
                                Color.RED);
                    } else {
                        backEdges.add(edge);
                        setEdgeColor(edge.getSource(), edge.getTarget(),
                                Color.BLUE);
                    }
                } else if (from.getCompNum() <= to.getCompNum()) {
                    backEdges.add(edge);
                    setEdgeColor(edge.getSource(), edge.getTarget(), Color.BLUE);
                } else {
                    crossEdges.add(edge);
                    setEdgeColor(edge.getSource(), edge.getTarget(),
                            Color.YELLOW);
                }
            }
        }
    }

    /**
     * Implementation of the dfs algorithm according to the script
     * "Effiziente Algorithmen"
     * 
     * @param aktNode
     *            actual Node
     * @param dfsNum2
     *            current dfs number
     * @param compNum2
     *            current completion number
     */
    private void dfs1(DFSNode aktNode, int dfsNum2, int compNum2) {
        Node n = aktNode.getOriginalNode();
        Iterator<Node> neighbors = n.getOutNeighborsIterator();
        while (neighbors.hasNext()) {
            DFSNode dn = getDFSNode(neighbors.next());
            if (!dn.isMarked()) {
                dfsNum++;
                dn.setDfsNum(dfsNum);
                dn.setLowpoint(dfsNum);
                dn.setMarked(true);
                dn.setPred(aktNode);
                Iterator<Edge> edges = (graph.getEdges(n, dn.getOriginalNode())
                        .iterator());
                if (edges.hasNext()) {
                    Edge edge = edges.next();
                    treeEdges.add(edge);
                    setEdgeColor(edge.getSource(), edge.getTarget(),
                            Color.GREEN);
                }
                dfs1(dn, dfsNum, compNum);
                compNum++;
                dn.setCompNum(compNum);
                aktNode.setLowpoint(Math.min(dn.getLowpoint(), aktNode
                        .getLowpoint()));
                String label = String.valueOf(dn.getDfsNum()) + "/"
                        + String.valueOf(dn.getCompNum());
                setLabel(dn.getOriginalNode(), label);
            } else if (dn.getPred() != aktNode) {
                aktNode.setLowpoint(Math.min(aktNode.getLowpoint(), dn
                        .getDfsNum()));
            }
        }

    }

    /**
     * Sets the color of a edge
     * 
     * @param node1
     *            The source node
     * @param node2
     *            The target node
     * @param c
     *            The new color
     */
    public static void setEdgeColor(Node node1, Node node2, Color c) {
        for (Iterator<Edge> i = node1.getEdgesIterator(); i.hasNext();) {
            Edge e = i.next();
            if ((e.getTarget() == node2) || (e.getSource() == node2)) {
                ColorAttribute ca = (ColorAttribute) e
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FILLCOLOR);

                ca.setColor(c);
                ca = (ColorAttribute) e
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FRAMECOLOR);

                ca.setColor(c);
                return;
            }
        }
    }

    /**
     * This method returns the DFS node corresponding to the given node
     * 
     * @param node
     *            The original node
     * @return DFS Node The corresponding DFS node
     */
    private DFSNode getDFSNode(Node node) {
        Iterator<DFSNode> nodes = dfsNodes.iterator();
        while (nodes.hasNext()) {
            DFSNode dn = nodes.next();
            if (node.equals(dn.getOriginalNode()))
                return dn;
        }
        return null;
    }

    /**
     * This method is looking for any unmarked node
     * 
     * @return DFSNode any unmarked node
     */
    private DFSNode unmarked() {
        Iterator<DFSNode> nodes = dfsNodes.iterator();
        while (nodes.hasNext()) {
            DFSNode tmp = nodes.next();
            if (!tmp.isMarked())
                return tmp;
        }
        return null;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
        dfsNum = 0;
        compNum = 0;
    }

    /**
     * Sets a Label to a node.
     * 
     * Taken from <code>org.graffiti.plugins.algorithms.bfs.BFS</code>
     * 
     * @param n
     *            The node to label
     * @param val
     *            The label
     */
    private void setLabel(Node n, String val) {
        LabelAttribute labelAttr = (LabelAttribute) searchForAttribute(n
                .getAttribute(""), LabelAttribute.class);
        if (labelAttr != null) {
            labelAttr.setLabel(val);
        } else {
            labelAttr = new NodeLabelAttribute("label");
            labelAttr.setLabel(val);
            n.addAttribute(labelAttr, "");
        }
    }

    /**
     * Taken from <code>org.graffiti.plugins.algorithms.bfs.BFS</code>
     */
    private Attribute searchForAttribute(Attribute attr,
            Class<? extends Attribute> attributeType) {
        if (attributeType.isInstance(attr))
            return attr;
        else {
            if (attr instanceof CollectionAttribute) {
                Iterator<Attribute> it = ((CollectionAttribute) attr)
                        .getCollection().values().iterator();
                while (it.hasNext()) {
                    Attribute newAttr = searchForAttribute(it.next(),
                            attributeType);
                    if (newAttr != null)
                        return newAttr;
                }
            } else if (attr instanceof CompositeAttribute)
                return null;
        }
        return null;
    }

    /**
     * Returns the backEdges.
     * 
     * @return the backEdges.
     */
    public HashSet<Edge> getBackEdges() {
        return backEdges;
    }

    /**
     * Returns the dfsNodes.
     * 
     * @return the dfsNodes.
     */
    public HashSet<DFSNode> getDfsNodes() {
        return dfsNodes;
    }

    /**
     * Returns the treeEdges.
     * 
     * @return the treeEdges.
     */
    public HashSet<Edge> getTreeEdges() {
        return treeEdges;
    }

    /**
     * Sets the selection.
     * 
     * @param selection
     *            the selection to set.
     */
    public void setSelection(Selection selection) {
        this.selection = selection;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
