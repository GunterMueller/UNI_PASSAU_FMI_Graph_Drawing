// =============================================================================
//
//   InterpreterVisitor.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: InterpreterVisitor.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.dot.parser;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;

/**
 * @author Andreas
 * @version $Revision: 5766 $ $Date: 2006/08/03 15:24:19
 * 
 *          N.B.: This class does not support nested subgraphs, but as there is
 *          no subgraph concept in Gravisto, there should not be a problem. If
 *          this gets in the way of a future project - which I doubt very much -
 *          please modify the method visit(ASTsubgraph, Object).
 */
public class InterpreterVisitor implements DOTParserVisitor {

    HashMap<String, Node> nodes = new HashMap<String, Node>();

    GraphElement currentGraphElement = null;

    Node currentNode = null;

    Node currentEdge = null;

    Graph graph = null;

    int subgraphIdCounter = 0;

    boolean subgraphAttributeAdded = false;

    HashMap<String, LinkedList<Node>> subgraphs = new HashMap<String, LinkedList<Node>>();

    HashMap<Node, HashMapAttribute> subgraphAttributesForNodes = new HashMap<Node, HashMapAttribute>();

    String currentSubgraphId = null;

    static String DEFAULT_FONT_NAME = "Courier New";

    Font font;

    FontRenderContext frc;

