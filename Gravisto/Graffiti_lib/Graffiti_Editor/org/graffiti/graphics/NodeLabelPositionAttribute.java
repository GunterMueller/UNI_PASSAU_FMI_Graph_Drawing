// =============================================================================
//
//   NodeLabelPositionAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeLabelPositionAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;

/**
 * DOCUMENT ME!
 * 
 * @author holleis
 * @version $Revision: 5768 $ Specifies position of a node label providing
 *          several parameters.
 */
public class NodeLabelPositionAttribute extends PositionAttribute {

    private IntegerAttribute absoluteXOffset;
    private IntegerAttribute absoluteYOffset;
    private DoubleAttribute relativeXOffset;
    private DoubleAttribute relativeYOffset;
    private StringAttribute alignmentX;
    private StringAttribute alignmentY;

    /**
     * The Label rotation in degree.
     */
    private DoubleAttribute rotation;

    /**
     * Constructor for NodeLabelPositionAttribute.
     * 
     * @param id
     */
    public NodeLabelPositionAttribute(String id) {
        this(id, CENTERED, CENTERED, 0d, 0d, 0, 0);
    }

    /**
     * Constructor for NodeLabelPositionAttribute.
     */
    public NodeLabelPositionAttribute(String id, String alignmentX,
            String alignmentY, double relativeXOffset, double relativeYOffset,
            int absoluteXOffset, int absoluteYOffset)

    {
        this(id, new StringAttribute(ALIGNMENT_X, alignmentX),
                new StringAttribute(ALIGNMENT_Y, alignmentY),
                new DoubleAttribute(RELATIVE_X_OFFSET, relativeXOffset),
                new DoubleAttribute(RELATIVE_Y_OFFSET, relativeYOffset),
                new IntegerAttribute(ABSOLUTE_X_OFFSET, absoluteXOffset),
                new IntegerAttribute(ABSOLUTE_Y_OFFSET, absoluteYOffset));
    }

    /**
     * Constructor for NodeLabelPositionAttribute.
     */
    public NodeLabelPositionAttribute(String id, StringAttribute alignmentX,
            StringAttribute alignmentY, DoubleAttribute relativeXOffset,
            DoubleAttribute relativeYOffset, IntegerAttribute absoluteXOffset,
            IntegerAttribute absoluteYOffset) {
        super(id);

        this.absoluteXOffset = absoluteXOffset;
        this.absoluteYOffset = absoluteYOffset;
        this.relativeXOffset = relativeXOffset;
        this.relativeYOffset = relativeYOffset;
        this.alignmentX = alignmentX;
        this.alignmentY = alignmentY;

        this.rotation = new DoubleAttribute(ROTATION, 0d);

        add(alignmentX, false);
        add(alignmentY, false);
        add(relativeXOffset, false);
        add(relativeYOffset, false);
        add(absoluteXOffset, false);
        add(absoluteYOffset, false);
        add(rotation, false);
    }

    /**
     * Sets the collection of attributes contained within this
     * <tt>CollectionAttribute</tt>
     * 
     * @param attrs
     *            the map that contains all attributes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    @Override
    public void setCollection(Map<String, Attribute> attrs) {
        if (attrs.keySet().contains(ABSOLUTE_X_OFFSET)
                && attrs.keySet().contains(ABSOLUTE_Y_OFFSET)
                && attrs.keySet().contains(RELATIVE_X_OFFSET)
                && attrs.keySet().contains(RELATIVE_Y_OFFSET)
                && attrs.keySet().contains(ALIGNMENT_X)
                && attrs.keySet().contains(ALIGNMENT_Y)) {
            for (String attrId : attrs.keySet()) {
                if (attrId.equals(ABSOLUTE_X_OFFSET)) {
                    setAbsoluteXOffset(((IntegerAttribute) attrs
                            .get(ABSOLUTE_X_OFFSET)).getInteger());
                } else if (attrId.equals(ABSOLUTE_Y_OFFSET)) {
                    setAbsoluteYOffset(((IntegerAttribute) attrs
                            .get(ABSOLUTE_Y_OFFSET)).getInteger());
                } else if (attrId.equals(RELATIVE_X_OFFSET)) {
                    setRelativeXOffset(((DoubleAttribute) attrs
                            .get(RELATIVE_X_OFFSET)).getDouble());
                } else if (attrId.equals(RELATIVE_Y_OFFSET)) {
                    setRelativeYOffset(((DoubleAttribute) attrs
                            .get(RELATIVE_Y_OFFSET)).getDouble());
                } else if (attrId.equals(ALIGNMENT_X)) {
                    setAlignmentX(((StringAttribute) attrs.get(ALIGNMENT_X))
                            .getString());
                } else if (attrId.equals(ALIGNMENT_Y)) {
                    setAlignmentY(((StringAttribute) attrs.get(ALIGNMENT_Y))
                            .getString());
                } else if (attrId.equals(ROTATION)) {
                    setRotationDegree(((DoubleAttribute) attrs.get(ROTATION))
                            .getDouble());
                }
            }
        } else if (attrs.keySet().contains("relHor")
                && attrs.keySet().contains("relVert")
                && attrs.keySet().contains("localAlign")) {
            // needed for loading older graphs
            for (String attrId : attrs.keySet()) {
                if (attrId.equals("localAlign")) {
                    setAbsoluteXOffset((int) ((DoubleAttribute) attrs
                            .get("localAlign")).getDouble());
                } else if (attrId.equals("relHor")) {
                    setRelativeXOffset(((DoubleAttribute) attrs.get("relHor"))
                            .getDouble());
                } else if (attrId.equals("relVert")) {
                    setRelativeYOffset(((DoubleAttribute) attrs.get("relVert"))
                            .getDouble());
                }
            }
        } else
            throw new IllegalArgumentException("Invalid value type.");
    }

    /**
     * Returns the absolute X offset.
     * 
     * @return int the absolute X offset
     */
    public int getAbsoluteXOffset() {
        return this.absoluteXOffset.getInteger();
    }

