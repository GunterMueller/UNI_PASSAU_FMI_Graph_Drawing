package org.graffiti.plugins.algorithms.phyloTrees.utility;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * Class to provide the functionality to scale and place nodes with coordinates
 * given in the unit circle.
 */
public class CircleScaling {

    /**
     * The {@link PhyloTreeGraphData} object containing the information about
     * the associated data.
     */
    private PhyloTreeGraphData data;

    /**
     * The root Node this object is associated with.
     */
    private Node root;

    /**
     * Indicates whether the radius should be scaled in order to best fit the
     * window.
     */
    private boolean scaleToWindow;

    /**
     * Indicates whether leaf labels should be rotated after setting its new
     * coordinates.
     */
    private boolean rotateLabels;

    /**
     * The center of the tree's circle.
     */
    private Point2D circleCenter;

    /**
     * The radius of the tree.
     */
    private double radius;

    /** This angle is ignored when computing the radius. */
    private double ignoreWedge;

    /**
     * Creates a new instance of this class. The root and data for the tree to
     * be scaled must be given.
     * 
     * @param root
     *            The root node of the tree.
     * @param data
     *            The data associated with the graph to which the tree belongs.
     * @param scaleToWindow
     *            Indicates whether or not to automatically scale the tree to
     *            fit the window size.
     * @param rotateLabels
     *            Indicates whether the labels of the leafs are to be rotated to
     *            face away from the circle center.
     */
    public CircleScaling(Node root, PhyloTreeGraphData data,
            boolean scaleToWindow, boolean rotateLabels, double ignoreWedge) {
        this.root = root;
        this.data = data;
        this.scaleToWindow = scaleToWindow;
        this.rotateLabels = rotateLabels;
        this.ignoreWedge = ignoreWedge;

        calculateAndSetScalingParameters();
    }

    /**
     * Sets the coordinates, which must be given in unit circle.
     * 
     * @param node
     *            The node whose coordinates are to be set.
     * @param xCoord
     *            The x coordinates that are to be set.
     * @param yCoord
     *            The y coordinates that are to be set.
     */
    public void setCoord(Node node, double xCoord, double yCoord) {
        double targetXCoord = xCoord * radius + circleCenter.getX();
        double targetYCoord = yCoord * radius + circleCenter.getY();
        GravistoUtil.setCoords(node, targetXCoord, targetYCoord);

        if (rotateLabels && node.getOutDegree() == 0
                && node.containsAttribute(GraphicAttributeConstants.LABEL)) {
            PhyloTreeUtil.setLabelRotation(node, circleCenter);
        }
    }

    /**
     * Returns the center of the circle.
     * 
     * @return The center of the circle.
     */
    public Point2D getCenter() {
        return circleCenter;
    }

    /**
     * Calculates and sets the parameters necessary for scaling and transforming
     * the graph. Updates the lower boundary of the tree in the
     * {@link PhyloTreeGraphData}.
     * 
     * The following attributes will be set:
     * <ul>
     * <li>radius</li>
     * <li>circleCenter</li>
     * </ul>
     */
    private void calculateAndSetScalingParameters() {
        Rectangle2D r2d = GravistoUtil.getVisibleAreaBounds();

        int leafNumber = data.getLeafCount(root);
        double labelOffset = GraphicAttributeConstants.LABEL_DISTANCE;
        double maxLabelLength = data.getMaxLabelWidth(root) + labelOffset;

        double maxLabelHeight = Math.max(data.getMaxLabelHeight(root),
                PhyloTreeConstants.MIN_VERTICAL_NODE_DISTANCE);

        double maxLength = Math.min(r2d.getWidth(), r2d.getHeight());
        double windowRadius = (maxLength - 2 * (maxLabelLength + labelOffset)) / 2;

        double minRadius = 150;

        double extent = leafNumber * maxLabelHeight;
        double linearRadius = extent / (2 * Math.PI - ignoreWedge)
                - labelOffset;

        if (linearRadius < minRadius) {
            linearRadius = minRadius;
        }

        double upperBoundary = data.getUpperBound(root);

        if (windowRadius >= linearRadius && scaleToWindow) {
            this.radius = windowRadius;
            this.circleCenter = new Point2D.Double(r2d.getCenterX(),
                    upperBoundary + r2d.getCenterY());
        } else {
            this.radius = linearRadius;
            double xVal = r2d.getX() + (maxLabelLength + linearRadius);
            double yVal = r2d.getY() + (maxLabelLength + linearRadius)
                    + upperBoundary;
            this.circleCenter = new Point2D.Double(xVal, yVal);
        }

        data.setVerticalSpace(root,
                2 * (this.radius + maxLabelLength + labelOffset));
    }
}