    public InterpreterVisitor() {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        for (Font font : fonts) {
            if (font.getName().equalsIgnoreCase(DEFAULT_FONT_NAME)) {
                this.font = font;
                break;
            }
        }
        if (font == null) {
            font = new Font("", Font.PLAIN, 12);
        }
        font = font.deriveFont(12.5f);
        frc = new FontRenderContext(null, true, true);
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.SimpleNode,
     * java.lang.Object)
     */
    public Object visit(SimpleNode node, Object data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Not yet implemented: SimpleNode");
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTparse, java.lang.Object)
     */
    public Object visit(ASTparse node, Object data) {
        this.graph = (Graph) data;

        return node.childrenAccept(this, null);
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTgraph, java.lang.Object)
     */
    public Object visit(ASTgraph node, Object data) {
        Token currentToken = node.first_token;

        if (currentToken.image.equals("strict")) {
            currentToken = currentToken.next;
            // TODO: Deal with strict. What does it mean anyway.
        }

        // Deal with directed/undirected...
        if (currentToken.image.equals("graph")) {
            this.graph.setDirected(false);
        } else if (currentToken.image.equals("digraph")) {
            this.graph.setDirected(true);
        }

        // Now the name (id) of the graph...
        currentToken = currentToken.next;
        this.graph.addString("", "id", currentToken.image);

        // The stmt_list has to be dealt with...
        node.childrenAccept(this, data);

        return data;
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTstmt_list,
     * java.lang.Object)
     */
    public Object visit(ASTstmt_list node, Object data) {
        return node.childrenAccept(this, data);
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTStatement,
     * java.lang.Object)
     */
    public Object visit(ASTStatement node, Object data) {
        return node.childrenAccept(this, data);
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTideq_stmt,
     * java.lang.Object)
     */
    public Object visit(ASTideq_stmt node, Object data) {
        // TODO: Dot uses this to set attributes. We do not need this for now.
        return null;
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTattr_stmt,
     * java.lang.Object)
     */
    public Object visit(ASTattr_stmt node, Object data) {
        // TODO: DOT uses this to set defaults for graph, node or edge. We do
        // not need this for now.
        return null;
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTnode_stmt,
     * java.lang.Object)
     */
    public Object visit(ASTnode_stmt node, Object data) {

        int numOfChildren = node.jjtGetNumChildren();
        ASTnode_id idNode = (ASTnode_id) node.jjtGetChild(0);
        Node newNode = (Node) idNode.jjtAccept(this, null);

        if (numOfChildren > 1) {
            ASTattr_list attrListNode = (ASTattr_list) node.jjtGetChild(1);
            attrListNode.jjtAccept(this, newNode);
        }

        return null;

    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTnode_id,
     * java.lang.Object)
     */
    public Object visit(ASTnode_id node, Object data) {

        // remove leading and trailing quotes if present...
        String idString = removeQuotes(node.first_token.image);

        // get the node...
        Node newNode = this.nodes.get(idString);

        // create if not present...
        if (newNode == null) {
            newNode = this.graph.addNode();
            this.nodes.put(idString, newNode);
            newNode.addString("", "id", idString);

            if (this.currentSubgraphId != null) {
                this.subgraphs.get(this.currentSubgraphId).addLast(newNode);

                HashMapAttribute currentNodeSubgraphAttribute = this.subgraphAttributesForNodes
                        .get(newNode);

                if (currentNodeSubgraphAttribute == null) {
                    currentNodeSubgraphAttribute = new HashMapAttribute(
                            "subgraphs");
                    newNode.addAttribute(currentNodeSubgraphAttribute, "");
                    this.subgraphAttributesForNodes.put(newNode,
                            currentNodeSubgraphAttribute);
                }

                currentNodeSubgraphAttribute.add(new BooleanAttribute(
                        this.currentSubgraphId, true));
            }

        }

        return newNode;

    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTedge_stmt,
     * java.lang.Object)
     */
    public Object visit(ASTedge_stmt node, Object data) {

        SimpleNode fromIdNode = (SimpleNode) node.jjtGetChild(0);
        LinkedList<Node> fromNodes = null;

        if (fromIdNode instanceof ASTsubgraph) { // it could be a whole subgraph
                                                 // (i.e. we take a group of
                                                 // nodes)
            String subgraphId = (String) fromIdNode.jjtAccept(this, null);
            fromNodes = this.subgraphs.get(subgraphId);
        } else { // it must be an ordinary node then...
            Node fromNode = (Node) fromIdNode.jjtAccept(this, null);
            fromNodes = new LinkedList<Node>();
            fromNodes.addFirst(fromNode);
        }

        ASTedgeRHS edgeRHS = (ASTedgeRHS) node.jjtGetChild(1);

        @SuppressWarnings("unchecked")
        LinkedList<Edge> newEdges = (LinkedList<Edge>) edgeRHS.jjtAccept(this,
                fromNodes);

        int numOfChildren = node.jjtGetNumChildren();

        if (numOfChildren > 2) { // some attributes to set...
            node.childrenAccept(this, newEdges);
        }

        return null;

    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTsubgraph,
     * java.lang.Object)
     */
    public Object visit(ASTsubgraph node, Object data) {
        Token currentToken = node.first_token;
        // check if an id is given

        String subgraphId = null;
        boolean autogeneratedId = false;

        if (currentToken.image.equals("subgraph")) {
            currentToken = currentToken.next;
            subgraphId = currentToken.image;
        } else {
            subgraphId = "subgraph_" + this.subgraphIdCounter;
            this.subgraphIdCounter++;
            autogeneratedId = true;
        }

        this.subgraphs.put(subgraphId, new LinkedList<Node>());
        HashMapAttribute subgraphAttributes = null;

        if (!this.subgraphAttributeAdded) {
            subgraphAttributes = new HashMapAttribute("subgraphs");
            this.graph.addAttribute(subgraphAttributes, "");
            this.subgraphAttributeAdded = true;
        } else {
            subgraphAttributes = (HashMapAttribute) this.graph
                    .getAttribute("subgraphs");
        }

        HashMapAttribute currentSubgraphAttribute = new HashMapAttribute(
                subgraphId);
        currentSubgraphAttribute.add(new BooleanAttribute("AutogeneratedId",
                autogeneratedId));

        subgraphAttributes.add(currentSubgraphAttribute);

        String oldSubgraphId = this.currentSubgraphId;
        this.currentSubgraphId = subgraphId;

        node.childrenAccept(this, null);

        this.currentSubgraphId = oldSubgraphId;

        return subgraphId;
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTedgeRHS,
     * java.lang.Object)
     */
    public Object visit(ASTedgeRHS node, Object data) {
        @SuppressWarnings("unchecked")
        LinkedList<Node> fromNodes = (LinkedList<Node>) data;

        Token currentToken = node.first_token;
        String edgeTypeString = currentToken.image;

        boolean isEdgeDirected = false;

        if (edgeTypeString.equals("->")) {
            isEdgeDirected = true;
        }

        SimpleNode toIdNode = (SimpleNode) node.jjtGetChild(1);
        LinkedList<Node> toNodes = null;

        if (toIdNode instanceof ASTsubgraph) {// it could be a whole subgraph
                                              // (i.e. we take a group of nodes)
            String subgraphId = (String) toIdNode.jjtAccept(this, null);
            toNodes = this.subgraphs.get(subgraphId);
        } else {
            // must be an ordinary node then...
            Node toNode = (Node) toIdNode.jjtAccept(this, null);
            toNodes = new LinkedList<Node>();
            toNodes.addFirst(toNode);
        }

        LinkedList<Edge> newEdges = new LinkedList<Edge>();

        // now we add an edge from every fromNode to every toNode
        for (Node currentFromNode : fromNodes) {
            for (Node currentToNode : toNodes) {
                newEdges.add(this.graph.addEdge(currentFromNode, currentToNode,
                        isEdgeDirected));
            }
        }

        int numOfChildren = node.jjtGetNumChildren();
        if (numOfChildren > 2) { // we seem to have a chained definition of
                                 // edges here: 1 -> 2 -> 3
            @SuppressWarnings("unchecked")
            Collection<? extends Edge> coll = (Collection<? extends Edge>) node
                    .childrenAccept(this, toNodes);
            newEdges.addAll(coll);
        }

        return newEdges;

    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTedgeop,
     * java.lang.Object)
     */
    public Object visit(ASTedgeop node, Object data) {
        return node.first_token;
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTattr_list,
     * java.lang.Object)
     */
    public Object visit(ASTattr_list node, Object data) {
        return node.childrenAccept(this, data);
    }

