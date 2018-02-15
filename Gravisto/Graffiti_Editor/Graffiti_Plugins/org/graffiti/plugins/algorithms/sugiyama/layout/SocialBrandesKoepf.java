// =============================================================================
//
//   SocialBrandesKoepf.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SocialBrandesKoepf.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.core.CoordinateAssignment;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This wrapper-class integrates the Brandes/Koepf-Algorithm from Social
 * Networks into the Sugiyama-Framework.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class SocialBrandesKoepf extends AbstractAlgorithm implements
        LayoutAlgorithm {
    /** The name of the algorithm */
    private final String ALGORITHM_NAME = "Brandes/Koepf from Social Networks";
    /** The <code>SugiyamaData</code>-Bean */
    private SugiyamaData data;
    /** The underlying implementation of the Brandes/Koepf-Algorithm */
    private CoordinateAssignment coordAssignment;

    /** Minimal offset between the nodes on a layer */
    private int MIN_OFFSET_X = 75;
    /** Minimal offset between each layer */
    private int MIN_OFFSET_Y = 75;
    /** Force the x-offset to user-value or not */
    private boolean force_offset_x = false;
    /** Force the y-offset to user-value or not */
    private boolean force_offset_y = false;

    /** The dummy-nodes in the graph */
    private HashSet<Node> dummies;
    /** The order of the nodes - order on each layer, etc */
    private LinkedList<Node>[] order;
    /** The computed relative coordinates from Brandes/Koepf */
    private HashMap<Node, Integer> coordinates;

    public boolean supportsArbitraryXPos() {
        return false;
    }

    /**
     * This method executes the algorithm - Initialize the data-structures, call
     * the Brandes/Koepf-Implementation and update the coordinates of the nodes
     * according to the relative coordinates returned from the
     * Brandes/Koepf-Implemenatation and user-defined offsets
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        this.init();
        coordinates = this.coordAssignment.getCoordinates();
        this.updateGraph();
        graph.getListenerManager().transactionFinished(this);

    }

    /**
     * This method updates the coordinates of the nodes after their relative
     * position has been calculated
     */
    private void updateGraph() {

        double min_x = Double.POSITIVE_INFINITY;
        double min_y = Double.POSITIVE_INFINITY;
        double max_x = Double.NEGATIVE_INFINITY;
        double max_y = Double.NEGATIVE_INFINITY;
        double offset_x;
        double offset_y;
        int pos_x;
        int max_nodes = 0;
        ArrayList<Node> layer;

        Iterator<Node> nodes = graph.getNodesIterator();
        Node cur;
        CoordinateAttribute ca;

        // find maximal x/y-coordinates
        while (nodes.hasNext()) {
            cur = nodes.next();
            try {
                ca = (CoordinateAttribute) cur
                        .getAttribute("graphics.coordinate");
                if (ca.getX() < min_x) {
                    min_x = ca.getX();
                }
                if (ca.getX() > max_x) {
                    max_x = ca.getX();
                }
                if (ca.getY() < min_y) {
                    min_y = ca.getY();
                }
                if (ca.getY() > max_y) {
                    max_y = ca.getY();
                }
            } catch (AttributeNotFoundException anfe) {
                // No GUI
            }
        }
        // compute x/y-offsets
        offset_y = (max_y - min_y) / data.getLayers().getNumberOfLayers();

        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            if (data.getLayers().getLayer(i).size() > max_nodes) {
                max_nodes = data.getLayers().getLayer(i).size();
            }
        }
        offset_x = (max_x - min_x) / max_nodes;

        if (offset_y < MIN_OFFSET_Y || force_offset_y) {
            offset_y = MIN_OFFSET_Y;
        }
        if (offset_x < MIN_OFFSET_X || force_offset_x) {
            offset_x = MIN_OFFSET_X;
        }

        // update coordinates on the nodes - write a coordinate-attribute
        // in the sugiyama-tree and let the framework take care of manipulating
        // the "real" graphics.coordinate
        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            layer = data.getLayers().getLayer(i);

            for (int j = 0; j < layer.size(); j++) {
                cur = layer.get(j);
                if (coordinates.containsKey(cur)) {
                    pos_x = coordinates.get(cur);
                    ca = new CoordinateAttribute(
                            SugiyamaConstants.SUBPATH_COORDINATE, min_x
                                    + (pos_x * offset_x), min_y
                                    + (i * offset_y));
                    try {
                        cur.addAttribute(ca, SugiyamaConstants.PATH_SUGIYAMA);
                    } catch (AttributeExistsException aee) {
                        cur.removeAttribute(SugiyamaConstants.PATH_COORDINATE);
                        cur.addAttribute(ca, SugiyamaConstants.PATH_SUGIYAMA);
                    }
                }
            }
        }
    }

    /**
     * Initialize the data-structures needed by the Brandes/Koepf-Implementation
     */
    @SuppressWarnings("unchecked")
    private void init() {
        dummies = data.getDummyNodes();
        order = new LinkedList[data.getLayers().getNumberOfLayers()];
        for (int i = 0; i < order.length; i++) {
            order[i] = new LinkedList<Node>();
            for (int j = 0; j < data.getLayers().getLayer(i).size(); j++) {
                order[i].add(data.getLayers().getLayer(i).get(j));
            }
        }
        HashMap<Edge, Integer> offset = new HashMap<Edge, Integer>();
        Iterator<Edge> edgeIter = graph.getEdgesIterator();
        while (edgeIter.hasNext()) {
            offset.put(edgeIter.next(), new Integer(0));
        }

        this.coordAssignment = new CoordinateAssignment(this.graph, this.order,
                offset, dummies);
    }

    @Override
    public void reset() {
        order = null;
        graph = null;
        dummies = null;
        coordinates = null;
        super.reset();
    }

    /**
     * Return the supported parameters of this algorithm. The user can define
     * the x/y-offsets in the graph and if he wants to force the offsets to the
     * value he defined
     * 
     * @return Returns a <code>Parameter[]</code> of supported parameters
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter p1 = new IntegerParameter(MIN_OFFSET_X, 1, 99999,
                "Minimal x-offset", "Set the minimal x-offset here");
        IntegerParameter p2 = new IntegerParameter(MIN_OFFSET_Y, 1, 99999,
                "Minimal y-offset", "Set the minimal y-offset here");
        BooleanParameter p3 = new BooleanParameter(false, "Force x-offset",
                "Do you want to force the x-offset to be the "
                        + "value you specified?");
        BooleanParameter p4 = new BooleanParameter(false, "Force y-offset",
                "Do you want to force the y-offset to be the "
                        + "value you specified?");

        this.parameters = new Parameter[] { p1, p2, p3, p4 };
        return this.parameters;
    }

    /**
     * Apply the configured parameters to the algorithm
     * 
     * @param params
     *            Parameters of the algorithm
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        MIN_OFFSET_X = ((IntegerParameter) params[0]).getValue();
        MIN_OFFSET_Y = ((IntegerParameter) params[1]).getValue();
        force_offset_x = ((BooleanParameter) params[2]).getValue();
        force_offset_y = ((BooleanParameter) params[3]).getValue();
    }

    public SugiyamaData getData() {
        return this.data;
    }

    public void setData(SugiyamaData theData) {
        this.data = theData;
    }

    public String getName() {
        return this.ALGORITHM_NAME;
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)
                || algorithmType
                        .equals(SugiyamaConstants.PARAM_RADIAL_SUGIYAMA);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
