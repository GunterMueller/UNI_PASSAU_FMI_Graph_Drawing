// =============================================================================
//
//   LayoutComposition.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * This is the class that should be extended by any other class that is supposed
 * to represent a layout.
 * 
 * @author Andreas
 * @version $Revision$ $Date$
 */
public abstract class LayoutComposition {

    /**
     * the value of the number that is used as the value of the attribute
     * "layout.orderNumber" at the given root Node
     */
    public static int orderSequenceNumber = 0;

    /**
     * Used to instantiate a new LayoutComposition. The information in
     * <code>root</code> is used in this case.
     * 
     * @param root
     *            the root Node of the new LayoutComposition
     * @param childCompositions
     *            the drawings of the children of <code>root</code>
     * @return the new LayoutComposition
     * @throws PreconditionException
     */
    public abstract LayoutComposition instance(Node root,
            List<LayoutComposition> childCompositions)
            throws PreconditionException;

    /**
     * Used to instantiate new LayoutCompositions. The information given is used
     * in this case. There may be several possible - different permutations of
     * the subtrees.
     * 
     * @param root
     *            the root Node of the new LayoutComposiiton
     * @param childCompositions
     *            the drawings of the children of <code>root</code>
     * @param horizontal
     *            this determines if the new LayoutComposition is laid out
     *            horizontally. It is laid out vertically otherwise.
     * @param nodeDistance
     *            the distance between the root and its children and between the
     *            children.
     * @param nodesWithDimensions
     *            determines whether the new LayoutComposition's Nodes have
     *            dimensions or they are reduced to points.
     * @return the list of new LayoutCompositions
     */
    public abstract List<LayoutComposition> instance(Node root,
            List<LayoutComposition> childCompositions, boolean horizontal,
            double nodeDistance, boolean nodesWithDimensions);

    /**
     * @return the root of this LayoutComposition
     */
    public abstract Node getRoot();

    /**
     * @return the width of the root of this LayoutComposition
     */
    public abstract double getRootWidth();

    /**
     * @return the height of the root of this LayoutComposition
     */
    public abstract double getRootHeight();

    /**
     * @return the width of this LayoutComposition
     */
    public abstract double getWidth();

    /**
     * @return the height of this LayoutComposition
     */
    public abstract double getHeight();

    /**
     * @return the distance between the root and the children and the children
     */
    public abstract double getNodeDistance();

    /**
     * Layout this LayoutComposition with the upper left corner at the specified
     * <code>position</code>
     * 
     * @param position
     */
    public void layout(Point2D position) {
        CoordinateAttribute ca = (CoordinateAttribute) this.getRoot()
                .getAttribute(
                        GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);

        ca.setX(position.getX() + this.getRelPositionOfRoot().getX());
        ca.setY(position.getY() + this.getRelPositionOfRoot().getY());

        if (this.getSubtrees().size() > 0) {
            Iterator<Point2D> subtreePositionsItr = this.getSubtreePositions()
                    .iterator();

            for (LayoutComposition childComposition : this.getSubtrees()) {
                Point2D subtreeRelativePosition = subtreePositionsItr.next();
                Point2D childCompositionTotalPosition = new Point2D.Double(
                        position.getX() + subtreeRelativePosition.getX(),
                        position.getY() + subtreeRelativePosition.getY());

                this.combine(position, childComposition,
                        childCompositionTotalPosition);

                childComposition.layout(childCompositionTotalPosition);
            }
        }
    }

    /**
     * This determines how the edge between this LayoutComposition and the
     * LayoutCompositions of the children are laid out.
     * 
     * @param rootCompositionTotalPosition
     * @param childComposition
     *            that the edge is being laid out to from the root of this
     *            LayoutComposition.
     * @param childCompositionTotalPosition
     */
    protected abstract void combine(Point2D rootCompositionTotalPosition,
            LayoutComposition childComposition,
            Point2D childCompositionTotalPosition);

