//=============================================================================
//
//   CyclicBKAttributesCreator.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================

package org.graffiti.plugins.algorithms.sugiyama.levelling;

import static java.lang.Math.atan2;
import static java.lang.Math.min;
import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * This class can be used to create test cases for the Cyclic-Brandes/Koepf
 * Algorithm. It estimates the number of levels to create by assuming that the
 * node with the smallest angle bigger than ANGLE_MARGIN needs to be put on
 * level 1 (i.e. the second level).
 * 
 * @author fueloep
 */
public class CyclicBKAttributesCreator extends AbstractCyclicLevelingAlgorithm
        implements LevellingAlgorithm {

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.decycling.DecyclingAlgorithm
     * #undo()
     */
    public void undo() {
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#
     * supportsAlgorithmType(java.lang.String)
     */
    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType.equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#supportsBigNodes
     * ()
     */
    public boolean supportsBigNodes() {
        return true;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#
     * supportsConstraints()
     */
    public boolean supportsConstraints() {
        return true;
    }

    // private static final double MIN_RADIUS = 5;

    @Override
    public void check() throws PreconditionException {
        if (true)
            return;
        // Graph graph = data.getGraph();
        // for (Node node : graph.getNodes())
        // {
        // double x = node.getDouble(GraphicAttributeConstants.COORDX_PATH);
        // double y = node.getDouble(GraphicAttributeConstants.COORDY_PATH);
        // double radius = sqrt(x * x + y * y);
        // if (radius < MIN_RADIUS)
        // throw new UnsupportedOperationException(
        // "Please move all Nodes at least " + MIN_RADIUS
        // + " away from the center. Node \"" + node.toString()
        // + "\" violates this condition.");
        // }
    }

    // all angles are rounded to multiples of this value
    private static final double ANGLE_MARGIN = 5.0 / 180.0 * Math.PI;

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.levelling.
     * AbstractCyclicLevelingAlgorithm.levelNodes()
     */
    @Override
    public void levelNodes() {
        // determine the number of layers and the node spacing:

        // find the smallest angle bigger than ANGLE_MARGIN
        Graph graph = data.getGraph();
        double minAngle = Double.POSITIVE_INFINITY;
        for (Node node : graph.getNodes()) {
            double x = node.getDouble(GraphicAttributeConstants.COORDX_PATH);
            double y = node.getDouble(GraphicAttributeConstants.COORDY_PATH);
            double angle = atan2(-y, x);
            if (angle < 0) {
                angle += 2 * Math.PI;
            }
            if (angle >= ANGLE_MARGIN - 0.000001) {
                minAngle = min(minAngle, angle);
            }
        }

        // round minAngle to a multiple of ANGLE_MARGIN
        minAngle = round(minAngle / ANGLE_MARGIN) * ANGLE_MARGIN;

        // estimate the number of layers
        numberOfLevels = (int) round((2 * Math.PI) / minAngle);
        double anglePerLayer = (2 * Math.PI) / numberOfLevels;

        // make room for the nodes
        ArrayList<List<Node>> layer = new ArrayList<List<Node>>(numberOfLevels);
        for (int i = 0; i < numberOfLevels; i++) {
            layer.add(new ArrayList<Node>());
        }

        // put the nodes in the layers
        for (Node node : graph.getNodes()) {
            double x = node.getDouble(GraphicAttributeConstants.COORDX_PATH);
            double y = node.getDouble(GraphicAttributeConstants.COORDY_PATH);
            double angle = atan2(-y, x);
            if (angle < 0) {
                angle += 2 * Math.PI;
            }
            int index = (int) round(angle / anglePerLayer);
            layer.get(index).add(node);
        }

        // remove empty layers
        for (Iterator<List<Node>> it = layer.iterator(); it.hasNext();)
            if (it.next().isEmpty()) {
                it.remove();
            }

        // sort each layer
        Comparator<Node> comparator = new CyclicBKAttributesCreator.NodeComparator();
        for (List<Node> currLayer : layer) {
            Collections.sort(currLayer, comparator);
        }

        // create the sugiyama attributes "level", "xpos" and "dummy"
        for (int level = 0; level < layer.size(); level++) {
            List<Node> currLayer = layer.get(level);
            for (int xpos = 0; xpos < currLayer.size(); xpos++) {
                Node node = currLayer.get(xpos);
                node.setInteger(SugiyamaConstants.PATH_LEVEL, level);
                node.setDouble(SugiyamaConstants.PATH_XPOS, xpos);

                // is the current node a dummy? (fillcolor == black ?)
                boolean isDummy = node
                        .getInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.RED) == 0
                        && node
                                .getInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                                        + Attribute.SEPARATOR
                                        + GraphicAttributeConstants.GREEN) == 0
                        && node
                                .getInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                                        + Attribute.SEPARATOR
                                        + GraphicAttributeConstants.BLUE) == 0;
                node.setBoolean(SugiyamaConstants.PATH_DUMMY, isDummy);
                if (isDummy) {
                    data.getDummyNodes().add(node);
                    AbstractCyclicLevelingAlgorithm.setDummyShape(node);
                }
            }
        }
    }

    /**
     * Compares two nodes on their distance to (0,0).
     */
    class NodeComparator implements Comparator<Node> {
        public int compare(Node node1, Node node2) {
            double x1 = node1.getDouble(GraphicAttributeConstants.COORDX_PATH);
            double y1 = node1.getDouble(GraphicAttributeConstants.COORDY_PATH);
            double h1 = Math.hypot(x1, y1);

            double x2 = node2.getDouble(GraphicAttributeConstants.COORDX_PATH);
            double y2 = node2.getDouble(GraphicAttributeConstants.COORDY_PATH);
            double h2 = Math.hypot(x2, y2);

            if (h1 < h2)
                return -1;
            if (h1 > h2)
                return +1;
            return 0;
        }
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "CyclicBKAttributesCreator";
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
