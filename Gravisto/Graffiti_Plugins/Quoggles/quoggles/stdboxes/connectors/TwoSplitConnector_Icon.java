/*
 * 
 */
package quoggles.stdboxes.connectors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.icons.IBoxIcon;

/**
 *
 */
public class TwoSplitConnector_Icon extends JPanel 
    implements IBoxIcon {

    /**
     * 
     */
    public TwoSplitConnector_Icon() {
        Dimension size = new Dimension(IBoxConstants.DEFAULT_ICON_WIDTH, 
            IBoxConstants.DEFAULT_ICON_HEIGHT);
        setSize(size);
        setPreferredSize(size);
        
        setOpaque(false);
    }

    /**
     * @see quoggles.icons.IBoxIcon#getNewBoxInstance()
     */
    public IBox getNewBoxInstance() {
        return new TwoSplitConnector_Box();
    }

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
            
        Graphics2D g2D = (Graphics2D)g.create();
        g2D.setStroke(new BasicStroke(3.0f));
        g2D.setColor(Color.BLACK);
            
        int h = getHeight();
        int w = getWidth() / 3;
        int hoff = w;
        int hhalf = h / 2;
        int whalf = w / 2;
        g2D.drawLine(hoff, hhalf, hoff + whalf, hhalf);
        g2D.drawLine(hoff + whalf, hhalf, hoff + w, 0);
        g2D.drawLine(hoff + whalf, hhalf, hoff + w, h);
    }
        
}
