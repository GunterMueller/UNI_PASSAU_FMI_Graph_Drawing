package org.graffiti.plugins.algorithms.planarity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Node;

/**
 * Stores the embedding of a biconnected component
 * 
 * @author Wolfgang Brunner
 */
public class TestedBicomp extends TestedObject {

    /**
     * Stores a Kuratowski subgraph
     */
    private KuratowskiSubgraph kuratowskiSubgraph;

    /**
     * The boundary of the embedding of the biconnected component
     */
    private List<Node> boundary;

    /**
     * The biconnected component
     */
    private Bicomp bicomp;

    /**
     * Constructs a new <code>TestedBicomp</code>
     * 
     * @param bicomp
     *            The <code>Bicomp</code> object
     * @param map
     *            The mapping between <code>org.graffiti.graph.Node</code> and
     *            <code>RealNode</code> objects
     */
    public TestedBicomp(Bicomp bicomp, HashMap<Node, RealNode> map) {
        super(map);
        this.bicomp = bicomp;
        planar = bicomp.isPlanar();
        nodes = new LinkedList<Node>();
        numberOfNodes = bicomp.numberOfNodes;
        for (Iterator<ArbitraryNode> i = bicomp.nodes.iterator(); i.hasNext();) {
            ArbitraryNode aNode = i.next();
            nodes.add(aNode.getRealNode().originalNode);
        }
        boundary = new LinkedList<Node>();
        for (Iterator<ArbitraryNode> i = bicomp.boundary.iterator(); i
                .hasNext();) {
            ArbitraryNode aNode = i.next();
            boundary.add(aNode.getRealNode().originalNode);
        }
        kuratowskiSubgraph = bicomp.kuratowskiSubgraph;
    }

    /**
     * Gives the Kuratowski subgraph
     * 
     * @return The Kuratowski subgraph
     */
    public KuratowskiSubgraph getKuratowskiSubgraph() {
        return bicomp.kuratowskiSubgraph;
    }

    /**
     * Gives all edges of the given node belonging to this biconnected component
     * 
     * @param node
     *            The node
     * 
     * @return The adjacency list
     */
    @Override
    public List<Node> getAdjacencyList(Node node) {
        RealNode rNode = map.get(node);
        List<Node> result = new LinkedList<Node>();
        if (!rNode.adjacencyList.isEmpty()) {
            if (rNode.adjacencyList.get(0).bicomp == bicomp) {
                addAll(result, rNode.adjacencyList);
                return result;
            }
        }
        List<RealNode> childs = rNode.separatedDFSChildList.getList();
        for (Iterator<RealNode> i = childs.iterator(); i.hasNext();) {
            RealNode child = i.next();
            if (!child.virtualParent.adjacencyList.isEmpty()
                    && child.virtualParent.adjacencyList.get(0).bicomp == bicomp) {
                addAll(result, child.virtualParent.adjacencyList);
                return result;
            }
        }
        return result;
    }

    /**
     * Gives all double edges from the given node
     * 
     * @param node
     *            The node
     * 
     * @return The list of double edges
     */
    @Override
    public List<Node> getDoubleEdgeTargets(Node node) { // FIXME
        RealNode sNode = map.get(node);
        List<Node> targets = new LinkedList<Node>();
        for (Iterator<Node> i = sNode.doubleEdgeTargets.iterator(); i.hasNext();) {
            Node target = i.next();
            RealNode tNode = map.get(target);
            if ((tNode.bicomp == bicomp)
                    || ((sNode.bicomp == bicomp) && (sNode.DFI
                            + sNode.DFSStartNumber > tNode.DFI
                            + tNode.DFSStartNumber))) {
                targets.add(target);
            }
        }
        return targets;
    }

    /**
     * Gives the number of double edges in the biconnected component
     * 
     * @return The number of double edges
     */
    @Override
    public int getNumberOfDoubleEdges() {
        int result = 0;
        for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Node node = i.next();
            result += getDoubleEdgeTargets(node).size();
        }
        return result;
    }

    /**
     * Gives the boundary of the biconnected component
     * 
     * @return The boundary
     */
    public List<Node> getBoundary() {
        return boundary;
    }

    /**
     * Gives a textual representation of the embedding
     * 
     * @return The <code>String</code> representing the biconnected component
     */
    @Override
    public String toString() {
        String result = "";

        result += "Consists of: " + toStringNodeList(nodes) + "\n";

        if (!isPlanar()) {
            result += "\nBiconnected component is not planar.\n";
            result += kuratowskiSubgraph;
        } else {
            result += "\nBiconnected component is planar.\n\n";

            // result += "\nBoundary of embedding in clockwise orientation:\n";
            // result += indent(toStringNodeList(getBoundary()), 4) + "\n\n";

            result += "Adjacency lists in clockwise orientation:\n";

            for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
                Node node = i.next();
                result += indent(toString(node) + ": "
                        + toStringNodeList(getAdjacencyList(node)) + "\n", 4);
            }
        }
        return result;
    }

}
