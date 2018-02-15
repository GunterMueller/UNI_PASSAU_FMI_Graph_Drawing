// =============================================================================
//
//   GraphElementGraphicAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphElementGraphicAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.image.BufferedImage;

import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IllegalIdException;
import org.graffiti.attributes.StringAttribute;

/**
 * Defines the common graphic attributes for nodes and edges
 * 
 * @version $Revision: 5768 $
 */
public abstract class GraphElementGraphicAttribute extends HashMapAttribute
        implements GraphicAttributeConstants {

    /** Color to fill the edge */
    protected ColorAttribute fillcolor;

    /** Color of the graph element frame */
    protected ColorAttribute framecolor;

    /** Thickness of the graph element frame. */
    protected DoubleAttribute frameThickness;

    /** The background image of the graph element */
    protected ImageAttribute backgroundImage;

    /** Mode of the frame line, e.g. dashed */
    protected LineModeAttribute lineMode;

    /** Holds the shape of this edge. */
    protected StringAttribute shape;

    /**
     * Constructor for GraphElementGraphicAttribute.
     * 
     * @param id
     * 
     * @throws IllegalIdException
     */
    public GraphElementGraphicAttribute(String id) throws IllegalIdException {
        super(id);
        this.backgroundImage = new ImageAttribute(BGIMAGE);
        this.framecolor = new ColorAttribute(FRAMECOLOR,
                DEFAULT_GRAPHIC_ELEMENT_FRAMECOLOR);
        this.fillcolor = new ColorAttribute(FILLCOLOR, DEFAULT_NODE_FILLCOLOR);
        this.frameThickness = new DoubleAttribute(FRAMETHICKNESS,
                DEFAULT_GRAPHIC_ELEMENT_FRAMETHICKNESS);
        this.lineMode = new LineModeAttribute(LINEMODE);
        add(backgroundImage, false);
        add(framecolor, false);
        add(fillcolor, false);
        add(frameThickness, false);
        add(lineMode, false);
    }

    /**
     * Constructor for GraphElementGraphicAttribute.
     * 
     * @param id
     *            the id of the attribute.
     * @param i
     *            the backgroundimage-value of the attriubte
     * @param frc
     *            the framecolor-value of the attribute.
     * @param fic
     *            the fillcolor-value of the attribute.
     * @param l
     *            DOCUMENT ME!
     * @param ft
     *            the framethickness-value of the attribute.
     * @param lm
     *            the linemode-value of the attribute.
     * @param s
     *            DOCUMENT ME!
     * 
     * @throws IllegalIdException
     */
    public GraphElementGraphicAttribute(String id, ImageAttribute i,
            ColorAttribute frc, ColorAttribute fic, LabelAttribute l,
            DoubleAttribute ft, LineModeAttribute lm, StringAttribute s)
            throws IllegalIdException {
        super(id);
        this.backgroundImage = new ImageAttribute(BGIMAGE, i.getTiled(), i
                .getMaximize(), i.getImage(), i.getReference());
        this.framecolor = new ColorAttribute(FRAMECOLOR, frc);
        this.fillcolor = new ColorAttribute(FILLCOLOR, fic);
        this.frameThickness = new DoubleAttribute(FRAMETHICKNESS, ft
                .getDouble());
        this.lineMode = new LineModeAttribute(LINEMODE, lm.getValue());
        add(backgroundImage, false);
        add(framecolor, false);
        add(fillcolor, false);
        add(frameThickness, false);
        add(lineMode, false);
    }

    /**
     * Constructor for GraphElementGraphicAttribute.
     * 
     * @param id
     *            the id of the attribute.
     * @param i
     *            the backgroundimage-value of the attriubte
     * @param frc
     *            the framecolor-value of the attribute.
     * @param fic
     *            the fillcolor-value of the attribute.
     * @param l
     *            DOCUMENT ME!
     * @param ft
     *            the framethickness-value of the attribute.
     * @param lm
     *            the linemode-value of the attribute.
     * @param s
     *            DOCUMENT ME!
     * 
     * @throws IllegalIdException
     */
    public GraphElementGraphicAttribute(String id, BufferedImage i,
            java.awt.Color frc, java.awt.Color fic, LabelAttribute l,
            double ft, LineModeAttribute lm, String s)
            throws IllegalIdException {
        super(id);
        this.backgroundImage = new ImageAttribute(BGIMAGE, false, false, i, "");
        this.framecolor = new ColorAttribute(FRAMECOLOR, frc);
        this.fillcolor = new ColorAttribute(FILLCOLOR, fic);
        this.frameThickness = new DoubleAttribute(FRAMETHICKNESS, ft);
        this.lineMode = new LineModeAttribute(LINEMODE, lm.getValue());
        add(backgroundImage, false);
        add(framecolor, false);
        add(fillcolor, false);
        add(frameThickness, false);
        add(lineMode, false);
    }

    /**
     * Sets the 'backgroundimage'-value.
     * 
     * @param bgi
     *            the 'backgroundimage'-value to be set.
     */
    public void setBackgroundImage(ImageAttribute bgi) {
        backgroundImage.setImage(bgi.getImage());
        backgroundImage.setMaximize(bgi.getMaximize());
        backgroundImage.setReference(bgi.getReference());
        backgroundImage.setTiled(bgi.getTiled());
    }

    /**
     * Returns the 'backgroundimage'-value of the encapsulated edge.
     * 
     * @return the 'backgroundimage'-value of the encapsulated edge.
     */
    public ImageAttribute getBackgroundImage() {
        return this.backgroundImage;
    }

    /**
     * Sets the 'fillcolor'-value.
     * 
     * @param fic
     *            the 'fillcolor'-value to be set.
     */
    public void setFillcolor(ColorAttribute fic) {
        this.fillcolor.setColor(fic.getColor());
    }

    /**
     * Returns the 'fillcolor'-value of the encapsulated edge.
     * 
     * @return the 'fillcolor'-value of the encapsulated edge.
     */
    public ColorAttribute getFillcolor() {
        return this.fillcolor;
    }

    /**
     * Sets the 'frameThickness'-value.
     * 
     * @param ft
     *            the 'frameThickness'-value to be set.
     */
    public void setFrameThickness(double ft) {
        this.frameThickness.setDouble(ft);
    }

    /**
     * Returns the 'frameThickness'-value of the encapsulated edge.
     * 
     * @return the 'frameThickness'-value of the encapsulated edge.
     */
    public double getFrameThickness() {
        return this.frameThickness.getDouble();
    }

    /**
     * Sets the 'framecolor'-value.
     * 
     * @param frc
     *            the 'framecolor'-valueto be set.
     */
    public void setFramecolor(ColorAttribute frc) {
        this.framecolor.setColor(frc.getColor());
    }

    /**
     * Returns the 'framecolor'-value of the encapsulated edge.
     * 
     * @return the 'framecolor'-value of the encapsulated edge.
     */
    public ColorAttribute getFramecolor() {
        return this.framecolor;
    }

    /**
     * Sets the 'lineMode'-value.
     * 
     * @param lma
     *            the 'lineMode'-value to be set.
     */
    public void setLineMode(LineModeAttribute lma) {
        this.lineMode.setDashArray(lma.getDashArray());
        this.lineMode.setDashPhase(lma.getDashPhase());
    }

    /**
     * Returns the 'lineMode'-value of the encapsulated edge.
     * 
     * @return the 'lineMode'-value of the encapsulated edge.
     */
    public LineModeAttribute getLineMode() {
        return this.lineMode;
    }

    /**
     * Sets the 'shape'-value.
     * 
     * @param sn
     *            the 'shape'-value to be set.
     */
    public void setShape(String sn) {
        this.shape.setString(sn);
    }

    /**
     * Returns the 'shape'-value of the encapsulated edge.
     * 
     * @return the 'shape'-value of the encapsulated edge.
     */
    public String getShape() {
        return this.shape.getString();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
