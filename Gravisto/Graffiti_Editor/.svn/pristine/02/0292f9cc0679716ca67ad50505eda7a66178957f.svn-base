package org.graffiti.plugins.algorithms.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * computes an ordering of the nodes with minimum crossings and the offset of
 * each edge
 * 
 * @author Matthias H�llm�ller
 */
public class CrossingReduction {

    /**
     * the graph
     */
    private Graph graph;

    /**
     * stores the current order of each level
     */
    private LinkedList<Node>[] order;

    /**
     * stores the offsets
     */
    private HashMap<Edge, Integer> offset = new HashMap<Edge, Integer>();

    /**
     * stores left and right adjacent horizontal edges for each node
     */
    private HashMap<Node, LinkedList<Edge>> hlEdges = new HashMap<Node, LinkedList<Edge>>();
    private HashMap<Node, LinkedList<Edge>> hrEdges = new HashMap<Node, LinkedList<Edge>>();

    /**
     * stores adjacent vertical edges for each node
     */
    private HashMap<Node, LinkedList<Edge>> vEdges = new HashMap<Node, LinkedList<Edge>>();

    /**
     * the constructor
     * 
     * @param graph
     *            the graph
     * @param order
     *            the order of nodes of each level
     */
    public CrossingReduction(Graph graph, LinkedList<Node>[] order) {
        this.graph = graph;
        this.order = order;
    }

    /**
     * computes the order with minimum crossing edges
     */
    @SuppressWarnings( { "unchecked", "nls" })
    public HashMap<Edge, Integer> minimizeCrossings() {

        // initialize offsets
        for (Edge e : this.graph.getEdges()) {
            this.offset.put(e, 0);
        }

        int counter = 0; // counts the number of sifting rounds

        // initialize LinkedList[] (HashMap) to store the best order (offset)
        // before, between and after running top-down and bottom-up
        LinkedList<Node>[] bestOrder = new LinkedList[this.order.length];
        for (int i = 0; i < this.order.length; i++) {
            bestOrder[i] = new LinkedList<Node>();
        }
        HashMap<Edge, Integer> bestOffset = new HashMap<Edge, Integer>();

        // as long as there is an improvement(fewer crossings)
        // -> level sweep: consider only two levels - first run top-down then
        // bottom-up
        while (true) {
            counter++;
            int bestCrossings; // remember best number of crossings
            int currentCrossings;

            // count all crossings before running top-down and store the current
            // order and offset
            int tempCrossings = countAllCrossings(this.order);
            bestCrossings = tempCrossings;
            for (int i = 0; i < this.order.length; i++) {
                bestOrder[i].clear();
                bestOrder[i].addAll(this.order[i]);
            }
            bestOffset = (HashMap<Edge, Integer>) this.offset.clone();

            // first run top-down...
            for (int i = 0; i < this.order.length - 1; i++) {

                // compute one round of sifting for current two levels
                storeAdjacentVerticalEdges(this.order[i], this.order[i + 1]);
                this.order[i + 1] = sifting(this.order[i], this.order[i + 1]);
            }

            // count all crossings after running top-down and before running
            // bottom-up and store the current order and offset
            currentCrossings = countAllCrossings(this.order);
            if (currentCrossings < bestCrossings) {
                bestCrossings = currentCrossings;
                for (int i = 0; i < this.order.length; i++) {
                    bestOrder[i].clear();
                    bestOrder[i].addAll(this.order[i]);
                }
                bestOffset = (HashMap<Edge, Integer>) this.offset.clone();
            }

            // ...then bottom-up
            for (int i = this.order.length - 1; i > 0; i--) {

                // compute one round of sifting for current two levels
                if ((i - 2) >= 0) {
                    storeAdjacentVerticalEdges(this.order[i - 2],
                            this.order[i - 1]);
                } else {
                    for (Node n : this.order[i - 1]) {
                        this.vEdges.put(n, new LinkedList<Edge>());
                    }
                }
                this.order[i - 1] = sifting(this.order[i], this.order[i - 1]);
            }

            // count all crossings after running top-down and bottom-up and
            // store the current order and offset
            currentCrossings = countAllCrossings(this.order);
            if (currentCrossings < bestCrossings) {
                bestCrossings = currentCrossings;
                for (int i = 0; i < this.order.length; i++) {
                    bestOrder[i].clear();
                    bestOrder[i].addAll(this.order[i]);
                }
                bestOffset = (HashMap<Edge, Integer>) this.offset.clone();
            }

            // get the best order and offset for the next runthrough
            for (int i = 0; i < bestOrder.length; i++) {
                this.order[i].clear();
                this.order[i].addAll(bestOrder[i]);
            }
            this.offset = (HashMap<Edge, Integer>) bestOffset.clone();

            // no improvement - break
            if (tempCrossings <= bestCrossings) {
                break;
            }

            // after x rounds -> exit the loop (just as precaution)
            if (counter == 10) {
                break;
            }

        }

        // minimize edge lengths by rotating the graph
        minimizeEdgeLengths();

        return this.offset;
    }

