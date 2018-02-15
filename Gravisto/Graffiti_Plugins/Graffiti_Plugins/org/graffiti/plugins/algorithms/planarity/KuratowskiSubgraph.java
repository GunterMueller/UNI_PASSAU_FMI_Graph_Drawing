package org.graffiti.plugins.algorithms.planarity;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Stores a found Kuratowski subgraph
 * 
 * @author Wolfgang Brunner
 */
public class KuratowskiSubgraph {

    /**
     * Constant representing a K 5 subgraph
     */
    public final static int K5 = 0;

    /**
     * Constant representing a K 3 3 subgraph
     */
    public final static int K33 = 1;

    /**
     * The type of the subgraph (K 5 or K 3 3)
     */
    private int type;

    /**
     * The list of <code>org.graffiti.graph.Node</code> objects in the
     * Kuratowski subgraph
     */
    private List<Node> nodes;

    /**
     * The list of paths in the Kurawoski subgraph. Each entry in the list is a
     * list of <code>org.graffiti.graph.Node</code> objects.
     */
    private List<LinkedList<Node>> paths;

    /**
     * The <code>org.graffiti.graph.Graph</code> the Kuratowski subgraph belongs
     * to
     */
    private Graph graph;

    /**
     * Constructs a new <code>KuratowskiSubgraph</code>
     * 
     * @param type
     *            The type of the subgraph
     * @param graph
     *            The graph the Kuratowski subgraph belongs to
     */
    public KuratowskiSubgraph(int type, Graph graph) {
        this.type = type;
        nodes = new LinkedList<Node>();
        paths = new LinkedList<LinkedList<Node>>();
        this.graph = graph;
    }

    /**
     * Adds a node to the subgraph
     * 
     * @param node
     *            The <code>ArbitraryNode</code> to add
     */
    public void addNode(ArbitraryNode node) {
        nodes.add(node.getRealNode().originalNode);
    }

    /**
     * Adds a path to the subgraph
     * 
     * @param path
     *            The path (a list of <code>ArbitraryNode</code> objects) to add
     */
    public void addPath(List<ArbitraryNode> path) {
        LinkedList<Node> originalNodesPath = new LinkedList<Node>();
        for (Iterator<ArbitraryNode> i = path.iterator(); i.hasNext();) {
            ArbitraryNode current = i.next();
            originalNodesPath.add(current.getRealNode().originalNode);
        }
        paths.add(originalNodesPath);
    }

    /**
     * Gives the type of the subgraph
     * 
     * @return The type
     */
    public int getType() {
        return type;
    }

    /**
     * Gives the list of nodes of this subgraph
     * 
     * @return The list of <code>org.graffiti.graph.Node</code> objects
     *         belonging to this subgraph
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * Gives the list of paths of this subgraph
     * 
     * @return The list of lists of <code>org.graffiti.graph.Node</code> objects
     *         belonging to this subgraph
     */
    public List<LinkedList<Node>> getPaths() {
        return paths;
    }

    /**
     * Gives the list of paths starting from the given node
     * 
     * @param node
     *            The node to paths start from
     * 
     * @return The list of paths
     */
    public List<LinkedList<Node>> getPaths(Node node) {
        List<LinkedList<Node>> result = new LinkedList<LinkedList<Node>>();
        for (Iterator<LinkedList<Node>> i = paths.iterator(); i.hasNext();) {
            LinkedList<Node> path = i.next();
            if (path.getFirst() == node) {
                result.add(path);
            } else if (path.getLast() == node) {
                LinkedList<Node> reverse = new LinkedList<Node>();
                for (Iterator<Node> j = path.iterator(); j.hasNext();) {
                    reverse.addFirst(j.next());
                }
                result.add(reverse);
            }
        }
        return result;
    }

    /**
     * Gives a textual representatio of the Kuratowski subgraph
     * 
     * @return The <code>String</code> representing the subgraph
     */
    @Override
    public String toString() {
        String result = "";
        switch (type) {
        case K5:
            result += "\nFound a K_5 minor consisting of the nodes ";
            break;
        case K33:
            result += "\nFound a K_3_3 minor consisting of the nodes ";
            break;
        default:
            result += "\nNo minor found.\n";
        }
        result += TestedObject.toStringNodeList(nodes) + "\n";
        result += "\nThe paths are:\n";
        for (Iterator<LinkedList<Node>> i = paths.iterator(); i.hasNext();) {
            result += TestedObject.indent(TestedObject.toStringNodeList(i
                    .next())
                    + "\n", 4);
        }
        return result;
    }

    /**
     * Sets the color of a path
     * 
     * @param path
     *            The path to color (a list of
     *            <code>org.graffiti.graph.Node</code> objects
     * @param c
     *            The new color
     */
    private void setPathColor(LinkedList<Node> path, Color c) {
        Node current = null;
        Node last = null;
        for (Iterator<Node> i = path.iterator(); i.hasNext();) {
            last = current;
            current = i.next();
            if (last != null) {
                PlanarityAlgorithm.setEdgeColor(last, current, c);
            }
        }
    }

    /**
     * Marks all nodes and edges belonging to the Kuratowski subgraph red
     */
    public void markKuratowskiSubgraph() {
        graph.getListenerManager().transactionStarted(this);
        for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Node node = i.next();
            PlanarityAlgorithm.setNodeColor(node, Color.RED);
        }
        for (Iterator<LinkedList<Node>> i = paths.iterator(); i.hasNext();) {
            LinkedList<Node> path = i.next();
            setPathColor(path, Color.RED);
        }
        graph.getListenerManager().transactionFinished(this);
    }

}
