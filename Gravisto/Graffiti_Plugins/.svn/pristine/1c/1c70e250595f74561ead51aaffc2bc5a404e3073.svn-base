package quoggles.stdboxes.connectors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.representation.BoxRepresentation;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;

/**
 * Represents a fork with two ends.
 */
public class TwoSplitConnector_Rep extends DefaultBoxRepresentation {

    private Point2D output1 = new Point2D.Double(1.0d, 0.0d);

    private Point2D output2 = new Point2D.Double(1.0d, 1.0d);

    
    /**
     * @param representedBox
     */
    public TwoSplitConnector_Rep(IBox representedBox) {
        super(representedBox);
        
        graphicalRep = new MyBoxRepresentation(this);
    }


    /**
     * @see quoggles.representation.IBoxRepresentation#getRelOutputsPos()
     */
    public Point2D[] getRelOutputsPos() {
        return new Point2D[]{ output1, output2 };
    }

    /**
     * Empty implementation.
     * 
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() { }


    /**
     * Provides a graphical repersentation of the connector.
     */
    class MyBoxRepresentation extends BoxRepresentation {

        /**
         * @param iBoxRep
         */
        public MyBoxRepresentation(IBoxRepresentation iBoxRep) {
            super(iBoxRep);
            Dimension size = new Dimension(IBoxConstants.DEFAULT_CONNECTOR_WIDTH,
                IBoxConstants.DEFAULT_CONNECTOR_HEIGHT);
            setSize(size);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setOpaque(false);
        }
        
        /**
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2D = (Graphics2D)g.create();
            g2D.setStroke(
                new BasicStroke(IBoxConstants.ONEONE_CONNECTOR_HEIGHT));
            g2D.setColor(Color.BLACK);
            
            int hhalf = getHeight() / 2;
            int whalf = getWidth() / 2;
            g2D.drawLine(0, hhalf, whalf, hhalf);
            g2D.drawLine(whalf, hhalf, getWidth(), 0);
            g2D.drawLine(whalf, hhalf, getWidth(), getHeight());
        }
        
    }

}
