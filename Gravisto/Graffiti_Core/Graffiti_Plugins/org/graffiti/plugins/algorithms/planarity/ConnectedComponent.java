package org.graffiti.plugins.algorithms.planarity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * The main class of the algorithm. Performs the planarity test on a connected
 * component.
 * 
 * @author Wolfgang Brunner
 */
public class ConnectedComponent {

    /**
     * The number of nodes in the connected component
     */
    public int numberOfNodes;

    /**
     * The number of edges in the connected component
     */
    public int numberOfEdges;

    /**
     * The number of already embedded edges in the connected component
     */
    public int numberOfEmbeddedEdges;

    /**
     * <code>true</code> if the connected component is planar
     */
    public boolean planar;

    /**
     * A list of the <code>RealNode</code> objects
     */
    public List<RealNode> realNodes;

    /**
     * The root of the depth first search tree in the connected component
     */
    public Node root;

    /**
     * The depth first search index of the root
     */
    public int DFSStartNumber;

    /**
     * Internal counter used while building the depth first search tree
     */
    private int currentDFSNumber;

    /**
     * The mapping between <code>org.graffiti.graph.Node</code> and
     * <code>RealNode</code> objects
     */
    public Map<Node, RealNode> map;

    /**
     * Determines whether the graph has already been tested
     */
    private boolean planarityTested;

    /**
     * The list of <code>Bicomp</code> objects belonging to the connected
     * component
     */
    public List<Bicomp> bicomps;

    /**
     * The <code>org.graffiti.graph.Graph</code> containing the connected
     * component
     */
    private Graph graph;

    /**
     * The number of loops in the connected component
     */
    public int loops;

    /**
     * The number of double edges in the connected component
     */
    public int doubleEdges;

    /**
     * If set to <code>true</code> the result of the planarity test is printed
     * as a text and the nodes and edges of the Kuratowski subgraph get colored
     */
    public boolean GUIMode;

    /**
     * Constructs a new <code>ConnectedComponent</code>
     * 
     * @param root
     *            The root of the depth first search tree of the connected
     *            component
     * @param DFSStartNumber
     *            The depth first search index of the root
     * @param map
     *            The mapping between <code>org.graffiti.graph.Node</code> and
     *            <code>RealNode</code> objects
     * @param graph
     *            The <code>org.graffiti.graph.Graph</code>
     * @param GUIMode
     *            Specifies whether the nodes and edges get colored
     */
    public ConnectedComponent(Node root, int DFSStartNumber,
            Map<Node, RealNode> map, Graph graph, boolean GUIMode) {
        this.GUIMode = GUIMode;
        numberOfNodes = 0;
        numberOfEdges = 0;
        numberOfEmbeddedEdges = 0;
        planar = true;
        realNodes = new ArrayList<RealNode>();
        this.root = root;
        this.DFSStartNumber = DFSStartNumber;
        this.map = map;
        planarityTested = false;
        bicomps = new LinkedList<Bicomp>();
        this.graph = graph;
        loops = 0;
        doubleEdges = 0;
    }

    /**
     * Adds a new <code>RealNode</code> to the connected component
     * 
     * @param originalNode
     *            The <code>org.graffiti.graph.Node</code> matching the new node
     * 
     * @return The new <code>RealNode</code>
     */
    public RealNode addRealNode(Node originalNode) {
        RealNode node = new RealNode(originalNode, DFSStartNumber);
        realNodes.add(node);
        numberOfNodes++;
        map.put(originalNode, node);
        return node;
    }

    /**
     * Checks whether the node is inactive (not pertinent and not externally
     * active)
     * 
     * @param w
     *            The node to test
     * @param current
     *            The node in processing
     * 
     * @return <code>true</code> if the node is inactive
     */
    public boolean inActive(RealNode w, RealNode current) {
        return (!pertinent(w, current) && !externallyActive(w, current));
    }

