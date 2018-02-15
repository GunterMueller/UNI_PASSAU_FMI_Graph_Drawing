// =============================================================================
//
//   AbstractLevellingAlgorithm.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractLevellingAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.levelling;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements the <code>Algorithm</code>-interface. It basically
 * extends the abstract class <code>AbstractAlgorithm</code>, including the
 * following features:
 * <ul>
 * <li>The method <code>addDummies</code> that inserts dummy-nodes into the
 * graph. An algorithm that implements the levelling-phase in the
 * sugiyama-algorithm may use this method to create the dummy-nodes or overwrite
 * it.
 * </ul>
 * 
 * @author Ferdinand H&uuml;bner
 */
public abstract class AbstractLevellingAlgorithm extends AbstractAlgorithm {

    /**
     * Reset the internal structure of this algorithm
     */
    @Override
    public void reset() {
        this.graph = null;
        this.parameters = null;
    }

    /**
     * This method creates dummy-nodes for each edge that connects two nodes
     * over more than one level in the graph
     * 
     * @param data
     *            The <code>SugiyamaData</code>-bean that stores neccessary
     *            information
     * @return Returns a <code>HashSet</code> of the inserted dummy-nodes
     */
    protected HashSet<Node> addDummies(SugiyamaData data) {

        HashSet<Node> dummies = data.getDummyNodes();
        NodeLayers layers = data.getLayers();
        Iterator<Node> nodeIterator;
        Iterator<Edge> edgeIterator;
        Node source;
        Node target;
        Node dummy;
        Edge edge;
        List<Edge> outEdges;

        // Check each layer for edges that go beyond one layer
        for (int i = 0; i < layers.getNumberOfLayers(); i++) {
            // Check each node on this layer
            nodeIterator = layers.getLayer(i).iterator();

            while (nodeIterator.hasNext()) {
                source = nodeIterator.next();

                // Put all the edges in a temporal set. We cannot iterate
                // directly over the collection of DirectedOutEdges, as
                // this collection will very likely change when a dummy-node
                // gets inserted, so a ConcurrentModificationException
                // would be thrown
                outEdges = new LinkedList<Edge>(source.getAllOutEdges());
                edgeIterator = outEdges.iterator();

                // Check each outgoing edge
                while (edgeIterator.hasNext()) {

                    edge = edgeIterator.next();
                    target = edge.getTarget();

                    // This edge connects two nodes over more than one layer,
                    // so a dummy-node hast to be inserted and the edge updated
                    if (layers.getLayer(i + 1) != null
                            && !layers.getLayer(i + 1).contains(target)) {

                        dummy = graph.addNode();
                        try {
                            dummy.getAttribute("graphics");
                        } catch (Exception e) {
                            dummy.addAttribute(new NodeGraphicAttribute(), "");
                        }
                        AbstractCyclicLevelingAlgorithm.setDummyShape(dummy);
                        dummies.add(dummy);

                        // Put the dummy on the next layer
                        if (layers.getLayer(i + 1) == null) {
                            layers.addLayer();
                        }
                        layers.getLayer(i + 1).add(dummy);

                        // Update the original edge and add a new edge from
                        // the dummy to the original target
                        edge.setTarget(dummy);
                        Edge newEdge = graph.addEdge(dummy, target, true);
                        try {
                            newEdge.getAttribute("graphics");
                        } catch (Exception e) {
                            newEdge
                                    .addAttribute(
                                            new org.graffiti.graphics.EdgeGraphicAttribute(),
                                            "");
                        }

                        dummy.addAttribute(new HashMapAttribute(
                                SugiyamaConstants.PATH_SUGIYAMA), "");
                    }
                }
            }
        }

        // mark all dummies as dummies in their attributes
        for (Node n : graph.getNodes()) {
            n.setBoolean(SugiyamaConstants.PATH_DUMMY, dummies.contains(n));
        }

        return dummies;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
