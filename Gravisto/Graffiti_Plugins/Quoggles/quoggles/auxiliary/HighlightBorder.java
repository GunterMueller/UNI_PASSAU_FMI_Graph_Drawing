/*
 * 
 */
package quoggles.auxiliary;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

/**
 * A LineBorder with additional space between the border and the component.
 */
public class HighlightBorder extends AbstractBorder {

    private Insets insets;
    
    private Color color;
    

    /**
     * 
     */
    public HighlightBorder(Color col) {
        insets = new Insets(0, 0, 0, 0);
        color = col;
    }

    /**
     * 
     */
    public HighlightBorder(Color col, Dimension size) {
        insets = new Insets(size.height, size.height, 0, 0);
        color = col;
    }


    public void setSize(Dimension size) {
        insets = new Insets(size.height, size.height, 0, 0);
    }
    
    /**
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    /**
     * Returns true.
     * 
     * @see javax.swing.border.Border#isBorderOpaque()
     */
    public boolean isBorderOpaque() {
        return false;
    }


    /**
     * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, 
        int x, int y, int width, int height) {
        
//        super.paintBorder(c, g, x, y, width, height);

          Graphics cg;
          cg = g.create();
          cg.translate(x, y);
          cg.setColor(color);

          cg.fillRect(0, 0, width, height);

          cg.dispose();
    }

}