    /**
     * Checks whether the node is internally active (pertinent and not
     * externally active)
     * 
     * @param w
     *            The node to test
     * @param current
     *            The node in processing
     * 
     * @return <code>true</code> if the node is internally active
     */
    public boolean internallyActive(RealNode w, RealNode current) {
        return (pertinent(w, current) && !externallyActive(w, current));
    }

    /**
     * Checks whether the node is pertinent
     * 
     * @param w
     *            The node to test
     * @param current
     *            The node in processing
     * 
     * @return <code>true</code> if the node is pertinent
     */
    public boolean pertinent(RealNode w, RealNode current) {
        return ((w.backedgeTarget == current) || !w.pertinentRoots.isEmpty());
    }

    /**
     * Checks whether the node is externally active
     * 
     * @param w
     *            The node to test
     * @param current
     *            The node in processing
     * 
     * @return <code>true</code> if the node is externally active
     */
    public boolean externallyActive(RealNode w, RealNode current) {
        if (w.leastAncestor < current.DFI)
            return true;
        if (w.separatedDFSChildList.isEmpty())
            return false;
        return (w.separatedDFSChildList.getFirst().lowPoint < current.DFI);
    }

    /**
     * Searches the next <code>Position</code> on the external face of a
     * biconnected component
     * 
     * @param p
     *            The start <code>Position</code>
     * @return The next <code>Position</code>
     */
    public Position getSuccessorOnExternalFace(Position p) {
        int nextIn = 0;
        ArbitraryNode current = p.pos;
        int currentIn = p.direction;
        HalfEdge e = (HalfEdge) current.link[1 - currentIn];
        ArbitraryNode next = e.to;
        if (current.degree == 1) {
            nextIn = currentIn;
        } else if (((HalfEdge) next.link[0]).to == current) {
            nextIn = 0;
        } else {
            nextIn = 1;
        }
        return new Position(next, nextIn);
    }

    /**
     * Searches the next <code>Position</code> on the external face of a
     * biconnected component whose node is pertinent or externally active
     * 
     * @param p
     *            The start <code>Position</code>
     * @return The next <code>Position</code>
     */
    public Position getActiveSuccessorOnExternalFace(Position p,
            RealNode current) {
        Position result = getSuccessorOnExternalFace(p);
        while (inActive(result.pos.getRealNode(), current)) {
            result = getSuccessorOnExternalFace(result);
            if (result.pos == p.pos)
                return null;
        }
        return result;
    }

    /**
     * Performes a depth first search from the given node
     * 
     * @param current
     *            The node to start the depth first search from
     * @param parent
     *            The parent of the current node
     * @return The lowpoint of the node
     */
    private int depthFirstSearch(Node current, Node parent) {
        RealNode pCurrentNode = addRealNode(current);
        RealNode pParentNode = map.get(parent);
        pCurrentNode.DFSParent = pParentNode;

        pCurrentNode.DFI = currentDFSNumber;
        currentDFSNumber++;

        int lowPoint = pCurrentNode.DFI;
        int leastAncestor = pCurrentNode.DFI;

        for (Iterator<Node> neighbours = current.getNeighborsIterator(); neighbours
                .hasNext();) {
            numberOfEdges++;
            Node neighbour = neighbours.next();
            if (!map.containsKey(neighbour)) {
                int l = depthFirstSearch(neighbour, current);
                if (l < lowPoint) {
                    lowPoint = l;
                }
            } else {
                RealNode pNeighbourNode = map.get(neighbour);
                if (pNeighbourNode != pParentNode) {
                    int l = pNeighbourNode.DFI;
                    if (l < lowPoint) {
                        lowPoint = l;
                    }
                    if (l < leastAncestor) {
                        leastAncestor = l;
                    }
                }

            }
        }
        pCurrentNode.leastAncestor = leastAncestor;
        pCurrentNode.lowPoint = lowPoint;
        return lowPoint;
    }

