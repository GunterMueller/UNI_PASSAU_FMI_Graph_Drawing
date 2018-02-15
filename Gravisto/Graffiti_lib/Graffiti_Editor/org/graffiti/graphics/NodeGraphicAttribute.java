// =============================================================================
//
//   NodeGraphicAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeGraphicAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.IllegalIdException;
import org.graffiti.attributes.NodeShapeAttribute;
import org.graffiti.attributes.StringAttribute;

/**
 * Defines all grahic attributes of a node
 * 
 * @author breu
 * @version $Revision: 5768 $
 */
public class NodeGraphicAttribute extends GraphElementGraphicAttribute {

    /**
     * Holds the coordinate of the node (to be sorted out: left up corner or
     * midpoint?)
     */
    private CoordinateAttribute coordinate;

    /** The surrounding rectangle at the node */
    private DimensionAttribute dimension;

    /** Set of all ports existing at the node */
    private PortsAttribute ports;

    /**
     * Constructor that sets the id to the given value, and initializes the
     * other attributes with default values.
     * 
     * @param id
     *            DOCUMENT ME!
     */
    public NodeGraphicAttribute(String id) {
        super(id);
        this.coordinate = new CoordinateAttribute(COORDINATE);
        this.dimension = new DimensionAttribute(DIMENSION, DEFAULT_NODE_SIZE
                .getHeight(), DEFAULT_NODE_SIZE.getWidth());
        this.ports = new PortsAttribute(PORTS);

        this.shape = new NodeShapeAttribute(SHAPE, DEFAULT_NODE_SHAPE);

        add(this.coordinate, false);
        add(this.dimension, false);
        add(this.ports, false);
        add(this.shape, false);
    }

    /**
     * Constructor for NodeGraphicAttribute.
     * 
     * @param c
     *            the coordinate-value of the attriubte.
     * @param d
     *            the dimension-value of the attribute.
     * @param p
     *            the ports-value of the attribute.
     * 
     * @throws IllegalIdException
     */
    public NodeGraphicAttribute(CoordinateAttribute c, DimensionAttribute d,
            PortsAttribute p) throws IllegalIdException {
        super(GRAPHICS);
        this.coordinate = new CoordinateAttribute(COORDINATE, c.getCoordinate());
        this.dimension = new DimensionAttribute(DIMENSION, d.getDimension());
        this.ports = new PortsAttribute(PORTS, p.getIngoing(), p.getOutgoing(),
                p.getCommon());

        // Paul: is this correct? jf
        this.shape = new NodeShapeAttribute(SHAPE, DEFAULT_NODE_SHAPE);

        add(this.coordinate, false);
        add(this.dimension, false);
        add(this.ports, false);

        add(this.shape, false);
    }

    /**
     * Constructor for NodeGraphicAttribute.
     * 
     * @param c
     *            the coordinate-value of the attriubte.
     * @param d
     *            the dimension-value of the attribute.
     * @param p
     *            the ports-value of the attribute.
     * 
     * @throws IllegalIdException
     */
    public NodeGraphicAttribute(Point2D c, Dimension d, PortsAttribute p)
            throws IllegalIdException {
        super(GRAPHICS);
        this.coordinate = new CoordinateAttribute(COORDINATE, c);
        this.dimension = new DimensionAttribute(DIMENSION, d);
        this.ports = new PortsAttribute(PORTS, p.getIngoing(), p.getOutgoing(),
                p.getCommon());

        // Paul: is this correct? jf
        this.shape = new NodeShapeAttribute(SHAPE, DEFAULT_NODE_SHAPE);

        add(this.dimension, false);
        add(this.ports, false);
        add(this.shape, false);
    }

    public NodeGraphicAttribute(Point2D position, Dimension size)
            throws IllegalIdException {
        this(position, size, new PortsAttribute(PORTS));
    }

    /**
     * Constructor for NodeGraphicAttribute.
     * 
     * @param x
     *            the x-coordinate-value of the attriubte.
     * @param y
     *            the y-coordinate-value of the attriubte.
     * @param h
     *            the height-value of the attribute.
     * @param w
     *            the width-value of the attribute.
     * @param p
     *            the ports-value of the attribute.
     * 
     * @throws IllegalIdException
     */
    public NodeGraphicAttribute(double x, double y, double h, double w,
            PortsAttribute p) throws IllegalIdException {
        super(GRAPHICS);
        this.coordinate = new CoordinateAttribute(COORDINATE, x, y);
        this.dimension = new DimensionAttribute(DIMENSION, h, w);
        this.ports = new PortsAttribute(PORTS, p.getIngoing(), p.getOutgoing(),
                p.getCommon());

        // Paul: is this correct? jf
        this.shape = new NodeShapeAttribute(SHAPE, DEFAULT_NODE_SHAPE);

        add(this.coordinate, false);
        add(this.dimension, false);
        add(this.ports, false);
        add(this.shape, false);
    }

