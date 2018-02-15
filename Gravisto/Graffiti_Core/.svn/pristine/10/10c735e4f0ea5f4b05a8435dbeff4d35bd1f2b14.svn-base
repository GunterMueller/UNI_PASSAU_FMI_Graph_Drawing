/*
 * Created on 18.05.2004
 */

package org.graffiti.plugins.algorithms.clustering;

// import ArrayHeapPriorityQueue;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;

/**
 * @author Markus K�ser
 * 
 */
public class TestAlgorithm extends AbstractAlgorithm {
    // private static final int numberOfNodes = 9;
    // boolean directed = true;
    // private int[] sources = {0,0,0,1,1,2,2,3,4,4,5,6,6,7};
    // private int[] targets = {1,2,4,4,5,3,6,8,5,7,8,7,8,7};
    // private int[] caps = {3,4,5,2,4,6,2,3,5,4,6,2,3,2};
    // private Node[] nodes = new Node[numberOfNodes];
    // private Edge[] edges = new Edge[sources.length];

    private static final Logger logger = Logger.getLogger(TestAlgorithm.class
            .getName());

    private static final String BFS_NUMBER = ClusteringSupportAlgorithms.BASE
            + "bfsNummer";

    private FlowNetworkSupportAlgorithms nsa = FlowNetworkSupportAlgorithms
            .getFlowNetworkSupportAlgorithms();

    private ClusteringSupportAlgorithms csa = ClusteringSupportAlgorithms
            .getClusteringSupportAlgorithms();

