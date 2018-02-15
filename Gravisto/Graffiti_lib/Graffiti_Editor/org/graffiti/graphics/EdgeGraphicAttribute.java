// =============================================================================
//
//   EdgeGraphicAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeGraphicAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.util.Map;
import java.util.regex.Pattern;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.EdgeShapeAttribute;
import org.graffiti.attributes.IllegalIdException;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.attributes.StringAttribute;

/**
 * Defines all graphic attributes of an edge
 * 
 * @author breu
 * @version $Revision: 5768 $
 */
public class EdgeGraphicAttribute extends GraphElementGraphicAttribute {

    /** Object for docking specification of an edge at source and target nodes */
    private DockingAttribute docking;

    /** The thickness of the edge without edge frame */
    private DoubleAttribute thickness;

    /**
     * Collection of all <code>CoordinateAttribute</code>s for the bends for the
     * edge.
     */
    private SortedCollectionAttribute bends;

    /** The class name used to generate an arrow at the target node. */
    private StringAttribute arrowhead;

    /** The class name used to generate an arrow at the source node. */
    private StringAttribute arrowtail;

    /** Specifies the type of the line (e.g. polyline or spline types). */
    private StringAttribute lineType;

    /**
     * The depth coordinate. Must be greater than or equal to 0 and less than 1.
     * Higher values represent locations farther from the viewer, which
     * corresponds to a right-hand coordinate system.
     */
    private DoubleAttribute depth;

    /**
     * Constructs an EdgeGraphicAttribute and initializes all its members.
     * 
     * @param ah
     *            the arrowhead-value of the attribute.
     * @param at
     *            the arrowtail-value of the attribute.
     * @param t
     *            the thickness-value of the attribute.
     * @param d
     *            the docking-value of the attribute.
     * @param b
     *            the <code>CollectionAttriubte</code> containing the bends.
     * @param lt
     *            the lineType of the edge.
     */
    public EdgeGraphicAttribute(String ah, String at, double t,
            DockingAttribute d, LinkedHashMapAttribute b, String lt) {
        super(GRAPHICS);
        setFillcolor(new ColorAttribute(FILLCOLOR, DEFAULT_EDGE_FILLCOLOR));
        setFrameThickness(DEFAULT_EDGE_FRAMETHICKNESS);
        arrowhead = new StringAttribute(ARROWHEAD, ah);
        arrowtail = new StringAttribute(ARROWTAIL, at);
        thickness = new DoubleAttribute(THICKNESS, t);
        docking = (DockingAttribute) d.copy();
        bends = (SortedCollectionAttribute) b.copy();
        lineType = new StringAttribute(LINETYPE, lt);
        depth = new DoubleAttribute(DEPTH, 0.0);

        // is this correct? jf - now it is! ph
        shape = new EdgeShapeAttribute(SHAPE,
                "org.graffiti.plugins.views.defaults.StraightLineEdgeShape");
        add(this.arrowhead, false);
        add(this.arrowtail, false);
        add(this.thickness, false);
        add(this.docking, false);
        add(this.bends, false);
        add(this.lineType, false);
        add(this.depth, false);
        add(this.shape, false);
    }

    /**
     * Constructs an EdgeGraphicAttribute and initializes all its members.
     * 
     * @throws IllegalIdException
     */
    public EdgeGraphicAttribute() throws IllegalIdException {
        this("", "", GraphicAttributeConstants.DEFAULT_EDGE_THICKNESS,
                new DockingAttribute(DOCKING),
                new LinkedHashMapAttribute(BENDS), "");
    }

    /**
     * Constructs an EdgeGraphicAttribute and initializes all its members.
     * 
     * @param directed
     *            indicates whether the edge is directed
     * 
     * @throws IllegalIdException
     */
    public EdgeGraphicAttribute(boolean directed) throws IllegalIdException {
        this();
        if (directed) {
            arrowhead.setString(GraphicAttributeConstants.ARROWSHAPE_CLASSNAME);
        }
    }

    /**
     * Constructs an EdgeGraphicAttribute and initializes all its members.
     * 
     * @param ah
     *            the arrowhead-value of the attribute.
     * @param at
     *            the arrowtail-value of the attribute.
     * @param t
     *            the thickness-value of the attribute.
     * @param d
     *            the docking-value of the attribute.
     * 
     * @throws IllegalIdException
     */
    public EdgeGraphicAttribute(String ah, String at, double t,
            DockingAttribute d) throws IllegalIdException {
        this(ah, at, t, d, new LinkedHashMapAttribute(BENDS), "");
    }

