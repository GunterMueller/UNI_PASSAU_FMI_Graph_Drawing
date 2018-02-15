// =============================================================================
//
//   LabelAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LabelAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.util.HashMap;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;

/**
 * Contains the graphic attribute label
 * 
 * @version $Revision: 5768 $
 */
public class LabelAttribute extends HashMapAttribute implements
        GraphicAttributeConstants {

    /** Holds color of label text. */
    protected ColorAttribute textcolor;

    // /**
    // * Position of the label within this graph element.
    // */
    // protected PositionAttribute position;

    /** Holds font of label text. */
    protected StringAttribute font;

    /** Holds the name of the label. */
    protected StringAttribute label;

    /** Holds the size of the font */
    protected IntegerAttribute fontSize;

    /** Holds the maximum width of the label. */
    protected DoubleAttribute maxWidth;

    /**
     * Caches previously used fonts in the getLabelHeight and getLabelWidth
     * method to provide faster access.
     */
    private static Map<String, Font> fontCache;

    /**
     * Instance of FontRenderContext used to determine label height and width.
     */
    private static FontRenderContext fontRenderContext;

    /**
     * Constructor for Label.
     * 
     * @param id
     *            the id of the attribute.
     */
    public LabelAttribute(String id) {
        super(id);
        this.label = new StringAttribute(LABEL, "");

        // this.position = new PositionAttribute(POSITION);
        this.font = new StringAttribute(FONT, "");
        this.textcolor = new ColorAttribute(TEXTCOLOR, java.awt.Color.BLACK);
        this.fontSize = new IntegerAttribute(FONT_SIZE, DEFAULT_FONT_SIZE);
        this.maxWidth = new DoubleAttribute(MAX_WIDTH, DEFAULT_MAX_WIDTH);
        add(this.fontSize, false);
        add(this.label, false);

        // add(this.position, false);
        add(this.font, false);
        add(this.textcolor, false);

        add(this.maxWidth, false);
    }

    /**
     * Constructor for Label.
     * 
     * @param id
     *            the id of the attribute.
     * @param l
     *            the label-value of the attribute.
     */
    public LabelAttribute(String id, String l) {
        super(id);
        this.label = new StringAttribute(LABEL, l);

        // this.position = new PositionAttribute(POSITION);
        this.font = new StringAttribute(FONT, "");
        this.textcolor = new ColorAttribute(TEXTCOLOR, java.awt.Color.BLACK);
        this.fontSize = new IntegerAttribute(FONT_SIZE, DEFAULT_FONT_SIZE);
        this.maxWidth = new DoubleAttribute(MAX_WIDTH, DEFAULT_MAX_WIDTH);
        add(this.fontSize, false);
        add(this.label, false);

        // add(this.position, false);
        add(this.font, false);
        add(this.textcolor, false);

        add(this.maxWidth, false);
    }

    /**
     * Constructor for Label.
     * 
     * @param id
     *            the id of the attribute.
     * @param l
     *            the label-value of the attribute.
     * @param p
     *            the position-value of the attribute.
     * @param a
     *            the alignment-value of the attribute.
     * @param f
     *            the font-value of the attribute.
     * @param tc
     *            the textcolor-value of the attribute.
     */
    public LabelAttribute(String id, String l, PositionAttribute p, String a,
            String f, ColorAttribute tc) {
        super(id);
        this.label = new StringAttribute(LABEL, l);

        // this.position = (PositionAttribute) p.copy();
        this.font = new StringAttribute(FONT, f);
        this.textcolor = new ColorAttribute(TEXTCOLOR, tc.getColor());
        this.fontSize = new IntegerAttribute(FONT_SIZE, DEFAULT_FONT_SIZE);
        this.maxWidth = new DoubleAttribute(MAX_WIDTH, DEFAULT_MAX_WIDTH);
        add(this.fontSize, false);
        add(this.label, false);

        // add(this.position, false);
        add(this.font, false);
        add(this.textcolor, false);

        add(this.maxWidth, false);
    }

    /**
     * Constructor for Label.
     * 
     * @param id
     *            the id of the attribute.
     * @param l
     *            the label-value of the attribute.
     * @param p
     *            the position-value of the attribute.
     * @param a
     *            the alignment-value of the attribute.
     * @param f
     *            the font-value of the attribute.
     * @param tc
     *            the textcolor-value of the attribute.
     */
    public LabelAttribute(String id, StringAttribute l, PositionAttribute p,
            StringAttribute a, StringAttribute f, ColorAttribute tc) {
        super(id);
        this.label = new StringAttribute(LABEL, l.getString());

        // this.position = (PositionAttribute) p.copy();
        this.font = new StringAttribute(FONT, f.getString());
        this.textcolor = tc;
        this.fontSize = new IntegerAttribute(FONT_SIZE, DEFAULT_FONT_SIZE);
        this.maxWidth = new DoubleAttribute(MAX_WIDTH, DEFAULT_MAX_WIDTH);
        add(this.fontSize, false);
        add(this.label, false);

        // add(this.position, false);
        add(this.font, false);
        add(this.textcolor, false);

        add(this.maxWidth, false);
    }

    /**
     * Constructor for Label.
     * 
     * @param id
     *            the id of the attribute.
     * @param l
     *            the label-value of the attribute.
     * @param p
     *            the position-value of the attribute.
     * @param a
     *            the alignment-value of the attribute.
     * @param f
     *            the font-value of the attribute.
     * @param tc
     *            the textcolor-value of the attribute.
     */
    public LabelAttribute(String id, String l, PositionAttribute p, String a,
            String f, java.awt.Color tc) {
        super(id);
        this.label = new StringAttribute(LABEL, l);

        // this.position = (PositionAttribute) p.copy();
        this.font = new StringAttribute(FONT, f);
        this.textcolor = new ColorAttribute(TEXTCOLOR, tc);
        this.fontSize = new IntegerAttribute(FONT_SIZE, DEFAULT_FONT_SIZE);
        this.maxWidth = new DoubleAttribute(MAX_WIDTH, DEFAULT_MAX_WIDTH);
        add(this.fontSize, false);
        add(this.label, false);

        // add(this.position, false);
        add(this.font, false);
        add(this.textcolor, false);

        add(this.maxWidth, false);
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
            if (attrId.equals(LABEL)) {
                setLabel(((StringAttribute) attrs.get(LABEL)).getString());

                // } else if (attrId.equals(POSITION)) {
                // setPosition((PositionAttribute) attrs.get(POSITION));
            } else if (attrId.equals(FONT)) {
                setFont(((StringAttribute) attrs.get(FONT)).getString());
            } else if (attrId.equals(TEXTCOLOR)) {
                setTextcolor((CollectionAttribute) attrs.get(TEXTCOLOR));
            } else if (attrId.equals(FONT_SIZE)) {
                setFontSize(((IntegerAttribute) attrs.get(FONT_SIZE))
                        .getInteger());
            } else if (attrId.equals(MAX_WIDTH)) {
                setMaxWidth(((DoubleAttribute) attrs.get(MAX_WIDTH))
                        .getDouble());
            } else {
                this.add((Attribute) attrs.get(attrId).copy());
            }
        }
    }

    /**
     * Sets the 'font'-value of the encapsulated label.
     * 
     * @param f
     *            the 'font'-value to be set.
     */
    public void setFont(String f) {
        this.font.setString(f);
    }

    /**
     * Returns the 'font'-value of the encapsulated label.
     * 
     * @return the 'font'-value of the encapsulated label.
     */
    public String getFont() {
        return this.font.getString();
    }

    /**
     * Sets the font size.
     * 
     * @param size
     *            the font size to set.
     */
    public void setFontSize(int size) {
        this.fontSize.setInteger(size);
    }

    /**
     * Returns the font size.
     * 
     * @return the font size.
     */
    public int getFontSize() {
        return this.fontSize.getInteger();
    }

    /**
     * Sets the maximum width.
     * 
     * @param maxWidth
     *            the maximum width to set.
     */
    public void setMaxWidth(double maxWidth) {
        this.maxWidth.setDouble(maxWidth);
    }

    /**
     * Returns the maximum width.
     * 
     * @return the maximum width.
     */
    public double getMaxWidth() {
        return maxWidth.getDouble();
    }

    /**
     * The width of this LabelAttribute prior to rotation.
     * 
     * @return The width of label prior to rotation.
     */
    public double getWidth() {
        String fontName = this.getFont();
        Font font = getFont(fontName);

        if (font.getSize() != this.getFontSize()) {
            font = font.deriveFont((float) this.getFontSize());
        }

        double fontWidth = 0;

        FontRenderContext frc = new FontRenderContext(null, true, true);

        String[] lines = this.getLabel().split("<[bB][rR]>");
        for (String line : lines) {
            int currentWidth = (int) Math.ceil(font.getStringBounds(line, frc)
                    .getWidth());
            if (currentWidth > fontWidth) {
                fontWidth = currentWidth;
            }
        }

        return fontWidth;
    }

    /**
     * The height of this Label, prior to rotation.
     * 
     * @return The height of the LabelAttribute prior to rotation.
     */
    public double getHeight() {
        assert label != null;

        String fontName = this.getFont();
        Font font = getFont(fontName);

        if (font.getSize() != this.getFontSize()) {
            font = font.deriveFont((float) this.getFontSize());
        }

        if (fontRenderContext == null) {
            fontRenderContext = new FontRenderContext(null, true, true);
        }
        FontRenderContext frc = fontRenderContext;

        String[] lines = this.getLabel().split("<[bB][rR]>");
        double lineHeight = font.getStringBounds(lines[0], frc).getHeight();

        return lines.length * lineHeight;
    }

    /**
     * Sets the 'label'-value.
     * 
     * @param l
     *            the 'label'-value to be set.
     */
    public void setLabel(String l) {
        this.label.setString(l);
    }

    /**
     * Returns the 'label'-value of the encapsulated label.
     * 
     * @return the 'label'-value of the encapsulated label.
     */
    public String getLabel() {
        return this.label.getString();
    }

    /**
     * Set the 'textcolor'-value.
     * 
     * @param tc
     *            the 'textcolor'-value to be set.
     */
    public void setTextcolor(CollectionAttribute tc) {
        textcolor.setCollection(tc.getCollection());
    }

    /**
     * Returns the 'textcolor'-value of the encapsulated label.
     * 
     * @return the 'textcolor'-value of the encapsulated label.
     */
    public ColorAttribute getTextcolor() {
        return this.textcolor;
    }

    /**
     * Returns a deep copy of this object.
     * 
     * @return A deep copy of this object.
     */
    @Override
    public Object copy() {
        LabelAttribute copied = new LabelAttribute(this.getId());
        copied.setLabel(new String(this.getLabel()));

        // copied.setPosition((PositionAttribute) this.getPosition().copy());
        copied.setFont(new String(this.getFont()));
        copied.setFontSize(this.getFontSize());
        copied.setTextcolor((ColorAttribute) this.getTextcolor().copy());
        copied.setMaxWidth(getMaxWidth());
        return copied;
    }

    /**
     * Returns the Font object associated with a given font name.
     * 
     * @param fontName
     *            The name of the font. Must not be null.
     * @return The Font object associated with the given name or the standard
     *         Font, if the specified Font does not exist.
     */
    private Font getFont(String fontName) {
        if (fontName == null) {
            fontName = "";
        }

        Font font = null;

        if (fontCache == null) {
            fontCache = new HashMap<String, Font>();
        }

        // use cached font
        if (fontCache.containsKey(fontName)) {
            font = fontCache.get(fontName);
        } else // find font and cache it
        {
            GraphicsEnvironment ge = GraphicsEnvironment
                    .getLocalGraphicsEnvironment();
            Font[] fonts = ge.getAllFonts();
            for (Font matchingFont : fonts) {
                if (matchingFont.getName().equalsIgnoreCase(fontName)) {
                    font = matchingFont;
                    break;
                }
            }
            if (font == null) {
                // use default font
                font = new Font("", Font.PLAIN, 12);
            }

            // cache font
            fontCache.put(fontName, font);
        }

        return font;
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
    // LabelAttribute labelAttr = null;
    //
    // try
    // {
    // labelAttr = (LabelAttribute) v;
    // }
    // catch(ClassCastException cce)
    // {
    // throw new IllegalArgumentException("Invalid value type.");
    // }
    //
    // this.label = new StringAttribute(LABEL, labelAttr.getLabel());
    //
    // // this.position =
    // // (PositionAttribute) ((LabelAttribute) v).getPosition().copy();
    // this.alignment = new StringAttribute(ALIGNMENT,
    // labelAttr.getAlignment());
    // this.alignment.setDescription("A string constant describing " +
    // "predefined positions of the label.");
    // this.font = new StringAttribute(FONT, labelAttr.getFont());
    // this.textcolor = (ColorAttribute) labelAttr.getTextcolor().copy();
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
