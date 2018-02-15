// =============================================================================
//
//   AbstractSortingCrossMinAlgorithm.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import java.util.ArrayList;
import java.util.Collection;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This abstract class provides methods for algorithms that minimize
 * edge-crossings.
 * <p>
 * <b> All subclasses must overwrite the following methods:
 * <ul>
 * <li>getName()
 * <li>minCrossings()
 * </ul>
 * </b>
 * 
 * @author scheu
 * @version $Revision$ $Date$
 */
public class AbstractSortingCrossMinAlgorithm extends
        AbstractLocalCrossMinAlgorithm {

    /** Bean that stores the results of this phase */
    protected SugiyamaData data;

    /** The layers of the graph */
    protected NodeLayers layers;

    /**
     * Boolean indicating which of both layers is considered fix. If true, the
     * upper layer is considered fix, if false, the layer below.
     */
    protected boolean topDown;

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter iterations = new IntegerParameter(3, 1, 10,
                "Iterations",
                "How many times do you want to go through the layers "
                        + "for cross-minimization ? "
                        + "(One iteration means to go through the layers "
                        + "topDown and bottomUp for one time)");

        this.parameters = new Parameter[] { iterations };

        return this.parameters;
    }

    /*
     * @see
     * org.graffiti.plugin.algorithm.Algorithm#setParameters(org.graffiti.plugin
     * .parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        this.iterations = ((IntegerParameter) params[0]).getValue();
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#supportsBigNodes
     * ()
     */
    public boolean supportsBigNodes() {
        return false;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#
     * supportsConstraints()
     */
    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.crossmin.
     * AbstractLocalCrossMinAlgorithm#getData()
     */
    @Override
    public SugiyamaData getData() {
        return this.data;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.crossmin.
     * AbstractLocalCrossMinAlgorithm
     * #setData(org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData)
     */
    @Override
    public void setData(SugiyamaData theData) {
        this.data = theData;
        layers = data.getLayers();
        super.setData(data);
    }

    /**
     * OVERWRITE THIS METHOD!
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "";
    }

    /**
     * This method initializes all nodes in the first layer. An unique
     * <code>X-Position</code> value is assigned to each node. Values range from
     * 0 to (#nodes-1).
     */
    @Override
    protected void initialize() {
        Node tmpNode;
        layers = data.getLayers();

        for (int i = 0; i < layers.getLayer(0).size(); i++) {
            tmpNode = layers.getLayer(0).get(i);
            try {
                tmpNode.setDouble(SugiyamaConstants.PATH_XPOS, i);
            } catch (AttributeNotFoundException anfe) {
                tmpNode.addDouble(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_XPOS, i);
            }
        }
    }

    /**
     * This method processes a layer by calling the <code>minCrossings</code>
     * method to minimize edge crossings of this layer b with its adjacent
     * layer. <br>
     * <b>Each subclass must overwrite the method <code>minCrossings</code> and
     * implement the sorting algorithm to rearrange the nodes in this layer b
     * for edge crossing minimization purposes.</b>
     * 
     * @see org.graffiti.plugins.algorithms.sugiyama.crossmin.AbstractLocalCrossMinAlgorithm#processLayers(int,
     *      boolean, org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers)
     * @param b
     *            layer to process
     * @param topDown
     *            If <code>true</code>, the iteration is top-down, i.e. consider
     *            layer b-1 fixed, if <code>false</code>, consider layer b+1
     *            fixed
     * @param layers
     *            This <code>NodeLayers</code>-Object stores the layers of the
     *            graph
     */
    @Override
    protected void processLayers(int b, boolean topDown, NodeLayers layers) {
        // Container for temporarily considered node
        Node u;
        // Container for all nodes in this layer b
        ArrayList<Node> nodes = layers.getLayer(b);

        this.topDown = topDown;

        // Check if each node in this layer b has a XPos assigned.
        try {
            for (int i = 0; i < nodes.size(); i++) {
                u = nodes.get(i);
                u.getDouble(SugiyamaConstants.PATH_XPOS);
            }
        } catch (AttributeNotFoundException anfe) {
            // If reached, layer hasn't been processed so far or a new node was
            // inserted after sugiyama was already run at least for one time. In
            // this case all but this new node have a XPOS value assigned.
            // Therefore set XPOS to all nodes again.

            // Set XPOS (most likely will be changed due to crossing number)
            for (int i = 0; i < nodes.size(); i++) {
                u = nodes.get(i);

                try {
                    u.setDouble(SugiyamaConstants.PATH_XPOS, i);
                } catch (AttributeNotFoundException anfe2) {
                    u.addDouble(SugiyamaConstants.PATH_SUGIYAMA,
                            SugiyamaConstants.SUBPATH_XPOS, i);
                }
            }
        }

        minCrossings(nodes);
    }

    /**
     * This method counts all crossings of edges of node u and node v on layer b
     * with their neighbor nodes on level b-1 if topDown is set, else on level
     * b+1. Node u considered left of Node v.
     * 
     * @param u
     *            Node which is left of v (u,v)
     * @param v
     *            Node which is right of u (u,v)
     * @return the total number of crossings when u is left of v.
     */
    protected int countCrossings(Node u, Node v) {
        // Number of crossings that will be returned
        int cross = 0;
        // initial relative x-Position of node u
        int uXPos = -1;
        // initial relative x-Position of node v
        int vXPos = -1;
        // relative x-Position of adjacent neighbors of node u
        int uNXPos;
        // relative x-Position of adjacent neighbors of node v
        int vNXPos;
        // Stores all adjacent neighbor nodes of u
        Collection<Node> uNNs;
        // Stores all adjacent neighbor nodes of v
        Collection<Node> vNNs;

        uXPos = (int) u.getDouble(SugiyamaConstants.PATH_XPOS);
        vXPos = (int) v.getDouble(SugiyamaConstants.PATH_XPOS);

        // Check if node u is in front of node v.
        // If (uXPos > vXPos), node v is in front of node u.
        // Therefore temporarily swap their XPos, so u is
        // in front of v for calculation purposes
        if (uXPos > vXPos) {
            int tmp = uXPos;
            uXPos = vXPos;
            vXPos = tmp;
        }

        // Distinguish between topDown and bottomUp
        if (topDown) {
            uNNs = u.getInNeighbors();
            vNNs = v.getInNeighbors();
        } else {
            uNNs = u.getOutNeighbors();
            vNNs = v.getOutNeighbors();
        }

        // Check for crossing on all edges of node u and node v with their
        // neighbors nodes
        // Edges: (u,uN) & (v,vN)
        for (Node uN : uNNs) {
            uNXPos = (int) uN.getDouble(SugiyamaConstants.PATH_XPOS);

            for (Node vN : vNNs) {
                vNXPos = (int) vN.getDouble(SugiyamaConstants.PATH_XPOS);

                // Check for crossing
                if (((uXPos - vXPos) * (uNXPos - vNXPos)) < 0) {
                    cross++;
                }
            }
        }

        return cross;
    }

    /**
     * OVERWRITE THIS METHOD!
     * <p>
     * This method minimizes edge-crossings
     * 
     * @param list
     *            the list to sort
     */
    protected void minCrossings(ArrayList<Node> list) {
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