    /**
     * Constructs an EdgeGraphicAttribute and initializes all its members.
     * 
     * @param ah
     *            the arrowhead-value of the attribute.
     * @param at
     *            the arrowtail-value of the attribute.
     * @param t
     *            the thickness-value of the attribute.
     * @param d
     *            the docking-value of the attribute.
     * 
     * @throws IllegalIdException
     */
    public EdgeGraphicAttribute(String ah, String at, DoubleAttribute t,
            DockingAttribute d) throws IllegalIdException {
        this(ah, at, t.getDouble(), d, new LinkedHashMapAttribute(BENDS), "");
    }

    /**
     * Constructs an EdgeGraphicAttribute and initializes all its members.
     * 
     * @param ah
     *            the arrowhead-value of the attribute.
     * @param at
     *            the arrowtail-value of the attribute.
     * @param t
     *            the thickness-value of the attribute.
     * @param d
     *            the docking-value of the attribute.
     * 
     * @throws IllegalIdException
     */
    public EdgeGraphicAttribute(StringAttribute ah, StringAttribute at,
            double t, DockingAttribute d) throws IllegalIdException {
        this(ah.getString(), at.getString(), t, d, new LinkedHashMapAttribute(
                BENDS), "");
    }

    /**
     * Sets the 'arrowhead'-value.
     * 
     * @param ah
     *            the 'arrowhead'-value to be set.
     */
    public void setArrowhead(String ah) {
        this.arrowhead.setString(ah);
    }

    /**
     * Returns the 'arrowhead'-value of the encapsulated edge.
     * 
     * @return the 'arrowhead'-value of the encapsulated edge.
     */
    public String getArrowhead() {
        return this.arrowhead.getString();
    }

    /**
     * Sets the 'arrowtail'-value.
     * 
     * @param at
     *            the 'arrowtail'-value to be set.
     */
    public void setArrowtail(String at) {
        this.arrowtail.setString(at);
    }

    /**
     * Returns the 'arrowtail'-value of the encapsulated edge.
     * 
     * @return the 'arrowtail'-value of the encapsulated edge.
     */
    public String getArrowtail() {
        return this.arrowtail.getString();
    }

    /**
     * Sets the 'bends'-value.
     * 
     * @param b
     *            the 'bends'-value to be set.
     */
    public void setBends(SortedCollectionAttribute b) {
        this.bends.setCollection(b.getCollection());
    }

    /**
     * Returns the collection of <code>CoordinateAttribute</code>s specifying
     * the bends for this edge.
     * 
     * @return the collection of <code>CoordinateAttribute</code>s specifying
     *         the bends for this edge.
     */
    public SortedCollectionAttribute getBends() {
        return this.bends;
    }

    public int getNumberOfBends() {
        // We add 1 in the end
        int maxBendNum = -1;

        for (String key : bends.getCollection().keySet()) {
            if (Pattern.matches("bend[0-9]+?", key)) {
                // 5 is position of the first digit after "bends"
                String numberString = key.substring(4);
                int currBendNum = Integer.parseInt(numberString);
                if (currBendNum > maxBendNum) {
                    maxBendNum = currBendNum;
                }
            }
        }

        return maxBendNum + 1;
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

        for (String attrId : attrs.keySet()) {
            if (attrId.equals(ARROWHEAD)) {
                setArrowhead(((StringAttribute) attrs.get(ARROWHEAD))
                        .getString());
            } else if (attrId.equals(ARROWTAIL)) {
                setArrowtail(((StringAttribute) attrs.get(ARROWTAIL))
                        .getString());
            } else if (attrId.equals(BENDS)) {
                setBends((SortedCollectionAttribute) attrs.get(BENDS));
            } else if (attrId.equals(DOCKING)) {
                setDocking((DockingAttribute) attrs.get(DOCKING));
            } else if (attrId.equals(THICKNESS)) {
                setThickness(((DoubleAttribute) attrs.get(THICKNESS))
                        .getDouble());
            } else if (attrId.equals(LINETYPE)) {
                setLineType(((StringAttribute) attrs.get(LINETYPE)).getString());
            } else if (attrId.equals(BGIMAGE)) {
                setBackgroundImage((ImageAttribute) attrs.get(BGIMAGE));
            } else if (attrId.equals(FRAMECOLOR)) {
                setFramecolor((ColorAttribute) attrs.get(FRAMECOLOR));
            } else if (attrId.equals(FILLCOLOR)) {
                setFillcolor((ColorAttribute) attrs.get(FILLCOLOR));
            } else if (attrId.equals(FRAMETHICKNESS)) {
                setFrameThickness(((DoubleAttribute) attrs.get(FRAMETHICKNESS))
                        .getDouble());
            } else if (attrId.equals(LINEMODE)) {
                setLineMode((LineModeAttribute) attrs.get(LINEMODE));
            } else if (attrId.equals(DEPTH)) {
                setDepth(((DoubleAttribute) attrs.get(DEPTH)).getDouble());
            } else if (attrId.equals(SHAPE)) {
                setShape(((StringAttribute) attrs.get(SHAPE)).getString());
            } else {
                this.add((Attribute) attrs.get(attrId).copy());
            }
        }

    }

