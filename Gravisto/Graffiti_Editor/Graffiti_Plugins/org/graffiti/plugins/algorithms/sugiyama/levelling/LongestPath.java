// =============================================================================
//
//   LongestPath.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LongestPath.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.levelling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements the longest path algorithm to create the layers in a
 * graph.
 * 
 * The attached graph has to be acyclic and directed.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class LongestPath extends AbstractLevellingAlgorithm implements
        LevellingAlgorithm {

    private final String NO_DECYCLING_DONE = "This graph has not been decycled"
            + " yet. Won't try to level a (possibly) cyclic graph!";

    private final String ALGORITHM_NAME = "LongestPath";

    /** The layers of the graph */
    private NodeLayers layers;

    /** Data-bean */
    private SugiyamaData data;

    /**
     * Getter-method to access the <code>SugiyamaData</code>-bean
     * 
     * @return Returns the attached <code>SugiyamaData</code>-bean
     */
    public SugiyamaData getData() {
        return this.data;
    }

    /**
     * Setter-method to store the <code>SugiyamaData</code>-bean.
     * 
     * @param theData
     *            the <code>SugiyamaData</code>-bean
     */
    public void setData(SugiyamaData theData) {
        this.data = theData;
        layers = data.getLayers();
    }

    /**
     * Accessor for the algorithm's name
     * 
     * @return Returns the name of this algorithm
     */
    public String getName() {
        return ALGORITHM_NAME;
    }

    /**
     * This method checks if all mandatory preconditions for executing the
     * algorithm are met
     */
    @Override
    public void check() throws PreconditionException {
        try {
            graph.getBoolean(SugiyamaConstants.PATH_HASBEENDECYCLED);
        } catch (AttributeNotFoundException anfe) {
            throw new PreconditionException(NO_DECYCLING_DONE);
        }
    }

    /**
     * Execute the algorithm - Create the layers and add dummy-nodes
     */
    public void execute() {
        try {
            this.check();
        } catch (PreconditionException pce) {
            throw new RuntimeException(NO_DECYCLING_DONE);
        }
        graph.getListenerManager().transactionStarted(this);
        createLayers();
        HashSet<Node> dummies;
        dummies = addDummies(data);
        data.setDummyNodes(dummies);
        // add level-attribute to the nodes
        ArrayList<Node> current;
        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            current = data.getLayers().getLayer(i);
            for (int j = 0; j < current.size(); j++) {
                try {
                    current.get(j).addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                            SugiyamaConstants.SUBPATH_LEVEL, i);
                } catch (AttributeExistsException aee) {
                    current.get(j).setInteger(SugiyamaConstants.PATH_LEVEL, i);
                }
            }
        }
        graph.getListenerManager().transactionFinished(this);
        graph.getListenerManager().transactionStarted(this);
        for (Node n : data.getDummyNodes()) {
            try {
                n.getInteger(SugiyamaConstants.PATH_LEVEL);
            } catch (Exception e) {
                for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
                    if (data.getLayers().getLayer(i).contains(n)) {
                        n.setBoolean(SugiyamaConstants.PATH_DUMMY, true);
                        n.setInteger(SugiyamaConstants.PATH_LEVEL, i);
                        break;
                    }
                }
                System.err.println("Can't find the dummy anywhere!");
            }
        }
        graph.getListenerManager().transactionFinished(this);

    }

    /**
     * This method is used to create the layers in the graph.
     * 
     * <ul>
     * <li>Search nodes that don't have incoming edges an place them on the
     * current layer
     * <li>Remove all outgoing edges of these nodes
     * <li>Iterate until there are no more remaining nodes
     * </ul>
     * 
     */
    private void createLayers() {

        Node tmpNode;
        Node neighbor;
        layers.addLayer();
        ArrayList<Node> remainingNodes = new ArrayList<Node>();
        Iterator<Node> nodeIterator = graph.getNodesIterator();
        int currentLayer = 0;
        int nodesLeft;
        Iterator<Edge> edgeIterator;
        Edge tmpEdge;

        // Place all nodes with initial indegree 0 on the first layer
        // Such a node exists, as the graph is acyclic
        while (nodeIterator.hasNext()) {
            tmpNode = nodeIterator.next();
            tmpNode.setInteger(SugiyamaConstants.PATH_INDEGREE, tmpNode
                    .getInDegree());
            if (tmpNode.getInteger(SugiyamaConstants.PATH_INDEGREE) == 0) {
                layers.getLayer(currentLayer).add(tmpNode);
            } else {
                remainingNodes.add(tmpNode);
            }
        }

        // "Remove" all outgoing edges from the nodes. That means: Get all
        // out-edges, for each out-edge: substract one from the target's
        // in-degree. There could be multiple edges from one node to another!
        nodeIterator = layers.getLayer(currentLayer).iterator();
        while (nodeIterator.hasNext()) {
            tmpNode = nodeIterator.next();

            edgeIterator = tmpNode.getDirectedOutEdgesIterator();
            while (edgeIterator.hasNext()) {
                tmpEdge = edgeIterator.next();
                neighbor = tmpEdge.getTarget();
                neighbor.setInteger(SugiyamaConstants.PATH_INDEGREE, neighbor
                        .getInteger(SugiyamaConstants.PATH_INDEGREE) - 1);
            }
        }
        nodesLeft = remainingNodes.size();
        while (!remainingNodes.isEmpty()) {
            layers.addLayer();
            currentLayer++;
            nodeIterator = remainingNodes.iterator();
            // Place all nodes with in-degree 0 on the current layer
            while (nodeIterator.hasNext()) {
                tmpNode = nodeIterator.next();
                if (tmpNode.getInteger(SugiyamaConstants.PATH_INDEGREE) == 0) {
                    layers.getLayer(currentLayer).add(tmpNode);
                }
            }
            remainingNodes.removeAll(layers.getLayer(currentLayer));

            // we don't want to run in an endless loop
            if (remainingNodes.size() == nodesLeft)
                throw new RuntimeException(
                        SugiyamaConstants.ERROR_INFINITE_LOOP);

            // "Remove" all outgoing edges from the nodes. That means: Get all
            // out-edges, for each out-edge: substract one from the target's
            // in-degree. There could be multiple edges from one node to
            // another!
            nodesLeft = remainingNodes.size();
            nodeIterator = layers.getLayer(currentLayer).iterator();
            while (nodeIterator.hasNext()) {
                tmpNode = nodeIterator.next();

                edgeIterator = tmpNode.getDirectedOutEdgesIterator();
                while (edgeIterator.hasNext()) {
                    tmpEdge = edgeIterator.next();
                    neighbor = tmpEdge.getTarget();
                    neighbor
                            .setInteger(
                                    SugiyamaConstants.PATH_INDEGREE,
                                    neighbor
                                            .getInteger(SugiyamaConstants.PATH_INDEGREE) - 1);
                }
            }
        }
    }

    @Override
    public boolean supportsAnimation() {
        return false;
    }

    @Override
    public Animation getAnimation() {
        throw new UnsupportedOperationException();
    }

    /**
     * Reset the internal state of the algorithm
     */
    @Override
    public void reset() {
        super.reset();
        graph = null;
        layers = null;
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
