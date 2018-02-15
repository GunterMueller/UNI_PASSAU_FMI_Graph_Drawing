package org.graffiti.plugins.algorithms.circulardrawing;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * @author demirci Created on Feb 22, 2005
 */
public class CircularPostprocessing extends AbstractAlgorithm {

    private static SortedMap nodeOrdering = new TreeMap();

    private static List longestPathNodes = new ArrayList();

    private static Map initialEdgeOrd = new HashMap();

    private static Map nodePosList = new HashMap();

    private static QuickSort quickSort;

    Integer crossAC = new Integer(0);

    Integer crossAPP = new Integer(0);

    Graph circularLayout = new AdjListGraph();

    int loopCounter = 0;

    long edgeOrderTime = 0;

    long countSingleNodeTime = 0;

    long countAllCrossTime = 0;

    long initPosListTime = 0;

    /**
     * Konstuktur.
     */
    public CircularPostprocessing() {
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "CircularPostprocessing";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.isEmpty())
            throw new PreconditionException(
                    "The graph is empty. Can't run CircularPostprocessing!");
    }

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult aresult = new DefaultAlgorithmResult();
        aresult.addToResult("crossAfterCircular", this.crossAC);
        aresult.addToResult("crossAfterPP", this.crossAPP);
        aresult.addToResult("circularLayout", this.graph);
        return aresult;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        nodeOrdering = new TreeMap();
        longestPathNodes = new ArrayList();
        initialEdgeOrd = new HashMap();
        nodePosList = new HashMap();
        crossAC = new Integer(0);
        crossAPP = new Integer(0);
        // graph = null;
    }

    /**
     * @param v1
     * @param v2
     * @return true if the nodes are next to other on the circle, false
     *         otherwise.
     */
    private boolean isNextToOther(Node v1, Node v2) {
        boolean bol = false;
        int n = longestPathNodes.size();
        int index1 = longestPathNodes.indexOf(v1);
        int index2 = longestPathNodes.indexOf(v2);
        if (index1 == (index2 - 1 + n) % n || index1 == (index2 + 1) % n) {
            bol = true;
        }
        return bol;
    }

    /**
     * @param u
     *            actual processed node
     * @return map the key is u and the value is a list of positions on the
     *         circle.
     */
    private Map initPosList(Node u) {

        long time1 = System.currentTimeMillis();
        int n = graph.getNumberOfNodes();
        Map m = new HashMap();
        // System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        // System.out.print("actual processed node is " +
        // u.getInteger("dfsParam.dfsNum"));
        // System.out.println(" has got the position " +
        // longestPathNodes.indexOf(u));

        int uPos = longestPathNodes.indexOf(u);
        List posList = new ArrayList();
        List posList1 = new ArrayList();
        List posList2 = new ArrayList();
        Collection neighbors = u.getNeighbors();
        Iterator neighborsIt = u.getNeighborsIterator();

        for (; neighborsIt.hasNext();) {

            Node v = (Node) neighborsIt.next();
            // System.out.println(" nachbar " +
            // v.getInteger("dfsParam.dfsNum"));

            int vPos = longestPathNodes.indexOf(v);
            Node v1 = (Node) longestPathNodes.get((vPos + 1) % n);
            Node v2 = (Node) longestPathNodes.get((vPos - 1 + n) % n);

            // init posList1
            if (neighbors.contains(v1)) {
                if (uPos > vPos) {
                    if (!posList1.contains(new Integer((vPos + 1) % n))) {
                        posList1.add(new Integer((vPos + 1) % n));
                    }
                } else {
                    if (!posList1.contains(new Integer(vPos))) {
                        posList1.add(new Integer(vPos));
                    }
                }
            }

            else if (neighbors.contains(v2)) {
                if (uPos > vPos) {
                    if (!posList1.contains(new Integer(vPos))) {
                        posList1.add(new Integer(vPos));
                    }
                } else {
                    if (!posList1.contains(new Integer((vPos - 1 + n) % n))) {
                        posList1.add(new Integer((vPos - 1 + n) % n));
                    }
                }
            }
            if (posList1.size() > 0) {
                posList = posList1;
            }

            // Initialize the posList2
            else {
                if (vPos > uPos) {
                    // u lie next to v.
                    if (isNextToOther(u, v)) {
                        // Compare the degree of the nodes.
                        // If degree of v lesser of the degree of u, move u.
                        if (v.getInDegree() <= u.getInDegree()) {
                            // move in clockwise
                            if (vPos == uPos + 1) {
                                Integer pos = new Integer(vPos);
                                if (!posList2.contains(pos)) {
                                    posList2.add(pos);
                                }
                            }
                            // move anticlockwise
                            else if (vPos == (uPos - 1 + n) % n) {
                                Integer pos = new Integer((vPos - 1 + n) % n);
                                if (!posList2.contains(pos)) {
                                    posList2.add(pos);
                                }
                            }
                        } // end of grad abfrage
                    } // end of benachbart abfrage

                    // u lie not next to v.
                    else {
                        // Add the both position vPos-1 and vPos+1 of v in the
                        // posList2.
                        Integer pos = new Integer(vPos);
                        if (!posList2.contains(pos)) {
                            posList2.add(pos);
                        }
                        pos = new Integer((vPos - 1 + n) % n);
                        if (!posList2.contains(pos)) {
                            posList2.add(pos);
                        }
                    }
                }

                // vPos < uPos
                else {
                    // u lie next to v.
                    if (isNextToOther(u, v)) {
                        // Compare the degree of the nodes.
                        // If degree of v lesser of the degree of u, move u.
                        if (v.getInDegree() <= u.getInDegree()) {
                            // move in anticlockwise
                            if ((vPos - 1 + n) % n == uPos) {
                                Integer pos = new Integer((vPos + 1) % n);
                                if (!posList2.contains(pos)) {
                                    posList2.add(pos);
                                }
                            }
                            // move in clockwise
                            if ((vPos + 1) % n == uPos) {
                                Integer pos = new Integer(vPos);
                                if (!posList2.contains(pos)) {
                                    posList2.add(pos);
                                }
                            }
                        } // end of grad vergleich
                    } // end of benachbart abfrage

                    // u lie not next to v.
                    else {
                        // Add the both position vPos-1 and vPos+1 of v in the
                        // posList2.
                        Integer pos = new Integer((vPos + 1) % n);
                        if (!posList2.contains(pos)) {
                            posList2.add(pos);
                        }
                        pos = new Integer(vPos);
                        if (!posList2.contains(pos)) {
                            posList2.add(pos);
                        }
                    }
                }
                posList = posList2;
            }
        }

        m.put(u, posList);
        nodePosList.put(u, posList);
        Time timer = new Time(System.currentTimeMillis() - time1);
        initPosListTime += timer.getTime();
        return m;
    }

    /**
     * @param v
     *            the node to move.
     * @param newLocation
     *            new position of v.
     */
    private void move(Node v, int newLocation) {
        longestPathNodes.remove(v);
        longestPathNodes.add(newLocation, v);
        actualizeEdgeOrdering(v);
    }

    /**
     * @param size
     *            length of the PathList.
     * @param oldLoc
     *            actual position of the node.
     * @param newLoc
     *            new position of the node, the node will moving on the
     *            position.
     * @return false if from the left side to the right, true if from the right
     *         sede to the left.
     */
    private boolean getDirection(int oldLoc, int newLoc) {
        int size = longestPathNodes.size();
        boolean direction = false;
        if (oldLoc < newLoc) {
            if (newLoc - oldLoc >= Math.round(size / 2)) {
                direction = true;
            }
        } else {
            if (oldLoc - newLoc <= Math.round(size / 2)) {
                direction = true;
            }
        }
        return direction;
    }

    /**
     * @param nodeOrder
     *            initial node ordering after circular phase 1.
     * @return collection of a randomize node ordering.
     */
    private List randomNodeOrder() {

        List randomOrder = new ArrayList();
        Iterator nodes = longestPathNodes.iterator();
        List list = new ArrayList();
        while (nodes.hasNext()) {
            list.add(nodes.next());
        }
        int l = list.size() - 1;
        while (l >= 0) {
            double zufall = Math.random();
            Float order = new Float(l * zufall);
            int pos = Math.round(order.floatValue());
            Node node = (Node) list.remove(pos);
            randomOrder.add(node);
            l--;
        }
        return randomOrder;
    }

    /**
     * @return a list of the nodes with descanding degree.
     */
    private List descandingDegreeNodeOrder() {
        List descandingDegree = new ArrayList();
        List ascandingDegreeList = new ArrayList();
        quickSort = new QuickSort(longestPathNodes);
        ascandingDegreeList = quickSort.getSortedList();
        Iterator it = ascandingDegreeList.iterator();
        while (it.hasNext()) {
            descandingDegree.add(0, it.next());
        }
        return descandingDegree;
    }

    /**
     * @return a list of the nodes with ascanding degree.
     */
    private List ascandingDegreeNodeOrder() {
        List ascandingDegreeList = new ArrayList();
        quickSort = new QuickSort(longestPathNodes);
        ascandingDegreeList = quickSort.getSortedList();
        return ascandingDegreeList;
    }

    /**
     * @return a list of the nodes which is anticlockwise ordered.
     */
    private List antiClockwiseNodeOrder() {
        List antiClockwise = new ArrayList();
        Iterator it = longestPathNodes.iterator();
        while (it.hasNext()) {
            antiClockwise.add(0, it.next());
        }
        return antiClockwise;
    }

    /**
     * print the node ordering!
     * 
     * @param nodeOrdering
     */
    private void printNodeOrder(Collection nodeOrdering) {
        long time1 = System.currentTimeMillis();
        Iterator orderedNodeIt = nodeOrdering.iterator();
        System.out.print("Ordnung der Knoten: [");
        while (orderedNodeIt.hasNext()) {
            Node edge = (Node) orderedNodeIt.next();
            if (orderedNodeIt.hasNext()) {
                System.out.print(edge.getInteger("node.id") + " , ");
            } else {
                System.out.print(edge.getInteger("node.id"));
            }
        }
        System.out.println("]");
    }

    /**
     * @return
     */
    public List edgeEndPointsOrd() {

        Map list1Map = new HashMap();
        Map list2Map = new HashMap();
        Map ordAdjMap = new HashMap();

        Iterator it = longestPathNodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            List list1 = new ArrayList();
            List list2 = new ArrayList();
            list1Map.put(node, list1);
            list2Map.put(node, list2);
        }

        it = longestPathNodes.iterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            Iterator neighbors = node.getInNeighborsIterator();
            while (neighbors.hasNext()) {
                Node neighbor = (Node) neighbors.next();
                int nodePos = longestPathNodes.indexOf(node);
                int neighborPos = longestPathNodes.indexOf(neighbor);

                if (nodePos < neighborPos) {
                    List list1 = (List) list1Map.remove(neighbor);
                    list1.add(0, node);
                    list1Map.put(neighbor, list1);
                } else {
                    List list2 = (List) list2Map.remove(neighbor);
                    list2.add(0, node);
                    list2Map.put(neighbor, list2);
                }
            }
        }
        List edgeEndPoints = new ArrayList();
        it = longestPathNodes.iterator();
        while (it.hasNext()) {
            List orderedNeigbors = new ArrayList();
            Node node = (Node) it.next();
            List list2 = (List) list2Map.get(node);
            List list1 = (List) list1Map.get(node);
            for (int i = 0; i < list1.size(); i++) {
                orderedNeigbors.add(list1.get(i));
                edgeEndPoints.add(list1.get(i));
            }
            for (int i = 0; i < list2.size(); i++) {
                orderedNeigbors.add(list2.get(i));
                edgeEndPoints.add(list2.get(i));
            }
            ordAdjMap.put(node, orderedNeigbors);
        }

        List clockwiseEdgeOedering = new ArrayList();
        Iterator lpIt = longestPathNodes.iterator();
        while (lpIt.hasNext()) {
            Node no = (Node) lpIt.next();
            List ordAdjList = (List) ordAdjMap.get(no);
            List nodeOrderedEdges = new ArrayList();
            for (int t = 0; t < ordAdjList.size(); t++) {
                Node noo = (Node) ordAdjList.get(t);
                Edge e = graph.getEdges(no, noo).iterator().next();
                nodeOrderedEdges.add(e);
                clockwiseEdgeOedering.add(e);
            }
            initialEdgeOrd.put(no, nodeOrderedEdges);
        }
        return clockwiseEdgeOedering;
    }

    /**
     * a clockwise edge ordering in the circle after circular.
     * 
     * @see org.graffiti.plugins.algorithms.circulardrawing.Circular
     */
    private void initialEdgeOrderding() {
        Iterator it = longestPathNodes.iterator();
        while (it.hasNext()) {
            Node n = (Node) it.next();
            List ordEdges = new ArrayList();
            // ordEdges = nodeEdgeOrd(n);
            // System.out.println(" Ord edges sind " + ordEdges);
            // initialEdgeOrd.put(n, ordEdges);
        }
    }

    /**
     * Actualize the initial edge ordering after moving the node v.
     * 
     * @param v
     *            node will be moved.
     */
    private void actualizeEdgeOrdering(Node v) {
        List newEdgeOrd = nodeEdgeOrd(v);
        initialEdgeOrd.remove(v);
        initialEdgeOrd.put(v, newEdgeOrd);

        Collection neighbors = v.getNeighbors();
        Iterator neighborsIt = neighbors.iterator();
        while (neighborsIt.hasNext()) {
            Node neighbor = (Node) neighborsIt.next();
            newEdgeOrd = nodeEdgeOrd(neighbor);
            initialEdgeOrd.remove(neighbor);
            initialEdgeOrd.put(neighbor, newEdgeOrd);
        }
    }

    /**
     * debug the edge ordering in the circular drawing
     * 
     * @param edgeOrdering
     */
    private void printEdgeOrdering(List edgeOrdering) {

        Iterator orderedEdgesIt = edgeOrdering.iterator();
        System.out.print("Clocwise ordnung der Kanten: [");
        while (orderedEdgesIt.hasNext()) {
            Edge edge = (Edge) orderedEdgesIt.next();
            if (orderedEdgesIt.hasNext()) {
                System.out.print(edge.getInteger("label.label") + " , ");
            } else {
                System.out.print(edge.getInteger("label.label"));
            }
        }
        System.out.println("]");
    }

    /**
     * @param v
     *            actual processed node.
     * @return list of the clocwise edge ordering of v.
     */
    private List nodeEdgeOrd(Node v) {
        long time1 = System.currentTimeMillis();
        loopCounter++;
        int size = longestPathNodes.size();
        List edgeOrd = new ArrayList();
        List tmpEdgeOrd = new ArrayList();
        SortedMap sm = new TreeMap();

        int loc = longestPathNodes.indexOf(v);
        Collection edges = v.getEdges();
        Iterator it = edges.iterator();
        while (it.hasNext()) {
            Edge e = (Edge) it.next();
            Node source = e.getSource();
            Node target = e.getTarget();
            Node node = null;
            int pos = -1;
            if (!v.equals(source)) {
                node = source;
                pos = longestPathNodes.indexOf(source);

            } else {
                node = target;
                pos = longestPathNodes.indexOf(target);
            }

            int posX = (loc - pos + size) % size;
            sm.put(new Integer(posX), e);
            // e.setInteger("graphics.sortId", posX);
            // tmpEdgeOrd.add(e);

        } // end of while

        // quickSort = new QuickSort(tmpEdgeOrd);
        // edgeOrd = quickSort.getSortedList();

        Iterator sm1It = sm.values().iterator();
        while (sm1It.hasNext()) {
            Edge e = (Edge) sm1It.next();
            edgeOrd.add(e);
            // System.out.println("Kante " + e.getString("label.label"));
        }

        Time timer = new Time(System.currentTimeMillis() - time1);
        edgeOrderTime += timer.getTime();
        return edgeOrd;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {

        System.out.println("!!Circular Postprocessing started");
        long time1 = System.currentTimeMillis();

        int nodeNumber = graph.getNumberOfNodes();
        Map graphNodes = new HashMap();
        Iterator nodes = graph.getNodesIterator();

        for (int i = 0; nodes.hasNext(); i++) {
            Node n = (Node) nodes.next();
            // System.out.print("( "+ n.getInteger("dfsParam.dfsNum") + ")" + "
            // , " );
            nodeOrdering.put(new Integer(n.getInteger("longestPath.position")),
                    n);
        }

        List clockWiseOrderdNodesList = new ArrayList();
        Iterator nodeOrderingIt = nodeOrdering.values().iterator();
        while (nodeOrderingIt.hasNext()) {
            Node n = (Node) nodeOrderingIt.next();
            longestPathNodes.add(n);
            clockWiseOrderdNodesList.add(0, n);
        }
        CircularConst circularConst = new CircularConst();
        ClockwiseEdgeOrdering cweo = new ClockwiseEdgeOrdering(graph,
                longestPathNodes);
        List cwoEdgeOrdering = cweo.edgeOrdering();
        long xyzTime = System.currentTimeMillis();
        List xyz = edgeEndPointsOrd();
        Time xyzTimer = new Time(System.currentTimeMillis() - xyzTime);
        // printEdgeOrdering(xyz);

        // System.out.println(" Kanten ordnung wurde initialisiert");

        // Schritt 1
        // Current number of the crossing in the drawing.
        int currentCrossing = 0;
        long time = 0;
        Time timer = null;
        CountAllCrossing allCrossing = new CountAllCrossing(longestPathNodes);
        time = System.currentTimeMillis();
        int xyzCrossing = allCrossing
                .calculateNumberOfCrossing(cwoEdgeOrdering);

        crossAC = new Integer(currentCrossing);
        timer = new Time(System.currentTimeMillis() - time);
        countAllCrossTime += timer.getTime();
        int oldCrossing = currentCrossing;

        int size = longestPathNodes.size();
        CountSingleNodeCrossing singleNodeCrossing = new CountSingleNodeCrossing(
                longestPathNodes, initialEdgeOrd);
        // int nodeNumber = graph.getNumberOfNodes();
        int edgeNumber = graph.getNumberOfEdges();
        CircularConst.CPP_DATA[0] = new Integer(nodeNumber);
        CircularConst.CPP_DATA[1] = new Integer(edgeNumber);
        CircularConst.CPPI_DATA[0] = new Integer(nodeNumber);
        CircularConst.CPPI_DATA[1] = new Integer(edgeNumber);
        CircularConst.CPPII_DATA[0] = new Integer(nodeNumber);
        CircularConst.CPPII_DATA[1] = new Integer(edgeNumber);

        // for a fixed number of times
        for (int i = 0; i < 1; i++) {
            System.out.println("ITERATION IST " + i + "th ITERATION");
            int localChangeInCrossing = 0;
            List nodeProcessingOrdering = new ArrayList();

            // List nodeProcessingOrdering = Circular.orgGraphNodesPath;
            if (CircularConst.SELECT_CPP == 0) {
                // System.out.println("Clockwise node ordering! ");
                nodeProcessingOrdering = longestPathNodes;
                // nodeProcessingOrdering = clockWiseOrderdNodesList;
            } else if (CircularConst.SELECT_CPP == 1) {
                // System.out.println("Anticlockwise node ordering! ");
                nodeProcessingOrdering = antiClockwiseNodeOrder();
            } else if (CircularConst.SELECT_CPP == 2) {
                // System.out.println("Descanding degree node ordering! ");
                nodeProcessingOrdering = descandingDegreeNodeOrder();
            } else if (CircularConst.SELECT_CPP == 3) {
                // System.out.println("Ascanding degree node ordering! ");
                nodeProcessingOrdering = ascandingDegreeNodeOrder();
            } else if (CircularConst.SELECT_CPP == 4) {
                // System.out.println("Random node ordering! ");
                nodeProcessingOrdering = randomNodeOrder();
            }

            // Bearbeite Knoten im Uhrzeigersinn
            Object[] o = nodeProcessingOrdering.toArray();

            // for each node u, in G
            // for (int index = o.length - 1; index >= 0; index--){
            int csnCallNumber = 0;
            for (int index = 0; index < o.length; index++) {
                Node u = (Node) o[index];
                int oldLoc = longestPathNodes.indexOf(u);

                initPosList(u);
                List posList = (List) nodePosList.get(u);
                System.out.println();
                // System.out.print("Positionen List von Knoten " +
                // u.getInteger("dfsParam.dfsNum") + " --> ");
                // System.out.println(posList);

                // Step 7
                // for each location in PositionList
                for (int j = 0; j < posList.size(); j++) {

                    // Step 8
                    int newLoc = ((Integer) posList.get(j)).intValue();

                    // Step 9
                    boolean direction = getDirection(oldLoc, newLoc);
                    time = System.currentTimeMillis();
                    // System.out.println("singleNodeCrossing started");
                    int oldNumberSingleNodeCrossing = singleNodeCrossing
                            .calculateSingleNodeCrossing(u, newLoc, direction);
                    csnCallNumber++;
                    // System.out.println("singleNodeCrossing finished");
                    timer = new Time(System.currentTimeMillis() - time);
                    countSingleNodeTime += timer.getTime();

                    // System.out.println("oldNumberSingleNodeCrossing " +
                    // oldNumberSingleNodeCrossing);
                    // place u at this location
                    move(u, newLoc);

                    // in the opposite direction
                    direction = !direction;
                    time = System.currentTimeMillis();
                    // System.out.println("singleNodeCrossing started");
                    int newNumberSingleNodeCrossing = singleNodeCrossing
                            .calculateSingleNodeCrossing(u, oldLoc, direction);
                    csnCallNumber++;
                    // System.out.println("singleNodeCrossing finished");
                    countSingleNodeTime += timer.getTime();

                    // System.out.println("newNumberSingleNodeCrossing " +
                    // newNumberSingleNodeCrossing);
                    int changeInCrossing = newNumberSingleNodeCrossing
                            - oldNumberSingleNodeCrossing;
                    // System.out.println("changeInCrossing " +
                    // changeInCrossing);
                    // System.out.println("currentCrossing " + currentCrossing);
                    int newCrossing = currentCrossing + changeInCrossing;
                    // System.out.println("newCrossing sind " + newCrossing);

                    // Step 10
                    if (changeInCrossing < 0) {
                        currentCrossing = newCrossing;
                        oldLoc = newLoc;
                    }
                    // Step 11
                    // place u back into its previous position.
                    else {
                        move(u, oldLoc);
                    }
                }
                // System.out.println(" } ");
            }
            System.out.println();

            time = System.currentTimeMillis();
            if (circularConst.getRuntime() == 0) {
                System.out.println("Number of corssing bevor "
                        + "Postprocessing: " + xyzCrossing);
                cweo = new ClockwiseEdgeOrdering(graph, longestPathNodes);
                cwoEdgeOrdering = cweo.edgeOrdering();
                int newCrossing = allCrossing
                        .calculateNumberOfCrossing(cwoEdgeOrdering);
                crossAPP = new Integer(newCrossing);
                System.out.println("Number of corssing after "
                        + "Postprocessing: " + newCrossing);
                int newXyzCrossing = allCrossing
                        .calculateNumberOfCrossing(edgeEndPointsOrd());
                System.out.println("newXyzCrossing ist " + newXyzCrossing);
            }
            timer = new Time(System.currentTimeMillis() - time);
            countAllCrossTime += timer.getTime();

            // System.out.println(" die Methode nodeEdgeOrd wurde " +
            // loopCounter + " mal aufgerufen");
            // System.out.println(" mathematisch " + graph.getNumberOfNodes() *
            // Math.log(
            // (new Double(graph.getNumberOfEdges())).doubleValue()));
            System.out.println("======= END OF " + i + "th ITERATION =======");

            Time timer2 = new Time(System.currentTimeMillis() - time1);
            /*
             * if (circularConst.getAlgorithm(0)== "1") {
             * CircularConst.CPP_DATA[2] = new Integer(newCrossing);
             * CircularConst.CPP_DATA[3] = new Long(timer2.getTime()); } else if
             * (circularConst.getAlgorithm(1)== "1") {
             * CircularConst.CPPI_DATA[2] = new Integer(newCrossing);
             * CircularConst.CPPI_DATA[3] = new Long(timer2.getTime()); } else
             * if (circularConst.getAlgorithm(2) == "1") {
             * CircularConst.CPPII_DATA[2] = new Integer(newCrossing);
             * CircularConst.CPPII_DATA[3] = new Long(timer2.getTime()); }
             */
            System.out.println("Gesamt Zeit: " + timer2.getTime() + " ms. = "
                    + timer2.toString() + " hh:mm:ss");
            System.out.println("Die Methode nodeEdgeOrd() nimmt "
                    + edgeOrderTime + " ms Zeit in Anspruch");
            System.out.println("Die Methode edgeEntPointsOrd() nimmt "
                    + xyzTimer.getTime() + " ms Zeit in Anspruch");
            System.out.println("Die Methode initPosList() nimmt "
                    + initPosListTime + " ms Zeit in Anspruch");
            System.out.println("CountAllCrossing nimmt " + countAllCrossTime
                    + " ms Zeit in Anspruch");
            System.out.println("CountSingleNodeCrossing wurde insgesamt "
                    + csnCallNumber + " mal aufgerufen");
            System.out.println("CountSingleNodeCrossing nimmt "
                    + countSingleNodeTime + " ms Zeit in Anspruch");
        }
        loopCounter = 0;
        edgeOrderTime = 0;
        countSingleNodeTime = 0;
        countAllCrossTime = 0;
        initPosListTime = 0;
        // Step 12
        // if no improvement was made during this iteration, stop.
        if (CircularConst.TEST == 0) {
            CircularLayout layout = new CircularLayout(longestPathNodes);
            layout.embeddingPathOnToCircle();
        }
        // reset();
    }
}
