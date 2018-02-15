// =============================================================================
//
//   EdgeComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.AttributeComponent;
import org.graffiti.plugin.view.EdgeComponentInterface;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.NodeComponentInterface;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * This component represents a <code>org.graffiti.graph.Edge</code>.
 * 
 * @version $Revision: 5766 $
 */
public class EdgeComponent extends AbstractGraphElementComponent implements
        EdgeComponentInterface {

    /**
     * 
     */
    private static final long serialVersionUID = 8471199118320005053L;

    /** The component of the source node of this edge. */
    private NodeComponentInterface sourceComp;

    /** The component of the target node of this edge. */
    private NodeComponentInterface targetComp;

    /**
     * Constructor for EdgeComponent.
     * 
     * @param ge
     *            the edge to which this component belongs.
     * @param source
     *            the <code>NodeComponent</code> of the source node of this
     *            edge.
     * @param target
     *            the <code>NodeComponent</code> of the target node of this
     *            edge.
     */
    public EdgeComponent(GraphElement ge, NodeComponent source,
            NodeComponent target) {
        super(ge);
        this.sourceComp = source;
        this.targetComp = target;
    }

    /**
     * Sets the source component.
     * 
     * @param snc
     *            the source component to be set.
     */
    public void setSourceComponent(NodeComponentInterface snc) {
        // System.out.println("setting source comp to " + snc);
        this.sourceComp = snc;
        nodeComponentChanged();
    }

    /**
     * Sets the source component.
     * 
     * @param tnc
     *            the source component to be set.
     */
    public void setTargetComponent(NodeComponentInterface tnc) {
        this.targetComp = tnc;
        nodeComponentChanged();
    }

    /**
     * Sets a standard shape for this edge. It uses a
     * <code>StraightLineEdgeShape</code>.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    @Override
    public void createStandardShape() {
        EdgeShape newShape = new StraightLineEdgeShape();
        EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) ((Edge) graphElement)
                .getAttribute(GRAPHICS);

        try {
            newShape.buildShape(edgeAttr, (NodeShape) this.sourceComp
                    .getShape(), (NodeShape) this.targetComp.getShape());
        } catch (ShapeNotFoundException e) {
            throw new RuntimeException("this should never happen since the "
                    + "standard edge shape should always " + "exist." + e);
        }

        this.shape = newShape;
        edgeAttr
                .setShape("org.graffiti.plugins.views.defaults.StraightLineEdgeShape");
    }

    /**
     * Draws the shape associated with this component onto the graphics context
     * of this component. This method uses the graphics attributes defined for
     * line color, thickness etc.
     * 
     * @param g
     *            the <code>Graphics</code> context to draw on.
     */
    @Override
    public void drawShape(Graphics g) {
        super.drawShape(g);

        Graphics2D drawArea = (Graphics2D) g;

        AffineTransform prevTransform = drawArea.getTransform();
        AffineTransform transform = (AffineTransform) prevTransform.clone();
        Rectangle2D bounds = shape.getRealBounds2D();
        transform.concatenate(AffineTransform.getTranslateInstance(-bounds
                .getX(), -bounds.getY()));
        drawArea.setTransform(transform);

        // //////Rectangle size = this.getBounds();
        // //////int width = (int)size.getWidth();
        // //////int height = (int)size.getHeight();
        // //////BufferedImage bimg =
        // ////// (BufferedImage) this.createImage(width, height);
        // //////Graphics2D drawArea = bimg.createGraphics();
        // //////drawArea.clearRect(0, 0, width, height);
        // //////drawArea.scale(org.graffiti.plugins.view.graffitiview.GraffitiView.ZOOM,
        // ////// org.graffiti.plugins.view.graffitiview.GraffitiView.ZOOM);
        // drawArea.setTransform
        // (new AffineTransform(
        // (org.graffiti.plugins.view.graffitiview.GraffitiView.ZOOM, 0d, 0d,
        // org.graffiti.plugins.view.graffitiview.GraffitiView.ZOOM, 0d, 0d));
        // drawArea.setTransform(new AffineTransform());
        // drawArea.scale(org.graffiti.plugins.view.graffitiview.GraffitiView.ZOOM,
        // org.graffiti.plugins.view.graffitiview.GraffitiView.ZOOM);
        EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) ((Edge) graphElement)
                .getAttribute(GRAPHICS);

        // outline (includes linewidth, linemode)
        Stroke stroke = new BasicStroke((float) edgeAttr.getFrameThickness(),
                DEFAULT_CAP, DEFAULT_JOIN, DEFAULT_MITER, edgeAttr
                        .getLineMode().getDashArray(), edgeAttr.getLineMode()
                        .getDashPhase());

        // used to draw the arrow
        float arrowThickness = 1f;

        if (edgeAttr.getFrameThickness() < arrowThickness) {
            arrowThickness = (float) edgeAttr.getFrameThickness();
        }

        Stroke smallStroke = new BasicStroke(arrowThickness, DEFAULT_CAP,
                DEFAULT_JOIN, DEFAULT_MITER, edgeAttr.getLineMode()
                        .getDashArray(), edgeAttr.getLineMode().getDashPhase());

        EdgeShape edgeShape = (EdgeShape) this.shape;

        ColorAttribute color;
        color = edgeAttr.getFillcolor();
        drawArea.setPaint(color.getColor());

        drawArea.setStroke(smallStroke);

        Shape hArrow = edgeShape.getHeadArrow();

        if (hArrow != null) {
            drawArea.fill(hArrow);
        }

        Shape tArrow = edgeShape.getTailArrow();

        if (tArrow != null) {
            drawArea.fill(tArrow);
        }

        // draw the outline of the shape according to attributes
        /*
         * must not be transparent because otherwise would lead to problems with
         * overlapping fill and frame
         */
        color = edgeAttr.getFramecolor();

        Color opaqueColor = new Color(color.getRed(), color.getGreen(), color
                .getBlue());
        drawArea.setPaint(opaqueColor);
        drawArea.setStroke(stroke);
        drawArea.draw(shape);
        drawArea.setStroke(smallStroke);

        if (hArrow != null) {
            drawArea.draw(hArrow);
        }

        if (tArrow != null) {
            drawArea.draw(tArrow);
        }
        drawArea.setTransform(prevTransform);
    }

    /**
     * Called when a graphic attribute of the edge represented by this component
     * has changed.
     * 
     * @param attr
     *            the graphic attribute that has triggered the event.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    @Override
    public void graphicAttributeChanged(Attribute attr)
            throws ShapeNotFoundException {
        /*
         * if the type of the shape or the size changed then we have to rebuild
         * the shape
         */
        String id = attr.getId();

        if (id.equals(LINEMODE) || id.equals(FRAMETHICKNESS)
                || id.equals(FRAMECOLOR)) {
            this.repaint();
        } else if (id.equals(DOCKING)) {
            ((EdgeShape) this.shape).buildShape((EdgeGraphicAttribute) attr,
                    (NodeShape) sourceComp.getShape(), (NodeShape) targetComp
                            .getShape());
        } else {
            // if another graphic attribute changed only repaint is needed
            // id.equals(SHAPE) || (id.equals(GRAPHICS))
            // || (id.equals(ARROWHEAD)) || (id.equals(ARROWTAIL))
            // || (id.equals(BENDS)) || (id.equals(LINETYPE))
            // || (id.equals(SHAPE)) {
            this.createNewShape();
        }
    }

    /**
     * Calls buildShape if no NodeShapes have changed.
     */
    public void updateShape() {
        nodeComponentChanged();

        // EdgeGraphicAttribute attr =
        // (EdgeGraphicAttribute) graphElement.getAttribute(GRAPHICS);
        // try {
        // ((EdgeShape) this.shape).buildShape(
        // attr,
        // (NodeShape) sourceComp.getShape(),
        // (NodeShape) targetComp.getShape());
        // } catch (ShapeNotFoundException e) {
        // throw new RuntimeException( "Arrowshape not found, should not"
        // + "have happened here.... " + e);
        // }
    }

    /**
     * Used when the shape changed in the datastructure. Makes the painter
     * create a new shape.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    @Override
    protected void recreate() throws ShapeNotFoundException {
        EdgeGraphicAttribute geAttr = (EdgeGraphicAttribute) this.graphElement
                .getAttribute(GRAPHICS);

        String shapeClass = geAttr.getShape();
        EdgeShape newShape = null;

        try {
            newShape = (EdgeShape) InstanceLoader.createInstance(shapeClass);
        } catch (InstanceCreationException ie) {
            throw new ShapeNotFoundException(ie.toString());
        }

        // get graphic attribute and pass it to the shape
        newShape.buildShape(geAttr, (NodeShape) this.sourceComp.getShape(),
                (NodeShape) this.targetComp.getShape());
        this.shape = newShape;
        this.adjustComponentSize();

        for (AttributeComponent attrComp : this.attributeComponents.values()) {
            attrComp.setShift(this.getLocation());
            attrComp.setGraphElementShape(this.shape);
            attrComp.createNewShape();
        }
    }

    /**
     * "Moves" the edge when a doching node is moved. Creating a new shape is
     * not necessary here, since the old one cannot be changed while moving and
     * changing the edge shape (the line mode, to be exact) calls another
     * method.
     * 
     * @throws ShapeNotFoundException
     *             if a shape could not be found
     */
    public void updateEdgeWhileMovingNodes() throws ShapeNotFoundException {
        EdgeGraphicAttribute geAttr = (EdgeGraphicAttribute) this.graphElement
                .getAttribute(GRAPHICS);
        EdgeShape newShape = null;

        try {
            newShape = (EdgeShape) InstanceLoader.createInstance(this.shape
                    .getClass());
        } catch (InstanceCreationException ie) {
            throw new ShapeNotFoundException(ie.toString());
        }

        // get graphic attribute and pass it to the shape
        newShape.buildShape(geAttr, (NodeShape) this.sourceComp.getShape(),
                (NodeShape) this.targetComp.getShape());
        this.shape = newShape;
        this.adjustComponentSize();

        // nodeComponentChanged();
        for (AttributeComponent attrComp : this.attributeComponents.values()) {
            attrComp.setShift(this.getLocation());
            attrComp.setGraphElementShape(this.shape);
            attrComp.createNewShape();
        }
    }

    /**
     * Called when source or target node shape have not changed.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    private void nodeComponentChanged() {
        try {
            ((EdgeShape) this.shape).buildShape(
                    (EdgeGraphicAttribute) this.graphElement
                            .getAttribute(GRAPHICS),
                    (NodeShape) this.sourceComp.getShape(),
                    (NodeShape) this.targetComp.getShape());
            repaint();
        } catch (ShapeNotFoundException e) {
            throw new RuntimeException("Arrowshape not found, should not"
                    + " have happened here ..." + e);
        }
    }

    // /**
    // * Just calls <code>adjustComponentSize</code> with parameter
    // * <code>false</code>
    // */
    // protected void adjustComponentSize() {
    // // this.adjustComponentSize(false);
    // GraphElementGraphicAttribute nAttr =
    // (GraphElementGraphicAttribute)this.graphElement.
    // getAttribute(GRAPHICS);
    // Rectangle2D bounds = this.shape.getRealBounds2D();
    // this.setSize((int)Math.round(shapeSize.getWidth() + fThickness),
    // (int)Math.round(shapeSize.getHeight() + fThickness));
    // }
    // Rect2D bounds = ((EdgeShape)shape).getRealBounds2D();
    // this.setLocation
    // ((int)Math.round(coord.getX() - this.getHeight()/2d),
    // (int)Math.round(coord.getY() - this.getWidth()/2d));
    // }
    // }
    // /**
    // * Called whenever the size of the shape within this component has
    // changed.
    // */
    // protected void adjustComponentSize(boolean onlyLocation) {
    // GraphElementGraphicAttribute nAttr =
    // (GraphElementGraphicAttribute)this.graphElement.
    // getAttribute(GRAPHICS);
    // if (!onlyLocation) {
    // Rectangle2D shapeSize = this.shape.getBounds2D();
    // double fThickness = nAttr.getFrameThickness();
    // this.setSize((int)Math.round(shapeSize.getWidth() + fThickness),
    // (int)Math.round(shapeSize.getHeight() + fThickness));
    // }
    // Rect2D bounds = ((EdgeShape)shape).getRealBounds2D();
    // this.setLocation
    // ((int)Math.round(coord.getX() - this.getHeight()/2d),
    // (int)Math.round(coord.getY() - this.getWidth()/2d));
    // }

    void reverse() {
        NodeComponentInterface tmp = sourceComp;
        sourceComp = targetComp;
        targetComp = tmp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(int x, int y) {
        return shape.contains(x, y);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