    /**
     * computes one round of sifting, considering the two current levels and
     * moving a vertex v of level 2 along a fixed ordering of all other vertices
     * of level 2 and place it to its local optimum - the fewest crossings
     * (there are three types of crossings: vertical, horizontal and
     * vertical/horizontal)
     * 
     * @param level1
     * @param level2
     * @return updated order of level 2
     */
    @SuppressWarnings("unchecked")
    private LinkedList<Node> sifting(LinkedList<Node> level1,
            LinkedList<Node> level2) {

        // get all vertical edges between level 1 and 2
        LinkedList<Edge> verticalEdges = getVerticalEdges(level1, level2);

        // get all horizontal edges on level 2
        LinkedList<Edge> horizontalEdges = getHorizontalEdges(level2);

        // one round of sifting - try each node
        LinkedList<Node> level2Temp = (LinkedList<Node>) level2.clone();
        for (Node v : level2Temp) {

            // get vertical edges of level1 to v ordered by positions of level1
            LinkedList<Edge> edgesToV = new LinkedList<Edge>();
            for (Node u : level1) {
                edgesToV.addAll(this.graph.getEdges(u, v));
            }

            // store left and right horizontal edges ordered ascending by
            // level2 for each node
            storeAdjacentHorizontalEdges(level2, horizontalEdges);

            // move v to the first position
            level2.addFirst(level2.remove(level2.indexOf(v)));

            // set offsets of edges to v to 1
            for (Edge e : edgesToV) {
                this.offset.put(e, 1);
            }

            // initialize counters for...
            int iTemp = 0; // temp position
            int j = 0; // offset
            int jTemp = 0; // temp offset
            int l = 0; // parting
            int lTemp = 0; // temp parting
            int c = 0; // crossing number
            int cTemp = 0; // temp crossing number

            // search the best position for v
            for (int i = 0; i < level2.size() - 1; i++) {

                // get the next node - v2
                Node v2 = level2.get(i + 1);// i

                // get edges to the next node
                LinkedList<Edge> edgesToV2 = new LinkedList<Edge>();
                for (Node u : level1) {
                    edgesToV2.addAll(this.graph.getEdges(u, v2));
                }

                // swap v and the next node and update the crossing
                // number
                c = c
                        - countVerticalCrossings(edgesToV, edgesToV2, level1,
                                level2);
                int positionOfV = level2.indexOf(v);
                level2.set(level2.indexOf(v2), v);
                level2.set(positionOfV, v2);
                c = c
                        + countVerticalCrossings(edgesToV, edgesToV2, level1,
                                level2);

                int c1 = 0; // crossing-number before reducing offset
                int c2 = 0; // crossing-number after reducing the offset

                // as long as there is an improvement
                do {

                    // if there are no edges from level 1 to v - break
                    // special case exactly one vertical edge - break
                    if (edgesToV.size() == 0
                            || edgesToV2.size() == 0
                            || (edgesToV.size() == 1
                                    && verticalEdges.size() == 1 && edgesToV
                                    .equals(verticalEdges))) {
                        break;
                    }

                    // try to improve the parting by reducing the
                    // offset of the next edge - comparing crossing
                    // numbers before and after reducing the offset
                    Edge el = edgesToV.get(l);
                    c1 = countVerticalCrossings(el, verticalEdges, level1,
                            level2);
                    this.offset.put(el, j);
                    c2 = countVerticalCrossings(el, verticalEdges, level1,
                            level2);

                    // if successful, then try again, else restore
                    // the offset
                    if (c2 <= c1) {

                        // update crossing number and try next parting
                        c = c - c1 + c2;
                        l++;

                        // if all edges are reduced once (from 1 to 0)
                        // reset parting to 0 and try to get improvement
                        // with setting this.offsets from 0 to -1
                        if (l == edgesToV.size()) {
                            j--;
                            l = 0;
                        }

                    } else {
                        this.offset.put(el, j + 1);
                    }

                } while (c2 <= c1);

                // update horizontal adjacencies
                int x = updateHAdj(level2, v, v2);

                // compute the change in crossing count for horizontal edges
                c = c + siftingSwapH(v, v2, level2);

                // compute the change in crossing count for horizontal and
                // vertical edges
                c = c + siftingSwapHV(v, v2, x);

                // remember the best position, offset, parting, crossing number
                if (c < cTemp) {
                    iTemp = i + 1;
                    jTemp = j;
                    lTemp = l;
                    cTemp = c;
                }
            }

            // set the best offsets for v's incident original edges
            for (int i = 0; i <= lTemp - 1; i++) {
                this.offset.put(edgesToV.get(i), jTemp);
            }
            for (int i = lTemp; i <= edgesToV.size() - 1; i++) {
                this.offset.put(edgesToV.get(i), jTemp + 1);
            }

            // move v to the best position
            level2.add(iTemp, level2.remove(level2.indexOf(v)));
        }

        return level2;
    }

