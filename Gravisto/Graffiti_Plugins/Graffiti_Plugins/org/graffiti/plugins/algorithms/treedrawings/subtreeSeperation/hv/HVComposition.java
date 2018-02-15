// =============================================================================
//
//   hvComposition.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: HVComposition.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.hv;

import java.awt.geom.Point2D;
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
import org.graffiti.graphics.DockingAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.Port;
import org.graffiti.graphics.PortAttribute;
import org.graffiti.graphics.PortsAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.treedrawings.Util;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutComposition;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.LayoutConstants;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover.TipoverComposition;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;
import org.graffiti.plugins.views.defaults.StraightLineEdgeShape;
import org.graffiti.selection.Selection;

/**
 * @author Andreas
 * @version $Revision: 5766 $ $Date: 2006-11-07 18:34:38 +0100 (Di, 07 Nov 2006)
 *          $
 */

public class HVComposition extends LayoutComposition {

    /**
     * The root of this HVComposition
     */
    protected Node root;

    /**
     * The width of the root Node for this HVComposition
     */
    protected double rootWidth;

    /**
     * The height of the root Node for this HVComposition
     */
    protected double rootHeight;

    /**
     * The width of this HVComposition
     */
    protected double width;

    /**
     * The height of this HVComposition
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
     * This determines whether this HVComposition is laid out horizontally or
     * not (vertically)
     */
    protected boolean horizontal;

    /**
     * The relative position of the root Node of this HVComposition
     */
    protected Point2D relPositionOfRoot;

    /**
     * The distance between the root and the children and between the children.
     */
    protected double nodeDistance;

    /**
     * Dummy constructor
     * 
     */
    public HVComposition() {
    }

    /**
     * This constructs a new HVComposition using the information provided.
     * 
     * @param root
     *            The root of this HVComposition.
     * @param childCompositions
     *            The subtrees of this HVComposition
     * @param horizontal
     *            This determines if the HVComposition should be horizontal or
     *            not (=vertical). Horizontal means that the firstNode will be
     *            positioned beneath the root. Vertical means that the firstNode
     *            will be position to the right of the root.
     * @param nodeDistance
     *            This determines the distance the nodes. TODO: Add horizontal
     *            and vertical distance (should be trivial)
     */
    protected HVComposition(Node root, double rootWidth, double rootHeight,
            List<LayoutComposition> childCompositions, boolean horizontal,
            double nodeDistance) {

        this.root = root;
        this.rootWidth = rootWidth;
        this.rootHeight = rootHeight;
        this.nodeDistance = nodeDistance;
        this.horizontal = horizontal;

        // see that the root is positioned correctly. I.e. the top left corner
        // of the bounding box has to be exactly on point (0, 0) of this
        // composition.
        this.relPositionOfRoot = new Point2D.Double(this.rootWidth / 2.0,
                this.rootHeight / 2.0);

        this.subtrees = new LinkedList<LayoutComposition>();

        this.subtreePositions = new LinkedList<Point2D>();

        if (childCompositions == null || childCompositions.size() == 0) {
            this.width = this.rootWidth;
            this.height = this.rootHeight;
        } else if (childCompositions.size() == 1) {
            LayoutComposition onlySubtree = childCompositions.get(0);
            this.subtrees.add(onlySubtree);

            if (this.horizontal) {
                this.width = this.rootWidth + nodeDistance
                        + onlySubtree.getWidth();
                this.height = Math
                        .max(onlySubtree.getHeight(), this.rootHeight);
                this.subtreePositions.add(new Point2D.Double(this.rootWidth
                        + nodeDistance, 0.0));
            } else {
                this.width = Math.max(onlySubtree.getWidth(), this.rootWidth);
                this.height = this.rootHeight + nodeDistance
                        + onlySubtree.getHeight();
                this.subtreePositions.add(new Point2D.Double(0.0,
                        this.rootHeight + nodeDistance));
            }
        } else {

            LayoutComposition firstSubtree = childCompositions.get(0);
            LayoutComposition secondSubtree = childCompositions.get(1);

            this.subtrees.add(firstSubtree);
            this.subtrees.add(secondSubtree);

            if (this.horizontal) {
                this.height = Math.max(this.rootHeight + nodeDistance
                        + firstSubtree.getHeight(), secondSubtree.getHeight());

                this.subtreePositions.add(new Point2D.Double(0.0,
                        this.rootHeight + nodeDistance));

                this.subtreePositions.add(new Point2D.Double(Math.max(
                        this.rootWidth, firstSubtree.getWidth())
                        + nodeDistance, 0.0));
                this.width = this.subtreePositions.get(1).getX()
                        + secondSubtree.getWidth();
            } else {
                this.width = Math.max(this.rootWidth + nodeDistance
                        + firstSubtree.getWidth(), secondSubtree.getWidth());
                this.subtreePositions.add(new Point2D.Double(this.rootWidth
                        + nodeDistance, 0.0));

                this.subtreePositions.add(new Point2D.Double(0.0, Math.max(
                        this.rootHeight, firstSubtree.getHeight())
                        + nodeDistance));
                this.height = this.subtreePositions.get(1).getY()
                        + secondSubtree.getHeight();

            }
        }

    }

