package org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;
import org.graffiti.plugins.algorithms.phyloTrees.PhylogeneticTree;
import org.graffiti.plugins.algorithms.phyloTrees.utility.GravistoUtil;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeConstants;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeUtil;

public abstract class AbstractTree implements PhylogeneticTree {
    protected static final String LABEL_X_ALIGNMENT_PATH = GraphicAttributeConstants.LABEL
            + Attribute.SEPARATOR
            + GraphicAttributeConstants.POSITION
            + Attribute.SEPARATOR + GraphicAttributeConstants.ALIGNMENT_X;

    /**
     * Returns whether the Node given as a parameter is a leaf.
     * 
     * @param node
     *            The Node to be tested. Must not be null.
     * @return <code>true</code> if node is a leaf, <code>false</code>
     *         otherwise.
     */
    public static boolean isLeaf(Node node) {
        assert node != null;

        return (node.getOutDegree() == 0);
    }

    /**
     * Returns whether the Node given as a parameter a root.
     * 
     * @param node
     *            The Node to be tested. Must not be null.
     * @return <code>true</code> if node is a root node, <code>false</code>
     *         otherwise.
     */
    public static boolean isRoot(Node node) {
        assert node != null : "node must not be null";

        return (node.getInDegree() == 0);
    }

    /**
     * Sets the attributes of the given Node as desired by the algorithm.
     * Distinguishes between root Node, inner Nodes and leaf Nodes.
     * 
     * @param node
     *            The Node whose attributes are to be set.
     * @see AbstractTree#prepareInnerNode(Node)
     * @see AbstractTree#prepareRootNode(Node)
     * @see AbstractTree#prepareLeafNode(Node)
     */
    protected void prepareNode(Node node) {
        if (isRoot(node)) {
            prepareRootNode(node);
        } else if (isLeaf(node)) {
            prepareLeafNode(node);
        } else {
            prepareInnerNode(node);
        }
    }

    protected void prepareRootNode(Node root) {
        NodeGraphicAttribute attr = (NodeGraphicAttribute) root
                .getAttribute(GraphicAttributeConstants.GRAPHICS);

        attr.setShape(PhyloTreeConstants.ROOT_NODE_SHAPE);

        // set appropriate size
        DimensionAttribute dim = attr.getDimension();
        dim.setWidth(PhyloTreeConstants.ROOT_NODE_DIAMETER);
        dim.setHeight(PhyloTreeConstants.ROOT_NODE_DIAMETER);

        // set node fillcolor
        ColorAttribute fill = attr.getFillcolor();
        fill.setColor(PhyloTreeConstants.ROOT_NODE_COLOR);

        // set node framecolor
        ColorAttribute frame = attr.getFramecolor();
        frame.setColor(PhyloTreeConstants.ROOT_NODE_COLOR);
    }

    protected void prepareLeafNode(Node node) {
        DimensionAttribute nodeDim = (DimensionAttribute) node
                .getAttribute(GraphicAttributeConstants.DIM_PATH);

        nodeDim.setHeight(1);
        nodeDim.setWidth(1);
    }

    protected void prepareInnerNode(Node node) {
        DimensionAttribute nodeDim = (DimensionAttribute) node
                .getAttribute(GraphicAttributeConstants.DIM_PATH);

        nodeDim.setHeight(1);
        nodeDim.setWidth(1);
    }

    /**
     * Sets the visual attributes of the given Edge as desired by the algorithm.
     * 
     * The Edge thickness is set and the arrows are deleted.
     * 
     * @param edge
     *            The Edge whose visual attributes are to be set.
     * @see PhyloTreeConstants#DEFAULT_EDGE_THICKNESS
     */
    protected void prepareEdge(Edge edge) {
        // set edge thickness
        edge.setDouble(GraphicAttributeConstants.FRAMETHICKNESS_PATH,
                PhyloTreeConstants.DEFAULT_EDGE_THICKNESS);

        // delete arrows
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        ega.setArrowhead("");
    }

    /**
     * Resets the rotation of any label in this Node.
     * 
     * @param node
     *            The Node, whose Label's rotation is to be reset to 0.
     */
    protected void resetLabelRotation(Node node) {
        if (node.containsAttribute(GraphicAttributeConstants.LABEL)) {
            LabelAttribute label = (LabelAttribute) node
                    .getAttribute(GraphicAttributeConstants.LABEL);
            NodeLabelPositionAttribute labelPos = (NodeLabelPositionAttribute) label
                    .getAttribute(GraphicAttributeConstants.POSITION);
            labelPos.setRotationRadian(0);
        }
    }

    /**
     * Resets the Color of an Edge to the default color.
     * 
     * @param e
     *            The Edge, whose color is to be reset.
     */
    protected void resetEdgeColor(Edge e) {
        GravistoUtil.setEdgeColor(e, PhyloTreeConstants.DEFAULT_EDGE_COLOR);
    }

    /**
     * Deletes all bends in the given Edge and sets the shape to straightline.
     * 
     * @param e
     *            The Edge whose bends are to be deleted.
     */
    protected void resetEdgeBends(Edge e) {
        GravistoUtil.setEdgeShape(e,
                GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME);
    }

    /**
     * Returns the parent of the given Node.
     * 
     * @param node
     *            The Node whose parent is to be returned. Must not be null and
     *            must have exactly one incoming edge.
     * @return The parent Node of the Node given as a parameter.
     */
    protected static Node getParent(Node node) {
        assert node != null : "node must not be null";
        assert node.getInDegree() == 1 : "node has an inappropriate number of incoming edges";

        return PhyloTreeUtil.getEdgeToParent(node).getSource();
    }
}
