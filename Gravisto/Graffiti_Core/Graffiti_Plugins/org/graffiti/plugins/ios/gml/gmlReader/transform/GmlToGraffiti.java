// =============================================================================
//
//   GmlToGraffiti.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlToGraffiti.java 5780 2010-05-10 20:33:09Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.ios.gml.gmlReader.gml.Gml;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlEdge;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlGraph;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlInt;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlKey;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlList;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlNode;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlValuable;
import org.graffiti.plugins.ios.gml.gmlReader.gml.GmlValue;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Transforms a graph represented in the GML format into the representation used
 * within graffiti.
 * 
 * @author ruediger
 */
public class GmlToGraffiti {

    /** The logger for development purposes. */
    private static final Logger logger = Logger.getLogger(GmlToGraffiti.class
            .getName());

    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The map mapping the GML ids to nodes. */
    protected Map<Integer, Node> nodeMap;

    /** The edge transformer for creating edges. */
    private EdgeTransformer edgeTransformer;

    /** The <code>Gml</code> object to be transformed. */
    private Gml gml;

    /** The graph to be created or modified. */
    private Graph graph;

    /** The node transformer for creating nodes. */
    private NodeTransformer nodeTransformer;

    /** Indicates whether the graph is directed or not. */
    private boolean directed = false;

    /**
     * Constructs a new <code>GmlToGraffiti</code>.
     * 
     * @param gml
     *            the GML graph to be transformed into a graffiti graph.
     * @param graph
     *            the graph into which to add the GML declarations.
     * 
     * @throws GmlToGraffitiException
     *             if the transformation fails.
     */
    public GmlToGraffiti(Gml gml, Graph graph) throws GmlToGraffitiException {
        assert gml != null;
        this.gml = gml;

        assert graph != null;
        this.graph = graph;

        this.nodeTransformer = new NodeTransformer(gml.getNodeStyle());
        this.nodeMap = new HashMap<Integer, Node>();
        this.edgeTransformer = new EdgeTransformer(gml.getEdgeStyle());
    }

    /**
     * Returns <code>true</code> if there were no errors detected during the
     * parsing phase, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if there were no errors detected during the
     *         parsing phase, <code>false</code> otherwise.
     */
    public boolean hasParseErrors() {
        return !this.gml.getErrors().isEmpty();
    }

    /**
     * Runs the transformation from Gml to Graph. The Gml instance is
     * subsequently transformed into an instance of type Graph. The created
     * graph will then be returned.
     * 
     * @return the created graph.
     * 
     * @throws GmlToGraffitiException
     *             if a fatal error is detected during the transformation.
     */
    public Graph transform() throws GmlToGraffitiException {
        logger.info("start transformation to graffiti: creating a new graph.");

        // process the declarations assiciated to the graph
        logger.info("evaluating GmlList.");
        assert this.gml != null;
        assert this.graph != null;

        GmlList l = gml.getList();

        if (l != null) {
            processList(l, graph);
        }

        // return the created graph
        return graph;
    }

    /**
     * Creates a new edge out of a GML edge declaration.
     * 
     * @param g
     *            the graph in which to add the edge.
     * @param e
     *            the edge to be processed for adding.
     * 
     * @throws GmlToGraffitiException
     *             if the transformation fails.
     */
    private void createEdge(Graph g, GmlEdge e) throws GmlToGraffitiException {
        assert g != null;
        assert e != null;

        GmlValue gv = e.getValue();

        // make sure we get a GmlList
        assert !(gv.isInt() || gv.isReal() || gv.isString());

        GmlList atts = (GmlList) gv.getValue();

        // find the source and the target id attributes which unfortunately
        // are not necessarily the first attributes in the list
        GmlList restore = null;
        Integer sourceId = null;
        Integer targetId = null;

        while ((sourceId == null) || (targetId == null)) {
            GmlValuable head = atts.getHead();
            assert !(head.isGraph() || head.isNode() || head.isEdge());

            GmlKey key = (GmlKey) head;
            String id = key.getId();

            if (id.equalsIgnoreCase("source")) {
                // take the id of the source node
                GmlValue v = key.getValue();
                assert v.isInt();

                GmlInt sId = (GmlInt) v;
                sourceId = (Integer) sId.getValue();
                assert sourceId != null;
                logger.info("source id " + sourceId + ".");
            } else if (id.equalsIgnoreCase("target")) {
                // take the id of the target node
                GmlValue v = key.getValue();
                assert v.isInt();

                GmlInt sId = (GmlInt) v;
                targetId = (Integer) sId.getValue();
                assert targetId != null;
                logger.info("target id " + targetId + ".");
            } else {
                restore = new GmlList(head.getLine(), head, restore);
            }

            atts = atts.getTail();

            // check for end of list
            if (atts == null) {
                break;
            }
        }

        if ((sourceId == null) || (targetId == null))
            throw new GmlToGraffitiException("Missing source or target (line "
                    + e.getLine() + ").");

        // restore the attribute list
        while (restore != null) {
            GmlValuable head = restore.getHead();
            atts = new GmlList(head.getLine(), head, atts);
            restore = restore.getTail();
        }

        // get the corresponding nodes from the node map
        Node source = nodeMap.get(sourceId);
        assert source != null;

        Node target = nodeMap.get(targetId);
        assert target != null;

        // add the corresponding edge to the graph
        edgeTransformer.createEdge(g, source, target, e, directed);
    }

