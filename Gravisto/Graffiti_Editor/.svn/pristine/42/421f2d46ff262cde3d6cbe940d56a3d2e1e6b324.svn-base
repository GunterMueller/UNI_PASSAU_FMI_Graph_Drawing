// =============================================================================
//
//   HexaNode.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeGridDrawings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford.Contur;
import org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford.XCoordComparator;

/**
 * @author Tom
 * @version $Revision$ $Date$
 */
public class HexaNode {
    private Node node;

    private double height;

    private double width;

    private double x;

    private double y;

    private int level;

    private int numberOfChildren;

    private MinMaxPosition minXP;

    private MinMaxPosition maxXP;

    private MinMaxPosition minYP;

    private MinMaxPosition maxYP;

    private MinMaxPosition minUpDiag;

    private MinMaxPosition maxUpDiag;

    private MinMaxPosition maxDownDiag;

    private MinMaxPosition minDownDiag;

    private Contur leftContur;

    private Contur rightContur;

    private int position;

    private ArrayList<Object> children;

    private HexaNode bendTarget;

    public HexaNode(Node node) {
        this.node = node;
        this.height = ((DimensionAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.DIMENSION)).getHeight();
        this.width = ((DimensionAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.DIMENSION)).getWidth();
        this.numberOfChildren = node.getOutDegree();
        this.minXP = new MinMaxPosition();
        this.maxXP = new MinMaxPosition();
        this.minYP = new MinMaxPosition();
        this.maxYP = new MinMaxPosition();
        this.minUpDiag = new MinMaxPosition();
        this.maxUpDiag = new MinMaxPosition();
        this.minDownDiag = new MinMaxPosition();
        this.maxDownDiag = new MinMaxPosition();
        leftContur = new Contur();
        rightContur = new Contur();
        bendTarget = null;

        Collection<Node> outNeighbour = node.getOutNeighbors();
        children = new ArrayList<Object>(outNeighbour);
        Collections.sort(children, new XCoordComparator());
    }

    public void setBend(HexaNode hn) {
        bendTarget = hn;
    }

    public Node getBend() {
        return bendTarget.getNode();
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getLevel() {
        return level;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public MinMaxPosition getMinXP() {
        return minXP;
    }

    public MinMaxPosition getMaxXP() {
        return maxXP;
    }

    public MinMaxPosition getMinYP() {
        return minYP;
    }

    public MinMaxPosition getMaxYP() {
        return maxYP;
    }

    public Contur getLeftContur() {
        return leftContur;
    }

    public Contur getRightContur() {
        return rightContur;
    }

    public ArrayList<Object> getChildren() {
        return children;
    }

    public void setX(double x) {
        this.x = x;
        ((CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE)).setX(x);

    }

    public void setY(double y) {
        this.y = y;
        ((CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE)).setY(y);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMinXP(MinMaxPosition minXP) {
        this.minXP = minXP;
    }

    public void setMaxXP(MinMaxPosition maxXP) {
        this.maxXP = maxXP;
    }

    public void setMinYP(MinMaxPosition minYP) {
        this.minYP = minYP;
    }

    public void setMaxYP(MinMaxPosition maxYP) {
        this.maxYP = maxYP;
    }

    public void setLeftContur(Contur leftContur) {
        this.leftContur = leftContur;
    }

    public void setRightContur(Contur rightContur) {
        this.rightContur = rightContur;
    }

    public void setChildren(ArrayList<Object> children) {
        this.children = children;
    }

    public Node getNode() {
        return node;
    }

    public boolean hasBend() {
        return (bendTarget != null);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    /**
     * Returns the maxDownDiag.
     * 
     * @return the maxDownDiag.
     */
    public MinMaxPosition getMaxDownDiag() {
        return maxDownDiag;
    }

    /**
     * Sets the maxDownDiag.
     * 
     * @param maxDownDiag
     *            the maxDownDiag to set.
     */
    public void setMaxDownDiag(MinMaxPosition maxDownDiag) {
        this.maxDownDiag = maxDownDiag;
    }

    /**
     * Returns the maxUpDiag.
     * 
     * @return the maxUpDiag.
     */
    public MinMaxPosition getMaxUpDiag() {
        return maxUpDiag;
    }

    /**
     * Sets the maxUpDiag.
     * 
     * @param maxUpDiag
     *            the maxUpDiag to set.
     */
    public void setMaxUpDiag(MinMaxPosition maxUpDiag) {
        this.maxUpDiag = maxUpDiag;
    }

    /**
     * Returns the minDownDiag.
     * 
     * @return the minDownDiag.
     */
    public MinMaxPosition getMinDownDiag() {
        return minDownDiag;
    }

    /**
     * Sets the minDownDiag.
     * 
     * @param minDownDiag
     *            the minDownDiag to set.
     */
    public void setMinDownDiag(MinMaxPosition minDownDiag) {
        this.minDownDiag = minDownDiag;
    }

    /**
     * Returns the minUpDiag.
     * 
     * @return the minUpDiag.
     */
    public MinMaxPosition getMinUpDiag() {
        return minUpDiag;
    }

    /**
     * Sets the minUpDiag.
     * 
     * @param minUpDiag
     *            the minUpDiag to set.
     */
    public void setMinUpDiag(MinMaxPosition minUpDiag) {
        this.minUpDiag = minUpDiag;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
