package quoggles.auxiliary;

import org.graffiti.graph.Node;

/**
 * Pair of a <code>Node</code> and a <code>List</code> (indicating 
 * output paths).
 */
public class NodeListPair {
    
    private Node node;
    
    private int[] outputPaths;        

    public NodeListPair(Object n) {
        node = (Node)n;
        outputPaths = new int[]{0,0,0,0};
    }

    public NodeListPair(Node n) {
        node = n;
        outputPaths = new int[]{0,0,0,0};
    }

    public NodeListPair(Node n, int[] op) {
        node = n;
        outputPaths = op;
    }

    public NodeListPair(Object n, int[] op) {
        node = (Node)n;
        outputPaths = op;
    }

    /**
     * @return node saved in this class
     */
    public Node getNode() {
        return node;
    }

    /**
     * @param node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * @return output paths int array saved in this class
     */
    public int[] getOutputPaths() {
        return outputPaths;
    }

    /**
     * @param onOutputPath
     */
    public void setOutputPaths(int[] op) {
        outputPaths = op;
    }

}

