package quoggles.stdboxes.connectors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import quoggles.auxiliary.Util;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.representation.BoxRepresentation;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;

/**
 * Represents a <code>OneOneConnector_Box</code>.
 */
public class OneOneConnector_Rep extends DefaultBoxRepresentation {

    private boolean drawFromTL = true;
    
    private Point2D relInputPosTL = new Point2D.Double(0d, 0d);

    private Point2D relOutputPosTL = new Point2D.Double(1d, 1d);

    private Point2D relInputPosBL = new Point2D.Double(0d, 1d);

    private Point2D relOutputPosBL = new Point2D.Double(1d, 0d);

    
    /**
     * Constructs the representation.
     * 
     * @param representedBox
     */
    public OneOneConnector_Rep(IBox representedBox) {
        super(representedBox);

        graphicalRep = new MyBoxRepresentation(this);
    }


    /**
     * @see quoggles.representation.IBoxRepresentation#getRelInputsPos()
     */
    public Point2D[] getRelInputsPos() {
        if (drawFromTL) {
            return new Point2D[]{ relInputPosTL };
        } else {
            return new Point2D[]{ relInputPosBL };
        }
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#getRelOutputsPos()
     */
    public Point2D[] getRelOutputsPos() {
        if (drawFromTL) {
            return new Point2D[]{ relOutputPosTL };
        } else {
            return new Point2D[]{ relOutputPosBL };
        }
    }

    /**
     * Empty implementation.
     * 
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() { }

    
    /**
     * The graphical representation (i.e. a line) of the connector.
     */
    public class MyBoxRepresentation extends BoxRepresentation { 
        
        /**
         * @param iBoxRep
         */
        public MyBoxRepresentation(IBoxRepresentation iBoxRep) {
            super(iBoxRep);
            Dimension size = new Dimension
                (IBoxConstants.ONEONE_CONNECTOR_WIDTH,
                 IBoxConstants.ONEONE_CONNECTOR_HEIGHT);
            setSize(size);
            setPreferredSize(size);
            setOpaque(false);
        }
        
        /**
         * @param drawFromTL
         */
        public void setDrawFromTL(boolean dFTL) {
            drawFromTL = dFTL;
        }
        
        /**
         * 
         * @return
         */
        public boolean getDrawFromTL() {
            return drawFromTL;
        }

        /**
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2D = (Graphics2D)g.create();
            g2D.setStroke(new BasicStroke
                (IBoxConstants.ONEONE_CONNECTOR_HEIGHT));
            g2D.setColor(Color.BLACK);
            
            if (drawFromTL) {
                g2D.drawLine(1, 1, getWidth()-1, getHeight()-1);
            } else {
                g2D.drawLine(1, getHeight()-1, getWidth()-1, 1);
            }
            g2D.dispose();
        }
                
        /**
         * @see java.awt.Component#contains(int, int)
         */
        public boolean contains(int x, int y) {
            Line2D line = new Line2D.Float();
            if (drawFromTL) {
                line.setLine(1, 1, getWidth()-1, getHeight()-1);
            } else {
                line.setLine(1, getHeight()-1, getWidth()-1, 1);
            }
            return super.contains(x, y) &&
                Util.lineContains(line, x, y);
        }

    }
}
