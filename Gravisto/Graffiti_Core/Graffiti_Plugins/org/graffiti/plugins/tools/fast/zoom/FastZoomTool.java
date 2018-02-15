// =============================================================================
//
//   ZoomTool.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.fast.zoom;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SpringLayout;

import org.graffiti.attributes.Attribute;
import org.graffiti.editor.GraffitiInternalFrame;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.gui.GraffitiToolbar;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.View2D;
import org.graffiti.plugin.view.ViewListener;
import org.graffiti.plugin.view.Viewport;
import org.graffiti.plugin.view.ViewportAdapter;
import org.graffiti.plugin.view.ViewportListener;
import org.graffiti.plugin.view.Zoomable;
import org.graffiti.plugins.views.defaults.GraffitiView;
import org.graffiti.plugins.views.fast.MathUtil;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;
import org.graffiti.util.SpringUtilities;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FastZoomTool extends GraffitiToolbar implements ViewListener,
        SessionListener, ViewportListener {
    /**
     * 
     */
    private static final long serialVersionUID = -507249060586314557L;
    private static final Pattern PATTERN = Pattern
            .compile("\\s*(\\d+(?:\\.\\d+)?)\\s*%?\\s*");
    private static final String[] COMBO_VALUES = new String[] { " 10%", " 25%",
            " 50%", " 75%", "100%", "125%", "150%", "175%", "200%" };
    private static final String DEFAULT_VALUE = "100%";

    // On zoomToFit, the width of the free border surrounding the graph fitting
    // into the viewport in display coordinates.
    private static final int SPACE = 50;
    private JComboBox combo;
    private JButton zoomToFitButton;
    private View currentView;
    private Viewport currentViewport;
    private boolean ignoreAction;

    public FastZoomTool() {
        super("FastZoom");
        ignoreAction = true;
        setLayout(new SpringLayout());
        combo = new JComboBox(COMBO_VALUES);
        combo.setEditable(true);
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ignoreAction)
                    return;
                onValueSelection();
            }
        });
        add(combo);
        ImageIcon icon = new ImageIcon(FastZoomPlugin.class
                .getResource("magnifier.png"));
        icon.setImage(icon.getImage().getScaledInstance(16, 16,
                Image.SCALE_SMOOTH));
        zoomToFitButton = new JButton(icon);
        zoomToFitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomToFit();
            }
        });
        add(zoomToFitButton);
        SpringUtilities.makeCompactGrid(this, 1, 2, 0, 0, 0, 0);
        GraffitiSingleton.getInstance().getMainFrame()
                .getViewportEventDispatcher().addListener(this);
        ignoreAction = false;
        updateText();
    }

    public void viewChanged(View newView) {
        currentView = newView;
        if (newView == null)
            return;
        if (newView instanceof View2D) {
            setViewPort(((View2D) newView).getViewport());
        } else {
            setViewPort(new ViewportAdapter(newView));
        }
    }

    public void sessionChanged(Session s) {
        if (s == null) {
            viewChanged(null);
        } else {
            viewChanged(s.getActiveView());
        }
    }

    public void sessionDataChanged(Session s) {
        sessionChanged(s);
    }

    public void setViewPort(Viewport viewport) {
        currentViewport = viewport;
        updateText();
    }

    public void onViewportChange(Viewport viewport) {
        if (currentViewport == viewport) {
            updateText();
        }
    }

    private void onValueSelection() {
        Object obj = combo.getSelectedItem();
        if (!(obj instanceof String) || currentViewport == null) {
            updateText();
            return;
        }
        String value = (String) obj;
        Matcher matcher = PATTERN.matcher(value);
        if (!matcher.matches()) {
            updateText();
            return;
        }
        currentViewport.setZoom(Double.valueOf(matcher.group(1)) / 100.0);
    }

    private void updateText() {
        ignoreAction = true;
        try {
            if (currentViewport == null) {
                combo.setSelectedItem(DEFAULT_VALUE);
            } else {
                combo.setSelectedItem((int) (currentViewport.getZoom() * 100)
                        + "%");
            }
        } finally {
            ignoreAction = false;
        }
    }

    private void zoomToFit() {
        if (currentView instanceof GraffitiView) {
            ancientZoomToFit();
            return;
        }
        if (currentViewport == null)
            return;
        Rectangle2D logicalElementBounds = currentViewport
                .getLogicalElementsBounds();
        Rectangle2D rotatedElementBounds = MathUtil.getTransformedBounds(
                logicalElementBounds, AffineTransform
                        .getRotateInstance(currentViewport.getRotation()));
        Rectangle2D physicalDisplayBounds = currentViewport.getDisplayBounds();
        double zoom = Math.min((physicalDisplayBounds.getWidth() - 2 * SPACE)
                / rotatedElementBounds.getWidth(), (physicalDisplayBounds
                .getHeight() - 2 * SPACE)
                / rotatedElementBounds.getHeight());
        zoom = Math.min(zoom, 1.0);
        currentViewport.setZoom(zoom);
        Rectangle2D zrElementBounds = MathUtil.getTransformedBounds(
                logicalElementBounds, currentViewport
                        .getZoomRotationTransform());
        Point2D pan = new Point2D.Double(-zrElementBounds.getCenterX()
                + physicalDisplayBounds.getCenterX(), -zrElementBounds
                .getCenterY()
                + physicalDisplayBounds.getCenterY());
        currentViewport.setTranslation(pan);
    }

    private void ancientZoomToFit() {
        try {
            final MainFrame mf = GraffitiSingleton.getInstance().getMainFrame();
            final Graph graph = mf.getActiveSession().getGraph();

            double minX = Integer.MAX_VALUE;
            double minY = Integer.MAX_VALUE;

            for (Node node : graph.getNodes()) {
                CoordinateAttribute ca = (CoordinateAttribute) node
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);

                DimensionAttribute da = (DimensionAttribute) node
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.DIMENSION);

                // get the minimal sum of a nodes coordinates and it's dimension
                minX = Math.min(ca.getX() - da.getWidth() / 2d, minX);
                minY = Math.min(ca.getY() - da.getHeight() / 2d, minY);
            }

            for (Edge edge : graph.getEdges()) {
                EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                        .getAttribute(GraphicAttributeConstants.GRAPHICS);
                Collection<Attribute> bendsColl = ega.getBends()
                        .getCollection().values();
                for (Attribute attr : bendsColl) {
                    CoordinateAttribute ca = (CoordinateAttribute) attr;
                    minX = Math.min(ca.getX(), minX);
                    minY = Math.min(ca.getY(), minY);
                }
            }

            // the minimal distance to the left and top border
            minX -= 10d;
            minY -= 10d;

            double maxX = Integer.MIN_VALUE;
            double maxY = Integer.MIN_VALUE;

            for (Node node : graph.getNodes()) {
                CoordinateAttribute ca = (CoordinateAttribute) node
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
                DimensionAttribute da = (DimensionAttribute) node
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.DIMENSION);

                // move the node to the left and top border
                final Point2D p = ca.getCoordinate();
                p.setLocation(ca.getX() - minX, ca.getY() - minY);
                ca.setCoordinate(p);

                // get the maximal sum of a nodes coordinates and it's dimension
                maxX = Math.max(p.getX() + da.getWidth() / 2d, maxX);
                maxY = Math.max(p.getY() + da.getHeight() / 2d, maxY);
            }

            final double bendOffset = 10d;

            for (Edge edge : graph.getEdges()) {
                EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                        .getAttribute(GraphicAttributeConstants.GRAPHICS);
                Collection<Attribute> bendsColl = ega.getBends()
                        .getCollection().values();
                for (Attribute attr : bendsColl) {
                    CoordinateAttribute ca = (CoordinateAttribute) attr;

                    // move the node to the left and top border
                    final Point2D p = ca.getCoordinate();
                    p.setLocation(ca.getX() - minX, ca.getY() - minY);
                    ca.setCoordinate(p);
                    maxX = Math.max(p.getX() + bendOffset, maxX);
                    maxY = Math.max(p.getY() + bendOffset, maxY);
                }
            }

            // zoom the graph to fit into the screen...
            for (GraffitiInternalFrame iFrame : mf.getActiveFrames()) {

                final Session ses = iFrame.getSession();

                if (mf.getActiveEditorSession() == ses) {

                    double frameWidth = iFrame.getWidth();
                    double frameHeight = iFrame.getHeight();

                    double zoom = 1.0;

                    // determine the zoom caused by a horizontal viewport
                    // transgression
                    if (maxX > frameWidth) {
                        zoom = frameWidth / maxX;
                    }

                    // determine the zoom caused by a vertical viewport
                    // transgression
                    if (maxY > frameHeight)
                        if ((frameHeight / maxY) < zoom) {
                            zoom = frameHeight / maxY;
                        }

                    // if the zoom changed, increase the zoom because of the
                    // viewport's scroll bars, which are not considered in the
                    // frame width
                    if (zoom != 1.0) {
                        zoom *= 0.95;
                    }

                    Session activeSession = mf.getActiveSession();
                    Zoomable zoomView = activeSession.getActiveView();
                    zoomView.setZoom(zoom);
                    this.viewChanged(activeSession.getActiveView());
                }
            }
        } catch (NullPointerException nlp) {
            // no view or no graph found -> do nothing
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
