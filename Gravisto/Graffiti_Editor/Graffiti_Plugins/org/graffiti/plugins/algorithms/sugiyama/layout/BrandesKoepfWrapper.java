// =============================================================================
//
//   BrandesKoepfWrapper.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BrandesKoepfWrapper.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.brandeskoepf.BKLayoutAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.algorithms.sugiyama.util.XPosComparator;

/**
 * This class acts as a wrapper to a Brandes/Koepf-implementation to compute
 * real x- and y-coordinates to the nodes in a graph.<br>
 * The underlying implementation of the Brandes/Koepf-algorithm is considered
 * buggy.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class BrandesKoepfWrapper extends AbstractAlgorithm implements
        LayoutAlgorithm {

    private BKLayoutAlgorithm bkalgo;

    private final String ALGORITHM_NAME = "Brandes/Koepf-Wrapper (buggy)";

    private SugiyamaData data;

    public BrandesKoepfWrapper() {
        bkalgo = new BKLayoutAlgorithm();
    }

    public boolean supportsArbitraryXPos() {
        return false;
    }

    @Override
    public void attach(Graph g) {
        bkalgo.attach(g);
        graph = g;
        bkalgo.dontDeleteGraphElements();
    }

    public String getName() {
        return ALGORITHM_NAME;
    }

    public SugiyamaData getData() {
        return data;
    }

    public void setData(SugiyamaData theData) {
        data = theData;
    }

    @Override
    public void check() throws PreconditionException {
        this.addLabels();
        bkalgo.check();
    }

    public void execute() {
        bkalgo.execute();
    }

    private void addLabels() {

        NodeLayers layers = data.getLayers();

        Node tmp;

        // Add the level-attribute
        for (int i = 0; i < layers.getNumberOfLayers(); i++) {
            for (int j = 0; j < layers.getLayer(i).size(); j++) {
                tmp = layers.getLayer(i).get(j);
                try {
                    tmp.setInteger(SugiyamaConstants.PATH_BK_LEVEL, i);
                } catch (AttributeNotFoundException anfe) {
                    tmp.addInteger("graphics", "level", i);
                }
            }
        }

        // Add the order-attribute - if there are "gaps" on a level, the order
        // on this level has to be adjusted, otherwise the bk-implementation
        // won't work:
        // build an array for each level, sort the nodes on this level
        // according to their sugiyama.xpos
        // their graphics.order for the bk-implementation is their index
        // in the sorted array; a sugiyama-xpos of 3,7,24 will result
        // in a graphics.order of 0,1,2 and the bk-implementation can
        // handle this level
        Iterator<Node> nodeIter = graph.getNodesIterator();

        for (int i = 0; i < layers.getNumberOfLayers(); i++) {
            ArrayList<Node> order = new ArrayList<Node>();
            for (int j = 0; j < layers.getLayer(i).size(); j++) {
                order.add(layers.getLayer(i).get(j));
            }
            Collections.sort(order, new XPosComparator());
            for (int j = 0; j < order.size(); j++) {
                try {
                    order.get(j).setInteger("graphics.order", j);
                } catch (AttributeNotFoundException anfe) {
                    order.get(j).addInteger("graphics", "order", j);
                }
            }
        }

        // Add the dummy-attributes
        int dummy;
        nodeIter = graph.getNodesIterator();
        while (nodeIter.hasNext()) {
            tmp = nodeIter.next();
            if (data.getDummyNodes().contains(tmp)) {
                dummy = 1;
            } else {
                dummy = 0;
            }
            try {
                tmp.setInteger(SugiyamaConstants.PATH_BK_DUMMY, dummy);
            } catch (AttributeNotFoundException anfe) {
                tmp.addInteger("graphics", "dummy", dummy);
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        bkalgo.reset();
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
