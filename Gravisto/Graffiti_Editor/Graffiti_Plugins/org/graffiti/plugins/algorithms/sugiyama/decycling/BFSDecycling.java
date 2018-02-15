// =============================================================================
//
//   BFSDecycling.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BFSDecycling.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.decycling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.util.EdgeUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements decycling of a dag using BFS
 * 
 * @author Ferdinand H&uuml;bner
 */
public class BFSDecycling extends AbstractAlgorithm implements
        DecyclingAlgorithm {
    private SugiyamaData data;

    private final String ALGORITHM_NAME = "BFSDecycling";

    private boolean deleteEdges = false;

    /**
     * @see org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#getData()
     */
    public void setData(SugiyamaData d) {
        data = d;
    }

    /**
     * @see org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#getData()
     */
    public SugiyamaData getData() {
        return this.data;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return this.ALGORITHM_NAME;
    }

    /**
     * Execute the algorithm - run BFS (from a selected Node, if one has been
     * selected by the user) and reverse backwards-edges
     */
    public void execute() {
        bfsInit();
        ArrayList<Node> todo = new ArrayList<Node>(graph.getNodes());
        BFSComparator comp = new BFSComparator();
        Collections.sort(todo, comp);
        HashSet<Node> processed = new HashSet<Node>();
        Iterator<Node> neighbors;
        Node tmp;
        Node neighbor;
        int bfsNum = 0;
        graph.getListenerManager().transactionStarted(this);

        // run bfs
        while (!todo.isEmpty()) {
            if (data.getStartNode() != null && bfsNum == 0) {
                tmp = data.getStartNode();
                todo.remove(tmp);
                System.out.println("Starting with selected node");
            } else {
                tmp = todo.remove(0);
            }

            if (tmp.getInteger(SugiyamaConstants.PATH_BFSNUM) == Integer.MAX_VALUE) {
                bfsNum++;
                tmp.setInteger(SugiyamaConstants.PATH_BFSNUM, bfsNum);
            }
            neighbors = tmp.getOutNeighborsIterator();
            while (neighbors.hasNext()) {
                neighbor = neighbors.next();
                if (!processed.contains(neighbor)) {
                    bfsNum++;
                    neighbor.setInteger(SugiyamaConstants.PATH_BFSNUM, bfsNum);
                    processed.add(neighbor);
                }
            }
            processed.add(tmp);
            Collections.sort(todo, comp);
        }

        // find backwards-edges
        Iterator<Edge> edges = graph.getEdgesIterator();
        Edge edge;
        Node source;
        Node target;
        while (edges.hasNext()) {
            edge = edges.next();
            source = edge.getSource();
            target = edge.getTarget();
            if (source.getInteger(SugiyamaConstants.PATH_BFSNUM) > target
                    .getInteger(SugiyamaConstants.PATH_BFSNUM)) {
                if (deleteEdges) {
                    data.getDeletedEdges().add(edge);
                } else {
                    data.getReversedEdges().add(edge);
                }
            }
        }

        // reverse/delete backwards-edges
        if (deleteEdges) {
            edges = data.getDeletedEdges().iterator();
        } else {
            edges = data.getReversedEdges().iterator();
        }
        while (edges.hasNext()) {
            if (deleteEdges) {
                graph.deleteEdge(edges.next());
            } else {
                edges.next().reverse();
            }
        }
        // set the hasbeendecycled-bit
        try {
            graph.setBoolean(SugiyamaConstants.PATH_HASBEENDECYCLED, true);
        } catch (AttributeNotFoundException anfe) {
            graph.addBoolean(SugiyamaConstants.PATH_SUGIYAMA,
                    SugiyamaConstants.SUBPATH_HASBEENDECYCLED, true);
        }
        graph.getListenerManager().transactionFinished(this);

    }

    /**
     * Re-reverse backwards-edges
     */
    public void undo() {
        data.setGraph(this.graph);
        EdgeUtil.reverseBendedEdge(data);
    }

    /**
     * Initialize BFS - Set bfsNum to Integer.MAX_VALUE
     */
    private void bfsInit() {
        Iterator<Node> nodeIter = graph.getNodesIterator();
        Node tmp;
        while (nodeIter.hasNext()) {
            tmp = nodeIter.next();
            try {
                tmp
                        .setInteger(SugiyamaConstants.PATH_BFSNUM,
                                Integer.MAX_VALUE);
            } catch (AttributeNotFoundException anfe) {
                tmp.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_BFSNUM, Integer.MAX_VALUE);
            }
        }
    }

    /**
     * Comparator to sort a collection of nodes according to their bfs-number
     * 
     * @author Ferdinand H&uuml;bner
     */
    private class BFSComparator implements Comparator<Node> {
        public int compare(Node a, Node b) {
            int dfsA;
            int dfsB;

            dfsA = a.getInteger(SugiyamaConstants.PATH_BFSNUM);
            dfsB = b.getInteger(SugiyamaConstants.PATH_BFSNUM);

            if (dfsA < dfsB)
                return -1;
            else if (dfsA > dfsB)
                return 1;
            else
                return 0;
        }
    }

    /**
     * Reset the internal state of the algorithm
     */
    @Override
    public void reset() {
        super.reset();
        graph = null;
        deleteEdges = false;
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

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        BooleanParameter p = new BooleanParameter(false, "Delete edges",
                "Delete Edges instead of reversing them");
        return new Parameter[] { p };
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] p) {
        deleteEdges = ((BooleanParameter) p[0]).getBoolean();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