    /**
     * Copys the edges from the original <code>org.graffiti.graph.Graph</code>
     * to this connected component. Loops and double edges are removed thereby.
     */
    private void createAdjacencyLists() {
        HalfEdge[] edges = new HalfEdge[numberOfNodes];
        for (int i = 0; i < numberOfNodes; i++) {
            edges[i] = null;
        }
        for (Iterator<RealNode> i = realNodes.iterator(); i.hasNext();) {
            RealNode node = i.next();
            Node current = node.originalNode;
            for (Iterator<Edge> j = current.getEdgesIterator(); j.hasNext();) {
                Edge edge = j.next();
                Node neighbour;
                if (edge.getSource() == current) {
                    neighbour = edge.getTarget();
                } else {
                    neighbour = edge.getSource();
                }
                RealNode nNode = map.get(neighbour);
                if (node != nNode) {

                    HalfEdge e = edges[nNode.DFI];
                    if ((e != null) && (e.from == node) && (e.to == nNode)) {
                        node.doubleEdgeTargets.add(neighbour);
                        doubleEdges++;
                    } else {
                        edges[nNode.DFI] = new HalfEdge(node, nNode, false);
                        node.completeAdjacencyList.add(nNode);
                    }
                } else {
                    node.loops++;
                    loops++;
                }
            }
        }
        doubleEdges /= 2;
    }

    /**
     * Fills the <code>DFSChildList</code> objects
     */
    private void createDFSChildLists() {
        Vector<LinkedList<RealNode>> buckets = new Vector<LinkedList<RealNode>>(
                numberOfNodes);
        for (int i = 0; i < numberOfNodes; i++) {
            buckets.add(i, new LinkedList<RealNode>());
        }
        for (int i = 0; i < numberOfNodes; i++) {
            RealNode pNode = realNodes.get(i);
            buckets.get(pNode.lowPoint).add(pNode);
        }
        for (int i = 0; i < numberOfNodes; i++) {
            for (Iterator<RealNode> nodes = buckets.get(i).iterator(); nodes
                    .hasNext();) {
                RealNode pNode = nodes.next();
                if (pNode.DFSParent != null) {
                    pNode.DFSParent.separatedDFSChildList.add(pNode);
                }
            }
        }
    }

    /**
     * Creates the data structures for each tree edge which are stored as
     * biconnected component
     */
    private void createTreeEdgeBicomps() {
        for (int i = 0; i < numberOfNodes; i++) {
            RealNode pNode = realNodes.get(i);
            if (pNode.DFSParent == null) {
                continue;
            }
            VirtualNode vNode;
            vNode = new VirtualNode(pNode, DFSStartNumber);
            pNode.virtualParent = vNode;
            HalfEdge e1 = new HalfEdge(vNode, pNode, false);
            HalfEdge e2 = new HalfEdge(pNode, vNode, false);
            e1.twin = e2;
            e2.twin = e1;
            pNode.link[0] = e2;
            pNode.link[1] = e2;
            vNode.link[0] = e1;
            vNode.link[1] = e1;
            vNode.edgeToChild = e1;
            e1.link[0] = vNode;
            e1.link[1] = vNode;
            e2.link[0] = pNode;
            e2.link[1] = pNode;
            numberOfEmbeddedEdges++;
        }
    }

