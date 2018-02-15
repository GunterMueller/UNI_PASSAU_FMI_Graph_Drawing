// =============================================================================
//
//   hvComposition.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TipoverComposition.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.treedrawings.Util;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutComposition;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutConstants;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.hv.HVComposition;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;
import org.graffiti.selection.Selection;

/**
 * @author Andreas
 * @version $Revision: 5766 $ $Date: 2007-06-20 12:11:37 +0200 (Mi, 20 Jun 2007)
 *          $
 */
public class TipoverComposition extends LayoutComposition {

    /**
     * The root of this TipoverComposition
     */
    protected Node root;

    /**
     * The width of the root Node for this LayoutComposition
     */
    protected double rootWidth;

    /**
     * The height of the root Node for this LayoutComposition
     */
    protected double rootHeight;

    /**
     * The width of this LayoutComposition
     */
    protected double width;

    /**
     * The height of this LayoutComposition
     */
    protected double height;

    /**
     * The relative positions of the subtrees
     */
    protected List<Point2D> subtreePositions;

    /**
     * The layouts of the subtrees
     */
    protected List<LayoutComposition> subtrees;

    /**
     * This determines whether this LayoutComposition is laid out horizontally
     * or not (vertically)
     */
    protected boolean horizontal;

    /**
     * The relative position of the root Node of this LayoutComposition
     */
    protected Point2D relPositionOfRoot;

    /**
     * The distance between the root and the children and between the children.
     */
    protected double nodeDistance;

    /**
     * Dummy constructor for factory.
     */
    public TipoverComposition() {
    }

