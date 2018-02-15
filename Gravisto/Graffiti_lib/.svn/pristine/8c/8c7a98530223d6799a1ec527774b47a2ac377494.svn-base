package org.graffiti.plugins.algorithms.circulardrawing;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;

/**
 * @author demirci Created on Mar 3, 2005
 */
public class CircularLayout {

    private double nodeSize = 25 * Math.sqrt(2.0);

    private double minNodeDistance = nodeSize;

    CircularConst circularConst = new CircularConst();

    private List path;

    private double A; // A = nodeDistance

    private double B; // B = center

    private double C; // C = Radius

    private int horisontalNodeDistance;

    private int vertikalNodeDistance;

    /**
     * Konstruktur
     */

    public CircularLayout() {
        horisontalNodeDistance = 100;
        vertikalNodeDistance = 50;
    }

    /**
     * Konstruktur
     */
    public CircularLayout(List path) {
        this.path = path;
        nodeSize = 25 * Math.sqrt(2.0);
        minNodeDistance = nodeSize;
        A = 0.0;
        B = 0.0;
        C = 0.0;
    }

    /**
     * Layout of the dfs tree.
     * 
     * @param levels
     * @param maxLevel
     */
    public void treeLayout(Hashtable levels, int maxLevel) {
        for (int i = 0; i < levels.size(); i++) {
            List level = (List) levels.get(new Integer(i));
            int levelSize = level.size();
            for (int j = 0; j < levelSize; j++) {
                Node node = (Node) level.get(j);
                int nodeLevel = node.getInteger("level");
                int nodeOrder = node.getInteger("order");
                CoordinateAttribute ca = (CoordinateAttribute) node
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);

                double x = (horisontalNodeDistance * maxLevel)
                        * (nodeOrder + 1) / (levelSize + 1);
                double y = nodeLevel * vertikalNodeDistance + 25.0;
                // System.out.print("Knoten " +
                // node.getInteger("dfsParam.dfsNum"));
                // System.out.println("hat Koordinate (" + x + " , " + y + ")");
                ca.setCoordinate(new Point2D.Double(x, y));
            }
        }
    }

    /**
     * Konstruktur
     * 
     * @param path
     *            node ordering after executing the algorithm Circular.
     * @see org.graffiti.plugins.algorithms.circulardrawing.Circular
     * @param map
     */
    /*
     * public CircularLayout (List path, Map map) { this.path = path; //this.map
     * = map; A = 93.17485623690747; B = 250.0; C = 180.0; }
     */

    public List getPath() {
        return path;
    }

    public void setPath(List l) {
        path = l;
    }

    public double getA() {
        return A;
    }

    public void setA(double a) {
        A = a;
    }

    public double getC() {
        return C;
    }

    public void setC(double c) {
        C = c;
    }

    public void setB(double b) {
        B = b;
    }

    public double getB() {
        return B;
    }

    public void setNodeSize(double nSize) {
        nodeSize = nSize;
    }

    public void setMinNodeDistance(double nodeDist) {
        minNodeDistance = nodeDist;
    }

    public void calculateCenter(int n) {
        double b = nodeSize + minNodeDistance;
        double radious = (n * b) / (2 * Math.PI);
        setB(radious + 50);
        setC(radious);
    }

    /**
     * Embedding in the gravisto
     * 
     */
    public void embeddingPathOnToCircle() {
        Object[] o = path.toArray();
        int n = path.size();
        calculateCenter(n);
        for (int i = o.length - 1; i >= 0; i--) {
            Node node = (Node) o[i];
            node.setInteger("longestPath.position", i);
            int dfsNum = node.getInteger("dfsParam.dfsNum");
            String label = new Integer(dfsNum).toString();
            setNodeLabel(node, label);
            CoordinateAttribute ca = (CoordinateAttribute) node
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            double x = Math.sin(2.0 * Math.PI * i / n) * C + B;
            double y = Math.cos(2.0 * Math.PI * i / n) * C + B;
            ca.setCoordinate(new Point2D.Double(x, y));
        }
        path = new ArrayList();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     * @param val
     *            DOCUMENT ME!
     */
    public void setLabel(Edge e, Integer val) {
        LabelAttribute labelAttr = (LabelAttribute) searchForAttribute(e
                .getAttribute(""), LabelAttribute.class);
        String valStr = val.toString();
        if (labelAttr != null) {
            labelAttr.setLabel(valStr);
        } else { // no label found
            labelAttr = new EdgeLabelAttribute("edgeNumber");
            labelAttr.setLabel(valStr);
            e.addAttribute(labelAttr, "");
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param n
     *            DOCUMENT ME!
     * @param val
     *            DOCUMENT ME!
     */
    public void setNodeLabel(Node n, String val) {
        NodeLabelAttribute labelAttr = (NodeLabelAttribute) searchForAttribute(
                n.getAttribute(""), LabelAttribute.class);

        if (labelAttr != null) {
            labelAttr.setLabel(val);
        }

        else { // no label found
            labelAttr = new NodeLabelAttribute("label");
            labelAttr.setLabel(val);
            n.addAttribute(labelAttr, "");
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param n
     *            DOCUMENT ME!
     * @param val
     *            DOCUMENT ME!
     */
    public void setLabel(Node n, Integer val, NodeLabelPositionAttribute pa) {
        NodeLabelAttribute labelAttr = (NodeLabelAttribute) searchForAttribute(
                n.getAttribute(""), LabelAttribute.class);
        String valStr = val.toString();
        if (labelAttr != null) {

            if (pa != null) {
                labelAttr.setLabel(valStr);
                labelAttr.setPosition(pa);
            } else {
                labelAttr.setLabel(valStr);
            }
        } else { // no label found
            labelAttr = new NodeLabelAttribute("label");
            labelAttr.setLabel(valStr);
            n.addAttribute(labelAttr, "");
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param n
     *            DOCUMENT ME!
     * @param val
     *            DOCUMENT ME!
     */
    public void setNodeColor(Node n, Color c) {
        ColorAttribute colorAttr = (ColorAttribute) searchForAttribute(n
                .getAttribute(""), ColorAttribute.class);

        if (colorAttr != null) {
            colorAttr.setColor(c);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param attr
     *            DOCUMENT ME!
     * @param attributeType
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Attribute searchForAttribute(Attribute attr, Class attributeType) {
        if (attributeType.isInstance(attr))
            return attr;
        else {
            if (attr instanceof CollectionAttribute) {
                Iterator it = ((CollectionAttribute) attr).getCollection()
                        .values().iterator();

                while (it.hasNext()) {
                    Attribute newAttr = searchForAttribute((Attribute) it
                            .next(), attributeType);

                    if (newAttr != null)
                        return newAttr;
                }
            } else if (attr instanceof CompositeAttribute)
                // TODO: treat those correctly; some of those have not yet
                // been correctly implemented
                return null;
        }

        return null;
    }

    /**
     * Set the color of a edge
     * 
     * @param node1
     *            The source node
     * @param node2
     *            The target node
     * @param c
     *            The new color
     */
    /*
     * public static void setEdgeColor(Node node1, Node node2, Color c) {
     * for(Iterator i = node1.getEdgesIterator(); i.hasNext();) { Edge e =
     * (Edge) i.next(); if ((e.getTarget() == node2) || (e.getSource() ==
     * node2)) { ColorAttribute ca = (ColorAttribute)
     * e.getAttribute(GraphicAttributeConstants .GRAPHICS + Attribute.SEPARATOR
     * + GraphicAttributeConstants.FILLCOLOR);
     * 
     * ca.setColor(c); ca = (ColorAttribute)
     * e.getAttribute(GraphicAttributeConstants .GRAPHICS + Attribute.SEPARATOR
     * + GraphicAttributeConstants.FRAMECOLOR);
     * 
     * ca.setColor(c); return; } } }
     */
    /**
     * @param e
     * @param c
     * @see org.graffiti.graph.Edge
     */

    public void setEdgeColor(Edge e, Color c) {

        ColorAttribute ca = (ColorAttribute) e
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.FILLCOLOR);
        ca.setOpacity(0);

        ca = (ColorAttribute) e.getAttribute(GraphicAttributeConstants.GRAPHICS
                + Attribute.SEPARATOR + GraphicAttributeConstants.FRAMECOLOR);
        ca.setOpacity(0);

        String s = GraphicAttributeConstants.FRAMETHICKNESS;

        return;

    }

    public static Color getEdgeColor(Edge e) {
        ColorAttribute ca = (ColorAttribute) e
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.FILLCOLOR);
        Color c = ca.getColor();
        return c;
    }

}
