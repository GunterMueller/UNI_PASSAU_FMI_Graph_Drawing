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

public class Biconnect extends AbstractAlgorithm implements
        CalculatingAlgorithm {

    // ~ Instance fields
    // ========================================================
    private int dfsnum;

    private boolean isBiconnect;

    private Node root;

    private Node separationNode = null;

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
        startBiconnect();
        GUIMode = false;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Test biconnectivity";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
    }

    /** Executes the test without GUI */
    public void testBiconnect() {
        if (!finished) {
            startBiconnect();
        }
    }

    /** Start the biconnectivity-test */
    private void startBiconnect() {
        isBiconnect = true;
        Connect connect = new Connect();
        connect.attach(graph);
        if (GUIMode) {
            connect.execute();
        } else {
            connect.testConnect();
        }
        if (!connect.isConnected()) {
            isBiconnect = false;
            if (GUIMode) {
                // System.out.println("\t Biconnected: " + isBiconnect);
            }
            return;
        }
        graph.getListenerManager().transactionStarted(this);
        if (!graph.isEmpty()) {
            dfsnum = 0;
            root = graph.getNodes().get(0);
            addAttributes();
            lowpoint(root);
            isBiconnect = biconnect();
            deleteAttributes();
        }
        if (GUIMode) {
            // System.out.println("\t Biconnected: " + isBiconnect);
        }
        graph.getListenerManager().transactionFinished(this);
        finished = true;
    }

    /**
     * Add to the graph the attributes <code>visited</code>, <code>low</code>,
     * <code>dfsnum</code> and <code>parent</code>
     */
    private void addAttributes() {
        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
            Node current = i.next();
            if (GUIMode) {
                // Connect.setNodeColor(current,
                // GraphicAttributeConstants.DEFAULT_NODE_FILLCOLOR);
            }
            current.addInteger("", "FPPlow", 0);
            current.addInteger("", "FPPdfsnum", 0);
            current.addInteger("", "FPPparent", -1);
            current.addBoolean("", "FPPvisited", false);
        }
    }

    /**
     * Remove the added attributes <code>visited</code>, <code>low</code>,
     * <code>dfsnum</code> and <code>parent</code>
     */
    private void deleteAttributes() {
        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
            try {
                Node current = i.next();
                current.removeAttribute("FPPlow");
                current.removeAttribute("FPPdfsnum");
                current.removeAttribute("FPPparent");
                current.removeAttribute("FPPvisited");
            } catch (Exception e) {
                /** */
            }
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
            if (!current.getBoolean("FPPvisited")) {
                dfsnum++;
                current.setInteger("FPPparent", node.getInteger("FPPdfsnum"));
                lowpoint(current);
                low = minLowLow(node, current);
                node.setInteger("FPPlow", low);
            }
            /** (node, current) is backtree */
            if ((current.getInteger("FPPdfsnum") < node.getInteger("FPPdfsnum"))
                    && (node.getInteger("FPPparent") != current
                            .getInteger("FPPdfsnum"))) {
                low = minLowDFSnum(node, current);
                node.setInteger("FPPlow", low);
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
     *         <code>false</code> otherwise.
     */
    private boolean biconnect() {
        if (rootIsArticulationPoint())
            return false;
        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
            Node current = i.next();
            if (current != root) {
                for (Iterator<Node> j = current.getNeighborsIterator(); j
                        .hasNext();) {
                    Node neighbour = j.next();
                    if ((neighbour.getInteger("FPPlow") >= current
                            .getInteger("FPPdfsnum"))
                            && (neighbour.getInteger("FPPparent") == current
                                    .getInteger("FPPdfsnum"))) {
                        if (GUIMode) {
                            // Connect.setNodeColor(neighbour, COLOR);
                        }
                        separationNode = neighbour;
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
            if (neighbour.getInteger("FPPparent") == 0) {
                children++;
                if (children == 2) {
                    separationNode = neighbour;
                    if (GUIMode) {
                        // Connect.setNodeColor(neighbour, COLOR);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return <code>true</code>, if the <code>Graph</code> is biconnected,
     *         <code>false</code> otherwise.
     */
    public boolean isBiconnected() {
        return isBiconnect;
    }

    /**
     * Returns null, if the graph is NOT connected.
     * 
     * @return a separationNode otherwise null
     */
    public Node getSeparationNode() {
        return separationNode;
    }

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult result = new DefaultAlgorithmResult();
        String text = "The graph is " + (isBiconnected() ? "" : "not ")
                + "biconnected.";
        result.setComponentForJDialog(text);
        result.addToResult("biconnected", isBiconnected());
        return result;
    }

}