    /**
     * Returns the rootHeight.
     * 
     * @return the rootHeight.
     */
    @Override
    public double getRootHeight() {
        return rootHeight;
    }

    /**
     * Returns the rootWidth.
     * 
     * @return the rootWidth.
     */
    @Override
    public double getRootWidth() {
        return rootWidth;
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

        return new HVComposition(root, rootWidth, rootHeight,
                childCompositionsInCorrectOrder, horizontal, nodeDistance);
    }

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

        LinkedList<LayoutComposition> resultList = new LinkedList<LayoutComposition>();

        if (childCompositions == null || childCompositions.size() < 2) {
            resultList.add(new HVComposition(root, rootWidth, rootHeight,
                    childCompositions, horizontal, nodeDistance));
        } else if (childCompositions != null && childCompositions.size() >= 2) {

            LayoutComposition subtreeOfV = childCompositions.get(0);
            LayoutComposition subtreeOfW = childCompositions.get(1);

            LinkedList<LayoutComposition> childCompositionsInReverseOrder = new LinkedList<LayoutComposition>();
            childCompositionsInReverseOrder.addLast(subtreeOfW);
            childCompositionsInReverseOrder.addLast(subtreeOfV);

            HVComposition givenOrderComposition = new HVComposition(root,
                    rootWidth, rootHeight, childCompositions, horizontal,
                    nodeDistance);

            HVComposition reverseOrderComposition = new HVComposition(root,
                    rootWidth, rootHeight, childCompositionsInReverseOrder,
                    horizontal, nodeDistance);

            if (givenOrderComposition.dominates(reverseOrderComposition)) {
                resultList.addLast(reverseOrderComposition);
            } else if (reverseOrderComposition.dominates(givenOrderComposition)) {
                resultList.addLast(givenOrderComposition);
            } else {
                resultList.addLast(reverseOrderComposition);
                resultList.addLast(givenOrderComposition);
            }
        }

