package org.graffiti.plugins.tools.stylemanager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.MatteBorder;

import org.graffiti.core.Bundle;
import org.graffiti.graph.Edge;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.views.fast.java2d.Java2DFastView;

public class ElementStyleRenderer extends JLabel implements ListCellRenderer,
        GraphicAttributeConstants {

    /**
     * 
     */
    private static final long serialVersionUID = -6851295455484997603L;

    private static final int ICON_SIZE = 30;

    private static final int ICON_BORDER = 2;

    private Graph nodeGraph;

    private Node node;

    private Graph edgeGraph;

    private Edge edge;

    private Java2DFastView view;

    private ImageIcon emptyIcon;

    private Bundle resourceBundle = Bundle.getBundle(StyleManager.class);

    public ElementStyleRenderer() {
        super();

        setOpaque(true);

        emptyIcon = new ImageIcon(new BufferedImage(ICON_SIZE, ICON_SIZE,
                BufferedImage.TYPE_INT_ARGB));

        view = new Java2DFastView();
    }

    private void createNodeGraph() {
        nodeGraph = new FastGraph();
        node = nodeGraph.addNode();
        node.addAttribute(new NodeGraphicAttribute(), "");
        CoordinateAttribute coordAttr = (CoordinateAttribute) node
                .getAttribute(COORD_PATH);
        coordAttr.setX(15);
        coordAttr.setY(15);
    }

    private void createEdgeGraph() {
        edgeGraph = new FastGraph();
        Node source = edgeGraph.addNode();
        source.addAttribute(new NodeGraphicAttribute(), "");
        CoordinateAttribute coordAttr = (CoordinateAttribute) source
                .getAttribute(COORD_PATH);
        coordAttr.setX(0);
        coordAttr.setY(ICON_SIZE - 2 * ICON_BORDER);
        DimensionAttribute dimAttr = (DimensionAttribute) source
                .getAttribute(DIM_PATH);
        dimAttr.setWidth(0);
        dimAttr.setHeight(0);
        Node target = edgeGraph.addNode();
        target.addAttribute(new NodeGraphicAttribute(), "");
        coordAttr = (CoordinateAttribute) target.getAttribute(COORD_PATH);
        coordAttr.setX(ICON_SIZE - 2 * ICON_BORDER);
        coordAttr.setY(0);
        dimAttr = (DimensionAttribute) target.getAttribute(DIM_PATH);
        dimAttr.setWidth(0);
        dimAttr.setHeight(0);
        edge = edgeGraph.addEdge(source, target, true);
        edge.addAttribute(new EdgeGraphicAttribute(), "");
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        if (index == 1) {
            setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
        } else {
            setBorder(null);
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if (value == null) {
            setIcon(emptyIcon);
            setText("<html><i>" + resourceBundle.getString("renderer.no_style")
                    + "</i></html>");
            return this;
        }

        ElementStyle style = (ElementStyle) value;

        RendererInfo info = style.getRendererInfo();
        if (info == null) {
            info = createRendererInfo(style);
            style.setRendererInfo(info);
        }

        setIcon(info.getIcon());
        setText(info.getDescription());

        return this;
    }

    private String getCellText(ElementStyle style) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><b>");
        sb.append(style.getStyleName());
        sb.append("</b><br /><i><small color=gray>");
        sb.append(style.getDescription());
        sb.append("</small></i></html>");

        return sb.toString();
    }

    private RendererInfo createRendererInfo(ElementStyle elementStyle) {
        if (elementStyle instanceof NodeStyle) {
            createNodeGraph();
            elementStyle.apply(node.getAttributes());
            view.setGraph(nodeGraph);
        } else {
            createEdgeGraph();
            elementStyle.apply(edge.getAttributes());
            view.setGraph(edgeGraph);
        }
        view.rebuild();

        Graphics2D graphics = (new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB)).createGraphics();
        view.print(graphics, 1, 1);
        graphics.dispose();

        Rectangle2D rect = view.getViewport().getLogicalElementsBounds();
        int height = (int) rect.getHeight();
        int width = (int) rect.getWidth();
        double zoomFactor = Math.min(1.0,
                (double) (ICON_SIZE - 2 * ICON_BORDER)
                        / Math.max(width, height));

        AffineTransform zoom = new AffineTransform(zoomFactor, 0, 0,
                zoomFactor, ICON_SIZE / 2 - rect.getCenterX() * zoomFactor,
                ICON_SIZE / 2 - rect.getCenterY() * zoomFactor);

        BufferedImage image = new BufferedImage(ICON_SIZE, ICON_SIZE,
                BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setBackground(view.getBackgroundColor());
        graphics.transform(zoom);
        view.print(graphics, width, height);

        String description = getCellText(elementStyle);

        return new RendererInfo(new ImageIcon(image), description);
    }
}
