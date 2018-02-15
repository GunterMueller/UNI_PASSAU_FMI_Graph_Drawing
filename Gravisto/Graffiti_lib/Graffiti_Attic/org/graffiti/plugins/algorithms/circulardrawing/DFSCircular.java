package org.graffiti.plugins.algorithms.circulardrawing;

import java.awt.Color;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * An modified implementation of the DFSCircular algorithm.
 * 
 * @author demirci Created on Jan 21, 2005
 */
public class DFSCircular extends AbstractAlgorithm {

    private List longestPathNodes = new ArrayList();

    private List remainingNodes = new ArrayList();

    int dfsPathL = 0;

    /**
     * @see org.graffiti.plugins.algorithms.circulardrawing.TestBiconnection
     */
    private static TestBiconnection biconTest;

    /** The number of the crossing in the current layout */
    int numberOfCrossing = 0;

    /** Konstruktur */
    public DFSCircular() {
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "DFSCircular";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Can't run DFSCircular.");
    }

    /**
     * @return a permutated list
     */
    private List permutateList() {
        List permutatedList = new ArrayList();
        int length = remainingNodes.size();
        for (int i = 0; i < length; i++) {
            int size = remainingNodes.size();
            double zufall = Math.random();
            Float pos = new Float((size - 1) * zufall);
            int position = Math.round(pos.floatValue());
            Node n = (Node) remainingNodes.remove(position);
            permutatedList.add(n);
        }
        return permutatedList;
    }

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult aresult = new DefaultAlgorithmResult();
        aresult.addToResult("circularLayout", this.graph);
        aresult.addToResult("longestPath", this.longestPathNodes);
        aresult.addToResult("dfsPathL", new Integer(dfsPathL));
        aresult.addToResult("numberOfCrossing", new Integer(numberOfCrossing));
        return aresult;
    }

    public int getNumberOfCrossing() {
        return numberOfCrossing;
    }

    private void setNumberOfCrossing(int cross) {
        numberOfCrossing = cross;
    }

    /**
     * @param node
     *            a remaining node.
     * @see org.graffiti.graph.Node
     * @return a next to position of a neighbor node on the circle.
     */
    private int calculatePositionII(Node node) {
        int pos = -1;
        int size = longestPathNodes.size();
        Object[] neighbors = node.getNeighbors().toArray();
        if (pos == -1) {
            for (int i = 0; i < neighbors.length; i++) {
                Node v1 = (Node) neighbors[i];
                if (longestPathNodes.indexOf(v1) != -1) {
                    pos = (longestPathNodes.indexOf(v1) + 1) % size;
                    // System.out.println(" Knoten " +
                    // node.getInteger("dfsParam.dfsNum") +
                    // " wurde an den Knoten " +
                    // v1.getInteger("dfsParam.dfsNum") + " platziert");
                    break;
                }
            }
        }
        return pos;
    }

    /**
     * Defined a random position for a remaining node which has'nt a neihbor
     * node on the circle.
     * 
     * @see org.graffiti.graph.Node
     * @return a random position on the circle.
     */
    private int calculatePositionIIIRandom() {
        System.out.println("selected Algorithm is DFSCircular");
        int n = longestPathNodes.size();
        double zufall = Math.random();
        Float floatPos = new Float(n * zufall);
        int pos = Math.round(floatPos.floatValue());
        return pos;
    }

    /**
     * @param node
     *            any remaining node
     * @see org.graffiti.graph.Node
     * @return node position in the longetsPath.
     */
    private int calculatePosition(Node node) {

        int size = longestPathNodes.size();
        int position = -1;
        Object[] neighbors = node.getNeighbors().toArray();

        // places node between two neighbors
        for (int i = 0; i < neighbors.length - 1; i++) {
            Node v1 = (Node) neighbors[i];

            if (longestPathNodes.indexOf(v1) != -1) {
                // System.out.println("v1 ist " +
                // v1.getInteger("dfsParam.dfsNum"));
                // System.out.println("v1 position ist " +
                // orgGraphNodesPath.indexOf(v1));
                for (int j = i + 1; j < neighbors.length; j++) {
                    Node v2 = (Node) neighbors[j];
                    if (longestPathNodes.indexOf(v2) != -1) {
                        // System.out.println("v2 ist " +
                        // v2.getInteger("dfsParam.dfsNum"));
                        // System.out.println("v2 position ist " +
                        // orgGraphNodesPath.indexOf(v2));
                        int modPos = (longestPathNodes.indexOf(v1) - longestPathNodes
                                .indexOf(v2))
                                % (size);
                        if (modPos < 0) {
                            modPos = modPos + size;
                        }
                        // System.out.println("modPos " + modPos);
                        int modPos2 = (longestPathNodes.indexOf(v2) - longestPathNodes
                                .indexOf(v1))
                                % (size);

                        if (modPos2 < 0) {
                            modPos2 = modPos2 + size;
                        }
                        // System.out.println("modPos2 " + modPos2);
                        if (modPos == 1) {
                            position = longestPathNodes.indexOf(v1);
                            // System.out.println("position ist " + position);
                            break;
                        }

                        else if (modPos2 == 1) {
                            position = longestPathNodes.indexOf(v2);
                            // System.out.println("position ist " + position);
                            break;
                        } else {
                            ;
                        }
                    }
                }
            }
            if (position != -1) {
                break;
            }
        }

        if (position == -1) {
            position = calculatePositionII(node);
        }

        // places the node at the random position
        if (position == -1) {
            position = calculatePositionIIIRandom();
        }

        return position;
    }

    /**
     * place the remaining nodes in the longestPath.
     */
    private void placeRemainingNodesIntoEmbeddingCircle() {
        Object[] remNodes = remainingNodes.toArray();
        for (int k = 0; k < remNodes.length; k++) {
            Node remNode = (Node) remNodes[k];
            // System.out.println("DFSCircular remNode ist " +
            // remNode.getInteger("dfsParam.dfsNum")
            // + "/" + remNode.getInteger("node.id"));
            int position = calculatePosition(remNode);
            if (position != -1) {
                longestPathNodes.add(position, remNode);
            } else {
                System.out
                        .println("dieser Knoten hat keine benachbarte Nachbarn in Kreis");
            }
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {

        long dfsTime = System.currentTimeMillis();

        CircularLayout layout = new CircularLayout();
        CircularConst circularConst = new CircularConst();
        remainingNodes = graph.getNodes();
        Iterator nodeIt = graph.getNodesIterator();
        for (int i = 1; nodeIt.hasNext(); i++) {
            Node node = (Node) nodeIt.next();
            node.setInteger("node.id", i);
            node.setInteger("longestPath.position", -1);
            if (circularConst.getRuntime() == 0) {
                layout.setNodeLabel(node, new Integer(i).toString());
                node.setInteger("graphics.sortId", node.getInDegree());
            }
        }

        Iterator egIt = graph.getEdgesIterator();
        for (int i = 1; egIt.hasNext(); i++) {
            Edge eg = (Edge) egIt.next();
            eg.setInteger("label.label", i);
            /*
             * if (circularConst.getRuntime() == 0) { layout.setLabel(eg, new
             * Integer(i)); }
             */
        }

        Node startNode = graph.getNodes().get(0);
        LongestPath lp = new LongestPath(graph);
        lp.attach(graph);
        lp.setSourceNode(startNode);
        // System.out.println("Longest path search started! ");
        lp.execute();
        // System.out.println("LongestPath was found! ");
        longestPathNodes = (List) lp.getResult().getResult().get("longestPath");

        Hashtable levels = (Hashtable) lp.getResult().getResult().get("levels");
        int maxLevelSize = ((Integer) lp.getResult().getResult()
                .get("maxLevel")).intValue();
        Hashtable orgGraphLevels = new Hashtable();
        lp.reset();

        dfsPathL = longestPathNodes.size();
        System.out.println("Die L�nge des gefundenen Weges ist " + dfsPathL
                + "/" + graph.getNumberOfNodes());
        Iterator longestPathIt = longestPathNodes.iterator();
        while (longestPathIt.hasNext()) {
            Node node = (Node) longestPathIt.next();
            node.setBoolean("in.lpath", true);
            remainingNodes.remove(node);
            if (circularConst.getDfsTree() == 1) {
                layout.setNodeColor(node, Color.GREEN);
            }
        }
        /*
         * System.out.print("{"); for (int i = 0; i < longestPathNodes.size();
         * i++) { Node n = (Node)longestPathNodes.get(i);
         * System.out.print(n.getInteger("dfsParam.dfsNum")); System.out.print("
         * , "); } System.out.println("}");
         */
        System.out
                .println("********************************************************************************");

        Iterator remNodesIt = remainingNodes.iterator();
        while (remNodesIt.hasNext()) {
            Node remNode = (Node) remNodesIt.next();
            remNode.setBoolean("in.lpath", false);
            remNode.setBoolean("in.path", false);
            remNode.setBoolean("left.path", false);
            remNode.setBoolean("right.path", false);
        }

        // activated analyse of the dfstree.
        if (circularConst.getDfsTree() == 1) {
            CircularLayout circularLayout = new CircularLayout();
            List treeEdges = (List) lp.getResult().getResult().get("treeEdges");
            Iterator edges = graph.getEdgesIterator();
            while (edges.hasNext()) {
                Edge e = (Edge) edges.next();
                if (!treeEdges.contains(e)) {
                    circularLayout.setEdgeColor(e, Color.WHITE);
                }
            }
            CircularLayout dfsTree = new CircularLayout();
            for (int t = 0; t < levels.size(); t++) {
                List level = (List) levels.get(new Integer(t));
                List orgLevel = new ArrayList();
                int levelSize = level.size();
                // System.out.print("level " + t + " Knoten sind [" );
                orgGraphLevels.put(new Integer(t), level);
                // System.out.println("]");
            }

            dfsTree.treeLayout(orgGraphLevels, maxLevelSize);
        } else {
            if (CircularConst.PERMUTATION_REM_NODES == 1) {
                remainingNodes = permutateList();
            }
            placeRemainingNodesIntoEmbeddingCircle();
            Iterator it = longestPathNodes.iterator();
            for (int x = 0; it.hasNext(); x++) {
                Node node = (Node) it.next();
                node.setInteger("longestPath.position", x);
            }
            if (circularConst.getRuntime() == 0) {
                CountAllCrossing allCrossing = new CountAllCrossing(
                        longestPathNodes);
                ClockwiseEdgeOrdering cweo = new ClockwiseEdgeOrdering(graph,
                        longestPathNodes);
                List cwoEdgeOrdering = cweo.edgeOrdering();
                int actualCrossing = allCrossing
                        .calculateNumberOfCrossing(cwoEdgeOrdering);

                // System.out.println("actualCrossing " + actualCrossing);
                setNumberOfCrossing(actualCrossing);
                System.out.println("Number of corssing after DFScircular: "
                        + actualCrossing);
            }

            Time dfsTimer = new Time(System.currentTimeMillis() - dfsTime);
            // System.out.println("DfsCircular Zeitbedarf " + dfsTimer.getTime()
            // + " [ms]");
            // step 17
            // Place the resulting longest path onto embedding circle
            if (CircularConst.TEST == 0) {
                CircularLayout layout2 = new CircularLayout(longestPathNodes);
                layout2.embeddingPathOnToCircle();
            }
        }
        graph.getListenerManager().transactionFinished(this);
        // reset();

    } // End of execute

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        longestPathNodes = new ArrayList();
        remainingNodes = new ArrayList();
        numberOfCrossing = 0;
        dfsPathL = 0;
        // graph = null;
    }
}
