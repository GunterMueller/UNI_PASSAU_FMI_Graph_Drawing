package org.graffiti.plugins.algorithms.connectivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;

/**
 * This class implements a fourconnenctivity test with the running time O(n^3)
 * 
 * @author Thomas
 * @version $Revision: 1408 $ $Date: 2006-08-21 09:46:50 +0200 (Mo, 21 Aug 2006)
 *          $
 */
public class Fourconnect extends AbstractAlgorithm implements
        CalculatingAlgorithm {

    private int dfsnum;

    private boolean isFourconnected;

    private Node root;

    private Node[] separationTriangle = new Node[3];

    private boolean finished = false;

    private HashMap<Node, Integer> dfs = new HashMap<Node, Integer>();

    private HashMap<Node, Integer> low = new HashMap<Node, Integer>();

    private HashMap<Node, Integer> parent = new HashMap<Node, Integer>();

    private HashMap<Node, Boolean> mark = new HashMap<Node, Boolean>();

    private HashMap<Node, Boolean> visited = new HashMap<Node, Boolean>();

    private Collection<Node[]> treeEdges = new LinkedList<Node[]>();

    private boolean rootIsArticulationPoint = false;

    /**
     * If set to <code>true</code> the result of the test is printed as a text
     * and the nodes and edges of graph get colored
     */
    private boolean GUIMode = false;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        GUIMode = true;
        startFourconnect();
        GUIMode = false;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Test fourconnectivity";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
    }

    /**
     * Executes the test without GUI
     */
    public void testFourconnect() {
        if (!finished) {
            startFourconnect();
        }
    }

    /**
     * Start the fourconnectivity-test
     */
    private void startFourconnect() {

        // Fourconnected
        graph.getListenerManager().transactionStarted(this);
        boolean done = false;
        isFourconnected = true;

        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext()
                && !done;) {
            Node current = i.next();

            for (Iterator<Node> j = graph.getNodes().iterator(); j.hasNext()
                    && !done;) {

                Node current2 = j.next();

                if (current != current2) {
                    resetAttributes();

                    // "remove" all combinations of two nodes...
                    mark.put(current, true);
                    mark.put(current2, true);

                    for (Node n : graph.getNodes()) {

                        if (current != n && current2 != n) {
                            root = n;
                            break;
                        }
                    }

                    // ... and run the 2-connectivity test
                    lowpoint(root);

                    isFourconnected = isBiconnected();
                    if (!isFourconnected) {
                        done = true;
                        separationTriangle[1] = current;
                        separationTriangle[2] = current2;

                        if (GUIMode) {
                            // Connect.setNodeColor(current, COLOR);
                            // Connect.setNodeColor(current2, COLOR);
                        }
                    }
                }
            }
        }

        if (GUIMode) {
            // System.out.println("\t Fourconnected: " + isFourconnected);
        }
        graph.getListenerManager().transactionFinished(this);
        finished = true;
    }

    /**
     * Adjust the attributes <code>visited</code>, <code>low</code>,
     * <code>dfsnum</code>, <code>parent</code> and <code>mark</code> to the
     * default value.
     */
    private void resetAttributes() {
        treeEdges = new LinkedList<Node[]>();
        rootIsArticulationPoint = false;
        dfsnum = 0;
        for (Node current : graph.getNodes()) {
            low.put(current, 0);
            dfs.put(current, 0);
            parent.put(current, -1);
            visited.put(current, false);
            mark.put(current, false);
        }
    }

    /**
     * @param node
     *            the <code>Node</code> get his <code>low</code> point number.
     */
    private void lowpoint(Node node) {
        int children = 0;
        dfs.put(node, dfsnum);
        low.put(node, dfsnum);
        visited.put(node, true);

        for (Node current : node.getNeighbors()) {
            if (!mark.get(current)) {
                // Tree Edge
                if (!visited.get(current)) {
                    dfsnum++;
                    parent.put(current, dfs.get(node));
                    lowpoint(current);
                    low.put(node, Math.min(low.get(node), low.get(current)));

                    // Only for performance
                    if (node == root) {
                        children++;
                        if (children >= 2) {
                            rootIsArticulationPoint = true;
                        }
                    }

                    Node[] treeEdge = new Node[2];
                    treeEdge[0] = node;
                    treeEdge[1] = current;
                    treeEdges.add(treeEdge);
                }

                // Backedge
                if ((dfs.get(current) < dfs.get(node))
                        && (parent.get(node) != dfs.get(current))) {
                    low.put(node, Math.min(low.get(node), dfs.get(current)));
                }
            }
        }
    }

    /**
     * @return <code>true</code>, if <code>Graph</code> is biconnected,
     *         otherwise <code>false</code>.
     */
    private boolean isBiconnected() {

        if (rootIsArticulationPoint) {
            separationTriangle[0] = root;
            if (GUIMode) {
                // Connect.setNodeColor(root, COLOR);
            }
            return false;
        }
        for (Node[] treeEdge : treeEdges) {
            if (treeEdge[0] != root && !mark.get(treeEdge[0])) {

                if (treeEdge[1] != null) {

                    if (low.get(treeEdge[1]) >= dfs.get(treeEdge[0])) {
                        separationTriangle[0] = treeEdge[0];
                        if (GUIMode) {
                            // Connect.setNodeColor(treeEdge[0], COLOR);
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return <code>true</code>, if the <code>Graph</code> is triconnected,
     *         <code>false</code> otherwise.
     */
    public boolean isFourconnected() {
        return isFourconnected;
    }

    /**
     * The array is empty, if the graph is NOT connected or NOT biconnected.
     * 
     * @return a separation pair as an array otherwise the array is empty
     */
    public Node[] getSeparationTriangle() {
        return separationTriangle;
    }

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult result = new DefaultAlgorithmResult();
        String text = "The graph is " + (isFourconnected() ? "" : "not ")
                + "fourconnected.";
        result.setComponentForJDialog(text);
        result.addToResult("fourconnected", isFourconnected());
        return result;
    }

}