    /**
     * Finds and returns the id of a gml node.
     * 
     * @param n
     *            the node to search for the id.
     * 
     * @return the id of the Node.
     * 
     * @throws GmlToGraffitiException
     *             if no id was detected.
     */
    private Integer findId(GmlNode n) throws GmlToGraffitiException {
        // make sure we get a GmlList
        GmlValue gv = n.getValue();
        assert !(gv.isInt() || gv.isReal() || gv.isString());

        GmlList atts = (GmlList) gv.getValue();
        boolean idFound = false;
        Integer nodeId = null;
        GmlList restore = null;

        // find the id of the node
        while (!idFound && (atts != null)) {
            GmlValuable head = atts.getHead();
            assert !(head.isGraph() || head.isNode() || head.isEdge());

            GmlKey key = (GmlKey) head;
            String id = key.getId();

            if (id.equals("id")) {
                GmlValue v = key.getValue();

                if (v.isInt()) {
                    idFound = true;

                    GmlInt idVal = (GmlInt) v;
                    nodeId = (Integer) idVal.getValue();
                    logger.info("id detected with value " + nodeId);
                } else
                    throw new GmlToGraffitiException("id is not an integer "
                            + "(line " + v.getLine() + ").");
            } else {
                restore = new GmlList(-1, head, restore);
            }

            atts = atts.getTail();
        }

        // restore the attribute list
        while (restore != null) {
            GmlValuable head = restore.getHead();
            atts = new GmlList(head.getLine(), head, atts);
            restore = restore.getTail();
        }

        return nodeId;
    }

    /**
     * Processes l and invokes the creation of the corresponding nodes, edges
     * and attributes.
     * 
     * @param l
     *            the list to process.
     * @param g
     *            the graph to be built.
     * 
     * @throws GmlToGraffitiException
     *             if a fatal error occurs during the transformation.
     */
    private void processList(GmlList l, Graph g) throws GmlToGraffitiException {
        // go through the list and add nodes, edges, attributes
        logger.info("iterating list");

        while (l != null) {
            // take a valuable and process it
            GmlValuable gv = l.getHead();
            assert gv != null;

            if (gv.isNode()) {
                // create a node
                logger.info("creating node.");

                GmlNode n = (GmlNode) gv;
                Integer nodeId = findId(n);
                Node node = nodeTransformer.createNode(g, n);

                // put the node and the corresponding id into the map
                if (nodeId != null) {
                    nodeMap.put(nodeId, node);
                } else
                    throw new GmlToGraffitiException("no node id declared "
                            + "(line " + n.getLine() + ").");
            } else if (gv.isEdge()) {
                // create an edge
                logger.info("creating edge.");

                GmlEdge e = (GmlEdge) gv;
                createEdge(g, e);
            } else if (gv.isGraph()) {
                logger.info("graph found");

                GmlGraph gg = (GmlGraph) gv;
                GmlList list = (GmlList) gg.getValue();

                // addAttributableAttributes(g, list, ".");
                processList(list, g);
            } else {
                // top level graph attributes; make a case distinction to
                // create attributes
                GmlKey key = (GmlKey) gv;
                String id = key.getId();
                GmlValue val = key.getValue();
                logger.info("attribute found with id " + id + " and value \""
                        + val.getValue() + "\".");

                if (id.equals("directed")) {
                    // we are expecting value 0 or 1
                    if (val.isInt()) {
                        int value = ((Integer) val.getValue()).intValue();
                        assert (value == 0) || (value == 1);

                        if (value == 1) {
                            directed = true;
                        } else {
                            directed = false;
                        }
                    } else {
                        logger.warning("directed should have a value of 0 or "
                                + " 1 at line " + val.getLine()
                                + " - using undirected.");
                        directed = false;
                    }
                } else if (id.equals("")) {
                    // add other cases
                    assert false;
                } else {
                    // add a non-default attribute
                    String path = "." + id;

                    if (val.isInt()) {
                        int value = ((Integer) val.getValue()).intValue();
                        g.setInteger(path, value);
                    } else if (val.isReal()) {
                        double value = ((Double) val.getValue()).doubleValue();
                        g.setDouble(path, value);
                    } else if (val.isString()) {
                        String value = (String) val.getValue();
                        g.setString(path, value);
                    } else {
                        // addAttributableAttributes(g, list, ".");
                        // assert false;
                    }
                }
            }

            // take the next element of the list and proceed
            if (l.getTail() == null) {
                l = null;
            } else {
                l = l.getTail();
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