    /**
     * computes the change of crossings between horizontal edges by swapping v
     * and v2
     * 
     * @param v
     *            first swapping node
     * @param v2
     *            second swapping node
     * @param nodes
     *            nodes of current swapping level
     */
    private int siftingSwapH(Node v, Node v2, LinkedList<Node> nodes) {

        // get incident edges of v and v2 - to get the order according to the
        // reference vertex, append the left list to the right
        LinkedList<Edge> hv = new LinkedList<Edge>();
        hv.addAll(this.hrEdges.get(v));
        hv.addAll(this.hlEdges.get(v));
        LinkedList<Edge> hv2 = new LinkedList<Edge>();
        hv2.addAll(this.hrEdges.get(v2));
        hv2.addAll(this.hlEdges.get(v2));

        // get adjacent nodes
        LinkedList<Node> vAdj = new LinkedList<Node>();
        LinkedList<Node> v2Adj = new LinkedList<Node>();

        // get adjacent nodes of v except v2
        for (Edge e : hv) {
            if (e.getSource().equals(v) && !e.getTarget().equals(v2)) {
                vAdj.add(e.getTarget());
            } else if (e.getTarget().equals(v) && !e.getSource().equals(v2)) {
                vAdj.add(e.getSource());
            }
        }

        // get adjacent nodes of v2 except v
        for (Edge e : hv2) {
            if (e.getSource().equals(v2) && !e.getTarget().equals(v)) {
                v2Adj.add(e.getTarget());
            } else if (e.getTarget().equals(v2) && !e.getSource().equals(v)) {
                v2Adj.add(e.getSource());
            }
        }

        // initialize change in crossing count (c) and help variables (i, j) and
        // number of nodes (r, s)
        int c = 0, i = 0, j = 0, r = 0, s = 0, n = 0;
        r = vAdj.size();
        s = v2Adj.size();
        n = nodes.size();

        // algorithm circular sifting (Baur/Brandes)
        while (i < r && j < s) {

            if ((nodes.indexOf(vAdj.get(i)) - nodes.indexOf(v2) + n) % n < (nodes
                    .indexOf(v2Adj.get(j))
                    - nodes.indexOf(v2) + n)
                    % n) {
                c = c - (s - j);
                i++;
            } else if ((nodes.indexOf(v2Adj.get(j)) - nodes.indexOf(v2) + n)
                    % n < (nodes.indexOf(vAdj.get(i)) - nodes.indexOf(v2) + n)
                    % n) {
                c = c + (r - i);
                j++;
            } else {
                c = c - (s - j) + (r - i);
                i++;
                j++;
            }
        }

        return c;
    }

