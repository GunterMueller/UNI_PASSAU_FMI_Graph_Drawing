package quoggles.auxiliary;

import org.graffiti.graph.Node;

/**
 * Pair of a <code>Node</code> and an <code>Object</code> (indicating 
 * the object that is tested on the current output path).
 */
public class NodeObjectPair {
    
    private Node node;

    private Object testObject;
    
    
    public NodeObjectPair(Node n) {
        node = n;
        testObject = null;
    }

    public NodeObjectPair(Node n, Object o) {
        node = n;
        testObject = o;
    }

    public NodeObjectPair(Object n) {
        node = (Node)n;
        testObject = null;
    }

    public NodeObjectPair(Object n, Object o) {
        node = (Node)n;
        testObject = o;
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
     * @return object saved in this class; tested object
     */
    public Object getTestObject() {
        return testObject;
    }

    /**
     * @param testObject
     */
    public void setTestObject(Object testObject) {
        this.testObject = testObject;
    }

}

