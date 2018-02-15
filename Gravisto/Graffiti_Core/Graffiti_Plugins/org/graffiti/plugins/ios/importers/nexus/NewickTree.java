package org.graffiti.plugins.ios.importers.nexus;

import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeConstants;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeUtil;

/**
 * This class builds a tree given in the Newick format by given Tokens.
 */
public class NewickTree implements NexusParsingConstants {
    /** The Graph, in which the tree is to be built. */
    private Graph graph;

    /**
     * Map containing alternative names for the names in the tree. If a name in
     * a tree is set as a key in this map, the value associated with that key is
     * saved in the Node.
     */
    private Map<String, String> translationMap;

    /**
     * The state of parsing, may be <code>STANDARD</code> or
     * <code>ATTRIBUTE</code>.
     */
    private ParseState state;

    /**
     * The Node of the subtree at the position of the last given Token.
     */
    Node current;

    /**
     * Creates a new {@link NewickTree} object which creates a new tree in the
     * Newick tree format.
     * 
     * @param g
     *            The Graph, in which the Newick tree is to be saved.
     * @param translationMap
     *            A {@link Map} that contains a key for every label of the tree
     *            that is to be replaced by another label. May be
     *            <code>null</code> if no translations are to be made.
     */
    public NewickTree(Graph g, Map<String, String> translationMap) {
        assert g != null;

        this.graph = g;
        this.translationMap = translationMap;

        state = ParseState.STANDARD;
        current = createNode(null);
    }

    /**
     * Creates a new {@link NewickTree} object which creates a new tree in the
     * Newick tree format.
     * 
     * @param g
     *            The Graph, in which the Newick tree is to be saved.
     */
    public NewickTree(Graph g) {
        assert g != null;

        this.graph = g;
        this.translationMap = null;

        state = ParseState.STANDARD;
        current = createNode(null);
    }

    /**
     * Returns whether the tree is completely parsed.
     */
    public boolean treeComplete() {
        return PhyloTreeUtil.isRoot(current);
    }

    /**
     * Parses a Token.
     * 
     * @throws ParseException
     *             If a Token cannot be parsed because it is unexpected.
     */
    public void parseToken(Token t) throws ParseException {
        assert t != null : "no Token has been given";

        TokenTypes tType = t.getType();

        if (state == ParseState.STANDARD) {
            if (tType == TokenTypes.SUBTREE_START) {
                current = createNode(current);
            } else if (tType == TokenTypes.NEW_NODE_START) {
                if (PhyloTreeUtil.isRoot(current))
                    throw new ParseException("Cannot create 2 root nodes", 0);

                current = PhyloTreeUtil.getEdgeToParent(current).getSource();
                current = createNode(current);

            } else if (tType == TokenTypes.SUBTREE_END) {
                if (PhyloTreeUtil.isRoot(current))
                    throw new ParseException("Closing unopened parenthesis", 0);

                current = PhyloTreeUtil.getEdgeToParent(current).getSource();
            } else if (tType == TokenTypes.LABEL) {
                setNodeName(current, t.getValue());
            } else if (tType == TokenTypes.ATTRIBUTE_DELIMITER) {
                state = ParseState.ATTRIBUTE;
            } else
                throw new ParseException("Illegal Token type in state"
                        + " ParseState.STANDARD", 0);
        } else if (state == ParseState.ATTRIBUTE) {
            if (tType == TokenTypes.LABEL) {
                setWeight(current, t.getValue());
                state = ParseState.STANDARD;
            } else
                throw new ParseException("Illegal Token type in state"
                        + " ParseState.ATTRIBUTE", 0);
        }
    }

    /**
     * Creates and returns a new Node and connects it to its parent.
     * 
     * @param parent
     *            The parent of the Node, that is to be created. If
     *            <code>null</code>, a root Node is created.
     * @return The newly created Node.
     */
    private Node createNode(Node parent) {
        Node newNode = graph.addNode();

        if (parent != null) {
            Edge edge = graph.addEdge(parent, newNode, true);
            PhyloTreeUtil.setEdgeNumber(edge, parent.getOutDegree());
        }

        return newNode;
    }

    /**
     * Sets the label of a given Node to a given name.
     * 
     * @param node
     *            The Node whose Label is to be set.
     * @param name
     *            The Label that is to be set.
     * @throws ParseException
     *             If the node already contains a label.
     */
    private void setNodeName(Node node, String name) throws ParseException {
        if (translationMap != null && translationMap.containsKey(name)) {
            name = translationMap.get(name);
        }

        NodeLabelAttribute labelAttr = new NodeLabelAttribute(
                GraphicAttributeConstants.LABEL, name);

        try {
            node.addAttribute(labelAttr,
                    GraphicAttributeConstants.LABEL_ATTRIBUTE_PATH);
        } catch (AttributeExistsException e) {
            throw new ParseException("Two labels in one Node.", 0);
        }
    }

    /**
     * Sets the weight of an Edge adjacent to the Node given.
     * 
     * @param node
     *            The Node for whose incoming {@link Edge} the weight is to be
     *            set.
     * @param weight
     *            The weight to set.
     */
    private void setWeight(Node node, String weight) throws ParseException {
        assert node != null;
        assert weight != null;

        // parse value
        double edgeWeight;

        try {
            edgeWeight = Double.valueOf(weight);
        } catch (NumberFormatException e) {
            throw new ParseException("Edge weight is malformated", 0);
        }

        // set value as attribute
        Collection<Edge> edgesToParent = node.getAllInEdges();
        if (edgesToParent.size() == 1) {
            for (Edge e : edgesToParent) {
                e.setDouble(PhyloTreeConstants.PATH_WEIGHT, edgeWeight);
            }
        }
    }

}