    /**
     * computes the change of crossings between horizontal and vertical edges by
     * swapping v and v2
     * 
     * @param v
     *            first swapping node
     * @param v2
     *            second swapping node
     * @param x
     *            number of edges between v and v2
     * @return change in crossing count
     */
    private int siftingSwapHV(Node v, Node v2, int x) {

        // get sizes of left and right horizontal edgelists
        int hrAdjV = this.hrEdges.get(v).size();
        int hlAdjV = this.hlEdges.get(v).size();
        int hrAdjV2 = this.hrEdges.get(v2).size();
        int hlAdjV2 = this.hlEdges.get(v2).size();

        // get sizes of vertical edgelists
        int vAdjV = this.vEdges.get(v).size();
        int vAdjV2 = this.vEdges.get(v2).size();

        // computes change in crossing count
        return ((hrAdjV2 - x) - hlAdjV2) * vAdjV + ((hlAdjV - x) - hrAdjV)
                * vAdjV2;
    }

    /**
     * stores left and right horizontal edges for each node ordered ascending by
     * nodes of level 2 in this.hlEdges and this.hrEdges
     * 
     * @param level2
     *            nodes of level 2
     * @param horizontalEdges
     *            all horizontal edges within level 2
     */
    private void storeAdjacentHorizontalEdges(LinkedList<Node> level2,
            LinkedList<Edge> horizontalEdges) {

        // get horizontal edges for each node
        for (Node v : level2) {

            // lists where incident edges of v are stored (left and right)
            LinkedList<Edge> hl = new LinkedList<Edge>();
            LinkedList<Edge> hr = new LinkedList<Edge>();

            // get number of nodes and position of reference vertex
            int n = level2.size();
            int s = level2.indexOf(v);

            // put each adjacent edge of v in the according list - left(hl) or
            // right(hr) - to get short edges
            for (Edge e : horizontalEdges) {

                // get the adjacent node u
                Node u = null;
                if (e.getSource().equals(v)) {
                    u = e.getTarget();
                } else if (e.getTarget().equals(v)) {
                    u = e.getSource();
                }

                // if e is incident to v
                if (u != null) {

                    // get the according list - is u in the first half of the
                    // circular order according to v put it in the right list,
                    // is it in the second half, put it in the left list
                    LinkedList<Edge> list = new LinkedList<Edge>();
                    if ((level2.indexOf(u) - s + n) % n <= n / 2) {
                        list = hr;
                    } else {
                        list = hl;
                    }

                    // insert the edge e at the right position according to the
                    // current layout of level2
                    boolean inserted = false;
                    for (Edge edge : list) {

                        // get the adjacent node w of v of the current edge
                        Node w = null;
                        if (edge.getSource().equals(v)) {
                            w = edge.getTarget();
                        } else if (edge.getTarget().equals(v)) {
                            w = edge.getSource();
                        }

                        // is the position of u is smaller than position of w
                        // concerning reference vertex s - add e at current
                        // index of edge
                        if ((level2.indexOf(u) - s + n) % n < (level2
                                .indexOf(w)
                                - s + n)
                                % n) {
                            list.add(list.indexOf(edge), e);
                            inserted = true;
                            break;
                        }
                    }

                    // if e is not inserted - it has to be placed at end of list
                    if (!inserted) {
                        list.addLast(e);
                    }

                }
            }

            // store lists with incident edges
            this.hlEdges.put(v, hl);
            this.hrEdges.put(v, hr);
        }
    }