    /**
     * Sets the absolute X offset.
     * 
     * @param absoluteXOffset
     *            the new absolute X offset
     */
    public void setAbsoluteXOffset(int absoluteXOffset) {
        this.absoluteXOffset.setInteger(absoluteXOffset);
    }

    /**
     * Returns the absolute Y offset.
     * 
     * @return int the absolute Y offset
     */
    public int getAbsoluteYOffset() {
        return this.absoluteYOffset.getInteger();
    }

    /**
     * Sets the absolute Y offset.
     * 
     * @param absoluteYOffset
     *            the new absolute Y offset
     */
    public void setAbsoluteYOffset(int absoluteYOffset) {
        this.absoluteYOffset.setInteger(absoluteYOffset);
    }

    /**
     * Returns the relative X offset.
     * 
     * @return int the relative X offset
     */
    public double getRelativeXOffset() {
        return this.relativeXOffset.getDouble();
    }

    /**
     * Sets the relative X offset.
     * 
     * @param relativeXOffset
     *            the new relative X offset
     */
    public void setRelativeXOffset(double relativeXOffset) {
        this.relativeXOffset.setDouble(relativeXOffset);
    }

    /**
     * Returns the relative Y offset.
     * 
     * @return int the relative Y offset
     */
    public double getRelativeYOffset() {
        return this.relativeYOffset.getDouble();
    }

    /**
     * Sets the relative Y offset.
     * 
     * @param relativeYOffset
     *            the new relative Y offset
     */
    public void setRelativeYOffset(double relativeYOffset) {
        this.relativeYOffset.setDouble(relativeYOffset);
    }

    /**
     * Returns the X alignment.
     * 
     * @return String the X alignment
     */
    public String getAlignmentX() {
        return this.alignmentX.getString();
    }

    /**
     * Sets the X alignment.
     * 
     * @param alignmentX
     *            the new X alignment
     */
    public void setAlignmentX(String alignmentX) {
        this.alignmentX.setString(alignmentX);
    }

    /**
     * Returns the Y alignment.
     * 
     * @return String the Y alignment
     */
    public String getAlignmentY() {
        return this.alignmentY.getString();
    }

    /**
     * Sets the Y alignment.
     * 
     * @param alignmentY
     *            the new Y alignment
     */
    public void setAlignmentY(String alignmentY) {
        this.alignmentY.setString(alignmentY);
    }

    /**
     * Returns the rotation in degree.
     * 
     * @return The rotation in degree.
     */
    public double getRotationDegree() {
        return rotation.getDouble();
    }

    /**
     * Sets the rotation in degree.
     * 
     * @param degree
     *            The rotation in degree.
     */
    public void setRotationDegree(double degree) {
        rotation.setDouble(degree);
    }

    /**
     * Returns the rotation of this Label in radian.
     * 
     * @return The rotation of this Label in radian.
     */
    public double getRotationRadian() {
        double radian = getRotationDegree() * (Math.PI / 180);
        return radian;
    }

    /**
     * Sets the rotation of this Label.
     * 
     * @param radian
     *            The rotation of the Label, which is to be set.
     */
    public void setRotationRadian(double radian) {
        double degree = radian * (180 / Math.PI);
        this.rotation.setDouble(degree);
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        NodeLabelPositionAttribute copied = new NodeLabelPositionAttribute(this
                .getId());
        copied.setAbsoluteXOffset(this.getAbsoluteXOffset());
        copied.setAbsoluteYOffset(this.getAbsoluteYOffset());
        copied.setRelativeXOffset(this.getRelativeXOffset());
        copied.setRelativeYOffset(this.getRelativeYOffset());
        copied.setAlignmentX(this.getAlignmentX());
        copied.setAlignmentY(this.getAlignmentY());
        copied.setRotationDegree(this.getRotationDegree());

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
    // throws IllegalArgumentException
    // {
    // NodeLabelPositionAttribute tmp;
    //
    // try
    // {
    // tmp = (NodeLabelPositionAttribute) v;
    // }
    // catch(ClassCastException cce)
    // {
    // throw new IllegalArgumentException("Invalid value type.");
    // }
    //
    // this.setRelHor(tmp.getRelHor());
    // this.setRelVert(tmp.getRelVert());
    // this.setLocalAlign(tmp.getLocalAlign());
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
