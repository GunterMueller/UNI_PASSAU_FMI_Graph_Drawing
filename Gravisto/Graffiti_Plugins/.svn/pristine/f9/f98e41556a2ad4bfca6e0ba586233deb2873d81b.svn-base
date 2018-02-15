/*
 * 
 */
package quoggles.auxiliary;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.border.LineBorder;

/**
 * A LineBorder with additional space between the border and the component.
 */
public class InsetLineBorder extends LineBorder {

    private Insets insets = new Insets(8, 8, 8, 8);
        

    /**
     * insets: 8, 8, 8, 8
     * thickness: 1
     * roundedCorners: false
     * 
     * @param color
     */
    public InsetLineBorder(Color color) {
        super(color, 1, false);
    }

    /**
     * insets: 8, 8, 8, 8
     * roundedCorners: false
     * 
     * @param color
     * @param thickness
     */
    public InsetLineBorder(Color color, int thickness) {
        super(color, thickness);
        // TODO Auto-generated constructor stub
    }

    /**
     * insets: 8, 8, 8, 8

     *      * @param color
     * @param thickness
     * @param roundedCorners
     */
    public InsetLineBorder
        (Color color, int thickness, boolean roundedCorners) {

        super(color, thickness, roundedCorners);
    }

    /**
     * @param color
     * @param thickness
     * @param roundedCorners
     * @param insets
     */
    public InsetLineBorder(Color color, int thickness, 
        boolean roundedCorners, Insets insets) {

        super(color, thickness, roundedCorners);
        this.insets = insets;
    }

    /**
     * roundedCorners: false
     * 
     * @param color
     * @param thickness
     * @param insets
     */
    public InsetLineBorder(Color color, int thickness, Insets insets) {

        super(color, thickness, false);
        this.insets = insets;
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
        return true;
    }

}