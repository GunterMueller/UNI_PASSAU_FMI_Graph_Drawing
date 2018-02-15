package org.graffiti.plugins.algorithms.phyloTrees.utility;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.phyloTrees.PhylogeneticTree;

/**
 * Saves data necessary to draw phylogenetic trees. A tree is identified via its
 * root node.
 * 
 * An instance of PhyloTreeGraphData is associated with a certain Graph
 * instance.
 */
public class PhyloTreeGraphData {
    /**
     * The DataSets, each associated with a tree associated with this graph.
     */
    private Map<Node, DataSet> dataSets = new HashMap<Node, DataSet>(4);

    /**
     * Contains the vertical space needed by a tree, which is represented by its
     * root node.
     */
    private Map<Node, Double> verticalSpace = new HashMap<Node, Double>(4);

    /**
     * Contains a ordered list of the root Nodes saved in this object.
     */
    private List<Node> rootNodes = new LinkedList<Node>();

    /** Instance of the active algorithm. */
    private PhylogeneticTree algorithm;

    /** The Parameters of the active algorithm. */
    private Parameter<?>[] algorithmParameters;

    /**
     * Enables or disables the usage of the weights set in an edge.
     * <code>true</code> disables this usage, <code>false</code> enable it.
     */
    private boolean fixedEdgeLength;

    /**
     * Sets the currently selected algorithm and the parameters associated with
     * that algorithm.
     * 
     * @param algorithm
     *            The name of the currently selected algorithm. Must not be
     *            null.
     * @param algoParameters
     *            The parameters associated with the set algorithm. Must not be
     *            null.
     */
    public void setAlgorithm(PhylogeneticTree algorithm,
            Parameter<?>[] algoParameters) {
        assert algorithm != null;
        assert algoParameters != null;

        this.algorithm = algorithm;
        this.algorithmParameters = algoParameters;
    }

    /**
     * Returns an instance of the active algorithm.
     * 
     * @return Instance of the active algorithm.
     */
    public PhylogeneticTree getAlgorithm() {
        return this.algorithm;
    }

    /**
     * Returns the parameters which have been selected for use with the active
     * algorithm.
     */
    public Parameter<?>[] getAlgorithmParameters() {
        return this.algorithmParameters;
    }

    /**
     * Adds a new root node to the tree
     * 
     * @param node
     *            The root node of a phylogenetic tree
     */
    public void addRootNode(Node node) {
        rootNodes.add(node);
        dataSets.put(node, new DataSet(node));
        verticalSpace.put(node, 1d);
    }

    /**
     * Removes a root node from this object.
     * 
     * @param root
     *            The root node to be removed.
     */
    public void removeRootNode(Node root) {
        assert isRoot(root) : "given Node is not a root Node";

        rootNodes.remove(root);
        dataSets.remove(root);
        verticalSpace.remove(root);
    }

    /**
     * Replaces a root Node with another node.
     * 
     * @param oldRoot
     *            The old Node saved as a root Node. Must be a Node previously
     *            set as root.
     * @param newRoot
     *            The new root Node, which replaces the old root Node. Must be a
     *            root Node.
     */
    public void replaceRootNode(Node oldRoot, Node newRoot) {
        assert isRoot(oldRoot) : "given old root Node is not a saved root Node";
        assert newRoot.getInDegree() == 0 : "given new root Node has incoming Edges";

        if (oldRoot != newRoot) {
            int index = rootNodes.indexOf(oldRoot);
            rootNodes.remove(oldRoot);
            rootNodes.add(index, newRoot);

            dataSets.remove(oldRoot);
            dataSets.put(newRoot, new DataSet(newRoot));
            assert isRoot(newRoot);

            double vertSpace = verticalSpace.remove(oldRoot);
            verticalSpace.put(newRoot, vertSpace);
        }
    }

    /**
     * Returns the root nodes of the associated graph.
     * 
     * @return Collection of root nodes
     */
    public Collection<Node> getRootNodes() {
        return new LinkedList<Node>(rootNodes);
    }

    /**
     * Determines whether the given Node is a root Node.
     * 
     * @param node
     *            the Node to be tested
     * @return true, if node is a root Node, false otherwise
     */
    public boolean isRoot(Node node) {
        return node != null && dataSets.containsKey(node);
    }

    /**
     * Returns the number of trees in the associated graph.
     * 
     * @return Number of trees in the graph.
     */
    public int getTreeCount() {
        return rootNodes.size();
    }

    /**
     * Indicate that the subtree with a given node as root has changed.
     * 
     * @param root
     *            The tree that has changed.
     */
    public void update(Node root) {
        assert isRoot(root) : "given Node is not a root Node";

        dataSets.get(root).update();
    }

    /**
     * Sets the vertical space a tree needs for its drawing.
     * 
     * @param root
     *            The root Node of the tree whose vertical space is to be set.
     * @param verticalSpace
     *            The vertical space of the tree.
     */
    public void setVerticalSpace(Node root, double verticalSpace) {
        assert isRoot(root) : "given Node is no set root Node";
        assert verticalSpace >= 0 : "vertical space must not be smaller then 0";

        this.verticalSpace.put(root, verticalSpace);
    }

