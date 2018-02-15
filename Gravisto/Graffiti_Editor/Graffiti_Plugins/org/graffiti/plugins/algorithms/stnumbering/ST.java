// =============================================================================
//
//   ST.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ST.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.stnumbering;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JOptionPane;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.dfs2.DFS;
import org.graffiti.plugins.algorithms.dfs2.DFSNode;
import org.graffiti.selection.Selection;

/**
 * This class implements the ST-Numbering Algorithm whith is based upon the
 * DFS/lowpoint numbering
 * 
 * @author Diana Lucic
 */
public class ST extends AbstractAlgorithm {
    /** the source node where the algorithm starts at */
    private Node sourceNode = null;

    /** the selected nodes */
    private Selection selection;

    /** collection of the tree edges */
    private HashSet<Edge> treeEdges = null;

    /** collection of the back edges */
    private HashSet<Edge> backEdges = null;

    /** collection of the DFS nodes */
    private HashSet<DFSNode> dfsNodes = null;

    /** the reference to the DFS algorithm */
    private DFS dfs = null;

    /** stack for the st-numbering */
    private Stack<DFSNode> stack = null;

    /** stack for the current lowpointpath */
    private Stack<DFSNode> lpp = null;

    /** stack for the tree path */
    private Stack<DFSNode> tp = null;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "ST-Numbering";
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
                "ST will start with the only selected node.");

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
        if (graph.isDirected())
            throw new PreconditionException(
                    "The graph is directed. Cannot run ST-Numbering.");

        // initialize all needed collections (HashSets)
        sourceNode = selection.getNodes().get(0);
        treeEdges = new HashSet<Edge>();
        backEdges = new HashSet<Edge>();
        dfsNodes = new HashSet<DFSNode>();

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // first execute DFS to get the lowpoint numbers
        dfs = new DFS();
        dfs.attach(graph);
        dfs.setSelection(selection);
        try {
            dfs.check();
        } catch (PreconditionException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Precondition(s) failed", JOptionPane.ERROR_MESSAGE);
        }
        dfs.execute();
        // fill the edge/node collections from the DFS algorithm
        treeEdges = dfs.getTreeEdges();
        backEdges = dfs.getBackEdges();
        dfsNodes = dfs.getDfsNodes();
        // unmark all nodes
        Iterator<DFSNode> nodes = dfsNodes.iterator();
        DFSNode s = null;
        while (nodes.hasNext()) {
            DFSNode tmp = nodes.next();
            tmp.setMarked(false);
            if (tmp.getDfsNum() == 2) {
                s = tmp;
            }
        }
        stack = new Stack<DFSNode>();
        DFSNode t = getDFSNode(sourceNode);
        // source = t = DFS-Num 1
        stack.push(t);
        t.setMarked(true);
        // top of stack is now s with DFS-Num 2
        stack.push(s);
        s.setMarked(true);
        int i = 1;
        lpp = new Stack<DFSNode>();
        tp = new Stack<DFSNode>();
        while (!stack.empty()) {
            DFSNode v = stack.pop();
            v.setStNum(i);
            String val = String.valueOf(v.getDfsNum()) + "/"
                    + String.valueOf(v.getLowpoint()) + "/"
                    + String.valueOf(v.getStNum());
            setLabel(v.getOriginalNode(), val);
            i++;
            // search for treeedge (v,w) with unmarked w
            Iterator<Node> neighbors = v.getOriginalNode()
                    .getNeighborsIterator();
            lpp.clear();
            tp.clear();
            while (neighbors.hasNext()) {
                DFSNode w = getDFSNode(neighbors.next());
                if (!w.isMarked()) {
                    Collection<Edge> edge = graph.getEdges(v.getOriginalNode(),
                            w.getOriginalNode());
                    edge.addAll(graph.getEdges(w.getOriginalNode(), v
                            .getOriginalNode()));
                    if (treeEdges.contains(edge.iterator().next())) {
                        // create the lowpoint path by following unmarked nodes
                        // with the same lowpoint
                        lpp.push(w);
                        w.setMarked(true);
                        getLowPointPath(w);
                        while (!lpp.empty()) {
                            stack.push(lpp.pop());
                        }
                    }
                }
            }
            Iterator<Edge> be = backEdges.iterator();
            while (be.hasNext()) {
                Edge e = be.next();
                DFSNode nb = null;
                // if there is a back edge to the actual node
                if (e.getSource().equals(v.getOriginalNode())
                        || e.getTarget().equals(v.getOriginalNode())) {
                    // find the target node of the back edge necessary because
                    // of the undirected edges
                    if (e.getSource().equals(v.getOriginalNode())) {
                        nb = getDFSNode(e.getTarget());
                    } else {
                        nb = getDFSNode(e.getSource());
                    }
                    // source/target-node of the back edge has to be unmarked
                    if (!nb.isMarked()) {
                        // create the treepath by following the predecessor
                        tp.push(nb);
                        nb.setMarked(true);
                        getTreePath(nb);
                        while (!tp.empty()) {
                            stack.push(tp.pop());
                        }
                    }
                }
            }
        }

    }

    /**
     * This method constructs the treepath by following the predecessor node the
     * method is called recursively terminates when the predecessor is marked
     * 
     * @param ta
     *            the DFS node to start with
     */
    private void getTreePath(DFSNode ta) {
        DFSNode pre = ta.getPred();
        if (!pre.isMarked()) {
            pre.setMarked(true);
            tp.push(pre);
            getTreePath(pre);
        }
    }

    /**
     * This method constructs the lowpoint path by following unmarked neighbors
     * with the same lowpoint. the method is called recursively terminates when
     * the predecessor is marked
     * 
     * @param w
     *            the DFS node to start with
     */
    private void getLowPointPath(DFSNode w) {
        Iterator<Node> neighbors = w.getOriginalNode().getNeighborsIterator();
        while (neighbors.hasNext()) {
            DFSNode nw = getDFSNode(neighbors.next());
            if (!nw.isMarked()) {
                // check if the lowpoint is the same, do not add the node to
                // whitch the back edge returns
                if (w.getLowpoint() == nw.getLowpoint()
                        && nw.getDfsNum() != nw.getLowpoint()) {
                    lpp.push(nw);
                    nw.setMarked(true);
                    getLowPointPath(nw);
                }
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
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
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
     * Sets the selection.
     * 
     * @param selection
     *            the selection to set.
     */
    public void setSelection(Selection selection) {
        this.selection = selection;
    }

    /**
     * Returns the dfsNodes.
     * 
     * @return the dfsNodes.
     */
    public HashSet<DFSNode> getDfsNodes() {
        return dfsNodes;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
