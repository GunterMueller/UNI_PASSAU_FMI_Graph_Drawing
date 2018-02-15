// =============================================================================
//
//   TestAttributeComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestAttributeComponent.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.attributecomponents.test;

import java.awt.FlowLayout;

import javax.swing.JLabel;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.attributecomponent.AbstractAttributeComponent;
import org.graffiti.plugin.view.ShapeNotFoundException;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5769 $ $Date: 2006-01-04 10:21:57 +0100 (Mi, 04 Jan 2006)
 *          $
 */
public class TestAttributeComponent extends AbstractAttributeComponent
        implements GraphicAttributeConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 3417314294320135349L;
    /** The <code>JLabel</code> that represents the label text. */
    protected JLabel label;

    /**
     * Called when an attribute of this component has changed.
     * 
     * @param attr
     *            the attribute that has triggered the event
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    @Override
    public void attributeChanged(Attribute attr) throws ShapeNotFoundException {
        // <code>attr</code> is often a CollectionAttribute,
        // e.g. after pressing apply in the inspector.
        GraphElement ge = (GraphElement) this.attr.getAttributable();

        if (ge instanceof Node) {
            Node n = (Node) ge;

            if (attr instanceof CollectionAttribute) {
                if (attr.getPath().equals("")) {
                    changeParameters(((CollectionAttribute) attr)
                            .getCollection().get(GRAPHICS), n);
                } else if (attr.getPath().equals(GRAPHICS)) {
                    changeParameters(attr, n);
                } else {
                    recreate();
                }
            } else if (attr.getId().equals("annotation")) {
                label.setText((String) attr.getValue());
                setSize(getPreferredSize());
            } else if (attr.getPath().startsWith(
                    Attribute.SEPARATOR + GRAPHICS + Attribute.SEPARATOR
                            + COORDINATE)) {
                setLocation(getNewX(n), getNewY(n));
            } else {
                recreate();
            }

            repaint();
        }
    }

    /**
     * Called when the component is created or when it must be recreated.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    @Override
    public void recreate() throws ShapeNotFoundException {
        System.out.println("recreate");

        GraphElement ge = (GraphElement) this.attr.getAttributable();

        if (ge instanceof Node) {
            Node n = (Node) ge;

            removeAll();

            FlowLayout fl = new FlowLayout(FlowLayout.CENTER, 0, 0);
            setLayout(fl);
            label = new JLabel((String) this.attr.getValue());
            add(label);
            setSize(getPreferredSize());
            setLocation(getNewX(n), getNewY(n));

            /*
             * MainFrame mf = GraffitiSingleton.getInstance().getMainFrame();
             * AbstractView view = (AbstractView)
             * mf.getActiveSession().getActiveView(); Container cont =
             * view.getParent(); setBackground(cont.getBackground());
             * 
             * setBackground(Color.white);
             */

            validate();
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param n
     *            Node of <code>attr</code>
     * 
     * @return new x coordinate
     */
    private int getNewX(Node n) {
        int x = (int) n.getDouble(GRAPHICS + Attribute.SEPARATOR + COORDINATE
                + Attribute.SEPARATOR + X);
        int wdiv2 = (int) (n.getDouble(GRAPHICS + Attribute.SEPARATOR
                + DIMENSION + Attribute.SEPARATOR
                + GraphicAttributeConstants.WIDTH) / 2d);

        return x + wdiv2;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param n
     *            Node of <code>attr</code>
     * 
     * @return new y coordinate
     */
    private int getNewY(Node n) {
        int y = (int) n.getDouble(GRAPHICS + Attribute.SEPARATOR + COORDINATE
                + Attribute.SEPARATOR + Y);
        int hdiv2 = (int) (n.getDouble(GRAPHICS + Attribute.SEPARATOR
                + DIMENSION + Attribute.SEPARATOR
                + GraphicAttributeConstants.HEIGHT) / 2d);

        return y + hdiv2;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param graphicsAttr
     *            <code>CollectionAttribute</code>
     * @param n
     *            Node of <code>attr</code>
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    private void changeParameters(Object graphicsAttr, Node n)
            throws ShapeNotFoundException {
        if ((graphicsAttr != null)
                && (graphicsAttr instanceof CollectionAttribute)) {
            CollectionAttribute cAttr = (CollectionAttribute) graphicsAttr;
            Object annotationObject = cAttr.getCollection().get("annotation");

            if ((annotationObject != null)
                    && (annotationObject instanceof TestAttribute)) {
                System.out.println("annotation changed");

                TestAttribute testAttr = (TestAttribute) annotationObject;
                label.setText((String) testAttr.getValue());
                setSize(getPreferredSize());
            }

            Object coordinateObject = cAttr.getCollection().get(COORDINATE);
            Object dimensionObject = cAttr.getCollection().get(DIMENSION);

            if (((coordinateObject != null) && (coordinateObject instanceof CoordinateAttribute))
                    || ((dimensionObject != null) && (dimensionObject instanceof DimensionAttribute))) {
                System.out.println("coordinates or dimension changed");

                setLocation(getNewX(n), getNewY(n));
            }
        } else {
            recreate();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