    /**
     * Creates a <code>Bicomp</code> object with the given root
     * 
     * @param vNode
     *            The root of the biconnected component
     */
    private void addBicomp(VirtualNode vNode) {
        Bicomp bicomp = new Bicomp(this, graph);
        bicomp.addNode(vNode);
        vNode.bicomp = bicomp;
        List<RealNode> queue = new LinkedList<RealNode>();
        queue.add(vNode.child);
        bicomp.numberOfEmbeddedEdges++;
        while (!queue.isEmpty()) {
            RealNode node = queue.remove(0);
            bicomp.addNode(node);
            node.bicomp = bicomp;
            List<RealNode> childs = node.separatedDFSChildList.getList();
            for (Iterator<RealNode> i = childs.iterator(); i.hasNext();) {
                RealNode rNode = i.next();
                if (rNode.lowPoint >= node.DFI) {
                    addBicomp(rNode.virtualParent);
                } else {
                    queue.add(rNode);
                    bicomp.numberOfEmbeddedEdges++;
                }
            }
        }
        for (Iterator<ArbitraryNode> i = bicomp.nodes.iterator(); i.hasNext();) {
            ArbitraryNode aNode = i.next();

            RealNode node = aNode.getRealNode();
            for (Iterator<RealNode> neighbours = node.completeAdjacencyList
                    .iterator(); neighbours.hasNext();) {
                ArbitraryNode nNode = neighbours.next();
                if (vNode.parent == nNode) {
                    nNode = vNode;
                }
                if ((nNode.bicomp == bicomp) && (aNode.bicomp == bicomp)) {
                    bicomp.numberOfEdges++;
                }
            }
        }
        bicomp.numberOfEdges /= 2;
        bicomps.add(bicomp);
    }

    /**
     * Splits the connected component in biconnected components
     */
    private void createResultBicomps() {
        bicomps = new LinkedList<Bicomp>();
        RealNode rNode = realNodes.get(0);
        List<RealNode> list = rNode.separatedDFSChildList.getList();
        for (Iterator<RealNode> i = list.iterator(); i.hasNext();) {

            RealNode current = i.next();
            VirtualNode vNode = current.virtualParent;
            addBicomp(vNode);
        }
    }

    /**
     * Sets the <code>visited</code> flag in all nodes in the list
     * 
     * @param nodes
     *            The list of nodes
     * @param vNode
     *            The value to set the flag to
     */
    private void setVisited(List<ArbitraryNode> nodes, VirtualNode vNode) {
        for (Iterator<ArbitraryNode> i = nodes.iterator(); i.hasNext();) {
            ArbitraryNode aNode = i.next();
            aNode.visited = vNode;
        }
    }

    /**
     * Performs a Walk Up
     * 
     * @param v
     *            The node in processing
     * @param w
     *            The start of the Walk Up
     */
    private void walkUp(RealNode v, RealNode w) {
        List<ArbitraryNode> visitedNodes = new LinkedList<ArbitraryNode>();
        VirtualNode lastVirtualNode = null;
        w.backedgeTarget = v;
        Position xPos = new Position(w, 1);
        Position yPos = new Position(w, 0);
        while ((xPos.pos != v) && (yPos.pos != v)) {
            if (xPos.pos.visited != null) {
                if (xPos.pos.visited.getRealNode() == v) {
                    xPos.pos.visited.unembeddedBackEdges++;
                    setVisited(visitedNodes, xPos.pos.visited);
                    return;
                }
            }
            if (yPos.pos.visited != null) {
                if (yPos.pos.visited.getRealNode() == v) {
                    yPos.pos.visited.unembeddedBackEdges++;
                    setVisited(visitedNodes, yPos.pos.visited);
                    return;
                }
            }
            visitedNodes.add(xPos.pos);
            visitedNodes.add(yPos.pos);

            VirtualNode z2 = null;
            if (xPos.pos instanceof VirtualNode) {
                z2 = (VirtualNode) xPos.pos;
            } else if (yPos.pos instanceof VirtualNode) {
                z2 = (VirtualNode) yPos.pos;
            }
            if (z2 != null) {
                lastVirtualNode = z2;
                RealNode c = z2.child;
                RealNode z = z2.parent;
                if (z != v) {
                    if (!z2.inPertinentRoots) {
                        z2.inPertinentRoots = true;
                        if (c.lowPoint < v.DFI) {
                            z.pertinentRoots.addLast(z2);
                        } else {
                            z.pertinentRoots.addFirst(z2);
                        }
                    }
                }
                xPos = new Position(z, 1);
                yPos = new Position(z, 0);

            } else {
                xPos = getSuccessorOnExternalFace(xPos);
                yPos = getSuccessorOnExternalFace(yPos);
            }
        }
        lastVirtualNode.unembeddedBackEdges++;
        setVisited(visitedNodes, lastVirtualNode);
    }

