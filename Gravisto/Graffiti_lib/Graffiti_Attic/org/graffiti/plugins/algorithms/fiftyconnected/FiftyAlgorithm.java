/*
 *Calculates the 50%-connected subgraph of a graph. The algorithm starts 
 *with a at least 50%-connected clique.
 */

package org.graffiti.plugins.algorithms.fiftyconnected;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * @author Szarowski Szymon
 */
public class FiftyAlgorithm extends AbstractAlgorithm {
    private Graph sourceClique;

    private Selection selection;

    private LinkedList borderNodes = new LinkedList();

    private LinkedList neighbours = new LinkedList();

    private LinkedList queue = new LinkedList();

    private static boolean heuristicActive = false;

    private int[] selectionSizes = new int[6];

    @Override
    public void attach(Graph g) {
        graph = g;
        graph.setDirected(false, true);
    }

    /**
     * Controls if preconditions are fulfilled. There have to be at least 3
     * Nodes and 2 Edges in the graph, graph musn't be empty and the selected
     * subgraph must be a at least 50%-connected graph.
     */
    @Override
    public void check() throws PreconditionException {
        Collection col;
        Iterator it;
        boolean slingFound = false;
        Node node;
        PreconditionException errors = new PreconditionException();
        if (graph.getNumberOfNodes() < 3 || graph.getNumberOfEdges() < 2) {
            errors.add("Please insert at least 3 Nodes and at least 2 edges.");
        }
        // The graph is inherited from AbstractAlgorithm.
        if (graph == null) {
            errors.add("The graph instance is empty.");
        } else {
            it = graph.getNodesIterator();
            while (it.hasNext() && !slingFound) {
                node = (Node) it.next();
                col = graph.getEdges(node, node);
                if (!col.isEmpty()) {
                    errors.add("The graph mustn't contain slings!");
                    slingFound = true;
                }
            }
        }
        if (selection == null || !checkSelection(selection)) {
            errors.add("Please select a not empty 50%-connected subgraph.");
        }
        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * Performs a basic check of the selection. Controlls if selection isn't
     * empty, controlls if enough edges are selected for every selected node, so
     * it is at least 50%-connected.
     * 
     * @param sel
     *            selected nodes
     * @return true if selected nodes constitute an at least 50%-connected
     *         subgraph of the main graph
     */
    private boolean checkSelection(Selection sel) {
        int i, k, deg;
        Node node;
        Edge edge;
        List nodeList = sel.getNodes();
        List edgeList = sel.getEdges();
        Iterator outEdgesIt;
        ListIterator itNode = nodeList.listIterator();
        ListIterator itEdge;
        if (sel.isEmpty())
            return false;
        while (!nodeList.isEmpty() && itNode.hasNext()) {
            node = (Node) itNode.next();
            deg = node.getOutDegree();
            k = (int) (deg / 2.0) + 1;
            i = 0;
            outEdgesIt = node.getAllOutEdges().iterator();
            while (i < k && outEdgesIt.hasNext()) {
                edge = (Edge) outEdgesIt.next();
                if (edgeList.contains(edge)) {
                    i++;
                }
            }
            // if there exists a node that has to few outgoing edges in the
            // edgeList then the Selection is wrong
            if (i < k)
                return false;
        }
        return true;
    }

    /**
     * Fill the borderNodes LinkedList with elements of the selection which have
     * at least one connection with a node outside the selection.
     */
    private void fillBorderNodes() {
        Node node;
        List selNodeList = selection.getNodes();
        List selEdgeList = selection.getEdges();
        ListIterator selitNode = selNodeList.listIterator();
        while (selitNode.hasNext()) {
            node = (Node) selitNode.next();
            if (!node.getBoolean("nodeComplete")) {
                borderNodes.add(node);
            }
        }
    }

    /**
     * Fill the neighbours LinkedList with Nodes which have at least one
     * connection with a node from the borderNodes LinkedList. This method also
     * sets the parameters of nodes, which describe the amount of connection of
     * neighbours to selection and amount of connections between neighbours.
     */
    private void fillNeighbours() {
        int i, k;
        Node node, nodeTmp;
        Iterator it;
        it = graph.getNodesIterator();
        // reset the connsToSelection attribute to 0
        while (it.hasNext()) {
            node = (Node) it.next();
            node.setInteger("connsToSelection", 0);
            node.setInteger("connsBetwNeighb", 0);
        }
        List selNodeList = selection.getNodes();
        ListIterator listIt = borderNodes.listIterator();
        // fill the neighbours list with the neighbours of the selection
        while (listIt.hasNext()) {
            node = (Node) listIt.next();
            // it contains the neighbours of border nodes
            it = node.getAllInNeighbors().iterator();
            while (it.hasNext()) {
                nodeTmp = (Node) it.next();
                // we are not interested in nodes, that are in the selection
                if (!selNodeList.contains(nodeTmp)
                        && !neighbours.contains(nodeTmp)) {
                    nodeTmp.setInteger("connsToSelection", 1);
                    neighbours.add(nodeTmp);
                }
                // calculate amounts of connections to selection
                else if (!selNodeList.contains(nodeTmp)
                        && neighbours.contains(nodeTmp)) {
                    i = neighbours.indexOf(nodeTmp);
                    nodeTmp = (Node) neighbours.get(i);
                    neighbours.remove(i);
                    k = nodeTmp.getInteger("connsToSelection");
                    nodeTmp.setInteger("connsToSelection", k + 1);
                    neighbours.add(i, nodeTmp);
                }
            }
        }
        LinkedList neighboursTmp = (LinkedList) neighbours.clone();
        listIt = neighbours.listIterator();
        while (listIt.hasNext()) {
            node = (Node) listIt.next();
            it = node.getAllInNeighbors().iterator();
            while (it.hasNext()) {
                nodeTmp = (Node) it.next();
                if (neighbours.contains(nodeTmp)) {
                    i = neighboursTmp.indexOf(nodeTmp);
                    nodeTmp = (Node) neighboursTmp.get(i);
                    neighboursTmp.remove(i);
                    k = nodeTmp.getInteger("connsBetwNeighb");
                    nodeTmp.setInteger("connsBetwNeighb", k + 1);
                    neighboursTmp.add(i, nodeTmp);
                }
            }
        }
        neighbours = neighboursTmp;
    }

    /**
     * Create priority queue ordered descending by quotient (# connections to
     * selection)/(#conns to neighbours 2nd degree) and 2nd criteria by # conns
     * to neighbours and fill the priority queue with nodes
     */
    private void createPrioQueue() {
        Node node;
        ListIterator listIt = neighbours.listIterator();
        while (queue.size() > 0) {
            queue.removeFirst();
        }
        while (listIt.hasNext()) {
            node = (Node) listIt.next();
            putNode(queue, node);
        }
    }

    /**
     * Resets the algorithms variables borderNodes, neighbours and queue
     */
    private void reload() {
        heuristicActive = false;
        borderNodes = new LinkedList();
        neighbours = new LinkedList();
        setGraphParams();
        fillBorderNodes();
        fillNeighbours();
        createPrioQueue();
    }

    /**
     * Executes the 50%-connected subgraph algorithm
     */
    public void execute() {
        // NodeParameter
        Node node = null;
        Node nodeTmp;
        Edge edge;
        double tmp, border;
        heuristicActive = false;
        int connsToNeighbours, position, i, k;
        selectionSizes[0] = 0;
        ListIterator listIt;
        Iterator it, it2;
        Iterator outEdges;
        LinkedList list = new LinkedList();
        LinkedList listTmp = new LinkedList();
        LinkedList queueTmp = new LinkedList();
        LinkedList result = new LinkedList();
        LinkedList resultTmp = new LinkedList();
        List selNodeList = selection.getNodes();
        List selEdgeList = selection.getEdges();
        ListIterator selitNode = selNodeList.listIterator();
        setGraphParams();
        fillBorderNodes();
        fillNeighbours();
        createPrioQueue();
        int lab = 0;
        Iterator labelIt = graph.getNodesIterator();
        while (labelIt.hasNext()) {
            lab++;
            node = (Node) labelIt.next();
            node.setInteger("label", lab);
        }
        // the 50%connected subgraph algorithm
        if (!queue.isEmpty()) {
            node = (Node) queue.getFirst();
        }
        while (queue.size() > 0 && node != null
                && node.getDouble("quotient") > 1.0 || node != null
                && getNeighbourQuotient(node) > 1.0 && queue.size() > 0) {
            node = (Node) queue.removeFirst();
            if (node.getDouble("quotient") > 1.0 && !heuristicActive) {
                // node that can be already inserted found
                selection.add(node);
                it = node.getAllOutEdges().iterator();
                // insert Edges in the selection
                while (it.hasNext()) {
                    edge = (Edge) it.next();
                    nodeTmp = edge.getTarget();
                    if (!nodeTmp.equals(node) && selection.contains(nodeTmp)) {
                        selection.add(edge);
                    }
                    nodeTmp = edge.getSource();
                    if (!nodeTmp.equals(node) && selection.contains(nodeTmp)) {
                        selection.add(edge);
                    }
                }
                reload();
            } else if (getNeighbourQuotient(node) > 1.0 || heuristicActive) {
                // looking at neighbours and searching for a node set that can
                // be inserted into the selection
                heuristicActive = true;
                refreshParameters(node, resultTmp);
                resultTmp.add(node);
                result = checkSolution(resultTmp);
                nodeTmp = getBestNeighbour(resultTmp, queue);
                if (nodeTmp != null) {
                    queue.remove(nodeTmp);
                    queue.addFirst(nodeTmp);
                }
                if (!result.isEmpty()) {
                    listIt = result.listIterator();
                    while (listIt.hasNext()) {
                        node = (Node) listIt.next();
                        selection.add(node);
                        it = node.getAllOutEdges().iterator();
                        // insert Edges in the selection
                        while (it.hasNext()) {
                            edge = (Edge) it.next();
                            nodeTmp = edge.getTarget();
                            if (!nodeTmp.equals(node)
                                    && selection.contains(nodeTmp)) {
                                selection.add(edge);
                            }
                            nodeTmp = edge.getSource();
                            if (!nodeTmp.equals(node)
                                    && selection.contains(nodeTmp)) {
                                selection.add(edge);
                            }
                        }
                    }
                    reload();
                }
            } else {
                System.out.println("No more selections possible, reloading!");
            }
            if (!queue.isEmpty()) {
                node = (Node) queue.getFirst();
            } else {
                reload();
                if (checkLastSelectionSizes() && !queue.isEmpty()) {
                    node = (Node) queue.getFirst();
                } else {
                    // end of iteration
                    node = null;
                }
            }
        }
    }

    /**
     * Checks if progress is made by the algorithm by controlling if the size of
     * the selection changed in at least one of the last five iterations.
     * 
     * @return true if selection.size() increased in at least one of the last 5
     *         iterations
     */
    private boolean checkLastSelectionSizes() {
        int i = 0;
        int size = selection.getNodes().size();
        boolean result = false;
        if (selectionSizes[0] < size) {
            selectionSizes[0] = size;
            selectionSizes[1] = -1;
            result = true;
            i = 6;
        }
        while (i < 6) {
            if (selectionSizes[i] == -1 && i < 5) {
                selectionSizes[i] = size;
                selectionSizes[i + 1] = -1;
                result = true;
                i = 6;
            } else if (selectionSizes[i] == -1 && i == 5) {
                result = false;
            }
            i++;
        }
        /*
         * System.out.println( "selectionSizes [" + selectionSizes[0] + "],[" +
         * selectionSizes[1] + "],[" + selectionSizes[2] + "],[" +
         * selectionSizes[3] + "],[" + selectionSizes[4] + "],[" +
         * selectionSizes[5] + "]");
         */
        return result;
    }

    /**
     * Checks if the nodes found by the algorithms heuristics already contain a
     * solution.
     * 
     * @param res
     *            list of nodes that can contain a solution
     * @return a list of nodes that can be directly added to selection or an
     *         empty list if such nodes were not found
     */
    private LinkedList checkSolution(LinkedList res) {
        /*
         * System.out.println( "check solution! List.size() = " + res.size() + "
         * queue.size() = " + queue.size());
         */
        Node nod;
        LinkedList list = new LinkedList();
        LinkedList list2 = new LinkedList();
        ListIterator listIt = res.listIterator();
        if (res.size() < 2)
            return new LinkedList();
        while (listIt.hasNext()) {
            putNode(list, (Node) listIt.next());
        }
        list2 = (LinkedList) list.clone();
        while (!list2.isEmpty()) {
            nod = (Node) list2.getLast();
            if (nod.getInteger("minAmountOfConns") <= nod
                    .getInteger("connsToSelection"))
                return list2;
            else {
                nod = (Node) list2.removeLast();
                refresh(nod, list2);
            }
        }
        return new LinkedList();
    }

    /**
     * Calculates and modifies changes in the "connsToSelection" and
     * "connsBetwNeighb" attributes in list after removing node from the
     * solution queue.
     * 
     * @param node
     *            a Node that was removed from list
     * @param list
     *            list of solution nodes
     */
    private void refresh(Node node, LinkedList list) {
        Node nod;
        int conSel, conNeig;
        ListIterator listIt = list.listIterator();
        while (listIt.hasNext()) {
            nod = (Node) listIt.next();
            conSel = nod.getInteger("connsToSelection");
            conNeig = nod.getInteger("connsBetwNeighb");
            if (nod.getAllInNeighbors().contains(node) && conSel > 0
                    && neighbours.contains(nod)) {
                conSel--;
                nod.setInteger("connsToSelection", conSel);
                conNeig++;
                nod.setInteger("connsBetwNeighb", conNeig);
                setQuotient(nod);
            }
        }
    }

    /**
     * Calculates and modifies changes in the "connsToSelection" and
     * "connsBetwNeighb" attributes in resList and in node, before adding a node
     * to a solution queue. This method is used in the heuristic to fill the
     * temporary solution queue in every iteration.
     * 
     * @param node
     *            a node tha has to be added to resList
     * @param resList
     *            list of nodes, that may be put in the solution
     */
    private void refreshParameters(Node node, LinkedList resList) {
        Node nodeTmp, nodeTmp2;
        int tmp = 0;
        int conSel, conNeig;
        neighbours.remove(node);
        Iterator it = node.getAllInNeighbors().iterator();
        while (it.hasNext()) {
            nodeTmp = (Node) it.next();
            // we don't care about selection
            if (!resList.isEmpty() && resList.contains(nodeTmp)) {
                tmp = resList.indexOf(nodeTmp);
                nodeTmp2 = (Node) resList.get(tmp);
                resList.remove(tmp);
                conSel = nodeTmp2.getInteger("connsToSelection");
                conSel++;
                nodeTmp2.setInteger("connsToSelection", conSel);
                conNeig = nodeTmp2.getInteger("connsBetwNeighb");
                conNeig--;
                nodeTmp2.setInteger("connsBetwNeighb", conNeig);
                setQuotient(nodeTmp2);
                resList.add(tmp, nodeTmp2);
                // for node connsTo Selection and connsToNieghb allready ok
            } else if (!resList.isEmpty() && !selection.contains(nodeTmp)
                    && !resList.contains(nodeTmp)) {
                if (neighbours.contains(nodeTmp)) // neighbour is in
                // neighbours
                {
                    conSel = nodeTmp.getInteger("connsToSelection");
                    conSel++;
                    nodeTmp.setInteger("connsToSelection", conSel);
                    conNeig = nodeTmp.getInteger("connsBetwNeighb");
                    conNeig--;
                    nodeTmp.setInteger("connsBetwNeighb", conNeig);
                    setQuotient(nodeTmp);
                } else
                // nodeTmp was neighbour 2nd degree
                {
                    tmp = connsToList(nodeTmp, neighbours);
                    nodeTmp.setInteger("connsBetwNeighb", tmp);
                    nodeTmp.setInteger("connsToSelection", 1);
                    setQuotient(nodeTmp);
                    neighbours.add(nodeTmp);
                    putNode(queue, nodeTmp);
                    conNeig = node.getInteger("connsBetwNeighb");
                    conNeig++;
                    node.setInteger("connsBetwNeighb", conNeig);
                }
            } else // if nodeTmp is neighbour 2nd degree
            if (resList.isEmpty() && !selection.contains(nodeTmp)
                    && !neighbours.contains(nodeTmp)) {
                // changed from node to nodeTmp
                conNeig = node.getInteger("connsBetwNeighb");
                conNeig++;
                node.setInteger("connsBetwNeighb", conNeig);
            }
            setQuotient(node);
        }
    }

    /**
     * Calculates the amount of connections from node to the elements of the
     * list.
     * 
     * @param node
     *            a node
     * @param list
     *            a list of nodes
     * @return amounts of connections between node and elements of the list
     */
    private int connsToList(Node node, LinkedList list) {
        int neigh = 0;
        Node nodeTmp;
        Iterator neighIt = node.getAllInNeighbors().iterator();
        while (neighIt.hasNext()) {
            nodeTmp = (Node) neighIt.next();
            if (list.contains(nodeTmp)) {
                neigh++;
            }
        }
        return neigh;
    }

    /**
     * Returns the next node to be considered in a heuristic solution. It is
     * this neighbour node of one of the nodes in resultNodes, that has the
     * smalles "quotient" attribute.
     * 
     * @param resultNodes
     *            nodes that are considered
     * @param list
     *            by "quotient" ordered list of not yet handled nodes
     * @return best node to be inserted to resultNodes
     */
    private Node getBestNeighbour(LinkedList resultNodes, LinkedList list) {
        Node nodeTmp;
        ListIterator listIt = list.listIterator();
        while (listIt.hasNext()) {
            nodeTmp = (Node) listIt.next();
            if (isNeighbour(nodeTmp, resultNodes))
                return nodeTmp;
        }
        return null;
    }

    /**
     * Checks if node is a neighbour of at least one node from the list.
     * 
     * @param node
     *            a node
     * @param list
     *            a list of nodes
     * @return true if a neighbour was found
     */
    private boolean isNeighbour(Node node, LinkedList list) {
        Node nodeTmp;
        Iterator it;
        it = node.getAllInNeighbors().iterator();
        while (it.hasNext()) {
            nodeTmp = (Node) it.next();
            if (list.contains(nodeTmp))
                return true;
        }
        return false;
    }

    /**
     * Returns a quotient, that contains the amount of neighbours in its
     * calculation.
     * 
     * @param node
     *            a node that "quotient" schould be calculated
     * @return "quotient" parameter calculated by ("connsToSelection"+
     *         "connsBetwNeighb"
     *         )/("degree"-"connsToSelection"-"connsBetwNeighb")
     */
    private double getNeighbourQuotient(Node node) {
        int deg = node.getInteger("degree");
        int conSel = node.getInteger("connsToSelection");
        int conNeig = node.getInteger("connsBetwNeighb");
        double tmp = deg - conSel - conNeig;
        if (tmp == 0.0)
            return Double.MAX_VALUE;
        return (conSel + conNeig) / tmp;
    }

    /**
     * Sets the "quotient" attribute, which is calculated by (s = amount of
     * connections to selected nodes)/(ns = amount of connections to nodes that
     * aren't selected and aren't neighbours of selected) There are two modes in
     * which this method runs. If the global variable heuristicActive is set
     * false: if ns==0 "quotient" is set to Double.MAX_VALUE if ns==0 and there
     * are any neighbours of the selection "quotient" is set to 1 else "quotient
     * is set to s/ns In the second mode heuristicActive=true an = amount of
     * neighbours if ns==0 "quotient is set to Double.MAX_VALUE else (s+an)/ns
     * 
     * @param node
     *            a node which has set the attributes "degree",
     *            "connsToSelection" and "connsBetwNeighb"
     */
    private static void setQuotient(Node node) {
        int deg = node.getInteger("degree");
        int conSel = node.getInteger("connsToSelection");
        int conNeig = node.getInteger("connsBetwNeighb");
        /*
         * System.out.println( "Setting quotient deg = " + deg + " conSel = " +
         * conSel + " conNeig = " + conNeig);
         */
        double tmp = deg - conSel - conNeig;
        if (tmp != 0.0 && !heuristicActive) {
            node.setDouble("quotient",
                    (node.getInteger("connsToSelection") / tmp));
        } else if (deg == conSel && !heuristicActive) {
            assert tmp == 0.0;
            node.setDouble("quotient", Double.MAX_VALUE);
        } else if (!heuristicActive) {
            node.setDouble("quotient", 1.0);
        }
        if (heuristicActive && tmp != 0.0) {
            node.setDouble("quotient", (conNeig + conSel) / tmp);
        } else if (heuristicActive && tmp == 0.0) {
            node.setDouble("quotient", Double.MAX_VALUE);
        }
    }

    /**
     * Creates a sorted queue, that is sorted decreasing by the Node attribute
     * "quotient".
     * 
     * @param q
     *            an empty or sorted List, that we use as a priority queue
     * @param n
     */
    private static void putNode(LinkedList q, Node n) {
        int j = 0;
        boolean ready = true;
        Node no;
        setQuotient(n);
        while (j < q.size() && ready) {
            no = (Node) q.get(j);
            if ((no.getDouble("quotient") < n.getDouble("quotient"))
                    || (no.getDouble("quotient") == n.getDouble("quotient") && no
                            .getInteger("connsBetwNeighb") < n
                            .getInteger("connsBetwNeighb"))) {
                q.add(j, n);
                ready = false;
            }
            j++;
        }
        if (ready == true) {
            q.addLast(n);
        }
    }

    /**
     * Sets Node parameters like degree, amount of connections in selection,
     * minimum amount of connections, so node is mor than 50%-connected and a
     * boolean valueif all edges from the node are already in selection.
     */
    private void setGraphParams() {
        int i, deg, k;
        Node node;
        Edge edge;
        Iterator itNode = graph.getNodes().iterator();
        Collection selEdgeList = selection.getEdges();
        Iterator outEdgesIt;
        while (itNode.hasNext()) {
            node = (Node) itNode.next();
            deg = node.getOutDegree();
            i = 0;
            outEdgesIt = node.getAllOutEdges().iterator();
            while (outEdgesIt.hasNext()) {
                edge = (Edge) outEdgesIt.next();
                if (selEdgeList.contains(edge)) {
                    i++;
                }
            }
            node.setInteger("amountOfConnsInSelection", i);
            node.setInteger("degree", deg);
            node.setInteger("minAmountOfConns", (int) (deg / 2.0) + 1);
            if (deg == i) {
                node.setBoolean("nodeComplete", true);
            } else {
                assert deg < i && deg > 0;
                node.setBoolean("nodeComplete", false);
            }
        }
    }

    /**
     * Returns the name of the algorithm for the plugin manager.
     */
    public String getName() {
        return "50%-connected Subgraph";
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter(selection,
                "Start nodes", "The Algorithm will start with the nodes");
        return new Parameter[] { selParam };
    }

    /**
     * Allows to restart the algorithm.
     */
    @Override
    public void reset() {
        Node node;
        Iterator it = selection.getNodes().iterator();
        while (it.hasNext()) {
            node = (Node) it.next();
            node.setBoolean("nodeComplete", false);
            node.setInteger("minAmountOfConns", 0);
            node.setInteger("degree", 0);
            node.setInteger("amountOfConnsInSelection", 0);
            node.setDouble("quotient", 0.0);
            node.setInteger("connsToSelection", 0);
            node.setInteger("connsBetwNeighb", 0);
        }
        borderNodes = new LinkedList();
        neighbours = new LinkedList();
        queue = new LinkedList();
        heuristicActive = false;
        selectionSizes = new int[6];

    }

    /**
     * Sets the parameters.
     * 
     * @param params
     *            the parameters
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
    }
}
