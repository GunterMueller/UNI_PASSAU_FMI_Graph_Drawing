// =============================================================================
//
//   CoordinateAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CoordinateAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.geom.Point2D;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;

/**
 * Contains the coordinate graphic attribute.
 * 
 * @author breu
 * @version $Revision: 5768 $
 */
public class CoordinateAttribute extends LinkedHashMapAttribute implements
        GraphicAttributeConstants {

    /** Contains horizontal coordinate */
    private DoubleAttribute x;

    /** Contains vertical coordinate */
    private DoubleAttribute y;

    /**
     * Contains depth coordinate. Must be greater than or equal to 0 and less
     * than 1. Higher values represent locations farther from the viewer, which
     * corresponds to a right-hand coordinate system.
     */
    private DoubleAttribute z;

    /**
     * Constructor for Coordinate that sets the coordinates to a random number.
     * 
     * @param id
     *            the id of the attribute.
     */
    public CoordinateAttribute(String id) {
        this(id, Math.random() * 400, Math.random() * 400);
    }

    /**
     * Constructor for Coordinate.
     * 
     * @param id
     *            the id of the attribute.
     * @param c
     *            the coordinate-value of the attriubte.
     */
    public CoordinateAttribute(String id, Point2D c) {
        this(id, c.getX(), c.getY());
    }

    /**
     * Constructor for Coordinate.
     * 
     * @param id
     *            the id of the attribute.
     * @param x
     *            the x-value of the attribute.
     * @param y
     *            the y-value of the attribute.
     */
    public CoordinateAttribute(String id, double x, double y) {
        this(id, x, y, 0.0);
    }

    public CoordinateAttribute(String id, double x, double y, double z) {
        super(id);
        this.x = new DoubleAttribute(X, x);
        this.y = new DoubleAttribute(Y, y);
        this.z = new DoubleAttribute(Z, z);
        this.add(this.x, false);
        this.add(this.y, false);
        this.add(this.z, false);
    }

    /**
     * Sets the collection of attributes contained within this
     * <tt>CollectionAttribute</tt>. The coordinate values are set, additional
     * values are simply added (that means that if there exists already a
     * subattribute with the same id, an exception will be thrown).
     * 
     * @param attrs
     *            the map that contains all attributes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    @Override
    public void setCollection(Map<String, Attribute> attrs) {

        for (String attrId : attrs.keySet()) {
            if (attrId.equals(X)) {
                setX(((DoubleAttribute) attrs.get(X)).getDouble());
            } else if (attrId.equals(Y)) {
                setY(((DoubleAttribute) attrs.get(Y)).getDouble());
            } else if (attrId.equals(Z)) {
                setZ(((DoubleAttribute) attrs.get(Z)).getDouble());
            } else {
                this.add((Attribute) attrs.get(attrId).copy());
            }
        }

    }

    /**
     * Sets the x and y values of this coordinate to the given points' values.
     * 
     * @param p
     *            <code>Point2D</code> to which this coordinate should be set.
     */
    public void setCoordinate(Point2D p) {
        this.x.setDouble(p.getX());
        this.y.setDouble(p.getY());
    }

    /**
     * Returns the encapsulated coordinate.
     * 
     * @return the encapsulated coordinate.
     */
    public Point2D getCoordinate() {
        return new Point2D.Double(this.getX(), this.getY());
    }

    /**
     * Sets the depth coordinate.
     * 
     * @param depth
     *            the new depth coordinate.
     * @see #z
     */
    public void setDepth(double depth) {
        this.z.setValue(depth);
    }

    /**
     * Returns the depth coordinate.
     * 
     * @return the depth coordinate.
     * @see #z
     */
    public double getDepth() {
        return z.getDouble();
    }

    /**
     * Sets the 'x1'-value.
     * 
     * @param x
     *            the 'x1'-value to be set.
     */
    public void setX(double x) {
        this.x.setDouble(x);
    }

    /**
     * Returns the 'x'-value of the encapsulated coordinate.
     * 
     * @return the 'x'-value of the encapsulated coordinate.
     */
    public double getX() {
        return this.x.getDouble();
    }

    /**
     * Sets the 'x2'-value.
     * 
     * @param y
     *            the 'x2'-value to be set.
     */
    public void setY(double y) {
        this.y.setDouble(y);
    }

    /**
     * Returns the 'y'-value of the encapsulated coordinate.
     * 
     * @return the 'y'-value of the encapsulated coordinate.
     */
    public double getY() {
        return this.y.getDouble();
    }

    /**
     * Sets the 'x3'-value.
     * 
     * @param z
     *            the 'x3'-value to be set.
     */
    public void setZ(double z) {
        this.z.setDouble(z);
    }

    /**
     * Returns the 'z'-value of the encapsulated coordinate.
     * 
     * @return the 'z'-value of the encapsulated coordinate.
     */
    public double getZ() {
        return this.z.getDouble();
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        CoordinateAttribute copied = new CoordinateAttribute(this.getId());
        copied.setX(this.getX());
        copied.setY(this.getY());
        copied.setZ(this.getZ());
        return copied;
    }

    // /**
    // * Sets the value of this <code>Attribute</code> to the given value
    // without
    // * informing the <code>ListenerManager</code>.
    // *
    // * @param v the new value.
    // *
    // * @exception IllegalArgumentException if <code>v</code> is not of the
    // * apropriate type.
    // */
    // protected void doSetValue(Object v)
    // throws IllegalArgumentException {
    // if(v instanceof Point2D) {
    // Point2D coord = (Point2D) v;
    // setCoordinate(coord);
    // } else if(v instanceof Map) {
    // Map map = (Map) v;
    // setCollection(map);
    // } else {
    // throw new IllegalArgumentException("Invalid value type.");
    // }
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