    /**
     * Merges two biconnected components
     * 
     * @param mergeStack
     *            Contains information to merge the components correctly
     */
    private void mergeBiconnectedComponent(Stack<Position> mergeStack) {
        Position r2Pos = mergeStack.pop();
        Position rPos = mergeStack.pop();
        if (rPos.direction == r2Pos.direction) {
            VirtualNode vNode = (VirtualNode) r2Pos.pos;
            vNode.swapAdjacencyList();
            vNode.edgeToChild.sign = -1;
            r2Pos.direction = 1 - r2Pos.direction;
        }
        ArbitraryNode node = r2Pos.pos;
        AdjacencyListLink last = node;
        AdjacencyListLink current = node.link[0];
        while (current != node) {
            HalfEdge currentEdge = (HalfEdge) current;
            currentEdge.from = rPos.pos;
            currentEdge.twin.to = rPos.pos;
            if (current.link[0] == last) {
                last = current;
                current = current.link[1];
            } else {
                last = current;
                current = current.link[0];
            }
        }
        if (rPos.pos.getRealNode().pertinentRoots.getFirst() != r2Pos.pos) {
            System.out.println("Deleted wrong node from pertinentRoots");
        }
        rPos.pos.getRealNode().pertinentRoots.removeFirst();
        RealNode child = ((VirtualNode) (r2Pos.pos)).child;
        RealNode parent = ((VirtualNode) (r2Pos.pos)).parent;
        parent.separatedDFSChildList.remove(child);

        HalfEdge e1 = (HalfEdge) rPos.pos.link[rPos.direction];
        if (e1.link[0] == rPos.pos) {
            e1.link[0] = r2Pos.pos.link[r2Pos.direction];
        } else {
            e1.link[1] = r2Pos.pos.link[r2Pos.direction];
        }
        HalfEdge e2 = (HalfEdge) r2Pos.pos.link[r2Pos.direction];
        if (e2.link[0] == r2Pos.pos) {
            e2.link[0] = rPos.pos.link[rPos.direction];
        } else {
            e2.link[1] = rPos.pos.link[rPos.direction];
        }
        rPos.pos.link[rPos.direction] = r2Pos.pos.link[1 - r2Pos.direction];
        HalfEdge e3 = (HalfEdge) r2Pos.pos.link[1 - r2Pos.direction];
        if (e3.link[0] == r2Pos.pos) {
            e3.link[0] = rPos.pos;
        } else {
            e3.link[1] = rPos.pos;
        }

        rPos.pos.degree += r2Pos.pos.degree;
    }

    /**
     * Embeds a edge
     * 
     * @param p1
     *            The upper <code>Position</code> to embed the edge to
     * @param p2
     *            The lower <code>Position</code> to embed the edge to
     * @param shortCircuit
     *            The type of the edge
     */
    private void embedEdge(Position p1, Position p2, boolean shortCircuit) {
        if (shortCircuit) {
            ArbitraryNode node1 = p1.pos;
            ArbitraryNode node2 = p2.pos;

            if (node2 == ((HalfEdge) node1.link[0]).to)
                return;
            if (node2 == ((HalfEdge) node1.link[1]).to)
                return;
        }
        if (!shortCircuit) {
            numberOfEmbeddedEdges++;
            if (p1.pos instanceof RealNode) {
                p1.pos.bicomp.numberOfEmbeddedEdges++;
            } else {
                ((VirtualNode) p1.pos).child.bicomp.numberOfEmbeddedEdges++;
            }
        }
        HalfEdge e1 = new HalfEdge(p1.pos, p2.pos, shortCircuit);
        HalfEdge e2 = new HalfEdge(p2.pos, p1.pos, shortCircuit);
        e1.twin = e2;
        e2.twin = e1;

        e1.link[0] = p1.pos;
        e1.link[1] = p1.pos.link[p1.direction];
        HalfEdge next = (HalfEdge) p1.pos.link[p1.direction];
        if (next.link[0] == p1.pos) {
            next.link[0] = e1;
        } else {
            next.link[1] = e1;
        }
        p1.pos.link[p1.direction] = e1;

        e2.link[0] = p2.pos;
        e2.link[1] = p2.pos.link[p2.direction];
        HalfEdge next2 = (HalfEdge) p2.pos.link[p2.direction];
        if (next2.link[0] == p2.pos) {
            next2.link[0] = e2;
        } else {
            next2.link[1] = e2;
        }
        p2.pos.link[p2.direction] = e2;

        p1.pos.degree++;
        p2.pos.degree++;
    }