    /**
     * store vertical edges between level 1 and level 2 ordered ascending by
     * level1 for each node in this.vEdges
     * 
     * @param level1
     *            nodes of level 1
     * @param level2
     *            nodes of level 2
     */
    private void storeAdjacentVerticalEdges(LinkedList<Node> level1,
            LinkedList<Node> level2) {

        // store vertical edges between level 1 and 2 ordered ascending by
        // level1
        for (Node v : level2) {
            LinkedList<Edge> edges = new LinkedList<Edge>();
            for (Node u : level1) {
                edges.addAll(this.graph.getEdges(u, v));
            }
            this.vEdges.put(v, edges);
        }
    }

    /**
     * compute number of edges between v and v2 (0 or 1 because multiedges are
     * not allowed) and update horizontal edges of v when swapping v and v2 (v2
     * has not to be updated, because it is not used any more)
     * 
     * @param v
     *            first swapping node
     * @param v2
     *            second swapping node
     * @return number of edges between v and v2
     */
    private int updateHAdj(LinkedList<Node> level2, Node v, Node v2) {

        // get left and right adjacency lists of v
        LinkedList<Edge> hrV = this.hrEdges.get(v);
        LinkedList<Edge> hlV = this.hlEdges.get(v);

        // v moves around the circle - so it has to be tested if the first
        // adjacent node of the left list still has to be there or if it should
        // be moved to the right list
        if (!hlV.isEmpty()) {

            // get the first edge
            Edge e = hlV.getFirst();

            // get the adjacent node u
            Node u = null;
            if (e.getSource().equals(v)) {
                u = e.getTarget();
            } else if (e.getTarget().equals(v)) {
                u = e.getSource();
            }

            // get n the number of nodes of the level
            int n = level2.size();

            // if necessary - move the first edge of left list to the end of the
            // right list
            if ((level2.indexOf(u) - level2.indexOf(v) + n) % n < n / 2) {
                hlV.removeFirst();
                hrV.addLast(e);
            }
        }

        // if there is an edge between v and v2 - move the first edge from the
        // rigth list to the end of the left list
        if (!hrV.isEmpty()) {
            Edge e = hrV.getFirst();

            if (e.getTarget().equals(v) && e.getSource().equals(v2)
                    || e.getTarget().equals(v2) && e.getSource().equals(v)) {
                e = hrV.removeFirst();
                hlV.addLast(e);

                // store adjacent lists
                this.hrEdges.put(v, hrV);
                this.hlEdges.put(v, hlV);

                // return 1 because there was an edge between v and v2
                return 1;
            }
        }

        // store adjacent lists
        this.hrEdges.put(v, hrV);
        this.hlEdges.put(v, hlV);

        // return o because there was no edge between v and v2
        return 0;
    }

    /**
     * computes the number of all crossings (vertical, horizontal and
     * vertical/horizontal) of the current layout of the graph
     * 
     * @param order
     *            the current layout
     * @return all crossings of the current layout
     */
    private int countAllCrossings(LinkedList<Node>[] order) {
        int crossings = 0;

        crossings += countHorizontalCrossings(order);
        crossings += countAllVerticalCrossings(order);
        crossings += countHVCrossings(order);

        return crossings;
    }

