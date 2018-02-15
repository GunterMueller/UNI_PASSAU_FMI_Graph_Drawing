package org.graffiti.plugins.algorithms.phyloTrees.utility;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.View2D;

/**
 * This class contains utility methods for easier access to Gravisto used
 * throughout the Phylogenetic Tree Plugins.
 */
public class GravistoUtil {
    /**
     * Returns the LabelAttribute of a Node. If no LabelAttribute is contained
     * in the given Node, a {@link AttributeNotFoundException} is thrown.
     * 
     * @param node
     *            The Node whose LabelAttribute is to be returned.
     * @return The LabelAttribute of the Node given as a parameter.
     * @throws AttributeNotFoundException
     *             If no LabelAttribute is set in the given Node.
     */
    public static LabelAttribute getLabelAttribute(Node node)
            throws AttributeNotFoundException {
        assert node != null : "node parameter must not be null";

        if (!node.containsAttribute(GraphicAttributeConstants.LABEL))
            throw new AttributeNotFoundException(
                    "No LabelAttribute is set in the given Node");

        Attribute attr = node.getAttribute(GraphicAttributeConstants.LABEL);
        return (LabelAttribute) attr;
    }

    /**
     * Returns the coordinates of a Node.
     * 
     * @param n
     *            The Node whose coordinates are to be returned.
     * @return The coordinates of the given Node.
     */
    public static Point2D getCoords(Node n) {
        assert n != null : "parameter is a null reference";

        CoordinateAttribute ca = (CoordinateAttribute) n
                .getAttribute(GraphicAttributeConstants.COORD_PATH);

        return ca.getCoordinate();
    }

    /**
     * Sets the given Point2D object as the new coordinates of the given Node.
     * 
     * @param node
     *            The Node whose parameters are to be changed.
     * @param coords
     *            The coordinates that are to be set.
     */
    public static void setCoords(Node node, Point2D coords) {
        assert node != null && coords != null : "Parameter is null reference";

        CoordinateAttribute ca = (CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca.setCoordinate(coords);
    }

    /**
     * Sets the given coordinates as the new coordinates of the given Node.
     * 
     * @param node
     *            The Node whose parameters are to be changed.
     * @param x
     *            The x coordinate to be set.
     * @param y
     *            The y coordinate to be set.
     */
    public static void setCoords(Node node, double x, double y) {
        Point2D coords = new Point2D.Double(x, y);
        setCoords(node, coords);
    }

    /**
     * Sets the shape of an edge.
     * 
     * @param edge
     *            The Edge whose shape is to be set.
     * @param shapeClassName
     *            The fully classified class name of the shape to set.
     */
    public static void setEdgeShape(Edge edge, String shapeClassName) {
        assert edge != null && shapeClassName != null : "Parameter is null reference";

        edge.changeString(GraphicAttributeConstants.SHAPE_PATH, shapeClassName);
    }

    /**
     * Sets the Color of a given Edge to a given Color.
     * 
     * @param edge
     *            The Edge whose color is to be changed.
     * @param newColor
     *            The new Color of the Edge.
     */
    public static void setEdgeColor(Edge edge, Color newColor) {
        assert edge != null && newColor != null : "Parameter is null reference";

        ColorAttribute ca = ((ColorAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.FRAMECOLOR));
        ca.setColor(newColor);
    }

    /**
     * Returns the visible area.
     * 
     * @return The Rectangle2D object describing the area that is visible to the
     *         user.
     */
    public static Rectangle2D getVisibleAreaBounds() {
        View v = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveSession().getActiveView();
        View2D g = (View2D) v;
        Rectangle2D r2d = g.getViewport().getDisplayBounds();
        return r2d;
    }
}
