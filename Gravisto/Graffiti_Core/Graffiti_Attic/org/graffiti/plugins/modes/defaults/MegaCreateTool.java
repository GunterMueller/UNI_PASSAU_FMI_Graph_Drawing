// =============================================================================
//
//   MegaCreateTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MegaCreateTool.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.modes.defaults;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.editor.GraffitiInternalFrame;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DockingAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.AttributeComponent;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugin.view.ShapeNotFoundException;
import org.graffiti.plugin.view.View;
import org.graffiti.undo.AddEdgeEdit;
import org.graffiti.undo.AddNodeEdit;

/**
 * A tool for creating and editing a graph.
 * 
 * @author Holleis
 * @deprecated
 */
@Deprecated
public class MegaCreateTool extends MegaTools {
    /** DOCUMENT ME! */
    protected CoordinateAttribute sourceCA = new CoordinateAttribute(
            GraphicAttributeConstants.COORDINATE);

    /** DOCUMENT ME! */
    protected CoordinateAttribute targetCA = new CoordinateAttribute(
            GraphicAttributeConstants.COORDINATE);

    /** DOCUMENT ME! */
    protected Edge dummyEdge = null;

    /** DOCUMENT ME! */
    protected EdgeGraphicAttribute dummyEdgeGraphAttr;

    /**
     * Component used to associate the key binding for deleting graph elements
     * with.
     */
    protected JComponent keyComponent;

    /** DOCUMENT ME! */
    protected Node dummyNode = null;

    /** Contains the first selected node when adding an edge. */
    protected Node first;

    /** DOCUMENT ME! */
    protected SortedCollectionAttribute bends;

    /** DOCUMENT ME! */
    protected SortedCollectionAttribute dummyBends;

    /** DOCUMENT ME! */
    protected boolean creatingEdge = false;

    /** DOCUMENT ME! */
    protected boolean dragged = false;

    /** DOCUMENT ME! */
    protected int numOfBends = 0;

    /** Removes last bend while creating edges. */
    private Action backAction;

    /** Deletes all selected items (incl. undo support). */
    private Action deleteAction;

    /** Aborts creation of edges. */
    private Action escapeAction;

    /**
     * Constructor for this tool. Registers a key used to delete graph elements.
     */
    public MegaCreateTool() {
        // it makes more dificulties if we allow deleteAction in this tool.
        // besides it is a little bit counterproductive if a
        // deletion action were feasible in a creating tool. ww
        // deleteAction =
        // new AbstractAction() {
        // public void actionPerformed(ActionEvent e) {
        // if(!selection.isEmpty()) {
        // GraphElementsDeletionEdit edit =
        // new GraphElementsDeletionEdit(selection.getElements(),
        // graph, geMap);
        // unmarkAll();
        // fireSelectionChanged();
        // edit.execute();
        // undoSupport.postEdit(edit);
        // }
        // }
        // };
        escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        };

        backAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (numOfBends == 0) {
                    // no bends, just do the same as does escapeAction
                    reset();
                } else {
                    numOfBends--;
                    dummyEdgeGraphAttr.getBends().remove("bend" + numOfBends);
                    bends.remove("bend" + numOfBends);

                    for (View view : session.getViews()) {
                        GraphElementComponent gec = view
                                .getComponentForElement(dummyEdge);

                        try {
                            gec.graphicAttributeChanged(dummyEdgeGraphAttr
                                    .getBends());
                        } catch (ShapeNotFoundException snfe) {
                        }
                    }
                }
            }
        };

        bends = new LinkedHashMapAttribute(GraphicAttributeConstants.BENDS);
    }

    /**
     * The method additionally registers a key used to delete graph elements.
     * 
     * @see org.graffiti.plugins.modes.deprecated.AbstractTool#activate()
     */
    public void activate() {
        super.activate();

        // System.out.println("Activating MegaCreateTool");

        // try {
        // throw new NullPointerException();
        // } catch (NullPointerException e) {
        // e.printStackTrace();
        // }

        try {
            JComponent view = this.session.getActiveView().getViewComponent();

            // I don't really understand why I had to do this, to be honest:
            while (!(view instanceof GraffitiInternalFrame)) {
                if (view.getParent() == null) {
                    break;
                } else {
                    view = (JComponent) view.getParent();
                }
            }

            keyComponent = view;

            String deleteName = "delete";
            view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(
                            KeyStroke.getKeyStroke(
                                    java.awt.event.KeyEvent.VK_DELETE, 0),
                            deleteName);
            view.getActionMap().put(deleteName, deleteAction);

            String escName = "escape";
            view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(
                            KeyStroke.getKeyStroke(
                                    java.awt.event.KeyEvent.VK_ESCAPE, 0),
                            escName);
            view.getActionMap().put(escName, escapeAction);

            String backName = "back";
            view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(
                            KeyStroke.getKeyStroke(
                                    java.awt.event.KeyEvent.VK_BACK_SPACE, 0),
                            backName);
            view.getActionMap().put(backName, backAction);
        } catch (ClassCastException cce) {
            System.err.println("Failed to register a key for some action in "
                    + getClass().getName() + ", activate()");
        }
    }

    /**
     * This method additionaly unregisters the key used for deleting graph
     * elements.
     * 
     * @see org.graffiti.plugins.modes.deprecated.AbstractTool#deactivate()
     */
    public void deactivate() {
        super.deactivate();

        // System.out.println("Deactivating MegaCreateTool");

        reset();

        try {
            keyComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .remove(
                            KeyStroke.getKeyStroke(
                                    java.awt.event.KeyEvent.VK_DELETE, 0));
            keyComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .remove(
                            KeyStroke.getKeyStroke(
                                    java.awt.event.KeyEvent.VK_ESCAPE, 0));
            keyComponent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(
                    KeyStroke.getKeyStroke(
                            java.awt.event.KeyEvent.VK_BACK_SPACE, 0));
        } catch (Exception e) {
            System.err.println("Failed to unregister a hotkey.");
        }
    }

    /**
     * Invoked when the mouse button has been pressed inside the editor panel
     * and handles what has to happen. Is actually empty since all functionality
     * is put into mousePressed etc.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Invoked when the mouse button has been pressed and dragged inside the
     * editor panel and handles what has to happen.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseDragged(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e))
            return;

        dragged = true;
        mouseMoved(e);
    }

    /**
     * Temporarily marks the component under cursor.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseMoved(MouseEvent e) {
        Component src = this.findComponentAt(e);

        if (creatingEdge) {
            // assure that no dummyXXX is hit
            Component tempEdgeComp = null;

            JComponent compSrc = (JComponent) e.getSource();

            if (src instanceof EdgeComponent
                    && ((EdgeComponent) src).getGraphElement()
                            .equals(dummyEdge)) {
                tempEdgeComp = src;
                tempEdgeComp.setVisible(false);

                src = compSrc.findComponentAt((e.getPoint()));

                src = getCorrectComp(src, (View) e.getComponent(), e);
                tempEdgeComp.setVisible(true);
            }

            if (src instanceof NodeComponent
                    && ((NodeComponent) src).getGraphElement()
                            .equals(dummyNode)) {
                src.setVisible(false);

                src = compSrc.findComponentAt((e.getPoint()));
                src = getCorrectComp(src, (View) e.getComponent(), e);
            }

            if (src instanceof EdgeComponent
                    && ((EdgeComponent) src).getGraphElement()
                            .equals(dummyEdge)) {
                tempEdgeComp = src;
                tempEdgeComp.setVisible(false);

                src = compSrc.findComponentAt((e.getPoint()));

                src = getCorrectComp(src, (View) e.getComponent(), e);
                tempEdgeComp.setVisible(true);
            }
        }

        if (lastSelectedComp != src) {
            if (lastSelectedComp != null && !selectedContain(lastSelectedComp)) {
                unDisplayAsMarked((GraphElementComponent) lastSelectedComp);
                src.getParent().repaint();
            }

            if (src instanceof NodeComponent
                    && (((NodeComponent) src).getGraphElement() != dummyNode)) {
                if (!selectedContain(src)) {
                    highlight(src);
                    src.getParent().repaint();
                }

                lastSelectedComp = src;
            }
        }

        if (src instanceof View) {
            lastSelectedComp = null;
        }

        if (dummyNode != null) {
            ((NodeGraphicAttribute) dummyNode
                    .getAttribute(GraphicAttributeConstants.GRAPHICS))
                    .getCoordinate().setCoordinate(e.getPoint());
        }

        if (creatingEdge) {
            for (View view : session.getViews()) {
                view.getViewComponent().repaint();
                view.autoscroll(e.getPoint());
            }
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released)
     * inside the editor panel and handles what has to happen.
     * 
     * @param e
     *            the mouse event
     */
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e))
            return;

        dragged = false;

        Component src = this.findComponentAt(e);

        if (creatingEdge) {
            // assure that no dummyXXX is hit
            Component tempEdgeComp = null;

            JComponent compSrc = (JComponent) e.getSource();

            if (src instanceof EdgeComponent
                    && ((EdgeComponent) src).getGraphElement()
                            .equals(dummyEdge)) {
                tempEdgeComp = src;
                tempEdgeComp.setVisible(false);

                src = compSrc.findComponentAt((e.getPoint()));
                src = getCorrectComp(src, (View) e.getComponent(), e);
                tempEdgeComp.setVisible(true);
            }

            if (src instanceof NodeComponent
                    && ((NodeComponent) src).getGraphElement()
                            .equals(dummyNode)) {
                src.setVisible(false);

                src = compSrc.findComponentAt((e.getPoint()));
                src = getCorrectComp(src, (View) e.getComponent(), e);
            }

            if (src instanceof EdgeComponent
                    && ((EdgeComponent) src).getGraphElement()
                            .equals(dummyEdge)) {
                tempEdgeComp = src;
                tempEdgeComp.setVisible(false);

                src = compSrc.findComponentAt((e.getPoint()));
                src = getCorrectComp(src, (View) e.getComponent(), e);
                tempEdgeComp.setVisible(true);
            }
        }

        NodeGraphicAttribute dummyGraphics = null;

        if (src instanceof NodeComponent) {
            Node clickedNode = (Node) ((NodeComponent) src).getGraphElement();

            unmarkAll();

            // unDisplayAsMarked(getAllMarkedComps());
            if (!creatingEdge) {
                // start adding edge
                creatingEdge = true;

                CollectionAttribute dummyCol = new HashMapAttribute("");
                dummyGraphics = new NodeGraphicAttribute();
                dummyGraphics.getDimension().setHeight(0d);
                dummyGraphics.getDimension().setWidth(0d);
                dummyGraphics.setFrameThickness(0d);

                // ZOOMED
                dummyGraphics.getCoordinate().setCoordinate(e.getPoint());
                dummyCol.add(dummyGraphics, false);
                dummyNode = this.graph.addNode(dummyCol);

                CollectionAttribute dummyEdgeCol = new HashMapAttribute("");
                dummyEdgeGraphAttr = new EdgeGraphicAttribute();
                dummyEdgeGraphAttr
                        .setShape("org.graffiti.plugins.views.defaults.SmoothLineEdgeShape");
                dummyEdgeCol.add(dummyEdgeGraphAttr, true);
                dummyBends = null;

                dummyEdge = this.graph.addEdge(clickedNode, dummyNode, true,
                        dummyEdgeCol);

                this.first = clickedNode;
                sourceCA.setCoordinate(e.getPoint());
            } else if ((this.first != clickedNode)
                    || ((bends != null) && !bends.isEmpty())) {
                // end edge here and create the edge
                // remove all temporary things
                this.graph.deleteEdge(dummyEdge);
                this.graph.deleteNode(dummyNode);
                dummyEdge = null;
                dummyNode = null;

                targetCA.setCoordinate(e.getPoint());

                CollectionAttribute col = new HashMapAttribute("");
                EdgeGraphicAttribute graphics = new EdgeGraphicAttribute();
                col.add(graphics, false);

                DockingAttribute dock = graphics.getDocking();
                dock.setSource("");
                dock.setTarget("");
                graphics.setDocking(dock);

                // setting the graphic attributes to the default values stored
                // in the preferences
                graphics.setThickness(prefs.getDouble("thickness", 1));
                graphics
                        .setFrameThickness(prefs.getDouble("frameThickness", 1));

                // setting the framecolor
                Preferences fc = prefs.node("framecolor");
                int red = fc.getInt("red", 0);
                int green = fc.getInt("green", 0);
                int blue = fc.getInt("blue", 0);
                int alpha = fc.getInt("alpha", 255);
                graphics.getFramecolor().setColor(
                        new Color(red, green, blue, alpha));

                // setting the fillcolor
                fc = prefs.node("fillcolor");
                red = fc.getInt("red", 0);
                green = fc.getInt("green", 0);
                blue = fc.getInt("blue", 0);
                alpha = fc.getInt("alpha", 255);
                graphics.getFillcolor().setColor(
                        new Color(red, green, blue, alpha));

                if (numOfBends > 0) {
                    graphics
                            .setShape(prefs
                                    .get("shape",
                                            "org.graffiti.plugins.views.defaults.SmoothLineEdgeShape"));
                } else {
                    graphics
                            .setShape(prefs
                                    .get("shape",
                                            "org.graffiti.plugins.views.defaults.StraightLineEdgeShape"));
                }

                graphics
                        .setArrowhead(prefs
                                .get("arrowhead",
                                        "org.graffiti.plugins.views.defaults.StandardArrowShape"));

                // setting the lineMode
                Preferences da = prefs.node("dashArray");
                String[] daEntries;

                try {
                    daEntries = da.keys();
                } catch (BackingStoreException bse) {
                    daEntries = new String[0];
                }

                // no dashArray exists
                if (daEntries.length == 0) {
                    graphics.getLineMode().setDashArray(null);
                } else {
                    float[] newDA = new float[daEntries.length];

                    for (int i = daEntries.length - 1; i >= 0; i--) {
                        newDA[i] = da.getFloat(daEntries[i], 10);
                    }

                    graphics.getLineMode().setDashArray(newDA);
                }

                graphics.getLineMode().setDashPhase(
                        prefs.getFloat("dashPhase", 0.0f));

                if (numOfBends > 0) {
                    graphics.setBends(bends);
                    numOfBends = 0;
                    bends = null;
                }

                Edge edge = this.graph.addEdge(this.first, clickedNode, true,
                        col);
                this.first = null;

                // unDisplayAsMarked(selection.getNodes());
                // unDisplayAsMarked(selection.getEdges());
                // selection = new Selection(ACTIVE);
                // selection.add(edge);
                GraphElementComponent tempGeComp = ((View) e.getComponent())
                        .getComponentForElement(edge);

                if (tempGeComp == null) {
                    System.out.println("Something bad happened!"
                            + "Better save and reload/restart.");
                }

                // getSelectedComps().add(tempGeComp);
                mark(tempGeComp, false, this);
                fireSelectionChanged();

                tempGeComp.getParent().repaint();

                // selectionModel.add(selection);
                // selectionModel.setActiveSelection(ACTIVE);
                AddEdgeEdit edit = new AddEdgeEdit(edge, graph, geMap);
                undoSupport.postEdit(edit);

                creatingEdge = false;
            }
        } else {
            // clicked on background (or edge) => bend or new node
            if (creatingEdge) {
                // add a bend ... to edge that is being created
                if (bends == null) {
                    bends = new LinkedHashMapAttribute(
                            GraphicAttributeConstants.BENDS);
                }

                bends.add(new CoordinateAttribute("bend" + numOfBends, e
                        .getPoint()));

                // add a bend ... to dummy edge
                if (dummyBends == null) {
                    dummyBends = new LinkedHashMapAttribute(
                            GraphicAttributeConstants.BENDS);
                    dummyEdgeGraphAttr.setBends(dummyBends);

                    // dummyBends = dummyEdgeGraphAttr.getBends();
                }

                dummyEdgeGraphAttr.getBends().add(
                        new CoordinateAttribute("bend" + numOfBends, e
                                .getPoint()), true);

                // dummyEdgeGraphAttr.getBends().setValue(dummyBends.getCollection());
                numOfBends++;

                // HACK to make the view update the edge ...
                Point pt = e.getPoint();
                pt.setLocation((int) pt.getX() + 1, (int) pt.getY() + 1);
                ((NodeGraphicAttribute) dummyNode
                        .getAttribute(GraphicAttributeConstants.GRAPHICS))
                        .getCoordinate().setCoordinate(pt);
            } else {
                // add new node
                Point2D coord = e.getPoint();

                CollectionAttribute col = new HashMapAttribute("");
                NodeGraphicAttribute graphics = new NodeGraphicAttribute();
                CoordinateAttribute cooAtt = graphics.getCoordinate();
                cooAtt.setCoordinate(coord);
                col.add(graphics, false);

                // setting the graphic attributes to the default values stored
                // in the preferences
                graphics
                        .setFrameThickness(prefs.getDouble("frameThickness", 3));

                // setting the dimension
                Preferences pref = prefs.node("dimension");
                double height = pref.getDouble("height", 25);
                double width = pref.getDouble("width", 25);
                graphics.getDimension().setDimension(
                        new Dimension((int) java.lang.Math.round(height),
                                (int) java.lang.Math.round(width)));

                // setting the framecolor
                pref = prefs.node("framecolor");

                int red = pref.getInt("red", 0);
                int green = pref.getInt("green", 0);
                int blue = pref.getInt("blue", 0);
                int alpha = pref.getInt("alpha", 255);
                graphics.getFramecolor().setColor(
                        new Color(red, green, blue, alpha));

                // setting the fillcolor
                pref = prefs.node("fillcolor");
                red = pref.getInt("red", 0);
                green = pref.getInt("green", 100);
                blue = pref.getInt("blue", 250);
                alpha = pref.getInt("alpha", 100);
                graphics.getFillcolor().setColor(
                        new Color(red, green, blue, alpha));

                // setting the shape
                graphics
                        .setShape(prefs
                                .get("shape",
                                        "org.graffiti.plugins.views.defaults.RectangleNodeShape"));

                // setting the lineMode
                Preferences da = prefs.node("dashArray");
                String[] daEntries;

                try {
                    daEntries = da.keys();
                } catch (BackingStoreException bse) {
                    daEntries = new String[0];
                }

                // no dashArray exists
                if (daEntries.length == 0) {
                    graphics.getLineMode().setDashArray(null);
                } else {
                    float[] newDA = new float[daEntries.length];

                    for (int i = daEntries.length - 1; i >= 0; i--) {
                        newDA[i] = da.getFloat(daEntries[i], 10);
                    }

                    graphics.getLineMode().setDashArray(newDA);
                }

                graphics.getLineMode().setDashPhase(
                        prefs.getFloat("dashPhase", 0.0f));

                Node node = this.graph.addNode(col);

                GraphElementComponent tempGeComp = ((View) e.getComponent())
                        .getComponentForElement(node);

                if (tempGeComp == null) {
                    System.out.println("The system has become instable. "
                            + "Please restart!");
                }

                // tempGeComp.paintComponent(tempGeComp.getGraphics());
                // getSelectedComps().add(tempGeComp);
                mark(tempGeComp, false, this);
                fireSelectionChanged();

                if (tempGeComp.getParent() != null) {
                    tempGeComp.getParent().repaint();
                }

                // selectionModel.add(selection);
                // selectionModel.setActiveSelection(ACTIVE);
                AddNodeEdit edit = new AddNodeEdit(node, graph, geMap);
                undoSupport.postEdit(edit);
            }
        }
    }

    /**
     * Invoked when the mouse button has been released inside the editor panel
     * and handles what has to happen.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseReleased(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e))
            return;

        super.mouseReleased(e);

        if (creatingEdge && dragged && ((bends == null) || bends.isEmpty())) {
            mousePressed(e);
        }
    }

    /**
     * Resets the tool to initial values.
     */

    public void reset() {
        if (dummyNode != null) {
            try {
                this.graph.deleteNode(dummyNode);
            } catch (GraphElementNotFoundException genfe) {
            }
        }

        // dummyEdge should have been removed automatically after
        // removal of dummyNode
        dummyNode = null;
        dummyEdge = null;

        // lastSelectedComp = null;
        creatingEdge = false;
        numOfBends = 0;
        bends = new LinkedHashMapAttribute(GraphicAttributeConstants.BENDS);
    }

    /**
     * Returns the component on which the user clicked (using the information
     * contained in the <code>MouseEvent</code>. This is used to get the
     * <code>GraphElementComponent ge</code> even if clicked on the
     * <code>LabelComponent</code> (associated with <code>ge</code>), for
     * instance.
     * 
     * @param src
     *            DOCUMENT ME!
     * @param view
     *            DOCUMENT ME!
     * @param me
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected Component getCorrectComp(Component src, View view, MouseEvent me) {
        if (src instanceof View || src instanceof GraphElementComponent)
            return src;
        else if (src instanceof AttributeComponent) {
            Component comp = view
                    .getComponentForElement((GraphElement) ((AttributeComponent) src)
                            .getAttribute().getAttributable());

            if (comp == null)
                return (Component) me.getSource();
            else
                return comp;
        } else {
            if (src.equals(me.getComponent()))
                return src;
            else
                return getCorrectComp(src.getParent(), view, me);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
