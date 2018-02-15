// =============================================================================
//
//   Rotationsystem.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.rotation;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.SDlayout.ObjectAttribute;

/**
 * This class calculates the rotationsystem for every node, which correspondes
 * to the drawing of the given graph.
 * 
 * @author Christina Ehrlinger
 * @version $Revision$ $Date$
 */
public class Rotationsystem extends AbstractAlgorithm {

    /**
     * This method checks, if the given graph is emtpy or null.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        // The graph is inherited from AbstractAlgorithm.
        if (graph == null) {
            errors.add("The graph instance may not be null.");
        } else if (graph.isEmpty()) {
            errors.add("The graph may not be emtpy.");
        }

        if (!errors.isEmpty()) {
            throw errors;
        }
    }

    /**
     * This method calculates the angle for every edge, so the rotation system
     * can be sorted.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    @Override
    public void execute() {
        List<Node> nodes = graph.getNodes();

        // Iteration on every node
        for (Iterator<Node> itNode = nodes.iterator(); itNode.hasNext();) {
            Node currentNode = itNode.next();
            boolean isSource = false;

            Collection<Edge> edges = currentNode.getEdges();
            List<Edge> rotation = new LinkedList<Edge>();

            // CoordinateAttribute for the current and the other node
            CoordinateAttribute currentCoord = (CoordinateAttribute) currentNode
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            CoordinateAttribute otherCoord;

            // Iteration on every edge of the current node
            for (Iterator<Edge> itEdge = edges.iterator(); itEdge.hasNext();) {
                Edge currentEdge = itEdge.next();

                EdgeGraphicAttribute edgeGraphic = (EdgeGraphicAttribute) currentEdge
                        .getAttribute(GraphicAttributeConstants.GRAPHICS);

                SortedCollectionAttribute bends = edgeGraphic.getBends();

                if (currentEdge.getSource().equals(currentNode)) {
                    isSource = true;
                    if (bends.getCollection().isEmpty()) {
                        otherCoord = (CoordinateAttribute) currentEdge
                                .getTarget().getAttribute(
                                        GraphicAttributeConstants.COORD_PATH);
                    } else {
                        // first bend
                        otherCoord = (CoordinateAttribute) bends
                                .getCollection().get("bend0");
                    }
                } else {
                    if (bends.getCollection().isEmpty()) {
                        otherCoord = (CoordinateAttribute) currentEdge
                                .getSource().getAttribute(
                                        GraphicAttributeConstants.COORD_PATH);
                    } else {
                        // last bend
                        otherCoord = (CoordinateAttribute) bends
                                .getCollection()
                                .get(concadenteString("bend",
                                        edgeGraphic.getNumberOfBends()));
                    }
                }

                // Calculation of the angle
                double deltaX = otherCoord.getX() - currentCoord.getX();
                double deltaY = otherCoord.getY() - currentCoord.getY();
                double angle = Math.toDegrees((Math.atan2(deltaY, deltaX)));

                if (isSource) {
                    currentEdge.setDouble("rotation.sourceAngle", angle);
                } else {
                    currentEdge.setDouble("rotation.targetAngle", angle);
                }

                rotation.add(currentEdge);
            }

            sortRotationsystemAtVertex(rotation);

            Attribute rotationAttribute = new ObjectAttribute("rotationsystem");
            rotationAttribute.setValue(rotation);
            currentNode.addAttribute(rotationAttribute, "");
        }
    }

    /**
     * method concadenates the given string and the given number to one string
     * 
     * @param string
     *            : first part
     * @param number
     *            : second part
     * @return the concadenated string
     */
    private String concadenteString(String string, int number) {
        String newString = string;
        number--;
        String numberString = ((Integer) number).toString();
        return newString + numberString;
    }

    /**
     * method sorts the given rotation system counterclockwise by the angle at
     * the attached node
     * 
     * @param rotation
     *            : the rotation system, which should be sorted
     */
    private void sortRotationsystemAtVertex(List<Edge> rotation) {

        Comparator<Edge> comparator = new Comparator<Edge>() {

            @Override
            public int compare(Edge o2, Edge o1) {
                if (o1.getSource().equals(o2.getSource())) {
                    return (int) (o1.getDouble("rotation.sourceAngle") - o2
                            .getDouble("rotation.sourceAngle"));
                } else if (o1.getSource().equals(o2.getTarget())) {
                    return (int) (o1.getDouble("rotation.sourceAngle") - o2
                            .getDouble("rotation.targetAngle"));
                } else if (o1.getTarget().equals(o2.getSource())) {
                    return (int) (o1.getDouble("rotation.targetAngle") - o2
                            .getDouble("rotation.sourceAngle"));
                } else {
                    return (int) (o1.getDouble("rotation.targetAngle") - o2
                            .getDouble("rotation.targetAngle"));
                }

            }
        };

        Collections.sort(rotation, comparator);
    }

    /**
     * method removes all additional attributes, this class has added to an edge
     * or a node. The attributes are the rotationsystem of every node and the
     * angles for every egde.
     */
    public void clearAttributes() {
        for (Node node : graph.getNodes()) {
            node.removeAttribute("rotationsystem");
        }
        for (Edge edge : graph.getEdges()) {
            edge.removeAttribute("rotation");
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    @Override
    public String getName() {
        return "Create Rotationsystem";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        clearAttributes();
        super.reset();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