    /**
     * computes the number of all horizontal crossings of the current layout
     * 
     * @param order
     *            the current layout
     * @return all horizontal crossings of the current layout
     */
    private int countHorizontalCrossings(LinkedList<Node>[] order) {

        int c = 0;
        for (int i = 0; i < order.length; ++i) {

            // size of current level
            int n = order[i].size();

            // initialize variables to store index
            int u1, u2, v1, v2 = 0;

            // get horizontal edges of current level
            LinkedList<Edge> edges = getHorizontalEdges(order[i]);

            // consider each pair of edges once
            for (int j = 0; j < edges.size() - 1; j++) {
                for (int h = j + 1; h < edges.size(); h++) {

                    // get current edges
                    Edge e1 = edges.get(j);
                    Edge e2 = edges.get(h);

                    // get index of current source and target node of first edge
                    // and store the smaller one in u1 the bigger one in v1
                    if (order[i].indexOf(e1.getSource()) < order[i].indexOf(e1
                            .getTarget())) {
                        u1 = order[i].indexOf(e1.getSource());
                        v1 = order[i].indexOf(e1.getTarget());
                    } else {
                        u1 = order[i].indexOf(e1.getTarget());
                        v1 = order[i].indexOf(e1.getSource());
                    }

                    // get index of current source and target node of second
                    // edge and store the smaller one in u2 the bigger one in v2
                    if (order[i].indexOf(e2.getSource()) < order[i].indexOf(e2
                            .getTarget())) {
                        u2 = order[i].indexOf(e2.getSource());
                        v2 = order[i].indexOf(e2.getTarget());
                    } else {
                        u2 = order[i].indexOf(e2.getTarget());
                        v2 = order[i].indexOf(e2.getSource());
                    }

                    // if the nodes of the two edges alternate in the current
                    // layout (u1 < u2 < v1 < v2) there is a crossing, so add 1
                    if ((u1 - u1 + n) % n < (u2 - u1 + n) % n
                            && (u2 - u1 + n) % n < (v1 - u1 + n) % n
                            && (v1 - u1 + n) % n < (v2 - u1 + n) % n) {
                        c += 1;
                    }
                }
            }
        }
        return c;
    }

    /**
     * computes the number of all crossings between horizontal and vertical
     * edges of the current layout
     * 
     * @param order
     *            the current layout
     * @return all horizontal/vertical crossings of the current layout
     */
    private int countHVCrossings(LinkedList<Node>[] order) {

        int c = 0;
        for (int i = 0; i < order.length - 1; i++) {
            storeAdjacentVerticalEdges(order[i], order[i + 1]);
            LinkedList<Edge> hEdges = getHorizontalEdges(order[i + 1]);

            for (Edge e : hEdges) {

                // store index of current nodes in i1 and i2 so that i1 contains
                // the smaller index
                int i1 = order[i + 1].indexOf(e.getTarget());
                int i2 = order[i + 1].indexOf(e.getSource());
                if (i2 < i1) {
                    i1 = order[i + 1].indexOf(e.getSource());
                    i2 = order[i + 1].indexOf(e.getTarget());
                }

                // set start and end index, so that running from start to end is
                // shorter (radial)
                int start = 0;
                int end = 0;
                int n = order[i + 1].size();
                if ((i1 - i2 + n) % n < (i2 - i1 + n) % n) {
                    if (i1 - i2 > 0) {
                        start = i1;
                        end = i2;
                    } else {
                        start = i2;
                        end = i1;
                    }
                } else if ((i1 - i2 + n) % n > (i2 - i1 + n) % n) {
                    if (i2 - i1 > 0) {
                        start = i1;
                        end = i2;
                    } else {
                        start = i2;
                        end = i1;
                    }
                } else {
                    start = i1;
                    end = i2;
                }

                Node v = null;

                // count crossings for nodes between start and end
                if (start < end) {

                    // no cut edge - just run from start to end
                    for (int j = start + 1; j < end; j++) {
                        v = order[i + 1].get(j);
                        c += this.vEdges.get(v).size();
                    }
                } else {

                    // cut edge - run from start to n and from 0 to end
                    for (int j = start + 1; j < n; j++) {
                        v = order[i + 1].get(j);
                        c += this.vEdges.get(v).size();
                    }
                    for (int j = 0; j < end; j++) {
                        v = order[i + 1].get(j);
                        c += this.vEdges.get(v).size();
                    }
                }
            }
        }

        return c;
    }