    /**
     * Returns the upper boundary for a given tree.
     * 
     * @param root
     *            The root Node of the tree whose upper boundary is to be
     *            returned.
     * @return The upper boundary of the tree given as a parameter.
     */
    public double getUpperBound(Node root) {
        assert isRoot(root) : "no valid root Node given";

        double upperBound = PhyloTreeConstants.VERTICAL_STARTING_POSITION;

        Iterator<Node> it = rootNodes.iterator();
        boolean nodeFound = false;

        while (it.hasNext() && !nodeFound) {
            Node node = it.next();
            if (node == root) {
                nodeFound = true;
            } else {
                upperBound += verticalSpace.get(node);
            }
        }

        return upperBound;
    }

    /**
     * Returns the given Edge's weight shifted.
     * 
     * The edge weight is shifted by the minimum edge weight, if this weight is
     * smaller or equal to 0. So the resulting edge weight will always be
     * greater than 0.
     * 
     * @param root
     *            The root Node of the tree, to which the given Edge belongs.
     * @param edge
     *            The Edge whose shifted Edge weight is to be returned.
     * @return The shifted edge weight of the given edge.
     */
    public double getShiftedEdgeWeight(Node root, Edge edge) {
        assert isRoot(root) : "given Node is not a root Node";

        if (fixedEdgeLength)
            return 1;
        else
            return dataSets.get(root).getShiftedEdgeWeight(edge);
    }

    /**
     * Returns the minimum edge weight of a given tree.
     * 
     * @param root
     *            The root of the tree, whose minimum weight is to be returned.
     * @return The minimum edge weight of the given tree.
     */
    public double getMinEdgeWeight(Node root) {
        assert isRoot(root) : "given Node is not a root Node";

        if (fixedEdgeLength)
            return 1;
        else
            return dataSets.get(root).getMinWeight();
    }

    /**
     * Returns the average edge weight of a given tree.
     * 
     * @param root
     *            The root of the tree whose average weight is to be returned.
     * @return The average edge weight of the tree given as a parameter.
     */
    public double getAverageEdgeWeight(Node root) {
        assert isRoot(root) : "given Node is not a root Node";

        if (fixedEdgeLength)
            return 1;
        else
            return dataSets.get(root).getAverageWeight();
    }

    /**
     * Returns the maximum path length of a tree, that is the maximum weight of
     * a path from the root to a leaf.
     * 
     * @param root
     *            The root node of the tree whose maximum path weight is to be
     *            returned.
     * @return The maximum path weight of the tree given as a parameter.
     */
    public double getMaxTreePathLength(Node root) {
        assert isRoot(root) : "given Node is not a root Node";

        DataSet dataSet = dataSets.get(root);

        if (fixedEdgeLength)
            return dataSet.getHeight();
        else
            return dataSet.getMaxPathWeight();
    }

    /**
     * Returns the maximum length of all the labels attached to leaves of a
     * given tree.
     * 
     * @param root
     *            The root of the tree.
     * @return The maximum width of the labels attached to the leaves of a given
     *         tree.
     */
    public double getMaxLabelWidth(Node root) {
        assert isRoot(root) : "given Node is not a root Node";

        return dataSets.get(root).getMaxLabelWidth();
    }

    /**
     * Returns the maximum height of all the labels attached to leaves of a
     * given tree.
     * 
     * @param root
     *            The root of the tree.
     * @return The maximum height of the labels attached to the leaves of a
     *         given tree.
     */
    public double getMaxLabelHeight(Node root) {
        assert isRoot(root) : "given Node is not a root Node";

        return dataSets.get(root).getMaxLabelHeight();
    }

    /**
     * Sets whether edge weight is fixed to a constant value for each edge or
     * whether the weight contained in the node is to be used. By default this
     * is set to <code>true</code>.
     * 
     * @param use
     *            <code>false</code> sets the edge weight of every edge to a
     *            fixed value, <code>true</code> enables the usage of the weight
     *            set in a node.
     */
    public void setUseWeight(boolean use) {
        this.fixedEdgeLength = !use;
    }

    /**
     * Returns the number of leaves in the tree specified by the root node.
     * 
     * @param root
     *            The root Node of the tree, whose number of leaves is to be
     *            returned.
     * @return The number of leaves in the given tree.
     */
    public int getLeafCount(Node root) {
        assert isRoot(root) : "given Node is not a root Node";

        return dataSets.get(root).getNumberOfLeaves();
    }

    /**
     * Returns the number of leaves in the tree specified by the root node.
     * 
     * @param root
     *            The root Node of the tree, whose number of leaves is to be
     *            returned.
     * @return The number of leaves in the given tree.
     */
    public int getNodeCount(Node root) {
        assert isRoot(root) : "given Node is not a root Node";

        return dataSets.get(root).getNumberOfNodes();
    }
}
