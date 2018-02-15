package org.graffiti.plugins.algorithms.fas;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;

public class DeleteEdgesWithMaximumCircuitNumber {

    private HashMap<Node, Integer> nodeNumber = new HashMap<Node, Integer>();

    private HashMap<Integer, Node> numberNode = new HashMap<Integer, Node>();

    private HashMap<Edge, Integer> edgeNumber = new HashMap<Edge, Integer>();

    private HashMap<Integer, Edge> numberEdge = new HashMap<Integer, Edge>();

    /**
     * Array, where all current nodes are saved
     */
    private int[] p;

    /**
     * Matrix to know, which paths are processed
     */
    private int[][] h;

    /**
     * Value for the current node position in p[]
     */
    private int k;

    /**
     * Adjacent-list of g as adjacent-matrix
     */
    private int[][] g;

    /**
     * The given graph
     */
    Graph graph;

    /**
     * The number of circuits at the end of the algorithm.
     */
    private int circuitNumber;

    /**
     * Array with edges
     */
    private int[] circuitsPerEdge;

    private Edge largestNumberEdge;

    /**
     * 
     * @param graph
     */
    public DeleteEdgesWithMaximumCircuitNumber(Graph graph) {

        this.graph = graph;
        this.circuitsPerEdge = new int[graph.getEdges().size()];

        g = createAdjacentList(graph);
        circuitNumber = 0;

    }

