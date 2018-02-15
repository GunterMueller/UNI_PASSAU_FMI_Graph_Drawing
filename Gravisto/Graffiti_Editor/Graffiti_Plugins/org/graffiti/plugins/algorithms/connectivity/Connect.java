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

public class Connect extends AbstractAlgorithm implements CalculatingAlgorithm {
    // ~ Instance fields
    // ========================================================
    private boolean isConnect;

    private Node independentNode = null;

    private Node sourcenode = null;

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
        startConnect();
        GUIMode = false;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Test connectivity";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
    }

    /** Executes the test without GUI */
    public void testConnect() {
        if (!finished) {
            startConnect();
        }
    }

    /** Start the connectivity-test */
    private void startConnect() {
        graph.getListenerManager().transactionStarted(this);
        isConnect = true;
        if (!graph.isEmpty()) {
            addAttributes();
            sourcenode = graph.getNodes().get(0);
            dfs(sourcenode);
            for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
                Node current = i.next();
                if (!current.getBoolean("FPPvisited")) {
                    independentNode = current;
                    if (GUIMode) {
                        // Connect.setNodeColor(current, COLOR);
                    }
                    isConnect = false;
                }
            }
            deleteAttributes();
        }
        if (GUIMode) {
            // System.out.println("\t Connected: " + isConnect);
        }
        graph.getListenerManager().transactionFinished(this);
        finished = true;

    }

    /** Add to the graph the attribute <code>visited</code> */
    private void addAttributes() {
        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
            Node current = i.next();
            if (GUIMode) {
                // Connect.setNodeColor(current,
                // GraphicAttributeConstants.DEFAULT_NODE_FILLCOLOR);
            }
            current.addBoolean("", "FPPvisited", false);
        }
    }

    /** Remove the attribute <code>visited</code> from the graph */
    private void deleteAttributes() {
        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
            Node current = i.next();
            try {
                current.removeAttribute("FPPvisited");
            } catch (Exception e) {
                /** */
            }
        }
    }

    /**
     * DFS method is recursiv. If a <code>Node</code> is visited, the attribute
     * <code>visited</code> will be define as <code>true</code>
     * 
     * @param node
     *            the <code>Node</code> to be visited.
     */
    private void dfs(Node node) {
        node.setBoolean("FPPvisited", true);
        for (Iterator<Node> i = node.getNeighborsIterator(); i.hasNext();) {
            Node current = i.next();
            try {
                if (!current.getBoolean("FPPvisited")) {
                    dfs(current);
                }
            } catch (Exception e) {
                /** */
            }
        }
    }

    /**
     * @return <code>true</code>, if the <code>Graph</code> is connected,
     *         <code>false</code> otherwise.
     */
    public boolean isConnected() {
        return isConnect;
    }

    /** @return one independent Node otherwise null */
    public Node getIndependentNode() {
        return independentNode;
    }

    /**
     * Sets the color of the node.
     * 
     * @param node
     *            The node to color
     * @param c
     *            The new color
     */
    /*
     * public static void setNodeColor(Node node, Color c) { ColorAttribute ca =
     * (ColorAttribute)node .getAttribute(GraphicAttributeConstants.GRAPHICS +
     * Attribute.SEPARATOR + GraphicAttributeConstants.FILLCOLOR);
     * ca.setColor(c); }
     */

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult result = new DefaultAlgorithmResult();
        String text = "The graph is " + (isConnected() ? "" : "not ")
                + "connected.";
        result.setComponentForJDialog(text);
        result.addToResult("connected", isConnected());
        return result;
    }
}
