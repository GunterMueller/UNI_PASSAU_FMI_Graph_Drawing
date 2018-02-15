// =============================================================================
//
//   LabelComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LabelComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.attributecomponents.simplelabel;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.EdgeLabelPositionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;
import org.graffiti.plugin.attributecomponent.AbstractAttributeComponent;
import org.graffiti.plugin.view.ShapeNotFoundException;

/**
 * This component represents a label for a node or an edge.
 * 
 * @version $Revision: 5766 $
 */
public class LabelComponent extends AbstractAttributeComponent implements
        GraphicAttributeConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 3952273847141729555L;

    /**
     * Flatness value used for the <code>PathIterator</code> used to place
     * labels.
     */
    protected final double flatness = 1.0d;

    /** Standard width of JTextField. */
    protected final int DEFAULT_WIDTH = 20;

    /** The <code>JLabel</code> that represents the label text. */
    protected JLabel label;

    /** The <code>LabelAttribute</code> that is displayed via this component. */
    protected LabelAttribute labelAttr;

    protected static final int[] htmlSizeToPtSize = { 8, 10, 12, 14, 18, 24, 36 };

    private static Pattern sizePattern = Pattern
            .compile("size\\s*=\\s*\"([^\"]*)\"");
    private static Pattern fontPattern = Pattern
            .compile("face\\s*=\\s*\"([^\"]*)\"");

    /**
     * While zooming the label width can change due to inaccuracies in the
     * rendering. The additional width tries to ensure that the complete label
     * is always visible. The value of 50 seems to be big enough for most cases.
     */
    private final int ADDITIONAL_WIDTH = 50;

    /**
     * Constructs a new <code>LabelComponent</code>
     */
    public LabelComponent() {
        super();
        this.setLayout(new GridLayout(1, 1));
        this.setOpaque(false);
        this.shift = new Point();
    }

    /**
     * obvious
     * 
     * @param attr
     */
    @Override
    public void setAttribute(Attribute attr) {
        this.attr = attr;
        this.labelAttr = (LabelAttribute) attr;
    }

    /**
     * Called when an attribute of the attribute represented by this component
     * has changed.
     * 
     * @param attr
     *            the attribute that has triggered the event.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    @Override
    public void attributeChanged(Attribute attr) throws ShapeNotFoundException {
        // System.out.println("labelComp, attrChanged: " + attr.getPath());
        if (attr.getId().equals(TEXTCOLOR)) {
        } else if ((Attribute.SEPARATOR + GRAPHICS + Attribute.SEPARATOR + COORDINATE)
                .equals(attr.getPath())) {
            CoordinateAttribute coordAttr = (CoordinateAttribute) attr;
            shift.setLocation(coordAttr.getX(), coordAttr.getY() - 1);
        } else if (attr.getPath().startsWith(
                Attribute.SEPARATOR + GRAPHICS + Attribute.SEPARATOR
                        + COORDINATE)) {
            Point newshift = new Point();

            if (attr.getId().equals(X)) {
                newshift.setLocation(((DoubleAttribute) attr).getDouble(),
                        shift.y);
                this.setLocation((this.getLocation().x + newshift.x) - shift.x,
                        this.getLocation().y);
            } else {
                newshift.setLocation(shift.getX(), ((DoubleAttribute) attr)
                        .getDouble() - 1);
                this.setLocation(this.getLocation().x,
                        (this.getLocation().y + newshift.y) - shift.y);
            }

            this.shift.setLocation(newshift.x, newshift.y);
        } else {
            this.recreate();
        }

        repaint();
    }

    /**
     * Paints the label contained in this component.
     * 
     * @see javax.swing.JComponent#paintChildren(Graphics)
     */
    @Override
    public void paintChildren(Graphics g) {
        // // setting fractions metrics to true ensures the correct zoom of
        // labels.
        // // now noninteger font sizes can be used.
        Graphics2D g2 = (Graphics2D) g;
        boolean fractionalMetrics = g2.getFontRenderContext()
                .usesFractionalMetrics();
        if (!fractionalMetrics) {
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        }
        super.paintChildren(g2);
        if (!fractionalMetrics) {
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        }
    }

    /**
     * Used when the shape changed in the datastructure. Makes the painter
     * create a new shape.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    @Override
    public void recreate() throws ShapeNotFoundException {
        this.removeAll();
        if (labelAttr == null) {
            System.err.println("No label attribute. Should not happen here.");
            return;
        }

        // ---------------------------------------------------------------------
        // compability to graphs,where the font attribute was html-styled

        String value = labelAttr.getFont();
        Matcher m1 = fontPattern.matcher(value);
        if (m1.find()) {
            String font = m1.group(1);
            labelAttr.setFont(font);
        }
        Matcher m2 = sizePattern.matcher(value);
        if (m2.find()) {
            int fontSize = 3;
            try {
                fontSize = Integer.parseInt(m2.group(1));
            } catch (NumberFormatException e) {
                // use default font size 3
            }
            if (fontSize > 7) {
                fontSize = 7;
            } else if (fontSize < 1) {
                fontSize = 1;
            }
            fontSize = htmlSizeToPtSize[fontSize - 1];
            labelAttr.setFontSize(fontSize);
        }

        // ---------------------------------------------------------------------

        String labelText = "<html><center>" + this.labelAttr.getLabel()
                + "</center></html>";
        label = new JLabel(labelText, SwingConstants.CENTER);

        Font font = new Font(labelAttr.getFont(), label.getFont().getStyle(),
                labelAttr.getFontSize());
        label.setFont(font);

        label.setForeground(this.labelAttr.getTextcolor().getColor());
        label.setSize((int) label.getPreferredSize().getWidth()
                + ADDITIONAL_WIDTH, (int) label.getPreferredSize().getHeight());
        label.setPreferredSize(label.getSize());
        this.setPreferredSize(label.getSize());
        this.setSize(label.getSize());
        this.add(label);

        Point2D loc = calculateLabelPosition();
        loc.setLocation(loc.getX() /* + ADDITIONAL_WIDTH/2 */, loc.getY());

        this.setLocation((int) (loc.getX() + shift.getX()),
                (int) (loc.getY() + shift.getY()));
        this.validate();
    }

    /**
     * Calculates a pair of two values: fst = sum of length of first (seg-1)
     * segments snd = length of segment number seg
     * 
     * @param pi
     *            DOCUMENT ME!
     * @param segStartPos
     *            DOCUMENT ME!
     * @param segEndPos
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected Pair calculateDists(PathIterator pi, Point2D segStartPos,
            Point2D segEndPos) {
        double[] seg = new double[6];

        double dist = 0;

        // fst
        double firstdist = 0;

        // snd
        double segnrdist = 0;
        double diffdist = 0;
        double lastx = 0;
        double lasty = 0;
        double newx = 0;
        double newy = 0;
        int type;
        int segcnt = 0;
        boolean haveFound = false;
        boolean foundStart = false;

        try {
            type = pi.currentSegment(seg);
            lastx = seg[0];
            lasty = seg[1];

            while (!pi.isDone() && !haveFound) {
                segcnt++;
                pi.next();
                type = pi.currentSegment(seg);

                switch (type) {
                case java.awt.geom.PathIterator.SEG_MOVETO:

                    if (!pi.isDone()) {
                        diffdist = Point2D.distance(lastx, lasty, seg[0],
                                seg[1]);
                        firstdist = dist;
                        dist += diffdist;
                        newx = seg[0];
                        newy = seg[1];

                        if (!foundStart
                                && ((lastx - segStartPos.getX()) <= Double.MIN_VALUE)
                                && ((lasty - segStartPos.getY()) <= Double.MIN_VALUE)) {
                            foundStart = true;
                            segnrdist = 0;
                        } else if (foundStart) {
                            segnrdist += diffdist;
                        }

                        if (((newx - segEndPos.getX()) <= Double.MIN_VALUE)
                                && ((newy - segEndPos.getY()) <= Double.MIN_VALUE)) {
                            haveFound = true;
                        }
                    }

                    break;

                case java.awt.geom.PathIterator.SEG_LINETO:
                    newx = seg[0];
                    newy = seg[1];
                    diffdist = Point2D.distance(lastx, lasty, newx, newy);

                    if (!foundStart
                            && (Math.abs(lastx - segStartPos.getX()) <= Double.MIN_VALUE)
                            && (Math.abs(lasty - segStartPos.getY()) <= Double.MIN_VALUE)) {
                        foundStart = true;

                        // System.out.println("found start");
                        firstdist = dist;
                        segnrdist = 0;
                    }

                    dist += diffdist;

                    if (foundStart) {
                        segnrdist += diffdist;
                    }

                    if ((Math.abs(newx - segEndPos.getX()) <= Double.MIN_VALUE)
                            && (Math.abs(newy - segEndPos.getY()) <= Double.MIN_VALUE)) {
                        // assert !foundStart :
                        haveFound = true;

                        // System.out.println("found end");
                    }

                    break;
                }

                lastx = newx;
                lasty = newy;
            }
        } catch (java.util.NoSuchElementException e) {
        }

        // System.out.println("returning " + firstdist + " " + segnrdist);
        return new Pair(firstdist, segnrdist);
    }

    /**
     * Using the information from the associated <code>LabelAttribute</code> and
     * the shape and position of the <code>GraphElement</code> to calculate the
     * position of the label.
     * 
     * @return Point2D specifying top left corner of label.
     */
    protected Point2D calculateLabelPosition() {
        // calculate position of label
        Point loc = new Point();

        double labelWidth = this.label.getWidth();
        double labelHeight = this.label.getHeight();

        GraphElement ge = (GraphElement) this.attr.getAttributable();

        if (ge instanceof Node) {

            // label is a nodelabel

            Node node = (Node) ge;
            NodeGraphicAttribute nodeAttr = (NodeGraphicAttribute) node
                    .getAttribute(GRAPHICS);

            // get the component's size
            DimensionAttribute size = nodeAttr.getDimension();
            double sizeX = size.getWidth();
            double sizeY = size.getHeight();

            CoordinateAttribute coord = nodeAttr.getCoordinate();

            // set the shift: the center of the node
            shift.setLocation((int) (coord.getX()), (int) (coord.getY()) - 1);
            /*
             * String align = labelAttr.getAlignment();
             * 
             * if (CENTERED.equals(align)) { loc.setLocation(-labelWidth / 2d,
             * -labelHeight / 2d); } else if (BELOW.equals(align)) {
             * loc.setLocation(-labelWidth / 2d, sizeY / 2d + LABEL_DISTANCE); }
             * else if (INSIDEBOTTOM.equals(align)) { // strange for flat nodes
             * loc.setLocation(-labelWidth / 2d, sizeY / 2d - labelHeight -
             * LABEL_DISTANCE); } else if (ABOVE.equals(align)) {
             * loc.setLocation(-labelWidth / 2d, -sizeY / 2d - labelHeight -
             * LABEL_DISTANCE); } else if (LEFT.equals(align)) {
             * loc.setLocation(-(sizeX / 2d + labelWidth + LABEL_DISTANCE),
             * -labelHeight / 2d); } else if (RIGHT.equals(align)) {
             * loc.setLocation(sizeX / 2d + LABEL_DISTANCE, -labelHeight / 2d);
             * } else if (INSIDETOP.equals(align)) { loc.setLocation(-labelWidth
             * / 2d, -sizeY / 2d + LABEL_DISTANCE); } else
             */
            {
                // no supported alignment constant: use relative positions
                NodeLabelPositionAttribute posAttr = ((NodeLabelAttribute) this.labelAttr)
                        .getPosition();

                if (posAttr == null) {
                    posAttr = new NodeLabelPositionAttribute(POSITION);
                }
                loc.setLocation((posAttr.getRelativeXOffset() * sizeX) / 2d
                        - labelWidth / 2d + posAttr.getAbsoluteXOffset(),
                        (posAttr.getRelativeYOffset() * sizeY) / 2d
                                - labelHeight / 2d
                                + posAttr.getAbsoluteYOffset());
            }
            return loc;
        } else {
            // label is an edgelabel
            EdgeLabelPositionAttribute posAttr = ((EdgeLabelAttribute) this.labelAttr)
                    .getPosition();

            if (posAttr == null) {
                posAttr = new EdgeLabelPositionAttribute(POSITION);
            }

            Point2D labelLoc = null;

            // labelLoc = MathUtil.interpolate(geShape,
            // posAttr.getAlignSegment(), posAttr.getRelAlign());

            // /*
            if (posAttr.getAlignmentSegment() <= 0) {
                // calc pos rel to whole edge
                PathIterator pi = geShape.getPathIterator(null, flatness);
                double dist = (this.iterateTill(pi, null)).getX();

                pi = geShape.getPathIterator(null, flatness);
                labelLoc = this.iterateTill(pi, new Double(posAttr
                        .getRelativeAlignment()
                        * dist));
            } else {
                // calc pos rel to spec seg
                PathIterator pi = geShape.getPathIterator(null);

                // fst = sum of length of first (alignSegment-1) segments
                // snd = (length of segment number alignSegment)
                PointPair segPos = calculateSegPos(pi, posAttr
                        .getAlignmentSegment());

                if (segPos == null) {
                    pi = geShape.getPathIterator(null, flatness);

                    double dist = (this.iterateTill(pi, null)).getX();

                    pi = geShape.getPathIterator(null, flatness);
                    labelLoc = this.iterateTill(pi, new Double(posAttr
                            .getRelativeAlignment()
                            * dist));
                } else {
                    pi = geShape.getPathIterator(null, flatness);

                    Pair dists = this.calculateDists(pi, segPos.getFst(),
                            segPos.getSnd());

                    // move along path till correct pos
                    pi = geShape.getPathIterator(null, flatness);
                    labelLoc = this.iterateTill(pi,
                            new Double(dists.getFst()
                                    + (posAttr.getRelativeAlignment() * dists
                                            .getSnd())));
                }
            }
            // */

            loc.setLocation(labelLoc.getX() - (labelWidth / 2.0d)
                    + posAttr.getAbsoluteXOffset() - shift.x, labelLoc.getY()
                    - (labelHeight / 2.0d) + posAttr.getAbsoluteYOffset()
                    - shift.y);
        }
        return loc;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param pi
     *            DOCUMENT ME!
     * @param segnr
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected PointPair calculateSegPos(PathIterator pi, int segnr) {
        // assert segnr>=0 :
        double[] seg = new double[6];

        // double firstx = 0;
        // double firsty = 0;
        double lastx = 0;
        double lasty = 0;

        double newx = 0;
        double newy = 0;

        int type;
        int segcnt = 0;

        PointPair pp = new PointPair();

        try {
            type = pi.currentSegment(seg);
            lastx = seg[0];
            lasty = seg[1];

            // firstx = lastx;
            // firsty = lasty;
            while (!pi.isDone() && (segcnt < segnr)) {
                segcnt++;
                pi.next();
                type = pi.currentSegment(seg);

                switch (type) {
                case java.awt.geom.PathIterator.SEG_MOVETO:

                    if (!pi.isDone()) {
                        newx = seg[0];
                        newy = seg[1];
                    } else
                        return null;

                    break;

                case java.awt.geom.PathIterator.SEG_LINETO:
                    newx = seg[0];
                    newy = seg[1];

                    break;

                case java.awt.geom.PathIterator.SEG_QUADTO:
                    newx = seg[2];
                    newy = seg[3];

                    break;

                case java.awt.geom.PathIterator.SEG_CUBICTO:
                    newx = seg[4];
                    newy = seg[5];

                    break;
                }

                // System.out.println("now found segnr=" + segcnt + "; at (" +
                // newx + ", " + newy + ")");
                if (segcnt == segnr) {
                    // System.out.println("found segstartpospoint at (" + lastx
                    // + ", " + lasty + ")");
                    pp.setFst(new Point2D.Double(lastx, lasty));

                    // System.out.println("found segendpospoint at (" + newx +
                    // ", " + newy + ")");
                    pp.setSnd(new Point2D.Double(newx, newy));
                }

                lastx = newx;
                lasty = newy;
            }
        } catch (java.util.NoSuchElementException e) {
        }

        // pp.setSnd(new Point2D.Double(lastx, lasty));
        if (segcnt < segnr) {
            // System.out.println("segnr out of bounds");
            pp = null;
        }

        return pp;
    }

    /**
     * If d == null then calculates length of path given by pi if d is a value
     * then calculates a position on the path near the distance given by this
     * parameter, measured from the start.
     * 
     * @param pi
     *            <code>PathIterator</code> describing the path
     * @param d
     *            null or distance
     * 
     * @return distance at first component of <code>Point2D</code> or the
     *         position wanted as <code>point2D</code> .
     */
    protected Point2D iterateTill(PathIterator pi, Double d) {
        double[] seg = new double[6];
        double limitDist;

        if (d == null) {
            limitDist = Double.POSITIVE_INFINITY;
        } else {
            limitDist = d.doubleValue();
        }

        double dist = 0;
        double lastx = 0;
        double lasty = 0;
        int type;

        try {
            type = pi.currentSegment(seg);
            lastx = seg[0];
            lasty = seg[1];

            while (!pi.isDone() && (dist < limitDist)) {
                pi.next();
                type = pi.currentSegment(seg);

                switch (type) {
                case java.awt.geom.PathIterator.SEG_MOVETO:

                    if (!pi.isDone()) {
                        dist += Point2D.distance(lastx, lasty, seg[0], seg[1]);
                        lastx = seg[0];
                        lasty = seg[1];
                    }

                    break;

                case java.awt.geom.PathIterator.SEG_LINETO:
                    dist += Point2D.distance(lastx, lasty, seg[0], seg[1]);

                    if ((d != null) && (dist >= limitDist)) {
                        // System.out.println(dist +" "+ limitDist);
                        double diffx = seg[0] - lastx;
                        double diffy = seg[1] - lasty;
                        double diffsqr = Math.sqrt((diffx * diffx)
                                + (diffy * diffy));

                        // System.out.println("diffx
                        // lastx += Math.sqrt(diffsq - diffy*diffy);
                        // lasty += Math.sqrt(diffsq - diffx*diffx);
                        double factor = (diffsqr - dist + limitDist) / diffsqr;
                        lastx += (diffx * factor);
                        lasty += (diffy * factor);
                    } else {
                        lastx = seg[0];
                        lasty = seg[1];
                    }

                    break;

                case java.awt.geom.PathIterator.SEG_QUADTO:

                    // unnecessary since this approximation uses only lines
                    // System.out.println(" quad");
                    dist += Point2D.distance(lastx, lasty, seg[2], seg[3]);
                    lastx = seg[2];
                    lasty = seg[3];

                    break;

                case java.awt.geom.PathIterator.SEG_CUBICTO:

                    // unnecessary since this approximation uses only lines
                    // System.out.println(" cube");
                    dist += Point2D.distance(lastx, lasty, seg[4], seg[5]);
                    lastx = seg[4];
                    lasty = seg[5];

                    break;
                }
            }
        } catch (java.util.NoSuchElementException e) {
        }

        if (d == null)
            return new Point2D.Double(dist, 0);
        else
            return new Point2D.Double(lastx, lasty);
    }

    class Pair {
        /** DOCUMENT ME! */
        private double fst = 0d;

        /** DOCUMENT ME! */
        private double snd = 0d;

        /**
         * Creates a new Pair object.
         */
        Pair() {
        }

        /**
         * Creates a new Pair object.
         * 
         * @param f
         *            DOCUMENT ME!
         * @param s
         *            DOCUMENT ME!
         */
        Pair(double f, double s) {
            fst = f;
            snd = s;
        }

        /**
         * DOCUMENT ME!
         * 
         * @param f
         *            DOCUMENT ME!
         */
        void setFst(double f) {
            fst = f;
        }

        /**
         * DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        double getFst() {
            return fst;
        }

        /**
         * DOCUMENT ME!
         * 
         * @param s
         *            DOCUMENT ME!
         */
        void setSnd(double s) {
            snd = s;
        }

        /**
         * DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        double getSnd() {
            return snd;
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @author $Author: gleissner $
     * @version $Revision: 5766 $ $Date: 2006-11-21 12:05:59 +0100 (Di, 21 Nov
     *          2006) $
     */
    class PointPair {
        /** DOCUMENT ME! */
        private Point2D fst = null;

        /** DOCUMENT ME! */
        private Point2D snd = null;

        /**
         * Creates a new PointPair object.
         */
        PointPair() {
        }

        /**
         * Creates a new PointPair object.
         * 
         * @param f
         *            DOCUMENT ME!
         * @param s
         *            DOCUMENT ME!
         */
        PointPair(Point2D f, Point2D s) {
            fst = f;
            snd = s;
        }

        /**
         * DOCUMENT ME!
         * 
         * @param f
         *            DOCUMENT ME!
         */
        void setFst(Point2D f) {
            fst = f;
        }

        /**
         * DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        Point2D getFst() {
            return fst;
        }

        /**
         * DOCUMENT ME!
         * 
         * @param s
         *            DOCUMENT ME!
         */
        void setSnd(Point2D s) {
            snd = s;
        }

        /**
         * DOCUMENT ME!
         * 
         * @return DOCUMENT ME!
         */
        Point2D getSnd() {
            return snd;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
