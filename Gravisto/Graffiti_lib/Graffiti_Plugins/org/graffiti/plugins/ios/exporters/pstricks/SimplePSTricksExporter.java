// =============================================================================
//
//   SimplePSTricksSerializer.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.pstricks;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugins.views.fast.java2d.Java2DFastView;

/**
 * A rather simple PSTricks exporter that keeps the generated code as slim and
 * simple as possible to keep it flexible and facilitate modifying it
 * afterwards.<br/>
 * For this reason, it does not take account of polynomial edges (bends) and
 * specific style definitions. If you look for an exporter, that tries to fit
 * the Gravisto style as precise as possible, you should look for
 * <code>GravistoPSTricksSerializer</code>
 * 
 * @author lessenic
 * @version $Revision$ $Date$
 */
public class SimplePSTricksExporter extends PSTricksExporter {
    /**
     * current node id this value is used to generate the node labels
     */
    private int nodeCount;

    /**
     * 
     */
    private Map<Node, String> nodes;

    private int bendsCount;

    private Rectangle2D boundingBox;

    /**
     * Defines one node in pstricks format.
     * 
     * @param node
     *            node to be defined
     */
    private void defineNode(Node node) {
        String nodeID = generateLabel(nodeCount++ - 1);
        stream.print("\\newcommand{\\draw" + nodeID + "}{");

        // shape
        if (node.getString("graphics.shape").equals(
                "org.graffiti.plugins.views.defaults.CircleNodeShape")) {
            stream.print("\\pscirclebox[style=gravistoNodeStyle]{");
        } else {
            stream.print("\\psframebox[style=gravistoNodeStyle]{");
        }

        // label
        try {
            CollectionAttribute attribute = node.getAttributes();

            Collection<Attribute> attrs = attribute.getCollection().values();

            for (Attribute nextAttribute : attrs) {
                if (nextAttribute instanceof NodeLabelAttribute) {
                    stream.print(((NodeLabelAttribute) nextAttribute)
                            .getLabel());
                }
            }

        } catch (AttributeNotFoundException e) {
            // do nothing
        }

        stream.println("}}");
        nodes.put(node, nodeID);
    }

    /**
     * Initializes the pspicture environment and sets some global styles.
     * 
     * @param g
     *            Graph
     */
    protected void initializeEnvironment(Graph g) {
        // create a dummy 1x1 image to get the bounding box from the view
        Java2DFastView view = new Java2DFastView();
        view.setGraph(g);
        Graphics2D graphics = (new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_RGB)).createGraphics();
        view.print(graphics, 1, 1);
        graphics.dispose();

        boundingBox = view.getViewport().getLogicalElementsBounds();

        double xsize = boundingBox.getMaxX() - boundingBox.getMinX();

        DecimalFormat formatter = new DecimalFormat("0.#####",
                new DecimalFormatSymbols(Locale.US));
        stream.println("\\psset{unit="
                + formatter.format(round(1.0 / Math.ceil(xsize), 10))
                + "\\textwidth}");
        stream.print("\\psset{yunit=-1}\n" + "\\begin{pspicture}");

        // upper left corner in the pstricks image
        stream.print("(" + round(boundingBox.getMinX(), COORDINATES_ACCURACY)
                + "," + round(boundingBox.getMinY(), COORDINATES_ACCURACY)
                + ")");

        // lower right corner in the pstricks image
        stream.println("(" + round(boundingBox.getMaxX(), COORDINATES_ACCURACY)
                + "," + round(boundingBox.getMaxY(), COORDINATES_ACCURACY)
                + ")");