    /**
     * Constructor for NodeGraphicAttribute.
     * 
     * @throws IllegalIdException
     */
    public NodeGraphicAttribute() throws IllegalIdException {
        super(GRAPHICS);
        this.coordinate = new CoordinateAttribute(COORDINATE);
        this.dimension = new DimensionAttribute(DIMENSION, DEFAULT_NODE_SIZE
                .getHeight(), DEFAULT_NODE_SIZE.getWidth());
        this.ports = new PortsAttribute(PORTS);

        // Paul: is this correct? jf
        this.shape = new NodeShapeAttribute(SHAPE, DEFAULT_NODE_SHAPE);

        add(this.coordinate, false);
        add(this.dimension, false);
        add(this.ports, false);

        add(this.shape, false);
    }

    /**
     * Sets the collection of attributes contained within this
     * <tt>CollectionAttribute</tt>. The known graphic attributes are set,
     * additional values are simply added (that means that if there exists
     * already a subattribute with the same id, an exception will be thrown).
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
            if (attrId.equals(COORDINATE)) {
                setCoordinate((CoordinateAttribute) attrs.get(COORDINATE));
            } else if (attrId.equals(DIMENSION)) {
                setDimension((DimensionAttribute) attrs.get(DIMENSION));
            } else if (attrId.equals(SHAPE)) {
                setShape(((StringAttribute) attrs.get(SHAPE)).getString());
            } else if (attrId.equals(PORTS)) {
                setPorts((PortsAttribute) attrs.get(PORTS));
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
            } else {
                this.add((Attribute) attrs.get(attrId).copy());
            }
        }
    }

    /**
     * Sets the 'coordinate'-value.
     * 
     * @param c
     *            the 'coordinate'-value to be set.
     */
    public void setCoordinate(CoordinateAttribute c) {
        this.coordinate.setCoordinate(c.getCoordinate());
    }

    /**
     * Returns the 'coordinate'-value of the encapsulated node.
     * 
     * @return the 'coordinate'-value of the encapsulated node.
     */
    public CoordinateAttribute getCoordinate() {
        return this.coordinate;
    }

    /**
     * Sets the 'dimension'-value.
     * 
     * @param d
     *            the 'dimension'-value to be set.
     */
    public void setDimension(DimensionAttribute d) {
        this.dimension.setDimension(d.getDimension());
    }

    /**
     * Returns the 'dimension'-value of the encapsulated node.
     * 
     * @return the 'dimension'-value of the encapsulated node.
     */
    public DimensionAttribute getDimension() {
        return this.dimension;
    }

    /**
     * Sets the 'ports'-value.
     * 
     * @param p
     *            the 'ports'-value to be set.
     */
    public void setPorts(PortsAttribute p) {
        this.ports.setCommon(p.getCommon());
        this.ports.setIngoing(p.getIngoing());
        this.ports.setOutgoing(p.getOutgoing());
    }

    /**
     * Returns the 'ports'-value of the encapsulated node.
     * 
     * @return the 'ports'-value of the encapsulated node.
     */
    public PortsAttribute getPorts() {
        return this.ports;
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        NodeGraphicAttribute copied = new NodeGraphicAttribute(id);

        for (Attribute attr : attributes.values()) {
            Attribute copAttr = (Attribute) attr.copy();
            try {
                Attribute exAttr = copied.getAttribute(copAttr.getId());
                exAttr.setValue(copAttr.getValue());
            } catch (AttributeNotFoundException anfe) {
                copied.add(copAttr, false);
            }
        }

        // copied.setCoordinate((CoordinateAttribute)
        // this.getCoordinate().copy());
        // copied.setDimension((DimensionAttribute) this.getDimension().copy());
        // copied.setPorts((PortsAttribute) this.getPorts().copy());
        //
        // // also have to copy the subattributes that are defined in
        // // GraphElementGraphicAttribute
        // copied.setBackgroundImage((ImageAttribute) this.getBackgroundImage()
        // .copy());
        // copied.setFramecolor((ColorAttribute) this.getFramecolor().copy());
        // copied.setFillcolor((ColorAttribute) this.getFillcolor().copy());
        // copied.setFrameThickness(this.getFrameThickness());
        // copied.setLineMode((LineModeAttribute) this.getLineMode().copy());
        // copied.setShape(new String(this.getShape()));

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
    // NodeGraphicAttribute tmp;
    //
    // try
    // {
    // tmp = (NodeGraphicAttribute) v;
    // }
    // catch(ClassCastException cce)
    // {
    // throw new IllegalArgumentException("Invalid value type.");
    // }
    //
    // setCoordinate(tmp.getCoordinate());
    // setDimension(tmp.getDimension());
    // setPorts(tmp.getPorts());
    // setShape(tmp.getShape());
    // setBackgroundImage(tmp.getBackgroundImage());
    // setFramecolor(tmp.getFramecolor());
    // setFillcolor(tmp.getFillcolor());
    // setFrameThickness(tmp.getFrameThickness());
    // setLineMode(tmp.getLineMode());
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