    /**
     * computes the number of all vertical crossings of the current layout
     * 
     * @param order
     *            the current layout
     * @return all vertical crossings of the current layout
     */
    private int countAllVerticalCrossings(LinkedList<Node>[] order) {

        int c = 0;
        for (int i = 0; i < order.length - 1; i++) {

            // get vertical edges between level1 and level2
            LinkedList<Edge> edges = getVerticalEdges(order[i], order[i + 1]);

            // consider each pair of edges once
            for (int j = 0; j < edges.size() - 1; j++) {
                for (int h = j + 1; h < edges.size(); h++) {
                    Edge e1 = edges.get(j);
                    Edge e2 = edges.get(h);

                    // add the number of crossings between the two edges
                    c += crossingsBetweenTwoEdges(e1, e2, order[i],
                            order[i + 1]);
                }
            }
        }

        return c;
    }

    /**
     * counts crossings between the edge and all other edges
     */
    private int countVerticalCrossings(Edge edge, Collection<Edge> edges,
            LinkedList<Node> level1, LinkedList<Node> level2) {

        int crossings = 0;

        // sum up of crossings of the edge and all other edges
        for (Edge e : edges) {
            crossings = crossings
                    + crossingsBetweenTwoEdges(e, edge, level1, level2);
        }

        return crossings;
    }

    /**
     * counts crossings between the edges of the two lists of edges
     */
    private int countVerticalCrossings(LinkedList<Edge> edgesNode1,
            Collection<Edge> edgesNode2, LinkedList<Node> level1,
            LinkedList<Node> level2) {

        int crossings = 0;

        // sum up crossings of edges of first list and edges of second list
        for (Edge e1 : edgesNode1) {
            for (Edge e2 : edgesNode2) {
                crossings = crossings
                        + crossingsBetweenTwoEdges(e1, e2, level1, level2);
            }
        }

        return crossings;
    }

    /**
     * counts crossings between the two edges
     */
    private int crossingsBetweenTwoEdges(Edge e1, Edge e2,
            LinkedList<Node> level1, LinkedList<Node> level2) {

        // get source and target nodes of the two edges
        Node u1, u2, v1, v2;
        if (level1.contains(e1.getSource())) {
            u1 = e1.getSource();
            v1 = e1.getTarget();
        } else {
            v1 = e1.getSource();
            u1 = e1.getTarget();
        }
        if (level1.contains(e2.getSource())) {
            u2 = e2.getSource();
            v2 = e2.getTarget();
        } else {
            v2 = e2.getSource();
            u2 = e2.getTarget();
        }

        int a, b; // help variables
        a = (int) Math.signum(level1.indexOf(u2) - level1.indexOf(u1));
        b = (int) Math.signum(level2.indexOf(v2) - level2.indexOf(v1));

        // return crossing count
        return Math.max(0, Math.abs(this.offset.get(e2) - this.offset.get(e1)
                + (b - a) / 2)
                + (Math.abs(a) + Math.abs(b)) / 2 - 1);
    }

    /**
     * returns all horizontal edges of current level
     * 
     * @param level2
     *            current level
     * @return all horizontal edges of current level
     */
    private LinkedList<Edge> getHorizontalEdges(LinkedList<Node> level2) {

        LinkedList<Edge> horizontalEdges = new LinkedList<Edge>();
        for (Node u : level2) {
            for (Node v : level2) {
                Collection<Edge> edges = this.graph.getEdges(u, v);
                for (Edge e : edges) {
                    if (!horizontalEdges.contains(e)) {
                        horizontalEdges.add(e);
                    }
                }
            }
        }

        return horizontalEdges;
    }

    /**
     * returns all vertical edges between current levels
     * 
     * @param level1
     *            current upper level
     * @param level2
     *            current lower level
     * @return vertical edges between current levels
     */
    private LinkedList<Edge> getVerticalEdges(LinkedList<Node> level1,
            LinkedList<Node> level2) {

        LinkedList<Edge> verticalEdges = new LinkedList<Edge>();
        for (Node v : level2) {
            for (Node u : level1) {
                verticalEdges.addAll(this.graph.getEdges(u, v));
            }
        }

        return verticalEdges;
    }