        // set some environment options
        stream.println("\\SpecialCoor");
        stream.println();
        stream.println("%");
        stream.println("% Default Styles");
        stream.println("%");
        stream
                .println("\\newpsstyle{gravistoNodeStyle}{linewidth=1.0,fillstyle=solid,fillcolor=white,shadow=false}");
        stream
                .println("\\newpsstyle{gravistoGridStyle}{linewidth=0.1,linecolor=gray}");
        stream.println("\\newpsstyle{gravistoEdgeStyle}{linewidth="
                + DEFAULT_EDGE_LINEWIDTH + "}");
        stream.println("\\newpsstyle{gravistoArrowStyle}{linewidth="
                + DEFAULT_EDGE_LINEWIDTH + ",fillstyle=solid,fillcolor=black}");
    }

    /*
     * @see org.graffiti.plugin.io.OutputSerializer#write(java.io.OutputStream,
     * org.graffiti.graph.Graph)
     */
    @Override
    public void write(OutputStream out, Graph g) throws IOException {
        super.write(out, g);

        nodes = new HashMap<Node, String>();
        nodeCount = 1;
        bendsCount = 1;

        initializeEnvironment(g);

        // Grid
        writeGrid(g);

        // define nodes
        stream.println();
        stream.println("%");
        stream.println("% node declarations");
        stream.println("%");
        for (Iterator<Node> i = g.getNodesIterator(); i.hasNext();) {
            defineNode(i.next());
        }

        // write nodes
        stream.println();
        stream.println("%");
        stream.println("% draw nodes to coordinates");
        stream.println("%");
        for (Iterator<Node> i = g.getNodesIterator(); i.hasNext();) {
            writeNode(i.next());
        }

        // write edges
        stream.println();
        stream.println("%");
        stream.println("% draw edges");
        stream.println("%");
        for (Iterator<Edge> i = g.getEdgesIterator(); i.hasNext();) {
            writeEdge(i.next());
        }

        // draw nodes again to overlap edges
        stream.println();
        stream.println("%");
        stream.println("% draw Nodes again to overlap Edges");
        stream.println("%");
        for (Iterator<Node> i = g.getNodesIterator(); i.hasNext();) {
            writeNodeByLabel(i.next());
        }

        // close graph
        stream.println("\\end{pspicture}");

        // finished
        out.close();
    }

    /**
     * Writes the bends of an edge in pstricks format.
     * 
     * @param edge
     *            The edge whose bends should be written
     */
    private void writeBends(Edge edge) {
        LinkedHashMapAttribute bends = (LinkedHashMapAttribute) edge
                .getAttribute("graphics.bends");
        for (Attribute key : bends.getCollection().values()) {
            CoordinateAttribute a = (CoordinateAttribute) key;

            // draw bend as node
            stream.println("\\rput(" + a.getX() + "," + a.getY()
                    + "){\\rnode{bend" + bendsCount + "}{}}");
            bendsCount++;
        }
    }

    /**
     * Writes one edge in pstricks format.
     * 
     * @param edge
     *            The edge that is written
     */
    private void writeEdge(Edge edge) {

        int position = bendsCount;
        boolean hasBends = edge.getString("graphics.shape").equals(
                "org.graffiti.plugins.views.defaults.PolyLineEdgeShape")
                || edge
                        .getString("graphics.shape")
                        .equals(
                                "org.graffiti.plugins.views.defaults.SmoothLineEdgeShape");

        stream.println();
        stream.println("% edge from node " + nodes.get(edge.getSource())
                + " to node " + nodes.get(edge.getTarget()));

        if (hasBends) {
            writeBends(edge);
        }

        stream.print("\\ncline[style=gravistoEdgeStyle]");

        if (edge.isDirected()) {
            // draw arrows if needed

            boolean drawTail = edge.getString("graphics.arrowtail").equals(
                    "org.graffiti.plugins.views.defaults.StandardArrowShape");
            boolean drawHead = !hasBends
                    && edge
                            .getString("graphics.arrowhead")
                            .equals(
                                    "org.graffiti.plugins.views.defaults.StandardArrowShape");

            if (drawTail || drawHead) {
                stream.print("{");

                if (drawTail) {
                    stream.print("<");
                }

                stream.print("-");

                if (drawHead) {
                    stream.print(">");
                }

                stream.print("}");
            }
        }

        // source node
        stream.print("{" + nodes.get(edge.getSource()) + "}");

        // add bends if edge has multiple segments
        if (hasBends) {
            while (position < bendsCount) {
                stream.println("{bend" + position + "}");
                stream.print("\\ncline[style=gravistoEdgeStyle]");

                if (position == bendsCount - 1
                        && edge.isDirected()
                        && edge
                                .getString("graphics.arrowhead")
                                .equals(
                                        "org.graffiti.plugins.views.defaults.StandardArrowShape")) {
                    // last segment
                    stream.print("{->}");
                }

                stream.print("{bend" + position + "}");
                position++;
            }
        }

        // target node
        stream.println("{" + nodes.get(edge.getTarget()) + "}");
    }

    /**
     * Writes the grid of a graph in pstricks format.
     * 
     * @param g
     *            The graph whose grid should be written
     */
    private void writeGrid(Graph g) {

        Grid grid = ((GridAttribute) g.getAttribute("graphics.grid")).getGrid();

        if (!grid.getShapes(boundingBox).isEmpty()) {
            stream.println("% Grid");
        }

        for (Shape shape : grid.getShapes(boundingBox)) {
            stream.println("\\pscustom[style=gravistoGridStyle]{");

            PathIterator pi = shape.getPathIterator(null);

            while (pi.isDone() == false) {
                describeCurrentSegment(stream, pi);
                pi.next();
            }

            stream.println("}");
        }
    }

    /**
     * Writes one node in pstricks format. The node is put to the coordinates
     * specified in the <code>node</code> class.
     * 
     * @param node
     *            The node that is written
     */
    private void writeNode(Node node) {
        String nodeID = nodes.get(node);
        stream.print("\\rput("
                + round(node.getDouble("graphics.coordinate.x"),
                        COORDINATES_ACCURACY)
                + ","
                + round(node.getDouble("graphics.coordinate.y"),
                        COORDINATES_ACCURACY) + "){");

        stream.println("\\rnode{" + nodeID + "}{\\draw" + nodeID + "}}");
    }

    private void writeNodeByLabel(Node node) {
        String nodeID = nodes.get(node);
        stream.println("\\rput(" + nodeID + "){\\draw" + nodeID + "}");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
