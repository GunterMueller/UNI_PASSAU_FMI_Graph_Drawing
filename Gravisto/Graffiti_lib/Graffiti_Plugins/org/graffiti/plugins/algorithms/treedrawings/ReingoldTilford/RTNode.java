// =============================================================================
//
//   RTNode.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RTNode.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * @author Beiqi
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class RTNode {
    private Node node;

    private double height;

    private double width;

    private double x;

    private double y;

    private int level;

    private int numberOfChildren;

    private MinMaxXPosition minXP;

    private MinMaxXPosition maxXP;

    private Contur leftContur;

    private Contur rightContur;

    private ArrayList<Object> children;

    public RTNode(Node node) {
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
        this.minXP = new MinMaxXPosition();
        this.maxXP = new MinMaxXPosition();
        leftContur = new Contur();
        rightContur = new Contur();

        Collection<Node> outNeighbour = node.getOutNeighbors();
        children = new ArrayList<Object>(outNeighbour);
        Collections.sort(children, new XCoordComparator());
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

    public MinMaxXPosition getMinXP() {
        return minXP;
    }

    public MinMaxXPosition getMaxXP() {
        return maxXP;
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

    public void setMinXP(MinMaxXPosition minXP) {
        this.minXP = minXP;
    }

    public void setMaxXP(MinMaxXPosition maxXP) {
        this.maxXP = maxXP;
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
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