    /**
     * Creates the Adjacent-List in an Array with size NxN (like in paper of
     * Tiernan, 1970).
     * 
     * @param g
     *            The original graph
     * @return The adjacent-list - "Array"
     */
    private int[][] createAdjacentList(Graph g) {

        createNodeIndex();
        createEdgeIndex();

        int[][] adjList = new int[g.getNumberOfNodes()][g.getNumberOfNodes()];

        for (int i = 0; i < adjList.length; i++) {

            for (int j = 0; j < adjList[1].length; j++) {
                adjList[i][j] = -1;
            }
        }
        this.circuitsPerEdge = new int[graph.getEdges().size()];
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();

            Iterator<Edge> edgesOutOfNodeIt = n.getDirectedOutEdgesIterator();

            int i = 0;
            while (edgesOutOfNodeIt.hasNext()) {

                Edge e = edgesOutOfNodeIt.next();
                int numberOfEdge = edgeToNumber(e);

                circuitsPerEdge[numberOfEdge] = 0;

                adjList[nodeToNumber(n)][i] = nodeToNumber(e.getTarget());
                i++;
            }
        }
        return adjList;
    }

    // /**
    // * Output of the Adjacent-List
    // */
    // private void printAdjacentListOfGraph()
    // {
    // System.out.println("G:");
    // for (int i = 0; i < g.length; i++)
    // {
    // for (int j = 0; j < g.length; j++)
    // {
    // if (g[i][j] >= 0)
    // {
    // System.out.print(g[i][j]);
    //
    // }
    // else
    // {
    // // ;-)
    // System.out.print("X");
    // }
    // System.out.print(" ");
    // }
    // System.out.println();
    // }
    // System.out.println();
    // }

    public void removeMaximumCircuitsEdges() {
        while (getCircuits()) {
            if (largestNumberEdge != null) {
                // The edge with the maximal related circuits
                print(largestNumberEdge);
                graph.deleteEdge(largestNumberEdge);
                int eNumber = edgeToNumber(largestNumberEdge);
                edgeNumber.remove(largestNumberEdge);
                numberEdge.remove(eNumber);

            }
        }
        System.out.println("#circuits: " + circuitNumber);
    }

    public boolean getCircuits() {
        initialize();

        // the smallest node in the circuits to calculate
        for (int i = 0; i < g.length; i++) {
            // smallest node in circuits
            p[0] = i;
            k = 0;
            clear(h);

            while (p[0] != -1) {
                pathExtension();
                circuitConfirmation();
                // node at p[0] is found in all circuits
                if (nodeClosure()) {
                    break;
                }
            }
        }
        setExtremeValues();
        if (largestNumberEdge != null)
            return true;
        return false;
    }

    /**
     * Initializes the required data structures for algorithm.
     * 
     */
    private void initialize() {

        // createNodeIndex();
        // createEdgeIndex();
        g = createAdjacentList(graph);

        // System.out
        // .println("Neuer Graph mit |E|:" + graph.getEdges().size() + " ");

        p = new int[g.length];
        for (int i = 0; i < p.length; i++) {
            p[i] = -1;
        }

        h = new int[g.length][g.length];
        clear(h);

        k = 0;
        p[0] = 0;

    }

    private void pathExtension() {
        // System.out.println("pathExtension");

        boolean extended = true;
        while (extended) {

            extended = false;
            for (int j = 0; (j < p.length && (g[p[k]][j] != -1)); j++) {
                if ((g[p[k]][j] > p[0])
                        && ((arrayContains(p, g[p[k]][j])) == -1)
                        && (arrayContains(h[p[k]], g[p[k]][j])) == -1) {

                    // extend the path
                    k++;
                    p[k] = g[p[k - 1]][j];

                    // printP();

                    extended = true;
                    break;
                }
            }
        }
    }

    /**
     * If there exists an edge between p[k] --> p[0] then, the circuit is given
     * out, and the counter at each edge is increased.
     * 
     */
    private void circuitConfirmation() {
        // System.out.println("circuitConfirmation");
        if (arrayContains(g[p[k]], p[0]) != -1) {
            // output of the circle (all the nodes in array p[])
            for (int i = 0; i < p.length && p[i] >= 0; i++) {

                System.out.print(p[i] + " ");

                Node n1, n2;

                if (i > 0) {

                    n1 = numberToNode(p[i - 1]);
                    n2 = numberToNode(p[i]);
                } else {
                    n1 = numberToNode(p[k]);
                    n2 = numberToNode(p[0]);
                }
                Collection<Edge> c = graph.getEdges(n1, n2);
                Edge e = c.iterator().next();
                // print(e);
                int pos = edgeToNumber(e);

                // System.out.println("circuitsPerEdge.length: "
                // + circuitsPerEdge.length + ", ZugriffsPos: " + pos);
                circuitsPerEdge[pos]++;
            }
            System.out.println();

            circuitNumber++;
        }
    }

    /**
     * Returns true if all circuits are processed for the current "start"-node.
     * 
     */
    private boolean nodeClosure() {

        // if k==0, then the node at p[0] is found in all circuits
        if (k == 0)
            return true;
        else {
            // clear row p[k] of h
            for (int m = 0; m < h[p[k]].length; m++) {
                h[p[k]][m] = -1;
            }

            int m = 0;
            while (h[p[k - 1]][m] > -1) {
                m++;
            }
            h[p[k - 1]][m] = p[k];
            p[k] = -1;
            k--;

            return false;
        }

    }

    /**
     * Returns the point of the given element, if containing. Else -1.
     * 
     * @param a
     * @param i
     * @return the position of the given element, if found. Else -1.
     */
    private int arrayContains(int a[], int i) {

        for (int j = 0; (j < a.length && a[j] != -1); j++) {
            if (a[j] == i)
                return j;
        }
        return -1;

    }

    private void clear(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {

                a[i][j] = -1;

            }
        }
    }

    // private void printP()
    // {
    //
    // System.out.print("P:");
    // int i = 0;
    // while (i < p.length && p[i] >= 0)
    // {
    // System.out.print(p[i] + " ");
    // i++;
    // }
    // System.out.println();
    // }
    //
    // private void printH()
    // {
    //
    // System.out.print("H:");
    // for (int i = 0; i < h.length; i++)
    // {
    //
    // for (int j = 0; j < h[0].length; j++)
    // {
    // System.out.print(h[i][j] + " ");
    // }
    // System.out.println();
    // }
    // }

    private void setExtremeValues() {

        int max = 0;
        largestNumberEdge = null;

        for (int i = 0; i < circuitsPerEdge.length; i++) {
            // test current edge
            Edge e = numberToEdge(i);
            if (e != null) {

                // the # of related circuits
                String labelString = circuitsPerEdge[i] + "";

                EdgeLabelAttribute edgeLabel = new EdgeLabelAttribute("label");
                // Check if there is an existing label
                try {
                    edgeLabel = (EdgeLabelAttribute) e.getAttributes()
                            .getAttribute("label");

                    edgeLabel.setLabel(labelString);
                } catch (AttributeNotFoundException ex) {
                    edgeLabel.setLabel(labelString);
                    e.getAttributes().add(edgeLabel);
                }

                if (circuitsPerEdge[i] > max) {
                    max = circuitsPerEdge[i];
                    largestNumberEdge = e;
                }

            }

            if (largestNumberEdge != null) {
                // EdgeGraphicAttribute ega =
                // (EdgeGraphicAttribute)largestNumberEdge
                // .getAttribute("graphics");
                //
                // ega.getFramecolor().setColor(Color.GREEN);
                // ega.getFillcolor().setColor(Color.GREEN);
            }
        }
        System.out.print("#related circuits: " + max + " ");
    }

    private void print(Edge e) {

        System.out.println("(" + nodeToNumber(e.getSource()) + "-->"
                + nodeToNumber(e.getTarget()) + ")");

    }

    private void createNodeIndex() {
        this.nodeNumber.clear();
        this.numberNode.clear();
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        int i = 0;
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            this.nodeNumber.put(n, new Integer(i));
            this.numberNode.put(new Integer(i), n);
            i++;
        }
    }

    private void createEdgeIndex() {
        this.edgeNumber.clear();
        this.numberEdge.clear();
        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        int i = 0;
        while (edgesIt.hasNext()) {
            Edge e = edgesIt.next();
            this.edgeNumber.put(e, new Integer(i));
            this.numberEdge.put(new Integer(i), e);
            i++;
        }
    }

    public int nodeToNumber(Node n) {
        return this.nodeNumber.get(n).intValue();
    }

    public Node numberToNode(int x) {
        return this.numberNode.get(new Integer(x));
    }

    public int edgeToNumber(Edge e) {
        return this.edgeNumber.get(e).intValue();
    }

    public Edge numberToEdge(int x) {
        return this.numberEdge.get(new Integer(x));
    }

    // private void labelNodes()
    // {
    //
    // Iterator<Node> nodesIt = this.graph.getNodesIterator();
    // while (nodesIt.hasNext())
    // {
    // Node nodeToLabel = nodesIt.next();
    // String labelString = (this.nodeNumber.get(nodeToLabel) + "");
    //
    // NodeLabelAttribute nodeLabel = new NodeLabelAttribute("label");
    // try
    // {
    // nodeLabel = (NodeLabelAttribute)nodeToLabel.getAttributes()
    // .getAttribute("label");
    //
    // nodeLabel.setLabel(labelString);
    // }
    // catch (AttributeNotFoundException e)
    // {
    // nodeLabel.setLabel(labelString);
    // nodeToLabel.getAttributes().add(nodeLabel);
    // }
    // }
    // }
}
