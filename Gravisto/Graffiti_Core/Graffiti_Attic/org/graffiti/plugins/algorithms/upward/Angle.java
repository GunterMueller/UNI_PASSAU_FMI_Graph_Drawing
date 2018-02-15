/**
 * the class compute the angle between two edges with one same node.
 * 
 * @author jin
 */
package org.graffiti.plugins.algorithms.upward;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;

public class Angle {
    /**
     * the first edge in counterclockwise order
     */
    private Edge first;

    /**
     * the second edge in counterclockwise order
     */
    private Edge second;

    /**
     * common node of two edges
     */
    private Node node;

    /**
     * the angle
     */
    private double value;

    /**
     * is large angle?
     */
    private boolean isLargeAngle;

    /**
     * is small angle?
     */
    private boolean isSmallAngle;

    /**
     * is source switch?
     */
    private boolean sourceSwitch;

    /**
     * is sink switch
     */
    private boolean sinkSwitch;

    /**
     * common node of two edges
     */
    private Node node1;

    /**
     * other end node of first edge
     */
    private Node node2;

    /**
     * other end node of second edge
     */
    private Node node3;

    /**
     * constructor
     */
    public Angle() {
    }

    /**
     * constructor with two edges.
     * 
     * @param first
     *            the first edge
     * @param second
     *            the second edge
     */
    public Angle(Edge first, Edge second) {
        this.first = first;
        this.second = second;
        this.isLargeAngle = false;
        this.isSmallAngle = false;
        this.sourceSwitch = false;
        this.sinkSwitch = false;
    }

    /**
     * constructor with three nodes of two edges.
     * 
     * @param node1
     *            common node of angle of two edges
     * @param node2
     *            other end node of first edge
     * @param node3
     *            other end node of second edge
     */
    public Angle(Node node1, Node node2, Node node3) {
        this.node1 = node1;
        this.node2 = node2;
        this.node3 = node3;
        this.isLargeAngle = false;
        this.isSmallAngle = false;
        this.sourceSwitch = false;
        this.sinkSwitch = false;
    }

    /**
     * compute angle
     */
    public void executeOfNodes() {
        this.node = this.node1;
        this.calculateAngle(this.node2, this.node3);
    }

    /**
     * is a large angle
     * 
     * @return return true, when the angle is a large angle
     */
    public boolean getIsLargeAngle() {
        return this.isLargeAngle;
    }

    /**
     * is a small angle
     * 
     * @return return true, when the angle is a small angle
     */
    public boolean getIsSmallAngle() {
        return this.isSmallAngle;
    }

    /**
     * get first edge
     * 
     * @return the first edge
     */
    public Edge getFirstEdge() {
        return this.first;
    }

    /**
     * set first edge
     * 
     * @param first
     *            the first edge
     */
    public void setFirstEdge(Edge first) {
        this.first = first;
    }

    /**
     * get second edge
     * 
     * @return the second edge
     */
    public Edge getSecondEdge() {
        return this.second;
    }

    /**
     * set second edge
     * 
     * @param second
     *            the second edge
     */
    public void setSecondEdge(Edge second) {
        this.second = second;
    }

    /**
     * get the value of angle
     * 
     * @return the value of the angle
     */
    public double getValue() {
        return this.value;
    }

    /**
     * set the value of angle
     * 
     * @param value
     *            the value of the angle
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * set the angle as true, when the angle is a source switch.
     * 
     * @param bool
     */
    private void setSourceSwitch(boolean bool) {
        this.sourceSwitch = bool;
    }

    /**
     * set the angle as true, when the angle is a sink switch.
     * 
     * @param bool
     */
    private void setSinkSwitch(boolean bool) {
        this.sinkSwitch = bool;
    }

    /**
     * get true, when the angle is a source switch.
     * 
     * @return true, when the angle is a source switch.
     */
    public boolean getSourceSwitch() {
        return this.sourceSwitch;
    }

    /**
     * get true, when the angle is a sink switch.
     * 
     * @return true, when the angle is a sink switch.
     */
    public boolean getSinkSwitch() {
        return this.sinkSwitch;
    }

    /**
     * get common node of the angle
     * 
     * @return common node of the angle
     */
    public Node getNode() {
        return this.node;
    }