    /**
     * Sets the 'docking'-value.
     * 
     * @param d
     *            the 'docking'-value to be set.
     */
    public void setDocking(DockingAttribute d) {
        this.docking.setSource(d.getSource());
        this.docking.setTarget(d.getTarget());
    }

    /**
     * Returns the 'docking'-value of the encapsulated edge.
     * 
     * @return the 'docking'-value of the encapsulated edge.
     */
    public DockingAttribute getDocking() {
        return this.docking;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param l
     */
    public void setLineType(String l) {
        this.lineType.setString(l);
    }

    /**
     * Returns the line type.
     * 
     * @return DOCUMENT ME!
     */
    public String getLineType() {
        return this.lineType.getString();
    }

    /**
     * Sets the 'thickness'-value.
     * 
     * @param t
     *            the 'thickness'-value of the encapsulated edge.
     */
    public void setThickness(double t) {
        this.thickness.setDouble(t);
    }

    /**
     * Returns the 'thickness'-value of the encapsulated edge.
     * 
     * @return the 'thickness'-value of the encapsulated edge.
     */
    public double getThickness() {
        return this.thickness.getDouble();
    }

    /**
     * Sets the 'depth'-value.
     * 
     * @param depth
     *            the 'depth'-value of the encapsulated edge.
     */
    public void setDepth(double depth) {
        this.depth.setDouble(depth);
    }

    /**
     * Returns the 'depth'-value of the encapsulated edge.
     * 
     * @return the 'depth'-value of the encapsulated edge.
     */
    public double getDepth() {
        return this.depth.getDouble();
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        EdgeGraphicAttribute copied = new EdgeGraphicAttribute();

        for (Attribute attr : attributes.values()) {
            Attribute copAttr = (Attribute) attr.copy();

            try {
                Attribute exAttr = copied.getAttribute(copAttr.getId());
                exAttr.setValue(copAttr.getValue());
            } catch (AttributeNotFoundException anfe) {
                copied.add(copAttr, false);
            }
        }

        // // first setting the subattributes defined in
        // // GraphElementGrapichAttribute
        // copied.setBackgroundImage((ImageAttribute) this.getBackgroundImage()
        // .copy());
        // copied.setFramecolor((ColorAttribute) this.getFramecolor().copy());
        // copied.setFillcolor((ColorAttribute) this.getFillcolor().copy());
        // copied.setFrameThickness(this.getFrameThickness());
        // copied.setLineMode((LineModeAttribute) this.getLineMode().copy());
        // copied.setShape(this.getShape());
        //
        // copied.setArrowhead(this.getArrowhead());
        // copied.setArrowtail(this.getArrowtail());
        // copied.setBends((SortedCollectionAttribute) this.getBends().copy());
        // copied.setDocking((DockingAttribute) this.getDocking().copy());
        // copied.setLineType(this.getLineType());
        // copied.setThickness(this.getThickness());
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
    // EdgeGraphicAttribute tmp;
    //
    // try
    // {
    // tmp = (EdgeGraphicAttribute) v;
    // }
    // catch(ClassCastException cce)
    // {
    // throw new IllegalArgumentException("Invalid value type.");
    // }
    //
    // setShape(tmp.getShape());
    // setBackgroundImage(tmp.getBackgroundImage());
    // setFramecolor(tmp.getFramecolor());
    // setFillcolor(tmp.getFillcolor());
    // setFrameThickness(tmp.getFrameThickness());
    // setLineMode(tmp.getLineMode());
    // setArrowhead(tmp.getArrowhead());
    // setArrowtail(tmp.getArrowtail());
    // setThickness(tmp.getThickness());
    // setDocking(tmp.getDocking());
    // setBends(tmp.getBends());
    // setLineType(tmp.getLineType());
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