    /**
     * Set the layout-Attributes for this LayoutComposition (including the ones
     * in the Nodes in the subtrees)
     * 
     * @param respectOrderNumber
     *            whether to respect the Attribute "layout.orderNumber" or not
     */
    public void setLayoutAttributes(boolean respectOrderNumber) {
        this.setLayoutAttributesLocal(LayoutComposition.orderSequenceNumber++);
        // TODO: See if respectOrderNumber option is really necessary.
        if (this.getSubtrees() != null) {

            if (respectOrderNumber) {
                TreeMap<Double, LayoutComposition> subtreesInCorrectOrder = new TreeMap<Double, LayoutComposition>();
                LinkedList<LayoutComposition> noOrderNumberFound = new LinkedList<LayoutComposition>();

                for (LayoutComposition currentSubtree : this.getSubtrees()) {
                    try {
                        double currentOrderNumber = currentSubtree.getRoot()
                                .getDouble("layout.orderNumber");
                        subtreesInCorrectOrder.put(currentOrderNumber,
                                currentSubtree);
                    } catch (AttributeNotFoundException a) {
                        noOrderNumberFound.addLast(currentSubtree);
                    }
                }

                for (LayoutComposition currentSubtree : subtreesInCorrectOrder
                        .values()) {
                    currentSubtree.setLayoutAttributes(true);
                }

                for (LayoutComposition currentSubtreeWithNoOrderNumber : noOrderNumberFound) {
                    currentSubtreeWithNoOrderNumber.setLayoutAttributes(true);
                }
            } else {
                for (LayoutComposition currentSubtree : this.getSubtrees()) {
                    currentSubtree.setLayoutAttributes(false);
                }
            }
        }
    }

    /**
     * This has to be implemented by the specific LayoutComposition to set the
     * layout-Attributes in its root Node.
     * 
     * @param orderNumber
     *            for the Attribute "layout.orderNumber" of the root of this
     *            LayoutComposition
     */
    protected abstract void setLayoutAttributesLocal(double orderNumber);

    /**
     * Returns the subtreePositions.
     * 
     * @return the subtreePositions.
     */
    public abstract List<Point2D> getSubtreePositions();

    /**
     * Returns the subtrees.
     * 
     * @return the subtrees.
     */
    public abstract List<LayoutComposition> getSubtrees();

    public abstract boolean isHorizontal();

    /**
     * Returns the relPositionOfRoot.
     * 
     * @return the relPositionOfRoot.
     */
    public abstract Point2D getRelPositionOfRoot();

    /**
     * A Composition dominates another Composition, iff its width and its height
     * are both greater or equal than the one it is compared with.
     * 
     * @param another
     *            another HVComposition that is compared with this.
     * @return whether this HVComposition dominates the given HVComposition
     *         (another)
     */
    public boolean dominates(LayoutComposition another) {
        if (another == null)
            return false;
        return this.getHeight() >= another.getHeight()
                && this.getWidth() >= another.getWidth();
    }

    /**
     * @return a short description of this HVComposition. Only the width and
     *         height is included.
     */
    @Override
    public String toString() {
        return "w x h: " + this.getWidth() + " x " + this.getHeight();
        // return showStructure();
    }

    /**
     * @return This return a description of the structure of this
     *         TipoverComposition. It includes the dimension of this
     *         TipoverComposition and the dimensions of all child
     *         TipoverCompositions and the way they are composed with each other
     *         (in a "vertical" or a "horizontal" way).
     */
    public String showStructure() {
        return "\n" + this.showStructure("", new Point2D.Double(0, 0));
    }

    /**
     * auxiliary method for <code>showStructure</code> that deals with indents
     * etc.
     * 
     * @param indent
     *            the current indent
     * @param position
     *            the position if the current subtree
     * @return a string that describes the structure of this LayoutComposition.
     */
    protected String showStructure(String indent, Point2D position) {
        String directionText = "";
        if (this.isHorizontal()) {
            directionText = "horizontal";
        } else {
            directionText = "vertical";
        }

        String descriptionText = this.getClass().getSimpleName();

        try {
            descriptionText += ", label: "
                    + ((NodeLabelAttribute) this.getRoot().getAttribute(
                            GraphicAttributeConstants.LABEL)).getLabel();
        } catch (AttributeNotFoundException a) {
        }

        String resultString = indent + position + ": " + descriptionText + " "
                + directionText + ": ( " + this.getWidth() + " x "
                + this.getHeight() + " )";
        resultString += " rootPos = " + this.getRelPositionOfRoot();

        if (this.getSubtrees() != null && this.getSubtrees().size() > 0) {
            Iterator<Point2D> relPositionItr = this.getSubtreePositions()
                    .iterator();
            for (LayoutComposition currentComposition : this.getSubtrees()) {
                resultString += "\n"
                        + currentComposition.showStructure(indent + "\t",
                                relPositionItr.next());
            }
        }

        return resultString;
    }

    public LayoutComposition() {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
