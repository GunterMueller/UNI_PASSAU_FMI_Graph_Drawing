// =============================================================================
//
//   EdgeLabelPositionAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeLabelPositionAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.IntegerAttribute;

/**
 * DOCUMENT ME!
 * 
 * @author holleis
 * @version $Revision: 5768 $ Specifies position of an edge label providing
 *          several parameters.
 */
public class EdgeLabelPositionAttribute extends PositionAttribute {

    /**
     * Specifies horizontal shift (from position given by <code>relAlign</code>
     * and <code>alignSegment</code>) of center of label.
     */
    private IntegerAttribute absoluteXOffset;

    /**
     * Specifies vertical shift (from position given by <code>relAlign</code>
     * and <code>alignSegment</code>) of center of label.
     */
    private IntegerAttribute absoluteYOffset;

    /**
     * Specifies alignment of the label relative to length of edge or edge
     * segment (whose number is given by <code>alignSegment</code>). Zero means
     * close to source, one means close to target.
     */
    private DoubleAttribute relativeAlignment;

    /**
     * Specifies the number of the line segment relative to which
     * <code>relAlign</code> works. Zero means relative to whole edge.
     */
    private IntegerAttribute alignmentSegment;

    /**
     * Constructor for NodeLabelPositionAttribute.
     * 
     * @param id
     */
    public EdgeLabelPositionAttribute(String id) {
        this(id, 0.5d, 0, 0, 0);
    }

    /**
     * Constructor for NodeLabelPositionAttribute.
     * 
     * @param id
     * @param relativeAlign
     * @param alignSeg
     * @param absoluteXOffset
     * @param absoluteYOffset
     */
    public EdgeLabelPositionAttribute(String id, double relativeAlign,
            int alignSeg, int absoluteXOffset, int absoluteYOffset) {
        this(id, new DoubleAttribute(RELATIVE_ALIGNMENT, relativeAlign),
                new IntegerAttribute(ALIGNMENT_SEGMENT, alignSeg),
                new IntegerAttribute(ABSOLUTE_X_OFFSET, absoluteXOffset),
                new IntegerAttribute(ABSOLUTE_Y_OFFSET, absoluteYOffset));
    }

    /**
     * Constructor for NodeLabelPositionAttribute.
     * 
     * @param id
     * @param relativeAlign
     * @param alignSeg
     * @param absoluteXOffset
     * @param absoluteYOffset
     */
    public EdgeLabelPositionAttribute(String id, DoubleAttribute relativeAlign,
            IntegerAttribute alignSeg, IntegerAttribute absoluteXOffset,
            IntegerAttribute absoluteYOffset) {
        super(id);
        this.relativeAlignment = relativeAlign;
        this.alignmentSegment = alignSeg;
        this.absoluteXOffset = absoluteXOffset;
        this.absoluteYOffset = absoluteYOffset;
        add(this.relativeAlignment, false);
        add(this.alignmentSegment, false);
        add(this.absoluteXOffset, false);
        add(this.absoluteYOffset, false);
    }

    /**
     * Sets the absolute X offset.
     * 
     * @param absoluteXOffset
     *            The absolute X offset to set
     */
    public void setAbsoluteXOffset(int absoluteXOffset) {
        this.absoluteXOffset.setInteger(absoluteXOffset);
    }

    /**
     * Returns the absolute X Offset
     * 
     * @return int the absolute X Offset
     */
    public int getAbsoluteXOffset() {
        return absoluteXOffset.getInteger();
    }

    /**
     * Sets the absolute Y offset.
     * 
     * @param absoluteYOffset
     *            The absolute Y offset to set
     */
    public void setAbsoluteYOffset(int absoluteYOffset) {
        this.absoluteYOffset.setInteger(absoluteYOffset);
    }

    /**
     * Returns the absolute Y Offset
     * 
     * @return int the absolute Y Offset
     */
    public int getAbsoluteYOffset() {
        return absoluteYOffset.getInteger();
    }

