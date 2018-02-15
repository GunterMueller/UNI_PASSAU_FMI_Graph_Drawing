// =============================================================================
//
//   PSTricksVisitor.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.pstricks;

import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.NodeShape;

/**
 * @author lessenic
 * @version $Revision$ $Date$
 */
public class EnhancedPSTricksExporter extends PSTricksExporter implements
        ExportVisitor {

    /*
     * @see
     * org.graffiti.plugins.ios.exporters.pstricks.ExportVisitor#visit(org.graffiti
     * .plugins.ios.exporters.pstricks.ArrowData)
     */
    @Override
    public void visit(ArrowData arrowData) {
        Shape shape = arrowData.getShape();

        if (shape == null)
            return;

        EdgeData edgeData = arrowData.getEdgeData();

        stream.println("\\definecolor{LColor}{RGB}{"
                + edgeData.getFrameColor().getRed() + ","
                + edgeData.getFrameColor().getGreen() + ","
                + edgeData.getFrameColor().getBlue() + "}");
        stream.println("\\definecolor{FColor}{RGB}{"
                + edgeData.getFillColor().getRed() + ","
                + edgeData.getFillColor().getGreen() + ","
                + edgeData.getFillColor().getBlue() + "}");

        // create a custom graphic object
        stream.println("\\pscustom[style=gravistoArrowStyle" + ",linewidth="
                + edgeData.getFrameThickness() + ",linecolor=LColor"
                + ",fillcolor=FColor]{% arrow");

        PathIterator pi = shape.getPathIterator(null);

        while (pi.isDone() == false) {
            describeCurrentSegment(stream, pi);
            pi.next();
        }

        stream.println("}");
    }

    /*
     * @see
     * org.graffiti.plugins.ios.exporters.pstricks.ExportVisitor#visit(org.graffiti
     * .plugins.ios.exporters.pstricks.EdgeData)
     */
    @Override
    public void visit(EdgeData edgeData) {

        stream.println("%Edge from Node"
                + generateLabel(edgeData.getSourceNode().getNodeId())
                + " to Node"
                + generateLabel(edgeData.getTargetNode().getNodeId()));

        EdgeShape shape = (EdgeShape) edgeData.getShape();

        // define a custom line color
        stream.println("\\definecolor{LColor}{RGB}{"
                + edgeData.getFrameColor().getRed() + ","
                + edgeData.getFrameColor().getGreen() + ","
                + edgeData.getFrameColor().getBlue() + "}");

        // draw edge
        stream.print("\\pscustom[style=gravistoEdgeStyle" + ",linewidth="
                + edgeData.getFrameThickness() + ",linecolor=LColor");

        if (edgeData.getLineMode() != null) {
            float[] dash = edgeData.getLineMode();
            stream.print(",linestyle=dashed" + ",dash=");

            for (int i = 0; i < dash.length; i++) {
                stream.print(dash[i]);

                if (i < dash.length - 1) {
                    stream.print(" ");
                }
            }
        }

        stream.println("]{%");

        PathIterator pi = shape.getPathIterator(null);

        while (pi.isDone() == false) {
            describeCurrentSegment(stream, pi);
            pi.next();
        }

        stream.println("}");

    }

    /*
     * @see
     * org.graffiti.plugins.ios.exporters.pstricks.ExportVisitor#visit(org.graffiti
     * .plugins.ios.exporters.pstricks.GraphData)
     */
    @Override
    public void visit(GraphData graphData) {

        double xsize = graphData.getBoundingBox().getMaxX()
                - graphData.getBoundingBox().getMinX();
        // double ysize = graphData.getBoundingBox().getMaxY()
        // - graphData.getBoundingBox().getMinY();

        DecimalFormat formatter = new DecimalFormat("0.#####",
                new DecimalFormatSymbols(Locale.US));
        stream.println("\\psset{unit="
                + formatter.format(round(1.0 / Math.ceil(xsize), 10))
                + "\\textwidth}");
        stream.print("\\psset{yunit=-1}\n" + "\\begin{pspicture}");

        // upper left corner in the PSTricks image
        stream.print("("
                + round(graphData.getBoundingBox().getMinX(),
                        COORDINATES_ACCURACY)
                + ","
                + round(graphData.getBoundingBox().getMinY(),
                        COORDINATES_ACCURACY) + ")");

        // lower right corner in the PSTricks image
        stream.println("("
                + round(graphData.getBoundingBox().getMaxX(),
                        COORDINATES_ACCURACY)
                + ","
                + round(graphData.getBoundingBox().getMaxY(),
                        COORDINATES_ACCURACY) + ")");

        // define a PSTricks style with default values
        stream
                .println("\\newpsstyle{gravistoStyle}{fillstyle=solid,shadow=false}");
        stream
                .println("\\newpsstyle{gravistoArrowStyle}{linewidth=0.1,fillstyle=solid,fillcolor=black}");
        stream
                .println("\\newpsstyle{gravistoGridStyle}{linewidth=0.1,linecolor=gray}");
        stream
                .println("\\newpsstyle{gravistoEdgeStyle}{linewidth=0.1,fillstyle=none}");

        stream.println();

        graphData.getGrid().accept(this);

        // Gravisto-compliance: we want the nodes to be drawn on top of the
        // edges if the z-coordinate equals. therefore, the edges are added to
        // the list before the nodes.
        List<GraphElementData> elements = new LinkedList<GraphElementData>();
        elements.addAll(graphData.getEdges());
        elements.addAll(graphData.getNodes());
        Collections.sort(elements);

        for (GraphElementData e : elements) {
            e.accept(this);
            stream.println();
        }

        // close graph
        stream.println("\\end{pspicture}");

        // finished
        stream.close();
    }

    /*
     * @see
     * org.graffiti.plugins.ios.exporters.pstricks.ExportVisitor#visit(org.graffiti
     * .plugins.ios.exporters.pstricks.GridData)
     */
    @Override
    public void visit(GridData gridData) {
        if (gridData.isEnabled()) {
            stream.println("% Grid");
        }

        for (Shape shape : gridData.getShapes()) {
            stream.println("\\pscustom[style=gravistoGridStyle]{");

            PathIterator pi = shape.getPathIterator(null);

            while (pi.isDone() == false) {
                describeCurrentSegment(stream, pi);
                pi.next();
            }

            stream.println("}");
        }
    }

    /*
     * @see
     * org.graffiti.plugins.ios.exporters.pstricks.ExportVisitor#visit(org.graffiti
     * .plugins.ios.exporters.pstricks.LabelData)
     */
    @Override
    public void visit(LabelData labelData) {
        stream.println("\\rput("
                + round(labelData.getX(), COORDINATES_ACCURACY) + ","
                + round(labelData.getY(), COORDINATES_ACCURACY) + "){"
                + labelData.getLabel() + "}");
    }

    /*
     * @see
     * org.graffiti.plugins.ios.exporters.pstricks.ExportVisitor#visit(org.graffiti
     * .plugins.ios.exporters.pstricks.NodeData)
     */
    @Override
    public void visit(NodeData nodeData) {

        stream.println("% Node " + generateLabel(nodeData.getNodeId()));
        stream.println("\\definecolor{LColor}{RGB}{"
                + nodeData.getFrameColor().getRed() + ","
                + nodeData.getFrameColor().getGreen() + ","
                + nodeData.getFrameColor().getBlue() + "}");
        stream.println("\\definecolor{FColor}{RGB}{"
                + nodeData.getFillColor().getRed() + ","
                + nodeData.getFillColor().getGreen() + ","
                + nodeData.getFillColor().getBlue() + "}");

        // draw node
        if (nodeData.getFrameColor().getTransparency() == Transparency.BITMASK) {
            // make node invisible
            stream.print("\\phantom{");
        }

        stream.print("\\pscustom[style=gravistoStyle" + ",linewidth="
                + nodeData.getFrameThickness() + ",linecolor=LColor"
                + ",fillcolor=FColor");

        if (nodeData.getFillColor().getTransparency() == Transparency.BITMASK) {
            stream.print(",fillstyle=none");
        }

        // TODO: works only with latest pstricks versions
        // \phantom is used instead so far
        //
        // if (nodeData.getFrameColor().getTransparency() == Color.BITMASK)
        // stream.print(",strokeopacity=0");
        //
        // if (nodeData.getFillColor().getTransparency() == Color.BITMASK)
        // stream.print(",opacity=0");

        if (nodeData.getLineMode() != null) {
            float[] dash = nodeData.getLineMode();
            stream.print(",linestyle=dashed" + ",dash=");

            for (int i = 1; i < dash.length; i++) {
                stream.print(dash[i]);

                if (i < dash.length) {
                    stream.print(" ");
                }
            }
        }

        stream.println("]{%");

        NodeShape shape = (NodeShape) nodeData.getShape();

        Rectangle2D rectangle = shape.getRealBounds2D();
        PathIterator pi = shape.getPathIterator(AffineTransform
                .getTranslateInstance(rectangle.getX(), rectangle.getY()));

        while (pi.isDone() == false) {
            describeCurrentSegment(stream, pi);
            pi.next();
        }

        stream.println("}");

        if (nodeData.getFrameColor().getTransparency() == Transparency.BITMASK) {
            // make node invisible
            stream.print("}");
        }

        // labels
        List<LabelData> labels = nodeData.getLabels();

        for (LabelData label : labels) {
            label.accept(this);
        }
    }

    /*
     * @see
     * org.graffiti.plugins.ios.exporters.pstricks.PSTricksSerializer#write(
     * java.io.OutputStream, org.graffiti.graph.Graph)
     */
    @Override
    public void write(OutputStream out, Graph g) throws IOException {
        super.write(out, g);
        new GraphData(g).accept(this);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