    /**
     * Performs a Walk Down
     * 
     * @param v2
     *            The node in processing
     * @param child
     *            The child to start the Walk Down from
     */
    public void walkDown(VirtualNode v2, RealNode child) {
        Bicomp bicomp = v2.child.bicomp;
        RealNode current = v2.getRealNode();
        Stack<Position> mergeStack = new Stack<Position>();
        ArbitraryNode v = v2.getRealNode();
        for (int v2Out = 0; v2Out <= 1; v2Out++) {
            Position wPos = getSuccessorOnExternalFace(new Position(v2,
                    1 - v2Out));
            while (wPos.pos != v2) {
                if (wPos.pos.getRealNode().backedgeTarget == v) {
                    while (!mergeStack.empty()) {
                        mergeBiconnectedComponent(mergeStack);
                    }
                    embedEdge(new Position(v2, v2Out), wPos, false);
                    v2.unembeddedBackEdges--;
                    wPos.pos.getRealNode().backedgeTarget = null;
                }
                if (!wPos.pos.getRealNode().pertinentRoots.isEmpty()) {
                    mergeStack.push(new Position(wPos.pos, wPos.direction));
                    VirtualNode w2 = wPos.pos.getRealNode().pertinentRoots
                            .getFirst();
                    Position xPos = null;
                    Position yPos = null;
                    xPos = getActiveSuccessorOnExternalFace(
                            new Position(w2, 1), current);
                    yPos = getActiveSuccessorOnExternalFace(
                            new Position(w2, 0), current);
                    if ((xPos == null) || (yPos == null)) {
                        break;
                    }
                    if (internallyActive(xPos.pos.getRealNode(), current)) {
                        wPos = xPos;
                    } else if (internallyActive(yPos.pos.getRealNode(), current)) {
                        wPos = yPos;
                    } else if (pertinent(xPos.pos.getRealNode(), current)) {
                        wPos = xPos;
                    } else {
                        wPos = yPos;
                    }
                    int w2Out = 1;
                    if (wPos.pos == xPos.pos) {
                        w2Out = 0;
                    }
                    mergeStack.push(new Position(w2, w2Out));
                } else if (inActive(wPos.pos.getRealNode(), current)) {
                    wPos = getSuccessorOnExternalFace(wPos);
                } else {
                    if ((child.lowPoint < current.DFI)
                            && (mergeStack.isEmpty())) {
                        embedEdge(new Position(v2, v2Out), wPos, true);
                    }
                    break;
                }
            }
            if (!mergeStack.isEmpty()) {
                if ((v2.unembeddedBackEdges > 0)
                        && (!bicomp.foundKuratowskiSubgraph())) {
                    bicomp.findKuratowskiSubgraph((VirtualNode) mergeStack
                            .pop().pos, v2);
                    if (GUIMode) {
                        bicomp.kuratowskiSubgraph.markKuratowskiSubgraph();
                    }
                }
                return;
            }
        }
        if ((v2.unembeddedBackEdges > 0) && (!bicomp.foundKuratowskiSubgraph())) {
            bicomp.findKuratowskiSubgraph(v2, v2);
            if (GUIMode) {
                bicomp.kuratowskiSubgraph.markKuratowskiSubgraph();
            }
        }
    }

