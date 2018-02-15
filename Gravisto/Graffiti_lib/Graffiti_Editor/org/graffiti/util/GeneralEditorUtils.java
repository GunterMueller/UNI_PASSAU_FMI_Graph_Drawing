/*
 * General auxiliary classes not fitting anywhere else.
 */

package org.graffiti.util;

import java.awt.Graphics;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;

/**
 * Some auxiliary routines that need editor functionality.
 * 
 * @see org.graffiti.util.GeneralUtils
 */
public class GeneralEditorUtils {

    /**
     * Searches if the given Attributable already contains a LabelAttribute. If
     * yes, its value is set to the given String. If not, a new label attribute
     * is instantiated and its value set to the given value.
     * 
     * @param ge
     *            graphelement
     * @param val
     *            new label string
     */
    public static void setLabel(GraphElement ge, String val) {
        LabelAttribute labelAttr = (LabelAttribute) GeneralUtils
                .searchForAttribute(ge.getAttribute(""), LabelAttribute.class);

        if (labelAttr != null) {
            labelAttr.setLabel(val);
        } else { // no label found
            if (ge instanceof Node) {
                labelAttr = new NodeLabelAttribute("label");
            } else if (ge instanceof Edge) {
                labelAttr = new EdgeLabelAttribute("label");
            } else {
                labelAttr = new LabelAttribute("label");
            }
            labelAttr.setLabel(val);
            ge.addAttribute(labelAttr, "");
        }
    }

    /**
     * Draws a rectangle on the given graphics context.
     * 
     * @param comp
     *            context to draw upon
     * @param p1
     *            first corner of the rectangle
     * @param p2
     *            second corner of the rectangle
     */
    public static void paintSelectionRectangle(JComponent comp, Point2D p1,
            Point2D p2) {
        int tlx;
        int tly;
        int w;
        int h;
        tlx = (int) Math.min(p1.getX(), p2.getX());
        tly = (int) Math.min(p1.getY(), p2.getY());
        w = (int) Math.abs(p1.getX() - p2.getX());
        h = (int) Math.abs(p1.getY() - p2.getY());

        Graphics g = comp.getGraphics();
        comp.paintImmediately(comp.getVisibleRect());

        g.drawRect(tlx, tly, w, h);

        comp.revalidate();
    }

}