    /*
     * @see
     * org.graffiti.plugins.ios.importers.dot.parser.DOTParserVisitor#visit(
     * org.graffiti.plugins.ios.importers.dot.parser.ASTa_list,
     * java.lang.Object)
     */
    public Object visit(ASTa_list node, Object data) {
        GraphElement graphElementData = (GraphElement) data;

        Token currentToken = node.first_token;

        String idString = currentToken.image;

        currentToken = currentToken.next;

        String equalSign = currentToken.image;

        String valueString = "";

        if (equalSign.equals("=")) {
            currentToken = currentToken.next;
            valueString = currentToken.image;
        }

        // Now check if we support the attribute...
        if (idString.equals("label")) {

            LabelAttribute labelAttr = null;
            valueString = removeQuotes(valueString);
            valueString = valueString.replaceAll("\\\\n", "<BR>");
            valueString = valueString.replaceAll("\\t", "    ");

            if (graphElementData instanceof Node) {
                Node currentNode = (Node) graphElementData;
                labelAttr = new NodeLabelAttribute(
                        GraphicAttributeConstants.LABEL);

                // TODO: Font not set properly...
                labelAttr.setFont(this.font.getName());
                labelAttr.setFontSize(this.font.getSize());
                // System.out.println(((StringAttribute)labelAttr
                // .getAttribute("font")).getString());
                //                
                // take care of the size of the nodes...

                DimensionAttribute da = null;

                Dimension dimOfLabel = calculateLabelSize(valueString);

                valueString = valueString.replaceAll("\\t",
                        "&nbsp;&nbsp;&nbsp;&nbsp;");
                valueString = valueString.replace(" ", "&nbsp;");
                labelAttr.setLabel(valueString);

                try {
                    try {
                        currentNode
                                .addAttribute(new NodeGraphicAttribute(), "");
                    } catch (AttributeExistsException a) {
                    }
                    da = (DimensionAttribute) currentNode
                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.DIMENSION);
                    da.setDimension(dimOfLabel);
                } catch (AttributeNotFoundException a) {
                    da = new DimensionAttribute("dimension", dimOfLabel);
                    currentNode.addAttribute(da,
                            GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.DIMENSION);
                }

            }

            else if (graphElementData instanceof Edge) {
                labelAttr = new EdgeLabelAttribute(
                        GraphicAttributeConstants.LABEL, valueString);
            }

            try {
                graphElementData.addAttribute(labelAttr,
                        GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);
            } catch (Exception e) {
                System.out.println("Could not add attribute " + idString
                        + " to " + graphElementData);
            }
        }
        // No explicit support. Just add a string then...
        else {
            try {
                idString = removeQuotes(idString);
                graphElementData.addString("", idString, valueString);
            } catch (Exception e) {
                String id = null;
                try {
                    id = graphElementData.getString("id");
                } catch (Exception e2) {
                    id = "unknown";
                }
                System.out.println("Could not add attribute " + idString
                        + " to " + graphElementData + "( " + id + ")");
            }
        }

        return node.childrenAccept(this, data);
    }

    private String removeQuotes(String string) {
        if (string.startsWith("\"") && string.endsWith("\"")) {
            string = string.substring(1, string.length() - 1);
        }

        return string;
    }

    private Dimension calculateLabelSize(String labelText) {

        if ((labelText == null) || (labelText.equals("")))
            return GraphicAttributeConstants.DEFAULT_NODE_SIZE;

        String[] lines = labelText.split("<BR>");

        // calculate height and width...
        int height = (int) Math.ceil(lines.length
                * font.getStringBounds(lines[0], frc).getHeight()) + 10;
        int width = GraphicAttributeConstants.DEFAULT_NODE_SIZE.width;

        for (int i = 0; i < lines.length; i++) {
            int currentWidth = (int) Math.ceil(font.getStringBounds(lines[i],
                    frc).getWidth()) + 10;
            if (currentWidth > width) {
                width = currentWidth;
            }
        }

        return new Dimension(width, height);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
