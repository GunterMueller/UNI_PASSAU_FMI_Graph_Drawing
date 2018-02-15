// =============================================================================
//
//   AbstractCyclicLevelingAlgorithm.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.levelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.CoordinatesUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.algorithms.sugiyama.util.XPosComparator;

/**
 * @author brunner, fueloep
 * @version $Revision$ $Date$
 */
public abstract class AbstractCyclicLevelingAlgorithm extends AbstractAlgorithm {

    protected int numberOfLevels;

    /** Bean to store the deleted edges of this phase */
    protected SugiyamaData data;

    public SugiyamaData getData() {
        return this.data;
    }

    public void setData(SugiyamaData data) {
        this.data = data;
    }

    protected abstract void levelNodes();

    protected void setNodeLevel(Node n, int level) {
        try {
            n.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                    SugiyamaConstants.SUBPATH_LEVEL, level);
        } catch (AttributeExistsException aee) {
            n.setInteger(SugiyamaConstants.PATH_LEVEL, level);
        }

    }

    protected void setNodeXPos(Node n, double xpos) {
        try {
            n.addDouble(SugiyamaConstants.PATH_SUGIYAMA,
                    SugiyamaConstants.SUBPATH_XPOS, xpos);
        } catch (AttributeExistsException aee) {
            n.setDouble(SugiyamaConstants.PATH_XPOS, xpos);
        }

    }

    protected int getNodeLevel(Node n) {
        return n.getInteger(SugiyamaConstants.PATH_LEVEL);
    }

    private int getNextLevel(int level) {
        if (level == numberOfLevels - 1)
            return 0;
        return level + 1;
    }

    public static void setDummyShape(Node dummy) {

        dummy.setString(GraphicAttributeConstants.SHAPE_PATH,
                GraphicAttributeConstants.CIRCLE_CLASSNAME);
        dummy.setDouble(GraphicAttributeConstants.DIMW_PATH, 10);
        dummy.setDouble(GraphicAttributeConstants.DIMH_PATH, 10);
        dummy.setInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                + Attribute.SEPARATOR + GraphicAttributeConstants.RED, 0);
        dummy.setInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                + Attribute.SEPARATOR + GraphicAttributeConstants.GREEN, 0);
        dummy.setInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                + Attribute.SEPARATOR + GraphicAttributeConstants.BLUE, 0);
    }

    private void addDummies(SugiyamaData data) {
        HashSet<Node> dummies = data.getDummyNodes();
        Collection<Edge> edges = new LinkedList<Edge>();
        edges.addAll(graph.getEdges());
        for (Edge e : edges) {
            Node source = e.getSource();
            Node target = e.getTarget();
            int sourceLevel = getNodeLevel(source);
            int targetLevel = getNodeLevel(target);
            if (sourceLevel + 1 == targetLevel) {
                continue;
            }
            if (sourceLevel == numberOfLevels - 1 && targetLevel == 0) {
                continue;
            }
            Node last = source;
            Node current = null;
            int level = getNextLevel(sourceLevel);
            while (level != targetLevel) {
                current = graph.addNode();
                try {
                    current.getAttribute("graphics");
                } catch (Exception ex) {
                    current.addAttribute(new NodeGraphicAttribute(), "");
                }
                setDummyShape(current);
                dummies.add(current);
                current.addAttribute(new HashMapAttribute(
                        SugiyamaConstants.PATH_SUGIYAMA), "");

                // set the dummy's level and xpos attributes
                setNodeLevel(current, level);
                ArrayList<Node> layer = data.getLayers().getLayer(level);
                layer.add(current);
                setNodeXPos(current, layer.size() - 1);

                if (level == getNextLevel(sourceLevel)) {
                    e.setTarget(current);
                } else {
                    graph.addEdge(last, current, true);
                }
                last = current;
                level = getNextLevel(level);
            }
            graph.addEdge(last, target, true);

        }
        data.setDummyNodes(dummies);

        // mark all dummies as dummies in their attributes
        for (Node n : graph.getNodes()) {
            n.setBoolean(SugiyamaConstants.PATH_DUMMY, dummies.contains(n));
        }
    }

    /**
     * Creates the NodeLayers data structure in the SugiyamaData object. Derived
     * from those arrays, each node gets an "xpos" attribute.
     */
    private void createLayer(SugiyamaData data) {
        NodeLayers layers = data.getLayers();
        for (int i = 0; i < numberOfLevels; i++) {
            layers.addLayer();
        }
        for (Node n : graph.getNodes()) {
            ArrayList<Node> layer = layers.getLayer(getNodeLevel(n));
            layer.add(n);
        }

        // sort nodes horizontally by their xpos attributes
        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            // if the nodes are already in the right order then the following
            // sorting run is linear (see documentation of Collections.sort)
            Collections
                    .sort(data.getLayers().getLayer(i), new XPosComparator());

            // rewrite all xpos attributes
            ArrayList<Node> layer = data.getLayers().getLayer(i);
            for (int x = 0; x < layer.size(); x++) {
                setNodeXPos(layer.get(x), x);
            }
        }
    }

    public void execute() {
        graph.getListenerManager().transactionStarted(this);

        // perform the actual level assignment
        levelNodes();

        // fill SugiyamaData.NodeLayers and derive each node's "xpos" from that
        createLayer(data);

        // insert dummy nodes where necessary
        addDummies(data);

        // update the nodes' coordinates according to "level" and "xpos"
        CoordinatesUtil.updateGraph(graph, data);

        graph.getListenerManager().transactionFinished(this);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
