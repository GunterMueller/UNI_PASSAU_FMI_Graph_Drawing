// =============================================================================
//
//   TopoSort.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TopoSort.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.decycling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements a simple topoSort-algorithm. It doesn't implement any
 * phase in the sugiyama-algorithm; it's only a helper-algorithm for algorithms
 * that need a topological order or need to test for cycles in the graph.
 * <p>
 * Please note that this algorithm throws a RuntimeException if the graph does
 * contain a cycle. This is intentional, so you have to catch this exception if
 * you use this algorithm.
 * 
 * @author Ferdinand H&uuml;bner
 * 
 */
public class TopoSort extends AbstractAlgorithm implements SugiyamaAlgorithm {

    /** SugiyamaData-bean */
    private SugiyamaData data;

    /** This algorithm's name */
    private final String ALGORITHM_NAME = "TopoSort";

    /**
     * Default constructor
     */
    public TopoSort() {

    }

    public String getName() {
        return ALGORITHM_NAME;
    }

    public void setData(SugiyamaData d) {
        data = d;
    }

    public SugiyamaData getData() {
        return this.data;
    }

    /**
     * Compute a topological numbering in the graph
     */
    public void execute() {

        this.init();
        HashSet<Node> processed = new HashSet<Node>();
        Iterator<Node> nodeIter = graph.getNodesIterator();
        Collection<Edge> outEdges;
        ArrayList<Node> zeroPreds = new ArrayList<Node>();
        Node tmp;
        Node neighbor;
        int topo = 0;

        while (nodeIter.hasNext()) {
            tmp = nodeIter.next();
            tmp.setInteger(SugiyamaConstants.PATH_TOPOPREDS, tmp.getInDegree());
        }

        nodeIter = graph.getNodesIterator();
        while (nodeIter.hasNext()) {
            tmp = nodeIter.next();
            if (tmp.getInteger(SugiyamaConstants.PATH_TOPOPREDS) == 0) {
                zeroPreds.add(tmp);
            }
        }

        if (zeroPreds.isEmpty())
            throw new RuntimeException();

        while (!zeroPreds.isEmpty()) {

            topo++;
            tmp = zeroPreds.remove(0);
            outEdges = tmp.getAllOutEdges();
            for (Edge outEdge : outEdges) {
                neighbor = outEdge.getTarget();
                neighbor.setInteger(SugiyamaConstants.PATH_TOPOPREDS, neighbor
                        .getInteger(SugiyamaConstants.PATH_TOPOPREDS) - 1);
                if (neighbor.getInteger(SugiyamaConstants.PATH_TOPOPREDS) == 0) {
                    zeroPreds.add(neighbor);
                }
            }
            try {
                tmp.setInteger(SugiyamaConstants.PATH_TOPO, topo);
            } catch (AttributeNotFoundException anfe) {
                tmp.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_TOPO, topo);
            }
            processed.add(tmp);
        }
        if (processed.size() != graph.getNodes().size())
            throw new RuntimeException("Did not process all nodes!");

    }

    /**
     * Init the graph to start topoSort
     */
    private void init() {

        Iterator<Node> nodeIterator = graph.getNodesIterator();
        Node tmp;
        while (nodeIterator.hasNext()) {
            tmp = nodeIterator.next();
            try {
                tmp.setInteger(SugiyamaConstants.PATH_TOPOPREDS, 0);
            } catch (AttributeNotFoundException anfe) {
                tmp.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_TOPOPREDS, 0);
            }
        }

    }

    public boolean supportsBigNodes() {
        return true;
    }

    public boolean supportsConstraints() {
        return true;
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