    /**
     * minimize the edge lengths by rotating the graph
     */
    private void minimizeEdgeLengths() {

        // run from inner to outer level
        for (int i = 0; i < this.order.length - 1; ++i) {

            // the angle between two vertices if uniformly distributed
            double innerAngleIncrement = 2.0 * Math.PI / this.order[i].size();
            double outerAngleIncrement = 2.0 * Math.PI
                    / this.order[i + 1].size();

            // average angle which is spanned by all edges
            double avgSpannedAngle = 0.0;
            double outerAngle = 0.0;
            int edgeCount = 0;

            // each node on current outer level
            for (Node n : this.order[i + 1]) {

                // get edges to current inner level
                for (Node v : this.order[i]) {
                    for (Edge e : this.graph.getEdges(n, v)) {

                        ++edgeCount;
                        double innerAngle = this.order[i].indexOf(v)
                                * innerAngleIncrement;

                        // sum up all the spanned angles
                        avgSpannedAngle += (innerAngle - outerAngle)
                                + (-this.offset.get(e) * 2.0 * Math.PI);
                    }
                }
                outerAngle += outerAngleIncrement;
            }

            // calculate the average spanned angle
            avgSpannedAngle /= edgeCount;

            // calculate how far the outer level should be rotated
            int rotation = (int) Math.round(avgSpannedAngle
                    / outerAngleIncrement);

            // rotate level i+1
            rotate(i + 1, rotation);

            // special case: just one node
            if (this.order[i + 1].size() == 1) {
                for (Node u : this.order[i + 1]) {
                    for (Edge e : u.getEdges()) {
                        this.offset.put(e, 0);
                    }
                }
            }
        }
    }

    /**
     * rotates the current level clockwise or counter-clockwise depending on the
     * sign of param rotation
     * 
     * @param level
     *            current level
     * @param rotation
     *            number of positions, which should be rotated
     */
    private void rotate(int level, int rotation) {

        // clockwise
        if (rotation > 0) {
            for (int i = 0; i < rotation; i++) {
                Node v = this.order[level].removeLast();
                this.order[level].addFirst(v);

                // update offsets of outgoing edges
                if (level + 1 < this.order.length) {
                    for (Node u : this.order[level + 1]) {
                        LinkedList<Edge> edges = (LinkedList<Edge>) this.graph
                                .getEdges(u, v);
                        for (Edge e : edges) {
                            this.offset.put(e, this.offset.get(e) - 1);
                        }
                    }
                }

                // update offsets of incoming edges
                for (Node w : this.order[level - 1]) {
                    LinkedList<Edge> edges = (LinkedList<Edge>) this.graph
                            .getEdges(v, w);
                    for (Edge e : edges) {

                        this.offset.put(e, this.offset.get(e) + 1);

                    }
                }
            }
        }

        // counter-clockwise
        if (rotation < 0) {
            for (int i = rotation; i < 0; i++) {
                Node v = this.order[level].removeFirst();
                this.order[level].addLast(v);

                // update offsets of outgoing edges
                if (level + 1 < this.order.length) {
                    for (Node u : this.order[level + 1]) {
                        LinkedList<Edge> edges = (LinkedList<Edge>) this.graph
                                .getEdges(u, v);
                        for (Edge e : edges) {
                            this.offset.put(e, this.offset.get(e) + 1);
                        }
                    }
                }

                // update offsets of incoming edges
                for (Node w : this.order[level - 1]) {
                    LinkedList<Edge> edges = (LinkedList<Edge>) this.graph
                            .getEdges(v, w);
                    for (Edge e : edges) {

                        this.offset.put(e, this.offset.get(e) - 1);

                    }
                }
            }
        }
    }

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