    /**
     * compute the angle
     */
    public void execute() {
        Node nodeFirstEdgeSource = this.getFirstEdge().getSource();
        Node nodeFirstEdgeTarget = this.getFirstEdge().getTarget();

        Node nodeSecondEdgeSource = this.getSecondEdge().getSource();
        Node nodeSecondEdgeTarget = this.getSecondEdge().getTarget();
        // two outgoing edge
        if (nodeFirstEdgeSource.equals(nodeSecondEdgeSource)) {
            this.node = nodeFirstEdgeSource;
            this.calculateAngle(nodeFirstEdgeTarget, nodeSecondEdgeTarget);
            this.isLargeAngle();
            this.setSourceSwitch(true);
        }
        // two incoming edge
        else if (nodeFirstEdgeTarget.equals(nodeSecondEdgeTarget)) {
            this.node = nodeFirstEdgeTarget;
            this.calculateAngle(nodeFirstEdgeSource, nodeSecondEdgeSource);
            this.isLargeAngle();
            this.setSinkSwitch(true);
        }
        // the first edge incoming and the second edge outgoing
        else if (nodeFirstEdgeTarget.equals(nodeSecondEdgeSource)) {
            this.node = nodeFirstEdgeTarget;
            this.calculateAngle(nodeFirstEdgeSource, nodeSecondEdgeTarget);
        }
        // the first edge outgoing and the second edge incoming
        else if (nodeFirstEdgeSource.equals(nodeSecondEdgeTarget)) {
            this.node = nodeFirstEdgeSource;
            this.calculateAngle(nodeFirstEdgeTarget, nodeSecondEdgeSource);
        }
    }

    /**
     * compute the angle
     * 
     * @param nodeFirst
     *            other end node of first edge
     * @param nodeSecond
     *            other end node of second edge
     */
    private void calculateAngle(Node nodeFirst, Node nodeSecond) {
        double x0 = node.getDouble(GraphicAttributeConstants.COORDX_PATH);
        double y0 = node.getDouble(GraphicAttributeConstants.COORDY_PATH);
        double x1 = nodeFirst.getDouble(GraphicAttributeConstants.COORDX_PATH);
        double y1 = nodeFirst.getDouble(GraphicAttributeConstants.COORDY_PATH);
        double x2 = nodeSecond.getDouble(GraphicAttributeConstants.COORDX_PATH);
        double y2 = nodeSecond.getDouble(GraphicAttributeConstants.COORDY_PATH);
        double value1 = this.angleOfEdgeAndXAxle(x1, y1, x0, y0);
        double value2 = this.angleOfEdgeAndXAxle(x2, y2, x0, y0);

        if (value1 > value2) {
            this.value = value2 - value1 + 2 * Math.PI;
        } else {
            this.value = value2 - value1;
        }
    }

    /**
     * get the angle between edge and x-axle
     * 
     * @param x
     *            x-coordinate of other end node of first or second edge
     * @param y
     *            y-coordinate of other end node of first or second edge
     * @param x0
     *            x-coordinate of common node of the angle
     * @param y0
     *            y-coordinate of common node of the angle
     * @return the angle between x-axle and first or second edge.
     */
    private double angleOfEdgeAndXAxle(double x, double y, double x0, double y0) {
        if ((x > x0) && (y == y0))
            return 0d;
        else if ((x < x0) && (y == y0))
            return Math.PI;
        else if ((x == x0) && (y > y0))
            return (3 * Math.PI) / 2;
        else if ((x == x0) && (y < y0))
            return Math.PI / 2;
        else if ((x > x0) && (y > y0))
            return 2 * Math.PI - Math.atan((y - y0) / (x - x0));
        else if ((x > x0) && (y < y0))
            return Math.abs(Math.atan((y - y0) / (x - x0)));
        else if ((x < x0) && (y > y0))
            return Math.abs(Math.atan((y - y0) / (x - x0))) + Math.PI;
        else if ((x < x0) && (y < y0))
            return Math.PI - Math.atan((y - y0) / (x - x0));
        else
            return 0d;
    }

    /**
     * is a large angle?
     */
    private void isLargeAngle() {
        if (this.value > Math.PI) {
            this.isLargeAngle = true;
        } else {
            this.isSmallAngle = true;
        }
    }
}
