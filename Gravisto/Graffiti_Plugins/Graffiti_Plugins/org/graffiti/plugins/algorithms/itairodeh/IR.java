// =============================================================================
//
//   IR.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: IR.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.itairodeh;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.Dash;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LineModeAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.dfs2.DFSNode;
import org.graffiti.plugins.algorithms.stnumbering.ST;
import org.graffiti.selection.Selection;

/**
 * This class implements the Itai-Rodeh Algorithm to construct 2 independent
 * spanning trees from one 2 connected tree depending on the ST-Numbering
 * algorithm
 * 
 * @author Diana Lucic
 */
public class IR extends AbstractAlgorithm {
    /** the selected node, to set the source */
    private Selection selection;

    /** The DFSNode representation of all nodes */
    private HashSet<DFSNode> dfsNodes = null;

    /** the reference to the ST Numbering algorithm */
    private ST st = null;

    /** Edges of the first spanning tree */
    private HashSet<Edge> ftEdges = null;

    /** Edges of the second spanning tree */
    private HashSet<Edge> stEdges = null;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Itai-Rodeh Construction";
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
                "IR will start with the only selected node.");

        return new Parameter[] { selParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run IR.");

        if ((selection == null) || (selection.getNodes().size() != 1))
            throw new PreconditionException(
                    "IR needs exactly one selected node.");
        if (graph.isDirected())
            throw new PreconditionException(
                    "The graph is directed. Cannot run Itai-Rodeh.");

        dfsNodes = new HashSet<DFSNode>();

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // start with running the ST Numbering
        st = new ST();
        st.attach(graph);
        st.setSelection(selection);
        try {
            st.check();
        } catch (PreconditionException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(),
                    "Precondition(s) failed", JOptionPane.ERROR_MESSAGE);
        }
        st.execute();
        dfsNodes = st.getDfsNodes();

        ftEdges = new HashSet<Edge>();
        stEdges = new HashSet<Edge>();
        Iterator<DFSNode> nodes = dfsNodes.iterator();
        while (nodes.hasNext()) {
            DFSNode tmp = nodes.next();
            Iterator<Node> neighbors = tmp.getOriginalNode()
                    .getNeighborsIterator();
            // construct the first tree
            while (neighbors.hasNext()) {
                DFSNode nb = getDFSNode(neighbors.next());
                if (nb.getStNum() < tmp.getStNum()) {
                    if (tmp.getStNum() == graph.getNumberOfNodes()) {
                        // if node is t then do not add edge (s,t)
                        if (nb.getStNum() != 1) {
                            ftEdges.add(graph.getEdges(tmp.getOriginalNode(),
                                    nb.getOriginalNode()).iterator().next());
                            break;
                        }
                    } else {
                        ftEdges.add(graph.getEdges(tmp.getOriginalNode(),
                                nb.getOriginalNode()).iterator().next());
                        break;
                    }
                }
            }
            // construct the second tree
            Iterator<Node> neighbors2 = tmp.getOriginalNode()
                    .getNeighborsIterator();
            while (neighbors2.hasNext()) {
                DFSNode nb = getDFSNode(neighbors2.next());
                // if node is s add edge (s,t)
                if (tmp.getStNum() == 1) {
                    if (nb.getStNum() == graph.getNumberOfNodes()) {
                        stEdges.add(graph.getEdges(tmp.getOriginalNode(),
                                nb.getOriginalNode()).iterator().next());
                        break;
                    }
                } else if (nb.getStNum() > tmp.getStNum()
                        && tmp.getStNum() != graph.getNumberOfNodes()) {
                    // for all nodes != {s,t} with a neighbor which has greater
                    // stNum
                    stEdges.add(graph.getEdges(tmp.getOriginalNode(),
                            nb.getOriginalNode()).iterator().next());
                    break;
                }
            }
        }
        // set all original edges color=white
        // deletion is not possible, because later the edges should be added
        // again
        // and the reference to the graph would be lost
        Iterator<Edge> edges = graph.getEdgesIterator();
        while (edges.hasNext()) {
            Edge e = edges.next();
            setEdgeColor(e, Color.WHITE);
            setEdgeDash(e, 0);
        }

        // add all edges from first tree in red color and thick
        Iterator<Edge> ftE = ftEdges.iterator();
        while (ftE.hasNext()) {
            Edge e = ftE.next();
            setEdgeColor(e, Color.BLACK);
            graph.addEdgeCopy(e, e.getSource(), e.getTarget());
        }

        // add all edges from first tree in red color and thick
        Iterator<Edge> stE = stEdges.iterator();
        while (stE.hasNext()) {
            Edge e = stE.next();
            setEdgeColor(e, Color.RED);
            setEdgeDash(e, 12);
            graph.addEdgeCopy(e, e.getSource(), e.getTarget());
        }
    }

    /**
     * Dashes the given edge
     * 
     * @param e
     *            The edge to dash
     * @param d
     *            The dash parameter
     */
    private void setEdgeDash(Edge e, float d) {
        Dash dash = null;
        if (d == 0) {
            dash = new Dash(null, 0);
        } else {
            dash = new Dash(new float[] { d, d }, 0);
        }
        LineModeAttribute lma = (LineModeAttribute) e
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.LINEMODE);
        lma.setDashArray(dash.getDashArray());
        return;
    }

    /**
     * Sets the color of a edge
     * 
     * @param e
     *            The edge to color
     * @param c
     *            The new color
     */
    public static void setEdgeColor(Edge e, Color c) {
        ColorAttribute ca = (ColorAttribute) e
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.FILLCOLOR);

        ca.setColor(c);
        ca = (ColorAttribute) e.getAttribute(GraphicAttributeConstants.GRAPHICS
                + Attribute.SEPARATOR + GraphicAttributeConstants.FRAMECOLOR);

        ca.setColor(c);
        return;
    }

    /**
     * Returns the corresponding DFSNode for the given node
     * 
     * @param node
     *            The original node
     * @return DFSNode The DFS Node
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

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
