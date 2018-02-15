package quoggles.auxiliary;

import org.graffiti.graph.Node;

/**
 * Pair of a <code>Node</code> and a <code>boolean</code> (indicating 
 * if the node is on an output path).
 */
public class NodeBoolPair {
        
    private Node node;
        
    private boolean onOutputPaths;        

    public NodeBoolPair(Object n) {
        node = (Node)n;
        onOutputPaths = false;
    }

    public NodeBoolPair(Node n) {
        node = n;
        onOutputPaths = false;
    }

    public NodeBoolPair(Node n, boolean oop) {
        node = n;
        onOutputPaths = oop;
    }

    public NodeBoolPair(Object n, boolean oop) {
        node = (Node)n;
        onOutputPaths = oop;
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
     * @return boolean value saved in this class; indicates whether or not
     * the node is on an output path
     */
    public boolean isOnOutputPath() {
        return onOutputPaths;
    }

    /**
     * @param onOutputPath
     */
    public void setOnOutputPaths(boolean oop) {
        onOutputPaths = oop;
    }

}