    private boolean makeUndirected;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        IsolatingCutClusteringAlgorithm icca = new IsolatingCutClusteringAlgorithm();
        int clusters = 5;
        icca.setAll(graph, clusters, false, true, true, true);
        icca.execute();
    }

    /**
     * 
     */
    public TestAlgorithm() {
        super();
        reset();
        // NodeParameter param1 = new NodeParameter("Node1","bla");
        // AttributeParameter param1 = new
        // AttributeParameter("AttribParam","bla");
        // ObjectParameter param1 = new ObjectParameter("objectparam","bla");

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return " clustering test algorithm ";
    }

    // leafes in a tree are all nodes with degree 1
    private Collection getLeafes() {
        Collection leaves = new LinkedList();
        Node node;
        for (Iterator nodeIt = graph.getNodesIterator(); nodeIt.hasNext();) {
            node = (Node) nodeIt.next();
            if (node.getEdges().size() == 1) {
                leaves.add(node);
            }
        }
        return leaves;
    }

    private void bfs(Node startNode) {
        LinkedList queue = new LinkedList();
        queue.addLast(startNode);
        setBfsNumber(startNode, 0);

        while (!queue.isEmpty()) {
            Node sourceNode = (Node) queue.removeFirst();
            Collection edges = sourceNode.getEdges();

            for (Iterator edgeIt = edges.iterator(); edgeIt.hasNext();) {
                Edge tempEdge = (Edge) edgeIt.next();
                Node targetNode = nsa.getOtherEdgeNode(sourceNode, tempEdge);

                // if the target is not yet marked and the edge not to be igored
                if (getBfsNumber(targetNode) != -1) {
                    queue.addLast(targetNode);
                    setBfsNumber(targetNode, getBfsNumber(sourceNode) + 1);
                }
            }
        }
    }

    private void removeBfsNumber(Node node) {
        try {
            node.removeAttribute(BFS_NUMBER);
        } catch (AttributeNotFoundException anfe) {
        }
    }

    private void setBfsNumber(Node node, int number) {
        removeBfsNumber(node);
        node.setInteger(BFS_NUMBER, number);
    }

    private int getBfsNumber(Node node) {
        int number = -1;
        try {
            number = node.getInteger(BFS_NUMBER);
        } catch (AttributeNotFoundException anfe) {
        }
        return number;
    }

    private void testHeap() {
        Integer in;
        Collection integers = new LinkedList();
        integers.add(new Integer(5));
        integers.add(new Integer(6));
        integers.add(new Integer(4));
        integers.add(new Integer(8));
        integers.add(new Integer(0));
        integers.add(new Integer(2));
        integers.add(new Integer(3));
        integers.add(new Integer(9));
        integers.add(new Integer(1));
        integers.add(new Integer(7));

        class IntComp implements Comparator {

            public int compare(Object o1, Object o2) {
                Integer i1 = (Integer) o1;
                Integer i2 = (Integer) o2;
                return (i1.intValue() - i2.intValue());
            }

        }
        Comparator comp = new IntComp();
        // ArrayHeapPriorityQueue heap = new
        // ArrayHeapPriorityQueue(integers,5,comp);
        // while(!heap.isEmpty()){
        // System.out.println(heap.deleteMin());
        // }
    }

    private void testColoringOfClusters() {
        Collection[] clusters = new Collection[16];
        clusters[0] = convertToCollection(nsa.getNodesWithLabel(graph, "a"));
        clusters[1] = convertToCollection(nsa.getNodesWithLabel(graph, "b"));
        clusters[2] = convertToCollection(nsa.getNodesWithLabel(graph, "c"));
        clusters[3] = convertToCollection(nsa.getNodesWithLabel(graph, "d"));
        clusters[4] = convertToCollection(nsa.getNodesWithLabel(graph, "e"));
        clusters[5] = convertToCollection(nsa.getNodesWithLabel(graph, "f"));
        clusters[6] = convertToCollection(nsa.getNodesWithLabel(graph, "g"));
        clusters[7] = convertToCollection(nsa.getNodesWithLabel(graph, "h"));
        clusters[8] = convertToCollection(nsa.getNodesWithLabel(graph, "i"));
        clusters[9] = convertToCollection(nsa.getNodesWithLabel(graph, "j"));
        clusters[10] = convertToCollection(nsa.getNodesWithLabel(graph, "k"));
        clusters[11] = convertToCollection(nsa.getNodesWithLabel(graph, "l"));
        clusters[12] = convertToCollection(nsa.getNodesWithLabel(graph, "m"));
        clusters[13] = convertToCollection(nsa.getNodesWithLabel(graph, "n"));
        clusters[14] = convertToCollection(nsa.getNodesWithLabel(graph, "o"));
        clusters[15] = convertToCollection(nsa.getNodesWithLabel(graph, "p"));
        csa.colorClusters(clusters);
    }

    private Collection convertToCollection(Object[] objects) {
        Collection list = new LinkedList();
        for (int i = 0; i < objects.length; i++) {
            list.add(objects[i]);
        }
        return list;
    }

    private void roundFlow() {
        for (Iterator edgeIt = graph.getEdgesIterator(); edgeIt.hasNext();) {
            Edge edge = (Edge) edgeIt.next();
            double flow = nsa.getFlow(edge);
            double temp = flow / 1000000000;
            temp = temp * 1000000000;
            System.out.println(edge.toString() + "  old Flow: " + flow
                    + ",  rounded Flow " + temp);
        }
    }

    // private void createGraph(){
    // graph = new AdjListGraph();
    // for (int i = 0; i < numberOfNodes; i++){
    // nodes[i] = graph.addNode();
    // nodes[i].setString("label.label","node_"+i);
    // }
    //             
    // for (int i = 0; i < sources.length; i++){
    // edges[i] = newEdge(nodes[sources[i]],nodes[targets[i]],caps[i]);
    // edges[i].setString("label.label","edge_"+i);
    // }
    // }
    //
    // private Edge newEdge(Node n1, Node n2, int cap){
    // Edge tempEdge = graph.addEdge(n1,n2,this.directed);
    // LabelAttribute tempAttr = new
    // LabelAttribute(FordFulkersonAlgorithm.CAPACITY, cap+"");
    // tempEdge.addAttribute(tempAttr,"");
    // return tempEdge;
    // }

    private void makeUndirected() {
        for (Iterator it = graph.getEdgesIterator(); it.hasNext();) {
            ((Edge) (it.next())).setDirected(false);
        }
    }

    private void makeCapacities() {
        for (Iterator it = graph.getEdgesIterator(); it.hasNext();) {
            Edge tempEdge = ((Edge) (it.next()));
            try {
                LabelAttribute tempAt = new LabelAttribute(
                        new FordFulkersonAlgorithm().capacity, 4 + "");
                tempEdge.addAttribute(tempAt, "");
            } catch (Exception e) {
            }
        }
    }
}
/*
 * public void reset(){ //BooleanParameter param1 = new
 * BooleanParameter(false,"make undirected","if set, makes graph undirected,
 * else makes graph directed."); //parameters = new Parameter[1];
 * //parameters[0] = param1; }
 * 
 * public void setAlgorithmParameters(Parameter<?>[] params){
 * super.setParameters(params); makeUndirected =
 * ((BooleanParameter)parameters[0]).getBoolean().booleanValue(); }
 */
