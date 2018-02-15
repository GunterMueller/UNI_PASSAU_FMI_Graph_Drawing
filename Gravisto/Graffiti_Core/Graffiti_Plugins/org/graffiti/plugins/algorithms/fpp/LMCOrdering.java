package org.graffiti.plugins.algorithms.fpp;

/**
 * @author Le Pham Hai Dang
 */

import java.util.LinkedList;
import java.util.Stack;

import org.graffiti.graph.Node;

/**
 * Transform the Canonical Ordering into the leftmost canonical (lmc-) ordering.
 */
public class LMCOrdering {

    // ~ Instance fields
    // ========================================================
    private static String PATH = "FPPnumber";

    private OrderNode[] canonicalOrder;

    private OrderNode[] leftmostOrdering;

    private LinkedList<Integer>[] vertexNumberList;

    private Stack<Node> outerfaceStack;

    private Node v2;

    // ~ Constructors
    // ===========================================================
    /**
     * @param reverseInduction
     *            <code>OrderNode[]</code>
     * @param verticesNumber
     *            <code>int</code>
     * @param calculateorder
     *            <code>CalculateOrder</code>
     */
    @SuppressWarnings("unchecked")
    public LMCOrdering(OrderNode[] reverseInduction, int verticesNumber,
            CalculateOrder calculateorder) {
        canonicalOrder = canonicalOrder(reverseInduction);
        vertexNumberList = new LinkedList[verticesNumber];
        outerfaceStack = new Stack<Node>();
        leftmostOrdering = new OrderNode[reverseInduction.length];
        v2 = calculateorder.getVertex2();
        calculateVertexNumberList();
        calculateLMC();
    }

    // ~ Methods
    // ================================================================
    /**
     * Transform the reverse ordering into the Canonical Ordering
     * 
     * @param reverseInduction
     *            <code>OrderNode[]</code>
     */
    private OrderNode[] canonicalOrder(OrderNode[] reverseInduction) {
        OrderNode[] order = new OrderNode[reverseInduction.length];
        int index = 0;
        for (int i = reverseInduction.length - 1; i > -1; i--) {
            order[index] = reverseInduction[i];
            index++;
        }
        return order;
    }

    /**
     * calculateVertexNumberList contains a list, which is sorted after the
     * canonical order
     */
    private void calculateVertexNumberList() {
        int number;
        String name;
        Node node;
        for (int i = 0; i < canonicalOrder.length; i++) {
            node = canonicalOrder[i].getRightvertex();
            if (node != null) {
                name = node.getString(PATH);
                number = StringToInt(name);

                Integer value = new Integer(i);
                if (vertexNumberList[number] == null) {
                    vertexNumberList[number] = new LinkedList<Integer>();
                    vertexNumberList[number].add(value);
                } else {
                    vertexNumberList[number].add(value);
                }
            }
        }
    }

    /**
     * This method calculates a canonical ordering into the leftmost canonical
     * ordering
     */
    private void calculateLMC() {
        Node node;
        int value, nodeIndex, leftIndex = 2;
        LinkedList<Integer> list;

        outerfaceStack.push(v2);
        while (!outerfaceStack.isEmpty()) {
            node = outerfaceStack.peek();
            nodeIndex = getNodeNumber(node);
            list = vertexNumberList[nodeIndex];
            if (list != null && !list.isEmpty()) {
                value = (list.getFirst()).intValue();
                if (canonicalOrder[value].getHandle()) {
                    Node[] handle = (canonicalOrder[value].getOrderList()
                            .toArray(new Node[0]));
                    for (int i = handle.length - 1; i > -1; i--) {
                        outerfaceStack.push(handle[i]);
                    }
                } else {
                    outerfaceStack.push(canonicalOrder[value].getOrderNode());
                }
                leftmostOrdering[leftIndex] = canonicalOrder[value];
                leftIndex++;
                list.removeFirst();
            } else {
                outerfaceStack.pop();
            }
        }
        leftmostOrdering[0] = canonicalOrder[0];
        leftmostOrdering[1] = canonicalOrder[1];
    }

    /** @return leftmostOrdering <code>OrderNode[]</code> */
    public OrderNode[] getLMCOrdering() {
        return leftmostOrdering;
    }

    /**
     * Return the dfsnum of the node
     * 
     * @param node
     *            <code>Node</code>
     * @return the dfsnum
     */
    public int getNodeNumber(Node node) {
        return StringToInt(node.getString(PATH));
    }

    /**
     * @param name
     *            <code>String</code>
     * @return name <code>int</code>
     */
    public int StringToInt(String name) {
        try {
            int value = new Integer(name).intValue();
            return value;
        } catch (Exception e) {
            System.out.println("LMCOrdering: Casting error: String to int ");
            return -1;
        }
    }

}
