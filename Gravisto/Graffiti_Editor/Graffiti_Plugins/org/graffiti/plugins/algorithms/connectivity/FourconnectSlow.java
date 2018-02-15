package org.graffiti.plugins.algorithms.connectivity;

/**
 * @author Le Pham Hai Dang
 */

import java.util.Iterator;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;

public class FourconnectSlow extends AbstractAlgorithm implements
        CalculatingAlgorithm {

    // ~ Instance fields
    // ========================================================
    private int dfsnum;

    private boolean isFourconnected;

    private Node root;

    private Node[] separationTriangle = new Node[3];

    private boolean finished = false;

    /**
     * If set to <code>true</code> the result of the test is printed as a text
     * and the nodes and edges of graph get colored
     */
    private boolean GUIMode = false;

    // ~ Methods
    // ================================================================

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

    /** Executes the test without GUI */
    public void testFourconnect() {
        if (!finished) {
            startFourconnect();
        }
    }

    /** Start the triconnectivity-test */
    private void startFourconnect() {
        // Triconnected
        Triconnect triconnect = new Triconnect();
        triconnect.attach(graph);
        if (GUIMode) {
            triconnect.execute();
        } else {
            triconnect.testTriconnect();
        }
        if (!triconnect.isTriconnected()) {
            isFourconnected = false;
            if (GUIMode) {
                // System.out.println("\t Fourconnected: " + isFourconnected);
            }
            return;
        }
        if (graph.getNumberOfNodes() <= 3) {
            isFourconnected = true;
            return;
        }

        // Fourconnected
        graph.getListenerManager().transactionStarted(this);
        // root = graph.getNodes().get(0);
        addAttributes();
        boolean done = false;
        isFourconnected = true;

        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext()
                && !done;) {
            Node current = i.next();

            for (Iterator<Node> j = graph.getNodes().iterator(); j.hasNext()
                    && !done;) {

                Node current2 = j.next();

                if (current != current2) {

                    dfsnum = 0;
                    setAttributes();
                    current.setBoolean("FPPmark", true);
                    current2.setBoolean("FPPmark", true);

                    for (Node n : graph.getNodes()) {

                        if (current != n && current2 != n) {
                            root = n;
                            break;
                        }
                    }
                    lowpoint(root);

                    isFourconnected = biconnect();
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
        deleteAttributes();
        graph.getListenerManager().transactionFinished(this);
        finished = true;
    }

    /**
     * Add to the graph the attributes <code>visited</code>, <code>low</code>,
     * <code>dfsnum</code>, <code>parent</code> and <code>mark</code>.
     */
    private void addAttributes() {
        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
            Node current = i.next();
            if (GUIMode) {
                // Connect.setNodeColor(current, Color.BLUE);
            }
            current.addBoolean("", "FPPvisited", false);
            current.addInteger("", "FPPlow", 0);
            current.addInteger("", "FPPdfsnum", 0);
            current.addInteger("", "FPPparent", -1);
            current.addBoolean("", "FPPmark", false);
        }
    }

    /**
     * Adjust the attributes <code>visited</code>, <code>low</code>,
     * <code>dfsnum</code>, <code>parent</code> and <code>mark</code> to the
     * default value.
     */
    private void setAttributes() {
        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
            Node current = i.next();
            current.setInteger("FPPlow", 0);
            current.setInteger("FPPdfsnum", 0);
            current.setInteger("FPPparent", -1);
            current.setBoolean("FPPvisited", false);
            current.setBoolean("FPPmark", false);
        }
    }

    /**
     * Remove the added attributes <code>visited</code>, <code>low</code>,
     * <code>dfsnum</code>, <code>parent</code> and <code>mark</code>
     */
    private void deleteAttributes() {
        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
            Node current = i.next();
            current.removeAttribute("FPPlow");
            current.removeAttribute("FPPdfsnum");
            current.removeAttribute("FPPparent");
            current.removeAttribute("FPPvisited");
            current.removeAttribute("FPPmark");
        }
    }

    /**
     * @param node
     *            the <code>Node</code> get his <code>low</code> point number.
     */
    private void lowpoint(Node node) {
        int low;
        node.setInteger("FPPdfsnum", dfsnum);
        node.setInteger("FPPlow", dfsnum);
        node.setBoolean("FPPvisited", true);
        for (Iterator<Node> i = node.getNeighborsIterator(); i.hasNext();) {
            Node current = i.next();
            if (!current.getBoolean("FPPmark")) {
                if (!current.getBoolean("FPPvisited")) {
                    dfsnum++;
                    current.setInteger("FPPparent", node
                            .getInteger("FPPdfsnum"));
                    lowpoint(current);
                    low = minLowLow(node, current);
                    node.setInteger("FPPlow", low);
                }
                /** (node, current) is backtree */
                if ((current.getInteger("FPPdfsnum") < node
                        .getInteger("FPPdfsnum"))
                        && (node.getInteger("FPPparent") != current
                                .getInteger("FPPdfsnum"))) {
                    low = minLowDFSnum(node, current);
                    node.setInteger("FPPlow", low);
                }
            }
        }
    }

    /**
     * @param node
     *            <code>Node</code> and vertex <code>Node</code>
     * @return a the min value of two <code>low</code>-values of two nodes
     */
    private int minLowLow(Node node, Node vertex) {
        return Math.min(node.getInteger("FPPlow"), vertex.getInteger("FPPlow"));
    }

    /**
     * @param node
     *            <code>Node</code> and vertex <code>Node</code>
     * @return a the min value of a low-value and a dfsnum between two nodes
     */
    private int minLowDFSnum(Node node, Node vertex) {
        return Math.min(node.getInteger("FPPlow"), vertex
                .getInteger("FPPdfsnum"));
    }

    /**
     * @return <code>true</code>, if <code>Graph</code> is biconnected,
     *         otherwise <code>false</code>.
     */
    private boolean biconnect() {
        if (rootIsArticulationPoint())
            return false;
        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
            Node current = i.next();
            if (current != root && !current.getBoolean("FPPmark")) {
                for (Iterator<Node> j = current.getNeighborsIterator(); j
                        .hasNext();) {
                    Node neighbour = j.next();
                    if ((neighbour.getInteger("FPPlow") >= current
                            .getInteger("FPPdfsnum"))
                            && (!neighbour.getBoolean("FPPmark"))
                            && (neighbour.getInteger("FPPparent") == current
                                    .getInteger("FPPdfsnum"))) {
                        separationTriangle[0] = current;
                        if (GUIMode) {
                            // Connect.setNodeColor(current, COLOR);
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * @return <code>true</code>, if the root <code>Node</code> is
     *         articulationpoint, <code>false</code> otherwise.
     */
    private boolean rootIsArticulationPoint() {
        int children = 0;
        for (Iterator<Node> i = root.getNeighborsIterator(); i.hasNext();) {
            Node neighbour = i.next();
            if (!neighbour.getBoolean("FPPmark")
                    && (neighbour.getInteger("FPPparent") == root
                            .getInteger("FPPdfsnum"))) {
                children++;
                if (children == 2) {
                    separationTriangle[0] = root;
                    if (GUIMode) {
                        // Connect.setNodeColor(root, COLOR);
                    }
                    return true;
                }
            }
        }
        return false;
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
