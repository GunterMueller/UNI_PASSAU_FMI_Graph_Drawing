// =============================================================================
//
//   AbstractGraphElementComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractGraphElementComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.AttributeComponent;
import org.graffiti.plugin.view.GraffitiViewComponent;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugin.view.GraphElementShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.Zoomable;

/**
 * Class that shares common members for all GraphElementComponents.
 * 
 * @version $Revision: 5766 $
 */
public abstract class AbstractGraphElementComponent extends
        GraphElementComponent implements GraffitiViewComponent,
        GraphicAttributeConstants {
    /**
     * 
     */
    private static final long serialVersionUID = 5187661095190599901L;

    /** The <code>GraphElement</code> that is represented by this component. */
    protected GraphElement graphElement;

    /** The <code>shape</code> that is drawn onto that component. */
    protected GraphElementShape shape;

    /**
     * A list of components whose position is dependent on the position of this
     * shape. This is only meant for edges that depend on the position (and
     * other graphics attributes) of nodes.
     */
    protected List<GraphElementComponent> dependentComponents;

    /**
     * A mapping between attribute classnames and attributeComponent classnames
     * that this <code>GraphElement</code> has. These attributes are therefore
     * attribute and their position is dependent on the position (and size) of
     * this GraphElement. (this applies mainly to nodes)
     */
    protected Map<Attribute, AttributeComponent> attributeComponents;

    /**
     * Constructor for GraphElementComponent.
     * 
     * @param ge
     *            DOCUMENT ME!
     */
    protected AbstractGraphElementComponent(GraphElement ge) {
        super();
        this.graphElement = ge;
        attributeComponents = new HashMap<Attribute, AttributeComponent>();
        dependentComponents = new ArrayList<GraphElementComponent>();
        this.setOpaque(false);
    }

    /**
     * Returns GraphElementShape object
     * 
     * @return DOCUMENT ME!
     */
    public GraphElementShape getShape() {
        return this.shape;
    }

    /**
     * Adds an <code>Attribute</code> and its <code>GraffitiViewComponent</code>
     * to the list of registered attributes that can be displayed. This
     * attribute is then treated as dependent on the position, size etc. of this
     * <code>GraphElement</code>.
     * 
     * @param attr
     *            the attribute that is registered as being able to be
     *            displayed.
     * @param ac
     *            the component that will be used to display the attribute.
     */
    public void addAttributeComponent(Attribute attr, AttributeComponent ac) {
        attributeComponents.put(attr, ac);
    }

    /**
     * Adds a <code>GraphElementComponent</code> to the list of dependent
     * <code>GraphElementComponent</code>s. These will nearly always be
     * <code>EdgeComponent</code>s that are dependent on their source or target
     * nodes.
     * 
     * @param comp
     *            the <code>GraphElementComponent</code> that is added to the
     *            list of dependent components.
     */
    public void addDependentComponent(GraphElementComponent comp) {
        this.dependentComponents.add(comp);
    }

    /**
     * Called when an attribute of the GraphElement represented by this
     * component has changed.
     * 
     * @param attr
     *            the attribute that has triggered the event.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    public void attributeChanged(Attribute attr) throws ShapeNotFoundException {
        if ("".equals(attr.getPath())) {
            this.graphicAttributeChanged(attr);
            this.nonGraphicAttributeChanged(attr);

            return;
        }

        if (attr.getPath().startsWith(
                Attribute.SEPARATOR + GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR)
                || attr.getPath().equals(GraphicAttributeConstants.GRAPHICS)) {
            this.graphicAttributeChanged(attr);
        } else {
            this.nonGraphicAttributeChanged(attr);
        }
    }

    /**
     * Removes a <code>GraphElementComponent</code> from the list of dependent
     * <code>GraphElementComponent</code>s.
     */
    public void clearDependentComponentList() {
        this.dependentComponents = new ArrayList<GraphElementComponent>();
    }

    /**
     * Called to initialise the shape of the NodeComponent correctly. Also calls
     * <code>repaint()</code>.
     * 
     * @exception ShapeNotFoundException
     *                thrown when the shapeclass couldn't be resolved.
     */
    public void createNewShape() throws ShapeNotFoundException {
        this.recreate();
    }

    /**
     * Called to initialise and draw a standard shape, if the specified
     * shapeclass could not be found.
     */
    public abstract void createStandardShape();

    /**
     * Returns the attributeComponents of given attribute.
     * 
     * @param attr
     * 
     * @return Map
     */
    public AttributeComponent getAttributeComponent(Attribute attr) {
        return attributeComponents.get(attr);
    }

    /**
     * Returns the attributeComponents of given attribute.
     * 
     * @return Map
     */
    public Iterator<AttributeComponent> getAttributeComponentIterator() {
        return attributeComponents.values().iterator();
    }

    /**
     * Returns the graphElement.
     * 
     * @return GraphElement
     */
    public GraphElement getGraphElement() {
        return graphElement;
    }

    /**
     * Removes all entries in the attributeComponent list.
     */
    public void clearAttributeComponentList() {
        this.attributeComponents = new HashMap<Attribute, AttributeComponent>();
    }

    /**
     * Returns whether the given coordinates lie within this component and
     * within its encapsulated shape. The coordinates are assumed to be relative
     * to the coordinate system of this component.
     * 
     * @see java.awt.Component#contains(int, int)
     */
    @Override
    public boolean contains(int x, int y) {
        AffineTransform zoom = getZoom();

        // x = (int) Math.round((x - ((p2dZoom.getX() - 1) * getX())) /
        // p2dZoom.getX());
        // y = (int) Math.round((y - ((p2dZoom.getY() - 1) * getY())) /
        // p2dZoom.getY());
        // tempAT.scale((tempAT.getScaleX() - 1)/tempAT.getScaleX(),
        // (tempAT.getScaleY() - 1)/tempAT.getScaleY());
        // AffineTransform tempAT = new AffineTransform(
        // (zoom.getScaleX() - 1)/zoom.getScaleX(),
        // zoom.getShearY(),
        // zoom.getShearX(),
        // (zoom.getScaleY() - 1)/zoom.getScaleY(),
        // zoom.getTranslateX(),
        // zoom.getTranslateY());
        // AffineTransform tempAT = (AffineTransform)zoom.clone();
        // Point2D zoomedLoc = tempAT.transform(getLocation(), null);
        // Point2D xyPt = null;
        // try {
        // xyPt =
        // zoom.inverseTransform(
        // new Point2D.Double(x - zoomedLoc.getX(), y - zoomedLoc.getY()),
        // null);
        // } catch (NoninvertibleTransformException nite) {
        // // when setting the zoom, it must have been checked that
        // // the transform is invertible
        // }
        //        
        // x = (int)xyPt.getX();
        // y = (int)xyPt.getY();
        Point2D p = null;

        try {
            p = zoom.inverseTransform(
                    new Point2D.Double(x + getX(), y + getY()), null);
        } catch (NoninvertibleTransformException e) {
        }

        // Point2D pt = null;
        // try {
        // pt = zoom.inverseTransform(getLocation(), null);
        // } catch (NoninvertibleTransformException e) {
        // }
        x = (int) (p.getX() - getX());
        y = (int) (p.getY() - getY());

        // x = (int)(p.getX());
        // y = (int)(p.getY());
        return (super.contains(x, y) && this.shape.contains(x, y));
    }

    /**
     * Called when a graphic attribute of the GraphElement represented by this
     * component has changed.
     * 
     * @param attr
     *            the graphic attribute that has triggered the event.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    public void graphicAttributeChanged(Attribute attr)
            throws ShapeNotFoundException {
        /*
         * if the type of the shape or the size changed then we have to rebuild
         * the shape
         */
        if (attr.getId().equals(SHAPE) || (attr.getId().equals(GRAPHICS))) {
            for (AttributeComponent ac : attributeComponents.values()) {
                ac.recreate();
            }

            this.createNewShape();

            // System.out.println("GEC1");
        } else { // if another graphic attribute changed only repaint is needed

            for (AttributeComponent ac : attributeComponents.values()) {
                ac.repaint();
            }

            this.repaint();
        }
    }

    /**
     * Called when a non-graphic attribute of the GraphElement represented by
     * this component has changed.
     * 
     * @param attr
     *            the attribute that has triggered the event.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    public void nonGraphicAttributeChanged(Attribute attr)
            throws ShapeNotFoundException {
        // System.out.println("nonGraphicAttributeChanged");
        Attribute runAttr = attr;

        while (!"".equals(runAttr.getPath())) {
            if (attributeComponents.containsKey(runAttr)) {
                // System.out.println("call attrChanged");
                ((GraffitiViewComponent) attributeComponents.get(runAttr))
                        .attributeChanged(attr);

                break;
            }

            // "else":
            runAttr = runAttr.getParent();
        }
    }

    /**
     * Paints the graph element contained in this component.
     * 
     * @param g
     *            the graphics context in which to paint.
     * 
     * @see javax.swing.JComponent#paintComponent(Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
        // AffineTransform transform = ((Graphics2D) g).getTransform();
        // transform.setTransform(1, transform.get);
        // ////////////////// ((Graphics2D)g).scale(getZoom().getX(),
        // getZoom().getY());
        // Graphics2D g2 = (Graphics2D)g.create();
        // ((Graphics2D) g).setTransform(AffineTransform.getScaleInstance(
        // getZoom().getX(), getZoom().getY()));
        // g2.dispose();
        // // int tmp_x = x;
        // // int tmp_y = y;
        // // int tmp_width = width;
        // // int tmp_height = height;
        // //
        // //// done by adjustcomponentsize
        // // setBounds((int) (x * zoom.getX()), (int) (y * zoom.getY()),
        // // (int) (width * zoom.getX()), (int) (height * zoom.getY()));
        // // setBounds(x, y, (int) (width * zoom.getX()), (int) (height *
        // zoom.getY()));
        //
        // // x = tmp_x;
        // // y = tmp_y;
        // // width = tmp_width;
        // // height = tmp_height;
        drawShape(g);
        adjustComponentSize();
        super.paintComponent(g);
    }

    // /**
    // * @see java.awt.Component#setBounds(int, int, int, int)
    // */
    // public void setBounds(int x, int y, int width, int height) {
    // super.setBounds(x, y, width, height);
    // this.x = x;
    // this.y = y;
    // this.width = width;
    // this.height = height;
    // }

    /**
     * Removes a <code>GraffitiViewComponent</code> of an <code>Attribute</code>
     * from collection of attribute components.
     * 
     * @param attr
     *            the attribute that has to be removed
     */
    public void removeAttributeComponent(Attribute attr) {
        attributeComponents.remove(attr);
    }

    /**
     * Removes a <code>GraphElementComponent</code> from the list of dependent
     * <code>GraphElementComponent</code>s.
     * 
     * @param comp
     *            the <code>GraphElementComponent</code> that is removed from
     *            the list of dependent components.
     */
    public void removeDependentComponent(GraphElementComponent comp) {
        this.dependentComponents.remove(comp);
    }

    /**
     * Retrieve the zoom value from the view this component is displayed in.
     * 
     * @return DOCUMENT ME!
     */
    protected AffineTransform getZoom() {
        Container parent = getParent();

        if (parent instanceof Zoomable) {
            AffineTransform zoom = ((Zoomable) parent).getZoomTransform();

            return zoom;
        }

        return View.NO_ZOOM;
    }

    /**
     * Draws the shape of the graph element contained in this component
     * according to its graphic attributes.
     * 
     * @param g
     *            the graphics context in which to draw.
     */
    protected void drawShape(Graphics g) {
        Graphics2D drawArea = (Graphics2D) g;
        drawArea.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        drawArea.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);
        drawArea.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE);
    }

    /**
     * Used when the shape changed in the datastructure. Makes the painter
     * create a new shape.
     */
    protected abstract void recreate() throws ShapeNotFoundException;

    /**
     * Called whenever the size of the shape within this component has changed.
     */
    protected void adjustComponentSize() {
        Rectangle2D bounds = this.shape.getRealBounds2D();

        this.setBounds((int) (bounds.getX()), (int) (bounds.getY()),
                (int) (bounds.getWidth()), (int) (bounds.getHeight()));

        // ZOOMED
        // this.setBounds((int)(bounds.getX() * getZoom().getX()),
        // (int)(bounds.getY() * getZoom().getY()),
        // (int)(bounds.getWidth() * getZoom().getX()),
        // (int)(bounds.getHeight() * getZoom().getY()));
        // half ZOOMED
        // this.setBounds((int)Math.ceil(bounds.getX()),
        // (int)Math.ceil(bounds.getY()),
        // (int)Math.ceil(bounds.getWidth() * getZoom().getX()),
        // (int)Math.ceil(bounds.getHeight() * getZoom().getY()));
        // // not ZOOMED
        // this.setLocation((int) Math.floor(bounds.getX()),
        // (int) Math.floor(bounds.getY()));
        // this.setSize((int) Math.ceil((((bounds.getWidth()) + 1))),
        // (int) Math.ceil((((bounds.getHeight())) + 1)));
        // // ZOOMED
        // this.setLocation((int) Math.floor(bounds.getX() * zoom.getX()),
        // (int) Math.floor(bounds.getY() * zoom.getY()));
        // this.setSize((int) Math.ceil((((bounds.getWidth() * zoom.getX()) +
        // 1))),
        // (int) Math.ceil((((bounds.getHeight()) * zoom.getY()) + 1)));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
