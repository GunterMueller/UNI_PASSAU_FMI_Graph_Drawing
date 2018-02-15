package org.graffiti.plugins.ios.importers.gdc;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graph.OptAdjListGraph;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.io.AbstractInputSerializer;
import org.graffiti.plugin.io.ParserException;

/**
 * This class provides a reader for graphs in gml format.
 * 
 * @see org.graffiti.plugin.io.AbstractIOSerializer
 */
public class GDCReader extends AbstractInputSerializer {
    private final double NODESIZE = 10d;

    /** The supported extensions. */
    private String[] extensions = { ".gdc" };

    /**
     * Constructs a new <code>GDCReader</code>
     */
    public GDCReader() {
    }

    /**
     * Returns the extensions supported by this reader.
     * 
     * @return the extensions supported by this reader.
     */
    public String[] getExtensions() {
        return this.extensions;
    }

    /**
     * Reads in a graph from the given input stream. <code>GraphElements</code>
     * read are <b>cloned</b> when added to the graph. Consider using the
     * <code>read(InputStream)</code> method when you start with an empty graph.
     * 
     * @param in
     *            the <code>InputStream</code> from which to read in the graph.
     * @param g
     *            the graph in which to read in the file.
     * 
     * @exception ParserException
     *                if an error occurs while parsing the .gml file.
     */
    @Override
    public void read(InputStream in, Graph g) throws ParserException {
        Graph graph;
        try {
            graph = read(in);
        } catch (IOException e) {
            throw new ParserException(e.getMessage());
        }
        g.addGraph(graph);
    }

    /**
     * Reads in a graph from the given input stream. This implementation returns
     * an instance of <code>OptAdjListGraph</code> (that's what the parser
     * returns).
     * 
     * @param in
     *            The input stream to read the graph from.
     * 
     * @return The newly read graph (an instance of <code>OptAdjListGraph</code>
     *         ).
     * 
     * @exception IOException
     *                If an IO error occurs.
     * @throws ParserException
     *             DOCUMENT ME!
     */
    @Override
    public Graph read(InputStream in) throws IOException {
        Graph graph = new OptAdjListGraph();
        try {
            boolean b_nrNodes = true;
            boolean b_nodePos = false;
            boolean b_edges = false;

            int nrNodes = 0;
            int nodeCnt = 0;

            Map<Integer, Node> nidMap = new HashMap<Integer, Node>();

            StringBuffer file = null;
            int buf = -2;
            while (in.available() > 0 && buf != -3) {
                file = new StringBuffer();
                while (in.available() > 0) {
                    if (buf == -2) {
                        buf = in.read();
                    }
                    if (buf < 0 || buf == 10 || buf == 13) {
                        break;
                    }
                    file.append((char) buf);
                    buf = -2;
                }

                // now "file" contains one line

                // TODO more sophisticated treatment of comments
                if (file.charAt(0) != '#') {

                    // System.out.println("file = " + file);
                    if (b_nrNodes) {
                        // read number of nodes
                        nrNodes = new Integer(file.toString()).intValue();
                        System.out.println("nrNodes: " + nrNodes);
                        b_nrNodes = false;
                        b_nodePos = true;

                    } else if (b_nodePos) {
                        // read nodes with coordinates
                        NodeGraphicAttribute nga = new NodeGraphicAttribute();
                        Node node = graph.addNode();
                        node.addAttribute(nga, "");
                        CoordinateAttribute coord = (CoordinateAttribute) node
                                .getAttribute(GraphicAttributeConstants.GRAPHICS
                                        + Attribute.SEPARATOR
                                        + GraphicAttributeConstants.COORDINATE);
                        String xcoord = file.substring(0, file.indexOf(" "));
                        String ycoord = file.substring(file.indexOf(" ") + 1);
                        coord.setX(new Double(xcoord).doubleValue());
                        coord.setY(new Double(ycoord).doubleValue());

                        ((DimensionAttribute) (node
                                .getAttribute(GraphicAttributeConstants.GRAPHICS
                                        + Attribute.SEPARATOR
                                        + GraphicAttributeConstants.DIMENSION)))
                                .setDimension(NODESIZE, NODESIZE);

                        nidMap.put(new Integer(nodeCnt), node);
                        nodeCnt++;
                        if (nodeCnt >= nrNodes) {
                            System.out.println("found last node");
                            b_nodePos = false;
                            b_edges = true;
                        }
                        System.out.println("node(" + new Integer(nodeCnt - 1)
                                + "): " + new Double(xcoord).doubleValue()
                                + " / " + new Double(ycoord).doubleValue()
                                + " [" + file + "]");

                    } else if (b_edges) {
                        // create edges
                        String sid = file.substring(0, file.indexOf(" "));
                        String tid = file.substring(file.indexOf(" ") + 1);
                        Node source = nidMap.get(sid);
                        Node target = nidMap.get(tid);
                        graph.addEdge(source, target, false);
                        System.out.println("edge: " + new Integer(sid) + "->"
                                + new Integer(tid));
                    } else {
                        // ignore trailing stuff
                    }

                } else {
                    System.out.println("comment: " + file);
                }
                if (in.available() > 0) {
                    buf = in.read();
                    if (buf == 10 || buf == 13) {
                        buf = -2;
                    }
                } else {
                    buf = -3;
                }
            }
            System.out.println("finished");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ParserException(e.getMessage());
        }

        return graph;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