        return resultList;

    } /*
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
     * org.graffiti.plugins.algorithms.treedrawings.LayoutComposition#getRoot()
     */
    @Override
    public Node getRoot() {
        return this.root;
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
            this.combine(rootCompositionTotalPosition,
                    (HVComposition) childComposition,
                    childCompositionTotalPosition);
        } else if (childComposition instanceof TipoverComposition) {
            this.combine(rootCompositionTotalPosition,
                    (TipoverComposition) childComposition,
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
     * The combine method used for combining a HVComposition (like this) with
     * another HVComposition.
     * 
     * @param rootCompositionTotalPosition
     * @param childComposition
     *            that the edge is being laid out to from the root of this
     *            HVComposition.
     * @param childCompositionTotalPosition
     */
    protected void combine(Point2D rootCompositionTotalPosition,
            HVComposition childComposition,
            Point2D childCompositionTotalPosition) {

        // add the out-ports for this HVComposition (the father of
        // childComposition)...

        PortsAttribute portsAttr = (PortsAttribute) this.root
                .getAttribute("graphics.ports");
        LinkedList<Port> ports = new LinkedList<Port>();

        // Only the "layout" port is needed. It is located in the upper
        // left quadrant.

        // calculate layout-port locations for this HVCompositions's root...
        double portPosX = 0.0;
        if (this.rootWidth > 0.0) {
            portPosX = -1.0 + (LayoutConstants.minNodeWidth / this.rootWidth);
        }

        double portPosY = 0.0;
        if (this.rootHeight > 0) {
            portPosY = -1.0 + (LayoutConstants.minNodeHeight / this.rootHeight);
        }

        ports.add(new Port("layout", portPosX, portPosY));
        // and set the outgoing ports for this HVComposition's root...
        portsAttr.setOutgoingPorts(ports);

        // set the ingoing ports for the childComposition's root...
        PortsAttribute childPortsAttr = (PortsAttribute) childComposition
                .getRoot().getAttribute("graphics.ports");
        LinkedList<Port> childPorts = new LinkedList<Port>();

        // Only the "layout" port is needed. It is located in the upper
        // left quadrant.

        // calculate layout-port locations for the childCompositions's
        // root...
        double childPortPosX = 0.0;
        if (childComposition.getRootWidth() > 0.0) {
            childPortPosX = -1.0
                    + (LayoutConstants.minNodeWidth / childComposition
                            .getRootWidth());
        }

        double childPortPosY = 0.0;
        if (childComposition.getRootHeight() > 0.0) {
            childPortPosY = -1.0
                    + (LayoutConstants.minNodeHeight / childComposition
                            .getRootHeight());
        }

        childPorts.add(new Port("layout", childPortPosX, childPortPosY));

        // and set the ingoing port(s) for the childComposition's root...
        childPortsAttr.setIngoingPorts(childPorts);

        // set the dockings (for source and target). There should only be one
        // edge...
        for (Edge currentEdge : childComposition.getRoot().getAllInEdges()) {
            DockingAttribute docking = (DockingAttribute) currentEdge
                    .getAttribute("graphics.docking");
            docking.setSource("layout");
            docking.setTarget("layout");

            EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) currentEdge
                    .getAttribute("graphics");

            SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                    "bends");
            edgeAttr.setBends(bends);
            edgeAttr.setShape(StraightLineEdgeShape.class.getName());
        }
    }

    /**
     * The combine method used for combining a HVComposition (like this) with a
     * TipoverComposition.
     * 
     * @param rootCompositionTotalPosition
     * @param childComposition
     *            that the edge is being laid out to from the root of this
     *            HVComposition.
     * @param childCompositionTotalPosition
     */
    protected void combine(Point2D rootCompositionTotalPosition,
            TipoverComposition childComposition,
            Point2D childCompositionTotalPosition) {

        // add the out-port for this HVComposition (the father of
        // childComposition)...
        PortsAttribute portsAttr = (PortsAttribute) this.root
                .getAttribute("graphics.ports");
        LinkedList<Port> ports = new LinkedList<Port>();

        // Only the "layout" port is needed. It is located in the upper
        // left quadrant.

        // calculate layout-port locations for this HVCompositions's root...
        double portPosX = 0.0;
        if (this.rootWidth > 0.0) {
            portPosX = -1.0 + (LayoutConstants.minNodeWidth / this.rootWidth);
        }

        double portPosY = 0.0;
        if (this.rootHeight > 0) {
            portPosY = -1.0 + (LayoutConstants.minNodeHeight / this.rootHeight);
        }

        ports.add(new Port("layout", portPosX, portPosY));
        // and set the outgoing ports for this HVComposition's root...
        portsAttr.setOutgoingPorts(ports);

        // Now add the ingoing port for the childComposition's root...
        PortsAttribute childPortsAttr = (PortsAttribute) childComposition
                .getRoot().getAttribute("graphics.ports");
        LinkedList<Port> childPorts = new LinkedList<Port>();

        // The "layout" port. It is located in the upper left quadrant,
        // but as childComposition is a TipoverComposition we have to check
        // whether we actually need it...

        boolean ingoingPortNeeded = false;
        if (rootCompositionTotalPosition.getY() == childCompositionTotalPosition
                .getY()) {
            ingoingPortNeeded = true;
            // calculate layout-port locations for the childCompositions's
            // root...
            double childPortPosX = 0.0;
            if (childComposition.getRootWidth() > 0.0) {
                childPortPosX = -1.0
                        + (LayoutConstants.minNodeWidth / childComposition
                                .getRootWidth());
            }

            double childPortPosY = 0.0;
            if (childComposition.getRootHeight() > 0.0) {
                childPortPosY = -1.0
                        + (LayoutConstants.minNodeHeight / childComposition
                                .getRootHeight());
            }

            childPorts.add(new Port("layout", childPortPosX, childPortPosY));

        } else {
            Iterator<Edge> onlyOneEdgeItr = childComposition.getRoot()
                    .getAllInEdges().iterator();

            if (onlyOneEdgeItr.hasNext()) {
                Edge incomingEdge = onlyOneEdgeItr.next();

                EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) incomingEdge
                        .getAttribute("graphics");
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        "bends");

                PortsAttribute portAttr = (PortsAttribute) Util.getAttribute(
                        this.getRoot(), "graphics.ports");

                double portOffset = 0.0;
                if (portAttr != null) {
                    DimensionAttribute da = (DimensionAttribute) this.root
                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.DIMENSION);

                    PortAttribute portAttr2 = portAttr.getPort("layout", true);

                    if (portAttr2 != null) {
                        Port port = portAttr2.getPort();
                        if (port.getX() != 0.0) {
                            portOffset = (da.getWidth() / 2.0) * port.getX();
                        }
                    }
                }

                double totalX1 = rootCompositionTotalPosition.getX()
                        + this.getRelPositionOfRoot().getX() + portOffset;

                double totalY = childCompositionTotalPosition.getY()
                        - this.nodeDistance / 2.0;

                bends.add(new CoordinateAttribute("bend1", new Point2D.Double(
                        totalX1, totalY)));

                double totalX2 = childCompositionTotalPosition.getX()
                        + childComposition.getRelPositionOfRoot().getX();

                bends.add(new CoordinateAttribute("bend2", new Point2D.Double(
                        totalX2, totalY)));

                edgeAttr.setBends(bends);
                edgeAttr.setShape(PolyLineEdgeShape.class.getName());
            }
        }

        // and set the ingoing port(s) for the childComposition's root...
        childPortsAttr.setIngoingPorts(childPorts);

        // set the dockings (for source and target). There should only be one
        // edge...
        for (Edge currentEdge : childComposition.getRoot().getAllInEdges()) {
            DockingAttribute docking = (DockingAttribute) currentEdge
                    .getAttribute("graphics.docking");
            docking.setSource("layout");

            // if ingoing port is not needed, we just don't set the docking
            // target, because gravisto will use the middle of the node as the
            // default...
            if (ingoingPortNeeded) {
                docking.setTarget("layout");
                // we also can set the edge shape...
                EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) currentEdge
                        .getAttribute("graphics");
                edgeAttr.setShape(StraightLineEdgeShape.class.getName());
            }
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

        StringAttribute layoutTypeAttribute = new StringAttribute("type", "hv");

        BooleanAttribute layoutDirectionAttribute = new BooleanAttribute(
                "isHorizontal", this.isHorizontal());

        boolean forceDirection = false;
        try {
            forceDirection = this.root.getBoolean("layout.forceDirection");
        } catch (AttributeNotFoundException a) {
        }

        BooleanAttribute forceDirectionAttribute = new BooleanAttribute(
                "forceDirection", forceDirection);

        DimensionAttribute rootDimensionAttribute = new DimensionAttribute(
                "rootDimension");
        rootDimensionAttribute.setWidth(this.getRootWidth());
        rootDimensionAttribute.setHeight(this.getRootHeight());

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
