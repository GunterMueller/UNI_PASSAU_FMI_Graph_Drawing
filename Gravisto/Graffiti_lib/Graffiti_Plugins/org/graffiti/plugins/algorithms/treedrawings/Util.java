// =============================================================================
//
//   Util.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Util.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings;

import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.Port;
import org.graffiti.graphics.PortsAttribute;

/**
 * Several methods that did not fit anywhere else ;-)
 * 
 * @author Andreas
 * @version $Revision: 5766 $ $Date: 2006-11-07 18:34:38 +0100 (Di, 07 Nov 2006)
 *          $
 */
public class Util {
    /**
     * Checks wether the <code>node</code> is a HelperNode.
     * 
     * @param node
     * @return true if the given node is a HelperNode, false otherwise.
     */
    public static boolean isHelperNode(Node node) {
        try {
            return node.getBoolean("HelperNode");
        } catch (AttributeNotFoundException a) {
            return false;
        }
    }

    /**
     * Add a HelperNode to the given <code>graph</code> (adds all necessary
     * Attributes).
     * 
     * @param graph
     * @return the new Node created and inserted into the <code>graph</code>.
     */
    public static Node addHelperNode(Graph graph) {
        Node newHelperNode = graph.addNode();
        DimensionAttribute daHelper = (DimensionAttribute) newHelperNode
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.DIMENSION);
        daHelper.setDimension(3.0, 3.0);
        newHelperNode.setBoolean("HelperNode", true);
        return newHelperNode;
    }

    /**
     * Add a helper node to the given graph and position it right outside the
     * bottom right quadrant of the given Node (location).
     * 
     * @param graph
     * @param location
     * @return the HelperNode inserted
     */
    public static Node addHelperNode(Graph graph, Node location) {
        Node newHelperNode = graph.addNode();

        // Check if graphics attribute is there (important for command-line)...
        try {
            newHelperNode.getAttribute(GraphicAttributeConstants.GRAPHICS);
        } catch (AttributeNotFoundException e) {
            newHelperNode.addAttribute(new NodeGraphicAttribute(), "");
        }

        DimensionAttribute daHelper = (DimensionAttribute) newHelperNode
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.DIMENSION);
        daHelper.setDimension(3.0, 3.0);

        CoordinateAttribute caLocation = (CoordinateAttribute) location
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);

        DimensionAttribute daLocation = (DimensionAttribute) location
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.DIMENSION);

        CoordinateAttribute caHelper = (CoordinateAttribute) newHelperNode
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        caHelper.setX(caLocation.getX() + daLocation.getWidth() / 2.0 + 5);
        caHelper.setY(caLocation.getY() + daLocation.getHeight() / 2.0 + 5);

        newHelperNode.setBoolean("HelperNode", true);
        return newHelperNode;
    }

    /**
     * Get an Attribute of <code>ge</code> with the given path <code>path</code>
     * and also catch the AttributeNotFoundException if it is not there.
     * 
     * @param ge
     * @param path
     * @return the Attribute if found, null otherwise.
     */
    public static Attribute getAttribute(GraphElement ge, String path) {
        try {
            return ge.getAttribute(path);
        } catch (AttributeNotFoundException a) {
            return null;
        }
    }

    /**
     * Delete all port information for ingoing ports at the specified Node
     * <code>node</code>.
     * 
     * @param node
     */
    public static void resetIngoingPorts(Node node) {
        PortsAttribute portsAttr = (PortsAttribute) node
                .getAttribute("graphics.ports");
        LinkedList<Port> portsIngoing = new LinkedList<Port>();
        portsAttr.setIngoingPorts(portsIngoing);
        LinkedList<Port> portsCommon = new LinkedList<Port>();
        portsAttr.setCommonPorts(portsCommon);
    }

    /**
     * Delete all port information for outgoing ports at the specified Node
     * <code>node</code>.
     * 
     * @param node
     */
    public static void resetOutgoingPorts(Node node) {
        PortsAttribute portsAttr = (PortsAttribute) node
                .getAttribute("graphics.ports");
        LinkedList<Port> portsOutgoing = new LinkedList<Port>();
        portsAttr.setOutgoingPorts(portsOutgoing);
        LinkedList<Port> portsCommon = new LinkedList<Port>();
        portsAttr.setCommonPorts(portsCommon);
    }

    /**
     * Find all nodes with an out-degree of more than <code>degree</code>
     * 
     * @param subtreeRoot
     *            the root of the tree that is investigated.
     * @param degree
     *            threshold specified
     * @param foundNodes
     *            all the nodes found
     */
    public synchronized static void findNodesWithOutDegreeMoreThan(
            Node subtreeRoot, int degree, List<Node> foundNodes) {
        int rootDegree = subtreeRoot.getOutDegree();

        if (rootDegree > degree) {
            foundNodes.add(subtreeRoot);
        }

        for (Node currentChild : subtreeRoot.getAllOutNeighbors()) {
            findNodesWithOutDegreeMoreThan(currentChild, degree, foundNodes);
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