    /**
     * Sets the alignSegment.
     * 
     * @param alignSegment
     *            The alignSegment to set
     */
    public void setAlignmentSegment(int alignSegment) {
        this.alignmentSegment.setInteger(alignSegment);
    }

    /**
     * Returns the alignSegment.
     * 
     * @return IntegerAttribute
     */
    public int getAlignmentSegment() {
        return alignmentSegment.getInteger();
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
        if (attrs.keySet().contains(RELATIVE_ALIGNMENT)
                && attrs.keySet().contains(ALIGNMENT_SEGMENT)
                && attrs.keySet().contains(ABSOLUTE_X_OFFSET)
                && attrs.keySet().contains(ABSOLUTE_Y_OFFSET)) {
            for (String attrId : attrs.keySet()) {
                if (attrId.equals(RELATIVE_ALIGNMENT)) {
                    setRelativeAlignment(((DoubleAttribute) attrs
                            .get(RELATIVE_ALIGNMENT)).getDouble());
                } else if (attrId.equals(ALIGNMENT_SEGMENT)) {
                    setAlignmentSegment(((IntegerAttribute) attrs
                            .get(ALIGNMENT_SEGMENT)).getInteger());
                } else if (attrId.equals(ABSOLUTE_X_OFFSET)) {
                    setAbsoluteXOffset(((IntegerAttribute) attrs
                            .get(ABSOLUTE_X_OFFSET)).getInteger());
                } else if (attrId.equals(ABSOLUTE_Y_OFFSET)) {
                    setAbsoluteYOffset(((IntegerAttribute) attrs
                            .get(ABSOLUTE_Y_OFFSET)).getInteger());
                } else {
                    this.add((Attribute) attrs.get(attrId).copy());
                }
            }
        } else if (attrs.keySet().contains("relAlign")
                && attrs.keySet().contains("alignSegment")
                && attrs.keySet().contains("absHor")
                && attrs.keySet().contains("absVert")) {
            // needed for loading older graphs
            for (String attrId : attrs.keySet()) {
                if (attrId.equals("relAlign")) {
                    setRelativeAlignment(((DoubleAttribute) attrs
                            .get("relAlign")).getDouble());
                } else if (attrId.equals("alignSegment")) {
                    setAlignmentSegment(((IntegerAttribute) attrs
                            .get("alignSegment")).getInteger());
                } else if (attrId.equals("absHor")) {
                    setAbsoluteXOffset((int) ((DoubleAttribute) attrs
                            .get("absHor")).getDouble());
                } else if (attrId.equals("absVert")) {
                    setAbsoluteYOffset((int) ((DoubleAttribute) attrs
                            .get("absVert")).getDouble());
                } else {
                    this.add((Attribute) attrs.get(attrId).copy());
                }
            }
        } else
            throw new IllegalArgumentException("Invalid value type.");
    }

    /**
     * Sets the relAlign.
     * 
     * @param relAlign
     *            The relAlign to set
     */
    public void setRelativeAlignment(double relAlign) {
        this.relativeAlignment.setDouble(relAlign);
    }

    /**
     * Returns the relAlign.
     * 
     * @return double
     */
    public double getRelativeAlignment() {
        return relativeAlignment.getDouble();
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        EdgeLabelPositionAttribute copied = new EdgeLabelPositionAttribute(this
                .getId());
        copied.setRelativeAlignment(this.getRelativeAlignment());
        copied.setAlignmentSegment(this.getAlignmentSegment());
        copied.setAbsoluteXOffset(this.getAbsoluteXOffset());
        copied.setAbsoluteYOffset(this.getAbsoluteYOffset());

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
    // EdgeLabelPositionAttribute tmp;
    //
    // try
    // {
    // tmp = (EdgeLabelPositionAttribute) v;
    // }
    // catch(ClassCastException cce)
    // {
    // throw new IllegalArgumentException("Invalid value type.");
    // }
    //
    // this.setRelAlign(tmp.getRelAlign());
    // this.setAlignSegment(tmp.getAlignSegment());
    // this.setAbsHor(tmp.getAbsHor());
    // this.setAbsVert(tmp.getAbsVert());
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
