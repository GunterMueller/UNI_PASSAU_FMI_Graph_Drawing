// =============================================================================
//
//   AbstractThreadSaveLayoutAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractThreadSaveLayoutAlgorithm.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.algorithm;

import java.util.Vector;

import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * Basis for Thread Safe (layout) Plugins and communication to the MainFrame
 */
public abstract class AbstractThreadSaveLayoutAlgorithm extends
        AbstractAlgorithm {

    /** DOCUMENT ME! */
    protected boolean redrawNeeded = false;

    /** DOCUMENT ME! */
    protected boolean settingsChanged = false; // if set to true

    /** DOCUMENT ME! */
    protected boolean threadSettingsChanged = false;

    /** DOCUMENT ME! */
    private Vector<Object> paramObjects;

    /** DOCUMENT ME! */
    private NodePosition[] nodePos; // cache for nodepositions

    /**
     * Sets or gets a Parameter Threadsafe for the Algorithm.
     * 
     * @param getParam
     *            If true, parameter with index "index" is returned, if false,
     *            the parameter is set (stored).
     * @param index
     *            Index of Parameter to set or get
     * @param setValue
     *            Object to store, if getParam is false
     * 
     * @return Desired Parameter object or Paramter that was just stored.
     */
    public synchronized Object getOrSetParam(boolean getParam, int index,
            Object setValue) {
        if (paramObjects == null) {
            paramObjects = new Vector<Object>();
        }

        if (getParam) {
            try {
                return paramObjects.get(index);
            } catch (Exception e) {
                return null;
            }
        } else {
            threadSettingsChanged = true;

            if (paramObjects.size() <= index) {
                paramObjects.setSize(index + 1);
            }

            paramObjects.setElementAt(setValue, index);

            // paramObjects.add(index, setValue);
            return setValue;
        }
    }

    /**
     * Stores a node position (from the layout thread) or sets the node position
     * of a node to a stored position (from the FrameMain thread). For
     * synchronizing only one method stores or sets coordinates.
     * 
     * @param setAllNodePositions
     *            If true all Nodepositions are actually modified.
     * @param index
     *            Index of node
     * @param n
     *            Node which should be modified or node that corespondends to
     *            the given coordinates.
     * @param x
     *            Node position (X) for storage.
     * @param y
     *            Node position (Y) for storage.
     */
    public synchronized void storeOrSetNodePosition(
            boolean setAllNodePositions, int index, Node n, double x, double y) {
        try {
            if (nodePos == null) {
                nodePos = new NodePosition[0];
            }

            if (setAllNodePositions) {
                if (redrawNeeded) {
                    redrawNeeded = false;

                    for (NodePosition np : nodePos) {
                        if (np != null) {
                            CoordinateAttribute cn = (CoordinateAttribute) np.n
                                    .getAttribute(GraphicAttributeConstants.COORD_PATH);

                            cn.setX(np.x);
                            cn.setY(np.y);
                        }
                    }
                }
            } else {
                if ((nodePos == null) || (index >= nodePos.length)) {
                    // Enlarge nodePos array
                    NodePosition[] nodePosNew = new NodePosition[index + 1];

                    System.arraycopy(nodePos, 0, nodePosNew, 0, nodePos.length);
                    nodePos = nodePosNew;
                }

                nodePos[index] = new NodePosition(n, x, y);
                redrawNeeded = true;
            }
        } catch (Exception e) {
            System.err.println("Exception while updating node positions.");
            System.err.println("Error is ignored.");
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
