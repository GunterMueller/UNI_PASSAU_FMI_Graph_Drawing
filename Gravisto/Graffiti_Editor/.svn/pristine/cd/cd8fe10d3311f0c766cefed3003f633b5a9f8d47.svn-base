// =============================================================================
//
//   FRLabelNode.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.labeling;

import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.algorithms.springembedderFR.FRNode;

/**
 * <code>FRLabelNode</code>s resemble labels in a <code>FRGraph</code>.
 * <p>
 * As the original graph stores labels as attributes of nodes and edges and as
 * the <code>FRSpringAlgorithm</code> places nodes, labels have to be treated as
 * nodes with connections to their parents.
 * <p>
 * When the <code>FRGraph</code> is created, for every label a
 * <code>FRLabelNode</code> is generated. At completion of graph rearrangement,
 * <code>FRLabelNode</code>s are retransformed into attributes. <br>
 * When retransforming labels into attributes, this class is used as a tagging
 * class to distinguish between regular nodes and labels.
 * 
 * @author Scholz
 * @see org.graffiti.graphics.LabelAttribute
 */
public abstract class FRLabelNode extends FRNode {

    /** Holds color of label text. */
    protected ColorAttribute textcolor;

    /** Holds font of label text. */
    protected StringAttribute font;

    /** Holds the name of the label. */
    protected StringAttribute label;

    /** Holds the size of the font */
    protected IntegerAttribute fontSize;

    /**
     * Sets height and width of node accourding to <code>label</code>.
     * <p>
     * <b><i>Note:</i></b> As this routine does not consider variable sized
     * letters, it is used only if there is no label manager available. If there
     * is, the label manager should be used to calculate label widths.
     */
    protected void adjustSize() {
        this.setWidth(fontSize.getInteger() * label.getString().length() + 3);
        this.setHeight(fontSize.getInteger() + 2);
    }

    /**
     * @param originalNode
     */
    protected FRLabelNode(Node originalNode) {
        // TODO Auto-generated constructor stub
        super(originalNode);

        this.label = new StringAttribute(GraphicAttributeConstants.LABEL,
                "label");
        this.font = new StringAttribute(GraphicAttributeConstants.FONT, "");
        this.textcolor = new ColorAttribute(
                GraphicAttributeConstants.TEXTCOLOR, java.awt.Color.BLACK);
        this.fontSize = new IntegerAttribute(
                GraphicAttributeConstants.FONT_SIZE,
                GraphicAttributeConstants.DEFAULT_FONT_SIZE);
        adjustSize();
    }

    /**
     * @param originalNode
     * @param movable
     */
    protected FRLabelNode(Node originalNode, boolean movable) {
        super(originalNode, movable);

        this.label = new StringAttribute(GraphicAttributeConstants.LABEL, "");
        this.font = new StringAttribute(GraphicAttributeConstants.FONT, "");
        this.textcolor = new ColorAttribute(
                GraphicAttributeConstants.TEXTCOLOR, java.awt.Color.BLACK);
        this.fontSize = new IntegerAttribute(
                GraphicAttributeConstants.FONT_SIZE,
                GraphicAttributeConstants.DEFAULT_FONT_SIZE);
        adjustSize();
    }

    /**
     * Creates a FRLabelNode with given label values.
     * 
     * @param originalNode
     *            position
     * @param movable
     * @param label
     * @param font
     * @param textcolor
     */
    protected FRLabelNode(Node originalNode, boolean movable, String label,
            String font, ColorAttribute textcolor) {
        super(originalNode, movable);

        this.label = new StringAttribute(GraphicAttributeConstants.LABEL, label);
        this.font = new StringAttribute(GraphicAttributeConstants.FONT, font);
        this.textcolor = new ColorAttribute(
                GraphicAttributeConstants.TEXTCOLOR, textcolor.getColor());
        this.fontSize = new IntegerAttribute(
                GraphicAttributeConstants.FONT_SIZE,
                GraphicAttributeConstants.DEFAULT_FONT_SIZE);
        adjustSize();
    }

    @Override
    public String getLabel() {
        return label.getString();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
