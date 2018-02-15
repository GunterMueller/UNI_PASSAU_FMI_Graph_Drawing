// =============================================================================
//
//   NodeComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.ImageAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.AttributeComponent;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugin.view.NodeComponentInterface;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * This component represents a <code>org.graffiti.graph.Node</code>.
 * 
 * @version $Revision: 5766 $
 */
public class NodeComponent extends AbstractGraphElementComponent implements
        NodeComponentInterface {
    /**
     * 
     */
    private static final long serialVersionUID = 35662450508219058L;

    /**
     * Constructor for NodeComponent.
     * 
     * @param ge
     */
    public NodeComponent(GraphElement ge) {
        super(ge);
    }

    /**
     * Creates a standard NodeShape (in this case a rectangle) and draws it.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    @Override
    public void createStandardShape() {
        NodeShape newShape = new RectangleNodeShape();
        NodeGraphicAttribute nodeAttr = (NodeGraphicAttribute) ((Node) graphElement)
                .getAttribute(GRAPHICS);

        try {
            newShape.buildShape(nodeAttr);
            this.shape = newShape;
            this.adjustComponentSize();
        } catch (ShapeNotFoundException e) {
            throw new RuntimeException("this should never happen since the "
                    + "standard node shape should always " + "exist." + e);
        }
    }

    /**
     * Called when a graphic attribute of the node represented by this component
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

        String id = attr.getId();

        if (id.equals(FRAMETHICKNESS)) {
            this.adjustComponentSize();
        } else if (attr.getPath().startsWith(
                Attribute.SEPARATOR + GRAPHICS + Attribute.SEPARATOR
                        + COORDINATE)) {
            this.adjustComponentSize();
        } else if (id.equals(DIMENSION)) {
            ((NodeShape) this.shape).buildShape((NodeGraphicAttribute) attr);
            this.adjustComponentSize();
        } else if (attr.getPath().startsWith(
                GRAPHICS + Attribute.SEPARATOR + PORTS)) {
            // ??? TODO (are ports diplayed? ???
            // evtl nur einzelne Kanten updaten?
            // this.updateDependentComponents();
        } else if (!(id.equals(LINEMODE) || id.equals(FRAMECOLOR) || id
                .equals(FILLCOLOR))) {
            this.createNewShape();
            this.adjustComponentSize();
        }

        for (AttributeComponent attrComp : this.attributeComponents.values()) {
            attrComp.setGraphElementShape(this.shape);
            attrComp.attributeChanged(attr);

            // attrComp.recreate();
        }

        this.updateDependentComponents();

        this.repaint();
    }

    /**
     * Draws the shape of the node contained in this component according to the
     * graphic attributes of the node.
     * 
     * @param g
     *            the graphics context in which to draw.
     */
    @Override
    protected void drawShape(Graphics g) {
        super.drawShape(g);

        Graphics2D drawArea = (Graphics2D) g;

        NodeGraphicAttribute nodeAttr = (NodeGraphicAttribute) ((Node) graphElement)
                .getAttribute(GRAPHICS);

        // outline
        Stroke backupStroke = drawArea.getStroke();

        double maxThickness;
        float frameThickness;

        int imageXStart = 0;
        int imageYStart = 0;

        ImageAttribute ia = nodeAttr.getBackgroundImage();
        Image image = ia.getImage().getImage();
        double shapeWidth = shape.getBounds2D().getWidth();
        double shapeHeight = shape.getBounds2D().getHeight();
        boolean adjust = ia.getMaximize();
        boolean tiled = ia.getTiled();
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double destWidth = shapeWidth;
        double destHeight = shapeHeight;

        if (shape instanceof RectangleNodeShape) {
            maxThickness = Math
                    .round(Math.min(nodeAttr.getDimension().getWidth() - 1d,
                            nodeAttr.getDimension().getHeight() - 1d));
            frameThickness = (float) (Math.max(0, Math.min(Math.round(nodeAttr
                    .getFrameThickness() * 2 - 1d), maxThickness)));
            destWidth = shapeWidth - frameThickness;
            destHeight = shapeHeight - frameThickness;
            if (adjust) {
                double x = destWidth / imageWidth;
                if (destHeight >= imageHeight * x) {
                    image = image.getScaledInstance((int) (destWidth),
                            (int) (imageHeight * (destWidth / imageWidth)),
                            Image.SCALE_SMOOTH);
                } else {
                    image = image.getScaledInstance(
                            (int) (imageWidth * (destHeight / imageHeight)),
                            (int) (destHeight), Image.SCALE_SMOOTH);
                }
            }
            imageYStart = (int) ((shapeHeight - image.getHeight(null)) / 2);
            imageXStart = (int) ((shapeWidth - image.getWidth(null)) / 2);
        } else {
            maxThickness = Math
                    .round(Math.min(nodeAttr.getDimension().getWidth() / 2d,
                            nodeAttr.getDimension().getHeight() / 2d));
            frameThickness = (float) Math.min(Math.round(nodeAttr
                    .getFrameThickness()), maxThickness);
            destWidth = shapeWidth - 2 * frameThickness;
            destHeight = shapeHeight - 2 * frameThickness;

            if (adjust) {
                double c = Math.max(destWidth / 2, destHeight / 2);
                double d = Math.min(destWidth / 2, destHeight / 2);
                double s;
                if (destWidth <= destHeight && imageWidth <= imageHeight
                        || destWidth >= destHeight && imageWidth >= imageHeight) {
                    s = d
                            / Math.sqrt(0.25
                                    * Math.pow(Math
                                            .max(imageWidth, imageHeight), 2)
                                    * Math.pow(d, 2)
                                    / Math.pow(c, 2)
                                    + 0.25
                                    * Math.pow(Math
                                            .min(imageWidth, imageHeight), 2));
                } else {
                    s = c
                            / Math.sqrt(0.25
                                    * Math.pow(Math
                                            .max(imageWidth, imageHeight), 2)
                                    * Math.pow(c, 2)
                                    / Math.pow(d, 2)
                                    + 0.25
                                    * Math.pow(Math
                                            .min(imageWidth, imageHeight), 2));
                }

                image = image.getScaledInstance((int) (imageWidth * s),
                        (int) (imageHeight * s), Image.SCALE_SMOOTH);

            }
            imageYStart = (int) ((shapeHeight - image.getHeight(null)) / 2);
            imageXStart = (int) ((shapeWidth - image.getWidth(null)) / 2);
        }

        Stroke stroke = new BasicStroke(frameThickness, DEFAULT_CAP,
                DEFAULT_JOIN, DEFAULT_MITER, nodeAttr.getLineMode()
                        .getDashArray(), nodeAttr.getLineMode().getDashPhase());

        drawArea.setStroke(stroke);

        ColorAttribute color = nodeAttr.getFillcolor();
        drawArea.setPaint(color.getColor());
        drawArea.fill(shape);

        Shape clip = drawArea.getClip();
        drawArea.setClip(shape);
        if (tiled) {
            int currentImageWidth = image.getWidth(null);
            int currentImageHeight = image.getHeight(null);
            do {
                imageXStart -= currentImageWidth;
            } while (imageXStart > 0);
            do {
                imageYStart -= currentImageHeight;
            } while (imageYStart > 0);

            for (int currentX = imageXStart; currentX < shapeWidth; currentX += currentImageWidth) {
                for (int currentY = imageYStart; currentY < shapeHeight; currentY += currentImageHeight) {
                    drawArea.drawImage(image, currentX, currentY, null);
                }
            }
        } else {
            drawArea.drawImage(image, imageXStart, imageYStart, null);
        }
        drawArea.setClip(clip);

        color = nodeAttr.getFramecolor();

        if (nodeAttr.getFrameThickness() == 0d)
            return;

        Color opaqueColor = new Color(color.getRed(), color.getGreen(), color
                .getBlue());
        drawArea.setPaint(opaqueColor);
        drawArea.draw(shape);

        drawArea.setStroke(backupStroke);

    }

    /**
     * Used when the shape changed in the datastructure.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    @Override
    protected void recreate() throws ShapeNotFoundException {
        NodeGraphicAttribute geAttr = (NodeGraphicAttribute) this.graphElement
                .getAttribute(GRAPHICS);

        // get classname of the shape to use and instanciate this
        String shapeClass = geAttr.getShape();

        NodeShape newShape = null;

        try {
            newShape = (NodeShape) InstanceLoader.createInstance(shapeClass);
        } catch (InstanceCreationException ie) {
            throw new ShapeNotFoundException(ie.toString());
        }

        // get graphic attribute and pass it to the shape
        newShape.buildShape(geAttr);
        this.shape = newShape;
        this.adjustComponentSize();
    }

    /**
     * Calls <code>updateShape</code> on all dependent (edge) components.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    protected void updateDependentComponents() {
        for (GraphElementComponent gec : dependentComponents) {
            EdgeComponent ec = null;

            try {
                ec = (EdgeComponent) gec;

                // System.out.println("depcomp: "+ec);
                // ec.updateShape();
                try {
                    // ec.createNewShape();
                    ec.updateEdgeWhileMovingNodes();

                    // System.out.println("NC2");
                } catch (ShapeNotFoundException snfe) {
                    throw new RuntimeException(" should not happen " + snfe);
                }
            } catch (ClassCastException cce) {
                throw new RuntimeException(
                        "Only EdgeComponents should be registered as "
                                + "Others should probably be attributeComponents!"
                                + cce);
            }
        }
    }

    /**
     * Returns the NodeGraphicAttribute of the Node of this NodeComponent.
     * 
     * @return the NodeGraphicAttribute of the Node of this NodeComponent
     */
    public NodeGraphicAttribute getNodeGraphicAttribute() {
        return (NodeGraphicAttribute) ((Node) graphElement)
                .getAttribute(GRAPHICS);
    }

    // /**
    // * Also calls attributeChanged on the (edge) components.
    // * (that will probably call <code>updateShape</code> or whatever is
    // * appropriate)
    // */
    // protected void updateDependentComponents(Attribute attr) {
    // for (Iterator it = dependentComponents.iterator(); it.hasNext();) {
    // EdgeComponent ec = null;
    // try {
    // ec = (EdgeComponent) it.next();
    // ec.attributeChanged(attr);
    // } catch (ShapeNotFoundException snfe) {
    // } catch (ClassCastException cce) {
    // throw new RuntimeException
    // ("Only EdgeComponents should be registered as " +
    // "Others should probably be attributeComponents!" + cce);
    // }
    // }
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
