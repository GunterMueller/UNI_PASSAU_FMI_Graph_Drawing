// =============================================================================
//
//   EdgeBorder.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeBorder.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.tool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.PathIterator;

import javax.swing.border.AbstractBorder;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.EdgeComponentInterface;
import org.graffiti.plugin.view.GraphElementShape;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 5768 $ Provides a border used to mark selected nodes.
 */
public class EdgeBorder extends AbstractBorder {

    /**
     * 
     */
    private static final long serialVersionUID = -2536785806090003576L;

    /** Color used to paint border. */
    protected Color color;

    /** DOCUMENT ME! */
    protected boolean showBends;

    /** Size of bullets used to mark bends. */
    protected int bulletSize;

    /**
     * Constructor for NodeBorder.
     * 
     * @param color
     *            DOCUMENT ME!
     * @param size
     *            DOCUMENT ME!
     * @param showBends
     *            DOCUMENT ME!
     */

    // public EdgeBorder(Color color, int size, Edge edge) {
    public EdgeBorder(Color color, int size, boolean showBends) {
        super();
        this.color = color;
        this.bulletSize = size;
        this.showBends = showBends;

        // this.edge = edge;
        // this.bends = (SortedCollectionAttribute)edge.getAttribute
        // (GraphicAttributeConstants.GRAPHICS +
        // Attribute.SEPARATOR + GraphicAttributeConstants.BENDS);
    }

    /**
     * Sets the insets to the value of <code>width</code>.
     * 
     * @see javax.swing.border.AbstractBorder#getBorderInsets(java.awt.Component,
     *      java.awt.Insets)
     */
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        // insets.top = this.borderWidth;
        // insets.left = this.borderWidth;
        // insets.bottom = this.borderWidth;
        // insets.right = this.borderWidth;
        Rectangle bounds = c.getBounds();
        insets.top = bounds.height;
        insets.left = bounds.width;
        insets.bottom = bounds.height;
        insets.right = bounds.width;

        return insets;
    }

    /**
     * @see javax.swing.border.AbstractBorder#getBorderInsets(java.awt.Component)
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0, 0, 0, 0));
    }

    /**
     * DOCUMENT ME!
     * 
     * @return true.
     * 
     * @see javax.swing.border.AbstractBorder#isBorderOpaque() Returns true.
     */
    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    /**
     * Paints the border.
     * 
     * @param c
     *            DOCUMENT ME!
     * @param g
     *            DOCUMENT ME!
     * @param bx
     *            DOCUMENT ME!
     * @param by
     *            DOCUMENT ME!
     * @param width
     *            DOCUMENT ME!
     * @param height
     *            DOCUMENT ME!
     */
    @Override
    public void paintBorder(Component c, Graphics g, int bx, int by, int width,
            int height) {
        double bulletSizeHalf = bulletSize / 2d;

        Graphics cg;
        cg = g.create();
        Rectangle bounds = c.getBounds();
        cg.translate(bx - bounds.x, by - bounds.y);
        cg.setColor(this.color);

        if (showBends) {
            Color lightColor = this.color.darker().darker();
            cg.setColor(lightColor);

            int bendBulletSize = (int) (bulletSize / 2d);

            if (bendBulletSize == 0) {
                bendBulletSize = 1;
            }

            SortedCollectionAttribute bends = (SortedCollectionAttribute) ((EdgeComponentInterface) c)
                    .getGraphElement().getAttribute(
                            GraphicAttributeConstants.BENDS_PATH);

            for (Attribute attribute : bends.getCollection().values()) {
                CoordinateAttribute bendCoord = (CoordinateAttribute) attribute;

                // cg.setClip(0, 0, width, height);
                // cg.fillOval((int)bendCoord.getX()-(c.getBounds().x),
                // (int)bendCoord.getY()-(c.getBounds().y),
                // 2*bulletSize, 2*bulletSize);
                cg.fillOval((int) (bendCoord.getX() - (bendBulletSize / 2d)),
                        (int) (bendCoord.getY() - (bendBulletSize / 2d)),
                        bendBulletSize, bendBulletSize);

                // cg.fillOval((int) (bendCoord.getX() - c.getX() -
                // (bendBulletSize / 2d)),
                // (int) (bendCoord.getY() - c.getY() - (bendBulletSize / 2d)),
                // bendBulletSize, bendBulletSize);
            }

            cg.setColor(this.color);
        }

        GraphElementShape grShape = ((EdgeComponentInterface) c).getShape();

        // GeneralPath grPath = new GeneralPath(grShape);
        PathIterator pi = grShape.getPathIterator(null);
        double[] seg = new double[6];
        int type;
        double x = 0;
        double y = 0;

        try {
            type = pi.currentSegment(seg);
            x = seg[0];
            y = seg[1];
            cg.fillRect((int) (x - bulletSizeHalf), (int) (y - bulletSizeHalf),
                    bulletSize, bulletSize);

            // cg.fillOval((int)x-2, (int)y-2, bulletSize, bulletSize);
            while (!pi.isDone()) {
                pi.next();
                type = pi.currentSegment(seg);

                switch (type) {
                case java.awt.geom.PathIterator.SEG_MOVETO:

                    // x = seg[0];
                    // y = seg[1];
                    break;

                case java.awt.geom.PathIterator.SEG_LINETO:
                    x = seg[0];
                    y = seg[1];

                    break;

                case java.awt.geom.PathIterator.SEG_QUADTO:
                    x = seg[2];
                    y = seg[3];

                    break;

                case java.awt.geom.PathIterator.SEG_CUBICTO:
                    x = seg[4];
                    y = seg[5];

                    break;
                }

                // cg.fillOval((int)x-2, (int)y-2, bulletSize, bulletSize);
                cg.fillRect((int) (x - bulletSizeHalf),
                        (int) (y - bulletSizeHalf), bulletSize, bulletSize);
            }
        } catch (java.util.NoSuchElementException e) {
        }

        cg.dispose();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