    /**
     * This constructs a new TipoverComposition using the information provided.
     * 
     * @param root
     *            the root Node for the new TipoverComposition
     * @param rootWidth
     *            the width of the root. Not necessarily the same as the real
     *            width of the root Node.
     * @param rootHeight
     *            the height of the root. Not necessarily the same as the real
     *            height of the root Node.
     * @param childCompositions
     *            of the TipoverCompositions that represent the drawings of the
     *            subtrees of the children of the root Node.
     * @param horizontal
     *            determines whether this TipoverComposition will be laid out
     *            horizontally or not (vertically).
     * @param nodeDistance
     *            The distance between the root and the children and between the
     *            children.
     */
    protected TipoverComposition(Node root, double rootWidth,
            double rootHeight, List<LayoutComposition> childCompositions,
            boolean horizontal, double nodeDistance) {

        this.root = root;
        this.rootWidth = rootWidth;
        this.rootHeight = rootHeight;
        this.nodeDistance = nodeDistance;
        this.horizontal = horizontal;

        if (childCompositions == null || childCompositions.size() == 0) {
            this.subtrees = new ArrayList<LayoutComposition>();
            this.width = this.rootWidth;
            this.height = this.rootHeight;
            this.relPositionOfRoot = new Point2D.Double(this.rootWidth / 2.0,
                    this.rootHeight / 2.0);
        } else {
            this.subtrees = new ArrayList<LayoutComposition>(childCompositions);

            this.subtreePositions = new LinkedList<Point2D>();

            this.relPositionOfRoot = new Point2D.Double();

            if (this.horizontal) {

                double currentRelXPos = 0.0;

                double topDistance = this.rootHeight + nodeDistance;

                double maximumChildHeight = 0;
                for (LayoutComposition currentComposition : this.subtrees) {

                    double currentHeight = currentComposition.getHeight();
                    if (currentHeight > maximumChildHeight) {
                        maximumChildHeight = currentHeight;
                    }

                    Point2D currentPosition = new Point2D.Double(
                            currentRelXPos, topDistance);
                    this.subtreePositions.add(currentPosition);

                    currentRelXPos += nodeDistance
                            + currentComposition.getWidth();

                }

                this.height = topDistance + maximumChildHeight;

                // Find out where to place the root node...
                // the position of the first child...

                LayoutComposition firstChild = this.subtrees.get(0);
                double firstChildXPosOffset = firstChild.getRelPositionOfRoot()
                        .getX();
                double firstChildRootXPos = this.subtreePositions.get(0).getX()
                        + firstChildXPosOffset;

                // the position of the last child...
                int lastChildIndex = this.subtrees.size() - 1;
                LayoutComposition lastChild = this.subtrees.get(lastChildIndex);
                double lastChildXPosOffset = lastChild.getRelPositionOfRoot()
                        .getX();
                double lastChildRootXPos = this.subtreePositions.get(
                        lastChildIndex).getX()
                        + lastChildXPosOffset;

                // TODO: !!!!!???
                // this.relPositionOfRoot.x = (firstChildRootXPos +
                // lastChildRootXPos) / 2.0;
                this.relPositionOfRoot.setLocation(
                        (firstChildRootXPos + lastChildRootXPos) / 2.0,
                        this.rootHeight / 2.0);

                // Because the root-Node has a dimension it can happen that its
                // left side is out of bounds of this TipoverComposition. Thus,
                // in this case we have to do some shifting.

                double howMuchOutOfBounds = this.rootWidth / 2.0
                        - this.relPositionOfRoot.getX();

                if (howMuchOutOfBounds > 0) {
                    // TODO: ???
                    // this.relPositionOfRoot.x += howMuchOutOfBounds;
                    this.relPositionOfRoot
                            .setLocation(this.relPositionOfRoot.getX()
                                    + howMuchOutOfBounds,
                                    this.relPositionOfRoot.getY());
                    for (int i = 0; i < this.subtrees.size(); i++) {
                        // TODO: ???
                        // this.relPositionsOfSubtrees.get(i).x +=
                        // howMuchOutOfBounds;
                        this.subtreePositions.get(i).setLocation(
                                this.subtreePositions.get(i).getX()
                                        + howMuchOutOfBounds,
                                this.subtreePositions.get(i).getY());
                    }

                }

                // Now let us determine the width of this TipoverComposition. We
                // define it as the distance between the distance of 0 (the left
                // corner of this TipoverComposition) and the
                // max(rightEdgeXCoordOfRoot, rightEdgeXCoordOfLastChild)

                double rightEdgeXCoordOfLastChild = this.subtreePositions.get(
                        lastChildIndex).getX()
                        + lastChild.getWidth();

                double rightEdgeXCoordOfRoot = this.relPositionOfRoot.getX()
                        + this.rootWidth / 2.0;

                this.width = Math.max(rightEdgeXCoordOfLastChild,
                        rightEdgeXCoordOfRoot);
            } else {
                double maximumChildWidth = 0.0;

                // TODO: !!!???
                // this.relPositionOfRoot.x = this.rootWidth / 2.0;
                this.relPositionOfRoot.setLocation(this.rootWidth / 2.0,
                        this.rootHeight / 2.0);
                double currentRelYpos = this.rootHeight + nodeDistance;

                double rightDistance = nodeDistance + this.rootWidth / 2.0;

                double halfOfCurrentHeight = 0.0;

                for (LayoutComposition currentComposition : this.subtrees) {

                    double currentHeight = currentComposition.getHeight();
                    double currentWidth = currentComposition.getWidth();

                    if (currentWidth > maximumChildWidth) {
                        maximumChildWidth = currentWidth;
                    }

                    Point2D currentPosition = new Point2D.Double(rightDistance,
                            currentRelYpos);
                    this.subtreePositions.add(currentPosition);

                    currentRelYpos += currentHeight + nodeDistance;
                }

                if (nodeDistance + maximumChildWidth > this.rootWidth / 2.0) {
                    this.width = this.rootWidth / 2.0 + nodeDistance
                            + maximumChildWidth;
                } else {
                    this.width = rootWidth;

                }

                this.height = currentRelYpos - halfOfCurrentHeight
                        - nodeDistance;
            }

        }
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#instance
     * (org.graffiti.graph.Node, java.util.List)
     */
    @Override
    public LayoutComposition instance(Node root,
            List<LayoutComposition> childCompositions)
            throws PreconditionException {
        boolean horizontal;
        double nodeDistance, rootWidth, rootHeight;

        try {
            horizontal = root.getBoolean("layout.isHorizontal");
            nodeDistance = root.getDouble("layout.nodeDistance");
            rootWidth = root.getDouble("layout.rootDimension.width");
            rootHeight = root.getDouble("layout.rootDimension.height");
        } catch (AttributeNotFoundException a) {
            Selection selection = new Selection();
            selection.add(root);
            PreconditionException errors = new PreconditionException();
            errors.add(a.getMessage()
                    + ". The corresponding node will be selected.", selection);

            throw errors;
        }

        TreeMap<Double, LayoutComposition> childCompositionsSorter = new TreeMap<Double, LayoutComposition>();

        // Get the orderNumber for each ChildComposition and insert them into
        // the TreeMap to order them by their order number...
        for (LayoutComposition currentComposition : childCompositions) {
            double currentOrderNumber;
            try {
                currentOrderNumber = currentComposition.getRoot().getDouble(
                        "layout.orderNumber");
            } catch (AttributeNotFoundException a) {
                Selection selection = new Selection();
                selection.add(currentComposition.getRoot());
                PreconditionException errors = new PreconditionException();
                errors.add(a.getMessage()
                        + ". The corresponding node will be selected.",
                        selection);

                throw errors;
            }

            if (childCompositionsSorter.containsKey(currentOrderNumber)) {
                Selection selection = new Selection();
                selection.add(root);
                PreconditionException errors = new PreconditionException();
                errors
                        .add(
                                "All the layout.orderNumber Attributes of the subtrees of a node must be unique. "
                                        + "The corresponding node will be selected.",
                                selection);

                throw errors;
            } else {
                childCompositionsSorter.put(currentOrderNumber,
                        currentComposition);
            }
        }

        LinkedList<LayoutComposition> childCompositionsInCorrectOrder = new LinkedList<LayoutComposition>();
        for (LayoutComposition currentComposition : childCompositionsSorter
                .values()) {
            childCompositionsInCorrectOrder.add(currentComposition);
        }

        return new TipoverComposition(root, rootWidth, rootHeight,
                childCompositionsInCorrectOrder, horizontal, nodeDistance);

    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#instance
     * (org.graffiti.graph.Node, java.util.List, boolean, double)
     */
    @Override
    public List<LayoutComposition> instance(Node root,
            List<LayoutComposition> childCompositions, boolean horizontal,
            double nodeDistance, boolean nodesWithDimensions) {

        double rootWidth, rootHeight;

        if (!nodesWithDimensions) {
            rootWidth = 0.0;
            rootHeight = 0.0;
        } else if (Util.isHelperNode(root)) {
            rootWidth = LayoutConstants.minNodeWidth;
            rootHeight = LayoutConstants.minNodeHeight;
        } else {
            DimensionAttribute da = (DimensionAttribute) root
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.DIMENSION);
            rootWidth = da.getWidth();
            rootHeight = da.getHeight();
        }

        // There is only one possible Composition, but we have to obey the
        // interface...
        LinkedList<LayoutComposition> resultList = new LinkedList<LayoutComposition>();

        resultList.add(new TipoverComposition(root, rootWidth, rootHeight,
                childCompositions, horizontal, nodeDistance));

        return resultList;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#getRoot()
     */
    @Override
    public Node getRoot() {
        return this.root;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#getRootWidth
     * ()
     */
    @Override
    public double getRootWidth() {
        return this.rootWidth;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#getRootHeight
     * ()
     */
    @Override
    public double getRootHeight() {
        return this.rootHeight;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#getWidth()
     */
    @Override
    public double getWidth() {
        return this.width;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#getHeight
     * ()
     */
    @Override
    public double getHeight() {
        return this.height;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.treedrawings.LayoutComposition#
     * getSubtreePositions()
     */
    @Override
    public List<Point2D> getSubtreePositions() {
        return this.subtreePositions;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#getSubtrees
     * ()
     */
    @Override
    public List<LayoutComposition> getSubtrees() {
        return this.subtrees;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#isHorizontal
     * ()
     */
    @Override
    public boolean isHorizontal() {
        return this.horizontal;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.treedrawings.LayoutComposition#
     * getRelPositionOfRoot()
     */
    @Override
    public Point2D getRelPositionOfRoot() {
        return this.relPositionOfRoot;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.treedrawings.LayoutComposition#
     * getNodeDistance()
     */
    @Override
    public double getNodeDistance() {
        return this.nodeDistance;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#combine()
     */
    @Override
    protected void combine(Point2D rootCompositionTotalPosition,
            LayoutComposition childComposition,
            Point2D childCompositionTotalPosition) {

        Util.resetOutgoingPorts(this.root);
        Util.resetIngoingPorts(childComposition.getRoot());

        if (childComposition instanceof HVComposition) {
            this.combineHelper(rootCompositionTotalPosition, childComposition,
                    childCompositionTotalPosition);
        } else if (childComposition instanceof TipoverComposition) {
            this.combineHelper(rootCompositionTotalPosition, childComposition,
                    childCompositionTotalPosition);
        } else
            throw new UnsupportedOperationException(
                    "You cannot combine the layouts"
                            + this.getClass()
                            + " with "
                            + childComposition.getClass()
                            + ". Please implement a method combine(Point2D, "
                            + childComposition.getClass()
                            + ", Point2D) and make the necessary changes to combine(Point2D rootCompositionTotalPosition, "
                            + "LayoutComposition childComposition, Point2D childCompositionTotalPosition) in "
                            + this.getClass());
    }

    /**
     * Helper method for the method combine()
     * 
     * @param rootCompositionTotalPosition
     * @param childComposition
     * @param childCompositionTotalPosition
     */
    public void combineHelper(Point2D rootCompositionTotalPosition,
            LayoutComposition childComposition,
            Point2D childCompositionTotalPosition) {

        Iterator<Edge> onlyOneEdgeItr = childComposition.getRoot()
                .getAllInEdges().iterator();

        if (onlyOneEdgeItr.hasNext()) {
            Edge incomingEdge = onlyOneEdgeItr.next();

            EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) incomingEdge
                    .getAttribute("graphics");
            SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                    "bends");

            if (this.horizontal) {

                double totalX1 = rootCompositionTotalPosition.getX()
                        + this.relPositionOfRoot.getX();

                double totalY = rootCompositionTotalPosition.getY()
                        + this.rootHeight + this.nodeDistance / 2.0;

                bends.add(new CoordinateAttribute("bend1", new Point2D.Double(
                        totalX1, totalY)));

                double totalX2 = childCompositionTotalPosition.getX()
                        + childComposition.getRelPositionOfRoot().getX();

                bends.add(new CoordinateAttribute("bend2", new Point2D.Double(
                        totalX2, totalY)));

            } else {
                double totalX = rootCompositionTotalPosition.getX()
                        + this.relPositionOfRoot.getX();
                double totalY = childCompositionTotalPosition.getY()
                        + childComposition.getRelPositionOfRoot().getY();

                bends.add(new CoordinateAttribute("bend1", new Point2D.Double(
                        totalX, totalY)));
            }

            edgeAttr.setBends(bends);
            edgeAttr.setShape(PolyLineEdgeShape.class.getName());

        }

    }

    /*
     * @seeorg.graffiti.plugins.algorithms.treedrawings.LayoutComposition#
     * setLayoutAttributesLocal()
     */
    @Override
    protected void setLayoutAttributesLocal(double orderNumber) {

        LinkedHashMapAttribute layoutAttribute = new LinkedHashMapAttribute(
                "layout");

        StringAttribute layoutTypeAttribute = new StringAttribute("type",
                "tipover");

        BooleanAttribute layoutDirectionAttribute = new BooleanAttribute(
                "isHorizontal", this.horizontal);

        boolean forceDirection = false;
        try {
            forceDirection = this.root.getBoolean("layout.forceDirection");
        } catch (AttributeNotFoundException a) {
        }

        BooleanAttribute forceDirectionAttribute = new BooleanAttribute(
                "forceDirection", forceDirection);

        DimensionAttribute rootDimensionAttribute = new DimensionAttribute(
                "rootDimension");
        rootDimensionAttribute.setWidth(this.rootWidth);
        rootDimensionAttribute.setHeight(this.rootHeight);

        DoubleAttribute nodeDistanceAttribute = new DoubleAttribute(
                "nodeDistance", this.nodeDistance);

        DoubleAttribute orderNumberAttribute = new DoubleAttribute(
                "orderNumber", orderNumber);

        layoutAttribute.add(layoutTypeAttribute);
        layoutAttribute.add(layoutDirectionAttribute);
        layoutAttribute.add(forceDirectionAttribute);
        layoutAttribute.add(rootDimensionAttribute);
        layoutAttribute.add(nodeDistanceAttribute);
        layoutAttribute.add(orderNumberAttribute);

        try {
            this.root.removeAttribute("layout");
        } catch (AttributeNotFoundException a) {
        }

        this.root.addAttribute(layoutAttribute, "");
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
