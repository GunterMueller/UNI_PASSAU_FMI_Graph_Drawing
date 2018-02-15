// =============================================================================
//
//   TikzSerializer.java
//
//   Copyright (c) 2001-2013, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.tikz;

import static java.util.Locale.ROOT;
import static org.graffiti.graphics.GraphicAttributeConstants.COORD_PATH;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.io.AbstractOutputSerializer;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * @author Andreas Gleißner
 * @version $Revision$ $Date$
 */
public class TikzSerializer extends AbstractOutputSerializer {
    private static final String HEADER = "\\input{../headers/fig_header}\n\\input{../headers/macros}\n\n\\begin{document}\n  \\begin{figure}\n    \\begin{tikzpicture}";
    
    private static final String FOOTER = "    \\end{tikzpicture}\n  \\end{figure}\n\\end{document}";
    
    private static final String NODE_FORMAT = "\\node[%s] (%s) at (%f, %f) {%s};\n";
    
    private static final String EDGE_FORMAT = "\\draw[%s] (%s) -- (%s);\n";
    
    private static final String LABEL_PATH = "label0";
    
    private static final String STYLE_PATH = "style";
    
    private double factor;
    
    private double xOffset;
    
    private double yOffset;
    
    private String nodeStyle;
    
    private String edgeStyle;
    
    private boolean isDirected;
    
    private int indentation;
    
    public TikzSerializer() {
        factor = 1.0;
        xOffset = 0.0;
        yOffset = 0.0;
        nodeStyle = "";
        edgeStyle = "";
        isDirected = false;
        indentation = 0;
    }

    @Override
    public void write(OutputStream stream, Graph g) throws IOException {
        Map<Node, String> nodesIdMap = new HashMap<>();
        int i = 0;
        for (Node node : g.getNodes()) {
            nodesIdMap.put(node, "n" + i);
            i++;
        }
        try (PrintWriter writer = new PrintWriter(stream)) {
            writer.println(HEADER);
            for (Node node : g.getNodes()) {
                String name = nodesIdMap.get(node);
                Point2D coord = ((CoordinateAttribute) node.getAttribute(COORD_PATH)).getCoordinate();
                double x = coord.getX() * factor + xOffset;
                double y = coord.getY() * -factor + yOffset;
                String label = "";
                if (node.containsAttribute("label0")) {
                    label = ((NodeLabelAttribute) node.getAttribute(LABEL_PATH)).getLabel();
                }
                String styleString = nodeStyle;
                if (node.containsAttribute(STYLE_PATH)) {
                    styleString = ((StringAttribute) node.getAttribute(STYLE_PATH)).getString();
                }
                for (i = 0; i < indentation; i++) {
                    writer.print(' ');
                }
                writer.format(ROOT, NODE_FORMAT, styleString, name, x, y, label);
            }
            for (Edge edge : g.getEdges()) {
                String sourceId = nodesIdMap.get(edge.getSource());
                String targetId = nodesIdMap.get(edge.getTarget());
                String styleString = edgeStyle;
                if (edge.containsAttribute(STYLE_PATH)) {
                    styleString = ((StringAttribute) edge.getAttribute(STYLE_PATH)).getString();
                }
                if (isDirected) {
                    if (styleString.isEmpty()) {
                        styleString = "->";
                    } else {
                        styleString = "->, " + styleString;
                    }
                }
                for (i = 0; i < indentation; i++) {
                    writer.print(' ');
                }
                writer.format(ROOT, EDGE_FORMAT, styleString, sourceId, targetId);
            }
            writer.println(FOOTER);
        }
    }

    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
        factor = ((DoubleParameter) params[0]).getDouble();
        xOffset = ((DoubleParameter) params[1]).getDouble();
        yOffset = ((DoubleParameter) params[2]).getDouble();
        nodeStyle = ((StringParameter) params[3]).getString();
        edgeStyle = ((StringParameter) params[4]).getString();
        isDirected = ((BooleanParameter) params[5]).getBoolean();
        indentation = ((IntegerParameter) params[6]).getInteger();
    }

    @Override
    protected Parameter<?>[] getAlgorithmParameters() {
        return new Parameter<?>[] {
                new DoubleParameter(0.02, "Factor", ""),
                new DoubleParameter(0.0, "X-Offset", ""),
                new DoubleParameter(0.0, "Y-Offset", ""),
                new StringParameter("", "Default Node Style", ""),
                new StringParameter("", "Default Edge Style", ""),
                new BooleanParameter(false, "Directed", ""),
                new IntegerParameter(6, "Indentation", "")
        };
    }

    @Override
    public String[] getExtensions() {
        return new String[]{ ".tex" };
    }

    @Override
    public String getName() {
        return "Quick & Dirty Tikz Exporter";
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
