package quoggles.auxiliary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import quoggles.constants.IBoxConstants;

/**
 * Represents a small bulb indicating the validness of a connection between
 * two boxes.
 */
public class ConnectedSign extends JPanel {
        
    public static final int DIAMETER = 10;

    public static final int RADIUS = DIAMETER / 2;

    private boolean good;
        
    private boolean maybe;
        
    private Color color;
        
    /**
     * Constructor. The given boolean value indicates whether the sign
     * represents a valid (<code>true</code>) or invalid connection.
     * 
     * @param ok true iff connection is valid
     */
    public ConnectedSign(boolean ok) {
        good = ok;
        Dimension size = new Dimension(DIAMETER, DIAMETER);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setVisible(true);
            
        if (good) {
            color = IBoxConstants.OK_SIGN_COLOR;
        } else {
            color = IBoxConstants.BAD_SIGN_COLOR;
        }
    }
        
    /**
     * Constructor. The given boolean value indicates whether the sign
     * represents a valid (<code>true</code>) or invalid connection.
     * 
     * @param ok true iff connection is valid
     */
    public ConnectedSign(boolean ok, boolean notReallyGood) {
        good = ok;
        maybe = notReallyGood;
        Dimension size = new Dimension(DIAMETER, DIAMETER);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setVisible(true);
            
        if (good) {
            if (notReallyGood) {
                color = IBoxConstants.NEARLY_OK_SIGN_COLOR;
            } else {
                color = IBoxConstants.OK_SIGN_COLOR;
            }
        } else {
            color = IBoxConstants.BAD_SIGN_COLOR;
        }
    }
        
    public boolean isGreen() {
        return good && !maybe;
    }
        
    public boolean isRed() {
        return !good;
    }

    public boolean isYellow() {
        return good && maybe;
    }
        
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics gc = g.create();
        gc.setColor(color);
        gc.fillOval(0, 0, DIAMETER, DIAMETER);
    }
}

