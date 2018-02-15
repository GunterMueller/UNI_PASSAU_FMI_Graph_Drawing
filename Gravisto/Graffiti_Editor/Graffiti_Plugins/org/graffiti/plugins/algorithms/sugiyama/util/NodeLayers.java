// =============================================================================
//
//   NodeLayers.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeLayers.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.ArrayList;

import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;

/**
 * This class represents the layers (or levels) of a graph. Each layer is an
 * <code>ArrayList</code> of Nodes on the current layer.
 */
public class NodeLayers {

    /** ArrayList of ArrayList of Node - An ArrayList that stores each layer */
    private ArrayList<ArrayList<Node>> layers;

    /** Y-Coordinates of the layers */
    private ArrayList<Double> layerCoordinates;

    /**
     * Default constructor for a <code>NodeLayer</code>.
     * 
     * Create a new ArrayList, set number of layers to 0
     */
    public NodeLayers() {
        super();
        layers = new ArrayList<ArrayList<Node>>();
        layerCoordinates = new ArrayList<Double>();
    }

    /**
     * Accessor to access the number of layers
     * 
     * @return The number of layers
     */
    public int getNumberOfLayers() {
        return layers.size();
    }

    /**
     * This method is used to access a certain layer
     * 
     * @param layerNum
     *            The "number" of the layer to access
     * @return Returns <code>Null</code> if no such layer exists, the layer
     *         itself otherwise.
     */
    public ArrayList<Node> getLayer(int layerNum) {
        if (layerNum >= getNumberOfLayers()) {
            // insert empty layers
            int toAdd = layerNum - getNumberOfLayers();
            for (int i = 0; i <= toAdd; i++) {
                layers.add(new ArrayList<Node>());
            }

            return layers.get(layerNum);
        } else
            return layers.get(layerNum);
    }

    /**
     * Append a new layer
     */
    public void addLayer() {
        layers.add(new ArrayList<Node>());
    }

    @Override
    public NodeLayers clone() {
        NodeLayers result = new NodeLayers();
        for (ArrayList<Node> layer : layers) {
            ArrayList<Node> newLayer = new ArrayList<Node>(layer);
            result.layers.add(newLayer);
        }
        return result;
    }

    /**
     * Returns the Y-coordinates of the layers.
     * 
     * @return the layerCoordinates.
     */
    public ArrayList<Double> getLayerCoordinates() {
        return layerCoordinates;
    }

    /**
     * Returns the Y-coordinate of the given layer.
     * 
     * @param x
     *            Number of the layer
     * @return Y-coordinate of the given layer.
     */
    public double getLayerCoordinate(int x) {
        return layerCoordinates.get(x);
    }

    /**
     * Updates the Y-coordinates of the layers.
     */
    public void updateLayerCoordinates() {
        layerCoordinates.clear();

        for (int nextLayer = 0; nextLayer < layers.size(); nextLayer++) {
            CoordinateAttribute co = (CoordinateAttribute) layers
                    .get(nextLayer).get(0).getAttribute("graphics.coordinate");
            layerCoordinates.add(co.getY());
        }
    }

    /**
     * Get the layer closest to the given y-coordinate
     * 
     * @param yCoordinate
     * @return number of the closest layer
     */
    public int getClosestLayer(double yCoordinate) {
        int result = 0;

        double diff = Double.MAX_VALUE;

        for (int layer = 0; layer < layerCoordinates.size(); layer++) {
            if (diff > Math.abs(layerCoordinates.get(layer) - yCoordinate)) {
                diff = Math.abs(layerCoordinates.get(layer) - yCoordinate);
                result = layer;
            } else
                return result;
        }
        return result;
    }

    /**
     * This method is used by the framework to normalize the xpos attribute of
     * each node to be equal to the index of the node in the array representing
     * a level of the graph.
     */
    public void normalizeLayers() {
        for (int i = 0; i < layers.size(); i++) {
            for (int j = 0; j < layers.get(i).size(); j++) {
                layers.get(i).get(j).setDouble(SugiyamaConstants.PATH_XPOS, j);
            }
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