    /**
     * Gives the result of the planarity test
     * 
     * @return <code>true</code> if the connected component ist planar
     */
    public boolean isPlanar() {
        if (!planarityTested) {
            testPlanarity();
        }
        for (Iterator<Bicomp> i = bicomps.iterator(); i.hasNext();) {
            Bicomp bicomp = i.next();
            if (!bicomp.isPlanar())
                return false;
        }
        return true;
    }

    /**
     * Stores the calculated embedding recursively in the lists
     * <code>adjacencyList</code>
     * 
     * @param node
     *            The node to start
     * @param sign
     *            The orientation of the subtree
     */
    public void buildAdjacencyLists(ArbitraryNode node, int sign) {

        buildAdjacencyList(node, sign);

        AdjacencyListLink last = node;
        int orientation = 0;
        if (sign == -1) {
            orientation = 1;
        }
        AdjacencyListLink current = node.link[orientation];
        while (current != node) {
            HalfEdge edge = (HalfEdge) current;
            if ((!edge.shortCircuitEdge)
                    && (edge.to.getRealNode().DFSParent == node.getRealNode())) {
                buildAdjacencyLists(edge.to, sign * edge.sign);
            }
            if (current.link[0] == last) {
                last = current;
                current = current.link[1];
            } else {
                last = current;
                current = current.link[0];
            }
        }
    }

    /**
     * Stores the calculated embedding of the given node in the list
     * <code>adjacencyList</code>
     * 
     * @param node
     *            The node to process
     * @param sign
     *            The orientation of the subtree
     */
    private void buildAdjacencyList(ArbitraryNode node, int sign) {
        int orientation = 0;
        if (sign == -1) {
            orientation = 1;
        }
        node.adjacencyList = new LinkedList<ArbitraryNode>();
        AdjacencyListLink current = node.link[orientation];
        AdjacencyListLink last = node;
        while (current != node) {
            HalfEdge edge = (HalfEdge) current;
            if (!edge.shortCircuitEdge) {
                node.adjacencyList.add(edge.to);
            }
            if (current.link[0] == last) {
                last = current;
                current = current.link[1];
            } else {
                last = current;
                current = current.link[0];
            }
        }
    }

    /**
     * Tests the planarity of the connected component
     */
    public void testPlanarity() {
        currentDFSNumber = 0;

        depthFirstSearch(root, null);

        if (numberOfNodes == 1) {
            numberOfEdges = 0;
            numberOfEmbeddedEdges = 0;
            planar = true;
            planarityTested = true;
            bicomps = new LinkedList<Bicomp>();
            Bicomp bicomp = new Bicomp(this, graph);
            bicomp.addNode(realNodes.get(0));
            bicomp.buildBoundary();
            bicomps.add(bicomp);
            return;
        }

        numberOfEdges /= 2;
        createAdjacencyLists();
        createDFSChildLists();
        createTreeEdgeBicomps();
        createResultBicomps();

        for (int i = numberOfNodes - 1; i >= 0; i--) {
            RealNode pNode = realNodes.get(i);

            for (Iterator<RealNode> neighbours = pNode.completeAdjacencyList
                    .iterator(); neighbours.hasNext();) {
                RealNode neighbour = neighbours.next();
                if ((neighbour.DFI > pNode.DFI)
                        && (neighbour.DFSParent != pNode)) {
                    walkUp(pNode, neighbour);
                }
            }
            for (RealNode current = pNode.separatedDFSChildList.getFirst(); current != null; current = current.rightDFSNeighbour) {
                VirtualNode vNode = current.virtualParent;
                walkDown(vNode, current);
            }
        }
        for (Iterator<Bicomp> i = bicomps.iterator(); i.hasNext();) {
            Bicomp bicomp = i.next();
            buildAdjacencyLists(bicomp.nodes.get(0), 1);
            bicomp.buildBoundary();
        }
        planarityTested = true;
    }
}
